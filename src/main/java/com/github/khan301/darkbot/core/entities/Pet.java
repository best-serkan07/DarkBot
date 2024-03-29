package com.github.khan301.darkbot.core.entities;

import static com.github.khan301.darkbot.Main.API;

public class Pet extends Ship {

    public Pet(int id) {
        super(id);
    }

    @Override
    public void update() {
        super.update();
        id = API.readMemoryInt(address + 56);
    }

    @Override
    public void update(long address) {
        super.update(address);

        clickable.setRadius(0);
    }
}
