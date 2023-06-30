package ch.hevs.isi.field;

import java.util.TimerTask;

public class PollTask extends TimerTask
{
    /**
     * An override of the function run, this task will be called each x times (x defined in MinecraftController) and will read every register in the map
     */
    @Override
    public void run()
    {
        ModbusRegister.poll();
    }
}
