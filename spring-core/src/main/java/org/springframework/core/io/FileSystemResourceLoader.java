/*
 * Copyright 2002-2017 the original author or authors.
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

/**
 * {@link ResourceLoader} implementation that resolves plain paths as
 * file system resources rather than as class path resources
 * (the latter is {@link DefaultResourceLoader}'s default strategy).
 *
 * <p><b>NOTE:</b> Plain paths will always be interpreted as relative
 * to the current VM working directory, even if they start with a slash.
 * (This is consistent with the semantics in a Servlet container.)
 * <b>Use an explicit "file:" prefix to enforce an absolute file path.</b>
 *
 * <p>{@link org.springframework.context.support.FileSystemXmlApplicationContext}
 * is a full-fledged ApplicationContext implementation that provides
 * the same resource path resolution strategy.
 *
 * @author Juergen Hoeller
 * @since 1.1.3
 * @see DefaultResourceLoader
 * @see org.springframework.context.support.FileSystemXmlApplicationContext
 */
public class FileSystemResourceLoader extends DefaultResourceLoader {

	/**
	 * Resolve resource paths as file system paths.
	 * <p>Note: Even if a given path starts with a slash, it will get
	 * interpreted as relative to the current VM working directory.
	 * <p>返回 FileSystemContextResource 类型的资源
	 * 将资源路径解析为文件系统路径，以 / 开头的会被解析为 VM 当前工作目录的相对路径
	 * @param path the path to the resource
	 * @return the corresponding Resource handle
	 * @see FileSystemResource
	 * @see org.springframework.web.context.support.ServletContextResourceLoader#getResourceByPath
	 */
	@Override
	protected Resource getResourceByPath(String path) {
		// 截取开头的 /，会当作 context 上下文的额相对路径
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		return new FileSystemContextResource(path);
	}


	/**
	 * FileSystemResource that explicitly expresses a context-relative path
	 * through implementing the ContextResource interface.
	 * <p>通过实现 ContextResource 接口显式地表示上下文相关路径的 FileSystemResource
	 */
	private static class FileSystemContextResource extends FileSystemResource implements ContextResource {

		public FileSystemContextResource(String path) {
			super(path);
		}

		// 返回文件资源的路径
		@Override
		public String getPathWithinContext() {
			return getPath();
		}
	}

}
