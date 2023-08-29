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


package org.kie.workbench.common.stunner.core.processors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.uberfire.annotations.processors.GenerationException;

public abstract class AbstractAdapterGenerator {

    protected static ExceptionInInitializerError INITIALIZER_EXCEPTION = null;
    protected Configuration config;

    public AbstractAdapterGenerator() {
        synchronized (AbstractAdapterGenerator.class) {
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
    }

    protected abstract String getTemplatePath();

    protected StringBuffer writeTemplate(final String packageName,
                                         final String className,
                                         final Map<String, Object> ctxt,
                                         final Messager messager) throws GenerationException {
        //Generate code
        try(final StringWriter sw = new StringWriter();
            final BufferedWriter bw = new BufferedWriter(sw)) {
            final Template template = config.getTemplate(getTemplatePath());
            template.process(ctxt,
                             bw);
            messager.printMessage(Diagnostic.Kind.NOTE,
                                  "Successfully generated code for [" + packageName + "." + className + "]");
            return sw.getBuffer();
        } catch (IOException ioe) {
            throw new GenerationException(ioe);
        } catch (TemplateException te) {
            throw new GenerationException(te);
        }
    }

    protected List<ProcessingElement> toElements(final Map<String, String> map) {
        List<ProcessingElement> result = new LinkedList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            result.add(new ProcessingElement(entry.getKey(),
                                             entry.getValue()));
        }
        return result;
    }

    protected List<ProcessingElementList> toElementList(final Map<String, List<String>> map) {
        List<ProcessingElementList> result = new LinkedList<>();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            result.add(new ProcessingElementList(entry.getKey(),
                                                 entry.getValue()));
        }
        return result;
    }

    protected List<ProcessingElementMap> toElementMap(final Map<String, Map<String, String>> map) {
        List<ProcessingElementMap> result = new LinkedList<>();
        map.entrySet().stream()
                .forEach(entry1 -> {
                    final Map<String, String> entryMap = new LinkedHashMap<String, String>();
                    entry1.getValue().entrySet().stream().forEach(entry2 -> {
                        entryMap.put(entry2.getKey(),
                                     entry2.getValue());
                    });
                    final ProcessingElementMap elementMap = new ProcessingElementMap(entry1.getKey(),
                                                                                     entryMap);
                    result.add(elementMap);
                });
        return result;
    }
}
