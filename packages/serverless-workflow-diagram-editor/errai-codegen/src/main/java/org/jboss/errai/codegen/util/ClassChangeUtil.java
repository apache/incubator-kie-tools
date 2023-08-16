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


package org.jboss.errai.codegen.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.core.compiler.batch.BatchCompiler;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BatchAnnotationProcessorManager;
import org.jboss.errai.common.metadata.ErraiAppPropertiesFiles;
import org.jboss.errai.common.metadata.RebindUtils;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Mike Brock
 */
/*
 * This block prevents the Maven Shade plugin to remove the specified classes
 */
public class ClassChangeUtil {
  private static final String USE_NATIVE_JAVA_COMPILER = "errai.marshalling.use_native_javac";
  private static final String CLASSLOADING_MODE_PROPERTY = "errai.marshalling.classloading.mode";

  private static final String classLoadingMode;
  private static final boolean useNativeJavac = Boolean.getBoolean(USE_NATIVE_JAVA_COMPILER);
  private static Logger log = getLogger(ClassChangeUtil.class);

  static {

    /*
     * This block prevents the Maven Shade plugin to remove the specified classes
     */
    @SuppressWarnings ("unused") Class<?>[] classes = new Class<?>[] {
            BatchAnnotationProcessorManager.class
    };

    if (System.getProperty(CLASSLOADING_MODE_PROPERTY) != null) {
      classLoadingMode = System.getProperty(CLASSLOADING_MODE_PROPERTY);
    }
    else {
      classLoadingMode = "thread";
    }
  }


  private static interface CompilerAdapter {
    int compile(OutputStream out, OutputStream errors, String outputPath, String toCompile, String classpath);
  }

  public static class JDKCompiler implements CompilerAdapter {
    final JavaCompiler compiler;

    public JDKCompiler(final JavaCompiler compiler) {
      this.compiler = compiler;
    }

    @Override
    public int compile(final OutputStream out, final OutputStream errors, final String outputPath, final String toCompile, final String classpath) {
      return compiler.run(null, out, errors, "-classpath", classpath, "-d", outputPath, toCompile);
    }
  }

  public static class JDTCompiler implements CompilerAdapter {
    @Override
    public int compile(final OutputStream out,
                       final OutputStream errors,
                       final String outputPath,
                       final String toCompile,
                       final String classpath) {

      return BatchCompiler.compile(new String[] { "-classpath", classpath, "-d", outputPath, "-source", "1.8", toCompile },
          new PrintWriter(out), new PrintWriter(errors),
          new CompilationProgress() {
            @Override
            public void begin(final int remainingWork) {
            }

            @Override
            public void done() {
            }

            @Override
            public boolean isCanceled() {
              return false;
            }

            @Override
            public void setTaskName(final String name) {
            }

            @Override
            public void worked(final int workIncrement, final int remainingWork) {
            }
          }) ? 0 : -1;
    }
  }

  public static Class<?> compileAndLoad(final File sourceFile,
                                     final String fullyQualifiedName) throws IOException {
    final String packageName = getPackageFromFQCN(fullyQualifiedName);
    final String className = getNameFromFQCN(fullyQualifiedName);

    return compileAndLoad(sourceFile, packageName, className);
  }

  public static Class<?> compileAndLoad(final File sourceFile,
                                     final String packageName,
                                     final String className) throws IOException {

    return compileAndLoad(sourceFile.getParentFile().getAbsolutePath(), packageName, className);

  }

  public static Class<?> compileAndLoad(final String sourcePath,
                                     final String packageName,
                                     final String className) throws IOException {
    final String tempDirectory = RebindUtils.getTempDirectory();

    return compileAndLoad(sourcePath, packageName, className, tempDirectory);
  }


  public static Class<?> compileAndLoad(final String sourcePath,
                                     final String packageName,
                                     final String className,
                                     final String outputPath) throws IOException {

    final String outputLocation = compileClass(sourcePath, packageName, className, outputPath);
    return loadClassDefinition(outputLocation, packageName, className);
  }

  public static String compileClass(final String sourcePath,
                                    final String packageName,
                                    final String className,
                                    final String outputPath) {
    try {

      final ByteArrayOutputStream errorOutputStream = new ByteArrayOutputStream();
      final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      final CompilerAdapter adapter;

      if (compiler == null || !useNativeJavac) {
        adapter = new JDTCompiler();
      }
      else {
        adapter = new JDKCompiler(compiler);
      }

      final File classOutputDir = new File(outputPath
          + File.separatorChar + RebindUtils.packageNameToDirName(packageName)
          + File.separatorChar).getAbsoluteFile();

      // delete any marshaller classes already there
      final Pattern matcher = Pattern.compile("^" + className + "(\\.|$).*class$");
      if (classOutputDir.exists()) {
        final File[] files = classOutputDir.listFiles();
        if (files != null) {
          for (final File file : files) {
            if (matcher.matcher(file.getName()).matches()) {
              file.delete();
            }
          }
        }
      }

      final StringBuilder sb = new StringBuilder(4096);
      final List<URL> moduleUrls = ErraiAppPropertiesFiles.getModulesUrls();
      final List<File> classpathElements = new ArrayList<>(moduleUrls.size());
      classpathElements.add(new File(outputPath));

      log.debug(">>> Searching for all jars");
      for (final URL url : moduleUrls) {
        final File file = getFileIfExists(url.getFile());
        if (file != null) {
          classpathElements.add(file);
        }
      }
      log.debug("<<< Done searching for all jars");

      for (final File file : classpathElements) {
        sb.append(file.getAbsolutePath()).append(File.pathSeparator);
      }

      sb.append(System.getProperty("java.class.path"));
      sb.append(findAllJarsByManifest());

      final String classPath = sb.toString();

      /**
       * Attempt to run the compiler without any classpath specified.
       */
      if (adapter.compile(System.out, errorOutputStream, outputPath,
          new File(sourcePath + File.separator + className + ".java").getAbsolutePath(), classPath) != 0) {

        System.err.println("*** FAILED TO COMPILE CLASS ***");
        System.err.println("*** Classpath Used: " + classPath);

        for (final byte b : errorOutputStream.toByteArray()) {
          System.err.print((char) b);
        }
        return null;
      }

      return new File(classOutputDir.getAbsolutePath() + File.separatorChar
          + className + ".class").getAbsolutePath();
    }
    catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Class<?> loadClassDefinition(final String path,
                                          final String packageName,
                                          final String className) throws IOException {
    if (path == null) return null;

    FileInputStream inputStream = new FileInputStream(path);
    byte[] classDefinition = new byte[inputStream.available()];

    final String classBase = path.substring(0, path.length() - ".class".length());

    final BootstrapClassloader clsLoader = new BootstrapClassloader(new File(path).getParentFile().getAbsolutePath(),
        "system".equals(classLoadingMode) ?
            ClassLoader.getSystemClassLoader() :
            Thread.currentThread().getContextClassLoader());

    final String fqcn;
    if ("".equals(packageName)) {
      fqcn = className;
    }
    else {
      fqcn = packageName + "." + className;
    }

    boolean success = false;
    try {
      final Class<?> loadClass = clsLoader.loadClass(fqcn);
      success = true;
      return loadClass;
    }
    catch (final Throwable t) {
      // fall through
    }
    finally {
      if (success) {
        try {
          inputStream.close();
        }
        catch (final Throwable ignore) {
        }
      }
    }

    inputStream.read(classDefinition);

    final File[] files = new File(path).getParentFile().listFiles();
    if (files != null) {
      for (final File file : files) {
        if (file.getName().startsWith(className + "$")) {
          String s = file.getName();
          s = s.substring(s.indexOf('$') + 1, s.lastIndexOf('.'));

          final String nestedClassName = fqcn + "$" + s;

          Class<?> cls = null;
          try {
            cls = clsLoader.loadClass(nestedClassName);
          } catch (final ClassNotFoundException ignored) {
          }

          if (cls != null)
            continue;

          final String innerClassBaseName = classBase + "$" + s;
          final File innerClass = new File(innerClassBaseName + ".class");
          if (innerClass.exists()) {
            try {
              inputStream = new FileInputStream(innerClass);
              classDefinition = new byte[inputStream.available()];
              inputStream.read(classDefinition);

              clsLoader.defineClassX(nestedClassName, classDefinition, 0, classDefinition.length);
            } finally {
              inputStream.close();
            }
          }
          else {
            break;
          }
        }
      }
    }

    final Class<?> mainClass = clsLoader
        .defineClassX(fqcn, classDefinition, 0, classDefinition.length);

    inputStream.close();

    for (int i = 1; i < Integer.MAX_VALUE; i++) {

      final String nestedClassName = fqcn + "$" + i;

      Class<?> cls = null;
      try {
        cls = clsLoader.loadClass(nestedClassName);
      }
      catch (final ClassNotFoundException ignored) {
      }

      if (cls != null) continue;


      final String innerClassBaseName = classBase + "$" + i;
      final File innerClass = new File(innerClassBaseName + ".class");
      if (innerClass.exists()) {
        try {
          inputStream = new FileInputStream(innerClass);
          classDefinition = new byte[inputStream.available()];
          inputStream.read(classDefinition);

          clsLoader.defineClassX(nestedClassName, classDefinition, 0, classDefinition.length);
        }
        finally {
          inputStream.close();
        }
      }
      else {
        break;
      }
    }

    return mainClass;
  }

  private static class BootstrapClassloader extends ClassLoader {
    private final String searchPath;

    private BootstrapClassloader(final String searchPath, final ClassLoader classLoader) {
      super(classLoader);
      this.searchPath = searchPath;
    }

    public Class<?> defineClassX(final String className, final byte[] b, final int off, final int len) {
      return super.defineClass(className, b, off, len);
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
      try {
        return super.findClass(name);
      }
      catch (final ClassNotFoundException e) {
        try {
          FileInputStream inputStream = null;
          final byte[] classDefinition;


          final File innerClass = new File(searchPath + "/" + name.substring(name.lastIndexOf('.') + 1) + ".class");
          if (innerClass.exists()) {
            try {
              inputStream = new FileInputStream(innerClass);
              classDefinition = new byte[inputStream.available()];
              inputStream.read(classDefinition);

              return defineClassX(name, classDefinition, 0, classDefinition.length);
            }
            finally {
              if (inputStream != null) inputStream.close();
            }

          }
        }
        catch (final IOException e2) {
          throw new RuntimeException("failed to load class: " + name, e2);
        }


      }
      throw new ClassNotFoundException(name);
    }
  }

  @SuppressWarnings("rawtypes")
  private static String findAllJarsByManifest() {
    final StringBuilder cp = new StringBuilder();
    try {
      log.debug(">>> Searching for all jars using " + JarFile.MANIFEST_NAME);
      final Enumeration[] enumerations = new Enumeration[]
          {
              Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME),
              ClassLoader.getSystemClassLoader().getResources(JarFile.MANIFEST_NAME)
          };

      for (final Enumeration resEnum : enumerations) {
        while (resEnum.hasMoreElements()) {
          try {
            String path = ((URL) resEnum.nextElement()).getFile();
            path = path.substring(0, path.length() - JarFile.MANIFEST_NAME.length() - 1);

            final File file = getFileIfExists(path);
            if (file != null) {
              cp.append(File.pathSeparator).append(file.getAbsolutePath());
            }
          }
          catch (final Exception e) {
            log.warn("Ignoring classpath entry with invalid manifest", e);
          }
        }
      }
    }
    catch (final IOException e1) {
      log.warn("Failed to build classpath using manifest discovery. Expect compilation failures...", e1);
    }
    finally {
      log.debug("<<< Done searching for all jars using " + JarFile.MANIFEST_NAME);
    }

    return cp.toString();
  }

  public static File getFileIfExists(String path) {
    final String originalPath = path;

    if (path.startsWith("file:")) {
      path = path.substring(5);

      final int outerElement = path.indexOf('!');
      if (outerElement != -1) {
        path = path.substring(0, outerElement);
      }
    }
    else if (path.startsWith("jar:")) {
      path = path.substring(4);

      final int outerElement = path.indexOf('!');
      if (outerElement != -1) {
        path = path.substring(0, outerElement);
      }
    }

    final File file = new File(path);
    if (file.exists()) {
      if (log.isDebugEnabled()) {
        log.debug("   EXISTS: " + originalPath + " -> " + file.getAbsolutePath());
      }
      return file;
    }

    if (log.isDebugEnabled()) {
      log.debug("  !EXISTS: " + originalPath + " -> " + file.getAbsolutePath());
    }
    return null;
  }

  private static String getPackageFromFQCN(final String fqcn) {
    final int index = fqcn.lastIndexOf('.');
    if (index == -1) {
      return "";
    }
    else {
      return fqcn.substring(0, index);
    }
  }

  private static String getNameFromFQCN(final String fqcn) {
    final int index = fqcn.lastIndexOf('.');
    if (index == -1) {
      return fqcn;
    }
    else {
      return fqcn.substring(index + 1);
    }
  }

  private static List<String> urlToFile(Enumeration<URL> urls) {
    final ArrayList<String> files = new ArrayList<String>();
    while (urls.hasMoreElements()) {
      final URL url = urls.nextElement();
      if (url.getProtocol().equals("file")) {
        files.add(url.getFile());
      }
    }
    return files;
  }

  /**
   * Finds all urls of classes that are not in jars.
   */
  private static Set<String> getClassLocations(final String packageName, final String simpleClassName) throws IOException {
      final String classResource = packageName.replaceAll("\\.", "/") + "/" + simpleClassName + ".class";
      final Set<String> locations = new LinkedHashSet<String>();

      // look for the class in every classloader we can think of. For example, current thread
      // classloading works in Jetty but not JBoss AS 7.
      locations.addAll(urlToFile(Thread.currentThread().getContextClassLoader().getResources(classResource)));
      locations.addAll(urlToFile(ClassChangeUtil.class.getClassLoader().getResources(classResource)));
      locations.addAll(urlToFile(ClassLoader.getSystemResources(classResource)));

      return locations;
  }

  public static Optional<File> getNewest(final Set<String> locations) {
    return locations.stream()
                    .map(url -> getFileIfExists(url))
                    .filter(f -> f != null)
                    .max(Comparator.comparingLong(f -> f.lastModified()));
  }

  public static Optional<Class<?>> loadClassIfPresent(final String packageName, final String simpleClassName) {
    final String fullyQualifiedClassName = packageName + "." + simpleClassName;

    try {
      log.info("Searching for class: {}", fullyQualifiedClassName);
      final Set<String> locations = getClassLocations(packageName, simpleClassName);
      final Optional<File> newest = getNewest(locations);

      if (locations.size() > 1) {
        log.warn("*** MULTIPLE VERSIONS OF " + fullyQualifiedClassName + " FOUND IN CLASSPATH: " +
                "Attempted to guess the newest one based on file dates. But you should clean your output directories.");

        locations.stream().forEach(loc -> log.warn(" Ambiguous version -> {}", loc));
      }

      if (newest.isPresent()) {
        log.info("Loading class {} found at {}", fullyQualifiedClassName, newest.get().getAbsolutePath().toString());
        return Optional.of(loadClassDefinition(newest.get().getAbsolutePath(), packageName, simpleClassName));
      }
      else {
        log.info("Could not find URL for {}. Attempting to load with context class loader.", fullyQualifiedClassName);
        try {
          // maybe we're in an appserver with a VFS, so try to load anyways.
          final Class<?> loadedClass = Thread.currentThread().getContextClassLoader().loadClass(fullyQualifiedClassName);
          log.info("Successfully loaded {} with context class loader.", fullyQualifiedClassName);
          return Optional.of(loadedClass);
        }
        catch (final ClassNotFoundException e) {
          log.warn("Could not load {} class.", fullyQualifiedClassName);

          return Optional.empty();
        }
      }
    }
    catch (final IOException e) {
      log.warn("Could not read {} class: " + fullyQualifiedClassName, e);

      return Optional.empty();
    }
  }

  public static String generateClassFile(final String packageName, final String simpleClassName,
          final String sourceDir, final String source, final String outputPath) {
    final File outputDir = new File(sourceDir + File.separator +
        RebindUtils.packageNameToDirName(packageName) + File.separator);

    final File classOutputPath = new File(outputPath);

    //noinspection ResultOfMethodCallIgnored
    outputDir.mkdirs();

    final File sourceFile
        = new File(outputDir.getAbsolutePath() + File.separator + simpleClassName + ".java");

    RebindUtils.writeStringToFile(sourceFile, source);

    return compileClass(outputDir.getAbsolutePath(),
        packageName,
        simpleClassName,
        classOutputPath.getAbsolutePath());
  }

  public static Class<?> compileAndLoadFromSource(final String packageName, final String simpleClassName,
          final String source) {
    log.info("Compiling and loading {}.{} from source...", packageName, simpleClassName);
    final File directory =
            new File(RebindUtils.getTempDirectory()
                    + "/errai.gen/classes/" + packageName.replaceAll("\\.", "/"));
    final File sourceFile = new File(directory.getAbsolutePath() + File.separator + simpleClassName + ".java");
    log.info("Using temporary directory for source and class files: {}", directory.getAbsolutePath());

    try {
      if (directory.exists()) {
        log.info("Directory {} already exists. Deleting directory and contents (enable debug logging to see deleted files).", directory.getAbsolutePath());
        final File[] files = directory.listFiles();
        if (files != null) {
          for (final File file : files) {
            log.debug("Deleting {}", file.getAbsolutePath());
            file.delete();
          }
        }
        log.debug("Deleting {}", directory.getAbsolutePath());
        directory.delete();
      }
      directory.mkdirs();

      log.info("Writing source file {}...", sourceFile.getAbsolutePath());
      final FileOutputStream outputStream = new FileOutputStream(sourceFile);
      outputStream.write(source.getBytes("UTF-8"));
      outputStream.flush();
      outputStream.close();

      log.info("Compiling {}.{} in source file {}...", packageName, simpleClassName, sourceFile.getAbsolutePath());
      final String compiledClassPath = compileClass(directory.getAbsolutePath(), packageName, simpleClassName,
              directory.getAbsolutePath());

      if (compiledClassPath == null) {
        log.warn("Could not compile {}.{} in source file {}...", packageName, simpleClassName, sourceFile.getAbsolutePath());
        return null;
      }
      else {
        log.info("Loading compiled class at {}...", compiledClassPath);
        return loadClassDefinition(compiledClassPath, packageName, simpleClassName);
      }
    }
    catch (final IOException e) {


      throw new RuntimeException("failed to generate class ", e);
    }
  }
}