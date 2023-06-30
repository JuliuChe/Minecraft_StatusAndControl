package ch.hevs.isi.db;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * TimeManager Class
 * This class allows to calculate a timestamp taking into account the time index of Minecraft Electrical Age and its
 * time acceleration. (1 day = 10 minutes)
 * You also have to indicate a number of days to subtract from the current time which will determine the time t0 which
 * defines the starting time of the simulation.
 */
public class TimeManager {

    // Start time
    private final LocalDateTime t0;

    // days since beginning of game
    private long days;

    // previous value of "CLOCK_FLOAT" coming from Minecraft EA
    private float lastEaClock;

    // timestamp for InfluxDB
    private long nanosForDB;


    /* ************************************************************************************************************ */
    /* Constructors                                                                                                 */
    /* ************************************************************************************************************ */
    /**
     * Constructor of TimeManager class, where the start time of the game is calculated from today at midnight minus
     * the given number of days. Today at midnight, from local time.
     *
     * @param minusDays The number of days to subtract to the current day.
     */
    public TimeManager(int minusDays) {
        t0 = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).minusDays(minusDays);
        days = 0;
        lastEaClock = 0f;
        nanosForDB = 0;
    }


    /* ************************************************************************************************************ */
    /* Public methods                                                                                               */
    /* ************************************************************************************************************ */
    /**
     * This method takes the index of the Electrical-Age clock and generates the corresponding timestamp.
     *
     * @param strEaClock The EA clock given as a {@code String}
     */
    public void setTimestamp(String strEaClock) {
        setTimestamp(Float.parseFloat(strEaClock));
    }

    /**
     * This method takes the index of the Electrical-Age clock and generates the corresponding timestamp.
     *
     * @param eaClock   The EA clock given as a {@code float}
     */
    public void setTimestamp(float eaClock) {
        // eaClock: value read in data point with label "CLOCK_FLOAT", if it's less than the previous one, it means
        // that a day has passed !
        if (eaClock < lastEaClock)
            days++;

        // Storing the EA Clock and computing the corresponding nanoseconds...
        lastEaClock = eaClock;
        long nanoOfDay = Math.round(eaClock * (24 * 3600E9));

        // Creates a timestamp from t0 and adding to it the passed days and the nanoseconds of the current days
        LocalDateTime tInsert = t0.plusDays(days);
        tInsert = tInsert.plus(Duration.ofNanos(nanoOfDay));
        //System.out.println("tInsert in method: " + tInsert.format(DateTimeFormatter.ISO_DATE_TIME));

        // Getting the related timestamp in nanoseconds according to the local time zone
        ZoneId zoneId = ZoneId.systemDefault();
        nanosForDB = tInsert.atZone(zoneId).toEpochSecond() * 1000000000;
    }

    /**
     * Gives the actual timestamp according to the EA clock.
     *
     * @return  The timestamp in nanoseconds
     */
    public long getNanosForDB() {
        return nanosForDB; // nanos for writing in DB
    }

    /**
     * Returns {@code true}
     * @return
     */
    public boolean isLastDay() {
        return (days % 3) == 2;
    }

    /* ************************************************************************************************************ */
    /*                                                                                                              */
    /* Public Main                                                                                                  */
    /*                                                                                                              */
    /* ************************************************************************************************************ */
    public static void main(String[] args) {
        TimeManager tm = new TimeManager(3);
        System.out.println("t0: " + tm.t0.toString());
        tm.setTimestamp(0.5f);
        System.out.println("nanosForDBs: " + tm.getNanosForDB());
        tm.setTimestamp("0.1");
        System.out.println("nanosForDBs: " + tm.getNanosForDB());
    }
}
