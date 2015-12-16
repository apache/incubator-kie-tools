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

package org.drools.workbench.screens.guided.dtable.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import org.drools.workbench.screens.guided.dtable.client.resources.css.AnalysisCssResources;
import org.drools.workbench.screens.guided.dtable.client.resources.css.CssResources;
import org.drools.workbench.screens.guided.dtable.client.resources.images.GuidedDecisionTableImageResources;
import org.kie.workbench.common.widgets.decoratedgrid.client.resources.TableImageResources;
import org.kie.workbench.common.widgets.client.resources.CollapseExpand;
import org.kie.workbench.common.widgets.client.resources.ItemImages;

/**
 * General Decision Table resources.
 */
public interface GuidedDecisionTableResources
        extends
        ClientBundle {

    GuidedDecisionTableResources INSTANCE = GWT.create( GuidedDecisionTableResources.class );

    TableImageResources tableImageResources();

    CollapseExpand collapseExpand();

    ItemImages itemImages();

    @Source("css/DecisionTable.css")
    CssResources css();

    @Source("css/Analysis.css")
    AnalysisCssResources analysisCss();

    GuidedDecisionTableImageResources images();

};
