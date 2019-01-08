package eu.printingin3d.smalogger.api.requestvisitor;

public class MaxACPower2Request extends MaxACPowerRequest {
    @Override
    public int getCommand() {
        return 0x51000200;
    }

    @Override
    public int getFirst() {
        return 0x00832A00;
    }

    @Override
    public int getLast() {
        return 0x00832AFF;
    }

}
