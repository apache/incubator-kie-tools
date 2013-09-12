/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.widgets;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.uberfire.mvp.Command;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PackageSelector extends Composite {

    interface PackageSelectorUIBinder
            extends UiBinder<Widget, PackageSelector> {

    }

    @UiField
    ListBox packageList;

    @UiField
    Icon newPackage;

    @Inject
    ValidatorService validatorService;

    @Inject
    NewPackagePopup newPackagePopup;

    private static PackageSelectorUIBinder uiBinder = GWT.create(PackageSelectorUIBinder.class);

    public static final String NOT_SELECTED = "NOT_SELECTED";
    public static final String NOT_SELECTED_DESC = "<default>";
    public static final String DEFAULT_PACKAGE = "defaultpkg";

    private DataObjectTO dataObject;

    private DataModelerContext context;

    public PackageSelector() {
        initWidget(uiBinder.createAndBindUi(this));
        newPackage.sinkEvents(Event.ONCLICK);
        newPackage.addHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                newPackagePopup.show();
            }
        }, ClickEvent.getType());
        packageList.addItem(NOT_SELECTED_DESC, NOT_SELECTED);
    }

    @PostConstruct
    private void init() {
        Command command = new Command() {
            @Override
            public void execute() {
                String newPackage = newPackagePopup.getPackageName();
                processNewPackage(newPackage);
            }
        };
        newPackagePopup.setAfterAddCommand(command);
    }

    private void processNewPackage(String newPackageName) {
        if (newPackageName != null && !"".equals(newPackageName.trim())) {
            boolean exists = false;
            newPackageName = newPackageName.trim();
            int count = packageList.getItemCount();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    if ((exists = newPackageName.equals(packageList.getValue(i)))) break;
                }
            }
            if (exists) {
                packageList.setSelectedValue(newPackageName);
            } else {
                packageList.addItem(newPackageName, newPackageName);
                packageList.setSelectedValue(newPackageName);
                DomEvent.fireNativeEvent(Document.get().createChangeEvent(), packageList);
            }
            if (context != null) {
                context.appendPackage(newPackageName.trim());
            }
        }
    }

    public void enableCreatePackage(boolean enable) {
        newPackage.setVisible(enable);
    }

    public ListBox getPackageList() {
        return packageList;
    }

    public DataModelerContext getContext() {
        return context;
    }

    public void setContext(DataModelerContext context) {
        this.context = context;
        initList();
    }

    private DataModelTO getDataModel() {
        return getContext() != null ? getContext().getDataModel() : null;
    }

    public DataObjectTO getDataObject() {
        return dataObject;
    }

    public void setDataObject(DataObjectTO dataObject) {
        this.dataObject = dataObject;
        initList();

        if (dataObject != null && dataObject.getPackageName() != null && !"".equals(dataObject.getPackageName())) {
            packageList.setSelectedValue(dataObject.getPackageName());
        } else {
            packageList.setSelectedValue(NOT_SELECTED);
        }
    }

    private void initList() {
        packageList.clear();
        List<String> packageNames = new ArrayList<String>();

        if (context != null && context.getCurrentProjectPackages() != null) {
            for (String packageName : context.getCurrentProjectPackages()) {
                packageNames.add(packageName);
            }

        }
        /* WM old processing
        if (getDataModel() != null) {
            String packageName;
            for (String className : getContext().getHelper().getClassList()) {
                packageName = DataModelerUtils.getInstance().extractPackageName(className);
                if (packageName != null && !DEFAULT_PACKAGE.equals(packageName) && !packageNames.contains(packageName)) {
                    packageNames.add(packageName);
                }
            }
        }
        */

        Collections.sort(packageNames);
        packageList.addItem(NOT_SELECTED_DESC, NOT_SELECTED);
        for (String packageName : packageNames) {
            packageList.addItem(packageName, packageName);
        }
    }

}