package ch.hevs.isi.field;

public class ModBusTest {

    public static void main(String[] args) {
        ModbusAccessor.getInstance();

        String ipAddress = "127.0.0.1";
        int port = 1502;

        if(!ModbusAccessor.getInstance().connect(ipAddress, port))
        {
            System.out.println("Connection failed");
            System.exit(-1);
        }
        System.out.println("Connect to ModBus server:" + ipAddress + ": "+ port);

        System.out.println("Read float value from Batt_P_float " + ModbusAccessor.getInstance().readFloat(57));

        System.out.println("Write float value in Remote_coal_sp ");
        ModbusAccessor.getInstance().writeFloat(209, 0.8f);

        System.out.println("Read float value from Remote_coal_sp " + ModbusAccessor.getInstance().readFloat(209));

        System.out.println("Read boolean value from REMOTE_SOLAR_SW " + ModbusAccessor.getInstance().readBoolean(401));

        System.out.println("Change boolean value in REMOTE_SOLAR_SW ");
        ModbusAccessor.getInstance().writeBoolean(401, false);

        System.out.println("Read boolean value from REMOTE_SOLAR_SW " + ModbusAccessor.getInstance().readBoolean(401));

    }


}
