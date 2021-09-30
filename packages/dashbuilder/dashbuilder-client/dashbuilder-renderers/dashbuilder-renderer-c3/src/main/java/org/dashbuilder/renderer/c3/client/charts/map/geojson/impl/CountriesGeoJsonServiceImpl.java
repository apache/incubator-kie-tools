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
package org.dashbuilder.renderer.c3.client.charts.map.geojson.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.dashbuilder.renderer.c3.client.charts.map.geojson.CountriesGeoJsonService;
import org.dashbuilder.renderer.c3.client.charts.map.geojson.GeoJsonLoader;
import org.dashbuilder.renderer.c3.client.jsbinding.geojson.Feature;
import org.dashbuilder.renderer.c3.client.jsbinding.geojson.FeatureCollection;
import org.uberfire.client.views.pfly.widgets.D3;

public class CountriesGeoJsonServiceImpl implements CountriesGeoJsonService {

    static final String COUNTRY_NAME_PROPERTY = "name";

    static final String LAT_LONG_REGEX = "\\s*(\\-?\\d+(\\.\\d+)?),\\s*(\\-?\\d+(\\.\\d+)?)\\s*";

    @Inject
    GeoJsonLoader geoJsonLoader;

    D3 d3;

    private FeatureCollection featureCollection;

    @PostConstruct
    public void setup() {
        featureCollection = geoJsonLoader.load();
        d3 = D3.Builder.get();
    }

    @Override
    public Feature[] getCountries() {
        return featureCollection.getFeatures();
    }

    @Override
    public String getCountryName(Feature country) {
        Object name = country == null ? null : country.getProperties().get(COUNTRY_NAME_PROPERTY);
        if (name != null) {
            return name.toString();
        }
        return "";
    }

    @Override
    public String getCountryNameByCode(String code) {
        return Arrays.stream(featureCollection.getFeatures())
                     .filter(f -> f.getId().equalsIgnoreCase(code))
                     .map(this::getCountryName)
                     .findFirst().orElse("");
    }

    @Override
    public Optional<Entry<String, Double>> findEntry(Map<String, Double> data, Feature feature) {
        if (usesLocation(data)) {
            return entriesByLocation(data, feature).findAny();
        } else {
            return entryByCountry(data, feature);
        }
    }

    @Override
    public Optional<Double> findValue(Map<String, Double> data, Feature feature) {
        if (usesLocation(data)) {
            return valueByLocation(data, feature);
        }
        return findEntry(data, feature).flatMap(e -> Optional.ofNullable(e.getValue()));
    }

    @Override
    public Optional<Feature> findCountry(String idNameOrLocation) {
        if (idNameOrLocation.matches(LAT_LONG_REGEX)) {
            return countryByLocation(idNameOrLocation);
        }
        return countryByIdOrName(idNameOrLocation);
    }

    public Optional<Feature> countryByIdOrName(String idOrName) {
        return Arrays.stream(featureCollection.getFeatures())
                     .filter(f -> {
                         String countryName = getCountryName(f);
                         return idOrName.equalsIgnoreCase(f.getId()) ||
                                idOrName.equalsIgnoreCase(countryName);
                     })
                     .findFirst();
    }

    public Optional<Feature> countryByLocation(String location) {
        return Arrays.stream(featureCollection.getFeatures())
                     .filter(f -> containsLocation(location, f))
                     .findFirst();
    }

    public Optional<Entry<String, Double>> entryByCountry(Map<String, Double> data, Feature value) {
        if (value == null) {
            return Optional.empty();
        }

        String countryID = value.getId();
        String countryName = getCountryName(value);

        return data.entrySet().stream().filter(k -> k.getKey().equalsIgnoreCase(countryID) ||
                                                    k.getKey().equalsIgnoreCase(countryName))
                   .findFirst();
    }

    public Optional<Double> valueByCountry(Map<String, Double> data, Feature value) {
        return entryByCountry(data, value).flatMap(e -> Optional.ofNullable(e.getValue()));
    }

    public Stream<Entry<String, Double>> entriesByLocation(Map<String, Double> data, Feature feature) {
        return data.entrySet().stream()
                   .filter(e -> e.getKey().matches(LAT_LONG_REGEX))
                   .filter(e -> containsLocation(e.getKey(), feature));
    }

    public Optional<Double> valueByLocation(Map<String, Double> data, Feature feature) {
        // we may have multiple entries for a given feature (for example, multiple states of a country)
        // stream not working here for some reason... (applyAsDouble is not a function)
        // entriesByLocation(data, feature).flatMapToDouble(Entry::getKey).sum();
        List<Double> values = entriesByLocation(data, feature).map(Entry::getValue).collect(Collectors.toList());
        if (values.isEmpty()) {
            return Optional.empty();
        }
        Double value = 0.0;
        for (Double v : values) {
            value += v;
        }
        return Optional.of(value);
    }

    protected boolean containsLocation(String latitudeLongitudeStr, Feature feature) {
        if (latitudeLongitudeStr == null || !latitudeLongitudeStr.matches(LAT_LONG_REGEX)) {
            return false;
        }
        // we use lat - long, but d3 considers long-lat
        Double[] latLong = extractLatLongInfo(latitudeLongitudeStr);
        Double[] longLat = {latLong[1], latLong[0]};
        return d3.geoContains(feature, longLat);
    }

    protected Double[] extractLatLongInfo(String value) {
        return Stream.of(value.replaceAll("\\s", "").split(","))
                     .map(Double::parseDouble)
                     .toArray(Double[]::new);
    }

    private boolean usesLocation(Map<String, Double> data) {
        if (data.isEmpty()) {
            return false;
        }
        return data.keySet().iterator().next().matches(LAT_LONG_REGEX);
    }

}
