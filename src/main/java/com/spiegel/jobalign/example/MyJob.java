package com.spiegel.jobalign.example;

import com.spiegel.jobalign.BaseDistributedJob;
import com.spiegel.jobalign.DistributedJobBuilder;
import org.redisson.Redisson;

/**
 * Created by Eidan on 4/22/2015.
 */
public class MyJob extends BaseDistributedJob {

    public static void main(String[] args) {
        Redisson redisson = Redisson.create();
        BaseDistributedJob myJob = new DistributedJobBuilder()
                .setJobName("MyJob")
                .setRedisson(redisson)
                .setCronExpression("0 0 * * * ?")
                .setJobLogic(() -> System.out.println("Job performed!"))
                .build();
        myJob.schedule();
    }

    public MyJob() {
        setRedisson(Redisson.create());
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
