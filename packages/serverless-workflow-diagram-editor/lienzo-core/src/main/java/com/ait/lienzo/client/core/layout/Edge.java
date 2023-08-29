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


package com.ait.lienzo.client.core.layout;

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.client.core.types.Point2D;

public class Edge {

    private final String source;
    private final String target;
    private final List<Point2D> bendingPoints;
    private String id;

    public Edge(final String edgeId, final String source, final String target) {
        this.id = edgeId;
        this.source = source;
        this.target = target;
        this.bendingPoints = new ArrayList<>();
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public List<Point2D> getBendingPoints() {
        return bendingPoints;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }
}
