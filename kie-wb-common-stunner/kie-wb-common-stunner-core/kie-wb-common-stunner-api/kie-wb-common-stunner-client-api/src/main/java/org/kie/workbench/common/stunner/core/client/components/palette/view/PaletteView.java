/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.components.palette.view;

public interface PaletteView<T, L, I extends PaletteElementView> {

    T setX(final double x);

    T setY(final double y);

    double getX();

    double getY();

    double getWidth();

    double getHeight();

    T add(final I item);

    T set(final int pos,
          final I item);

    T remove(final int pos);

    T attach(final L layer);

    T show();

    T hide();

    T clear();

    void destroy();
}
