/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.shape.view.glyph;

public abstract class AbstractGlyph<G> implements Glyph<G> {

    protected final G group;
    protected final double width;
    protected final double height;

    protected AbstractGlyph( final G group,
                             final double width,
                             final double height ) {
        this.group = group;
        this.width = width;
        this.height = height;
    }

    protected abstract G doCopy();

    @Override
    public G getGroup() {
        return group;
    }

    @Override
    public G copy() {
        return doCopy();
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }
}
