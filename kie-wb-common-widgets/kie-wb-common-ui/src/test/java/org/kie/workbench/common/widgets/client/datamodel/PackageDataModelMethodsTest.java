/*
* Copyright 2010 Red Hat, Inc. and/or its affiliates.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.kie.workbench.common.widgets.client.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.enterprise.inject.Instance;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.validation.client.dynamic.DynamicValidator;
import org.junit.Test;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.kie.soup.project.datamodel.imports.HasImports;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.MethodInfo;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.soup.project.datamodel.oracle.ProjectDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;

import static org.junit.Assert.*;
import static org.kie.workbench.common.widgets.client.datamodel.PackageDataModelOracleTestUtils.*;
import static org.mockito.Mockito.*;

public class PackageDataModelMethodsTest {

    @Mock
    private Instance<DynamicValidator> validatorInstance;

    @Test
    public void testMethodsOnJavaClass_TreeMap() throws Exception {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder(new RawMVELEvaluator())
                .addClass(TreeMap.class)
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator(), "java.util")
                .setProjectOracle(projectLoader)
                .build();

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller(packageLoader);
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl(service,
                                                                                       validatorInstance);

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName(packageLoader.getPackageName());
        dataModel.setModelFields(packageLoader.getProjectModelFields());
        dataModel.setMethodInformation(packageLoader.getProjectMethodInformation());
        PackageDataModelOracleTestUtils.populateDataModelOracle(mock(Path.class),
                                                                new MockHasImports(),
                                                                oracle,
                                                                dataModel);

        oracle.getFieldCompletions(TreeMap.class.getSimpleName(),
                                   FieldAccessorsAndMutators.ACCESSOR,
                                   new Callback<ModelField[]>() {
                                       @Override
                                       public void callback(final ModelField[] getters) {
                                           assertEquals(2,
                                                        getters.length);
                                           assertEquals("empty",
                                                        getters[0].getName());
                                           assertEquals("this",
                                                        getters[1].getName());
                                       }
                                   });

        oracle.getMethodInfos(TreeMap.class.getSimpleName(),
                              new Callback<List<MethodInfo>>() {
                                  @Override
                                  public void callback(final List<MethodInfo> mis) {
                                      assertContains("ceilingEntry",
                                                     mis);
                                      assertContains("ceilingKey",
                                                     mis);
                                      assertContains("clear",
                                                     mis);
                                      assertContains("clone",
                                                     mis);
                                      assertContains("comparator",
                                                     mis);
                                      assertContains("containsKey",
                                                     mis);
                                      assertContains("containsValue",
                                                     mis);
                                      assertContains("descendingKeySet",
                                                     mis);
                                      assertContains("descendingMap",
                                                     mis);
                                      assertContains("firstEntry",
                                                     mis);
                                      assertContains("firstKey",
                                                     mis);
                                      assertContains("floorEntry",
                                                     mis);
                                      assertContains("floorKey",
                                                     mis);
                                      assertContains("get",
                                                     mis);
                                      assertContains("headMap",
                                                     mis);
                                      assertContains("headMap",
                                                     mis);
                                      assertContains("higherEntry",
                                                     mis);
                                      assertContains("higherKey",
                                                     mis);
                                      assertContains("lastEntry",
                                                     mis);
                                      assertContains("lastKey",
                                                     mis);
                                      assertContains("lowerEntry",
                                                     mis);
                                      assertContains("lowerKey",
                                                     mis);
                                      assertContains("navigableKeySet",
                                                     mis);
                                      assertContains("pollFirstEntry",
                                                     mis);
                                      assertContains("pollLastEntry",
                                                     mis);
                                      assertContains("put",
                                                     mis);
                                      assertContains("remove",
                                                     mis);
                                      assertContains("size",
                                                     mis);
                                      assertContains("subMap",
                                                     mis);
                                      assertContains("subMap",
                                                     mis);
                                      assertContains("tailMap",
                                                     mis);
                                      assertContains("tailMap",
                                                     mis);
                                      assertContains("values",
                                                     mis);
                                  }
                              });

        oracle.getFieldCompletions(TreeMap.class.getSimpleName(),
                                   FieldAccessorsAndMutators.MUTATOR,
                                   new Callback<ModelField[]>() {
                                       @Override
                                       public void callback(final ModelField[] setters) {
                                           assertEquals(0,
                                                        setters.length);
                                       }
                                   });
    }

    @Test
    public void testMethodsOnJavaClass_ArrayList() throws Exception {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder(new RawMVELEvaluator())
                .addClass(ArrayList.class)
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator(), "java.util")
                .setProjectOracle(projectLoader)
                .build();

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller(packageLoader);
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl(service,
                                                                                       validatorInstance);

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName(packageLoader.getPackageName());
        dataModel.setModelFields(packageLoader.getProjectModelFields());
        dataModel.setMethodInformation(packageLoader.getProjectMethodInformation());
        PackageDataModelOracleTestUtils.populateDataModelOracle(mock(Path.class),
                                                                new MockHasImports(),
                                                                oracle,
                                                                dataModel);

        oracle.getMethodInfos(ArrayList.class.getSimpleName(),
                              new Callback<List<MethodInfo>>() {
                                  @Override
                                  public void callback(final List<MethodInfo> methodInfos) {
                                      assertNotNull(methodInfos);
                                      assertFalse(methodInfos.isEmpty());
                                      for (final MethodInfo methodInfo : methodInfos) {
                                          assertFalse("Method " + methodInfo.getName() + " is not allowed.",
                                                      checkBlackList(methodInfo.getName()));
                                      }
                                  }
                              });
    }

    @Test
    public void testMethodsOnJavaClass_Number() throws Exception {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder(new RawMVELEvaluator())
                .addClass(Number.class)
                .addFact("int").end()
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator(), "p0")
                .setProjectOracle(projectLoader)
                .build();

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller(packageLoader);
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl(service,
                                                                                       validatorInstance);

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName(packageLoader.getPackageName());
        dataModel.setModelFields(packageLoader.getProjectModelFields());
        dataModel.setMethodInformation(packageLoader.getProjectMethodInformation());
        PackageDataModelOracleTestUtils.populateDataModelOracle(mock(Path.class),
                                                                new HasImports() {

                                                                    private Imports imports = new Imports() {{
                                                                        addImport(new Import("java.lang.Number"));
                                                                    }};

                                                                    @Override
                                                                    public Imports getImports() {
                                                                        return imports;
                                                                    }

                                                                    @Override
                                                                    public void setImports(final Imports imports) {
                                                                        //Do nothing
                                                                    }
                                                                },
                                                                oracle,
                                                                dataModel);

        oracle.getMethodInfos(Number.class.getSimpleName(),
                              new Callback<List<MethodInfo>>() {
                                  @Override
                                  public void callback(final List<MethodInfo> methodInfos) {
                                      assertNotNull(methodInfos);
                                      assertFalse(methodInfos.isEmpty());
                                      for (final MethodInfo methodInfo : methodInfos) {
                                          assertFalse("Method " + methodInfo.getName() + " is not allowed.",
                                                      checkBlackList(methodInfo.getName()));
                                      }
                                  }
                              });
        oracle.getMethodInfos("int",
                              new Callback<List<MethodInfo>>() {
                                  @Override
                                  public void callback(final List<MethodInfo> methodInfos) {
                                      assertNotNull(methodInfos);
                                      assertTrue(methodInfos.isEmpty());
                                  }
                              });
    }

    private boolean checkBlackList(String methodName) {
        return ("hashCode".equals(methodName)
                || "equals".equals(methodName)
                || "addAll".equals(methodName)
                || "containsAll".equals(methodName)
                || "iterator".equals(methodName)
                || "removeAll".equals(methodName)
                || "retainAll".equals(methodName)
                || "toArray".equals(methodName)
                || "listIterator".equals(methodName)
                || "subList".equals(methodName)
                || "entrySet".equals(methodName)
                || "keySet".equals(methodName)
                || "putAll".equals(methodName));
    }
}
