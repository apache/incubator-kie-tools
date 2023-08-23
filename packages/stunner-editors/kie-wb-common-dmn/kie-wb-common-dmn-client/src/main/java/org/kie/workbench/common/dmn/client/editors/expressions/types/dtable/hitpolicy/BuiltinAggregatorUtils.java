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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.model.BuiltinAggregator;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;

@ApplicationScoped
public class BuiltinAggregatorUtils {

    private TranslationService translationService;

    public BuiltinAggregatorUtils() {
        //CDI proxy
    }

    @Inject
    public BuiltinAggregatorUtils(final TranslationService translationService) {
        this.translationService = translationService;
    }

    public List<BuiltinAggregator> getAllValues() {
        final List<BuiltinAggregator> builtinAggregators = new ArrayList<>();
        builtinAggregators.add(null);
        builtinAggregators.addAll(Arrays.asList(BuiltinAggregator.values()));
        return builtinAggregators;
    }

    public String toString(final BuiltinAggregator aggregator) {
        if (aggregator == null) {
            return translationService.getTranslation(DMNEditorConstants.DecisionTableEditor_NullBuiltinAggregator);
        }
        return aggregator.value();
    }

    public BuiltinAggregator toEnum(final String value) {
        if (value.equals(translationService.getTranslation(DMNEditorConstants.DecisionTableEditor_NullBuiltinAggregator))) {
            return null;
        }
        return BuiltinAggregator.fromValue(value);
    }
}
