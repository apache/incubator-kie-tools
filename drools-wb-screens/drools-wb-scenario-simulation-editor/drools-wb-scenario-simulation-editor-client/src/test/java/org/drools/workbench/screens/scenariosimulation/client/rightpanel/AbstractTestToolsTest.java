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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.IntStream;

import com.google.gwt.dom.client.LIElement;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy;
import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.junit.Before;
import org.mockito.Mock;

abstract class AbstractTestToolsTest {

    @Mock
    protected LIElement lIElementMock;

    @Mock
    protected ViewsProvider viewsProviderMock;

    protected SortedMap<String, FactModelTree> dataObjectFactTreeMap;
    protected SortedMap<String, FactModelTree> simpleJavaTypeTreeMap;
    protected SortedMap<String, FactModelTree> instanceFactTreeMap;
    protected SortedMap<String, FactModelTree> simpleJavaInstanceFactTreeMap;
    protected String FACT_NAME;
    protected String FACT_PACKAGE = "test.scesim.package";
    protected final String GRID_COLUMN_TITLE = "GRID_COLUMN_TITLE";
    protected final String GRID_COLUMN_GROUP = "GIVEN";
    protected final String GRID_COLUMN_ID = "GRID_COLUMN_ID";
    protected final String GRID_CELL_TEXT = "GRID_CELL_TEXT";
    protected final String FULL_PACKAGE = "test.scesim";
    protected final String VALUE = "VALUE";
    protected final String VALUE_CLASS_NAME = String.class.getName();
    protected final int ROW_COUNT = 4;
    protected final int ROW_INDEX = 3;
    protected final int COLUMN_INDEX = 5;
    protected FactModelTree FACT_MODEL_TREE;

    @Before
    public void setup() {
        dataObjectFactTreeMap = getDataObjectFactTreeMap();
        simpleJavaTypeTreeMap = getSimpleJavaTypeFieldsMap();
        instanceFactTreeMap = new TreeMap<>();
        dataObjectFactTreeMap.keySet().forEach(key -> instanceFactTreeMap.put(getRandomString(), dataObjectFactTreeMap.get(key)));
        simpleJavaInstanceFactTreeMap = new TreeMap<>();
        simpleJavaTypeTreeMap.keySet().forEach(key -> simpleJavaInstanceFactTreeMap.put(getRandomString(), simpleJavaTypeTreeMap.get(key)));
        FACT_NAME = new ArrayList<>(dataObjectFactTreeMap.keySet()).get(0);
        FACT_MODEL_TREE = dataObjectFactTreeMap.get(FACT_NAME);
    }

    protected String getRandomFactModelTree(Map<String, FactModelTree> source, int position) {
        return new ArrayList<>(source.keySet()).get(position);
    }

    protected SortedMap<String, FactModelTree> getDataObjectFactTreeMap() {
        SortedMap<String, FactModelTree> toReturn = new TreeMap<>();
        IntStream
                .range(0, 3)
                .forEach(id -> {
                    String key = getRandomString();
                    FactModelTree value = new FactModelTree(key, FACT_PACKAGE, getMockSimpleProperties(), new HashMap<>());
                    toReturn.put(key, value);
                    if (id == 1) {
                        value.addSimpleProperty(getRandomString(), getRandomFactModelTree(toReturn, 0));
                    }
                    if (id == 2) {
                        value.addSimpleProperty(getRandomString(), getRandomFactModelTree(toReturn, 1));
                        // Recursion
                        value.addSimpleProperty(getRandomString(), value.getFactName());
                    }
                });
        return toReturn;
    }

    protected SortedMap<String, FactModelTree> getSimpleJavaTypeFieldsMap() {
        SortedMap<String, FactModelTree> toReturn = new TreeMap<>();
        for (String key : DataManagementStrategy.SIMPLE_CLASSES_MAP.keySet()) {
            Map<String, String> simpleProperties = new HashMap<>();
            String fullName = DataManagementStrategy.SIMPLE_CLASSES_MAP.get(key).getCanonicalName();
            simpleProperties.put("value", fullName);
            String packageName = fullName.substring(0, fullName.lastIndexOf("."));
            FactModelTree value = new FactModelTree(key, packageName, simpleProperties, new HashMap<>());
            toReturn.put(key, value);
        }
        return toReturn;
    }

    protected Map<String, String> getMockSimpleProperties() {
        Map<String, String> toReturn = new HashMap<>();
        IntStream
                .range(0, +3)
                .forEach(id -> toReturn.put(getRandomString(), getRandomType()));
        return toReturn;
    }

    protected String getRandomString() {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder builder = new StringBuilder();
        int numberOfLetters = letters.length();
        Random random = new Random();
        int sizeOfRandomString = random.nextInt(6) + 3;
        IntStream
                .range(0, sizeOfRandomString)
                .forEach(position -> builder.append(letters.charAt(random.nextInt(numberOfLetters))));
        return builder.toString();
    }

    protected String getRandomType() {
        int type = new Random().nextInt(4);
        switch (type) {
            case 0:
                return "lava.lang.String";
            case 1:
                return "byte";
            case 2:
                return "java.lang.Integer";
            case 3:
                return "java.lang.Boolean";
            default:
                return "int";
        }
    }
}