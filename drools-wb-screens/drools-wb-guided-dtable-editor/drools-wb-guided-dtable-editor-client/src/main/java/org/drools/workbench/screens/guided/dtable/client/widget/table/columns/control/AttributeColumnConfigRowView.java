/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.table.columns.control;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.DefaultValueWidgetFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;

public class AttributeColumnConfigRowView extends HorizontalPanel {

    public void addRemoveAttributeButton(final ClickHandler clickHandler,
                                         final boolean isEditable) {
        final Anchor anchor = new Anchor();

        anchor.setText(GuidedDecisionTableConstants.INSTANCE.Delete());
        anchor.addClickHandler(clickHandler);
        anchor.setEnabled(isEditable);

        add(anchor);
    }

    public void addColumnLabel(AttributeCol52 attributeColumn) {
        SmallLabel label = new SmallLabel(attributeColumn.getAttribute());
        ColumnUtilities.setColumnLabelStyleWhenHidden(label,
                                                      attributeColumn.isHideColumn());
        add(label);
    }

    public void addDefaultValue(AttributeCol52 attributeColumn,
                                boolean isEditable,
                                DefaultValueWidgetFactory.DefaultValueChangedEventHandler handler) {
        final FlowPanel panel = new FlowPanel();

        panel.add(new SmallLabel(new StringBuilder(GuidedDecisionTableConstants.INSTANCE.DefaultValue()).append(GuidedDecisionTableConstants.COLON).toString()));
        panel.add(DefaultValueWidgetFactory.getDefaultValueWidget(attributeColumn,
                                                                  !isEditable,
                                                                  handler));
        add(panel);
    }

    public CheckBox addUseRowNumberCheckBox(AttributeCol52 attributeColumn,
                                            boolean isEditable,
                                            ClickHandler clickHandler) {
        final CheckBox chkUseRowNumber = new CheckBox(GuidedDecisionTableConstants.INSTANCE.UseRowNumber());
        chkUseRowNumber.setValue(attributeColumn.isUseRowNumber());
        chkUseRowNumber.setEnabled(isEditable);
        chkUseRowNumber.addClickHandler(clickHandler);
        add(chkUseRowNumber);
        return chkUseRowNumber;
    }

    public CheckBox addReverseOrderCheckBox(AttributeCol52 attributeColumn,
                                            boolean isEditable,
                                            ClickHandler clickHandler) {
        final CheckBox chkReverseOrder = new CheckBox(GuidedDecisionTableConstants.INSTANCE.ReverseOrder());
        chkReverseOrder.setValue(attributeColumn.isReverseOrder());
        chkReverseOrder.setEnabled(attributeColumn.isUseRowNumber() && isEditable);
        chkReverseOrder.addClickHandler(clickHandler);
        add(chkReverseOrder);
        return chkReverseOrder;
    }

    public CheckBox addHideColumnCheckBox(AttributeCol52 attributeColumn,
                                          ClickHandler clickHandler) {
        final CheckBox chkHideColumn = new CheckBox(new StringBuilder(GuidedDecisionTableConstants.INSTANCE.HideThisColumn())
                                                            .append(GuidedDecisionTableConstants.COLON)
                                                            .toString());
        chkHideColumn.setValue(attributeColumn.isHideColumn());
        chkHideColumn.addClickHandler(clickHandler);
        add(chkHideColumn);
        return chkHideColumn;
    }

    /**
     * @param dialogueText
     * @return Returns true if the dialogue with 'dialogueText' was confirmed
     */
    public boolean confirmDeleteColumnActionDialogue(String dialogueText) {
        return Window.confirm(dialogueText);
    }
}
