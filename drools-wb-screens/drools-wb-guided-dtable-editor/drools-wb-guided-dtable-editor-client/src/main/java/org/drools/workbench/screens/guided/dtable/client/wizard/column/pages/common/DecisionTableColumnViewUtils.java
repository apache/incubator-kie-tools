/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;

public class DecisionTableColumnViewUtils {

    public static int getCurrentIndexFromList(final String currentValue,
                                              final ListBox list) {
        int currentIndexFromList = getCurrentIndexFromListWithoutDefaultSelect(currentValue,
                                                                               list);

        if (currentIndexFromList == -1) {
            return 0;
        }

        return currentIndexFromList;
    }

    public static int getCurrentIndexFromListWithoutDefaultSelect(final String currentValue,
                                                                  final ListBox list) {
        for (int index = 0; index < list.getItemCount(); index++) {
            final String value = list.getValue(index);

            if (value != null && value.equals(currentValue)) {
                return index;
            }
        }

        return -1;
    }

    public static boolean nil(String s) {
        return s == null || s.equals("");
    }

    public static void addWidgetToContainer(final IsWidget widget,
                                            final Div container) {

        clean(container);
        append(widget,
               container);
    }

    public static String getColumnManagementGroupTitle(final BaseColumn column) {
        String managementSectionHeader = null;
        if (column instanceof ActionInsertFactCol52) {
            managementSectionHeader = concatenateFactTypeAndBoundName(((ActionInsertFactCol52) column).getFactType(),
                                                                      ((ActionInsertFactCol52) column).getBoundName());
        } else if (column instanceof ActionSetFieldCol52) {
            managementSectionHeader = concatenateFactTypeAndBoundName(null,
                                                                      ((ActionSetFieldCol52) column).getBoundName());
        } else if (column instanceof Pattern52) {
            managementSectionHeader = concatenateFactTypeAndBoundName(((Pattern52) column).getFactType(),
                                                                      ((Pattern52) column).getBoundName());
            if (((Pattern52) column).isNegated()) {
                managementSectionHeader = GuidedDecisionTableConstants.INSTANCE.negatedPattern() + " " + managementSectionHeader;
            }
        } else if (column instanceof ActionRetractFactCol52) {
            managementSectionHeader = GuidedDecisionTableConstants.INSTANCE.RetractActions();
        } else if (column instanceof ActionWorkItemCol52) {
            managementSectionHeader = GuidedDecisionTableConstants.INSTANCE.ExecuteWorkItemActions();
        } else if (column instanceof BRLActionColumn) {
            managementSectionHeader = GuidedDecisionTableConstants.INSTANCE.BrlActions();
        } else if (column instanceof BRLConditionColumn) {
            managementSectionHeader = GuidedDecisionTableConstants.INSTANCE.BrlConditions();
        }

        if (managementSectionHeader == null || managementSectionHeader.isEmpty()) {
            return column.getHeader();
        } else {
            return managementSectionHeader;
        }
    }

    private static String concatenateFactTypeAndBoundName(final String factType,
                                                          final String boundName) {
        return Stream.of(factType,
                         boundName != null && !boundName.isEmpty() ? "[" + boundName + "]" : boundName)
                .filter(text -> text != null && !text.isEmpty())
                .collect(Collectors.joining(" "));
    }

    private static void append(final IsWidget widget,
                               final Div container) {
        DOMUtil.appendWidgetToElement(container,
                                      widget);
    }

    private static void clean(final Div container) {
        DOMUtil.removeAllChildren(container);
    }
}
