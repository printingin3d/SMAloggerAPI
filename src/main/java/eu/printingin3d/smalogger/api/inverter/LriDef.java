package eu.printingin3d.smalogger.api.inverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum LriDef {
    OperationHealth(0x00214800, 40), // *08* Condition (aka INV_STATUS)
    CoolsysTmpNom(0x00237700), // *40* Operating condition temperatures
    DcMsWatt(0x00251E00, 28), // *40* DC power input (aka SPOT_PDC1 / SPOT_PDC2)
    MeteringTotWhOut(0x00260100, 16), // *00* Total yield (aka SPOT_ETOTAL)
    MeteringDyWhOut(0x00262200, 16), // *00* Day yield (aka SPOT_ETODAY)
    GridMsTotW(0x00263F00), // *40* Power (aka SPOT_PACTOT)
    BatChaStt(0x00295A00), // *00* Current battery charge status
    OperationHealthSttOk(0x00411E00), // *00* Nominal power in Ok Mode (aka INV_PACMAX1)
    OperationHealthSttWrn(0x00411F00), // *00* Nominal power in Warning Mode (aka INV_PACMAX2)
    OperationHealthSttAlm(0x00412000), // *00* Nominal power in Fault Mode (aka INV_PACMAX3)
    OperationGriSwStt(0x00416400, 40), // *08* Grid relay/contactor (aka INV_GRIDRELAY)
    OperationRmgTms(0x00416600), // *00* Waiting time until feed-in
    DcMsVol(0x00451F00, 28), // *40* DC voltage input (aka SPOT_UDC1 / SPOT_UDC2)
    DcMsAmp(0x00452100, 28), // *40* DC current input (aka SPOT_IDC1 / SPOT_IDC2)
    MeteringPvMsTotWhOut(0x00462300), // *00* PV generation counter reading
    MeteringGridMsTotWhOut(0x00462400), // *00* Grid feed-in counter reading
    MeteringGridMsTotWhIn(0x00462500), // *00* Grid reference counter reading
    MeteringCsmpTotWhIn(0x00462600), // *00* Meter reading consumption meter
    MeteringGridMsDyWhOut(0x00462700), // *00* ?
    MeteringGridMsDyWhIn(0x00462800), // *00* ?
    MeteringTotOpTms(0x00462E00, 16), // *00* Operating time (aka SPOT_OPERTM)
    MeteringTotFeedTms(0x00462F00, 16), // *00* Feed-in time (aka SPOT_FEEDTM)
    MeteringGriFailTms(0x00463100), // *00* Power outage
    MeteringWhIn(0x00463A00), // *00* Absorbed energy
    MeteringWhOut(0x00463B00), // *00* Released energy
    MeteringPvMsTotWOut(0x00463500), // *40* PV power generated
    MeteringGridMsTotWOut(0x00463600), // *40* Power grid feed-in
    MeteringGridMsTotWIn(0x00463700), // *40* Power grid reference
    MeteringCsmpTotWIn(0x00463900), // *40* Consumer power
    GridMsWphsA(0x00464000), // *40* Power L1 (aka SPOT_PAC1)
    GridMsWphsB(0x00464100), // *40* Power L2 (aka SPOT_PAC2)
    GridMsWphsC(0x00464200), // *40* Power L3 (aka SPOT_PAC3)
    GridMsPhVphsA(0x00464800), // *00* Grid voltage phase L1 (aka SPOT_UAC1)
    GridMsPhVphsB(0x00464900), // *00* Grid voltage phase L2 (aka SPOT_UAC2)
    GridMsPhVphsC(0x00464A00), // *00* Grid voltage phase L3 (aka SPOT_UAC3)
    GridMsAphsA_1(0x00465000), // *00* Grid current phase L1 (aka SPOT_IAC1)
    GridMsAphsB_1(0x00465100), // *00* Grid current phase L2 (aka SPOT_IAC2)
    GridMsAphsC_1(0x00465200), // *00* Grid current phase L3 (aka SPOT_IAC3)
    GridMsAphsA(0x00465300), // *00* Grid current phase L1 (aka SPOT_IAC1_2)
    GridMsAphsB(0x00465400), // *00* Grid current phase L2 (aka SPOT_IAC2_2)
    GridMsAphsC(0x00465500), // *00* Grid current phase L3 (aka SPOT_IAC3_2)
    GridMsHz(0x00465700), // *00* Grid frequency (aka SPOT_FREQ)
    MeteringSelfCsmpSelfCsmpWh(0x0046AA00), // *00* Energy consumed internally
    MeteringSelfCsmpActlSelfCsmp(0x0046AB00), // *00* Current self-consumption
    MeteringSelfCsmpSelfCsmpInc(0x0046AC00), // *00* Current rise in self-consumption
    MeteringSelfCsmpAbsSelfCsmpInc(0x0046AD00), // *00* Rise in self-consumption
    MeteringSelfCsmpDySelfCsmpInc(0x0046AE00), // *00* Rise in self-consumption today
    BatDiagCapacThrpCnt(0x00491E00), // *40* Number of battery charge throughputs
    BatDiagTotAhIn(0x00492600), // *00* Amp hours counter for battery charge
    BatDiagTotAhOut(0x00492700), // *00* Amp hours counter for battery discharge
    BatTmpVal(0x00495B00), // *40* Battery temperature
    BatVol(0x00495C00), // *40* Battery voltage
    BatAmp(0x00495D00), // *40* Battery current
    NameplateLocation(0x00821E00, 40), // *10* Device name (aka INV_NAME)
    NameplateMainModel(0x00821F00, 40), // *08* Device class (aka INV_CLASS)
    NameplateModel(0x00822000, 40), // *08* Device type (aka INV_TYPE)
    NameplateAvalGrpUsr(0x00822100), // * * Unknown
    NameplatePkgRev(0x00823400, 40), // *08* Software package (aka INV_SWVER)
    InverterWLim(0x00832A00), // *00* Maximum active power device (aka INV_PACMAX1_2) (Some inverters like
                              // SB3300/SB1200)
    GridMsPhVphsA2B6100(0x00464B00), GridMsPhVphsB2C6100(0x00464C00), GridMsPhVphsC2A6100(0x00464D00);

    private static final Logger LOGGER = LoggerFactory.getLogger(LriDef.class);

    public static LriDef intToEnum(int value) {
        for (LriDef lri : values()) {
            if (lri.value == value) {
                return lri;
            }
        }

        LOGGER.error("Unknown response type: " + Integer.toString(value, 16));

        return null;
    }

    private final int value;
    private final int recordSize;

    LriDef(int value, int recordSize) {
        this.value = value;
        this.recordSize = recordSize;
    }

    LriDef(int value) {
        this(value, 28);
    }

    public int getRecordSize() {
        return recordSize;
    }
}
