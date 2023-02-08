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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.amihaiemil.eoyaml.exceptions.YamlIndentationException;
/**
 * YamlLines decorator which iterates over them and verifies
 * that their indentation is correct.<br><br>
 *
 * This class can be used as follows:
 *
 * <pre>
 *
 * YamlLines wellIndented = new SameIndentationLevel(
 *     new WellIndented(lines)
 * );//Iterate over the lines which are at the same indentation level
 * </pre>
 * or
 * <pre>
 *
 * YamlLines wellIndented = new SameIndentationLevel(
 *     new WellIndented(
 *         new NoDirectivesOrMarkers(
 *             lines
 *         )//ignore markers or directives
 *     )
 * );//Iterate over the lines which are at the same indentation level
 * </pre>
 * @checkstyle ExecutableStatementCount (400 lines)
 * @checkstyle CyclomaticComplexity (400 lines)
 * @checkstyle NestedIfDepth (400 lines)
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 5c56959866454db16c9d07e9546bf73c6b0b1930 $
 * @since 3.1.2
 */
final class WellIndented implements YamlLines {

    /**
     * YamlLines.
     */
    private final YamlLines yamlLines;

    /**
     * If this is true, then we will try to adjust a wrong indentation,
     * instead of throwin an exception. This mechanism is not safe becuase
     * the resulting YAML might not be the expected one.
     */
    private final boolean guessIndentation;

    /**
     * Ctor.
     * @param yamlLines The Yaml lines.
     */
    WellIndented(final YamlLines yamlLines) {
        this(yamlLines, false);
    }

    /**
     * Ctor.
     * @param yamlLines The Yaml lines.
     * @param guessIndentation If indentation is not correct, try to
     *  adjust it instead of throwing an exception. This mechanism is not
     *  safe because the resulting YAML might not be the expected one.
     */
    WellIndented(final YamlLines yamlLines, final boolean guessIndentation) {
        this.yamlLines = yamlLines;
        this.guessIndentation = guessIndentation;
    }

    /**
     * Returns an iterator over these Yaml lines.
     * It will verify that each line is properly indented in relation
     * to the previous one and will complain if the indentation is not
     * correct.
     * @checkstyle LineLength (50 lines)
     * @return Iterator over these yaml lines.
     */
    @Override
    public Iterator<YamlLine> iterator() {
        final Iterator<YamlLine> iterator = this.yamlLines.iterator();
        final List<YamlLine> wellIndented = new ArrayList<>();
        YamlLine previous;
        boolean withinBlockScalar = false;
        if(iterator.hasNext()) {
            previous = iterator.next();
            wellIndented.add(previous);
            while(iterator.hasNext()) {
                YamlLine line = iterator.next();
                if(!(previous instanceof YamlLine.NullYamlLine)) {
                    final CharSequence prevLineLastChar =
                        previous.trimmed().substring(previous.trimmed().length() - 1);
                    if(">|".contains(prevLineLastChar)) {
                        withinBlockScalar = true;
                    }
                    int prevIndent = previous.indentation();
                    if(previous.trimmed().matches("^\\s*-.*:(|\\s.*)$")) {
                        withinBlockScalar = false;
                        prevIndent += 2;
                    }
                    if(!withinBlockScalar) {
                        int lineIndent = line.indentation();
                        if (previous.requireNestedIndentation()) {
                            if (lineIndent < prevIndent + 2) {
                                throw new YamlIndentationException(
                                    "Indentation of line " + (line.number() + 1)
                                    + " [" + line.trimmed() + "]"
                                    + " is not ok. It should be greater than the one"
                                    + " of line " + (previous.number() + 1)
                                    + " [" + previous.trimmed() + "]"
                                    + " by at least 2 spaces."
                                );
                            }
                        } else {
                            if (!"---".equals(previous.trimmed()) && lineIndent > prevIndent) {
                                if (!":".contains(prevLineLastChar)) {
                                    throw new YamlIndentationException(
                                        "Indentation of line " + (line.number() + 1)
                                        + " [" + line.trimmed() + "]"
                                        + " is greater than the one of line "
                                        + (previous.number() + 1)
                                        + " [" + previous.trimmed() + "]. "
                                        + "It should be less or equal."
                                    );
                                }
                            }
                        }
                    }
                }
                previous = line;
                wellIndented.add(line);
            }
        }
        return wellIndented.iterator();
    }

    @Override
    public Collection<YamlLine> original() {
        return this.yamlLines.original();
    }

    @Override
    public YamlNode toYamlNode(final YamlLine prev) {
        return this.yamlLines.toYamlNode(prev);
    }

}
