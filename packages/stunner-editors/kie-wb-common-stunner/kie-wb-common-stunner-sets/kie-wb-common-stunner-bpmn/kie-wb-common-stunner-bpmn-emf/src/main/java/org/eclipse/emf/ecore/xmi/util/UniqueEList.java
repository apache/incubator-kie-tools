/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.eclipse.emf.ecore.xmi.util;

import java.util.Collection;

/**
 * A <code>BasicEList</code> that allows only {@link #isUnique unique} elements.
 */
public class UniqueEList<E> extends BasicEList<E> {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an empty instance with no initial capacity.
     */
    public UniqueEList() {
        super();
    }

    /**
     * Creates an empty instance with the given capacity.
     * @param initialCapacity the initial capacity of the list before it must grow.
     * @throws IllegalArgumentException if the <code>initialCapacity</code> is negative.
     */
    public UniqueEList(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Creates an instance that is a copy of the collection, with duplicates removed.
     * @param collection the initial contents of the list.
     */
    public UniqueEList(Collection<? extends E> collection) {
        super(collection.size());
        addAll(collection);
    }

    /**
     * Returns <code>true</code> because this list requires uniqueness.
     * @return <code>true</code>.
     */
    @Override
    protected boolean isUnique() {
        return true;
    }

    /**
     * A <code>UniqueEList</code> that {@link #useEquals uses} <code>==</code> instead of <code>equals</code> to compare members.
     */
    public static class FastCompare<E> extends UniqueEList<E> {

        private static final long serialVersionUID = 1L;

        /**
         * Creates an empty instance with no initial capacity.
         */
        public FastCompare() {
            super();
        }

        /**
         * Creates an empty instance with the given capacity.
         * @param initialCapacity the initial capacity of the list before it must grow.
         * @throws IllegalArgumentException if the <code>initialCapacity</code> is negative.
         */
        public FastCompare(int initialCapacity) {
            super(initialCapacity);
        }

        /**
         * Creates an instance that is a copy of the collection, with duplicates removed.
         * @param collection the initial contents of the list.
         */
        public FastCompare(Collection<? extends E> collection) {
            super(collection.size());
            addAll(collection);
        }

        /**
         * Returns <code>false</code> because this list uses <code>==</code>.
         * @return <code>false</code>.
         */
        @Override
        protected boolean useEquals() {
            return false;
        }
    }
}
