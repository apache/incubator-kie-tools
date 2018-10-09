/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.editors.types.persistence.validation;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.messages.DataTypeFlashMessage;

import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.NAME_DATA_FIELD;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.UUID_ATTR;
import static org.kie.workbench.common.dmn.client.editors.types.messages.DataTypeFlashMessage.Type.ERROR;

abstract class ErrorMessage {

    final TranslationService translationService;

    ErrorMessage(final TranslationService translationService) {
        this.translationService = translationService;
    }

    DataTypeFlashMessage getFlashMessage(final DataType dataType) {
        return new DataTypeFlashMessage(ERROR, getStrongMessage(dataType), getRegularMessage(), getErrorElementSelector(dataType));
    }

    private String getErrorElementSelector(final DataType dataType) {
        return "[" + UUID_ATTR + "=\"" + dataType.getUUID() + "\"] [data-field=\"" + NAME_DATA_FIELD + "\"]";
    }

    abstract String getStrongMessage(final DataType dataType);

    abstract String getRegularMessage();
}
