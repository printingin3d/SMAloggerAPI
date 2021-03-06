package eu.printingin3d.smalogger.api.requestvisitor;

import java.io.IOException;

import eu.printingin3d.physics.Energy;
import eu.printingin3d.smalogger.api.exception.UnexpectedValueException;
import eu.printingin3d.smalogger.api.inverter.LriDef;
import eu.printingin3d.smalogger.api.response.EnergyProductionResponse;

public class EnergyProductionRequest extends AbstractInverterValue64Request<EnergyProductionResponse> {
    private long totalEnergy;
    private long todayEnergy;

    @Override
    public int getCommand() {
        return 0x54000200;
    }

    @Override
    public int getFirst() {
        return 0x00260100;
    }

    @Override
    public int getLast() {
        return 0x002622FF;
    }

    @Override
    protected void putValue(LriDef lri, int cls, long value) throws IOException {
        switch (lri) {
        case MeteringTotWhOut:
            this.totalEnergy = value;
            break;
        case MeteringDyWhOut:
            this.todayEnergy = value;
            break;
        default:
            throw new UnexpectedValueException("Unexpected value: " + lri);
        }
    }

    @Override
    public EnergyProductionResponse closeParse() {
        return new EnergyProductionResponse(Energy.fromWattHour(totalEnergy), Energy.fromWattHour(todayEnergy));
    }

}
