package com.ylb;

import com.spring.YlbApplicationContext;
import com.ylb.config.AppConfig;
import com.ylb.service.OrderService;
import com.ylb.service.UserInterface;
import com.ylb.service.UserService;

public class Test {

	public static void main(String[] args) throws Exception {
		YlbApplicationContext context = new YlbApplicationContext(AppConfig.class);
		UserService userService;
		OrderService orderService;
		UserInterface userInterface;
		try {
//			userService = (UserService)context.getBean("userService");
//			System.out.println(userService);
//			userService = (UserService)context.getBean("userService");
//			System.out.println(userService);
			userInterface = (UserInterface)context.getBean("userService");
			System.out.println(userInterface);
			orderService = (OrderService)context.getBean("orderService");
			System.out.println(orderService);
			userInterface.test();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
