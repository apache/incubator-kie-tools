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
package org.kie.workbench.common.screens.library.client.widgets;

import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

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

    public void init( String projectName, Command details, Command select ) {
        this.projectName.setTextContent( projectName );
        this.projectName.setOnclick( e -> {
            e.stopImmediatePropagation();
            select.execute();
        } );
        projectListItem.setOnclick( e -> details.execute() );
    }
}
