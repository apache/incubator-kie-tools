/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.rule.client.widget.attribute;

import java.util.Objects;
import java.util.stream.Stream;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HorizontalPanel;
import org.drools.workbench.models.datamodel.rule.RuleAttribute;
import org.drools.workbench.models.datamodel.rule.RuleMetadata;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.client.editor.AttributeSelectorPopup;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.gwtbootstrap3.client.ui.Button;
import org.uberfire.ext.widgets.common.client.common.InfoPopup;

public class GuidedRuleAttributeSelectorPopup extends AttributeSelectorPopup {

    private RuleModel model;
    private boolean lockLHS;
    private boolean lockRHS;
    private Command refresh;

    public void init(final RuleModel model,
                     final boolean lockLHS,
                     final boolean lockRHS,
                     final Command refresh) {
        this.model = model;
        this.lockLHS = lockLHS;
        this.lockRHS = lockRHS;
        this.refresh = refresh;

        initialize();

        setFreezePanel(lockLHS,
                       lockRHS);
    }

    @Override
    protected String[] getAttributes() {
        return RuleAttributeWidget.getAttributesList();
    }

    @Override
    protected String[] getReservedAttributes() {
        int size = model.attributes.length;
        String[] duplicates = new String[size];
        for (int i = 0; i < size; i++) {
            duplicates[i] = model.attributes[i].getAttributeName();
        }
        return duplicates;
    }

    @Override
    protected void handleAttributeAddition(final String attributeName) {
        if (attributeName.equals(RuleAttributeWidget.LOCK_LHS) || attributeName.equals(RuleAttributeWidget.LOCK_RHS)) {
            model.addMetadata(new RuleMetadata(attributeName,
                                               "true"));
        } else {
            model.addAttribute(new RuleAttribute(attributeName,
                                                 ""));
        }
        refresh.execute();
    }

    @Override
    protected boolean isMetadataUnique(final String metadataName) {
        return Stream.of(model.metadataList)
                .noneMatch(ruleMetadata -> Objects.equals(metadataName, ruleMetadata.getAttributeName()));
    }

    @Override
    protected String metadataNotUniqueMessage(final String metadataName) {
        return GuidedRuleEditorResources.CONSTANTS.MetadataNotUnique0(metadataName);
    }

    @Override
    protected void handleMetadataAddition(final String metadataName) {
        model.addMetadata(new RuleMetadata(metadataName,
                                           ""));
        refresh.execute();
    }

    private void setFreezePanel(final boolean lockLHS,
                                final boolean lockRHS) {
        HorizontalPanel hz = new HorizontalPanel();
        if (!lockLHS) {
            hz.add(createFreezeButton(GuidedRuleEditorResources.CONSTANTS.Conditions(),
                                      RuleAttributeWidget.LOCK_LHS));
        }
        if (!lockRHS) {
            hz.add(createFreezeButton(GuidedRuleEditorResources.CONSTANTS.Actions(),
                                      RuleAttributeWidget.LOCK_RHS));
        }
        hz.add(new InfoPopup(GuidedRuleEditorResources.CONSTANTS.FrozenAreas(),
                             GuidedRuleEditorResources.CONSTANTS.FrozenExplanation()));

        if (hz.getWidgetCount() > 1) {
            addAttribute(GuidedRuleEditorResources.CONSTANTS.FreezeAreasForEditing(),
                         hz);
        }
    }

    private Button createFreezeButton(final String text,
                                      final String metadataName) {
        return new Button(text,
                          (ClickEvent event) -> {
                              model.addMetadata(new RuleMetadata(metadataName,
                                                                 "true"));
                              refresh.execute();
                              hide();
                          });
    }
}
