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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.string;

import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.util.ValuePairEditorUtil;

public class CharacterValuePairEditor
    extends AbstractStringValuePairEditor {

    private boolean valid = true;

    public CharacterValuePairEditor() {
        super();
    }

    public CharacterValuePairEditor( AbstractStringValuePairEditorView view ) {
        super( view );
    }

    @Override
    public void onValueChange() {
        String originalCurrentValue = view.getValue();
        currentValue = trimCharValue( originalCurrentValue );
        if ( originalCurrentValue != null && !originalCurrentValue.equals( currentValue ) ) {
            view.setValue( currentValue );
        }
        valid = currentValue == null || ValuePairEditorUtil.isValidCharacterLiteral( currentValue );
        if ( valid ) {
            view.clearErrorMessage();
        } else {
            view.setErrorMessage( Constants.INSTANCE.character_value_pair_editor_invalid_character_message() );
        }

        if ( editorHandler != null ) {
            editorHandler.onValueChange();
        }
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void setValue( String value ) {
        super.setValue( ValuePairEditorUtil.unquoteCharacterLiteral( value ) );
    }

    private String trimCharValue( String charValue ) {
        if ( charValue == null || "".equals( charValue ) ) {
            return null;
        }
        if ( ValuePairEditorUtil.isBlankCharaterSequence( charValue ) ) {
            return Character.toString( ' ' );
        } else {
            return charValue.trim();
        }
    }
}