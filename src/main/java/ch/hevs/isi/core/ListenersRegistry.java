package ch.hevs.isi.core;

import java.util.Vector;

/**
 * This class stores a HashMap of DataPointListeners.
 * It stores a list of connectors to the MinecraftWorld
 */
public class ListenersRegistry {

    private static Vector<DataPointListener> listenersRegistryList = null;

    private static ListenersRegistry listenR=null;

    private ListenersRegistry()
    {
        if(listenersRegistryList ==null)
        {
            listenersRegistryList = new Vector<>();
        }
    }

    /**
     * This method is called to update the datapoint values for each connector of the hashmap
     * @param dp : The datapoint to be updated
     */
    public void updateConnectors(DataPoint dp)
    {
        for(DataPointListener i : listenersRegistryList)
        {
            i.onNewValue(dp);//call the onNewValue method of each connector
        }
    }

    /**
     * This method provides an instance of the list.
     * This method shall be called within the singleton pattern
     * @return a pointer to the list of listeners
     */
    public static ListenersRegistry getInstance()
    {
        if(listenR==null)
        {
            listenR = new ListenersRegistry();
        }
        return listenR;
    }

    /**
     * This method is called when we need to know how many listeners we have in the HashMap
     * @return the number of listeners in the list
     */
    public int sizeOfList()
    {
        return listenersRegistryList.size();
    }

    /**
     * This adds/register a connector to the Registry
     * @param dpl a DatapointListeners. The class that calls this method should implement the DataPointListener
     *            Interface and implement the OnNewValue() method
     */
    public void subscribe(DataPointListener dpl)
    {
        listenersRegistryList.add(dpl);
    }

    /**
     * This simply removes a connector from the registry (unregister)
     * @param dpl a DatapointListeners. The class that calls this method should implement the DataPointListener
     *            Interface and implement the OnNewValue() method
     * @return a boolean specifying if the DataPointListener was correctly removed from the list
     *          True : correctly removed
     *          False: not possible to remove (not in the list or not accessible)
     */
    public Boolean removeMember(DataPointListener dpl)
    {
        return listenersRegistryList.remove(dpl);
    }
}
