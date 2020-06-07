/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.core.io;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link ResourceLoader} implementation that interprets plain resource paths
 * as relative to a given {@code java.lang.Class}.
 * <p>ResourceLoader 的实现类，将普通的资源路径解析为给定 java.lang.Class 相关的资源，
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see Class#getResource(String)
 * @see ClassPathResource#ClassPathResource(String, Class)
 */
public class ClassRelativeResourceLoader extends DefaultResourceLoader {

	private final Class<?> clazz;


	/**
	 * Create a new ClassRelativeResourceLoader for the given class.
	 * <p>根据指定的 class 创建 ClassRelativeResourceLoader 实例，
	 * 内部会通过 class 来获取对应的 ClassLoader，这个 ClassLoader 可以用于
	 * 自定义解析策略的加载，也可以用于 ClassPathResource 类型的资源（协议是 classpath:），
	 * 参考 DefaultResourceLoader.getResource()
	 * @param clazz the class to load resources through
	 */
	public ClassRelativeResourceLoader(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		this.clazz = clazz;
		setClassLoader(clazz.getClassLoader());
	}

	// 返回 ClassRelativeContextResource 类型的资源
	@Override
	protected Resource getResourceByPath(String path) {
		return new ClassRelativeContextResource(path, this.clazz);
	}


	/**
	 * ClassPathResource that explicitly expresses a context-relative path
	 * through implementing the ContextResource interface.
	 * <p>通过实现 ContextResource 接口显式地表示上下文相关路径的 ClassRelativeContextResource
	 */
	private static class ClassRelativeContextResource extends ClassPathResource implements ContextResource {

		private final Class<?> clazz;

		public ClassRelativeContextResource(String path, Class<?> clazz) {
			super(path, clazz);
			this.clazz = clazz;
		}

		// 返回文件资源的路径
		@Override
		public String getPathWithinContext() {
			return getPath();
		}

		// 根据资源的相对路径创建对应的资源，内部会将相对路径拼接进去
		@Override
		public Resource createRelative(String relativePath) {
			String pathToUse = StringUtils.applyRelativePath(getPath(), relativePath);
			return new ClassRelativeContextResource(pathToUse, this.clazz);
		}
	}

}
