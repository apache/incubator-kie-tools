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

package org.kie.workbench.common.screens.datamodeller.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Legend;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.kie.workbench.common.screens.datamodeller.client.widgets.editor.DataObjectBrowser;
import org.kie.workbench.common.screens.datamodeller.client.widgets.maindomain.MainDomainEditor;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;

@Dependent
public class DataModelerScreenViewImpl
        extends KieEditorViewImpl
        implements DataModelerScreenPresenter.DataModelerScreenView {

    interface DataModelerScreenViewBinder
            extends
            UiBinder<Widget, DataModelerScreenViewImpl> {

    }

    private static DataModelerScreenViewBinder uiBinder = GWT.create(DataModelerScreenViewBinder.class);

    @UiField
    Column dataObjectPanel;

    @UiField
    FlowPanel domainContainerPanel;

    @UiField
    Legend domainContainerTitle;

    private DataObjectBrowser dataObjectBrowser;

    private MainDomainEditor mainDomainEditor;

    @Inject
    public DataModelerScreenViewImpl(DataObjectBrowser dataObjectBrowser,
                                     MainDomainEditor mainDomainEditor) {
        initWidget(uiBinder.createAndBindUi(this));
        this.dataObjectBrowser = dataObjectBrowser;
        this.mainDomainEditor = mainDomainEditor;
    }

    @PostConstruct
    private void initUI() {
        dataObjectPanel.add(dataObjectBrowser);
        domainContainerPanel.add(mainDomainEditor);
    }

    @Override
    public void setContext(DataModelerContext context) {
        dataObjectBrowser.setContext(context);
        mainDomainEditor.setContext(context);
    }

    @Override
    public void refreshTypeLists(boolean keepSelection) {
        mainDomainEditor.refreshTypeList(keepSelection);
    }

    @Override
    public void redraw() {
        dataObjectBrowser.redrawFields();
    }

    @Override
    public void showYesNoCancelPopup(String title,
                                     String message,
                                     Command yesCommand,
                                     String yesButtonText,
                                     ButtonType yesButtonType,
                                     Command noCommand,
                                     String noButtonText,
                                     ButtonType noButtonType) {

        YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup(title,
                                                                                 message,
                                                                                 yesCommand,
                                                                                 yesButtonText,
                                                                                 yesButtonType,
                                                                                 noCommand,
                                                                                 noButtonText,
                                                                                 noButtonType,
                                                                                 new Command() {
                                                                                     @Override
                                                                                     public void execute() {
                                                                                         //do nothing, but let the cancel button be shown.
                                                                                     }
                                                                                 },
                                                                                 null,
                                                                                 null);
        yesNoCancelPopup.setClosable(false);
        yesNoCancelPopup.show();
    }

    @Override
    public void showYesNoCancelPopup(String title,
                                     String message,
                                     Command yesCommand,
                                     Command noCommand) {

        YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup(title,
                                                                                 message,
                                                                                 yesCommand,
                                                                                 noCommand,
                                                                                 new Command() {
                                                                                     @Override
                                                                                     public void execute() {
                                                                                         //do nothing, but let the cancel button be shown.
                                                                                     }
                                                                                 });
        yesNoCancelPopup.setClosable(false);
        yesNoCancelPopup.show();
    }

    @Override
    public void showParseErrorsDialog(String title,
                                      String message,
                                      Command onCloseCommand) {

        YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup(title,
                                                                                 message,
                                                                                 onCloseCommand,
                                                                                 CommonConstants.INSTANCE.OK(),
                                                                                 null,
                                                                                 null,
                                                                                 null,
                                                                                 null,
                                                                                 null,
                                                                                 null,
                                                                                 null);
        yesNoCancelPopup.setClosable(false);
        yesNoCancelPopup.show();
    }

    @Override
    public void setDomainContainerTitle(String title,
                                        String tooltip) {
        domainContainerTitle.setText(title);
        domainContainerTitle.setTitle(tooltip);
    }
}