package cn.wpbxin.bean.customxml;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 自定义标签解析测试类
 */
public class CustomxmlMainApp {
	public static void main(String[] args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("cn/wpbxin/bean/customxml/spring-customxml.xml");
		CustomComponent cc = (CustomComponent) applicationContext.getBean("customComponent");
		System.out.println(cc);
	}
}
