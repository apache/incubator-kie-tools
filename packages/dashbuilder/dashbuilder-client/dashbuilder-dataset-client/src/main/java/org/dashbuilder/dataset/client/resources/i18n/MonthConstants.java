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

public interface MonthConstants extends ConstantsWithLookup {

    public static final MonthConstants INSTANCE = GWT.create( MonthConstants.class );

    String JANUARY();
    String FEBRUARY();
    String MARCH();
    String APRIL();
    String MAY();
    String JUNE();
    String JULY();
    String AUGUST();
    String SEPTEMBER();
    String OCTOBER();
    String NOVEMBER();
    String DECEMBER();
}
