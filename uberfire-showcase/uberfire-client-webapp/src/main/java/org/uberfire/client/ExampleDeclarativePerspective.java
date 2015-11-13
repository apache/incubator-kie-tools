/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.util.Layouts;

import com.google.gwt.user.client.ui.FlowPanel;

@WorkbenchPerspective( identifier = "ExampleDeclarativePerspective", isTransient = true )
public class ExampleDeclarativePerspective extends FlowPanel {

    @Inject
    @WorkbenchPanel( parts = "HomeScreen" )
    FlowPanel theOnlyPanel;

    @PostConstruct
    void doLayout() {
        Layouts.setToFillParent( theOnlyPanel );
        add( theOnlyPanel );
    }
}
