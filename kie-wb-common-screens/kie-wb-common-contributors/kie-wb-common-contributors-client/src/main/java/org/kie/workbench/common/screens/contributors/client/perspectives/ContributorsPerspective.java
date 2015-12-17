/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.contributors.client.perspectives;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.util.Layouts;

/**
 * This perspective display some indicators about the commit activity around the available GIT repositories
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "ContributorsPerspective")
public class ContributorsPerspective extends FlowPanel {

    @Inject
    @WorkbenchPanel(parts = "ContributorsScreen")
    FlowPanel contributors;

    @PostConstruct
    void doLayout() {
        Layouts.setToFillParent( contributors );
        add( contributors );
    }
}
