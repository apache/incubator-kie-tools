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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import jakarta.json.GwtIncompatible;
/**
 * A Yaml mapping.
 * @checkstyle ExecutableStatementCount (300 lines)
 * @checkstyle ReturnCount (1000 lines)
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 25e40a757c35944bde3edd33ad5b4bf759362c9b $
 * @since 1.0.0
 */
public interface YamlMapping extends YamlNode {

    /**
     * Return the keys' set of this mapping.<br><br>
     * @return Set of YamlNode keys.
     */
    Set<YamlNode> keys();

    /**
     * Get the YamlNode mapped to the specified key.
     * @param key YamlNode key. Could be a simple scalar,
     *  a YamlMapping or a YamlSequence.
     * @return The found YamlNode or null if nothing is found.
     */
    YamlNode value(final YamlNode key);

    /**
     * Fetch the values of this mapping.
     * @return Collection of {@link YamlNode}
     */
    default Collection<YamlNode> values() {
        final List<YamlNode> values = new LinkedList<>();
        for(final YamlNode key : this.keys()) {
            values.add(this.value(key));
        }
        return values;
    }
    /**
     * Get the Yaml mapping associated with the given key.
     * @param key String key
     * @return Yaml mapping or null if the key is missing, or not pointing
     *  to a mapping.
     */
    default YamlMapping yamlMapping(final String key) {
        return this.yamlMapping(
            Yaml.createYamlScalarBuilder().addLine(key).buildPlainScalar()
        );
    }

    /**
     * Get the Yaml mapping associated with the given key.
     * @param key Yaml node (mapping or sequence) key
     * @return Yaml mapping or null if the key is missing, or not pointing
     *  to a mapping.
     */
    default YamlMapping yamlMapping(final YamlNode key) {
        final YamlNode value = this.value(key);
        final YamlMapping found;
        if (value instanceof YamlMapping) {
            found = (YamlMapping) value;
        } else {
            found = null;
        }
        return found;
    }

    /**
     * Get the Yaml sequence associated with the given key.
     * @param key String key
     * @return Yaml sequence or null if the key is missing, or not pointing
     *  to a sequence.
     */
    default YamlSequence yamlSequence(final String key) {
        return this.yamlSequence(
            Yaml.createYamlScalarBuilder().addLine(key).buildPlainScalar()
        );
    }

    /**
     * Get the Yaml sequence associated with the given key.
     * @param key Yaml node (mapping or sequence) key
     * @return Yaml sequence or null if the key is missing, or not pointing
     *  to a sequence
     */
    default YamlSequence yamlSequence(final YamlNode key) {
        final YamlNode value = this.value(key);
        final YamlSequence found;
        if (value instanceof YamlSequence) {
            found =  (YamlSequence) value;
        } else {
            found = null;
        }
        return found;
    }

    /**
     * Get the String associated with the given key.
     * @param key String key
     * @return String or null if the key is missing, or not pointing
     *  to a scalar.
     */
    default String string(final String key) {
        return this.string(
            Yaml.createYamlScalarBuilder().addLine(key).buildPlainScalar()
        );
    }

    /**
     * Get the String associated with the given key.
     * @param key Yaml node key
     * @return String or null if the key is missing, or not pointing
     *  to a scalar.
     */
    default String string(final YamlNode key) {
        final YamlNode value = this.value(key);
        final String found;
        if (value instanceof Scalar) {
            found = ((Scalar) value).value();
        } else {
            found = null;
        }
        return found;
    }
    /**
     * Get the String folded block scalar associated with the given key.
     * @param key String key
     * @return String or null if the key is missing, or not pointing
     *  to a folded block scalar.
     */
    default String foldedBlockScalar(final String key) {
        return this.foldedBlockScalar(
            Yaml.createYamlScalarBuilder().addLine(key).buildPlainScalar()
        );
    }

    /**
     * Get the String folded block scalar associated with the given key.
     * @param key Yaml node key.
     * @return String or null if the key is missing, or not pointing
     *  to a folded block scalar.
     */
    default String foldedBlockScalar(final YamlNode key) {
        final YamlNode value = this.value(key);
        final String found;
        if (value instanceof Scalar) {
            found = ((Scalar) value).value();
        } else {
            found = null;
        }
        return found;
    }
    /**
     * Get the String lines of the literal block scalar associated
     * with the given key.
     * @param key String key
     * @return Collection of String or null if the key is missing,
     *  or not pointing to a literal block scalar.
     */
    default Collection<String> literalBlockScalar(final String key) {
        return this.literalBlockScalar(
            Yaml.createYamlScalarBuilder().addLine(key).buildPlainScalar()
        );
    }

    /**
     * Get the String lines of the literal block scalar associated
     * with the given key.
     * @param key String key
     * @return Collection of String or null if the key is missing,
     *  or not pointing to a literal block scalar.
     */
    default Collection<String> literalBlockScalar(final YamlNode key) {
        final Collection<String> found;
        final YamlNode value = this.value(key);
        if(value instanceof Scalar) {
            found = Arrays.asList(
                ((Scalar) value)
                    .value()
                    .split(CompatibilityWrapper.lineSeparator())
            );
        } else {
            found = null;
        }
        return found;
    }
    /**
     * Get the YamlNode mapped to the specified key.
     * @param key String key.
     * @return The found YamlNode or null if nothing is found.
     */
    default YamlNode value(final String key) {
        return this.value(
            Yaml.createYamlScalarBuilder().addLine(key).buildPlainScalar()
        );
    }

    /**
     * Convenience method to directly read an integer value
     * from this map. It is equivalent to:
     * <pre>
     *     YamlMapping map = ...;
     *     int value = Integer.parseInt(map.string(...));
     * </pre>
     * @param key The key of the value.
     * @return Found integer or -1 if there is no value for the key,
     *  or the value is not a Scalar.
     * @throws NumberFormatException - if the Scalar value
     *  is not a parsable integer.
     */
    default int integer(final String key) {
        return this.integer(
            Yaml.createYamlScalarBuilder().addLine(key).buildPlainScalar()
        );
    }

    /**
     * Convenience method to directly read an integer value
     * from this map. It is equivalent to:
     * <pre>
     *     YamlMapping map = ...;
     *     int value = Integer.parseInt(map.string(...));
     * </pre>
     * @param key The key of the value.
     * @return Found integer or -1 if there is no value for the key,
     *  or the value is not a Scalar.
     * @throws NumberFormatException - if the Scalar value
     *  is not a parsable integer.
     */
    default int integer(final YamlNode key) {
        final YamlNode value = this.value(key);
        if(value instanceof Scalar) {
            return Integer.parseInt(((Scalar) value).value());
        }
        return -1;
    }

    /**
     * Convenience method to directly read a float value
     * from this map. It is equivalent to:
     * <pre>
     *     YamlMapping map = ...;
     *     float value = Float.parseFloat(map.string(...));
     * </pre>
     * @param key The key of the value.
     * @return Found float or -1 if there is no value for the key,
     *  or the value is not a Scalar.
     * @throws NumberFormatException - if the Scalar value
     *  is not a parsable float.
     */
    default float floatNumber(final String key) {
        return this.floatNumber(
            Yaml.createYamlScalarBuilder().addLine(key).buildPlainScalar()
        );
    }

    /**
     * Convenience method to directly read a float value
     * from this map. It is equivalent to:
     * <pre>
     *     YamlMapping map = ...;
     *     float value = Float.parseFloat(map.string(...));
     * </pre>
     * @param key The key of the value.
     * @return Found float or -1 if there is no value for the key,
     *  or the value is not a Scalar.
     * @throws NumberFormatException - if the Scalar value
     *  is not a parsable float.
     */
    default float floatNumber(final YamlNode key) {
        final YamlNode value = this.value(key);
        if(value instanceof Scalar) {
            return Float.parseFloat(((Scalar) value).value());
        }
        return -1;
    }

    /**
     * Convenience method to directly read a double value
     * from this map. It is equivalent to:
     * <pre>
     *     YamlMapping map = ...;
     *     double value = Double.parseDouble(map.string(...));
     * </pre>
     * @param key The key of the value.
     * @return Found double or -1.0 if there is no value for the key,
     *  or the value is not a Scalar.
     * @throws NumberFormatException - if the Scalar value
     *  is not a parsable double.
     */
    default double doubleNumber(final String key) {
        return this.doubleNumber(
            Yaml.createYamlScalarBuilder().addLine(key).buildPlainScalar()
        );
    }

    /**
     * Convenience method to directly read a double value
     * from this map. It is equivalent to:
     * <pre>
     *     YamlMapping map = ...;
     *     double value = Double.parseDouble(map.string(...));
     * </pre>
     * @param key The key of the value.
     * @return Found double or -1.0 if there is no value for the key,
     *  or the value is not a Scalar.
     * @throws NumberFormatException - if the Scalar value
     *  is not a parsable double.
     */
    default double doubleNumber(final YamlNode key) {
        final YamlNode value = this.value(key);
        if(value instanceof Scalar) {
            return Double.parseDouble(((Scalar) value).value());
        }
        return -1.0;
    }

    /**
     * Convenience method to directly read a long value
     * from this map. It is equivalent to:
     * <pre>
     *     YamlMapping map = ...;
     *     long value = Long.parseLong(map.string(...));
     * </pre>
     * @param key The key of the value.
     * @return Found long or -1L if there is no value for the key,
     *  or the value is not a Scalar.
     * @throws NumberFormatException - if the Scalar value
     *  is not a parsable long.
     */
    default long longNumber(final String key) {
        return this.longNumber(
            Yaml.createYamlScalarBuilder().addLine(key).buildPlainScalar()
        );
    }

    /**
     * Convenience method to directly read a long value
     * from this map. It is equivalent to:
     * <pre>
     *     YamlMapping map = ...;
     *     long value = Long.parseLong(map.string(...));
     * </pre>
     * @param key The key of the value.
     * @return Found long or -1L if there is no value for the key,
     *  or the value is not a Scalar.
     * @throws NumberFormatException - if the Scalar value
     *  is not a parsable long.
     */
    default long longNumber(final YamlNode key) {
        final YamlNode value = this.value(key);
        if(value instanceof Scalar) {
            return Long.parseLong(((Scalar) value).value());
        }
        return -1L;
    }

    /**
     * Convenience method to directly read a LocalDate value
     * from this map. It is equivalent to:
     * <pre>
     *     YamlMapping map = ...;
     *     LocalDate date = LocalDate.parse(map.string(...));
     * </pre>
     * @param key The key of the value.
     * @return Found LocalDate or null if there is no value for the key,
     *  or the value is not a Scalar.
     * @throws DateTimeParseException - if the Scalar value cannot be parsed.
     */
    @GwtIncompatible
    default java.time.LocalDate date(final String key) {
        return this.date(
            Yaml.createYamlScalarBuilder().addLine(key).buildPlainScalar()
        );
    }

    /**
     * Convenience method to directly read a LocalDate value
     * from this map. It is equivalent to:
     * <pre>
     *     YamlMapping map = ...;
     *     LocalDate date = LocalDate.parse(map.string(...));
     * </pre>
     * @param key The key of the value.
     * @return Found LocalDate or null if there is no value for the key,
     *  or the value is not a Scalar.
     * @throws DateTimeParseException - if the Scalar value cannot be parsed.
     */
    @GwtIncompatible
    default java.time.LocalDate date(final YamlNode key) {
        final YamlNode value = this.value(key);
        if(value instanceof Scalar) {
            return java.time.LocalDate.parse(((Scalar) value).value());
        }
        return null;
    }

    /**
     * Convenience method to directly read a LocalDateTime value
     * from this map. It is equivalent to:
     * <pre>
     *     YamlMapping map = ...;
     *     LocalDateTime dateTime = LocalDateTime.parse(map.string(...));
     * </pre>
     * @param key The key of the value.
     * @return Found LocalDateTime or null if there is no value for the key,
     *  or the value is not a Scalar.
     * @throws DateTimeParseException - if the Scalar value cannot be parsed.
     */
    @GwtIncompatible
    default java.time.LocalDateTime dateTime(final String key) {
        return this.dateTime(
            Yaml.createYamlScalarBuilder().addLine(key).buildPlainScalar()
        );
    }

    /**
     * Convenience method to directly read a LocalDateTime value
     * from this map. It is equivalent to:
     * <pre>
     *     YamlMapping map = ...;
     *     LocalDateTime dateTime = LocalDateTime.parse(map.string(...));
     * </pre>
     * @param key The key of the value.
     * @return Found LocalDateTime or null if there is no value for the key,
     *  or the value is not a Scalar.
     * @throws DateTimeParseException - if the Scalar value cannot be parsed.
     */
    @GwtIncompatible
    default java.time.LocalDateTime dateTime(final YamlNode key) {
        final YamlNode value = this.value(key);
        if(value instanceof Scalar) {
            return java.time.LocalDateTime.parse(((Scalar) value).value());
        }
        return null;
    }
}
