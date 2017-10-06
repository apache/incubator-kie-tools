/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PermissionTreeI18NImpl implements PermissionTreeI18n {

    @Override
    public String perspectivesNodeName() {
        return PermissionTreeConstants.INSTANCE.perspectivesNodeName();
    }

    @Override
    public String perspectivesNodeHelp() {
        return PermissionTreeConstants.INSTANCE.perspectivesNodeHelp();
    }

    @Override
    public String perspectiveResourceName() {
        return PermissionTreeConstants.INSTANCE.perspectiveResourceName();
    }

    @Override
    public String perspectiveCreate() {
        return PermissionTreeConstants.INSTANCE.perspectiveCreate();
    }

    @Override
    public String perspectiveRead() {
        return PermissionTreeConstants.INSTANCE.perspectiveRead();
    }

    @Override
    public String perspectiveUpdate() {
        return PermissionTreeConstants.INSTANCE.perspectiveUpdate();
    }

    @Override
    public String perspectiveDelete() {
        return PermissionTreeConstants.INSTANCE.perspectiveDelete();
    }

    @Override
    public String editorsNodeName() {
        return PermissionTreeConstants.INSTANCE.editorsNodeName();
    }

    @Override
    public String editorsNodeHelp() {
        return PermissionTreeConstants.INSTANCE.editorsNodeHelp();
    }

    @Override
    public String editorResourceName() {
        return PermissionTreeConstants.INSTANCE.editorResourceName();
    }

    @Override
    public String editorRead() {
        return PermissionTreeConstants.INSTANCE.editorRead();
    }
}