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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A plain scalar value read from somewhere.
 * @author Mihai Andronace (amihaiemil@gmail.com)
 * @version $Id: 8aa92f57d80848cca2d99b758752ca8453356def $
 * @since 3.1.3
 */
final class ReadPlainScalar extends BaseScalar {

    /**
     * Pattern to match scalars in mappings or sequences.
     *
     * Ignore zero or more spaces and a hyphen (-) followed by one
     * or more spaces.
     *
     * A quoted scalar (<b>EscapedScalar</b>) literal is inside:
     *  - ('(?:[^'\\]|\\.)*') : a single (') quoted string or
     *  - ("(?:[^"\\]|\\.)*") : double (") quoted string
     *
     * A scalar for a mapping (<b>MappingScalar</b>) are characters after
     * the last unescaped ':':
     *  - (keys_regex):[ ]+(.*) : Any characters before a colon followed by
     *    one or more spaces.
     *
     * The sequence scalar (<b>UnescapedSequenceScalar</b>) is:
     *   - -[ ]+(.*) : Any characters after a hyphen (-) and one more spaces.
     */
    private static final Pattern QUOTED_LITERAL_MAP_SEQ = Pattern.compile("^("
        + "[ ]*(-[ ]+)"
        + "(?<EscapedScalar>"
            + "('(?:[^'\\\\]|\\\\.)*')|"
            + "(\"(?:[^\"\\\\]|\\\\.)*\")"
        + ")|"
        + "(((?<key>[^:'\"]+)|(?<keyQ>\".+\")|(?<keySQ>'.+'))*:[ ]+"
        + "(?<MappingScalar>.*))|"
        + "(-[ ]+(?<UnescapedSequenceScalar>.*))"
        + ")$"
    );

    /**
     * Name of the regex group for escaped scalars (between "" or '').
     */
    private static final String ESCAPED_SCALAR = "EscapedScalar";

    /**
     * Name of the regex group for scalars in a mapping.
     */
    private static final String MAPPING_SCALAR = "MappingScalar";

    /**
     * Name of the regex group for unescaped scalars in a sequence.
     */
    private static final String UNESCAPED_SEQUENCE_SCALAR =
        "UnescapedSequenceScalar";

    /**
     * All YAML Lines of the document.
     */
    private final AllYamlLines all;

    /**
     * Line where the plain scalar value is supposed to be found.
     * The Scalar can be either after the ":" character, if this
     * line is from a mapping, or after the "-" character, if
     * this line is from a sequence, or it represents the whole line,
     * if no "-" or ":" are found.
     */
    private final YamlLine scalar;

    /**
     * Constructor.
     * @param all All lines of the document.
     * @param scalar YamlLine containing the scalar.
     */
    ReadPlainScalar(final AllYamlLines all, final YamlLine scalar) {
        this.all = all;
        this.scalar = scalar;
    }

    /**
     * Unescaped String value of this scalar. Pay attention, if the
     * scalar's value is the "null" String, then we return null, because
     * "null" is a reserved keyword in YAML, indicating a null Scalar.
     * @checkstyle ReturnCount (50 lines)
     * @return String or null if the Strings value is "null".
     */
    @Override
    public String value() {
        String value = this.scalar.trimmed();
        Matcher matcher = this.escapedSequenceScalar(this.scalar);
        if(matcher.matches()) {
            if (matcher.group(ESCAPED_SCALAR) != null) {
                value = matcher.group(ESCAPED_SCALAR);
            } else if (matcher.group(MAPPING_SCALAR) != null) {
                value = matcher.group(MAPPING_SCALAR).trim();
            } else if (matcher.group(UNESCAPED_SEQUENCE_SCALAR) != null) {
                value = matcher.group(UNESCAPED_SEQUENCE_SCALAR).trim();
            }
        }
        if("null".equals(value)) {
            return null;
        } else {
            return this.unescape(value);
        }
    }

    @Override
    public Comment comment() {
        final Comment comment;
        if(this.scalar instanceof YamlLine.NullYamlLine) {
            comment = new Concatenated(
                new BuiltComment(this, ""),
                new BuiltComment(this, "")
            );
        } else {
            final int lineNumber = this.scalar.number();
            comment = new Concatenated(
                new ReadComment(
                    new Backwards(
                        new FirstCommentFound(
                            new Backwards(
                                new Skip(
                                    this.all,
                                    line -> line.number() >= lineNumber,
                                    line -> line.trimmed().startsWith("..."),
                                    line -> line.trimmed().startsWith("%"),
                                    line -> line.trimmed().startsWith("!!")
                                )
                            ),
                            false
                        )
                    ),
                    this
                ),
                new ReadComment(
                    new Skip(
                        this.all,
                        line -> line.number() != lineNumber
                    ),
                    this
                )
            );
        }
        return comment;
    }

    /**
     * Remove the possible escaping quotes or apostrophes surrounding the
     * given value.
     * @param value The value to unescape.
     * @return The value without quotes or apostrophes.
     */
    private String unescape(final String value) {
        final String unescaped;
        if(value == null) {
            unescaped = value;
        } else {
            if (value.startsWith("\"") && value.endsWith("\"")) {
                unescaped = value.substring(1, value.length() - 1);
            } else if (value.startsWith("'") && value.endsWith("'")) {
                unescaped = value.substring(1, value.length() - 1);
            } else {
                unescaped = value;
            }
        }
        return unescaped;
    }

    /**
     * Returns true if there's a YamlMapping starting right after the
     * dash, on the same line.
     * @param dashLine Line.
     * @return True of false.
     */
    private Matcher escapedSequenceScalar(final YamlLine dashLine) {
        final String trimmed = dashLine.trimmed();
        return QUOTED_LITERAL_MAP_SEQ.matcher(trimmed);
    }
}
