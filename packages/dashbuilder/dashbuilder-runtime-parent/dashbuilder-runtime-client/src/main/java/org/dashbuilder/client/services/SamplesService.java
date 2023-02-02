/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import elemental2.dom.DomGlobal;
import elemental2.dom.XMLHttpRequest;
import org.dashbuilder.client.setup.RuntimeClientSetup;
import org.dashbuilder.json.Json;
import org.jboss.errai.ioc.client.api.EntryPoint;

@EntryPoint
@ApplicationScoped
public class SamplesService {

    Map<String, List<SampleInfo>> samplesByCategory;

    final String SAMPLES_FILE = "samples.json";
    final String SAMPLE_ID_KEY = "id";
    final String SAMPLE_NAME_KEY = "name";

    @PostConstruct
    void init() {
        var setup = RuntimeClientSetup.Builder.get();
        var userSamplesUrl = setup == null ? null : RuntimeClientSetup.Builder.get().getSamplesUrl();
        var samplesUrl = userSamplesUrl.endsWith("/") ? userSamplesUrl : userSamplesUrl + "/";
        samplesByCategory = new HashMap<>();
        if (userSamplesUrl != null) {
            var xhr = new XMLHttpRequest();
            xhr.open("GET", SAMPLES_FILE, false);
            xhr.send();
            if (xhr.status >= 200 && xhr.status < 300) {
                this.extractSamplesFromResponse(samplesUrl, xhr.responseText);
            } else {
                DomGlobal.console.warn("Not able to load samples, server responded with " + xhr.status);
            }
        }

    }

    public Map<String, List<SampleInfo>> samplesByCategory() {
        return samplesByCategory;
    }

    public Collection<SampleInfo> allSamples() {
        return samplesByCategory.values()
                .stream()
                .flatMap(s -> s.stream())
                .collect(Collectors.toList());
    }

    void extractSamplesFromResponse(String samplesUrl, String txt) {
        var samplesJson = Json.parse(txt);
        for (var cat : samplesJson.keys()) {
            var samplesArray = samplesJson.getArray(cat);
            var samples = new ArrayList<SampleInfo>();
            for (var i = 0; i < samplesArray.length(); i++) {
                var sampleJson = samplesArray.getObject(i);
                var id = sampleJson.getString(SAMPLE_ID_KEY);
                var name = sampleJson.getString(SAMPLE_NAME_KEY);
                var sampleBaseUrl = samplesUrl + id + "/";
                var samplesSourceUrl = sampleBaseUrl + id + ".dash.yaml";
                samples.add(new SampleInfo(id,
                        name == null ? id : name,
                        sampleBaseUrl + id + ".svg",
                        samplesSourceUrl));
            }
            if (samples.size() > 0) {
                samplesByCategory.put(cat, samples);
            }
        }
    }

    public static class SampleInfo {

        private String id;
        private String name;

        private String svgUrl;
        private String sourceUrl;

        public SampleInfo(String id,
                          String name,
                          String svgUrl,
                          String sourceUrl) {
            this.id = id;
            this.name = name;
            this.svgUrl = svgUrl;
            this.sourceUrl = sourceUrl;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getSvgUrl() {
            return svgUrl;
        }

        public String getSourceUrl() {
            return sourceUrl;
        }

    }
}
