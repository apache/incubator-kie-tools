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

import javax.enterprise.inject.Instance;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.validation.client.dynamic.DynamicValidator;
import org.junit.Test;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.soup.project.datamodel.oracle.ProjectDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.kie.workbench.common.widgets.client.datamodel.testclasses.Person;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PackageDataModelGettersAndSettersTest {

    @Mock
    private Instance<DynamicValidator> validatorInstance;

    @Test
    public void testGettersAndSettersOnDeclaredModel() throws Exception {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder(new RawMVELEvaluator())
                .addFact("Person")
                .addField(new ModelField("age",
                                         Integer.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_NUMERIC_INTEGER))
                .addField(new ModelField("sex",
                                         String.class.getName(),
                                         ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                         ModelField.FIELD_ORIGIN.DECLARED,
                                         FieldAccessorsAndMutators.BOTH,
                                         DataType.TYPE_STRING))
                .end()
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator())
                .setProjectOracle(projectLoader)
                .build();

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller(packageLoader);
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl(service,
                                                                                       validatorInstance);

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName(packageLoader.getPackageName());
        dataModel.setModelFields(packageLoader.getProjectModelFields());
        PackageDataModelOracleTestUtils.populateDataModelOracle(mock(Path.class),
                                                                new MockHasImports(),
                                                                oracle,
                                                                dataModel);

        oracle.getFieldCompletions("Person",
                                   FieldAccessorsAndMutators.ACCESSOR,
                                   new Callback<ModelField[]>() {
                                       @Override
                                       public void callback(final ModelField[] getters) {
                                           assertEquals(3,
                                                        getters.length);
                                           assertEquals("age",
                                                        getters[0].getName());
                                           assertEquals("sex",
                                                        getters[1].getName());
                                           assertEquals(DataType.TYPE_THIS,
                                                        getters[2].getName());
                                       }
                                   });

        oracle.getFieldCompletions("Person",
                                   FieldAccessorsAndMutators.MUTATOR,
                                   new Callback<ModelField[]>() {
                                       @Override
                                       public void callback(final ModelField[] setters) {
                                           assertEquals(2,
                                                        setters.length);
                                           assertEquals("age",
                                                        setters[0].getName());
                                           assertEquals("sex",
                                                        setters[1].getName());
                                       }
                                   });
    }

    @Test
    public void testGettersAndSettersOnJavaClass() throws Exception {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder(new RawMVELEvaluator())
                .addClass(Person.class)
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator(),
                                                                                                           "org.kie.workbench.common.widgets.client.datamodel")
                .setProjectOracle(projectLoader)
                .build();

        //Emulate server-to-client conversions
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller(packageLoader);
        final AsyncPackageDataModelOracle oracle = new AsyncPackageDataModelOracleImpl(service,
                                                                                       validatorInstance);

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName(packageLoader.getPackageName());
        dataModel.setModelFields(packageLoader.getProjectModelFields());
        PackageDataModelOracleTestUtils.populateDataModelOracle(mock(Path.class),
                                                                new MockHasImports(),
                                                                oracle,
                                                                dataModel);

        oracle.getFieldCompletions("Person",
                                   FieldAccessorsAndMutators.ACCESSOR,
                                   new Callback<ModelField[]>() {
                                       @Override
                                       public void callback(final ModelField[] getters) {
                                           assertEquals(2,
                                                        getters.length);
                                           assertEquals("age",
                                                        getters[0].getName());
                                           assertEquals(DataType.TYPE_THIS,
                                                        getters[1].getName());
                                       }
                                   });

        oracle.getFieldCompletions("Person",
                                   FieldAccessorsAndMutators.MUTATOR,
                                   new Callback<ModelField[]>() {
                                       @Override
                                       public void callback(final ModelField[] setters) {
                                           assertEquals(1,
                                                        setters.length);
                                           assertEquals("age",
                                                        setters[0].getName());
                                       }
                                   });
    }
}
