/**
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Tab;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.widgets.droolsdomain.DroolsDomainEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.JPADomainEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.maindomain.MainDomainEditor;
import org.kie.workbench.common.services.datamodeller.core.DataModel;

public class DomainEditorContainer extends Composite {

    interface DomainEditorContainerUIBinder
            extends UiBinder<Widget, DomainEditorContainer> {

    }

    private static DomainEditorContainerUIBinder uiBinder = GWT.create(DomainEditorContainerUIBinder.class);

    @UiField
    SimplePanel mainPanel;

    DeckPanel deck = new DeckPanel();

    private Tab mainTab = new Tab();

    private Tab droolsTab = new Tab();

    public static int MAIN_DOMAIN = 0;

    public static int DROOLS_DOMAIN = 1;

    public static final int JPA_DOMAIN = 2;

    @Inject
    private MainDomainEditor mainDomainEditor;

    @Inject
    private DroolsDomainEditor droolsDomainEditor;

    @Inject
    private JPADomainEditor jpaDomainEditor;

    private DataModelerContext context;

    public DomainEditorContainer() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @PostConstruct
    private void init() {

        mainPanel.add( deck );
        deck.add( mainDomainEditor );
        deck.add( droolsDomainEditor );
        deck.add( jpaDomainEditor );

        showDomain( MAIN_DOMAIN );

    }

    public DataModelerContext getContext() {
        return context;
    }

    private DataModel getDataModel() {
        return getContext() != null ? getContext().getDataModel() : null;
    }

    public void setContext(DataModelerContext context) {
        this.context = context;
        mainDomainEditor.setContext( context );
        droolsDomainEditor.setContext( context );
        jpaDomainEditor.setContext( context );
    }

    public void showDomain( int domainId ) {
        deck.showWidget( domainId );
    }
}