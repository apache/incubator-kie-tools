/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.server.management.client.navigation;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.LinkedGroup;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.server.management.client.widget.CustomGroupItem;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class ServerNavigationView extends Composite
        implements ServerNavigationPresenter.View {

    private ServerNavigationPresenter presenter;

    private TranslationService translationService;

    @DataField
    Element title = DOM.createElement( "strong" );

    @Inject
    @DataField("new-server-template-button")
    Button newServerTemplate;

    @Inject
    @DataField("refresh-template-list-icon")
    Icon refresh;

    @Inject
    @DataField("template-server-list-group")
    LinkedGroup serverTemplates;

    private final Map<String, CustomGroupItem> idItem = new HashMap<String, CustomGroupItem>();

    private CustomGroupItem selected = null;

    @Inject
    public ServerNavigationView( final TranslationService translationService ) {
        super();
        this.translationService = translationService;
    }

    @Override
    public void init( final ServerNavigationPresenter presenter ) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void init() {
        title.setInnerText( getTitleText() );
    }

    @EventHandler("new-server-template-button")
    public void addTemplate( final ClickEvent event ) {
        presenter.newTemplate();
    }

    @EventHandler("refresh-template-list-icon")
    public void refresh( final ClickEvent event ) {
        presenter.refresh();
    }

    @Override
    public void addTemplate( final String id,
                             final String name ) {
        final CustomGroupItem template = new CustomGroupItem( name,
                                                              IconType.FOLDER_O,
                                                              new Command() {
                                                                  @Override
                                                                  public void execute() {
                                                                      presenter.select( id );
                                                                  }
                                                              } );

        idItem.put( id, template );

        serverTemplates.add( template );
    }

    @Override
    public void select( final String id ) {
        if ( selected != null ) {
            selected.setActive( false );
            selected.removeStyleName( "active" );
        }
        selected = idItem.get( id );
        selected.setActive( true );
    }

    @Override
    public void clean() {
        serverTemplates.clear();
        selected = null;
        serverTemplates.clear();
    }

    private String getTitleText() {
        return translationService.format( Constants.ServerNavigationView_TitleText );
    }
}
