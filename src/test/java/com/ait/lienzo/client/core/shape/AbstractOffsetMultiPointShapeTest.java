/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package com.ait.lienzo.client.core.shape;

import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class AbstractOffsetMultiPointShapeTest {

    @Mock
    private PathPartList partList;

    @Mock
    private Attributes attributes;

    private AbstractOffsetMultiPointShape multiPointShape;

    @Before
    public void setup() {
        multiPointShape = spy(makeMultiPointShape());
    }

    @Test
    public void testIsPathPartListPreparedWhenPathPartListSizeIsGreaterThanOrEqualToOne() {

        when(partList.size()).thenReturn(1);

        final boolean isPathPartListPrepared = multiPointShape.isPathPartListPrepared(attributes);

        assertTrue(isPathPartListPrepared);
    }

    @Test
    public void testIsPathPartListPreparedWhenPathPartListSizeIsLessThanOneAndParseReturnsTrue() {

        when(partList.size()).thenReturn(0);
        doReturn(true).when(multiPointShape).parse(attributes);

        final boolean isPathPartListPrepared = multiPointShape.isPathPartListPrepared(attributes);

        assertTrue(isPathPartListPrepared);
    }

    @Test
    public void testIsPathPartListPreparedWhenPathPartListSizeIsLessThanOneAndParseReturnsFalse() {

        when(partList.size()).thenReturn(0);
        doReturn(false).when(multiPointShape).parse(attributes);

        final boolean isPathPartListPrepared = multiPointShape.isPathPartListPrepared(attributes);

        assertFalse(isPathPartListPrepared);
    }

    private AbstractOffsetMultiPointShape makeMultiPointShape() {

        return new AbstractOffsetMultiPointShape(null) {
            @Override
            public List<Attribute> getBoundingBoxAttributes() {
                return null;
            }

            @Override
            public BoundingBox getBoundingBox() {
                return null;
            }

            @Override
            public Shape setPoint2DArray(final Point2DArray points) {
                return null;
            }

            @Override
            public Point2DArray getPoint2DArray() {
                return null;
            }

            @Override
            public Point2D getTailOffsetPoint() {
                return null;
            }

            @Override
            public Point2D getHeadOffsetPoint() {
                return null;
            }

            @Override
            public boolean parse(final Attributes attr) {
                return false;
            }

            @Override
            public PathPartList getPathPartList() {
                return partList;
            }
        };
    }
}
