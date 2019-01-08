package eu.printingin3d.smalogger.api.requestvisitor;

import java.io.IOException;
import java.nio.ByteBuffer;

import eu.printingin3d.smalogger.api.exception.UnexpectedValueException;
import eu.printingin3d.smalogger.api.inverter.LriDef;

public class SoftwareVersionRequest extends AbstractInverterRequest<String> {
    private String version; // format: "03.01.05.R"

    @Override
    public int getCommand() {
        return 0x58000200;
    }

    @Override
    public int getFirst() {
        return 0x00823400;
    }

    @Override
    public int getLast() {
        return 0x008234FF;
    }

    @Override
    protected void parse(LriDef lri, int cls, ByteBuffer bb) throws IOException {
        if (lri != LriDef.NameplatePkgRev) {
            throw new UnexpectedValueException("Unexpected value: " + lri);
        }

        bb.position(bb.position() + 16); // skipping 16 bytes

        // INV_SWVER
        char type = (char) bb.get();
        String releaseType;
        if (type > 5) {
            releaseType = String.format("%c", type);
        } else {
            releaseType = String.format("%c", "NEABRS".charAt(type));// NOREV-EXPERIMENTAL-ALPHA-BETA-RELEASE-SPECIAL
        }
        char build = (char) bb.get();
        char minor = (char) bb.get();
        char major = (char) bb.get();
        // Vmajor and Vminor = 0x12 should be printed as '12' and not '18' (BCD)
        this.version = String.format("%c%c.%c%c.%02d.%s", '0' + (major >> 4), '0' + (major & 0x0F),
                '0' + (minor >> 4), '0' + (minor & 0x0F), (int) build, releaseType);
    }

    @Override
    public String closeParse() {
        return version;
    }

}
