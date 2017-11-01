/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.java.nio.fs.jgit.ws;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchKey;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.file.Watchable;

public class JGitFileSystemWatchServices implements Serializable {

    private final Collection<JGitWatchService> watchServices = new CopyOnWriteArrayList<>();

    public JGitFileSystemWatchServices() {
    }

    public WatchService newWatchService(String fsName) {
        final JGitWatchService ws = new JGitWatchService(fsName,
                                                         p -> watchServices.remove(p));
        watchServices.add(ws);
        return ws;
    }

    public synchronized void publishEvents(Path watchable,
                                           List<WatchEvent<?>> elist) {
        if (watchServices.isEmpty()) {
            return;
        }

        for (JGitWatchService ws : watchServices) {
            ws.publish(new WatchKey() {

                @Override
                public boolean isValid() {
                    return true;
                }

                @Override
                public List<WatchEvent<?>> pollEvents() {
                    return new CopyOnWriteArrayList<>(elist);
                }

                @Override
                public boolean reset() {
                    return !watchServices.isEmpty();
                }

                @Override
                public void cancel() {
                }

                @Override
                public Watchable watchable() {
                    return watchable;
                }
            });
            synchronized (ws) {
                ws.notifyAll();
            }
        }
    }

    public void close() {
        watchServices.forEach(ws -> ws.closeWithoutNotifyParent());
        watchServices.clear();
    }

}
