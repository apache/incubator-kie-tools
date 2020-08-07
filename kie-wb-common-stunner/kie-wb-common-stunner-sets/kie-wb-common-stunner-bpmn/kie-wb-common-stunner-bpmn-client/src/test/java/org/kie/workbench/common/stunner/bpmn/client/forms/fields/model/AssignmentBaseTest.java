/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.model;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.google.gwt.junit.GWTMockUtilities;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.URL;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AssignmentBaseTest {

    public void setUp() throws Exception {
        // Prevent runtime GWT.create() error at DesignerEditorConstants.INSTANCE
        GWTMockUtilities.disarm();
        // MockDesignerEditorConstants replaces DesignerEditorConstants.INSTANCE
        final Answer answer = invocation -> invocation.getMethod().getName();
        final StunnerFormsClientFieldsConstants constants = mock(StunnerFormsClientFieldsConstants.class,
                                                                 answer);
        setFinalStaticField(StunnerFormsClientFieldsConstants.class.getDeclaredField("CONSTANTS"),
                            constants);

        // Prevent GWT calls in StringUtils
        URL url = mock(URL.class);
        when(url.decodeQueryString(anyString())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            return urlDecode((String) args[0]);
        });
        when(url.encodeQueryString(anyString())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            return urlEncode((String) args[0]);
        });
        StringUtils.setURL(url);
    }

    public void tearDown() {
        GWTMockUtilities.restore();
    }

    /**
     * Implementation of urlEncode for PowerMocked StringUtils
     * @param s
     * @return
     */
    public String urlEncode(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        try {
            return URLEncoder.encode(s,
                                     "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    /**
     * Implementation of urlDecode for PowerMocked StringUtils
     * @param s
     * @return
     */
    public String urlDecode(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        try {
            return URLDecoder.decode(s,
                                     "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    private void setFinalStaticField(Field field,
                                     Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field,
                              field.getModifiers() & ~Modifier.FINAL);
        field.set(null,
                  newValue);
    }
}
