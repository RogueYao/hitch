package com.yian.commons.initial.annotation;

import com.yian.commons.enums.InitialResolverType;
import com.yian.commons.groups.Group;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Documented
public @interface InitialResolver {
    InitialResolverType resolver();

    Class<?>[] groups() default Group.All.class;

    //默认值
    String def() default "";
}
