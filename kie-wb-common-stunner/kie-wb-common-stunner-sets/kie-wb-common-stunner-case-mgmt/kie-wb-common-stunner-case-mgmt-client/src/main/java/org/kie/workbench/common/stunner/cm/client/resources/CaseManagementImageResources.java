/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.cm.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;

public interface CaseManagementImageResources extends ClientBundleWithLookup {

    CaseManagementImageResources INSTANCE = GWT.create(CaseManagementImageResources.class);

    @Source("images/cmicon.png")
    ImageResource cmicon();

    @ClientBundle.Source("images/icons/stage_icon.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categoryStages();

    @ClientBundle.Source("images/icons/task_icon.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categoryTasks();

    @ClientBundle.Source("images/icons/subprocess_icon.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categorySubprocesses();

    @ClientBundle.Source("images/icons/subcase_icon.svg")
    @DataResource.MimeType("image/svg+xml")
    DataResource categorySubcases();

    @ClientBundle.Source("images/icons/stage_icon.png")
    ImageResource stage();

    @ClientBundle.Source("images/icons/task_icon.png")
    ImageResource task();

    @ClientBundle.Source("images/icons/subprocess_icon.png")
    ImageResource subprocess();

    @ClientBundle.Source("images/icons/subcase_icon.png")
    ImageResource subcase();
}
