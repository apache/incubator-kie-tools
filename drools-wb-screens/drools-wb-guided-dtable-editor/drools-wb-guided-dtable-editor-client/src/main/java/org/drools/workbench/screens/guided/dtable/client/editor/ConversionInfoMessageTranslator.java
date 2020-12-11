/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.editor;

import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.shared.XLSConversionResultMessage;

public class ConversionInfoMessageTranslator {

    private ConversionInfoMessageTranslator() {

    }

    public static String translate(final XLSConversionResultMessage infoMessage) {
        switch (infoMessage.getType()) {
            case DIALECT_NOT_CONVERTED:
                return GuidedDecisionTableConstants.INSTANCE.DialectNotConverted();
            case RULE_NAME_NOT_CONVERTED:
                return GuidedDecisionTableConstants.INSTANCE.RuleNameNotConverted();
            default:
                return GuidedDecisionTableConstants.INSTANCE.RanIntoIssueWhenConverting();
        }
    }
}
