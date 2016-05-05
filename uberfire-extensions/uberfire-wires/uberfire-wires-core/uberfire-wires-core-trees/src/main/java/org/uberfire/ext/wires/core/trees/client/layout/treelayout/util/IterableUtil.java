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
package org.uberfire.ext.wires.core.trees.client.layout.treelayout.util;

import java.util.Iterator;
import java.util.List;

/**
 * Util (general purpose) methods dealing with {@link Iterable}.
 * <p/>
 * Adapted from https://code.google.com/p/treelayout/ to be available to GWT clients
 * <p/>
 * @author Udo Borkowski (ub@abego.org)
 */
public class IterableUtil {

    private static class ReverseIterable<T> implements Iterable<T> {

        private List<T> list;

        public ReverseIterable( List<T> list ) {
            this.list = list;
        }

        @Override
        public Iterator<T> iterator() {
            return IteratorUtil.createReverseIterator( list );
        }
    }

    ;

    /**
     * Returns an {@link Iterable} with an iterator iterating the given list
     * from the end to the start.
     * <p/>
     * I.e. the iterator does the reverse of the {@link List#iterator()}.
     * @param <T>
     * @param list
     * @return a reverse {@link Iterable} of the list
     */
    public static <T> Iterable<T> createReverseIterable( List<T> list ) {
        // When the list is empty we can use the "forward" iterable (i.e. the
        // list itself) also as the reverseIterable as it will do nothing.
        if ( list.size() == 0 ) {
            return list;
        }

        return new ReverseIterable<T>( list );
    }
}
