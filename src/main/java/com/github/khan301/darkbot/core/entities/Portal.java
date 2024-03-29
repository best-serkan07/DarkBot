package com.github.khan301.darkbot.core.entities;

import com.github.khan301.darkbot.core.objects.Map;

import static com.github.khan301.darkbot.Main.API;

public class Portal extends Entity {

    public Map target;

    public int type;

    public int targetId;

    public Portal(int id, int targetId) {
        super(id);

        this.targetId = targetId;
    }

    @Override
    public void update() {
        super.update();

        type = API.readMemoryInt(address + 112);
    }
}
