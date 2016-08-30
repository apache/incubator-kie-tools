/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.client.preferences.central;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.EventListener;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.common.client.dom.Window;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.AbstractWorkbenchScreenActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;

@Dependent
@Templated
public class PreferencesCentralNavbarView implements IsElement,
                                                     PreferencesCentralNavbarPresenter.View {

    private final TranslationService translationService;

    private PreferencesCentralNavbarPresenter presenter;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private PerspectiveManager perspectiveManager;

    @Inject
    @DataField
    Div navbar;

    @Inject
    @DataField
    UnorderedList menu;

    @Inject
    public PreferencesCentralNavbarView( final TranslationService translationService ) {
        super();
        this.translationService = translationService;
    }

    @Override
    public void init( final PreferencesCentralNavbarPresenter presenter ) {
        this.presenter = presenter;

        for ( AbstractWorkbenchScreenActivity preferencesActivity : presenter.getPreferencesActivitiesIterator() ) {
            final ListItem li = (ListItem) Window.getDocument().createElement( "li" );
            li.setTextContent( preferencesActivity.getTitle() );
            li.setOnclick( new EventListener() {
                @Override
                public void call( final Event event ) {
                    placeManager.goTo( preferencesActivity.getIdentifier() );
                }
            } );
            menu.appendChild( li );
        }
    }
}
