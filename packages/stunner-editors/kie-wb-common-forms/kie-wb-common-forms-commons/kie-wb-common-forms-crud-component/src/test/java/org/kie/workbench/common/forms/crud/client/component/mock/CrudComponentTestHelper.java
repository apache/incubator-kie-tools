/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.crud.client.component.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import org.kie.workbench.common.forms.crud.client.component.CrudActionsHelper;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.IsFormView;
import org.uberfire.ext.widgets.table.client.ColumnMeta;

public class CrudComponentTestHelper<FORM_MODEL> implements CrudActionsHelper<CrudModel> {

    private boolean embeddedForms = true;
    private List<CrudModel> models;

    private AsyncDataProvider<CrudModel> dataProvider;

    private IsFormView<FORM_MODEL> formView;

    public CrudComponentTestHelper(IsFormView<FORM_MODEL> formView,
                                   List<CrudModel> listModels) {
        this.formView = formView;
        this.models = listModels;
        dataProvider = new AsyncDataProvider<CrudModel>() {
            @Override
            protected void onRangeChanged(HasData<CrudModel> hasData) {
                if (models != null) {
                    updateRowCount(models.size(),
                                   true);
                    updateRowData(0,
                                  models);
                } else {
                    updateRowCount(0,
                                   true);
                    updateRowData(0,
                                  new ArrayList<CrudModel>());
                }
            }
        };
    }

    public void setEmbeddedForms(boolean embeddedForms) {
        this.embeddedForms = embeddedForms;
    }

    @Override
    public int getPageSize() {
        return 5;
    }

    @Override
    public boolean showEmbeddedForms() {
        return embeddedForms;
    }

    @Override
    public boolean isAllowCreate() {
        return true;
    }

    @Override
    public boolean isAllowEdit() {
        return true;
    }

    @Override
    public boolean isAllowDelete() {
        return true;
    }

    @Override
    public List<ColumnMeta<CrudModel>> getGridColumns() {
        List<ColumnMeta<CrudModel>> metas = new ArrayList<>();

        ColumnMeta<CrudModel> columnMeta = new ColumnMeta<>(new TextColumn<CrudModel>() {
            @Override
            public String getValue(CrudModel model) {
                if (model.getName() == null) {
                    return "";
                }
                return String.valueOf(model.getName());
            }
        },
                                                            "Name");

        metas.add(columnMeta);

        columnMeta = new ColumnMeta<>(new TextColumn<CrudModel>() {
            @Override
            public String getValue(CrudModel model) {
                if (model.getLastName() == null) {
                    return "";
                }
                return String.valueOf(model.getLastName());
            }
        },
                                      "Last Name");

        metas.add(columnMeta);

        columnMeta = new ColumnMeta<>(new TextColumn<CrudModel>() {
            @Override
            public String getValue(CrudModel model) {
                if (model.getBirthday() == null) {
                    return "";
                }
                return String.valueOf(model.getBirthday());
            }
        },
                                      "Birthday");

        metas.add(columnMeta);

        return metas;
    }

    @Override
    public AsyncDataProvider<CrudModel> getDataProvider() {
        return dataProvider;
    }

    @Override
    public void createInstance() {
        models.add(new CrudModel("Ned",
                                 "Stark",
                                 new Date()));
    }

    @Override
    public void deleteInstance(int index) {
        if (index != -1 && index < models.size()) {
            models.remove(index);
        }
    }

    @Override
    public void editInstance(int index) {
        // TODO Auto-generated method stub

    }
}
