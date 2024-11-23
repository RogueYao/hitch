package com.yian.commons.initial.realize;


import com.yian.commons.constant.HtichConstants;
import com.yian.commons.initial.InitialParser;
import com.yian.commons.initial.annotation.InitialResolver;
import com.yian.commons.utils.RequestUtils;

public class CurrentUserInitialParser implements InitialParser {
    @Override
    public boolean isMatch(Class clazz) {
        return clazz.isAssignableFrom(String.class);
    }

    @Override
    public Object getDefaultValue(Class clazz, InitialResolver initialResolver) {
        return RequestUtils.getRequestHeader(HtichConstants.HEADER_ACCOUNT_KEY);
    }
}
