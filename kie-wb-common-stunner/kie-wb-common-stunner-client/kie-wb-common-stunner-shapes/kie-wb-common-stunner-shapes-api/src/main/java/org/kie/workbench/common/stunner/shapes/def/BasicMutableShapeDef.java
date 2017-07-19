/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.shapes.def;

import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.MutableShapeDef;

public interface BasicMutableShapeDef<W>
        extends BasicShapeDef<W>,
                MutableShapeDef<W> {

    String getBackgroundColor(final W element);

    double getBackgroundAlpha(final W element);

    String getBorderColor(final W element);

    double getBorderSize(final W element);

    double getBorderAlpha(final W element);

    @Override
    default double getAlpha(final W element) {
        return 1;
    }

    @Override
    default String getFontFamily(final W element) {
        return "Verdana";
    }

    @Override
    default String getFontColor(final W element) {
        return "#000000";
    }

    @Override
    default String getFontBorderColor(final W element) {
        return "#000000";
    }

    @Override
    default double getFontSize(final W element) {
        return 10;
    }

    @Override
    default double getFontBorderSize(final W element) {
        return 1;
    }

    @Override
    default HasTitle.Position getFontPosition(final W element) {
        return HasTitle.Position.CENTER;
    }

    @Override
    default double getFontRotation(final W element) {
        return 0;
    }
}
