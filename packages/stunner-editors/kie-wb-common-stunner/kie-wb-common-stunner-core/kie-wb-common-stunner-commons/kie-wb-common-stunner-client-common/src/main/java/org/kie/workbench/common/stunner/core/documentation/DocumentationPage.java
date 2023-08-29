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


package org.kie.workbench.common.stunner.core.documentation;

import java.util.function.Supplier;

import org.uberfire.client.views.pfly.multipage.PageImpl;
import org.uberfire.mvp.Command;

public class DocumentationPage extends PageImpl {

    private DocumentationView view;
    private Command onFocus;

    public DocumentationPage(DocumentationView view, String label, Command onFocus, Supplier<Boolean> isSelected) {
        super(view, label);
        this.view = view;
        this.onFocus = onFocus;
        view.setIsSelected(isSelected);
    }

    @Override
    public void onFocus() {
        onFocus.execute();
        view.refresh();
    }

    public DocumentationView getDocumentationView() {
        return view;
    }
}