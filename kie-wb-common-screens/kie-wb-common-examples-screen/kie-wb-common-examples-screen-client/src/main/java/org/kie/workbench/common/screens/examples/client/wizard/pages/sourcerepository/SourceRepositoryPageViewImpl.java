/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.examples.client.wizard.pages.sourcerepository;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.common.client.dom.RadioInput;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.common.StyleHelper;

@Dependent
@Templated
public class SourceRepositoryPageViewImpl extends Composite implements SourceRepositoryPageView {

    @Inject
    @DataField("stockRadio")
    RadioInput stockRadio;

    @Inject
    @DataField("customRadio")
    RadioInput customRadio;

    @DataField("repository-form")
    Element repositoryGroup = DOM.createDiv();

    @Inject
    @DataField("repositoryUrlInput")
    TextInput repositoryUrlInput;

    @DataField("repository-help")
    Element repositoryHelp = DOM.createSpan();

    private SourceRepositoryPage presenter;

    @Override
    public void init(final SourceRepositoryPage presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initialise() {
        repositoryUrlInput.setValue("");
    }

    @Override
    public void setPlaceHolder(final String placeHolder) {
        repositoryUrlInput.setAttribute("placeholder",
                                        placeHolder);
    }

    @Override
    public void setUrlGroupType(final ValidationState state) {
        StyleHelper.addUniqueEnumStyleName(repositoryGroup,
                                           ValidationState.class,
                                           state);
    }

    @Override
    public void showUrlHelpMessage(final String message) {
        repositoryHelp.getStyle().setVisibility(Style.Visibility.VISIBLE);
        repositoryHelp.setInnerText(message);
    }

    @Override
    public void hideUrlHelpMessage() {
        repositoryHelp.getStyle().setVisibility(Style.Visibility.HIDDEN);
        repositoryHelp.setInnerText("");
    }

    @Override
    public void setStockRepositoryOption() {
        stockRadio.setChecked(true);
        customRadio.setChecked(false);
    }

    @Override
    public void disableStockRepositoryOption() {
        stockRadio.setDisabled(true);
    }

    @Override
    public void setCustomRepositoryOption() {
        customRadio.setChecked(true);
        stockRadio.setChecked(false);
    }

    @Override
    public void showRepositoryUrlInputForm() {
        repositoryGroup.getStyle().setVisibility(Style.Visibility.VISIBLE);
    }

    @Override
    public void hideRepositoryUrlInputForm() {
        repositoryGroup.getStyle().setVisibility(Style.Visibility.HIDDEN);
    }

    @Override
    public String getCustomRepositoryValue() {
        return repositoryUrlInput.getValue();
    }

    @Override
    public void setCustomRepositoryValue(final String value) {
        repositoryUrlInput.setValue(value);
    }

    @EventHandler("stockRadio")
    public void handleStockRadioClick(ClickEvent event) {
        presenter.playgroundRepositorySelected();
    }

    @EventHandler("customRadio")
    public void handleCustomRadioClick(ClickEvent event) {
        presenter.onCustomRepositorySelected();
    }

    @EventHandler("repositoryUrlInput")
    public void handleRepositoryUrlInputValueChange(ChangeEvent event) {
        presenter.onCustomRepositoryValueChanged();
    }
}
