package eu.printingin3d.smalogger.api.requestvisitor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.printingin3d.smalogger.api.inverter.LriDef;
import eu.printingin3d.smalogger.api.response.DayDataItem;

public class DayDataRequest extends AbstractInverterRequest<List<DayDataItem>> {
	private final int startTime;
	
	private final List<DayDataItem> items = new ArrayList<>();

	public DayDataRequest() {
		this.startTime = (int)(System.currentTimeMillis()/1000L) - 86400 - 300;
	}

	@Override
	public int getCommand() {
		return 0x70000200;
	}

	@Override
	public int getFirst() {
		return startTime;
	}

	@Override
	public int getLast() {
		return startTime + 86400;
	}

	@Override
	protected void parse(LriDef lri, int cls, ByteBuffer bb) {
	}

	@Override
	public void parseBody(ByteBuffer bb) throws IOException {
		// the first non header byte is at position 41
        for (int ix = 41; ix < bb.limit() - 4; ix+=12) {
        	bb.position(ix);
        	LocalDateTime dt = LocalDateTime.ofEpochSecond(bb.getInt(), 0, OffsetDateTime.now().getOffset());
        	long value = bb.getLong();
        	
        	double power = 0.0;
        	if (!items.isEmpty()) {
        		DayDataItem last = items.get(items.size()-1);
        		double d = last.getDt().until(dt, ChronoUnit.SECONDS) / 3600.0;
        		power = (value - last.getWh()) / d;
        	}
			items.add(new DayDataItem(dt, value, power));
        }
	}

	@Override
	public List<DayDataItem> closeParse() {
		return Collections.unmodifiableList(items);
	}

}
