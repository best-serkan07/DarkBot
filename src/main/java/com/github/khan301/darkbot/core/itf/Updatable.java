package com.github.khan301.darkbot.core.itf;

public abstract class Updatable {

    public long address;

    public abstract void update();

    public void update(long address) {
        this.address = address;
    }
}
