package org.uberfire.backend.vfs.impl;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.backend.vfs.FileSystem;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

@Portable
@Dependent
public class ObservablePathImpl implements ObservablePath {

    private Path path;
    private Path original;

    @Inject
    private transient SessionInfo sessionInfo;

    private transient List<Command> onRenameCommand = new ArrayList<Command>();
    private transient List<Command> onDeleteCommand = new ArrayList<Command>();
    private transient List<Command> onUpdateCommand = new ArrayList<Command>();
    private transient List<Command> onCopyCommand = new ArrayList<Command>();
    private transient List<ParameterizedCommand<OnConcurrentRenameEvent>> onConcurrentRenameCommand = new ArrayList<ParameterizedCommand<OnConcurrentRenameEvent>>();
    private transient List<ParameterizedCommand<OnConcurrentDelete>> onConcurrentDeleteCommand = new ArrayList<ParameterizedCommand<OnConcurrentDelete>>();
    private transient List<ParameterizedCommand<OnConcurrentUpdateEvent>> onConcurrentUpdateCommand = new ArrayList<ParameterizedCommand<OnConcurrentUpdateEvent>>();
    private transient List<ParameterizedCommand<OnConcurrentCopyEvent>> onConcurrentCopyCommand = new ArrayList<ParameterizedCommand<OnConcurrentCopyEvent>>();

    public ObservablePathImpl() {
    }

    @Override
    public ObservablePath wrap( final Path path ) {
        if ( path instanceof ObservablePathImpl ) {
            this.original = ( (ObservablePathImpl) path ).path;
        } else {
            this.original = path;
        }
        this.path = this.original;
        return this;
    }

    @Override
    public FileSystem getFileSystem() {
        return path.getFileSystem();
    }

    @Override
    public String getFileName() {
        return path.getFileName();
    }
    
	public static String removeExtension(final String filename) {
		if (filename == null) {
			return null;
		}
		final int index = indexOfExtension(filename);
		if (index == -1) {
			return filename;
		} else {
			return filename.substring(0, index);
		}
	}

	public static int indexOfExtension(final String filename) {
		if (filename == null) {
			return -1;
		}
		final int extensionPos = filename.lastIndexOf(".");
		return extensionPos;
	}

    @Override
    public String toURI() {
        return path.toURI();
    }

    @Override
    public int compareTo( final Path o ) {
        return path.compareTo( o );
    }

    @Override
    public void onRename( final Command command ) {
        this.onRenameCommand.add( command );
    }

    @Override
    public void onDelete( final Command command ) {
        this.onDeleteCommand.add( command );
    }

    @Override
    public void onUpdate( final Command command ) {
        this.onUpdateCommand.add( command );
    }

    @Override
    public void onCopy( final Command command ) {
        this.onCopyCommand.add( command );
    }

    @Override
    public void onConcurrentRename( final ParameterizedCommand<OnConcurrentRenameEvent> command ) {
        this.onConcurrentRenameCommand.add( command );
    }

    @Override
    public void onConcurrentDelete( final ParameterizedCommand<OnConcurrentDelete> command ) {
        this.onConcurrentDeleteCommand.add( command );
    }

    @Override
    public void onConcurrentUpdate( final ParameterizedCommand<OnConcurrentUpdateEvent> command ) {
        this.onConcurrentUpdateCommand.add( command );
    }

    @Override
    public void onConcurrentCopy( final ParameterizedCommand<OnConcurrentCopyEvent> command ) {
        this.onConcurrentCopyCommand.add( command );
    }

    @Override
    public void dispose() {
        onRenameCommand.clear();
        onDeleteCommand.clear();
        onUpdateCommand.clear();
        onCopyCommand.clear();
        onConcurrentRenameCommand.clear();
        onConcurrentDeleteCommand.clear();
        onConcurrentUpdateCommand.clear();
        onConcurrentCopyCommand.clear();
        if ( IOC.getBeanManager() != null ) {
            IOC.getBeanManager().destroyBean( this );
        }
    }

    void onResourceRenamed( @Observes final ResourceRenamedEvent renamedEvent ) {
        if ( path != null && path.equals( renamedEvent.getSourcePath() ) ) {
            path = renamedEvent.getDestinationPath();
            if ( sessionInfo.getId().equals( renamedEvent.getSessionInfo().getId() ) ) {
                if ( !onRenameCommand.isEmpty() ) {
                    for ( final Command command : onRenameCommand ) {
                        command.execute();
                    }
                }
            } else {
                if ( !onConcurrentRenameCommand.isEmpty() ) {
                    for ( final ParameterizedCommand<OnConcurrentRenameEvent> command : onConcurrentRenameCommand ) {
                        command.execute( new OnConcurrentRenameEvent() {
                            @Override
                            public Path getSource() {
                                return renamedEvent.getSourcePath();
                            }

                            @Override
                            public Path getTarget() {
                                return renamedEvent.getDestinationPath();
                            }

                            @Override
                            public String getId() {
                                return renamedEvent.getSessionInfo().getId();
                            }

                            @Override
                            public Identity getIdentity() {
                                return renamedEvent.getSessionInfo().getIdentity();
                            }
                        } );
                    }
                }
            }
        }
    }

    void onResourceDeleted( @Observes final ResourceDeletedEvent deletedEvent ) {
        if ( path != null && path.equals( deletedEvent.getPath() ) ) {
            if ( sessionInfo.getId().equals( deletedEvent.getSessionInfo().getId() ) ) {
                if ( !onDeleteCommand.isEmpty() ) {
                    for ( final Command command : onDeleteCommand ) {
                        command.execute();
                    }
                }
            } else {
                if ( !onConcurrentDeleteCommand.isEmpty() ) {
                    for ( final ParameterizedCommand<OnConcurrentDelete> command : onConcurrentDeleteCommand ) {
                        command.execute( new OnConcurrentDelete() {
                            @Override
                            public Path getPath() {
                                return deletedEvent.getPath();
                            }

                            @Override
                            public String getId() {
                                return deletedEvent.getSessionInfo().getId();
                            }

                            @Override
                            public Identity getIdentity() {
                                return deletedEvent.getSessionInfo().getIdentity();
                            }
                        } );
                    }
                }
            }
        }
    }

    void onResourceUpdated( @Observes final ResourceUpdatedEvent updatedEvent ) {
        if ( path != null && path.equals( updatedEvent.getPath() ) ) {
            if ( sessionInfo.getId().equals( updatedEvent.getSessionInfo().getId() ) ) {
                if ( !onUpdateCommand.isEmpty() ) {
                    for ( final Command command : onUpdateCommand ) {
                        command.execute();
                    }
                }
            } else {
                if ( !onConcurrentUpdateCommand.isEmpty() ) {
                    for ( final ParameterizedCommand<OnConcurrentUpdateEvent> command : onConcurrentUpdateCommand ) {
                        command.execute( new OnConcurrentUpdateEvent() {
                            @Override
                            public Path getPath() {
                                return updatedEvent.getPath();
                            }

                            @Override
                            public String getId() {
                                return updatedEvent.getSessionInfo().getId();
                            }

                            @Override
                            public Identity getIdentity() {
                                return updatedEvent.getSessionInfo().getIdentity();
                            }
                        } );
                    }
                }
            }
        }
    }

    void onResourceCopied( @Observes final ResourceCopiedEvent copiedEvent ) {
        if ( path != null && path.equals( copiedEvent.getSourcePath() ) ) {
            if ( sessionInfo.getId().equals( copiedEvent.getSessionInfo().getId() ) ) {
                if ( !onCopyCommand.isEmpty() ) {
                    for ( final Command command : onCopyCommand ) {
                        command.execute();
                    }
                }
            } else {
                if ( !onConcurrentCopyCommand.isEmpty() ) {
                    for ( final ParameterizedCommand<OnConcurrentCopyEvent> command : onConcurrentCopyCommand ) {
                        command.execute( new OnConcurrentCopyEvent() {
                            @Override
                            public Path getSource() {
                                return copiedEvent.getSourcePath();
                            }

                            @Override
                            public Path getTarget() {
                                return copiedEvent.getDestinationPath();
                            }

                            @Override
                            public String getId() {
                                return copiedEvent.getSessionInfo().getId();
                            }

                            @Override
                            public Identity getIdentity() {
                                return copiedEvent.getSessionInfo().getIdentity();
                            }
                        } );
                    }
                }
            }
        }
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Path ) ) {
            return false;
        }

        if ( o instanceof ObservablePathImpl ) {
            return this.original.equals( ( (ObservablePathImpl) o ).original );
        }

        return this.original.equals( o );
    }

    @Override
    public int hashCode() {
        return this.original.toURI().hashCode();
    }

}
