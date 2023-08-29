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

package org.kie.workbench.common.dmn.client.editors.types.listview.confirmation;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage.Type.WARNING;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.UUID_ATTR;

abstract class WarningMessage {

    final TranslationService translationService;

    WarningMessage(final TranslationService translationService) {
        this.translationService = translationService;
    }

    FlashMessage getFlashMessage(final DataType dataType,
                                 final Command onSuccess,
                                 final Command onError) {
        return new FlashMessage(WARNING, getStrongMessage(dataType), getRegularMessage(), getErrorElementSelector(dataType), onSuccess, onError);
    }

    String getErrorElementSelector(final DataType dataType) {
        return "[" + UUID_ATTR + "=\"" + dataType.getUUID() + "\"] .bootstrap-select";
    }

    abstract String getStrongMessage(final DataType dataType);

    abstract String getRegularMessage();
}
