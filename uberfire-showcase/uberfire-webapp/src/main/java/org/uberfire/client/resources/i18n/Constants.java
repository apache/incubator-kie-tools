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

package org.uberfire.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * This uses GWT to provide client side compile time resolving of locales. See:
 * http://code.google.com/docreader/#p=google-web-toolkit-doc-1-5&s=google-web-
 * toolkit-doc-1-5&t=DevGuideInternationalization (for more information).
 * <p>
 * Each method name matches up with a key in Constants.properties (the
 * properties file can still be used on the server). To use this, use
 * <code>GWT.create(Constants.class)</code>.
 */
public interface Constants
        extends
        Messages {

    Constants INSTANCE = GWT.create(Constants.class);

    String SignOut();

    String WelcomeUser();

    String experimental_perspective();

    String experimental_perspective_description();

    String experimental_asset_explorer();

    String experimental_asset_explorer_description();

    String experimental_asset_editor();

    String experimental_asset_editor_description();

    String experimental_features_editor();

    String admin_perspective();

    String apps_perspective();

    String multi_screem_perspective();

    String plugin_authoring();

    String preferences_perspective();

    String simple_perspective();

    String standalone_editor_perspective();

    String global_feature1();

    String global_feature2();

    String global_feature3();

    String global_feature_description();

    String group1();

    String group1_feature1();

    String group1_feature2();

    String group1_feature3();

    String group2();

    String group2_feature1();

    String group3();

    String group3_feature1();

    String group3_feature2();

    String group3_feature3();

    String experimental_feature_description();

    String experimental_asset_editor_action();

    String experimental_asset_editor_action2();

    String experimental_asset_editor_action_description();

    @Key("experimental_asset_editor_actions.experimental")
    String experimental_asset_editor_actionsExperimental();

    @Key("experimental_asset_editor_actions.experimental2")
    String experimental_asset_editor_actionsExperimental2();

    @Key("experimental_asset_editor_actions.experimentalText")
    String experimental_asset_editor_actionsExperimentalText();

    @Key("experimental_asset_editor_actions.experimental2Text")
    String experimental_asset_editor_actionsExperimental2Text();

    @Key("experimental_asset_explorer_actions.add")
    String experimental_asset_explorer_actionsAdd();

    @Key("ExperimentalExplorerViewImpl.emptyTitle")
    String ExperimentalExplorerViewImplEmptyTitle();

    @Key("ExperimentalExplorerViewImpl.emptyText")
    String ExperimentalExplorerViewImplEmptyText();

    @Key("ExperimentalExplorerViewImpl.add")
    String ExperimentalExplorerViewImplAdd();
}
