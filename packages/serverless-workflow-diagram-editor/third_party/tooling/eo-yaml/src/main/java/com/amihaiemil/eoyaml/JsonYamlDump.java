/**
 * Copyright (c) 2016-2020, Mihai Emil Andronache
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
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

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

/**
 * A YamlDump that works with javax-json-api.
 * @author criske
 * @version $Id: a28c92c2cb59566ca1883195e14a26a8718ab379 $
 * @since 5.1.7
 */
final class JsonYamlDump implements YamlDump {

    /**
     * JsonValue to dump.
     */
    private final JsonValue value;

    /**
     * Constructor.
     *
     * @param value JsonValue to dump.
     */
    JsonYamlDump(final JsonValue value) {
        this.value = value;
    }

    @Override
    public YamlNode dump() {
        final YamlNode node;
        final JsonValue safeValue;
        if (this.value == null) {
            safeValue = JsonValue.NULL;
        } else {
            safeValue = this.value;
        }
        if (safeValue instanceof JsonObject) {
            node = new JsonYamlMapping((JsonObject) safeValue);
        } else if (safeValue instanceof JsonArray) {
            node = new JsonYamlSequence((JsonArray) safeValue);
        } else {
            node = new PlainStringScalar(safeValue
                .toString()
                .replace("\"", "")
            );
        }
        return node;
    }
}
