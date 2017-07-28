/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.home.model;

import org.uberfire.mvp.Command;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceType;

public class ModelUtils {

    public static HomeShortcut makeShortcut(final String iconCss,
                                            final String heading,
                                            final String subHeading,
                                            final Command onClickCommand) {
        return new HomeShortcut(iconCss,
                                heading,
                                subHeading,
                                onClickCommand,
                                null,
                                null,
                                null,
                                null);
    }

    public static HomeShortcut makeShortcut(final String iconCss,
                                            final String heading,
                                            final String subHeading,
                                            final Command onClickCommand,
                                            final String resourceId,
                                            final ResourceType resourceType) {
        return new HomeShortcut(iconCss,
                                heading,
                                subHeading,
                                onClickCommand,
                                null,
                                resourceId,
                                resourceType,
                                null);
    }

    public static HomeShortcut makeShortcut(final String iconCss,
                                            final String heading,
                                            final String subHeading,
                                            final Command onClickCommand,
                                            final String resourceId,
                                            final ResourceType resourceType,
                                            final ResourceAction resourceAction) {
        return new HomeShortcut(iconCss,
                                heading,
                                subHeading,
                                onClickCommand,
                                null,
                                resourceId,
                                resourceType,
                                resourceAction);
    }

    public static HomeShortcut makeShortcut(final String iconCss,
                                            final String heading,
                                            final String subHeading,
                                            final Command onClickCommand,
                                            final String permission) {
        return new HomeShortcut(iconCss,
                                heading,
                                subHeading,
                                onClickCommand,
                                permission,
                                null,
                                null,
                                null);
    }
}
