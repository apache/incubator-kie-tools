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

import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.AttributeColumnPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.BaseDecisionTableColumnPlugin;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

@Dependent
public class AttributeColumnPlugin extends BaseDecisionTableColumnPlugin {

    private AttributeColumnPage page;

    @Inject
    public AttributeColumnPlugin(final AttributeColumnPage page,
                                 final Event<WizardPageStatusChangeEvent> changeEvent,
                                 final TranslationService translationService) {
        super(changeEvent,
              translationService);

        this.page = page;
    }

    private String attribute;

    @Override
    public String getTitle() {
        return translate(GuidedDecisionTableErraiConstants.AttributeColumnPlugin_AddAttributeColumn);
    }

    @Override
    public List<WizardPage> getPages() {
        return new ArrayList<WizardPage>() {{
            add(page);
        }};
    }

    @Override
    public Boolean generateColumn() {
        presenter.appendColumn(getAttributeCol52());

        return true;
    }

    private AttributeCol52 getAttributeCol52() {
        final AttributeCol52 column = new AttributeCol52();

        column.setAttribute(attribute);

        return column;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;

        fireChangeEvent(page);
    }

    @Override
    public Type getType() {
        return Type.ADVANCED;
    }
}
