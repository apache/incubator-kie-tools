package org.kie.workbench.common.services.backend;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.guvnor.common.services.builder.IncrementalBuilderExecutorManager;
import org.guvnor.common.services.builder.IncrementalBuilderExecutorManagerFactory;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.service.ProjectService;

/**
 * Producer for Executor services so we can plug-in a different implementation in tests
 */
@Alternative
@ApplicationScoped
public class TestIncrementalBuilderExecutorManagerFactoryImpl implements IncrementalBuilderExecutorManagerFactory {

    @Inject
    private ProjectService projectService;

    @Inject
    private BuildService buildService;

    @Inject
    private Event<BuildResults> buildResultsEvent;

    @Inject
    private Event<IncrementalBuildResults> incrementalBuildResultsEvent;

    @Override
    public IncrementalBuilderExecutorManager getExecutorManager() {
        final IncrementalBuilderExecutorManager manager = new IncrementalBuilderExecutorManager();
        manager.setServices( projectService, buildService, buildResultsEvent, incrementalBuildResultsEvent );
        manager.setExecutorService( new ExecutorService() {
            @Override
            public void shutdown() {
            }

            @Override
            public List<Runnable> shutdownNow() {
                return null;
            }

            @Override
            public boolean isShutdown() {
                return true;
            }

            @Override
            public boolean isTerminated() {
                return false;
            }

            @Override
            public boolean awaitTermination( long timeout,
                                             TimeUnit unit ) throws InterruptedException {
                return true;
            }

            @Override
            public <T> Future<T> submit( Callable<T> task ) {
                return null;
            }

            @Override
            public <T> Future<T> submit( Runnable task,
                                         T result ) {
                return null;
            }

            @Override
            public Future<?> submit( Runnable task ) {
                return null;
            }

            @Override
            public <T> List<Future<T>> invokeAll( Collection<? extends Callable<T>> tasks ) throws InterruptedException {
                return null;
            }

            @Override
            public <T> List<Future<T>> invokeAll( Collection<? extends Callable<T>> tasks,
                                                  long timeout,
                                                  TimeUnit unit ) throws InterruptedException {
                return null;
            }

            @Override
            public <T> T invokeAny( Collection<? extends Callable<T>> tasks ) throws InterruptedException, ExecutionException {
                return null;
            }

            @Override
            public <T> T invokeAny( Collection<? extends Callable<T>> tasks,
                                    long timeout,
                                    TimeUnit unit ) throws InterruptedException, ExecutionException, TimeoutException {
                return null;
            }

            @Override
            public void execute( Runnable command ) {
                command.run();
            }
        } );

        return manager;
    }
}
