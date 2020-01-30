/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.client.handlers;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.model.Package;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.widgets.client.resources.i18n.KieWorkbenchWidgetsConstants;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

@Templated
@ApplicationScoped
public class NewResourceViewImpl implements NewResourceView,
                                            IsElement {

    @DataField
    DivElement fileNameGroup = Document.get().createDivElement();
    @DataField
    FormLabel fileTypeLabel = new FormLabel();
    @Inject
    @DataField
    TextBox fileNameTextBox;
    @Inject
    @DataField
    HelpBlock fileNameHelpInline;
    @Inject
    @DataField
    PackageListBox packageListBox;
    @DataField
    DivElement packageGroup = Document.get().createDivElement();
    @Inject
    @DataField
    HelpBlock packageHelpInline;
    @DataField
    DivElement handlerExtensionsGroup = Document.get().createDivElement();
    @Inject
    @DataField
    FlowPanel handlerExtensions;
    TranslationService translationService;
    BaseModal modal;
    private final Command cancelCommand = new Command() {
        @Override
        public void execute() {
            hide();
        }
    };
    private NewResourcePresenter presenter;
    private final Command okCommand = new Command() {
        @Override
        public void execute() {
            onOKButtonClick();
        }
    };

    private final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons(okCommand,
                                                                                     cancelCommand);

    @Inject
    public NewResourceViewImpl(TranslationService translationService) {
        this.translationService = translationService;
        footer.enableOkButton(true);
    }

    @PostConstruct
    public void init() {
        modal = new BaseModal();

        modal.setBody(ElementWrapperWidget.getWidget(this.getElement()));

        modal.add(footer);

        fileNameTextBox.setPlaceholder(translationService.getTranslation(KieWorkbenchWidgetsConstants.NewResourceViewResourceNamePlaceholder));
        fileTypeLabel.setShowRequiredIndicator(true);
    }

    @Override
    public void init(final NewResourcePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void show() {
        //Clear previous resource name
        fileNameTextBox.setText("");

        clearErrors();

        packageListBox.clearSelectElement();

        modal.show();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @Override
    public void setActiveHandler(final NewResourceHandler handler) {
        final List<Pair<String, ? extends IsWidget>> extensions = handler.getExtensions();
        final boolean showExtensions = !(extensions == null || extensions.isEmpty());
        fileTypeLabel.setText(handler.getDescription());

        packageListBox.setUp(handler.supportsDefaultPackage());

        handlerExtensions.clear();
        handlerExtensionsGroup.getStyle().setDisplay(showExtensions ? Style.Display.BLOCK : Style.Display.NONE);
        if (showExtensions) {
            extensions.forEach(pair -> {
                if (pair.getK1() != null && !pair.getK1().isEmpty()) {
                    final FormLabel extensionLabel = GWT.create(FormLabel.class);
                    extensionLabel.setText(pair.getK1());
                    handlerExtensions.add(extensionLabel);
                }
                handlerExtensions.add(pair.getK2());
            });
        }
    }

    void onOKButtonClick() {

        clearErrors();

        //Generic validation
        final String fileName = fileNameTextBox.getText();
        if (fileName == null || fileName.trim().isEmpty()) {
            fileNameGroup.addClassName(ValidationState.ERROR.getCssName());
            fileNameHelpInline.setText(translationService.getTranslation(KieWorkbenchWidgetsConstants.NewResourceViewFileNameIsMandatory));
            return;
        }

        if (packageListBox.getSelectedPackage() == null) {
            packageGroup.addClassName(ValidationState.ERROR.getCssName());
            packageHelpInline.setText(translationService.getTranslation(KieWorkbenchWidgetsConstants.NewResourceViewMissingPath));
            return;
        }

        //Specialized validation
        presenter.validate(fileName,
                           new ValidatorWithReasonCallback() {

                               @Override
                               public void onSuccess() {
                                   presenter.makeItem(fileName);
                               }

                               @Override
                               public void onFailure() {

                               }

                               @Override
                               public void onFailure(final String reason) {
                                   fileNameGroup.addClassName(ValidationState.ERROR.getCssName());
                                   fileNameHelpInline.setText(reason);
                               }
                           });
    }

    @Override
    public Package getSelectedPackage() {
        return packageListBox.getSelectedPackage();
    }

    @Override
    public void setTitle(String title) {
        modal.setTitle(title);
    }

    @Override
    public void setResourceName(String resourceName) {
        fileNameTextBox.setText(resourceName);
    }

    protected void clearErrors() {
        fileNameGroup.removeClassName(ValidationState.ERROR.getCssName());
        fileNameHelpInline.clearError();
        packageGroup.removeClassName(ValidationState.ERROR.getCssName());
        packageHelpInline.clearError();
    }
}
