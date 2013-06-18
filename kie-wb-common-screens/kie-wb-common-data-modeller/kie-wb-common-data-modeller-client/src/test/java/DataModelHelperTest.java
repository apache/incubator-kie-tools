import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.datamodeller.client.DataModelHelper;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: jschatte
 * Date: 14/06/13
 * Time: 13:00
 * To change this template use File | Settings | File Templates.
 */
public class DataModelHelperTest {

    private DataModelHelper helper;

    @Before
    public void setUp() {
        helper = new DataModelHelper();
        /*
         * Set up siblingsMap such that:
         * a.A --> {a.B, a.b.F}             A--+        K
         * a.B --> {a.C}                    |  |        |
         * a.C --> {d.D, d.e.G}             B  F        L
         * d.D --> {d.E}                    |
         * d.e.G --> {d.e.H}                C--+
         * k.l.K --> {k.l.L}                |  |
         *                                  D  G
         *                                  |  |
         *                                  E  H
         */
        helper.dataObjectExtended("a.A", "a.B", true);
        helper.dataObjectExtended("a.A", "a.b.F", true);
        helper.dataObjectExtended("a.B", "a.C", true);
        helper.dataObjectExtended("a.C", "d.D", true);
        helper.dataObjectExtended("a.C", "d.e.G", true);
        helper.dataObjectExtended("d.D", "d.E", true);
        helper.dataObjectExtended("d.e.G", "d.e.H", true);
        helper.dataObjectExtended("k.l.K", "k.l.L", true);
    }

    @Test
    public void testIsAssignableFrom() throws Exception {
        assertFalse(helper.isAssignableFrom(null, null));
        assertFalse(helper.isAssignableFrom("", null));
        assertFalse(helper.isAssignableFrom(null, ""));
        assertFalse(helper.isAssignableFrom("", ""));

        assertFalse(helper.isAssignableFrom("a.A", "a.A"));
        assertFalse(helper.isAssignableFrom("d.E", "d.E"));

        // Test no extension relation at all
        assertTrue(helper.isAssignableFrom("a.A", "u.v.W"));
        assertTrue(helper.isAssignableFrom("a.A", "a.b.c.K"));
        assertTrue(helper.isAssignableFrom("a.A", "a.b.c.L"));
        assertTrue(helper.isAssignableFrom("a.b.c.K", "a.A"));
        assertTrue(helper.isAssignableFrom("a.b.c.L", "a.A"));

        // Test existing extensions
        assertTrue(helper.isAssignableFrom("a.B", "a.A")); //Should return true
        assertFalse(helper.isAssignableFrom("a.A", "a.b.F"));
        assertFalse(helper.isAssignableFrom("a.A", "d.E"));

        // D and H are below C in the extension hierarchy, but (at least for now) should be allowed
        // to change parent to A for example
        assertTrue(helper.isAssignableFrom("d.D", "a.A"));
        assertTrue(helper.isAssignableFrom("d.e.H", "a.A"));

        // Change extensions and retest
        assertTrue(helper.isAssignableFrom("a.b.F", "k.l.K"));
        assertTrue(helper.isAssignableFrom("k.l.K", "a.b.F"));
        helper.dataObjectExtended("a.A", "a.b.F", false);
        helper.dataObjectExtended("k.l.K", "a.b.F", true);
        assertFalse(helper.isAssignableFrom("k.l.K", "a.b.F"));
        assertTrue(helper.isAssignableFrom("a.A", "a.b.F"));

        helper.dataObjectExtended("a.B", "a.C", false);
        assertTrue(helper.isAssignableFrom("a.A", "d.E"));
        assertTrue(helper.isAssignableFrom("a.A", "d.e.H"));
        assertTrue(helper.isAssignableFrom("d.D", "a.A"));
        assertTrue(helper.isAssignableFrom("d.e.H", "a.A"));

        // Restore setup before exiting
        helper.dataObjectExtended("a.B", "a.C", true);
        helper.dataObjectExtended("k.l.K", "a.b.F", false);
        helper.dataObjectExtended("a.A", "a.b.F", true);
    }
}
