package cn.wpbxin.bean.customxml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * 自定义命名空间处理器
 */
public class MyNamespaceHandler extends NamespaceHandlerSupport {
	@Override
	public void init() {
		registerBeanDefinitionParser("customComponent", new CustomComponentBeanDefinitionParser());
		// 可以注册多个命名空间，处理多个元素标签
	}
}