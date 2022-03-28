/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.kogito.core.internal.engine;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.kogito.core.internal.engine.exceptions.EngineException;

public class JavaEngine {

    private final Configuration engine;

    public JavaEngine() {
        this.engine = new Configuration(Configuration.getVersion());
        engine.setClassForTemplateLoading(this.getClass(), "/templates/");
    }

    protected String evaluate(String template, TemplateParameters templateParameters) {
        try {
            Writer writer = new StringWriter();
            Template compiledTemplate = this.engine.getTemplate(template);
            compiledTemplate.process(templateParameters, writer);
            return writer.toString();
        } catch (Exception e) {
            String message = "Can't evaluate template " + template;
            JavaLanguageServerPlugin.logException(message, e);
            throw new EngineException(message, e);
        }
    }

    public BuildInformation buildImportClass(String uri, String importText) {

        TemplateParameters item = new TemplateParameters();
        item.setClassName(getClassName(uri));
        item.setQuery(importText);

        String content = this.evaluate(Templates.TEMPLATE_CLASS, item);

        return new BuildInformation(uri, getContent(uri), content, 2, getEndOfLinePosition(content, 2));
    }

    public BuildInformation buildPublicContent(String uri, String fqcn, String completeText) {

        TemplateParameters item = new TemplateParameters();
        item.setClassName(getClassName(uri));
        item.setQuery(completeText);
        item.setFqcn(fqcn);

        String content = this.evaluate(Templates.TEMPLATE_ACCESSORS, item);

        return new BuildInformation(uri, getContent(uri), content, 5, getEndOfLinePosition(content, 5));
    }

    protected int getEndOfLinePosition(String content, int lineNumber) {
        String[] split = content.split("\n");
        JavaLanguageServerPlugin.logInfo(split[lineNumber]);
        return split[lineNumber].length();
    }

    protected String getClassName(String url) {
        String fileName = Paths.get(url).getFileName().toString();
        if (fileName.contains(".")) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        } else {
            return fileName;
        }
    }

    protected String getContent(String uri) {
        try {
            return Files.readString(Path.of(URI.create(uri)));
        } catch (IOException e) {
            JavaLanguageServerPlugin.logException("Can't read content from: " + uri, e);
            return "";
        }
    }
}
