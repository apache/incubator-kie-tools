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
 * StartMarkers. Decorator over some YamlLines, that
 * only iterates over the YAML Document start markers (---).<br><br>
 * It is used by {@link ReadYamlStream} to separate YAML documents.
 * @author Mihai Andronache(amihaiemil@gmail.com)
 * @version $Id: 9a316878752a6a3002ce39edaa9b39c46bdaf4ab $
 * @since 3.1.4
 */
final class StartMarkers implements YamlLines {

    /**
     * Yaml lines.
     */
    private YamlLines yamlLines;

    /**
     * Constructor.
     * @param yamlLines The YAML Lines.
     */
    StartMarkers(final YamlLines yamlLines){
        this.yamlLines = yamlLines;
    }

    /**
     * Returns an iterator containing only the Start Marker lines (---),
     * with the possible exception of the FIRST line, which can also be
     * the NullYamlLine (in YAML Streams, the Start Marker
     * of the first document can be missing). e.g. Both are valid:
     * <pre>
     * ---
     * test: 1
     * ---
     * test 2
     * </pre>
     * and
     * <pre>
     * test: 1
     * ---
     * test: 2
     * </pre>
     * @return Iterator over these yaml lines.
     */
    @Override
    public Iterator<YamlLine> iterator() {
        Iterator<YamlLine> iterator = this.yamlLines.iterator();
        if (iterator.hasNext()) {
            final List<YamlLine> docsStart = new ArrayList<>();
            final YamlLine first = iterator.next();
            if("---".equals(first.trimmed())) {
                docsStart.add(first);
            } else {
                docsStart.add(new YamlLine.NullYamlLine());
            }
            while (iterator.hasNext()) {
                final YamlLine current = iterator.next();
                final String currentLine = current.trimmed();
                if ("---".equals(currentLine)) {
                    docsStart.add(current);
                }
            }
            iterator = docsStart.iterator();
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
