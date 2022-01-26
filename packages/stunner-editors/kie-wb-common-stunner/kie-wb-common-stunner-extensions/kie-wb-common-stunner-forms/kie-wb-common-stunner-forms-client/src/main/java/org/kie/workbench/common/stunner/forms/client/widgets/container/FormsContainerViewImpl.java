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

package org.kie.workbench.common.stunner.forms.client.widgets.container;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.kie.workbench.common.stunner.forms.client.widgets.container.displayer.FormDisplayer;

@Templated
@Dependent
public class FormsContainerViewImpl implements FormsContainerView,
                                               IsElement {

    @Inject
    @DataField
    private HTMLDivElement content;

    @Override
    public void addDisplayer(FormDisplayer displayer) {
        content.appendChild(displayer.getElement());
    }

    @Override
    public void clear() {
        DOMUtil.removeAllChildren(content);
    }

    @Override
    public void removeDisplayer(FormDisplayer displayer) {
        DOMUtil.removeFromParent(displayer.getElement());
    }
}
