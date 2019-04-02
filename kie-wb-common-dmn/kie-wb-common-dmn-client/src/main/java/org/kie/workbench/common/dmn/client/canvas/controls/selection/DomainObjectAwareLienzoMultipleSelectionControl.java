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

package org.kie.workbench.common.dmn.client.canvas.controls.selection;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.LienzoMultipleSelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.MultipleSelection;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Element;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * Specializes {@link LienzoMultipleSelectionControl} to also support selection of a single {@link DomainObject}.
 * Selection of an {@link Element}, clearance of the canvas or destruction of the underlying session also deselects
 * any {@link DomainObject}.
 * @param <H> {@link AbstractCanvasHandler}
 */
@Dependent
@MultipleSelection
@DMNEditor
public class DomainObjectAwareLienzoMultipleSelectionControl<H extends AbstractCanvasHandler> extends LienzoMultipleSelectionControl<H> {

    private Optional<DomainObject> selectedDomainObject = Optional.empty();

    @Inject
    public DomainObjectAwareLienzoMultipleSelectionControl(final Event<CanvasSelectionEvent> canvasSelectionEvent,
                                                           final Event<CanvasClearSelectionEvent> clearSelectionEvent) {
        super(canvasSelectionEvent,
              clearSelectionEvent);
    }

    @Override
    public Optional<Object> getSelectedItemDefinition() {
        if (selectedDomainObject.isPresent()) {
            return Optional.of(selectedDomainObject.get());
        } else {
            return super.getSelectedItemDefinition();
        }
    }

    @Override
    protected void onSelect(final Collection<String> uuids) {
        selectedDomainObject = Optional.empty();
        super.onSelect(uuids);
    }

    @Override
    public SelectionControl<H, Element> select(final String uuid) {
        selectedDomainObject = Optional.empty();
        return super.select(uuid);
    }

    @Override
    public void clear() {
        selectedDomainObject = Optional.empty();
        super.clear();
    }

    @Override
    protected void onClearSelection() {
        selectedDomainObject = Optional.empty();
        super.onClearSelection();
    }

    @Override
    public void destroy() {
        selectedDomainObject = Optional.empty();
        super.destroy();
    }

    @Override
    protected void onDestroy() {
        selectedDomainObject = Optional.empty();
        super.onDestroy();
    }

    @Override
    protected void handleCanvasElementSelectedEvent(final CanvasSelectionEvent event) {
        selectedDomainObject = Optional.empty();
        super.handleCanvasElementSelectedEvent(event);
    }

    @Override
    protected void handleCanvasClearSelectionEvent(final CanvasClearSelectionEvent event) {
        selectedDomainObject = Optional.empty();
        super.handleCanvasClearSelectionEvent(event);
    }

    void handleDomainObjectSelectedEvent(final @Observes DomainObjectSelectionEvent event) {
        checkNotNull("event", event);
        if (Objects.equals(getCanvasHandler(), event.getCanvasHandler())) {
            selectedDomainObject = Optional.ofNullable(event.getDomainObject());
        }
    }

    private AbstractCanvasHandler getCanvasHandler() {
        return getSelectionControl().getCanvasHandler();
    }
}
