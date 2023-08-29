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


package org.jboss.errai.reflections;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Pattern;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.jboss.errai.reflections.scanners.MethodParameterScanner;
import org.jboss.errai.reflections.scanners.Scanner;
import org.jboss.errai.reflections.scanners.SubTypesScanner;
import org.jboss.errai.reflections.scanners.TypeAnnotationsScanner;
import org.jboss.errai.reflections.serializers.Serializer;
import org.jboss.errai.reflections.util.ClasspathHelper;
import org.jboss.errai.reflections.util.ConfigurationBuilder;
import org.jboss.errai.reflections.util.FilterBuilder;
import org.jboss.errai.reflections.util.Utils;
import org.jboss.errai.reflections.vfs.Vfs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

/**
 * Reflections one-stop-shop object
 * <p>Reflections scans your classpath, indexes the metadata, allows you to query it on runtime
 * and may save and collect that information for many modules within your project.
 * <p>Using Reflections you can query your metadata such as:
 * <ul>
 * <li>get all subtypes of some type
 * <li>get all types/methods/fields annotated with some annotation, w/o annotation parameters matching
 * </ul>
 * <p>a typical use of Reflections would be:
 * <pre>
 * Reflections reflections = new Reflections("my.package.prefix"); //replace my.package.prefix with your package prefix, of course
 *
 * Set&#60Class&#60? extends SomeType>> subTypes = reflections.getSubTypesOf(SomeType.class);
 * Set&#60Class&#60?>> annotated = reflections.getTypesAnnotatedWith(SomeAnnotation.class);
 * Set&#60Class&#60?>> annotated1 = reflections.getTypesAnnotatedWith(
 *      new SomeAnnotation() {public String value() {return "1";}
 *                            public Class&#60? extends Annotation> annotationType() {return SomeAnnotation.class;}});
 * </pre>
 * basically, to use Reflections for scanning and querying, instantiate it with a {@link org.jboss.errai.reflections.Configuration}, for example
 * <pre>
 *      new Reflections(
 *          new ConfigurationBuilder()
 *              .filterInputsBy(new FilterBuilder().include("your project's common package prefix here..."))
 *              .setUrls(ClasspathHelper.forClassLoader())
 *              .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner().filterResultsBy(myClassAnnotationsFilter)));
 * </pre>
 * and than use the convenient methods to query the metadata, such as {@link #getSubTypesOf},
 * {@link #getTypesAnnotatedWith}, {@link #getMethodsAnnotatedWith}, {@link #getFieldsAnnotatedWith}, {@link #getResources}
 * <br>use {@link #getStore()} to access and query the store directly
 * <p>in order to save a metadata use {@link #save(String)} or {@link #save(String, org.jboss.errai.reflections.serializers.Serializer)}
 * for example with {@link org.jboss.errai.reflections.serializers.XmlSerializer} or {@link org.jboss.errai.reflections.serializers.JavaCodeSerializer}
 * <p>in order to collect pre saved metadata and avoid re-scanning, use {@link #collect(String, com.google.common.base.Predicate)}
 * <p>* be aware that when using the constructor new Reflections("my.package"), only urls with prefix 'my.package' will be scanned,
 * and any transitive classes in other urls will not be scanned (for example if my.package.SomeClass extends other.package.OtherClass,
 * than the later will not be scanned). in that case specify the urls manually using {@link ConfigurationBuilder}
 * <p><p><p>For Javadoc, source code, and more information about Reflections Library, see http://code.google.com/p/reflections/
 */
public class Reflections extends ReflectionUtils {
  private static final Logger log = LoggerFactory.getLogger(Reflections.class);

  protected final transient Configuration configuration;
  private Store store;

  /**
   * constructs a Reflections instance and scan according to given {@link Configuration}
   * <p>it is preferred to use {@link org.jboss.errai.reflections.util.ConfigurationBuilder}
   */
  public Reflections(final Configuration configuration) {
    this.configuration = configuration;
    store = new Store(configuration);

    //inject to scanners
    for (Scanner scanner : configuration.getScanners()) {
      scanner.setConfiguration(configuration);
      scanner.setStore(store.get(scanner));
    }
  }

  /**
   * a convenient constructor for scanning within a package prefix
   * <p>this actually create a {@link Configuration} with:
   * <br> - urls that contain resources with name {@code prefix}
   * <br> - acceptsInput where name starts with the given {@code prefix}
   * <br> - scanners set to the given {@code scanners}, otherwise defaults to {@link TypeAnnotationsScanner} and {@link SubTypesScanner}.
   * <br> - scanner results filter is set to accept results matching given {@code prefix}
   */
  public Reflections(final String prefix, final Scanner... scanners) {
    this(new ConfigurationBuilder() {
      {
        final Predicate<String> filter = new FilterBuilder.Include(FilterBuilder.prefix(prefix));

        setUrls(ClasspathHelper.forPackage(prefix));
        filterInputsBy(filter);

        if (scanners != null && scanners.length != 0) {
          for (Scanner scanner : scanners) {
            scanner.filterResultsBy(Predicates.<String>and(filter, scanner.getResultFilter()));
          }
          setScanners(scanners);
        }
        else {
          setScanners(
              new TypeAnnotationsScanner().filterResultsBy(filter),
              new SubTypesScanner().filterResultsBy(filter));
        }
      }
    });
  }

  /**
   * a convenient constructor for scanning within given package prefixes and urls containing given classes.
   * <p>given urlHints is an array of either String or Class elements, where Strings results in scanning package prefix
   * and Class results in scanning url that contains that class, for example
   * <pre>
   *     new Reflections(new Object[] {"my.package", com.google.inject.Module, "javax.persistence"})
   * </pre>
   * would result in scanning packages 'my.package' and 'javax.persistence' and also the url that contains the class of com.google.inject.Module
   * <p>this actually create a {@link Configuration} with:
   * <br> - urls that contain resources with name {@code prefix} or that contains given classes
   * <br> - acceptsInput where name starts with the given {@code prefix} or with the classes package name
   * <br> - scanners set to the given {@code scanners}, otherwise defaults to {@link TypeAnnotationsScanner} and {@link SubTypesScanner}.
   * <br> - scanner results filter is set to accept results matching given {@code prefix} or the given classes package name
   *
   * @param urlHints
   *     is an array of either String or Class elements, where Strings results in scanning package prefix and Class results in scanning urls containing the class
   */
  public Reflections(final Object[] urlHints, final Scanner... scanners) {
    this(new ConfigurationBuilder() {
      {
        final List<String> prefixes = Lists.newArrayList();

        for (Object urlHint : urlHints) {
          if (urlHint instanceof String) {
            addUrls(ClasspathHelper.forPackage((String) urlHint));
            prefixes.add((String) urlHint);
          }
          else if (urlHint instanceof Class) {
            addUrls(ClasspathHelper.forClass((Class) urlHint));
            prefixes.add(((Class) urlHint).getPackage().getName());
          }
        }

        final FilterBuilder filter = new FilterBuilder();
        for (String prefix : prefixes) {
          filter.include(FilterBuilder.prefix(prefix));
        }
        filterInputsBy(filter);

        if (scanners != null && scanners.length != 0) {
          for (Scanner scanner : scanners) {
            scanner.filterResultsBy(Predicates.<String>and(filter, scanner.getResultFilter()));
          }
          setScanners(scanners);
        }
        else {
          setScanners(
              new TypeAnnotationsScanner().filterResultsBy(filter),
              new SubTypesScanner().filterResultsBy(filter));
        }
      }
    });
  }

  //used for serialization
  protected Reflections() {
    configuration = null;
  }

  //
  protected void scan() {
    if (configuration.getUrls() == null || configuration.getUrls().isEmpty()) {
      //  log.error("given scan urls are empty. set urls in the configuration");
      return;
    }
    else {
      if (log.isDebugEnabled()) {
        StringBuilder urls = new StringBuilder();
        for (URL url : configuration.getUrls()) {
          urls.append("\t").append(url.toExternalForm()).append("\n");
        }
        log.debug("going to scan these urls:\n" + urls);
      }
    }

    long time = System.currentTimeMillis();

    ExecutorService executorService = configuration.getExecutorService();

    if (executorService == null) {
      for (URL url : configuration.getUrls()) {
        try {
          for (final Vfs.File file : Vfs.fromURL(url).getFiles()) {
            scan(file);
          }
        }
        catch (ReflectionsException e) {
          log.error("could not create Vfs.Dir from url. ignoring the exception and continuing", e);
        }

      }
    }
    else {
      //todo use CompletionService
      List<Future<?>> futures = Lists.newArrayList();
      try {
        for (URL url : configuration.getUrls()) {
          try {
            for (final Vfs.File file : Vfs.fromURL(url).getFiles()) {
              futures.add(executorService.submit(new Runnable() {
                public void run() {
                  scan(file);
                }
              }));
            }
          }
          catch (ReflectionsException e) {
            log.error("could not create Vfs.Dir from url. ignoring the exception and continuing", e);
          }
        }

        for (Future future : futures) {
          try {
            future.get();
          }
          catch (Exception e) {
            throw new RuntimeException(e);
          }
        }
      }
      finally {
        executorService.shutdown();
      }
    }

    time = System.currentTimeMillis() - time;

    Integer keys = store.getKeysCount();
    Integer values = store.getValuesCount();

    log.info(format("Reflections took %d ms to scan %d urls, producing %d keys and %d values %s",
        time, configuration.getUrls().size(), keys, values,
        executorService != null && executorService instanceof ThreadPoolExecutor ?
            format("[using %d cores]", ((ThreadPoolExecutor) executorService).getMaximumPoolSize()) : ""));
  }

  private void scan(Vfs.File file) {
    String input = file.getRelativePath();
    if (configuration.acceptsInput(input)) {
      for (Scanner scanner : configuration.getScanners()) {
        try {
          if (scanner.acceptsInput(input)) {
            scanner.scan(file);
          }
        }
        catch (Exception e) {
          log.warn("could not scan file " + file.getFullPath() + " with scanner " + scanner.getName(), e);
        }
      }
    }
  }

  /**
   * collect saved Reflection xml resources and merge it into a Reflections instance
   * <p>by default, resources are collected from all urls that contains the package META-INF/reflections
   * and includes files matching the pattern .*-reflections.xml
   */
  public static Reflections collect() {
    return new Reflections(new ConfigurationBuilder()).
        collect("META-INF/reflections", new FilterBuilder().include(".*-reflections.xml"));
  }

  /**
   * collect saved Reflections resources from all urls that contains the given packagePrefix and matches the given resourceNameFilter
   * and de-serializes them using the serializer configured in the configuration
   * <p/>
   * it is preferred to use a designated resource prefix (for example META-INF/reflections but not just META-INF),
   * so that relevant urls could be found much faster
   */
  public Reflections collect(final String packagePrefix, final Predicate<String> resourceNameFilter) {
    return collect(packagePrefix, resourceNameFilter, configuration.getSerializer());
  }

  /**
   * collect saved Reflections resources from all urls that contains the given packagePrefix and matches the given resourceNameFilter
   * and de-serializes them using the serializer configured in the configuration
   * <p/>
   * it is preferred to use a designated resource prefix (for example META-INF/reflections but not just META-INF),
   * so that relevant urls could be found much faster
   */
  public Reflections collect(final String packagePrefix, final Predicate<String> resourceNameFilter, final Serializer serializer) {
    for (final Vfs.File file : Vfs.findFiles(ClasspathHelper.forPackage(packagePrefix), packagePrefix, resourceNameFilter)) {
      InputStream inputStream = null;
      try {
        inputStream = file.openInputStream();
        merge(serializer.read(inputStream));
        log.info("Reflections collected metadata from " + file + " using serializer " + serializer.getClass().getName());
      }
      catch (IOException e) {
        throw new ReflectionsException("could not merge " + file, e);
      }
      finally {
        Utils.close(inputStream);
      }
    }

    return this;
  }

  /**
   * merges saved Reflections resources from the given input stream, using the serializer configured in this instance's Configuration
   * <br> useful if you know the serialized resource location and prefer not to look it up the classpath
   */
  public Reflections collect(final InputStream inputStream) {
    try {
      merge(configuration.getSerializer().read(inputStream));
      log.info("Reflections collected metadata from input stream using serializer " + configuration.getSerializer().getClass().getName());
    }
    catch (Exception ex) {
      throw new ReflectionsException("could not merge input stream", ex);
    }

    return this;
  }

  /**
   * merges saved Reflections resources from the given file, using the serializer configured in this instance's Configuration
   * <p/>
   * useful if you know the serialized resource location and prefer not to look it up the classpath
   */
  public Reflections collect(final File file) {
    FileInputStream inputStream = null;
    try {
      inputStream = new FileInputStream(file);
      return collect(inputStream);
    }
    catch (FileNotFoundException e) {
      throw new ReflectionsException("could not obtain input stream from file " + file, e);
    }
    finally {
      if (inputStream != null) try {
        inputStream.close();
      }
      catch (IOException e) { /*ignore*/ }
    }
  }

  /**
   * merges a Reflections instance metadata into this instance
   */
  public Reflections merge(final Reflections reflections) {
    store.merge(reflections.store);
    return this;
  }

  //query

  /**
   * gets all sub types in hierarchy of a given type
   * <p/>depends on SubTypesScanner configured, otherwise an empty set is returned
   */
  public <T> Set<Class<? extends T>> getSubTypesOf(final Class<T> type) {
    Set<String> subTypes = store.getSubTypesOf(type.getName());
    return ImmutableSet.copyOf(ReflectionUtils.<T>forNames(subTypes));
  }

  /**
   * get types annotated with a given annotation, both classes and annotations
   * <p>{@link java.lang.annotation.Inherited} is honored
   * <p><i>Note that this (@Inherited) meta-annotation type has no effect if the annotated type is used for anything other than a class.
   * Also, this meta-annotation causes annotations to be inherited only from superclasses; annotations on implemented interfaces have no effect.</i>
   * <p/>depends on TypeAnnotationsScanner and SubTypesScanner configured, otherwise an empty set is returned
   */
  public Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation) {
    Set<String> typesAnnotatedWith = store.getTypesAnnotatedWith(annotation.getName());
    return ImmutableSet.copyOf(forNames(typesAnnotatedWith));
  }

  /**
   * get types annotated with a given annotation, both classes and annotations
   * <p>{@link java.lang.annotation.Inherited} is honored according to given honorInherited
   * <p><i>Note that this (@Inherited) meta-annotation type has no effect if the annotated type is used for anything other than a class.
   * Also, this meta-annotation causes annotations to be inherited only from superclasses; annotations on implemented interfaces have no effect.</i>
   * <p/>depends on TypeAnnotationsScanner and SubTypesScanner configured, otherwise an empty set is returned
   */
  public Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation, boolean honorInherited) {
    Set<String> typesAnnotatedWith = store.getTypesAnnotatedWith(annotation.getName(), honorInherited);
    return ImmutableSet.copyOf(forNames(typesAnnotatedWith));
  }

  //todo create a string version of these

  /**
   * get types annotated with a given annotation, both classes and annotations, including annotation member values matching
   * <p>{@link java.lang.annotation.Inherited} is honored
   * <p/>depends on TypeAnnotationsScanner configured, otherwise an empty set is returned
   */
  public Set<Class<?>> getTypesAnnotatedWith(final Annotation annotation) {
    return getTypesAnnotatedWith(annotation, true);
  }

  /**
   * get types annotated with a given annotation, both classes and annotations, including annotation member values matching
   * <p>{@link java.lang.annotation.Inherited} is honored according to given honorInherited
   * <p/>depends on TypeAnnotationsScanner configured, otherwise an empty set is returned
   */
  public Set<Class<?>> getTypesAnnotatedWith(final Annotation annotation, boolean honorInherited) {
    return getMatchingAnnotations(
        getTypesAnnotatedWith(annotation.annotationType(), honorInherited), annotation);
  }

  /**
   * get all methods annotated with a given annotation
   * <p/>depends on MethodAnnotationsScanner configured, otherwise an empty set is returned
   */
  public Set<Method> getMethodsAnnotatedWith(final Class<? extends Annotation> annotation) {
    Set<String> annotatedWith = store.getMethodsAnnotatedWith(annotation.getName());

    Set<Method> result = Sets.newHashSet();
    for (String annotated : annotatedWith) {
      result.add(Utils.getMethodFromDescriptor(annotated));
    }

    return result;
  }

  /**
   * get all methods annotated with a given annotation, including annotation member values matching
   * <p/>depends on MethodAnnotationsScanner configured, otherwise an empty set is returned
   */
  public Set<Method> getMethodsAnnotatedWith(final Annotation annotation) {
    return getMatchingAnnotations(
        getMethodsAnnotatedWith(annotation.annotationType()), annotation);
  }

  /**
   * get all fields annotated with a given annotation
   * <p/>depends on FieldAnnotationsScanner configured, otherwise an empty set is returned
   */
  public Set<Field> getFieldsAnnotatedWith(final Class<? extends Annotation> annotation) {
    final Set<Field> result = Sets.newHashSet();

    Collection<String> annotatedWith = store.getFieldsAnnotatedWith(annotation.getName());
    for (String annotated : annotatedWith) {
      result.add(Utils.getFieldFromString(annotated));
    }

    return result;
  }

  /**
   * get all methods annotated with a given annotation, including annotation member values matching
   * <p/>depends on FieldAnnotationsScanner configured, otherwise an empty set is returned
   */
  public Set<Field> getFieldsAnnotatedWith(final Annotation annotation) {
    return getMatchingAnnotations(
        getFieldsAnnotatedWith(annotation.annotationType()), annotation);
  }

  /**
   * get 'converter' methods that could effectively convert from type 'from' to type 'to'
   * <p>depends on ConvertersScanner configured, otherwise an empty set is returned
   *
   * @param from
   *     - the type to convert from
   * @param to
   *     - the required return type
   */
  public Set<Method> getConverters(final Class<?> from, final Class<?> to) {
    Set<Method> result = Sets.newHashSet();

    Set<String> converters = store.getConverters(from.getName(), to.getName());
    for (String converter : converters) {
      result.add(Utils.getMethodFromDescriptor(converter));
    }

    return result;
  }
  

  /** get methods with any parameter annotated with given annotation */
  public Set<Method> getMethodsWithAnyParamAnnotated(Class<? extends Annotation> annotation) {
      Set<String> methods = getStore().get(MethodParameterScanner.class, annotation.getName());
      Set<Method> result = Sets.newHashSet();
      for (String method : methods) {
        result.add(Utils.getMethodFromDescriptor(method));
      }
      return result;

  }

  /**
   * get resources relative paths where simple name (key) matches given namePredicate
   * <p>depends on ResourcesScanner configured, otherwise an empty set is returned
   */
  public Set<String> getResources(final Predicate<String> namePredicate) {
    return store.getResources(namePredicate);
  }

  /**
   * get resources relative paths where simple name (key) matches given regular expression
   * <p>depends on ResourcesScanner configured, otherwise an empty set is returned
   * <pre>Set<String> xmls = reflections.getResources(".*\\.xml");</pre>
   */
  public Set<String> getResources(final Pattern pattern) {
    return getResources(new Predicate<String>() {
      public boolean apply(String input) {
        return pattern.matcher(input).matches();
      }
    });
  }

  /**
   * returns the store used for storing and querying the metadata
   */
  public Store getStore() {
    return store;
  }

  /**
   * serialize to a given directory and filename
   * <p>* it is preferred to specify a designated directory (for example META-INF/reflections),
   * so that it could be found later much faster using the load method
   * <p>see the documentation for the save method on the configured {@link org.jboss.errai.reflections.serializers.Serializer}
   */
  public File save(final String filename) {
    return save(filename, configuration.getSerializer());
  }

  /**
   * serialize to a given directory and filename using given serializer
   * <p>* it is preferred to specify a designated directory (for example META-INF/reflections),
   * so that it could be found later much faster using the load method
   */
  public File save(final String filename, final Serializer serializer) {
    File file = serializer.save(this, filename);
    log.info("Reflections successfully saved in " + file + " using " + serializer.getClass().getSimpleName());
    return file;
  }
}
