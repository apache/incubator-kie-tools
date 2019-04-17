/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.jbpm.server.service.impl.documents.storage;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import org.apache.commons.fileupload.FileItem;
import org.kie.workbench.common.forms.jbpm.model.document.DocumentData;
import org.kie.workbench.common.forms.jbpm.server.service.impl.documents.DocumentUploadSession;
import org.kie.workbench.common.forms.jbpm.service.shared.documents.DocumentUploadChunk;
import org.uberfire.server.BaseUploadServlet;

@WebServlet(name = "FormsDocumentServlet", urlPatterns = "/documentUploadServlet")
public class FormsDocumentServlet extends BaseUploadServlet {

    @Inject
    protected UploadedDocumentStorage storage;

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp) throws ServletException, IOException {

        Map<String, Object> response = new HashMap<>();

        try {

            FileItem fileItem = getFileItem(req);

            String id = UUID.randomUUID().toString();

            String content = Base64.getEncoder().encodeToString(fileItem.get());

            DocumentUploadChunk chunk = new DocumentUploadChunk(id, fileItem.getName(), 0, 1, content);

            DocumentUploadSession session = new DocumentUploadSession(chunk.getDocumentId(), chunk.getDocumentName(), chunk.getMaxChunks());

            session.add(chunk);

            storage.uploadContentChunk(chunk);

            session.setState(DocumentUploadSession.State.MERGING);

            storage.merge(session);

            DocumentData data = new DocumentData(id,
                                                 fileItem.getName(),
                                                 fileItem.getSize(),
                                                 "",
                                                 System.currentTimeMillis());

            response.put("document", data);
        } catch (Exception e) {
            response.put("error", "error");
        } finally {
            writeResponse(resp, response);
        }
    }

    protected void writeResponse(HttpServletResponse response,
                                 Map<String, Object> uploadResponse) throws IOException {
        Gson gson = new Gson();
        response.setContentType("text/html");
        response.getWriter().write(gson.toJson(uploadResponse));
        response.getWriter().flush();
    }
}
