/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.BaseDecisionTableColumnPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.MetaDataColumnPlugin;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.mvp.UberView;

import static org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTableColumnViewUtils.nil;

@Dependent
public class MetaDataColumnPage extends BaseDecisionTableColumnPage<MetaDataColumnPlugin> {

    private View view;

    @Inject
    public MetaDataColumnPage(final View view,
                              final TranslationService translationService) {
        super(translationService);

        this.view = view;
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
        final boolean hasMetaData = !nil(plugin().getMetaData());

        callback.callback(hasMetaData);
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

    public void emptyMetadataError() {
        view.showError(translate(GuidedDecisionTableErraiConstants.MetaDataColumnPage_MetadataNameEmpty));
    }

    public void columnNameIsAlreadyInUseError() {
        view.showError(translate(GuidedDecisionTableErraiConstants.MetaDataColumnPage_ThatColumnNameIsAlreadyInUsePleasePickAnother));
    }

    public interface View extends UberElement<MetaDataColumnPage> {

        void showError(String errorMessage);

        void clear();
    }
}
