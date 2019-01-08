package eu.printingin3d.smalogger.api.response;

public class OperationTimeResponse {
    private final long operationTime;
    private final long feedInTime;

    public OperationTimeResponse(long operationTime, long feedInTime) {
        this.operationTime = operationTime;
        this.feedInTime = feedInTime;
    }

    public long getOperationTime() {
        return operationTime;
    }

    public long getFeedInTime() {
        return feedInTime;
    }
}
