package eu.printingin3d.smalogger.api.requestvisitor;

import java.io.IOException;

import eu.printingin3d.smalogger.api.exception.UnexpectedValueException;
import eu.printingin3d.smalogger.api.inverter.LriDef;
import eu.printingin3d.smalogger.api.smajava.TagDefs;

public class DeviceStatusRequest extends AbstractInverterAttributeRequest<String> {
    private int status;

    @Override
    public int getCommand() {
        return 0x51800200;
    }

    @Override
    public int getFirst() {
        return 0x00214800;
    }

    @Override
    public int getLast() {
        return 0x002148FF;
    }

    @Override
    protected final void putValue(LriDef lri, int cls, int value) throws IOException {
        if (lri == LriDef.OperationHealth) {
            this.status = value;
        } else {
            throw new UnexpectedValueException("Unexpected value: " + lri);
        }
    }

    @Override
    public final String closeParse() {
        return TagDefs.getInstance().getDesc(status, "?");
    }

}
