/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.client.lienzo.components.mediators;

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.client.lienzo.util.ToolboxRefreshEvent;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

@Dependent
public class ZoomLevelSelector implements IsWidget {

    public interface View extends UberView<ZoomLevelSelector> {

        void setText(String text);

        void add(String text,
                 Command onClick);

        void clear();

        void setEnabled(boolean enabled);

        void dropUp();
    }

    private final View view;
    private Command onReset;
    private Command onDecreaseLevel;
    private Command onIncreaseLevel;
    private final Event<ToolboxRefreshEvent> toolboxRefreshEvent;

    @Inject
    public ZoomLevelSelector(final View view, final Event<ToolboxRefreshEvent> toolboxRefreshEvent) {
        this.toolboxRefreshEvent = toolboxRefreshEvent;
        this.view = view;
        this.onReset = () -> {
        };
        this.onIncreaseLevel = () -> {
        };
        this.onDecreaseLevel = () -> {
        };
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public ZoomLevelSelector onDecreaseLevel(final Command onDecreaseLevel) {
        checkNotNull("onDecreaseLevel", onDecreaseLevel);
        this.onDecreaseLevel = onDecreaseLevel;
        return this;
    }

    public ZoomLevelSelector onIncreaseLevel(final Command onIncreaseLevel) {
        checkNotNull("onIncreaseLevel", onIncreaseLevel);
        this.onIncreaseLevel = onIncreaseLevel;
        return this;
    }

    public ZoomLevelSelector onReset(final Command onReset) {
        checkNotNull("onReset", onReset);
        this.onReset = onReset;
        return this;
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }

    public ZoomLevelSelector setText(final String text) {
        view.setText(text);
        return this;
    }

    public ZoomLevelSelector dropUp() {
        view.dropUp();
        return this;
    }

    public ZoomLevelSelector add(final String text,
                                 final Command onClick) {
        view.add(text, onClick);
        return this;
    }

    public ZoomLevelSelector clear() {
        view.clear();
        return this;
    }

    public ZoomLevelSelector setEnabled(boolean enabled) {
        view.setEnabled(enabled);
        return this;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    void onReset() {
        onReset.execute();
        fireToolboxRefreshEvent();
    }

    void onIncreaseLevel() {
        onIncreaseLevel.execute();
        fireToolboxRefreshEvent();
    }

    void onDecreaseLevel() {
        onDecreaseLevel.execute();
        fireToolboxRefreshEvent();
    }

    protected void fireToolboxRefreshEvent() {
        toolboxRefreshEvent.fire(new ToolboxRefreshEvent());
    }

    @PreDestroy
    public void destroy() {
        onIncreaseLevel = null;
        onDecreaseLevel = null;
    }
}
