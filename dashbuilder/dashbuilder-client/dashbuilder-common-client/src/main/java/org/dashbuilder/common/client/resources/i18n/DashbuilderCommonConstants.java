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
package org.dashbuilder.common.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;

public interface DashbuilderCommonConstants extends ConstantsWithLookup {

    DashbuilderCommonConstants INSTANCE = GWT.create( DashbuilderCommonConstants.class );

    String add();
    String remove();
    String noData();
    String key();
    String value();
    String actions();
    String newValue();
    String currentFilePath();
    String uploadSuccessful();
    String uploadFailed();
    String uploadFailedAlreadyExists();
    String clearAll();

}
