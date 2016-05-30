package com.ait.lienzo.test.stub.overlays;

import com.ait.lienzo.test.annotation.StubClass;

/**
 * In-memory array implementation stub for class <code>com.ait.tooling.nativetools.client.collection.NFastArrayList.FastArrayListJSO</code>.
 *
 * @author Roger Martinez
 * @since 1.0
 *
 */
@StubClass( "com.ait.tooling.nativetools.client.collection.NFastArrayList$FastArrayListJSO" )
public class FastArrayListJSO<M> extends NArrayBaseJSO<FastArrayListJSO<M>>
{
    protected FastArrayListJSO()
    {
    }

    static <M> FastArrayListJSO<M> make()
    {
        return new FastArrayListJSO();
    }

    @SuppressWarnings("unchecked")
    M get(int indx) {
        return (M) list.get( indx );
    }

    void add(M value) {
        list.add( value );
    }

    void set(int i, M value) {
        list.set( i, value );
    }

    boolean contains(M value) {
        return list.contains( value );
    }

    void remove(M value) {
        list.remove( value );
    }

    void moveUp(M value) {
        // TODO
    }

    void moveDown(M value) {
        // TODO
    }

    void unshift(M value) {
        doUnShift( value );
    }

    void splice(int beg, int removed, M value) {
        // TODO
    }

    M shift() {
        return doShift();
    }
    
    @SuppressWarnings("unchecked")
    M pop() {
        M result = null;
        
        if ( !list.isEmpty() ) {
            int i = list.size() - 1;
            result = (M) list.get( i );
            list.remove( i );
        }
        
        return result;
    }

    void push(M value) {
        list.add( value );
    }

    @SuppressWarnings("unchecked")
    private  <V> V doShift() {
        V t = (V) list.get( 0 );
        list.remove( 0 );
        return t;
    }

    private <V> void doUnShift( final V value ) {
        list.add( 0, value );
    }
    
}