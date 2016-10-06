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

package org.uberfire.ext.wires.client.preferences.settings.home.category;

import java.util.List;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.preferences.shared.bean.PreferenceRootElement;
import org.uberfire.ext.wires.client.preferences.settings.home.item.SettingsHomeItemPresenter;
import org.uberfire.ext.wires.client.preferences.settings.home.register.SettingShortcut;

public class SettingsHomeCategoryPresenter {

    public interface View extends UberElement<SettingsHomeCategoryPresenter> {

        void add( SettingsHomeItemPresenter.View rootItemView );
    }

    private final View view;

    private final ManagedInstance<SettingsHomeItemPresenter> settingsHomeItemPresenterProvider;

    @Inject
    public SettingsHomeCategoryPresenter( final View view,
                                          final ManagedInstance<SettingsHomeItemPresenter> settingsHomeItemPresenterProvider ) {
        this.view = view;
        this.settingsHomeItemPresenterProvider = settingsHomeItemPresenterProvider;
    }

    public void setup( final List<SettingShortcut> settingsShortcuts ) {
        settingsShortcuts.forEach( settingShortcut -> {
            final SettingsHomeItemPresenter itemPresenter = settingsHomeItemPresenterProvider.get();
            itemPresenter.setup( settingShortcut );
            view.add( itemPresenter.getView() );
        });
    }

    public View getView() {
        return view;
    }
}
