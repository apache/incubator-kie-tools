/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.testscenario.service;

import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.model.TestScenarioModelContent;
import org.drools.workbench.screens.testscenario.model.TestScenarioResult;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsCreate;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRead;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;

/**
 * Service definition for Globals editor
 */
@Remote
public interface ScenarioTestEditorService
        extends
        SupportsCreate<Scenario>,
        SupportsRead<Scenario>,
        SupportsSaveAndRename<Scenario, Metadata>,
        SupportsDelete,
        SupportsCopy {

    public static final String TEST_SCENARIO_EDITOR_SETTINGS = "test-scenario-editor-settings";
    public static final String TEST_SCENARIO_EDITOR_MAX_RULE_FIRINGS = "max-rule-firings";

    TestScenarioModelContent loadContent(final Path path);

    TestScenarioResult runScenario(final String userName,
                                   final Path path,
                                   final Scenario scenario);
}
