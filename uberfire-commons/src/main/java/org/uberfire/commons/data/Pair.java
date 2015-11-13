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

package org.uberfire.commons.data;

/**
 * This is a simple class to store a pair of objects.
 * @param <K1>
 * @param <K2>
 */
public class Pair<K1, K2> {

    public static enum PairEqualsMode {
        BOTH,
        K1,
        K2
    }

    private final PairEqualsMode equalsMode;

    /**
     * First item.
     */
    private final K1 k1;

    /**
     * Second item.
     */
    private final K2 k2;

    /**
     * Creates a new pair using the two keys provided.
     * @param k1
     * @param k2
     */
    public Pair(
            final K1 k1,
            final K2 k2 ) {
        this.k1 = k1;
        this.k2 = k2;
        this.equalsMode = PairEqualsMode.BOTH;
    }

    /**
     * Creates a new pair using the two keys provided.
     * @param k1
     * @param k2
     */
    public Pair(
            final K1 k1,
            final K2 k2,
            final PairEqualsMode equalsMode ) {
        this.k1 = k1;
        this.k2 = k2;
        this.equalsMode = equalsMode;
    }

    /**
     * static factory method
     * @param <K1>
     * @param <K2>
     * @param k1
     * @param k2
     * @return
     */
    public static <K1, K2> Pair<K1, K2> newPair( final K1 k1,
                                                 final K2 k2 ) {
        return new Pair<K1, K2>( k1, k2, PairEqualsMode.BOTH );
    }

    /**
     * static factory method
     * @param <K1>
     * @param <K2>
     * @param k1
     * @param k2
     * @return
     */
    public static <K1, K2> Pair<K1, K2> newPair( final K1 k1,
                                                 final K2 k2,
                                                 final PairEqualsMode equalsMode ) {
        return new Pair<K1, K2>( k1, k2, equalsMode );
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final Pair<?, ?> pair = (Pair<?, ?>) o;

        if ( PairEqualsMode.BOTH.equals( equalsMode ) || PairEqualsMode.K1.equals( equalsMode ) ) {
            if ( k1 != null ? !k1.equals( pair.k1 ) : pair.k1 != null ) {
                return false;
            }
        }
        if ( PairEqualsMode.BOTH.equals( equalsMode ) || PairEqualsMode.K2.equals( equalsMode ) ) {
            if ( k2 != null ? !k2.equals( pair.k2 ) : pair.k2 != null ) {
                return false;
            }
        }

        return true;
    }

    /**
     * @return the first key
     */
    public K1 getK1() {
        return this.k1;
    }

    /**
     * @return the second key
     */
    public K2 getK2() {
        return this.k2;
    }

    @Override
    public int hashCode() {
        int result = 0;
        if ( PairEqualsMode.BOTH.equals( equalsMode ) || PairEqualsMode.K1.equals( equalsMode ) ) {
            result = k1 != null ? k1.hashCode() : 0;
        }
        if ( PairEqualsMode.BOTH.equals( equalsMode ) || PairEqualsMode.K2.equals( equalsMode ) ) {
            result = 31 * result + ( k2 != null ? k2.hashCode() : 0 );
        }
        return result;
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "k1=" + k1 +
                ", k2=" + k2 +
                '}';
    }
}