package ch.hevs.isi.field;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.locator.BaseLocator;

/**
 * This class is used to create a ModBus link and Write/Read
 * a float or a boolean value
 */
public class ModbusAccessor {
    private static ModbusAccessor mb_Access = null;
    private ModbusMaster mb_Master;

    private ModbusAccessor()
    {
    }

    /**
     *  This class create a singleton of ModBusAccessor
     * @return mb_Access : the singleton of ModbusAccessor
     */
    public static ModbusAccessor getInstance()
    {
        if(mb_Access==null)
        {
            mb_Access = new ModbusAccessor();   //Create a new modbus accessor
        }
        return mb_Access;
    }

    /**
     * This class connect the modbusAccessor to a given IP address and port using the TCP protocol
     * @param ipAddress : the IP address of the server
     * @param port      : the port of the server
     * @return tell whether we are connected or not
     */
    public boolean connect(String ipAddress, int port)
    {
        IpParameters parameters = new IpParameters();
        parameters.setHost(ipAddress);
        parameters.setPort(port);

        //Create a new modbus factory
        ModbusFactory mb_Fact = new ModbusFactory();

        //Create the master (connection informations to the field), returns a ModbusMaster
        mb_Master = mb_Fact.createTcpMaster(parameters, true);
        try
        {
            //
            mb_Master.init();
            return true;
        } catch (ModbusInitException e)
        {
            return false;
        }
    }

    /**
     * This function will read a float value given a register Address
     * @param regAddress : the address of the register we want to know what's the value of
     * @return the float value stocked in the register
     */
    public Float readFloat(int regAddress)
    {
        try
        {
            return mb_Master.getValue(BaseLocator.holdingRegister(1, regAddress, DataType.FOUR_BYTE_FLOAT)).floatValue();
        }
        catch (ModbusTransportException e)
        {
            e.printStackTrace();
            System.exit(10);
        }
        catch (ErrorResponseException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    /**
     * This function will change the value inside a register
     * @param regAddress : the address of the register we want to change the value of
     * @param newValue : the new value inside the register
     */
    public void writeFloat(int regAddress, float newValue)
    {
        try
        {
            mb_Master.setValue(BaseLocator.holdingRegister(1, regAddress, DataType.FOUR_BYTE_FLOAT), newValue);
        }
        catch (ModbusTransportException e)
        {
            throw new RuntimeException(e);
        }
        catch (ErrorResponseException e)
        {
                e.printStackTrace();
                System.exit(1);
        }
    }

    /**
     * This function will read a boolean value given a register Address
     * @param regAddress : the address of the register we want to know what's the value of
     * @return the boolean value of the register
     */
    public boolean readBoolean(int regAddress)
    {
        try {
            return mb_Master.getValue(BaseLocator.coilStatus(1, regAddress));
        }
        catch (ModbusTransportException e)
        {
            throw new RuntimeException(e);
        }
        catch (ErrorResponseException e)
        {
            throw  new Error(e);
        }
    }
    /**
     * This function will change the value inside a register
     * @param regAddress : the address of the register we want to change the value of
     * @param newValue : the new value inside the register
     */
    public void writeBoolean(int regAddress, boolean newValue)
    {
        try
        {
            mb_Master.setValue(BaseLocator.coilStatus(1, regAddress), newValue);
        }
        catch (ModbusTransportException e)
        {
            throw new RuntimeException(e);
        }
        catch (ErrorResponseException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
