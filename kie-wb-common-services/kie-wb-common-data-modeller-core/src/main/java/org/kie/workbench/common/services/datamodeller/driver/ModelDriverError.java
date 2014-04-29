package org.kie.workbench.common.services.datamodeller.driver;

import org.uberfire.java.nio.file.Path;

public class ModelDriverError {

    private long id;

    private String message;

    private Path file;

    private int line;

    private int column;

    private Exception nativeError;

    public ModelDriverError( long id, String message, Path file, int line, int column, Exception nativeError ) {
        this.id = id;
        this.message = message;
        this.file = file;
        this.line = line;
        this.column = column;
        this.nativeError = nativeError;
    }

    public ModelDriverError( String message, Path file ) {
        this.message = message;
        this.file = file;
    }

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage( String message ) {
        this.message = message;
    }

    public Path getFile() {
        return file;
    }

    public void setFile( Path file ) {
        this.file = file;
    }

    public int getLine() {
        return line;
    }

    public void setLine( int line ) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn( int column ) {
        this.column = column;
    }

    public Exception getNativeError() {
        return nativeError;
    }

    public void setNativeError( Exception nativeError ) {
        this.nativeError = nativeError;
    }
}
