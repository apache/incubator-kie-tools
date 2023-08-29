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


package org.kie.workbench.common.stunner.core.client.canvas.controls.select;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl.KogitoKeyPress;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeysMatcher;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.graph.Element;

import static org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key.ESC;

public abstract class AbstractSelectionControl<H extends AbstractCanvasHandler>
        implements SelectionControl<H, Element>,
                   CanvasRegistrationControl<H, Element>,
                   CanvasControl.SessionAware<ClientSession> {

    protected final Event<CanvasSelectionEvent> canvasSelectionEvent;
    protected Event<CanvasClearSelectionEvent> canvasClearSelectionEvent;
    private final MapSelectionControl<H> selectionControl;

    @Inject
    public AbstractSelectionControl(final Event<CanvasSelectionEvent> canvasSelectionEvent,
                                    final Event<CanvasClearSelectionEvent> clearSelectionEvent) {
        this.canvasSelectionEvent = canvasSelectionEvent;
        this.canvasClearSelectionEvent = clearSelectionEvent;
        this.selectionControl = MapSelectionControl.build(this::fireSelectionEvent,
                                                          this::fireClearSelectionEvent);
    }

    protected AbstractSelectionControl(final MapSelectionControl<H> selectionControl,
                                       final Event<CanvasSelectionEvent> canvasSelectionEvent,
                                       final Event<CanvasClearSelectionEvent> clearSelectionEvent) {
        this.selectionControl = selectionControl;
        this.canvasSelectionEvent = canvasSelectionEvent;
        this.canvasClearSelectionEvent = clearSelectionEvent;
    }

    @Override
    public final void init(final H canvasHandler) {
        selectionControl.init(canvasHandler);
        onEnable(canvasHandler);
    }

    protected void onEnable(final H canvasHandler) {
    }

    @Override
    public final void register(final Element element) {
        selectionControl.register(element);
        onRegister(element);
    }

    protected void onRegister(final Element element) {
    }

    @Override
    public final void deregister(final Element element) {
        selectionControl.deregister(element);
        onDeregister(element);
    }

    protected void onDeregister(final Element element) {
    }

    @Override
    public SelectionControl<H, Element> select(final String uuid) {
        selectionControl.select(uuid);
        onSelect(Collections.singletonList(uuid));
        return this;
    }

    protected void onSelect(final Collection<String> uuids) {
    }

    @Override
    public SelectionControl<H, Element> deselect(final String uuid) {
        selectionControl.deselect(uuid);
        return this;
    }

    @Override
    public boolean isSelected(final Element element) {
        return selectionControl.isSelected(element);
    }

    @Override
    public Collection<String> getSelectedItems() {
        return selectionControl.getSelectedItems();
    }

    @Override
    public final SelectionControl<H, Element> clearSelection() {
        selectionControl.clearSelection();
        onClearSelection();
        return this;
    }

    protected void onClearSelection() {
    }

    @Override
    public Optional<Object> getSelectedItemDefinition() {
        if (null != selectionControl) {
            return selectionControl.getSelectedItemDefinition();
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void clear() {
        clearSelection();
    }

    @Override
    public void destroy() {
        onDestroy();
        selectionControl.destroy();
    }

    protected void onDestroy() {
    }

    void onShapeRemovedEvent(final @Observes CanvasShapeRemovedEvent shapeRemovedEvent) {
        checkNotNull("event", shapeRemovedEvent);
        handleShapeRemovedEvent(shapeRemovedEvent);
    }

    void onCanvasElementSelectedEvent(final @Observes CanvasSelectionEvent event) {
        checkNotNull("event", event);
        handleCanvasElementSelectedEvent(event);
    }

    void onCanvasClearSelectionEvent(final @Observes CanvasClearSelectionEvent event) {
        checkNotNull("event", event);
        handleCanvasClearSelectionEvent(event);
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }

    protected void handleShapeRemovedEvent(final CanvasShapeRemovedEvent shapeRemovedEvent) {
        selectionControl.onShapeRemoved(shapeRemovedEvent);
    }

    protected void handleCanvasElementSelectedEvent(final CanvasSelectionEvent event) {
        selectionControl.onCanvasElementSelected(event);
    }

    protected void handleCanvasClearSelectionEvent(final CanvasClearSelectionEvent event) {
        selectionControl.onCanvasClearSelection(event);
    }

    @Override
    public void bind(final ClientSession session) {
        if (session instanceof EditorSession) {
            ((EditorSession) session).getKeyboardControl().addKeyShortcutCallback(this::onKeyDownEvent);
            ((EditorSession) session).getKeyboardControl().addKeyShortcutCallback(new KogitoKeyPress(new KeyboardEvent.Key[]{ESC}, "Edit | Unselect", this::clearSelection));
            selectionControl.setReadonly(false);
        }
    }

    protected MapSelectionControl<H> getSelectionControl() {
        return selectionControl;
    }

    private void onKeyDownEvent(final KeyboardEvent.Key... keys) {
        if (KeysMatcher.doKeysMatch(keys, ESC)) {
            clearSelection();
        }
    }

    private void fireSelectionEvent(final CanvasSelectionEvent e) {
        canvasSelectionEvent.fire(e);
        onSelect(e.getIdentifiers());
    }

    private void fireClearSelectionEvent(final CanvasClearSelectionEvent e) {
        canvasClearSelectionEvent.fire(e);
        onClearSelection();
    }

    public static Collection<String> shapesToIdentifiers(final Collection<?> shapes) {
        return shapes.stream()
                .filter(shape -> shape instanceof ShapeView)
                .map(shape -> ((ShapeView) shape).getUUID())
                .collect(Collectors.toList());
    }
}
