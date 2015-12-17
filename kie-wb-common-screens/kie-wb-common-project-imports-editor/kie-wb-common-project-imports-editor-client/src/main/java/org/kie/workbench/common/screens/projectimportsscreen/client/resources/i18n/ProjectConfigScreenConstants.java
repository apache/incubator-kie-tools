/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projectimportsscreen.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface ProjectConfigScreenConstants
        extends
        Messages {

    ProjectConfigScreenConstants INSTANCE = GWT.create( ProjectConfigScreenConstants.class );

    String ProjectModel();

    String StatefulKSessions();

    String StatelessKSessions();

    String EqualsBehavior();

    String EventProcessingMode();

    String Clock();

    String Realtime();

    String Pseudo();

    String AddKSession();

    String DeleteKSession();

    String Add();

    String AddFromRepository();

    String Delete();

    String MakeDefault();

    String Rename();

    String Name();

    String Identity();

    String Equality();

    String Stream();

    String Cloud();

    String PleaseSelectAKBase();

    String Ok();

    String Cancel();

    String PleaseSetAName();

    String PleaseSelectAKSession();

    String New();

    String Save();

    String SaveSuccessful( String fileName );

    String Build();

    String BuildSuccessful();

    String EnableKieProject();

    String KBases();

    String Line();

    String Column();

    String Text();

    String Level();

    String PomDotXml();

    String KModuleDotXml();

    String Problems();

    String FileName();

    String GroupID();

    String ArtifactID();

    String VersionID();

    String Dependencies();

    String PomDotXmlMetadata();

    String KModuleDotXmlMetadata();

    String ExternalImports();

}
