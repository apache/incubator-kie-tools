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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.BaseDecisionTableColumnPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.MetaDataColumnPlugin;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElement;

import static org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTableColumnViewUtils.nil;

@Dependent
public class MetaDataColumnPage extends BaseDecisionTableColumnPage<MetaDataColumnPlugin> {

    private View view;

    Caller<ValidationService> validationService;

    @Inject
    public MetaDataColumnPage(final View view,
                              final TranslationService translationService,
                              final Caller<ValidationService> validationService) {
        super(translationService);

        this.view = view;
        this.validationService = validationService;
    }

    @Override
    protected UberElement<?> getView() {
        return view;
    }

    @Override
    public String getTitle() {
        return translate(GuidedDecisionTableErraiConstants.MetaDataColumnPage_AddNewMetadata);
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        final String metadata = plugin().getMetaData();
        final boolean hasMetaData = !nil(metadata);
        final boolean isUnique = hasMetaData && presenter.isMetaDataUnique(metadata);

        if(!hasMetaData) {
            view.showError(translate(GuidedDecisionTableErraiConstants.MetaDataColumnPage_MetadataNameEmpty));
        }
        else if(!isUnique) {
            view.showError(translate(GuidedDecisionTableErraiConstants.MetaDataColumnPage_ThatColumnNameIsAlreadyInUsePleasePickAnother));
        }

        if(isUnique) {
            isValidIdentifier(metadata, callback);
        } else {
            callback.callback(false);
        }
    }

    @Override
    public void prepareView() {
        view.init(this);
        view.clear();
    }

    public String getMetadata() {
        return plugin().getMetaData();
    }

    public void setMetadata(String metadata) {
        plugin().setMetaData(metadata.trim());
    }

    public interface View extends UberElement<MetaDataColumnPage> {

        void showError(String errorMessage);

        void hideError();

        void clear();
    }

    private void isValidIdentifier(String text, Callback<Boolean> callback) {
        validationService.call(new RemoteCallback<Map<String, Boolean>>() {
                                   @Override
                                   public void callback(Map<String, Boolean> evaluatedIdentifiers) {
                                       if (!evaluatedIdentifiers.get(text)) {
                                           view.showError(translate(GuidedDecisionTableErraiConstants.MetaDataColumnPage_IsNotValidIdentifier));
                                           callback.callback(false);
                                       } else {
                                           view.hideError();
                                           callback.callback(true);
                                       }
                                   }
                               }).evaluateJavaIdentifiers(new String[] {text});
    }
}
