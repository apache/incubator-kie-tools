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


package org.jboss.errai.codegen.gwt.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.dev.javac.testing.GeneratorContextBuilder;
import com.google.gwt.dev.javac.testing.JavaSource;
import com.google.gwt.dev.javac.testing.Source;
import org.jboss.errai.common.metadata.RebindUtils;

/**
 * A factory that helps create a mock TypeOracle that knows about a certain
 * collection of classes.
 *
 * @author Mike Brock
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
public final class MockacleFactory {

  private static final Source MVEL_NULL_TYPE = new JavaSource("org.mvel2.util.NullType") {
    @Override
    public String getSource() {
      return "package org.mvel2.util;" +
              "public class NullType {}";
    }
  };

  private final File pathToTestFiles;
  private final List<Source> sourceFilesToAdd = new ArrayList<Source>();

  /**
   * Creates a new type oracle factory that can load classes whose source is
   * rooted at the given directory.
   *
   * @param sourceDir
   *          The root directory for the tree of .java files that will be loaded
   *          into the mock TypeOracle. Must exist and must be a directory.
   */
  public MockacleFactory(File sourceDir) {
    if (!sourceDir.exists()) {
      throw new IllegalArgumentException("Source location " + sourceDir.getAbsolutePath() + " does not exist");
    }
    if (!sourceDir.isDirectory()) {
      throw new IllegalArgumentException("Source location " + sourceDir.getAbsolutePath() + " is not a directory");
    }
    pathToTestFiles = sourceDir;

    sourceFilesToAdd.add(MVEL_NULL_TYPE);
  }

  public void addTestClass(final String fqcn) {

    sourceFilesToAdd.add(new JavaSource(fqcn) {
//      @Override
//      public String getPath() {
//        return getRelativePathToClassFromName(fqcn);
//      }

      @Override
      public String getSource() {
        return RebindUtils.readFileToString(new File(pathToTestFiles, getPath()));
      }
    });
  }

  public TypeOracle generateMockacle() {
    final GeneratorContextBuilder contextBuilder = GeneratorContextBuilder.newCoreBasedBuilder();

    for (final Source source : sourceFilesToAdd) {
      contextBuilder.add(source);
    }

    final GeneratorContext context = contextBuilder.buildGeneratorContext();

    return context.getTypeOracle();
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

  private static String getRelativePathToClassFromName(final String fqcn) {
    return fqcn.replaceAll("\\.", "/") + ".java";
  }

  private String getFullyQualifiedPathToClassFromName(final String fqcn) {
    return new File(pathToTestFiles, getPackageFromFQCN(fqcn)).getAbsolutePath();
  }
}
