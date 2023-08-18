/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.editor;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import org.dashbuilder.dataset.client.ExternalDataSetParserProvider;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.GlobalDisplayerSettings;
import org.dashbuilder.displayer.client.PerspectiveCoordinator;
import org.dashbuilder.displayer.client.widgets.DisplayerErrorWidget;
import org.dashbuilder.displayer.client.widgets.DisplayerViewer;
import org.dashbuilder.displayer.json.DisplayerSettingsJSONMarshaller;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;

import static org.dashbuilder.common.client.StringUtils.isBlank;

@Dependent
public class DisplayerDragComponent implements LayoutDragComponent {

    SyncBeanManager beanManager;
    DisplayerViewer viewer;
    PerspectiveCoordinator perspectiveCoordinator;
    GlobalDisplayerSettings globalDisplayerSettings;

    @Inject
    DisplayerErrorWidget displayError;

    @Inject
    ExternalDataSetParserProvider dataSetParserProvider;

    @Inject
    public DisplayerDragComponent(SyncBeanManager beanManager,
                                  DisplayerViewer viewer,
                                  PerspectiveCoordinator perspectiveCoordinator,
                                  GlobalDisplayerSettings globalDisplayerSettings) {

        this.beanManager = beanManager;
        this.viewer = viewer;
        this.perspectiveCoordinator = perspectiveCoordinator;
        this.globalDisplayerSettings = globalDisplayerSettings;
    }

    @PostConstruct
    public void init() {
        // use the client side implementation
        DisplayerSettingsJSONMarshaller.get().setDataSetJsonParser(dataSetParserProvider.get());
    }

    public DisplayerType getDisplayerType() {
        return null;
    }

    public DisplayerSubType getDisplayerSubType() {
        return null;
    }

    @Override
    public HTMLElement getShowWidget(final RenderingContext ctx) {
        var settingsOp = getDisplayerSettings(ctx.getComponent());
        if (settingsOp.isPresent()) {
            var settings = settingsOp.get();
            var error = settings.getError();
            if (error.isPresent()) {
                displayError.show(error.get(), null);
                return displayError.getElement();
            }
            viewer.init(settings);
            int containerWidth = ctx.getContainer().getOffsetWidth() - 40;
            adjustSize(settings, containerWidth);
            var displayer = viewer.draw();
            perspectiveCoordinator.addDisplayer(displayer);
            return Js.cast(viewer.getDisplayerContainer());
        }
        return null;
    }

    protected void adjustSize(DisplayerSettings settings, int containerWidth) {
        int displayerWidth = settings.getChartWidth();
        int tableWidth = settings.getTableWidth();
        if (containerWidth > 0 && displayerWidth > containerWidth) {
            int ratio = containerWidth * 100 / displayerWidth;
            settings.setChartWidth(containerWidth);
            settings.setChartHeight(settings.getChartHeight() * ratio / 100);
        }
        if (tableWidth == 0 || tableWidth > containerWidth) {
            settings.setTableWidth(containerWidth > 20 ? containerWidth - 20 : 0);
        }
    }

    private Optional<DisplayerSettings> getDisplayerSettings(LayoutComponent component) {
        var settings = component.getSettings();
        if (settings != null) {
            var displayerSettings = (DisplayerSettings) settings;
            globalDisplayerSettings.apply(displayerSettings);
            if (isDataMissing(displayerSettings)) {
                displayerSettings.setError("A displayer must have a dataset 'uuid' in 'lookup' or an inline dataSet.");
            }
            return Optional.of(displayerSettings);
        }

        return Optional.empty();
    }

    private boolean isDataMissing(DisplayerSettings displayerSettings) {
        var lookup = displayerSettings.getDataSetLookup();
        return (lookup == null || isBlank(lookup.getDataSetUUID())) && displayerSettings.getDataSet() == null;
    }
}
