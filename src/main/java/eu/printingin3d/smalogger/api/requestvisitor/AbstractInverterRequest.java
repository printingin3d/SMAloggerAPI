package eu.printingin3d.smalogger.api.requestvisitor;

import java.nio.ByteBuffer;
import java.rmi.UnexpectedException;
import java.util.Date;

import eu.printingin3d.smalogger.api.inverter.LriDef;
import eu.printingin3d.smalogger.api.inverterdata.IInverterCommand;

public abstract class AbstractInverterRequest<T> implements IInverterCommand {

	protected abstract void parse(LriDef lri, ByteBuffer bb, Date datetime) throws UnexpectedException;
	
	public void parseOneSegment(LriDef lri, ByteBuffer bb) throws UnexpectedException {
		Date datetime = new Date(bb.getInt() * 1000l);
		
		parse(lri, bb, datetime);
	}
	
	public abstract T closeParse();
}
