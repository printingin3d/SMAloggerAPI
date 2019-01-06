package eu.printingin3d.smalogger.api.inverterdata;

import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.printingin3d.smalogger.api.inverter.InvDeviceClass;
import eu.printingin3d.smalogger.api.inverter.LriDef;
import eu.printingin3d.smalogger.api.smajava.Misc;
import eu.printingin3d.smalogger.api.smajava.TagDefs;

public class InverterData {
	private static final Logger LOGGER = LoggerFactory.getLogger(InverterData.class);
	
	public String DeviceName;
	public String BTAddress;
	public String IPAddress;
	public short SUSyID;
	public long Serial;
	public char NetID;
	public float BT_Signal;
	public long InverterDatetime;
	public long WakeupTime;
	public long SleepTime;
	public long Pdc1;
	public long Pdc2;
	public long Udc1;
	public long Udc2;
	public long Idc1;
	public long Idc2;
	public long Pmax1;
	public long Pmax2;
	public long Pmax3;
    public long GridFreq;
    public long OperationTime;
    public long FeedInTime;
    public long EToday;
    public long ETotal;
    public short modelID;
    public String DeviceType;
    public String DeviceClass;
    public InvDeviceClass DevClass;
    public String SWVersion;	//"03.01.05.R"
    public int DeviceStatus;
    public int GridRelayStatus;
    public DayData[] dayData = new DayData[288];
    public MonthData[] monthData = new MonthData[31];
    public ArrayList<EventData> eventData;
    public long calPdcTot;
    public long calPacTot;
    public float calEfficiency;
    public long BatChaStt;			// Current battery charge status
    public long BatDiagCapacThrpCnt;	// Number of battery charge throughputs
    public long BatDiagTotAhIn;		// Amp hours counter for battery charge
    public long BatDiagTotAhOut;		// Amp hours counter for battery discharge
    public long BatTmpVal;			// Battery temperature
    public long BatVol;				// Battery voltage
    public long BatAmp;						// Battery current
    public long Temperature;					// Inverter Temperature
    
    
    public void SetInverterData(LriDef lri, int value, Date datetime)
    {
    	String strWatt = "%-12s: %d (W) %s";
	    
    	switch (lri)
        {
        case OperationHealthSttOk: //INV_PACMAX1
            this.Pmax1 = value;

            LOGGER.debug(String.format(strWatt, "INV_PACMAX1", value, datetime));
            break;

        case OperationHealthSttWrn: //INV_PACMAX2
            this.Pmax2 = value;

            LOGGER.debug(String.format(strWatt, "INV_PACMAX2", value, datetime));
            break;

        case OperationHealthSttAlm: //INV_PACMAX3
            this.Pmax3 = value;

            LOGGER.debug(String.format(strWatt, "INV_PACMAX3", value, datetime));
            break;

        case GridMsHz: //SPOT_FREQ
            this.GridFreq = value;

            LOGGER.debug(String.format("%-12s: %.2f (Hz) %s\n", "SPOT_FREQ", Misc.toHz(value), datetime));
            break;          

        case BatChaStt:
            this.BatChaStt = value;
            break;

        case BatDiagCapacThrpCnt:
            this.BatDiagCapacThrpCnt = value;
            break;

        case BatDiagTotAhIn:
            this.BatDiagTotAhIn = value;
            break;

        case BatDiagTotAhOut:
            this.BatDiagTotAhOut = value;
            break;

        case BatTmpVal:
            this.BatTmpVal = value;
            break;

        case BatVol:
            this.BatVol = value;
            break;

        case BatAmp:
            this.BatAmp = value;
            break;

		case CoolsysTmpNom:
			this.Temperature = value;
			break;

		default:
			LOGGER.error("Wrong enum given to this method (SetInverterData), {}", lri);
			break;
        }
    }
    
    public void SetInverterDataINVNAME(String deviceName, Date datetime)
    {
    	this.WakeupTime = datetime.getTime();
    	this.DeviceName = deviceName;

    	LOGGER.debug(String.format("%-12s: '%s' %s", "INV_NAME", this.DeviceName, datetime));
    }
    
    public void SetInverterDataSWVER(String swVersion, Date datetime)
    {
    	//INV_SWVER
        this.SWVersion = swVersion;
        LOGGER.debug(String.format("%-12s: '%s' %s", "INV_SWVER", this.SWVersion, datetime));
    }
    
    public void SetInverterDataAttribute(LriDef lri, int attribute, Date datetime)
    {
    	switch(lri)
    	{
		    case OperationHealth: //INV_STATUS:
                this.DeviceStatus = attribute;
		
                LOGGER.debug(String.format("%-12s: '%s' %s", "INV_STATUS", TagDefs.GetInstance().getDesc(this.DeviceStatus, "?"), datetime.toString()));
		        break;
		
		    case OperationGriSwStt: //INV_GRIDRELAY
                this.GridRelayStatus = attribute;
		
                LOGGER.debug(String.format("%-12s: '%s' %s", "INV_GRIDRELAY", TagDefs.GetInstance().getDesc(this.GridRelayStatus, "?"), datetime.toString()));
		        break;
		        
	        case NameplateMainModel: //INV_CLASS
	            
                this.DevClass = InvDeviceClass.intToEnum(attribute);
				String devclass = TagDefs.GetInstance().getDesc(attribute);
				if (!devclass.isEmpty()) {
					this.DeviceClass = devclass;
				} else
				{
					this.DeviceClass = "UNKNOWN CLASS";
                    LOGGER.warn("Unknown Device Class. Report this issue at https://sbfspot.codeplex.com/workitem/list/basic with following info:");
                    LOGGER.warn(String.format("0x%08lX and Device Class=...", attribute));
                }
	
				LOGGER.debug(String.format("%-12s: '%s' %s", "INV_CLASS", this.DeviceClass, datetime));
	            break;
	            
	        case NameplateModel: //INV_TYPE
	            
				String devtype = TagDefs.GetInstance().getDesc(attribute);
				if (!devtype.isEmpty()) {
					this.DeviceType = devtype;
				} else
				{
					this.DeviceType = "UNKNOWN TYPE";
					LOGGER.warn("Unknown Inverter Type. Report this issue at https://sbfspot.codeplex.com/workitem/list/basic with following info:");
					LOGGER.warn(String.format("0x%08lX and Inverter Type=<Fill in the exact type> (e.g. SB1300TL-10)", attribute));
				}
	
				LOGGER.debug(String.format("%-12s: '%s' %s", "INV_TYPE", this.DeviceType, datetime));
	            break;
	        default:
	        	LOGGER.error("Wrong enum given to this method (SetInverterDataAttribute), {}", lri);
				break;
    	}
    }
    
    public void SetInverterDataCls(LriDef lri, int value, long cls, Date datetime)
    {
    	String strWatt = "%-12s: %d (W) %s";
	    String strVolt = "%-12s: %.2f (V) %s";
	    String strAmp = "%-12s: %.3f (A) %s";
	    
    	switch(lri)
    	{
		    case DcMsWatt: //SPOT_PDC1 / SPOT_PDC2
		        if (cls == 1)   // MPP1
		        {
		            this.Pdc1 = value;
		            LOGGER.debug(String.format(strWatt, "SPOT_PDC1", value, datetime));
		        }
		        if (cls == 2)   // MPP2
		        {
		            this.Pdc2 = value;
		            LOGGER.debug(String.format(strWatt, "SPOT_PDC2", value, datetime));
		        }
		
		        break;
		
		    case DcMsVol: //SPOT_UDC1 / SPOT_UDC2
		        if (cls == 1)
		        {
		            this.Udc1 = value;
		            LOGGER.debug(String.format(strVolt, "SPOT_UDC1", Misc.toVolt(value), datetime));
		        }
		        if (cls == 2)
		        {
		            this.Udc2 = value;
		            LOGGER.debug(String.format(strVolt, "SPOT_UDC2", Misc.toVolt(value), datetime));
		        }
		
		        break;
		
		    case DcMsAmp: //SPOT_IDC1 / SPOT_IDC2
		        if (cls == 1)
		        {
		            this.Idc1 = value;
		            LOGGER.debug(String.format(strAmp, "SPOT_IDC1", Misc.toAmp(value), datetime));
		        }
		        if (cls == 2)
		        {
		            this.Idc2 = value;
		            LOGGER.debug(String.format(strAmp, "SPOT_IDC2", Misc.toAmp(value), datetime));
		        }
		        break;
		default:
			LOGGER.error("Wrong enum given to this method (SetInverterDataCls), {}", lri);
			break;
    	}
    }
    
    public void SetInverterData64(LriDef lri, long value64, Date datetime)
    {
	    String strkWh = "%-12s: %.3f (kWh) %s";
	    String strHour = "%-12s: %.3f (h) %s";
	    
    	switch(lri)
    	{
		    case MeteringTotWhOut: //SPOT_ETOTAL
		        this.ETotal = value64;
		
		        LOGGER.debug(String.format(strkWh, "SPOT_ETOTAL", Misc.tokWh(value64), datetime));
		        break;
		
		    case MeteringDyWhOut: //SPOT_ETODAY
		        //This function gives us the current inverter time
		        this.InverterDatetime = datetime.getTime();
		        this.EToday = value64;
		
		        LOGGER.debug(String.format(strkWh, "SPOT_ETODAY", Misc.tokWh(value64), datetime));
		        break;
		
		    case MeteringTotOpTms: //SPOT_OPERTM
		        this.OperationTime = value64;
		
		        LOGGER.debug(String.format(strHour, "SPOT_OPERTM", Misc.toHour(value64), datetime));
		        break;
		
		    case MeteringTotFeedTms: //SPOT_FEEDTM
		        this.FeedInTime = value64;
		
		        LOGGER.debug(String.format(strHour, "SPOT_FEEDTM", Misc.toHour(value64), datetime));
		        break;
		default:
			LOGGER.error("Wrong enum given to this method (SetInverterData64), {}", lri);
			break;
    	}
    }
}
