package eu.printingin3d.smalogger.api.requestvisitor;

import java.math.BigDecimal;

import eu.printingin3d.physics.Current;
import eu.printingin3d.physics.Energy;
import eu.printingin3d.physics.Frequency;
import eu.printingin3d.physics.Power;
import eu.printingin3d.physics.Temperature;
import eu.printingin3d.physics.Time;
import eu.printingin3d.physics.Voltage;

public final class ConversionConstants {
    private ConversionConstants() {}
    
    private static final BigDecimal TEMPERATURE_CONV = new BigDecimal("0.1"); 
    private static final BigDecimal VOLTAGE_CONV = new BigDecimal("0.01"); 
    private static final BigDecimal CURRENT_CONV = new BigDecimal("0.001");
    private static final BigDecimal FREQUENCY_CONV = new BigDecimal("0.01");
    
    public static Time convertTime(long value) {
        return Time.ofSecond(BigDecimal.valueOf(value));
    }
    
    public static Energy convertEnergy(long value) {
        return Energy.fromWattHour(BigDecimal.valueOf(value));
    }
    
    public static Power convertPower(int value) {
        return new Power(new BigDecimal(value));
    }
    
    public static Temperature convertTemp(int value) {
        return Temperature.fromCelsius(new BigDecimal(value).multiply(TEMPERATURE_CONV));
    }
    
    public static Voltage convertVoltage(int value) {
        return new Voltage(new BigDecimal(value).multiply(VOLTAGE_CONV));
    }
    
    public static Current convertCurrent(int value) {
        return new Current(new BigDecimal(value).multiply(CURRENT_CONV));
    }
    
    public static Frequency convertFrequency(int value) {
        return new Frequency(new BigDecimal(value).multiply(FREQUENCY_CONV));
    }
}
