package com.github.khan301.darkbot.core.entities;

import com.github.khan301.darkbot.config.ConfigEntity;
import com.github.khan301.darkbot.config.NpcInfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Npc extends Ship {

    public NpcInfo npcInfo;

    public Npc(int id) {
        super(id);
    }

    @Override
    public void update() {
        String oldName = playerInfo.username;

        super.update();

        //noinspection StringEquality
        if (oldName != playerInfo.username) {
            npcInfo = ConfigEntity.INSTANCE.getOrCreateNpcInfo(playerInfo.username);
        }
    }
}
