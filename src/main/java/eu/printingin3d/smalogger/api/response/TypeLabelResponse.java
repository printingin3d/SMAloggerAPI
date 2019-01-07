package eu.printingin3d.smalogger.api.response;

import eu.printingin3d.smalogger.api.inverter.InvDeviceClass;

public class TypeLabelResponse {
    private final String deviceType;
    private final String deviceClass;
    private final InvDeviceClass devClass;
    private final String deviceName;
    
	public TypeLabelResponse(String deviceType, String deviceClass, InvDeviceClass devClass, String deviceName) {
		this.deviceType = deviceType;
		this.deviceClass = deviceClass;
		this.devClass = devClass;
		this.deviceName = deviceName;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public String getDeviceClass() {
		return deviceClass;
	}

	public InvDeviceClass getDevClass() {
		return devClass;
	}

	public String getDeviceName() {
		return deviceName;
	}
}
