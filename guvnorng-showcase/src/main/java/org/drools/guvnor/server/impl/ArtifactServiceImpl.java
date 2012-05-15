/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.server.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.enterprise.context.ApplicationScoped;

import org.drools.guvnor.shared.ArtifactService;

@ApplicationScoped
public class ArtifactServiceImpl implements ArtifactService {

    @Override
    public String getArtifactContent(final String artifactId) {
        System.out.println("ArtifactServiceImpl::getArtifactContent::OK");
//        try {
//            Path path = new Path(decode(artifactId));
//            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
//
//            InputStream stream = file.getContents(false);
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//
//            while ((line = br.readLine()) != null) {
//                sb.append(line + "\n");
//            }
//
//            br.close();
//            return sb.toString();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return "some content here!!!!";
    }

    @Override
    public void save(final String artifactId, final String content) {
        System.out.println("ArtifactServiceImpl::save::OK");
//        try {
//            PipedInputStream in = new PipedInputStream();
//            OutputStream out = new PipedOutputStream(in);
//
//            out.write(content.getBytes());
//            out.close();
//
//            Path path = new Path(decode(artifactId));
//            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
//
//            file.setContents(in, true, true, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private String decode(String content) {
        try {
            return URLDecoder.decode(content, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return content;
        }
    }
}
