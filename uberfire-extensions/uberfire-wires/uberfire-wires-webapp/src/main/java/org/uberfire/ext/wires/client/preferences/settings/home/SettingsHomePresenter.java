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

package org.uberfire.ext.wires.client.preferences.settings.home;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.wires.client.preferences.settings.home.category.SettingsHomeCategoryPresenter;
import org.uberfire.ext.wires.client.preferences.settings.home.register.WorkbenchSettings;
import org.uberfire.workbench.events.NotificationEvent;

@WorkbenchScreen(identifier = SettingsHomePresenter.IDENTIFIER)
public class SettingsHomePresenter {

    public static final String IDENTIFIER = "PreferencesCentralHomePresenter";

    public interface View extends UberElement<SettingsHomePresenter> {

        void add( final SettingsHomeCategoryPresenter.View categoryView );
    }

    private final View view;

    private final WorkbenchSettings workbenchSettings;

    private final ManagedInstance<SettingsHomeCategoryPresenter> categoryPresenterProvider;

    private final Event<NotificationEvent> notification;

    @Inject
    public SettingsHomePresenter( final View view,
                                  final WorkbenchSettings workbenchSettings,
                                  final ManagedInstance<SettingsHomeCategoryPresenter> categoryPresenterProvider,
                                  final Event<NotificationEvent> notification ) {
        this.view = view;
        this.workbenchSettings = workbenchSettings;
        this.categoryPresenterProvider = categoryPresenterProvider;
        this.notification = notification;
    }

    @PostConstruct
    public void init() {
        view.init( this );

        workbenchSettings.getSettingsByCategory().forEach( ( category, settings ) -> {
            SettingsHomeCategoryPresenter categoryPresenter = categoryPresenterProvider.get();
            categoryPresenter.setup( settings );
            view.add( categoryPresenter.getView() );
        } );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Settings";
    }

    @WorkbenchPartView
    public View getView() {
        return view;
    }
}
