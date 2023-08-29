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
package org.kie.workbench.common.dmn.api.property.dimensions;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
public class DecisionServiceRectangleDimensionsSet implements RectangleDimensionsSet {

    public static final double DEFAULT_WIDTH = 200.0;

    public static final double DEFAULT_HEIGHT = 200.0;

    private static final double MIN_WIDTH = 100.0;

    private static final double MIN_HEIGHT = 100.0;

    @Property
    protected Width width;

    @Property
    protected Height height;

    public DecisionServiceRectangleDimensionsSet() {
        this(new Width(DEFAULT_WIDTH),
             new Height(DEFAULT_HEIGHT));
    }

    public DecisionServiceRectangleDimensionsSet(final Width width,
                                                 final Height height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public Width getWidth() {
        return width;
    }

    @Override
    public void setWidth(final Width width) {
        this.width = width;
    }

    @Override
    public Height getHeight() {
        return height;
    }

    @Override
    public void setHeight(final Height height) {
        this.height = height;
    }

    public double getMinimumWidth() {
        return MIN_WIDTH;
    }

    public double getMaximumWidth() {
        return Double.MAX_VALUE;
    }

    public double getMinimumHeight() {
        return MIN_HEIGHT;
    }

    public double getMaximumHeight() {
        return Double.MAX_VALUE;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DecisionServiceRectangleDimensionsSet)) {
            return false;
        }

        final DecisionServiceRectangleDimensionsSet that = (DecisionServiceRectangleDimensionsSet) o;

        if (width != null ? !width.equals(that.width) : that.width != null) {
            return false;
        }
        return height != null ? height.equals(that.height) : that.height == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(width != null ? width.hashCode() : 0,
                                         height != null ? height.hashCode() : 0);
    }
}