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

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.ResourceType;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.SplashScreenFilter;

public interface SplashScreenActivity extends Activity {

    void closeIfOpen();

    void forceShow();

    String getTitle();

    IsWidget getTitleDecoration();

    IsWidget getWidget();

    Integer getBodyHeight();

    SplashScreenFilter getFilter();

    Boolean intercept(final PlaceRequest intercepted);

    boolean isEnabled();

    @Override
    default ResourceType getResourceType() {
        return ActivityResourceType.SPLASH;
    }
}
