package org.kie.uberfire.social.activities.service;

public interface SocialPredicate<T> {

    boolean test( T t );
}
