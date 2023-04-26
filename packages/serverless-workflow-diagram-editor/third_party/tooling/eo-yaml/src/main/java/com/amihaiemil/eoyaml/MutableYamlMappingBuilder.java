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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * YamlMappingBuilder mutable implementation, for better memory cosumption.
 * This class is <b>mutable and NOT thread-safe</b>.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 5cd58ad0679f1fff9b8a05be7ce21ccda16c9cba $
 * @since 6.1.0
 */
final class MutableYamlMappingBuilder implements YamlMappingBuilder {
    /**
     * Added pairs.
     */
    private final Map<YamlNode, YamlNode> pairs;

    /**
     * Default ctor.
     */
    MutableYamlMappingBuilder() {
        this(new LinkedHashMap<>());
    }

    /**
     * Constructor.
     * @param pairs Pairs used in building the YamlMapping.
     */
    MutableYamlMappingBuilder(final Map<YamlNode, YamlNode> pairs) {
        this.pairs = pairs;
    }

    @Override
    public YamlMappingBuilder add(final String key, final String value) {
        return this.add(
            new PlainStringScalar(key),
            new PlainStringScalar(value)
        );
    }

    @Override
    public YamlMappingBuilder add(final YamlNode key, final String value) {
        return this.add(key, new PlainStringScalar(value));
    }

    @Override
    public YamlMappingBuilder add(final String key, final YamlNode value) {
        return this.add(new PlainStringScalar(key), value);
    }

    @Override
    public YamlMappingBuilder add(final YamlNode key, final YamlNode value) {
        if(key == null || key.isEmpty()) {
            throw new IllegalArgumentException(
                "The key in YamlMapping cannot be null or empty!"
            );
        }
        this.pairs.put(key, value);
        return this;
    }

    @Override
    public YamlMapping build(final String comment) {
        YamlMapping mapping = new RtYamlMapping(this.pairs, comment);
        if (pairs.isEmpty()) {
            mapping = new EmptyYamlMapping(mapping);
        }
        return mapping;
    }
}
