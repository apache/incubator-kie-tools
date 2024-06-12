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

package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Shape;

public abstract class BaseShapesExample<T extends Shape<T>> extends BaseExample implements Example {

    protected Shape<T>[] shapes;

    protected int numberOfShapes = 10; // Default
    protected boolean ignoreLocation = false;

    public BaseShapesExample(String title) {
        super(title);
    }

    protected void setPaddings(int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        this.leftPadding = leftPadding;
        this.topPadding = topPadding;
        this.rightPadding = rightPadding;
        this.bottomPadding = bottomPadding;
    }

    protected void setLocation() {
        console.log("Random Location for " + this.getClass().getName() + "--->");
        for (int i = 0; i < shapes.length; i++) {
            setRandomLocation(shapes[i]);
        }
    }

    public void destroy() {
        super.destroy();
    }

    @Override
    public void onResize() {
        super.onResize();
        if (!ignoreLocation) {
            setLocation();
        }
        layer.batch();
    }
}
