/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.screens.guided.rule.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import org.drools.workbench.screens.guided.rule.client.resources.i18n.Constants;
import org.kie.workbench.common.widgets.client.resources.ItemImages;
import org.drools.workbench.screens.guided.rule.client.resources.css.GuidedRuleEditorCss;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages;

/**
 * Resources for the Guided Rule Editor
 */
public interface GuidedRuleEditorResources extends ClientBundle {

    GuidedRuleEditorResources INSTANCE = GWT.create( GuidedRuleEditorResources.class );

    Constants CONSTANTS = GWT.create( Constants.class );

    ItemImages itemImages();

    @Source("css/GuidedRuleEditor.css")
    GuidedRuleEditorCss css();

    GuidedRuleEditorImages images();

}
