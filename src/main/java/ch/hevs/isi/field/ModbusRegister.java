package ch.hevs.isi.field;

import ch.hevs.isi.core.DataPoint;
import java.util.HashMap;
import java.util.Map;

abstract class ModbusRegister
{
    private int address;
    protected static Map<DataPoint, ModbusRegister>  dpRegMap = new HashMap<>();

    /**
     * Constructor of ModbusRegister
     * @param address, the address of the register
     */
    protected ModbusRegister(int address)
    {
        this.address = address;
    }

    /**
     * This function return the address of a register
     * @return address: the address of the register
     */
    public int getAddress()
    {
        return address;
    }

    /**
     * This function read the map and return the register depending on the datapoint given as parameter
     * @param dp : datapoint from which we want to know the register
     * @return the register of the datapoint
     */
    public static ModbusRegister getRegisterFromDataPoint(DataPoint dp)
    {
        return dpRegMap.get(dp);
    }

    /**
     * This function will be called each x time (x defined in MinecraftController) and will read every register in the map
     */
    static void poll()
    {
        for (ModbusRegister mr : dpRegMap.values())
        {
            mr.read();
        }
    }

    /**
     * This function will be implemented in FloatRegister and BooleanRegister
     */
    public abstract void read();

    /**
     * This function will be implemented in FloatRegister and BooleanRegister
     */
    public abstract void write();
}
