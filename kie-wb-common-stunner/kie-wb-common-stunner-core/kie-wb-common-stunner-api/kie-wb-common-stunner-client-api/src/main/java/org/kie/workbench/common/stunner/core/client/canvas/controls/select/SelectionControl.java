/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.canvas.controls.select;

import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasRegistationControl;
import org.kie.workbench.common.stunner.core.graph.Element;

import java.util.Collection;

/**
 * Mediator for elements selection operations in a canvas.
 */
public interface SelectionControl<C extends CanvasHandler, E extends Element> extends CanvasRegistationControl<C, E> {

    SelectionControl<C, E> select( E item );

    SelectionControl<C, E> deselect( E item );

    boolean isSelected( final E item );

    Collection<String> getSelectedItems();

    SelectionControl<C, E> clearSelection();

}
