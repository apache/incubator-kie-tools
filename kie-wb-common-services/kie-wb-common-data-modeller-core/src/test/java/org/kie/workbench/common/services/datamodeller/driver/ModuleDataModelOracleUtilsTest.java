/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodeller.driver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.soup.project.datamodel.oracle.TypeSource;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ModuleDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.JavaEnum;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.Visibility;
import org.kie.workbench.common.services.datamodeller.core.impl.DataModelImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.DataObjectImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.JavaEnumImpl;
import org.kie.workbench.common.services.datamodeller.driver.impl.ModuleDataModelOracleUtils;
import org.kie.workbench.common.services.datamodeller.driver.testclasses.ExternalEnum1;
import org.kie.workbench.common.services.datamodeller.driver.testclasses.ExternalEnum2;
import org.kie.workbench.common.services.datamodeller.driver.testclasses.ExternalPojo1;
import org.kie.workbench.common.services.datamodeller.driver.testclasses.ExternalPojo2;

import static org.junit.Assert.*;

public class ModuleDataModelOracleUtilsTest {

    private ModuleDataModelOracle dataModelOracle;

    private static final String TEST_PACKAGE = "org.kie.workbench.common.services.datamodeller.driver.testclasses";

    @Before
    public void init() {
        try {
            ModuleDataModelOracleUtilsTest.class.getClassLoader().loadClass(ExternalPojo1.class.getName());
            ModuleDataModelOracleBuilder dmoBuilder = ModuleDataModelOracleBuilder.newModuleOracleBuilder(new RawMVELEvaluator());

            dmoBuilder.addPackage(TEST_PACKAGE);
            dmoBuilder.addClass(ExternalPojo1.class, false, type -> TypeSource.JAVA_DEPENDENCY);
            dmoBuilder.addClass(ExternalPojo2.class, false, type -> TypeSource.JAVA_DEPENDENCY);
            dmoBuilder.addClass(ExternalEnum1.class, false, type -> TypeSource.JAVA_DEPENDENCY);
            dmoBuilder.addClass(ExternalEnum2.class, false, type -> TypeSource.JAVA_DEPENDENCY);

            dataModelOracle = dmoBuilder.build();
        } catch (Exception e) {
            fail("Model loading failed: " + e.getMessage());
        }
    }

    @Test
    public void loadExternalDepsTest() {
        DataModel dataModel = new DataModelImpl();
        try {
            ModuleDataModelOracleUtils.loadExternalDependencies(dataModel,
                                                                dataModelOracle, ModuleDataModelOracleUtilsTest.class.getClassLoader());

            //check that the expected classes were properly loaded.
            List<DataObject> expectedExternalClasses = createExpectedExternalClasses();
            assertEquals(expectedExternalClasses.size(), dataModel.getExternalClasses().size());

            for (DataObject externalClass : dataModel.getExternalClasses()) {
                //properties read from DMO are not necessary sorted.
                sortProperties(externalClass);
                assertTrue(expectedExternalClasses.contains(externalClass));
            }

            //check that the expected enums where properly loaded.
            List<JavaEnum> expectedExternalEnums = createExpectedExternalEnums();
            assertEquals(expectedExternalEnums.size(), dataModel.getDependencyJavaEnums().size());
            for (JavaEnum externalEnum : dataModel.getDependencyJavaEnums()) {
                assertTrue(expectedExternalEnums.contains(externalEnum));
            }
        } catch (Exception e) {
            fail("External deps loading test failed: " + e.getMessage());
        }
    }

    private List<DataObject> createExpectedExternalClasses() {
        List<DataObject> result = new ArrayList<>();

        DataObject dataObject1 = new DataObjectImpl(TEST_PACKAGE, "ExternalPojo1");
        dataObject1.setSuperClassName(Object.class.getName());
        dataObject1.addProperty("field1", String.class.getName());
        dataObject1.addProperty("field2", String.class.getName());
        result.add(dataObject1);

        DataObject dataObject2 = new DataObjectImpl(TEST_PACKAGE, "ExternalPojo2");
        dataObject2.setSuperClassName(Object.class.getName());
        dataObject2.addProperty("field3", String.class.getName());
        dataObject2.addProperty("field4", String.class.getName());
        result.add(dataObject2);

        return result;
    }

    private List<JavaEnum> createExpectedExternalEnums() {
        List<JavaEnum> result = new ArrayList<>();

        result.add(new JavaEnumImpl(TEST_PACKAGE, "ExternalEnum1", Visibility.PUBLIC));
        result.add(new JavaEnumImpl(TEST_PACKAGE, "ExternalEnum2", Visibility.PUBLIC));

        return result;
    }

    private void sortProperties(DataObject dataObject) {
        Collections.sort(dataObject.getProperties(), new Comparator<ObjectProperty>() {
            @Override
            public int compare(ObjectProperty o1, ObjectProperty o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
}