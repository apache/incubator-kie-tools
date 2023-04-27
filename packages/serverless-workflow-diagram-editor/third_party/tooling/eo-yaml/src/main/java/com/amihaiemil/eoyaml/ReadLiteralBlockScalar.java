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
 * Read Yaml literal block Scalar. This is a Scalar spanning multiple lines.
 * This Scalar's lines will be treated as separate lines and won't be folded
 * into a single line. Example of Literal Block Scalar:
 * <pre>
 *   literal block scalar: |
 *     a multiline text
 *     line two of the scalar
 *     line three of the scalar
 * </pre>
 * @author Sherif Waly (sherifwaly95@gmail.com)
 * @version $Id: ab7da7edcb6e5f87eb3f1bcc674488dbadab6c8d $
 * @since 1.0.2
 *
 */
final class ReadLiteralBlockScalar extends BaseScalar {

    /**
     * Yaml line just previous to the one where this scalar starts. E.g.
     * <pre>
     * 0  block:|
     * 1    line1
     * 2    line2
     * </pre>
     * In the above example the scalar consists of line1 and line2, while
     * "previous" is line 0.
     */
    private final YamlLine previous;

    /**
     * All lines of the YAML document.
     */
    private final AllYamlLines all;

    /**
     * The significant lines of this literal block scalar.
     */
    private final YamlLines significant;

    /**
     * Ctor.
     * @param lines All lines.
     */
    ReadLiteralBlockScalar(final AllYamlLines lines) {
        this(new YamlLine.NullYamlLine(), lines);
    }

    /**
     * Ctor.
     * @param previous Previous YAML line.
     * @param lines All yaml lines.
     */
    ReadLiteralBlockScalar(final YamlLine previous, final AllYamlLines lines) {
        this.previous = previous;
        this.all = lines;
        this.significant = new GreaterIndentation(
            previous,
            new Skip(
                lines,
                line -> line.number() <= previous.number(),
                line -> {
                    final YamlLine key = previous;
                    final Skip.Line skipLine = (Skip.Line) line;
                    if(skipLine.indentation() == key.indentation()) {
                        // mark that we finished the block by storing the
                        // block's key line
                        skipLine.store(key);
                    }
                    // if the key is set then we can safely skip remaining lines
                    return skipLine.getStored().equals(key);
                },
                line -> line.trimmed().endsWith("|"),
                line -> line.trimmed().startsWith("---"),
                line -> line.trimmed().startsWith("..."),
                line -> line.trimmed().startsWith("%"),
                line -> line.trimmed().startsWith("!!")
            )
        );
    }

    /**
     * Value of this scalar.
     * @return String
     */
    public String value() {
        StringBuilder builder = new StringBuilder();
        for (final YamlLine yamlLine: this.significant) {
            int previousIndent = previous.indentation();
            builder.append(yamlLine.contents(Math.max(previousIndent, 0)));
            builder.append(CompatibilityWrapper.lineSeparator());
        }
        return builder.toString();
    }

    @Override
    public Comment comment() {
        return new ReadComment(
            //@checkstyle LineLength (50 lines)
            new Backwards(
                new FirstCommentFound(
                    new Backwards(
                        new Skip(
                            this.all,
                            line -> {
                                final boolean skip;
                                if(this.previous.number() < 0) {
                                    if(this.significant.iterator().hasNext()) {
                                        skip = line.number() >= this.significant
                                                .iterator().next().number();
                                    } else {
                                        skip = false;
                                    }
                                } else {
                                    skip = line.number() >= this.previous.number();
                                }
                                return skip;
                            },
                            line -> line.trimmed().startsWith("---"),
                            line -> line.trimmed().startsWith("..."),
                            line -> line.trimmed().startsWith("%"),
                            line -> line.trimmed().startsWith("!!")
                        )
                    )
                )
            ),
            this
        );
    }

}
