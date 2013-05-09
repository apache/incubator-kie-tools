package org.drools.workbench.screens.globals.backend.server.util;

import org.junit.Test;
import org.drools.workbench.screens.globals.backend.server.util.GlobalsPersistence;
import org.drools.workbench.screens.globals.model.Global;
import org.drools.workbench.screens.globals.model.GlobalsModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for GlobalsPersistence
 */
public class GlobalsPersistenceTest {

    @Test
    public void testMarshalling() {
        final GlobalsModel model = new GlobalsModel();
        final String expected = "global java.lang.String myString;\n";

        model.getGlobals().add( new Global( "myString",
                                            "java.lang.String" ) );
        final String actual = GlobalsPersistence.getInstance().marshal( model );

        assertNotNull( actual );
        assertEquals( expected,
                      actual );
    }

    @Test
    public void testUnmarshalling() {
        final String content = "global java.lang.String myString;\n";
        final GlobalsModel model = GlobalsPersistence.getInstance().unmarshal( content );

        assertNotNull( model );
        assertEquals( 1,
                      model.getGlobals().size() );
        assertEquals( "java.lang.String",
                      model.getGlobals().get( 0 ).getClassName() );
        assertEquals( "myString",
                      model.getGlobals().get( 0 ).getAlias() );
    }

}
