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

import com.amihaiemil.eoyaml.exceptions.YamlReadingException;

/**
 * Default implementation of {@link YamlLine}.
 * "Rt" stands for "Runtime".
 * @checkstyle CyclomaticComplexity (200 lines)
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 8383a0bec90f4ab8fbfadf13126a04f82db50bb7 $
 * @since 1.0.0
 */
final class RtYamlLine implements YamlLine {

    /**
     * Content.
     */
    private String value;

    /**
     * Line nr.
     */
    private int number;

    /**
     * Ctor.
     * @param value Contents of this line.
     * @param number Number of the line.
     */
    RtYamlLine(final String value, final int number) {
        this.value = value;
        this.number = number;
    }

    @Override
    public String trimmed() {
        String trimmed = this.value.trim();
        int i = 0;
        while(i < trimmed.length()) {
            if(i > 0 && trimmed.charAt(i) == '#') {
                trimmed = trimmed.substring(0, i);
                break;
            } else if(trimmed.charAt(i) == '"') {
                i++;
                while(i < trimmed.length() && trimmed.charAt(i) != '"') {
                    i++;
                }
            } else if(trimmed.charAt(i) == '\'') {
                i++;
                while(i < trimmed.length() && trimmed.charAt(i) != '\'') {
                    i++;
                }
            }
            i++;
        }
        return trimmed.trim();
    }

    @Override
    public String contents(final int previousIndent) {
        String contents;
        int indentation = indentation();
        if (indentation == 0 && previousIndent <= 0) {
            contents = this.value;
        } else if (indentation > previousIndent) {
            contents = this.value.substring(previousIndent + 2);
        } else {
            throw new YamlReadingException("Literal must be indented "
                    + "at least 2 spaces from previous element.");
        }
        return contents;
    }

    @Override
    public String comment() {
        String comment = "";
        String trimmed = this.value.trim();
        int i = 0;
        while(i < trimmed.length()) {
            if(trimmed.charAt(i) == '#') {
                comment = trimmed.substring(i + 1);
                break;
            } else if(trimmed.charAt(i) == '"') {
                i++;
                while(i < trimmed.length() && trimmed.charAt(i) != '"') {
                    i++;
                }
            } else if(trimmed.charAt(i) == '\'') {
                i++;
                while(i < trimmed.length() && trimmed.charAt(i) != '\'') {
                    i++;
                }
            }
            i++;
        }
        return comment.trim();
    }

    @Override
    public int number() {
        return this.number;
    }

    @Override
    public int indentation() {
        int index = 0;
        while (index < this.value.length() && this.value.charAt(index) == ' '){
            index++;
        }
        return index;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @Override
    public int compareTo(final YamlLine other) {
        int result = -1;
        if (this == other) {
            result = 0;
        } else if (other == null) {
            result = 1;
        } else {
            result = this.trimmed().compareTo(other.trimmed());
        }
        return result;
    }

    @Override
    public boolean requireNestedIndentation() {
        final boolean result;

        if("---".equals(this.trimmed())) {
            result = false;
        } else {
            final String specialCharacters = ":>|-?";
            final CharSequence prevLineLastChar =
                this.trimmed().substring(this.trimmed().length() - 1);
            result = specialCharacters.contains(prevLineLastChar);
        }
        return result;
    }
}
