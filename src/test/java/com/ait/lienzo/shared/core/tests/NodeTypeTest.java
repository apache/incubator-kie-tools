package com.ait.lienzo.shared.core.tests;

import com.ait.lienzo.shared.core.types.NodeType;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NodeTypeTest {

    @Test
    public void testEquals()
    {
        NodeType group = NodeType.GROUP;
        assertFalse(group.equals(null));
        assertTrue(group.equals(group));

        NodeType layer = NodeType.LAYER;
        assertFalse(layer.equals(group));
        assertFalse(group.equals(layer));

        NodeTypeExtension something = NodeTypeExtension.SOMETHING;
        assertFalse(group.equals(something));
        assertFalse(something.equals(group));

        NodeTypeExtension overrideGroup = NodeTypeExtension.GROUP;
        assertTrue(overrideGroup.equals(group));
        assertTrue(group.equals(overrideGroup));
    }

    private static class NodeTypeExtension extends NodeType
    {
        public static NodeTypeExtension SOMETHING = new NodeTypeExtension("Something");
        public static NodeTypeExtension GROUP = new NodeTypeExtension("Group");

        protected NodeTypeExtension(String value)
        {
            super(value);
        }
    }
}
