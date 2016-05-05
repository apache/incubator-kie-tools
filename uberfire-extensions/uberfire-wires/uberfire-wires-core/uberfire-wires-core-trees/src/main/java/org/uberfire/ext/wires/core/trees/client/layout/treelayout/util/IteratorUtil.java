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
import java.util.ListIterator;

/**
 * Util (general purpose) methods dealing with {@link Iterator}.
 * <p/>
 * Adapted from https://code.google.com/p/treelayout/ to be available to GWT clients
 * <p/>
 * @author Udo Borkowski (ub@abego.org)
 */
public class IteratorUtil {

    private static class ReverseIterator<T> implements Iterator<T> {

        private ListIterator<T> listIterator;

        public ReverseIterator( List<T> list ) {
            this.listIterator = list.listIterator( list.size() );
        }

        @Override
        public boolean hasNext() {
            return listIterator.hasPrevious();
        }

        @Override
        public T next() {
            return listIterator.previous();
        }

        @Override
        public void remove() {
            listIterator.remove();
        }
    }

    /**
     * Returns an {@link Iterator} iterating the given list from the end to the
     * start.
     * <p/>
     * I.e. the iterator does the reverse of the {@link List#iterator()}.
     * @param <T>
     * @param list
     * @return a reverse {@link Iterator} of the list
     */
    public static <T> Iterator<T> createReverseIterator( List<T> list ) {
        return new ReverseIterator<T>( list );
    }
}
