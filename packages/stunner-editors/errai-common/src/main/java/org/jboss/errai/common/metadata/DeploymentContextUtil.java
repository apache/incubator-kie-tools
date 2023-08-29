/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.jboss.errai.common.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A set of utilities for processing a {@link DeploymentContext}
 *
 * @author Heiko Braun <hbraun@redhat.com>
 */
class DeploymentContextUtil {
  private static final Logger log = LoggerFactory.getLogger("ClasspathScanning");

  static File identifyDeployment(final URL url) {
    String actualFilePath = url.getPath();
    if (actualFilePath.startsWith("file:")) {
      actualFilePath = actualFilePath.substring(5);
    }

    final int nestedSeparator = actualFilePath.indexOf('!');
    if (nestedSeparator != -1) {
      actualFilePath = actualFilePath.substring(0, nestedSeparator);
    }

    log.debug("scanning inside: " + actualFilePath);

    return findActualDeploymentFile(new File(actualFilePath));
  }

  static void process(final DeploymentContext ctx) {
    for (final URL url : ctx.getConfigUrls()) {
      final File file = DeploymentContextUtil.identifyDeployment(url);

      /*
       * several config urls may derive from the same archive
       * don't process them twice
       */
      if (!ctx.hasProcessed(file)) {
        ctx.markProcessed(file);
        if (file.getName().endsWith(".ear")){
          DeploymentContextUtil.processNestedZip(file, ctx);
        }
      }
    }
  }

  private static File findActualDeploymentFile(File start) {
    int pivotPoint;
    String rootPath = start.getPath();

    do {
      start = new File(rootPath);
      rootPath = rootPath.substring(0, (pivotPoint = rootPath.lastIndexOf(File.separator)) < 0 ? 0 : pivotPoint);
    }
    while (!start.exists() && pivotPoint > 0);

    return start;
  }

  private static void processNestedZip(final File file, final DeploymentContext ctx) {
    try {
      if (file.getName().matches(".+\\.(ear|war|sar)$") && !file.isDirectory()) // process only certain deployment types
      {
        if (file.getName().endsWith(".war"))
          ctx.getSubContexts().put(file.getName(), file); // WEB-INF/classes

        final ZipInputStream zipFile = new ZipInputStream(new FileInputStream(file));
        ZipEntry zipEntry;

        try {
          while ((zipEntry = zipFile.getNextEntry()) != null) {
            if (zipEntry.getName().matches(".+\\.(zip|jar|war)$")) // expand nested zip archives
            {
              if (!ctx.getSubContexts().containsKey(zipEntry.getName())) {
                final File tmpUnZip = expandZipEntry(zipFile, zipEntry, ctx);
                ctx.getSubContexts().put(zipEntry.getName(), tmpUnZip);
                processNestedZip(tmpUnZip, ctx);
              }
            }
          }
        }
        finally {
          zipFile.close();
        }
      }
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to process nested zip", e);
    }
  }

  private static File expandZipEntry(final ZipInputStream stream, final ZipEntry entry, final DeploymentContext ctx) {
    final String tmpUUID = "erraiBootstrap_" + UUID.randomUUID().toString().replaceAll("\\-", "_");
    final String tmpDir = System.getProperty("java.io.tmpdir") + "/" + tmpUUID;
    final int idx = entry.getName().lastIndexOf('/');
    final String tmpFileName = tmpDir + "/" + entry.getName().substring(idx == -1 ? 0 : idx);

    try {
      final File tmpDirFile = new File(tmpDir);
      if (!tmpDirFile.exists() && !tmpDirFile.mkdirs()) {
        throw new RuntimeException("unable to create temporary directory: " + tmpDirFile.getAbsolutePath());
      }
      ctx.markTmpFile(tmpDirFile);

      final File newFile = new File(tmpFileName);

      final FileOutputStream outStream = new FileOutputStream(newFile);
      final byte[] buf = new byte[1024];
      int read;
      while ((read = stream.read(buf)) != -1) {
        outStream.write(buf, 0, read);
      }

      outStream.flush();
      outStream.close();

      return newFile;
    }
    catch (RuntimeException e) {
      throw e;
    }
    catch (Exception e) {
      throw new RuntimeException("Error reading from stream", e);
    }
  }
}
