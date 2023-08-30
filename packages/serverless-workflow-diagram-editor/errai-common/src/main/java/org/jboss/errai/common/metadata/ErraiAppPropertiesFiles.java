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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tiago Bento <tfernand@redhat.com>
 */
public class ErraiAppPropertiesFiles {

  private static final Logger log = LoggerFactory.getLogger(ErraiAppPropertiesFiles.class);

  static final String FILE_NAME = "ErraiApp.properties";

  private static final String META_INF_FILE_NAME = "META-INF/" + FILE_NAME;

  /**
   * Returns the URLs of all ErraiApp.properties and META-INF/ErraiApp.properties.
   */
  public static List<URL> getUrls(final ClassLoader... classLoaders) {
    return Stream.of(classLoaders).flatMap(ErraiAppPropertiesFiles::getUrls).collect(Collectors.toList());
  }

  private static Stream<URL> getUrls(final ClassLoader classLoader) {
    try {

      final List<URL> rootDirResources = Collections.list(classLoader.getResources(FILE_NAME));
      final List<URL> metaInfResources = Collections.list(classLoader.getResources(META_INF_FILE_NAME));

      final List<URL> allResources = new ArrayList<>();
      allResources.addAll(rootDirResources);
      allResources.addAll(metaInfResources);

      logModulesWithErraiAppPropertiesFileInRootDir(rootDirResources);

      return allResources.stream();
    } catch (final IOException e) {
      throw new RuntimeException("failed to load " + FILE_NAME + " from classloader", e);
    }
  }

  public static List<URL> getModulesUrls() {
    return getModulesUrls(ErraiAppPropertiesFiles.class.getClassLoader());
  }

  static List<URL> getModulesUrls(final ClassLoader... classLoader) {
    return getUrls(classLoader).stream()
            .peek(ErraiAppPropertiesFiles::logUnreadablePropertiesFiles)
            .map(ErraiAppPropertiesFiles::getModuleDir)
            .distinct() //due to modules containing files both in classpath:/ and classpath:META-INF/
            .map(ErraiAppPropertiesFiles::decodeUrl)
            .collect(Collectors.toList());
  }

  static String getModuleDir(final URL url) {

    final String urlString = url.toExternalForm();

    final int metaInfEndIndex = urlString.indexOf(META_INF_FILE_NAME);
    if (metaInfEndIndex > -1) {
      return urlString.substring(0, metaInfEndIndex);
    }

    final int rootDirEndIndex = urlString.indexOf(FILE_NAME);
    if (rootDirEndIndex > -1) {
      return urlString.substring(0, rootDirEndIndex);
    }

    throw new RuntimeException("URL " + url.toExternalForm() + " is not of a " + FILE_NAME);
  }

  private static URL decodeUrl(final String moduleUrlString) {
    try {
      return new URL(URLDecoder.decode(moduleUrlString.replaceAll("\\+", "%2b"), "UTF-8"));
    } catch (final IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to scan configuration Url's", e);
    }
  }

  private static void logUnreadablePropertiesFiles(final URL url) {
    try {
      try (InputStream stream = url.openStream()) {
        new Properties().load(stream);
      }
    } catch (final IOException e) {
      System.err.println("could not read properties file");
      e.printStackTrace();
    }
  }

  private static void logModulesWithErraiAppPropertiesFileInRootDir(final List<URL> fileUrls) {
    fileUrls.stream().map(ErraiAppPropertiesFiles::getModuleDir).forEach(m -> {
      log.warn("Module {} contains {} in root dir. Please consider moving it to META-INF/", m, FILE_NAME);
    });
  }

}
