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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.MetaDataColumnPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.BaseDecisionTableColumnPlugin;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

@Dependent
public class MetaDataColumnPlugin extends BaseDecisionTableColumnPlugin {

    private MetaDataColumnPage page;

    private String metaData;

    @Inject
    public MetaDataColumnPlugin(final MetaDataColumnPage page,
                                final Event<WizardPageStatusChangeEvent> changeEvent,
                                final TranslationService translationService) {
        super(changeEvent,
              translationService);

        this.page = page;
    }

    @Override
    public String getTitle() {
        return translate(GuidedDecisionTableErraiConstants.MetaDataColumnPlugin_AddMetadataColumn);
    }

    @Override
    public List<WizardPage> getPages() {
        return new ArrayList<WizardPage>() {{
            add(page);
        }};
    }

    @Override
    public Boolean generateColumn() {
        presenter.appendColumn(metadataColumn());

        return true;
    }

    @Override
    public Type getType() {
        return Type.ADVANCED;
    }

    private MetadataCol52 metadataColumn() {
        final MetadataCol52 column = new MetadataCol52();

        column.setMetadata(metaData);
        column.setHideColumn(true);

        return column;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;

        fireChangeEvent(page);
    }
}
