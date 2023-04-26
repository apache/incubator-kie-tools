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
 * Iterate over the lines which are YAML comments (begin with "#") and break
 * iteration when a non-comment line is found. In essence, this reads the lines
 * of the first comment from a given YamlLines, if it exists.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 25922d677101d9c3d755c126a7918d5290c8459c $
 * @since 4.2.0
 */
final class FirstCommentFound implements YamlLines {

    /**
     * Lines where we look for the comment.
     */
    private final YamlLines lines;

    /**
     * We need this flag to distinguish between the comment
     * of the overall YAML document and the comment on top
     * of the very first node.
     */
    private final boolean documentComment;

    /**
     * Ctor.
     * @param lines The Yaml lines where we look for the comment.
     */
    FirstCommentFound(final YamlLines lines) {
        this(lines, false);
    }

    /**
     * Ctor.
     * @param lines The Yaml lines where we look for the comment.
     * @param documentComment Are we looking for the comment of
     *  the overall YAML?
     */
    FirstCommentFound(final YamlLines lines, final boolean documentComment) {
        this.lines = lines;
        this.documentComment = documentComment;
    }

    /**
     * Returns an iterator over the lines of the first found comment.
     * @return Iterator over these yaml lines.
     */
    @Override
    public Iterator<YamlLine> iterator() {
        final Iterator<YamlLine> iterator;
        if(this.documentComment) {
            iterator = this.documentComment();
        } else {
            iterator = this.nodeComment();
        }
        return iterator;
    }

    @Override
    public Collection<YamlLine> original() {
        return this.lines.original();
    }

    @Override
    public YamlNode toYamlNode(final YamlLine prev) {
        return this.lines.toYamlNode(prev);
    }

    /**
     * Node comment. We are taking all the comment lines
     * from above the node and stop when we find a non-comment
     * line or the dash line (---), which signifies the start
     * of the document: the start of the document is the end
     * of the first node's comment, any comment above that is
     * the comment of the overall YAML document..
     * @return Iterator of YamlLine.
     */
    private Iterator<YamlLine> nodeComment() {
        Iterator<YamlLine> iterator = this.lines.iterator();
        final List<YamlLine> comment = new ArrayList<>();
        while (iterator.hasNext()) {
            final YamlLine line = iterator.next();
            boolean hasComment = !line.comment().isEmpty();
            boolean notYamlStart = !"---".equals(line.trimmed());
            if(notYamlStart && hasComment && line.trimmed().startsWith("#")) {
                comment.add(line);
            } else {
                break;
            }
        }
        iterator = comment.iterator();
        return iterator;
    }

    /**
     * Document comment. We are interested only in the comment-lines
     * which are above the "---" marker (the YAML document start-marker).
     * @return Iterator of YamlLine.
     */
    private Iterator<YamlLine> documentComment() {
        final List<YamlLine> comment = new ArrayList<>();
        Iterator<YamlLine> iterator = this.lines.iterator();
        while (iterator.hasNext()) {
            YamlLine line = iterator.next();
            if("---".equals(line.trimmed())) {
                while(iterator.hasNext()) {
                    line = iterator.next();
                    if(!line.comment().isEmpty()) {
                        if(line.trimmed().startsWith("#")) {
                            comment.add(line);
                        }
                    } else if (!line.trimmed().isEmpty()){
                        break;
                    }
                }
                break;
            }
        }
        iterator = comment.iterator();
        return iterator;
    }
}
