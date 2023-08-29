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


package org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.mvp.UberElement;

/**
 * Renders a label for a form field
 */
public interface FieldLabelView extends UberElement<FieldLabelView.Presenter> {

    /**
     * Renders a HTML label for the given inputId
     * @param inputId The id of the HTML input the label's for
     * @param label The text to display on the label
     * @param required Determines if the label should have the required mark "*"
     * @param helpMessage The help message to display
     */
    void renderForInputId(String inputId,
                          String label,
                          boolean required,
                          String helpMessage);

    /**
     * Renders a HTML label for the given form widget and includes it on the label DOM
     * @param isWidget The form widget to add to the label.
     * @param label The text to display on the label
     * @param required Determines if the label should have the required mark "*"
     * @param helpMessage The help message to display
     */
    void renderForInput(IsWidget isWidget,
                        String label,
                        boolean required,
                        String helpMessage);

    void setRequired(boolean required);

    interface Presenter {

    }
}
