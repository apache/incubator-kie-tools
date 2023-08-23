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

package org.kie.workbench.common.dmn.client.editors.included.imports.messages;

import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;

import static org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage.Type.ERROR;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.IncludedModelNameIsBlankErrorMessage_RegularMessage;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.IncludedModelNameIsBlankErrorMessage_StrongMessage;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.IncludedModelNameIsNotUniqueErrorMessage_RegularMessage;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.IncludedModelNameIsNotUniqueErrorMessage_StrongMessage;
import static org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView.CARD_UUID_ATTR;

public class IncludedModelErrorMessageFactory {

    private final TranslationService translationService;

    @Inject
    public IncludedModelErrorMessageFactory(final TranslationService translationService) {
        this.translationService = translationService;
    }

    public FlashMessage getNameIsNotUniqueFlashMessage(final BaseIncludedModelActiveRecord includedModel) {
        return new FlashMessage(ERROR,
                                translate(IncludedModelNameIsNotUniqueErrorMessage_StrongMessage, includedModel.getName()),
                                translate(IncludedModelNameIsNotUniqueErrorMessage_RegularMessage),
                                getErrorElementSelector(includedModel));
    }

    public FlashMessage getNameIsBlankFlashMessage(final BaseIncludedModelActiveRecord includedModel) {
        return new FlashMessage(ERROR,
                                translate(IncludedModelNameIsBlankErrorMessage_StrongMessage),
                                translate(IncludedModelNameIsBlankErrorMessage_RegularMessage),
                                getErrorElementSelector(includedModel));
    }

    private String getErrorElementSelector(final BaseIncludedModelActiveRecord includedModel) {
        return "[" + CARD_UUID_ATTR + "=\"" + includedModel.getUUID() + "\"] [data-field=\"title-input\"]";
    }

    private String translate(final String key,
                             final Object... args) {
        return translationService.format(key, args);
    }
}
