# job-align

This library aligns schedulers from multiple server instances so that they won't fire the same job simultaneously.

Jobs are identified by their job name (either stated explicitly or implicitly taken from the class name).

We use [Redisson](https://github.com/mrniko/redisson) to communicate with a redis instance/cluster and coordinate
the job firing with locking and timestamp saving.
 
 
Usage:

    public class MyJob extends BaseDistributedJob {
    
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
OR

    Redisson redisson = Redisson.create();
    BaseDistributedJob myJob = new DistributedJobBuilder()
            .setJobName("MyJob")
            .setRedisson(redisson)
            .setCronExpression("0 0 * * * ?")
            .setJobLogic(() -> System.out.println("Job performed!"))
            .build();
    myJob.schedule();
            
            
That's it.

You are guaranteed that this job will fire only once, even when deployed on multiple server instances.


## TODO
* Unschedule a job
* Usage of different Java Redis clients. Especially jedis and Spring redisTemplate
* Job Sharding


## Contribution
Please submit a pull request along with a test and i'll merge it.




