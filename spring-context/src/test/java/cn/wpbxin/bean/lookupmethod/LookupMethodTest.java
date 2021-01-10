package cn.wpbxin.bean.lookupmethod;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class LookupMethodTest {
	public static void main(String[] args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("cn/wpbxin/bean/lookupmethod/spring-lookup-method.xml");
		LookupMethodBean test = (LookupMethodBean) applicationContext.getBean("lookupMethodBean");
		test.showResult();
	}
}
