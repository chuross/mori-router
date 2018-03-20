package com.github.chuross.morirouter.annotation;


import com.github.chuross.morirouter.core.TransitionFactory;

public @interface RouterPath {

    String name();

    String uri();

    String[] transitionNames();

    Class<? extends TransitionFactory> enterTransitionFactory();

    Class<? extends TransitionFactory> exitTransitionFactory();
}
