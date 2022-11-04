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

package org.kie.workbench.common.stunner.core.client.shape.view.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.core.client.shape.TextWrapperStrategy;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle.HorizontalAlignment;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle.Orientation;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle.ReferencePosition;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle.Size;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle.VerticalAlignment;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeViewHandler;

/**
 * The default view font related attributes handler to generic shape views.
 * <p>
 * It allows specifying functions which provide the different attributes for
 * handling the resulting canvas shape's font styles.
 * @param <W> The domain's object type.
 * @param <V> The shape view type.
 */
public class FontHandler<W, V extends ShapeView> implements ShapeViewHandler<W, V> {

    private final Function<W, Double> alphaProvider;
    private final Function<W, String> fontFamilyProvider;
    private final Function<W, String> fontColorProvider;
    private final Function<W, Double> fontSizeProvider;
    private final Function<W, String> strokeColorProvider;
    private final Function<W, Double> strokeSizeProvider;
    private final Function<W, Double> strokeAlphaProvider;
    private final Function<W, Double> positionXOffsetProvider;
    private final Function<W, Double> positionYOffsetProvider;
    private final Function<W, Double> rotationProvider;
    private final Function<W, TextWrapperStrategy> textWrapperStrategyProvider;
    private Function<W, HasTitle.VerticalAlignment> verticalAlignmentProvider;
    private Function<W, HasTitle.HorizontalAlignment> horizontalAlignmentProvider;
    private Function<W, HasTitle.ReferencePosition> referencePositionProvider;
    private Function<W, HasTitle.Orientation> orientationProvider;
    private final Function<W, Size> sizeConstraintsProvider;
    private final Map<Enum, Double> margins;
    private final Function<W, Map<Enum, Double>> marginsProvider;

    FontHandler(final Function<W, Double> alphaProvider,
                final Function<W, String> fontFamilyProvider,
                final Function<W, String> fontColorProvider,
                final Function<W, Double> fontSizeProvider,
                final Function<W, String> strokeColorProvider,
                final Function<W, Double> strokeSizeProvider,
                final Function<W, Double> strokeAlphaProvider,
                final Function<W, Double> positionXOffsetProvider,
                final Function<W, Double> positionYOffsetProvider,
                final Function<W, Double> rotationProvider,
                final Function<W, TextWrapperStrategy> textWrapperStrategyProvider,
                final Function<W, VerticalAlignment> verticalAlignmentProvider,
                final Function<W, HorizontalAlignment> horizontalAlignmentProvider,
                final Function<W, ReferencePosition> referencePositionProvider,
                final Function<W, Orientation> orientationProvider,
                final Function<W, Size> sizeConstraintsProvider,
                final Map<Enum, Double> margins,
                final Function<W, Map<Enum, Double>> marginsProvider) {
        this.alphaProvider = alphaProvider;
        this.fontFamilyProvider = fontFamilyProvider;
        this.fontColorProvider = fontColorProvider;
        this.fontSizeProvider = fontSizeProvider;
        this.strokeColorProvider = strokeColorProvider;
        this.strokeSizeProvider = strokeSizeProvider;
        this.strokeAlphaProvider = strokeAlphaProvider;
        this.positionXOffsetProvider = positionXOffsetProvider;
        this.positionYOffsetProvider = positionYOffsetProvider;
        this.rotationProvider = rotationProvider;
        this.textWrapperStrategyProvider = textWrapperStrategyProvider;
        this.verticalAlignmentProvider = verticalAlignmentProvider;
        this.horizontalAlignmentProvider = horizontalAlignmentProvider;
        this.referencePositionProvider = referencePositionProvider;
        this.orientationProvider = orientationProvider;
        this.sizeConstraintsProvider = sizeConstraintsProvider;
        this.margins = margins;
        this.marginsProvider = marginsProvider;
    }

    @Override
    public void handle(final W element,
                       final V view) {
        if (view instanceof HasTitle) {
            final HasTitle hasTitle = (HasTitle) view;
            final Double alpha = alphaProvider.apply(element);
            final String fontFamily = fontFamilyProvider.apply(element);
            final String fontColor = fontColorProvider.apply(element);
            final Double fontSize = fontSizeProvider.apply(element);
            final String strokeColor = strokeColorProvider.apply(element);
            final Double strokeSize = strokeSizeProvider.apply(element);
            final Double strokeAlpha = strokeAlphaProvider.apply(element);
            final Double positionXOffset = positionXOffsetProvider.apply(element);
            final Double positionYOffset = positionYOffsetProvider.apply(element);
            final Double rotation = rotationProvider.apply(element);
            final TextWrapperStrategy wrapperStrategy = textWrapperStrategyProvider.apply(element);
            final Size sizeConstraints = sizeConstraintsProvider.apply(element);
            final VerticalAlignment verticalAlignment = verticalAlignmentProvider.apply(element);
            final HorizontalAlignment horizontalAlignment = horizontalAlignmentProvider.apply(element);
            final ReferencePosition referencePosition = referencePositionProvider.apply(element);
            final Orientation orientation = orientationProvider.apply(element);
            final Map<Enum, Double> margins = marginsProvider.apply(element);

            if (fontFamily != null && fontFamily.trim().length() > 0) {
                hasTitle.setTitleFontFamily(fontFamily);
            }
            if (fontColor != null && fontColor.trim().length() > 0) {
                hasTitle.setTitleFontColor(fontColor);
            }
            if (strokeColor != null && strokeColor.trim().length() > 0) {
                hasTitle.setTitleStrokeColor(strokeColor);
            }
            if (fontSize != null) {
                hasTitle.setTitleFontSize(fontSize);
            }
            if (strokeSize != null) {
                hasTitle.setTitleStrokeWidth(strokeSize);
            }
            if (strokeAlpha != null) {
                hasTitle.setTitleStrokeAlpha(strokeAlpha);
            }
            if (null != alpha) {
                hasTitle.setTitleAlpha(alpha);
            }
            if (null != positionXOffset) {
                hasTitle.setTitleXOffsetPosition(positionXOffset);
            }
            if (null != positionYOffset) {
                hasTitle.setTitleYOffsetPosition(positionYOffset);
            }
            if (null != rotation) {
                hasTitle.setTitleRotation(rotation);
            }
            if (wrapperStrategy != null) {
                hasTitle.setTitleWrapper(wrapperStrategy);
            }

            Optional.ofNullable(margins).ifPresent(m -> this.margins.putAll(m));
            if (!this.margins.isEmpty()) {
                hasTitle.setMargins(this.margins);
            }

            if (sizeConstraints != null) {
                hasTitle.setTitleSizeConstraints(sizeConstraints);
            }

            applyTextPosition(hasTitle, rotation, verticalAlignment, horizontalAlignment, referencePosition,
                              orientation);
        }
    }

    private void applyTextPosition(final HasTitle hasTitle, final Double rotation,
                                   final VerticalAlignment verticalAlignment, final HorizontalAlignment horizontalAlignment,
                                   final ReferencePosition referencePosition, final Orientation orientation) {

        Orientation currentOrientation = orientation;
        //apply text position
        if (rotation != null) {
            switch (rotation.intValue()) {
                case 90:
                case 270:
                    currentOrientation = Orientation.VERTICAL;
                    break;
                default:
                    currentOrientation = Orientation.HORIZONTAL;
            }
        }

        if (Stream.of(verticalAlignment, horizontalAlignment, referencePosition, currentOrientation).allMatch(Objects::nonNull)) {
            hasTitle.setTitlePosition(verticalAlignment, horizontalAlignment, referencePosition, currentOrientation);
        }
    }

    public static class Builder<W, V extends ShapeView> {

        private Function<W, Double> alphaProvider;
        private Function<W, String> fontFamilyProvider;
        private Function<W, String> fontColorProvider;
        private Function<W, Double> fontSizeProvider;
        private Function<W, String> strokeColorProvider;
        private Function<W, Double> strokeSizeProvider;
        private Function<W, Double> strokeAlphaProvider;
        private Function<W, Double> positionXOffsetProvider;
        private Function<W, Double> positionYOffsetProvider;
        private Function<W, Double> rotationProvider;
        private Function<W, TextWrapperStrategy> textWrapperStrategyProvider;
        private Function<W, HasTitle.VerticalAlignment> verticalAlignmentProvider;
        private Function<W, HasTitle.HorizontalAlignment> horizontalAlignmentProvider;
        private Function<W, HasTitle.ReferencePosition> referencePositionProvider;
        private Function<W, HasTitle.Orientation> orientationProvider;
        private Function<W, Size> sizeConstraintsProvider;
        private final Map<Enum, Double> margins = new HashMap<>();
        private Function<W, Map<Enum, Double>> marginsProvider;

        public Builder() {
            this.alphaProvider = value -> null;
            this.fontFamilyProvider = value -> null;
            this.fontColorProvider = value -> null;
            this.fontSizeProvider = value -> null;
            this.strokeColorProvider = value -> null;
            this.strokeSizeProvider = value -> null;
            this.positionXOffsetProvider = value -> null;
            this.positionYOffsetProvider = value -> null;
            this.rotationProvider = value -> null;
            this.strokeAlphaProvider = value -> null;
            this.textWrapperStrategyProvider = value -> null;
            this.verticalAlignmentProvider = value -> null;
            this.horizontalAlignmentProvider = value -> null;
            this.referencePositionProvider = value -> null;
            this.orientationProvider = value -> null;
            this.sizeConstraintsProvider = value -> null;
            this.marginsProvider = value -> null;
        }

        public Builder<W, V> alpha(Function<W, Double> alphaProvider) {
            this.alphaProvider = alphaProvider;
            return this;
        }

        public Builder<W, V> fontFamily(Function<W, String> provider) {
            this.fontFamilyProvider = provider;
            return this;
        }

        public Builder<W, V> fontColor(Function<W, String> provider) {
            this.fontColorProvider = provider;
            return this;
        }

        public Builder<W, V> fontSize(Function<W, Double> provider) {
            this.fontSizeProvider = provider;
            return this;
        }

        public Builder<W, V> strokeColor(Function<W, String> provider) {
            this.strokeColorProvider = provider;
            return this;
        }

        public Builder<W, V> strokeSize(Function<W, Double> provider) {
            this.strokeSizeProvider = provider;
            return this;
        }

        public Builder<W, V> strokeAlpha(Function<W, Double> provider) {
            this.strokeAlphaProvider = provider;
            return this;
        }

        public Builder<W, V> verticalAlignment(Function<W, HasTitle.VerticalAlignment> provider) {
            this.verticalAlignmentProvider = provider;
            return this;
        }

        public Builder<W, V> horizontalAlignment(Function<W, HasTitle.HorizontalAlignment> provider) {
            this.horizontalAlignmentProvider = provider;
            return this;
        }

        public Builder<W, V> referencePosition(Function<W, HasTitle.ReferencePosition> provider) {
            this.referencePositionProvider = provider;
            return this;
        }

        public Builder<W, V> orientation(Function<W, HasTitle.Orientation> provider) {
            this.orientationProvider = provider;
            return this;
        }

        public Builder<W, V> positionXOffset(Function<W, Double> provider) {
            this.positionXOffsetProvider = provider;
            return this;
        }

        public Builder<W, V> positionYOffset(Function<W, Double> provider) {
            this.positionYOffsetProvider = provider;
            return this;
        }

        public Builder<W, V> rotation(Function<W, Double> provider) {
            this.rotationProvider = provider;
            return this;
        }

        public Builder<W, V> textWrapperStrategy(Function<W, TextWrapperStrategy> provider) {
            this.textWrapperStrategyProvider = provider;
            return this;
        }

        public Builder<W, V> textSizeConstraints(Function<W, Size> provider) {
            this.sizeConstraintsProvider = provider;
            return this;
        }

        public Builder<W, V> margin(Enum direction, Double margin) {
            margins.put(direction, margin);
            return this;
        }

        public Builder<W, V> margins(Function<W, Map<Enum, Double>> marginsProvider) {

            this.marginsProvider = marginsProvider;
            return this;
        }

        public FontHandler<W, V> build() {
            return new FontHandler<>(alphaProvider,
                                     fontFamilyProvider,
                                     fontColorProvider,
                                     fontSizeProvider,
                                     strokeColorProvider,
                                     strokeSizeProvider,
                                     strokeAlphaProvider,
                                     positionXOffsetProvider,
                                     positionYOffsetProvider,
                                     rotationProvider,
                                     textWrapperStrategyProvider,
                                     verticalAlignmentProvider,
                                     horizontalAlignmentProvider,
                                     referencePositionProvider,
                                     orientationProvider,
                                     sizeConstraintsProvider,
                                     margins,
                                     marginsProvider);
        }
    }
}