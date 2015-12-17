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

package org.kie.workbench.common.screens.datamodeller.client.util;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.uberfire.commons.data.Pair;

public class UIUtil {

    public static final String NOT_SELECTED = "NOT_SELECTED";

    public static void setSelectedValue( final Select select,
                                         final String value ) {
        select.setValue( value );
        refreshSelect( select );
    }

    public static void refreshSelect( final Select select ) {
        Scheduler.get().scheduleDeferred( new Command() {
            public void execute() {
                select.refresh();
            }
        } );
    }

    public static void initList( final Select select,
                                 boolean includeEmptyItem ) {
        select.clear();
        if ( includeEmptyItem ) {
            select.add( emptyOption() );
        }
        refreshSelect( select );
    }

    public static void initList( final Select select,
            final List<Pair<String, String>> options,
            boolean includeEmptyItem ) {
        initList( select, options, null, includeEmptyItem );
    }

    public static Pair<String, String> emptyValue() {
        return new Pair<String, String>( "", NOT_SELECTED );
    }

    /**
     *
     * @param select Select object to be initialized
     *
     * @param options List of pairs <"text to show", "value"> representing the select options.
     *
     * @param selectedValue The current selected value.
     *
     * @param includeEmptyItem true if the empty optio should be included.
     *
     *
     */
    public static void initList( final Select select,
                                final List<Pair<String, String>> options,
                                final String selectedValue,
                                boolean includeEmptyItem ) {
        initList( select, includeEmptyItem );
        for ( Pair<String, String> optionPair: options ) {
            select.add( newOption( optionPair.getK1(), optionPair.getK2() ));
        }
        if ( selectedValue != null ) {
            setSelectedValue( select, selectedValue );
        } else {
            refreshSelect( select );
        }
    }

    public static Option newOption( final String text, final String value ) {
        final Option option = new Option();
        option.setValue( value );
        option.setText( text );
        return option;
    }

    public static Option emptyOption() {
        return newOption( "", NOT_SELECTED );
    }
}
