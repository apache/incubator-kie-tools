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
import javax.enterprise.event.Event;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import com.ait.lienzo.client.widget.panel.impl.LienzoPanelScrollEvent;
import com.ait.lienzo.client.widget.panel.impl.ScrollablePanel;

@Dependent
@Typed(ScrollableLienzoPanel.class)
public class ScrollableLienzoPanel
        extends DelegateLienzoPanel<StunnerLienzoBoundsPanel> {

    private final StunnerLienzoBoundsPanel panel;
    private final Event<LienzoPanelScrollEvent> scrollEvent;

    @Inject
    public ScrollableLienzoPanel(final StunnerLienzoBoundsPanel panel, final Event<LienzoPanelScrollEvent> scrollEvent) {
        this.panel = panel;
        this.scrollEvent = scrollEvent;
    }

    @PostConstruct
    public void init() {
        panel.setPanelBuilder(ScrollableLienzoPanelView::new);
    }

    public void beginScrollEventTrack() {
        getView().addScrollEventListener(event -> scrollEvent.fire(new LienzoPanelScrollEvent()));
    }

    public ScrollableLienzoPanel refresh() {
        getView().refresh();
        return this;
    }

    public ScrollablePanel getView() {
        return (ScrollablePanel) getDelegate().getView();
    }

    @Override
    protected StunnerLienzoBoundsPanel getDelegate() {
        return panel;
    }
}
