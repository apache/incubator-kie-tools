/**
 * Copyright (c) 2016-2020, Mihai Emil Andronache
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
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
 * YAML Scalar reflected from an Object.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: bfd0c9e15131ae8d55a4212cadbc48fee250fd1b $
 * @since 4.3.3
 */
final class ReflectedYamlScalar extends BaseScalar {

    /**
     * Scalar object being reflected.
     */
    private final Object scalar;

    /**
     * Constructor.
     * @param scalar Scalar Object
     */
    ReflectedYamlScalar(final Object scalar) {
        this.scalar = scalar;
    }

    @Override
    public String value() {
        final String value;
        if(this.scalar == null) {
            value = null;
        } else {
            value = String.valueOf(scalar);
        }
        return value;
    }

    @Override
    public Comment comment() {
        return new Comment() {
            @Override
            public YamlNode yamlNode() {
                return ReflectedYamlScalar.this;
            }

            @Override
            public String value() {
                return "";
            }
        };
    }
}
