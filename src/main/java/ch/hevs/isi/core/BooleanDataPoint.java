package ch.hevs.isi.core;

/**
 * This class defines a boolean datapoint extracted from the minecraft world
 * This is a specialization of the DataPoint class
 */
public class BooleanDataPoint extends DataPoint {

    private Boolean value;

    /**
     * This is the constructor of a BooleanDatapoint
     * @param label : the label/name of the datapoint
     * @param isOutput : whether the datapoint is anOutput : set to true
     */
    public BooleanDataPoint(String label, Boolean isOutput)
    {
        super(label, isOutput);
        this.value=false;
        System.out.println("New Boolean value constructed !");
    }

    /**
     * This is a setter for the Boolean value (true or false) of a datapoint
     * @param val : the value of the boolean to set
     */
    public void setValue(Boolean val)
    {
        if(val!=this.value)     //If the value we want to save is not the same as the value already saved
        {
            this.value=val;
        }
        update();
    }

    /**
     * This is a getter to the BooleanDataPoint value
     * This shall be used whenever the value of the BooleanDataPoint is needed
     * @return the value of the BooleanDataPoint
     */
    public Boolean getValue()
    {
        return value;
    }

    /**
     * This method is an overwrite of the standard object method which returns the address of the object
     * In this case this method "stringify" the BooleanDataPoint object
     * @return a String representing the boolean value of the BooleanDataPoint
     */
    public String toString()
    {
     return value?"true":"false";
    }
}
