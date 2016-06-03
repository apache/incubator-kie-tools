package com.ait.lienzo.shared.core.tests;

import com.ait.lienzo.shared.core.types.PaletteType;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PaletteTypeTest {

    @Test
    public void testEquals()
    {
        PaletteType palette = PaletteType.PALETTE;
        assertFalse(palette.equals(null));
        assertTrue(palette.equals(palette));

        PaletteType paletteItem = PaletteType.PALETTE_ITEM;
        assertFalse(palette.equals(paletteItem));
        assertFalse(paletteItem.equals(palette));

        PaletteTypeExtension other = PaletteTypeExtension.OTHER;
        assertFalse(other.equals(palette));
        assertFalse(palette.equals(other));

        PaletteTypeExtension overloadPalette = PaletteTypeExtension.PALETTE;
        assertTrue(palette.equals(overloadPalette));
        assertTrue(overloadPalette.equals(palette));
    }

    private static class PaletteTypeExtension extends PaletteType
    {
        public static final PaletteTypeExtension OTHER = new PaletteTypeExtension("Other");
        public static final PaletteTypeExtension PALETTE = new PaletteTypeExtension("Palette");

        protected PaletteTypeExtension(String value)
        {
            super(value);
        }
    }
}
