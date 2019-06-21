package org.uberfire.java.nio.fs.jgit;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class FileSystemLockManager {

    final Map<String, FileSystemLock> fileSystemsLocks = new ConcurrentHashMap<>();

    private static class LazyHolder {

        static final FileSystemLockManager INSTANCE = new FileSystemLockManager();
    }

    public static FileSystemLockManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public FileSystemLock getFileSystemLock(File directory,
                                            String lockName,
                                            TimeUnit lastAccessTimeUnit,
                                            long lastAccessThreshold) {

        return fileSystemsLocks.computeIfAbsent(directory.getAbsolutePath(),
                                                key -> new FileSystemLock(directory,
                                                                          lockName,
                                                                          lastAccessTimeUnit,
                                                                          lastAccessThreshold));
    }
}
