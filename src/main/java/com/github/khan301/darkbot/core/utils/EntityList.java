package com.github.khan301.darkbot.core.utils;

import com.github.khan301.darkbot.Main;
import com.github.khan301.darkbot.core.entities.*;
import com.github.khan301.darkbot.core.itf.Obstacle;
import com.github.khan301.darkbot.core.itf.Updatable;
import com.github.khan301.darkbot.core.objects.swf.Array;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

import static com.github.khan301.darkbot.Main.API;

public class EntityList extends Updatable {

    private final Main main;
    private final Array entitiesAddress;
    private final List<List<? extends Entity>> allEntities;
    private final HashSet<Integer> ids;

    public final List<Obstacle> obstacles;

    public final List<Barrier> barriers;
    public final List<Box> boxes;
    public final List<Npc> npcs;
    public final List<Portal> portals;
    public final List<Ship> ships;
    public final List<BattleStation> battleStations;
    public final List<Entity> unknown;

    public EntityList(Main main) {
        this.main = main;

        this.entitiesAddress = new Array(0);
        this.allEntities = new ArrayList<>();

        this.ids = new HashSet<>();

        this.obstacles = new ArrayList<>();

        this.barriers = new ArrayList<>();
        this.boxes = new ArrayList<>();
        this.npcs = new ArrayList<>();
        this.portals = new ArrayList<>();
        this.ships = new ArrayList<>();
        this.battleStations = new ArrayList<>();
        this.unknown = new ArrayList<>();

        this.allEntities.add(barriers);
        this.allEntities.add(boxes);
        this.allEntities.add(npcs);
        this.allEntities.add(portals);
        this.allEntities.add(ships);
        this.allEntities.add(battleStations);
        this.allEntities.add(unknown);

        this.main.status.add(this::refreshRadius);
    }

    @Override
    public void update() {

        synchronized (Main.UPDATE_LOCKER) {
            removeAllInvalidEntities();

            refreshEntities();
        }

    }

    @Override
    public void update(long address) {
        super.update(address);

        entitiesAddress.update(API.readMemoryLong(address + 40));

        clear();
    }

    private void refreshEntities() {

        entitiesAddress.update();

        for (int i = 0; i < entitiesAddress.size; i++) {

            long found = entitiesAddress.elements[i];

            int id = API.readMemoryInt(found + 56);

            if (!ids.add(id)) continue;

            int rnd = API.readMemoryInt(found + 112);

            String key = API.readMemoryString(API.readMemoryLong(found + 136));

            if (!key.equals("ERROR") && !key.isEmpty()) {
                System.out.println(key);
            }

            if (key.equals("NOA")) {
                barriers.add(whenAdd(new Barrier(id), found));
            } else if (id < 0 && rnd == 3) {
                boxes.add(whenAdd(new Box(id), found));
            } else if (id <= 150000499 && id >= 150000156) {
                portals.add(whenAdd(main.starManager.fromIdPortal(id), found));
            } else if (id <= 150000950 && id >= 150000500) {

                int hullId = API.readMemoryInt(found + 116);

                if (hullId > 0 && hullId < 255) {
                    battleStations.add(whenAdd(new BattleStation(id), found));
                } else {
                    unknown.add(whenAdd(new Entity(id), found));
                }

            } else {

                int npc = API.readMemoryInt(found + 112);
                int visible = API.readMemoryInt(found + 116);
                int c = API.readMemoryInt(found + 120);
                int d = API.readMemoryInt(found + 124);

                if ((visible == 1 || visible == 0) && (c == 1 || c == 0) && d == 0) {
                    if (npc == 1) {
                        npcs.add(whenAdd(new Npc(id), found));
                    } else if (npc == 0 && found != main.hero.address && found != main.hero.pet.address) {
                        ships.add(whenAdd(new Ship(id), found));
                    }
                } else {
                    unknown.add(whenAdd(new Entity(id), found));
                }

            }

        }

    }

    private <E extends Entity> E whenAdd(E entity, long address) {

        entity.update(address);
        entity.update();

        if (entity instanceof Obstacle)
            obstacles.add((Obstacle) entity);

        if (main.isRunning())
            entity.clickable.setRadius(0);

        return entity;
    }

    private void whenRemove(Entity entity) {
        entity.removed();
    }

    private void removeAllInvalidEntities() {
        for (List<? extends Entity> entities : allEntities) {
            for (int i = 0; i < entities.size(); i++) {
                Entity entity = entities.get(i);

                if (entity.isInvalid(address)) {
                    entities.remove(i);
                    ids.remove(entity.id);
                    whenRemove(entity);
                    i--;
                } else {
                    entity.update();
                }
            }
        }

        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle obstacle = obstacles.get(i);
            if (obstacle.isRemoved()) {
                obstacles.remove(i);
                i--;
            }
        }
    }

    private void doInEachEntity(Consumer<Entity> consumer) {
        for (List<? extends Entity> entities : allEntities) {
            entities.forEach(consumer);
        }
    }

    private void clear() {

        ids.clear();

        obstacles.clear();

        for (List<? extends Entity> entities : allEntities) {
            for (Entity entity : entities) {
                entity.removed = true;
            }
            entities.clear();
        }
    }

    private void refreshRadius(boolean value) {
        if (value) doInEachEntity(entity -> entity.clickable.setRadius(0));
        else doInEachEntity(entity -> entity.clickable.reset());
    }
}
