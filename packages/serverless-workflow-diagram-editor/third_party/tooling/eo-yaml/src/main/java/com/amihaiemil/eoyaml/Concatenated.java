/**
 * Copyright (c) 2016-2021, Mihai Emil Andronache
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
 * A ScalarComment read from somewhere.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 3dcb617ee2e893a21e5ea2072523b3a4196c603d $
 * @since 5.2.1
 */
final class Concatenated implements ScalarComment {

    /**
     * Comment above the scalar.
     */
    private final Comment above;

    /**
     * Inline Comment.
     */
    private final Comment inline;

    /**
     * Ctor.
     * @param above Comment above the scalar.
     * @param inline Comment inline with the scalar.
     */
    Concatenated(
        final Comment above,
        final Comment inline
    ) {
        this.above = above;
        this.inline = inline;
    }

    @Override
    public YamlNode yamlNode() {
        return this.inline.yamlNode();
    }

    @Override
    public String value() {
        final StringBuilder comment = new StringBuilder();
        final String aboveValue = this.above.value();
        final String inlineValue = this.inline.value();

        if(inlineValue.trim().isEmpty()){
            comment.append(aboveValue);
        } else if(aboveValue.trim().isEmpty()){
            comment.append(inlineValue);
        } else {
            comment
                .append(aboveValue)
                .append(CompatibilityWrapper.lineSeparator())
                .append(inlineValue);
        }
        return comment.toString();
    }

    @Override
    public Comment above() {
        return this.above;
    }

    @Override
    public Comment inline() {
        return this.inline;
    }
}
