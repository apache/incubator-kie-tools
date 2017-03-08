/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.wbtest.client.resize;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.util.Layouts;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;

@Dependent
@Named("org.uberfire.wbtest.client.resize.ResizeTestScreenActivity")
public class ResizeTestScreenActivity extends AbstractTestScreenActivity {

    private ResizeTestWidget widget;

    @Inject
    public ResizeTestScreenActivity(PlaceManager pm) {
        super(pm);
    }

    @Override
    public void onStartup(PlaceRequest place) {
        super.onStartup(place);
        String id = place.getParameter("debugId",
                                       "");
        widget = new ResizeTestWidget(id);
        Layouts.setToFillParent(widget);
    }

    @Override
    public IsWidget getWidget() {
        return widget;
    }
}
