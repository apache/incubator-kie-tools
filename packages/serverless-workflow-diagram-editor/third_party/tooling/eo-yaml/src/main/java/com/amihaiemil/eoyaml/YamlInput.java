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

import java.io.IOException;

/**
 * Yaml input.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: b2f1463fc24dde66e8e44f98ed2bd40afd37ba56 $
 * @since 1.0.0
 */
public interface YamlInput {

    /**
     * Read the given input as a Yaml mapping.
     * @return Read YamlMapping.
     * @throws IOException if the input cannot be read for some reason
     */
    YamlMapping readYamlMapping() throws IOException;

    /**
     * Read the given input as a Yaml sequence.
     * @return Read YamlSequence.
     * @throws IOException if the input cannot be read for some reason
     */
    YamlSequence readYamlSequence() throws IOException;

    /**
     * Read the given input as a Yaml stream.
     * @return Read YamlStream.
     * @throws IOException if the input cannot be read for some reason
     */
    YamlStream readYamlStream() throws IOException;

    /**
     * Read the given input as a plain scalar. e.g.
     * <pre>
     * ---
     * lonelyScalar
     * ...
     * </pre>
     * @return Read Scalar.
     * @throws IOException if the input cannot be read for some reason
     */
    Scalar readPlainScalar() throws IOException;

    /**
     * Read the given input as a folded block scalar. e.g.
     * <pre>
     * ---
     * &gt;
     *   Long scalar which
     *   has been folded for
     *   readability
     * ...
     * </pre>
     * @return Read Scalar.
     * @throws IOException if the input cannot be read for some reason
     */
    Scalar readFoldedBlockScalar() throws IOException;

    /**
     * Read the given input as a literal block scalar. e.g.
     * <pre>
     * ---
     * |
     *   line1
     *   line2
     *   line3
     * ...
     * </pre>
     * @return Read Scalar.
     * @throws IOException if the input cannot be read for some reason
     */
    Scalar readLiteralBlockScalar() throws IOException;
}
