package com.github.khan301.darkbot.core.objects;

import com.github.khan301.darkbot.core.itf.Updatable;

import static com.github.khan301.darkbot.Main.API;

public class ShipInfo extends Updatable {

    public int speed;
    public long target;

    public ShipInfo() {
    }

    public ShipInfo(long address) {
        this.address = address;
    }

    @Override
    public void update() {
        target = API.readMemoryLong(address + 112);
        speed = API.readMemoryInt(API.readMemoryLong(address + 72) + 40);
    }

}
