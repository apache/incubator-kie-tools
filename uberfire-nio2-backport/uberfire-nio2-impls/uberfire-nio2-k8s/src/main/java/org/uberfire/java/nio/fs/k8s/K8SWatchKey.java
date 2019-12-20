package org.uberfire.java.nio.fs.k8s;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.uberfire.java.nio.base.WatchContext;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchEvent.Kind;
import org.uberfire.java.nio.file.WatchKey;
import org.uberfire.java.nio.file.Watchable;

import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.K8S_FS_NO_IMPL;

@SuppressWarnings("serial")
public class K8SWatchKey implements WatchKey {
    private final transient K8SWatchService service;
    private final transient Path path;
    private final AtomicReference<State> state = new AtomicReference<>(State.READY);
    private final AtomicBoolean valid = new AtomicBoolean(true);
    private final BlockingQueue<WatchEvent<?>> events = new LinkedBlockingQueue<>();
    @SuppressWarnings("rawtypes")
    private final transient Map<Kind, Event> eventKinds = new ConcurrentHashMap<>();

    K8SWatchKey(K8SWatchService service, Path path) {
        this.service = service;
        this.path = path;
    }

    @Override
    public boolean isValid() {
        return !service.isClose() && valid.get();
    }

    @Override
    public List<WatchEvent<?>> pollEvents() {
        List<WatchEvent<?>> result = new ArrayList<>(events.size());
        events.drainTo(result);
        eventKinds.clear();
        return Collections.unmodifiableList(result);
    }

    @Override
    public boolean reset() {
        if (isValid()) {
            events.clear();
            eventKinds.clear();
            return state.compareAndSet(State.SIGNALLED, State.READY);
        } else {
            return false;
        }
    }

    @Override
    public void cancel() {
        valid.set(false);
    }

    @Override
    public Watchable watchable() {
        return this.path;
    }

    @SuppressWarnings("rawtypes")
    protected boolean postEvent(WatchEvent.Kind kind) {
        Event event = eventKinds.computeIfAbsent(kind, k -> {
            Event e = new Event(kind, new Context(K8SWatchKey.this.path.getFileName()));
            return events.offer(e) ? e : null;
        });
        if (event == null) {
            return false;
        } else {
            event.increaseCount();
            return true;
        }
    }

    protected boolean isQueued() {
        return state.get() == State.SIGNALLED;
    }

    protected void signal() {
        state.compareAndSet(State.READY, State.SIGNALLED);
    }

    enum State {
        READY,
        SIGNALLED
    }

    @SuppressWarnings("rawtypes")
    private static final class Event implements WatchEvent<Context> {

        private final AtomicInteger count = new AtomicInteger(0);
        private final transient Kind kind;
        private final transient Context context;

        private Event(Kind kind, Context context) {
            this.kind = kind;
            this.context = context;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Kind kind() {
            return this.kind;
        }

        @Override
        public int count() {
            return count.get();
        }

        @Override
        public Context context() {
            return this.context;
        }

        private int increaseCount() {
            return count.incrementAndGet();
        }
    }
    
    private static final class Context implements WatchContext {
        private final Path path;
        
        private Context(Path path) {
            this.path = path;
        }
        
        @Override
        public Path getPath() {
            return this.path;
        }

        @Override
        public Path getOldPath() {
            return this.path;
        }

        @Override
        public String getSessionId() {
            return K8S_FS_NO_IMPL;
        }

        @Override
        public String getMessage() {
            return K8S_FS_NO_IMPL;
        }

        @Override
        public String getUser() {
            return K8S_FS_NO_IMPL;
        }
    }
}
