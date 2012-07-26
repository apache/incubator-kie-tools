/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.moduleeditor.drools;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import com.google.gwt.user.client.Command;

public class SuggestionCompletionCacheTest {

    private boolean executed;
    private boolean loaded;

    @Before
    public void setUp() throws Exception {
        executed = false;
        loaded = false;
    }

    @Test
    public void testCache() throws Exception {

        final SuggestionCompletionCache cache = new SuggestionCompletionCache() {

            public void loadPackage(String packageName,
                             Command command) {
                loaded = true;
                command.execute();
            }
        };

        cache.loadPackage( "xyz", new Command() {
            public void execute() {
            }
        });
        assertTrue (loaded);

        SuggestionCompletionEngine eng = new SuggestionCompletionEngine();
        cache.cache.put( "foo",  eng);

        cache.loadPackage( "foo", new Command() {

            public void execute() {
                executed = true;
            }

        });

        assertTrue(executed);

        assertNotNull(cache.getEngineFromCache( "foo" ));

        cache.loadPackage( "foo", new Command() {

            public void execute() {

            }

        });




    }
}
