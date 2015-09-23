package com.spiegel.jobalign.example;

import com.spiegel.jobalign.BaseDistributedJob;
import org.redisson.Redisson;

/**
 * Created by eidan on 9/23/15.
 */
public class MyJob2 extends BaseDistributedJob {

    public static void main(String[] args) {
        new MyJob2();
    }

    public MyJob2() {
        setRedisson(Redisson.create());
        schedule();
    }

    @Override
    public void performJobLogic(int shardNumber) {
        System.out.println("Job 2 performed!");
    }

    @Override
    public String getCronExpression() {
        return "0 0 * * * ?";
    }
}
