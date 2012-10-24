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

package org.uberfire.backend.server.impl;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GoogleGadgetServlet extends HttpServlet {

    private static String PAGE_HEADER = "<!DOCTYPE html>\n" +
            "<html lang=\"en\" dir=\"ltr\">\n" +
            "  <head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title></title>\n" +
            "\t<script src=\"";

    private static String PAGE_FOOTER = "\"></script>\n" +
            "  </head>\n" +
            "  <body>\n" +
            "  </body>\n" +
            "</html>";

    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        final String queryString = req.getQueryString();

        final PrintWriter writer = resp.getWriter();

        writer.print(PAGE_HEADER);
        writer.print(queryString.substring(queryString.indexOf("src=") + 4));
        writer.print(PAGE_FOOTER);
        writer.close();
    }

}
