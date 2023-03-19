/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.renderer.c3.client.charts.map.geojson;

import java.util.Map;
import java.util.Optional;

import org.dashbuilder.renderer.c3.client.jsbinding.geojson.Feature;

/**
 * Class for handling GeoJson files that contains country information 
 */
public interface CountriesGeoJsonService {

    public Feature[] getCountries();

    public String getCountryName(Feature country);

    public String getCountryNameByCode(String code);

    public Optional<Feature> findCountry(String idNameOrLocation);

    public Optional<Map.Entry<String, Double>> findEntry(Map<String, Double> data, Feature feature);

    public Optional<Double> findValue(Map<String, Double> data, Feature feature);

}
