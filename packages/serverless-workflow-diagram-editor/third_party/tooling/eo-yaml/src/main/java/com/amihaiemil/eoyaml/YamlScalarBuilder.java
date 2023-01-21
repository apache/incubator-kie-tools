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

/**
 * Builder of Yaml Scalar. Implementations should be immutable and thread-safe.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: ed36a943b92db64522ba4a30d48818ec121cc53d $
 * @since 4.0.0
 */
public interface YamlScalarBuilder {

    /**
     * Add a line of text this Scalar. You can use this multiple
     * times or just once, if your String already contains NEW_LINE
     * chars.
     * @param value String
     * @return An instance of this builder.
     */
    YamlScalarBuilder addLine(final String value);

    /**
     * Build a plain Scalar. Ideally, you should use this when
     * your scalar is short, a single line of text.<br><br>
     * If you added more lines of text, all of them will be put together,
     * separated by spaces.
     * @return The built Scalar.
     */
    default Scalar buildPlainScalar() {
        return this.buildPlainScalar("");
    }

    /**
     * Build a Folded Block Scalar. Use this when your scalar has multiple
     * lines of text, but you don't care about the newlines, you want them
     * all separated by spaces. <br><br>
     *
     * The difference from buildPlainScalar() comes when you are printing
     * the created YAML:
     * <pre>
     *     plain: a very long scalar which should have been built as Folded
     *     folded:&gt;
     *       a very long scalar which
     *       has been folded for readability
     * </pre>
     *
     * @return The built Scalar.
     */
    default Scalar buildFoldedBlockScalar() {
        return this.buildFoldedBlockScalar("");
    }

    /**
     * Build a Literal Block Scalar. Use this when your scalar has multiple
     * lines and you want these lines to be separated.
     *
     * @return The built Scalar.
     */
    default Scalar buildLiteralBlockScalar() {
        return this.buildLiteralBlockScalar("");
    }

    /**
     * Build a plain Scalar. Ideally, you should use this when
     * your scalar is short, a single line of text.<br><br>
     * If you added more lines of text, all of them will be put together,
     * separated by spaces.
     * @param inline Inline comment referring to the built scalar. If it has
     *  more lines, they will be concatenated into a single line.
     * @return The built Scalar.
     */
    default Scalar buildPlainScalar(final String inline) {
        return this.buildPlainScalar("", inline);
    }

    /**
     * Build a plain Scalar. Ideally, you should use this when
     * your scalar is short, a single line of text.<br><br>
     * If you added more lines of text, all of them will be put together,
     * separated by spaces.
     * @param above The multiple line comment Comment above the scalar.
     * @param inline Inline comment referring to the built scalar. If it has
     *  more lines, they will be concatenated into a single line.
     * @return The built Scalar.
     */
    default Scalar buildPlainScalar(
        final Collection<String> above, final String inline) {
        return this.buildPlainScalar(
            String.join(CompatibilityWrapper.lineSeparator(), above), inline
        );
    }

    /**
     * Build a plain Scalar. Ideally, you should use this when
     * your scalar is short, a single line of text.<br><br>
     * If you added more lines of text, all of them will be put together,
     * separated by spaces.
     * @param above Comment above the scalar. It may have multiple lines.
     * @param inline Inline comment referring to the built scalar. If it has
     *  more lines, they will be concatenated into a single line.
     * @return The built Scalar.
     */
    Scalar buildPlainScalar(final String above, final String inline);

    /**
     * Build a Folded Block Scalar. Use this when your scalar has multiple
     * lines of text, but you don't care about the newlines, you want them
     * all separated by spaces. <br><br>
     *
     * The difference from buildPlainScalar() comes when you are printing
     * the created YAML:
     * <pre>
     *     plain: a very long scalar which should have been built as Folded
     *     folded:&gt;
     *       a very long scalar which
     *       has been folded for readability
     * </pre>
     * @param comment Comment referring to the built scalar.
     * @return The built Scalar.
     */
    Scalar buildFoldedBlockScalar(final String comment);

    /**
     * Build a Literal Block Scalar. Use this when your scalar has multiple
     * lines and you want these lines to be separated.
     * @param comment Comment referring to the built scalar.
     * @return The built Scalar.
     */
    Scalar buildLiteralBlockScalar(final String comment);

}
