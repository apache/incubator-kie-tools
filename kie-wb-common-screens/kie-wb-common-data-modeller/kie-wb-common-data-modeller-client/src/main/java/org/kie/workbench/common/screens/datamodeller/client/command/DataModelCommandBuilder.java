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

package org.kie.workbench.common.screens.datamodeller.client.command;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.JavaClass;
import org.kie.workbench.common.services.datamodeller.core.Method;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;

@ApplicationScoped
public class DataModelCommandBuilder {

    private DataModelChangeNotifier notifier;

    @Inject
    public DataModelCommandBuilder( DataModelChangeNotifier notifier ) {
        this.notifier = notifier;
    }

    public FieldAnnotationValueChangeCommand buildFieldAnnotationValueChangeCommand( final DataModelerContext context, final String source,
            final DataObject dataObject,
            final ObjectProperty field,
            final String annotationClassName, final String valuePair,
            final Object newValue, final boolean removeIfNull ) {

        return new FieldAnnotationValueChangeCommand( context, source, dataObject, field, annotationClassName, valuePair,
                newValue, removeIfNull, notifier );
    }

    public FieldAddAnnotationCommand buildFieldAnnotationAddCommand( final DataModelerContext context, final String source,
            final DataObject dataObject,
            final ObjectProperty field,
            final String annotationClassName, final List<ValuePair> valuePairs ) {

        return new FieldAddAnnotationCommand( context, source, dataObject, field,
                annotationClassName, valuePairs, notifier);
    }

    public FieldAddAnnotationCommand buildFieldAnnotationAddCommand( final DataModelerContext context, final String source,
            final DataObject dataObject,
            final ObjectProperty field,
            final Annotation annotation ) {
        return new FieldAddAnnotationCommand( context, source, dataObject, field, annotation, notifier );
    }

    public FieldAddAnnotationCommand buildFieldAnnotationAddCommand( final DataModelerContext context, final String source,
            final DataObject dataObject,
            final ObjectProperty field,
            final String annotationClassName ) {

        return new FieldAddAnnotationCommand( context, source, dataObject, field,
                annotationClassName, notifier);
    }

    public FieldRemoveAnnotationCommand buildFieldAnnotationRemoveCommand( final DataModelerContext context, final String source,
            final DataObject dataObject,
            final ObjectProperty field,
            final String annotationClassName ) {
        return new FieldRemoveAnnotationCommand( context, source, dataObject, field, annotationClassName, notifier );
    }

    public FieldAddOrRemoveAnnotationCommand buildFieldAddOrRemoveAnnotationCommand( final DataModelerContext context, final String source,
            final DataObject dataObject,
            final ObjectProperty field,
            final String annotationClassName,
            final boolean doAdd ) {
        return new FieldAddOrRemoveAnnotationCommand( context, source, dataObject, field, annotationClassName, doAdd, notifier );

    }

    public AddMethodCommand buildMethodAddCommand( final DataModelerContext context, final String source,
                                                                       final DataObject dataObject,
                                                                       final Method method) {

        return new AddMethodCommand( context, source, dataObject, method, notifier );
    }

    public RemoveMethodCommand buildMethodRemoveCommand( final DataModelerContext context, final String source,
                                                   final DataObject dataObject,
                                                   final Method method) {

        return new RemoveMethodCommand( context, source, dataObject, method, notifier );
    }

    public MethodAddAnnotationCommand buildMethodAnnotationAddCommand( final DataModelerContext context, final String source,
                                                                      final DataObject dataObject,
                                                                      final Method method,
                                                                      final String annotationClassName, final List<ValuePair> valuePairs ) {

        return new MethodAddAnnotationCommand( context, source, dataObject, method,
                annotationClassName, valuePairs, notifier );
    }

    public DataObjectAddAnnotationCommand buildDataObjectAddAnnotationCommand( final DataModelerContext context, final String source,
            final DataObject dataObject,
            final String annotationClassName, final List<ValuePair> valuePairs ) {

        return new DataObjectAddAnnotationCommand( context, source, dataObject, annotationClassName, valuePairs, notifier);
    }

    public DataObjectAddAnnotationCommand buildDataObjectAddAnnotationCommand( final DataModelerContext context, final String source,
            final DataObject dataObject,
            final Annotation annotation ) {
        return new DataObjectAddAnnotationCommand( context, source, dataObject, annotation, notifier );
    }

    public DataObjectAddAnnotationCommand buildDataObjectAddAnnotationCommand( final DataModelerContext context, final String source,
            final DataObject dataObject,
            final String annotationClassName ) {

        return new DataObjectAddAnnotationCommand( context, source, dataObject, annotationClassName, notifier);
    }

    public DataObjectAddOrRemoveAnnotationCommand buildDataObjectAddOrRemoveAnnotationCommand( final DataModelerContext context, final String source,
            final DataObject dataObject,
            final String annotationClassName,
            final boolean doAdd) {
        return new DataObjectAddOrRemoveAnnotationCommand( context, source, dataObject, annotationClassName, doAdd, notifier );
    }

    public DataObjectRemoveAnnotationCommand buildDataObjectRemoveAnnotationCommand( final DataModelerContext context, final String source,
            final DataObject dataObject,
            final String annotationClassName ) {
        return new DataObjectRemoveAnnotationCommand( context, source, dataObject, annotationClassName, notifier );
    }

    public DataObjectAnnotationValueChangeCommand buildDataObjectAnnotationValueChangeCommand( final DataModelerContext context, final String source,
            final DataObject dataObject,
            final String annotationClassName, final String valuePair,
            final Object newValue, final boolean removeIfNull ) {

        return new DataObjectAnnotationValueChangeCommand( context, source, dataObject, annotationClassName, valuePair, newValue, removeIfNull, notifier );
    }

    public FieldTypeChangeCommand buildChangeTypeCommand( final DataModelerContext context, final String source,
            final DataObject dataObject,
            final ObjectProperty field,
            final String newType,
            final boolean multiple ) {

        return new FieldTypeChangeCommand( context, source, dataObject, field, newType, multiple, notifier );
    }

    public AddPropertyCommand buildAddPropertyCommand( final DataModelerContext context, final String source,
            final DataObject dataObject,
            final String propertyName,
            final String propertyLabel,
            final String propertyType,
            final Boolean isMultiple ) {
        return new AddPropertyCommand( context, source, dataObject, propertyName, propertyLabel,
                propertyType, isMultiple, notifier );
    }

    public RemovePropertyCommand buildRemovePropertyCommand(final DataModelerContext context,
                                                            final String source,
                                                            final DataObject dataObject,
                                                            final String propertyName) {
        return new RemovePropertyCommand(context,
                                         source,
                                         dataObject,
                                         propertyName,
                                         notifier);
    }

    public DataObjectSuperClassChangeCommand buildDataObjectSuperClassChangeCommand( final DataModelerContext context,
            final String source,
            final DataObject dataObject,
            final String newSuperClass ) {
        return new DataObjectSuperClassChangeCommand( context, source, dataObject, newSuperClass, notifier );
    }

    public DataObjectPackageChangeCommand buildDataObjectPackageChangeCommand( final DataModelerContext context,
            final String source,
            final DataObject dataObject,
            final String newPackage ) {
        return new DataObjectPackageChangeCommand( context, source, dataObject, newPackage, notifier );
    }

    public DataObjectNameChangeCommand buildDataObjectNameChangeCommand( final DataModelerContext context,
            final String source,
            final DataObject dataObject,
            final String newName ) {
        return new DataObjectNameChangeCommand( context, source, dataObject, newName, notifier );
    }

    public DataObjectAddNestedClassCommand buildDataObjectAddNestedClassCommand( final DataModelerContext context,
            final String source,
            final DataObject dataObject,
            final JavaClass nestedClass) {
        return new DataObjectAddNestedClassCommand( context, source, dataObject, notifier, nestedClass );
    }

    public DataObjectRemoveNestedClassCommand buildDataObjectRemoveNestedClassCommand( final DataModelerContext context,
            final String source,
            final DataObject dataObject,
            final JavaClass nestedClass) {
        return new DataObjectRemoveNestedClassCommand( context, source, dataObject, notifier, nestedClass );
    }
}
