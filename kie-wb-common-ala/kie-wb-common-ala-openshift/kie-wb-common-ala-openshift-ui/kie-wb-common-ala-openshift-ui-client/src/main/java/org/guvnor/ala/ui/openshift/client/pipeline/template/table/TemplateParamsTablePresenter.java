/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.ui.openshift.client.pipeline.template.table;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.guvnor.ala.ui.openshift.model.TemplateParam;
import org.uberfire.client.mvp.UberElement;

public class TemplateParamsTablePresenter {

    public interface View
            extends UberElement<TemplateParamsTablePresenter> {

        HasData<TemplateParam> getDisplay();

        void redraw();
    }

    public interface ParamChangeHandler {

        void onParamChange(String paramName,
                           String newValue,
                           String oldValue);
    }

    private final View view;

    private ListDataProvider<TemplateParam> dataProvider = createDataProvider();

    private ParamChangeHandler paramChangeHandler;

    @Inject
    public TemplateParamsTablePresenter(final View view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        dataProvider.addDataDisplay(view.getDisplay());
    }

    public void setItems(final List<TemplateParam> items) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(items);
        dataProvider.flush();
    }

    public void clear() {
        dataProvider.getList().clear();
        dataProvider.flush();
    }

    public View getView() {
        return view;
    }

    public void setParamChangeHandler(final ParamChangeHandler paramChangeHandler) {
        this.paramChangeHandler = paramChangeHandler;
    }

    protected void onParamChange(final String paramName,
                                 final String newValue,
                                 final String oldValue) {
        if (paramChangeHandler != null) {
            paramChangeHandler.onParamChange(paramName,
                                             newValue,
                                             oldValue);
        }
    }

    /**
     * for testing purposes.
     */
    ListDataProvider<TemplateParam> createDataProvider() {
        return new ListDataProvider<>();
    }
}
