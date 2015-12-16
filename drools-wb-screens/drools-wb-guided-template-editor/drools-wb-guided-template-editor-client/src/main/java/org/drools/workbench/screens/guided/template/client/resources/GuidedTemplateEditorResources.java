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

package org.drools.workbench.screens.guided.template.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import org.drools.workbench.screens.guided.template.client.resources.css.GuidedTemplateEditorCss;
import org.drools.workbench.screens.guided.template.client.resources.images.GuidedTemplateEditorImages;
import org.kie.workbench.common.widgets.client.resources.CollapseExpand;
import org.kie.workbench.common.widgets.client.resources.ItemImages;
import org.kie.workbench.common.widgets.decoratedgrid.client.resources.TableImageResources;

/**
 * Resources for the Guided Template Editor
 */
public interface GuidedTemplateEditorResources extends ClientBundle {

    GuidedTemplateEditorResources INSTANCE = GWT.create( GuidedTemplateEditorResources.class );

    TableImageResources tableImageResources();

    CollapseExpand collapseExpand();

    ItemImages itemImages();

    @Source("css/GuidedTemplateEditor.css")
    GuidedTemplateEditorCss css();

    GuidedTemplateEditorImages images();

}
