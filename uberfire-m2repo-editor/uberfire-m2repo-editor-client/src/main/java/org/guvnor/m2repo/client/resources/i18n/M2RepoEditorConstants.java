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

package org.guvnor.m2repo.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * EnumEditor I18N constants
 */
public interface M2RepoEditorConstants
        extends
        Messages {

    public static final M2RepoEditorConstants INSTANCE = GWT.create(M2RepoEditorConstants.class);

    String OK();

    String InvalidModelName(final String name);

    String NameTakenForModel(final String name);

    String ModelNameChangeWarning();

    String Rename();

    String Delete();

    String Name();

    String DoesNotExtend();

    String CreatesCircularDependency(final String text);

    String TypeExtends();

    String AddField();

    String AddAnnotation();

    String AreYouSureYouWantToRemoveTheField0(final String name);

    String AreYouSureYouWantToRemoveTheAnnotation0(final String name);

    String MoveUp();

    String MoveDown();

    String RemoveThisFactType();

    String CannotDeleteADeclarationThatIsASuperType();

    String AreYouSureYouWantToRemoveThisFact();

    String AddNewFactType();

    String InvalidDataTypeName(final String dataType);

    String Type();

    String FieldNameAttribute();

    String chooseType();

    String WholeNumberInteger();

    String TrueOrFalse();

    String Text();

    String Date();

    String DecimalNumber();

    String AreYouSureYouWantToDeleteTheseItems();

    String JarDetails();

    String M2RepositoryContent();

    String Path();

    String GAV();

    String LastModified();

    String Open();

    String Download();

    String NoArtifactAvailable();

    String Downloading();

    String ArtifactUpload();

    String Cancel();

    String SelectFileUpload();

    String UploadedSuccessfully();

    String RefreshedSuccessfully();

    String InvalidJarNotPom();

    String UploadFailed();

    String UnsupportedFileType();

    String InvalidPom();

    String Uploading();

    String Upload();

    String Refresh();
}
