/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.client.perspective.workspace;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.drools.guvnor.client.mvp.ExplorerViewCenterPanel;
import org.drools.guvnor.client.workbench.PositionSelectorPopup.Position;
import org.drools.guvnor.client.workbench.Workbench;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class WorkspacePerspectiveViewImpl extends Composite
    implements
    WorkspacePerspectivePresenter.MyView {

    @Inject
    private UiBinder<Widget, WorkspacePerspectiveViewImpl> uiBinder;

    @Inject
    private IOCBeanManager                                 manager;

    @UiField
    public SpanElement                                     userName;

    @UiField
    public HTMLPanel                                       titlePanel;

    @UiField
    public HTMLPanel                                       footerPanel;

    //@UiField(provided = true) MultiContentPanel contentPanel;

    @UiField(provided = true)
    public ExplorerViewCenterPanel                         tabbedPanel;

    @UiField(provided = true)
    public NavigationPanel                                 navPanel;

    public Workbench                                       workbench;

    @PostConstruct
    public void init() {
        final Workbench workbench = new Workbench();

        initWidget( workbench );

        //this.navPanel = manager.lookupBean( NavigationPanel.class ).getInstance();
        this.workbench = manager.lookupBean( Workbench.class ).getInstance();
        this.workbench.addRootPanel( "Navigation",
                                     Position.WEST,
                                     new Label( "Nav panel" ) );

        //this.contentPanel = manager.lookupBean(MultiContentPanel.class).getInstance();
        //this.tabbedPanel = manager.lookupBean(ExplorerViewCenterPanel.class).getInstance();
        //initWidget(uiBinder.createAndBindUi(this));
    }

    public void setUserName(String userName) {
        this.userName.setInnerText( userName );
    }

    /*
     * @Override public MultiContentPanel getContentPanel() { return
     * contentPanel; }
     */
    @Override
    public ExplorerViewCenterPanel getTabbedPanel() {
        return tabbedPanel;
    }
}
