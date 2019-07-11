package com.github.khan301.darkbot.modules;

import com.github.khan301.darkbot.Main;
import com.github.khan301.darkbot.core.entities.Portal;
import com.github.khan301.darkbot.core.itf.MapChange;
import com.github.khan301.darkbot.core.itf.Module;
import com.github.khan301.darkbot.core.manager.HeroManager;
import com.github.khan301.darkbot.core.manager.StarManager;
import com.github.khan301.darkbot.core.objects.Map;
import com.github.khan301.darkbot.core.utils.Drive;

public class MapModule implements Module, MapChange {


    private Main main;
    private HeroManager hero;
    private Drive drive;
    private StarManager star;

    private Module back;
    private Portal current;
    private Map target;

    @Override
    public void install(Main main) {
        this.hero = main.hero;
        this.drive = main.hero.drive;
        this.star = main.starManager;
        this.main = main;
        this.back = main.module;
    }

    @Override
    public boolean canRefresh() {
        return false;
    }

    public void setTarget(Map target) {
        this.target = target;

        current = star.next(hero.map, hero.locationInfo, target);
    }

    public void setTargetAndBack(Map target) {
        setTarget(target);
    }

    @Override
    public void tick() {

        if (current != null) {

            if (current.target != target) {
                current = star.next(hero.map, hero.locationInfo, target);
            }

            double distance = current.locationInfo.distance(hero);

            hero.runMode();

            if (distance < 100 && !drive.isMoving()) {
                hero.jumpPortal(current);
            } else if (current.locationInfo.isLoaded()) {
                drive.move(current);
            }
        }

    }

    @Override
    public void onMapChange() {
        if (hero.map == target) {
            if (back != null) {
                main.setModule(back);
                back = null;
            }
            current = null;
        } else {
            current = star.next(hero.map, hero.locationInfo, target);
        }
    }
}
