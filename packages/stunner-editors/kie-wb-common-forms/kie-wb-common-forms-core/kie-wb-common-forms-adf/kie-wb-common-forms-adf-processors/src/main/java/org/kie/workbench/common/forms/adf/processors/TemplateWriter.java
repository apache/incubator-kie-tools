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


package org.kie.workbench.common.forms.adf.processors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.uberfire.annotations.processors.GenerationException;

public class TemplateWriter {

    private static final String TEMPLATES_FOLDER = "templates/";

    private TemplateWriter() {
    }

    public static StringBuffer writeTemplate(final String templateName, final Map<String, Object> context) throws GenerationException {

        // The code used to contain 'new InputStreamReader(this.getClass().getResourceAsStream(templateName))' which for
        // some reason was causing issues during concurrent invocation of this method (e.g. in parallel Maven build).
        // The stream returned by 'getResourceAsStream(templateName)' was sometimes already closed (!) and as the
        // Template class tried to read from the stream it resulted in IOException. Changing the code to
        // 'getResource(templateName).openStream()' seems to be a sensible workaround
        try (InputStream templateIs = TemplateWriter.class.getResource(TEMPLATES_FOLDER + templateName).openStream();
             StringWriter sw = new StringWriter();
             BufferedWriter bw = new BufferedWriter(sw)) {

            Configuration config = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

            Template template = new Template("", new InputStreamReader(templateIs), config);

            template.process(context, bw);

            return sw.getBuffer();

        } catch (IOException | TemplateException ioe) {
            throw new GenerationException(ioe);
        }
    }
}
