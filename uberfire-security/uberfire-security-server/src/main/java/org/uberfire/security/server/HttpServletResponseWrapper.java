package org.uberfire.security.server;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * Uberfire Security passes HttpServletResponse objects deep into its
 * pluggable parts, who are allowed to call any method including
 * {@link #sendRedirect(String)} or either of the
 * {@link #sendError(int)} methods. The
 * framework relies on strict behaviour of the {@link #isCommitted()}
 * method: it must always return true once a forward or error has been
 * sent. This wrapper guarantees that behaviour.
 *
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
class HttpServletResponseWrapper extends javax.servlet.http.HttpServletResponseWrapper {

    private boolean committed;

    /**
     * Creates a wrapper for the given response object.
     *
     * @param response The response object to wrap. Must not be null.
     */
    public HttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void sendError( int sc, String msg ) throws IOException {
        committed = true;
        super.sendError( sc, msg );
    }

    @Override
    public void sendError( int sc ) throws IOException {
        committed = true;
        super.sendError( sc );
    }

    @Override
    public void sendRedirect( String location ) throws IOException {
        committed = true;
        super.sendRedirect( location );
    }

    @Override
    public void flushBuffer() throws IOException {
        committed = true;
        super.flushBuffer();
    }

    @Override
    public boolean isCommitted() {
        return committed || super.isCommitted();
    }

}
