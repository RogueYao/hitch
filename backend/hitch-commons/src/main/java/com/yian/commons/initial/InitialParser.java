package com.yian.commons.initial;

import com.yian.commons.initial.annotation.InitialResolver;

public interface InitialParser {

    boolean isMatch(Class clazz);


    Object getDefaultValue(Class clazz, InitialResolver initialResolver);
}
