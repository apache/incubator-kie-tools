/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.canvas;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith( MockitoJUnitRunner.class )
public class TransformImplTest {

    private static final double[] translate = new double[] { 10, 20 };
    private static final double[] scale = new double[] { 2, 5 };

    private TransformImpl tested;

    @Before
    public void setup() throws Exception {
        this.tested = new TransformImpl( translate, scale );
    }

    @Test
    public void testGetTranslate() {
        final double[] t = tested.getTranslate();
        assertEquals( translate, t );
    }

    @Test
    public void testGetScale() {
        final double[] s = tested.getScale();
        assertEquals( scale, s );
    }

    @Test
    public void testTranform() {
        final double[] t = tested.transform( 1, 1 );
        assertEquals( 12, t[0], 0 );
        assertEquals( 25, t[1], 0 );
        final double[] t1 = tested.transform( 2, 2 );
        assertEquals( 14, t1[0], 0 );
        assertEquals( 30, t1[1], 0 );
    }

}
