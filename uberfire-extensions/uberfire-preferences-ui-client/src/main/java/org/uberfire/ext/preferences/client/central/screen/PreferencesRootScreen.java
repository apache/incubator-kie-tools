/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.preferences.client.central.screen;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.preferences.client.central.PreferencesCentralNavBarScreen;
import org.uberfire.ext.preferences.client.central.actions.PreferencesCentralActionsScreen;
import org.uberfire.ext.preferences.client.central.form.DefaultPreferenceForm;
import org.uberfire.ext.preferences.client.event.HierarchyItemFormInitializationEvent;
import org.uberfire.ext.preferences.client.event.HierarchyItemSelectedEvent;
import org.uberfire.ext.preferences.client.utils.PreferenceFormBeansInfo;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@WorkbenchScreen(identifier = PreferencesRootScreen.IDENTIFIER)
public class PreferencesRootScreen {

    public static final String IDENTIFIER = "PreferencesRootScreen";

    public interface View extends UberElement<PreferencesRootScreen> {

        HTMLElement getNavbarContainer();

        HTMLElement getEditorContainer();

        HTMLElement getActionsContainer();

        String getTranslation(String key);
    }

    private final View view;

    private final PlaceManager placeManager;

    private final PreferenceFormBeansInfo preferenceFormBeansInfo;

    private final Event<HierarchyItemFormInitializationEvent> hierarchyItemFormInitializationEvent;

    private PlaceRequest openedPreferencePlaceRequest;

    @Inject
    public PreferencesRootScreen(final View view,
                                 final PlaceManager placeManager,
                                 final PreferenceFormBeansInfo preferenceFormBeansInfo,
                                 final Event<HierarchyItemFormInitializationEvent> hierarchyItemFormInitializationEvent) {
        this.view = view;
        this.placeManager = placeManager;
        this.preferenceFormBeansInfo = preferenceFormBeansInfo;
        this.hierarchyItemFormInitializationEvent = hierarchyItemFormInitializationEvent;
    }

    @OnStartup
    public void setup() {
        placeManager.goTo(new DefaultPlaceRequest(PreferencesCentralNavBarScreen.IDENTIFIER),
                          view.getNavbarContainer());
        placeManager.goTo(new DefaultPlaceRequest(PreferencesCentralActionsScreen.IDENTIFIER),
                          view.getActionsContainer());
    }

    @OnClose
    public void onClose() {
        placeManager.closePlace(PreferencesCentralNavBarScreen.IDENTIFIER);
        placeManager.closePlace(PreferencesCentralActionsScreen.IDENTIFIER);
    }

    public void hierarchyItemSelectedEvent(@Observes HierarchyItemSelectedEvent hierarchyItemSelectedEvent) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id",
                       hierarchyItemSelectedEvent.getItemId());
        parameters.put("title",
                       view.getTranslation(hierarchyItemSelectedEvent.getPreference().bundleKey()));

        final HTMLElement editorContainer = view.getEditorContainer();

        if (openedPreferencePlaceRequest != null) {
            placeManager.closePlace(openedPreferencePlaceRequest);
        }
        openedPreferencePlaceRequest = new DefaultPlaceRequest(getPreferenceFormIdentifier(hierarchyItemSelectedEvent.getPreferenceIdentifier()),
                                                               parameters);
        placeManager.goTo(openedPreferencePlaceRequest,
                          editorContainer);

        final HierarchyItemFormInitializationEvent event = new HierarchyItemFormInitializationEvent(hierarchyItemSelectedEvent.getHierarchyElement());
        hierarchyItemFormInitializationEvent.fire(event);
    }

    public String getPreferenceFormIdentifier(final String preferenceIdentifier) {
        final String customForm = preferenceFormBeansInfo.getPreferenceFormFor(preferenceIdentifier);
        return customForm != null ? customForm : DefaultPreferenceForm.IDENTIFIER;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Preferences Root Screen";
    }

    @WorkbenchPartView
    public View getView() {
        return view;
    }
}
