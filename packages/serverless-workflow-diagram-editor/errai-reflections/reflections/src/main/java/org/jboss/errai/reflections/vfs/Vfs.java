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


package org.jboss.errai.reflections.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.jboss.errai.reflections.ReflectionsException;
import org.jboss.errai.reflections.util.Utils;

/**
 * a simple virtual file system bridge
 * <p>use the {@link org.jboss.errai.reflections.vfs.Vfs#fromURL(java.net.URL)} to get a {@link org.jboss.errai.reflections.vfs.Vfs.Dir}
 * and than use {@link org.jboss.errai.reflections.vfs.Vfs.Dir#getFiles()} to iterate over it's {@link org.jboss.errai.reflections.vfs.Vfs.File}
 * <p>for example:
 * <pre>
 *      Vfs.Dir dir = Vfs.fromURL(url);
 *      Iterable<Vfs.File> files = dir.getFiles();
 *      for (Vfs.File file : files) {
 *          InputStream is = file.openInputStream();
 *      }
 * </pre>
 * <p>use {@link org.jboss.errai.reflections.vfs.Vfs#findFiles(java.util.Collection, com.google.common.base.Predicate)} to get an
 * iteration of files matching given name predicate over given list of urls
 * <p><p>{@link org.jboss.errai.reflections.vfs.Vfs#fromURL(java.net.URL)} uses static {@link org.jboss.errai.reflections.vfs.Vfs.DefaultUrlTypes} to resolves URLs
 * and it can be plugged in with {@link org.jboss.errai.reflections.vfs.Vfs#addDefaultURLTypes(org.jboss.errai.reflections.vfs.Vfs.UrlType)} or {@link org.jboss.errai.reflections.vfs.Vfs#setDefaultURLTypes(java.util.List)}.
 * <p>for example:
 * <pre>
 *      Vfs.addDefaultURLTypes(new Vfs.UrlType() {
 *          public boolean matches(URL url)         {
 *              return url.getProtocol().equals("http");
 *          }
 *          public Vfs.Dir createDir(final URL url) {
 *              return new HttpDir(url); //implement this type... (check out a naive implementation on VfsTest)
 *          }
 *      });
 *
 *      Vfs.Dir dir = Vfs.fromURL(new URL("http://mirrors.ibiblio.org/pub/mirrors/maven2/org/slf4j/slf4j-api/1.5.6/slf4j-api-1.5.6.jar"));
 * </pre>
 */
public abstract class Vfs {
  private static List<UrlType> defaultUrlTypes = Lists.<UrlType>newArrayList(DefaultUrlTypes.values());

  /**
   * an abstract vfs dir
   */
  public interface Dir {
    String getPath();

    Iterable<File> getFiles();

    void close();
  }

  /**
   * an abstract vfs file
   */
  public interface File {
    String getName();

    String getRelativePath();

    String getFullPath();

    InputStream openInputStream() throws IOException;
  }

  /**
   * a matcher and factory for a url
   */
  public interface UrlType {
    boolean matches(URL url);

    Dir createDir(URL url);
  }

  /**
   * the default url types that will be used when issuing {@link org.jboss.errai.reflections.vfs.Vfs#fromURL(java.net.URL)}
   */
  public static List<UrlType> getDefaultUrlTypes() {
    return defaultUrlTypes;
  }

  /**
   * sets the static default url types. can be used to statically plug in urlTypes
   */
  public static void setDefaultURLTypes(final List<UrlType> urlTypes) {
    defaultUrlTypes = urlTypes;
  }

  /**
   * add a static default url types. can be used to statically plug in urlTypes
   */
  public static void addDefaultURLTypes(UrlType urlType) {
    defaultUrlTypes.add(urlType);
  }

  /**
   * tries to create a Dir from the given url, using the defaultUrlTypes
   */
  public static Dir fromURL(final URL url) {
    return fromURL(url, defaultUrlTypes);
  }

  /**
   * tries to create a Dir from the given url, using the given urlTypes
   */
  public static Dir fromURL(final URL url, final List<UrlType> urlTypes) {
    for (UrlType type : urlTypes) {
      if (type.matches(url)) {
        try {
          return type.createDir(url);
        }
        catch (Exception e) {
          throw new ReflectionsException("could not create Dir using " + type.getClass().getName() + " from url " + url.toExternalForm(), e);
        }
      }
    }

    throw new ReflectionsException("could not create Vfs.Dir from url, no matching UrlType was found [" + url.toExternalForm() + "]\n" +
            "Available types: " + urlTypes + "\n" +
            "Do you need to add app server specific support to your deployment? (For example, errai-jboss-as-support for AS7)");
  }

  /**
   * tries to create a Dir from the given url, using the given urlTypes
   */
  public static Dir fromURL(final URL url, final UrlType... urlTypes) {
    return fromURL(url, Lists.<UrlType>newArrayList(urlTypes));
  }

  /**
   * return an iterable of all {@link org.jboss.errai.reflections.vfs.Vfs.File} in given urls, matching filePredicate
   */
  public static Iterable<File> findFiles(final Collection<URL> inUrls, final Predicate<File> filePredicate) {
    Iterable<File> result = new ArrayList<File>();

    for (URL url : inUrls) {
      Iterable<File> iterable = Iterables.filter(fromURL(url).getFiles(), filePredicate);
      result = Iterables.concat(result, iterable);
    }

    return result;
  }

  /**
   * return an iterable of all {@link org.jboss.errai.reflections.vfs.Vfs.File} in given urls, starting with given packagePrefix and matching nameFilter
   */
  public static Iterable<File> findFiles(final Collection<URL> inUrls, final String packagePrefix, final Predicate<String> nameFilter) {
    Predicate<File> fileNamePredicate = new Predicate<File>() {
      public boolean apply(File file) {
        String path = file.getRelativePath();
        if (path.startsWith(packagePrefix)) {
          String filename = path.substring(path.indexOf(packagePrefix) + packagePrefix.length());
          return !Utils.isEmpty(filename) && nameFilter.apply(filename.substring(1));
        }
        else {
          return false;
        }
      }
    };

    return findFiles(inUrls, fileNamePredicate);
  }

  /**
   * default url types used by {@link org.jboss.errai.reflections.vfs.Vfs#fromURL(java.net.URL)}
   * <p/>
   * <p>jarfile - creates a {@link org.jboss.errai.reflections.vfs.ZipDir} over jar file
   * <p>jarUrl - creates a {@link org.jboss.errai.reflections.vfs.ZipDir} over a jar url (contains ".jar!/" in it's name)
   * <p>directory - creates a {@link org.jboss.errai.reflections.vfs.SystemDir} over a file system directory
   * <p>vfsfile and vfszip - creates a {@link org.jboss.errai.reflections.vfs.ZipDir} over jboss vfs types
   */
  public static enum DefaultUrlTypes implements UrlType {
    jarfile {
      public boolean matches(URL url) {
        return url.getProtocol().equals("file") && url.toExternalForm().endsWith(".jar");
      }

      public Dir createDir(final URL url) {
        return new ZipDir(url);
      }
    },

    jarUrl {
      public boolean matches(URL url) {
        return url.toExternalForm().contains(".jar!");
      }

      public Dir createDir(URL url) {
        return new ZipDir(url);
      }
    },

    directory {
      public boolean matches(URL url) {
        return url.getProtocol().equals("file") && new java.io.File(normalizePath(url)).isDirectory();
      }

      public Dir createDir(final URL url) {
        return new SystemDir(url);
      }
    },

    vfsfile {
      public boolean matches(URL url) {
        return url.getProtocol().equals("vfsfile") && url.toExternalForm().endsWith(".jar");
      }

      public Dir createDir(URL url) {
        return new ZipDir(url.toString().replaceFirst("vfsfile:", "file:"));
      }
    },

    vfszip {
      public boolean matches(URL url) {
        return url.getProtocol().equals("vfszip") && url.toExternalForm().endsWith(".jar");
      }

      public Dir createDir(URL url) {
        return new ZipDir(url.toString().replaceFirst("vfszip:", "file:"));
      }
    }
  }

  //
  public static String normalizePath(final URL url) {
    try {
      return url.toURI().getPath();
    }
    catch (URISyntaxException e) {
      String actualFilePath = url.getPath();
      String nestedPath = "";
      if (actualFilePath.startsWith("file:")) {
        actualFilePath = actualFilePath.substring(5);
      }

      int nestedSeperator = actualFilePath.indexOf('!');
      if (nestedSeperator != -1) {
        nestedPath = actualFilePath.substring(nestedSeperator + 1);
        actualFilePath = actualFilePath.substring(0, nestedSeperator);

        if (nestedPath.equals("/")) {
          nestedPath = "";
        }
      }
      return actualFilePath;
    }
  }
}
