/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.promise;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import elemental2.promise.Promise;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;

import static org.uberfire.client.promise.PromisePolyfillBootstrapper.ensurePromiseApiIsAvailable;

@Dependent
public class Promises {

    @PostConstruct
    public void init() {
        ensurePromiseApiIsAvailable();
    }

    // Reducers

    /**
     * Returns a resolved Promise when every Promise in the list is resolved.
     * If any Promise is rejected, the resulting Promise will be rejected.
     */
    @SafeVarargs
    public final <O> Promise<O> all(final Promise<O>... promises) {
        return Arrays.stream(promises).reduce(resolve(), (p1, p2) -> p1.then(ignore -> p2));
    }

    /**
     * Maps the objects to Promises using the provided function then behaves just like {@link org.uberfire.client.promise.Promises#all}.
     */
    public <T, O> Promise<O> all(final List<T> objects, final Function<T, Promise<O>> f) {
        return objects.stream().map(f).reduce(resolve(), (p1, p2) -> p1.then(ignore -> p2));
    }

    /**
     * Reduces a list of promises using the accumulator passed.
     */
    public final <O> Promise<O> reduce(final Promise<O> identity,
                                       final Collection<Promise<O>> promises,
                                       final BinaryOperator<Promise<O>> accumulator) {
        return promises.stream().reduce(identity, accumulator);
    }

    /**
     * Maps the objects to Promises using the provided function but only execute the Promises when the
     * previous Promise is resolved. If a rejection occurs in the middle of the chain, the remaining
     * Promises are not executed and the resulting Promise is rejected.
     */
    public <T, O> Promise<O> reduceLazily(final List<T> objects,
                                          final Function<T, Promise<O>> f) {
        return objects.stream()
                .<Supplier<Promise<O>>>
                        map(o -> () -> f.apply(o))
                .<Supplier<Promise<O>>>
                        reduce(this::resolve,
                               (p1, p2) -> () -> p1.get().then(ignore -> p2.get())
                )
                .get();
    }

    /**
     * Behaves just like {@link org.uberfire.client.promise.Promises#reduceLazily} but exposes a reference to the Promise chain as a
     * parameter to the mapping function.
     */
    public <T, O> Promise<O> reduceLazilyChaining(final List<T> objects,
                                                  final BiFunction<Supplier<Promise<O>>, T, Promise<O>> f) {

        return objects.stream()
                .<Function<Supplier<Promise<O>>, Supplier<Promise<O>>>>
                        map(o -> next -> () -> f.apply(next, o))
                .<Function<Supplier<Promise<O>>, Supplier<Promise<O>>>>
                        reduce(next -> this::resolve,
                               (p1, p2) -> uberNext -> () -> {
                                   final Supplier<Promise<O>> next = p2.apply(uberNext);
                                   final Supplier<Promise<O>> chain = () -> next.get().then(ignore -> uberNext.get());
                                   return p1.apply(chain).get().then(ignore -> next.get());
                               }
                )
                .apply(this::resolve).get();
    }

    // Callers

    /**
     * Promisifies a {@link Caller} remote call. If an exception is thrown inside the call function, the
     * resulting Promise is rejected with a {@link org.uberfire.client.promise.Promises.Error} instance.
     */
    public <T, S> Promise<S> promisify(final Caller<T> caller,
                                       final Function<T, S> call) {

        return create((resolve, reject) -> call.apply(caller.call(
                (RemoteCallback<S>) resolve::onInvoke,
                defaultRpcErrorCallback(reject))));
    }

    /**
     * Promisifies a {@link Caller} remote call. If an exception is thrown inside the call function, the
     * resulting Promise is rejected with a {@link org.uberfire.client.promise.Promises.Error} instance.
     */
    public <T, S> Promise<S> promisify(final Caller<T> caller,
                                       final Consumer<T> call) {

        return create((resolve, reject) -> call.accept(caller.call(
                (RemoteCallback<S>) resolve::onInvoke,
                defaultRpcErrorCallback(reject))));
    }

    private <M> ErrorCallback<M> defaultRpcErrorCallback(final Promise.PromiseExecutorCallbackFn.RejectCallbackFn reject) {
        return (final M o, final Throwable throwable) -> {
            reject.onInvoke(new Promises.Error<>(o, throwable));
            return false;
        };
    }

    /**
     * To be used inside {@link Promise#catch_} blocks. Decides whether to process a RuntimeException that
     * caused a prior Promise rejection or to process an expected object rejected by a prior Promise. To proceed
     * with default error handlers, reject the untreated exception inside the catchBlock function.
     */
    @SuppressWarnings("unchecked")
    public <V, T> Promise<T> catchOrExecute(final Object o,
                                            final Function<RuntimeException, Promise<T>> catchBlock,
                                            final Function<V, Promise<T>> expectedRejectionHandler) {

        if (o instanceof JavaScriptObject) {
            // A RuntimeException occurred inside a promise and was transformed in a JavaScriptObject
            return resolve()
                    .then(i -> catchBlock.apply(new RuntimeException("Client-side exception inside Promise: " + o.toString())))
                    .catch_(this::handleCatchBlockExceptions);
        }

        if (o instanceof RuntimeException) {
            return resolve()
                    .then(i -> catchBlock.apply((RuntimeException) o))
                    .catch_(this::handleCatchBlockExceptions);
        }

        if (o instanceof Promises.Error) {
            return resolve()
                    .then(i -> catchBlock.apply((RuntimeException) ((Error) o).getThrowable()))
                    .catch_(this::handleCatchBlockExceptions);
        }

        return expectedRejectionHandler.apply((V) o);
    }

    private <T> Promise<T> handleCatchBlockExceptions(final Object rejectedObject) {

        if (rejectedObject instanceof Throwable) {
            GWT.getUncaughtExceptionHandler().onUncaughtException((Throwable) rejectedObject);
            return resolve();
        }

        return reject(rejectedObject);
    }

    public <T> Promise<T> resolve() {
        return resolve(null);
    }

    public <T> Promise<T> resolve(final T object) {
        return create((resolve, reject) -> resolve.onInvoke(object));
    }

    public <T> Promise<T> reject(final Object object) {
        return create((resolve, reject) -> reject.onInvoke(object));
    }

    public <T> Promise<T> create(final Promise.PromiseExecutorCallbackFn<T> executor) {
        return new Promise<>(executor);
    }

    public static class Error<T> {

        private final T o;

        private final Throwable throwable;

        private Error(final T o, final Throwable throwable) {
            this.o = o;
            this.throwable = throwable;
        }

        private T getObject() {
            return o;
        }

        private Throwable getThrowable() {
            return throwable;
        }
    }
}
