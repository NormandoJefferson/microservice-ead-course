package com.ead.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient // Para transformar em um eureka client.
public class CourseApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseApplication.class, args);
    }

}
