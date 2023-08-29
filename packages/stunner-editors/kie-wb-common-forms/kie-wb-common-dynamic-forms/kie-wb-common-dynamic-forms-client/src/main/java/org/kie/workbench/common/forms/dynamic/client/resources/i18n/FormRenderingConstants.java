/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.dynamic.client.resources.i18n;

import org.jboss.errai.ui.shared.api.annotations.TranslationKey;

public interface FormRenderingConstants {

    @TranslationKey(defaultValue = "")
    String MultipleSubformNoColumns = "MultipleSubform.noColumns";

    @TranslationKey(defaultValue = "")
    String MultipleSubformNoCreationForm = "MultipleSubform.noCreationForm";

    @TranslationKey(defaultValue = "")
    String MultipleSubformWrongCreationForm = "MultipleSubform.wrongCreationForm";

    @TranslationKey(defaultValue = "")
    String MultipleSubformNoEditionForm = "MultipleSubform.noEditionForm";

    @TranslationKey(defaultValue = "")
    String MultipleSubformWongEditionForm = "MultipleSubform.wrongEditionForm";

    @TranslationKey(defaultValue = "")
    String SubFormNoForm = "SubForm.noForm";

    @TranslationKey(defaultValue = "")
    String SubFormWrongForm = "SubForm.wrongForm";

    @TranslationKey(defaultValue = "")
    String ListBoxFieldRendererEmptyOptionText = "ListBoxFieldRenderer.emptyOptionText";

    @TranslationKey(defaultValue = "")
    String EditableColumnGeneratorValueHeader = "EditableColumnGenerator.valueHeader";

    @TranslationKey(defaultValue = "")
    String LOVCreationComponentViewImplNoItems = "LOVCreationComponentViewImpl.noItems";

    @TranslationKey(defaultValue = "")
    String LOVCreationComponentViewImplAddButton = "LOVCreationComponentViewImpl.addButton";

    @TranslationKey(defaultValue = "")
    String LOVCreationComponentViewImplRemoveButton = "LOVCreationComponentViewImpl.removeButton";

    @TranslationKey(defaultValue = "")
    String LOVCreationComponentViewImplMoveUp = "LOVCreationComponentViewImpl.moveUp";

    @TranslationKey(defaultValue = "")
    String LOVCreationComponentViewImplMoveDown = "LOVCreationComponentViewImpl.moveDown";

    @TranslationKey(defaultValue = "")
    String CharacterEditableColumnGeneratorValidationError = "CharacterEditableColumnGenerator.validationError";

    @TranslationKey(defaultValue = "")
    String InvalidInteger = "InvalidInteger";

    @TranslationKey(defaultValue = "")
    String InvalidIntegerWithRange = "InvalidIntegerWithRange";

    @TranslationKey(defaultValue = "")
    String InvalidDecimal = "InvalidDecimal";

    @TranslationKey(defaultValue = "")
    String InvalidDecimalWithRange = "InvalidDecimalWithRange";

    @TranslationKey(defaultValue = "")
    String DecimalEditableColumnGeneratorInvalidNumber = "DecimalEditableColumnGenerator.invalidNumber";

    @TranslationKey(defaultValue = "")
    String BooleanEditableColumnGeneratorYes = "BooleanEditableColumnGenerator.yes";

    @TranslationKey(defaultValue = "")
    String BooleanEditableColumnGeneratorNo= "BooleanEditableColumnGenerator.no";

    @TranslationKey(defaultValue = "")
    String DatePickerWrapperViewImplShowDateTooltip= "DatePickerWrapperViewImpl.showDateTooltip";

    @TranslationKey(defaultValue = "")
    String DatePickerWrapperViewImplClearDateTooltip= "DatePickerWrapperViewImpl.clearDateTooltip";
}
