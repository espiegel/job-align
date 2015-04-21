package com.spiegel.jobalign;

import com.spiegel.jobalign.factory.DefaultKeyValueProvider;
import com.spiegel.jobalign.factory.DefaultLockProvider;
import com.spiegel.jobalign.factory.KeyValueProvider;
import com.spiegel.jobalign.factory.LockProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;

/**
 * Created by Eidan on 4/22/2015.
 */
@RunWith(JUnit4.class)
public class TwoSchedulersTest {
    private boolean jobPerformed = false;

    @Test
    public void testScheduler() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 3);
        int second = calendar.get(Calendar.SECOND);
        String cron = second+" * * * * ?";

        LockProvider lockProvider = new DefaultLockProvider();
        DefaultKeyValueProvider defaultKeyValueStore = new DefaultKeyValueProvider();

        BaseDistributedJob myJob1 = new MyJob(lockProvider, defaultKeyValueStore, cron);
        BaseDistributedJob myJob2 = new MyJob(lockProvider, defaultKeyValueStore, cron);

        myJob1.schedule();
        myJob2.schedule();

        Assert.assertEquals(false, jobPerformed);

        try {
            Thread.sleep(5000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(true, jobPerformed);
    }

    public class MyJob extends BaseDistributedJob {

        private final String cron;

        public MyJob(LockProvider lockProvider, KeyValueProvider keyValueProvider, String cron) {
            super(lockProvider, keyValueProvider);
            this.cron = cron;
        }

        @Override
        public void performJobLogic() {
            setJobPerformed(!jobPerformed);
        }

        @Override
        public String getCronExpression() {
            return cron;
        }
    }

    public void setJobPerformed(boolean flag) {
        jobPerformed = flag;
    }
}
