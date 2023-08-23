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

package org.kie.workbench.common.dmn.client.marshaller.converters.dd;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.property.styling.StylingSet;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIColor;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNStyle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class FontStylingSetPropertyConverterTest {

    @Test
    public void testWbFromDMN() {
        final JSIDMNStyle jsiDmnStyle = mock(JSIDMNStyle.class);

        final JSIColor fontColor = mock(JSIColor.class);
        when(fontColor.getRed()).thenReturn(10);
        when(fontColor.getGreen()).thenReturn(20);
        when(fontColor.getBlue()).thenReturn(30);

        final String fontFamily = "Arial";
        final double fontSize = 11.0;

        when(jsiDmnStyle.getFontColor()).thenReturn(fontColor);
        when(jsiDmnStyle.getFontFamily()).thenReturn(fontFamily);
        when(jsiDmnStyle.getFontSize()).thenReturn(fontSize);

        final StylingSet convertedResult = FontStylingSetPropertyConverter.wbFromDMN(jsiDmnStyle);

        assertThat(convertedResult.getFontColour().getValue()).isEqualTo("#0a141e");
        assertThat(convertedResult.getFontFamily().getValue()).isEqualTo(fontFamily);
        assertThat(convertedResult.getFontSize().getValue()).isEqualTo(fontSize);
    }
}
