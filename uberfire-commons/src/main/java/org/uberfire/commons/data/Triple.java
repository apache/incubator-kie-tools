/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

public class Triple<K1, K2, K3> {

    /**
     * First item.
     */
    private final K1 k1;

    /**
     * Second item.
     */
    private final K2 k2;

    /**
     * Third item.
     */
    private final K3 k3;

    /**
     * Creates a new triple using the keys provided.
     * @param k1
     * @param k2
     */
    public Triple(
            final K1 k1,
            final K2 k2,
            final K3 k3 ) {
        this.k1 = k1;
        this.k2 = k2;
        this.k3 = k3;
    }

    /**
     * static factory method
     * @param <K1>
     * @param <K2>
     * @param k1
     * @param k2
     * @return
     */
    public static <K1, K2, K3> Triple<K1, K2, K3> newTriple( final K1 k1,
                                                             final K2 k2,
                                                             final K3 k3 ) {
        return new Triple<K1, K2, K3>( k1, k2, k3 );
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

    /**
     * @return the third key
     */
    public K3 getK3() {
        return this.k3;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Triple ) ) {
            return false;
        }

        Triple triple = (Triple) o;

        if ( k1 != null ? !k1.equals( triple.k1 ) : triple.k1 != null ) {
            return false;
        }
        if ( k2 != null ? !k2.equals( triple.k2 ) : triple.k2 != null ) {
            return false;
        }
        if ( k3 != null ? !k3.equals( triple.k3 ) : triple.k3 != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = k1 != null ? k1.hashCode() : 0;
        result = 31 * result + ( k2 != null ? k2.hashCode() : 0 );
        result = 31 * result + ( k3 != null ? k3.hashCode() : 0 );
        return result;
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "k1=" + k1 +
                ", k2=" + k2 +
                ", k3=" + k3 +
                '}';
    }
}
