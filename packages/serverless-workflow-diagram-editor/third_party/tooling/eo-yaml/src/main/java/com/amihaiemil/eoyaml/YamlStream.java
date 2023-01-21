/**
 * Copyright (c) 2016-2020, Mihai Emil Andronache
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * Neither the name of the copyright holder nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package com.amihaiemil.eoyaml;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * A YAML Stream of documents. Documents are separated by 3 dashes (---).<br>
 * This interface also offers integrations with Java 8's Stream API.<br>
 * All the methods have a default implementations based on the YamlNode
 * values Collection.
 *
 * @checkstyle FinalParameters (400 lines)
 * @checkstyle JavadocMethod (400 lines)
 * @checkstyle LineLength (400 lines)
 * @checkstyle ParameterName (400 lines)
 * @checkstyle FinalParameters (400 lines)
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: aad913b2279664e8464f5d0a6f56bb4658687351 $
 * @since 3.1.1
 */
public interface YamlStream extends YamlNode, Stream<YamlNode> {

    /**
     * Fetch the values from this stream.
     * @return Collection of {@link YamlNode}
     */
    Collection<YamlNode> values();

    default Comment comment() {
        return new BuiltComment(this, "");
    }

    default Iterator<YamlNode> iterator() {
        return this.values().stream().iterator();
    }

    default Spliterator<YamlNode> spliterator() {
        return this.values().stream().spliterator();
    }

    default boolean isParallel() {
        return this.values().stream().isParallel();
    }

    default Stream<YamlNode> sequential() {
        return this.values().stream().sequential();
    }

    default Stream<YamlNode> parallel() {
        return this.values().stream().parallel();
    }

    default Stream<YamlNode> unordered() {
        return this.values().stream().unordered();
    }

    default Stream<YamlNode> onClose(Runnable closeHandler) {
        return this.values().stream().onClose(closeHandler);
    }

    default void close() {
        this.values().stream().close();
    }

    default Stream<YamlNode> filter(Predicate<? super YamlNode> predicate) {
        return this.values().stream().filter(predicate);
    }

    default <R> Stream<R> map(Function<? super YamlNode, ? extends R> mapper) {
        return this.values().stream().map(mapper);
    }

    default IntStream mapToInt(ToIntFunction<? super YamlNode> mapper) {
        return this.values().stream().mapToInt(mapper);
    }

    default LongStream mapToLong(ToLongFunction<? super YamlNode> mapper) {
        return this.values().stream().mapToLong(mapper);
    }

    default DoubleStream mapToDouble(ToDoubleFunction<? super YamlNode> mapper) {
        return this.values().stream().mapToDouble(mapper);
    }

    default <R> Stream<R> flatMap(Function<? super YamlNode, ? extends Stream<? extends R>> mapper) {
        return this.values().stream().flatMap(mapper);
    }

    default IntStream flatMapToInt(Function<? super YamlNode, ? extends IntStream> mapper) {
        return this.values().stream().flatMapToInt(mapper);
    }

    default LongStream flatMapToLong(Function<? super YamlNode, ? extends LongStream> mapper) {
        return this.values().stream().flatMapToLong(mapper);
    }

    default DoubleStream flatMapToDouble(Function<? super YamlNode, ? extends DoubleStream> mapper) {
        return this.values().stream().flatMapToDouble(mapper);
    }

    default Stream<YamlNode> distinct() {
        return this.values().stream().distinct();
    }

    default Stream<YamlNode> sorted() {
        return this.values().stream().sorted();
    }

    default Stream<YamlNode> sorted(Comparator<? super YamlNode> comparator) {
        return this.values().stream().sorted(comparator);
    }

    default Stream<YamlNode> peek(Consumer<? super YamlNode> action) {
        return this.values().stream().peek(action);
    }

    default Stream<YamlNode> limit(long maxSize) {
        return this.values().stream().limit(maxSize);
    }

    default Stream<YamlNode> skip(long n) {
        return this.values().stream().skip(n);
    }

    default void forEach(Consumer<? super YamlNode> action) {
        this.values().stream().forEach(action);
    }

    default void forEachOrdered(Consumer<? super YamlNode> action) {
        this.values().stream().forEachOrdered(action);
    }

    default Object[] toArray() {
        return this.values().stream().toArray();
    }

    default <A> A[] toArray(IntFunction<A[]> generator) {
        return this.values().stream().toArray(generator);
    }

    default YamlNode reduce(YamlNode identity, BinaryOperator<YamlNode> accumulator) {
        return this.values().stream().reduce(identity, accumulator);
    }

    default Optional<YamlNode> reduce(BinaryOperator<YamlNode> accumulator) {
        return this.values().stream().reduce(accumulator);
    }

    default <U> U reduce(U identity, BiFunction<U, ? super YamlNode, U> accumulator, BinaryOperator<U> combiner) {
        return this.values().stream().reduce(identity, accumulator, combiner);
    }

    default <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super YamlNode> accumulator,
                          BiConsumer<R, R> combiner) {
        return this.values().stream().collect(supplier, accumulator, combiner);
    }

    default <R, A> R collect(Collector<? super YamlNode, A, R> collector) {
        return this.values().stream().collect(collector);
    }

    default Optional<YamlNode> min(Comparator<? super YamlNode> comparator) {
        return this.values().stream().min(comparator);
    }

    default Optional<YamlNode> max(Comparator<? super YamlNode> comparator) {
        return this.values().stream().max(comparator);
    }

    default long count() {
        return this.values().stream().count();
    }

    default boolean anyMatch(Predicate<? super YamlNode> predicate) {
        return this.values().stream().anyMatch(predicate);
    }

    default boolean allMatch(Predicate<? super YamlNode> predicate) {
        return this.values().stream().allMatch(predicate);
    }

    default boolean noneMatch(Predicate<? super YamlNode> predicate) {
        return this.values().stream().noneMatch(predicate);
    }

    default Optional<YamlNode> findFirst() {
        return this.values().stream().findFirst();
    }

    default Optional<YamlNode> findAny() {
        return this.values().stream().findAny();
    }


}
