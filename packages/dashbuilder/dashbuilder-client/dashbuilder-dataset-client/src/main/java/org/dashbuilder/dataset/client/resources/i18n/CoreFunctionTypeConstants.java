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
package org.dashbuilder.dataset.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;

public interface CoreFunctionTypeConstants extends ConstantsWithLookup {

    public static final CoreFunctionTypeConstants INSTANCE = GWT.create( CoreFunctionTypeConstants.class );

    String IS_NULL();
    String NOT_NULL();
    String EQUALS_TO();
    String NOT_EQUALS_TO();
    String LIKE_TO();
    String GREATER_THAN();
    String GREATER_OR_EQUALS_TO();
    String LOWER_THAN();
    String LOWER_OR_EQUALS_TO();
    String BETWEEN();
    String TIME_FRAME();
    String IN();
    String NOT_IN();
}
