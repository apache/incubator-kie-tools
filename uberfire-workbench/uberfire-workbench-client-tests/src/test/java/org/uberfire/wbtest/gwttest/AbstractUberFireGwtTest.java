package org.uberfire.wbtest.gwttest;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;

import com.github.gwtbootstrap.client.Bootstrap;
import com.google.common.base.Predicate;
import com.google.gwt.user.client.Timer;


public abstract class AbstractUberFireGwtTest extends AbstractErraiCDITest {

    boolean debugAsyncTesting = false;

    @Override
    protected void gwtSetUp() throws Exception {

        disableBus = true;

        // let GwtBootstrap inject its scripts (otherwise, its widgets will blow up during the test)
        new Bootstrap().onModuleLoad();

        super.gwtSetUp();
    }

    /**
     * Entry point for building step-by-step asynchronous tests. Use the initial predicate to test for workbench startup
     * completion, then chain an arbitrary number of additional actions to execute afterward: JUnit assertions,
     * framework actions such as {@code PlaceManager.goTo()}, and even additional predicates to poll on.
     * <p>
     * The async processing will log its progress on stdout if the field {@link #debugAsyncTesting} is set to
     * {@code true}.
     *
     * @param predicate
     *            the predicate to test. Must not be null. Will be called over and over (every 500 ms) until it returns
     *            false.
     * @return a builder object for chaining the additional actions that will be executed once {@code predicate} returns
     *         false.
     */
    protected AfterPolling pollWhile( final Predicate<Void> predicate ) {
        final List<Object> chainedActions = new ArrayList<Object>();

        delayTestFinish( 15000 );

        new Timer() {
            private final long startTime = System.currentTimeMillis();
            @Override
            public void run() {
                debugPrint("Testing predicate");
                if ( !predicate.apply( null ) ) {
                    final ListIterator<Object> chain = chainedActions.listIterator();
                    new Timer() {
                        @Override
                        public void run() {
                            if ( chain.hasNext() ) {
                                Object next = chain.next();
                                if ( next instanceof Runnable ) {
                                    debugPrint("Running supplied task");
                                    ((Runnable) next).run();
                                    schedule( 0 );
                                } else if ( next instanceof Predicate ) {
                                    debugPrint("Testing intermediate predicate");
                                    if ( predicate.apply( null ) ) {
                                        debugPrint("Intermediate predicate is true. Will try again in 500ms.");
                                        chain.previous();
                                        schedule( 500 );
                                    } else {
                                        debugPrint("Intermediate predicate became false. Continuing.");
                                        schedule( 0 );
                                    }
                                } else if ( next instanceof Integer ) {
                                    debugPrint("Delaying " + next + " ms");
                                    schedule( (Integer) next );
                                } else {
                                    throw new AssertionError( "Unknown entry in doAfter list: " + next );
                                }
                            } else {
                                debugPrint("Test passed!");
                                finishTest();
                            }
                        }
                    }.schedule( 0 );
                } else {
                    if ( System.currentTimeMillis() - startTime > 5000 ) {
                        debugPrint( "Still waiting on pollWhile() condition..." );
                    }
                    schedule( 500 );
                }
            }
        }.schedule( 500 );

        return new AfterPolling() {
            @Override
            public AfterPolling thenDo( Runnable runnable ) {
                chainedActions.add( runnable );
                return this;
            }

            @Override
            public AfterPolling thenPollWhile( Predicate<Void> isTrue ) {
                chainedActions.add( isTrue );
                return this;
            }

            @Override
            public AfterPolling thenDelay( int delayMillis ) {
                chainedActions.add( delayMillis );
                return this;
            }
        };
    }

    private void debugPrint( String msg ) {
        if ( debugAsyncTesting ) {
            System.out.println( msg );
        }
    }

    /**
     * Builder for chaining additional tasks to perform after a {@link AbstractUberFireGwtTest#pollWhile(Predicate)}
     * condition has been satisfied.
     */
    public interface AfterPolling {

        /**
         * Adds the given arbitrary code to be executed after all previous steps in the chain have been satisfied.
         * This is a good place to put JUnit assertions.
         *
         * @param runnable
         *            the code to run. Must not be null.
         * @return this object again, for chaining more steps.
         */
        AfterPolling thenDo( Runnable runnable );

        /**
         * Adds a new predicate to test every 500 ms. The next step in the chain will not be executed until the given
         * predicate returns false.
         *
         * @param isTrue
         *            the predicate to poll every 500ms. Not null.
         * @return this object again, for chaining more steps.
         */
        AfterPolling thenPollWhile( Predicate<Void> isTrue );

        /**
         * Adds a delay of the given length to the chain. The next step in the chain will be executed after the given
         * time has elapsed.
         *
         * @param delayMillis
         *            the amount of time to delay (in milliseconds). Must be a nonnegative number.
         * @return this object again, for chaining more steps.
         */
        AfterPolling thenDelay( int delayMillis );
    }

}
