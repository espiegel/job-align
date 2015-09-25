# job-align

This library aligns schedulers from multiple server instances so that they won't fire the same job simultaneously.

Jobs are identified by their job name (either stated explicitly or implicitly taken from the class name).

We use [Redisson](https://github.com/mrniko/redisson) to communicate with a redis instance/cluster and coordinate
the job firing with locking and timestamp saving.
 
 
Usage:


```java
    public class MyJob extends BaseDistributedJob {
    
        public MyJob() {
            setRedisson(Redisson.create());
            schedule();
        }
    
        @Override
        public void performJobLogic(int shardNumber) {
            System.out.println("Job performed!");
        }
    
        @Override
        public String getCronExpression() {
            return "0 0 * * * ?";
        }
    }
```

OR

```java
    Redisson redisson = Redisson.create();
    BaseDistributedJob myJob = new DistributedJobBuilder()
            .setJobName("MyJob")
            .setRedisson(redisson)
            .setCronExpression("0 0 * * * ?")
            .setJobLogic((shardNumber) -> System.out.println("Job performed!"))
            .build();
    myJob.schedule();
```            
            
That's it.

You are guaranteed that this job will fire only once, even when deployed on multiple server instances.


#Job Sharding:
If you've got a large job that you want to split up into batches you can do so with sharding. For example if you've
got 1000 operations to perform and you want to split them up into 5 batches of 200 you can use the following:
```java
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
```

What's nice about this is that you can deploy this code on any amount of server instances you want and you're guaranteed
that the servers will split the job shards between them equally.

Note that the system clock on all servers must be aligned. (You can achieve that using [NTP](https://en.wikipedia.org/wiki/Network_Time_Protocol))

## TODO
* Unschedule a job
* Usage of different Java Redis clients. Especially jedis and Spring redisTemplate

## Contribution
Please submit a pull request along with a test and i'll merge it.




