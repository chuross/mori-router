package com.github.chuross.morirouter.core;


public class BaseTransitionFactory implements TransitionFactory {

    @Override
    public String getFactoryClassName() {
        return getClass().getCanonicalName();
    }
}
