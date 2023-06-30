package ch.hevs.isi.field;

import ch.hevs.isi.core.DataPoint;
import ch.hevs.isi.core.DataPointListener;
import ch.hevs.isi.core.ListenersRegistry;
import ch.hevs.isi.utils.Utility;

import java.io.*;
import java.util.Timer;

public class FieldConnector implements DataPointListener {

    private static FieldConnector fdc=null;

    private FieldConnector()
    {
        ListenersRegistry.getInstance().subscribe(this);
    }

    /**
     * This method provides an instance of the Field connector (i.e. the fdc variable).
     * This method shall be called within the singleton pattern
     * @return a pointer to the Field Connector
     */
    public static FieldConnector getInstance()
    {
        if(fdc==null) {
            fdc = new FieldConnector();
        }
        return fdc;
    }

    /**
     * This method gives the details of the location of the Minecraft world to which it connects to. It then
     * initializes the connector
     * @param url : the url of the server
     * @param port : the port of the server
     */
    public void initialize(String url, int port)
    {
        System.out.print("Connecting.....");
        boolean b= ModbusAccessor.getInstance().connect(url, port);
        while(!b)
        {
            b= ModbusAccessor.getInstance().connect(url, port);
            Utility.waitSomeTime(1000);
        }
        System.out.println("Connected");
        Utility.waitSomeTime(3000);
        createDatapoint();
    }

    /**
     * This updates the Field
     * It connects to the Minecraft world and feed it with the DataPoint label and value
     * @param dp a datapoint in the minecraft world
     */
    @Override
    public void onNewValue(DataPoint dp) {
        //System.out.println("Field Connector onNewValue() : " + dp.getLabel() + " value is :" + dp);
        pushToFieldconnector(dp);
    }

    /**
     * Push the value from the Datapoint to the FieldConnector
     * @param dp the datapoint to be pushed to the minecraft program
     */
    private void pushToFieldconnector(DataPoint dp)
    {
        ModbusRegister reg = ModbusRegister.getRegisterFromDataPoint(dp);
        if(reg== null)
        {
            System.err.println(dp.getLabel() + " is not mapped to a register");
        }
        else {
            reg.write();
        }
        //System.out.println("Pushed to Field : " + dp.getLabel() + " value is : " + dp);
    }

    /**
     * Create all the datapoint from the csv file containing all datapoints :
     * Columns names are : DataPoint NAME, TYPE (Float/Boolean), Input, Output, range, offset and register Address
     */
    private static void createDatapoint()
    {
        try(BufferedReader br = Utility.fileParser(null, "ModbusMap.csv"))
        {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null)
            {
                String[] values = line.split(";");

                if (values[1].equals("B"))
                {
                    boolean isOut;
                    isOut=values[3].equals("Y");
                    int address = Integer.parseInt(values[4]);
                    new BooleanRegister(values[0], isOut, address);
                }

                else if (values[1].equals("F"))
                {
                    boolean isOut;
                    isOut=values[3].equals("Y");
                    int address = Integer.parseInt(values[4]);
                    int range = Integer.parseInt(values[5]);
                    int offset = Integer.parseInt(values[6]);
                    new FloatRegister(values[0], isOut, address, range, offset);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (NullPointerException e)
        {
            System.err.println("File not found... Please check the path for the ModBusMap.csv file");
            System.exit(11);
        }
    }

    /**
     * called to start polling all the value from the map
     * @param time the time between 2 polls
     */
    public void startPolling(int time)
    {
        Timer tm = new Timer();
        tm.scheduleAtFixedRate(new PollTask(), time, time);
    }
}
