/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.dashbuilder.client.external.csv;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.def.DataColumnDef;

/**
 * Retrieves columns from CSV
 *
 */
@ApplicationScoped
public class CSVColumnsFunction implements Function<String, List<DataColumnDef>> {

    @Override
    public List<DataColumnDef> apply(String t) {
        if (t.trim().isEmpty()) {
            return Collections.emptyList();
        }

        var columnsLine = t.split("\n")[0];
        var columnsNames = columnsLine.split(",");
        return columnsLine.trim().isEmpty() ? Collections.emptyList() :
                Arrays.stream(columnsNames)
                        .map(cl -> new DataColumnDef(cl, ColumnType.LABEL))
                        .collect(Collectors.toList());
    }

}
