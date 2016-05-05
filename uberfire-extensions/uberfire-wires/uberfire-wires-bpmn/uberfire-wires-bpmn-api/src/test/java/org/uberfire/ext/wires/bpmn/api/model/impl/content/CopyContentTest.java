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
package org.uberfire.ext.wires.bpmn.api.model.impl.content;

import java.util.HashSet;

import org.junit.Test;
import org.uberfire.ext.wires.bpmn.api.model.Content;
import org.uberfire.ext.wires.bpmn.api.model.Property;
import org.uberfire.ext.wires.bpmn.api.model.Role;
import org.uberfire.ext.wires.bpmn.api.model.impl.properties.DefaultPropertyImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.roles.DefaultRoleImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.types.StringType;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CopyContentTest {

    @Test
    public void testCopyContent() {
        final Content content = new DefaultContentImpl( "id",
                                                        "title",
                                                        "description",
                                                        new HashSet<Role>() {{
                                                            add( new DefaultRoleImpl( "a-role" ) );
                                                        }},
                                                        new HashSet<Property>() {{
                                                            add( new DefaultPropertyImpl( "id",
                                                                                          new StringType(),
                                                                                          "caption",
                                                                                          "description",
                                                                                          true,
                                                                                          true ) );
                                                        }}
        );
        final Content copy = content.copy();

        assertNotNull( copy );
        assertFalse( content == copy );
        assertEquals( content.getId(),
                      copy.getId() );
        assertEquals( content.getTitle(),
                      copy.getTitle() );
        assertEquals( content.getDescription(),
                      copy.getDescription() );
        assertEquals( content.getRoles(),
                      copy.getRoles() );
        assertEquals( content.getProperties(),
                      copy.getProperties() );
    }

}
