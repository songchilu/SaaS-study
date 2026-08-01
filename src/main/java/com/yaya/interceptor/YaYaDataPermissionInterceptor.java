package com.yaya.interceptor;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import com.yaya.annotation.DataPermission;
import com.yaya.entity.SysRoleDept;
import com.yaya.util.SecurityUtils;
import lombok.SneakyThrows;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义数据权限逻辑 - 配置sql拦截器
 * 要想mapper层的查询函数在进行查询时带有数据权限，那么一定要在mapper指定函数上添加注解 @DataPermission 否则权限不生效
 */
public class YaYaDataPermissionInterceptor implements DataPermissionHandler {

    private static final String DEPT_TABLE = "sys_department";//部门表名称
    private static final String DEPT_ID_COLUMN = "dept_id";//部门表主键(或者其他表作为部门权限数据管理的列名)
    private static final String DEPT_TREE_PATH_COLUMN = "tree_path";//部门表祖宗列存储的列名

    /**
     * 核心逻辑处理
     * 权限注解要设置在mapper层
     * 数据权限简单概括
     * 1. 设计层面 - 数据库表的设计要合理
     *  🌟 数据库表要有dept_id列和create_id列这两个列一个是控制部门数据权限的一个是控制用户自己数据权限的
     * 2. 实现层面 - 当前方法
     *  🌟 在SQL的WHERE语句后拼接SQL,如果是全部数据：不拼接;当前用户数据：拼接create_id=?; 当前部门数据：拼接dept_id; 当前部门及其子部门或者自定义部门数据具体看下面的代码实现
     *
     * @param where             SQL语句的WHERE 关键字后面的条件语句
     * @param mappedStatementId 哪一个在mapper层中的函数
     * @return 返回最新的SQL WHERE 条件后的表达式
     */
    @SneakyThrows
    @Override
    public Expression getSqlSegment(Expression where, String mappedStatementId) {
        /*
         * 获取用户角色,介绍一个原则
         * 1. 平台角色(超管|系统管理员|运营管理员)拥有全部数据的权限,所以不进行权限拼接
         * 2. 未登录的用户拥有全部的数据权限(因为未登录的用户可能是定时任务)
         * 3. 角色中没有设置data_scope权限范围的,拥有全部数据权限
         * 4. 角色中设置了data_scope并且设置为1的,拥有全部数据权限
         * 5. 角色中设置了data_scope并且不为空,也不为1,那么根据data_scope值进行数据权限分配,不同的值的权限范围如下:
         *    🌟1-所有数据
         *    🌟2-部门及子部门数据
         *    🌟3-本部门数据
         *    🌟4-本人数据
         *    🌟5-自定义部门数据
         */

        if(SecurityUtils.getUserId()==null || SecurityUtils.isRootOrAdminOrOperation() || SecurityUtils.getDataScope()==null || (SecurityUtils.getDataScope()!=null && SecurityUtils.getDataScope()==1)){
            return where;
        }else {
            /*
             * 约定：只有在mapper层方法上添加@DataPermission注解的函数，才可以进行数据权限配置
             */
            //第一步 获取mapper层的类(反射)
            Class<?> clazz = Class.forName(mappedStatementId.substring(0, mappedStatementId.lastIndexOf(StringPool.DOT)));
            //第二步 获取当前执行的方法
            String methodName = mappedStatementId.substring(mappedStatementId.lastIndexOf(StringPool.DOT) + 1);
            //第三步 获取当前执行的接口类里所有的方法
            Method[] methods = clazz.getDeclaredMethods();
            for(Method method : methods){
                if(method.getName().equals(methodName)){
                    //获取带有数据权限注解的函数
                    DataPermission annotation = method.getAnnotation(DataPermission.class);
                    if(annotation==null){ //最高权限
                        return where;
                    }else {
                        //获取注解中的配置信息
                        String deptAlias = annotation.deptAlias();//部门别名
                        String deptIdColumnName = annotation.deptIdColumnName();//部门列名
                        String userAlias = annotation.userAlias();//用户别名
                        String userIdColumnName = annotation.userIdColumnName();//用户列名

                        //获取DataScope
                        Integer dataScope = SecurityUtils.getDataScope();
                        //构建条件列
                        Column deptColumn = buildColumn(deptAlias, deptIdColumnName);
                        Column userColumn = buildColumn(userAlias, userIdColumnName);
                        //获取操作用户ID+部门ID
                        Long userId = SecurityUtils.getUserId();
                        Long deptId = SecurityUtils.getDeptId();

                        //1-所有数据(上面已处理,从2开始处理) 2-部门及子部门数据 3-本部门数据 4-本人数据 5-自定义部门数据
                        if(dataScope==2){ //部门及子部门数据
                            String sql=deptColumn + " IN(SELECT "+DEPT_ID_COLUMN+" FROM "+DEPT_TABLE+" WHERE "+DEPT_ID_COLUMN+"="+deptId+" OR FIND_IN_SET("+ deptId +","+DEPT_TREE_PATH_COLUMN+"))";
                            if(where==null){
                                return CCJSqlParserUtil.parseCondExpression("("+sql+")");
                            }else {
                                return new AndExpression(where, CCJSqlParserUtil.parseCondExpression("("+sql+")"));
                            }
                        }else if(dataScope==3){ //本部门数据
                            /*
                                本部门数据 SQL示例：select * from table where xxx=? AND yyy=? AND dept_id=?
                                where关键字后的查询条件 xxx=? AND yyy=? 是函数where(Expression)传递过来的,这个 AND dept_id=?是部门需要根据数据权限拼接的
                             */
                            String sql=deptColumn.toString()+"="+deptId;
                            if(where==null){
                                return CCJSqlParserUtil.parseCondExpression(sql);
                            }else {
                                return new AndExpression(where, CCJSqlParserUtil.parseCondExpression(sql));
                            }
                        }else if(dataScope==4){
                            /*
                                本人数据 SQL示例：select * from table where xxx=? AND yyy=? AND create_id=?
                                where关键字后的查询条件 xxx=? AND yyy=? 是函数where(Expression)传递过来的,这个 AND create_id=?是用户需要根据数据权限拼接的
                             */
                            String sql=userColumn.toString()+"="+userId;
                            if(where==null){
                                return CCJSqlParserUtil.parseCondExpression(sql);
                            }else {
                                return new AndExpression(where, CCJSqlParserUtil.parseCondExpression(sql));
                            }
                        }else if(dataScope==5){ //自定义部门数据
                            //当前用户角色下自定义部门列表
                            List<SysRoleDept> roleDeptList = SecurityUtils.getRoleDept();
                            if(CollectionUtils.isEmpty(roleDeptList)){//还没有授权,说明用户没有任何数据权限,那么在where条件后添加一个错误的条件，让用户查不到任何数据
                                /*
                                    指定部门数据 SQL示例：select * from table where xxx=? AND yyy=? AND dept_id IN ()
                                    where关键字后的查询条件 xxx=? AND yyy=? 是函数where(Expression)传递过来的,这个 AND dept_id IN () 是用户需要根据数据权限拼接的,如果为空可以直接拼一个永远不成立的条件 例如 1=0
                                 */
                                String sql="1=0";
                                if(where==null){
                                    return CCJSqlParserUtil.parseCondExpression(sql);
                                }else {
                                    return new AndExpression(where, CCJSqlParserUtil.parseCondExpression(sql));
                                }
                            }else {
                                String collect = roleDeptList.stream().map(x->x.getDeptId().toString()).collect(Collectors.joining(StringPool.COMMA));
                                String sql=deptColumn+" IN( "+collect+" )";
                                if(where==null){
                                    return CCJSqlParserUtil.parseCondExpression(sql);
                                }else {
                                    return new AndExpression(where, CCJSqlParserUtil.parseCondExpression(sql));
                                }
                            }
                        }
                    }
                }
            }
            return where;
        }
    }

    /**
     * 构建列引用
     *
     * @param alias      表别名
     * @param columnName 列名
     * @return 列引用
     */
    private Column buildColumn(String alias, String columnName) {
        if (StringUtils.isNotBlank(alias)) {
            return new Column(alias + StringPool.DOT + columnName);
        }
        return new Column(columnName);
    }

}
