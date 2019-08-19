/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.rule.client.editor;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.kie.workbench.common.widgets.client.resources.CommonImages;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

public class MoveUpButton
        extends Image {

    public MoveUpButton(ClickHandler moveDownListener) {
        super(CommonImages.INSTANCE.shuffleUp());

        this.setAltText(CommonConstants.INSTANCE.MoveUp());
        this.setTitle(GuidedRuleEditorResources.CONSTANTS.MoveUp());
        this.addClickHandler(moveDownListener);
    }
}
