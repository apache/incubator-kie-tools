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

package org.kie.workbench.common.stunner.client.widgets.canvas;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import com.ait.lienzo.client.widget.panel.impl.PreviewPanel;
import com.ait.lienzo.client.widget.panel.impl.ScrollablePanel;

@Dependent
@Typed(PreviewLienzoPanel.class)
public class PreviewLienzoPanel
        extends DelegateLienzoPanel<StunnerLienzoBoundsPanel> {

    private static final int DEFAULT_WIDTH = 420;
    private static final int DEFAULT_HEIGHT = 210;

    private final StunnerLienzoBoundsPanel panel;

    @Inject
    public PreviewLienzoPanel(final StunnerLienzoBoundsPanel panel) {
        this.panel = panel;
    }

    @PostConstruct
    public void init() {
        panel.setPanelBuilder(() -> new PreviewPanel(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public PreviewLienzoPanel observe(final ScrollableLienzoPanel panel) {
        ((PreviewPanel) getDelegate().getView())
                .observe((ScrollablePanel) panel.getDelegate().getView());

        return this;
    }

    @Override
    protected StunnerLienzoBoundsPanel getDelegate() {
        return panel;
    }
}
