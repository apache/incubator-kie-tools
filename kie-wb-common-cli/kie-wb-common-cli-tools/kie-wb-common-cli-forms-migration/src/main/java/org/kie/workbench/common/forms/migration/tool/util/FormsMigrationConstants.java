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

package org.kie.workbench.common.forms.migration.tool.util;

public interface FormsMigrationConstants {

    String DEFAULT_LANG = "en";

    String DATA_HOLDER_TYPE_BASIC = "basicType";

    String INFO = "[INFO] ";

    String WARNING = "[WARNING] ";

    String ERROR = "[ERROR] ";

    String BPMN_FORMS_SUFFIX = "-taskform";

    String LEGACY_FOMRS_EXTENSION = "form";

    String NEW_FOMRS_EXTENSION = "frm";

    String BPMN_PARSING_ERROR = "%s Cannot read process %s: something wrong happened reading the process file. The " +
            "migration will continue but forms related to this process won't be migrated. If you want to migrate its forms" +
            " please fix the process file and start the migration again\n";

    String TASK_FORM_VARIABLE = "TaskName";

    String HTML_COMPONENT = "org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent";

    String HTML_CODE_PARAMETER = "HTML_CODE";
    
    String UNSUPORTED_FIELD_HTML_TEMPLATE = "<div class='alert'><span class='pficon pficon-warning-triangle-o'></span>" +
            "Impossible to migrate field <strong>%s</strong> from the old jBPM Form: It has an unsupported type <strong>%s</strong>. " +
            "Please try to add it back again to the form using the right Form Control</div>";
}
