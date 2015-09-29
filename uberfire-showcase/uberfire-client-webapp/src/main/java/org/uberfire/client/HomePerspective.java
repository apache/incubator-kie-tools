/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;

import com.google.gwt.user.client.ui.Composite;

@WorkbenchPerspective(identifier = "HomePerspective",
                      isDefault = true)
@Templated
public class HomePerspective extends Composite {

    @DataField
    @WorkbenchPanel(parts = "MoodScreen?uber=fire&uber1=fire1")
    WorkbenchPanelPanel moodScreen = new WorkbenchPanelPanel( 100 );

    @DataField
    @WorkbenchPanel(parts = "HomeScreen?uber=fire")
    WorkbenchPanelPanel homeScreen = new WorkbenchPanelPanel( 100 );

    @DataField
    @WorkbenchPanel(parts = "AnotherScreen")
    WorkbenchPanelPanel anotherScreen = new WorkbenchPanelPanel( 100 );

}
