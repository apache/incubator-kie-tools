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

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.events.ChangeType;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;

public abstract class AbstractDataModelCommand implements DataModelCommand {

    protected DataModelerContext context;

    protected String source;

    protected DataObject dataObject;

    protected String annotationClassName;

    protected String valuePair;

    protected Object newValue;

    protected boolean removeAnnotationIfValueIsNull;

    protected DataModelChangeNotifier notifier;

    protected List<ValuePair> valuePairs = new ArrayList<ValuePair>(  );

    public AbstractDataModelCommand( DataModelerContext context, String source, DataObject dataObject,
            String annotationClassName, String valuePair, Object newValue, boolean removeAnnotationIfValueIsNull,
            DataModelChangeNotifier notifier ) {
        this.context = context;
        this.source = source;
        this.dataObject = dataObject;
        this.annotationClassName = annotationClassName;
        this.valuePair = valuePair;
        this.newValue = newValue;
        this.removeAnnotationIfValueIsNull = removeAnnotationIfValueIsNull;
        this.notifier = notifier;
    }

    public AbstractDataModelCommand( DataModelerContext context, String source, DataObject dataObject,
            DataModelChangeNotifier notifier ) {
        this.context = context;
        this.source = source;
        this.dataObject = dataObject;
        this.notifier = notifier;
    }

    public AbstractDataModelCommand( DataModelerContext context, String source, DataObject dataObject,
            String annotationClassName,
            DataModelChangeNotifier notifier ) {
        this.context = context;
        this.source = source;
        this.dataObject = dataObject;
        this.annotationClassName = annotationClassName;
        this.notifier = notifier;
    }

    public DataModelerContext getContext() {
        return context;
    }

    public void setContext( DataModelerContext context ) {
        this.context = context;
    }

    public String getSource() {
        return source;
    }

    public void setSource( String source ) {
        this.source = source;
    }

    public DataObject getDataObject() {
        return dataObject;
    }

    public void setDataObject( DataObject dataObject ) {
        this.dataObject = dataObject;
    }

    public String getAnnotationClassName() {
        return annotationClassName;
    }

    public void setAnnotationClassName( String annotationClassName ) {
        this.annotationClassName = annotationClassName;
    }

    public String getValuePair() {
        return valuePair;
    }

    public void setValuePair( String valuePair ) {
        this.valuePair = valuePair;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue( Object newValue ) {
        this.newValue = newValue;
    }

    public boolean isRemoveAnnotationIfValueIsNull() {
        return removeAnnotationIfValueIsNull;
    }

    public void setRemoveAnnotationIfValueIsNull( boolean removeAnnotationIfValueIsNull ) {
        this.removeAnnotationIfValueIsNull = removeAnnotationIfValueIsNull;
    }

    public AbstractDataModelCommand withValuePair( ValuePair valuePair ) {
        if ( valuePairs == null ) {
            valuePairs = new ArrayList<ValuePair>(  );
        }
        valuePairs.add( valuePair );
        return this;
    }

    public AbstractDataModelCommand withValuePair( String name, Object value ) {
        return withValuePair( new ValuePair( name, value ) );
    }

    protected void notifyFieldChange( ChangeType changeType,
            DataModelerContext context,
            String source,
            DataObject dataObject,
            ObjectProperty field,
            String annotationClassName,
            String memberName,
            Object oldValue,
            Object newValue ) {

        if ( notifier != null ) {
            notifier.notifyFieldChange( changeType, context, source, dataObject, field, annotationClassName, memberName, oldValue, newValue );
        }
    }

    protected void notifyObjectChange( ChangeType changeType,
            DataModelerContext context,
            String source,
            DataObject dataObject,
            String annotationClassName,
            String memberName,
            Object oldValue,
            Object newValue ) {

        if ( notifier != null ) {
            notifier.notifyObjectChange( changeType, context, source, dataObject, annotationClassName, memberName, oldValue, newValue );
        }
    }

    protected void notifyChange( DataModelerEvent event ) {
        if ( notifier != null ) notifier.notifyChange( event );
    }

}
