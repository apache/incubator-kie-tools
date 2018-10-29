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

package org.uberfire.experimental.client.resources.i18n;

import org.jboss.errai.ui.shared.api.annotations.TranslationKey;

public interface UberfireExperimentalConstants {

    @TranslationKey(defaultValue = "")
    String experimentalFeaturesTitle = "ExperimentalFeaturesEditorScreenViewImpl.experimentalFeaturesTitle";

    @TranslationKey(defaultValue = "")
    String disabledExperimentalFeature = "DisabledExperimentalFeature";

    @TranslationKey(defaultValue = "")
    String disabledGlobalExperimentalFeature = "DisabledGlobalExperimentalFeature";

    @TranslationKey(defaultValue = "")
    String disabledFeatureTitle = "DisabledFeatureTitle";

    @TranslationKey(defaultValue = "")
    String experimentalFeaturesGeneralGroupKey = "experimentalFeatures.generalGroup";

    @TranslationKey(defaultValue = "")
    String experimentalFeaturesGlobalGroupKey = "experimentalFeatures.globalGroup";

    @TranslationKey(defaultValue = "")
    String ExperimentalFeaturesGroupEnableAll = "ExperimentalFeaturesGroup.enableAll";

    @TranslationKey(defaultValue = "")
    String ExperimentalFeaturesGroupDisableAll = "ExperimentalFeaturesGroup.disableAll";

    @TranslationKey(defaultValue = "")
    String GlobalExperimentalFeatures = "experimentalFeatures.global";

    @TranslationKey(defaultValue = "")
    String GlobalExperimentalFeaturesHelp = "experimentalFeatures.globalHelp";

    @TranslationKey(defaultValue = "")
    String GlobalExperimentalFeaturesEdit = "experimentalFeatures.globalEdit";
}
