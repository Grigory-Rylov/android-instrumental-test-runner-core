import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import tasks.Task;

import java.util.concurrent.CountDownLatch;

/**
 * Created by grishberg on 05.09.17.
 */
public class TasksRunner {
    private final AndroidDebugBridge adb;
    private final IDevice device;
    private final CountDownLatch countDownLatch;

    public TasksRunner(AndroidDebugBridge adb,
                       IDevice device,
                       CountDownLatch countDownLatch) {
        this.adb = adb;
        this.device = device;
        this.countDownLatch = countDownLatch;
    }

    public void run(Task task) {
        task.execute();
        countDownLatch.countDown();
    }
}
