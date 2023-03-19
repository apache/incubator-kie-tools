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
package org.dashbuilder.navigation;

import org.dashbuilder.navigation.workbench.NavWorkbenchCtx;
import org.junit.Test;
import org.uberfire.workbench.model.ActivityResourceType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class NavItemContextTest {

    @Test
    public void testEmpty() {
        NavItemContext ctx = NavItemContext.get("");
        assertEquals(ctx.getPropertyIds().size(), 0);

        ctx = NavItemContext.create();
        assertEquals(ctx.getPropertyIds().size(), 0);
    }

    @Test
    public void testParse() {
        NavItemContext ctx = NavItemContext.get("a=A;b=B;");
        assertEquals(ctx.getPropertyIds().size(), 2);
        assertEquals(ctx.getProperty("a"), "A");
        assertEquals(ctx.getProperty("b"), "B");
    }

    @Test
    public void testFormat() {
        NavItemContext ctx = NavItemContext.create();
        ctx.setProperty("a", "A");
        ctx.setProperty("b", "B");
        assertEquals(ctx.getPropertyIds().size(), 2);
        assertEquals(ctx.getProperty("a"), "A");
        assertEquals(ctx.getProperty("b"), "B");
        assertEquals(ctx.toString(), "a=A;b=B;");
    }

    @Test
    public void testPerspective() {
        NavWorkbenchCtx ctx = NavWorkbenchCtx.perspective("A");
        assertEquals(ctx.getPropertyIds().size(), 2);
        assertEquals(ctx.getResourceId(), "A");
        assertEquals(ctx.getResourceType(), ActivityResourceType.PERSPECTIVE);
        assertEquals(ctx.getProperty(NavWorkbenchCtx.RESOURCE_ID), "A");
        assertEquals(ctx.getProperty(NavWorkbenchCtx.RESOURCE_TYPE), "PERSPECTIVE");
        assertEquals(ctx.toString(), "resourceId=A;resourceType=PERSPECTIVE;");
    }

    @Test
    public void testPermissions() {
        NavWorkbenchCtx ctx = NavWorkbenchCtx.permission("p1", "p2", "p3");

        assertThat(ctx.getPermissions())
                .hasSize(3)
                .contains("p1", "p2", "p3");

        ctx.clearPermissions();

        assertThat(ctx.getPermissions())
                .isEmpty();
    }

    @Test
    public void testRemoveProperty() {
        NavWorkbenchCtx ctx = NavWorkbenchCtx.get("a=1;b=2;c=3");

        ctx.removeProperty("b");

        NavWorkbenchCtx expectedCtx = NavWorkbenchCtx.get("a=1;c=3");
        assertTrue(ctx.includesPropertiesOf(expectedCtx));
    }
}
