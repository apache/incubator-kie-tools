package org.uberfire.io.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.uberfire.commons.data.Pair;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.AbstractBasicFileAttributeView;
import org.uberfire.java.nio.base.AbstractPath;
import org.uberfire.java.nio.base.NeedsPreloadedAttrs;
import org.uberfire.java.nio.base.NotImplementedException;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileTime;

import static org.uberfire.commons.data.Pair.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.io.attribute.DublinCoreAttributesUtil.*;

/**
 *
 */
public class DublinCoreView
        extends AbstractBasicFileAttributeView<AbstractPath>
        implements NeedsPreloadedAttrs {

    static final String TITLE              = "dcore.title";
    static final String CREATOR            = "dcore.creator";
    static final String SUBJECT            = "dcore.subject";
    static final String DESCRIPTION        = "dcore.description";
    static final String PUBLISHER          = "dcore.publisher";
    static final String CONTRIBUTOR        = "dcore.contributor";
    static final String TYPE               = "dcore.type";
    static final String FORMAT             = "dcore.format";
    static final String IDENTIFIER         = "dcore.identifier";
    static final String SOURCE             = "dcore.source";
    static final String LANGUAGE           = "dcore.language";
    static final String RELATION           = "dcore.relation";
    static final String COVERAGE           = "dcore.coverage";
    static final String RIGHTS             = "dcore.rights";
    static final String LAST_MODIFIED_TIME = "lastModifiedTime";
    static final String LAST_ACCESS_TIME   = "lastAccessTime";
    static final String CREATION_TIME      = "creationTime";

    private static final Set<String> PROPERTIES = new HashSet<String>() {{
        add( TITLE );
        add( CREATOR );
        add( SUBJECT );
        add( DESCRIPTION );
        add( PUBLISHER );
        add( CONTRIBUTOR );
        add( TYPE );
        add( FORMAT );
        add( IDENTIFIER );
        add( SOURCE );
        add( LANGUAGE );
        add( RELATION );
        add( COVERAGE );
        add( RIGHTS );
    }};

    private final DublinCoreAttributes attrs;

    public DublinCoreView( final AbstractPath path ) {
        super( path );
        final Map<String, Object> content = path.getAttrStorage().getContent();

        final BasicFileAttributes fileAttrs = path.getFileSystem().provider().getFileAttributeView( path, BasicFileAttributeView.class ).readAttributes();

        final Map<String, List<String>> dcore = new HashMap<String, List<String>>() {{
            for ( final String property : PROPERTIES ) {
                put( property, new ArrayList<String>() );
            }
        }};

        for ( final Map.Entry<String, Object> entry : content.entrySet() ) {
            if ( entry.getKey().startsWith( TITLE ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( TITLE ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( CREATOR ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( CREATOR ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( SUBJECT ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( SUBJECT ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( DESCRIPTION ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( DESCRIPTION ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( PUBLISHER ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( PUBLISHER ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( CONTRIBUTOR ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( CONTRIBUTOR ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( TYPE ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( TYPE ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( FORMAT ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( FORMAT ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( IDENTIFIER ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( IDENTIFIER ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( SOURCE ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( SOURCE ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( LANGUAGE ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( LANGUAGE ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( RELATION ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( RELATION ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( COVERAGE ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( COVERAGE ).add( result.getK1(), result.getK2() );
            } else if ( entry.getKey().startsWith( RIGHTS ) ) {
                final Pair<Integer, String> result = extractValue( entry );
                dcore.get( RIGHTS ).add( result.getK1(), result.getK2() );
            }
        }

        this.attrs = new DublinCoreAttributes() {
            @Override
            public List<String> titles() {
                return dcore.get( TITLE );
            }

            @Override
            public List<String> creators() {
                return dcore.get( CREATOR );
            }

            @Override
            public List<String> subjects() {
                return dcore.get( SUBJECT );
            }

            @Override
            public List<String> descriptions() {
                return dcore.get( DESCRIPTION );
            }

            @Override
            public List<String> publishers() {
                return dcore.get( PUBLISHER );
            }

            @Override
            public List<String> contributors() {
                return dcore.get( CONTRIBUTOR );
            }

            @Override
            public List<String> types() {
                return dcore.get( TYPE );
            }

            @Override
            public List<String> formats() {
                return dcore.get( FORMAT );
            }

            @Override
            public List<String> identifiers() {
                return dcore.get( IDENTIFIER );
            }

            @Override
            public List<String> sources() {
                return dcore.get( SOURCE );
            }

            @Override
            public List<String> languages() {
                return dcore.get( LANGUAGE );
            }

            @Override
            public List<String> relations() {
                return dcore.get( RELATION );
            }

            @Override
            public List<String> coverages() {
                return dcore.get( COVERAGE );
            }

            @Override
            public List<String> rights() {
                return dcore.get( RIGHTS );
            }

            @Override
            public FileTime lastModifiedTime() {
                return fileAttrs.lastModifiedTime();
            }

            @Override
            public FileTime lastAccessTime() {
                return fileAttrs.lastAccessTime();
            }

            @Override
            public FileTime creationTime() {
                return fileAttrs.creationTime();
            }

            @Override
            public boolean isRegularFile() {
                return fileAttrs.isRegularFile();
            }

            @Override
            public boolean isDirectory() {
                return fileAttrs.isDirectory();
            }

            @Override
            public boolean isSymbolicLink() {
                return fileAttrs.isSymbolicLink();
            }

            @Override
            public boolean isOther() {
                return fileAttrs.isOther();
            }

            @Override
            public long size() {
                return fileAttrs.size();
            }

            @Override
            public Object fileKey() {
                return fileAttrs.fileKey();
            }
        };
    }

    private Pair<Integer, String> extractValue( final Map.Entry<String, Object> entry ) {
        int start = entry.getKey().indexOf( '[' );
        if ( start < 0 ) {
            return newPair( 0, entry.getValue().toString() );
        }
        int end = entry.getKey().indexOf( ']' );

        return newPair( Integer.valueOf( entry.getKey().substring( start + 1, end ) ), entry.getValue().toString() );
    }

    @Override
    public String name() {
        return "dcore";
    }

    @Override
    public DublinCoreAttributes readAttributes() throws IOException {
        return attrs;
    }

    @Override
    public Map<String, Object> readAttributes( final String... attributes ) {
        return toMap( readAttributes(), attributes );
    }

    @Override
    public Class<? extends BasicFileAttributeView>[] viewTypes() {
        return new Class[]{ DublinCoreView.class };
    }

    @Override
    public void setAttribute( final String attribute,
                              final Object value ) throws IOException {
        checkNotEmpty( "attribute", attribute );
        checkCondition( "invalid attribute", PROPERTIES.contains( attribute ) );

        throw new NotImplementedException();
    }

}
