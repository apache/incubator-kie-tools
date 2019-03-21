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

import static org.dashbuilder.renderer.c3.client.charts.map.geojson.impl.CountriesGeoJsonServiceImpl.COUNTRY_NAME_PROPERTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.dashbuilder.renderer.c3.client.charts.map.geojson.GeoJsonLoader;
import org.dashbuilder.renderer.c3.client.jsbinding.geojson.Feature;
import org.dashbuilder.renderer.c3.client.jsbinding.geojson.FeatureCollection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import jsinterop.base.JsPropertyMap;

@RunWith(MockitoJUnitRunner.class)
public class CountriesGeoJsonServiceImplTest {

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
    
    @InjectMocks
    CountriesGeoJsonServiceImpl countriesGeoJsonServiceImpl;
    
    @Before
    public void setup() {
        when(properties.get(COUNTRY_NAME_PROPERTY)).thenReturn(COUNTRY_NAME);
        when(feature.getProperties()).thenReturn(properties);
        when(feature.getId()).thenReturn(COUNTRY_CODE);
        when(featureCollection.getFeatures()).thenReturn(new Feature[] { feature } );
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
    
    public void valueByCountryTest() {
        Double val = 1d;
        Optional<Double> valueByCountry;
        Map<String, Double> data = new HashMap<>();
        data.put(COUNTRY_NAME, val);
        valueByCountry = countriesGeoJsonServiceImpl.valueByCountry(data, feature);
        assertTrue(valueByCountry.isPresent());
        assertEquals(val, valueByCountry.get());
        valueByCountry = countriesGeoJsonServiceImpl.valueByCountry(data, null);
        assertTrue(!valueByCountry.isPresent());
        valueByCountry = countriesGeoJsonServiceImpl.valueByCountry(new HashMap<>(), null);
        assertTrue(!valueByCountry.isPresent());
        
    }
}