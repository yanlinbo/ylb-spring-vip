package com.ylb.service;

import com.spring.InitializingBean;
import com.spring.annotion.Autowired;
import com.spring.annotion.Component;
import com.spring.annotion.Scope;
import com.spring.annotion.Value;

@Component("userService")
@Scope("prototype")
public class UserService implements InitializingBean, UserInterface {
	
	@Autowired
	private OrderService orderService;
	
	@Value("严林博")
	private String userName;

	public void test() {
		
		System.out.println("==orderService=="+orderService);
		
		System.out.println("==userName=="+userName);
	}

	@Override
	public void afterPropertiesSet() {
		// TODO Auto-generated method stub
		System.out.println("初始化-----");
	}
}
