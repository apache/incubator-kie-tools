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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.booleans.BooleanValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.booleans.MultipleBooleanValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.generic.GenericValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.enums.MultipleEnumValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.enums.EnumValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.numeric.MultipleNumericValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.numeric.NumericValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.string.CharacterValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.string.MultipleCharacterValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.string.MultipleStringValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.string.StringValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.util.ValuePairEditorUtil;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;

@Dependent
public class ValuePairEditorProvider {

    @Inject
    private GenericValuePairEditor genericValuePairEditor;

    public ValuePairEditorProvider() {
    }

    public ValuePairEditor getValuePairEditor( AnnotationValuePairDefinition valuePairDefinition ) {
        ValuePairEditor result = null;

        if ( ValuePairEditorUtil.isNumberType( valuePairDefinition ) ) {

            if ( !valuePairDefinition.isArray() ) {
                result = GWT.create( NumericValuePairEditor.class );
            } else {
                result = GWT.create( MultipleNumericValuePairEditor.class );
            }

        } else if ( valuePairDefinition.isPrimitiveType() && ( valuePairDefinition.getClassName().equals( "boolean" ) ||
                valuePairDefinition.getClassName().equals( "java.lang.Boolean" ) ) ) {

            if ( !valuePairDefinition.isArray() ) {
                result = GWT.create( BooleanValuePairEditor.class );
            } else {
                result = GWT.create( MultipleBooleanValuePairEditor.class );
            }

        } else if ( valuePairDefinition.isPrimitiveType() && ( valuePairDefinition.getClassName().equals( "char" ) ||
                valuePairDefinition.getClassName().equals( "java.lang.Character" ) ) ) {

            if ( !valuePairDefinition.isArray() ) {
                result = GWT.create( CharacterValuePairEditor.class );
            } else {
                result = GWT.create( MultipleCharacterValuePairEditor.class );
            }

        } else if ( valuePairDefinition.isString() ) {

            if ( !valuePairDefinition.isArray() ) {
                result = GWT.create( StringValuePairEditor.class );
            } else {
                result = GWT.create( MultipleStringValuePairEditor.class );
            }

        } else if ( valuePairDefinition.isEnum() ) {

            if ( !valuePairDefinition.isArray() ) {
                result = GWT.create( EnumValuePairEditor.class );
            } else {
                result = GWT.create( MultipleEnumValuePairEditor.class );
            }
        }

        if ( result == null ) {
            result = genericValuePairEditor;
        }
        result.init( valuePairDefinition );

        return result;
    }

}
