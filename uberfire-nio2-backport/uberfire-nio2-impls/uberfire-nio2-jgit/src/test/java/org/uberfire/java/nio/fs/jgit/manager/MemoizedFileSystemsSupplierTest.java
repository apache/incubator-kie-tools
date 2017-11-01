/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.java.nio.fs.jgit.manager;

import java.util.function.Supplier;

import org.junit.Test;

import static org.junit.Assert.*;

public class MemoizedFileSystemsSupplierTest {

    public static int instanceCount = 0;

    @Test
    public void supplierTest() {

        getSupplier().get();
        getSupplier().get();
        assertEquals(2,
                     instanceCount);

        instanceCount = 0;
        final Supplier<DummyObject> supplier = getLazySupplier();
        supplier.get();
        supplier.get();
        supplier.get();
        supplier.get();
        assertEquals(1,
                     instanceCount);
    }

    Supplier<DummyObject> getLazySupplier() {
        return MemoizedFileSystemsSupplier.of(getSupplier());
    }

    Supplier<DummyObject> getSupplier() {
        return () -> new DummyObject();
    }

    private class DummyObject {

        public DummyObject() {
            test();
            instanceCount++;
        }

        public void test() {
            System.out.println("new Instance");
        }
    }
}