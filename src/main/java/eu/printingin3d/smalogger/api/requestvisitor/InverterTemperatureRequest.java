package eu.printingin3d.smalogger.api.requestvisitor;

import eu.printingin3d.physics.Temperature;
import eu.printingin3d.smalogger.api.inverter.LriDef;

public class InverterTemperatureRequest extends AbstractInverterOneIntRequest<Temperature> {

    public InverterTemperatureRequest() {
        super(LriDef.CoolsysTmpNom, x -> Temperature.fromCelsius(x * 0.01));
    }

    @Override
    public int getCommand() {
        return 0x52000200;
    }

    @Override
    public int getFirst() {
        return 0x00237700;
    }

    @Override
    public int getLast() {
        return 0x002377FF;
    }
}
