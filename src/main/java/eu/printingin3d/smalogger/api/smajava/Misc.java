package eu.printingin3d.smalogger.api.smajava;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Misc 
{
	public static final short NaN_S16 =	(short) 0x8000;		// "Not a Number" representation for SHORT (converted to 0 by SBFspot)
	public static final short NaN_U16 = (short) 0xFFFF;		// "Not a Number" representation for USHORT (converted to 0 by SBFspot)
	public static final int NaN_S32	= (int) 0x80000000L;	// "Not a Number" representation for LONG (converted to 0 by SBFspot)
	public static final int NaN_U32 = (int) 0xFFFFFFFFL;	// "Not a Number" representation for ULONG (converted to 0 by SBFspot)
	public static final long NaN_S64 = 0x8000000000000000l;	// "Not a Number" representation for LONGLONG (converted to 0 by SBFspot)
	public static final long NaN_U64 = 0xFFFFFFFFFFFFFFFFl;	// "Not a Number" representation for ULONGLONG (converted to 0 by SBFspot)
	public static final String SYM_DEGREE = "\u00b0"; //"\302\260" for linux ?
	
	public static double tokWh(long value)
	{
		return value / 1000d;
	}
	
	public static float tokW(long value)
	{
		return value / 1000f;
	}
	
	public static double toHour(long value)
	{
		return value / 3600d;	//Make sure to divide by a double value
	}
	
	public static float toCelc(long value)
	{
		return value / 100f;
	}
	
	public static float toAmp(long value)
	{
		return value / 1000f;
	}
	
	public static float toVolt(long value)
	{
		return value / 100f;
	}
	
	public static float toHz(long value)
	{
		return value / 100f;
	}

	public static String printDate(long date)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		return sdf.format(date);
	}
	
	public static String printDate(Date date)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		return sdf.format(date);
	}

	public static void HexDump(byte[] buf, int count, int radix)
	{
	    int i, j;
	    System.out.printf("--------:");
	    for (i=0; i < radix; i++)
	    {
	    	System.out.printf(" %02X", i);
	    }
	    for (i = 0, j = 0; i < count; i++)
	    {
	        if (j % radix == 0)
	        {
				/*
				if (i > 0)
				{
					for (int ii = radix; ii>0; ii--)
						System.out.print(((buf[i-ii] >= ' ') && (buf[i-ii] <= '~')) ? buf[i-ii] : '_');
				}*/
				
	            if (radix == 16) {
					System.out.printf("\n%08X: ", j);
				} else {
					System.out.printf("\n%08d: ", j);
				}
	        }
	        System.out.printf("%02X ", buf[i]);
	        j++;
	    }
	    System.out.printf("\n");
	}
}
