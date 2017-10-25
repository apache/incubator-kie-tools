/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.services.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Provider for our HTTP Exceptions to use BusinessExceptions to be thrown to
 * the client side if
 * something bad happens on the server side
 */
@Provider
public class HttpStatusExceptionHandler implements ExceptionMapper<BusinessException> {

    @Override
    public Response toResponse(final BusinessException exception) {
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error: Business Exception " + exception.getMessage()).build();
    }
}
