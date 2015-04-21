package com.spiegel.jobalign;

import com.spiegel.jobalign.factory.DefaultKeyValueProvider;
import com.spiegel.jobalign.factory.DefaultLockProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;

/**
 * Created by Eidan on 4/22/2015.
 */
@RunWith(JUnit4.class)
public class SchedulerTest {

    private boolean jobPerformed = false;

    @Test
    public void testScheduler() {
        BaseDistributedJob myJob = new BaseDistributedJob(new DefaultLockProvider(), new DefaultKeyValueProvider()) {
            @Override
            public void performJobLogic() {
                setJobPerformed(true);
            }

            @Override
            public String getCronExpression() {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.SECOND, 3);
                int second = calendar.get(Calendar.SECOND);
                return second+" * * * * ?";
            }
        };
        myJob.schedule();

        Assert.assertEquals(false, jobPerformed);

        try {
            Thread.sleep(3000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(true, jobPerformed);
    }

    public void setJobPerformed(boolean flag) {
        jobPerformed = flag;
    }
}
