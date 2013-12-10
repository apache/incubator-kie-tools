/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.screens.guided.dtable.client.resources.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface GuidedDecisionTableImageResources
        extends
        ClientBundle {

    @Source("delete_item_small.gif")
    ImageResource deleteItemSmall();

    @Source("emptyArrow.png")
    ImageResource arrowSpacerIcon();

    @Source("icon-unmerge.png")
    ImageResource toggleUnmergeIcon();

    @Source("icon-merge.png")
    ImageResource toggleMergeIcon();

    @Source("edit.gif")
    ImageResource edit();

    @Source("editDisabled.gif")
    ImageResource editDisabled();

    @Source("error.gif")
    ImageResource error();

    @Source("information.gif")
    ImageResource information();

    @Source("warning.gif")
    ImageResource warning();

    @Source("config.png")
    ImageResource config();

    @Source("BPM_FileIcons_guideddtable.png")
    ImageResource typeGuidedDecisionTable();

    @ClientBundle.Source("shuffle_down.gif")
    ImageResource shuffleDown();

    @ClientBundle.Source("shuffle_up.gif")
    ImageResource shuffleUp();

}
