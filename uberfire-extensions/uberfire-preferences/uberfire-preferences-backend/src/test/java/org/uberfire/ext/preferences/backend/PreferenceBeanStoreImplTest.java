/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.preferences.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.preferences.shared.PreferenceScope;
import org.uberfire.ext.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.ext.preferences.shared.PreferenceStore;
import org.uberfire.ext.preferences.shared.bean.BasePreference;
import org.uberfire.ext.preferences.shared.bean.BasePreferencePortable;
import org.uberfire.ext.preferences.shared.bean.PreferenceHierarchyElement;
import org.uberfire.ext.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PreferenceBeanStoreImplTest {

    private PreferenceStore preferenceStore;

    private PreferenceBeanStoreImpl preferenceBeanStoreImpl;

    private PreferenceScopeResolutionStrategy preferenceScopeResolutionStrategy;

    private PreferenceScopeResolutionStrategyInfo scopeInfo;

    private PreferenceScope lastScope;

    @Before
    public void setup() {
        preferenceScopeResolutionStrategy = mock( PreferenceScopeResolutionStrategy.class );
        preferenceStore = mock( PreferenceStore.class );
        preferenceBeanStoreImpl = spy( new PreferenceBeanStoreImpl( preferenceStore, preferenceScopeResolutionStrategy, null ) );

        lastScope = mock( PreferenceScope.class );
        scopeInfo = new PreferenceScopeResolutionStrategyInfo( Arrays.asList( lastScope ), mock( PreferenceScope.class ) );
        doReturn( scopeInfo ).when( preferenceScopeResolutionStrategy ).getInfo();

        doAnswer( invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            Class<?> clazz = (Class<?>) args[ 0 ];
            return getPortablePreferenceByClass( clazz );
        } ).when( preferenceBeanStoreImpl ).lookupPortablePreference( any( Class.class ) );
    }

    @Test
    public void loadTest() {
        MyPreference myPreference = new MyPreferencePortableGeneratedImpl();
        MySharedPreference mySharedPreference = new MySharedPreferencePortableGeneratedImpl();
        MySharedPreference2 mySharedPreference2 = new MySharedPreference2PortableGeneratedImpl();

        doReturn( myPreference ).when( preferenceStore ).get( MyPreference.class.getSimpleName() );
        doReturn( mySharedPreference ).when( preferenceStore ).get( MySharedPreference.class.getSimpleName() );
        doReturn( mySharedPreference2 ).when( preferenceStore ).get( MySharedPreference2.class.getSimpleName() );

        final MyPreferencePortableGeneratedImpl loadedMyPreference = preferenceBeanStoreImpl.load( new MyPreferencePortableGeneratedImpl() );
        final MySharedPreference2PortableGeneratedImpl loadedMySharedPreference2 = preferenceBeanStoreImpl.load( new MySharedPreference2PortableGeneratedImpl() );

        verify( preferenceStore, times( 3 ) ).get( anyString() );

        verify( preferenceStore ).get( MyPreference.class.getSimpleName() );
        verify( preferenceStore ).get( MySharedPreference.class.getSimpleName() );
        verify( preferenceStore ).get( MySharedPreference2.class.getSimpleName() );

        assertSame( myPreference, loadedMyPreference );
        assertSame( mySharedPreference, loadedMyPreference.mySharedPreference );
        assertSame( mySharedPreference2, loadedMySharedPreference2 );
    }

    @Test
    public void saveTest() {
        final MyPreferencePortableGeneratedImpl myPreference = preferenceBeanStoreImpl.load( new MyPreferencePortableGeneratedImpl() );

        preferenceBeanStoreImpl.save( myPreference );

        verify( preferenceStore, times( 2 ) ).put( any( PreferenceScope.class ), anyString(), any( Object.class ) );

        verify( preferenceStore ).put( eq( scopeInfo.defaultScope() ), eq( MyPreference.class.getSimpleName() ), eq( myPreference ) );
        verify( preferenceStore ).put( eq( scopeInfo.defaultScope() ), eq( MySharedPreference.class.getSimpleName() ), eq( myPreference.mySharedPreference ) );
    }

    @Test
    public void saveDefaultValueTest() {
        final MyPreferencePortableGeneratedImpl myPreference = new MyPreferencePortableGeneratedImpl();

        preferenceBeanStoreImpl.saveDefaultValue( (MyPreferencePortableGeneratedImpl) myPreference.defaultValue( myPreference ) );

        verify( preferenceStore, times( 2 ) ).put( any( PreferenceScope.class ), anyString(), any( Object.class ) );

        verify( preferenceStore ).put( eq( lastScope ), eq( MyPreference.class.getSimpleName() ), eq( myPreference ) );
        verify( preferenceStore ).put( eq( lastScope ), eq( MySharedPreference.class.getSimpleName() ), eq( myPreference.mySharedPreference ) );
    }

    @Test
    public void saveCollectionTest() {
        final List<BasePreferencePortable<? extends BasePreference<?>>> preferencesToSave = getRootPortablePreferences();
        final MyPreference myPreference = (MyPreference) preferencesToSave.get( 0 );
        final MySharedPreference2 mySharedPreference2 = (MySharedPreference2) preferencesToSave.get( 1 );

        preferenceBeanStoreImpl.save( preferencesToSave );

        verify( preferenceStore, times( 3 ) ).put( any( PreferenceScope.class ), anyString(), any( Object.class ) );

        verify( preferenceStore ).put( scopeInfo.defaultScope(), MyPreference.class.getSimpleName(), myPreference );
        verify( preferenceStore ).put( scopeInfo.defaultScope(), MySharedPreference.class.getSimpleName(), myPreference.mySharedPreference );
        verify( preferenceStore ).put( eq( scopeInfo.defaultScope() ), eq( MySharedPreference2.class.getSimpleName() ), same( mySharedPreference2 ) );
    }

    @Test
    public void buildHierarchyStructureTest() {
        final List<BasePreferencePortable<? extends BasePreference<?>>> rootPreferences = getRootPortablePreferences();
        final MyPreferencePortableGeneratedImpl myPreference = (MyPreferencePortableGeneratedImpl) rootPreferences.get( 0 );

        doReturn( getPortablePreferences() ).when( preferenceBeanStoreImpl ).getPortablePreferences();

        final PreferenceHierarchyElement<?> preferenceHierarchyElement = preferenceBeanStoreImpl.buildHierarchyStructureForPreference( myPreference.identifier() );

        final PreferenceHierarchyElement<?> firstElement = preferenceHierarchyElement;
        assertEquals( myPreference.identifier(), firstElement.getPortablePreference().identifier() );
        assertTrue( firstElement.isRoot() );
        assertFalse( firstElement.isShared() );
        assertEquals( 2, firstElement.getChildren().size() );

        final PreferenceHierarchyElement<?> firstElementFirstChild = firstElement.getChildren().get( 0 );
        assertEquals( ( (MyInnerPreferencePortableGeneratedImpl) myPreference.myInnerPreference ).identifier(), firstElementFirstChild.getPortablePreference().identifier() );
        assertFalse( firstElementFirstChild.isRoot() );
        assertFalse( firstElementFirstChild.isShared() );
        assertEquals( 0, firstElementFirstChild.getChildren().size() );

        final PreferenceHierarchyElement<?> firstElementSecondChild = firstElement.getChildren().get( 1 );
        assertEquals( ( (MySharedPreferencePortableGeneratedImpl) myPreference.mySharedPreference ).identifier(), firstElementSecondChild.getPortablePreference().identifier() );
        assertFalse( firstElementSecondChild.isRoot() );
        assertTrue( firstElementSecondChild.isShared() );
        assertEquals( 1, firstElementSecondChild.getChildren().size() );

        final PreferenceHierarchyElement<?> firstElementSecondChildFirstChild = firstElementSecondChild.getChildren().get( 0 );
        assertEquals( ( (MyInnerPreference2PortableGeneratedImpl) myPreference.mySharedPreference.myInnerPreference2 ).identifier(), firstElementSecondChildFirstChild.getPortablePreference().identifier() );
        assertFalse( firstElementSecondChildFirstChild.isRoot() );
        assertFalse( firstElementSecondChildFirstChild.isShared() );
        assertEquals( 1, firstElementSecondChildFirstChild.getChildren().size() );

        final PreferenceHierarchyElement<?> firstElementSecondChildFirstChildFirstChild = firstElementSecondChildFirstChild.getChildren().get( 0 );
        assertEquals( "MySharedPreference2", firstElementSecondChildFirstChildFirstChild.getPortablePreference().identifier() );
        assertTrue( firstElementSecondChildFirstChildFirstChild.isRoot() );
        assertFalse( firstElementSecondChildFirstChildFirstChild.isShared() );
        assertEquals( 0, firstElementSecondChildFirstChildFirstChild.getChildren().size() );
    }

    private List<BasePreferencePortable<? extends BasePreference<?>>> getRootPortablePreferences() {
        final MyPreferencePortableGeneratedImpl myPreference = preferenceBeanStoreImpl.load( new MyPreferencePortableGeneratedImpl() );
        final MySharedPreference2PortableGeneratedImpl mySharedPreference2 = preferenceBeanStoreImpl.load( new MySharedPreference2PortableGeneratedImpl() );

        List<BasePreferencePortable<? extends BasePreference<?>>> rootPreferences = new ArrayList<>();
        rootPreferences.add( myPreference );
        rootPreferences.add( mySharedPreference2 );

        return rootPreferences;
    }

    private List<BasePreferencePortable<?>> getPortablePreferences() {
        List<BasePreferencePortable<?>> portablePreferences = new ArrayList<>();

        portablePreferences.add( getPortablePreferenceByClass( MyPreference.class ) );
        portablePreferences.add( getPortablePreferenceByClass( MyInnerPreference.class ) );
        portablePreferences.add( getPortablePreferenceByClass( MyInnerPreference2.class ) );
        portablePreferences.add( getPortablePreferenceByClass( MySharedPreference.class ) );
        portablePreferences.add( getPortablePreferenceByClass( MySharedPreference2.class ) );

        return portablePreferences;
    }

    private BasePreferencePortable<?> getPortablePreferenceByClass( final Class<?> clazz ) {
        if ( MyPreference.class.equals( clazz ) ) {
            return new MyPreferencePortableGeneratedImpl();
        } else if ( MyInnerPreference.class.equals( clazz ) ) {
            return new MyInnerPreferencePortableGeneratedImpl();
        } else if ( MyInnerPreference2.class.equals( clazz ) ) {
            return new MyInnerPreference2PortableGeneratedImpl();
        } else if ( MySharedPreference.class.equals( clazz ) ) {
            return new MySharedPreferencePortableGeneratedImpl();
        } else if ( MySharedPreference2.class.equals( clazz ) ) {
            return new MySharedPreference2PortableGeneratedImpl();
        }

        return null;
    }
}
