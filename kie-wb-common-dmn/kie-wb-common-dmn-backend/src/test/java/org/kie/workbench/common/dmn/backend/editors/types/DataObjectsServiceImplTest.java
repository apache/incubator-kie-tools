/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.backend.editors.types;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.commons.util.Maps;
import org.kie.soup.project.datamodel.commons.oracle.ModuleDataModelOracleImpl;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.workbench.common.dmn.api.editors.types.DataObject;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.backend.editors.types.classes.APerson;
import org.kie.workbench.common.dmn.backend.editors.types.classes.BPet;
import org.kie.workbench.common.dmn.backend.editors.types.classes.CFamily;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataObjectsServiceImplTest {

    @Mock
    private DataModelService dataModelService;

    @Mock
    private ModuleClassLoaderHelper moduleClassLoaderHelper;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private WorkspaceProject workspaceProject;

    @Mock
    private Path projectRootPath;

    @Mock
    private KieModule kieModule;

    private ModuleDataModelOracle dataModelOracle;

    private DataObjectsServiceImpl service;

    @Before
    public void setup() {
        service = new DataObjectsServiceImpl(dataModelService,
                                             moduleClassLoaderHelper,
                                             moduleService);
        dataModelOracle = new ModuleDataModelOracleImpl();
        dataModelOracle.addModulePackageNames(Collections.singletonList(APerson.class.getPackage().getName()));

        when(workspaceProject.getRootPath()).thenReturn(projectRootPath);
        when(dataModelService.getModuleDataModel(projectRootPath)).thenReturn(dataModelOracle);
        when(moduleService.resolveModule(projectRootPath)).thenReturn(kieModule);
        when(moduleClassLoaderHelper.getModuleClassLoader(kieModule)).thenReturn(Thread.currentThread().getContextClassLoader());
    }

    @Test
    public void testLoadDataObjects_NoProperties() {
        final Maps.Builder<String, ModelField[]> modelFieldsBuilder = new Maps.Builder<>();
        modelFieldsBuilder.put(BPet.class.getName(),
                               new ModelField[]{
                                       newModelField(DataType.TYPE_THIS,
                                                     BPet.class.getName(),
                                                     BPet.class.getSimpleName())
                               });

        final Map<String, ModelField[]> modelFields = modelFieldsBuilder.build();
        dataModelOracle.addModuleModelFields(modelFields);

        final List<DataObject> dataObjects = service.loadDataObjects(workspaceProject);

        assertThat(dataObjects).isNotEmpty();
        assertThat(dataObjects).hasSize(1);

        assertThat(dataObjects.get(0).getClassType()).isEqualTo(BPet.class.getName());
        assertThat(dataObjects.get(0).getProperties()).isEmpty();
    }

    @Test
    public void testLoadDataObjects_ResolvedJavaPrimitiveProperties() {
        final Maps.Builder<String, ModelField[]> modelFieldsBuilder = new Maps.Builder<>();
        modelFieldsBuilder.put(APerson.class.getName(),
                               new ModelField[]{
                                       newModelField(DataType.TYPE_THIS,
                                                     APerson.class.getName(),
                                                     APerson.class.getSimpleName()),
                                       newModelField("booleanField",
                                                     "boolean",
                                                     DataType.TYPE_BOOLEAN),
                                       newModelField("byteField",
                                                     "byte",
                                                     DataType.TYPE_NUMERIC_BYTE),
                                       newModelField("charField",
                                                     "char",
                                                     DataType.TYPE_STRING),
                                       newModelField("floatField",
                                                     "float",
                                                     DataType.TYPE_NUMERIC_FLOAT),
                                       newModelField("intField",
                                                     "int",
                                                     DataType.TYPE_NUMERIC_INTEGER),
                                       newModelField("longField",
                                                     "long",
                                                     DataType.TYPE_NUMERIC_LONG),
                                       newModelField("shortField",
                                                     "short",
                                                     DataType.TYPE_NUMERIC_SHORT),
                                       newModelField("doubleField",
                                                     "double",
                                                     DataType.TYPE_NUMERIC_DOUBLE)
                               });

        final Map<String, ModelField[]> modelFields = modelFieldsBuilder.build();
        dataModelOracle.addModuleModelFields(modelFields);

        final List<DataObject> dataObjects = service.loadDataObjects(workspaceProject);

        assertThat(dataObjects).isNotEmpty();
        assertThat(dataObjects).hasSize(1);

        assertThat(dataObjects.get(0).getClassType()).isEqualTo(APerson.class.getName());
        assertThat(dataObjects.get(0).getProperties()).hasSize(8);
        assertThat(dataObjects.get(0).getProperties().get(0).getProperty()).isEqualTo("booleanField");
        assertThat(dataObjects.get(0).getProperties().get(0).getType()).isEqualTo(BuiltInType.BOOLEAN.getName());
        assertThat(dataObjects.get(0).getProperties().get(1).getProperty()).isEqualTo("byteField");
        assertThat(dataObjects.get(0).getProperties().get(1).getType()).isEqualTo(BuiltInType.NUMBER.getName());
        assertThat(dataObjects.get(0).getProperties().get(2).getProperty()).isEqualTo("charField");
        assertThat(dataObjects.get(0).getProperties().get(2).getType()).isEqualTo(BuiltInType.STRING.getName());
        assertThat(dataObjects.get(0).getProperties().get(3).getProperty()).isEqualTo("floatField");
        assertThat(dataObjects.get(0).getProperties().get(3).getType()).isEqualTo(BuiltInType.NUMBER.getName());
        assertThat(dataObjects.get(0).getProperties().get(4).getProperty()).isEqualTo("intField");
        assertThat(dataObjects.get(0).getProperties().get(4).getType()).isEqualTo(BuiltInType.NUMBER.getName());
        assertThat(dataObjects.get(0).getProperties().get(5).getProperty()).isEqualTo("longField");
        assertThat(dataObjects.get(0).getProperties().get(5).getType()).isEqualTo(BuiltInType.NUMBER.getName());
        assertThat(dataObjects.get(0).getProperties().get(6).getProperty()).isEqualTo("shortField");
        assertThat(dataObjects.get(0).getProperties().get(6).getType()).isEqualTo(BuiltInType.NUMBER.getName());
        assertThat(dataObjects.get(0).getProperties().get(7).getProperty()).isEqualTo("doubleField");
        assertThat(dataObjects.get(0).getProperties().get(7).getType()).isEqualTo(BuiltInType.NUMBER.getName());
    }

    @Test
    public void testLoadDataObjects_ResolvedJavaBoxedProperties() {
        final Maps.Builder<String, ModelField[]> modelFieldsBuilder = new Maps.Builder<>();
        modelFieldsBuilder.put(APerson.class.getName(),
                               new ModelField[]{
                                       newModelField(DataType.TYPE_THIS,
                                                     APerson.class.getName(),
                                                     APerson.class.getSimpleName()),
                                       newModelField("booleanField",
                                                     Boolean.class.getName(),
                                                     DataType.TYPE_BOOLEAN),
                                       newModelField("byteField",
                                                     Byte.class.getName(),
                                                     DataType.TYPE_NUMERIC_BYTE),
                                       newModelField("charField",
                                                     Character.class.getName(),
                                                     DataType.TYPE_STRING),
                                       newModelField("floatField",
                                                     Float.class.getName(),
                                                     DataType.TYPE_NUMERIC_FLOAT),
                                       newModelField("intField",
                                                     Integer.class.getName(),
                                                     DataType.TYPE_NUMERIC_INTEGER),
                                       newModelField("longField",
                                                     Long.class.getName(),
                                                     DataType.TYPE_NUMERIC_LONG),
                                       newModelField("shortField",
                                                     Short.class.getName(),
                                                     DataType.TYPE_NUMERIC_SHORT),
                                       newModelField("doubleField",
                                                     Double.class.getName(),
                                                     DataType.TYPE_NUMERIC_DOUBLE)
                               });

        final Map<String, ModelField[]> modelFields = modelFieldsBuilder.build();
        dataModelOracle.addModuleModelFields(modelFields);

        final List<DataObject> dataObjects = service.loadDataObjects(workspaceProject);

        assertThat(dataObjects).isNotEmpty();
        assertThat(dataObjects).hasSize(1);

        assertThat(dataObjects.get(0).getClassType()).isEqualTo(APerson.class.getName());
        assertThat(dataObjects.get(0).getProperties()).hasSize(8);
        assertThat(dataObjects.get(0).getProperties().get(0).getProperty()).isEqualTo("booleanField");
        assertThat(dataObjects.get(0).getProperties().get(0).getType()).isEqualTo(BuiltInType.BOOLEAN.getName());
        assertThat(dataObjects.get(0).getProperties().get(1).getProperty()).isEqualTo("byteField");
        assertThat(dataObjects.get(0).getProperties().get(1).getType()).isEqualTo(BuiltInType.NUMBER.getName());
        assertThat(dataObjects.get(0).getProperties().get(2).getProperty()).isEqualTo("charField");
        assertThat(dataObjects.get(0).getProperties().get(2).getType()).isEqualTo(BuiltInType.STRING.getName());
        assertThat(dataObjects.get(0).getProperties().get(3).getProperty()).isEqualTo("floatField");
        assertThat(dataObjects.get(0).getProperties().get(3).getType()).isEqualTo(BuiltInType.NUMBER.getName());
        assertThat(dataObjects.get(0).getProperties().get(4).getProperty()).isEqualTo("intField");
        assertThat(dataObjects.get(0).getProperties().get(4).getType()).isEqualTo(BuiltInType.NUMBER.getName());
        assertThat(dataObjects.get(0).getProperties().get(5).getProperty()).isEqualTo("longField");
        assertThat(dataObjects.get(0).getProperties().get(5).getType()).isEqualTo(BuiltInType.NUMBER.getName());
        assertThat(dataObjects.get(0).getProperties().get(6).getProperty()).isEqualTo("shortField");
        assertThat(dataObjects.get(0).getProperties().get(6).getType()).isEqualTo(BuiltInType.NUMBER.getName());
        assertThat(dataObjects.get(0).getProperties().get(7).getProperty()).isEqualTo("doubleField");
        assertThat(dataObjects.get(0).getProperties().get(7).getType()).isEqualTo(BuiltInType.NUMBER.getName());
    }

    @Test
    public void testLoadDataObjects_ResolvedBuiltInTypesProperties() {
        final Maps.Builder<String, ModelField[]> modelFieldsBuilder = new Maps.Builder<>();
        modelFieldsBuilder.put(APerson.class.getName(),
                               new ModelField[]{
                                       newModelField(DataType.TYPE_THIS,
                                                     APerson.class.getName(),
                                                     APerson.class.getSimpleName()),
                                       newModelField("stringField",
                                                     String.class.getName(),
                                                     DataType.TYPE_STRING),
                                       newModelField("characterField",
                                                     Character.class.getName(),
                                                     DataType.TYPE_STRING),
                                       newModelField("localDateField",
                                                     LocalDate.class.getName(),
                                                     DataType.TYPE_LOCAL_DATE),
                                       newModelField("localTimeField",
                                                     LocalTime.class.getName(),
                                                     DataType.TYPE_LOCAL_DATE),
                                       newModelField("offsetTimeField",
                                                     OffsetTime.class.getName(),
                                                     DataType.TYPE_DATE),
                                       newModelField("zonedDateTimeField",
                                                     ZonedDateTime.class.getName(),
                                                     DataType.TYPE_DATE),
                                       newModelField("offsetDateTimeField",
                                                     OffsetDateTime.class.getName(),
                                                     DataType.TYPE_DATE),
                                       newModelField("localDateTimeField",
                                                     LocalDateTime.class.getName(),
                                                     DataType.TYPE_LOCAL_DATE),
                                       newModelField("mapField",
                                                     Map.class.getName(),
                                                     DataType.TYPE_OBJECT),
                                       newModelField("temporalAccessorField",
                                                     ChronoLocalDate.class.getName(),
                                                     DataType.TYPE_OBJECT)
                               });

        final Map<String, ModelField[]> modelFields = modelFieldsBuilder.build();
        dataModelOracle.addModuleModelFields(modelFields);

        final List<DataObject> dataObjects = service.loadDataObjects(workspaceProject);

        assertThat(dataObjects).isNotEmpty();
        assertThat(dataObjects).hasSize(1);

        assertThat(dataObjects.get(0).getClassType()).isEqualTo(APerson.class.getName());
        assertThat(dataObjects.get(0).getProperties()).hasSize(10);
        assertThat(dataObjects.get(0).getProperties().get(0).getProperty()).isEqualTo("stringField");
        assertThat(dataObjects.get(0).getProperties().get(0).getType()).isEqualTo(BuiltInType.STRING.getName());
        assertThat(dataObjects.get(0).getProperties().get(1).getProperty()).isEqualTo("characterField");
        assertThat(dataObjects.get(0).getProperties().get(1).getType()).isEqualTo(BuiltInType.STRING.getName());
        assertThat(dataObjects.get(0).getProperties().get(2).getProperty()).isEqualTo("localDateField");
        assertThat(dataObjects.get(0).getProperties().get(2).getType()).isEqualTo(BuiltInType.DATE.getName());
        assertThat(dataObjects.get(0).getProperties().get(3).getProperty()).isEqualTo("localTimeField");
        assertThat(dataObjects.get(0).getProperties().get(3).getType()).isEqualTo(BuiltInType.TIME.getName());
        assertThat(dataObjects.get(0).getProperties().get(4).getProperty()).isEqualTo("offsetTimeField");
        assertThat(dataObjects.get(0).getProperties().get(4).getType()).isEqualTo(BuiltInType.TIME.getName());
        assertThat(dataObjects.get(0).getProperties().get(5).getProperty()).isEqualTo("zonedDateTimeField");
        assertThat(dataObjects.get(0).getProperties().get(5).getType()).isEqualTo(BuiltInType.DATE_TIME.getName());
        assertThat(dataObjects.get(0).getProperties().get(6).getProperty()).isEqualTo("offsetDateTimeField");
        assertThat(dataObjects.get(0).getProperties().get(6).getType()).isEqualTo(BuiltInType.DATE_TIME.getName());
        assertThat(dataObjects.get(0).getProperties().get(7).getProperty()).isEqualTo("localDateTimeField");
        assertThat(dataObjects.get(0).getProperties().get(7).getType()).isEqualTo(BuiltInType.DATE_TIME.getName());
        assertThat(dataObjects.get(0).getProperties().get(8).getProperty()).isEqualTo("mapField");
        assertThat(dataObjects.get(0).getProperties().get(8).getType()).isEqualTo(BuiltInType.CONTEXT.getName());
        assertThat(dataObjects.get(0).getProperties().get(9).getProperty()).isEqualTo("temporalAccessorField");
        assertThat(dataObjects.get(0).getProperties().get(9).getType()).isEqualTo(BuiltInType.ANY.getName());
    }

    @Test
    public void testLoadDataObjects_ResolvedCustomProperty() {
        final Maps.Builder<String, ModelField[]> modelFieldsBuilder = new Maps.Builder<>();
        modelFieldsBuilder.put(APerson.class.getName(),
                               new ModelField[]{
                                       newModelField(DataType.TYPE_THIS,
                                                     APerson.class.getName(),
                                                     APerson.class.getSimpleName())
                               });
        modelFieldsBuilder.put(CFamily.class.getName(),
                               new ModelField[]{
                                       newModelField(DataType.TYPE_THIS,
                                                     CFamily.class.getName(),
                                                     CFamily.class.getSimpleName()),
                                       newModelField("mother",
                                                     APerson.class.getName(),
                                                     APerson.class.getSimpleName())
                               });

        final Map<String, ModelField[]> modelFields = modelFieldsBuilder.build();
        dataModelOracle.addModuleModelFields(modelFields);

        final List<DataObject> dataObjects = service.loadDataObjects(workspaceProject);

        assertThat(dataObjects).isNotEmpty();
        assertThat(dataObjects).hasSize(2);

        assertThat(dataObjects.get(0).getClassType()).isEqualTo(APerson.class.getName());
        assertThat(dataObjects.get(0).getProperties()).isEmpty();

        assertThat(dataObjects.get(1).getClassType()).isEqualTo(CFamily.class.getName());
        assertThat(dataObjects.get(1).getProperties()).hasSize(1);
        assertThat(dataObjects.get(1).getProperties().get(0).getProperty()).isEqualTo("mother");
        assertThat(dataObjects.get(1).getProperties().get(0).getType()).isEqualTo(APerson.class.getName());
    }

    @Test
    public void testLoadDataObjects_UnknownCustomProperty() {
        final Maps.Builder<String, ModelField[]> modelFieldsBuilder = new Maps.Builder<>();
        modelFieldsBuilder.put(CFamily.class.getName(),
                               new ModelField[]{
                                       newModelField(DataType.TYPE_THIS,
                                                     CFamily.class.getName(),
                                                     CFamily.class.getSimpleName()),
                                       newModelField("mother",
                                                     APerson.class.getName(),
                                                     APerson.class.getSimpleName())
                               });

        final Map<String, ModelField[]> modelFields = modelFieldsBuilder.build();
        dataModelOracle.addModuleModelFields(modelFields);

        final List<DataObject> dataObjects = service.loadDataObjects(workspaceProject);

        assertThat(dataObjects).isNotEmpty();
        assertThat(dataObjects).hasSize(1);

        assertThat(dataObjects.get(0).getClassType()).isEqualTo(CFamily.class.getName());
        assertThat(dataObjects.get(0).getProperties()).hasSize(1);
        assertThat(dataObjects.get(0).getProperties().get(0).getProperty()).isEqualTo("mother");
        assertThat(dataObjects.get(0).getProperties().get(0).getType()).isEqualTo(BuiltInType.ANY.getName());
    }

    @Test
    public void testLoadDataObjects_NoJavaFilesAvailable() {
        final List<DataObject> dataObjects = service.loadDataObjects(workspaceProject);

        assertThat(dataObjects).isEmpty();
    }

    private ModelField newModelField(final String name,
                                     final String className,
                                     final String type) {
        return new ModelField(name,
                              className,
                              ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                              ModelField.FIELD_ORIGIN.SELF,
                              FieldAccessorsAndMutators.BOTH,
                              type);
    }
}