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


package org.jboss.errai.common.metadata;

import java.net.URL;
import java.util.Collections;
import java.util.function.Predicate;

import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.reflections.vfs.Vfs;

/**
 * A trivial URL Type handler for Reflections VFS which matches any classpath
 * entry matching a given predicate and returns an empty
 * VFS directory. When used with the extension ".jnilib", this helps silence a
 * bunch of warnings that happen when you compile an Errai app on a Mac OS X
 * JVM.
 * <p>
 * Instances are fully immutable and therefore threadsafe.
 */
public final class NoOpUrl implements Vfs.UrlType {

  private final Predicate<URL> test;

  public static NoOpUrl forSuffix(final String urlSuffix) {
    final Predicate<URL> test = new Predicate<URL>() {
      @Override
      public boolean test(final URL url) {
        return url.toExternalForm().endsWith(urlSuffix);
      }

      @Override
      public String toString() {
        return "*" + urlSuffix;
      }
    };

    return new NoOpUrl(test);
  }

  public static NoOpUrl forProtocol(final String protocol) {
    final Predicate<URL> test = new Predicate<URL>() {
      @Override
      public boolean test(final URL url) {
        return url.getProtocol().equals(protocol);
      }

      @Override
      public String toString() {
        return protocol + "://*";
      }
    };

    return new NoOpUrl(test);
  }

  /**
   * A VFS directory rooted at a given URL which does not contain any entries.
   *
   * @author Jonathan Fuerth <jfuerth@redhat.com>
   */
  private static final class EmptyVfsDir implements Vfs.Dir {
    private final URL url;

    private EmptyVfsDir(final URL url) {
      this.url = url;
    }

    @Override
    public String getPath() {
      return url.getPath();
    }

    @Override
    public Iterable<org.jboss.errai.reflections.vfs.Vfs.File> getFiles() {
      return Collections.emptyList();
    }

    @Override
    public void close() {
      // no op
    }
  }

  /**
   * Creates a new LeafUrlType that matches URLs using the given predicate..
   *
   * @param test Must not be null.
   */
  public NoOpUrl(final Predicate<URL> test) {
    this.test = Assert.notNull(test);
  }

  @Override
  public boolean matches(final URL url) {
    return test.test(url);
  }

  @Override
  public Vfs.Dir createDir(final URL url) {
    return new EmptyVfsDir(url);
  }

  @Override
  public String toString() {
    return test.toString();
  }
}
