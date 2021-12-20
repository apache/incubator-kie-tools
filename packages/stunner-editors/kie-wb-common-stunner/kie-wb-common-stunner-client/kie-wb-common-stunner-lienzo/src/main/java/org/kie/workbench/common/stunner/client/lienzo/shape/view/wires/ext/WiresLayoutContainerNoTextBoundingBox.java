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

package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IDrawable;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.wires.WiresLayoutContainer;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.BoundingPoints;
import com.ait.lienzo.tools.client.collection.NFastArrayList;

/**
 * The purpose of this class is to allow Items to align without taking account Text.
 * JBPM-8555
 */
public class WiresLayoutContainerNoTextBoundingBox extends WiresLayoutContainer {

    public WiresLayoutContainerNoTextBoundingBox() {

        super(new Group() {

            @Override
            public BoundingBox getBoundingBox() {
                final BoundingBox bbox = new BoundingBox();

                final NFastArrayList list = getChildNodes();

                final int size = list.size();

                for (int i = 0; i < size; i++) {
                    final BoundingPoints bpts = ((IDrawable) list.get(i)).getBoundingPoints();

                    if (null != bpts && !(getChildNodes().get(i) instanceof Text)) {
                        bbox.addBoundingBox(bpts.getBoundingBox());
                    }
                }
                return bbox;
            }
        });
    }
}
