package com.github.chuross.morirouter.core;


public class MoriRouterOptions {

    private static final TransitionFactory DEFAULT_TRANSITION_FACTORY = new DefaultTransitionFactory();
    private int containerId;
    private TransitionFactory enterTransitionFactory;
    private TransitionFactory exitTransitionFactory;

    private MoriRouterOptions(
            int containerId,
            TransitionFactory enterTransitionFactory,
            TransitionFactory exitTransitionFactory
    ) {
        this.containerId = containerId;
        this.enterTransitionFactory = enterTransitionFactory != null ? enterTransitionFactory : DEFAULT_TRANSITION_FACTORY;
        this.exitTransitionFactory = exitTransitionFactory != null ? exitTransitionFactory : DEFAULT_TRANSITION_FACTORY;
    }

    public int getContainerId() {
        return containerId;
    }

    public TransitionFactory getEnterTransitionFactory() {
        return enterTransitionFactory;
    }

    public TransitionFactory getExitTransitionFactory() {
        return exitTransitionFactory;
    }

    public static class Builder {

        private int containerId;
        private TransitionFactory enterTransitionFactory;
        private TransitionFactory exitTransitionFactory;

        public Builder(int containerId) {
            this.containerId = containerId;
        }

        public Builder setEnterTransitionFactory(TransitionFactory enterTransitionFactory) {
            this.enterTransitionFactory = enterTransitionFactory;
            return this;
        }

        public Builder setExitTransitionFactory(TransitionFactory exitTransitionFactory) {
            this.exitTransitionFactory = exitTransitionFactory;
            return this;
        }

        public MoriRouterOptions build() {
            return new MoriRouterOptions(
                    containerId,
                    enterTransitionFactory,
                    exitTransitionFactory
            );
        }
    }
}
