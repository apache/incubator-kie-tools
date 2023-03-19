/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.displayer.impl;

import org.dashbuilder.displayer.SelectorDisplayerSettingsBuilder;

public abstract class AbstractSelectorSettingsBuilder<T extends SelectorDisplayerSettingsBuilder> extends AbstractDisplayerSettingsBuilder<T> implements SelectorDisplayerSettingsBuilder<T> {

    @Override
    public T width(int width) {
        displayerSettings.setSelectorWidth(width);
        return (T) this;
    }

    @Override
    public T margins(int top, int bottom, int left, int right) {
        displayerSettings.setChartMarginTop(top);
        displayerSettings.setChartMarginBottom(bottom);
        displayerSettings.setChartMarginLeft(left);
        displayerSettings.setChartMarginRight(right);
        return (T) this;
    }

    @Override
    public T multiple(boolean multiple) {
        displayerSettings.setSelectorMultiple(multiple);
        return (T) this;
    }
}
