package cn.wpbxin.bean;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class XmlBeanDefinitionReaderTest {
	public static void main(String[] args) {
		Resource resource = new ClassPathResource("cn/wpbxin/bean/spring-xmlbeans.xml");  // (1.1)
		// ClassPathResource resource = new ClassPathResource("beans.xml");  // (1.2)
		// Resource[] resources = PathMatchingResourcePatternResolver.getResources(locationPattern);  // (1.3)，需要遍历获取 BeanDefinition
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
		reader.loadBeanDefinitions(resource);
	}

}