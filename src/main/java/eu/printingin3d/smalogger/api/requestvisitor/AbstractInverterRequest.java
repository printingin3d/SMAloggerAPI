package eu.printingin3d.smalogger.api.requestvisitor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

import eu.printingin3d.smalogger.api.inverter.LriDef;
import eu.printingin3d.smalogger.api.inverterdata.IInverterCommand;

public abstract class AbstractInverterRequest<T> implements IInverterCommand {
    private Date datetime;

    protected abstract void parse(LriDef lri, int cls, ByteBuffer bb) throws IOException;

    public void parseBody(ByteBuffer bb) throws IOException {
        // the first non header byte is at position 41
        for (int ix = 41; ix < bb.limit() - 4;) {
            bb.position(ix);
            int code = bb.getInt();
            int cls = code & 0xFF;

            LriDef lri = LriDef.intToEnum((code & 0x00FFFF00));

            parseOneSegment(lri, cls, bb);

            ix += lri == null ? 12 : lri.getRecordSize();
        }
    }

    protected void parseOneSegment(LriDef lri, int cls, ByteBuffer bb) throws IOException {
        this.datetime = new Date(bb.getInt() * 1000L);

        parse(lri, cls, bb);
    }

    public abstract T closeParse();

    public Date getDatetime() {
        return datetime;
    }
}
