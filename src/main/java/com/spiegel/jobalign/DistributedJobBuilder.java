package com.spiegel.jobalign;

import com.spiegel.jobalign.factory.KeyValueProvider;
import com.spiegel.jobalign.factory.LockProvider;

/**
 * Created by Eidan on 4/22/2015.
 */
public class DistributedJobBuilder {

    private String cron;
    private Runnable performJobLogic;
    private LockProvider lockProvider;
    private KeyValueProvider keyValueProvider;

    public DistributedJobBuilder setCronExpression(String cronExpression) {
        this.cron = cronExpression;
        return this;
    }

    public DistributedJobBuilder setJobLogic(Runnable jobLogic) {
        this.performJobLogic = jobLogic;
        return this;
    }

    public DistributedJobBuilder setLockProvider(LockProvider lockProvider) {
        this.lockProvider = lockProvider;
        return this;
    }

    public DistributedJobBuilder setKeyValueProvider(KeyValueProvider keyValueProvider) {
        this.keyValueProvider = keyValueProvider;
        return this;
    }

    public BaseDistributedJob build() {
        if(cron == null) {
            throw new IllegalStateException("You must set a cron expression!");
        }
        if(performJobLogic == null) {
            throw new IllegalStateException("You must set the job logic!");
        }
        if(lockProvider == null) {
            throw new IllegalStateException("You must set a lock provider!");
        }
        if(keyValueProvider == null) {
            throw new IllegalStateException("You must set a key-value provider!");
        }

        return new BaseDistributedJob(lockProvider, keyValueProvider) {
            @Override
            public void performJobLogic() {
                performJobLogic.run();
            }

            @Override
            public String getCronExpression() {
                return cron;
            }
        };
    }
}
