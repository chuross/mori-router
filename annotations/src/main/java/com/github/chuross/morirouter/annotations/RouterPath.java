package com.github.chuross.morirouter.annotations;


public @interface RouterPath {

    String name();

    String uri();

    String[] transitionNames();

    Class<? extends TransitionFactory> transitionFactory();
}
