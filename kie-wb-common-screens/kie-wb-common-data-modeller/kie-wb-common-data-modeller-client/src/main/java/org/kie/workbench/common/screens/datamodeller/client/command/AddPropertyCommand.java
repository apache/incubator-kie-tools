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

import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.model.maindomain.MainDomainAnnotations;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldCreatedEvent;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.ObjectPropertyImpl;

public class AddPropertyCommand extends AbstractDataModelCommand {

    private String propertyName;

    private String propertyLabel;

    private String propertyType;

    private Boolean isMultiple;

    private ObjectProperty property;

    public AddPropertyCommand( final DataModelerContext context, final String source, final DataObject dataObject,
            final String propertyName, final String propertyLabel, final String propertyType,
            final Boolean isMultiple, final DataModelChangeNotifier notifier ) {
        super( context, source, dataObject, notifier );
        this.propertyName = propertyName;
        this.propertyLabel = propertyLabel;
        this.propertyType = propertyType;
        this.isMultiple = isMultiple;
    }

    @Override
    public void execute() {

        boolean multiple = isMultiple && !getContext().getHelper().isPrimitiveType( propertyType ); //extra check
        property = new ObjectPropertyImpl( propertyName,
                propertyType,
                multiple );

        if ( propertyLabel != null && !"".equals( propertyLabel ) ) {
            Annotation annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( MainDomainAnnotations.LABEL_ANNOTATION ) );
            annotation.setValue( MainDomainAnnotations.VALUE_PARAM, propertyLabel );
            property.addAnnotation( annotation );
        }

        dataObject.addProperty( property );

        if ( !property.isBaseType() ) {
                getContext().getHelper().dataObjectReferenced( property.getClassName(), dataObject.getClassName() );
        }

        notifyChange( new DataObjectFieldCreatedEvent( getContext().getContextId(), getSource(), getDataObject(), property ) );
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName( String propertyName ) {
        this.propertyName = propertyName;
    }

    public String getPropertyLabel() {
        return propertyLabel;
    }

    public void setPropertyLabel( String propertyLabel ) {
        this.propertyLabel = propertyLabel;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType( String propertyType ) {
        this.propertyType = propertyType;
    }

    public Boolean getIsMultiple() {
        return isMultiple;
    }

    public void setIsMultiple( Boolean isMultiple ) {
        this.isMultiple = isMultiple;
    }

    public ObjectProperty getProperty() {
        return property;
    }
}
