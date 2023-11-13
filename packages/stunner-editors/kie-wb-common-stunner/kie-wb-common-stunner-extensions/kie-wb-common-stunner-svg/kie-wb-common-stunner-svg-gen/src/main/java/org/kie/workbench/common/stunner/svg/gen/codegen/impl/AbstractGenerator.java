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


package org.kie.workbench.common.stunner.svg.gen.codegen.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.uberfire.annotations.processors.GenerationException;

public abstract class AbstractGenerator {

    protected static ExceptionInInitializerError INITIALIZER_EXCEPTION = null;
    protected Configuration config;

    public AbstractGenerator() {
        try {
            this.config = new Configuration();
            this.config.setClassForTemplateLoading(this.getClass(),
                                                   "templates");
            this.config.setObjectWrapper(new DefaultObjectWrapper());
        } catch (NoClassDefFoundError var2) {
            if (var2.getCause() == null) {
                var2.initCause(INITIALIZER_EXCEPTION);
            }
            throw var2;
        } catch (ExceptionInInitializerError var3) {
            INITIALIZER_EXCEPTION = var3;
            throw var3;
        }
    }

    protected abstract String getTemplatePath();

    protected StringBuffer writeTemplate(final Map<String, Object> ctxt,
                                         final String path) throws GenerationException {
        //Generate code
        try (final StringWriter sw = new StringWriter();
             final BufferedWriter bw = new BufferedWriter(sw)) {
            final Template template = config.getTemplate(path + ".ftl");
            template.process(ctxt,
                             bw);
            return sw.getBuffer();
        } catch (IOException ioe) {
            throw new GenerationException(ioe);
        } catch (TemplateException te) {
            throw new GenerationException(te);
        }
    }

    protected StringBuffer writeTemplate(final Map<String, Object> ctxt) throws GenerationException {
        return writeTemplate(ctxt,
                             getTemplatePath());
    }

    public static String formatString(final String pattern,
                                      final String... values) {
        return String.format(pattern,
                             values);
    }

    public static String formatDouble(final String pattern,
                                      final double... values) {
        return SVGGeneratorFormatUtils.format(pattern,
                                              values);
    }

    public static String formatDouble(final double value) {
        return SVGGeneratorFormatUtils.format(value);
    }
}
