/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;

public class DecisionTableUIModelMapperHelper {

    public static final int ROW_INDEX_COLUMN_COUNT = 1;

    public enum DecisionTableSection {
        NONE,
        ROW_INDEX,
        INPUT_CLAUSES,
        OUTPUT_CLAUSES,
        ANNOTATION_CLAUSES
    }

    public static DecisionTableSection getSection(final DecisionTable dtable,
                                                  final int columnIndex) {
        final int inputClauseColumnCount = dtable.getInput().size();
        final int outputClauseColumnCount = dtable.getOutput().size();
        final int annotationClauseColumnCount = dtable.getAnnotations().size();

        int _columnIndex = columnIndex;
        if ((_columnIndex = _columnIndex - ROW_INDEX_COLUMN_COUNT) < 0) {
            return DecisionTableSection.ROW_INDEX;
        }
        if ((_columnIndex = _columnIndex - inputClauseColumnCount) < 0) {
            return DecisionTableSection.INPUT_CLAUSES;
        }
        if ((_columnIndex = _columnIndex - outputClauseColumnCount) < 0) {
            return DecisionTableSection.OUTPUT_CLAUSES;
        }
        if (_columnIndex - annotationClauseColumnCount < 0) {
            return DecisionTableSection.ANNOTATION_CLAUSES;
        }
        return DecisionTableSection.NONE;
    }

    public static int getInputEntryIndex(final DecisionTable dtable,
                                         final int columnIndex) {
        final int inputClauseColumnCount = dtable.getInput().size();

        int _columnIndex = columnIndex;
        if ((_columnIndex = _columnIndex - ROW_INDEX_COLUMN_COUNT) < 0) {
            throw new IllegalArgumentException("columnIndex referenced 'Row index' column. Should be a valid InputEntry column.");
        }
        if (_columnIndex > inputClauseColumnCount - 1) {
            throw new IllegalArgumentException("columnIndex did not reference a valid InputEntry column.");
        }
        return _columnIndex;
    }

    public static int getOutputEntryIndex(final DecisionTable dtable,
                                          final int columnIndex) {
        final int inputClauseColumnCount = dtable.getInput().size();
        final int outputClauseColumnCount = dtable.getOutput().size();

        int _columnIndex = columnIndex;
        if ((_columnIndex = _columnIndex - ROW_INDEX_COLUMN_COUNT) < 0) {
            throw new IllegalArgumentException("columnIndex referenced 'Row index' column. Should be a valid OutputEntry column.");
        }
        if ((_columnIndex = _columnIndex - inputClauseColumnCount) < 0) {
            throw new IllegalArgumentException("columnIndex referenced an InputEntry column. Should be a valid OutputEntry column.");
        }
        if (_columnIndex > outputClauseColumnCount - 1) {
            throw new IllegalArgumentException("columnIndex did not reference a valid OutputEntry column.");
        }
        return _columnIndex;
    }

    public static int getAnnotationEntryIndex(final DecisionTable dtable,
                                              final int columnIndex) {

        final int inputClauseColumnCount = dtable.getInput().size();
        final int outputClauseColumnCount = dtable.getOutput().size();
        final int annotationClauseColumnCount = dtable.getAnnotations().size();

        int _columnIndex = columnIndex;
        if ((_columnIndex = _columnIndex - ROW_INDEX_COLUMN_COUNT) < 0) {
            throw new IllegalArgumentException("columnIndex referenced 'Row index' column. Should be a valid AnnotationEntry column.");
        }
        if ((_columnIndex = _columnIndex - inputClauseColumnCount) < 0) {
            throw new IllegalArgumentException("columnIndex referenced an InputEntry column. Should be a valid AnnotationEntry column.");
        }
        if ((_columnIndex = _columnIndex - outputClauseColumnCount) < 0) {
            throw new IllegalArgumentException("columnIndex referenced an OutputEntry column. Should be a valid AnnotationEntry column.");
        }
        if (_columnIndex > annotationClauseColumnCount - 1) {
            throw new IllegalArgumentException("columnIndex did not reference a valid AnnotationEntry column.");
        }

        return _columnIndex;
    }
}
