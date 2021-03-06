package eu.printingin3d.smalogger.api.requestvisitor;

import java.io.IOException;

import eu.printingin3d.physics.Time;
import eu.printingin3d.smalogger.api.exception.UnexpectedValueException;
import eu.printingin3d.smalogger.api.inverter.LriDef;
import eu.printingin3d.smalogger.api.response.OperationTimeResponse;

public class OperationTimeRequest extends AbstractInverterValue64Request<OperationTimeResponse> {
    private long operationTime;
    private long feedInTime;

    @Override
    public int getCommand() {
        return 0x54000200;
    }

    @Override
    public int getFirst() {
        return 0x00462E00;
    }

    @Override
    public int getLast() {
        return 0x00462FFF;
    }

    @Override
    protected void putValue(LriDef lri, int cls, long value) throws IOException {
        switch (lri) {
        case MeteringTotOpTms:
            this.operationTime = value;
            break;
        case MeteringTotFeedTms:
            this.feedInTime = value;
            break;
        default:
            throw new UnexpectedValueException("Unexpected value: " + lri);
        }
    }

    @Override
    public OperationTimeResponse closeParse() {
        return new OperationTimeResponse(Time.ofSecond(operationTime), Time.ofSecond(feedInTime));
    }

}
