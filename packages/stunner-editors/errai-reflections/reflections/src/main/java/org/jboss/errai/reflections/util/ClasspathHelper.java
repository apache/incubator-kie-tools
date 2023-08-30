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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Set;

import com.google.common.collect.Sets;
import org.jboss.errai.reflections.Reflections;

/**
 * Some classpath convenient methods
 */
public abstract class ClasspathHelper {

    public static ClassLoader[] defaultClassLoaders = new ClassLoader[]{getContextClassLoader(), getStaticClassLoader()};

    /** returns {@code Thread.currentThread().getContextClassLoader()} */
    public static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /** returns {@code Reflections.class.getClassLoader()} */
    public static ClassLoader getStaticClassLoader() {
        return Reflections.class.getClassLoader();
    }

    /** returns given classLoaders, if not null,
     * otherwise defaults to both {@link #getContextClassLoader()} and {@link #getStaticClassLoader()} */
    public static ClassLoader[] classLoaders(ClassLoader... classLoaders) {
        return classLoaders != null && classLoaders.length != 0 ? classLoaders : defaultClassLoaders;
    }

    /** returns urls with resources of package starting with given name, using {@link ClassLoader#getResources(String)}
     * <p>that is, forPackage("org.reflections") effectively returns urls from classpath with packages starting with {@code org.reflections}
     * <p>if optional {@link ClassLoader}s are not specified, then both {@link #getContextClassLoader()} and {@link #getStaticClassLoader()} are used for {@link ClassLoader#getResources(String)}
     */
    public static Set<URL> forPackage(String name, ClassLoader... classLoaders) {
        final Set<URL> result = Sets.newHashSet();

        final ClassLoader[] loaders = classLoaders(classLoaders);
        final String resourceName = name.replace(".", "/");

        for (ClassLoader classLoader : loaders) {
            try {
                final Enumeration<URL> urls = classLoader.getResources(resourceName);
                while (urls.hasMoreElements()) {
                    final URL url = urls.nextElement();
                    final URL normalizedUrl = new URL(url.toExternalForm().substring(0, url.toExternalForm().lastIndexOf(resourceName)));
                    result.add(normalizedUrl);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /** returns the url that contains the given class, using {@link ClassLoader#getResource(String)}
     * <p>if optional {@link ClassLoader}s are not specified, then either {@link #getContextClassLoader()} or {@link #getStaticClassLoader()} are used for {@link ClassLoader#getResources(String)}
     * */
    public static URL forClass(Class<?> aClass, ClassLoader... classLoaders) {
        final ClassLoader[] loaders = classLoaders(classLoaders);
        final String resourceName = aClass.getName().replace(".", "/") + ".class";

        for (ClassLoader classLoader : loaders) {
            try {
                final URL url = classLoader.getResource(resourceName);
                if (url != null) {
                    final String normalizedUrl = url.toExternalForm().substring(0, url.toExternalForm().lastIndexOf(aClass.getPackage().getName().replace(".", "/")));
                    return new URL(normalizedUrl);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /** returns urls using {@link java.net.URLClassLoader#getURLs()} up the classloader parent hierarchy
     * <p>if optional {@link ClassLoader}s are not specified, then both {@link #getContextClassLoader()} and {@link #getStaticClassLoader()} are used for {@link ClassLoader#getResources(String)}
     * */
    public static Set<URL> forClassLoader(ClassLoader... classLoaders) {
        final Set<URL> result = Sets.newHashSet();

        final ClassLoader[] loaders = classLoaders(classLoaders);

        for (ClassLoader classLoader : loaders) {
            while (classLoader != null) {
                if (classLoader instanceof URLClassLoader) {
                    URL[] urls = ((URLClassLoader) classLoader).getURLs();
                    if (urls != null) {
                        for (URL url : urls) {
                            try {
                              String urlString = url.toExternalForm();
                              String decodedUrlString =
                                URLDecoder.decode(urlString.replaceAll("\\+", "%2b"), "UTF-8");
                              result.add(new URL(decodedUrlString));
                            }
                            catch (IOException ioe) {
                              throw new RuntimeException("Failed to scan configuration Url's", ioe);
                            }
                        }
                    }
                }
                classLoader = classLoader.getParent();
            }
        }
        return result;
    }
}

