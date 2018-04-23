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

    String[] uris() default {};

    boolean isReorderingAllowed() default false;

    Class<? extends TransitionFactory> sharedEnterTransitionFactory() default DefaultTransitionFactory.class;

    Class<? extends TransitionFactory> sharedExitTransitionFactory() default DefaultTransitionFactory.class;

    Class<? extends TransitionFactory> overrideEnterTransitionFactory() default DefaultTransitionFactory.class;

    Class<? extends TransitionFactory> overrideExitTransitionFactory() default DefaultTransitionFactory.class;
}
