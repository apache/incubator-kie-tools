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
import java.util.stream.Collectors;

import jakarta.json.JsonArray;
/**
 * Representation of a {@link JsonArray} as YAML Sequence.
 * @author criske
 * @version $Id: 79245e3928650eee9ae9bb2316e3077d1ad664bd $
 * @since 5.1.7
 */
final class JsonYamlSequence extends BaseYamlSequence {

    /**
     * Json array being mapped.
     */
    private final JsonArray array;

    /**
     * Ctor.
     * @param array Json array being mapped.
     */
    JsonYamlSequence(final JsonArray array) {
        this.array = array;
    }

    @Override
    public Collection<YamlNode> values() {
        return this.array.stream()
            .map(value -> new JsonYamlDump(value).dump())
            .collect(Collectors.toList());
    }

    @Override
    public Comment comment() {
        return new Comment() {
            @Override
            public YamlNode yamlNode() {
                return JsonYamlSequence.this;
            }

            @Override
            public String value() {
                return "";
            }
        };
    }
}
