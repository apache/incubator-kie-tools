/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.scenariosimulation.client;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class used to provide common methods used by different classes
 */
public class TestUtils {

    public static final int NUMBER_OF_ROWS = 3;
    public static final int NUMBER_OF_COLUMNS = 3;

    // headersMap represents the column_id:column_title map
    public static Map<Integer, String> getHeadersMap() {
        return IntStream.range(0, NUMBER_OF_COLUMNS)
                .boxed()
                .collect(
                        Collectors.toMap(
                                columnId -> columnId,
                                columnId -> "COL-" + columnId
                        ));
    }

    // rowsMap represents the row_id : (column_id:cell_text) map
    public static Map<Integer, Map<Integer, String>> getRowsMap() {
        return IntStream.range(0, NUMBER_OF_ROWS)
                .boxed()
                .collect(
                        Collectors.toMap(
                                columnId -> columnId,
                                rowId -> IntStream.range(0, NUMBER_OF_COLUMNS)
                                        .boxed()
                                        .collect(
                                                Collectors.toMap(
                                                        columnId -> columnId,
                                                        columnId -> "CELL-" + rowId + "-" + columnId
                                                ))
                        )
                );
    }
}
