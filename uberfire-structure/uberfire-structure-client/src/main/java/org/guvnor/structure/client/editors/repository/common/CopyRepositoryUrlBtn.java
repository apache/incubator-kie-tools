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

package org.guvnor.structure.client.editors.repository.common;

import org.guvnor.structure.client.resources.i18n.CommonConstants;
import org.gwtbootstrap3.client.ui.InputGroupAddon;

public class CopyRepositoryUrlBtn extends InputGroupAddon {

    public void init(boolean isViewButton,
                     String uri,
                     String text) {
        setDataClipboardTargetAttribute(uri);
        setDataClipboardTextAttribute(text);
        setButtonAttribute(isViewButton,
                           uri);
        setCopyRepositoryUrlTitle();
    }

    protected void setDataClipboardTargetAttribute(String value) {
        getElement().setAttribute("data-clipboard-target",
                                  value);
    }

    protected void setDataClipboardTextAttribute(String value) {
        getElement().setAttribute("data-clipboard-text",
                                  value);
    }

    protected void setButtonAttribute(boolean isViewButton,
                                      String value) {
        String idBase = isViewButton ? "view-button-" : "button-";
        getElement().setId(idBase + value);
    }

    protected void setCopyRepositoryUrlTitle() {
        getElement().setTitle(CommonConstants.INSTANCE.copyRepositoryUrl());
    }
}
