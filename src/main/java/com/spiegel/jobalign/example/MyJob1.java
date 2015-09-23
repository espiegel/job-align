package com.spiegel.jobalign.example;

import com.spiegel.jobalign.BaseDistributedJob;
import com.spiegel.jobalign.DistributedJobBuilder;
import org.redisson.Redisson;

/**
 * Created by Eidan on 4/22/2015.
 */
public class MyJob1 {

    public static void main(String[] args) {
        Redisson redisson = Redisson.create();
        BaseDistributedJob myJob = new DistributedJobBuilder()
                .setJobName("MyJob1")
                .setRedisson(redisson)
                .setCronExpression("0 0 * * * ?")
                .setJobLogic((shardNumber) -> System.out.println("Job 1 performed!"))
                .build();
        myJob.schedule();
    }
}
