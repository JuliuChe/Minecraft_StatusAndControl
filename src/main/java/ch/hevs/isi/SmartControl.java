package ch.hevs.isi;

import ch.hevs.isi.core.*;
import ch.hevs.isi.db.DatabaseConnector;
import ch.hevs.isi.field.FieldConnector;
import ch.hevs.isi.web.WebConnector;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * This class is responsible for managing the Minecraft world. It also sets uo the database and web connectors.
 * Its purpose is to read the values of the datapoints in the minecraft world and take decision and write back values to the minecraft world.
 * It is "smart", as it takes decision based on the values of the datapoints.
 */
public class SmartControl extends TimerTask
{
    private static FloatDataPoint GRID_U_FLOAT;
    private static FloatDataPoint BATT_CHRG_FLOAT;
    private static FloatDataPoint SOLAR_P_FLOAT;
    private static FloatDataPoint COAL_P_FLOAT;
    private static FloatDataPoint COAL_AMOUNT;
    private static FloatDataPoint HOME_P_FLOAT;
    private static FloatDataPoint PUBLIC_P_FLOAT;
    private static FloatDataPoint FACTORY_P_FLOAT;
    private static FloatDataPoint BUNKER_P_FLOAT;
    private static FloatDataPoint CLOCK_FLOAT;
    private static FloatDataPoint WIND_P_FLOAT;
    private static FloatDataPoint REMOTE_FACTORY_SP=((FloatDataPoint) DataPoint.getDataPointFromLabel("REMOTE_FACTORY_SP"));
    private static FloatDataPoint REMOTE_COAL_SP=((FloatDataPoint) DataPoint.getDataPointFromLabel("REMOTE_COAL_SP"));
    private static BooleanDataPoint REMOTE_SOLAR_SW = ((BooleanDataPoint) DataPoint.getDataPointFromLabel("REMOTE_SOLAR_SW"));
    private static BooleanDataPoint REMOTE_WIND_SW = ((BooleanDataPoint) DataPoint.getDataPointFromLabel("REMOTE_WIND_SW"));

    private static FloatDataPoint SCORE;


    private int mode = 0;
    private static float lastClock=0;
    private static float maxCoalPower;
    private static float remainingTime=2.5f;
    private static float[] delta;
    private static int nDelta=0;

    private static final int nfilt=10;

    /**
     * This function will be called each x time (x defined in StartWorld) and will read every register in the map
     * And take decisions that will update the datapoints and then the Field ...
     */
    @Override
    public void run() {
        runWorld();
        System.out.println("SmartControl.runWorld() called " + new Date());
    }

    /**
     * This function sets up a timer (i.er. Thread) that will instantiate a SmartControl object every x time
     * @param time : time between each call of the function
     */
    public static void startPolling(int time)
    {
        Timer tm = new Timer();
        tm.scheduleAtFixedRate(new SmartControl(), 0, time);
    }

    private SmartControl() {

    }

    /**
     * This function simply the world and sets up everything :
     * It instantiates a database connector, a field connector and a web connector
     * It also initializes all the datapoints of the minecraft world to be used later.
     * It also sets up the starting values for the factory the coal factory, the solar panels and the wind turbines
     * It takes the following arguments
     * @param dbProtocol : The protocol of connection to the database (http or https)
     * @param dbHostName : the hoatname of the database (e.g. eu-central-1-1.aws.cloud2.influxdata.com)
     * @param dbOrg : the organization of the database (e.g. SIn14)
     * @param dbBucket : the bucket of the database (e.g. SIn14)
     * @param dbToken : The Token to acccess the database
     * @param modbusTcpHost : the hostname of the modbus server (e.g. localhost)
     * @param modbusTcpPort: the port of the modbus server (e.g. 1502)
     * @param dbName : The name of the database (e.g. Minecraft)
     */
    public static void startWorld(String dbProtocol, String dbHostName, String dbOrg, String dbBucket, String dbToken, String modbusTcpHost, int modbusTcpPort, String dbName) {

        FieldConnector.getInstance().initialize(modbusTcpHost, modbusTcpPort);
        FieldConnector.getInstance().startPolling(3000);
        DatabaseConnector.getInstance().initialize(dbProtocol, dbHostName, dbOrg, dbBucket, dbToken, dbName);
        WebConnector.getInstance();


        GRID_U_FLOAT=((FloatDataPoint) DataPoint.getDataPointFromLabel("GRID_U_FLOAT"));
        BATT_CHRG_FLOAT=((FloatDataPoint) DataPoint.getDataPointFromLabel("BATT_CHRG_FLOAT"));
        SOLAR_P_FLOAT=((FloatDataPoint) DataPoint.getDataPointFromLabel("SOLAR_P_FLOAT"));
        COAL_P_FLOAT=((FloatDataPoint) DataPoint.getDataPointFromLabel("COAL_P_FLOAT"));
        COAL_AMOUNT=((FloatDataPoint) DataPoint.getDataPointFromLabel("COAL_AMOUNT"));
        HOME_P_FLOAT=((FloatDataPoint) DataPoint.getDataPointFromLabel("HOME_P_FLOAT"));
        PUBLIC_P_FLOAT=((FloatDataPoint) DataPoint.getDataPointFromLabel("PUBLIC_P_FLOAT"));
        FACTORY_P_FLOAT=((FloatDataPoint) DataPoint.getDataPointFromLabel("FACTORY_P_FLOAT"));
        BUNKER_P_FLOAT=((FloatDataPoint) DataPoint.getDataPointFromLabel("BUNKER_P_FLOAT"));
        CLOCK_FLOAT=((FloatDataPoint) DataPoint.getDataPointFromLabel("CLOCK_FLOAT"));
        WIND_P_FLOAT=((FloatDataPoint) DataPoint.getDataPointFromLabel("WIND_P_FLOAT"));

        REMOTE_FACTORY_SP=((FloatDataPoint) DataPoint.getDataPointFromLabel("REMOTE_FACTORY_SP"));
        REMOTE_COAL_SP=((FloatDataPoint) DataPoint.getDataPointFromLabel("REMOTE_COAL_SP"));

        REMOTE_SOLAR_SW = ((BooleanDataPoint) DataPoint.getDataPointFromLabel("REMOTE_SOLAR_SW"));
        REMOTE_WIND_SW = ((BooleanDataPoint) DataPoint.getDataPointFromLabel("REMOTE_WIND_SW"));
        SCORE = ((FloatDataPoint) DataPoint.getDataPointFromLabel("SCORE"));

        //Start the world by activating the solar panels and the wind turbine
        REMOTE_SOLAR_SW.setValue(true);
        REMOTE_WIND_SW.setValue(true);
        REMOTE_FACTORY_SP.setValue(1f);
        REMOTE_COAL_SP.setValue(1f);

        maxCoalPower=500f;

        lastClock=CLOCK_FLOAT.getValue();
        delta=new float[nfilt];
        startPolling(3000);

    }




    /* This method controls the Minecraft world based on its current state and take actions accordingly
    There is 4 different mode of runing this world :
    mode 0 : The world is running normally
    mode 1 : The world is running in a "battery saving" mode
    mode 2 : The world is running in a "battery charging" mode
    mode 3 : The world is running in a "battery discharging" mode, this simply boost the production of the factory if we have some coal left and battery charge is sufficient

     */
    private void runWorld() {
        if(CLOCK_FLOAT.getValue()-lastClock>0){
        remainingTime=remainingTime-(CLOCK_FLOAT.getValue()-lastClock);}

        lastClock=CLOCK_FLOAT.getValue();

    if(!Float.isNaN(SCORE.getValue()) && SCORE.getValue()!=0)
    {
        System.out.println("World is finished running, final SCORE is : " + SCORE.getValue());
        System.out.println("Leaving Java program ...");
        System.exit(123);
    }

        if(GRID_U_FLOAT.getValue()>=950 && BATT_CHRG_FLOAT.getValue()>=1)
        {
            System.out.println("World is dead overCharge of the batteries");
            System.exit(100);

        }
        if(GRID_U_FLOAT.getValue()<=700 && BATT_CHRG_FLOAT.getValue()<=0)
        {
            System.out.println("World is dead batteries fully discharged");
            System.exit(0);
        }

        float consumer=(HOME_P_FLOAT.getValue())+(PUBLIC_P_FLOAT.getValue())+(FACTORY_P_FLOAT.getValue())+(BUNKER_P_FLOAT.getValue());
        float producer=(SOLAR_P_FLOAT.getValue())+(WIND_P_FLOAT.getValue())+COAL_P_FLOAT.getValue();
        float battery=BATT_CHRG_FLOAT.getValue();
        float coal=COAL_AMOUNT.getValue();


        float maxFac=((coal*300000f)/(remainingTime*60f*10f*maxCoalPower))*0.90f;

        if(maxFac>1)
        {
            maxFac=1;
        }

        if(nDelta==(nfilt-1)){
            nDelta=0;
        }

        delta[nDelta] = (producer - consumer);
        nDelta+=1;

        float meanDelta=0;
        for (float v : delta) {
            meanDelta += v;
        }
        meanDelta/=nfilt;

        switch(mode){
            case 0: //Normal Mode

                if(remainingTime<=0.5f)
                {

                    mode=3;

                }
                if(BATT_CHRG_FLOAT.getValue()>0.92f){

                    mode=2;
                }
                    if(BATT_CHRG_FLOAT.getValue()<0.6f)
                    {
                        mode=1;
                    }

                    if(!REMOTE_SOLAR_SW.getValue() || !REMOTE_WIND_SW.getValue())
                    {
                        REMOTE_SOLAR_SW.setValue(true);
                        REMOTE_WIND_SW.setValue(true);
                    }

                    if(nDelta==9 || nDelta==4 ){
                        if (meanDelta > 0) {
                            System.out.println("Mean Delta is : " + (meanDelta/3100));
                            if(REMOTE_FACTORY_SP.getValue()>0 ){
                                if(REMOTE_FACTORY_SP.getValue()<1) {

                                    REMOTE_FACTORY_SP.setValue(REMOTE_FACTORY_SP.getValue() * 1.1f);
                                }
                                else
                                {
                                    REMOTE_FACTORY_SP.setValue(1);
                                }
                            }
                            else{
                                REMOTE_FACTORY_SP.setValue(meanDelta/3100);
                            }
                        }
                        else
                        {
                                REMOTE_FACTORY_SP.setValue(REMOTE_FACTORY_SP.getValue()*0.9f);
                            }
                        }


                  if (meanDelta > 0) {

                          if (REMOTE_COAL_SP.getValue()>=maxFac) {
                              REMOTE_COAL_SP.setValue(maxFac);
                          }
                          else {
                              REMOTE_COAL_SP.setValue(REMOTE_COAL_SP.getValue()*0.8f);
                          }
                  }
                  else {

                          if (REMOTE_COAL_SP.getValue()>=maxFac) {
                              REMOTE_COAL_SP.setValue(maxFac);
                          }
                          else {
                              REMOTE_COAL_SP.setValue(REMOTE_COAL_SP.getValue()*1.2f);
                          }
                  }


                break;
            case 1: //Battery too low

                if(battery<0.5f)
                {
                        REMOTE_COAL_SP.setValue(1f);
                    REMOTE_FACTORY_SP.setValue(0f);
                }
                else if(BATT_CHRG_FLOAT.getValue()<0.6f)
                {
                    REMOTE_COAL_SP.setValue(maxFac);
                    REMOTE_FACTORY_SP.setValue(0f);

                }
                else {
                    mode = 0;
                }

                break;
            case 2: //Battery too high
                if(battery>0.92f)
                {
                    REMOTE_COAL_SP.setValue(0f);
                    REMOTE_WIND_SW.setValue(false);
                    REMOTE_FACTORY_SP.setValue(1f);
                }
                else if (BATT_CHRG_FLOAT.getValue()<0.90f){
                    mode = 0;
                }
                break;
            case 3: //Battery Boost
                if(battery<0.5f)
                {
                    REMOTE_COAL_SP.setValue(2*maxFac);
                    REMOTE_FACTORY_SP.setValue(0f);
                }
                else if(lastClock>0.74f)
                {
                    REMOTE_COAL_SP.setValue(maxFac);
                    REMOTE_FACTORY_SP.setValue(battery);
                }
                else {
                    mode = 0;
                }
                break;
            default:
                break;
        }

        System.out.println(" Coal amount is : " + COAL_AMOUNT.getValue()+ " Max Coal Factory setpoint is : " + maxFac);
        System.out.println("Battery Charge is : " + BATT_CHRG_FLOAT.getValue());
        System.out.println("Coal Factory setpoint is : " + REMOTE_COAL_SP.getValue());
        System.out.println("Factory setpoint is : " + REMOTE_FACTORY_SP.getValue());


    }
}
