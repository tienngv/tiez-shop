//package com.tshop.configurations.runner;
//
//import com.tienngv.security.entity.User;
//import com.tienngv.security.request.UserDto;
//import com.tienngv.security.service.UserService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//@Order(2)
//public class InitUserRunner implements CommandLineRunner {
//    private final UserService userService;
//
//    @Override
//    public void run(String... args) {
//        if (!userService.existsByUsername("admin")) {
//            UserDto userDto = new UserDto();
//            userDto.setFullName("Nguyen Gia Viet Tien");
//            userDto.setUsername("admin");
//            userDto.setPassword("123456");
//            userDto.setEmail("Admini@gmail.com");
//            userDto.setRole(User.Role.ADMIN.toString());
//            userService.save(userDto);
//        }else {
//            log.info("Admin already exists");
//        }
//    }
//}
