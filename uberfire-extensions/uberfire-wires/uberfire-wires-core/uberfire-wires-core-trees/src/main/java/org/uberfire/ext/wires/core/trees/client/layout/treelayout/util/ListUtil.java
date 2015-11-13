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

import java.util.List;

/**
 * Util (general purpose) methods dealing with {@link List}.
 * <p/>
 * Adapted from https://code.google.com/p/treelayout/ to be available to GWT clients
 * <p/>
 * @author Udo Borkowski (ub@abego.org)
 */
public class ListUtil {

    /**
     * @param <T>
     * @param list [!list.isEmpty()]
     * @return the last element of the list
     */
    public static <T> T getLast( List<T> list ) {
        return list.get( list.size() - 1 );
    }

}
