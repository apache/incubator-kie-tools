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

package org.uberfire.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * Workbench I18N constants
 */
public interface WorkbenchConstants
        extends
        Messages {

    WorkbenchConstants INSTANCE = GWT.create(WorkbenchConstants.class);

    String maximizePanel();

    String minimizePanel();

    String closePanel();

    String selectView();

    String expandToolbar();

    String collapseToolbar();

    String showSplashHelp();

    String lockHint();

    String lockOwnedHint();

    String lockError();

    String lockedMessage(String lockedBy);

    String splashScreenNoneAvailable();

    String switchToDefaultView();

    String switchToCompactView();

    String closingWindowMessage();
}
