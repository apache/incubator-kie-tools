/*
 * Copyright 2015 JBoss Inc
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

package org.drools.workbench.screens.guided.dtable.client.widget.table.cells;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKButton;

public class AnalysisPopup
        extends BaseModal {

    private final SimplePanel panel = new SimplePanel();

    public AnalysisPopup() {
        setTitle(GuidedDecisionTableConstants.INSTANCE.Analysis());

        add(panel);
        add(new ModalFooterOKButton(new Command() {
            @Override
            public void execute() {
                hide();
            }
        }));
    }

    public void setHTML(String html) {
        panel.clear();
        panel.setWidget(new HTMLPanel(html));
    }
}
