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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jakarta.json.GwtIncompatible;

/**
 * YamlMapping reflected from a Java Bean.
 * @checkstyle BooleanExpressionComplexity (300 lines)
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: b09d17a137ca82e387dfec05a86595db90e3e39f $
 * @since 4.3.3
 */
@GwtIncompatible
final class ReflectedYamlMapping extends BaseYamlMapping {

    /**
     * Java Bean being reflected.
     */
    private final Object bean;

    /**
     * Constructor.
     * @param bean Serializable get/set Java Bean.
     */
    ReflectedYamlMapping(final Object bean) {
        if(bean instanceof Collection || bean.getClass().isArray()) {
            throw new IllegalArgumentException(
                "YamlMapping can only be reflected "
              + "from an Object or from a Map."
            );
        }
        this.bean = bean;
    }

    @Override
    public Set<YamlNode> keys() {
        final Set<YamlNode> keys = new LinkedHashSet<>();
        if(this.bean instanceof Map) {
            for(final Object key : ((Map) this.bean).keySet()) {
                keys.add(this.objectToYamlNode(key));
            }
        } else {
            final Method[] methods = this.bean.getClass().getDeclaredMethods();
            for (final Method method : methods) {
                if (Modifier.isPublic(method.getModifiers())
                    && method.getParameterCount() == 0
                    && !method.getReturnType().equals(Void.TYPE)
                ) {
                    keys.add(new MethodKey(method));
                }
            }
        }
        return keys;
    }

    @Override
    public YamlNode value(final YamlNode key) {
        YamlNode node = null;
        if(this.bean instanceof Map) {
            for(final Object mapKey : ((Map) bean).keySet()) {
                if(key.equals(this.objectToYamlNode(mapKey))) {
                    node = this.objectToYamlNode(((Map) bean).get(mapKey));
                    break;
                }
            }
        } else {
            if (key instanceof Scalar) {
                node = this.objectToYamlNode(
                    this.invokeMethod(((Scalar) key).value())
                );
            } else {
                throw new IllegalArgumentException(
                    "Reflected YamlMapping can only have string keys "
                  + "representing the method names of the reflected Java Bean!"
                );
            }
        }
        return node;
    }

    @Override
    public Comment comment() {
        return new Comment() {
            @Override
            public YamlNode yamlNode() {
                return ReflectedYamlMapping.this;
            }

            @Override
            public String value() {
                return "";
            }
        };
    }

    /**
     * Invoke the method represented by given YamlNode key, on the
     * encapsulated Java Bean.
     * @param keyName String key in the YamlMapping, representing a method.
     * @return Object, the result of the method's invocation.
     */
    private Object invokeMethod(final String keyName) {
        Object value = null;
        final Method[] methods = this.bean.getClass().getDeclaredMethods();
        for(final Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())
                && method.getParameterCount() == 0
                && !method.getReturnType().equals(Void.TYPE)
                && (method.getName().equalsIgnoreCase(keyName)
                || method.getName().equalsIgnoreCase("get" + keyName))
            ) {
                try {
                    value = method.invoke(this.bean);
                } catch (final IllegalAccessException iae) {
                    throw new IllegalStateException(iae);
                } catch (final InvocationTargetException ite) {
                    throw new IllegalStateException(ite);
                }
            }
        }
        return value;
    }

    /**
     * Turn a Java Object to an appropriate YAML Node.
     * @param value Object value.
     * @return YamlNode.
     */
    private YamlNode objectToYamlNode(final Object value) {
        return Yaml.createYamlDump(value).dump();
    }

    /**
     * A YAML Scalar which will be the key in this reflected
     * YamlMapping.
     */
    static class MethodKey extends BaseScalar {

        /**
         * The object's method which is the key in the
         * reflected YamlMapping.
         */
        private final Method method;

        /**
         * Constructor.
         * @param method Method key.
         */
        MethodKey(final Method method) {
            this.method = method;
        }

        @Override
        public String value() {
            String keyName;
            final String methodName = this.method.getName();
            if(methodName.startsWith("get") && methodName.length() > 3) {
                final String first = String.valueOf(
                        this.method.getName().substring(3).charAt(0)
                );
                keyName = first.toLowerCase();
                if(methodName.substring(3).length() > 1) {
                    keyName = keyName + methodName.substring(4);
                }

            } else {
                keyName = this.method.getName();
            }
            return keyName;
        }

        @Override
        public Comment comment() {
            return new Comment() {
                @Override
                public YamlNode yamlNode() {
                    return MethodKey.this;
                }

                @Override
                public String value() {
                    return "";
                }
            };
        }
    }
}
