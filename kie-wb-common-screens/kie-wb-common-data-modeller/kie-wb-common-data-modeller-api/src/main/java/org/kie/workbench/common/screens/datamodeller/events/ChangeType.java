/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.events;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public enum ChangeType {

    DATA_MODEL_STATUS_CHANGE,
    OBJECT_NAME_CHANGE,
    CLASS_NAME_CHANGE,
    SUPER_CLASS_NAME_CHANGE,
    PACKAGE_NAME_CHANGE,
    FIELD_NAME_CHANGE,
    FIELD_TYPE_CHANGE,
    FIELD_ANNOTATION_VALUE_CHANGE,
    FIELD_ANNOTATION_ADD_CHANGE,
    FIELD_ANNOTATION_REMOVE_CHANGE,
    TYPE_ANNOTATION_VALUE_CHANGE,
    TYPE_ANNOTATION_ADD_CHANGE,
    TYPE_ANNOTATION_REMOVE_CHANGE

}
