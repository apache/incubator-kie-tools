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
package org.uberfire.ext.wires.core.grids.client.widget.layer.impl;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.user.client.Command;

public class GridLayerRedrawManager {

    private static final GridLayerRedrawManager instance = new GridLayerRedrawManager();

    private final Comparator<PrioritizedCommand> COMPARATOR = new Comparator<PrioritizedCommand>() {
        @Override
        public int compare(final PrioritizedCommand o1,
                           final PrioritizedCommand o2) {
            return o1.getPriority() - o2.getPriority();
        }
    };

    SortedSet<PrioritizedCommand> commands = new TreeSet<PrioritizedCommand>(COMPARATOR);

    private AnimationScheduler.AnimationCallback callback;

    private GridLayerRedrawManager() {
        callback = new AnimationScheduler.AnimationCallback() {

            @Override
            public void execute(double time) {
                final SortedSet<PrioritizedCommand> clone = commands;
                commands = new TreeSet<PrioritizedCommand>(COMPARATOR);

                if (!clone.isEmpty()) {
                    final Iterator<PrioritizedCommand> itr = clone.iterator();
                    while (itr.hasNext()) {
                        final PrioritizedCommand command = itr.next();
                        command.execute();
                    }
                }
            }
        };
    }

    public static final GridLayerRedrawManager get() {
        return instance;
    }

    public void schedule(final PrioritizedCommand command) {
        Objects.requireNonNull(command, "command");
        if (!commands.contains(command)) {
            commands.add(command);
            kick();
        }
    }

    private void kick() {
        if (commands.size() > 0) {
            AnimationScheduler.get().requestAnimationFrame(callback);
        }
    }

    public static abstract class PrioritizedCommand implements Command {

        private int priority = 0;

        public PrioritizedCommand(final int priority) {
            this.priority = priority;
        }

        public int getPriority() {
            return priority;
        }
    }
}
