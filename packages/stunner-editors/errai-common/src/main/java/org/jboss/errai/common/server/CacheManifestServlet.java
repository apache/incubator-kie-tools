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


package org.jboss.errai.common.server;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.errai.common.client.api.Assert;

/**
 * This servlet provides a cache manifest file specific to the requesting user
 * agent. It responds to .appcache requests and dispatches to user agent
 * specific appcache.manifest files (i.e. safari.appcache.manifest). These files
 * are generated at compile time by a dedicated linker. See the Errai reference
 * guide for details on how to activate this linker.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@WebServlet(urlPatterns = "*.appcache")
public class CacheManifestServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final String MANIFEST_FILE_EXTENSION = ".appcache.manifest";

  // Lazily populated cache for user agent names for which a manifest file
  // exists, on a per module basis
  private final Map<String, Set<String>> manifestsPerModule = new ConcurrentHashMap<String, Set<String>>();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    final Pattern pattern = Pattern.compile("/([a-zA-Z0-9_]+)/errai.appcache");
    final Matcher matcher = pattern.matcher(req.getServletPath());
    if (!matcher.find()) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    final String module = matcher.group(1);
    String userAgentManifestPath = null;
    final String referrer = req.getHeader("referer");
    if (referrer != null && referrer.contains("gwt.codesvr")) {
      // Serve an empty manifest in development mode. This is not reliable as
      // some browser won't send the referer header when requesting the
      // manifest. In that case we simply return a 404 (the manifest is not
      // needed in dev mode anyway but we try to avoid the error, if possible).
      userAgentManifestPath = "/" + module + "/dev.appcache.manifest";
    }
    else {
      userAgentManifestPath = "/" + module + "/" + getUserAgentManifestName(req, module);
    }

    resp.setHeader("Cache-Control", "no-cache");
    resp.setHeader("Pragma", "no-cache");
    resp.setContentType("text/cache-manifest");
    req.getRequestDispatcher(userAgentManifestPath).forward(req, resp);
  }

  private String getUserAgentManifestName(HttpServletRequest req, String module) {
    final String userAgentHeader = req.getHeader("user-agent").toLowerCase();

    String agentPrefix = "";
    // Do not change the order of these checks. To verify this, compile a
    // nocache.js file in pretty mode and compare the corresponding client-side
    // logic provided by GWT. Tested with GWT 2.5 and 2.6.
    if (userAgentHeader.contains("opera") && manifestExists("opera", module)) {
      agentPrefix = "opera";
    }
    else if (userAgentHeader.contains("webkit") && manifestExists("safari", module)) {
      agentPrefix = "safari";
    }
    else if (userAgentHeader.contains("msie 10") && manifestExists("ie10", module)) {
      agentPrefix = "ie10";
    }
    else if (userAgentHeader.contains("msie 10") && manifestExists("ie9", module)) {
      agentPrefix = "ie9";
    }
    else if (userAgentHeader.contains("msie 9") && manifestExists("ie9", module)) {
      agentPrefix = "ie9";
    }
    else if (userAgentHeader.contains("msie 8") && manifestExists("ie8", module)) {
      agentPrefix = "ie8";
    }
    else if (userAgentHeader.contains("msie") && manifestExists("ie6", module)) {
      agentPrefix = "ie6";
    }
    else if (userAgentHeader.contains("msie") && manifestExists("ie8", module)) {
      agentPrefix = "ie8";
    }
    else if (userAgentHeader.contains("gecko") && manifestExists("gecko1_8", module)) {
      agentPrefix = "gecko1_8";
    }

    return agentPrefix + MANIFEST_FILE_EXTENSION;
  }

  /**
   * Returns the set of user agent names for which a manifest file exists in the
   * provided module.
   * 
   * @param module
   *          the name of the GWT module. Not null.
   * @return the user agent names for which a manifest file exists.
   */
  private Set<String> getManifestsForModule(String module) {
    Assert.notNull(module);

    Set<String> manifests = manifestsPerModule.get(module);
    if (manifests == null) {
      final String path = getServletContext().getRealPath(module);
      final File[] manifestFiles = new File(path).listFiles(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return name.endsWith(MANIFEST_FILE_EXTENSION);
        }
      });

      manifests = new HashSet<String>();
      if (manifestFiles != null) {
        for (final File manifestFile : manifestFiles) {
          final String name = manifestFile.getName();
          manifests.add(name.replace(MANIFEST_FILE_EXTENSION, ""));
        }
      }

      manifestsPerModule.put(module, manifests);
    }

    return manifests;
  }

  /**
   * Checks if a user agent specific manifest file exists for the provided
   * module.
   * 
   * @param userAgent
   *          the user agent
   * @param module
   *          the GWT module name
   * @return true if the manifest exists, otherwise false.
   */
  private boolean manifestExists(String userAgent, String module) {
    return getManifestsForModule(module).contains(userAgent);
  }
}
