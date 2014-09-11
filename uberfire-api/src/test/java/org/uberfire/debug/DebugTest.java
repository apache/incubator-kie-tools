package org.uberfire.debug;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class DebugTest {

    @Test
    public void testAbbreviatedName() {
        assertEquals( "ju.List",
                      Debug.abbreviatedName( List.class ) );
    }

    @Test
    public void testMemorableNumber() {
        assertEquals( "badodyre",
                      Debug.toMemorableString( 56385664 ) );
    }

    @Test
    public void testObjectId() {
        Map<String, String> m = new HashMap<String, String>();
        assertEquals( Debug.abbreviatedName( m.getClass() ) + "@" + Debug.toMemorableString( System.identityHashCode( m ) ),
                      Debug.objectId( m ) );
    }

    @Test
    public void testShortNameWithInnerClass() {
        assertEquals( "Map$Entry",
                      Debug.shortName( Map.Entry.class ) );
    }

    @Test
    public void testShortNameInDefaultPackage() throws Exception {
        assertEquals( "NaughtyClassInDefaultPackage",
                      Debug.shortName( Class.forName("NaughtyClassInDefaultPackage") ) );
    }

    @Test
    public void testAbbreviatedNameInDefaultPackage() throws Exception {
        assertEquals( "NaughtyClassInDefaultPackage",
                      Debug.abbreviatedName( Class.forName("NaughtyClassInDefaultPackage") ) );
    }

}
