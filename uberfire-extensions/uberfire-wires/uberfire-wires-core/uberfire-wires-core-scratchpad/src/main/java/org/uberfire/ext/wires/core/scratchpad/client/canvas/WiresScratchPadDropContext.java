/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.scratchpad.client.canvas;

import org.uberfire.ext.wires.core.api.containers.WiresContainer;
import org.uberfire.ext.wires.core.api.factories.ShapeDropContext;

public class WiresScratchPadDropContext implements ShapeDropContext<WiresContainer> {

    private WiresContainer context;

    @Override
    public WiresContainer getContext() {
        return context;
    }

    @Override
    public void setContext( final WiresContainer context ) {
        this.context = context;
    }

}
