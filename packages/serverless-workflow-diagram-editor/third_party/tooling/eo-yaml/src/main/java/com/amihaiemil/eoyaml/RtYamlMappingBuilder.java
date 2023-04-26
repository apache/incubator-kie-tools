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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * YamlMappingBuilder implementation. "Rt" stands for "Runtime".
 * This class is immutable and thread-safe.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: dafbf15c0177e8e4fda575c3c18c29cfb8bea91a $
 * @since 1.0.0
 *
 */
final class RtYamlMappingBuilder implements YamlMappingBuilder {

    /**
     * Added pairs.
     */
    private final Map<YamlNode, YamlNode> pairs;

    /**
     * Default ctor.
     */
    RtYamlMappingBuilder() {
        this(new LinkedHashMap<>());
    }

    /**
     * Constructor.
     * @param pairs Pairs used in building the YamlMapping.
     */
    RtYamlMappingBuilder(final Map<YamlNode, YamlNode> pairs) {
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
        final Map<YamlNode, YamlNode> withAdded = new LinkedHashMap<>();
        withAdded.putAll(this.pairs);
        withAdded.put(key, value);
        return new RtYamlMappingBuilder(withAdded);
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
