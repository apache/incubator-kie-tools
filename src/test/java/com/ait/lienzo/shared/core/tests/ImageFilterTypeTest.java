package com.ait.lienzo.shared.core.tests;

import com.ait.lienzo.shared.core.types.ImageFilterType;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ImageFilterTypeTest {

    @Test
    public void testEquals()
    {
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

    private static class ImageFilterTypeExtension extends ImageFilterType
    {
        private static ImageFilterTypeExtension SomeNewValue = new ImageFilterTypeExtension("SomeNewValue");
        private static ImageFilterTypeExtension AlphaScaleColorImageDataFilterType = new ImageFilterTypeExtension("AlphaScaleColorImageDataFilter");

        protected ImageFilterTypeExtension(String value)
        {
            super(value);
        }
    }
}
