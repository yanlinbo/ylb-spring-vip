package com.ylb.service;

import java.lang.reflect.Field;

import com.spring.BeanPostProcessor;
import com.spring.annotion.Component;
import com.spring.annotion.Value;

@Component
public class VlueBeanPostProcessor implements BeanPostProcessor{

	@Override
	public Object postProcessorBeforeInitialization(Object bean, String beanName) {
		for(Field field : bean.getClass().getDeclaredFields()) {
			if(field.isAnnotationPresent(Value.class)) {
				field.setAccessible(true);
				try {
					field.set(bean, field.getAnnotation(Value.class).value());
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return bean;
	}

//	@Override
//	public Object postProcessorAfterInitialization(Object bean, String beanName) {
//		// TODO Auto-generated method stub
//		Object proxyInstance = null;
//		if("userService".equals(beanName)) {
//			System.out.println("只针对userService做初始化后的处理*******");
//			 proxyInstance = Proxy.newProxyInstance(VlueBeanPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
//				
//				@Override
//				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//					// TODO Auto-generated method stub
//					System.out.println("切面的逻辑在这里面来处理=========");
//					return method.invoke(bean);
//				}
//			});
//		}
//		return proxyInstance;
//	}

}
