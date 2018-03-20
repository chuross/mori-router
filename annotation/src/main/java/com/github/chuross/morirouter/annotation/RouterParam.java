package com.github.chuross.morirouter.annotation;


public @interface RouterParam {

    String name();

    boolean required() default false;
}
