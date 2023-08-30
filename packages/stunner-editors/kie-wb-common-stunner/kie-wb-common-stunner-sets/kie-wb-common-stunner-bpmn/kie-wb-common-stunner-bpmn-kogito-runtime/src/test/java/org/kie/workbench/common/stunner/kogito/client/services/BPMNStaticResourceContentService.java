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


package org.kie.workbench.common.stunner.kogito.client.services;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import elemental2.promise.Promise;
import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.appformer.kogito.bridge.client.resource.interop.ResourceContentOptions;
import org.appformer.kogito.bridge.client.resource.interop.ResourceListOptions;
import org.uberfire.client.promise.Promises;

public class BPMNStaticResourceContentService implements ResourceContentService {

    static final String LOG_SERVICE_TASK_DATA_URI = "data:image/gif;base64,R0lGODlhEAAQAMQAAG+Fr3CFr3yRuIOSsYaUroidwIuWrI+ZqJGlx5WdpZugoKGknaeomK6slLKvkL21idSyaNq9fN3o+ODIj+Ps+evx+vP2+/f4+/n6/AAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABkALAAAAAAQABAAAAVlYCaOZEk+aIqa4oO9MPSwLvxGsvlcwYUglwluRnJYjkiko9SwBCy/guDZKDEq2GyWUVpUApXotLIoKSjodFpRSlACFDGAkigdJHg8Gn8oGSQBEnISBiUEeYh4BCUDjY6PAyySIyEAOw==";
    static final String EMAIL_SERVICE_TASK_DATA_URI = "data:image/gif;base64,R0lGODlhEAAQAPcAAAAAAIAAAACAAICAAAAAgIAAgACAgICAgMDAwP8AAAD/AP//AAAA/////wD//////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMwAAZgAAmQAAzAAA/wAzAAAzMwAzZgAzmQAzzAAz/wBmAABmMwBmZgBmmQBmzABm/wCZAACZMwCZZgCZmQCZzACZ/wDMAADMMwDMZgDMmQDMzADM/wD/AAD/MwD/ZgD/mQD/zAD//zMAADMAMzMAZjMAmTMAzDMA/zMzADMzMzMzZjMzmTMzzDMz/zNmADNmMzNmZjNmmTNmzDNm/zOZADOZMzOZZjOZmTOZzDOZ/zPMADPMMzPMZjPMmTPMzDPM/zP/ADP/MzP/ZjP/mTP/zDP//2YAAGYAM2YAZmYAmWYAzGYA/2YzAGYzM2YzZmYzmWYzzGYz/2ZmAGZmM2ZmZmZmmWZmzGZm/2aZAGaZM2aZZmaZmWaZzGaZ/2bMAGbMM2bMZmbMmWbMzGbM/2b/AGb/M2b/Zmb/mWb/zGb//5kAAJkAM5kAZpkAmZkAzJkA/5kzAJkzM5kzZpkzmZkzzJkz/5lmAJlmM5lmZplmmZlmzJlm/5mZAJmZM5mZZpmZmZmZzJmZ/5nMAJnMM5nMZpnMmZnMzJnM/5n/AJn/M5n/Zpn/mZn/zJn//8wAAMwAM8wAZswAmcwAzMwA/8wzAMwzM8wzZswzmcwzzMwz/8xmAMxmM8xmZsxmmcxmzMxm/8yZAMyZM8yZZsyZmcyZzMyZ/8zMAMzMM8zMZszMmczMzMzM/8z/AMz/M8z/Zsz/mcz/zMz///8AAP8AM/8AZv8Amf8AzP8A//8zAP8zM/8zZv8zmf8zzP8z//9mAP9mM/9mZv9mmf9mzP9m//+ZAP+ZM/+ZZv+Zmf+ZzP+Z///MAP/MM//MZv/Mmf/MzP/M////AP//M///Zv//mf//zP///yH5BAEAAA0ALAAAAAAQABAAQAhPABsIHEiwYAMADxICQKiQ4QMABx0mnDhxYcSFGDNmNMiRIEOJFRUefEixJEKIHVOqLKix5caTJSmePOhPocmKI0k+XGjzYcSYJlGuHNoxIAA7";
    static final String MILESTONE_SERVICE_TASK_DATA_URI = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAArklEQVR42mNQUVH5GyMnt+s+P/+zBwICTx/w8S0HsjMecHEZvWJg4GEgBIAG/NNQUXkO1HQbaMB/JPz1AS/vRiBOusvAwE+OARj4IR/f0ftcXP5PeXlFyDIAGd/j47v/iIfHjjgD+Pl/3efjOwF0QdwVBgaJUAYGZtwu4Of/B8TfgXj/fR6e+v8MDCzYvF7IwMCJYgDQSUcfsbKa7sehAW8gAvFjBnLBqAGjBgABAJ+4l3hcf8LlAAAAAElFTkSuQmCC";

    static final String DEFAULT_DECLARATIONS = "[\n" +
            "  [\n" +
            "    \"name\" : \"Email\",\n" +
            "    \"parameters\" : [\n" +
            "      \"From\" : new StringDataType(),\n" +
            "      \"To\" : new StringDataType(),\n" +
            "      \"Subject\" : new StringDataType(),\n" +
            "      \"Body\" : new StringDataType()\n" +
            "    ],\n" +
            "    \"displayName\" : \"Email\",\n" +
            "    \"icon\" : \"defaultemailicon.gif\"\n" +
            "  ],\n" +
            "\n" +
            "  [\n" +
            "    \"name\" : \"Log\",\n" +
            "    \"parameters\" : [\n" +
            "      \"Message\" : new StringDataType()\n" +
            "    ],\n" +
            "    \"displayName\" : \"Log\",\n" +
            "    \"icon\" : \"defaultlogicon.gif\"\n" +
            "  ],\n" +
            "\n" +
            "  [\n" +
            "     \"name\" : \"BusinessRuleTask\",\n" +
            "     \"parameters\" : [\n" +
            "       \"Language\" : new StringDataType(),\n" +
            "       \"KieSessionName\" : new StringDataType(),\n" +
            "       \"KieSessionType\" : new StringDataType()\n" +
            "     ],\n" +
            "     \"displayName\" : \"Business Rule Task\",\n" +
            "     \"icon\" : \"defaultbusinessrulesicon.png\",\n" +
            "     \"category\" : \"Decision tasks\"\n" +
            "   ],\n" +
            "\n" +
            "   [\n" +
            "     \"name\" : \"DecisionTask\",\n" +
            "     \"parameters\" : [\n" +
            "       \"Language\" : new StringDataType(),\n" +
            "       \"Namespace\" : new StringDataType(),\n" +
            "       \"Model\" : new StringDataType(),\n" +
            "       \"Decision\" : new StringDataType()\n" +
            "     ],\n" +
            "     \"displayName\" : \"Decision Task\",\n" +
            "     \"icon\" : \"defaultdecisionicon.png\",\n" +
            "     \"category\" : \"Decision tasks\"\n" +
            "   ]]";

    static final String ANOTHER_DECLARATION = "[\n" +
            "  [\n" +
            "    \"name\" : \"AnotherLog\",\n" +
            "    \"parameters\" : [\n" +
            "      \"Message\" : new StringDataType()\n" +
            "    ],\n" +
            "    \"displayName\" : \"AnotherLog\",\n" +
            "    \"icon\" : \"defaultlogicon.gif\"\n" +
            "  ]\n" +
            "]";

    private static final String PATTERN_ALL_WID = "*.wid";
    private static final String PATTERN_GLOBAL_WID = "global/*.wid";
    private static final Map<String, String> GLOBAL_WID_ENTRIES =
            new HashMap<String, String>() {{
                put("global/default.wid", DEFAULT_DECLARATIONS);
                put("global/test_empty_wid.wid", "[ [] ]");
                put("global/test_empty_wids.wid", "[[ ] ,[ ]]");
            }};
    private static final Map<String, String> ALL_WID_ENTRIES =
            new HashMap<String, String>() {{
                put("src/main/resources/org/test/test_empty_file.wid", "");
                put("src/main/resources/org/test/test_empty_list.wid", "[]");
                put("src/main/resources/org/test/another.wid", ANOTHER_DECLARATION);
            }};
    private static final Map<String, String> ICON_ENTRIES =
            new HashMap<String, String>() {{
                put("global/defaultemailicon.gif", EMAIL_SERVICE_TASK_DATA_URI);
                put("global/defaultlogicon.gif", LOG_SERVICE_TASK_DATA_URI);
                put("src/main/resources/org/test/defaultlogicon.gif", LOG_SERVICE_TASK_DATA_URI);
                put("global/defaultmilestoneicon.png", MILESTONE_SERVICE_TASK_DATA_URI);
            }};

    private final Promises promises;

    @Inject
    public BPMNStaticResourceContentService(final Promises promises) {
        this.promises = promises;
    }

    @Override
    public Promise<String> get(final String uri) {
        if (uri.endsWith(".wid")) {
            if (uri.startsWith("global")) {
                return promises.resolve(GLOBAL_WID_ENTRIES.getOrDefault(uri, ""));
            } else {
                return promises.resolve(ALL_WID_ENTRIES.getOrDefault(uri, ""));
            }
        }
        return promises.resolve(ICON_ENTRIES.getOrDefault(uri, ""));
    }

    @Override
    public Promise<String> get(final String uri,
                               final ResourceContentOptions options) {
        return get(uri);
    }

    @Override
    public Promise<String[]> list(final String pattern) {
        switch (pattern) {
            case PATTERN_GLOBAL_WID:
                return promises.resolve(GLOBAL_WID_ENTRIES.keySet().toArray(new String[0]));
            case PATTERN_ALL_WID:
                return promises.resolve(ALL_WID_ENTRIES.keySet().toArray(new String[0]));
            default:
                return promises.resolve(new String[0]);
        }
    }

    @Override
    public Promise<String[]> list(String pattern, ResourceListOptions options) {
        return list(pattern);
    }
}
