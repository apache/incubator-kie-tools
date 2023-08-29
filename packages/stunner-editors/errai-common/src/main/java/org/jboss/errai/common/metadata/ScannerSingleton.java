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

import java.io.File;
import java.util.concurrent.FutureTask;

import org.jboss.errai.reflections.ReflectionsException;

/**
 * Shared scanner instance used with {@link com.google.gwt.core.ext.Generator}'s
 *
 * @author Heiko Braun
 * @author Mike Brock
 */
public class ScannerSingleton {

  private static volatile MetaDataScanner scanner;

  private static final String ERRAI_REFLECTIONS_CACHE_PROPERTY = "errai.reflections.cache";
  private static final String CACHE_FILE_NAME = RebindUtils.getClasspathHash() + ".cache.xml";

  private static final Object lock = new Object();

  private static final FutureTask<MetaDataScanner> future = new FutureTask<>(() -> {

    if (erraiReflectionsCacheIsEnabled() && cacheFileExists()) {
      try {
        return MetaDataScanner.createInstance(getCacheFile());
      } catch (final ReflectionsException e) {
        e.printStackTrace();
      }
    }

    return MetaDataScanner.createInstance();
  });


  static {
    new Thread(future).start();
  }


  public static MetaDataScanner getOrCreateInstance() {
    synchronized (lock) {
      if (scanner == null) {
        try {
          scanner = future.get();

          if (scanner != null && erraiReflectionsCacheIsEnabled()) {
            scanner.save(getCacheFile().getAbsolutePath());
          }
        }
        catch (Throwable t) {
          t.printStackTrace();
          throw new RuntimeException("failed to load class metadata", t);
        }
      }
      return scanner;
    }
  }

  private static boolean erraiReflectionsCacheIsEnabled() {
    return Boolean.getBoolean(ERRAI_REFLECTIONS_CACHE_PROPERTY);
  }

  private static File getCacheFile() {
    return new File(RebindUtils.getErraiCacheDir(), CACHE_FILE_NAME).getAbsoluteFile();
  }

  private static boolean cacheFileExists() {
    return getCacheFile().exists();
  }
}
