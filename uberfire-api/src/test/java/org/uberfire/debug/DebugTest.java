/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
