/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.experimental.client.editor.group;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditor;

public class TestExperimentalFeaturesGroup extends ExperimentalFeaturesGroup {

    public TestExperimentalFeaturesGroup(ExperimentalFeaturesGroupView view, TranslationService translationService, ManagedInstance<ExperimentalFeatureEditor> editorInstance) {
        super(view, translationService, editorInstance);
    }

    public String getLabelKey() {
        return labelKey;
    }
}
