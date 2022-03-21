/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.svg.gen.model.impl;

import com.ait.lienzo.client.core.shape.Picture;

public class ImageDefinition extends AbstractShapeDefinition<Picture> {

    private final String href;

    public ImageDefinition(final String id,
                           final String href) {
        super(id);
        this.href = href;
    }

    @Override
    public Class<Picture> getViewType() {
        return Picture.class;
    }

    public String getHref() {
        return href;
    }

    @Override
    public String toString() {
        return this.getClass().getName()
                + " [x=" + getX() + "]"
                + " [y =" + getY() + "]"
                + " [href=" + href + "]";
    }
}
