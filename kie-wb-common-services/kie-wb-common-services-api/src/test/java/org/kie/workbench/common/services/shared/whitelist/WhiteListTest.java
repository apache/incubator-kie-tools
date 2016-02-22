/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.shared.whitelist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.guvnor.common.services.project.model.POM;
import org.junit.Test;

import static org.junit.Assert.*;

public class WhiteListTest {

    @Test
    public void testContainsAny() throws Exception {

        final WhiteList whiteList = new WhiteList();

        assertFalse( whiteList.containsAny( getSet() ) );
        assertFalse( whiteList.containsAny( getSet( "org.test" ) ) );

        whiteList.add( "org.something" );
        assertFalse( whiteList.containsAny( getSet( "org.test" ) ) );

        whiteList.add( "org.test" );
        assertTrue( whiteList.containsAny( getSet( "org.test" ) ) );
    }

    private Collection<String> getSet( String... items ) {
        final ArrayList<String> list = new ArrayList<String>();
        for ( String item : items ) {
            list.add( item );
        }
        return list;
    }
}