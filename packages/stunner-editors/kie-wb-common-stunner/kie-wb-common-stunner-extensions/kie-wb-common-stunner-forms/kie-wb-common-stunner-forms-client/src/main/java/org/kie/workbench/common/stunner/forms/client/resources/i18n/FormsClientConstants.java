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


package org.kie.workbench.common.stunner.forms.client.resources.i18n;

import org.jboss.errai.ui.shared.api.annotations.TranslationKey;

public interface FormsClientConstants {

    @TranslationKey(defaultValue = "[{0}] Forms Generation")
    String FormsNotificationTitle = "forms.notificationTitle";

    @TranslationKey(defaultValue = "Cannot generate forms, there are no User Tasks selected")
    String FormsNoItemsSelectedForGeneration = "forms.noItemsSelectedForGeneration";

    @TranslationKey(defaultValue = "Forms generation completed successfully for [{0}]")
    String FormsGenerationSuccess = "forms.generationSuccess";

    @TranslationKey(defaultValue = "Forms generation failed for [{0}]")
    String FormsGenerationFailure = "forms.generationFailure";

    @TranslationKey(defaultValue = "Generate forms")
    String FormsGenerateTaskForm = "forms.generateTaskForm";

    @TranslationKey(defaultValue = "Properties")
    String FormsPropertiesDockTitle = "docks.forms.title";
}
