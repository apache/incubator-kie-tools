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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import jakarta.json.GwtIncompatible;
/**
 * A YamlDump that works with the Reflection API.
 * @checkstyle LineLength (100 lines)
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 3c4c63e0c1ae23f1095433ae80598939447b00da $
 * @since 4.3.3
 */
@GwtIncompatible
final class ReflectedYamlDump implements YamlDump {

    /**
     * If the value is any of these types, it is a Scalar.
     */
    private static final List<Class> SCALAR_TYPES = Arrays.asList(
        Integer.class, Long.class, Float.class, Double.class, Short.class,
        String.class, Boolean.class, Character.class, Byte.class
    );

    /**
     * Object to dump.
     */
    private final Object object;

    /**
     * Constructor.
     * @param object Object to dump.
     */
    ReflectedYamlDump(final Object object){
        this.object = object;
    }

    @Override
    public YamlNode dump() {
        final YamlNode node;
        if(this.object == null || SCALAR_TYPES.contains(this.object.getClass())) {
            node = new ReflectedYamlScalar(this.object);
        } else if(this.object instanceof Collection || this.object.getClass().isArray()){
            node = new ReflectedYamlSequence(this.object);
        } else {
            node = new ReflectedYamlMapping(this.object);
        }
        return node;
    }
}
