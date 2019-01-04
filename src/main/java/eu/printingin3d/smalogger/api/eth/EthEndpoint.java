package eu.printingin3d.smalogger.api.eth;

import java.nio.ByteBuffer;

public class EthEndpoint 
{
	public short SUSyID;
	public int   Serial;
	public short Ctrl;
	
	public EthEndpoint(ByteBuffer bb)
	{
		SUSyID = bb.getShort();
		Serial = bb.getInt();
		Ctrl = bb.getShort();
	}
}
