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

import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link EntityResolver} implementation that delegates to a {@link BeansDtdResolver}
 * and a {@link PluggableSchemaResolver} for DTDs and XML schemas, respectively.
 * <p>EntityResolver 实现，委托给 BeansDtdResolver 和 PluggableSchemaResolver
 * 分别对 dtd 和 xsd 进行解析。
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Rick Evans
 * @since 2.0
 * @see BeansDtdResolver
 * @see PluggableSchemaResolver
 */
public class DelegatingEntityResolver implements EntityResolver {

	// dtd 文件后缀
	/** Suffix for DTD files. */
	public static final String DTD_SUFFIX = ".dtd";

	// xsd 文件后缀
	/** Suffix for schema definition files. */
	public static final String XSD_SUFFIX = ".xsd";

	// dtd 解析器
	private final EntityResolver dtdResolver;

	// xsd 解析器
	private final EntityResolver schemaResolver;


	/**
	 * Create a new DelegatingEntityResolver that delegates to
	 * a default {@link BeansDtdResolver} and a default {@link PluggableSchemaResolver}.
	 * <p>创建一个新的 DelegatingEntityResolver 实例对象，委托给默认的 BeansDtdResolver 和默认的 PluggableSchemaResolver 。
	 *
	 * <p>Configures the {@link PluggableSchemaResolver} with the supplied
	 * {@link ClassLoader}.
	 * <p>使用提供的类加载器配置 PluggableSchemaResolver 。
	 *
	 * @param classLoader the ClassLoader to use for loading
	 * (can be {@code null}) to use the default ClassLoader)
	 */
	public DelegatingEntityResolver(@Nullable ClassLoader classLoader) {
		this.dtdResolver = new BeansDtdResolver();
		this.schemaResolver = new PluggableSchemaResolver(classLoader);
	}

	/**
	 * Create a new DelegatingEntityResolver that delegates to
	 * the given {@link EntityResolver EntityResolvers}.
	 * <p>创建一个新的 DelegatingEntityResolver 实例对象，委托给指定的 EntityResolver ，即自定义解析器。
	 * @param dtdResolver the EntityResolver to resolve DTDs with
	 * @param schemaResolver the EntityResolver to resolve XML schemas with
	 */
	public DelegatingEntityResolver(EntityResolver dtdResolver, EntityResolver schemaResolver) {
		Assert.notNull(dtdResolver, "'dtdResolver' is required");
		Assert.notNull(schemaResolver, "'schemaResolver' is required");
		this.dtdResolver = dtdResolver;
		this.schemaResolver = schemaResolver;
	}


	@Override
	@Nullable
	public InputSource resolveEntity(@Nullable String publicId, @Nullable String systemId)
			throws SAXException, IOException {

		if (systemId != null) {
			// .dtd 后缀的解析，即 DTD 模式
			if (systemId.endsWith(DTD_SUFFIX)) {
				return this.dtdResolver.resolveEntity(publicId, systemId);
			}
			// .xsd 后缀的解析，即 XSD 模式
			else if (systemId.endsWith(XSD_SUFFIX)) {
				return this.schemaResolver.resolveEntity(publicId, systemId);
			}
		}

		// Fall back to the parser's default behavior.
		return null;
	}


	@Override
	public String toString() {
		return "EntityResolver delegating " + XSD_SUFFIX + " to " + this.schemaResolver +
				" and " + DTD_SUFFIX + " to " + this.dtdResolver;
	}

}
