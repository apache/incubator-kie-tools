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

import java.util.Collections;
import java.util.Set;

/**
 * Representation of an empty YAML Mapping (or "{}").  A decorator around
 * {@link YamlMapping} with no keys.
 * @author Andrew Newman
 * @version $Id: a59c837ee6cdffc821d6bf0707c179914e588670 $
 * @since 5.1.17
 */
public class EmptyYamlMapping extends BaseYamlMapping {

    /**
     * Decorated object - used for getting comments.
     */
    private final YamlMapping mapping;

    /**
     * Wrap an existing mapping - expects comments() to be implemented.
     * @param mapping The mapping to wrap.
     */
    public EmptyYamlMapping(final YamlMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public final Set<YamlNode> keys() {
        return Collections.emptySet();
    }

    @Override
    public final YamlNode value(final YamlNode key) {
        return null;
    }

    @Override
    public final Comment comment() {
        return this.mapping.comment();
    }
}
