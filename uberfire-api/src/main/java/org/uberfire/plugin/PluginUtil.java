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
package org.uberfire.plugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * Utilities for working with external (GWT-compiled) plugins.
 */
public class PluginUtil {

    private PluginUtil() {
    }

    /**
     * {@link List} is a {@link JsType} but {@link Collection#iterator()} is
     * {@link JsIgnore}d and therefore not exported to JavaScript.
     * <p>
     * This method takes a list and converts it to a new list so it can be
     * iterated over in the current script (e.g. using enhanced for loops), even
     * if the instance was provided by an external (GWT-compiled) script.
     *
     * @param externalList A list, possibly provided by an external script. Must not be null.
     * @return an immutable list containing the original elements of the
     * provided list
     */
    public static <T> List<T> ensureIterable(List<T> externalList) {
        checkNotNull("externalList",
                     externalList);

        // toArray(T[]) is @JsIgnored
        @SuppressWarnings("unchecked")
        final List<T> tmp = (List<T>) Arrays.asList(externalList.toArray());
        return Collections.unmodifiableList(tmp);
    }

    /**
     * {@link Set} is a {@link JsType} but {@link Collection#iterator()} is
     * {@link JsIgnore}d and therefore not exported to JavaScript.
     * <p>
     * This method takes a set and converts it to a new set so it can be
     * iterated over in the current script (e.g. using enhanced for loops), even
     * if the instance was provided by an external (GWT-compiled) script.
     *
     * @param externalSet A set, possibly provided by an external script. Must not be null.
     * @return an immutable set containing the original elements of the provided
     * set
     */
    public static <T> Set<T> ensureIterable(Set<T> externalSet) {
        checkNotNull("externalSet",
                     externalSet);

        // toArray(T[]) is @JsIgnored
        @SuppressWarnings("unchecked")
        final List<T> tmp = (List<T>) Arrays.asList(externalSet.toArray());
        return Collections.unmodifiableSet(new HashSet<T>(tmp));
    }

    /**
     * {@link Integer} is not a {@link JsType} and can't be shared across
     * scripts.
     * <p>
     * This method converts a regular int to an {@link Integer} using -1 as a
     * placeholder for null.
     *
     * @param value
     * @return boxed {@link Integer}, null if provided value is -1.
     */
    public static Integer toInteger(int value) {
        return (value != -1) ? value : null;
    }
}
