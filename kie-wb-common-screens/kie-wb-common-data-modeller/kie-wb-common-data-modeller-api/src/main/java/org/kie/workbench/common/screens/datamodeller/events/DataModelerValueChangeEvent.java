/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.events;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.services.datamodeller.core.DataObject;

@Portable
public class DataModelerValueChangeEvent extends DataModelerEvent {

    public static String ANNOTATION_CLASS_NAME = "ANNOTATION_CLASS_NAME";

    private String valueName;

    private Object oldValue;

    private Object newValue;

    private ChangeType changeType;

    private Map<String, Object> changeParams = new HashMap<String, Object>(  );

    public DataModelerValueChangeEvent() {
    }

    public DataModelerValueChangeEvent( ChangeType changeType,
            String contextId,
            String source,
            DataObject currentDataObject,
            String valueName,
            Object oldValue,
            Object newValue ) {

        super(contextId, source, currentDataObject);
        this.changeType = changeType;
        this.valueName = valueName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public DataModelerValueChangeEvent withValueName( String valueName ) {
        setValueName( valueName );
        return this;
    }

    public DataModelerValueChangeEvent withParam( String paramName, Object paramValue ) {
        addParam( paramName, paramValue );
        return this;
    }

    public DataModelerValueChangeEvent withAnnotationClassName( String annotationClassName ) {
        addParam( ANNOTATION_CLASS_NAME, annotationClassName );
        return this;
    }

    public String getAnnotationClassName( ) {
        return (String) getParam( ANNOTATION_CLASS_NAME );
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName( String valueName ) {
        this.valueName = valueName;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }

    public DataModelerValueChangeEvent withOldValue( Object oldValue ) {
        setOldValue( oldValue );
        return this;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    public DataModelerValueChangeEvent withNewValue( Object newValue ) {
        setNewValue( newValue );
        return this;
    }

    public void setChangeType( ChangeType changeType ) {
        this.changeType = changeType;
    }

    public DataModelerValueChangeEvent withChangeType( ChangeType changeType ) {
        setChangeType( changeType );
        return this;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public Object getParam( String name ) {
        return changeParams.get( name );
    }

    public void addParam( String name, Object value ) {
        changeParams.put( name, value );
    }
}
