package cn.wpbxin.bean.replacedmethod;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ReplacedMethodTest {
	public static void main(String[] args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("cn/wpbxin/bean/replacedmethod/spring-replaced-method.xml");
		ReplacedMethodBean replacedMethodBean = (ReplacedMethodBean)applicationContext.getBean("replacedMethodBean");
		replacedMethodBean.replacedMethod();
	}
}
