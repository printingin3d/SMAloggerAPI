package eu.printingin3d.smalogger.api.requestvisitor;

import java.nio.ByteBuffer;
import java.rmi.UnexpectedException;
import java.util.Date;

import eu.printingin3d.smalogger.api.inverter.LriDef;
import eu.printingin3d.smalogger.api.inverterdata.IInverterCommand;

public abstract class AbstractInverterRequest<T> implements IInverterCommand {
	private Date datetime;

	protected abstract void parse(LriDef lri, int cls, ByteBuffer bb) throws UnexpectedException;
	
	public void parseOneSegment(LriDef lri, int cls, ByteBuffer bb) throws UnexpectedException {
		this.datetime = new Date(bb.getInt() * 1000l);
		
		parse(lri, cls, bb);
	}
	
	public abstract T closeParse();

	public Date getDatetime() {
		return datetime;
	}
}
