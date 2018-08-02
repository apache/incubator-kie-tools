/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.kie.workbench.common.services.backend.compiler.HttpCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultHttpCompilationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utils for the Rest client/server classes
 */
public class RestUtils {

    protected static Logger logger = LoggerFactory.getLogger(RestUtils.class);

    public static HttpCompilationResponse readDefaultCompilationResponseFromBytes(byte[] bytes) {

        ObjectInput in = null;
        ByteArrayOutputStream bos = null;

        try {
            in = new ObjectInputStream(new ByteArrayInputStream(bytes));
            Object newObj = in.readObject();
            return (HttpCompilationResponse) newObj;
        } catch (NotSerializableException nse) {
            nse.printStackTrace();
            StringBuilder sb = new StringBuilder("NotSerializableException:").append(nse.getMessage());
            logger.error(sb.toString());
        } catch (IOException ioe) {
            StringBuilder sb = new StringBuilder("IOException:").append(ioe.getMessage());
            logger.error(sb.toString());
        } catch (ClassNotFoundException cnfe) {
            StringBuilder sb = new StringBuilder("ClassNotFoundException:").append(cnfe.getMessage());
            logger.error(sb.toString());
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder("Exception:").append(e.getMessage());
            logger.error(sb.toString());
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }
        }
        return new DefaultHttpCompilationResponse(Boolean.FALSE);
    }

    public static byte[] serialize(Object obj) {
        byte[] returnArray = null;
        try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
            try (ObjectOutputStream o = new ObjectOutputStream(b)) {
                o.writeObject(obj);
            }
            returnArray = b.toByteArray();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        return returnArray;
    }
}
