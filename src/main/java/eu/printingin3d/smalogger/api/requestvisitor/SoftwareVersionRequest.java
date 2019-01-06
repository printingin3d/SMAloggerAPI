package eu.printingin3d.smalogger.api.requestvisitor;

import java.nio.ByteBuffer;
import java.rmi.UnexpectedException;

import eu.printingin3d.smalogger.api.inverter.LriDef;

public class SoftwareVersionRequest extends AbstractInverterRequest<String> {
	private String version;     // format: "03.01.05.R"

	@Override
	public long getCommand() {
		return 0x58000200;
	}

	@Override
	public long getFirst() {
		return 0x00823400;
	}

	@Override
	public long getLast() {
		return 0x008234FF;
	}

	@Override
	protected void parse(LriDef lri, ByteBuffer bb) throws UnexpectedException {
		if (lri != LriDef.NameplatePkgRev) {
			throw new UnexpectedException("Unexpected value: "+lri);
		}
		
		bb.position(bb.position() + 16);    // skipping 16 bytes
		
    	//INV_SWVER
    	char Vtype = (char) bb.get();
        String ReleaseType;
        if (Vtype > 5) {
			ReleaseType = String.format("%c", Vtype);
		}
		else {
			ReleaseType = String.format("%c", "NEABRS".charAt(Vtype));//NOREV-EXPERIMENTAL-ALPHA-BETA-RELEASE-SPECIAL
		}
        char Vbuild = (char) bb.get();
        char Vminor = (char) bb.get();
        char Vmajor = (char) bb.get();
        //Vmajor and Vminor = 0x12 should be printed as '12' and not '18' (BCD)
        this.version = String.format("%c%c.%c%c.%02d.%s", '0'+(Vmajor >> 4), '0'+(Vmajor & 0x0F), '0'+(Vminor >> 4), '0'+(Vminor & 0x0F), (int)Vbuild, ReleaseType);  
	}

	@Override
	public String closeParse() {
		return version;
	}

}
