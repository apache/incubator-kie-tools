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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout;
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
import org.kie.workbench.common.stunner.sw.definition.ActionsContainer;
import org.kie.workbench.common.stunner.sw.definition.CallFunctionAction;
import org.kie.workbench.common.stunner.sw.definition.CallSubflowAction;
import org.kie.workbench.common.stunner.sw.definition.CallbackState;
import org.kie.workbench.common.stunner.sw.definition.End;
import org.kie.workbench.common.stunner.sw.definition.EventRef;
import org.kie.workbench.common.stunner.sw.definition.EventState;
import org.kie.workbench.common.stunner.sw.definition.EventTimeout;
import org.kie.workbench.common.stunner.sw.definition.ForEachState;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.OnEvent;
import org.kie.workbench.common.stunner.sw.definition.OperationState;
import org.kie.workbench.common.stunner.sw.definition.ParallelState;
import org.kie.workbench.common.stunner.sw.definition.SleepState;
import org.kie.workbench.common.stunner.sw.definition.Start;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;
import org.kie.workbench.common.stunner.sw.definition.Workflow;

public class AnyStateShapeDef<W> implements ShapeViewDef<W, SVGShapeView>,
                                            SVGShapeViewDef<W, ShapeViewFactory> {

    public static final TitleHandler<ShapeView> TITLE_HANDLER = new TitleHandler<>();
    private final ViewAttributesHandler<W, SVGShapeView> viewHandler;
    private final FontHandler<W, SVGShapeView> fontHandler;

    private final boolean isSymmetric;

    private final FontStyle fontStyle;

    public enum FontStyle {
        INSIDE_CENTER_WITH_AlPHA,
        INSIDE_CENTER,
        OUTSIDE_CENTER_BOTTOM,
        INSIDE_LEFT_WITH_MARGIN
    }

    public AnyStateShapeDef() {
        this(FontStyle.INSIDE_LEFT_WITH_MARGIN, false);
    }

    public AnyStateShapeDef(FontStyle fontStyle) {
        this(fontStyle, false);
    }

    public AnyStateShapeDef(FontStyle fontStyle, boolean isSymmetric) {
        this.isSymmetric = isSymmetric;
        this.fontStyle = fontStyle;
        viewHandler =
                new ViewAttributesHandler.Builder<W, SVGShapeView>().build();
        fontHandler =
                new DefaultFontHandlerBuilder<W, SVGShapeView>(this.fontStyle).build();
    }

    // TODO: Refactor this, no need for storing state...
    public static final SVGShapeViewResources<Object, ShapeViewFactory> VIEW_RESOURCES =
            new SVGShapeViewResources<Object, ShapeViewFactory>()
                    .put(InjectState.class, ShapeViewFactory::injectState)
                    .put(SwitchState.class, ShapeViewFactory::switchState)
                    .put(EventState.class, ShapeViewFactory::eventState)
                    .put(OperationState.class, ShapeViewFactory::operationState)
                    .put(SleepState.class, ShapeViewFactory::sleepState)
                    .put(ParallelState.class, ShapeViewFactory::parallelState)
                    .put(ForEachState.class, ShapeViewFactory::forEachState)
                    .put(CallbackState.class, ShapeViewFactory::callbackState)
                    .put(Workflow.class, ShapeViewFactory::container)
                    .put(Start.class, ShapeViewFactory::startState)
                    .put(End.class, ShapeViewFactory::endState)
                    .put(ActionsContainer.class, ShapeViewFactory::container)
                    .put(OnEvent.class, ShapeViewFactory::container)
                    .put(EventRef.class, ShapeViewFactory::event)
                    .put(EventTimeout.class, ShapeViewFactory::eventTimeout)
                    .put(CallFunctionAction.class, ShapeViewFactory::action)
                    .put(CallSubflowAction.class, ShapeViewFactory::action);

    // TODO: Refactor this, no need for storing state...
    public static final Map<Class<?>, Glyph> GLYPHS =
            new HashMap<Class<?>, Glyph>() {{
                put(InjectState.class, GlyphFactory.STATE_INJECT);
                put(SwitchState.class, GlyphFactory.STATE_SWITCH);
                put(EventState.class, GlyphFactory.STATE_EVENT);
                put(OperationState.class, GlyphFactory.STATE_OPERATION);
                put(SleepState.class, GlyphFactory.STATE_INJECT);
                put(ParallelState.class, GlyphFactory.STATE_INJECT);
                put(ForEachState.class, GlyphFactory.STATE_INJECT);
                put(CallbackState.class, GlyphFactory.STATE_INJECT);
                put(Workflow.class, GlyphFactory.TRANSITION);
                put(Start.class, GlyphFactory.START);
                put(End.class, GlyphFactory.END);
                put(ActionsContainer.class, GlyphFactory.CALL_FUNCTION);
                put(OnEvent.class, GlyphFactory.EVENTS);
                put(EventRef.class, GlyphFactory.EVENT);
                put(EventTimeout.class, GlyphFactory.EVENT_TIMEOUT);
                put(CallFunctionAction.class, GlyphFactory.CALL_FUNCTION);
                put(CallSubflowAction.class, GlyphFactory.CALL_SUBFLOW);
            }};

    @Override
    public SVGShapeView<?> newViewInstance(ShapeViewFactory factory, W instance) {
        /*if (instance instanceof OnEvent) {
            return VIEW_RESOURCES.getResource(factory, instance).build(295d,100d, false);
        }
        if (instance instanceof ActionsContainer) {
            return VIEW_RESOURCES.getResource(factory, instance).build(185d, 160d, false);
        }
        if (instance instanceof Timeout) {
            return VIEW_RESOURCES.getResource(factory, instance).build(28d, 28d, false);
        }*/
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
        final SizeHandler theSizeHandler;

        // Asymmetric shapes such as rectangles
        if (!isSymmetric) {
            theSizeHandler = new SizeHandler.Builder<>()
                    .maxWidth(o -> 90d)
                    .maxHeight(o -> 90d)
                    .build();
            return Optional.of((BiConsumer<View<W>, SVGShapeView>) theSizeHandler::handle);
        }

        // Symmetric shapes such as a perfect square or circle
        theSizeHandler = new SizeHandler.Builder<>()
                .radius(o -> 23d)
                .build();
        return Optional.of((BiConsumer<View<W>, SVGShapeView>) theSizeHandler::handle);
    }

    private static class DefaultFontHandlerBuilder<W, V extends ShapeView>
            extends FontHandler.Builder<W, V> {

        public DefaultFontHandlerBuilder(FontStyle fontStyle) {
            switch (fontStyle) {
                case INSIDE_CENTER_WITH_AlPHA:
                    initInsideCenterWithAlpha();
                    break;
                case INSIDE_CENTER:
                    initInsideCenter();
                    break;
                case OUTSIDE_CENTER_BOTTOM:
                    initOutsideCenterBottom();
                    break;
                default:
                    initInsideLeftWithMargin();
            }
        }

        private void initInsideLeftWithMargin() {
            this.verticalAlignment(bean -> HasTitle.VerticalAlignment.MIDDLE)
                    .horizontalAlignment(bean -> HasTitle.HorizontalAlignment.LEFT)
                    .referencePosition(bean -> HasTitle.ReferencePosition.INSIDE)
                    .orientation(bean -> HasTitle.Orientation.HORIZONTAL)
                    .textSizeConstraints(bean -> new HasTitle.Size(95, 95, HasTitle.Size.SizeType.PERCENTAGE))
                    .textWrapperStrategy(bean -> TextWrapperStrategy.TRUNCATE_WITH_LINE_BREAK)
                    .margins(o -> new HashMap<Enum, Double>() {{
                        put(DirectionLayout.HorizontalAlignment.LEFT, 85d);
                    }})
                    .alpha(bean -> 1d)
                    .strokeAlpha(bean -> 0d);
        }

        private void initInsideCenterWithAlpha() {
            this.verticalAlignment(bean -> HasTitle.VerticalAlignment.MIDDLE)
                    .horizontalAlignment(bean -> HasTitle.HorizontalAlignment.CENTER)
                    .referencePosition(bean -> HasTitle.ReferencePosition.INSIDE)
                    .orientation(bean -> HasTitle.Orientation.HORIZONTAL)
                    .textSizeConstraints(bean -> new HasTitle.Size(95, 95, HasTitle.Size.SizeType.PERCENTAGE))
                    .textWrapperStrategy(bean -> TextWrapperStrategy.TRUNCATE_WITH_LINE_BREAK)
                    .alpha(bean -> 0.4d)
                    .strokeAlpha(bean -> 0d);
        }

        private void initInsideCenter() {
            this.verticalAlignment(bean -> HasTitle.VerticalAlignment.MIDDLE)
                    .horizontalAlignment(bean -> HasTitle.HorizontalAlignment.CENTER)
                    .referencePosition(bean -> HasTitle.ReferencePosition.INSIDE)
                    .orientation(bean -> HasTitle.Orientation.HORIZONTAL)
                    .textSizeConstraints(bean -> new HasTitle.Size(95, 95, HasTitle.Size.SizeType.PERCENTAGE))
                    .textWrapperStrategy(bean -> TextWrapperStrategy.TRUNCATE_WITH_LINE_BREAK)
                    .alpha(bean -> 1d)
                    .strokeAlpha(bean -> 0d);
        }

        private void initOutsideCenterBottom() {
            this.verticalAlignment(bean -> HasTitle.VerticalAlignment.BOTTOM)
                    .horizontalAlignment(bean -> HasTitle.HorizontalAlignment.CENTER)
                    .referencePosition(bean -> HasTitle.ReferencePosition.OUTSIDE)
                    .orientation(bean -> HasTitle.Orientation.HORIZONTAL)
                    .textSizeConstraints(bean -> new HasTitle.Size(95, 95, HasTitle.Size.SizeType.PERCENTAGE))
                    .textWrapperStrategy(bean -> TextWrapperStrategy.TRUNCATE_WITH_LINE_BREAK)
                    .alpha(bean -> 1d)
                    .strokeAlpha(bean -> 0d);
        }
    }
}
