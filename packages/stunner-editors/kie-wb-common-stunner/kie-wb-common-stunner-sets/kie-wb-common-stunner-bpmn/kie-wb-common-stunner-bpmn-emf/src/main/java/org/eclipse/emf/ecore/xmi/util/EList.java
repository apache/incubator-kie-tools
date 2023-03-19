/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.eclipse.emf.ecore.xmi.util;

import java.util.List;

/**
 * A list that supports move.
 */
public interface EList<E> extends List<E> {

    /**
     * Moves the object to the new position, if is in the list.
     * @param newPosition the position of the object after the move.
     * @param object the object to move.
     */
    void move(int newPosition, E object);

    /**
     * Moves the object from the old position to the new position.
     * @param newPosition the position of the object after the move.
     * @param oldPosition the position of the object before the move.
     * @return the moved object.
     */
    E move(int newPosition, int oldPosition);
}
