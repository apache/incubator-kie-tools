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


package org.kie.workbench.common.stunner.core.definition.shape;

import java.util.function.Supplier;

import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;

/**
 * A glyph for a definition which renders a thumbnail of the definition's shape..
 * This way the glyph and the shape will be the same ones along all components and widgets.
 */
public final class ShapeGlyph implements Glyph {

    private String definitionId;
    private Supplier<ShapeFactory> factorySupplier;

    public static ShapeGlyph create() {
        return new ShapeGlyph();
    }

    private ShapeGlyph() {
    }

    public void setDefinitionId(final String definitionId) {
        this.definitionId = definitionId;
    }

    public void setFactorySupplier(final Supplier<ShapeFactory> factorySupplier) {
        this.factorySupplier = factorySupplier;
    }

    public String getDefinitionId() {
        return definitionId;
    }

    public Supplier<ShapeFactory> getFactorySupplier() {
        return factorySupplier;
    }
}
