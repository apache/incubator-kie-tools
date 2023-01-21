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
import java.util.Collections;

/**
 * Representation of an empty YAML Sequence (or "[]").  A decorator around
 * {@link YamlSequence} with no values.
 * @author Andrew Newman
 * @version $Id: 6d7cc7d357394b494cbf3e56cb4cdd0a1513d852 $
 * @since 5.1.17
 */
public class EmptyYamlSequence extends BaseYamlSequence {

    /**
     * Decorated object - used for getting comments.
     */
    private final YamlSequence sequence;

    /**
     * Wrap an existing sequence - expects comments() to be implemented.
     * @param sequence The sequence to wrap.
     */
    public EmptyYamlSequence(final YamlSequence sequence) {
        this.sequence = sequence;
    }

    @Override
    public final Collection<YamlNode> values() {
        return Collections.emptyList();
    }

    @Override
    public final Comment comment() {
        return sequence.comment();
    }
}
