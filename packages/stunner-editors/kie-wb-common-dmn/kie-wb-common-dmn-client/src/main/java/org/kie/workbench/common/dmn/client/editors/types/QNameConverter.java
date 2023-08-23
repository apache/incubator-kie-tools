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

package org.kie.workbench.common.dmn.client.editors.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.Dependent;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

@Dependent
public class QNameConverter {

    private static final String QNAME_URI = "@1";
    private static final String QNAME_LOCALPART = "@2";
    private static final String QNAME_PREFIX = "@3";
    private static final String QNAME_ENCODING = "[" + QNAME_URI + "][" + QNAME_LOCALPART + "][" + QNAME_PREFIX + "]";

    protected static final String QNAME_DECODING_PATTERN = "^\\[(.*?)\\]\\[(.*?)\\]\\[(.*?)\\]$";

    private DMNModelInstrumentedBase dmnModel;

    public QNameConverter() {
        //CDI proxy
    }

    public void setDMNModel(final DMNModelInstrumentedBase dmnModel) {
        this.dmnModel = dmnModel;
    }

    public QName toModelValue(final String componentValue) {
        //The componentValue is an encoded QName. See QNAME_ENCODING. Convert back to a QName.
        try {
            final List<String> matches = getRegexGroups(componentValue);
            if (matches.size() == 4) {
                final String namespace = matches.get(1);
                final String localPart = matches.get(2);
                final String prefix = matches.get(3);

                return new QName(namespace,
                                 localPart,
                                 prefix);
            }
        } catch (IllegalStateException ise) {
            //Swallow; as we throw an IllegalArgumentException when the match was unexpected
        }
        throw new IllegalArgumentException("Encoded form '" + componentValue + "' did not match '" + QNAME_DECODING_PATTERN + "'. Unable to convert to Model value.");
    }

    // Delegate RegEx handling to method to allow overriding RegEx implementation in Unit Tests.
    // Gwt provides its own RegEx engine for which the API is similar but incompatible with Java's RegEx.
    protected List<String> getRegexGroups(final String componentValue) {
        final List<String> regExGroups = new ArrayList<>();
        final RegExp p = RegExp.compile(QNAME_DECODING_PATTERN);
        final MatchResult m = p.exec(componentValue);
        for (int i = 0; i < m.getGroupCount(); i++) {
            regExGroups.add(m.getGroup(i));
        }
        return regExGroups;
    }

    public String toWidgetValue(final QName modelValue) {
        String encoding = QNAME_ENCODING;
        String namespace = modelValue.getNamespaceURI();
        String localPart = modelValue.getLocalPart();
        String prefix = modelValue.getPrefix();

        if (dmnModel != null) {
            Optional<String> nsPrefix = dmnModel.getPrefixForNamespaceURI(namespace);
            if (nsPrefix.isPresent()) {
                prefix = nsPrefix.get();
                namespace = "";
            }
        }

        encoding = encoding.replaceFirst(QNAME_URI, namespace);
        encoding = encoding.replaceFirst(QNAME_LOCALPART, localPart);
        encoding = encoding.replaceFirst(QNAME_PREFIX, prefix);

        return encoding;
    }
}
