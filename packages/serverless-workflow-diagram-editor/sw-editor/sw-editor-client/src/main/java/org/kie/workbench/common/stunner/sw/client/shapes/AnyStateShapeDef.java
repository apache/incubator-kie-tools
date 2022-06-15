/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.client.shapes;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.core.client.shape.TextWrapperStrategy;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.TitleHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.ViewAttributesHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeViewDef;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGShapeViewDef;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeViewResources;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.sw.client.resources.GlyphFactory;
import org.kie.workbench.common.stunner.sw.client.resources.ShapeViewFactory;
import org.kie.workbench.common.stunner.sw.definition.CallFunctionAction;
import org.kie.workbench.common.stunner.sw.definition.CallSubflowAction;
import org.kie.workbench.common.stunner.sw.definition.End;
import org.kie.workbench.common.stunner.sw.definition.EventRef;
import org.kie.workbench.common.stunner.sw.definition.EventState;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.OnEvent;
import org.kie.workbench.common.stunner.sw.definition.Start;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;
import org.kie.workbench.common.stunner.sw.definition.Workflow;

public class AnyStateShapeDef<W> implements ShapeViewDef<W, SVGShapeView>,
                                            SVGShapeViewDef<W, ShapeViewFactory> {

    public static final TitleHandler<ShapeView> TITLE_HANDLER = new TitleHandler<>();
    private final ViewAttributesHandler<W, SVGShapeView> viewHandler;
    private final FontHandler<W, SVGShapeView> fontHandler;

    private final boolean isRectangularShape;

    public AnyStateShapeDef() {
        this(true);
    }

    public AnyStateShapeDef(boolean isRectangularShape) {
        this.isRectangularShape = isRectangularShape;
        viewHandler =
                new ViewAttributesHandler.Builder<W, SVGShapeView>().build();
        fontHandler =
                new DefaultFontHandlerBuilder<W, SVGShapeView>(isRectangularShape).build();
    }

    // TODO: Refactor this, no need for storing state...
    public static final SVGShapeViewResources<Object, ShapeViewFactory> VIEW_RESOURCES =
            new SVGShapeViewResources<Object, ShapeViewFactory>()
                    .put(InjectState.class, ShapeViewFactory::injectState)
                    .put(SwitchState.class, ShapeViewFactory::switchState)
                    .put(EventState.class, ShapeViewFactory::eventState)
                    // TODO: Why need for workflow here?
                    .put(Workflow.class, ShapeViewFactory::container)
                    .put(Start.class, ShapeViewFactory::startState)
                    .put(End.class, ShapeViewFactory::endState)
                    .put(OnEvent.class, ShapeViewFactory::container)
                    .put(EventRef.class, ShapeViewFactory::event)
                    .put(CallFunctionAction.class, ShapeViewFactory::action)
                    .put(CallSubflowAction.class, ShapeViewFactory::action);

    // TODO: Refactor this, no need for storing state...
    public static final Map<Class<?>, Glyph> GLYPHS =
            Stream.of(new AbstractMap.SimpleEntry<>(InjectState.class, GlyphFactory.STATE_INJECT),
                      new AbstractMap.SimpleEntry<>(SwitchState.class, GlyphFactory.STATE_SWITCH),
                      new AbstractMap.SimpleEntry<>(EventState.class, GlyphFactory.STATE_EVENT),
                      // TODO: Why need for workflow here?
                      new AbstractMap.SimpleEntry<>(Workflow.class, GlyphFactory.TRANSITION),
                      new AbstractMap.SimpleEntry<>(Start.class, GlyphFactory.START),
                      new AbstractMap.SimpleEntry<>(End.class, GlyphFactory.END),
                      new AbstractMap.SimpleEntry<>(OnEvent.class, GlyphFactory.EVENTS),
                      new AbstractMap.SimpleEntry<>(EventRef.class, GlyphFactory.EVENT),
                      new AbstractMap.SimpleEntry<>(CallFunctionAction.class, GlyphFactory.CALL_FUNCTION),
                      new AbstractMap.SimpleEntry<>(CallSubflowAction.class, GlyphFactory.CALL_SUBFLOW))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    @Override
    public SVGShapeView<?> newViewInstance(ShapeViewFactory factory, W instance) {
        return VIEW_RESOURCES.getResource(factory, instance).build(false);
    }

    @Override
    public Glyph getGlyph(final Class<? extends W> type,
                          final String defId) {
        return GLYPHS.get(type);
    }

    @Override
    public BiConsumer<W, SVGShapeView> viewHandler() {
        return viewHandler::handle;
    }

    @Override
    public Optional<BiConsumer<W, SVGShapeView>> fontHandler() {
        return Optional.of(fontHandler::handle);
    }

    @Override
    public Optional<BiConsumer<String, SVGShapeView>> titleHandler() {
        return Optional.of(TITLE_HANDLER::handle);
    }

    @Override
    public Class<ShapeViewFactory> getViewFactoryType() {
        return ShapeViewFactory.class;
    }

    @Override
    public Optional<BiConsumer<View<W>, SVGShapeView>> sizeHandler() {
        if (isRectangularShape) {
            return Optional.empty();
        }
        SizeHandler theSizeHandler = new SizeHandler.Builder<>()
                .radius(o -> 23d)
                .build();
        return Optional.of((BiConsumer<View<W>, SVGShapeView>) theSizeHandler::handle);
    }

    private static class DefaultFontHandlerBuilder<W, V extends ShapeView>
            extends FontHandler.Builder<W, V> {

        public DefaultFontHandlerBuilder(boolean isRectangularShape) {
            if (isRectangularShape) {
                initDefaultForRectangularShapes();
            } else {
                initDefaultForCircularShapes();
            }
        }

        private void initDefaultForRectangularShapes() {
            this.verticalAlignment(bean -> HasTitle.VerticalAlignment.MIDDLE)
                    .horizontalAlignment(bean -> HasTitle.HorizontalAlignment.CENTER)
                    .referencePosition(bean -> HasTitle.ReferencePosition.INSIDE)
                    .orientation(bean -> HasTitle.Orientation.HORIZONTAL)
                    .textSizeConstraints(bean -> new HasTitle.Size(100, 100, HasTitle.Size.SizeType.PERCENTAGE))
                    .textWrapperStrategy(bean -> TextWrapperStrategy.TRUNCATE_WITH_LINE_BREAK);
        }

        private void initDefaultForCircularShapes() {
            this.verticalAlignment(bean -> HasTitle.VerticalAlignment.BOTTOM)
                    .horizontalAlignment(bean -> HasTitle.HorizontalAlignment.CENTER)
                    .referencePosition(bean -> HasTitle.ReferencePosition.OUTSIDE)
                    .orientation(bean -> HasTitle.Orientation.HORIZONTAL)
                    .textSizeConstraints(bean -> new HasTitle.Size(100, 100, HasTitle.Size.SizeType.PERCENTAGE))
                    .textWrapperStrategy(bean -> TextWrapperStrategy.TRUNCATE_WITH_LINE_BREAK);
        }

        // TODO: If this method not present, why not text algin on bottom works?
        public static Double getStrokeAlpha(Double strokeWidth) {
            return Optional.ofNullable(strokeWidth)
                    .filter(value -> value > 0)
                    .map(value -> 1.0)
                    .orElse(0.0);
        }
    }
}
