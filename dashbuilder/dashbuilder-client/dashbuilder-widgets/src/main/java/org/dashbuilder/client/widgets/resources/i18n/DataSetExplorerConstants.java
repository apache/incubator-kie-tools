/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.widgets.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;

/**
 * <p>Data Set Explorer constants.</p>
 *
 * @since 0.3.0 
 */
public interface DataSetExplorerConstants extends ConstantsWithLookup {

    DataSetExplorerConstants INSTANCE = GWT.create( DataSetExplorerConstants.class );

    String title();
    String newDataSet();
    String noDataSets();
    String cache();
    String push();
    String refresh();
    String edit();
    String delete();
    String bean();
    String csv();
    String sql();
    String el();
    String enabled();
    String disabled();
    String bytes();
    String rows();
    String currentStatus();
    String currentSize();
    String notFound();
    String loading();
    String error();
    String type();
    String message();
    String cause();
    String ok();
}
