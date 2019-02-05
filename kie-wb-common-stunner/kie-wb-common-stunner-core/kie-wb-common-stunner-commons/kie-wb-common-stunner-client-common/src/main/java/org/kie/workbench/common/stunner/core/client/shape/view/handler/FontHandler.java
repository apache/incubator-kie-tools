/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.function.Function;

import org.kie.workbench.common.stunner.core.client.shape.TextWrapperStrategy;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
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
    private final Function<W, HasTitle.Position> positionProvider;
    private final Function<W, Double> positionXOffsetProvider;
    private final Function<W, Double> positionYOffsetProvider;
    private final Function<W, Double> rotationProvider;
    private final Function<W, TextWrapperStrategy> textWrapperStrategyProvider;

    FontHandler(final Function<W, Double> alphaProvider,
                final Function<W, String> fontFamilyProvider,
                final Function<W, String> fontColorProvider,
                final Function<W, Double> fontSizeProvider,
                final Function<W, String> strokeColorProvider,
                final Function<W, Double> strokeSizeProvider,
                final Function<W, HasTitle.Position> positionProvider,
                final Function<W, Double> strokeAlphaProvider,
                final Function<W, Double> positionXOffsetProvider,
                final Function<W, Double> positionYOffsetProvider,
                final Function<W, Double> rotationProvider,
                final Function<W, TextWrapperStrategy> textWrapperStrategyProvider) {
        this.alphaProvider = alphaProvider;
        this.fontFamilyProvider = fontFamilyProvider;
        this.fontColorProvider = fontColorProvider;
        this.fontSizeProvider = fontSizeProvider;
        this.strokeColorProvider = strokeColorProvider;
        this.strokeSizeProvider = strokeSizeProvider;
        this.positionProvider = positionProvider;
        this.positionXOffsetProvider = positionXOffsetProvider;
        this.positionYOffsetProvider = positionYOffsetProvider;
        this.rotationProvider = rotationProvider;
        this.strokeAlphaProvider = strokeAlphaProvider;
        this.textWrapperStrategyProvider = textWrapperStrategyProvider;
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
            final HasTitle.Position position = positionProvider.apply(element);
            final Double positionXOffset = positionXOffsetProvider.apply(element);
            final Double positionYOffset = positionYOffsetProvider.apply(element);
            final Double rotation = rotationProvider.apply(element);
            final TextWrapperStrategy wrapperStrategy = textWrapperStrategyProvider.apply(element);
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
            if (null != position) {
                hasTitle.setTitlePosition(position);
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
                hasTitle.setTextWrapper(wrapperStrategy);
            }
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
        private Function<W, HasTitle.Position> positionProvider;
        private Function<W, Double> positionXOffsetProvider;
        private Function<W, Double> positionYOffsetProvider;
        private Function<W, Double> rotationProvider;
        private Function<W, TextWrapperStrategy> textWrapperStrategyProvider;

        public Builder() {
            this.alphaProvider = value -> null;
            this.fontFamilyProvider = value -> null;
            this.fontColorProvider = value -> null;
            this.fontSizeProvider = value -> null;
            this.strokeColorProvider = value -> null;
            this.strokeSizeProvider = value -> null;
            this.positionProvider = value -> null;
            this.positionXOffsetProvider = value -> null;
            this.positionYOffsetProvider = value -> null;
            this.rotationProvider = value -> null;
            this.strokeAlphaProvider = value -> null;
            this.textWrapperStrategyProvider = value -> null;
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

        public Builder<W, V> position(Function<W, HasTitle.Position> provider) {
            this.positionProvider = provider;
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

        public FontHandler<W, V> build() {
            return new FontHandler<>(alphaProvider,
                                     fontFamilyProvider,
                                     fontColorProvider,
                                     fontSizeProvider,
                                     strokeColorProvider,
                                     strokeSizeProvider,
                                     positionProvider,
                                     strokeAlphaProvider,
                                     positionXOffsetProvider,
                                     positionYOffsetProvider,
                                     rotationProvider,
                                     textWrapperStrategyProvider);
        }
    }
}