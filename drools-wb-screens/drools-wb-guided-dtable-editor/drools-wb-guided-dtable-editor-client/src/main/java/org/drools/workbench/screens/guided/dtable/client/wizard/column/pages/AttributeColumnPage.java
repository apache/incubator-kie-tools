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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.BaseDecisionTableColumnPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.AttributeColumnPlugin;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.drools.workbench.screens.guided.rule.client.widget.attribute.RuleAttributeWidget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElement;

import static org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTableColumnViewUtils.nil;

@Dependent
public class AttributeColumnPage extends BaseDecisionTableColumnPage<AttributeColumnPlugin> {

    private View view;

    @Inject
    public AttributeColumnPage(final View view,
                               final TranslationService translationService) {
        super(translationService);

        this.view = view;
    }

    @Override
    public String getTitle() {
        return translate(GuidedDecisionTableErraiConstants.AttributeColumnPage_AddNewAttribute);
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        final boolean hasAttribute = !nil(plugin().getAttribute());

        callback.callback(hasAttribute);
    }

    @Override
    protected UberElement<?> getView() {
        return view;
    }

    @Override
    public void prepareView() {
        view.init(this);
        view.setupAttributeList(getAttributes());
    }

    List<String> getAttributes() {
        return removeAttributesAlreadyAdded(rawAttributes());
    }

    private List<String> rawAttributes() {
        return new ArrayList<String>() {{
            addAll(attributesWithoutChooseAttribute());
        }};
    }

    private List<String> attributesWithoutChooseAttribute() {
        final List<String> originalAttributes = Arrays.asList(RuleAttributeWidget.getAttributesList());
        final List<String> attributes = new ArrayList<>(originalAttributes);

        attributes.remove(GuidedRuleEditorResources.CONSTANTS.Choose());

        return attributes;
    }

    private List<String> removeAttributesAlreadyAdded(final List<String> attributeList) {
        final List<String> attributes = new ArrayList<>(attributeList);

        attributes.removeAll(presenter.getReservedAttributeNames());

        return attributes;
    }

    void selectItem(String selectedItemText) {
        plugin().setAttribute(selectedItemText);
    }

    String selectedAttribute() {
        return plugin().getAttribute();
    }

    public interface View extends UberElement<AttributeColumnPage> {

        void setupAttributeList(final List<String> attributes);

        boolean isAttributeDescriptionHidden();

        void hideAttributeDescription();

        void showAttributeDescription();
    }
}
