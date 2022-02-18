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

package org.kie.workbench.common.widgets.client.popups.launcher;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.widgets.client.popups.launcher.events.AppLauncherAddEvent;
import org.kie.workbench.common.widgets.client.popups.launcher.events.AppLauncherRemoveEvent;
import org.kie.workbench.common.widgets.client.popups.launcher.events.AppLauncherUpdatedEvent;

@ApplicationScoped
public class AppLauncherPresenter {

    @Inject
    private AppLauncherView view;

    @Inject
    private Event<AppLauncherUpdatedEvent> updatedEvent;

    private List<AppLauncherAddEvent> appLauncherAddEvents = new ArrayList<>();

    public IsWidget getView() {
        return view;
    }

    public void onAppLauncherRemoveEvent(@Observes final AppLauncherRemoveEvent event) {
        appLauncherAddEvents.stream().filter(e -> e.getAppName().equals(event.getAppName())).findFirst().ifPresent(e -> appLauncherAddEvents.remove(e));
        view.removeAllAppLauncher();
        appLauncherAddEvents.stream().forEach(e -> view.addAppLauncher(e.getAppName(), e.getUrl(), e.getIconClass()));
        updatedEvent.fire(new AppLauncherUpdatedEvent());
    }

    public void onAppLauncherAddEvent(@Observes final AppLauncherAddEvent event) {
        appLauncherAddEvents.add(event);
        view.addAppLauncher(event.getAppName(), event.getUrl(), event.getIconClass());
        updatedEvent.fire(new AppLauncherUpdatedEvent());
    }

    public boolean isAppLauncherEmpty() {
        return appLauncherAddEvents.isEmpty();
    }

    public interface AppLauncherView extends IsWidget {

        void addAppLauncher(String name, String url, String iconClass);

        void removeAllAppLauncher();

    }

}