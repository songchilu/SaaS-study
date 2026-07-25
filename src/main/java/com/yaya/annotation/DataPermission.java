package com.yaya.annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限注解，只有添加了注解@DataPermission的mapper层函数才可以对数据进行授权
 * 数据权限 {@link com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DataPermission {
    /*
     * SQL语句中部门表起了别名,这里要设置部门表别名
     * 在多表联查时，设置给哪个表添加权限，通过别名定位
     */
    String deptAlias() default "";

    /*
     * 对于查询指定表时,对当前表进行权限搜索,使用部门ID进行数据权限标记
     */
    String deptIdColumnName() default "dept_id";

    /*
     * SQL语句中用户表起了别名,这里要设置用户表别名
     * 在多表联查时，设置给哪个表添加权限，通过别名定位
     */
    String userAlias() default "";

    /*
     * 对于查询指定表时,对当前表进行权限搜索,使用创建人ID进行数据权限标记
     */
    String userIdColumnName() default "create_id";
}
