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
import java.util.Iterator;

import jakarta.json.GwtIncompatible;

/**
 * A Yaml sequence.
 * @checkstyle ReturnCount (400 lines)
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 68d9eef07b5e32f27e1c498d4a749161dd84534c $
 * @since 1.0.0
 */
public interface YamlSequence extends YamlNode, Iterable<YamlNode> {

    /**
     * Fetch the values of this sequence.
     * @return Collection of {@link YamlNode}
     */
    Collection<YamlNode> values();

    /**
     * Returns this YamlSequence's children Iterator.<br><br>
     * It is equivalent to YamlSequence.values().iterator().
     * @return Iterator of YamlNode.
     */
    default Iterator<YamlNode> iterator() {
        return this.values().iterator();
    }

    /**
     * The number of Yaml elements (scalars, mappings and sequences) found in
     * this sequence.
     * @return Integer.
     */
    default int size() {
        return this.values().size();
    }

    /**
     * Get the Yaml mapping  from the given index.
     * @param index Integer index.
     * @return Yaml mapping.
     */

    default YamlMapping yamlMapping(final int index) {
        YamlMapping mapping = null;
        int count = 0;
        for (final YamlNode node : this.values()) {
            if (count == index && node instanceof YamlMapping) {
                mapping = (YamlMapping) node;
            }
            count = count + 1;
        }
        return mapping;
    }
    /**
     * Get the Yaml sequence from the given index.
     * @param index Integer index.
     * @return Yaml sequence.
     */
    default YamlSequence yamlSequence(final int index) {
        YamlSequence sequence = null;
        int count = 0;
        for (final YamlNode node : this.values()) {
            if (count == index && node instanceof YamlSequence) {
                sequence = (YamlSequence) node;
            }
            count = count + 1;
        }
        return sequence;
    }


    /**
     * Get the String from the given index.
     * @param index Integer index.
     * @return String.
     */
    default String string(final int index) {
        String value = null;
        int count = 0;
        for (final YamlNode node : this.values()) {
            if(count == index && (node instanceof Scalar)) {
                value = ((Scalar) node).value();
                break;
            }
            count++;
        }
        return value;
    }

    /**
     * Get the folded block scalar from the given index.
     * @param index Integer index.
     * @return The folded block scalar as String.
     */
    default String foldedBlockScalar(final int index) {
        String value = null;
        int count = 0;
        for (final YamlNode node : this.values()) {
            if(count == index && (node instanceof Scalar)) {
                value = ((Scalar) node).value();
                break;
            }
            count++;
        }
        return value;
    }

    /**
     * Get the literal block scalar from the given index.
     * @param index Integer index.
     * @return The folded block scalar as String.
     */
    default Collection<String> literalBlockScalar(final int index) {
        Collection<String> value = null;
        int count = 0;
        for (final YamlNode node : this.values()) {
            if(count == index && (node instanceof Scalar)) {
                value = Arrays.asList(
                    ((Scalar) node)
                        .value().split(CompatibilityWrapper.lineSeparator())
                );
                break;
            }
            count++;
        }
        return value;
    }

    /**
     * Convenience method to directly read an integer value
     * from this sequence. It is equivalent to:
     * <pre>
     *     YamlSequence sequence = ...;
     *     int value = Integer.parseInt(sequence.string(...));
     * </pre>
     * @param index The index of the value.
     * @return Found integer.
     * @throws NumberFormatException - if the Scalar value
     *  is not a parsable integer.
     */
    default int integer(final int index) {
        final String value = this.string(index);
        if(value != null && !value.isEmpty()) {
            return Integer.parseInt(value);
        }
        return -1;
    }

    /**
     * Convenience method to directly read a float value
     * from this sequence. It is equivalent to:
     * <pre>
     *     YamlSequence sequence = ...;
     *     float value = Float.parseFloat(sequence.string(...));
     * </pre>
     * @param index The index of the value.
     * @return Found float.
     * @throws NumberFormatException - if the Scalar value
     *  is not a parsable float.
     */
    default float floatNumber(final int index) {
        final String value = this.string(index);
        if(value != null && !value.isEmpty()) {
            return Float.parseFloat(value);
        }
        return -1;
    }

    /**
     * Convenience method to directly read a double value
     * from this sequence. It is equivalent to:
     * <pre>
     *     YamlSequence sequence = ...;
     *     double value = Double.parseDouble(sequence.string(...));
     * </pre>
     * @param index The index of the value.
     * @return Found double.
     * @throws NumberFormatException - if the Scalar value
     *  is not a parsable double.
     */
    default double doubleNumber(final int index) {
        final String value = this.string(index);
        if(value != null && !value.isEmpty()) {
            return Double.parseDouble(value);
        }
        return -1.0;
    }

    /**
     * Convenience method to directly read a long value
     * from this sequence. It is equivalent to:
     * <pre>
     *     YamlSequence sequence = ...;
     *     long value = Long.parseLong(sequence.string(...));
     * </pre>
     * @param index The index of the value.
     * @return Found long.
     * @throws NumberFormatException - if the Scalar value
     *  is not a parsable long.
     */
    default long longNumber(final int index) {
        final String value = this.string(index);
        if(value != null && !value.isEmpty()) {
            return Long.parseLong(value);
        }
        return -1L;
    }

    /**
     * Convenience method to directly read a LocalDate value
     * from this sequence. It is equivalent to:
     * <pre>
     *     YamlSequence sequence = ...;
     *     LocalDate dateTime = LocalDate.parse(sequence.string(...));
     * </pre>
     * @param index The index of the value.
     * @return Found LocalDate.
     * @throws DateTimeParseException - if the Scalar value cannot be parsed.
     */
    @GwtIncompatible
    default java.time.LocalDate date(final int index) {
        final String value = this.string(index);
        if(value != null && !value.isEmpty()) {
            return java.time.LocalDate.parse(value);
        }
        return null;
    }

    /**
     * Convenience method to directly read a LocalDateTime value
     * from this sequence. It is equivalent to:
     * <pre>
     *     YamlSequence sequence = ...;
     *     LocalDateTime dateTime = LocalDateTime.parse(sequence.string(...));
     * </pre>
     * @param index The index of the value.
     * @return Found LocalDateTime.
     * @throws DateTimeParseException - if the Scalar value cannot be parsed.
     */
    @GwtIncompatible
    default java.time.LocalDateTime dateTime(final int index) {
        final String value = this.string(index);
        if(value != null && !value.isEmpty()) {
            return java.time.LocalDateTime.parse(value);
        }
        return null;
    }
}
