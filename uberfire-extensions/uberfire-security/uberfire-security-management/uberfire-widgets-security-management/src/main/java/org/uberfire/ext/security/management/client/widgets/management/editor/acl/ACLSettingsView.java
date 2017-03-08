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

package org.uberfire.ext.security.management.client.widgets.management.editor.acl;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;

@Dependent
@Templated
public class ACLSettingsView extends Composite
        implements ACLSettings.View {

    @DataField
    LabelElement homePerspectiveLabel = Document.get().createLabelElement();
    @Inject
    @DataField
    Span homePerspectiveName;
    @Inject
    @DataField
    FlowPanel homePerspectiveSelectorPanel;
    @Inject
    @DataField
    Span homePerspectiveHelp;
    @DataField
    LabelElement priorityLabel = Document.get().createLabelElement();
    @Inject
    @DataField
    Span priorityValue;
    @Inject
    @DataField
    FlowPanel prioritySelectorPanel;
    @Inject
    @DataField
    Span priorityHelp;
    private ACLSettings presenter;

    @Override
    public void init(ACLSettings presenter) {
        this.presenter = presenter;
        homePerspectiveLabel.setInnerText(UsersManagementWidgetsConstants.INSTANCE.homePerspective());
        homePerspectiveHelp.setAttribute("title",
                                         UsersManagementWidgetsConstants.INSTANCE.homePerspectiveTooltip());
        priorityLabel.setInnerText(UsersManagementWidgetsConstants.INSTANCE.priority());
        priorityHelp.setAttribute("title",
                                  UsersManagementWidgetsConstants.INSTANCE.priorityTooltip());
    }

    @Override
    public ACLSettings.View setHomePerspectiveName(String name) {
        homePerspectiveName.setTextContent("\"" + name + "\"");
        return this;
    }

    @Override
    public ACLSettings.View setHomePerspectiveTitle(String name) {
        homePerspectiveName.setTitle(name);
        return this;
    }

    @Override
    public ACLSettings.View setNoHomePerspectiveDefined() {
        String undefined = UsersManagementWidgetsConstants.INSTANCE.noHomePerspective();
        homePerspectiveName.setTextContent(undefined);
        return this;
    }

    @Override
    public ACLSettings.View setHomePerspectiveSelector(IsWidget widget) {
        homePerspectiveSelectorPanel.clear();
        homePerspectiveSelectorPanel.add(widget);
        return this;
    }

    @Override
    public ACLSettings.View setHomePerspectiveSelectorEnabled(boolean enabled) {
        homePerspectiveSelectorPanel.setVisible(enabled);
        if (enabled) {
            homePerspectiveName.getStyle().setProperty("display",
                                                       "none");
        } else {
            homePerspectiveName.getStyle().removeProperty("display");
        }
        return this;
    }

    @Override
    public ACLSettings.View setPriorityName(String name) {
        priorityValue.setTextContent("\"" + name + "\"");
        return this;
    }

    @Override
    public ACLSettings.View setPrioritySelector(IsWidget widget) {
        prioritySelectorPanel.clear();
        prioritySelectorPanel.add(widget);
        return this;
    }

    @Override
    public ACLSettings.View setPrioritySelectorEnabled(boolean enabled) {
        prioritySelectorPanel.setVisible(enabled);
        if (enabled) {
            priorityValue.getStyle().setProperty("display",
                                                 "none");
        } else {
            priorityValue.getStyle().removeProperty("display");
        }
        return this;
    }
}
