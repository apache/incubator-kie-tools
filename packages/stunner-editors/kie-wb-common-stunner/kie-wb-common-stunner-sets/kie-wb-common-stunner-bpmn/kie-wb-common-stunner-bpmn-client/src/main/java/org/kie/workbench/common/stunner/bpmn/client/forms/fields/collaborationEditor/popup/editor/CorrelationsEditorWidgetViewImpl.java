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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.collaborationEditor.popup.editor;

import java.util.Collection;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.databinding.client.api.handler.list.BindableListChangeHandler;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerBPMNConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.Correlation;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;

import static com.google.gwt.dom.client.Style.Display.NONE;
import static com.google.gwt.dom.client.Style.Display.TABLE;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.collaborationEditor.popup.editor.CorrelationsEditorValidator.getCorrelationName;

@Dependent
@Templated("CorrelationsEditorWidget.html#widget")
public class CorrelationsEditorWidgetViewImpl extends Composite implements CorrelationsEditorWidgetView {

    @DataField
    private final TableElement table = Document.get().createTableElement();

    @Inject
    @DataField
    protected Button addCorrelationButton;

    @DataField
    private HeadingElement tableTitle = Document.get().createHElement(3);

    @DataField
    protected TableCellElement idTableHeader = Document.get().createTHElement();

    @DataField
    protected TableCellElement nameTableHeader = Document.get().createTHElement();

    @DataField
    protected TableCellElement propertyIdTableHeader = Document.get().createTHElement();

    @DataField
    protected TableCellElement propertyNameTableHeader = Document.get().createTHElement();

    @Inject
    @DataField
    @Table(root = "tbody")
    protected ListWidget<Correlation, CorrelationListItemWidgetView> correlationListItems;

    @Inject
    @DataField
    private AnchorElement toggleErrorsAnchor;

    private Presenter presenter;

    private boolean showErrors;

    @Inject
    protected ClientTranslationService translationService;

    @Override
    public void init(final CorrelationsEditorWidgetViewImpl.Presenter presenter) {
        this.presenter = presenter;

        tableTitle.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.Correlations_Title());

        addCorrelationButton.setText(StunnerFormsClientFieldsConstants.CONSTANTS.Add());
        addCorrelationButton.setIcon(IconType.PLUS);

        idTableHeader.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.Id());
        nameTableHeader.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.Name());
        propertyIdTableHeader.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.PropertyId());
        propertyNameTableHeader.setInnerText(StunnerFormsClientFieldsConstants.CONSTANTS.PropertyName());

        correlationListItems.addValueChangeHandler(valueChangeEvent -> {
            ValueChangeEvent.fire(this.presenter, valueChangeEvent.getValue());
        });

        correlationListItems.addBindableListChangeHandler(new BindableListChangeHandler<Correlation>() {
            @Override
            public void onItemChanged(List<Correlation> source, int index, Correlation item) {
                ValueChangeEvent.fire(presenter, getCorrelations());
            }

            @Override
            public void onItemsAddedAt(List<Correlation> source, int index, Collection<? extends Correlation> items) {
                ValueChangeEvent.fire(presenter, getCorrelations());
            }

            @Override
            public void onItemsRemovedAt(List<Correlation> source, List<Integer> indexes) {
                ValueChangeEvent.fire(presenter, getCorrelations());
            }
        });
    }

    @Override
    public Widget getWidget() {
        return this;
    }

    @Override
    public List<Correlation> getCorrelations() {
        return correlationListItems.getValue();
    }

    @Override
    public void setCorrelations(List<Correlation> correlations) {
        correlationListItems.setValue(correlations);
        ValueChangeEvent.fire(this.presenter, correlations);
    }

    @Override
    public void update(List<CorrelationsEditorValidationItem> validationItems) {
        if (CorrelationsEditorValidator.hasInvalidCorrelation(validationItems)) {
            toggleErrorsAnchor.getStyle().setVisibility(Style.Visibility.VISIBLE);
        } else {
            toggleErrorsAnchor.getStyle().setVisibility(Style.Visibility.HIDDEN);
        }

        if (showErrors) {
            toggleErrorsAnchor.setInnerText(translationService.getValue(StunnerBPMNConstants.CORRELATION_HIDE_ERRORS));
        } else {
            toggleErrorsAnchor.setInnerText(translationService.getValue(StunnerBPMNConstants.CORRELATION_SHOW_ERRORS));
        }

        int itemsCount = correlationListItems.getComponentCount();
        if (itemsCount > 0) {
            setDisplayStyle(TABLE);
            validationItems.stream()
                    .forEach(vi -> {
                        CorrelationListItemWidgetView listItemWidgetView =
                                correlationListItems.getComponent(vi.getCorrelation());
                        listItemWidgetView.setParentWidget(presenter);
                        listItemWidgetView.update(vi, showErrors);
                        if (vi.isEmptyName()) {
                            getCorrelationName(validationItems, vi.getCorrelation().getId()).ifPresent(correlationName -> {
                                listItemWidgetView.syncIDName(correlationName);
                            });
                        }
                    });
        } else {
            setDisplayStyle(NONE);
        }
    }

    private void setDisplayStyle(Style.Display displayStyle) {
        table.getStyle().setDisplay(displayStyle);
    }

    @EventHandler("addCorrelationButton")
    public void handleAddCorrelationButton(final ClickEvent e) {
        presenter.addCorrelation();
    }

    @EventHandler("toggleErrorsAnchor")
    public void handleToggleErrors(final ClickEvent e) {
        showErrors = !showErrors;
        ValueChangeEvent.fire(presenter, this.getCorrelations());
    }
}