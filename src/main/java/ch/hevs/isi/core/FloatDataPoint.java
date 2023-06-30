package ch.hevs.isi.core;

public class FloatDataPoint extends DataPoint {

    private float value;

    /**
     * This is the constructor for a FloatDatapoint
     *  @param label : the label/name of the datapoint
     *  @param isOutput : whether the datapoint is anOutput : set to true
     */
    public FloatDataPoint(String label, Boolean isOutput)
    {
        super(label, isOutput);
        value = Float.NaN;
        System.out.println("New Float value constructed !");
    }

    /**
     * This is a setter for the float value of a datapoint
     * This method is called when a value needs to be assigned to a datapoint
     * @param val : the value of the float to set
     */
    public void setValue(float val)
    {
        if(val!=this.value)
        {
            this.value=val;
        }
        update();
    }

    /**
     * This is a getter to the FloatDataPoint value
     * This shall be used whenever the value of the FloatDataPoint is needed
     * @return a float representing the value of the FloatDataPoint
     */
    public Float getValue()
    {
        return value;
    }

    /**
     * This method is an overwrite of the standard object method which returns the address of the object
     * In this case this method "stringify" the FloatDataPoint object
     * @return a String representing the float value of the FloatDataPoint
     */
    public String toString()
    {
     return String.valueOf(value);
    }
}
