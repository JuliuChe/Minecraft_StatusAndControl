package ch.hevs.isi.web;

import ch.hevs.isi.core.*;
import ch.hevs.isi.utils.Utility;

/**
 * This class is used to test the WebConnector
 * This class uses the onNewValue() method to perform some specific tasks
 * In this case, it simply stops the program when the solar panel is turned off
 */
public class WebConnectorTest implements DataPointListener {

    /**
     * This method is called to add the WebConnectorTest to the ListenersRegistry
     * It subscribe it to the distribution list of the datapoints
     * This class uses the onNewValue() method to perform some specific tasks
     * In this case, it simply stops the program when the solar panel is turned off
     */
    private WebConnectorTest()
    {
        ListenersRegistry.getInstance().subscribe(this);
    }


    public static void main(String[] args) {
        new BooleanDataPoint("REMOTE_SOLAR_SW", true);
        new BooleanDataPoint("REMOTE_WIND_SW", true);
        new FloatDataPoint("REMOTE_COAL_SP", true);
        new FloatDataPoint("REMOTE_FACTORY_SP", true);
        new FloatDataPoint("GRID_U_FLOAT", false);
        ((FloatDataPoint) DataPoint.getDataPointFromLabel("GRID_U_FLOAT")).setValue(22.5f);

        new WebConnectorTest();


        WebConnector.getInstance();

        float U;
        while(true)
        {
            U=((FloatDataPoint) DataPoint.getDataPointFromLabel("GRID_U_FLOAT")).getValue();
            if(U>1000)
            {
                U=0;
            }
            else{
                U+=1;
            }
            ((FloatDataPoint) DataPoint.getDataPointFromLabel("GRID_U_FLOAT")).setValue(U);
            Utility.waitSomeTime(100);
        }
    }

    @Override
    public void onNewValue(DataPoint dp) {

        if(dp.getLabel().equals("REMOTE_SOLAR_SW")){
            if(((BooleanDataPoint)dp).getValue()){
                System.out.println("Test successful solar toggled -------------- END OF TEST");
                System.exit(0);
            }
        }
    }
}
