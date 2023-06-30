package ch.hevs.isi.core;

import java.util.HashMap;
import java.util.Map;

/**
 * This class defines the generic datapoint extracted from the minecraft world
 */
public abstract class DataPoint {

    private static Map<String, DataPoint> dataPointMap=new HashMap<>();
    private String  label;
    private Boolean isOutput;

    /**
     * Constructor of the datapoint object
     * @param label : name of the datapoint that we create
     * @param isOut : set if the datapoint is an output or not
     */
    protected DataPoint(String label, Boolean isOut)
    {
        this.isOutput=isOut;
        this.label=label;
        dataPointMap.put(label, this);
    }

    /**
     * Function called when a value is updated in a datapoint
     */
    protected void update()
    {
            ListenersRegistry.getInstance().updateConnectors(this);
            //DatabaseConnector.getInstance().onNewValue(this);
    }

    /**
     * This method provides a specific datapoint when called
     * @param label : The label of the dataPoint
     * @return The datapoint
     */
    public static DataPoint getDataPointFromLabel(String label)
    {
        return dataPointMap.get(label)==null?null:dataPointMap.get(label);
    }

    /**
     * This method provides the label of a datapoint
     * @return The label corresponding to a datapoint
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * This method is used to determine if a datapoint is an output
     * @return a Boolean which is true if the datapoint is an output and false if it is not
     */
    public Boolean isOutput()
    {
        return isOutput;
    }
}
