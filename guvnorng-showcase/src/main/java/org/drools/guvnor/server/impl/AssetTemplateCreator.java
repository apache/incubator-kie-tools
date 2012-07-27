/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.server.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.drools.guvnor.client.common.AssetFormats;

import org.drools.repository.AssetItem;
import org.uberfire.backend.util.LoggingHelper;

public class AssetTemplateCreator {

    private static final LoggingHelper log = LoggingHelper.getLogger(AssetTemplateCreator.class);

    /**
     * For some format types, we add some sugar by adding a new template.
     */
    protected void applyPreBuiltTemplates(String ruleName,
            String format,
            AssetItem asset) {
        if (format.equals(AssetFormats.DSL_TEMPLATE_RULE)) {
            asset.updateContent("when\n\nthen\n");
        } else if (format.equals(AssetFormats.FUNCTION)) {
            asset.updateContent("function <returnType> " + ruleName + "(<args here>) {\n\n\n}");
        } else if (format.equals(AssetFormats.DSL)) {
            asset.updateContent("[when]Condition sentence template {var}=" + "rule language mapping {var}\n" + "[then]Action sentence template=rule language mapping");
        } else if (format.equals(AssetFormats.DRL)) {
            asset.updateContent("when\n\t#conditions\nthen\n\t#actions");
        } else if (format.equals(AssetFormats.ENUMERATION)) {

        } else if (format.equals(AssetFormats.SPRING_CONTEXT)) {
            asset.updateContent(getTemplateFromFile("spring-context-sample.xml"));
        } else if (format.equals(AssetFormats.SERVICE_CONFIG)) {
            asset.updateContent("");
        } else if (format.equals(AssetFormats.WORKITEM_DEFINITION)) {
            asset.updateContent(getTemplateFromFile("workitem-definition-sample.xml"));
        } else if (format.equals(AssetFormats.CHANGE_SET)) {
            asset.updateContent(getTemplateFromFile("change-set-sample.xml"));
        }
    }

    private String getTemplateFromFile(String fileName) {
        try {
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            BufferedInputStream inContent = new BufferedInputStream(this.getClass().getClassLoader().getResourceAsStream(fileName));
            IOUtils.copy(inContent,
                    outContent);

            return outContent.toString();
        } catch (IOException ex) {
            log.error("Error reading spring-context-sample.xml",
                    ex);
            throw new IllegalArgumentException("Error " + fileName, ex);
        }
    }
}
