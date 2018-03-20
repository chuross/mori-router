package com.github.chuross.morirouter.annotation;


import com.github.chuross.morirouter.core.DefaultTransitionFactory;
import com.github.chuross.morirouter.core.TransitionFactory;

public @interface RouterPath {

    String name();

    String uri() default "";

    String[] transitionNames() default {};

    Class<? extends TransitionFactory> enterTransitionFactory() default DefaultTransitionFactory.class;

    Class<? extends TransitionFactory> exitTransitionFactory() default DefaultTransitionFactory.class;
}
