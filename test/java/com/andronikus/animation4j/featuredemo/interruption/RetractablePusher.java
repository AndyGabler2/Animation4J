package com.andronikus.animation4j.featuredemo.interruption;

public class RetractablePusher {

    private boolean broken = false;
    private boolean extending = false;

    public boolean isBroken() {
        return broken;
    }

    public void setBroken(boolean broken) {
        this.broken = broken;
    }

    public boolean isExtending() {
        return extending;
    }

    public void setExtending(boolean extending) {
        this.extending = extending;
    }
}
