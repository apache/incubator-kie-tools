/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.scenariosimulation.webapp.client.editor;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.promise.Promise;
import org.drools.workbench.screens.scenariosimulation.kogito.client.editor.ScenarioSimulationEditorKogitoWrapper;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.mvp.AbstractActivity;
import org.uberfire.client.mvp.EditorActivity;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.ResourceType;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.bridge.Notification;

@ApplicationScoped
@Named(ScenarioSimulationEditorActivity.ID)
public class ScenarioSimulationEditorActivity extends AbstractActivity implements EditorActivity {
    public static final String ID = "ScenarioSimulationEditor";

    private static final String BASE_URI = "/asserts/";
    public static final String BASE_DMN_URI = BASE_URI + "dmn/";
    public static final Path DMN_PATH = PathFactory.newPath("DMN", BASE_DMN_URI);

    @Inject
    protected ScenarioSimulationEditorKogitoWrapper scenarioSimulationEditorKogitoWrapper;

    @Override
    public void onStartup(final PlaceRequest place) {
        super.onStartup(place);
        scenarioSimulationEditorKogitoWrapper.onStartup(place);
    }

    @Override
    public void onOpen() {
        super.onOpen();
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public IsWidget getWidget() {
        return scenarioSimulationEditorKogitoWrapper.getWidget();
    }

    @Override
    public Promise<Void> setContent(String path, String value) {
        return scenarioSimulationEditorKogitoWrapper.setContent(path, value);
    }

    @Override
    public Promise<String> getContent() {
        return scenarioSimulationEditorKogitoWrapper.getContent();
    }

    @Override
    public Promise<String> getPreview() {
        return null;
    }

    @Override
    public Promise<List<Notification>> validate() {
        return Promise.resolve(Collections.emptyList());
    }

    @Override
    public String getIdentifier() {
        return ID;
    }

    @Override
    public ResourceType getResourceType() {
        return ActivityResourceType.EDITOR;
    }

}
