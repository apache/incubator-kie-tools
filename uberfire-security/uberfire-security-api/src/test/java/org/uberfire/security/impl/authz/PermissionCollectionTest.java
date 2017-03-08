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
package org.uberfire.security.impl.authz;

import org.junit.Test;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;

import static org.junit.Assert.*;

public class PermissionCollectionTest {

    Permission p1 = new DotNamedPermission("resource.read");
    Permission p2 = new DotNamedPermission("resource.read",
                                           true);
    Permission p3 = new DotNamedPermission("resource.read",
                                           false);
    Permission p4 = new DotNamedPermission("resource.read.id1",
                                           true);
    Permission p5 = new DotNamedPermission("resource.read.id1",
                                           false);
    Permission p6 = new DotNamedPermission("resource.read.id2",
                                           true);
    Permission p7 = new DotNamedPermission("resource.read.id2",
                                           false);
    Permission p8 = new DotNamedPermission("perspective.read.id1",
                                           true);

    @Test
    public void testNotAdded() {
        PermissionCollection pc = new DefaultPermissionCollection();
        pc.add(new DotNamedPermission("resource.read",
                                      true));
        pc.add(new DotNamedPermission("resource.read.id1")); // Not added
        pc.add(new DotNamedPermission("resource.read.id1",
                                      true)); // Not added
        pc.add(new DotNamedPermission("resource.read.id1",
                                      false));
        assertEquals(pc.collection().size(),
                     2);
    }

    @Test
    public void testEmpty() {
        PermissionCollection pc = new DefaultPermissionCollection();
        assertFalse(pc.implies(p1));
        assertFalse(pc.implies(p2));
        assertFalse(pc.implies(p3));
    }

    @Test
    public void testGranted() {
        PermissionCollection pc = new DefaultPermissionCollection();
        pc.add(new DotNamedPermission("resource.read",
                                      true));
        assertTrue(pc.implies(p1));
        assertTrue(pc.implies(p2));
        assertFalse(pc.implies(p3));
    }

    @Test
    public void testAbstain() {
        PermissionCollection pc = new DefaultPermissionCollection();
        pc.add(new DotNamedPermission("resource.read"));
        assertTrue(pc.implies(p1));
        assertFalse(pc.implies(p2));
        assertFalse(pc.implies(p3));
    }

    @Test
    public void testDenied() {
        PermissionCollection pc = new DefaultPermissionCollection();
        pc.add(new DotNamedPermission("resource.read",
                                      false));
        assertFalse(pc.implies(p1));
        assertFalse(pc.implies(p2));
        assertTrue(pc.implies(p3));
    }

    @Test
    public void testChildGranted() {
        PermissionCollection pc = new DefaultPermissionCollection();
        pc.add(new DotNamedPermission("",
                                      false));
        pc.add(new DotNamedPermission("resource.read",
                                      false));
        pc.add(new DotNamedPermission("resource.read.id1",
                                      true));
        assertTrue(pc.implies(p4));
        assertTrue(pc.implies(p5));
        assertFalse(pc.implies(p6));
        assertTrue(pc.implies(p7));
    }

    @Test
    public void testChildDenied() {
        PermissionCollection pc = new DefaultPermissionCollection();
        pc.add(new DotNamedPermission("",
                                      true));
        pc.add(new DotNamedPermission("resource.read",
                                      true));
        pc.add(new DotNamedPermission("resource.read.id1",
                                      false));
        assertTrue(pc.implies(p4));
        assertTrue(pc.implies(p5));
        assertTrue(pc.implies(p6));
        assertFalse(pc.implies(p7));
    }

    @Test
    public void testOtherAbstain() {
        PermissionCollection pc = new DefaultPermissionCollection();
        pc.add(new DotNamedPermission("resource.read",
                                      true));
        pc.add(new DotNamedPermission("resource.read.id1",
                                      false));
        assertFalse(pc.implies(p8));
    }

    @Test
    public void testMergeNull() {
        PermissionCollection pc1 = new DefaultPermissionCollection();
        PermissionCollection pc2 = null;
        PermissionCollection result = pc1.merge(pc2,
                                                0);
        assertSame(result,
                   pc1);
    }

    @Test
    public void testMergeNoConflict() {
        PermissionCollection pc1 = new DefaultPermissionCollection();
        pc1.add(new DotNamedPermission("resource.read",
                                       true));
        pc1.add(new DotNamedPermission("resource.read.id1",
                                       false));

        PermissionCollection pc2 = new DefaultPermissionCollection();
        pc2.add(new DotNamedPermission("resource.read.id2",
                                       false));

        PermissionCollection result = pc1.merge(pc2,
                                                0);
        assertEquals(result.collection().size(),
                     2);
        assertEquals(result.get("resource.read").getResult(),
                     AuthorizationResult.ACCESS_GRANTED);
        assertEquals(result.get("resource.read.id1").getResult(),
                     AuthorizationResult.ACCESS_DENIED);
        assertNull(result.get("resource.read.id2"));
    }

    @Test
    public void testMergeGrantWinsByDefault() {
        PermissionCollection pc1 = new DefaultPermissionCollection();
        pc1.add(new DotNamedPermission("resource.read",
                                       true));
        pc1.add(new DotNamedPermission("resource.read.id1",
                                       false));

        PermissionCollection pc2 = new DefaultPermissionCollection();
        pc2.add(new DotNamedPermission("resource.read",
                                       true));

        PermissionCollection result = pc1.merge(pc2,
                                                0);
        assertEquals(result.collection().size(),
                     1);
        assertEquals(result.get("resource.read").getResult(),
                     AuthorizationResult.ACCESS_GRANTED);
    }

    @Test
    public void testMergePriorityWins() {
        PermissionCollection pc1 = new DefaultPermissionCollection();
        pc1.add(new DotNamedPermission("resource.read",
                                       true));
        pc1.add(new DotNamedPermission("resource.read.id1",
                                       false));

        PermissionCollection pc2 = new DefaultPermissionCollection();
        pc2.add(new DotNamedPermission("resource.read",
                                       true));

        PermissionCollection result = pc1.merge(pc2,
                                                -1);
        assertEquals(result.collection().size(),
                     2);
        assertEquals(result.get("resource.read").getResult(),
                     AuthorizationResult.ACCESS_GRANTED);
        assertEquals(result.get("resource.read.id1").getResult(),
                     AuthorizationResult.ACCESS_DENIED);

        result = pc1.merge(pc2,
                           1);
        assertEquals(result.collection().size(),
                     1);
        assertEquals(result.get("resource.read").getResult(),
                     AuthorizationResult.ACCESS_GRANTED);
        assertNull(result.get("resource.read.id1"));
    }

    @Test
    public void testMergeGrantedWins() {
        PermissionCollection pc1 = new DefaultPermissionCollection();
        pc1.add(new DotNamedPermission("resource.read.id1",
                                       false));

        PermissionCollection pc2 = new DefaultPermissionCollection();
        pc2.add(new DotNamedPermission("resource.read.id1",
                                       true));

        PermissionCollection result = pc1.merge(pc2,
                                                0);
        assertEquals(result.collection().size(),
                     1);
        assertEquals(result.get("resource.read.id1").getResult(),
                     AuthorizationResult.ACCESS_GRANTED);
    }

    @Test
    public void testMergeThisWins() {
        PermissionCollection pc1 = new DefaultPermissionCollection();
        pc1.add(new DotNamedPermission("resource.read.id1",
                                       false));

        PermissionCollection pc2 = new DefaultPermissionCollection();
        pc2.add(new DotNamedPermission("resource.read.id1",
                                       true));

        PermissionCollection result = pc1.merge(pc2,
                                                -1);
        assertEquals(result.collection().size(),
                     1);
        assertEquals(result.get("resource.read.id1").getResult(),
                     AuthorizationResult.ACCESS_DENIED);
    }

    @Test
    public void testMergeOtherWins() {
        PermissionCollection pc1 = new DefaultPermissionCollection();
        pc1.add(new DotNamedPermission("resource.read.id1",
                                       true));

        PermissionCollection pc2 = new DefaultPermissionCollection();
        pc2.add(new DotNamedPermission("resource.read.id1",
                                       false));

        PermissionCollection result = pc1.merge(pc2,
                                                1);
        assertEquals(result.collection().size(),
                     1);
        assertEquals(result.get("resource.read.id1").getResult(),
                     AuthorizationResult.ACCESS_DENIED);
    }

    @Test
    public void testOverwriteExisting() {
        PermissionCollection pc = new DefaultPermissionCollection();
        pc.add(new DotNamedPermission("resource.read",
                                      true));
        assertEquals(pc.collection().size(),
                     1);
        assertEquals(pc.get("resource.read").getResult(),
                     AuthorizationResult.ACCESS_GRANTED);

        pc.add(new DotNamedPermission("resource.read",
                                      false));
        assertEquals(pc.collection().size(),
                     1);
        assertEquals(pc.get("resource.read").getResult(),
                     AuthorizationResult.ACCESS_DENIED);
    }
}
