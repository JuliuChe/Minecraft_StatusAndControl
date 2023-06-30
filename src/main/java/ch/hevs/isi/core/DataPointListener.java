package ch.hevs.isi.core;

/**This is an interface which provides the methods to be implemented
 * by each connector
 */
public interface DataPointListener {

    /**
     * This method is called whenever a datapoint's value changes. The datapoint is then supplied to the connector
     * @param dp : a datapoint from the minecraft world
     */
    void onNewValue(DataPoint dp);
}
