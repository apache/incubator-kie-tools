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

package org.kie.workbench.common.stunner.core.client.canvas;

public abstract class AbstractCanvasGrid implements CanvasGrid {

    private final double primSize;
    private final double primAlpha;
    private final String primColor;
    private final double secSize;
    private final double secAlpha;
    private final String secColor;

    public AbstractCanvasGrid( final double primSize,
                               final double primAlpha,
                               final String primColor,
                               final double secSize,
                               final double secAlpha,
                               final String secColor ) {
        this.primSize = primSize;
        this.primAlpha = primAlpha;
        this.primColor = primColor;
        this.secSize = secSize;
        this.secAlpha = secAlpha;
        this.secColor = secColor;
    }

    @Override
    public double getPrimarySize() {
        return primSize;
    }

    @Override
    public double getPrimaryAlpha() {
        return primAlpha;
    }

    @Override
    public String getPrimaryColor() {
        return primColor;
    }

    @Override
    public double getSecondarySize() {
        return secSize;
    }

    @Override
    public double getSecondaryAlpha() {
        return secAlpha;
    }

    @Override
    public String getSecondaryColor() {
        return secColor;
    }
}
