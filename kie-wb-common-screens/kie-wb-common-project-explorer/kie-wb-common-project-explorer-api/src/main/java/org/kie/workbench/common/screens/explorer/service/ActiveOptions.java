package org.kie.workbench.common.screens.explorer.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ActiveOptions {

    private final HashSet<Option> set;

    public ActiveOptions() {
        this.set = new HashSet<Option>();
    }

    public ActiveOptions( Option... options ) {
        this.set = new HashSet<Option>( Arrays.asList( options ) );
    }

    public ActiveOptions( Set<Option> options ) {
        this.set = new HashSet<Option>( options );
    }

    public ActiveOptions( ActiveOptions options ) {
        set = new HashSet( options.getValues() );
    }

    public void add( Option option ) {
        set.add( option );
    }

    public boolean contains( Option option ) {
        return set.contains( option );
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public Collection<Option> getValues() {
        return set;
    }

    public void addAll( Option... options ) {
        for (Option option : options) {
            set.add( option );
        }
    }

    public void clear() {
        set.clear();
    }

    public void addAll( Set<Option> optionSet ) {
        set.addAll( optionSet );
    }

    public boolean remove( Option treeNavigator ) {
        return set.remove( treeNavigator );
    }
}
