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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.io.Files;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.dev.cfg.ModuleDef;
import com.google.gwt.dev.javac.StandardGeneratorContext;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class RebindUtils {
  public static final String ERRAI_DEVEL_NOCACHE_PROPERTY = "errai.devel.nocache";
  public static boolean NO_CACHE = Boolean.getBoolean(ERRAI_DEVEL_NOCACHE_PROPERTY);

  private static String hashSeed = "errai21CR2";

  private static volatile String _tempDirectory;

  public static String getTempDirectory() {
    if (_tempDirectory != null) {
      return _tempDirectory;
    }

    final String useramePortion = System.getProperty("user.name").replaceAll("[^0-9a-zA-Z]", "-");
    final File file =
        new File(System.getProperty("java.io.tmpdir") + "/" + useramePortion + "/errai/" + getClasspathHash() + "/");

    if (!file.exists()) {
      // noinspection ResultOfMethodCallIgnored
      file.mkdirs();
    }

    return _tempDirectory = file.getAbsolutePath();
  }

  private static final String[] hashableExtensions = { ".java", ".class", ".properties", ".xml" };

  private static boolean isValidFileType(final String fileName) {
    for (final String extension : hashableExtensions) {
      if (fileName.endsWith(extension))
        return true;
    }
    return false;
  }

  public static String getClasspathHash() {
    try {
      final MessageDigest md = MessageDigest.getInstance("SHA-1");
      final String classPath = System.getProperty("java.class.path");
      md.update(hashSeed.getBytes());

      for (final String p : classPath.split(System.getProperty("path.separator"))) {
        _recurseDir(new File(p), new FileVisitor() {
          @Override
          public void visit(final File f) {
            final String fileName = f.getName();
            if (isValidFileType(fileName)) {
              md.update(fileName.getBytes());
              final long lastModified = f.lastModified();
              // md.update((byte) ((lastModified >> 56 & 0xFF)));
              // md.update((byte) ((lastModified >> 48 & 0xFF)));
              // md.update((byte) ((lastModified >> 40 & 0xFF)));
              // md.update((byte) ((lastModified >> 32 & 0xFF)));
              md.update((byte) ((lastModified >> 24 & 0xFF)));
              md.update((byte) ((lastModified >> 16 & 0xFF)));
              md.update((byte) ((lastModified >> 8 & 0xFF)));
              md.update((byte) ((lastModified & 0xFF)));

              final long length = f.length();
              //
              // md.update((byte) ((length >> 56 & 0xFF)));
              // md.update((byte) ((length >> 48 & 0xFF)));
              // md.update((byte) ((length >> 40 & 0xFF)));
              // md.update((byte) ((length >> 32 & 0xFF)));
              md.update((byte) ((length >> 24 & 0xFF)));
              md.update((byte) ((length >> 16 & 0xFF)));
              md.update((byte) ((length >> 8 & 0xFF)));
              md.update((byte) ((length & 0xFF)));
            }
          }
        });
      }

      return hashToHexString(md.digest());
    }
    catch (final Exception e) {
      throw new RuntimeException("failed to generate hash for classpath fingerprint", e);
    }
  }

  public static String hashToHexString(final byte[] hash) {
    final StringBuilder hexString = new StringBuilder();
    for (final byte b : hash) {
      hexString.append(Integer.toHexString(0xFF & b));
    }
    return hexString.toString();
  }

  public static File getErraiCacheDir() {
    String cacheDir = System.getProperty("errai.devel.debugCacheDir");
    if (cacheDir == null)
      cacheDir = new File(".errai/").getAbsolutePath();
    final File fileCacheDir = new File(cacheDir);
    // noinspection ResultOfMethodCallIgnored
    fileCacheDir.mkdirs();
    return fileCacheDir;
  }

  /**
   * Writes the given Java class source to a file in the correct package subdirectory within the directory returned by
   * {@link #getErraiCacheDir()}.
   *
   * @param packageName
   *          The package name of the Java class.
   * @param simpleClassName
   *          The simple name of the Java class.
   * @param source
   *          The source of the Java class.
   */
  public static void writeStringToJavaSourceFileInErraiCacheDir(final String packageName, final String simpleClassName, final String source) {
    final File dir = new File(getErraiCacheDir() + File.separator + packageName.replace('.', File.separatorChar));
    dir.mkdirs();
    final File sourceFile = new File(dir, simpleClassName + ".java");

    writeStringToFile(sourceFile, source);
  }

  public static void writeStringToFile(final File file, final String data) {
    try {
      final OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file, false));
      outputStream.write(data.getBytes("UTF-8"));
      outputStream.close();
    }
    catch (final IOException e) {
      throw new RuntimeException("could not write file for debug cache", e);
    }
  }

  public static String readFileToString(final File file) {
    try {
      return Files.toString(file, Charset.forName("UTF-8"));
    }
    catch (final IOException e) {
      throw new RuntimeException("could not read file for debug cache", e);
    }
  }

  public static String packageNameToDirName(final String pkg) {
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < pkg.length(); i++) {
      if (pkg.charAt(i) == '.') {
        sb.append(File.separator);
      }
      else {
        sb.append(pkg.charAt(i));
      }
    }
    return sb.toString();
  }

  private interface FileVisitor {
    public void visit(File f);
  }

  private static void _recurseDir(final File f, final FileVisitor visitor) {
    if (f.isDirectory()) {
      for (final File file : f.listFiles()) {
        _recurseDir(file, visitor);
      }
    }
    else {
      visitor.visit(f);
    }
  }

  private static ModuleDef getModuleDef(final GeneratorContext context) {
    final StandardGeneratorContext standardGeneratorContext =
      (StandardGeneratorContext) context;
    try {
      final Field moduleField = StandardGeneratorContext.class.getDeclaredField("module");
      moduleField.setAccessible(true);
      return (ModuleDef) moduleField.get(standardGeneratorContext);
    }
    catch (final Throwable t) {
      try {
        // for GWT versions higher than 2.5.1 we need to get the ModuleDef out of the
        // CompilerContext
        final Field compilerContextField = StandardGeneratorContext.class.getDeclaredField("compilerContext");
        compilerContextField.setAccessible(true);
        // Using plain Object because CompilerContext doesn't exist in GWT 2.5
        final Object compilerContext = compilerContextField.get(standardGeneratorContext);
        final Method getModuleMethod = compilerContext.getClass().getMethod("getModule");
        return (ModuleDef) getModuleMethod.invoke(compilerContext);
      }
      catch (final Throwable t2) {
        throw new RuntimeException("could not get module definition (you may be using an incompatible GWT version)", t);
      }
    }
  }

  public static Set<File> getAllModuleXMLs(final GeneratorContext context) {
    final ModuleDef moduleDef = getModuleDef(context);

    try {
      final Field gwtXmlFilesField = ModuleDef.class.getDeclaredField("gwtXmlFiles");
      gwtXmlFilesField.setAccessible(true);
      return (Set<File>) gwtXmlFilesField.get(moduleDef);
    }
    catch (final Throwable t) {
      throw new RuntimeException("could not access 'gwtXmlFiles' field from the module definition " +
          "(you may be using an incompatible GWT version)");
    }
  }

  public static Set<String> getInheritedModules(final GeneratorContext context) {
    final ModuleDef moduleDef = getModuleDef(context);

    try {
      final Field inheritedModules = ModuleDef.class.getDeclaredField("inheritedModules");
      inheritedModules.setAccessible(true);
      return (Set<String>) inheritedModules.get(moduleDef);
    }
    catch (final Throwable t) {
      throw new RuntimeException("could not access 'inheritedModules' field from the module definition " +
          "(you may be using an incompatible GWT version)");
    }
  }

  public static boolean isModuleInherited(final GeneratorContext context, final String moduleName) {
    return getInheritedModules(context).contains(moduleName);
  }

  public static Set<String> getReloadablePackageNames(final GeneratorContext context) {
    final Set<String> result = new HashSet<String>();
    final ModuleDef module = getModuleDef(context);
    if (module == null) {
      return result;
    }

    final String moduleName = module.getCanonicalName().replace(".JUnit", "");
    result.add(StringUtils.substringBeforeLast(moduleName, "."));

    final List<String> dottedModulePaths = new ArrayList<String>();
    for (final File moduleXmlFile : getAllModuleXMLs(context)) {
      String fileName = moduleXmlFile.getAbsolutePath();
      fileName = fileName.replace(File.separatorChar, '.');
      dottedModulePaths.add(fileName);
    }

    for (final String inheritedModule : getInheritedModules(context)) {
      for (final String dottedModulePath : dottedModulePaths) {
        if (dottedModulePath.contains(inheritedModule)) {
          result.add(StringUtils.substringBeforeLast(inheritedModule, "."));
        }
      }
    }

    return result;
  }

  public static String getModuleName(final GeneratorContext context) {
    try {
      return getModuleDef(context).getCanonicalName();
    }
    catch (final Throwable t) {
      return null;
    }
  }

  private static volatile GeneratorContext _lastTranslatableContext;
  private static volatile Set<String> _translatablePackagesCache;

  /**
   * Returns a list of all translatable packages accessible to the module under compilation
   * (including inherited modules).
   */
  public static Set<String> findTranslatablePackages(final GeneratorContext context) {
    if (context.equals(_lastTranslatableContext) && _translatablePackagesCache != null) {
      return _translatablePackagesCache;
    }
    _lastTranslatableContext = context;

    final JPackage[] jPackages = context.getTypeOracle().getPackages();
    final Set<String> packages = new HashSet<String>(jPackages.length * 2);
    for (final JPackage p : jPackages) {
      packages.add(p.getName());
    }

    return _translatablePackagesCache = Collections.unmodifiableSet(packages);
  }
}
