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
import java.util.LinkedList;
import java.util.List;

/**
 * YAML sequence implementation (rt means runtime).
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: b92ff2168827dc07f133ac0f7304e16b7c096e9f $
 * @since 1.0.0
 * @see http://yaml.org/spec/1.2/spec.html#sequence//
 */
final class RtYamlSequence extends BaseYamlSequence {

    /**
     * Comments referring to this sequence.
     */
    private Comment comment;

    /**
     * Nodes in this sequence.
     */
    private final List<YamlNode> nodes = new LinkedList<>();

    /**
     * Ctor.
     * @param elements Elements of this sequence.
     */
    RtYamlSequence(final Collection<YamlNode> elements) {
        this(elements, "");
    }

    /**
     * Constructor.
     * @param elements Elements of this sequence.
     * @param comment Comment referring to this sequence itself.
     */
    RtYamlSequence(
        final Collection<YamlNode> elements,
        final String comment
    ) {
        this.nodes.addAll(elements);
        this.comment = new BuiltComment(this, comment);
    }

    @Override
    public Collection<YamlNode> values() {
        final List<YamlNode> children = new LinkedList<>();
        children.addAll(this.nodes);
        return children;
    }

    @Override
    public Comment comment() {
        return this.comment;
    }

}
