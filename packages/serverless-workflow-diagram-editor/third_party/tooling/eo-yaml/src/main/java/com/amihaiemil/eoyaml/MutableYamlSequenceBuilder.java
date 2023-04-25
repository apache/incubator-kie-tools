/**
 * Copyright (c) 2016-2022, Mihai Emil Andronache
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

import java.util.LinkedList;
import java.util.List;

/**
 * YamlSequenceBuilder mutable implementation, for better memory cosumption.
 * This class is <b>mutable and NOT thread-safe</b>.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 370a284310675841e89e04becb5cb4cbcb867ae2 $
 * @since 6.1.0
 */
final class MutableYamlSequenceBuilder implements YamlSequenceBuilder {
    /**
     * Added nodes.
     */
    private final List<YamlNode> nodes;

    /**
     * Default ctor.
     */
    MutableYamlSequenceBuilder() {
        this(new LinkedList<>());
    }

    /**
     * Constructor.
     * @param nodes Nodes used in building the YamlSequence
     */
    MutableYamlSequenceBuilder(final List<YamlNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    public YamlSequenceBuilder add(final String value) {
        return this.add(new PlainStringScalar(value));
    }

    @Override
    public YamlSequenceBuilder add(final YamlNode node) {
        this.nodes.add(node);
        return this;
    }

    @Override
    public YamlSequence build(final String comment) {
        YamlSequence sequence = new RtYamlSequence(this.nodes, comment);
        if (this.nodes.isEmpty()) {
            sequence = new EmptyYamlSequence(sequence);
        }
        return sequence;
    }
}
