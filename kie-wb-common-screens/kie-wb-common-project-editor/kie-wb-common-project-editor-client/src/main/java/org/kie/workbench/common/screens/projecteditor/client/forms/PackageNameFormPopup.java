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

package org.kie.workbench.common.screens.projecteditor.client.forms;

import org.kie.workbench.common.widgets.client.popups.text.FormPopup;
import org.kie.workbench.common.widgets.client.popups.text.FormPopupView;

import javax.inject.Inject;
import java.util.List;

public class PackageNameFormPopup
        extends FormPopup {

    private final PackageNameFormPopupView view;

    @Inject
    public PackageNameFormPopup(PackageNameFormPopupView view) {
        super(view);

        this.view = view;
    }

    public void setPackageNames(List<String> packageNames) {
        for(String packageName:packageNames){
            view.addItem(packageName);
        }
    }
}
