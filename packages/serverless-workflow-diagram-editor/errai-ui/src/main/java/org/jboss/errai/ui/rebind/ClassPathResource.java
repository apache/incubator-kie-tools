/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.jboss.errai.ui.rebind;

import java.io.IOException;
import java.io.InputStream;

import org.jboss.errai.common.client.api.Assert;
import org.lesscss.Resource;

/**
 * Allows less resources that are loaded from the classpath.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class ClassPathResource implements Resource {

  private final String path;
  private final ClassLoader classLoader;

  public ClassPathResource(final String path, final ClassLoader classLoader) {
    this.path = Assert.notNull(path);
    this.classLoader = Assert.notNull(classLoader);
  }

  @Override
  public boolean exists() {
    return classLoader.getResource(path) != null;
  }

  @Override
  public long lastModified() {
    try {
      return classLoader.getResource(path).openConnection().getLastModified();
    } catch (final IOException e) {
      return 0;
    }
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return classLoader.getResourceAsStream(path);
  }

  @Override
  public Resource createRelative(final String relativeResourcePath) throws IOException {
    final String newPath;
    if (relativeResourcePath.startsWith("/")) {
      newPath = relativeResourcePath.substring(1);
    }
    else {
      final int endOfParentPath = path.lastIndexOf('/');
      final String parentPath = path.substring(0, endOfParentPath+1);
      newPath = parentPath + relativeResourcePath;
    }
    return new ClassPathResource(newPath, classLoader);
  }

  @Override
  public String getName() {
    return path;
  }

  @Override
  public String toString() {
    return String.format("[%s]", path);
  }

}
