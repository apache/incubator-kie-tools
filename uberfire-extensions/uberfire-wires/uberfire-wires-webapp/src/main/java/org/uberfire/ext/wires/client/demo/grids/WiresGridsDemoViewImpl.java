/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.client.demo.grids;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.ListBox;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.wires.client.resources.i18n.WiresGridsDemoConstants;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.BlueTheme;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.GreenTheme;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.MultiColouredTheme;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.RedTheme;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.BoundaryTransformMediator;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;

@Dependent
@Templated
public class WiresGridsDemoViewImpl extends Composite implements WiresGridsDemoView {

    private static final double VP_SCALE = 1.0;
    private final DefaultGridLayer gridLayer = new DefaultGridLayer();
    private final RestrictedMousePanMediator mousePanMediator = new RestrictedMousePanMediator(gridLayer);
    private final Map<String, GridRendererTheme> themes = new HashMap<String, GridRendererTheme>();
    @DataField("gridPanel")
    GridLienzoPanel gridPanel = new GridLienzoPanel(200,
                                                    200);
    @DataField("zoom")
    ListBox zoom;
    @DataField("basicRendererSelector")
    ListBox basicRendererSelector;
    @DataField("chkShowMerged")
    CheckBox chkShowMerged;
    @DataField("btnAppendRow")
    Button btnAppendRow;
    @DataField("btnDeleteRow")
    Button btnDeleteRow;
    private TranslationService translationService;

    @Inject
    public WiresGridsDemoViewImpl(final ListBox zoom,
                                  final ListBox basicRendererSelector,
                                  final CheckBox chkShowMerged,
                                  final Button btnAppendRow,
                                  final Button btnDeleteRow,
                                  final TranslationService translationService) {
        this.zoom = zoom;
        this.basicRendererSelector = basicRendererSelector;
        this.chkShowMerged = chkShowMerged;
        this.btnAppendRow = btnAppendRow;
        this.btnDeleteRow = btnDeleteRow;
        this.translationService = translationService;
    }

    @PostConstruct
    public void setup() {
        setupCanvas();
        setupZoomSelector();
        setupStyleSelector();

        chkShowMerged.setText(translationService.getTranslation(WiresGridsDemoConstants.Options_ShowMerged));
        btnAppendRow.setText(translationService.getTranslation(WiresGridsDemoConstants.Options_AppendRow));
        btnDeleteRow.setText(translationService.getTranslation(WiresGridsDemoConstants.Options_DeleteRow));
    }

    private void setupCanvas() {
        mousePanMediator.setBatchDraw(true);
        mousePanMediator.setTransformMediator(new BoundaryTransformMediator());
        gridPanel.getViewport().getMediators().push(mousePanMediator);

        final Transform transform = new Transform().scale(VP_SCALE);
        gridPanel.getViewport().setTransform(transform);

        gridPanel.add(gridLayer);
    }

    private void setupZoomSelector() {
        for (int pct = 50; pct <= 150; pct = pct + 10) {
            zoom.addItem(Integer.toString(pct));
        }

        zoom.setSelectedIndex(5);
    }

    private void setupStyleSelector() {
        final RedTheme redRenderer = new RedTheme();
        final GreenTheme greenRenderer = new GreenTheme();
        final BlueTheme blueRenderer = new BlueTheme();
        final MultiColouredTheme multiColouredTheme = new MultiColouredTheme();
        themes.put(redRenderer.getName(),
                   redRenderer);
        themes.put(greenRenderer.getName(),
                   greenRenderer);
        themes.put(blueRenderer.getName(),
                   blueRenderer);
        themes.put(multiColouredTheme.getName(),
                   multiColouredTheme);

        for (String name : themes.keySet()) {
            basicRendererSelector.addItem(name);
        }
    }

    @Override
    public void select(final GridWidget selectedGridWidget) {
        gridLayer.select(selectedGridWidget);
    }

    @Override
    public void selectLinkedColumn(final GridColumn<?> selectedGridColumn) {
        gridLayer.selectLinkedColumn(selectedGridColumn);
    }

    @Override
    public Set<GridWidget> getGridWidgets() {
        return gridLayer.getGridWidgets();
    }

    @Override
    public void add(final GridWidget gridWidget) {
        gridLayer.add(gridWidget);
    }

    @Override
    public void refresh() {
        gridLayer.batch();
    }

    @Override
    public GridLayer getGridLayer() {
        return gridLayer;
    }

    @Override
    public GridLienzoPanel getGridPanel() {
        return gridPanel;
    }

    @Override
    public HandlerRegistration addKeyDownHandler(final KeyDownHandler handler) {
        return gridPanel.addDomHandler(handler,
                                       KeyDownEvent.getType());
    }

    @Override
    public HandlerRegistration addZoomChangeHandler(final ChangeHandler handler) {
        return zoom.addChangeHandler(handler);
    }

    @Override
    public int getSelectedZoomLevel() {
        final int selectedIndex = zoom.getSelectedIndex();
        final int pct = Integer.parseInt(zoom.getValue(selectedIndex));
        return pct;
    }

    @Override
    public void setZoom(final int zoom) {
        final Transform transform = new Transform();
        final double tx = gridPanel.getViewport().getTransform().getTranslateX();
        final double ty = gridPanel.getViewport().getTransform().getTranslateY();
        transform.translate(tx,
                            ty);
        transform.scale(((double) zoom / 100.0));

        gridPanel.getViewport().setTransform(transform);
        gridPanel.getViewport().batch();
    }

    @Override
    public HandlerRegistration addThemeChangeHandler(final ChangeHandler handler) {
        return basicRendererSelector.addChangeHandler(handler);
    }

    @Override
    public GridRendererTheme getSelectedTheme() {
        final GridRendererTheme theme = themes.get(basicRendererSelector.getItemText(basicRendererSelector.getSelectedIndex()));
        return theme;
    }

    @Override
    public HandlerRegistration addMergedStateValueChangeHandler(final ValueChangeHandler<Boolean> handler) {
        return chkShowMerged.addValueChangeHandler(handler);
    }

    @Override
    public void setMergedState(final boolean isMerged) {
        chkShowMerged.setValue(isMerged);
    }

    @Override
    public HandlerRegistration addAppendRowClickHandler(final ClickHandler handler) {
        return btnAppendRow.addClickHandler(handler);
    }

    @Override
    public HandlerRegistration addDeleteRowClickHandler(final ClickHandler handler) {
        return btnDeleteRow.addClickHandler(handler);
    }

    @Override
    public void onResize() {
        gridPanel.onResize();
    }
}
