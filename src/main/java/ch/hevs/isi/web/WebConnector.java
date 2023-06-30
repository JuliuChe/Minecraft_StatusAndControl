package ch.hevs.isi.web;

import ch.hevs.isi.core.*;
import ch.hevs.isi.field.FieldConnector;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class WebConnector extends WebSocketServer implements DataPointListener  {

    private static WebConnector wbc=null;

    /**
     * This method provides an instance of the WebConnector
     *  This method shall be called within the singleton pattern
     */
    private WebConnector()
    {
        super(new InetSocketAddress(8888));
        ListenersRegistry.getInstance().subscribe(this);
        start();
    }

    /**
     * This method provides an instance of the WebConnector
     * @return a pointer to the WebConnector
     */
    public static WebConnector getInstance()
    {
        if(wbc==null)
        {
            wbc = new WebConnector();
        }
        return wbc;
    }


    /**
     * This updates the WebConnector
     * It connects to the Minecraft world and get the datapoint label and value (i.e. the dp variable)
     * @param dp : a datapoint from the minecraft world
     */
    @Override
    public void onNewValue(DataPoint dp) {
       // System.out.println("Web Connector onNewValue() : " + dp.getLabel() + " value is :" + dp);
        pushToWeb(dp);
    }

    /**
     * This method pushes the value of the DataPoint to the WebConnector
     * @param dp the DataPoint to be pushed
     */
    private void pushToWeb(DataPoint dp)
    {
        broadcast(dp.getLabel()+"="+dp);
        //Alternatively, we can use the following code
        //this.getConnections().forEach(webSocket -> webSocket.send(dp.getLabel()+"="+dp));
        //System.out.println("Pushed to Web : " + dp.getLabel() + " value is : " + dp);
    }



    /**
     * This method provides an instance of the WebConnector
     * @param webSocket the socket from the client
     * @param clientHandshake the handshake from the client
     */
    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake)
    {
        System.out.println("onOpen");
        webSocket.send("Welcome Handsome !");
        FieldConnector.getInstance().startPolling(3000);

    }


    /**
     *  This method is called when a message is received from the WebConnector
     * @param webSocket : the socket from the client
     * @param s : the message received from the client
     */
    @Override
    public void onMessage(WebSocket webSocket, String s)
    {
        System.out.println("onMessage: " + s);
        String[] tab=s.split("=");
        String label = tab[0];
        String value = tab[1];
        DataPoint dp=DataPoint.getDataPointFromLabel(label);
        if(dp!=null){
        if(value.equals("true")||value.equals("false"))
        {

            ((BooleanDataPoint) dp).setValue(Boolean.parseBoolean(value));
        }
        else
        {
            ((FloatDataPoint) dp).setValue(Float.parseFloat(value) );
        }
        }
    }

    /**
     * This method is called when there is an error on the WebServerSocket.
     * The error is displayed on the console
     * @param webSocket : the socket from the client
     * @param e : the exception thrown by the error on the socket
     */
    @Override
    public void onError(WebSocket webSocket, Exception e)
    {
        System.out.println("onError");
        e.printStackTrace();
    }

    /**
     * This method is used in order for multiple clients to connect to the WebConnector
     * As webServerSocket is a thread this method is required
     * This method is called when the WebConnector is started
     */
    @Override
    public void onStart()
    {
        System.out.println("onStart");
    }

    /**
     * This method is called when a client disconnects from the WebConnector
     * It is called whenever a client disconnects from the server and display to all other connected clients the address of the disconnected client
     * @param webSocket : the socket from the client
     * @param i : the status code of the client
     * @param s : the reason of the client's disconnection
     * @param b : a boolean that indicates if the client closed the connection
     */
    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b)
    {
        broadcast("Welcome Client " + webSocket.getRemoteSocketAddress().getHostName() + " disconnected.");
        System.out.println("onClose");
    }

}
