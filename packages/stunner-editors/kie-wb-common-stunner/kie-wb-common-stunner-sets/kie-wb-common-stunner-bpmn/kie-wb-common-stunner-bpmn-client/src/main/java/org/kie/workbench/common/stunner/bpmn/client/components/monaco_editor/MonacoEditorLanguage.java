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


package org.kie.workbench.common.stunner.bpmn.client.components.monaco_editor;

public class MonacoEditorLanguage {

    static final String LANG_JAVA = "java";
    static final String TITLE_JAVA = "Java";
    static final String LANG_MVEL = "mvel";
    static final String TITLE_MVEL = "MVEL";
    static final String LANG_DROOLS = "drools";
    static final String TITLE_DROOLS = "DROOLS";
    static final String LANG_FEEL = "feel";
    static final String TITLE_FEEL = "FEEL";
    static final String JAVA_MODULE = "vs/basic-languages/java/java";

    public static MonacoEditorLanguage JAVA =
            new MonacoEditorLanguage(LANG_JAVA,
                                     TITLE_JAVA,
                                     LANG_JAVA,
                                     new String[]{JAVA_MODULE});

    // Let's assume use of Java syntax for MVEL.
    public static MonacoEditorLanguage MVEL =
            new MonacoEditorLanguage(LANG_MVEL,
                                     TITLE_MVEL,
                                     LANG_JAVA,
                                     new String[]{JAVA_MODULE});

    // Let's assume use of Java syntax for DROOLS.
    public static MonacoEditorLanguage DROOLS =
            new MonacoEditorLanguage(LANG_DROOLS,
                                     TITLE_DROOLS,
                                     LANG_JAVA,
                                     new String[]{JAVA_MODULE});

    public static MonacoEditorLanguage FEEL =
            new MonacoEditorLanguage(LANG_FEEL,
                                     TITLE_FEEL,
                                     LANG_FEEL,
                                     new String[0]);

    private String id;
    private String title;
    private String code;
    private String[] modules;
    private MonacoEditorOptions options;

    public MonacoEditorLanguage() {
    }

    public MonacoEditorLanguage(String id, String title, String code, String[] modules) {
        this(id, title, code, modules, MonacoEditorOptions.buildDefaultOptions(code));
    }

    public MonacoEditorLanguage(String id, String title, String code, String[] modules, MonacoEditorOptions options) {
        this.id = id;
        this.title = title;
        this.code = code;
        this.modules = modules;
        this.options = options;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLanguageCode() {
        return code;
    }

    public String[] getModules() {
        return modules;
    }

    public MonacoEditorOptions buildOptions() {
        return options.copy();
    }
}
