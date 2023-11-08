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

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.mediator.MousePanMediator;
import com.ait.lienzo.client.core.mediator.MouseWheelZoomMediator;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.client.widget.panel.impl.ScrollablePanel;
import com.ait.lienzo.client.widget.panel.mediators.PanelMediators;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.appformer.client.context.EditorContextProvider;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.preview.TogglePreviewEvent;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.preview.TogglePreviewUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyEventHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl.KogitoKeyShortcutKeyDownThenUp;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key;

import static com.ait.lienzo.client.core.mediator.EventFilter.ALT;
import static com.ait.lienzo.client.core.mediator.EventFilter.META;
import static org.appformer.client.context.OperatingSystem.LINUX;
import static org.appformer.client.context.OperatingSystem.MACOS;
import static org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeysMatcher.doKeysMatch;

@Dependent
public class LienzoCanvasMediators {

    static final AbstractCanvas.Cursors CURSOR_DEFAULT = AbstractCanvas.Cursors.DEFAULT;
    static final AbstractCanvas.Cursors CURSOR_GRAB = AbstractCanvas.Cursors.GRAB;
    static final AbstractCanvas.Cursors CURSOR_GRABBING = AbstractCanvas.Cursors.GRABBING;
    static final AbstractCanvas.Cursors CURSOR_PREVIEW = AbstractCanvas.Cursors.CROSSHAIR;
    static final AbstractCanvas.Cursors CURSOR_ZOOM_IN = AbstractCanvas.Cursors.ZOOM_IN;
    static final AbstractCanvas.Cursors CURSOR_ZOOM_OUT = AbstractCanvas.Cursors.ZOOM_OUT;

    private final KeyEventHandler keyEventHandler;
    private final Function<LienzoBoundsPanel, PanelMediators> mediatorsBuilder;
    private final Event<TogglePreviewEvent> togglePreviewEvent;
    private PanelMediators mediators;
    private LienzoPanel panel;
    Consumer<AbstractCanvas.Cursors> cursor;

    @Inject
    public LienzoCanvasMediators(final KeyEventHandler keyEventHandler,
                                 final EditorContextProvider editorContextProvider,
                                 final Event<TogglePreviewEvent> togglePreviewEvent) {
        this(keyEventHandler,
             getMediatorsBuilder(editorContextProvider),
             togglePreviewEvent);
    }

    private static Function<LienzoBoundsPanel, PanelMediators> getMediatorsBuilder(final EditorContextProvider editorContextProvider) {
        return editorContextProvider.getOperatingSystem().orElse(LINUX).equals(MACOS)
                ? panel -> PanelMediators.build(panel, META, ALT)
                : PanelMediators::build;
    }

    LienzoCanvasMediators(final KeyEventHandler keyEventHandler,
                          final Function<LienzoBoundsPanel, PanelMediators> mediatorsBuilder,
                          final Event<TogglePreviewEvent> togglePreviewEvent) {
        this.keyEventHandler = keyEventHandler;
        this.mediatorsBuilder = mediatorsBuilder;
        this.togglePreviewEvent = togglePreviewEvent;
    }

    public void init(final Supplier<LienzoCanvas> canvas) {
        keyEventHandler.setEnabled(true);
        keyEventHandler.addKeyShortcutCallback(new KogitoKeyShortcutKeyDownThenUp(new Key[]{Key.ALT}, "Navigate | Hold and drag to Pan", this::enablePan, this::clear));
        keyEventHandler.addKeyShortcutCallback(new KogitoKeyShortcutKeyDownThenUp(new Key[]{Key.CONTROL}, "Navigate | Hold and scroll to Zoom", this::enableZoom, this::clear));
        keyEventHandler.addKeyShortcutCallback(new KogitoKeyShortcutKeyDownThenUp(new Key[]{Key.CONTROL, Key.ALT}, "Navigate | Hold to Preview", this::enablePreview, this::disablePreview));

        keyEventHandler
                .setTimerDelay(150)
                .addKeyShortcutCallback(new KeyboardControl.KeyShortcutCallback() {
                    @Override
                    public void onKeyShortcut(Key... keys) {
                        if (doKeysMatch(keys,
                                        Key.CONTROL)) {
                            enableZoom();
                        } else if (doKeysMatch(keys,
                                               Key.ALT)) {
                            enablePan();
                        } else if (doKeysMatch(keys,
                                               Key.CONTROL,
                                               Key.ALT) ||
                                doKeysMatch(keys,
                                            Key.ALT,
                                            Key.CONTROL)) {
                            enablePreview();
                        }
                    }

                    @Override
                    public void onKeyUp(Key key) {
                        disablePreview();
                        clear();
                    }
                });

        cursor = c -> canvas.get().getView().setCursor(c);
        panel = (LienzoPanel) canvas.get().getView().getPanel();

        mediators = mediatorsBuilder.apply(panel.getView());
        mediators.getZoomMediator().setCallback(new MouseWheelZoomMediator.Callback() {
            @Override
            public void onActivate() {
                cursor.accept(CURSOR_DEFAULT);
            }

            @Override
            public void onZoomIn() {
                cursor.accept(CURSOR_ZOOM_IN);
            }

            @Override
            public void onZoomOut() {
                cursor.accept(CURSOR_ZOOM_OUT);
            }

            @Override
            public void onDeactivate() {
                cursor.accept(CURSOR_DEFAULT);
            }
        });
        mediators.getPanMediator().setCallback(new MousePanMediator.Callback() {
            @Override
            public void onActivate() {
                cursor.accept(CURSOR_GRAB);
            }

            @Override
            public void onDragStart() {
                cursor.accept(CURSOR_GRABBING);
            }

            @Override
            public void onDragEnd() {
                cursor.accept(CURSOR_GRAB);
            }

            @Override
            public void onDeactivate() {
                cursor.accept(CURSOR_DEFAULT);
            }
        });

        disablePreview();

        setScaleAboutPoint(true);
    }

    public void setMinScale(final double minScale) {
        mediators.getZoomMediator().setMinScale(minScale);
    }

    public void setMaxScale(final double maxScale) {
        mediators.getZoomMediator().setMaxScale(maxScale);
    }

    public void setZoomFactor(final double factor) {
        mediators.getZoomMediator().setZoomFactor(factor);
    }

    public void setScaleAboutPoint(final boolean scaleAboutPoint) {
        mediators.getZoomMediator().setScaleAboutPoint(scaleAboutPoint);
    }

    public void enable() {
        keyEventHandler.setEnabled(true);
    }

    public void disable() {
        keyEventHandler.setEnabled(false);
        clear();
    }

    public void destroy() {
        if (null != mediators) {
            mediators.destroy();
            mediators = null;
        }
    }

    PanelMediators getMediators() {
        return mediators;
    }

    private void enableZoom() {
        if (null != mediators) {
            MouseWheelZoomMediator mouseWheelZoomMediator = mediators.getZoomMediator();
            if (null != mouseWheelZoomMediator) {
                mouseWheelZoomMediator.attempt_deactivate();
            }
        }
    }

    private void enablePan() {
        if (null != mediators) {
            MousePanMediator mousePanMediator = mediators.getPanMediator();
            if (null != mousePanMediator) {
                mousePanMediator.attempt_activate();
            }
        }
    }

    private void enablePreview() {
        if (panel.getView() instanceof ScrollablePanel) {
            ScrollablePanel scrollablePanel = (ScrollablePanel) panel.getView();
            if (TogglePreviewUtils.IsPreviewAvailable(scrollablePanel)) {
                if (null != mediators) {
                    TogglePreviewEvent event = TogglePreviewUtils.buildEvent(scrollablePanel,
                                                                             TogglePreviewEvent.EventType.TOGGLE);
                    togglePreviewEvent.fire(event);
                }
            }
        }
    }

    private void disablePreview() {
        togglePreviewEvent.fire(new TogglePreviewEvent(TogglePreviewEvent.EventType.HIDE));
    }

    private void clear() {
        if (null != mediators) {
            MouseWheelZoomMediator mouseWheelZoomMediator = mediators.getZoomMediator();
            if (null != mouseWheelZoomMediator) {
                mouseWheelZoomMediator.attempt_activate();
            }
            MousePanMediator mousePanMediator = mediators.getPanMediator();
            if (null != mousePanMediator) {
                mousePanMediator.attempt_deactivate();
            }
        }
    }
}
