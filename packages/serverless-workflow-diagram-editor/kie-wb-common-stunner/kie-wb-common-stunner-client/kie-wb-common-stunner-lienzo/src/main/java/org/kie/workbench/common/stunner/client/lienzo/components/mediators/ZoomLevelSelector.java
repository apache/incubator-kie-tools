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

import elemental2.dom.HTMLElement;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.kie.j2cl.tools.di.core.IsElement;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

@Dependent
public class ZoomLevelSelector implements IsElement {

    public interface View extends UberView<ZoomLevelSelector> {

        void setText(String text);

        void add(String text,
                 Command onClick);

        void clear();

        void setEnabled(boolean enabled);

        void updatePreviewButton(boolean enabled);

        void dropUp();

        void applyTheme();
    }

    private final View view;
    private Command onScaleToFit;
    private Command onDecreaseLevel;
    private Command onIncreaseLevel;
    private Command onPreview;

    @Inject
    public ZoomLevelSelector(final View view) {
        this.view = view;
        this.onScaleToFit = () -> {
        };
        this.onIncreaseLevel = () -> {
        };
        this.onDecreaseLevel = () -> {
        };
        this.onPreview = () -> {
        };
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.setEnabled(true);
    }

    public ZoomLevelSelector onPreview(final Command onPreview) {
        checkNotNull("onPreview", onPreview);
        this.onPreview = onPreview;
        return this;
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

    public ZoomLevelSelector onScaleToFitSize(final Command onScaleToFit) {
        checkNotNull("onScaleToFit", onScaleToFit);
        this.onScaleToFit = onScaleToFit;
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

    public ZoomLevelSelector setPreviewEnabled(boolean enabled) {
        view.updatePreviewButton(enabled);
        return this;
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    void onPreview() {
        onPreview.execute();
    }

    void onScaleToFitSize() {
        onScaleToFit.execute();
    }

    void onIncreaseLevel() {
        onIncreaseLevel.execute();
    }

    void onDecreaseLevel() {
        onDecreaseLevel.execute();
    }

    void applyTheme() {
        view.applyTheme();
    }

    @PreDestroy
    public void destroy() {
        onIncreaseLevel = null;
        onDecreaseLevel = null;
    }
}
