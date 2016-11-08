/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.forms.model;

import org.kie.workbench.common.forms.metaModel.FieldDef;
import org.kie.workbench.common.forms.model.util.IDGenerator;
import org.kie.workbench.common.forms.service.FieldManager;

public abstract class FieldDefinition {

    public static final String ID_PREFFIX = "field" + FieldManager.FIELD_NAME_SEPARATOR;

    protected boolean annotatedId;

    protected String code;

    private String id;

    protected String name;

    @FieldDef( label = "Label", position = 0)
    protected String label;

    @FieldDef( label = "Required", position = 99)
    protected Boolean required = Boolean.FALSE;

    @FieldDef( label = "Readonly", position = 100)
    protected Boolean readonly = Boolean.FALSE;

    @FieldDef( label = "Validate on Value Change", position = 101)
    protected Boolean validateOnChange = Boolean.TRUE;

    protected String binding;

    protected String standaloneClassName;

    protected FieldDefinition( String code ) {
        id = ID_PREFFIX + IDGenerator.generateRandomId();
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel( String label ) {
        this.label = label;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired( Boolean required ) {
        this.required = required;
    }

    public Boolean getReadonly() {
        return readonly;
    }

    public void setReadonly( Boolean readonly ) {
        this.readonly = readonly;
    }

    public boolean isAnnotatedId() {
        return annotatedId;
    }

    public void setAnnotatedId( boolean annotatedId ) {
        this.annotatedId = annotatedId;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding( String binding ) {
        this.binding = binding;
    }

    public String getStandaloneClassName() {
        return standaloneClassName;
    }

    public void setStandaloneClassName(String standaloneClassName) {
        this.standaloneClassName = standaloneClassName;
    }

    public FieldTypeInfo getFieldTypeInfo() {
        return new DefaultFieldTypeInfo( standaloneClassName );
    }

    public Boolean getValidateOnChange() {
        return validateOnChange;
    }

    public void setValidateOnChange( Boolean validateOnChange ) {
        this.validateOnChange = validateOnChange;
    }

    public void copyFrom( FieldDefinition other ) {
        if ( other == null ) return;
        setLabel(other.getLabel());

        setAnnotatedId( other.isAnnotatedId() );
        if ( !other.isAnnotatedId() ) setReadonly( other.getReadonly() );

        setStandaloneClassName( other.getStandaloneClassName());
        setBinding( other.getBinding() );

        setRequired( other.getRequired() );
        setReadonly( other.getReadonly() );
        setValidateOnChange( other.getValidateOnChange() );

        doCopyFrom( other );
    }

    protected abstract void doCopyFrom( FieldDefinition other );
}
