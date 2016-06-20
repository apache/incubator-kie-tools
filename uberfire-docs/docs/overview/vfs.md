# VFS
UberFire offers a virtual file system (VFS) API that’s accessible from both client-side (runs in the web browser) and server-side (runs in a Java app server) code.

The VFS has pluggable backends, and UberFire includes both a plain filesystem backend and a Git backend. This means you can write code that runs in the web browser that interacts with the contents of a filesystem directory or a Git repository: exploring, reading files, checking in changes, reviewing history, even branching and merging
