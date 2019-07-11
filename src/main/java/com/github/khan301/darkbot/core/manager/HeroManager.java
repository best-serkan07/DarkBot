package com.github.khan301.darkbot.core.manager;

import com.github.khan301.darkbot.Main;
import com.github.khan301.darkbot.core.BotInstaller;
import com.github.khan301.darkbot.core.entities.Pet;
import com.github.khan301.darkbot.core.entities.Portal;
import com.github.khan301.darkbot.core.entities.Ship;
import com.github.khan301.darkbot.core.itf.Manager;
import com.github.khan301.darkbot.core.objects.Map;
import com.github.khan301.darkbot.core.utils.Drive;

import static com.github.khan301.darkbot.Main.API;

public class HeroManager extends Ship implements Manager {

    public static HeroManager instance;
    private final Main main;

    private long staticAddress;
    private long settingsAddress;

    public final Pet pet;
    public final Drive drive;

    public Map map;

    public Ship target;

    public int config;
    private long configTime;
    private long portalTime;

    public HeroManager(Main main) {
        super(0);
        instance = this;

        this.main = main;
        this.drive = new Drive(this, main.mapManager);
        this.pet = new Pet(0);
        this.map = new Map(0, "LOADING", false, new Portal[0]);
    }

    @Override
    public void install(BotInstaller botInstaller) {
        botInstaller.screenManagerAddress.add(value -> staticAddress = value + 240);
        botInstaller.settingsAddress.add(value -> this.settingsAddress = value);
    }

    public void tick() {

        long address = API.readMemoryLong(staticAddress);

        if (this.address != address) {
            update(address);
        }

        update();

        drive.checkMove();
    }

    @Override
    public void update() {

        super.update();

        pet.update();

        config = API.readMemoryInt(settingsAddress + 56);

        long petAddress = API.readMemoryLong(address + 176);

        if (petAddress != pet.address) {
            pet.update(petAddress);
        }
    }

    @Override
    public void update(long address) {
        super.update(address);

        pet.update(API.readMemoryLong(address + 176));
        clickable.setRadius(0);
        id = API.readMemoryInt(address + 56);
    }

    public void setTarget(Ship entity) {
        this.target = entity;
    }

    public long timeTo(double distance) {
        return (long) (distance * 1000 / shipInfo.speed);
    }

    public int nextMap() {
        return API.readMemoryInt(API.readMemoryInt(settingsAddress + 204));
    }

    public void jumpPortal(Portal portal) {
        if (portal.target.id != nextMap() && System.currentTimeMillis() - portalTime > 10000) {
            API.keyboardClick('j');
            portalTime = System.currentTimeMillis();
        }
    }

    public void attackMode() {
        if (config != main.config.OFFENSIVE_CONFIG && System.currentTimeMillis() - configTime > 6000) {
            API.keyboardClick('c');
            API.keyboardClick(main.config.OFFENSIVE_FORMATION);
            configTime = System.currentTimeMillis();
        }
    }

    public void runMode() {
        if (config != main.config.RUN_CONFIG && System.currentTimeMillis() - configTime > 6000) {
            API.keyboardClick(main.config.RUN_FORMATION);
            API.keyboardClick('c');
            configTime = System.currentTimeMillis();
        }
    }
}