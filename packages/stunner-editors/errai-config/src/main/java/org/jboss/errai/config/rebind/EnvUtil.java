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


package org.jboss.errai.config.rebind;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.errai.common.metadata.ErraiAppPropertiesFiles;
import org.jboss.errai.common.rebind.CacheStore;
import org.jboss.errai.common.rebind.CacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mike Brock
 */
public abstract class EnvUtil {
  public static class EnvironmentConfigCache implements CacheStore {
    private volatile EnvironmentConfig environmentConfig;
    private final Map<String, String> permanentProperties = new ConcurrentHashMap<>();

    public EnvironmentConfigCache() {
      clear();
    }

    @Override
    public synchronized void clear() {
      environmentConfig = newEnvironmentConfig();
      environmentConfig.getFrameworkProperties().putAll(permanentProperties);
    }

    public synchronized EnvironmentConfig get() {
      return environmentConfig;
    }

    public void addPermanentFrameworkProperty(final String name, final String value) {
      permanentProperties.put(name, value);
      environmentConfig.getFrameworkProperties().put(name, value);
    }
  }

  public static final String CONFIG_ERRAI_IOC_ENABLED_ALTERNATIVES = "errai.ioc.enabled.alternatives";
  public static final String CONFIG_ERRAI_BINDABLE_TYPES = "errai.ui.bindableTypes";
  public static final String CONFIG_ERRAI_NONBINDABLE_TYPES = "errai.ui.nonbindableTypes";

  private static volatile Boolean _isJUnitTest;

  public static boolean isJUnitTest() {
    if (_isJUnitTest != null) return _isJUnitTest;

    for (final StackTraceElement el : new Throwable().getStackTrace()) {
      if (el.getClassName().startsWith("com.google.gwt.junit.client.")
              || el.getClassName().startsWith("org.junit")) {
        return _isJUnitTest = Boolean.TRUE;
      }
    }
    return _isJUnitTest = Boolean.FALSE;
  }

  private static volatile Boolean _isDevMode;

  public static boolean isDevMode() {
    if (_isDevMode != null) return _isDevMode;

    for (final StackTraceElement el : new Throwable().getStackTrace()) {
      if (el.getClassName().startsWith("com.google.gwt.dev.shell.OophmSessionHandler") ||
              el.getClassName().startsWith("com.google.gwt.dev.codeserver")) {
        return _isDevMode = Boolean.TRUE;
      }
    }
    return _isDevMode = Boolean.FALSE;
  }

  private static volatile Boolean _isProdMode;

  public static boolean isProdMode() {
    if (_isProdMode != null) return _isProdMode;

    return _isProdMode = Boolean.valueOf(!isDevMode() && !isJUnitTest());
  }

  public static void recordEnvironmentState() {
    isJUnitTest();
    isDevMode();
    isProdMode();
  }

  private static Logger log = LoggerFactory.getLogger(EnvUtil.class);

  private static EnvironmentConfig newEnvironmentConfig() {
    final Map<String, String> frameworkProps = new HashMap<>();
    processErraiAppPropertiesFiles(frameworkProps);
    return new EnvironmentConfig(frameworkProps);
  }

  private static void processErraiAppPropertiesFiles(final Map<String, String> frameworkProps) {
    for (final URL url : getErraiAppPropertiesFilesUrls()) {
      InputStream inputStream = null;
      try {
        log.debug("checking " + url.getFile() + " for configured types ...");
        inputStream = url.openStream();

        final ResourceBundle props = new PropertyResourceBundle(inputStream);
        processErraiAppPropertiesBundle(frameworkProps, props);
      } catch (final IOException e) {
        throw new RuntimeException("error reading ErraiApp.properties", e);
      }
      finally {
        if (inputStream != null) {
          try {
            inputStream.close();
          }
          catch (final IOException e) {
            //
          }
        }
      }
    }
  }

  private static void processErraiAppPropertiesBundle(final Map<String, String> frameworkProps, final ResourceBundle props) {
    for (final String key : props.keySet()) {
      final String value = props.getString(key);
      updateFrameworkProperties(frameworkProps, key, value);
    }
  }

  private static void updateFrameworkProperties(final Map<String, String> frameworkProps, final String key, final String value) {
    if (frameworkProps.containsKey(key)) {
      if (isListValuedProperty(key)) {
        // TODO should validate that different values don't conflict
        final String oldValue = frameworkProps.get(key);
        final String newValue = oldValue + " " + value;
        log.debug("Merging property {} = {}", key, newValue);
        frameworkProps.put(key, newValue);
      } else {
        log.warn("The property {} has been set multiple times.", key);
        frameworkProps.put(key, value);
      }
    } else {
      frameworkProps.put(key, value);
    }
  }

  private static boolean isListValuedProperty(final String key) {
    return key.equals(CONFIG_ERRAI_IOC_ENABLED_ALTERNATIVES)
            || key.equals(CONFIG_ERRAI_BINDABLE_TYPES)
            || key.equals(CONFIG_ERRAI_NONBINDABLE_TYPES);
  }

  public static Collection<URL> getErraiAppPropertiesFilesUrls() {
    final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    final ClassLoader envUtilClassLoader = EnvUtil.class.getClassLoader();

    return ErraiAppPropertiesFiles.getUrls(contextClassLoader, envUtilClassLoader);
  }

  /**
   * @return an instance of {@link EnvironmentConfig}. Do NOT retain a reference to this value. Call every time
   *         you need additional configuration information.
   */
  public static EnvironmentConfig getEnvironmentConfig() {
    return CacheUtil.getCache(EnvironmentConfigCache.class).get();
  }
}
