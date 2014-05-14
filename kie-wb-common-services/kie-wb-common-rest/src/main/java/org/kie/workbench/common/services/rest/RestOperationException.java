package org.kie.workbench.common.services.rest;


public class RestOperationException extends RuntimeException {

    /** Generated serial version UID */
    private static final long serialVersionUID = 3065096836333886139L;

    protected Integer status = null;

    public RestOperationException(String s) {
        super(s);
    }

    protected RestOperationException(String s, Status status) {
        super(s);
        this.status = status.getValue();
    }

    public RestOperationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    protected RestOperationException(String s, Throwable throwable, Status status) {
        super(s, throwable);
        this.status = status.getValue();
    }

    public int getStatus() {
        return status;
    }

    public enum Status { 
        /** === SYNTAX === */
        
        // The request could not be understood by the server due to malformed syntax. 
        // The client SHOULD NOT repeat the request without modifications. 
        BAD_REQUEST(400), 
       
        /** === FUNCTIONAL === */
        
        // The server understood the request, but is refusing to fulfill it. 
        // Authorization will not help and the request SHOULD NOT be repeated.
        FORBIDDEN(403),
       
        // The server has not found anything matching the Request-URI. 
        // No indication is given of whether the condition is temporary or permanent. 
        NOT_FOUND(404), 
        
        // The request could not be completed due to a conflict with the current state of the resource. 
        // This code is only allowed in situations where it is expected that 
        // the user might be able to resolve the conflict and resubmit the request. 
        CONFLICT(409), 

        /** === TECHNICAL === */
        
        // The server encountered an unexpected condition which prevented it from fulfilling the request. 
        INTERNAL_SERVER_ERROR(500),
        
        /**
         * SHOULD NOT BE USED! (because these are inappropriate) 
         * Added for documentation
         */
        
        // The method specified in the Request-Line is not allowed for the resource identified by the Request-URI. 
        // The response MUST include an Allow header containing a list of valid methods for the requested resource. 
        METHOD_NOT_ALLOWED(405),
        
        // The requested resource is no longer available at the server and no forwarding address is known. 
        // This condition is expected to be considered permanent.
        GONE(410),
        
        // The precondition given in one or more of the request-header fields evaluated to false when it was tested on the server.
        PRE_CONDITION_FAILED(412),

        /** === FORMAT === */
        
        // The resource identified by the request is only capable of generating response entities which have content characteristics 
        // not acceptable according to the accept headers sent in the request.  (e.g. XML, JSON)
        NOT_ACCEPTABLE(406),
        
        //  The server is refusing to service the request because the entity of the request is 
        // in a format not supported by the requested resource for the requested method.
        UNSUPPORTED_MEDIA_TYPE(415),
        
        /** === AUTH === */
        
        // The request requires user authentication. 
        UNAUTHORIZED(401);
        
        private final int status;
        
        Status(int s) { 
           this.status = s; 
        }
        
        public int getValue() { 
            return this.status;
        }
    }
   
    // Syntax
    public static RestOperationException badRequest(String msg) { 
        return new RestOperationException(msg, Status.BAD_REQUEST);
    }
   
    // Command not accepted 
    public static RestOperationException forbidden(String msg) { 
        return new RestOperationException(msg, Status.FORBIDDEN);
    }
   
    // permission problem
    public static RestOperationException conflict(String msg, Exception e) { 
        return new RestOperationException(msg, e, Status.CONFLICT);
    }
  
    // instance does not exist
    public static RestOperationException notFound(String msg) { 
        return new RestOperationException(msg, Status.NOT_FOUND);
    }
   
    // technical exception (including serialization problems)
    public static RestOperationException internalServerError(String msg) { 
        return new RestOperationException(msg, Status.INTERNAL_SERVER_ERROR);
    }
    
    public static RestOperationException internalServerError(String msg, Exception e) { 
        return new RestOperationException(msg, e, Status.INTERNAL_SERVER_ERROR);
    }
}
