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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * YamlLines implementation that looks for the lines which have
 * an indentation level greater the one of a given reference line.
 * The lines "nested" after the given reference line.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 107b69bda1d265572886b3b4da1cc502e9b2c848 $
 * @since 4.2.0
 */
final class GreaterIndentation implements YamlLines {

    /**
     * Reference YAML line.
     */
    private YamlLine reference;

    /**
     * YamlLines.
     */
    private final YamlLines yamlLines;

    /**
     * Ctor.
     * @param reference Reference YAML Line.
     * @param yamlLines The Yaml lines.
     */
    GreaterIndentation(final YamlLine reference, final YamlLines yamlLines) {
        this.reference = reference;
        this.yamlLines = yamlLines;
    }

    /**
     * Returns an iterator over these Yaml lines.
     * It <b>only</b> iterates over the lines which have a greater
     * indentation than the reference line. It breaks iteration when
     * a line with a lesser or equal indentation is met, since that should
     * be the beginning of another YAML object.
     * @return Iterator over these yaml lines.
     */
    @Override
    public Iterator<YamlLine> iterator() {
        Iterator<YamlLine> iterator = this.yamlLines.iterator();
        if (iterator.hasNext()) {
            final List<YamlLine> greater = new ArrayList<>();
            while (iterator.hasNext()) {
                YamlLine current = iterator.next();
                if(current.indentation() > this.reference.indentation()) {
                    greater.add(current);
                } else if (
                    current.indentation() <= this.reference.indentation()
                ) {
                    break;
                }
            }
            iterator = greater.iterator();
        }
        return iterator;
    }

    @Override
    public Collection<YamlLine> original() {
        return this.yamlLines.original();
    }

    @Override
    public YamlNode toYamlNode(
        final YamlLine prev,
        final boolean guessIndentation
    ) {
        return this.yamlLines.toYamlNode(prev, guessIndentation);
    }
}
