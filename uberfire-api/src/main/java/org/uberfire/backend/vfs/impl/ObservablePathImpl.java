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
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceCopied;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamed;
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

    public static String removeExtension( final String filename ) {
        if ( filename == null ) {
            return null;
        }
        final int index = indexOfExtension( filename );
        if ( index == -1 ) {
            return filename;
        } else {
            return filename.substring( 0, index );
        }
    }

    public static int indexOfExtension( final String filename ) {
        if ( filename == null ) {
            return -1;
        }
        final int extensionPos = filename.lastIndexOf( "." );
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
        if ( path != null && path.equals( renamedEvent.getPath() ) ) {
            path = renamedEvent.getDestinationPath();
            if ( sessionInfo.getId().equals( renamedEvent.getSessionInfo().getId() ) ) {
                executeRenameCommands();
            } else {
                executeConcurrentRenameCommand( renamedEvent.getPath(),
                                                renamedEvent.getDestinationPath(),
                                                renamedEvent.getSessionInfo().getId(),
                                                renamedEvent.getSessionInfo().getIdentity() );
            }
        }
    }

    void onResourceDeleted( @Observes final ResourceDeletedEvent deletedEvent ) {
        if ( path != null && path.equals( deletedEvent.getPath() ) ) {
            if ( sessionInfo.getId().equals( deletedEvent.getSessionInfo().getId() ) ) {
                executeDeleteCommands();
            } else {
                executeConcurrentDeleteCommand( deletedEvent.getPath(),
                                                deletedEvent.getSessionInfo().getId(),
                                                deletedEvent.getSessionInfo().getIdentity() );
            }
        }
    }

    void onResourceUpdated( @Observes final ResourceUpdatedEvent updatedEvent ) {
        if ( path != null && path.equals( updatedEvent.getPath() ) ) {
            if ( sessionInfo.getId().equals( updatedEvent.getSessionInfo().getId() ) ) {
                executeUpdateCommands();
            } else {
                executeConcurrentUpdateCommand( updatedEvent.getPath(),
                                                updatedEvent.getSessionInfo().getId(),
                                                updatedEvent.getSessionInfo().getIdentity() );
            }
        }
    }

    void onResourceCopied( @Observes final ResourceCopiedEvent copiedEvent ) {
        if ( path != null && path.equals( copiedEvent.getPath() ) ) {
            if ( sessionInfo.getId().equals( copiedEvent.getSessionInfo().getId() ) ) {
                executeCopyCommands();
            } else {
                executeConcurrentCopyCommand( copiedEvent.getPath(),
                                              copiedEvent.getDestinationPath(),
                                              copiedEvent.getSessionInfo().getId(),
                                              copiedEvent.getSessionInfo().getIdentity() );
            }
        }
    }

    void onResourceBatchEvent( @Observes final ResourceBatchChangesEvent batchEvent ) {
        if ( path != null && batchEvent.containPath( path ) ) {
            if ( sessionInfo.getId().equals( batchEvent.getSessionInfo().getId() ) ) {
                for ( final ResourceChange change : batchEvent.getChanges( path ) ) {
                    switch ( change.getType() ) {
                        case COPY:
                            executeCopyCommands();
                            break;
                        case DELETE:
                            executeDeleteCommands();
                            break;
                        case RENAME:
                            path = ( (ResourceRenamed) change ).getDestinationPath();
                            executeRenameCommands();
                            break;
                        case UPDATE:
                            executeUpdateCommands();
                            break;
                    }
                }
            } else {
                for ( final ResourceChange change : batchEvent.getChanges( path ) ) {
                    switch ( change.getType() ) {
                        case COPY:
                            executeConcurrentCopyCommand( path,
                                                          ( (ResourceCopied) change ).getDestinationPath(),
                                                          batchEvent.getSessionInfo().getId(),
                                                          batchEvent.getSessionInfo().getIdentity() );
                            break;
                        case DELETE:
                            executeConcurrentDeleteCommand( path,
                                                            batchEvent.getSessionInfo().getId(),
                                                            batchEvent.getSessionInfo().getIdentity() );
                            break;
                        case RENAME:
                            path = ( (ResourceRenamed) change ).getDestinationPath();
                            executeConcurrentRenameCommand( path,
                                                            ( (ResourceRenamed) change ).getDestinationPath(),
                                                            batchEvent.getSessionInfo().getId(),
                                                            batchEvent.getSessionInfo().getIdentity() );
                            break;
                        case UPDATE:
                            executeConcurrentUpdateCommand( path,
                                                            batchEvent.getSessionInfo().getId(),
                                                            batchEvent.getSessionInfo().getIdentity() );
                            break;
                    }
                }
            }
        }
    }

    private void executeRenameCommands() {
        if ( !onRenameCommand.isEmpty() ) {
            for ( final Command command : onRenameCommand ) {
                command.execute();
            }
        }
    }

    private void executeConcurrentRenameCommand( final Path path,
                                                 final Path destinationPath,
                                                 final String sessionId,
                                                 final Identity identity ) {
        if ( !onConcurrentRenameCommand.isEmpty() ) {
            for ( final ParameterizedCommand<OnConcurrentRenameEvent> command : onConcurrentRenameCommand ) {
                final OnConcurrentRenameEvent event = new OnConcurrentRenameEvent() {
                    @Override
                    public Path getSource() {
                        return path;
                    }

                    @Override
                    public Path getTarget() {
                        return destinationPath;
                    }

                    @Override
                    public String getId() {
                        return sessionId;
                    }

                    @Override
                    public Identity getIdentity() {
                        return identity;
                    }
                };
                command.execute( event );
            }
        }
    }

    private void executeCopyCommands() {
        if ( !onCopyCommand.isEmpty() ) {
            for ( final Command command : onCopyCommand ) {
                command.execute();
            }
        }
    }

    private void executeConcurrentCopyCommand( final Path path,
                                               final Path destinationPath,
                                               final String sessionId,
                                               final Identity identity ) {
        if ( !onConcurrentCopyCommand.isEmpty() ) {
            final OnConcurrentCopyEvent copyEvent = new OnConcurrentCopyEvent() {
                @Override
                public Path getSource() {
                    return path;
                }

                @Override
                public Path getTarget() {
                    return destinationPath;
                }

                @Override
                public String getId() {
                    return sessionId;
                }

                @Override
                public Identity getIdentity() {
                    return identity;
                }
            };
            for ( final ParameterizedCommand<OnConcurrentCopyEvent> command : onConcurrentCopyCommand ) {
                command.execute( copyEvent );
            }
        }
    }

    private void executeUpdateCommands() {
        if ( !onUpdateCommand.isEmpty() ) {
            for ( final Command command : onUpdateCommand ) {
                command.execute();
            }
        }
    }

    private void executeConcurrentUpdateCommand( final Path path,
                                                 final String sessionId,
                                                 final Identity identity ) {
        if ( !onConcurrentUpdateCommand.isEmpty() ) {
            final OnConcurrentUpdateEvent event = new OnConcurrentUpdateEvent() {
                @Override
                public Path getPath() {
                    return path;
                }

                @Override
                public String getId() {
                    return sessionId;
                }

                @Override
                public Identity getIdentity() {
                    return identity;
                }
            };
            for ( final ParameterizedCommand<OnConcurrentUpdateEvent> command : onConcurrentUpdateCommand ) {
                command.execute( event );
            }
        }
    }

    private void executeDeleteCommands() {
        if ( !onDeleteCommand.isEmpty() ) {
            for ( final Command command : onDeleteCommand ) {
                command.execute();
            }
        }
    }

    private void executeConcurrentDeleteCommand( final Path path,
                                                 final String sessionId,
                                                 final Identity identity ) {
        if ( !onConcurrentDeleteCommand.isEmpty() ) {
            final OnConcurrentDelete event = new OnConcurrentDelete() {
                @Override
                public Path getPath() {
                    return path;
                }

                @Override
                public String getId() {
                    return sessionId;
                }

                @Override
                public Identity getIdentity() {
                    return identity;
                }
            };
            for ( final ParameterizedCommand<OnConcurrentDelete> command : onConcurrentDeleteCommand ) {
                command.execute( event );
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
