package org.uberfire.java.nio.fs.jgit.util.commands;

import java.io.IOException;
import java.util.function.Consumer;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;

public class WriteConfiguration {

    private final Repository repo;
    private final Consumer<StoredConfig> consumer;

    public WriteConfiguration(final Repository repo,
                              final Consumer<StoredConfig> consumer) {
        this.repo = repo;
        this.consumer = consumer;
    }

    public void execute() {
        final StoredConfig cfg = repo.getConfig();
        consumer.accept(cfg);
        try {
            cfg.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
