/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.java.nio.base;

import static org.uberfire.commons.data.Pair.newPair;
import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.commons.validation.Preconditions.checkInstanceOf;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.uberfire.apache.commons.io.FilenameUtils;
import org.uberfire.commons.data.Pair;
import org.uberfire.java.nio.EncodingUtil;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.ClosedWatchServiceException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.InvalidPathException;
import org.uberfire.java.nio.file.LinkOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.WatchEvent.Kind;
import org.uberfire.java.nio.file.WatchEvent.Modifier;
import org.uberfire.java.nio.file.WatchKey;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.file.attribute.AttributeView;

public abstract class AbstractPath<FS extends FileSystem>
        implements Path,
                   AttrHolder {

    public static final Pattern WINDOWS_DRIVER = Pattern.compile( "^/?[A-Z|a-z]+(:).*" );
    public static final String DEFAULT_WINDOWS_DRIVER = "C:";

    protected final FS fs;
    protected final boolean usesWindowsFormat;

    protected final boolean isAbsolute;
    protected final byte[] path;
    protected final boolean isRoot;
    protected final boolean isRealPath;
    protected final boolean isNormalized;
    protected final String host;

    protected String toStringFormat;
    protected File file = null;

    protected final List<Pair<Integer, Integer>> offsets = new ArrayList<Pair<Integer, Integer>>();

    protected final AttrsStorage attrsStorage = new AttrsStorageImpl();

    protected abstract Path newPath( FS fs,
                                     String substring,
                                     String host,
                                     boolean realPath,
                                     boolean isNormalized );

    protected abstract Path newRoot( FS fs,
                                     String substring,
                                     String host,
                                     boolean realPath );

    protected AbstractPath( final FS fs,
                            final File file ) {
        this( checkNotNull( "fs", fs ), checkNotNull( "file", file ).getAbsolutePath(), "", false, false, true );
    }

    protected AbstractPath( final FS fs,
                            final String path,
                            final String host,
                            boolean isRoot,
                            boolean isRealPath,
                            boolean isNormalized ) {
        checkNotNull( "path", path );
        this.fs = checkNotNull( "fs", fs );
        this.host = checkNotNull( "host", host );
        this.isRealPath = isRealPath;
        this.isNormalized = isNormalized;
        this.usesWindowsFormat = path.matches( ".*\\\\.*" );

        final RootInfo rootInfo = setupRoot( fs, path, host, isRoot );
        this.path = rootInfo.path;

        checkNotNull( "rootInfo", rootInfo );

        this.isAbsolute = rootInfo.isAbsolute;

        int lastOffset = rootInfo.startOffset;
        for ( int i = lastOffset; i < this.path.length; i++ ) {
            final byte b = this.path[ i ];
            if ( b == getSeparator() ) {
                offsets.add( newPair( lastOffset, i ) );
                i++;
                lastOffset = i;
            }
        }

        if ( lastOffset < this.path.length ) {
            offsets.add( newPair( lastOffset, this.path.length ) );
        }

        this.isRoot = rootInfo.isRoot;
    }

    protected abstract RootInfo setupRoot( final FS fs,
                                           final String path,
                                           final String host,
                                           final boolean isRoot );

    @Override
    public FS getFileSystem() {
        return fs;
    }

    @Override
    public boolean isAbsolute() {
        return isAbsolute;
    }

    @Override
    public Path getRoot() {
        if ( isRoot ) {
            return this;
        }
        if ( isAbsolute || !host.isEmpty() ) {
            return newRoot( fs, substring( -1 ), host, isRealPath );
        }
        return null;
    }

    private String substring( int index ) {
        final byte[] result;
        if ( index == -1 ) {
            result = new byte[ offsets.get( 0 ).getK1() ];
            System.arraycopy( path, 0, result, 0, result.length );
        } else {
            final Pair<Integer, Integer> offset = offsets.get( index );
            result = new byte[ offset.getK2().intValue() - offset.getK1().intValue() ];
            System.arraycopy( path, offset.getK1(), result, 0, result.length );
        }

        return new String( result );
    }

    private String substring( int beginIndex,
                              int endIndex ) {
        final int initPos;
        if ( beginIndex == -1 ) {
            initPos = 0;
        } else {
            initPos = offsets.get( beginIndex ).getK1();
        }
        final Pair<Integer, Integer> offsetEnd = offsets.get( endIndex );
        final byte[] result = new byte[ offsetEnd.getK2().intValue() - initPos ];
        System.arraycopy( path, initPos, result, 0, result.length );

        return new String( result );
    }

    @Override
    public Path getFileName() {
        if ( getNameCount() == 0 ) {
            return null;
        }
        return getName( getNameCount() - 1 );
    }

    @Override
    public Path getParent() {
        if ( getNameCount() <= 0 ) {
            return null;
        }
        if ( getNameCount() == 1 ) {
            return getRoot();
        }
        return newPath( fs, substring( -1, getNameCount() - 2 ), host, isRealPath, isNormalized );
    }

    @Override
    public int getNameCount() {
        return offsets.size();
    }

    @Override
    public Path getName( int index ) throws IllegalArgumentException {
        if ( isRoot && index > 0 ) {
            throw new IllegalArgumentException();
        }
        if ( index < 0 ) {
            throw new IllegalArgumentException();
        }
        if ( index >= offsets.size() ) {
            throw new IllegalArgumentException();
        }

        return newPath( fs, substring( index ), host, isRealPath, false );
    }

    @Override
    public Path subpath( int beginIndex,
                         int endIndex ) throws IllegalArgumentException {
        if ( beginIndex < 0 ) {
            throw new IllegalArgumentException();
        }
        if ( beginIndex >= offsets.size() ) {
            throw new IllegalArgumentException();
        }
        if ( endIndex > offsets.size() ) {
            throw new IllegalArgumentException();
        }
        if ( beginIndex >= endIndex ) {
            throw new IllegalArgumentException();
        }

        return newPath( fs, substring( beginIndex, endIndex - 1 ), host, isRealPath, false );
    }

    @Override
    public URI toUri() throws IOException, SecurityException {
        if ( !isAbsolute() ) {
            return toAbsolutePath().toUri();
        }
        if ( fs.provider().isDefault() && !isRealPath ) {
            return URI.create( "default://" + host + toURIString() );
        }
        return URI.create( fs.provider().getScheme() + "://" + host + toURIString() );
    }

    private String toURIString() {
        if ( usesWindowsFormat ) {
            return encodePath( "/" + toString().replace( "\\", "/" ) );
        }
        return encodePath( new String( path ) );
    }

    private String encodePath( final String s ) {
        return EncodingUtil.encodePath(s);
    }
    
    @Override
    public Path toAbsolutePath() throws IOException, SecurityException {
        if ( isAbsolute() ) {
            return this;
        }
        if ( host.isEmpty() ) {
            return newPath( fs, FilenameUtils.normalize( defaultDirectory() + toString(), !usesWindowsFormat ), host, isRealPath, true );
        }
        return newPath( fs, defaultDirectory() + toString( false ), host, isRealPath, true );
    }

    protected abstract String defaultDirectory();

    @Override
    public Path toRealPath( final LinkOption... options )
            throws IOException, SecurityException {
        if ( isRealPath ) {
            return this;
        }
        return newPath( fs, FilenameUtils.normalize( toString(), !usesWindowsFormat ), host, true, true );
    }

    @Override
    public Iterator<Path> iterator() {
        return new Iterator<Path>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < getNameCount();
            }

            @Override
            public Path next() {
                if ( i < getNameCount() ) {
                    Path result = getName( i );
                    i++;
                    return result;
                } else {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean startsWith( final Path other ) {
        checkNotNull( "other", other );

        if ( !( other instanceof AbstractPath ) ) {
            return false;
        }

        final AbstractPath<?> that = (AbstractPath) other;

        if ( that.path.length > path.length ) {
            return false;
        }

        int thisOffsetCount = getNameCount();
        int thatOffsetCount = that.getNameCount();

        if ( thatOffsetCount > thisOffsetCount ) {
            return false;
        }

        if ( ( thatOffsetCount == thisOffsetCount ) &&
                ( path.length != that.path.length ) ) {
            return false;
        }

        for ( int i = 0; i < thatOffsetCount; i++ ) {
            final Pair<Integer, Integer> o1 = offsets.get( i );
            final Pair<Integer, Integer> o2 = that.offsets.get( i );
            if ( !o1.equals( o2 ) ) {
                return false;
            }
        }

        int i = 0;
        while ( i < that.path.length ) {
            if ( this.path[ i ] != that.path[ i ] ) {
                return false;
            }
            i++;
        }

        if ( i < path.length && this.path[ i ] != fs.getSeparator().charAt( 0 ) ) {
            return false;
        }

        return true;
    }

    @Override
    public boolean startsWith( final String other ) throws InvalidPathException {
        checkNotNull( "other", other );
        return startsWith( getFileSystem().getPath( other ) );
    }

    @Override
    public boolean endsWith( final Path other ) {
        checkNotNull( "other", other );

        if ( !( other instanceof AbstractPath ) ) {
            return false;
        }

        final AbstractPath<?> that = (AbstractPath) other;

        int thisLen = path.length;
        int thatLen = that.path.length;

        if ( thatLen > thisLen ) {
            return false;
        }

        if ( thisLen > 0 && thatLen == 0 ) {
            return false;
        }

        if ( that.isAbsolute() && !this.isAbsolute() ) {
            return false;
        }

        int thisOffsetCount = getNameCount();
        int thatOffsetCount = that.getNameCount();

        if ( thatOffsetCount > thisOffsetCount ) {
            return false;
        } else {
            if ( thatOffsetCount == thisOffsetCount ) {
                if ( thisOffsetCount == 0 ) {
                    return true;
                }
                int expectedLen = thisLen;
                if ( this.isAbsolute() && !that.isAbsolute() ) {
                    expectedLen--;
                }
                if ( thatLen != expectedLen ) {
                    return false;
                }
            } else {
                if ( that.isAbsolute() ) {
                    return false;
                }
            }
        }

        int thisPos = offsets.get( thisOffsetCount - thatOffsetCount ).getK1();
        int thatPos = that.offsets.get( 0 ).getK1();

        if ( ( thatLen - thatPos ) != ( thisLen - thisPos ) ) {
            return false;
        }

        while ( thatPos < thatLen ) {
            if ( this.path[ thisPos++ ] != that.path[ thatPos++ ] ) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean endsWith( final String other ) throws InvalidPathException {
        checkNotNull( "other", other );
        return endsWith( getFileSystem().getPath( other ) );
    }

    @Override
    public Path normalize() {
        if ( isNormalized ) {
            return this;
        }

        return newPath( fs, FilenameUtils.normalize( new String( path ), !usesWindowsFormat ), host, isRealPath, true );
    }

    @Override
    public Path resolve( final Path other ) {
        checkNotNull( "other", other );
        if ( other.isAbsolute() ) {
            return other;
        }
        if ( other.toString().trim().length() == 0 ) {
            return this;
        }

        final StringBuilder sb = new StringBuilder();
        sb.append( new String( path ) );
        if ( path[ path.length - 1 ] != getSeparator() ) {
            sb.append( getSeparator() );
        }
        sb.append( other.toString() );

        return newPath( fs, sb.toString(), host, isRealPath, false );
    }

    @Override
    public Path resolve( final String other ) throws InvalidPathException {
        checkNotNull( "other", other );
        return resolve( newPath( fs, other, host, isRealPath, false ) );
    }

    @Override
    public Path resolveSibling( final Path other ) {
        checkNotNull( "other", other );

        final Path parent = this.getParent();
        if ( parent == null || other.isAbsolute() ) {
            return other;
        }

        return parent.resolve( other );
    }

    @Override
    public Path resolveSibling( final String other ) throws InvalidPathException {
        checkNotNull( "other", other );

        return resolveSibling( newPath( fs, other, host, isRealPath, false ) );
    }

    @Override
    public Path relativize( final Path otherx ) throws IllegalArgumentException {
        checkNotNull( "otherx", otherx );
        final AbstractPath other = checkInstanceOf( "otherx", otherx, AbstractPath.class );

        if ( this.equals( other ) ) {
            return emptyPath();
        }

        if ( isAbsolute() != other.isAbsolute() ) {
            throw new IllegalArgumentException();
        }

        if ( isAbsolute() && !this.getRoot().equals( other.getRoot() ) ) {
            throw new IllegalArgumentException();
        }

        if ( this.path.length == 0 ) {
            return other;
        }

        int n = ( getNameCount() > other.getNameCount() ) ? other.getNameCount() : getNameCount();
        int i = 0;
        while ( i < n ) {
            if ( !this.getName( i ).equals( other.getName( i ) ) ) {
                break;
            }
            i++;
        }

        int numberOfDots = getNameCount() - i;

        if ( numberOfDots == 0 && i < other.getNameCount() ) {
            return other.subpath( i, other.getNameCount() );
        }

        final StringBuilder sb = new StringBuilder();
        while ( numberOfDots > 0 ) {
            sb.append( ".." );
            if ( numberOfDots > 1 ) {
                sb.append( getSeparator() );
            }
            numberOfDots--;
        }

        if ( i < other.getNameCount() ) {
            if ( sb.length() > 0 ) {
                sb.append( getSeparator() );
            }
            sb.append( ( (AbstractPath<FS>) other.subpath( i, other.getNameCount() ) ).toString( false ) );
        }

        return newPath( fs, sb.toString(), host, isRealPath, false );
    }

    private Path emptyPath() {
        return newPath( fs, "", host, isRealPath, true );
    }

    @Override
    public int compareTo( final Path other ) {
        checkNotNull( "other", other );
        throw new UnsupportedOperationException();
    }

    @Override
    public WatchKey register( WatchService watcher,
                              Kind<?>[] events,
                              Modifier... modifiers )
            throws UnsupportedOperationException, IllegalArgumentException,
            ClosedWatchServiceException, IOException, SecurityException {
        return watcher.poll();
    }

    @Override
    public WatchKey register( WatchService watcher,
                              Kind<?>... events )
            throws UnsupportedOperationException, IllegalArgumentException,
            ClosedWatchServiceException, IOException, SecurityException {
        return watcher.poll();
    }

    @Override
    public String toString() {
        if ( toStringFormat == null ) {
            toStringFormat = toString( false );
        }
        return toStringFormat;
    }

    public String toString( boolean addHost ) {
        if ( !addHost || host.isEmpty() ) {
            return new String( path );
        }
        if ( isAbsolute ) {
            return host + new String( path );
        } else {
            return host + ":" + new String( path );
        }
    }

    private char getSeparator() {
        if ( usesWindowsFormat ) {
            return '\\';
        }
        return fs.getSeparator().toCharArray()[ 0 ];
    }

    public void clearCache() {
        file = null;
        attrsStorage.clear();
    }

    @Override
    public boolean equals( final Object o ) {
        checkNotNull( "o", o );

        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof AbstractPath ) ) {
            return false;
        }

        AbstractPath other = (AbstractPath) o;

        if ( isAbsolute != other.isAbsolute ) {
            return false;
        }
        if ( isRealPath != other.isRealPath ) {
            return false;
        }
        if ( isRoot != other.isRoot ) {
            return false;
        }
        if ( usesWindowsFormat != other.usesWindowsFormat ) {
            return false;
        }
        if ( !host.equals( other.host ) ) {
            return false;
        }
        if ( !fs.equals( other.fs ) ) {
            return false;
        }

        if ( !usesWindowsFormat && !Arrays.equals( path, other.path ) ) {
            return false;
        }

        if ( usesWindowsFormat && !( new String( path ).equalsIgnoreCase( new String( other.path ) ) ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = fs != null ? fs.hashCode() : 0;
        result = 31 * result + ( usesWindowsFormat ? 1 : 0 );
        result = 31 * result + ( isAbsolute ? 1 : 0 );

        if ( !usesWindowsFormat ) {
            result = 31 * result + ( path != null ? Arrays.hashCode( path ) : 0 );
        } else {
            result = 31 * result + ( path != null ? new String( path ).toLowerCase().hashCode() : 0 );
        }

        result = 31 * result + ( isRoot ? 1 : 0 );
        result = 31 * result + ( isRealPath ? 1 : 0 );
        result = 31 * result + ( isNormalized ? 1 : 0 );
        return result;
    }

    public String getHost() {
        return host;
    }

    public boolean isRealPath() {
        return isRealPath;
    }

    @Override
    public AttrsStorage getAttrStorage() {
        return attrsStorage;
    }

    @Override
    public <V extends AttributeView> void addAttrView( final V view ) {
        attrsStorage.addAttrView( view );
    }

    @Override
    public <V extends AttributeView> V getAttrView( final Class<V> type ) {
        return attrsStorage.getAttrView( type );
    }

    @Override
    public <V extends AttributeView> V getAttrView( final String name ) {
        return (V) attrsStorage.getAttrView( name );
    }

    public static class RootInfo {

        private final int startOffset;
        private final boolean isAbsolute;
        private final boolean isRoot;
        private final byte[] path;

        public RootInfo( int startOffset,
                         boolean isAbsolute,
                         boolean isRoot,
                         byte[] path ) {
            this.startOffset = startOffset;
            this.isAbsolute = isAbsolute;
            this.isRoot = isRoot;
            this.path = path;
        }
    }
}
