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


package org.kie.workbench.common.stunner.core.client.canvas.controls;

import java.util.Collection;
import java.util.Optional;

import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Element;

/**
 * Mediator for elements selection operations in a canvas.
 */
public interface SelectionControl<C extends CanvasHandler, E extends Element>
        extends CanvasControl<C> {

    SelectionControl<C, E> select(String itemId);

    SelectionControl<C, E> deselect(String itemId);

    boolean isSelected(E item);

    Collection<String> getSelectedItems();

    SelectionControl<C, E> clearSelection();

    /**
     * Gets the first selected object; whether a {@link Element} or a {@link DomainObject}.
     * If multiple {@link Element} are selected the first selected will be returned. If
     * no {@link Element} or {@link DomainObject} is selected this will attempt to return
     * the {@link Diagram} root {@link Element}.
     * @return The selected object or {@link Optional#empty()}
     */
    Optional<Object> getSelectedItemDefinition();
}