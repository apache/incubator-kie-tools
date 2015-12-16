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

package org.drools.workbench.screens.guided.template.client.resources.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Images for Guided Rule Editor
 */
public interface GuidedTemplateEditorImages extends
                                            ClientBundle {

    @ClientBundle.Source("BPM_FileIcons_guidedrule.png")
    ImageResource typeGuidedRuleTemplate();

    @ClientBundle.Source("emptyArrow.png")
    ImageResource arrowSpacerIcon();

    @ClientBundle.Source("icon-unmerge.png")
    ImageResource toggleUnmergeIcon();

    @ClientBundle.Source("icon-merge.png")
    ImageResource toggleMergeIcon();

}
