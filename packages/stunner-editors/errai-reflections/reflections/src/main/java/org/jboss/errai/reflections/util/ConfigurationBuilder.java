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


package org.jboss.errai.reflections.util;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import org.jboss.errai.reflections.Configuration;
import org.jboss.errai.reflections.adapters.JavassistAdapter;
import org.jboss.errai.reflections.adapters.MetadataAdapter;
import org.jboss.errai.reflections.scanners.Scanner;
import org.jboss.errai.reflections.scanners.SubTypesScanner;
import org.jboss.errai.reflections.scanners.TypeAnnotationsScanner;
import org.jboss.errai.reflections.serializers.Serializer;
import org.jboss.errai.reflections.serializers.XmlSerializer;

/**
 * a fluent builder for {@link org.jboss.errai.reflections.Configuration}, to be used for constructing a {@link org.jboss.errai.reflections.Reflections} instance
 * <p>usage:
 * <pre>
 *      new Reflections(
 *          new ConfigurationBuilder()
 *              .filterInputsBy(new FilterBuilder().include("your project's common package prefix here..."))
 *              .setUrls(ClasspathHelper.forClassLoader())
 *              .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner().filterResultsBy(myClassAnnotationsFilter)));
 * </pre>
 * <br>{@link #executorService} is used optionally used for parallel scanning. if value is null then scanning is done in a simple for loop
 * <p>defaults: accept all for {@link #inputsFilter},
 * {@link #executorService} is null,
 * {@link #serializer} is {@link org.jboss.errai.reflections.serializers.XmlSerializer}
 */
@SuppressWarnings({"RawUseOfParameterizedType"})
public class ConfigurationBuilder implements Configuration {
    private final Map<String, Scanner> scanners = new HashMap<String, Scanner>();
    private Set<URL> urls = Sets.newHashSet();
    private MetadataAdapter metadataAdapter = new JavassistAdapter();
    private Predicate<String> inputsFilter = Predicates.alwaysTrue();
    private Serializer serializer;
    private ExecutorService executorService;

    public ConfigurationBuilder() {
      final Scanner[] builtins = new Scanner[] {new TypeAnnotationsScanner(), new SubTypesScanner()};
      for (final Scanner scanner : builtins) {
        scanners.put(scanner.getName(), scanner);
      }
    }

    public Set<Scanner> getScanners() {
      return new HashSet<Scanner>(scanners.values());
	}

    /** set the scanners instances for scanning different metadata */
    public ConfigurationBuilder setScanners(final Scanner... scanners) {
        for (final Scanner scanner : scanners) {
          this.scanners.put(scanner.getName(), scanner);
        }
        return this;
    }

    public Set<URL> getUrls() {
        return urls;
    }

    /** set the urls to be scanned
     * <p>use {@link org.jboss.errai.reflections.util.ClasspathHelper} convenient methods to get the relevant urls
     * */
    public ConfigurationBuilder setUrls(final Collection<URL> urls) {
		this.urls = Sets.newHashSet(urls);
        return this;
	}

    /** set the urls to be scanned
     * <p>use {@link org.jboss.errai.reflections.util.ClasspathHelper} convenient methods to get the relevant urls
     * */
    public ConfigurationBuilder setUrls(final URL... urls) {
		this.urls = Sets.newHashSet(urls);
        return this;
	}

    /** set the urls to be scanned
     * <p>use {@link org.jboss.errai.reflections.util.ClasspathHelper} convenient methods to get the relevant urls
     * */
    public ConfigurationBuilder setUrls(final Collection<URL>... urlss) {
        urls.clear();
        addUrls(urlss);
        return this;
    }

    /** add urls to be scanned
     * <p>use {@link org.jboss.errai.reflections.util.ClasspathHelper} convenient methods to get the relevant urls
     * */
    public ConfigurationBuilder addUrls(final Collection<URL> urls) {
        this.urls.addAll(urls);
        return this;
    }

    /** add urls to be scanned
     * <p>use {@link org.jboss.errai.reflections.util.ClasspathHelper} convenient methods to get the relevant urls
     * */
    public ConfigurationBuilder addUrls(final URL... urls) {
        this.urls.addAll(Sets.newHashSet(urls));
        return this;
    }

    /** add urls to be scanned
     * <p>use {@link org.jboss.errai.reflections.util.ClasspathHelper} convenient methods to get the relevant urls
     * */
    public ConfigurationBuilder addUrls(final Collection<URL>... urlss) {
        for (Collection<URL> urls : urlss) { addUrls(urls); }
        return this;
    }

    public MetadataAdapter getMetadataAdapter() {
        return metadataAdapter;
    }

    /** sets the metadata adapter used to fetch metadata from classes */
    public ConfigurationBuilder setMetadataAdapter(final MetadataAdapter metadataAdapter) {
        this.metadataAdapter = metadataAdapter;
        return this;
    }

    public boolean acceptsInput(String inputFqn) {
        return inputsFilter.apply(inputFqn);
    }

    /** sets the input filter for all resources to be scanned
     * <p> supply a {@link com.google.common.base.Predicate} or use the {@link FilterBuilder}*/
    public ConfigurationBuilder filterInputsBy(Predicate<String> inputsFilter) {
        this.inputsFilter = inputsFilter;
        return this;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    /** sets the executor service used for scanning. */
    public ConfigurationBuilder setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    /** sets the executor service used for scanning to ThreadPoolExecutor with core size as {@link java.lang.Runtime#availableProcessors()}
     * <p>default is ThreadPoolExecutor with a single core */
    public ConfigurationBuilder useParallelExecutor() {
        return useParallelExecutor(Runtime.getRuntime().availableProcessors());
    }

    /** sets the executor service used for scanning to ThreadPoolExecutor with core size as the given availableProcessors parameter
     * <p>default is ThreadPoolExecutor with a single core */
    public ConfigurationBuilder useParallelExecutor(final int availableProcessors) {
        setExecutorService(Executors.newFixedThreadPool(availableProcessors));
        return this;
    }

    public Serializer getSerializer() {
        if (serializer == null) {
            serializer = new XmlSerializer(); //lazily defaults to XmlSerializer
        }
        return serializer;
    }

    /** sets the serializer used when issuing {@link org.jboss.errai.reflections.Reflections#save} */
    public ConfigurationBuilder setSerializer(Serializer serializer) {
        this.serializer = serializer;
        return this;
    }
}
