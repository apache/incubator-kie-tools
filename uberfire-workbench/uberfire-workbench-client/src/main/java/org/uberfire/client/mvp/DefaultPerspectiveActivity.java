/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

public abstract class DefaultPerspectiveActivity extends AbstractActivity implements PerspectiveActivity {

    public static final String DEFAULT_PERSPECTIVE_NAME = "AuthoringPerspective";

    public abstract String getPlaceRequestIdentifier();

    @Override
    public PerspectiveDefinition getDefaultPerspectiveLayout() {
        final PerspectiveDefinition perspective = new PerspectiveDefinitionImpl();
        perspective.getRoot().addPart(new PartDefinitionImpl(new DefaultPlaceRequest(getPlaceRequestIdentifier())));
        return perspective;
    }

    @Override
    public String getIdentifier() {
        return DEFAULT_PERSPECTIVE_NAME;
    }

    @Override
    public IsWidget getWidget() {
        return null;
    }
}
