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
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * {@link EntityResolver} implementation that attempts to resolve schema URLs into
 * local {@link ClassPathResource classpath resources} using a set of mappings files.
 * <p>使用一组映射文件将模式 url 解析为本地类路径资源的 EntityResolver 实现。
 *
 * <p>By default, this class will look for mapping files in the classpath using the
 * pattern: {@code META-INF/spring.schemas} allowing for multiple files to exist on
 * the classpath at any one time.
 * <p>默认情况下，这个类将使用模式 META-INF/spring.schemas 在类路径中查找映射文件，模式允许类路径中同时存在多个文件。
 *
 * <p>The format of {@code META-INF/spring.schemas} is a properties file where each line
 * should be of the form {@code systemId=schema-location} where {@code schema-location}
 * should also be a schema file in the classpath. Since {@code systemId} is commonly a
 * URL, one must be careful to escape any ':' characters which are treated as delimiters
 * in properties files.
 * <p>META-INF/spring.schemas 的格式是一个属性文件，其中的每一行都应该是systemId=schema-location的形式，
 * 而schema-location 也应该是 classpath 下的 schema 文件。由于 systemId 通常是一个 URL ，
 * 因此必须小心跳过属性文件中被当作分隔符的 ':' 字符
 *
 * <p>The pattern for the mapping files can be overridden using the
 * {@link #PluggableSchemaResolver(ClassLoader, String)} constructor.
 * <p>可以通过构造器 PluggableSchemaResolver(ClassLoader, String) 来覆盖映射文件的匹配模式
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public class PluggableSchemaResolver implements EntityResolver {

	/**
	 * The location of the file that defines schema mappings.
	 * Can be present in multiple JAR files.
	 * <p>定义 schema 映射的文件路径，可以出现在多个JAR文件中。
	 */
	public static final String DEFAULT_SCHEMA_MAPPINGS_LOCATION = "META-INF/spring.schemas";


	private static final Log logger = LogFactory.getLog(PluggableSchemaResolver.class);

	@Nullable
	private final ClassLoader classLoader;
	// schema 映射文件路径
	private final String schemaMappingsLocation;

	// schema URL 到 本地 schema 路径 的映射集合
	/** Stores the mapping of schema URL -> local schema path. */
	@Nullable
	private volatile Map<String, String> schemaMappings;


	/**
	 * Loads the schema URL -> schema file location mappings using the default
	 * mapping file pattern "META-INF/spring.schemas".
	 * <p>使用默认的映射文件模式 "META-INF/spring.schemas"
	 * 加载 schema URL -> schema file location 映射
	 * @param classLoader the ClassLoader to use for loading
	 * (can be {@code null}) to use the default ClassLoader)
	 * @see PropertiesLoaderUtils#loadAllProperties(String, ClassLoader)
	 */
	public PluggableSchemaResolver(@Nullable ClassLoader classLoader) {
		this.classLoader = classLoader;
		this.schemaMappingsLocation = DEFAULT_SCHEMA_MAPPINGS_LOCATION;
	}

	/**
	 * Loads the schema URL -> schema file location mappings using the given
	 * mapping file pattern.
	 * <p>使用给定的映射文件模式加载 schema URL -> schema file location 映射
	 * @param classLoader the ClassLoader to use for loading
	 * (can be {@code null}) to use the default ClassLoader)
	 * @param schemaMappingsLocation the location of the file that defines schema mappings
	 * (must not be empty)
	 * @see PropertiesLoaderUtils#loadAllProperties(String, ClassLoader)
	 */
	public PluggableSchemaResolver(@Nullable ClassLoader classLoader, String schemaMappingsLocation) {
		Assert.hasText(schemaMappingsLocation, "'schemaMappingsLocation' must not be empty");
		this.classLoader = classLoader;
		this.schemaMappingsLocation = schemaMappingsLocation;
	}


	@Override
	@Nullable
	public InputSource resolveEntity(@Nullable String publicId, @Nullable String systemId) throws IOException {
		if (logger.isTraceEnabled()) {
			logger.trace("Trying to resolve XML entity with public id [" + publicId +
					"] and system id [" + systemId + "]");
		}

		if (systemId != null) {
			// 获取 systemId 对应的 Resource 所在路径
			String resourceLocation = getSchemaMappings().get(systemId);
			if (resourceLocation == null && systemId.startsWith("https:")) {
				// Retrieve canonical http schema mapping even for https declaration
				// 若申明的 https 映射找不到，找检索对应的 http 模式映射
				resourceLocation = getSchemaMappings().get("http:" + systemId.substring(6));
			}
			if (resourceLocation != null) {
				// 创建 ClassPathResource 实例对象
				Resource resource = new ClassPathResource(resourceLocation, this.classLoader);
				try {
					// 创建 InputSource 对象，并设置 publicId 和 systemId
					InputSource source = new InputSource(resource.getInputStream());
					source.setPublicId(publicId);
					source.setSystemId(systemId);
					if (logger.isTraceEnabled()) {
						logger.trace("Found XML schema [" + systemId + "] in classpath: " + resourceLocation);
					}
					return source;
				}
				catch (FileNotFoundException ex) {
					if (logger.isDebugEnabled()) {
						logger.debug("Could not find XML schema [" + systemId + "]: " + resource, ex);
					}
				}
			}
		}

		// Fall back to the parser's default behavior.
		return null;
	}

	/**
	 * Load the specified schema mappings lazily.
	 * <p>懒加载指定的 schema mappings ，默认从 "META-INF/spring.schemas" 进行加载
	 */
	private Map<String, String> getSchemaMappings() {
		Map<String, String> schemaMappings = this.schemaMappings;
		// 双重检查锁，实现 schemaMappings 单例
		if (schemaMappings == null) {
			synchronized (this) {
				schemaMappings = this.schemaMappings;
				if (schemaMappings == null) {
					if (logger.isTraceEnabled()) {
						logger.trace("Loading schema mappings from [" + this.schemaMappingsLocation + "]");
					}
					try {
						// 加载 schemaMappingsLocation（默认是"META-INF/spring.schemas"）文件中的属性
						Properties mappings =
								PropertiesLoaderUtils.loadAllProperties(this.schemaMappingsLocation, this.classLoader);
						if (logger.isTraceEnabled()) {
							logger.trace("Loaded schema mappings: " + mappings);
						}
						schemaMappings = new ConcurrentHashMap<>(mappings.size());
						// 将 Properties 转成 schemaMappings
						CollectionUtils.mergePropertiesIntoMap(mappings, schemaMappings);
						this.schemaMappings = schemaMappings;
					}
					catch (IOException ex) {
						throw new IllegalStateException(
								"Unable to load schema mappings from location [" + this.schemaMappingsLocation + "]", ex);
					}
				}
			}
		}
		return schemaMappings;
	}


	@Override
	public String toString() {
		return "EntityResolver using schema mappings " + getSchemaMappings();
	}

}
