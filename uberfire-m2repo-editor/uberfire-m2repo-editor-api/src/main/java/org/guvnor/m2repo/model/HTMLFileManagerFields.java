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

package org.guvnor.m2repo.model;

/**
 * This is a central location for all form fields use in HTML forms for submission to the file servlet.
 * The all must be unique, of course.
 */
public class HTMLFileManagerFields {

    //Name for Upload Form control
    public static final String UPLOAD_FIELD_NAME_ATTACH = "fileUploadElement";

    //Upload field for GroupID
    public static final String GROUP_ID = "groupId";

    //Upload field for ArtifactID
    public static final String ARTIFACT_ID = "artifactId";

    //Upload field for Version
    public static final String VERSION_ID = "version";

    //Indicator for uploaded JAR contains no pom.xml or pom.properties
    public static final String UPLOAD_MISSING_POM = "MISSING_POM";

    //Indicator for uploaded pom.xml file could not be parsed
    public static final String UPLOAD_UNABLE_TO_PARSE_POM = "UNABLE_TO_PARSE_POM";

    //Indicator for upload being successful
    public static final String UPLOAD_OK = "OK";
}
