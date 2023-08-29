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


package org.kie.workbench.common.stunner.core.client.shape;

import org.kie.workbench.common.stunner.core.client.shape.view.BoundingBox;
import org.kie.workbench.common.stunner.core.client.shape.view.HasManageableControlPoints;
import org.kie.workbench.common.stunner.core.client.shape.view.IsConnector;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

public class ConnectorViewStub implements ShapeView<Object>,
                                          IsConnector,
                                          HasManageableControlPoints<Object> {

    public final static String UUID = "cv-stub";

    @Override
    public Object setUUID(final String uuid) {
        return this;
    }

    @Override
    public String getUUID() {
        return UUID;
    }

    @Override
    public double getShapeX() {
        return 0;
    }

    @Override
    public double getShapeY() {
        return 0;
    }

    @Override
    public Point2D getShapeAbsoluteLocation() {
        return new Point2D(0,
                           0);
    }

    @Override
    public Object setShapeLocation(Point2D location) {
        return this;
    }

    @Override
    public double getAlpha() {
        return 0;
    }

    @Override
    public Object setAlpha(final double alpha) {
        return this;
    }

    @Override
    public String getFillColor() {
        return "#000000";
    }

    @Override
    public Object setFillColor(final String color) {
        return this;
    }

    @Override
    public double getFillAlpha() {
        return 0;
    }

    @Override
    public Object setFillAlpha(final double alpha) {
        return this;
    }

    @Override
    public String getStrokeColor() {
        return "#000000";
    }

    @Override
    public Object setStrokeColor(final String color) {
        return this;
    }

    @Override
    public double getStrokeAlpha() {
        return 0;
    }

    @Override
    public Object setStrokeAlpha(final double alpha) {
        return this;
    }

    @Override
    public double getStrokeWidth() {
        return 0;
    }

    @Override
    public Object setStrokeWidth(final double width) {
        return this;
    }

    @Override
    public Object setDragEnabled(final boolean draggable) {
        return this;
    }

    @Override
    public Object moveToTop() {
        return this;
    }

    @Override
    public Object moveToBottom() {
        return this;
    }

    @Override
    public Object moveUp() {
        return this;
    }

    @Override
    public Object moveDown() {
        return this;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return null;
    }

    @Override
    public void removeFromParent() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Object connect(ShapeView headShapeView,
                          Connection _headMagnetsIndex,
                          ShapeView tailShapeView,
                          Connection _tailMagnetsIndex) {
        return this;
    }

    @Override
    public Object getUserData() {
        return null;
    }

    @Override
    public void setUserData(Object userData) {

    }

    @Override
    public Object addControlPoint(ControlPoint controlPoint, int index) {
        return this;
    }

    @Override
    public Object updateControlPoints(ControlPoint[] controlPoints) {
        return this;
    }

    @Override
    public Object deleteControlPoint(int index) {
        return this;
    }

    @Override
    public ControlPoint[] getManageableControlPoints() {
        return new ControlPoint[0];
    }

    @Override
    public Object showControlPoints(ControlPointType type) {
        return this;
    }

    @Override
    public Object hideControlPoints() {
        return this;
    }

    @Override
    public boolean areControlsVisible() {
        return false;
    }
}
