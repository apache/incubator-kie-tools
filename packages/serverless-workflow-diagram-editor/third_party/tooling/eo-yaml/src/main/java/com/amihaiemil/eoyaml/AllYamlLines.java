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
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amihaiemil.eoyaml.exceptions.YamlReadingException;

/**
 * YamlLines default implementation. "All" refers to the fact that
 * we iterate over all of them, irrespective of indentation. There
 * are cases where we need to iterate only over the lines which are
 * at the same indentation level and for that we use the decorator
 * {@link SameIndentationLevel}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 6218fc9f5707a2de23b0c65415b1f149c5e29c04 $
 * @since 1.0.0
 */
final class AllYamlLines implements YamlLines {

    /**
     * There are 3 types of nodes: scalar, sequence and mapping.  This matches
     * either a sequence or map - no match indicates it's a scalar.
     *
     * Does not handle '?' mapping key or flow mapping
     * (https://yaml.org/spec/1.2/spec.html#id2790832).
     *
     * A sequence (group 2) is:
     *  - [ ]* : 0 or more spaces
     *    - [\-](|[ ]+.*) : a dash (-) optionally followed by 1 or more
     *      spaces and any other characters.
     *
     * A map (group 4) is:
     *  - [ ]* : 0 or more spaces followed by:
     *    - a key:
     *      - ('(?:[^'\\]|\\.)*') : a single (') quoted string or
     *      - ("(?:[^"\\]|\\.)*") : double (") quoted string
     *      - ([^"']*) : a non quoted string (characters other than ' or ").
     *        - followed by:
     *          - :(|[ ].*) : a colon (:) optionally followed by a space
     *            and any other characters.
     */
    private static final Pattern SEQUENCE_OR_MAP = Pattern.compile("^("
            + "([\\-](|[ ]+.*))|"
            + "((?:"
                + "('(?:[^'\\\\]|\\\\.)*')|"
                  + "(\"(?:[^\"\\\\]|\\\\.)*\")|"
                  + "([^\"']*)"
                + "):(|[ ].*))"
            + ")$");

    /**
     * Yaml lines.
     */
    private Collection<YamlLine> lines;

    /**
     * Ctor.
     * @param lines Yaml lines collection.
     */
    AllYamlLines(final Collection<YamlLine> lines) {
        this.lines = lines;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (final YamlLine line : this.lines) {
            builder.append(line.toString()).append(CompatibilityWrapper.lineSeparator());
        }
        return builder.toString();
    }

    @Override
    public Collection<YamlLine> original() {
        return this.lines;
    }

    @Override
    public YamlNode toYamlNode(
        final YamlLine prev,
        final boolean guessIndentation
    ) {
        final YamlNode node;
        final String prevLine = prev.trimmed();
        if(prevLine.isEmpty()) {
            node = this.mappingSequenceOrPlainScalar(prev, guessIndentation);
        } else {
            final String lastChar = prevLine.substring(prevLine.length() - 1);
            if (prevLine.matches(Follows.FOLDED_SEQUENCE)) {
                node = new ReadYamlSequence(prev, this, guessIndentation);
            } else if (lastChar.equals(Follows.LITERAL_BLOCK_SCALAR)) {
                node = new ReadLiteralBlockScalar(prev, this);
            } else if (lastChar.equals(Follows.FOLDED_BLOCK_SCALAR)) {
                node = new ReadFoldedBlockScalar(prev, this);
            } else {
                node = this.mappingSequenceOrPlainScalar(
                    prev, guessIndentation
                );
            }
        }
        return node;
    }

    @Override
    public Iterator<YamlLine> iterator() {
        return this.lines.iterator();
    }

    /**
     * Try to figure out what YAML node (mapping, sequence or scalar) is found
     * after the given line.
     * @param prev YamlLine just previous to the node we're trying to find.
     * @param guessIndentation If true, we will guess the correct indentation,
     *  if any YAML line is misplaced.
     * @return Found YamlNode.
     */
    private YamlNode mappingSequenceOrPlainScalar(
        final YamlLine prev,
        final boolean guessIndentation
    ) {
        YamlNode node = null;
        final YamlLine first = new Skip(
            this,
            line -> line.number() <= prev.number(),
            line -> line.trimmed().startsWith("#"),
            line -> line.trimmed().startsWith("---"),
            line -> line.trimmed().startsWith("..."),
            line -> line.trimmed().startsWith("%"),
            line -> line.trimmed().startsWith("!!")
        ).iterator().next();
        Matcher matcher = SEQUENCE_OR_MAP.matcher(first.trimmed());
        if (matcher.matches()) {
            if (matcher.group(2) != null) {
                node = new ReadYamlSequence(prev, this, guessIndentation);
            } else if (matcher.group(4) != null) {
                node = new ReadYamlMapping(prev.number(),
                        prev, this, guessIndentation);
            }
        } else if (this.original().size() == 1) {
            node = new ReadPlainScalar(this, first);
        }
        if (node == null) {
            throw new YamlReadingException(
                "Could not parse YAML starting at line " + (first.number() + 1)
                + " . It should be a sequence (line should start with '-'), "
                + "a mapping (line should contain ':') or it should be a plain "
                + "scalar, but it has " + this.lines.size() + " lines, "
                + "while a plain scalar should be only 1 line!"
            );
        } else {
            return node;
        }
    }
}
