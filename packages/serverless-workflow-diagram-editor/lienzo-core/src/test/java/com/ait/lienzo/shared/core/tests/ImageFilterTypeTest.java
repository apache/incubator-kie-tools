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

package com.ait.lienzo.shared.core.tests;

import com.ait.lienzo.shared.core.types.ImageFilterType;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ImageFilterTypeTest {

    @Test
    public void testEquals() {
        ImageFilterType brightness = ImageFilterType.BrightnessImageDataFilterType;
        assertFalse(brightness.equals(null));
        assertTrue(brightness.equals(brightness));

        ImageFilterTypeExtension someNewValue = ImageFilterTypeExtension.SomeNewValue;
        assertFalse(someNewValue.equals(brightness));
        assertFalse(brightness.equals(someNewValue));

        ImageFilterType alpha = ImageFilterType.AlphaScaleColorImageDataFilterType;
        assertFalse(brightness.equals(alpha));
        assertFalse(alpha.equals(brightness));

        ImageFilterTypeExtension overrideAlpha = ImageFilterTypeExtension.AlphaScaleColorImageDataFilterType;
        assertFalse(overrideAlpha.equals(someNewValue));
        assertFalse(someNewValue.equals(overrideAlpha));

        assertTrue(overrideAlpha.equals(alpha));
        assertTrue(alpha.equals(overrideAlpha));
    }

    private static class ImageFilterTypeExtension extends ImageFilterType {

        private static ImageFilterTypeExtension SomeNewValue = new ImageFilterTypeExtension("SomeNewValue");
        private static ImageFilterTypeExtension AlphaScaleColorImageDataFilterType = new ImageFilterTypeExtension("AlphaScaleColorImageDataFilter");

        protected ImageFilterTypeExtension(String value) {
            super(value);
        }
    }
}
