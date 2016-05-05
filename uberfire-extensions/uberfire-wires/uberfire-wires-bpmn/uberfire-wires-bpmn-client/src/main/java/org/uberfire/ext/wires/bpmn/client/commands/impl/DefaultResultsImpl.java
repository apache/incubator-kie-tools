/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.wires.bpmn.client.commands.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.uberfire.ext.wires.bpmn.client.commands.Result;
import org.uberfire.ext.wires.bpmn.client.commands.ResultType;
import org.uberfire.ext.wires.bpmn.client.commands.Results;

/**
 * Results from the execution of a command
 */
public class DefaultResultsImpl implements Results {

    private List<Result> results = new ArrayList<Result>();

    @Override
    public void addMessage( final Result result ) {
        results.add( result );
    }

    @Override
    public List<Result> getMessages() {
        return results;
    }

    @Override
    public List<Result> getMessages( final ResultType type ) {
        final List<Result> filteredResults = new ArrayList<Result>();
        for ( Result result : results ) {
            if ( result.getType().equals( type ) ) {
                filteredResults.add( result );
            }
        }
        return Collections.unmodifiableList( filteredResults );
    }

    @Override
    public boolean contains( final ResultType type ) {
        for ( Result result : results ) {
            if ( result.getType().equals( type ) ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof DefaultResultsImpl ) ) {
            return false;
        }

        DefaultResultsImpl that = (DefaultResultsImpl) o;

        if ( !results.equals( that.results ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return results.hashCode();
    }

    @Override
    public String toString() {
        return "DefaultResultsImpl{" +
                "results=" + results +
                '}';
    }

}
