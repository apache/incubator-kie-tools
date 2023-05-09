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
 * Nested Yaml types. If a YAML line ends with one
 * of these, it means a certain type of YAML Node follows.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: af6f85c38951818fe986ace378db53fce3893d7e $
 * @since 1.0.2
 */
final class Follows {

    /**
     * Hidden ctor.
     */
    private Follows() {
    }

    /**
     * If this is the last char on a line, it means a folded block scalar
     * should be nested bellow (see Example 2.15 from YAML spec 1.2).
     * <pre>
     *   foldedScalar: >
     *     a long line split into
     *     several short
     *     lines for readability
     * </pre>
     */
    static final String FOLDED_BLOCK_SCALAR = ">";

    /**
     * If this is the last char on a line, it means a literal block scalar
     * should be nested bellow.
     * <pre>
     *   literalScalar: |
     *     line 1
     *     line 2
     *     line 3
     * </pre>
     */
    static final String LITERAL_BLOCK_SCALAR = "|";

    /**
     * If the line ends with this, it means a folded sequence follows after it.
     * This is a RegEx pattern because we want it to work even if there are
     * spaces between the | and the -. Both "| -" and "|-" line endings
     * should be fine.
     * E.g.
     * <pre>
     *     foldedSequence: |-
     *       some
     *       sequence
     *       values
     * </pre>
     */
    static final String FOLDED_SEQUENCE = "^.+\\|[ ]*\\-$";
}
