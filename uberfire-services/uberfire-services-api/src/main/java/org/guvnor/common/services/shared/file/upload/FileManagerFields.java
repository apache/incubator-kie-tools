/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.common.services.shared.file.upload;

/**
 * This is a central location for all form fields use in HTML forms for submission to the file servlet.
 * The all must be unique, of course.
 */
public class FileManagerFields {

    public static final String UPLOAD_FIELD_NAME_ATTACH = "fileUploadElement";
    public static final String FORM_FIELD_PATH = "attachmentPath";
    public static final String FORM_FIELD_NAME = "fileName";
    public static final String FORM_FIELD_FULL_PATH = "attachmentFullPath";
    public static final String FORM_FIELD_OPERATION = "operation";
}
