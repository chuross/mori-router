package com.github.chuross.morirouter.annotations;


public @interface RouterParam {

    String name();

    boolean required() default true;
}
