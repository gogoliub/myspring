package com.demo.app;

import com.demo.service.TestService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * User: lb on 2016/10/24.
 * Date:2016-10-24-11:47
 * desc：
 */
@SpringBootApplication
@EnableDiscoveryClient
@RestController
@EnableCircuitBreaker
@ComponentScan("com.demo.service")
public class Application {
  @Autowired
  public TestService testService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    private DiscoveryClient client;

    //
//
    @RequestMapping("/add")
    public Integer add(@RequestParam Integer a, @RequestParam Integer b) {
        ServiceInstance serviceInstance = client.getLocalServiceInstance();
        int r = a + b;
        logger.info("/add, host{}, service_id:{}, result:" + serviceInstance.getHost(), serviceInstance.getServiceId(), r);
        return r;
    }


    @RequestMapping("/hello")
    @HystrixCommand(fallbackMethod = "fallback")
//    @HystrixCommand(fallbackMethod = "addServiceFallback"
//            , commandProperties = {@HystrixProperty (name =  "execution.isolation.thread.timeoutInMilliseconds" , value =  "5" )},
//            groupKey= "UserGroup" ,
//            commandKey =  "hello", //默认值：当前执行方法名
//            threadPoolProperties = {
//                    @HystrixProperty(name =  "coreSize" , value =  "2" ),
//                    @HystrixProperty (name =  "maxQueueSize" , value =  "101" ),
//                    @HystrixProperty (name =  "keepAliveTimeMinutes" , value =  "2" ),
//                    @HystrixProperty (name =  "queueSizeRejectionThreshold" , value =  "15" ),
//                    @HystrixProperty (name =  "metrics.rollingStats.numBuckets" , value =  "12" ),
//                    @HystrixProperty (name =  "metrics.rollingStats.timeInMilliseconds" , value =  "1440" )
//            }
//    )
    public String hello() {
       return "helloworld";
    }


    public String addServiceFallback() {
        return "error";
    }

    @RequestMapping("/test")
    public String  tests(){
        return testService.mockGetUserInfo();
    }


    public String fallback(){
        return "some exception occur call fallback method.";
    }

}
