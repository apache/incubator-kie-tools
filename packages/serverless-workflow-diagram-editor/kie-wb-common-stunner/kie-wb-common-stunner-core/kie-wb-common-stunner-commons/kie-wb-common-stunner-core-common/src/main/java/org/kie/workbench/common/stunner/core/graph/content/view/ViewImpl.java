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


package org.kie.workbench.common.stunner.core.graph.content.view;

import jsinterop.annotations.JsType;
import org.kie.j2cl.tools.processors.annotations.GWT3Export;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;

@JsType
@GWT3Export
public final class ViewImpl<W> implements View<W> {

    protected W definition;
    protected Bounds bounds;

    public ViewImpl(W definition,
                    Bounds bounds) {
        this.definition = definition;
        this.bounds = bounds;
    }

    @Override
    public W getDefinition() {
        return definition;
    }

    @Override
    public void setDefinition(final W definition) {
        this.definition = definition;
    }

    @Override
    public Bounds getBounds() {
        return bounds;
    }

    @Override
    public void setBounds(final Bounds bounds) {
        this.bounds = bounds;
    }
}
