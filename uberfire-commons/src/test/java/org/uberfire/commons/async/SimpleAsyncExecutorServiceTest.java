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
package org.uberfire.commons.async;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SimpleAsyncExecutorServiceTest {

    private String spUberfireAsyncExecutorSafeMode;
    private String spJavaNamingFactoryInitial;

    @Before
    public void before() {
        SimpleAsyncExecutorService.recycle();
        spUberfireAsyncExecutorSafeMode = System.getProperty( "org.uberfire.async.executor.safemode" );
        spJavaNamingFactoryInitial = System.getProperty( Context.INITIAL_CONTEXT_FACTORY );
        System.clearProperty( "org.uberfire.async.executor.safemode" );
        System.clearProperty( Context.INITIAL_CONTEXT_FACTORY );
    }

    @After
    public void after() {
        if ( spUberfireAsyncExecutorSafeMode != null ) {
            System.setProperty( "org.uberfire.async.executor.safemode",
                                spUberfireAsyncExecutorSafeMode );
        }
        if ( spJavaNamingFactoryInitial != null ) {
            System.setProperty( Context.INITIAL_CONTEXT_FACTORY,
                                spJavaNamingFactoryInitial );
        }
    }

    @Test
    public void testUseJDNILookup() throws NamingException {
        //Test ExecutorService is looked up from JNDI
        System.setProperty( Context.INITIAL_CONTEXT_FACTORY,
                            MockInitialContextFactory.class.getName() );

        final Context context = mock( Context.class );
        final DisposableExecutor service = mock( DisposableExecutor.class );
        when( context.lookup( "java:module/SimpleAsyncExecutorService" ) ).thenReturn( service );
        MockInitialContextFactory.setCurrentContext( context );

        final DisposableExecutor executor1 = SimpleAsyncExecutorService.getDefaultInstance();

        assertNotNull( executor1 );
        assertTrue( executor1 instanceof DisposableExecutor );
        assertSame( service,
                    executor1 );

        final DisposableExecutor executor2 = SimpleAsyncExecutorService.getDefaultInstance();

        assertNotNull( executor2 );
        assertTrue( executor2 instanceof DisposableExecutor );
        assertSame( service,
                    executor2 );

        assertSame( executor1,
                    executor2 );
    }

    @Test
    public void testUseExecutorThreadPool() {
        //Test ExecutorService is a "simple" implementation
        System.setProperty( "org.uberfire.async.executor.safemode",
                            "true" );

        final DisposableExecutor executor1 = SimpleAsyncExecutorService.getDefaultInstance();

        assertNotNull( executor1 );
        assertTrue( executor1 instanceof SimpleAsyncExecutorService );

        final DisposableExecutor executor2 = SimpleAsyncExecutorService.getDefaultInstance();

        assertNotNull( executor2 );
        assertTrue( executor2 instanceof SimpleAsyncExecutorService );

        assertSame( executor1,
                    executor2 );
    }

    public static class MockInitialContextFactory implements InitialContextFactory {

        private static final ThreadLocal<Context> currentContext = new ThreadLocal<Context>();

        @Override
        public Context getInitialContext( final Hashtable<?, ?> environment ) throws NamingException {
            return currentContext.get();
        }

        public static void setCurrentContext( final Context context ) {
            currentContext.set( context );
        }

    }

}
