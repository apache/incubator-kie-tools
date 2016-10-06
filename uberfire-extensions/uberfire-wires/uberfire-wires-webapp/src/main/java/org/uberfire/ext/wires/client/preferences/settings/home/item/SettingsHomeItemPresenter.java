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

package org.uberfire.ext.wires.client.preferences.settings.home.item;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.preferences.shared.bean.PreferenceRootElement;
import org.uberfire.ext.wires.client.preferences.central.PreferencesCentralPerspective;
import org.uberfire.ext.wires.client.preferences.settings.home.register.SettingShortcut;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

public class SettingsHomeItemPresenter {

    public interface View extends UberElement<SettingsHomeItemPresenter> {

    }

    private final View view;

    private final PlaceManager placeManager;

    private SettingShortcut settingShortcut;

    @Inject
    public SettingsHomeItemPresenter( final View view,
                                      final PlaceManager placeManager ) {
        this.view = view;
        this.placeManager = placeManager;
    }

    public void setup( final SettingShortcut settingShortcut ) {
        this.settingShortcut = settingShortcut;
        view.init( this );
    }

    public void enter() {
        settingShortcut.getCommand().execute();
    }

    public SettingShortcut getSettingShortcut() {
        return settingShortcut;
    }

    public View getView() {
        return view;
    }
}
