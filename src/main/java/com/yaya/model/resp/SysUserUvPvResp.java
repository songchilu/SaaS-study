package com.yaya.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 封装首页-图标
 */
@Data
public class SysUserUvPvResp {
    @Schema(description = "时间")
    private String statDate;
    @Schema(description = "访客(UV)")
    private Integer totalUv;
    @Schema(description = "浏览量(PV)")
    private Integer totalPv;
}
