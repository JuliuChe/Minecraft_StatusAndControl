package ch.hevs.isi.db;

import ch.hevs.isi.core.DataPoint;
import ch.hevs.isi.core.DataPointListener;
import ch.hevs.isi.core.ListenersRegistry;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class represent a connection to the database in which all the value of the minecraft world are stored
 * This class allows the user to connect to its own database and stores the values of the Minecraft world
 * It implements the DataPointListener Interface
 */
public class DatabaseConnector implements DataPointListener {

    private static DatabaseConnector dbc=null;
    private String Protocol;
    private String Hostname;
    private String Org;
    private String Bucket;
    private String Token;
    private TimeManager _timeManager;
    private long _timeStamp=0;
    private String dbName;
    private final static int BACK_DAYS=3;

    /**
     * This is a private constructor as this class is instanced as a singleton.
     * The constructor is called by the getInstance() method.
     */
    private DatabaseConnector()
    {
        ListenersRegistry.getInstance().subscribe(this);
        _timeManager= new TimeManager(BACK_DAYS);
    }

    /**
     * This method provides an instance of the Database connector (i.e. the dbc variable).
     * This method shall be called within the singleton pattern
     * @return a pointer to the database connector
     */
    public static DatabaseConnector getInstance()
    {
        if(dbc==null) {
            dbc = new DatabaseConnector();
        }
        return dbc;
    }


    /**
     * This method gives the details of the location of the Influx DB to which it connects to. It then initializes the connector
     * @param dbProtocol : This is the protocol of access to the database typically HTTP /HTTPS
     * @param dbHostName : This is the address (IP/DNS) of the database typically influx.sdi.hevs.ch
     * @param dbOrg : This is the organization of the Influx DB in this case the organization is SIn14
     * @param dbBucket : This is the bucket of the database
     * @param dbToken : This is the token (unique identifier allowing access to the database) of the InfluxDB
     * @param dbName : This is the name of the simulation
     */
    public void initialize(String dbProtocol,String dbHostName,String dbOrg,String dbBucket,String dbToken, String dbName)
    {
        Protocol = dbProtocol;
        Hostname=dbHostName;
        Org=dbOrg;
        Bucket=dbBucket;
        Token = dbToken;
        this.dbName = dbName;
    }

    /**
     * This updates the Database
     * It connects to the database and feed it with the DataPoint label and value
     * @param dp : a datapoint from the minecraft world
     */
    @Override
    public void onNewValue(DataPoint dp) {

        //System.out.println("DataBase Connector onNewValue() : " + dp.getLabel() + " value is :" + dp);
        if(dp.getLabel().equals("CLOCK_FLOAT"))
        {
            _timeManager.setTimestamp(dp.toString());
            _timeStamp=_timeManager.getNanosForDB();
        }
        if(_timeStamp!=0)
        {
            pushToDatabase(dp);
        }


    }

    /**
     * This method erase the database, if it exists
     */
    public void eraseDatabase()
    {
        try{
            URL url = new URL(Protocol, Hostname, "/api/v2/delete?org=" +Org + "&bucket=" + Bucket);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Token " +Token);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            String req="{\n\"start\": \"2000-01-01T00:00:00Z\",\n\"stop\": \"2023-06-25T10:45:00Z\",\n\"predicate\": \"_measurement=\\\""+dbName+"\\\"\"\n}";
            writer.write(req);
            writer.flush();
            int respCode = connection.getResponseCode();
            if(respCode!=204)
            {
                System.out.println(respCode);
            }
            writer.close();
            connection.disconnect();
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called by the onNewValue method and does set up a TCP/IP connection to the database in order to feed it with the Datapoint
     * @param dp : a datapoint
     */
    private void pushToDatabase(DataPoint dp) {
        try{
            URL url = new URL(Protocol, Hostname, "/api/v2/write?org=" +Org + "&bucket=" + Bucket);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Token " +Token);
            connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(dbName + " " + dp.getLabel() + "=" + dp + " " + _timeStamp);
            writer.flush();
        int respCode = connection.getResponseCode();
        if(respCode!=204)
        {
            System.out.println(respCode);
        }
        writer.close();
        connection.disconnect();
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
       // System.out.println("Pushed to database : " + dp.getLabel() + " value is : " + dp + " " + _timeStamp);
    }
}
