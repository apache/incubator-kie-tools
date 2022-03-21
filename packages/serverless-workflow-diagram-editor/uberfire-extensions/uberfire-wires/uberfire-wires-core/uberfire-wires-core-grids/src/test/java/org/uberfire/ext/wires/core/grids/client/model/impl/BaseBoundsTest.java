/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.core.grids.client.model.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BaseBoundsTest {

    @Test
    public void validConstructorParameters() {
        final BaseBounds b = new BaseBounds(10.0,
                                            20.0,
                                            30.0,
                                            40.0);
        assertEquals(10.0,
                     b.getX(),
                     0.0);
        assertEquals(20.0,
                     b.getY(),
                     0.0);
        assertEquals(30.0,
                     b.getWidth(),
                     0.0);
        assertEquals(40.0,
                     b.getHeight(),
                     0.0);
    }

    @SuppressWarnings("unused")
    @Test(expected = IllegalStateException.class)
    public void invalidConstructorWidthParameter() {
        final BaseBounds b = new BaseBounds(10.0,
                                            20.0,
                                            -30.0,
                                            40.0);
    }

    @SuppressWarnings("unused")
    @Test(expected = IllegalStateException.class)
    public void invalidConstructorHeightParameter() {
        final BaseBounds b = new BaseBounds(10.0,
                                            20.0,
                                            30.0,
                                            -40.0);
    }

    @Test(expected = IllegalStateException.class)
    public void invalidSetterWidthParameter() {
        final BaseBounds b = new BaseBounds(10.0,
                                            20.0,
                                            30.0,
                                            40.0);
        b.setWidth(-30.0);
    }

    @Test(expected = IllegalStateException.class)
    public void invalidSetterHeightParameter() {
        final BaseBounds b = new BaseBounds(10.0,
                                            20.0,
                                            30.0,
                                            40.0);
        b.setHeight(-40.0);
    }
}
