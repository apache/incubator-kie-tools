/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.screens.factmodel.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * EnumEditor I18N constants
 */
public interface Constants
        extends
        Messages {

    public static final Constants INSTANCE = GWT.create( Constants.class );

    String OK();

    String InvalidModelName( final String name );

    String NameTakenForModel( final String name );

    String ModelNameChangeWarning();

    String Rename();

    String Delete();

    String Name();

    String DoesNotExtend();

    String CreatesCircularDependency( final String text );

    String TypeExtends();

    String AddField();

    String AddAnnotation();

    String AreYouSureYouWantToRemoveTheField0( final String name );

    String AreYouSureYouWantToRemoveTheAnnotation0( final String name );

    String MoveUp();

    String MoveDown();

    String RemoveThisFactType();

    String CannotDeleteADeclarationThatIsASuperType();

    String AreYouSureYouWantToRemoveThisFact();

    String AddNewFactType();

    String InvalidDataTypeName( final String dataType );

    String Type();

    String FieldNameAttribute();

    String chooseType();

    String newFactModelDescription();

}
