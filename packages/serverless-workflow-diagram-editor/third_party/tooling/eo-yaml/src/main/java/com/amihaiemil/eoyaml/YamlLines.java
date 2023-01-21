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

import java.util.Collection;
import java.util.Iterator;

/**
 * Iterable yaml lines.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 3ca17cbba924aa21654b7659a4bfce0bec120426 $
 * @since 1.0.0
 */
interface YamlLines extends Iterable<YamlLine> {

    /**
     * Return all the original underlying lines. No matter what
     * decorators are added on top of the base YamlLines implementation,
     * the call to this method should always be delegated down to the
     * base method, with no changes.
     * @return The original YamlLines as a Collection.
     */
    Collection<YamlLine> original();

    /**
     * Turn these lines into a YamlNode.
     * @param prev Previous YamlLine
     * @param guessIndentation If set to true, we will try to guess
     *  the correct indentation of misplaced lines.
     * @return YamlNode
     */
    YamlNode toYamlNode(final YamlLine prev, final boolean guessIndentation);

    /**
     * Default iterator which doesn't skip any line,
     * iterates over all of them.
     * @return Iterator of YamlLine.
     */
    Iterator<YamlLine> iterator();

    /**
     * Get a certain YamlLine.
     * @checkstyle ReturnCount (50 lines)
     * @param number Number of the line.
     * @return YamlLine or throws {@link IndexOutOfBoundsException}.
     */
    default YamlLine line(final int number) {
        final Collection<YamlLine> lines = this.original();
        if(number < 0 && lines.size() > 0) {
            return lines.iterator().next();
        }
        for(final YamlLine line : lines){
            if(line.number() == number) {
                return line;
            }
        }
        throw new IllegalArgumentException(
            "Couldn't find line " + number
          + ". Pay attention, there are "
          + this.original().size() + " lines!");
    }

}
