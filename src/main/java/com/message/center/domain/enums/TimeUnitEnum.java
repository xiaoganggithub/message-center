package com.message.center.domain.enums;

import lombok.Getter;

/**
 * 时间单位枚举
 */
@Getter
public enum TimeUnitEnum {
    SECOND("秒", "SECOND", 1),
    MINUTE("分钟", "MINUTE", 60),
    HOUR("小时", "HOUR", 3600),
    DAY("天", "DAY", 86400);

    private final String name;
    private final String code;
    private final int seconds;

    TimeUnitEnum(String name, String code, int seconds) {
        this.name = name;
        this.code = code;
        this.seconds = seconds;
    }

    /**
     * 根据code获取枚举
     * @param code 时间单位代码
     * @return 时间单位枚举
     */
    public static TimeUnitEnum getByCode(String code) {
        for (TimeUnitEnum unit : values()) {
            if (unit.getCode().equals(code)) {
                return unit;
            }
        }
        return SECOND;
    }

    /**
     * 将时间窗口转换为秒数
     * @param window 时间窗口大小
     * @return 秒数
     */
    public long toSeconds(int window) {
        return (long) window * this.seconds;
    }
}