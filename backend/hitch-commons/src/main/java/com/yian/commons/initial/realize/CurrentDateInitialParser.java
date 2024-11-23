package com.yian.commons.initial.realize;


import com.yian.commons.initial.InitialParser;
import com.yian.commons.initial.annotation.InitialResolver;

import java.util.Date;

public class CurrentDateInitialParser implements InitialParser {
    @Override
    public boolean isMatch(Class clazz) {
        return clazz.isAssignableFrom(Date.class);
    }

    @Override
    public Object getDefaultValue(Class clazz, InitialResolver initialResolver) {
        return new Date();
    }
}
