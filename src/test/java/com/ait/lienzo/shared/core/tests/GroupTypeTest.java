package com.ait.lienzo.shared.core.tests;

import com.ait.lienzo.shared.core.types.GroupType;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GroupTypeTest
{

    @Test
    public void testEquals()
    {
        GroupType groupType = GroupType.GROUP;
        assertFalse(groupType.equals(null));
        assertTrue(groupType.equals(groupType));

        GroupTypeExtension extension = GroupTypeExtension.GROUP_EXTENSION;
        assertFalse(groupType.equals(extension));
        assertFalse(extension.equals(groupType));

        GroupTypeExtension overrideGroup = GroupTypeExtension.GROUP;
        assertFalse(extension.equals(overrideGroup));
        assertFalse(overrideGroup.equals(extension));

        assertTrue(groupType.equals(overrideGroup));
        assertTrue(overrideGroup.equals(groupType));
    }

    private static class GroupTypeExtension extends GroupType
    {
        public static final GroupTypeExtension GROUP_EXTENSION = new GroupTypeExtension("GroupExtension");
        public static final GroupTypeExtension GROUP = new GroupTypeExtension("Group");

        protected GroupTypeExtension(final String value)
        {
            super(value);
        }
    }
}
