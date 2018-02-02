/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.widgets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.definition.type.Description;
import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Label;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;
import org.kie.api.definition.type.TypeSafe;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.model.EditorModelContent;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.DataModelImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.DataObjectImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.PropertyTypeFactoryImpl;
import org.kie.workbench.common.services.datamodeller.driver.impl.annotations.CommonAnnotations;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.uberfire.commons.data.Pair;

public class DataModelerEditorsTestHelper {

    public static final String NEW_NAME = "NewName";

    public static final String NEW_PACKAGE = "NewPackage";

    public static final String NEW_SUPERCLASS = "NewSuperClass";

    public static final String NEW_DESCRIPTION = "NewDescription";

    public static final String NEW_LABEL = "NewLabel";

    public static final String NEW_TYPE = "NewType";

    public static final String NEW_FIELD_NAME = "newFieldName";

    public static DataObject createTestObject1() {

        //set general properties and annotations.
        DataObject dataObject = new DataObjectImpl("org.test", "TestObject1");
        dataObject.setSuperClassName("java.lang.Object");
        dataObject.addAnnotation(createAnnotation(Label.class, new Pair<String, Object>("value", "TestObject1Label")));
        dataObject.addAnnotation(createAnnotation(Description.class, new Pair<String, Object>("value", "TestObject1Description")));

        //set annotations for drools & jbpm domain tests.
        dataObject.addAnnotation(createAnnotation(TypeSafe.class, new Pair<String, Object>("value", true)));
        dataObject.addAnnotation(createAnnotation(PropertyReactive.class));
        dataObject.addAnnotation(createAnnotation(Role.class, new Pair<String, Object>("value", Role.Type.EVENT.name())));
        dataObject.addAnnotation(createAnnotation(Timestamp.class, new Pair<String, Object>("value", "field2")));
        dataObject.addAnnotation(createAnnotation(Duration.class, new Pair<String, Object>("value", "field3")));
        dataObject.addAnnotation(createAnnotation(Expires.class, new Pair<String, Object>("value", "1h")));
        dataObject.addAnnotation(createAnnotation(XmlRootElement.class));

        //add fields
        ObjectProperty field1 = dataObject.addProperty("field1", Integer.class.getName());
        field1.addAnnotation(createAnnotation(Label.class, new Pair<String, Object>("value", "Field1Label")));
        field1.addAnnotation(createAnnotation(Description.class, new Pair<String, Object>("value", "Field1Description")));

        //set annotations for drools & jbpm domain tests
        field1.addAnnotation(createAnnotation(Position.class, new Pair<String, Object>("value", 0)));
        field1.addAnnotation(createAnnotation(Key.class));

        ObjectProperty field2 = dataObject.addProperty("field2", Integer.class.getName());
        field2.addAnnotation(createAnnotation(Label.class, new Pair<String, Object>("value", "Field2Label")));
        field2.addAnnotation(createAnnotation(Description.class, new Pair<String, Object>("value", "Field2Description")));

        ObjectProperty field3 = dataObject.addProperty("field3", Long.class.getName());
        field1.addAnnotation(createAnnotation(Label.class, new Pair<String, Object>("value", "Field3Label")));
        field1.addAnnotation(createAnnotation(Description.class, new Pair<String, Object>("value", "Field3Description")));

        return dataObject;
    }

    public static DataModel createTestModel(DataObject... dataObjects) {
        DataModel dataModel = new DataModelImpl();
        for (DataObject dataObject : dataObjects) {
            dataModel.addDataObject(dataObject);
        }
        return dataModel;
    }

    public static DataModelerContext createTestContext() {
        DataModelerContext context = new DataModelerContext("123456789");
        context.init(PropertyTypeFactoryImpl.getInstance().getBasePropertyTypes());

        List<AnnotationDefinition> commonAnnotations = CommonAnnotations.getCommonAnnotations();
        Map<String, AnnotationDefinition> annotationDefinitions = new HashMap<String, AnnotationDefinition>();
        for (AnnotationDefinition annotationDefinition : commonAnnotations) {
            annotationDefinitions.put(annotationDefinition.getClassName(), annotationDefinition);
        }
        context.setAnnotationDefinitions(annotationDefinitions);

        EditorModelContent content = new EditorModelContent();
        content.setDataModel(createTestModel());
        content.setCurrentModule(new KieModule());
        context.setEditorModelContent(content);

        return context;
    }

    public static Annotation createAnnotation(Class cls, String memberName, Object value) {

        AnnotationDefinition annotationDefinition = DriverUtils.buildAnnotationDefinition(cls);
        Annotation annotation = new AnnotationImpl(annotationDefinition);

        if (memberName != null) {
            annotation.setValue(memberName, value);
        }

        return annotation;
    }

    public static Annotation createAnnotation(Class cls, Pair<String, Object>... valuePairs) {

        AnnotationDefinition annotationDefinition = DriverUtils.buildAnnotationDefinition(cls);
        Annotation annotation = new AnnotationImpl(annotationDefinition);

        for (Pair<String, Object> valuePair : valuePairs) {
            annotation.setValue(valuePair.getK1(), valuePair.getK2());
        }
        return annotation;
    }
}
