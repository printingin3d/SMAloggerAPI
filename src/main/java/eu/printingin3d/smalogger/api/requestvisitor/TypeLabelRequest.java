package eu.printingin3d.smalogger.api.requestvisitor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import eu.printingin3d.smalogger.api.exception.UnexpectedValueException;
import eu.printingin3d.smalogger.api.inverter.InvDeviceClass;
import eu.printingin3d.smalogger.api.inverter.LriDef;
import eu.printingin3d.smalogger.api.response.TypeLabelResponse;
import eu.printingin3d.smalogger.api.smajava.TagDefs;

public class TypeLabelRequest extends AbstractInverterAttributeRequest<TypeLabelResponse> {
    private String deviceType;
    private String deviceClass;
    private InvDeviceClass devClass;
    private String deviceName;

	@Override
	public int getCommand() {
		return 0x58000200;
	}

	@Override
	public int getFirst() {
		return 0x00821E00;
	}

	@Override
	public int getLast() {
		return 0x008220FF;
	}

	@Override
	protected void putValue(LriDef lri, int cls, int value) throws IOException {
    	switch(lri) {
	        case NameplateMainModel:	            
                this.devClass = InvDeviceClass.intToEnum(value);
                this.deviceClass = TagDefs.getInstance().getDesc(value, "UNKNOWN CLASS");
	            break;
	        case NameplateModel: //INV_TYPE
	        	this.deviceType = TagDefs.getInstance().getDesc(value, "UNKNOWN TYPE");
	            break;
	        default:
	        	throw new UnexpectedValueException("Unexpected value: "+lri);
    	}
	}

	@Override
	protected void parse(LriDef lri, int cls, ByteBuffer bb) throws IOException {
		if (lri == LriDef.NameplateLocation) {
        	int DEVICE_NAME_LENGTH = 33; //32 bytes + terminating zero
            this.deviceName = new String(Arrays.copyOfRange(bb.array(), bb.position(), bb.position()+DEVICE_NAME_LENGTH-1)).trim();
		} else {
			super.parse(lri, cls, bb);
		}
	}

	@Override
	public TypeLabelResponse closeParse() {
		return new TypeLabelResponse(deviceType, deviceClass, devClass, deviceName);
	}

}
