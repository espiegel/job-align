package com.spiegel.jobalign.example;

import com.spiegel.jobalign.BaseDistributedJob;
import com.spiegel.jobalign.DistributedJobBuilder;
import org.redisson.Redisson;

import java.util.stream.IntStream;

/**
 * Created by eidan on 9/23/15.
 */
public class ShardedJobExample {

    public static void main(String[] args) {
        Redisson redisson = Redisson.create();
        BaseDistributedJob myJob = new DistributedJobBuilder()
            .setJobName("MyJob")
            .setRedisson(redisson)
            .setCronExpression("0 * * * * ?")
            .setShards(5)
            .setJobLogic((shardNumber) ->
                IntStream.range(shardNumber * 200, (shardNumber + 1) * 200 - 1)
                    .forEach(i -> System.out.println("Operation " + i + " performed!")))
            .build();
        myJob.schedule();
    }
}
