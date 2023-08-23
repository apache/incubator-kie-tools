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

import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;

import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.UUID_ATTR;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.ReferencedDataTypeWarningMessage_RegularMessage;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.ReferencedDataTypeWarningMessage_StrongMessage;

public class ReferencedDataTypeWarningMessage extends WarningMessage {

    @Inject
    public ReferencedDataTypeWarningMessage(final TranslationService translationService) {
        super(translationService);
    }

    @Override
    String getStrongMessage(final DataType dataType) {
        return translationService.format(ReferencedDataTypeWarningMessage_StrongMessage, dataType.getName());
    }

    @Override
    String getRegularMessage() {
        return translationService.format(ReferencedDataTypeWarningMessage_RegularMessage);
    }

    @Override
    String getErrorElementSelector(final DataType dataType) {
        return "[" + UUID_ATTR + "=\"" + dataType.getUUID() + "\"] select";
    }
}
