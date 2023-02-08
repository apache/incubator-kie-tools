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
 * SameIndentationLevel. Decorates some YamlLines
 * and iterates only over those which are at the same
 * indentation level with the first one.
 * Use this class as follows:
 * <pre>
 *  YamlLines noDirs = new SameIndentationLevel(
 *      new AllYamlLines(lines)
 *  ); //Iterates only over the lines which have the same indentation.
 * </pre>
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 4ccc551374153dfab426c22f9dae965c19af6bcd $
 * @since 3.0.2
 */
final class SameIndentationLevel implements YamlLines {

    /**
     * YamlLines.
     */
    private final YamlLines yamlLines;

    /**
     * Ctor.
     * @param yamlLines The Yaml lines.
     */
    SameIndentationLevel(final YamlLines yamlLines) {
        this.yamlLines = yamlLines;
    }

    /**
     * Returns an iterator over these Yaml lines.
     * It <b>only</b> iterates over the lines which are at the same
     * level of indentation with the first! It breaks iteration
     * when a line with smaller indentation is met since that is the
     * beginning of another YAML object.
     * @return Iterator over these yaml lines.
     */
    @Override
    public Iterator<YamlLine> iterator() {
        Iterator<YamlLine> iterator = this.yamlLines.iterator();
        if (iterator.hasNext()) {
            final List<YamlLine> sameIndentation = new ArrayList<>();
            final YamlLine first = iterator.next();
            sameIndentation.add(first);
            int firstIndentation = first.indentation();
            boolean firstIsDashMap = this.mappingStartsAtDash(first);
            while (iterator.hasNext()) {
                YamlLine current = iterator.next();
                if (firstIsDashMap
                        && this.mapping(current)
                        && current.indentation() == firstIndentation + 2) {
                    sameIndentation.add(current);
                } else if(current.indentation() == firstIndentation) {
                    sameIndentation.add(current);
                } else if (current.indentation() < firstIndentation) {
                    break;
                }
            }
            iterator = sameIndentation.iterator();
        }
        return iterator;
    }

    @Override
    public Collection<YamlLine> original() {
        return this.yamlLines.original();
    }

    @Override
    public YamlNode toYamlNode(final YamlLine prev) {
        return this.yamlLines.toYamlNode(prev);
    }

    /**
     * Returns true if there's a YamlMapping starting right after the
     * dash, on the same line.
     * @param dashLine Line.
     * @return True of false.
     */
    private boolean mappingStartsAtDash(final YamlLine dashLine) {
        final String trimmed = dashLine.trimmed();
        final boolean escapedScalar = trimmed.matches("^\\s*-\\s*\".*\"$")
            || trimmed.matches("^\\s*-\\s*'.*'$");
        return trimmed.matches("^\\s*-.*:(\\s.*)?$") && !escapedScalar;
    }

    /**
     * Returns true if the line is a mapping entry.
     * @param dashLine Line.
     * @return True of false.
     */
    private boolean mapping(final YamlLine dashLine) {
        final String trimmed = dashLine.trimmed();
        final boolean escapedScalar = trimmed.matches("^\\s*\".*\"$")
                || trimmed.matches("^\\s*'.*'$");
        return trimmed.matches("^.*:\\s.+$") && !escapedScalar;
    }

}
