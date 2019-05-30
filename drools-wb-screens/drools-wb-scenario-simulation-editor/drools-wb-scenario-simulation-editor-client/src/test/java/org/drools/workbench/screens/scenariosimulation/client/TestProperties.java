/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.scenariosimulation.api.model.FactMappingType;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;

/**
 * Put all statically defined properties here
 */
public class TestProperties {

    public static final int ROW_INDEX = 2;
    public static final int ROW_COUNT = 3;
    public static final int COLUMN_INDEX = 3;
    public static final int COLUMN_NUMBER = COLUMN_INDEX + 1;

    public static final int FIRST_INDEX_LEFT = 2;
    public static final int FIRST_INDEX_RIGHT = 4;
    public static final String COLUMN_ID = "COLUMN ID";

    public static final String COLUMN_GROUP = FactMappingType.EXPECT.name();

    public static final String FULL_PACKAGE = "test.scesim";

    public static final String LOWER_CASE_VALUE = "value";

    public static final String MULTIPART_VALUE = "MULTIPART.VALUE";

    public static final List<String> MULTIPART_VALUE_ELEMENTS = Collections.unmodifiableList(Arrays.asList(MULTIPART_VALUE.split("\\.")));

    public static final String CLASS_NAME = "TestClass";

    public static final String PROPERTY_NAME = "testProperty";

    public static final String FULL_CLASS_NAME = FULL_PACKAGE + "." + CLASS_NAME;

    public static final String FULL_PROPERTY_NAME = CLASS_NAME + "." + PROPERTY_NAME;

    public static final List<String> FULL_PROPERTY_NAME_ELEMENTS = Arrays.asList(CLASS_NAME, PROPERTY_NAME);

    public static final String VALUE_CLASS_NAME = String.class.getName();

    public static final String LIST_CLASS_NAME = List.class.getName();
    public static final String MAP_CLASS_NAME = Map.class.getName();

    public static final String FACT_IDENTIFIER_NAME = "FACT_IDENTIFIER_NAME";

    public static final String FACT_ALIAS = "FACT_ALIAS";

    public static final String GRID_PROPERTY_TITLE = "GRID_PROPERTY_TITLE";
    public static final String GRID_COLUMN_GROUP = "GIVEN";
    public static final String GRID_COLUMN_ID = "GRID_COLUMN_ID";

    public static final String FULL_CLASSNAME_CREATED = FULL_PACKAGE + "." + CLASS_NAME;
    public static final String LIST_PROPERTY_NAME = "listProperty";
    public static final String FULL_PROPERTY_PATH = CLASS_NAME + "." + LIST_PROPERTY_NAME;
    public static final List<String> FULL_PROPERTY_PATH_ELEMENTS = Arrays.asList(FULL_PROPERTY_PATH.split("\\."));

    public static final String TEST_PROPERTYNAME = "TEST-PROPERTYNAME";

    public static final String MAP_TEST_KEY = "TEST-KEY";
    public static final String MAP_TEST_VALUE = "TEST-MULTIPART_VALUE";
    public static final Map<String, String> TEST_INSTANCE_PROPERTY_MAP = Collections.singletonMap(MAP_TEST_KEY, MAP_TEST_VALUE);
    public static final Map<String, String> TEST_KEY_PROPERTY_MAP = Collections.singletonMap("TEST-KEY1", "TEST-KEY2");
    public static final Map<String, String> TEST_VALUE_PROPERTYY_MAP = Collections.singletonMap("TEST-VALUE1", "TEST-VALUE2");

    public static final String TEST_JSON = "TEST-JSON";
    public static final String TEST_CLASSNAME = "TEST-CLASSNAME";
    public static final String TEST_KEY = TEST_CLASSNAME + "#" + TEST_PROPERTYNAME;
    public static final int CHILD_COUNT = 3;
    public static final String ITEM_ID = String.valueOf(CHILD_COUNT - 1);
    public static final String UPDATED_VALUE = "UPDATED_VALUE";
    public static final int JSON_ARRAY_SIZE = 2;
    public static final Set<String> KEY_SET = new HashSet<>(Arrays.asList("prop1", "prop2"));

    public static final String ELEMENT1_ID = "ELEMENT1_ID";
    public static final String ELEMENT2_ID = "ELEMENT2_ID";

    public static final String TEST_ITEM_ID = "TEST-ITEM-ID";
    public static final String EXPANDABLE_PROPERTY = "EXPANDABLE_PROPERTY";
    public static final Map<String, String> TEST_PROPERTIES_MAP = Collections.singletonMap("TEST-KEY", "TEST-MULTIPART_VALUE");
    public static final Map<String, String> EXPANDABLE_PROPERTIES_MAP = Collections.singletonMap("EXPANDABLE-KEY", "EXPANDABLE-MULTIPART_VALUE");
    public static final Map<String, Map<String, String>> EXPANDABLE_PROPERTIES_VALUES = Collections.singletonMap(EXPANDABLE_PROPERTY, EXPANDABLE_PROPERTIES_MAP);
    public static final Map<String, List<String>> ITEM_ID_EXPANDABLE_PROPERTIES_MAP_LOCAL = new HashMap<>();
    public static final List<String> EXPANDABLE_PROPERTIES = Collections.singletonList(EXPANDABLE_PROPERTY);

    public static final String INNER_TEXT = "INNER_TEXT";
    public static final String TEST_PROPERTYVALUE = "TEST-PROPERTYVALUE";
    public static final String TEST_NEWVALUE = "TEST_NEWVALUE";

    public static final String GRID_COLUMN_ID_1 = GRID_COLUMN_ID + "_1";
    public static final String GRID_PROPERTY_TITLE_1 = GRID_PROPERTY_TITLE + "_1";
    public static final String FACT_ALIAS_1 = FACT_ALIAS + "_1";
    public static final String FULL_CLASS_NAME_1 = FULL_CLASS_NAME + "_1";
    public static final String FACT_IDENTIFIER_NAME_1 = FACT_IDENTIFIER_NAME + "_1";
    public static final String VALUE_1 = MULTIPART_VALUE + "_1";

    public static final String FACT_NAME = "FACT_NAME";
    public static final String FIELD_NAME = "FIELD_NAME";

    public static final String FILE_CONTENT = "FILE_CONTENT";

    public static final String FULL_FACT_CLASSNAME = "FULL_FACT_CLASSNAME";

    public static final String STRING_CLASS_NAME = String.class.getCanonicalName();
    public static final String NUMBER_CLASS_NAME = Number.class.getCanonicalName();
    public static final Map<String, String> EXPECTED_MAP_FOR_NOT_SIMPLE_TYPE = new HashMap<>();
    public static final Map<String, String> EXPECTED_MAP_FOR_NOT_SIMPLE_TYPE_1 = new HashMap<>();
    public static final Map<String, String> EXPECTED_MAP_FOR_NOT_SIMPLE_TYPE_2 = new HashMap<>();
    public static final Map<String, String> EXPECTED_MAP_FOR_NOT_SIMPLE_TYPE_3 = new HashMap<>();

    public static final Double GRID_WIDTH = 100.0;
    public static final Double HEADER_HEIGHT = 10.0;
    public static final Double HEADER_ROW_HEIGHT = 10.0;
    public static final int UI_COLUMN_INDEX = 0;
    public static final int UI_ROW_INDEX = 1;
    public static final double CLICK_POINT_X = 5;
    public static final double CLICK_POINT_Y = 5;
    public static final int OFFSET_X = 0;

    public static final int MOUSE_EVENT_X = 32;
    public static final int MOUSE_EVENT_Y = 64;
    public static final double GRID_COMPUTED_LOCATION_X = 100.0;
    public static final double GRID_COMPUTED_LOCATION_Y = 200.0;

    public static final List<GridColumn.HeaderMetaData> HEADER_META_DATA = new ArrayList<>();
    public static final List<GridRow> GRID_ROWS = new ArrayList<>();

    public static final String MAIN_TITLE_TEXT = "MAIN_TITLE_TEXT";
    public static final String MAIN_QUESTION_TEXT = "MAIN_QUESTION_TEXT";
    public static final String TEXT1_TEXT = "TEXT1_TEXT";
    public static final String TEXT_QUESTION_TEXT = "TEXT_QUESTION_TEXT";
    public static final String TEXT_WARNING_TEXT = "TEXT_WARNING_TEXT";
    public static final String OKDELETE_BUTTON_TEXT = "OKDELETE_BUTTON_TEXT";
    public static final String OPTION1_TEXT = "OPTION1_TEXT";
    public static final String OPTION2_TEXT = "OPTION2_TEXT";
    public static final String OKPRESERVE_BUTTON_TEXT = "OKPRESERVE_BUTTON_TEXT";

    public static final String OK_BUTTON_TEXT = "OK_BUTTON_TEXT";

    public static final String TITLE = "TITLE";
    public static final String YES_BUTTON_TEXT = "YES_BUTTON_TEXT";
    public static final String NO_BUTTON_TEXT = "NO_BUTTON_TEXT";
    public static final String CONFIRM_MESSAGE = "CONFIRM_MESSAGE";
    public static final String INLINE_NOTIFICATION_MESSAGE = "INLINE_NOTIFICATION_MESSAGE";

    public static final String MAIN_TEXT = "MAIN_TEXT";

    public static final String TEXT_DANGER_TEXT = "TEXT_DANGER_TEXT";

    public static final String PLACEHOLDER = "PLACEHOLDER";
    public static final String LIST_VALUE = "[ \"Ford\", \"BMW\", \"Fiat\" ]";
    public static final String MAP_VALUE = "{\"name\":\"myname\",\"age\":29}";

    public static final String SCENARIO_TYPE = "SCENARIO_TYPE";
    public static final String FILE_NAME = "FILE_NAME";
    public static final String KIE_SESSION = "KIE_SESSION";
    public static final String RULE_FLOW_GROUP = "RULE_FLOW_GROUP";
    public static final String DMO_SESSION = "DMO_SESSION";
    public static final String DMN_FILE_PATH = "DMN_FILE_PATH";
    public static final String DMN_NAMESPACE = "DMN_NAMESPACE";
    public static final String DMN_NAME = "DMN_NAME";

    public static final String FACT_PACKAGE = "test.scesim.package";
    public static final String GRID_COLUMN_TITLE = "GRID_COLUMN_TITLE";
    public static final String GRID_CELL_TEXT = "GRID_CELL_TEXT";

    public static final String LIST_GROUP_ITEM = "list-group-item";

    public static final String COLUMN_INSTANCE_TITLE_FIRST = "COLUMN_INSTANCE_TITLE_FIRST";
    public static final String COLUMN_PROPERTY_TITLE_FIRST = "COLUMN_PROPERTY_TITLE_FIRST";
    public static final String COLUMN_GROUP_FIRST = "OTHER";

    public static final int HEADER_ROWS = 2;
    public static final String COLUMN_ONE_TITLE = "column one";
    public static final String COLUMN_TWO_TITLE = "column two";

    public static final String TEST = "test";
    public static final String DEFAULT_VALUE = "DEFAULT_VALUE";
    public static final String KIEASSETSDROPDOWNVIEW_SELECT = "KIEASSETSDROPDOWNVIEW_SELECT";

    public static Integer MX = 300;
    public static Integer MY = 200;
    public static final Integer DX = 325;
    public static final Integer DY = 250;
    public static final Double CELL_WIDTH = 50d;
    public static final Integer LARGE_LAYER = 1000;
    public static final Integer TINY_LAYER = 100;
    public static final Integer SCROLL_TOP = 15;
    public static final Integer SCROLL_LEFT = 75;
    public static final String RAW_VALUE = "raw";
    public static final String ERROR_VALUE = "error";
    public static final String NULL = "null";
    public static final String EXCEPTION = "GenericException";

    public static final String EXPRESSION_ALIAS_DESCRIPTION = "EXPRESSION_ALIAS_DESCRIPTION";
    public static final String EXPRESSION_ALIAS_GIVEN = "EXPRESSION_ALIAS_GIVEN";
    public static final String EXPRESSION_ALIAS_INTEGER = "EXPRESSION_ALIAS_INTEGER";
    public static final int COLUMNS = 6;
    public static final double HEADER_ROWS_HEIGHT = 100.0;
}
