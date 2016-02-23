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

package org.uberfire.ext.editor.commons.backend.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.VFSLockService;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.ext.editor.commons.backend.service.restriction.LockRestrictor;
import org.uberfire.ext.editor.commons.service.ValidationService;
import org.uberfire.ext.editor.commons.service.restriction.PathOperationRestriction;
import org.uberfire.ext.editor.commons.service.restrictor.CopyRestrictor;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

@RunWith(MockitoJUnitRunner.class)
public class CopyServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private User identity;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private VFSLockService lockService;

    @Mock
    private ValidationService validationService;

    @Spy
    @InjectMocks
    private CopyServiceImpl copyService;

    @Spy
    @InjectMocks
    private LockRestrictor lockRestrictor;

    private final List<String> restrictedFileNames = new ArrayList<String>();

    @Before
    public void setup() {
        when( identity.getIdentifier() ).thenReturn( "user" );

        doReturn( getPath() ).when( copyService ).copyPath( any( Path.class ), any( String.class ), any( String.class ) );
        doNothing().when( copyService ).copyPathIfExists( any( Path.class ), any( String.class ), any( String.class ) );
        doNothing().when( copyService ).startBatch( Matchers.<Collection<Path>>any() );
        doNothing().when( copyService ).endBatch();

        List<CopyRestrictor> copyRestrictors = new ArrayList<CopyRestrictor>();
        copyRestrictors.add( new CopyRestrictor() {
            @Override
            public PathOperationRestriction hasRestriction( final Path path ) {
                if ( restrictedFileNames.contains( path.getFileName() ) ) {
                    return new PathOperationRestriction() {
                        @Override
                        public String getMessage( final Path path ) {
                            return path.toURI() + " cannot be copied.";
                        }
                    };
                }

                return null;
            }
        } );
        when( copyService.getCopyRestrictors() ).thenReturn( copyRestrictors );
    }

    @Test
    public void copyRestrictedPathTest() {
        final Path path = getPath( "restricted-file.txt" );

        givenThatPathIsRestricted( path );

        try {
            whenPathIsCopied( path );
        } catch ( RuntimeException e ) {
            thenPathWasNotCopied( path, e );
        }

        thenPathWasNotCopied( path );
    }

    @Test
    public void copyUnrestrictedPathTest() {
        final Path path = getPath();

        givenThatPathIsUnrestricted( path );
        whenPathIsCopied( path );
        thenPathWasCopied( path );
    }

    @Test
    public void copyRestrictedPathIfExistsTest() {
        final List<Path> paths = new ArrayList<Path>();
        paths.add( getPath( "file0.txt" ) );
        paths.add( getPath( "file1.txt" ) );
        paths.add( getPath( "file2.txt" ) );

        givenThatPathIsUnrestricted( paths.get( 0 ) );
        givenThatPathIsRestricted( paths.get( 1 ) );
        givenThatPathIsUnrestricted( paths.get( 2 ) );

        try {
            whenPathsAreCopiedIfExists( paths );
        } catch ( RuntimeException e ) {
            thenPathWasNotCopiedIfExists( paths.get( 1 ), e );
        }

        thenPathWasCopiedIfExists( paths.get( 0 ) );
        thenPathWasNotCopiedIfExists( paths.get( 1 ) );

        // This will not be copied because the process stops when some exception is raised.
        thenPathWasNotCopiedIfExists( paths.get( 2 ) );
    }

    @Test
    public void copyUnrestrictedPathIfExistsTest() {
        final List<Path> paths = new ArrayList<Path>();
        paths.add( getPath( "file0.txt" ) );
        paths.add( getPath( "file1.txt" ) );
        paths.add( getPath( "file2.txt" ) );

        givenThatPathIsUnrestricted( paths.get( 0 ) );
        givenThatPathIsUnrestricted( paths.get( 1 ) );
        givenThatPathIsUnrestricted( paths.get( 2 ) );

        whenPathsAreCopiedIfExists( paths );

        thenPathWasCopiedIfExists( paths.get( 0 ) );
        thenPathWasCopiedIfExists( paths.get( 1 ) );
        thenPathWasCopiedIfExists( paths.get( 2 ) );
    }

    @Test
    public void pathHasNoCopyRestrictionTest() {
        final Path path = getPath();

        givenThatPathIsUnrestricted( path );
        boolean hasRestriction = whenPathIsCheckedForCopyRestrictions( path );
        thenPathHasNoCopyRestrictions( hasRestriction );
    }

    @Test
    public void pathHasCopyRestrictionTest() {
        final Path path = getPath();

        givenThatPathIsRestricted( path );
        boolean hasRestriction = whenPathIsCheckedForCopyRestrictions( path );
        thenPathHasCopyRestrictions( hasRestriction );
    }

    private void givenThatPathIsRestricted( final Path path ) {
        restrictedFileNames.add( path.getFileName() );
    }

    private void givenThatPathIsUnrestricted( final Path path ) {
        restrictedFileNames.remove( path.getFileName() );
    }

    private void whenPathIsCopied( final Path path ) {
        copyService.copy( path, "newName", "comment" );
    }

    private void whenPathsAreCopiedIfExists( final Collection<Path> paths ) {
        copyService.copyIfExists( paths, "newName", "comment" );
    }

    private boolean whenPathIsCheckedForCopyRestrictions( final Path path ) {
        return copyService.hasRestriction( path );
    }

    private void thenPathWasCopied( final Path path ) {
        verify( copyService ).copyPath( eq( path ), any( String.class ), any( String.class ) );
    }

    private void thenPathWasNotCopied( final Path path ) {
        verify( copyService, never() ).copyPath( eq( path ), any( String.class ), any( String.class ) );
    }

    private void thenPathWasNotCopied( final Path path,
                                        final RuntimeException e ) {
        assertEquals( path.toURI() + " cannot be copied.", e.getMessage() );
    }

    private void thenPathWasCopiedIfExists( final Path path ) {
        verify( copyService ).copyPathIfExists( eq( path ), any( String.class ), any( String.class ) );
    }

    private void thenPathWasNotCopiedIfExists( final Path path ) {
        verify( copyService, never() ).copyPathIfExists( eq( path ), any( String.class ), any( String.class ) );
    }

    private void thenPathWasNotCopiedIfExists( final Path path,
                                                final RuntimeException e ) {
        assertEquals( path.toURI() + " cannot be copied.", e.getMessage() );
    }

    private void thenPathHasNoCopyRestrictions( final boolean hasRestriction ) {
        assertFalse( hasRestriction );
    }

    private void thenPathHasCopyRestrictions( final boolean hasRestriction ) {
        assertTrue( hasRestriction );
    }

    private Path getPath() {
        return getPath( "file.txt" );
    }

    private Path getPath( String fileName ) {
        return PathFactory.newPath( fileName, "file://tmp/" + fileName );
    }
}
