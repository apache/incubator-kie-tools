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

    @Before
    public void setup() {
        preferenceScopeResolutionStrategy = mock( PreferenceScopeResolutionStrategy.class );
        preferenceStore = mock( PreferenceStore.class );
        preferenceBeanStoreImpl = spy( new PreferenceBeanStoreImpl( preferenceStore, preferenceScopeResolutionStrategy, null ) );

        PreferenceScopeResolutionStrategyInfo scopeInfo = new PreferenceScopeResolutionStrategyInfo( Arrays.asList( mock( PreferenceScope.class ) ), mock( PreferenceScope.class ) );
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
        MyInheritedPreference myInheritedPreference = new MyInheritedPreferencePortableGeneratedImpl();
        MyInheritedPreference2 myInheritedPreference2 = new MyInheritedPreference2PortableGeneratedImpl();

        doReturn( myPreference ).when( preferenceStore ).get( MyPreference.class.getName() );
        doReturn( myInheritedPreference ).when( preferenceStore ).get( MyInheritedPreference.class.getName() );
        doReturn( myInheritedPreference2 ).when( preferenceStore ).get( MyInheritedPreference2.class.getName() );

        final MyPreferencePortableGeneratedImpl loadedMyPreference = preferenceBeanStoreImpl.load( new MyPreferencePortableGeneratedImpl() );

        verify( preferenceStore, times( 3 ) ).get( anyString() );

        verify( preferenceStore ).get( MyPreference.class.getName() );
        verify( preferenceStore ).get( MyInheritedPreference.class.getName() );
        verify( preferenceStore ).get( MyInheritedPreference2.class.getName() );

        assertSame( myPreference, loadedMyPreference );
        assertSame( myInheritedPreference, loadedMyPreference.myInheritedPreference );
        assertSame( myInheritedPreference2, loadedMyPreference.myInheritedPreference.myInnerPreference2.myInheritedPreference2 );
    }

    @Test
    public void saveTest() {
        final MyPreferencePortableGeneratedImpl myPreference = preferenceBeanStoreImpl.load( new MyPreferencePortableGeneratedImpl() );

        preferenceBeanStoreImpl.save( myPreference );

        verify( preferenceStore, times( 3 ) ).put( anyString(), any( Object.class ) );

        verify( preferenceStore ).put( MyPreference.class.getName(), myPreference );
        verify( preferenceStore ).put( MyInheritedPreference.class.getName(), myPreference.myInheritedPreference );
        verify( preferenceStore ).put( MyInheritedPreference2.class.getName(), myPreference.myInheritedPreference.myInnerPreference2.myInheritedPreference2 );
    }

    @Test
    public void saveDefaultValueTest() {
        final MyPreferencePortableGeneratedImpl myPreference = new MyPreferencePortableGeneratedImpl();

        preferenceBeanStoreImpl.saveDefaultValue( (MyPreferencePortableGeneratedImpl) myPreference.defaultValue( myPreference ) );

        verify( preferenceStore, times( 1 ) ).put( any( PreferenceScope.class ), anyString(), any( Object.class ) );

        verify( preferenceStore ).put( any( PreferenceScope.class ), eq( MyPreference.class.getName() ), eq( myPreference ) );
    }

    @Test
    public void saveCollectionTest() {
        final List<BasePreferencePortable<? extends BasePreference<?>>> preferencesToSave = getRootPortablePreferences();
        final MyPreference myPreference = (MyPreference) preferencesToSave.get( 0 );
        final MyInheritedPreference2 myInheritedPreference2 = (MyInheritedPreference2) preferencesToSave.get( 1 );

        preferenceBeanStoreImpl.save( preferencesToSave );

        verify( preferenceStore, times( 4 ) ).put( anyString(), any( Object.class ) );

        verify( preferenceStore ).put( MyPreference.class.getName(), myPreference );
        verify( preferenceStore ).put( MyInheritedPreference.class.getName(), myPreference.myInheritedPreference );
        verify( preferenceStore ).put( eq( MyInheritedPreference2.class.getName() ), same( myPreference.myInheritedPreference.myInnerPreference2.myInheritedPreference2 ) );
        verify( preferenceStore ).put( eq( MyInheritedPreference2.class.getName() ), same( myInheritedPreference2 ) );
    }

    @Test
    public void buildHierarchyStructureTest() {
        final List<BasePreferencePortable<? extends BasePreference<?>>> rootPreferences = getRootPortablePreferences();
        final MyPreferencePortableGeneratedImpl myPreference = (MyPreferencePortableGeneratedImpl) rootPreferences.get( 0 );
        final MyInheritedPreference2PortableGeneratedImpl myInheritedPreference2 = (MyInheritedPreference2PortableGeneratedImpl) rootPreferences.get( 1 );

        doReturn( rootPreferences ).when( preferenceBeanStoreImpl ).getRootPortablePreferences();

        final List<PreferenceHierarchyElement<?>> preferenceHierarchyElements = preferenceBeanStoreImpl.buildHierarchyStructure();

        assertEquals( 2, preferenceHierarchyElements.size() );

        final PreferenceHierarchyElement<?> firstElement = preferenceHierarchyElements.get( 0 );
        assertEquals( myPreference.key(), firstElement.getPortablePreference().key() );
        assertTrue( firstElement.isRoot() );
        assertFalse( firstElement.isInherited() );
        assertEquals( 2, firstElement.getChildren().size() );

        final PreferenceHierarchyElement<?> firstElementFirstChild = firstElement.getChildren().get( 0 );
        assertEquals( ( (MyInnerPreferencePortableGeneratedImpl) myPreference.myInnerPreference ).key(), firstElementFirstChild.getPortablePreference().key() );
        assertFalse( firstElementFirstChild.isRoot() );
        assertFalse( firstElementFirstChild.isInherited() );
        assertEquals( 0, firstElementFirstChild.getChildren().size() );

        final PreferenceHierarchyElement<?> firstElementSecondChild = firstElement.getChildren().get( 1 );
        assertEquals( ( (MyInheritedPreferencePortableGeneratedImpl) myPreference.myInheritedPreference ).key(), firstElementSecondChild.getPortablePreference().key() );
        assertFalse( firstElementSecondChild.isRoot() );
        assertTrue( firstElementSecondChild.isInherited() );
        assertEquals( 1, firstElementSecondChild.getChildren().size() );

        final PreferenceHierarchyElement<?> firstElementSecondChildFirstChild = firstElementSecondChild.getChildren().get( 0 );
        assertEquals( ( (MyInnerPreference2PortableGeneratedImpl) myPreference.myInheritedPreference.myInnerPreference2 ).key(), firstElementSecondChildFirstChild.getPortablePreference().key() );
        assertFalse( firstElementSecondChildFirstChild.isRoot() );
        assertFalse( firstElementSecondChildFirstChild.isInherited() );
        assertEquals( 1, firstElementSecondChildFirstChild.getChildren().size() );

        final PreferenceHierarchyElement<?> firstElementSecondChildFirstChildFirstChild = firstElementSecondChildFirstChild.getChildren().get( 0 );
        assertEquals( ( (MyInheritedPreference2PortableGeneratedImpl) myPreference.myInheritedPreference.myInnerPreference2.myInheritedPreference2 ).key(), firstElementSecondChildFirstChildFirstChild.getPortablePreference().key() );
        assertFalse( firstElementSecondChildFirstChildFirstChild.isRoot() );
        assertTrue( firstElementSecondChildFirstChildFirstChild.isInherited() );
        assertEquals( 0, firstElementSecondChildFirstChildFirstChild.getChildren().size() );

        final PreferenceHierarchyElement<?> secondElement = preferenceHierarchyElements.get( 1 );
        assertEquals( myInheritedPreference2.key(), secondElement.getPortablePreference().key() );
        assertTrue( secondElement.isRoot() );
        assertFalse( secondElement.isInherited() );
        assertEquals( 0, secondElement.getChildren().size() );
    }

    private List<BasePreferencePortable<? extends BasePreference<?>>> getRootPortablePreferences() {
        final MyPreferencePortableGeneratedImpl myPreference = preferenceBeanStoreImpl.load( new MyPreferencePortableGeneratedImpl() );
        final MyInheritedPreference2PortableGeneratedImpl myInheritedPreference2 = preferenceBeanStoreImpl.load( new MyInheritedPreference2PortableGeneratedImpl() );

        List<BasePreferencePortable<? extends BasePreference<?>>> rootPreferences = new ArrayList<>();
        rootPreferences.add( myPreference );
        rootPreferences.add( myInheritedPreference2 );

        return rootPreferences;
    }

    private BasePreferencePortable<?> getPortablePreferenceByClass( final Class<?> clazz ) {
        if ( MyPreference.class.equals( clazz ) ) {
            return new MyPreferencePortableGeneratedImpl();
        } else if ( MyInnerPreference.class.equals( clazz ) ) {
            return new MyInnerPreferencePortableGeneratedImpl();
        } else if ( MyInnerPreference2.class.equals( clazz ) ) {
            return new MyInnerPreference2PortableGeneratedImpl();
        } else if ( MyInheritedPreference.class.equals( clazz ) ) {
            return new MyInheritedPreferencePortableGeneratedImpl();
        } else if ( MyInheritedPreference2.class.equals( clazz ) ) {
            return new MyInheritedPreference2PortableGeneratedImpl();
        }

        return null;
    }
}
