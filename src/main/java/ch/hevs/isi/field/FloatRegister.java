package ch.hevs.isi.field;

import ch.hevs.isi.core.FloatDataPoint;

public class FloatRegister extends ModbusRegister{
    private FloatDataPoint fDP;
    private final int range;
    private final int offset;

    /**
     * This function will construct a float register
     * @param label : the name of the register
     * @param isOutput : tell whether this is an output or not
     * @param address : the address of the register
     * @param range : the range of the value
     * @param offset : the offset of th value
     */
    public FloatRegister(String label, boolean isOutput, int address, int range, int offset)
    {
        super(address);
        fDP = new FloatDataPoint(label, isOutput);
        this.range = range;
        this.offset = offset;
        dpRegMap.put(fDP, this);
        System.out.println("Created a floatRegister with label: " + label + ", output set to : " + isOutput + ", address : " + address + ", range: " + range + ", offset: " + offset );
    }

    /**
     * This function will read the value of a register in minecraft(between 0 and 1), adapt it to a real value
     * and save the value in a datapoint
     */
    @Override
    public void read()
    {
        fDP.setValue(ModbusAccessor.getInstance().readFloat(getAddress()) * range + offset);
    }

    /**
     * This function will read the value of a datapoint, adapt it to a minecraft value (between 0 and 1)
     * and write the value in minecraft
     */
    @Override
    public void write()
    {
        ModbusAccessor.getInstance().writeFloat(getAddress(), (fDP.getValue() - offset) / range);
    }
}
