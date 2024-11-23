package com.yian.commons.initial.realize;

import com.yian.commons.initial.InitialParser;
import com.yian.commons.initial.annotation.InitialResolver;
import com.yian.commons.utils.SnowflakeIdWorker;

/**
 * 雪花ID生成器
 */
public class SnowflakeIDInitialParser implements InitialParser {
    SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(10, 11);

    @Override
    public boolean isMatch(Class clazz) {
        return clazz.isAssignableFrom(String.class);
    }

    @Override
    public Object getDefaultValue(Class clazz, InitialResolver initialResolver) {
        return String.valueOf(snowflakeIdWorker.nextId());
    }
}
