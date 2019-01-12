package eu.printingin3d.smalogger.api.response;

import eu.printingin3d.physics.Time;

public class OperationTimeResponse {
    private final Time operationTime;
    private final Time feedInTime;

    public OperationTimeResponse(Time operationTime, Time feedInTime) {
        this.operationTime = operationTime;
        this.feedInTime = feedInTime;
    }

    public Time getOperationTime() {
        return operationTime;
    }

    public Time getFeedInTime() {
        return feedInTime;
    }
}
