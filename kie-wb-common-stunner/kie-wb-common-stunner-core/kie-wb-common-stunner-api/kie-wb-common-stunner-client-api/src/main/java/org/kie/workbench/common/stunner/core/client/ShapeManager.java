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

package org.kie.workbench.common.stunner.core.client;

import com.google.gwt.safehtml.shared.SafeUri;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;

import java.util.Collection;

public interface ShapeManager {

    Collection<ShapeSet<?>> getShapeSets();

    ShapeSet<?> getShapeSet( String id );

    ShapeSet<?> getDefaultShapeSet( String definitionSetId );

    ShapeFactory getFactory( String definitionId );

    SafeUri getThumbnail( String definitionSetId );

}
