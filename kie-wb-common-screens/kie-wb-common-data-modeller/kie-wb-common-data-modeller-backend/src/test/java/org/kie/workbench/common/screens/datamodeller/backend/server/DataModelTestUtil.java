/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.datamodeller.backend.server;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.HasAnnotations;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.Visibility;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.DataModelImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.DataObjectImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.ObjectPropertyImpl;

/**
 * Also used in org.jbpm.formModeler.panels.modeler.backend.indexing.IndexFormsTest
 */
public class DataModelTestUtil {

    private final Map<String, AnnotationDefinition> systemAnnotations;

    public DataModelTestUtil(Map<String, AnnotationDefinition> systemAnnos ) {
        this.systemAnnotations = systemAnnos;
    }

    public DataModel createModel(Class... classes) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        DataModel dataModel = new DataModelImpl();

        for( Class clazz : classes ) {
            DataObject dataObject = createDataObject(clazz);
            dataModel.addDataObject( dataObject );
        }

        return dataModel;
    }

    public DataObject createDataObject(Class clazz) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class superClass = clazz.getSuperclass();
        String superClassName = null;
        if( superClass != null && ! superClass.equals(Object.class)) {
            superClassName = superClass.getCanonicalName();
        }
        DataObject dataObj = createDataObject( clazz.getPackage().getName(),  clazz.getSimpleName(), superClassName);
        addAnnotations(dataObj, clazz.getAnnotations());

        for( Field field : clazz.getDeclaredFields() ) {
            String fieldName = field.getName();
            String fieldType = field.getType().getCanonicalName();
            ObjectProperty fieldProp = addProperty( dataObj, fieldName, fieldType, true, false, null );

            addAnnotations(fieldProp, field.getAnnotations());
        }

        return dataObj;
    }

    private static final String VALUE_METHOD_NAME = "value";

    private void addAnnotations( HasAnnotations hasAnnos, java.lang.annotation.Annotation [] annos ) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for( java.lang.annotation.Annotation anno : annos ) {
            String annoClassName = anno.annotationType().getCanonicalName();
            String valueFieldName = null;
            Object value = getAnnotationValue(anno);
            if( value != null ) {
                valueFieldName = VALUE_METHOD_NAME;
            }
            Annotation annotation = createAnnotation( systemAnnotations, annoClassName, valueFieldName, value);
            hasAnnos.addAnnotation( annotation );
        }
    }

    private static final Class<?> [] NO_CLASS_PARAMETERS = new Class[0];
    private static final Object [] NO_PARAMETERS = new Object[0];

    private Object getAnnotationValue(java.lang.annotation.Annotation anno) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class annoClass = anno.annotationType();

        Object valueObj = null;

        try {
            Method valueMethod = annoClass.getDeclaredMethod(VALUE_METHOD_NAME, NO_CLASS_PARAMETERS );
           // value should be accessible -- if not, there's something wrong with the anno, not with us!
           valueObj = valueMethod.invoke(anno, NO_PARAMETERS);
        } catch (NoSuchMethodException nsme) {
            // no-op -- no value in anno
        }

        // In this test, the DefaultJavaRoasterModelAnnotationDriver is being used
        // (instead of the DefaultJavaModelAnnotationDriver)
        // which means that annotation values are stored as strings
        if( valueObj != null ) {
            if( ! ClassUtils.isPrimitiveOrWrapper(valueObj.getClass()) && ! (valueObj instanceof String) ) {
                valueObj = valueObj.toString();
            }
        }
        return valueObj;
    }

    public DataObject createDataObject( String packageName, String name, String superClassName ) {
        DataObject dataObject = new DataObjectImpl( packageName, name );
        dataObject.setSuperClassName( superClassName );
        return dataObject;
    }

    public ObjectProperty addProperty( DataObject dataObject, String name, String className, boolean baseType, boolean multiple, String bag ) {
        // TODO set modifiers.
        ObjectProperty property = new ObjectPropertyImpl( name, className, multiple,bag, Visibility.PUBLIC, false, false );
        dataObject.addProperty( property );
        return property;
    }

    public Annotation createAnnotation( Map<String, AnnotationDefinition> systemAnnotations, String className, String memberName, Object value ) {
        AnnotationDefinition annotationDefinition = systemAnnotations.get( className );

        Annotation annotation = new AnnotationImpl( annotationDefinition );
        if ( memberName != null ) {
            annotation.setValue( memberName, value );
        }

        return annotation;
    }
}
