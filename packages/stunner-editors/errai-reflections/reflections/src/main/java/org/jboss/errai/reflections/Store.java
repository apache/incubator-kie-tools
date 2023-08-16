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

import java.lang.annotation.Inherited;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import org.jboss.errai.reflections.scanners.ConvertersScanner;
import org.jboss.errai.reflections.scanners.FieldAnnotationsScanner;
import org.jboss.errai.reflections.scanners.MethodAnnotationsScanner;
import org.jboss.errai.reflections.scanners.ResourcesScanner;
import org.jboss.errai.reflections.scanners.Scanner;
import org.jboss.errai.reflections.scanners.SubTypesScanner;
import org.jboss.errai.reflections.scanners.TypeAnnotationsScanner;
import org.jboss.errai.reflections.scanners.reg.ScannerRegistry;

import static com.google.common.collect.Multimaps.newSetMultimap;
import static com.google.common.collect.Multimaps.synchronizedSetMultimap;

/**
 * stores metadata information in multimaps
 * <p>use the different query methods (getXXX) to query the metadata
 * <p>the query methods are string based, and does not cause the class loader to define the types
 * <p>use {@link org.jboss.errai.reflections.Reflections#getStore()} to access this store
 */
public class Store {

  private final LoadingCache<String, Multimap<String, String>> loadingCache;

  public Store(final Configuration configuration) {
    this(configuration.getExecutorService() != null);
  }

  protected Store(final boolean parallelExecutor) {
    // if (parallelExecutor) {
    final CacheLoader<String, Multimap<String, String>> from = CacheLoader.from(new Function<String, Multimap<String, String>>() {
      public Multimap<String, String> apply(final String indexName) {
        return synchronizedSetMultimap(newSetMultimap(new HashMap<String, Collection<String>>(), new Supplier<Set<String>>() {
          public Set<String> get() {
            return Sets.newHashSet();
          }
        }));
      }
    });

    loadingCache = CacheBuilder.newBuilder().build(from);

  }

  protected Store() {
    this(false);
  }

  /**
   * get the values of given keys stored for the given scanner class
   */
  public Set<String> get(final Class<? extends Scanner> scannerClass, final String... keys) {
    final Set<String> result = Sets.newHashSet();

    final Multimap<String, String> map = get(scannerClass);
    for (final String key : keys) {
      result.addAll(map.get(key));
    }

    return result;
  }

  /**
   * return the multimap store of the given scanner. not immutable
   */
  public Multimap<String, String> get(final Scanner scanner) {
    return get(scanner.getName());
  }

  /**
   * return the multimap store of the given scanner class. not immutable
   */
  public Multimap<String, String> get(final Class<? extends Scanner> scannerClass) {
    return get(ScannerRegistry.getRegistry().getName(scannerClass));
  }

  /**
   * return the multimap store of the given scanner name. not immutable
   */
  public Multimap<String, String> get(final String scannerName) {
    try {
      return loadingCache.get(scannerName);
    }
    catch (ExecutionException e) {
      throw new RuntimeException("error loading from cache", e);
    }
  }

  /**
   * return the store map. not immutable
   */
  public Map<String, Multimap<String, String>> getStoreMap() {
    return loadingCache.asMap();
  }

  /**
   * merges given store into this
   */
  void merge(final Store outer) {
    final ConcurrentMap<String, Multimap<String, String>> storeMap = loadingCache.asMap();
    final ConcurrentMap<String, Multimap<String, String>> outerStoreMap = outer.loadingCache.asMap();
    for (final String indexName : outerStoreMap.keySet()) {
      Multimap<String, String> stringStringMultimap = storeMap.get(indexName);
      if (stringStringMultimap == null) {
        storeMap.put(indexName, stringStringMultimap = HashMultimap.create());
      }

      stringStringMultimap.putAll(outer.get(indexName));
    }
  }

  /**
   * return the keys count
   */
  public Integer getKeysCount() {

    Integer keys = 0;
    for (final Multimap<String, String> multimap : loadingCache.asMap().values()) {
      keys += multimap.keySet().size();
    }
    return keys;
  }

  /**
   * return the values count
   */
  public Integer getValuesCount() {
    Integer values = 0;
    for (final Multimap<String, String> multimap : loadingCache.asMap().values()) {
      values += multimap.size();
    }
    return values;
  }

  //query

  /**
   * get sub types of a given type
   */
  public Set<String> getSubTypesOf(final String type) {
    final Set<String> result = new HashSet<String>();

    final Set<String> subTypes = get(SubTypesScanner.class, type);
    result.addAll(subTypes);

    for (final String subType : subTypes) {
      result.addAll(getSubTypesOf(subType));
    }

    return result;
  }

  /**
   * get types annotated with a given annotation, both classes and annotations
   * <p>{@link java.lang.annotation.Inherited} is honored
   * <p><i>Note that this (@Inherited) meta-annotation type has no effect if the annotated type is used for anything other than a class.
   * Also, this meta-annotation causes annotations to be inherited only from superclasses; annotations on implemented interfaces have no effect.</i>
   */
  public Set<String> getTypesAnnotatedWith(final String annotation) {
    return getTypesAnnotatedWith(annotation, true);
  }

  /**
   * get types annotated with a given annotation, both classes and annotations
   * <p>{@link java.lang.annotation.Inherited} is honored according to given honorInherited
   * <p><i>Note that this (@Inherited) meta-annotation type has no effect if the annotated type is used for anything other than a class.
   * Also, this meta-annotation causes annotations to be inherited only from superclasses; annotations on implemented interfaces have no effect.</i>
   */
  public Set<String> getTypesAnnotatedWith(final String annotation, final boolean honorInherited) {
    final Set<String> result = new HashSet<String>();

    if (isAnnotation(annotation)) {
      final Set<String> types = get(TypeAnnotationsScanner.class, annotation);
      result.addAll(types); //directly annotated

      if (honorInherited && isInheritedAnnotation(annotation)) {
        //when honoring @Inherited, meta-annotation should only effect annotated super classes and it's sub types
        for (final String type : types) {
          if (isClass(type)) {
            result.addAll(getSubTypesOf(type));
          }
        }
      }
      else if (!honorInherited) {
        //when not honoring @Inherited, meta annotation effects all subtypes, including annotations interfaces and classes
        for (final String type : types) {
          if (isAnnotation(type)) {
            result.addAll(getTypesAnnotatedWith(annotation, false));
          }
          else if (hasSubTypes(type)) {
            result.addAll(getSubTypesOf(type));
          }
        }
      }
    }
    return result;
  }

  /**
   * get method names annotated with a given annotation
   */
  public Set<String> getMethodsAnnotatedWith(final String annotation) {
    return get(MethodAnnotationsScanner.class, annotation);
  }

  /**
   * get fields annotated with a given annotation
   */
  public Set<String> getFieldsAnnotatedWith(final String annotation) {
    return get(FieldAnnotationsScanner.class, annotation);
  }

  /**
   * get 'converter' methods that could effectively convert from type 'from' to type 'to'
   */
  public Set<String> getConverters(final String from, final String to) {
    return get(ConvertersScanner.class, ConvertersScanner.getConverterKey(from, to));
  }

  /**
   * get resources relative paths where simple name (key) equals given name
   */
  public Set<String> getResources(final String key) {
    return get(ResourcesScanner.class, key);
  }

  /**
   * get resources relative paths where simple name (key) matches given namePredicate
   */
  public Set<String> getResources(final Predicate<String> namePredicate) {
    final Set<String> keys = get(ResourcesScanner.class).keySet();
    final Collection<String> matches = Collections2.filter(keys, namePredicate);

    return get(ResourcesScanner.class, matches.toArray(new String[matches.size()]));
  }

  /**
   * get resources relative paths where simple name (key) matches given regular expression
   * <pre>Set&#60String> xmls = reflections.getResources(".*\\.xml");</pre>
   */
  public Set<String> getResources(final Pattern pattern) {
    return getResources(new Predicate<String>() {
      public boolean apply(final String input) {
        return pattern.matcher(input).matches();
      }
    });
  }

  //support

  /**
   * is the given type name a class. <p>causes class loading
   */
  public boolean isClass(final String type) {
    //todo create a string version of this
    return !isInterface(type);
  }

  /**
   * is the given type name an interface. <p>causes class loading
   */
  public boolean isInterface(final String aClass) {
    //todo create a string version of this
    return ReflectionUtils.forName(aClass).isInterface();
  }

  /**
   * is the given type is an annotation, based on the metadata stored by TypeAnnotationsScanner
   */
  public boolean isAnnotation(final String typeAnnotatedWith) {
    return getTypeAnnotations().contains(typeAnnotatedWith);
  }

  /**
   * is the given annotation an inherited annotation, based on the metadata stored by TypeAnnotationsScanner
   */
  public boolean isInheritedAnnotation(final String typeAnnotatedWith) {
    return get(TypeAnnotationsScanner.class).get(Inherited.class.getName()).contains(typeAnnotatedWith);
  }

  /**
   * does the given type has sub types, based on the metadata stored by SubTypesScanner
   */
  public boolean hasSubTypes(final String typeAnnotatedWith) {
    return getSuperTypes().contains(typeAnnotatedWith);
  }

  /**
   * get all super types that have stored sub types, based on the metadata stored by SubTypesScanner
   */
  public Multiset<String> getSuperTypes() {
    return get(SubTypesScanner.class).keys();
  }

  /**
   * get all annotations, based on metadata stored by TypeAnnotationsScanner
   */
  public Set<String> getTypeAnnotations() {
    return get(TypeAnnotationsScanner.class).keySet();
  }
}
