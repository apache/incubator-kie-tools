/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.wbtest.client.resize;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.util.Layouts;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;

@Dependent
@Named("org.uberfire.wbtest.client.resize.OverflowTestScreen")
public class OverflowTestScreen extends AbstractTestScreenActivity {

    private final Panel panel = new VerticalPanel();

    @Inject
    public OverflowTestScreen(PlaceManager placeManager) {
        super(placeManager);

        Button dumpHierarchyButton = new Button("Dump Layout Hierarchy to System.out");
        dumpHierarchyButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                System.out.println(Layouts.getContainmentHierarchy(panel,
                                                                   true));
            }
        });
        panel.add(dumpHierarchyButton);

        for (int i = 0; i < 100; i++) {
            panel.add(new Label("Filler row " + i));
        }
    }

    @Override
    public IsWidget getWidget() {
        return panel;
    }
}
