package com.yian.commons.initial.annotation;

import com.yian.commons.groups.Group;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface RequestInitial {
    Class<?>[] groups() default Group.All.class;
}
