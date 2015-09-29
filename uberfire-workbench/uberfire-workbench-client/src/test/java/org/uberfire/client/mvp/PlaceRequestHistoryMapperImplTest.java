/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.mvp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import javax.enterprise.context.Dependent;

import org.jboss.errai.ioc.client.QualifierUtil;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanManagerImpl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.impl.ObservablePathImpl;
import org.uberfire.client.util.MockIOCBeanDef;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

import com.google.common.collect.ImmutableMap;

public class PlaceRequestHistoryMapperImplTest {

    private PlaceRequestHistoryMapperImpl placeRequestHistoryMapper;
    
    @BeforeClass
    public static void setupBeans() {
        ((SyncBeanManagerImpl) IOC.getBeanManager()).reset();

        IOC.getBeanManager().registerBean( new MockIOCBeanDef<ObservablePath, ObservablePathImpl>( new ObservablePathImpl(),
                                                                                                   ObservablePath.class,
                                                                                                   Dependent.class,
                                                                                                   new HashSet<Annotation>( Arrays.asList( QualifierUtil.DEFAULT_QUALIFIERS ) ),
                                                                                                   null,
                                                                                                   true,
                                                                                                   true ) );
    }



    @Before
    public void setup() {
        placeRequestHistoryMapper = new PlaceRequestHistoryMapperImpl() {
            @Override
            String urlDecode( String value ) {
                try {
                    return URLDecoder.decode( value, "UTF-8" );
                } catch ( UnsupportedEncodingException e ) {
                    throw new RuntimeException( e );
                }
            }
        };
    }

    @Test
    public void createPlaceRequest() throws Exception {
        String url = " http://127.0.0.1:8888/org.uberfire.UberfireShowcase/out.16590-4829.erraiBus?z=12&clientId=16590-4829";
        PlaceRequest placeRequest = placeRequestHistoryMapper.getPlaceRequest( url );
        assertEquals( url.substring( 0, url.indexOf( "?" ) ), placeRequest.getIdentifier() );

        Map<String, String> parameters = placeRequest.getParameters();

        assertFalse( parameters.isEmpty() );
        assertTrue( parameters.containsKey( "z" ) );
        assertTrue( parameters.containsKey( "clientId" ) );
        assertEquals( "12", parameters.get( "z" ) );
        assertEquals( "16590-4829", parameters.get( "clientId" ) );
    }

    @Test
    public void createPathPlaceRequest() throws Exception {
        final Path path = PathFactory.newPath( "file", "default://master@repo/path/to/file" );
        final PlaceRequest placeRequestOriginal = new PathPlaceRequest( path );

        PlaceRequest placeRequest = placeRequestHistoryMapper.getPlaceRequest( placeRequestOriginal.getFullIdentifier() );
        assertEquals( placeRequestOriginal.getFullIdentifier(), placeRequest.getFullIdentifier() );

        assertTrue( placeRequest.getParameters().isEmpty() );
    }

    @Test
    public void createPathPlaceRequestWithSpaces() throws Exception {
        final Path path = PathFactory.newPath( "Dummy rule.drl", "default://master@uf-playground/mortgages/src/main/resources/org/mortgages/Dummy%20rule.drl" );
        final PlaceRequest placeRequestOriginal = new PathPlaceRequest( path );

        PlaceRequest placeRequest = placeRequestHistoryMapper.getPlaceRequest( placeRequestOriginal.getFullIdentifier() );
        assertEquals( placeRequestOriginal.getFullIdentifier(), placeRequest.getFullIdentifier() );

        assertTrue( placeRequest.getParameters().isEmpty() );
    }

    @Test
    public void identifierAndParametersShouldBeUrlDecoded() throws Exception {
        PlaceRequest placeRequest = placeRequestHistoryMapper.getPlaceRequest( "place%20id?par%26am%201=value%201" );
        assertEquals( "place id", placeRequest.getIdentifier() );
        assertEquals( ImmutableMap.of( "par&am 1", "value 1" ), placeRequest.getParameters() );
    }
}
