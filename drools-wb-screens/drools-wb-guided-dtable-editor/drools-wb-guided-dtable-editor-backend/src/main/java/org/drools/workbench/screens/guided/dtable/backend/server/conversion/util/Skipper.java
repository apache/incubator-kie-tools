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
package org.drools.workbench.screens.guided.dtable.backend.server.conversion.util;

import java.util.Objects;

import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.models.guided.dtable.shared.model.RuleNameColumn;
import org.drools.workbench.screens.guided.dtable.shared.XLSConversionResultMessage;
import org.drools.workbench.screens.guided.dtable.shared.XLSConversionResultMessageType;

import static org.drools.workbench.models.datamodel.rule.Attribute.DIALECT;

public class Skipper {

    private Skipper() {
    }

    /**
     * @param notificationReporter Logs the reasons for skipping when the user needs to know about it.
     * @param baseColumn Column that is investigated. If skipped this is not added to the XLS
     * @return If the baseColumn should be left out of XLS or not.
     */
    public static boolean shouldSkip(final NotificationReporter notificationReporter,
                                     final BaseColumn baseColumn) {

        if (baseColumn instanceof RowNumberCol52) {
            // No use for this in XLS
            return true;
        } else if (baseColumn instanceof RuleNameColumn) {
            notificationReporter.report(new XLSConversionResultMessage(XLSConversionResultMessageType.RULE_NAME_NOT_CONVERTED,
                                                                       "Rule Name column conversion is not " +
                                                                               "supported yet. Conversion ignored this column."));
            return true;
        } else if (isDialect(baseColumn)) {
            notificationReporter.report(new XLSConversionResultMessage(XLSConversionResultMessageType.DIALECT_NOT_CONVERTED,
                                                                       "Dialect is not a supported column type in XLS Decision tables. Conversion ignored this column."));
            return true;
        } else {
            return false;
        }
    }

    public static boolean isDialect(final BaseColumn baseColumn) {
        return baseColumn instanceof AttributeCol52 && Objects.equals(((AttributeCol52) baseColumn).getAttribute(),
                                                                      DIALECT.getAttributeName());
    }
}
