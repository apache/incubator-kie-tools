/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.client.widget.pipeline;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class PipelineView
        implements org.jboss.errai.ui.client.local.api.IsElement,
                   PipelinePresenter.View {

    @Inject
    @DataField("pipeline-container")
    private Div container;

    private PipelinePresenter presenter;

    @Override
    public void init(final PipelinePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void addStage(final IsElement element) {
        container.appendChild(element.getElement());
    }

    @Override
    public void clearStages() {
        DOMUtil.removeAllChildren(container);
    }
}
