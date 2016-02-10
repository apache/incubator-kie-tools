/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.plugin.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface CommonConstants
        extends
        Messages {

    public static final CommonConstants INSTANCE = GWT.create( CommonConstants.class );

    String SavePerspective();

    String PerspectiveName();

    String PerspectiveNameHolder();

    String TagName();

    String AddTag();

    String TagLabel();

    String TagNameHolder();

    String InvalidPerspectiveName();

    String InvalidTagName();

    String InvalidParameterName();

    String InvalidActivityID();

    String InvalidMenuLabel();

    String EmptyTagName();

    String EmptyParameterName();

    String EmptyActivityID();

    String EmptyMenuLabel();

    String DuplicatedMenuLabel();

    String DuplicateParameterName();

    String LoadPerspective();

    String EditHtml();

    String EditComponent();

    String Add();

    String AddNewParameter();

    String ParamKey();

    String ParamKeyPlaceHolder();

    String ParamValue();

    String ParamValuePlaceHolder();

    String InvalidGridConfiguration();

    String MenusNoMenuItems();

    String MenusMoveUpHint();

    String MenusMoveDownHint();

    String MenusActivityID();

    String MenusLabel();

    String MenusDeleteHint();

    String MenusDelete();

    String DragAndDrop();

    String NewPerspective();

    String NewPerspectivePopUpTitle();

    String NewPerspectiveLayoutPopUpTitle();

    String NewScreen();

    String NewScreenPopUpTitle();

    String NewEditor();

    String NewEditorPopUpTitle();

    String NewSplashScreen();

    String NewSplashScreenPopUpTitle();

    String NewDynamicMenu();

    String NewDynamicMenuPopUpTitle();

    String HTMLComponent();

    String HTMLplaceHolder();

    String ScreenComponent();

    String ScreenConfiguration();

    String PlaceName();
}