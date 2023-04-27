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

import java.util.Set;

/**
 * Decorator for a {@link YamlMapping} which throws
 * YamlNodenotFoundException, instead of returning null,
 * if a given key doesn't exist in the mapping, or if it points
 * to a different type of node than the demanded one.<br><br>
 * It is based on the fail-fast and null-is-bad idea <br>
 * see here: http://www.yegor256.com/2014/05/13/why-null-is-bad.html
 * @deprecated This class will be moved to the extensions package in one
 *  of the future releases. There will be no changes to it other than a
 *  more suitable package.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 31dd44906ab8f91053b0c35c9ffa0f2f40a101a5 $
 * @since 1.0.0
 */
@Deprecated
public final class StrictYamlMapping extends BaseYamlMapping {

    /**
     * Original YamlMapping.
     */
    private YamlMapping decorated;

    /**
     * Ctor.
     * @param decorated Original YamlMapping
     */
    public StrictYamlMapping(final YamlMapping decorated) {
        this.decorated = decorated;
    }

    @Override
    public Set<YamlNode> keys() {
        return this.decorated.keys();
    }

    @Override
    public YamlNode value(final YamlNode key) {
        YamlNode found = this.decorated.value(key);
        if (found == null) {
            throw new YamlNodeNotFoundException(
                "No YAML found for key " + key
            );
        }
        return found;
    }

    @Override
    public Comment comment() {
        return this.decorated.comment();
    }
}
