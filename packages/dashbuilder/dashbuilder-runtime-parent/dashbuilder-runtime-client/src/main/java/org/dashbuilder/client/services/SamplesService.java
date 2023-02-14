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
import java.util.Optional;
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

    final String SAMPLE_ID_PARAM = "sampleId";

    String samplesEditUrl;

    @PostConstruct
    void init() {
        var setup = RuntimeClientSetup.Builder.get();
        var userSamplesUrl = setup == null ? null : RuntimeClientSetup.Builder.get().getSamplesUrl();
        samplesByCategory = new HashMap<>();

        if (userSamplesUrl != null) {
            var xhr = new XMLHttpRequest();
            samplesEditUrl = setup.getSamplesEditService();
            xhr.open("GET", SAMPLES_FILE, false);
            xhr.send();
            if (xhr.status >= 200 && xhr.status < 300) {
                this.extractSamplesFromResponse(userSamplesUrl, xhr.responseText);
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

    public boolean isSample(String importID) {
        return samplesByCategory.values()
                .stream()
                .flatMap(Collection::stream)
                .anyMatch(sample -> sample.sourceUrl.equals(importID));

    }

    void extractSamplesFromResponse(String userSamplesUrl, String txt) {
        var samplesUrl = userSamplesUrl.endsWith("/") ? userSamplesUrl : userSamplesUrl + "/";
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
                var editUrl = buildSampleUrl(id);
                samples.add(new SampleInfo(id,
                        name == null ? id : name,
                        sampleBaseUrl + id + ".svg",
                        samplesSourceUrl,
                        editUrl));
            }
            if (samples.size() > 0) {
                samplesByCategory.put(cat, samples);
            }
        }
    }

    String buildSampleUrl(String id) {
        if (samplesEditUrl != null) {
            var sampleUrl = samplesEditUrl;
            var sampleIdParam = SAMPLE_ID_PARAM + "=" + id;
            if (sampleUrl.indexOf('?') == -1) {
                sampleUrl += "?";
            } else {
                sampleUrl += "&";
            }
            sampleUrl += sampleIdParam;
            return sampleUrl;
        }
        return null;
    }

    public static class SampleInfo {

        /**
         * The sample ID which will be used as base for the SVG, source URLs and edit service (if available)
         */
        private String id;
        /**
         * A human friendly name for the sample
         */
        private String name;

        private String svgUrl;
        private String sourceUrl;
        private Optional<String> editUrl;

        public SampleInfo(String id,
                          String name,
                          String svgUrl,
                          String sourceUrl,
                          String editUrl) {
            this.id = id;
            this.name = name;
            this.svgUrl = svgUrl;
            this.sourceUrl = sourceUrl;
            this.editUrl = Optional.ofNullable(editUrl);
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

        public Optional<String> getEditUrl() {
            return editUrl;
        }

    }
}
