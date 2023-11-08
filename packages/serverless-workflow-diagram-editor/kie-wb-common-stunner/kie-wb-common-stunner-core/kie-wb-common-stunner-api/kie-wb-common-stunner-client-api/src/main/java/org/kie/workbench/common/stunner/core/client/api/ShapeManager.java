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


package org.kie.workbench.common.stunner.core.client.api;

import java.util.Collection;

import org.kie.workbench.common.stunner.core.client.ShapeSet;

public interface ShapeManager {

    /**
     * Returns the available Shape Sets.
     */
    Collection<ShapeSet<?>> getShapeSets();

    /**
     * Returns the Shape Set instance by its identifier.
     */
    ShapeSet<?> getShapeSet(final String shapeSetId);

    /**
     * Returns the default Shape Set instance for the given Definition Set identifier.
     */
    ShapeSet<?> getDefaultShapeSet(final String definitionSetId);

    /**
     * Returns the uri for the default icon for the given Definition Set.
     */
    String getThumbnail(final String definitionSetId);
}
