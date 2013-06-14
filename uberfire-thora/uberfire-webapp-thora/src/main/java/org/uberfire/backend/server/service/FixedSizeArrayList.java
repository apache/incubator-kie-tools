package org.uberfire.backend.server.service;

import java.io.Serializable;
import java.util.ArrayList;

public class FixedSizeArrayList<T> extends ArrayList<T> implements Serializable {

    private static final long serialVersionUID = 2990269624723322961L;
    private int maxSize = 15;

    public FixedSizeArrayList() {
    }

    public FixedSizeArrayList( int i ) {
        super( i );
        this.maxSize = i;
    }

    @Override
    public boolean add( T t ) {
        if ( size() >= maxSize ) {
            remove( 0 );
        }
        return super.add( t );
    }
}
