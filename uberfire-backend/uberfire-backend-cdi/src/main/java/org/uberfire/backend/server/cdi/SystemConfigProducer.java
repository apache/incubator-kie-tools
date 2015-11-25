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

package org.uberfire.backend.server.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessProducer;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.commons.services.cdi.Startable;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.commons.services.cdi.Veto;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceNio2WrapperImpl;
import org.uberfire.io.impl.cluster.IOServiceClusterImpl;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.FileSystemState;
import org.uberfire.java.nio.file.FileStore;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.InvalidPathException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.PathMatcher;
import org.uberfire.java.nio.file.PatternSyntaxException;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.file.attribute.UserPrincipalLookupService;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

public class SystemConfigProducer implements Extension {

    private static final Logger logger = LoggerFactory.getLogger( SystemConfigProducer.class );

    private static final String CDI_METHOD = "cdi";

    private static final String START_METHOD = System.getProperty( "org.uberfire.start.method", "cdi" );

    private final List<OrderedBean> startupEagerBeans = new LinkedList<OrderedBean>();
    private final List<OrderedBean> startupBootstrapBeans = new LinkedList<OrderedBean>();

    private boolean systemFSNotExists = true;
    private boolean ioStrategyBeanNotFound = true;

    private final Comparator<OrderedBean> priorityComparator = new Comparator<OrderedBean>() {
        @Override
        public int compare( final OrderedBean o1,
                            final OrderedBean o2 ) {
            return o1.priority - o2.priority;
        }
    };

    public void processSystemFSProducer( @Observes ProcessProducer<?, FileSystem> pp ) {
        if ( pp.getAnnotatedMember().getJavaMember().getName().equals( "systemFS" ) ) {
            ioStrategyBeanNotFound = false;
        }
    }

    public void processIOServiceProducer( @Observes ProcessProducer<?, IOService> pp ) {
        if ( pp.getAnnotatedMember().getJavaMember().getName().equals( "ioStrategy" ) ) {
            ioStrategyBeanNotFound = false;
        }
    }

    public <X> void processBean( @Observes final ProcessBean<X> event ) {
        if ( event.getBean().getName() != null && event.getBean().getName().equals( "systemFS" ) ) {
            systemFSNotExists = false;
        } else if ( event.getBean().getName() != null && event.getBean().getName().equals( "ioStrategy" ) ) {
            ioStrategyBeanNotFound = false;
        }
        if ( event.getAnnotated().isAnnotationPresent( Startup.class ) && ( event.getAnnotated().isAnnotationPresent( ApplicationScoped.class )
                || event.getAnnotated().isAnnotationPresent( Singleton.class ) ) ) {
            final Startup startupAnnotation = event.getAnnotated().getAnnotation( Startup.class );
            final StartupType type = startupAnnotation.value();
            final int priority = startupAnnotation.priority();
            final Bean<?> bean = event.getBean();
            switch ( type ) {
                case EAGER:
                    startupEagerBeans.add( new OrderedBean( bean,
                                                            priority ) );
                    break;
                case BOOTSTRAP:
                    startupBootstrapBeans.add( new OrderedBean( bean,
                                                                priority ) );
                    break;
            }
        } else if ( event.getAnnotated().isAnnotationPresent( Named.class ) && ( event.getAnnotated().isAnnotationPresent( ApplicationScoped.class )
                || event.getAnnotated().isAnnotationPresent( Singleton.class ) ) ) {
            final Named namedAnnotation = event.getAnnotated().getAnnotation( Named.class );

            if ( namedAnnotation.value().endsWith( "-startable" ) ) {
                final Bean<?> bean = event.getBean();
                startupBootstrapBeans.add( new OrderedBean( bean, 10 ) );
            }
        }
    }

    public void afterDeploymentValidation( final @Observes AfterDeploymentValidation event,
                                           final BeanManager manager ) {
        if ( CDI_METHOD.equalsIgnoreCase( START_METHOD ) ) {
            //Force execution of Bootstrap bean's @PostConstruct methods first
            runPostConstruct( manager,
                              startupBootstrapBeans );

            //Followed by execution of remaining Eager bean's @PostConstruct methods
            runPostConstruct( manager,
                              startupEagerBeans );
        }
    }

    private void runPostConstruct( final BeanManager manager,
                                   final List<OrderedBean> orderedBeans ) {
        //Sort first, by priority
        Collections.sort( orderedBeans,
                          priorityComparator );
        for ( OrderedBean ob : orderedBeans ) {
            // the call to toString() is a cheat to force the bean to be initialized
            final Bean<?> bean = ob.bean;
            manager.getReference( bean,
                                  bean.getBeanClass(),
                                  manager.createCreationalContext( bean ) ).toString();
        }
    }

    private class OrderedBean {

        Bean<?> bean;
        int priority;

        private OrderedBean( final Bean<?> bean,
                             final int priority ) {
            this.bean = bean;
            this.priority = priority;
        }
    }

    <T> void processAnnotatedType( @Observes ProcessAnnotatedType<T> pat ) {
        if ( pat.getAnnotatedType().isAnnotationPresent( Veto.class ) ) {
            pat.veto();
        }
    }

    void afterBeanDiscovery( @Observes final AfterBeanDiscovery abd,
                             final BeanManager bm ) {

        if ( systemFSNotExists ) {
            buildSystemFS( abd, bm );
        }

        if ( ioStrategyBeanNotFound ) {
            buildIOStrategy( abd, bm );
        }

        if ( !CDI_METHOD.equalsIgnoreCase( START_METHOD ) ) {
            buildStartableBean( abd, bm );
        }
    }

    private void buildSystemFS( final AfterBeanDiscovery abd,
                                final BeanManager bm ) {
        final InjectionTarget<DummyFileSystem> it = bm.createInjectionTarget( bm.createAnnotatedType( DummyFileSystem.class ) );

        abd.addBean( new Bean<FileSystem>() {

            @Override
            public Class<?> getBeanClass() {
                return FileSystem.class;
            }

            @Override
            public Set<InjectionPoint> getInjectionPoints() {
                return it.getInjectionPoints();
            }

            @Override
            public String getName() {
                return "systemFS";
            }

            @Override
            public Set<Annotation> getQualifiers() {

                return new HashSet<Annotation>() {{
                    add( new AnnotationLiteral<Default>() {
                    } );
                    add( new AnnotationLiteral<Any>() {
                    } );
                    add( new NamedLiteral( "systemFS" ) );
                }};
            }

            @Override
            public Class<? extends Annotation> getScope() {
                return ApplicationScoped.class;
            }

            @Override
            public Set<Class<? extends Annotation>> getStereotypes() {
                return Collections.emptySet();
            }

            @Override
            public Set<Type> getTypes() {
                return new HashSet<Type>() {{
                    add( FileSystem.class );
                    add( Object.class );
                }};
            }

            @Override
            public boolean isAlternative() {
                return false;
            }

            @Override
            public boolean isNullable() {
                return false;
            }

            @Override
            public FileSystem create( CreationalContext<FileSystem> ctx ) {
                final Bean<IOService> bean = (Bean<IOService>) bm.getBeans( "configIO" ).iterator().next();
                final CreationalContext<IOService> _ctx = bm.createCreationalContext( bean );
                final IOService ioService = (IOService) bm.getReference( bean, IOService.class, _ctx );

                FileSystem systemFS;
                try {
                    systemFS = ioService.newFileSystem( URI.create( "git://system" ),
                                                        new HashMap<String, Object>() {{
                                                            put( "init", Boolean.TRUE );
                                                            put( "internal", Boolean.TRUE );
                                                        }} );
                } catch ( FileSystemAlreadyExistsException e ) {
                    systemFS = ioService.getFileSystem( URI.create( "git://system" ) );
                }

                PriorityDisposableRegistry.register( "systemFS", systemFS );

                return systemFS;
            }

            @Override
            public void destroy( final FileSystem instance,
                                 final CreationalContext<FileSystem> ctx ) {
                try {
                    instance.dispose();
                } catch ( final Exception ex ) {
                    logger.warn( ex.getMessage(), ex );
                }
                ctx.release();
            }
        } );
    }

    private void buildIOStrategy( final AfterBeanDiscovery abd,
                                  final BeanManager bm ) {

        final InjectionTarget<IOServiceNio2WrapperImpl> it = bm.createInjectionTarget( bm.createAnnotatedType( IOServiceNio2WrapperImpl.class ) );

        abd.addBean( new Bean<IOService>() {

            @Override
            public Class<?> getBeanClass() {
                return IOService.class;
            }

            @Override
            public Set<InjectionPoint> getInjectionPoints() {
                return it.getInjectionPoints();
            }

            @Override
            public String getName() {
                return "ioStrategy";
            }

            @Override
            public Set<Annotation> getQualifiers() {

                return new HashSet<Annotation>() {{
                    add( new AnnotationLiteral<Default>() {
                    } );
                    add( new AnnotationLiteral<Any>() {
                    } );
                    add( new NamedLiteral( "ioStrategy" ) );
                }};
            }

            @Override
            public Class<? extends Annotation> getScope() {
                return ApplicationScoped.class;
            }

            @Override
            public Set<Class<? extends Annotation>> getStereotypes() {
                return Collections.emptySet();
            }

            @Override
            public Set<Type> getTypes() {
                return new HashSet<Type>() {{
                    add( IOService.class );
                    add( Object.class );
                }};
            }

            @Override
            public boolean isAlternative() {
                return false;
            }

            @Override
            public boolean isNullable() {
                return false;
            }

            @Override
            public IOService create( CreationalContext<IOService> ctx ) {

                final Bean<ClusterServiceFactory> clusterFactoryBean = (Bean<ClusterServiceFactory>) bm.getBeans( "clusterServiceFactory" ).iterator().next();
                final CreationalContext<ClusterServiceFactory> _ctx = bm.createCreationalContext( clusterFactoryBean );
                final ClusterServiceFactory clusterServiceFactory = (ClusterServiceFactory) bm.getReference( clusterFactoryBean, ClusterServiceFactory.class, _ctx );

                final IOService result;

                if ( clusterServiceFactory == null ) {
                    result = new IOServiceNio2WrapperImpl();
                } else {
                    result = new IOServiceClusterImpl( new IOServiceNio2WrapperImpl(), clusterServiceFactory );
                }

                return result;
            }

            @Override
            public void destroy( final IOService instance,
                                 final CreationalContext<IOService> ctx ) {
                ctx.release();
            }
        } );
    }

    private void buildStartableBean( final AfterBeanDiscovery abd,
                                     final BeanManager bm ) {

        abd.addBean( new Bean<Startable>() {

            @Override
            public Class<?> getBeanClass() {
                return Startable.class;
            }

            @Override
            public Set<InjectionPoint> getInjectionPoints() {
                return Collections.emptySet();
            }

            @Override
            public String getName() {
                return "startablebean";
            }

            @Override
            public Set<Annotation> getQualifiers() {

                return new HashSet<Annotation>() {{
                    add( new AnnotationLiteral<Default>() {
                    } );
                    add( new AnnotationLiteral<Any>() {
                    } );
                }};
            }

            @Override
            public Class<? extends Annotation> getScope() {
                return ApplicationScoped.class;
            }

            @Override
            public Set<Class<? extends Annotation>> getStereotypes() {
                return Collections.emptySet();
            }

            @Override
            public Set<Type> getTypes() {
                return new HashSet<Type>() {{
                    add( Startable.class );
                    add( Object.class );
                }};
            }

            @Override
            public boolean isAlternative() {
                return false;
            }

            @Override
            public boolean isNullable() {
                return false;
            }

            @Override
            public Startable create( CreationalContext<Startable> ctx ) {

                return new Startable() {
                    @Override
                    public int hashCode() {
                        return super.hashCode();
                    }

                    @Override
                    public void start() {
                        //Force execution of Bootstrap bean's @PostConstruct methods first
                        runPostConstruct( bm,
                                          startupBootstrapBeans );

                        //Followed by execution of remaining Eager bean's @PostConstruct methods
                        runPostConstruct( bm,
                                          startupEagerBeans );
                    }
                };
            }

            @Override
            public void destroy( final Startable instance,
                                 final CreationalContext<Startable> ctx ) {

                ctx.release();
            }
        } );
    }

    public static class DummyFileSystem implements FileSystem {

        private FileSystemState state = FileSystemState.NORMAL;

        @Override
        public FileSystemProvider provider() {
            return null;
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Override
        public String getSeparator() {
            return null;
        }

        @Override
        public Iterable<Path> getRootDirectories() {
            return null;
        }

        @Override
        public Iterable<FileStore> getFileStores() {
            return null;
        }

        @Override
        public Set<String> supportedFileAttributeViews() {
            return null;
        }

        @Override
        public Path getPath( String first,
                             String... more ) throws InvalidPathException {
            return null;
        }

        @Override
        public PathMatcher getPathMatcher( String syntaxAndPattern ) throws IllegalArgumentException, PatternSyntaxException, UnsupportedOperationException {
            return null;
        }

        @Override
        public UserPrincipalLookupService getUserPrincipalLookupService() throws UnsupportedOperationException {
            return null;
        }

        @Override
        public WatchService newWatchService() throws UnsupportedOperationException, IOException {
            return null;
        }

        @Override
        public void close() throws IOException {

        }

        @Override
        public void dispose() {

        }
    }

    private class DummyStarable implements Startable {

        @Override
        public void start() {

        }
    }

    public class NamedLiteral extends AnnotationLiteral<Named> implements Named {

        private final String value;

        @Override
        public String value() {
            return value;
        }

        public NamedLiteral( String value ) {
            this.value = value;
        }

    }
}