/*
 * Copyright (C) 2009 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.common.metadata;

import org.jboss.errai.reflections.vfs.SystemDir;
import org.jboss.errai.reflections.vfs.Vfs;
import org.jboss.errai.reflections.vfs.ZipDir;

import java.io.File;
import java.net.URL;

/**
 * An {@link Vfs.UrlType} for scanning web application archives.
 * It simply delegates to {@link SystemDir} and
 * {@link ZipDir} respectively
 *
 * @author Heiko Braun
 */
public class WarUrlType implements Vfs.UrlType {
  public boolean matches(final URL url) {
    return url.getProtocol().equals("file") && url.toExternalForm().endsWith(".war");
  }

  public Vfs.Dir createDir(final URL url) {
    final File file = new File(url.toExternalForm());

    if (file.isDirectory())
      return new SystemDir(url);
    else
      return new ZipDir(url);
  }
}
