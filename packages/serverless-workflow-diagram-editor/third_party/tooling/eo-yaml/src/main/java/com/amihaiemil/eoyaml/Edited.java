/**
 * Copyright (c) 2016-2023, Mihai Emil Andronache
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
 * A YamlLine with contents edited by us.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 1b93c3f8df1769be052cc5d00fae945ae29d77df $
 * @since 7.0.0
 */
final class Edited implements YamlLine {
    /**
     * New content of the YamlLine.
     */
    private final String newContent;

    /**
     * Original YamlLine to preserve other suff besides the new content.
     */
    private final YamlLine original;

    /**
     * Ctor.
     * @param newContent New content of the line.
     * @param original Original line to preserveother stuff such as indentation
     *  and line number.
     */
    Edited(final String newContent, final YamlLine original) {
        this.newContent = newContent;
        this.original = original;
    }

    @Override
    public String value() {
        return this.newContent;
    }

    @Override
    public int number() {
        return this.original.number();
    }

    @Override
    public int indentation() {
        return this.original.indentation();
    }
}
