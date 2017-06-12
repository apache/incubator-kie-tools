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
package org.kie.workbench.common.screens.library.client.widgets.library;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class ProjectItemWidget implements IsElement {

    @Inject
    @DataField
    Anchor projectName;

    @Inject
    @DataField
    Div projectListItem;

    @Inject
    @DataField
    Div projectListItemKebab;

    @Inject
    @DataField
    Div projectListItemText;

    private String project;

    public void init(final String project,
                     final Command details,
                     final Command select) {
        this.project = project;
        this.projectName.setTextContent(project);
        this.projectName.setOnclick(e -> {
            e.stopImmediatePropagation();
            select.execute();
        });
        projectListItem.setOnclick(e -> details.execute());
    }

    public String getProject() {
        return project;
    }

    public void select() {
        projectListItem.getClassList().add("selected");
    }

    public void unselect() {
        projectListItem.getClassList().remove("selected");
    }
}
