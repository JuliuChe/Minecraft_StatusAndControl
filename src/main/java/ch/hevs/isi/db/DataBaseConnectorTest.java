package ch.hevs.isi.db;

import ch.hevs.isi.core.FloatDataPoint;

import static ch.hevs.isi.MinecraftController.usage;

public class DataBaseConnectorTest {
    public static void main(String[] args) {
        String dbProtocol       = "http";
        String dbHostName       = "localhost";
        String dbOrg           = "labo";
        String dbBucket       = "root";
        String dbToken       = "root";

        // Check the number of arguments and show usage message if the number does not match.
        String[] parameters = null;

        // If there is only one number given as parameter, construct the parameters according the group number.
        if (args.length >= 5) {
            parameters = args;

            // Decode parameters for influxDB
            String[] dbParams = parameters[0].split("://");
            if (dbParams.length != 2) {
                usage();
            }

            dbProtocol    = dbParams[0];
            dbHostName    = dbParams[1];
            dbOrg        = parameters[1];
            dbBucket    = parameters[2];
            dbToken    = parameters[3];


        DatabaseConnector.getInstance().initialize(dbProtocol, dbHostName, dbOrg, dbBucket, dbToken, "Test");

        FloatDataPoint tm = new FloatDataPoint("CLOCK_FLOAT", true);
        tm.setValue(0f);

        FloatDataPoint fdata=new FloatDataPoint("TestPoint", false);
        fdata.setValue(0.123f);
        tm.setValue(0.1f);
        fdata.setValue(0.128f);
        tm.setValue(0.2f);
        fdata.setValue(0.120f);
        tm.setValue(0.3f);
        fdata.setValue(0.130f);
        } else {
            usage();
        }
}}
