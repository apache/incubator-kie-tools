/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.bpmn.definition.property.connectors;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.metaModel.FieldDef;
import org.kie.workbench.common.forms.metaModel.ListBox;
import org.kie.workbench.common.forms.metaModel.SelectorDataProvider;
import org.kie.workbench.common.forms.metaModel.TextArea;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;

import javax.validation.Valid;

import static org.kie.workbench.common.stunner.bpmn.util.FieldLabelConstants.*;

@Portable
@Bindable
@PropertySet
public class SequenceFlowExecutionSet implements BPMNPropertySet {

    @Name
    public static final transient String propertySetName = "Implementation/Execution";

    @Property
    @FieldDef( label = FIELDDEF_PRIORITY, property = "value", position = 1 )
    @Valid
    private Priority priority;

    @Property
    @FieldDef( label = FIELDDEF_CONDITION_EXPRESSION, property = "value", position = 2 )
    @TextArea( rows = 5 )
    @Valid
    private ConditionExpression conditionExpression;

    @Property
    @FieldDef( label = FIELDDEF_CONDITION_EXPRESSION_LANGUAGE, property = "value", position = 3 )
    @ListBox
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.REMOTE,
            className = "org.kie.workbench.common.stunner.bpmn.backend.dataproviders.ScriptLanguageFormProvider" )
    @Valid
    protected ConditionExpressionLanguage conditionExpressionLanguage;

    public SequenceFlowExecutionSet() {
        this( new Priority( "" ),
                new ConditionExpression( "" ),
                new ConditionExpressionLanguage( "" )
        );
    }

    public SequenceFlowExecutionSet( @MapsTo( "priority" ) Priority priority,
                                     @MapsTo( "conditionExpression" ) ConditionExpression conditionExpression,
                                     @MapsTo( "conditionExpressionLanguage" ) ConditionExpressionLanguage conditionExpressionLanguage ) {
        this.priority = priority;
        this.conditionExpression = conditionExpression;
        this.conditionExpressionLanguage = conditionExpressionLanguage;
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority( Priority priority ) {
        this.priority = priority;
    }

    public ConditionExpression getConditionExpression() {
        return conditionExpression;
    }

    public void setConditionExpression( ConditionExpression conditionExpression ) {
        this.conditionExpression = conditionExpression;
    }

    public ConditionExpressionLanguage getConditionExpressionLanguage() {
        return conditionExpressionLanguage;
    }

    public void setConditionExpressionLanguage( ConditionExpressionLanguage conditionExpressionLanguage ) {
        this.conditionExpressionLanguage = conditionExpressionLanguage;
    }
}
