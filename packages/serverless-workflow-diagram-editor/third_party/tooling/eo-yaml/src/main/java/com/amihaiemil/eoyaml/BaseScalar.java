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

/**
 * Base Yaml Scalar which all implementations of Scalar should extend.
 * It implementing toString(), equals, hashcode and compareTo methods.
 * <br><br>
 * These methods should be default methods on the interface,
 * but we are not allowed to have default implementations of java.lang.Object
 * methods.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: d6f36e4a76dd4ff5cf92ac56979eb07335fafe12 $
 * @since 4.0.0
 */
abstract class BaseScalar extends BaseYamlNode implements Scalar {

    @Override
    public final Node type() {
        return Node.SCALAR;
    }

    /**
     * Equality of two objects.
     * @param other Reference to the right hand Scalar
     * @return True if object are equal and False if are not.
     */
    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (other == null || !(other instanceof Scalar)) {
            result = false;
        } else if (this == other) {
            result = true;
        } else {
            result = this.compareTo((Scalar) other) == 0;
        }
        return result;
    }

    /**
     * Hash Code of this scalar.
     * @return Value of hashCode() of type int.
     */
    @Override
    public int hashCode() {
        return this.value().hashCode();
    }

    /**
     * Compare this Scalar to another node.<br><br>
     *
     * A Scalar is always considered less than a Sequence or a Mapping.<br>
     * If o is Scalar then their String values are compared lexicographically
     *
     * @param other The other AbstractNode.
     * @return
     *  a value < 0 if this < o <br>
     *   0 if this == o or <br>
     *  a value > 0 if this > o
     */
    @Override
    public int compareTo(final YamlNode other) {
        int result = -1;
        if (this == other) {
            result = 0;
        } else if (other == null) {
            result = 1;
        } else if (other instanceof Scalar) {
            final String value = this.value();
            final String otherVal = ((Scalar) other).value();
            if(value == null && otherVal == null) {
                result = 0;
            } else if(value != null && otherVal == null) {
                result = 1;
            } else if (value == null && otherVal != null) {
                result = -1;
            } else {
                result = this.value().compareTo(otherVal);
            }
        }
        return result;
    }

    @Override
    public final boolean isEmpty() {
        return this.value() == null || this.value().isEmpty();
    }
}
