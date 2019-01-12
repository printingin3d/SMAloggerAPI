package eu.printingin3d.smalogger.api.response;

import java.time.LocalDateTime;

import eu.printingin3d.physics.Energy;
import eu.printingin3d.physics.Power;

public class DayDataItem {
    private final LocalDateTime dt;
    private final Energy wh; // total energy produced in Wh
    private final Power power; // average energy produced in the last section in W

    public DayDataItem(LocalDateTime dt, Energy wh, Power power) {
        this.dt = dt;
        this.wh = wh;
        this.power = power;
    }

    public LocalDateTime getDt() {
        return dt;
    }

    public Energy getWh() {
        return wh;
    }

    public Power getPower() {
        return power;
    }

    @Override
    public String toString() {
        return "DayDataItem [dt=" + dt + ", wh=" + wh + ", power=" + power + "]";
    }

}
