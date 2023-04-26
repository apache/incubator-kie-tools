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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import jakarta.json.GwtIncompatible;

/**
 * YamlSequence reflected from a Collection or an array of Object.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: bf7105fd4a67657a10207dedcd0d6354f62aab9b $
 * @since 4.3.3
 */
@GwtIncompatible
final class ReflectedYamlSequence extends BaseYamlSequence {

    /**
     * Object sequence.
     */
    private final Collection<Object> sequence;

    /**
     * Constructor.
     * @param sequence Collection or array of Object.
     */
    ReflectedYamlSequence(final Object sequence) {
        if(sequence instanceof Collection) {
            this.sequence = (Collection<Object>) sequence;
        } else if(sequence.getClass().isArray()) {
            final Object[] array = (Object[]) sequence;
            this.sequence = Arrays.asList(array);
        } else {
            throw new IllegalArgumentException(
                "YamlSequence can only be reflected "
              + "from a Collection or from an array."
            );
        }
    }

    @Override
    public Collection<YamlNode> values() {
        final List<YamlNode> values = new ArrayList<>();
        for(final Object value : this.sequence) {
            values.add(Yaml.createYamlDump(value).dump());
        }
        return values;
    }

    @Override
    public Comment comment() {
        return new Comment() {
            @Override
            public YamlNode yamlNode() {
                return ReflectedYamlSequence.this;
            }

            @Override
            public String value() {
                return "";
            }
        };
    }

}
