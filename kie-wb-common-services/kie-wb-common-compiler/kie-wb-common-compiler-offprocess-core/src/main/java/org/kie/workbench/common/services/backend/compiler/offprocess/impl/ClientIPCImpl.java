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
package org.kie.workbench.common.services.backend.compiler.offprocess.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.TailerDirection;
import net.openhft.chronicle.wire.DocumentContext;
import net.openhft.chronicle.wire.Wire;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponseOffProcess;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.offprocess.ClientIPC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * Client to access the result of the build executed in a separated process
 */

public class ClientIPCImpl implements ClientIPC {

    private ResponseSharedMap map;
    private QueueProvider provider;
    private Logger logger = LoggerFactory.getLogger(ClientIPCImpl.class);

    public ClientIPCImpl(ResponseSharedMap map, QueueProvider provider) {
        this.map = map;
        this.provider = provider;
    }

    public KieCompilationResponse getResponse(String uuid) {
        if(isLoaded(uuid)) {
            return (KieCompilationResponse)map.getResponse(uuid);
        }else {
            return new DefaultKieCompilationResponse(false, "");
        }
    }

    private boolean isLoaded(String uuid) {
        ExcerptTailer tailer = provider.getQueue().createTailer();
        DefaultKieCompilationResponseOffProcess res = readThisDocument(tailer);
        DefaultKieCompilationResponse kres = new DefaultKieCompilationResponse(res);
        if (uuid.equals(kres.getRequestUUID())) {
            if (!map.contains(kres.getRequestUUID())) {
                map.addResponse(uuid, kres);
                return true;
            }
        } else {
            //we loop in the queue to find our Response by UUID, from the tail of the queue backward
            tailer.toEnd();
            tailer.direction(TailerDirection.BACKWARD);
            res = loopOverQueue(tailer,uuid, 0l);
        }

        kres = new DefaultKieCompilationResponse(res);
        if (!map.contains(kres.getRequestUUID())) {
            map.addResponse(uuid, new DefaultKieCompilationResponse(res));
            return true;
        } else {
            return false;
        }
    }


    private DefaultKieCompilationResponseOffProcess loopOverQueue(ExcerptTailer tailer, String uuid, long previousIndex) {
        long currentIndex = tailer.index();
        if(logger.isDebugEnabled()) {
            logger.debug("current index on loopOverQueue:{}", currentIndex);
        }
        DefaultKieCompilationResponseOffProcess  res = readThisDocument(tailer);
        if(uuid.equals(res.getRequestUUID())){
            return res;
        }else{
            if(currentIndex == previousIndex){
                // No more elements in the queue
                return new DefaultKieCompilationResponseOffProcess(false, "");
            }
            return loopOverQueue(tailer, uuid, currentIndex);
        }
    }

    private DefaultKieCompilationResponseOffProcess readThisDocument(ExcerptTailer tailer) {
        if(logger.isDebugEnabled()) {
            logger.debug("current index on readThisDocument:{}", tailer.index());
        }
        DefaultKieCompilationResponseOffProcess res = null;
        try (DocumentContext dc = tailer.readingDocument()) {
            if (dc.isPresent()) {
                if(logger.isDebugEnabled()) {
                    logger.debug("Document Context index:{}", dc.index());
                }
                Wire wire = dc.wire();
                Bytes bytes = wire.bytes();
                if (!bytes.isEmpty()) {
                    try {
                        Object obj = deserialize(bytes.toByteArray());
                        res = (DefaultKieCompilationResponseOffProcess) obj;
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }
        if(res == null){
            res = new DefaultKieCompilationResponseOffProcess(false, "");
        }
        return res;
    }

    private static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream b = new ByteArrayInputStream(bytes)) {
            try (ObjectInputStream o = new ObjectInputStream(b)) {
                return o.readObject();
            }
        }
    }
}
