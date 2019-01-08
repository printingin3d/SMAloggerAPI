package eu.printingin3d.smalogger.api.response;

import java.time.LocalDateTime;

public class DayDataItem {
    private final LocalDateTime dt;
    private final long wh; // total energy produced in Wh
    private final double power; // average energy produced in the last section in W

    public DayDataItem(LocalDateTime dt, long wh, double power) {
        this.dt = dt;
        this.wh = wh;
        this.power = power;
    }

    public LocalDateTime getDt() {
        return dt;
    }

    public long getWh() {
        return wh;
    }

    public double getPower() {
        return power;
    }

    @Override
    public String toString() {
        return "DayDataItem [dt=" + dt + ", wh=" + wh + ", power=" + power + "]";
    }

}
