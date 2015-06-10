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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;

public class DomainEditorContainer extends Composite {

    interface DomainEditorContainerUIBinder
            extends UiBinder<Widget, DomainEditorContainer> {

    }

    private static DomainEditorContainerUIBinder uiBinder = GWT.create(DomainEditorContainerUIBinder.class);

    @UiField
    SimplePanel mainPanel;

    DeckPanel deck = new DeckPanel();

    @Inject
    private DomainEditorRegistry domainEditorRegistry;

    private DataModelerContext context;

    private List<DomainEditor> domainEditors;

    private List<String> instantiatedDomains = new ArrayList<String>(  );

    private Map<String, Integer> domainEditorIndex = new HashMap<String, Integer>( );

    public DomainEditorContainer() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @PostConstruct
    private void init() {

        mainPanel.add( deck );
        this.domainEditors = domainEditorRegistry.getNewDomainEditorInstances();
        int index = 0;
        for ( DomainEditor domainEditor : domainEditors ) {
            deck.add( domainEditor.getWidget() );
            domainEditorIndex.put( domainEditor.getName(), new Integer( index ) );
            instantiatedDomains.add( domainEditor.getName() );
            index++;
        }

        showDomain( 0 );
    }

    public DataModelerContext getContext() {
        return context;
    }

    public void setContext(DataModelerContext context) {
        this.context = context;
        for ( DomainEditor domainEditor : domainEditors ) {
            domainEditor.setContext( context );
        }
    }

    public void showDomain( String domainName ) {
        Integer index;
        if ( ( index = domainEditorIndex.get( domainName ) ) != null ) {
            showDomain( index );
        }
    }

    public List<String> getInstantiatedDomains() {
        return instantiatedDomains;
    }

    private void showDomain( int domainId ) {
        if ( deck.getWidgetCount() > 0 ) {
            deck.showWidget( domainId );
        }
    }

}