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

package org.uberfire.java.nio.file;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;

import static org.fest.assertions.api.Assertions.*;

public class SimpleFileVisitorTest extends AbstractBaseTest {

    final AtomicInteger preDir   = new AtomicInteger();
    final AtomicInteger postDir  = new AtomicInteger();
    final AtomicInteger fileC    = new AtomicInteger();
    final AtomicInteger failFile = new AtomicInteger();

    final SimpleFileVisitor<Path> simple = new SimpleFileVisitor<Path>() {

        @Override
        public FileVisitResult preVisitDirectory( Path dir,
                                                  BasicFileAttributes attrs ) throws IOException {
            preDir.addAndGet( 1 );
            return super.preVisitDirectory( dir, attrs );
        }

        @Override
        public FileVisitResult visitFile( Path file,
                                          BasicFileAttributes attrs ) throws IOException {
            fileC.addAndGet( 1 );
            return super.visitFile( file, attrs );
        }

        @Override
        public FileVisitResult visitFileFailed( Path file,
                                                IOException exc ) throws IOException {
            failFile.addAndGet( 1 );
            return super.visitFileFailed( file, exc );
        }

        @Override
        public FileVisitResult postVisitDirectory( Path dir,
                                                   IOException exc ) throws IOException {
            postDir.addAndGet( 1 );
            return super.postVisitDirectory( dir, exc );
        }
    };

    @Test
    public void testWalker() {

        final Path dir = newTempDir( null );

        final Path file1 = Files.createTempFile( dir, "foo", "bar" );
        Files.createTempFile( dir, "foo", "bar" );

        cleanupVisitor();
        Files.walkFileTree( dir, simple );

        assertThat( preDir.get() ).isEqualTo( 1 );
        assertThat( postDir.get() ).isEqualTo( 1 );
        assertThat( fileC.get() ).isEqualTo( 2 );
        assertThat( failFile.get() ).isEqualTo( 0 );

        cleanupVisitor();
        Files.walkFileTree( file1, simple );

        assertThat( preDir.get() ).isEqualTo( 0 );
        assertThat( postDir.get() ).isEqualTo( 0 );
        assertThat( fileC.get() ).isEqualTo( 1 );
        assertThat( failFile.get() ).isEqualTo( 0 );
    }

    @Test
    public void testWalkerDeep2() {
        final Path dir = newTempDir( null );
        final Path subDir = newTempDir( dir );
        final Path subSubDir = newTempDir( subDir );
        newTempDir( subSubDir );

        cleanupVisitor();
        Files.walkFileTree( dir, simple );

        assertThat( preDir.get() ).isEqualTo( 4 );
        assertThat( postDir.get() ).isEqualTo( 4 );
        assertThat( fileC.get() ).isEqualTo( 0 );
        assertThat( failFile.get() ).isEqualTo( 0 );
    }

    @Test
    public void testWalkerDeep1() {
        final Path dir = newTempDir( null );
        final Path subDir = newTempDir( dir );
        final Path subSubDir = newTempDir( subDir );
        final Path subSubSubDir = newTempDir( subSubDir );

        Files.createTempFile( dir, "foo", "bar" );
        Files.createTempFile( dir, "foo", "bar" );

        cleanupVisitor();
        Files.walkFileTree( dir, simple );

        assertThat( preDir.get() ).isEqualTo( 4 );
        assertThat( postDir.get() ).isEqualTo( 4 );
        assertThat( fileC.get() ).isEqualTo( 2 );
        assertThat( failFile.get() ).isEqualTo( 0 );
    }

    @Test
    public void testException() {
        final Path dir = newTempDir( null );

        final Path file = Files.createTempFile( dir, "foo", "bar" );

        final IOException myException = new IOException();

        try {
            simple.visitFileFailed( file, myException );
            fail( "should throw an exception" );
        } catch ( Exception ex ) {
            assertThat( ex ).isEqualTo( myException );
        }

        try {
            simple.postVisitDirectory( file, myException );
            fail( "should throw an exception" );
        } catch ( Exception ex ) {
            assertThat( ex ).isEqualTo( myException );
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void preVisitDirectoryNull1() {
        final Path dir = newTempDir( null );
        final Path file = Files.createTempFile( dir, "foo", "bar" );

        simple.preVisitDirectory( null, Files.readAttributes( file, BasicFileAttributes.class ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void preVisitDirectoryNull2() {
        final Path dir = newTempDir( null );

        simple.preVisitDirectory( dir, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void preVisitDirectoryNull3() {
        simple.preVisitDirectory( null, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void visitFileNull1() {
        final Path dir = newTempDir( null );
        final Path file = Files.createTempFile( dir, "foo", "bar" );

        simple.visitFile( null, Files.readAttributes( file, BasicFileAttributes.class ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void visitFileNull2() {
        final Path dir = newTempDir( null );

        simple.visitFile( dir, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void visitFileNull3() {
        simple.visitFile( null, null );
    }

    @Test
    public void postVisitDirectoryNull1() {
        final Path dir = newTempDir( null );
        final Path file = Files.createTempFile( dir, "foo", "bar" );

        simple.postVisitDirectory( dir, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void postVisitDirectoryNull2() {
        simple.postVisitDirectory( null, new IOException() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void postVisitDirectoryNull3() {
        simple.postVisitDirectory( null, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void visitFileFailedNull1() {
        final Path dir = newTempDir( null );
        final Path file = Files.createTempFile( dir, "foo", "bar" );

        simple.visitFileFailed( file, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void visitFileFailedNull2() {
        simple.visitFileFailed( null, new IOException() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void visitFileFailedNull3() {
        simple.visitFileFailed( null, null );
    }

    protected void cleanupVisitor() {
        preDir.set( 0 );
        postDir.set( 0 );
        fileC.set( 0 );
        failFile.set( 0 );
    }

}
