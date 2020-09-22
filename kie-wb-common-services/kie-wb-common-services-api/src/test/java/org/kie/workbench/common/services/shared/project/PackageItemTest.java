/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.shared.project;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PackageItemTest {

    @Test
    public void testEmptyConstructorForErrai() {
        assertNull(new PackageItem().getCaption());
        assertNull(new PackageItem().getPackageName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPackageNameNull() {
        new PackageItem(null,
                        "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCaptionNull() {
        new PackageItem("",
                        null);
    }

    @Test
    public void testEquals() {
        final PackageItem packageItem = new PackageItem("",
                                                        PackageItem.DEFAULT_PACKAGE_NAME);
        assertTrue(
                packageItem
                        .equals(packageItem));

        assertTrue(
                new PackageItem("",
                                PackageItem.DEFAULT_PACKAGE_NAME)
                        .equals(new PackageItem("",
                                                PackageItem.DEFAULT_PACKAGE_NAME)));
        assertTrue(
                new PackageItem("org.test",
                                "org.test")
                        .equals(new PackageItem("org.test",
                                                "org.test")));

        assertFalse(
                new PackageItem("",
                                PackageItem.DEFAULT_PACKAGE_NAME)
                        .equals("not package item"));

        assertFalse(
                new PackageItem("org.test",
                                "org.test")
                        .equals(new PackageItem("",
                                                PackageItem.DEFAULT_PACKAGE_NAME)));
    }

    @Test
    public void testHashcode() {
        assertEquals(
                new PackageItem("",
                                PackageItem.DEFAULT_PACKAGE_NAME).hashCode(),
                new PackageItem("",
                                PackageItem.DEFAULT_PACKAGE_NAME).hashCode());
        assertEquals(
                new PackageItem("org.test",
                                "org.test").hashCode(),
                new PackageItem("org.test",
                                "org.test").hashCode());

        assertNotEquals(
                new PackageItem("org.test",
                                "org.test").hashCode(),
                new PackageItem("",
                                PackageItem.DEFAULT_PACKAGE_NAME).hashCode());
    }
}