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
 * Decorator class to cache values of trimmed() and indentation() method for
 * a YamlLine.
 * @author Sherif Waly (sherifwaly95@gmail.com)
 * @version $Id: 9579aaa4f8568a643153383cfc86ef4464175491 $
 * @since 1.0.0
 *
 */
final class CachedYamlLine implements YamlLine {

    /**
     * Content line.
     */
    private YamlLine line;

    /**
     * Cached trimmed line.
     */
    private String trimmed;

    /**
     * Cached contents line.
     */
    private String contents;

    /**
     * Cached indentation.
     */
    private int indentation = -1;

    /**
     * Cached value.
     */
    private Boolean hasNestedNode;

    /**
     * Ctor.
     * @param line YamlLine
     */
    CachedYamlLine(final YamlLine line) {
        this.line = line;
    }

    @Override
    public int compareTo(final YamlLine other) {
        return this.line.compareTo(other);
    }

    @Override
    public String trimmed() {
        if(this.trimmed == null) {
            this.trimmed = this.line.trimmed();
        }
        return this.trimmed;
    }

    @Override
    public String contents(final int previousIndent) {
        if(this.contents == null) {
            this.contents = this.line.contents(previousIndent);
        }
        return this.contents;
    }

    @Override
    public String comment() {
        return this.line.comment();
    }

    @Override
    public int number() {
        return this.line.number();
    }

    @Override
    public int indentation() {
        if(this.indentation == -1) {
            this.indentation = this.line.indentation();
        }
        return this.indentation;
    }

    @Override
    public boolean requireNestedIndentation() {
        if (this.hasNestedNode == null) {
            this.hasNestedNode = this.line.requireNestedIndentation();
        }
        return this.hasNestedNode;
    }

    @Override
    public String toString() {
        return this.line.toString();
    }

}
