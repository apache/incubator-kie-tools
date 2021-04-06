/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.marshalling.rebind.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.util.ClassChangeUtil;
import org.jboss.errai.common.metadata.RebindUtils;
import org.jboss.errai.common.rebind.ClassListReader;
import org.jboss.errai.marshalling.server.MappingContextSingleton;
import org.jboss.errai.marshalling.server.ServerMappingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * Discovers directories where generated class files can be written.
 */
public class OutputDirectoryUtil {

  private static final class ModuleStrategy implements DiscoveryStrategy {
    @Override
    public Set<String> getCandidate(final GeneratorContext context,
                                    final DiscoveryContext veto) {

      final String moduleWorkingDir = RebindUtils.guessWorkingDirectoryForModule(context);
      return Arrays
              .stream(candidateOutputDirectories)
              .map(relPath -> moduleWorkingDir + File.separator + relPath)
              .collect(Collectors.toSet());
    }
  }

  private static final class CurrentWorkingDirectoryStrategy implements DiscoveryStrategy {
    @Override
    public Set<String> getCandidate(final GeneratorContext context,
                                    final DiscoveryContext veto) {
      final String cwd = new File("").getAbsolutePath();
      return Arrays
              .stream(candidateOutputDirectories)
              .map(relPath -> cwd + File.separator + relPath)
              .collect(Collectors.toSet());
    }
  }

  private static final class MarshallerModelStrategy implements DiscoveryStrategy {
    @Override
    public Set<String> getCandidate(final GeneratorContext context,
                                    final DiscoveryContext discoveryContext) {

      final ServerMappingContext ctx = MappingContextSingleton.get();

      final Map<String, String> matchNames = new HashMap<String, String>();

      for (final MetaClass cls : ctx.getDefinitionsFactory().getExposedClasses()) {
        matchNames.put(cls.getName(), cls.getName());
      }

      final File cwd = new File("").getAbsoluteFile();
      final Set<File> roots = findMatchingOutputDirectoryByModel(matchNames, cwd);

      if (roots.isEmpty()) {        
        discoveryContext.veto();
      }

      final Set<String> rootsPaths = new HashSet<String>();
      for (final File f : roots) {
        rootsPaths.add(f.getAbsolutePath());
      }

      return rootsPaths;
    }
  }

  private static final class ClassListManifestStrategy implements DiscoveryStrategy {
    private static class Candidate {
      int score;
      File root;
    }
    @Override
    public Set<String> getCandidate(final GeneratorContext context, final DiscoveryContext veto) {
      final File cwd = new File("").getAbsoluteFile();
      final Set<File> matching = findAllMatching("classlist.mf", cwd);
      final Set<String> candidateDirectories = new HashSet<String>();

      if (!matching.isEmpty()) {

        Candidate bestCandidate = null;
        String gwtModuleName = RebindUtils.getModuleName(context);

        if (gwtModuleName != null) {

          if (gwtModuleName.endsWith(".JUnit")) {
            gwtModuleName = gwtModuleName.substring(0, gwtModuleName.length() - 6);
          }
          final int endIndex = gwtModuleName.lastIndexOf('.');
          if (endIndex != -1) {
            gwtModuleName = gwtModuleName.substring(0, endIndex);
          }
          else {
            gwtModuleName = "";
          }

          for (final File f : matching) {
            final Candidate candidate = new Candidate();
            candidate.root = f.getParentFile();

            try {
              final Set<String> clazzes = ClassListReader.getClassSetFromFile(f);

              for (final String fqcn : clazzes) {
                final JClassType type = context.getTypeOracle().findType(fqcn);
                if (type != null && fqcn.startsWith(gwtModuleName)) {
                  candidate.score++;
                }
              }
            } catch (final Throwable ignored) {}

            if (candidate.score > 0 && (bestCandidate == null || candidate.score > bestCandidate.score)) {
              bestCandidate = candidate;
            }
          }

          if (bestCandidate != null) {
            candidateDirectories.add(bestCandidate.root.getAbsolutePath());
          }
        }
      }

      return candidateDirectories;
    }
  }

  private static class ReverseMatchResult {
    private final boolean match;
    private final File matchRoot;

    private ReverseMatchResult(final boolean match, final File matchRoot) {
      this.match = match;
      this.matchRoot = matchRoot;
    }

    public boolean isMatch() {
      return match;
    }

    public File getMatchRoot() {
      return matchRoot;
    }
  }

  private static Logger log = LoggerFactory.getLogger(OutputDirectoryUtil.class);

  public static final String OUTPUT_DIR_PROP = "errai.server.classOutput";

  public static final Optional<String> OUTPUT_DIR = Optional.ofNullable(System.getProperty(OUTPUT_DIR_PROP, null));

  private static final String[] candidateOutputDirectories = {
      "war/WEB-INF/classes/",
      "web/WEB-INF/classes/",
      "target/war/WEB-INF/classes/",
      "WEB-INF/classes/",
      "src/main/webapp/WEB-INF/classes/"
  };

  private static final DiscoveryStrategy[] rootDiscoveryStrategies = new DiscoveryStrategy[] {
        new CurrentWorkingDirectoryStrategy(),
        new ClassListManifestStrategy(),
        new MarshallerModelStrategy(),
        new ModuleStrategy(),
  };

  public static Set<File> findMatchingOutputDirectoryByModel(final Map<String, String> toMatch, final File from) {
    final HashSet<File> matching = new HashSet<File>();
    _findMatchingOutputDirectoryByModel(matching, toMatch, from);
    return matching;
  }

  private static void _findMatchingOutputDirectoryByModel(final Set<File> matching,
                                                          final Map<String, String> toMatch,
                                                          final File from) {
    if (from.getName().startsWith(".")) return;
    
    if (from.isDirectory()) {
      final File[] files = from.listFiles();
      if (files != null) {
        for (final File file : files) {
          _findMatchingOutputDirectoryByModel(matching, toMatch, file);
        }
      }
    }
    else {
      String name = from.getName();
      if (name.endsWith(".class") && toMatch.containsKey(name = name.substring(0, name.length() - 6)) 
              && Arrays.stream(candidateOutputDirectories).anyMatch(s -> from.getAbsolutePath().contains(s))) {
        
        final String full = toMatch.get(name);
        final ReverseMatchResult res = reversePathMatch(full, from);

        if (res.isMatch()) {
          matching.add(res.getMatchRoot());
        }
      }
    }
  }

  public static Set<File> findAllMatching(final String fileName, final File from) {
    final HashSet<File> matching = new HashSet<File>();
    _findAllMatching(matching, fileName, from);
    return matching;
  }

  public static void _findAllMatching(final HashSet<File> matching, final String fileName, final File from) {
    if (from.isDirectory()) {
      final File[] files = from.listFiles();
      if (files != null) {
        for (final File file : files) {
          _findAllMatching(matching, fileName, file);
        }
      }
      else {
        log.debug("Failed to read: " + from.getAbsolutePath());
      }
    }
    else {
      if (fileName.equals(from.getName())) {
        matching.add(from);
      }
    }
  }

  private static ReverseMatchResult reversePathMatch(final String fqcn, final File location) {
    final List<String> stk = new ArrayList<String>(Arrays.asList(fqcn.split("\\.")));

    File curr = location;

    if (!stk.isEmpty()) {
      // remove the last element -- as that would be the file name.
      stk.remove(stk.size() - 1);
    }

    while (!stk.isEmpty()) {
      final String el = stk.remove(stk.size() - 1);
      curr = curr.getParentFile();
      if (curr == null || !curr.getName().equals(el)) {
        break;
      }
    }

    if (curr != null) {
      curr = curr.getParentFile();
    }

    if (stk.isEmpty()) {
      return new ReverseMatchResult(true, curr);
    }
    else {
      return new ReverseMatchResult(false, curr);
    }
  }

  public static void forEachDiscoveredOutputDir(final GeneratorContext context, final Consumer<File> consumer) {
    log.info("Searching candidate output directories...");
    File outputDirCdt;

    int deposits = 0;

    Strategies:
    for (final DiscoveryStrategy strategy : rootDiscoveryStrategies) {
      final DiscoveryContext discoveryContext = DiscoveryContext.create();
      for (final String path : strategy.getCandidate(context, discoveryContext)) {
        log.info("Considering '" + path + "' as an output path (" + strategy.getClass().getSimpleName() + ")");

        if (discoveryContext.isVetoed()) {
          continue Strategies;
        }

        outputDirCdt = new File(path).getAbsoluteFile();
        if (outputDirCdt.exists()) {
          log.info("   Accepting '" + outputDirCdt + "' output directory.");
          consumer.accept(outputDirCdt);
          deposits++;
        }
        else {
          log.info("   Rejecting " + outputDirCdt + " because it does not exist");
        }
      }
      if (deposits > 0) {
        break;
      }
    }

    if (deposits == 0) {
      log.warn("   A target output could not be resolved through configuration or auto-detection!\n"
              + "   Your deployment may be missing required class files.");
    }
  }

  public static void generateClassFileInDiscoveredDirs(final GeneratorContext context, final String packageName, final String simpleClassName,
          final String sourceOutputTemp, final String source) {
    forEachDiscoveredOutputDir(context, outputDirCdt -> {
      try {
        final String classFilePath = ClassChangeUtil.generateClassFile(packageName, simpleClassName, sourceOutputTemp,
                source, outputDirCdt.getAbsolutePath());
        log.info("** Wrote {}.{} class to {}", packageName, simpleClassName, classFilePath);
      }
      catch (final Throwable t) {
        log.warn("Encountered error while trying to generate {}.{} class in {}", packageName, simpleClassName, outputDirCdt.getAbsolutePath());
      }
    });
  }

  public static void generateClassFileInTmpDir(final String packageName, final String simpleClassName, final String source, final String tmpDirPath) {
    final String classFilePath = ClassChangeUtil.generateClassFile(packageName, simpleClassName, tmpDirPath, source, tmpDirPath);
    try {
      ClassChangeUtil.loadClassDefinition(classFilePath, packageName, simpleClassName);
    } catch (final IOException e) {
      throw new RuntimeException("Could not load " + packageName + "." + simpleClassName, e);
    }
  }

}
