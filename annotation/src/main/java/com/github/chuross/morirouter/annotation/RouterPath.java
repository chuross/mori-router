package com.github.chuross.morirouter.annotation;


import com.github.chuross.morirouter.core.DefaultTransitionFactory;
import com.github.chuross.morirouter.core.TransitionFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface RouterPath {

    String name();

    String uri() default "";

    String[] transitionNames() default {};

    Class<? extends TransitionFactory> enterTransitionFactory() default DefaultTransitionFactory.class;

    Class<? extends TransitionFactory> exitTransitionFactory() default DefaultTransitionFactory.class;
}
