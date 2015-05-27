package org.uberfire.mocks;

import java.lang.annotation.Annotation;
import javax.enterprise.event.Event;

public class EventSourceMock<T> implements Event<T> {

    @Override
    public void fire( T event ) {
        throw new UnsupportedOperationException( "mocking testing class" );
    }

    @Override
    public Event<T> select( Annotation... qualifiers ) {
        throw new UnsupportedOperationException( "mocking testing class" );
    }

    @Override
    public <U extends T> Event<U> select( Class<U> subtype,
                                          Annotation... qualifiers ) {
        throw new UnsupportedOperationException( "mocking testing class" );
    }

}
