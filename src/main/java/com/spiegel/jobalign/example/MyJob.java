package com.spiegel.jobalign.example;

import com.spiegel.jobalign.BaseDistributedJob;
import com.spiegel.jobalign.DistributedJobBuilder;
import com.spiegel.jobalign.factory.RedisProvider;
import org.redisson.Redisson;

/**
 * Created by Eidan on 4/22/2015.
 */
public class MyJob extends BaseDistributedJob {

    public static void main(String[] args) {
        RedisProvider redisProvider = new RedisProvider(Redisson.create());
        BaseDistributedJob myJob = new DistributedJobBuilder()
                .setLockProvider(redisProvider)
                .setKeyValueProvider(redisProvider)
                .setCronExpression("0 0 * * * ?")
                .setJobLogic(() -> System.out.println("Job performed!"))
                .build();
        myJob.schedule();
    }

    public MyJob() {
        RedisProvider redisProvider = new RedisProvider(Redisson.create());
        setKeyValueProvider(redisProvider);
        setLockProvider(redisProvider);

        schedule();
    }

    @Override
    public void performJobLogic() {
        System.out.println("Job performed!");
    }

    @Override
    public String getCronExpression() {
        return "0 0 * * * ?";
    }
}
