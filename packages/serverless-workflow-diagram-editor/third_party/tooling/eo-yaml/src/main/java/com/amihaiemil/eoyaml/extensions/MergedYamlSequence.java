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
package com.amihaiemil.eoyaml.extensions;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Supplier;

import com.amihaiemil.eoyaml.BaseYamlSequence;
import com.amihaiemil.eoyaml.Comment;
import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlSequence;
import com.amihaiemil.eoyaml.YamlSequenceBuilder;

/**
 * Merge two YAML Sequences into a single one. Very useful in implementing
 * Updating of an existing YamlSequence. Use it like this:
 * <pre>
 *     final YamlSequence original = ...;
 *     final YamlSequence changed = ...;
 *     final YamlSequence merged = new MergedYamlSequence(
 *         original, changed, true|false
 *     );
 *     //or, via the Supplier constructor:
 *     final YamlSequence merged = new MergedYamlSequence(
 *         original,
 *         () -&gt; {
 *             //create the changed version on-the-fly, right here.
 *         },
 *         true|false
 *     );
 * </pre>
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: bccefba7fdc4dce03915f593f66a535f304e8c22 $
 * @since 4.1.0
 */
public final class MergedYamlSequence extends BaseYamlSequence {
    /**
     * The merged YamlSequence.
     */
    private YamlSequence merged;

    /**
     * Constructor. By default, it's simply going to add the elements
     * of changed to the ones of original.
     * @param original YamlSequence in which the changes will be merged.
     * @param changed YamlSequence containing the differences.
     */
    public MergedYamlSequence(
        final YamlSequence original,
        final YamlSequence changed
    ) {
        this(original, changed, false);
    }

    /**
     * Constructor. By default, it's simply going to add the elements
     * of changed to the ones of original.
     * @param original YamlSequence in which the changes will be merged.
     * @param changed Supplier of YamlSequence containing the differences.
     */
    public MergedYamlSequence(
        final YamlSequence original,
        final Supplier<YamlSequence> changed
    ) {
        this(original, changed, false);
    }

    /**
     * Constructor. You can choose whether to override existing indices
     * or not.
     * @param original YamlSequence in which the changes will be merged.
     * @param changed YamlSequence containing the differences.
     * @param overrideIndices Override existing indices or not?
     */
    public MergedYamlSequence(
        final YamlSequence original,
        final Supplier<YamlSequence> changed,
        final boolean overrideIndices
    ) {
        this(original, changed.get(), overrideIndices);
    }

    /**
     * Constructor. You can choose whether conflicting keys will be
     * overriden or not.
     * @param original YamlMapping in which the changes will be merged.
     * @param changed YamlMapping containing the differences.
     * @param overrideIndices Override existing indices or not?
     */
    public MergedYamlSequence(
        final YamlSequence original,
        final YamlSequence changed,
        final boolean overrideIndices
    ) {
        if(original == null && changed == null) {
            throw new IllegalArgumentException(
                "Both sequences cannot be null!"
            );
        } else {
            this.merged = merge(original, changed, overrideIndices);
        }
    }

    @Override
    public Collection<YamlNode> values() {
        return this.merged.values();
    }

    @Override
    public Comment comment() {
        return this.merged.comment();
    }

    /**
     * Merge the two sequences.
     * @checkstyle CyclomaticComplexity (100 lines)
     * @checkstyle ExecutableStatementCount (100 lines)
     * @return Merged YamlSequence.
     * @param original Original sequence.
     * @param changed Changed sequence.
     * @param overrideIndices Should the existing indices be overriden
     *  or not?
     */
    private static YamlSequence merge(
        final YamlSequence original,
        final YamlSequence changed,
        final boolean overrideIndices
    ) {
        final YamlSequence merged;
        if(original == null || original.size() == 0) {
            merged = changed;
        } else if (changed == null || changed.size() == 0) {
            merged = original;
        } else {
            if(overrideIndices) {
                if (changed.size() >= original.size()) {
                    merged = changed;
                } else {
                    YamlSequenceBuilder builder = Yaml
                        .createYamlSequenceBuilder();
                    int continueFrom = -1;
                    for(final YamlNode node : changed) {
                        builder = builder.add(node);
                        continueFrom++;
                    }
                    final Iterator<YamlNode> originalIt = original.iterator();
                    int originalIdx = -1;
                    while(originalIt.hasNext()) {
                        if(originalIdx >= continueFrom) {
                            builder = builder.add(originalIt.next());
                        } else {
                            originalIt.next();
                        }
                        originalIdx++;
                    }
                    merged = builder.build();
                }
            } else {
                YamlSequenceBuilder builder = Yaml
                    .createYamlSequenceBuilder();
                for(final YamlNode node : original) {
                    builder = builder.add(node);
                }
                for(final YamlNode node : changed) {
                    builder = builder.add(node);
                }
                merged = builder.build();
            }
        }
        return merged;
    }
}
