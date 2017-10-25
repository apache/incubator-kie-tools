/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.ala.openshift.access;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesList;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.client.dsl.Loadable;
import io.fabric8.kubernetes.client.utils.Serialization;
import io.fabric8.kubernetes.client.utils.Utils;
import io.fabric8.openshift.api.model.DoneableTemplate;
import io.fabric8.openshift.api.model.Template;
import io.fabric8.openshift.api.model.TemplateBuilder;
import io.fabric8.openshift.client.dsl.TemplateResource;
import org.guvnor.ala.openshift.access.exceptions.OpenShiftClientException;
import org.guvnor.ala.openshift.config.OpenShiftRuntimeConfig;

/**
 * Kubernetes template wrapper/utility for openshift ala.
 */
@JsonIgnoreType
public class OpenShiftTemplate {

    private final TemplateResource<Template, KubernetesList, DoneableTemplate> resource;
    private final Template template;

    public OpenShiftTemplate(OpenShiftClient client, OpenShiftRuntimeConfig runtimeConfig) {
        Loadable<TemplateResource<Template, KubernetesList, DoneableTemplate>> loadable = null;
        String prjName = runtimeConfig.getProjectName();
        String templateUri = runtimeConfig.getResourceTemplateUri();
        String templateName = runtimeConfig.getResourceTemplateName();
        if (prjName != null) {
            loadable = client.getDelegate().templates().inNamespace(prjName);
        } else {
            loadable = client.getDelegate().templates();
        }
        TemplateResource<Template, KubernetesList, DoneableTemplate> res = null;
        if (templateUri != null) {
            URL templateUrl = client.toUrl(templateUri);
            if (templateUrl != null) {
                res = loadable.load(templateUrl);
            }
        } else if (templateName != null && !templateName.isEmpty()) {
            res = loadable.load(templateName);
        }
        resource = res;
        template = res != null ? res.get() : null;
        if (template == null) {
            throw new OpenShiftClientException(String.format("could not load template with project [%s] and uri [%s] or name [%s]", prjName, templateUri, templateName));
        }
    }

    public OpenShiftTemplate(File templateFile) {
        this(templateFile.toURI());
    }

    public OpenShiftTemplate(String templateUri) {
        this(URI.create(templateUri));
    }

    public OpenShiftTemplate(URI templateUri) {
        this.resource = null;
        try {
            InputStream templateStream = templateUri.toURL().openStream();
            this.template = load(templateStream);
            templateStream.close();
        } catch (IOException ioe) {
            throw new OpenShiftClientException(ioe.getMessage(), ioe);
        }
        if (template == null) {
            throw new OpenShiftClientException(String.format("could not load template with uri [%s]", templateUri));
        }
    }

    public OpenShiftTemplate(URL templateUrl) {
        this.resource = null;
        try {
            InputStream templateStream = templateUrl.openStream();
            this.template = load(templateStream);
            templateStream.close();
        } catch (IOException ioe) {
            throw new OpenShiftClientException(ioe.getMessage(), ioe);
        }
        if (template == null) {
            throw new OpenShiftClientException(String.format("could not load template with url [%s]", templateUrl));
        }
    }

    public OpenShiftTemplate(InputStream templateStream) {
        this.resource = null;
        this.template = load(templateStream);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Template load(InputStream templateStream) {
        String generatedName = Utils.randomString("template-", 10);
        Template temp = null;
        Object item = Serialization.unmarshal(templateStream);
        if (item instanceof Template) {
            temp = (Template) item;
        } else if (item instanceof HasMetadata) {
            HasMetadata h = (HasMetadata) item;
            temp = new TemplateBuilder()
                    .withNewMetadata()
                    .withName(generatedName)
                    .withNamespace(h != null && h.getMetadata() != null ? h.getMetadata().getNamespace() : null)
                    .endMetadata()
                    .withObjects(h).build();
        } else if (item instanceof KubernetesResourceList) {
            List<HasMetadata> list = ((KubernetesResourceList<HasMetadata>) item).getItems();
            temp = new TemplateBuilder()
                    .withNewMetadata()
                    .withName(generatedName)
                    .endMetadata()
                    .withObjects(list.toArray(new HasMetadata[list.size()])).build();
        } else if (item instanceof HasMetadata[]) {
            temp = new TemplateBuilder()
                    .withNewMetadata()
                    .withName(generatedName)
                    .endMetadata()
                    .withObjects((HasMetadata[]) item).build();
        } else if (item instanceof Collection) {
            List<HasMetadata> items = new ArrayList<>();
            for (Object o : (Collection) item) {
                if (o instanceof HasMetadata) {
                    items.add((HasMetadata) o);
                }
            }
            temp = new TemplateBuilder()
                    .withNewMetadata()
                    .withName(generatedName)
                    .endMetadata()
                    .withObjects(items.toArray(new HasMetadata[items.size()])).build();
        }
        return temp;
    }

    public Collection<Parameter> getParameters() {
        return getParameterMap().values();
    }

    public SortedMap<String, Parameter> getParameterMap() {
        SortedMap<String, Parameter> map = new TreeMap<String, Parameter>();
        if (template != null) {
            List<io.fabric8.openshift.api.model.Parameter> list = template.getParameters();
            for (io.fabric8.openshift.api.model.Parameter param : list) {
                map.put(param.getName(), new Parameter(param));
            }
        }
        return map;
    }

    // package-protected only for use by OpenShiftClient
    KubernetesList process(Map<String, String> parameters) {
        if (resource != null) {
            return resource.process(parameters);
        } else {
            throw new IllegalStateException("cannot process parameters with null template resource");
        }
    }

    /**
     * Read-only parameter wrapper.
     */
    @JsonIgnoreType
    public class Parameter {

        private final io.fabric8.openshift.api.model.Parameter param;

        private Parameter(io.fabric8.openshift.api.model.Parameter param) {
            this.param = param;
        }

        public String getName() {
            return param.getName();
        }

        public String getDisplayName() {
            return param.getDisplayName();
        }

        public String getDescription() {
            return param.getDescription();
        }

        public boolean isRequired() {
            Boolean b = param.getRequired();
            return b != null && b.booleanValue();
        }

        public String getGenerate() {
            return param.getGenerate();
        }

        public String getFrom() {
            return param.getFrom();
        }

        public String getValue() {
            return param.getValue();
        }

    }

}
