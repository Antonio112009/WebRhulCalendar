package launch.runnable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Threads {

    public void start() {
        Task task = new Task();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);
        executorService.scheduleAtFixedRate(task::downloadCalendars, 0, 1, TimeUnit.MINUTES);
    }
}
