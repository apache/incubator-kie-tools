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
 * A line of yaml.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: a528b60bdf080cc626b47a7e919557eb787bc51c $
 * @since 1.0.0
 */
interface YamlLine extends Comparable<YamlLine> {
    /**
     * Indicates we don't know or don't have a line number for the given
     * comment (may not have come from a file).
     */
    int UNKNOWN_LINE_NUMBER = -1;

    /**
     * The line's trimmed contents with comments, aliases etc removed.
     * @return Trimmed string (leading and trailing spaces) contents.
     */
    String trimmed();

    /**
     * The line's contents with spaces, tabs, etc maintained.
     * @param previousIndent How deeply nested is the Yaml line - this is used
     *                       to remove leading spaces.
     * @return String line contents.
     */
    String contents(int previousIndent);

    /**
     * Return the comment, if any, from this line.
     * @return Comment of empty string.
     */
    String comment();

    /**
     * Number of the line (count start from 0).
     * @return Integer.
     */
    int number();

    /**
     * This line's indentation (number of spaces at the beginning of it).>br>
     * Should be a multiple of 2! If not, IllegalStateException is thrown.
     * @return Integer.
     * @throws IllegalStateException if the indentation is not multiple of 2.
     */
    int indentation();

    /**
     * Do the following line(s) require a deeper indentation than this line's?
     * @return True or false
     */
    boolean requireNestedIndentation();

    /**
     * YamlLine null object.
     */
    class NullYamlLine implements YamlLine {

        @Override
        public String trimmed() {
            return "";
        }

        @Override
        public String contents(final int previousIndent) {
            return "";
        }

        @Override
        public String comment() {
            return "";
        }

        @Override
        public int number() {
            return UNKNOWN_LINE_NUMBER;
        }

        @Override
        public int indentation() {
            return -1;
        }

        @Override
        public int compareTo(final YamlLine other) {
            return -1;
        }

        @Override
        public boolean requireNestedIndentation() {
            return false;
        }

    }
}
