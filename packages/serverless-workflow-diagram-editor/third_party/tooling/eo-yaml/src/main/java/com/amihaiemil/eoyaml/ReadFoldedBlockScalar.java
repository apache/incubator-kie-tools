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

import java.util.ArrayList;
import java.util.List;

/**
 * Read Yaml folded block Scalar. This is a Scalar spanning multiple lines.
 * This Scalar's newlines will be ignored ("folded"), the scalar's value
 * is a single line of text split in multiple lines for readability.<br><br>
 * Example of Folded Block Scalar:
 * <pre>
 *   folded block scalar: >
 *     a long line split into
 *     several short
 *     lines for readability
 * </pre>
 * @author Sherif Waly (sherifwaly95@gmail.com)
 * @version $Id: 34269ef5370463a3aa4170c76634ad8f385435a7 $
 * @since 1.0.2
 */
final class ReadFoldedBlockScalar extends BaseFoldedScalar {

    /**
     * Yaml line just previous to the one where this scalar starts. E.g.
     * <pre>
     * 0  folded:>
     * 1    folded scalar on more
     * 2    lines for more
     * 3    readability
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
     * The significant lines of this folded block scalar.
     */
    private final YamlLines significant;

    /**
     * Ctor.
     * @param lines All lines.
     */
    ReadFoldedBlockScalar(final AllYamlLines lines) {
        this(new YamlLine.NullYamlLine(), lines);
    }

    /**
     * Ctor.
     * @param previous Previous YAML line.
     * @param lines Given lines to represent.
     */
    ReadFoldedBlockScalar(final YamlLine previous, final AllYamlLines lines) {
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
                line -> line.trimmed().endsWith(">"),
                line -> line.trimmed().startsWith("---"),
                line -> line.trimmed().startsWith("..."),
                line -> line.trimmed().startsWith("%"),
                line -> line.trimmed().startsWith("!!")
            )
        );
    }

    /**
     * Checks whether StringBuilder do not end with newline or not.
     * @param builder StringBuilder
     * @return Boolean Whether builder do not end with newline char or not
     */
    private boolean doNotEndWithNewLine(final StringBuilder builder) {
        return builder.length() > 0
                && !builder.toString().endsWith(CompatibilityWrapper.lineSeparator());
    }
    /**
     * Value of this scalar.
     * @return String
     */
    public String value() {
        StringBuilder builder = new StringBuilder();
        final String newLine = CompatibilityWrapper.lineSeparator();
        for(final YamlLine line: this.significant) {
            if(line.trimmed().length() == 0 || line.indentation() > 0) {
                if(this.doNotEndWithNewLine(builder)) {
                    builder.append(newLine);
                }
                int indentation = line.indentation();
                for(int i = 0; i < indentation; i++) {
                    builder.append(' ');
                }
                builder.append(line.trimmed());
                builder.append(newLine);
            } else {
                if(this.doNotEndWithNewLine(builder)) {
                    builder.append(' ');
                }
                builder.append(line.trimmed());
            }
        }
        return builder.toString();
    }

    @Override
    public Comment comment() {
        //@checkstyle LineLength (50 lines)
        return new ReadComment(
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

    @Override
    List<String> unfolded() {
        final List<String> unfolded = new ArrayList<>();
        for(final YamlLine line : this.significant) {
            unfolded.add(line.toString());
        }
        return unfolded;
    }

}
