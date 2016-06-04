package com.ait.lienzo.shared.core.tests;

import com.ait.lienzo.shared.core.types.ProxyType;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProxyTypeTest {

    @Test
    public void testEquals()
    {
        ProxyTypeExtension foo = ProxyTypeExtension.FOO;
        assertFalse(foo.equals(null));
        assertTrue(foo.equals(foo));

        ProxyTypeExtension bar = ProxyTypeExtension.BAR;
        assertFalse(foo.equals(bar));
        assertFalse(bar.equals(foo));

        ProxyTypeAdditionalExtension additionalFoo= ProxyTypeAdditionalExtension.FOO;
        assertTrue(additionalFoo.equals(foo));
        assertTrue(foo.equals(additionalFoo));
    }

    private static class ProxyTypeExtension extends ProxyType
    {
        public static final ProxyTypeExtension FOO = new ProxyTypeExtension("Foo");
        public static final ProxyTypeExtension BAR = new ProxyTypeExtension("Bar");

        protected ProxyTypeExtension(String value)
        {
            super(value);
        }
    }

    private static class ProxyTypeAdditionalExtension extends ProxyType
    {
        public static final ProxyTypeAdditionalExtension FOO = new ProxyTypeAdditionalExtension("Foo");

        protected ProxyTypeAdditionalExtension(String value)
        {
            super(value);
        }
    }
}
