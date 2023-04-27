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

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;


/**
 * Representation of a {@link JsonObject} as YAML Mapping.
 * @author criske
 * @version $Id: 7cfff4571a86d56402f6eb54e8e132026aaddf4e $
 * @since 5.1.7
 */
final class JsonYamlMapping extends BaseYamlMapping {

    /**
     * Json object being mapped.
     */
    private final JsonObject object;

    /**
     * Ctor.
     * @param object Json object being mapped.
     */
    JsonYamlMapping(final JsonObject object) {
        this.object = object;
    }

    @Override
    public Set<YamlNode> keys() {
        final Set<YamlNode> keys = new LinkedHashSet<>();
        this.object.keySet().forEach(key -> keys
            .add(new PlainStringScalar(key)));
        return keys;
    }

    @Override
    public YamlNode value(final YamlNode key) {
        final JsonValue jsonValue = this.object.get(key.asScalar().value());
        return new JsonYamlDump(jsonValue).dump();
    }

    @Override
    public Comment comment() {
        return new Comment() {
            @Override
            public YamlNode yamlNode() {
                return JsonYamlMapping.this;
            }

            @Override
            public String value() {
                return "";
            }
        };
    }
}
