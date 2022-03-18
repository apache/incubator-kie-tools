/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.scenariosimulation.client.interfaces;

import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a function that accepts two arguments and produces a result.
 * This is the four-arity specialization of {@link Function}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object, Object, Object)}.
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @param <S> the type of the third argument to the function
 * @param <E> the type of the fourth argument to the function
 * @param <R> the type of the result of the function
 * @see Function
 */
@FunctionalInterface
public interface TetraFunction<T, U, S, E, R> {

    /**
     * Applies this function to the given arguments.
     * @param t the first function argument
     * @param u the second function argument
     * @param s the third function argument
     * @param e the fourth function argument
     * @return the function result
     */
    R apply(T t, U u, S s, E e);

    /**
     * Returns a composed function that first applies this function to
     * its input, and then applies the {@code after} function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     * @param <V> the type of output of the {@code after} function, and of the
     * composed function
     * @param after the function to apply after this function is applied
     * @return a composed function that first applies this function and then
     * applies the {@code after} function
     * @throws NullPointerException if after is null
     */
    default <V> TetraFunction<T, U, S, E, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t, U u, S s, E e) -> after.apply(apply(t, u, s, e));
    }
}
