/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client.components.rows;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.infra.DndDataJSONConverter;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class EmptyDropRow {

    private final View view;
    private String id;
    private DndDataJSONConverter converter = new DndDataJSONConverter();
    private ParameterizedCommand<RowDrop> dropCommand;
    private LayoutDragComponentHelper layoutDragComponentHelper;
    @Inject
    public EmptyDropRow(final View view,
                        LayoutDragComponentHelper layoutDragComponentHelper) {
        this.view = view;
        this.layoutDragComponentHelper = layoutDragComponentHelper;
    }

    @PostConstruct
    public void post() {
        view.init(this);
    }

    public void init(ParameterizedCommand<RowDrop> dropCommand,
                     String titleText,
                     String subTitleText) {
        this.dropCommand = dropCommand;
        view.setupText(titleText,
                       subTitleText);
    }

    public void drop(String dropData) {
        LayoutDragComponent component = extractComponent(dropData);
        if (thereIsAComponent(component)) {
            dropCommand.execute(new RowDrop(layoutDragComponentHelper.getLayoutComponent(component),
                                            id,
                                            RowDrop.Orientation.AFTER));
        }
    }

    private LayoutDragComponent extractComponent(String dropData) {
        return converter
                .readJSONDragComponent(dropData);
    }

    private boolean thereIsAComponent(LayoutDragComponent component) {
        return component != null;
    }

    public UberElement<EmptyDropRow> getView() {
        return view;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public interface View extends UberElement<EmptyDropRow> {

        void setupText(String titleText,
                       String subTitleText);
    }
}