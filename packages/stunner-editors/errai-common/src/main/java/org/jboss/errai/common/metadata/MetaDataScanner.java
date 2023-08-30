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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import org.jboss.errai.common.rebind.CacheStore;
import org.jboss.errai.common.rebind.CacheUtil;
import org.jboss.errai.reflections.Configuration;
import org.jboss.errai.reflections.Reflections;
import org.jboss.errai.reflections.scanners.FieldAnnotationsScanner;
import org.jboss.errai.reflections.scanners.MethodAnnotationsScanner;
import org.jboss.errai.reflections.util.ConfigurationBuilder;
import org.jboss.errai.reflections.vfs.Vfs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scans component meta data. The scanner creates a {@link DeploymentContext} that identifies nested
 * subdeployments (i.e. WAR inside EAR) and processes the resulting archive Url's using the <a
 * href="http://code.google.com/p/reflections/">Reflections</a> library.
 * <p/>
 * <p/>
 * The initial set of config URLs (entry points) is discovered through ErraiApp.properties and META-INF/ErraiApp.properties.
 *
 * @author Heiko Braun <hbraun@redhat.com>
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class MetaDataScanner extends Reflections {

  private static final Logger log = LoggerFactory.getLogger(MetaDataScanner.class);

  private static final String EXTENSION_KEY = "errai.class_scanning_extension";

  private static final ErraiPropertyScanner propScanner = new ErraiPropertyScanner(file -> file.endsWith(".properties"));

  private final Map<Class<? extends Annotation>, Set<Class<?>>> _annotationCache = new HashMap<>();

  static MetaDataScanner createInstance() {
    return createInstance(ErraiAppPropertiesFiles.getModulesUrls());
  }

  public static MetaDataScanner createInstance(final List<URL> urls) {
    return createInstance(urls, null);
  }

  static MetaDataScanner createInstance(final File cacheFile) {
    return createInstance(ErraiAppPropertiesFiles.getModulesUrls(), cacheFile);
  }

  private static MetaDataScanner createInstance(final List<URL> urls, final File cacheFile) {
    registerDefaultHandlers();

    final DeploymentContext ctx = new DeploymentContext(urls);
    final List<URL> actualUrls = ctx.process();

    final MetaDataScanner scanner = new MetaDataScanner(actualUrls, cacheFile);
    ctx.close(); // needs to be closed after the scanner is created
    return scanner;
  }

  private MetaDataScanner(final List<URL> urls, final File cacheFile) {
    super(getConfiguration(urls));
    try {
      for (final Class<? extends Vfs.UrlType> cls : findExtensions()) {
        try {
          final Vfs.UrlType urlType = cls.asSubclass(Vfs.UrlType.class).newInstance();
          registerTypeHandler(urlType);
          log.info("added class scanning extensions: " + cls.getName());
        } catch (final Throwable t) {
          throw new RuntimeException("could not load scanner extension: " + cls.getName(), t);
        }
      }
    } catch (final Throwable t) {
      t.printStackTrace();
    }
    if (cacheFile != null) {
      collect(cacheFile);
    } else {
      scan();
    }
  }

  private List<Class<? extends Vfs.UrlType>> findExtensions() {

    final List<Class<? extends Vfs.UrlType>> extensions = new ArrayList<>();

    for (final URL url : getErraiAppPropertiesFilesUrls()) {
      InputStream inputStream = null;
      try {
        inputStream = url.openStream();

        final ResourceBundle props = new PropertyResourceBundle(inputStream);

        for (final Object o : props.keySet()) {
          final String key = (String) o;

          if (key.equals(EXTENSION_KEY)) {
            final String clsName = props.getString(key);

            try {
              final Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass(clsName);
              extensions.add(aClass.asSubclass(Vfs.UrlType.class));
            } catch (final Throwable t) {
              throw new RuntimeException("could not load class scanning extension: " + clsName, t);
            }
          }
        }

      } catch (final IOException e) {
        throw new RuntimeException("error reading ErraiApp.properties", e);
      } finally {
        if (inputStream != null) {
          try {
            inputStream.close();
          } catch (final IOException e) {
            //
          }
        }
      }
    }
    return extensions;
  }

  private static Collection<URL> getErraiAppPropertiesFilesUrls() {
    final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    final ClassLoader metaDataScannerClassLoader = MetaDataScanner.class.getClassLoader();

    return ErraiAppPropertiesFiles.getUrls(contextClassLoader, metaDataScannerClassLoader);
  }

  private static Configuration getConfiguration(final List<URL> urls) {
    return new ConfigurationBuilder().setUrls(urls).setExecutorService(Executors.newFixedThreadPool(2))
            .setScanners(new FieldAnnotationsScanner(), new MethodAnnotationsScanner(),
                    new ExtendedTypeAnnotationScanner(), propScanner);
  }

  private static void registerTypeHandler(final Vfs.UrlType handler) {
    Vfs.addDefaultURLTypes(handler);
  }

  private static void registerDefaultHandlers() {
    final List<Vfs.UrlType> urlTypes = Vfs.getDefaultUrlTypes();
    urlTypes.add(new WarUrlType());
    urlTypes.add(NoOpUrl.forSuffix(".jnilib"));
    urlTypes.add(NoOpUrl.forSuffix(".zip"));
    urlTypes.add(NoOpUrl.forSuffix(".pom"));
    urlTypes.add(NoOpUrl.forProtocol("bundleresource"));
    // thread safe?
    Vfs.setDefaultURLTypes(urlTypes);
  }

  public Set<Class<?>> getTypesAnnotatedWithExcluding(final Class<? extends Annotation> annotation,
          final String excludeRegex) {
    final Pattern p = Pattern.compile(excludeRegex);

    final Set<String> result = new HashSet<>();
    final Set<String> types = getStore().getTypesAnnotatedWith(annotation.getName());
    for (final String className : types) {
      if (!p.matcher(className).matches()) {
        result.add(className);
      }
    }

    return ImmutableSet.copyOf(forNames(result));
  }

  public Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation,
          final Collection<String> packages) {
    final Set<Class<?>> results = new HashSet<>();
    for (final Class<?> cls : getTypesAnnotatedWith(annotation)) {
      if (packages.contains(cls.getPackage().getName())) {
        results.add(cls);
      }
    }
    return results;
  }

  public Set<Method> getMethodsAnnotatedWithExcluding(final Class<? extends Annotation> annotation,
          final String excludeRegex) {
    final Set<Method> results = new HashSet<>();
    final Pattern p = Pattern.compile(excludeRegex);
    for (final Method method : getMethodsAnnotatedWith(annotation)) {
      if (!p.matcher(method.getClass().getName()).matches()) {
        results.add(method);
      }
    }
    return results;
  }

  public Set<Method> getMethodsAnnotatedWith(final Class<? extends Annotation> annotation,
          final Collection<String> packages) {
    final Set<Method> results = new HashSet<>();
    for (final Method method : getMethodsAnnotatedWith(annotation)) {
      if (packages.contains(method.getDeclaringClass().getPackage().getName())) {
        results.add(method);
      }
    }
    return results;
  }

  public Set<Field> getFieldsAnnotatedWith(final Class<? extends Annotation> annotation,
          final Collection<String> packages) {
    final Set<Field> results = new HashSet<>();
    for (final Field field : getFieldsAnnotatedWith(annotation)) {
      if (packages.contains(field.getDeclaringClass().getPackage().getName())) {
        results.add(field);
      }
    }
    return results;
  }

  @Override
  public Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation) {
    Set<Class<?>> types = _annotationCache.get(annotation);
    if (types == null) {
      types = new HashSet<>(super.getTypesAnnotatedWith(annotation));

      if (annotation.isAnnotationPresent(Inherited.class)) {
        for (final Class<?> cls : new ArrayList<>(types)) {
          types.addAll(getSubTypesOf(cls));
        }
      }

      _annotationCache.put(annotation, types);
    }

    return types;
  }

  public String getHashForTypesAnnotatedWith(final String seed, final Class<? extends Annotation> annotation) {
    if (!CacheUtil.getCache(CacheHolder.class).ANNOTATIONS_TO_CLASS.containsKey(annotation.getName())) {
      return "0";
    } else {
      try {
        final MessageDigest md = MessageDigest.getInstance("SHA-256");

        if (seed != null) {
          md.update(seed.getBytes());
        }

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (final SortableClassFileWrapper classFileWrapper : CacheUtil
                .getCache(CacheHolder.class).ANNOTATIONS_TO_CLASS.get(annotation.getName())) {
          byteArrayOutputStream.reset();
          final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
          classFileWrapper.getClassFile().write(dataOutputStream);
          dataOutputStream.flush();
          md.update(byteArrayOutputStream.toByteArray());
        }

        return RebindUtils.hashToHexString(md.digest());

      } catch (final Exception e) {
        throw new RuntimeException("could not generate hash", e);
      }
    }
  }

  public Multimap<String, String> getErraiProperties() {
    return propScanner.getProperties();
  }

  public static class CacheHolder implements CacheStore {
    final Map<String, Set<SortableClassFileWrapper>> ANNOTATIONS_TO_CLASS = new ConcurrentHashMap<>();

    @Override
    public void clear() {
      ANNOTATIONS_TO_CLASS.clear();
    }
  }
}
