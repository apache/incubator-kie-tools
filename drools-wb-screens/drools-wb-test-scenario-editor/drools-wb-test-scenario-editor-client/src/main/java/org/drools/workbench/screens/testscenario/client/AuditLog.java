/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.testscenario.client;

import java.util.Set;

import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;

public class AuditLog
        extends AccordionGroup {

    public AuditLog() {

        setIcon(IconType.CERTIFICATE);
        setHeading(TestScenarioConstants.INSTANCE.AuditLogColon());
        setDefaultOpen(false);
        getElement().getStyle().setMarginTop(2, Style.Unit.PX);
        getElement().getStyle().setMarginBottom(2, Style.Unit.PX);

    }

    public void fill(Set<String> log) {
        clear();
        setVisible(true);
        VerticalPanel list = new VerticalPanel();

        for (String line : log) {
            list.add(new Line(line));
        }

        ScrollPanel scrollPanel = new ScrollPanel(list);
        scrollPanel.setHeight("300px");
        add(scrollPanel);
    }

}
