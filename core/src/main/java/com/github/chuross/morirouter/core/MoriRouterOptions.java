package com.github.chuross.morirouter.core;


public class MoriRouterOptions {

    private int containerId;
    private Object enterTransition;
    private Object exitTransition;

    private MoriRouterOptions(
            int containerId,
            Object enterTransition,
            Object exitTransition
    ) {
        this.containerId = containerId;
        this.enterTransition = enterTransition;
        this.exitTransition = exitTransition;
    }

    public int getContainerId() {
        return containerId;
    }

    public Object getEnterTransition() {
        return enterTransition;
    }

    public Object getExitTransition() {
        return exitTransition;
    }

    public static class Builder {

        private int containerId;
        private Object enterTransition;
        private Object exitTransition;

        public Builder(int containerId) {
            this.containerId = containerId;
        }

        public Builder setEnterTransition(Object enterTransition) {
            this.enterTransition = enterTransition;
            return this;
        }

        public Builder setExitTransition(Object exitTransition) {
            this.exitTransition = exitTransition;
            return this;
        }

        public MoriRouterOptions build() {
            return new MoriRouterOptions(
                    containerId,
                    enterTransition,
                    exitTransition
            );
        }
    }
}
