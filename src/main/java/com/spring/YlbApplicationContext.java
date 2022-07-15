package com.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spring.annotion.Autowired;
import com.spring.annotion.Component;
import com.spring.annotion.ComponentScan;
import com.spring.annotion.Scope;

public class YlbApplicationContext {
	
	private Class configClass;
	
	private Map<String,BeanDefinition> beanDefinitionMap = new HashMap<>();
	
	private Map<String,Object> singletonObjects = new HashMap<>();
	
	private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

	public YlbApplicationContext(Class configClass) throws Exception {
//		super();
		this.configClass = configClass;
		//1,扫描
		scan(configClass);
		//2,找出单例bean
		for(Map.Entry<String,BeanDefinition> entry : beanDefinitionMap.entrySet()) {
			String beanName = entry.getKey();
			BeanDefinition beanDefinition = entry.getValue();
			if("singleton".equals(beanDefinition.getScope())) {
				Object bean = createBean(beanName,beanDefinition);
				singletonObjects.put(beanName, bean);
			}
		}
		
	}
	
	private Object createBean(String beanName,BeanDefinition beanDefinition) throws Exception {
		BeanDefinition bean = beanDefinitionMap.get(beanName);
		Class clazz = bean.getType();
		Object instance = null;
		try {
			instance = clazz.getConstructor().newInstance();
			//依赖注入
			for(Field field: clazz.getDeclaredFields()) {
				if(field.isAnnotationPresent(Autowired.class)) {
					field.setAccessible(true);
					field.set(instance,getBean(field.getName()));
				}
			}
			
			//依赖注入完成后进行初始化前逻辑
			for(BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
				beanPostProcessor.postProcessorBeforeInitialization(instance, beanName);
			}
			
			//进行初始化
			if(instance instanceof InitializingBean) {
				((InitializingBean) instance).afterPropertiesSet();
			}
			
			//初始化后要执行的逻辑
			for(BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
				beanPostProcessor.postProcessorAfterInitialization(instance, beanName);
			}
			
			
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return instance;
		
	}

	@SuppressWarnings("unchecked")
	private void scan(Class configClass) throws Exception {
		if(configClass.isAnnotationPresent(ComponentScan.class)) {
			ComponentScan ComponentScanAnnotation= (ComponentScan)configClass.getAnnotation(ComponentScan.class);
			String path = ComponentScanAnnotation.value();
			path = path.replace(".", "/");
			System.out.println(path);
			ClassLoader classLoader = YlbApplicationContext.class.getClassLoader();
			URL url = classLoader.getResource(path);
			File file = new File(url.getFile());
			
			if(file.isDirectory()) {
				for(File f : file.listFiles()) {
					String absolutePath = f.getAbsolutePath();
					absolutePath = absolutePath.substring(absolutePath.indexOf("com"),absolutePath.indexOf(".class"));
					absolutePath = absolutePath.replace("\\", ".");
					System.out.println(absolutePath);
					
					try {
						Class<?> clazz = classLoader.loadClass(absolutePath);
						if(clazz.isAnnotationPresent(Component.class)) {
							//当实现初始化的接口BeanPostProcessor 时
							if(BeanPostProcessor.class.isAssignableFrom(clazz)) {
								BeanPostProcessor instance = (BeanPostProcessor)clazz.getConstructor().newInstance();
								beanPostProcessorList.add( instance);
							}
							
							Component annotationComponent = clazz.getAnnotation(Component.class);
							String beanName = annotationComponent.value();
							//当注解上没有标注value时
							if("".equals(beanName)) {
								beanName  =Introspector.decapitalize(clazz.getSimpleName());
							}
							
							BeanDefinition beanDefinition = new BeanDefinition();
							beanDefinition.setType(clazz);
							if(clazz.isAnnotationPresent(Scope.class)) {
								Scope annotationScope = clazz.getAnnotation(Scope.class);
								beanDefinition.setScope(annotationScope.value());
							}else {
								beanDefinition.setScope("singleton");
							}
							beanDefinitionMap.put(beanName, beanDefinition);
						}
						
						
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
			}
		}
	}

	public Object getBean(String beanName) throws Exception {
		if(!beanDefinitionMap.containsKey(beanName)) {
			throw new Exception();
		}
		Object bean = null;
		BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
		if("singleton".equals(beanDefinition.getScope())) {
		  bean = singletonObjects.get(beanName);
		  if(bean == null) {
			  bean = createBean(beanName,beanDefinition);
			  singletonObjects.put(beanName, bean);
		  }
		}else {
			//原型bean
		  bean = createBean(beanName,beanDefinition);
		}
		return bean;
		
		
	}
}
