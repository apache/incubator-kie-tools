/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.mvp;

import java.util.Set;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ioc.client.api.EnabledByProperty;
import org.uberfire.mvp.PlaceRequest;

/**
 * This {@link ActivityManager} implementation is active for plugins only, to
 * satisfy compile-time dependencies (of other components on the plugin's
 * classpath that require an activity manager). Plugins don't get access to the
 * main application's {@link ActivityManager} as it would allow them to control
 * all activities of the application, not just their own.
 */
@ApplicationScoped
@EnabledByProperty(value = "uberfire.plugin.mode.active")
public class PluginActivityManagerImpl implements ActivityManager {

    private void fail() {
        // Plugins should not be able to interact with the activity manager.
        throw new RuntimeException("Invalid use of ActivityManager in plugin.");
    }

    @Override
    public <T extends Activity> Set<T> getActivities(final Class<T> clazz) {
        fail();
        return null;
    }

    @Override
    public SplashScreenActivity getSplashScreenInterceptor(final PlaceRequest placeRequest) {
        fail();
        return null;
    }

    @Override
    public Set<Activity> getActivities(final PlaceRequest placeRequest) {
        fail();
        return null;
    }

    @Override
    public boolean containsActivity(final PlaceRequest placeRequest) {
        fail();
        return false;
    }

    @Override
    public Activity getActivity(final PlaceRequest placeRequest) {
        fail();
        return null;
    }

    @Override
    public <T extends Activity> T getActivity(final Class<T> clazz,
                                              final PlaceRequest placeRequest) {
        fail();
        return null;
    }

    @Override
    public void destroyActivity(final Activity activity) {
        fail();
    }

    @Override
    public boolean isStarted(final Activity activity) {
        fail();
        return false;
    }

    @Override
    public Set<Activity> getActivities(PlaceRequest placeRequest,
                                       boolean secure) {
        fail();
        return null;
    }

    @Override
    public Activity getActivity(PlaceRequest placeRequest,
                                boolean secure) {
        fail();
        return null;
    }

    @Override
    public <T extends Activity> T getActivity(Class<T> clazz,
                                              PlaceRequest placeRequest,
                                              boolean secure) {
        fail();
        return null;
    }
}
