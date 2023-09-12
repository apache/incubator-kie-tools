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
package org.kie.workbench.common.dmn.client.shape.def;

import java.util.Optional;
import java.util.function.BiConsumer;

import org.kie.workbench.common.dmn.api.definition.DMNViewDefinition;
import org.kie.workbench.common.dmn.client.shape.view.handlers.DMNViewHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.TitleHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.ViewAttributesHandler;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGShapeViewDef;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public interface DMNSVGShapeDef<W extends DMNViewDefinition, F>
        extends DMNShapeDef<W, SVGShapeView>,
                SVGShapeViewDef<W, F> {

    @Override
    default Optional<BiConsumer<String, SVGShapeView>> titleHandler() {
        return Optional.of(newTitleHandler()::handle);
    }

    @Override
    @SuppressWarnings("unchecked")
    default Optional<BiConsumer<W, SVGShapeView>> fontHandler() {
        return Optional.of(newFontHandler()::handle);
    }

    @Override
    default Optional<BiConsumer<View<W>, SVGShapeView>> sizeHandler() {
        return Optional.of(newSizeHandler()::handle);
    }

    @Override
    default BiConsumer<W, SVGShapeView> viewHandler() {
        return newViewAttributesHandler()::handle;
    }

    default TitleHandler<ShapeView> newTitleHandler() {
        return DMNViewHandlers.TITLE_HANDLER;
    }

    default DMNViewHandlers.FontHandlerBuilder<W, SVGShapeView> newFontHandlerBuilder() {
        return new DMNViewHandlers.FontHandlerBuilder<>();
    }

    default SizeHandler<W, SVGShapeView> newSizeHandler() {
        return new DMNViewHandlers.SizeHandlerBuilder<W, SVGShapeView>().build();
    }

    default DMNViewHandlers.ViewAttributesHandlerBuilder<W, SVGShapeView> newViewAttributesHandlerBuilder() {
        return new DMNViewHandlers.ViewAttributesHandlerBuilder<>();
    }

    default FontHandler<W, SVGShapeView> newFontHandler() {
        return newFontHandlerBuilder().build();
    }

    default ViewAttributesHandler<W, SVGShapeView> newViewAttributesHandler() {
        return newViewAttributesHandlerBuilder().build();
    }
}
