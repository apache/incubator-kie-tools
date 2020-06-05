/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.Collections;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;

public class GuidedDecisionTableModellerViewImpl extends Composite implements GuidedDecisionTableModellerView {

    private static final double VP_SCALE = 1.0;

    private static GuidedDecisionTableModellerViewImplUiBinder uiBinder = GWT.create(GuidedDecisionTableModellerViewImplUiBinder.class);

    private final GuidedDecisionTableModellerBoundsHelper boundsHelper = new GuidedDecisionTableModellerBoundsHelper();

    private TranslationService translationService;

    @UiField
    HTMLPanel pinnedModeIndicator;

    @UiField(provided = true)
    GridLienzoPanel gridPanel = new GridLienzoPanel() {

        @Override
        public void onResize() {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    updatePanelSize();
                    refreshScrollPosition();

                    final TransformMediator restriction = mousePanMediator.getTransformMediator();
                    final Transform transform = restriction.adjust(gridLayer.getViewport().getTransform(),
                                                                   gridLayer.getVisibleBounds());
                    gridLayer.getViewport().setTransform(transform);
                    gridLayer.draw();
                }
            });
        }
    };

    private TransformMediator defaultTransformMediator;

    private GuidedDecisionTableModellerView.Presenter presenter;

    private final DefaultGridLayer gridLayer = defaultGridLayer();

    private final RestrictedMousePanMediator mousePanMediator = restrictedMousePanMediator();

    public GuidedDecisionTableModellerViewImpl() {
        //CDI proxy
    }

    @Inject
    public GuidedDecisionTableModellerViewImpl(final TranslationService translationService) {

        this.translationService = translationService;

        initWidget(uiBinder.createAndBindUi(this));
    }

    DefaultGridLayer defaultGridLayer() {
        return new DefaultGridLayer() {
            @Override
            public void enterPinnedMode(final GridWidget gridWidget,
                                        final Command onStartCommand) {
                super.enterPinnedMode(gridWidget,
                                      new Command() {
                                          @Override
                                          public void execute() {
                                              onStartCommand.execute();
                                              presenter.onViewPinned(true);
                                          }
                                      });
            }

            @Override
            public void exitPinnedMode(final Command onCompleteCommand) {
                super.exitPinnedMode(new Command() {
                    @Override
                    public void execute() {
                        onCompleteCommand.execute();
                        presenter.onViewPinned(false);
                    }
                });
            }

            @Override
            public TransformMediator getDefaultTransformMediator() {
                return defaultTransformMediator;
            }
        };
    }

    RestrictedMousePanMediator restrictedMousePanMediator() {
        return new RestrictedMousePanMediator(gridLayer) {
            @Override
            protected void onMouseMove(final NodeMouseMoveEvent event) {
                super.onMouseMove(event);
                presenter.updateRadar();
            }
        };
    }

    protected void initWidget(final Widget widget) {
        super.initWidget(widget);
    }

    @Override
    public void init(final GuidedDecisionTableModellerView.Presenter presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void setup() {
        setupGridPanel();
    }

    void setupGridPanel() {
        //Lienzo stuff - Set default scale
        final Transform transform = newTransform().scale(VP_SCALE);
        gridPanel.getViewport().setTransform(transform);

        //Lienzo stuff - Add mouse pan support
        defaultTransformMediator = new BoundaryTransformMediator(GuidedDecisionTableModellerViewImpl.this);
        mousePanMediator.setTransformMediator(defaultTransformMediator);
        gridPanel.getViewport().getMediators().push(mousePanMediator);
        mousePanMediator.setBatchDraw(true);

        gridPanel.setBounds(getBounds());
        gridPanel.getScrollPanel().addDomHandler(scrollEvent -> getPresenter().updateRadar(),
                                                 ScrollEvent.getType());

        //Wire-up widgets
        gridPanel.add(gridLayer);

        //Set ID on GridLienzoPanel for Selenium tests.
        gridPanel.getElement().setId("dtable_container_" + Document.get().createUniqueId());
    }

    @Override
    public void onResize() {
        gridPanel.onResize();
        getPresenter().updateRadar();
    }

    @Override
    public HandlerRegistration addKeyDownHandler(final KeyDownHandler handler) {
        return gridPanel.addKeyDownHandler(handler);
    }

    @Override
    public HandlerRegistration addContextMenuHandler(final ContextMenuHandler handler) {
        return gridPanel.addDomHandler(handler,
                                       ContextMenuEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseDownHandler(final MouseDownHandler handler) {
        return rootPanel().addDomHandler(handler,
                                         MouseDownEvent.getType());
    }

    RootPanel rootPanel() {
        return RootPanel.get();
    }

    @Override
    public void clear() {
        gridLayer.removeAll();
    }

    @Override
    public void addDecisionTable(final GuidedDecisionTableView gridWidget) {
        //Ensure the first Decision Table is visible
        if (gridLayer.getGridWidgets().isEmpty()) {
            final Point2D translation = getTranslation(gridWidget);
            final Transform t = gridLayer.getViewport().getTransform();
            t.translate(translation.getX(),
                        translation.getY());
        }
        gridLayer.add(gridWidget);
        gridLayer.batch();
    }

    private Point2D getTranslation(final GuidedDecisionTableView gridWidget) {
        final double boundsPadding = GuidedDecisionTableModellerBoundsHelper.BOUNDS_PADDING;
        final Transform t = gridLayer.getViewport().getTransform();
        final double requiredTranslateX = boundsPadding - gridWidget.getX();
        final double requiredTranslateY = boundsPadding - gridWidget.getY();
        final double actualTranslateX = t.getTranslateX();
        final double actualTranslateY = t.getTranslateY();
        final double dx = requiredTranslateX - actualTranslateX;
        final double dy = requiredTranslateY - actualTranslateY;
        return new Point2D(dx,
                           dy);
    }

    @Override
    public void removeDecisionTable(final GuidedDecisionTableView gridWidget,
                                    final Command afterRemovalCommand) {
        if (gridWidget == null) {
            return;
        }
        final Command remove = () -> {
            gridLayer.remove(gridWidget);

            afterRemovalCommand.execute();

            gridLayer.batch();
        };
        if (gridLayer.isGridPinned()) {
            final GridPinnedModeManager.PinnedContext context = gridLayer.getPinnedContext();
            if (gridWidget.equals(context.getGridWidget())) {
                gridLayer.exitPinnedMode(remove);
            }
        } else {
            remove.execute();
        }
    }

    @Override
    public void refreshScrollPosition() {
        gridPanel.refreshScrollPosition();
    }

    @Override
    public void setZoom(final int zoom) {
        //Set zoom preserving translation
        final Transform transform = newTransform();
        final double tx = gridPanel.getViewport().getTransform().getTranslateX();
        final double ty = gridPanel.getViewport().getTransform().getTranslateY();
        transform.translate(tx,
                            ty);
        transform.scale(zoom / 100.0);

        //Ensure the change in zoom keeps the view in bounds. IGridLayer's visibleBounds depends
        //on the Viewport Transformation; so set it to the "proposed" transformation before checking.
        gridPanel.getViewport().setTransform(transform);
        final TransformMediator restriction = mousePanMediator.getTransformMediator();
        final Transform newTransform = restriction.adjust(transform,
                                                          gridLayer.getVisibleBounds());
        gridPanel.getViewport().setTransform(newTransform);
        gridPanel.getViewport().batch();
        gridPanel.refreshScrollPosition();
    }

    @Override
    public GridLayer getGridLayerView() {
        return gridLayer;
    }

    @Override
    public GridLienzoPanel getGridPanel() {
        return gridPanel;
    }

    @Override
    public Bounds getBounds() {

        if (presenter == null) {
            return boundsHelper.getBounds(Collections.emptySet());
        } else {
            return boundsHelper.getBounds(presenter.getAvailableDecisionTables());
        }
    }

    @Override
    public void select(final GridWidget selectedGridWidget) {
        getGridLayer().select(selectedGridWidget);
    }

    @Override
    public void selectLinkedColumn(final GridColumn<?> link) {
        gridLayer.selectLinkedColumn(link);
    }

    @Override
    public Set<GridWidget> getGridWidgets() {
        return gridLayer.getGridWidgets();
    }

    @Override
    public void setPinnedModeIndicatorVisibility(final boolean visibility, final double headerCaptionWidth) {
        style(pinnedModeIndicator).setTop(visibility ? 0.5 : -2.0, Style.Unit.EM);
        style(pinnedModeIndicator).setLeft(headerCaptionWidth, Style.Unit.PX);
    }

    private Style style(final HTMLPanel pinnedModeIndicator) {
        return pinnedModeIndicator.getElement().getStyle();
    }

    @Override
    public void showGenericVetoMessage() {
        ErrorPopup.showMessage(translate(GuidedDecisionTableErraiConstants.NewGuidedDecisionTableColumnWizard_GenericVetoError));
    }

    @Override
    public void showUnableToDeleteColumnMessage(final ConditionCol52 column) {
        ErrorPopup.showMessage(translate(GuidedDecisionTableErraiConstants.NewGuidedDecisionTableColumnWizard_DeletePatternInUseVetoError0,
                                         column.getHeader()));
    }

    @Override
    public void showUnableToDeleteColumnMessage(final ActionCol52 column) {
        ErrorPopup.showMessage(translate(GuidedDecisionTableErraiConstants.NewGuidedDecisionTableColumnWizard_DeletePatternInUseVetoError0,
                                         column.getHeader()));
    }

    private String translate(final String key,
                             final String... args) {
        return translationService.format(key, args);
    }

    Presenter getPresenter() {
        return presenter;
    }

    DefaultGridLayer getGridLayer() {
        return gridLayer;
    }

    Transform newTransform() {
        return new Transform();
    }

    interface GuidedDecisionTableModellerViewImplUiBinder extends UiBinder<Widget, GuidedDecisionTableModellerViewImpl> {

    }
}
