package ch.hevs.isi.field;

import ch.hevs.isi.core.BooleanDataPoint;

public class BooleanRegister extends ModbusRegister {
    private BooleanDataPoint bDP;

    /**
     * Constructor of the boolean register
     * @param label: the name of the register
     * @param isOutput: tell whether the register is an output
     * @param address: the address of the register
     */
    public BooleanRegister(String label, boolean isOutput, int address)
    {
        super(address);
        bDP = new BooleanDataPoint(label, isOutput);
        dpRegMap.put(bDP, this);
        System.out.println("Created a booleanRegister with label: " + label + ", output set to : " + isOutput + ", address : " + address );

    }

    /**
     *  Read the value of a register in minecraft and save it in a datapoint
     */
    @Override
    public void read()
    {
        bDP.setValue(ModbusAccessor.getInstance().readBoolean(getAddress()));
    }

    /**
     * Read the value in a datapoint and write it in minecraft
     */
    @Override
    public void write()
    {
        ModbusAccessor.getInstance().writeBoolean(getAddress(), bDP.getValue());
    }

}
