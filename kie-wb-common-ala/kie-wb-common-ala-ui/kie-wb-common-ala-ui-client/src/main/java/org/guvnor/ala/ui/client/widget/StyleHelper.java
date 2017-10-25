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

package org.guvnor.ala.ui.client.widget;

import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.HTMLElement;

public class StyleHelper {

    public static void setFormStatus(final HTMLElement form,
                                     final FormStatus status) {
        if (status.equals(FormStatus.ERROR)) {
            DOMUtil.addUniqueEnumStyleName(form,
                                           ValidationState.class,
                                           ValidationState.ERROR);
        } else {
            DOMUtil.addUniqueEnumStyleName(form,
                                           ValidationState.class,
                                           ValidationState.NONE);
        }
    }
}
