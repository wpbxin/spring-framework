package cn.wpbxin.bean.customxml;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * 自定义 BeanDefinition 解析器
 */
public class CustomComponentBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
	@Override
	protected Class<?> getBeanClass(Element element) {
		return CustomComponent.class;
	}

	@Override
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		String name = element.getAttribute("name");
		String flag = element.getAttribute("flag");
		if (StringUtils.hasText(name)) {
			builder.addPropertyValue("name", name);
		}
		if (StringUtils.hasText(flag)) {
			builder.addPropertyValue("flag", Boolean.valueOf(flag));
		}
	}
}
