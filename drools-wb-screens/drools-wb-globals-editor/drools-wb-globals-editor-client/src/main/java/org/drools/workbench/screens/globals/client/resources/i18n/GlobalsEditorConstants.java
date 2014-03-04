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

package org.drools.workbench.screens.globals.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * Globals Editor I18N constants
 */
public interface GlobalsEditorConstants
        extends
        Messages {

    public static final GlobalsEditorConstants INSTANCE = GWT.create( GlobalsEditorConstants.class );

    String globalsEditorTitle0( String resource );

    String globalsEditorReadOnlyTitle0( String resource );

    String newGlobalDescription();

    String noGlobalsDefined();

    String alias();

    String className();

    String add();

    String remove();

    String addGlobalPopupTitle();

    String OK();

    String cancel();

    String aliasIsMandatory();

    String classNameIsMandatory();

    String promptForRemovalOfGlobal0( String alias );

    String globalsResourceTypeDescription();
}
