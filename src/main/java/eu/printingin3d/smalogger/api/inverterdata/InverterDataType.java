package eu.printingin3d.smalogger.api.inverterdata;

public enum InverterDataType implements IInverterCommand {
	/**
	 * SPOT_PDC1, SPOT_PDC2
	 */
	SpotDCPower			( 0x53800200, 0x00251E00, 0x00251EFF),
	/**
	 * SPOT_UDC1, SPOT_UDC2, SPOT_IDC1, SPOT_IDC2
	 */
	SpotDCVoltage		( 0x53800200, 0x00451F00, 0x004521FF),
	/**
	 * INV_PACMAX1, INV_PACMAX2, INV_PACMAX3
	 */
	MaxACPower			( 0x51000200, 0x00411E00, 0x004120FF),
	/**
	 * INV_PACMAX1_2
	 */
	MaxACPower2			( 0x51000200, 0x00832A00, 0x00832AFF),
	/**
	 * INV_NAME, INV_TYPE, INV_CLASS
	 */
	TypeLabel			( 0x58000200, 0x00821E00, 0x008220FF),
	/**
	 * INV_GRIDRELAY
	 */
	GridRelayStatus		( 0x51800200, 0x00416400, 0x004164FF),

	sbftest             ( 0, 0, 0 );
	
	/**
	 * Value used for requesting this data type from the inverter.
	 */
	private final long command;
	/**
	 * Value used for requesting this data type from the inverter.
	 */
	private final long first;
	/**
	 * Value used for requesting this data type from the inverter.
	 */
	private final long last;
	
	private InverterDataType(long command, long first, long last)
	{
		this.command = command;
		this.first = first;
		this.last = last;
	}

	@Override
	public long getCommand() {
		return command;
	}

	@Override
	public long getFirst() {
		return first;
	}

	@Override
	public long getLast() {
		return last;
	}
}
