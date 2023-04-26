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

import com.amihaiemil.eoyaml.exceptions.YamlReadingException;

/**
 * YAML node.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 6f2d803bb0aafbdcc5a24f68c05992f305ac4e3d $
 * @since 1.0.0
 * @see <a href="http://yaml.org/spec/1.2/spec.html#node/">Spec1.2/node</a>
 */
public interface YamlNode extends Comparable<YamlNode> {

    /**
     * Is this YamlNode empty?
     * @return True or false.
     */
    boolean isEmpty();

    /**
     * Comment referring to this Node.
     * @return Comment. If there is no comment, it will return
     *  an "empty" comment (an instance of Comment with empty-string value()).
     */
    Comment comment();

    /**
     * Type of the node.
     * @return Node type.
     */
    Node type();

    /**
     * Gives a String value of the node.
     * @return String value of the node.
     * @throws YamlReadingException If the node type is not
     *  Scalar.
     * @throws ClassCastException   If the node type couldn't
     *  defined correctly.
     */
    Scalar asScalar() throws YamlReadingException, ClassCastException;

    /**
     * Gives a Mapping value of the node.
     * @return Mapping value of the node.
     * @throws YamlReadingException If the node type is not
     *  Mapping.
     * @throws ClassCastException   If the node type couldn't
     *  defined correctly.
     */
    YamlMapping asMapping() throws YamlReadingException, ClassCastException;

    /**
     * Gives a Sequence value of the node.
     * @return Sequence value of the node.
     * @throws YamlReadingException If the node type is not
     *  Sequence.
     * @throws ClassCastException   If the node type couldn't
     *  defined correctly.
     */
    YamlSequence asSequence() throws YamlReadingException, ClassCastException;

    /**
     * Gives a Stream value of the node.
     * @return Stream value of the node.
     * @throws YamlReadingException If the node type is not
     *  Stream.
     * @throws ClassCastException   If the node type couldn't
     *  defined correctly.
     */
    YamlStream asStream() throws YamlReadingException, ClassCastException;

    /**
     * Gives the {@link T} instance from node class and node type.
     * @param clazz Requested a {@link YamlNode} class.
     * @param type Requested a {@link Node}.
     * @param <T> Requested a {@link YamlNode} class instance.
     * @return Requested a {@link YamlNode}.
     * @throws YamlReadingException If the type is not a {@link T}.
     * @throws ClassCastException   If the node type couldn't
     *  defined correctly.
     */
    <T extends YamlNode> T asClass(Class<T> clazz, Node type)
        throws YamlReadingException, ClassCastException;

}
