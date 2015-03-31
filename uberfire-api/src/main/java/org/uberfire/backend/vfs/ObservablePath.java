package org.uberfire.backend.vfs;

import org.uberfire.commons.lifecycle.Disposable;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.rpc.SessionInfo;

public interface ObservablePath extends Path,
                                        Disposable {

    void onRename( final Command command );

    void onDelete( final Command command );

    void onUpdate( final Command command );

    void onCopy( final Command command );

    void onConcurrentRename( final ParameterizedCommand<OnConcurrentRenameEvent> command );

    void onConcurrentDelete( final ParameterizedCommand<OnConcurrentDelete> command );

    void onConcurrentUpdate( final ParameterizedCommand<OnConcurrentUpdateEvent> command );

    void onConcurrentCopy( final ParameterizedCommand<OnConcurrentCopyEvent> command );

    ObservablePath wrap( final Path path );

    public interface OnConcurrentUpdateEvent extends SessionInfo {

        Path getPath();
    }

    public interface OnConcurrentDelete extends SessionInfo {

        Path getPath();
    }

    public interface OnConcurrentRenameEvent extends SessionInfo {

        Path getSource();

        Path getTarget();
    }

    public interface OnConcurrentCopyEvent extends SessionInfo {

        Path getSource();

        Path getTarget();
    }
}
