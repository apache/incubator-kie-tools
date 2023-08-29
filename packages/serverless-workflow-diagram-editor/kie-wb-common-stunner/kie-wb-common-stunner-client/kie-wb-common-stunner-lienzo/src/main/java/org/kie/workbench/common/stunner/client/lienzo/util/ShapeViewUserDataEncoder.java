/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.client.lienzo.util;

import java.util.Objects;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.IDrawable;
import org.kie.workbench.common.stunner.client.lienzo.shape.impl.ShapeStateDefaultHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

import static java.util.Objects.isNull;

/**
 * This class is responsible to encode the {@link ShapeView} properties within the {@link ShapeView#getUserData()}
 * that is used by the Diagram SVG generation.
 * <p>
 * example: &shapeType=BORDER&renderType=FILL
 */

public class ShapeViewUserDataEncoder {

    ShapeViewUserDataEncoder() {

    }

    public static final ShapeViewUserDataEncoder get() {
        return new ShapeViewUserDataEncoder();
    }

    public void applyShapeViewType(final Supplier<ShapeView> shapeSupplier, ShapeStateDefaultHandler.ShapeType shapeType) {
        if (isNull(shapeSupplier)) {
            return;
        }

        applyShapeViewType(shapeSupplier.get(), shapeType);
    }

    public void applyShapeViewType(final ShapeView shapeView, ShapeStateDefaultHandler.ShapeType shapeType) {
        if (isNull(shapeType)) {
            return;
        }

        applyShapeViewUserData(shapeView, "shapeType", shapeType.name());
    }

    public void applyShapeViewUserData(final ShapeView shapeView, String key, String value) {
        if (isNull(shapeView)) {
            return;
        }
        Object previousUserData = shapeView.getUserData();
        if (isNull(previousUserData) || String.valueOf(previousUserData).contains(key)) {
            previousUserData = "?";
        } else {
            previousUserData += "&";
        }
        shapeView.setUserData(previousUserData + key + "=" + value);
    }

    @SuppressWarnings("all")
    public void setShapeViewChildrenIds(String uuid, IContainer container) {
        //recursive call to set children in case of container
        container.getChildNodes().toList().stream()
                .filter(Objects::nonNull)
                .filter(child -> child instanceof IContainer)
                .forEach(child -> setShapeViewChildrenIds(uuid, (IContainer) child));
        //set the children ids
        container.getChildNodes().toList().stream()
                .filter(Objects::nonNull)
                .filter(child -> !(child instanceof IContainer))
                .filter(child -> child instanceof IDrawable<?>)
                .forEach(child -> {
                    IDrawable drawable = (IDrawable) child;
                    String suffix = "";
                    if (null != drawable.getUserData()) {
                        suffix = drawable.getUserData().toString();
                    } else if (null != drawable.getID()) {
                        suffix = "_" + drawable.getID();
                    }
                    drawable.setID(uuid + suffix);
                });
    }
}