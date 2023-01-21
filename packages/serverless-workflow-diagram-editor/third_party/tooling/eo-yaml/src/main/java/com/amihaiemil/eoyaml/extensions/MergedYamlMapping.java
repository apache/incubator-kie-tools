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

import java.util.Set;
import java.util.function.Supplier;

import com.amihaiemil.eoyaml.BaseYamlMapping;
import com.amihaiemil.eoyaml.Comment;
import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlMappingBuilder;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlSequence;
import com.amihaiemil.eoyaml.YamlSequenceBuilder;


/**
 * Merge two YAML Mappings into a single one. Very useful in implementing
 * Updating of an existing YamlMapping. Use it like this:
 * <pre>
 *     final YamlMapping original = ...;
 *     final YamlMapping changed = ...;
 *     final YamlMapping merged = new MergedYamlMapping(
 *         original, changed, true|false
 *     );
 *     //or, via the Supplier constructor:
 *     final YamlMapping merged = new MergedYamlMapping(
 *         original,
 *         () -&gt; {
 *             //create the changed version on-the-fly, right here.
 *         },
 *         true|false
 *     );
 * </pre>
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 6bd6766e8f3b6b1d1493457c9e082853aa3135f9 $
 * @since 4.1.0
 */
public final class MergedYamlMapping extends BaseYamlMapping {

    /**
     * The merged YamlMapping.
     */
    private YamlMapping merged;

    /**
     * Constructor. By default, conflicting keys will not be overriden.
     * @param original YamlMapping in which the changes will be merged.
     * @param changed YamlMapping containing the differences.
     */
    public MergedYamlMapping(
        final YamlMapping original,
        final YamlMapping changed
    ) {
        this(original, changed, false);
    }

    /**
     * Constructor. By default, conflicting keys will not be overriden.
     * @param original YamlMapping in which the changes will be merged.
     * @param changed Supplier of YamlMapping containing the differences.
     */
    public MergedYamlMapping(
        final YamlMapping original,
        final Supplier<YamlMapping> changed
    ) {
        this(original, changed, false);
    }

    /**
     * Constructor. You can choose whether conflicting keys will be
     * overriden or not.
     * @param original YamlMapping in which the changes will be merged.
     * @param changed YamlMapping containing the differences.
     * @param overrideConflicts Override key conflicts or not?
     */
    public MergedYamlMapping(
        final YamlMapping original,
        final Supplier<YamlMapping> changed,
        final boolean overrideConflicts
    ) {
        this(original, changed.get(), overrideConflicts);
    }

    /**
     * Constructor. You can choose whether conflicting keys will be
     * overriden or not.
     * @param original YamlMapping in which the changes will be merged.
     * @param changed YamlMapping containing the differences.
     * @param overrideConflicts Override key conflicts or not?
     */
    public MergedYamlMapping(
        final YamlMapping original,
        final YamlMapping changed,
        final boolean overrideConflicts
    ) {
        if(original == null && changed == null) {
            throw new IllegalArgumentException(
                "Both mappings cannot be null!"
            );
        } else {
            this.merged = merge(original, changed, overrideConflicts);
        }
    }

    @Override
    public Set<YamlNode> keys() {
        return this.merged.keys();
    }

    @Override
    public YamlNode value(final YamlNode key) {
        return this.merged.value(key);
    }

    @Override
    public Comment comment() {
        return this.merged.comment();
    }

    /**
     * Merge the two mappings.
     * @param original Original mapping.
     * @param changed Changed mapping.
     * @param overrideConflicts Should conflicting keys be overriden or not?
     * @return Merged mapping.
     * @checkstyle HiddenField (500 lines)
     */
    private YamlMapping merge(
        final YamlMapping original,
        final YamlMapping changed,
        final boolean overrideConflicts
    ) {
        final YamlMapping merged;
        if(original == null || original.keys().isEmpty()) {
            merged = changed;
        } else if (changed == null || changed.keys().isEmpty()) {
            merged = original;
        } else {
            merged = this.recursiveMerge(original, changed, overrideConflicts);
        }
        return merged;
    }

    /**
     * Recursively merge to mappings.
     * @param original Original mapping.
     * @param changed Changed mapping.
     * @param overrideConflicts Should conflicting keys be overridden or not?
     * @return Merged mapping.
     * @checkstyle CyclomaticComplexity (200 lines)
     * @checkstyle ExecutableStatementCount (200 lines)
     */
    private YamlMapping recursiveMerge(
        final YamlMapping original,
        final YamlMapping changed,
        final boolean overrideConflicts
    ) {
        YamlMappingBuilder originalBuilder = this
            .yamlMappingBuilderFrom(original);
        final Set<YamlNode> changedKeys = changed.keys();
        for(final YamlNode key : changedKeys) {
            final YamlNode originalValue = original.value(key);
            final YamlNode changedValue = changed.value(key);
            if (changedValue instanceof YamlMapping
                && originalValue instanceof YamlMapping) {
                originalBuilder = originalBuilder.add(
                    key,
                    this.recursiveMerge(
                        (YamlMapping) originalValue,
                        (YamlMapping) changedValue,
                        overrideConflicts
                    )
                );
            } else if(overrideConflicts
                && changedValue instanceof YamlSequence
                && originalValue instanceof YamlSequence){
                final YamlSequence originalSeq = (YamlSequence) originalValue;
                final YamlSequence changedSeq = (YamlSequence) changedValue;
                YamlSequenceBuilder originalSeqBuilder = this
                    .yamlSequenceBuilderFrom(originalSeq);
                for (final YamlNode node : changedSeq.values()) {
                    if (!originalSeq.values().contains(node)) {
                        originalSeqBuilder = originalSeqBuilder.add(node);
                    }
                }
                final Comment newComment;
                if(!changedSeq.comment().value().isEmpty()){
                    newComment = changedSeq.comment();
                }else{
                    newComment = originalSeq.comment();
                }
                originalBuilder = originalBuilder.add(
                    key,
                    originalSeqBuilder.build(newComment.value())
                );
            } else {
                final YamlNode newValue;
                if (originalValue != null) {
                    if (overrideConflicts) {
                        newValue = changedValue;
                    } else {
                        newValue = originalValue;
                    }
                } else {
                    newValue = changedValue;
                }
                originalBuilder = originalBuilder.add(key, newValue);
            }
        }
        final Comment newComment;
        if(overrideConflicts && !changed.comment().value().isEmpty()){
            newComment = changed.comment();
        }else{
            newComment = original.comment();
        }
        return originalBuilder.build(newComment.value());
    }

    /**
     * Create a {@link YamlSequenceBuilder} from a {@link YamlSequence} source.
     * @param source YamlMapping source.
     * @return Builder of YamlSequence.
     */
    private YamlSequenceBuilder yamlSequenceBuilderFrom(
        final YamlSequence source
    ) {
        YamlSequenceBuilder builder = Yaml.createYamlSequenceBuilder();
        for (final YamlNode node : source.values()) {
            builder = builder.add(node);
        }
        return builder;
    }

    /**
     * Create a {@link YamlMappingBuilder} from a {@link YamlMapping} source.
     * @param source YamlMapping source.
     * @return Builder of YamlMapping.
     */
    private YamlMappingBuilder yamlMappingBuilderFrom(
        final YamlMapping source
    ) {
        YamlMappingBuilder builder = Yaml
            .createYamlMappingBuilder();
        for (final YamlNode key : source.keys()) {
            builder = builder.add(key, source.value(key));
        }
        return builder;
    }
}
