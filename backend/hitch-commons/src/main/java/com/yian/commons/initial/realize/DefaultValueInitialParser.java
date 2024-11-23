package com.yian.commons.initial.realize;


import com.yian.commons.initial.InitialParser;
import com.yian.commons.initial.annotation.InitialResolver;
import com.yian.commons.utils.reflect.ReflectUtils;

public class DefaultValueInitialParser implements InitialParser {
    @Override
    public boolean isMatch(Class clazz) {
        return ReflectUtils.isBasicTypes(clazz);
    }

    @Override
    public Object getDefaultValue(Class clazz, InitialResolver initialResolver) {
        Object defValue = null;
        try {
            defValue = ReflectUtils.getDefValue(clazz, initialResolver.def());
        } catch (Exception e) {

        }
        return defValue;
    }
}
