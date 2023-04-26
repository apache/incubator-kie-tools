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

import java.util.Iterator;
import java.util.List;

/**
 * Built Comments. These are comments within a YamlNode which
 * have been added by the user when building it!
 *
 * Use this class when building YAML. For reading YAML, there
 * should be an analogue implementation working with YamlLines.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 46ffdeb7930d104873fdd44fdda9ed0c13428b10 $
 * @since 4.2.0
 */
final class BuiltComments implements Comments {

    /**
     * The list of comments.
     */
    private List<Comment> comments;

    /**
     * Constructor.
     * @param comments The comments.
     */
    BuiltComments(final List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public Iterator<Comment> iterator() {
        return this.comments.iterator();
    }

    @Override
    public Comment referringTo(final YamlNode node) {
        Comment found = null;
        for(final Comment comment : this.comments) {
            if(comment.yamlNode().equals(node)) {
                found = comment;
                break;
            }
        }
        if(found == null) {
            found = new BuiltComment(node, "");
        }
        return found;
    }
}
