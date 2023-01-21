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
 * YamlStreamBuilder implementation. "Rt" stands for "Runtime".
 * This class is immutable and thread-safe.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 559858292b99713877a7f877a21cc5f0db8c51ba $
 * @since 3.1.1
 */
final class RtYamlStreamBuilder implements YamlStreamBuilder {

    /**
     * Added nodes.
     */
    private final List<YamlNode> documents;

    /**
     * Default ctor.
     */
    RtYamlStreamBuilder() {
        this(new LinkedList<YamlNode>());
    }

    /**
     * Constructor.
     * @param documents YAML documents used in building the YamlStream.
     */
    RtYamlStreamBuilder(final List<YamlNode> documents) {
        this.documents = documents;
    }

    @Override
    public YamlStreamBuilder add(final YamlNode document) {
        final List<YamlNode> list = new LinkedList<>();
        list.addAll(this.documents);
        list.add(document);
        return new RtYamlStreamBuilder(list);
    }

    @Override
    public YamlStream build() {
        return new BuiltYamlStream(this.documents);
    }

    /**
     * Built YamlStream.
     * @author Mihai Andronache (amihaiemil@gmail.com)
     * @version $Id: 559858292b99713877a7f877a21cc5f0db8c51ba $
     * @since 3.1.1
     */
    static class BuiltYamlStream extends BaseYamlStream {

        /**
         * Documents as a List.
         */
        private Collection<YamlNode> documents;

        /**
         * Constructor.
         * @param documents Added YamlNodes.
         */
        BuiltYamlStream(final Collection<YamlNode> documents) {
            this.documents = documents;
        }

        @Override
        public Collection<YamlNode> values() {
            return this.documents;
        }
    }

}
