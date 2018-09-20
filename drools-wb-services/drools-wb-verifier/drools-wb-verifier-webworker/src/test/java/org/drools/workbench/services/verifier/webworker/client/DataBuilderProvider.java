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

package org.drools.workbench.services.verifier.webworker.client;

import java.util.ArrayList;

public class DataBuilderProvider {

    public static DataBuilder row(final Object... items) {
        final DataBuilder dataBuilder = new DataBuilder();

        dataBuilder.row(items);

        return dataBuilder;
    }

    public static class DataBuilder {

        private ArrayList<Object[]> list = new ArrayList<>();

        public DataBuilder row(final Object... items) {
            list.add(items);
            return this;
        }

        public Object[][] end() {

            final Object[][] result = new Object[list.size()][list.get(0).length + 2];

            for (int i = 0; i < list.size(); i++) {
                result[i][0] = i + 1;
                result[i][1] = "description";
            }

            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j < list.get(i).length; j++) {
                    result[i][j + 2] = list.get(i)[j];
                }
            }

            return result;
        }
    }
}
