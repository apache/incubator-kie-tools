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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import jsinterop.base.JsPropertyMap;
import org.dashbuilder.renderer.c3.client.charts.map.geojson.GeoJsonLoader;
import org.dashbuilder.renderer.c3.client.jsbinding.geojson.Feature;
import org.dashbuilder.renderer.c3.client.jsbinding.geojson.FeatureCollection;
import org.dashbuilder.renderer.c3.client.jsbinding.geojson.Geometry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.views.pfly.widgets.D3;

import static org.dashbuilder.renderer.c3.client.charts.map.geojson.impl.CountriesGeoJsonServiceImpl.COUNTRY_NAME_PROPERTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CountriesGeoJsonServiceImplTest {

    private static final String COUNTRY_LOCATION = "2.0, 1.0";
    private static final Double[] LONG_LAT = {1.0, 2.0};

    private static final String COUNTRY_CODE = "ID";

    private static final String COUNTRY_NAME = "Name";

    @Mock
    GeoJsonLoader geoJsonLoader;

    @Mock
    JsPropertyMap<Object> properties;

    @Mock
    Feature feature;

    @Mock
    FeatureCollection featureCollection;

    @Mock
    Geometry geometry;

    @Mock
    D3 d3;

    @InjectMocks
    CountriesGeoJsonServiceImpl countriesGeoJsonServiceImpl;

    @Before
    public void setup() {
        when(properties.get(COUNTRY_NAME_PROPERTY)).thenReturn(COUNTRY_NAME);
        when(feature.getProperties()).thenReturn(properties);
        when(feature.getGeometry()).thenReturn(geometry);
        when(feature.getId()).thenReturn(COUNTRY_CODE);
        when(featureCollection.getFeatures()).thenReturn(new Feature[]{feature});
        when(geoJsonLoader.load()).thenReturn(featureCollection);
    }

    @Test
    public void getCountryNameTest() {
        String countryName = countriesGeoJsonServiceImpl.getCountryName(feature);
        assertEquals(COUNTRY_NAME, countryName);
        countryName = countriesGeoJsonServiceImpl.getCountryName(null);
        assertTrue(countryName.isEmpty());
    }

    @Test
    public void getCountryNameByCodeTest() {
        String countryName = countriesGeoJsonServiceImpl.getCountryNameByCode(COUNTRY_CODE);
        assertEquals(COUNTRY_NAME, countryName);
        countryName = countriesGeoJsonServiceImpl.getCountryNameByCode("123");
        assertTrue(countryName.isEmpty());
        countryName = countriesGeoJsonServiceImpl.getCountryNameByCode(null);
        assertTrue(countryName.isEmpty());
    }

    @Test
    public void countryByIdOrNameTest() {
        Optional<Feature> countryByIdOrName = countriesGeoJsonServiceImpl.countryByIdOrName(COUNTRY_CODE);
        assertTrue(countryByIdOrName.isPresent());
        assertEquals(feature, countryByIdOrName.get());
        countryByIdOrName = countriesGeoJsonServiceImpl.countryByIdOrName(COUNTRY_NAME);
        assertTrue(countryByIdOrName.isPresent());
        assertEquals(feature, countryByIdOrName.get());
        countryByIdOrName = countriesGeoJsonServiceImpl.countryByIdOrName("123");
        assertTrue(!countryByIdOrName.isPresent());
    }

    @Test
    public void entryByCountryTest() {
        Optional<Entry<String, Double>> entryByCountry;
        Map<String, Double> data = new HashMap<>();
        data.put(COUNTRY_NAME, 1d);
        entryByCountry = countriesGeoJsonServiceImpl.entryByCountry(data, feature);
        assertTrue(entryByCountry.isPresent());
        entryByCountry = countriesGeoJsonServiceImpl.entryByCountry(data, null);
        assertTrue(!entryByCountry.isPresent());
        entryByCountry = countriesGeoJsonServiceImpl.entryByCountry(new HashMap<>(), feature);
        assertTrue(!entryByCountry.isPresent());
    }

    @Test
    public void valueByCountryTest() {
        Double val = 1d;
        Optional<Double> valueByCountry;
        Map<String, Double> data = new HashMap<>();
        data.put(COUNTRY_NAME, val);
        valueByCountry = countriesGeoJsonServiceImpl.valueByCountry(data, feature);
        assertTrue(valueByCountry.isPresent());
        assertEquals(val, valueByCountry.get());
        valueByCountry = countriesGeoJsonServiceImpl.valueByCountry(data, null);
        assertFalse(valueByCountry.isPresent());
        valueByCountry = countriesGeoJsonServiceImpl.valueByCountry(new HashMap<>(), null);
        assertFalse(valueByCountry.isPresent());
    }

    @Test
    public void entryByLocationTest() {
        when(d3.geoContains(any(), any())).thenReturn(true);
        Optional<Entry<String, Double>> entryByLocation;
        Map<String, Double> data = new HashMap<>();
        data.put(COUNTRY_LOCATION, 1d);
        entryByLocation = countriesGeoJsonServiceImpl.entriesByLocation(data, feature).findFirst();
        ;
        assertTrue(entryByLocation.isPresent());
        verify(d3).geoContains(any(), any());

        when(d3.geoContains(any(), any())).thenReturn(false);
        entryByLocation = countriesGeoJsonServiceImpl.entriesByLocation(data, null).findFirst();
        assertFalse(entryByLocation.isPresent());

        entryByLocation = countriesGeoJsonServiceImpl.entriesByLocation(new HashMap<>(), feature).findFirst();
        assertFalse(entryByLocation.isPresent());

        when(d3.geoContains(any(), any())).thenReturn(false);
        entryByLocation = countriesGeoJsonServiceImpl.entriesByLocation(data, feature).findFirst();
        assertFalse(entryByLocation.isPresent());
    }

    @Test
    public void valueByLocationTest() {
        when(d3.geoContains(any(), any())).thenReturn(true);
        Double val = 1d;
        Optional<Double> valueByLocation;
        Map<String, Double> data = new HashMap<>();
        data.put(COUNTRY_LOCATION, val);
        valueByLocation = countriesGeoJsonServiceImpl.valueByLocation(data, feature);
        assertTrue(valueByLocation.isPresent());
        assertEquals(val, valueByLocation.get());
        verify(d3).geoContains(feature, LONG_LAT);

        when(d3.geoContains(any(), any())).thenReturn(false);
        valueByLocation = countriesGeoJsonServiceImpl.valueByLocation(data, null);
        assertFalse(valueByLocation.isPresent());

        valueByLocation = countriesGeoJsonServiceImpl.valueByLocation(new HashMap<>(), null);
        assertFalse(valueByLocation.isPresent());
    }

    @Test
    public void findCountryWithLocationTest() {
        when(d3.geoContains(any(), any())).thenReturn(true);
        Map<String, Double> data = new HashMap<>();
        data.put(COUNTRY_LOCATION, 1d);
        Optional<Feature> findCountry = countriesGeoJsonServiceImpl.findCountry(COUNTRY_LOCATION);
        assertTrue(findCountry.isPresent());
        assertEquals(COUNTRY_CODE, findCountry.get().getId());
        assertEquals(COUNTRY_NAME, countriesGeoJsonServiceImpl.getCountryName(findCountry.get()));
        verify(d3).geoContains(feature, LONG_LAT);
    }

    @Test
    public void findCountryWithCountryNameTest() {
        Map<String, Double> data = new HashMap<>();
        data.put(COUNTRY_NAME, 1d);
        Optional<Feature> findCountry = countriesGeoJsonServiceImpl.findCountry(COUNTRY_NAME);
        assertTrue(findCountry.isPresent());
        assertEquals(COUNTRY_CODE, findCountry.get().getId());
        assertEquals(COUNTRY_NAME, countriesGeoJsonServiceImpl.getCountryName(findCountry.get()));
        verify(d3, times(0)).geoContains(any(), any());
    }

}
