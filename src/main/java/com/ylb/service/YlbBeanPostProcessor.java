package com.ylb.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.spring.BeanPostProcessor;
import com.spring.annotion.Component;

@Component
public class YlbBeanPostProcessor implements BeanPostProcessor{

	@Override
	public Object postProcessorBeforeInitialization(Object bean, String beanName) {
		// TODO Auto-generated method stub
		return bean;
	}

	@Override
	public Object postProcessorAfterInitialization(Object bean, String beanName) {
		// TODO Auto-generated method stub
		Object proxyInstance = null;
		if("userService".equals(beanName)) {
			System.out.println("只针对userService做初始化后的处理*******");
			 proxyInstance = Proxy.newProxyInstance(YlbBeanPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
				
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					// TODO Auto-generated method stub
					System.out.println("切面的逻辑在这里面来处理=========");
					return method.invoke(bean);
				}
			});
		}
		return proxyInstance;
	}

}
