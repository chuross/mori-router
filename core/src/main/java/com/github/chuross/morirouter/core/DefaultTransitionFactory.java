package com.github.chuross.morirouter.core;


import java.util.concurrent.Callable;

public class DefaultTransitionFactory implements TransitionFactory {

    private Callable<Object> transitionCallable;

    public DefaultTransitionFactory() {
    }

    public DefaultTransitionFactory(Callable<Object> transitionCallable) {
        this.transitionCallable = transitionCallable;
    }

    @Override
    public Object create() {
        if (transitionCallable == null) return null;
        try {
            return transitionCallable.call();
        } catch (Exception e) {
            return null;
        }
    }

}
