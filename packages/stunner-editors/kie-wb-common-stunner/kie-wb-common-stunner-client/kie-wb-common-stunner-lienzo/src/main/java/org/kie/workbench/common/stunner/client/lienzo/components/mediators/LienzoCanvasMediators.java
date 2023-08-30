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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.client.widget.panel.mediators.PanelMediators;
import org.appformer.client.context.EditorContextProvider;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.kie.workbench.common.stunner.client.lienzo.components.views.LienzoCanvasNotification;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyEventHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl.KogitoKeyShortcutKeyDownThenUp;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;

import static com.ait.lienzo.client.core.mediator.EventFilter.ALT;
import static com.ait.lienzo.client.core.mediator.EventFilter.META;
import static org.appformer.client.context.OperatingSystem.LINUX;
import static org.appformer.client.context.OperatingSystem.MACOS;
import static org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeysMatcher.doKeysMatch;

@Dependent
public class LienzoCanvasMediators {

    static final AbstractCanvas.Cursors CURSOR_ZOOM = AbstractCanvas.Cursors.ROW_RESIZE;
    static final AbstractCanvas.Cursors CURSOR_PAN = AbstractCanvas.Cursors.POINTER;
    static final AbstractCanvas.Cursors CURSOR_PREVIEW = AbstractCanvas.Cursors.CROSSHAIR;
    static final AbstractCanvas.Cursors CURSOR_DEFAULT = AbstractCanvas.Cursors.DEFAULT;

    private final KeyEventHandler keyEventHandler;
    private final ClientTranslationService translationService;
    private final LienzoCanvasNotification notification;
    private final Function<LienzoBoundsPanel, PanelMediators> mediatorsBuilder;
    private PanelMediators mediators;
    Consumer<AbstractCanvas.Cursors> cursor;

    @Inject
    public LienzoCanvasMediators(final KeyEventHandler keyEventHandler,
                                 final ClientTranslationService translationService,
                                 final LienzoCanvasNotification notification,
                                 final EditorContextProvider editorContextProvider) {
        this(keyEventHandler,
             translationService,
             notification,
             getMediatorsBuilder(editorContextProvider));
    }

    private static Function<LienzoBoundsPanel, PanelMediators> getMediatorsBuilder(final EditorContextProvider editorContextProvider) {
        return editorContextProvider.getOperatingSystem().orElse(LINUX).equals(MACOS)
                ? panel -> PanelMediators.build(panel, META, ALT)
                : PanelMediators::build;
    }

    LienzoCanvasMediators(final KeyEventHandler keyEventHandler,
                          final ClientTranslationService translationService,
                          final LienzoCanvasNotification notification,
                          final Function<LienzoBoundsPanel, PanelMediators> mediatorsBuilder) {
        this.keyEventHandler = keyEventHandler;
        this.translationService = translationService;
        this.notification = notification;
        this.mediatorsBuilder = mediatorsBuilder;
    }

    public void init(final Supplier<LienzoCanvas> canvas) {
        keyEventHandler.addKeyShortcutCallback(new KogitoKeyShortcutKeyDownThenUp(new Key[]{Key.ALT}, "Navigate | Hold and drag to Pan", this::enablePan, this::clear));
        keyEventHandler.addKeyShortcutCallback(new KogitoKeyShortcutKeyDownThenUp(new Key[]{Key.CONTROL}, "Navigate | Hold and scroll to Zoom", this::enableZoom, this::clear));
        keyEventHandler.addKeyShortcutCallback(new KogitoKeyShortcutKeyDownThenUp(new Key[]{Key.CONTROL, Key.ALT}, "Navigate | Hold to Preview", this::enablePreview, this::clear));

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
                        clear();
                    }
                });
        cursor = c -> canvas.get().getView().setCursor(c);
        final LienzoPanel panel = (LienzoPanel) canvas.get().getView().getPanel();
        this.mediators = mediatorsBuilder.apply(panel.getView());
        this.notification.init(() -> panel);
        setScaleAboutPoint(false);
    }

    public void setMinScale(final double minScale) {
        mediators.getZoomMediator().setMinScale(minScale);
    }

    public void setMaxScale(final double maxScale) {
        mediators.getZoomMediator().setMaxScale(maxScale);
        mediators.getPreviewMediator().setMaxScale(maxScale);
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
            cursor.accept(CURSOR_ZOOM);
        }
    }

    private void enablePan() {
        if (null != mediators) {
            cursor.accept(CURSOR_PAN);
        }
    }

    private void enablePreview() {
        if (null != mediators && mediators.enablePreview()) {
            cursor.accept(CURSOR_PREVIEW);
            notification.show(translationService.getNotNullValue(CoreTranslationMessages.MEDIATOR_PREVIEW));
        }
    }

    private void clear() {
        if (null != mediators) {
            cursor.accept(CURSOR_DEFAULT);
            mediators.disablePreview();
            notification.hide();
        }
    }
}
