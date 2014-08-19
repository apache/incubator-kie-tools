package org.uberfire.backend.server.util;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.Service;

/**
 * Produces an empty {@code Set<Service>} in order to prevent deploy-time failure.
 * The issue is <a href="https://code.google.com/p/guava-libraries/issues/detail?id=1527">described in the Guava bug
 * tracker</a>. This workaround is described in <a
 * href="https://code.google.com/p/guava-libraries/issues/detail?id=1433#c20">a comment on separate issue</a>.
 * 
 */
@ApplicationScoped
public class Guava14Workaround {

    @Produces Set<Service> dummyServices() {
        return ImmutableSet.of();
    }

}
