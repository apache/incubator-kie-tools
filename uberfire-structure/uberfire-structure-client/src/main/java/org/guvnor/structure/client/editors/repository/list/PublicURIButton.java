/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.client.editors.repository.list;

import com.google.gwt.dom.client.Style;
import org.guvnor.structure.repositories.PublicURI;
import org.gwtbootstrap3.client.ui.Button;

public class PublicURIButton
        extends Button {

    public PublicURIButton(final PublicURI protocol,
                           final boolean isNotFirst) {
        super(getProtocol(protocol));
        getElement().getStyle().setMarginLeft(5,
                                              Style.Unit.PX);

        if (isNotFirst) {
            getElement().getStyle().setPaddingLeft(5,
                                                   Style.Unit.PX);
        }
    }

    private static String getProtocol(final PublicURI publicURI) {
        return publicURI.getProtocol() == null ? "default" : publicURI.getProtocol();
    }
}
