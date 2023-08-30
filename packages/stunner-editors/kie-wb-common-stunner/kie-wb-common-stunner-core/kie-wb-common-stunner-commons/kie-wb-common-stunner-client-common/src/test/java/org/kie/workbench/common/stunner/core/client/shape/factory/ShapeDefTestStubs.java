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


package org.kie.workbench.common.stunner.core.client.shape.factory;

import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;

public final class ShapeDefTestStubs {

    public static class TestShapeDefType1 implements ShapeDef<Object> {

        @Override
        public Class<? extends ShapeDef> getType() {
            return TestShapeDefType1.class;
        }
    }

    public static class TestShapeDefType2 implements ShapeDef<Object> {

        @Override
        public Class<? extends ShapeDef> getType() {
            return TestShapeDefType2.class;
        }
    }

    public static abstract class TestShapeDefFactoryStub implements ShapeDefFactory<Object, ShapeDef<Object>, Shape> {

    }
}
