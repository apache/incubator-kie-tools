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

package org.kie.workbench.common.screens.projecteditor.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface ProjectEditorConstants
        extends
        Messages {

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

    String Compile();

    String BuildAndInstall();

    String BuildAndDeploy();

    String SaveBeforeBuildAndDeploy();

    String Building();

    String BuildSuccessful();

    String DeploySuccessful();

    String BuildFailed();

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

    String GroupIdExample();

    String GroupIdMoreInfo();

    String ArtifactID();

    String ArtifactIDExample();

    String ArtifactIDMoreInfo();

    String Version();

    String VersionExample();

    String VersionMoreInfo();

    String MoreInfo();

    String Dependencies();

    String PomDotXmlMetadata();

    String KModuleDotXmlMetadata();

    String newProjectDescription();

    String newPackageDescription();

    String NoRepositorySelectedPleaseSelectARepository();

    String XMLMarkIsNotAllowed();

    String GroupIdMissing();

    String ArtifactIdMissing();

    String VersionMissing();

    String ProjectScreen();

    String NewProject();

    String PleaseSelectAnItem();

    String ExternalDataObjects();

    String ExternalDataObjectsMetadata();

    String ProjectSettings();

    String ProjectGeneralSettings();

    String ProjectName();

    String ProjectNamePlaceHolder();

    String ProjectDescription();

    String ProjectDescriptionPlaceHolder();

    String GroupArtifactVersion();

    String Metadata();

    String Source();

    String KnowledgeBaseSettings();

    String Imports();

    String Categories();

    String DSL();

    String Enums();

    String DependenciesList();

    String KnowledgeBasesAndSessions();

    String BracketDefaultBracket();

    String Packages();

    String NewProjectWizard();

    String IncludedKnowledgeBases();

    String Stateful();

    String Stateless();

    String State();

    String WorkItemHandler();

    String Default();

    String Options();

    String ConsoleLogger();

    String FileLogger();

    String Interval();

    String WorkingMemoryEventListener();

    String AgendaEventListener();

    String ProcessEventListener();

    String Kind();

    String Type();

    String Close();

    String Listeners();

    String WorkItemHandlers();

    String InvalidPackageName( String packageName );

    String KnowledgeSessions();

    String ThereAlreadyExistAnItemWithTheGivenNamePleaseSelectAnotherName();

    String ProjectScreenWithName( String projectName );

    String RefreshProblemsPanel();

    String Refreshing();

    String ABuildIsAlreadyRunning();

    String XsdIDError();

    String NoDependencies();

    String EnterAGroupID();

    String EnterAnArtifactID();

    String EnterAVersion();

    String ExceptionPackageAlreadyExists0( final String packageName );

    String ProjectStructure();

    String InheritedFromAParentPOM();

    String BuildProcessStarted();

    String ContainerId();

    String ServerTemplate();

    String StartContainer();

    String ContainerIdAlreadyInUse();

    String FieldMandatory0( final String fieldName );

    String DeploymentDescriptor();

    String Deployments();

    String NoProjectSelected();

    String SearchDependencies();

    String AllPackagesIncluded();

    String PackagesNotIncluded();

    String WhiteList();

    String SomePackagesIncluded();

    String Repositories();

    String RepositoriesValidation();

    String RepositoriesValidationExplanationL1();

    String RepositoriesValidationExplanationL2();

    String RepositoryInclude();

    String Select();

    String DependencyIsMissingAVersion();

    String DependencyIsMissingAnArtifactId();

    String DependencyIsMissingAGroupId();

    String Dependency();

    String NoServersAvailableForProvisioning();

}
