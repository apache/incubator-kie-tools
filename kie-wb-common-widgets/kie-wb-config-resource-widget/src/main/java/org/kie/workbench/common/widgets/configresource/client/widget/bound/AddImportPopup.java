/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.configresource.client.widget.bound;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.workbench.common.widgets.configresource.client.resources.i18n.ImportConstants;
import org.kie.workbench.common.widgets.configresource.client.widget.Sorters;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class AddImportPopup extends BaseModal {

    interface AddGlobalPopupBinder
            extends
            UiBinder<Widget, AddImportPopup> {

    }

    private static AddGlobalPopupBinder uiBinder = GWT.create(AddGlobalPopupBinder.class);

    @UiField
    FormGroup importTypeGroup;

    @UiField
    ListBox importTypeListBox;

    private Command callbackCommand;

    private final Command okCommand = this::onOKButtonClick;

    private final Command cancelCommand = this::hide;

    private final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons(okCommand,
                                                                                     cancelCommand);

    public AddImportPopup() {
        setTitle(ImportConstants.INSTANCE.addImportPopupTitle());

        add(new ModalBody() {{
            add(uiBinder.createAndBindUi(AddImportPopup.this));
        }});
        add(footer);

        importTypeListBox.getElement().getStyle().setWidth(100.0,
                                                           Style.Unit.PCT);
        importTypeListBox.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                final boolean enable = importTypeListBox.getSelectedIndex() > 0;
                footer.enableOkButton(enable);
            }
        });
    }

    private void onOKButtonClick() {
        if (callbackCommand != null) {
            callbackCommand.execute();
        }
        hide();
    }

    public String getImportType() {
        return importTypeListBox.getSelectedValue();
    }

    public void setContent(final Command callbackCommand,
                           final List<Import> allAvailableImportTypes) {
        this.callbackCommand = callbackCommand;
        this.importTypeListBox.clear();

        if (allAvailableImportTypes.size() > 0) {
            allAvailableImportTypes.sort(Sorters.sortByFQCN());
            importTypeListBox.addItem(ImportConstants.INSTANCE.ChooseAFactType());
            for (Import importType : allAvailableImportTypes) {
                importTypeListBox.addItem(importType.getType());
            }
            importTypeListBox.setEnabled(true);
        } else {
            importTypeListBox.addItem(ImportConstants.INSTANCE.noTypesAvailable());
            importTypeListBox.setEnabled(false);
        }

        footer.enableOkButton(false);
        importTypeListBox.setSelectedIndex(0);
    }
}
