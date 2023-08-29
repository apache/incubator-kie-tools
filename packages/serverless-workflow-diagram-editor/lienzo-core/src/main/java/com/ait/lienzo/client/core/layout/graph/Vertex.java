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


package com.ait.lienzo.client.core.layout.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.ait.lienzo.client.core.types.Point2D;

import static com.ait.lienzo.tools.common.api.java.util.UUID.uuid;

public class Vertex {

    private List<OutgoingEdge> outgoingEdges;
    private Point2D position;
    private int width;
    private int height;
    private String id;

    public Vertex() {
        this(uuid());
    }

    public Vertex(final String id) {
        this.id = id;
    }

    public List<OutgoingEdge> getOutgoingEdges() {
        if (Objects.isNull(outgoingEdges)) {
            outgoingEdges = new ArrayList<>();
        }
        return outgoingEdges;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}

