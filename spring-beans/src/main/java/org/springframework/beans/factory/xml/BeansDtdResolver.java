/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory.xml;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/**
 * {@link EntityResolver} implementation for the Spring beans DTD,
 * to load the DTD from the Spring class path (or JAR file).
 * <p>EntityResolver 实现类，Spring beans DTD 的解析器，从 Spring 的 classpath 或者 JAR 文件中加载 DTD。
 *
 * <p>Fetches "spring-beans.dtd" from the class path resource
 * "/org/springframework/beans/factory/xml/spring-beans.dtd",
 * no matter whether specified as some local URL that includes "spring-beans"
 * in the DTD name or as "https://www.springframework.org/dtd/spring-beans-2.0.dtd".
 * <p>无论是否在 DTD 名称中指定了包含 "spring-beans" 的 URL，
 * 还是使用 "https://www.springframework.org/dtd/spring-beans-2.0.dtd"，
 * 都会从 classpath 资源 "/org/springframework/beans/factory/xml/spring-beans.dtd" 下
 * 抓取 spring-beans.dtd" 文件 ，
 *
 * @author Juergen Hoeller
 * @author Colin Sampaleanu
 * @since 04.06.2003
 * @see ResourceEntityResolver
 */
public class BeansDtdResolver implements EntityResolver {
	// dtd 扩展名
	private static final String DTD_EXTENSION = ".dtd";
	// Spring beans DTD 的文件名
	private static final String DTD_NAME = "spring-beans";

	private static final Log logger = LogFactory.getLog(BeansDtdResolver.class);


	@Override
	@Nullable
	public InputSource resolveEntity(@Nullable String publicId, @Nullable String systemId) throws IOException {
		if (logger.isTraceEnabled()) {
			logger.trace("Trying to resolve XML entity with public ID [" + publicId +
					"] and system ID [" + systemId + "]");
		}
		// 以 .dtd 结尾
		if (systemId != null && systemId.endsWith(DTD_EXTENSION)) {
			// 获取最后一个 / 的索引位置
			int lastPathSeparator = systemId.lastIndexOf('/');
			// 获取 spring-beans 的位置
			int dtdNameStart = systemId.indexOf(DTD_NAME, lastPathSeparator);
			// 存在 spring-beans
			if (dtdNameStart != -1) {
				String dtdFile = DTD_NAME + DTD_EXTENSION;
				if (logger.isTraceEnabled()) {
					logger.trace("Trying to locate [" + dtdFile + "] in Spring jar on classpath");
				}
				try {
					// 创建 ClassPathResource 对象，相对于当前类对象路径的资源，其实就是 /org/springframework/beans/factory/xml/spring-beans.dtd
					Resource resource = new ClassPathResource(dtdFile, getClass());
					// 创建 InputSource 对象，并设置 publicId 和 systemId
					InputSource source = new InputSource(resource.getInputStream());
					source.setPublicId(publicId);
					source.setSystemId(systemId);
					if (logger.isTraceEnabled()) {
						logger.trace("Found beans DTD [" + systemId + "] in classpath: " + dtdFile);
					}
					return source;
				}
				catch (FileNotFoundException ex) {
					if (logger.isDebugEnabled()) {
						logger.debug("Could not resolve beans DTD [" + systemId + "]: not found in classpath", ex);
					}
				}
			}
		}

		// Fall back to the parser's default behavior.
		return null;
	}


	@Override
	public String toString() {
		return "EntityResolver for spring-beans DTD";
	}

}
