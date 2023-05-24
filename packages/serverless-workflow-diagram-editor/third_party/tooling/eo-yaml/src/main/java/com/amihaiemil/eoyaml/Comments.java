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
import java.util.Iterator;

/**
 * These are all the comments within a YAML node.<br><br>
 * For example, in the case of a mapping, they are the comments
 * referring to the key:value pairs. In the case of a sequence,
 * these are the comments referring to its elements.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 16cbafd276747fbce4bc4b83aaab37f59bad5bdf $
 * @since 4.2.0
 */
interface Comments extends Iterable<Comment> {

    /**
     * Get the Comment referring to the specified YamlNode.
     * @param plainScalar The plain scalar YAML node as String.
     * @return Comment.
     */
    default Comment referringTo(final String plainScalar) {
        return this.referringTo(
            Yaml.createYamlScalarBuilder()
                .addLine(plainScalar)
                .buildPlainScalar()
        );
    }

    /**
     * Get the Comment referring to the specified YamlNode.
     * @param node YamlNode for which we search the referring comment.
     * @return Comment.
     */
    Comment referringTo(final YamlNode node);

    /**
     * Empty comments. Use this as an alternative to null, when you have no
     * Comments to give.
     * @author Mihai Andronache (amihaiemil@gmail.com)
     * @version $Id: 16cbafd276747fbce4bc4b83aaab37f59bad5bdf $
     * @since 4.2.0
     */
    class Empty implements Comments {

        @Override
        public Comment referringTo(final YamlNode node) {
            return new BuiltComment(node, "");
        }

        @Override
        public Iterator<Comment> iterator() {
            return new ArrayList<Comment>().iterator();
        }
    }

}
