# job-align
A library for aligning distributed scheduling in Spring with Redis

Usage:

    public class MyJob extends BaseDistributedJob {
    
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
    
OR

    RedisProvider redisProvider = new RedisProvider(Redisson.create());
    BaseDistributedJob myJob = new DistributedJobBuilder()
            .setLockProvider(redisProvider)
            .setKeyValueProvider(redisProvider)
            .setCronExpression("0 0 * * * ?")
            .setJobLogic(() -> System.out.println("Job performed!"))
            .build();
    myJob.schedule();
            
            
That's it.

You are guaranteed that this job will fire only once even when deployed on multiple server instances.




