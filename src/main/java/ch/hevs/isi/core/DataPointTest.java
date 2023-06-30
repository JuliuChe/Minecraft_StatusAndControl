package ch.hevs.isi.core;

import ch.hevs.isi.db.DatabaseConnector;
import ch.hevs.isi.field.FieldConnector;
import ch.hevs.isi.web.WebConnector;

public class DataPointTest implements DataPointListener
{

    public static void main(String[] args)
    {
        BooleanDataPoint pv= new BooleanDataPoint("PVCmd", false);
        FloatDataPoint gVolt = new FloatDataPoint("GridVoltage", true);

        DatabaseConnector.getInstance();
        FieldConnector.getInstance();
        WebConnector.getInstance();

        System.out.println("Get label from pv :" + pv.getLabel());
        System.out.println("PV Value is set to true ");
        pv.setValue(true);
        DataPoint dp=DataPoint.getDataPointFromLabel("PVCmd");
        System.out.println("Get Datapoint from label : " + dp.getLabel());
        System.out.println("Is PVCmd an output ? : " + pv.isOutput());
        System.out.println("PV Value is now : " + pv.getValue());
        System.out.println("toString on PV : " + pv);

        System.out.println("Get label from GVolt :" + gVolt.getLabel());
        System.out.println("GridVoltage Value is set to 1000.5 ");
        gVolt.setValue(1000.5f);
        dp=DataPoint.getDataPointFromLabel("GridVoltage");
        System.out.println("Get Datapoint from label GridVoltage : " + dp.getLabel());
        System.out.println("Is GridVoltage an output ? : " + gVolt.isOutput());

        System.out.println("GridVoltage Value is now : " + gVolt.getValue());
        System.out.println("toString on GridVoltage : " + gVolt);
        gVolt.setValue(995.5f);
        gVolt.setValue(995.5f);
        gVolt.setValue(995.5f);
        gVolt.setValue(995.5f);


    }

    @Override
    public void onNewValue(DataPoint dp) {
        System.out.println("test");
    }
}
