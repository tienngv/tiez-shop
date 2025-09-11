package com.tiezshop.configurations.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(1)
public class InitRolesRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        log.info(" Running Command Line Runner Oder(1) .............");
    }
}
