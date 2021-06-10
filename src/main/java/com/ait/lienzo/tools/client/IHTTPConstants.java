/*
   Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.tools.client;

public interface IHTTPConstants
{
    String CHARSET_UTF_8                    = "UTF-8";

    String CONTENT_TEXT_PLAIN               = "text/plain";

    String CONTENT_TYPE_HTML                = "text/html";

    String CONTENT_TYPE_TEXT_XML            = "text/xml";

    String CONTENT_TYPE_APPLICATION_XML     = "application/xml";

    String CONTENT_TYPE_APPLICATION_JSON    = "application/json";

    String ACCEPT_HEADER                    = "Accept";

    String CONTENT_TYPE_HEADER              = "Content-Type";

    String USER_AGENT_HEADER                = "User-Agent";

    String CACHE_CONTROL_HEADER             = "Cache-Control";

    String CONTENT_LENGTH_HEADER            = "Content-Length";

    String DATE_HEADER                      = "Date";

    String PRAGMA_HEADER                    = "Pragma";

    String ACCEPT_CHARSET_HEADER            = "Accept-Charset";

    String ACCEPT_ENCODING_HEADER           = "Accept-Encoding";

    String ACCEPT_LANGUAGE_HEADER           = "Accept-Language";

    String COOKIE_HEADER                    = "Cookie";

    String IF_UNMODIFIED_SINCE_HEADER       = "If-Unmodified-Since";

    String REFERER_HEADER                   = "Referer";

    String UPGRADE_HEADER                   = "Upgrade";

    String CONTENT_DISPOSITION_HEADER       = "Content-Disposition";

    String EXPIRES_HEADER                   = "Expires";

    String SET_COOKIE_HEADER                = "Set-Cookie";

    String STRICT_TRANSPORT_SECURITY_HEADER = "Strict-Transport-Security";
    
    String WWW_AUTHENTICATE                 = "WWW-Authenticate";

    String X_FRAME_OPTIONS_HEADER           = "X-Frame-Options";

    String X_POWERED_BY_HEADER              = "X-Powered-By";

    String X_XSS_PROTECTION_HEADER          = "X-XSS-Protection";

    String X_FORWARDED_FOR_HEADER           = "X-Forwarded-For";

    String X_USER_ID_HEADER                 = "X-User-ID";

    String X_USER_NAME_HEADER               = "X-User-Name";

    String X_CLIENT_VERSION_HEADER          = "X-Client-Version";

    String X_SESSION_ID_HEADER              = "X-Session-ID";

    String X_CLIENT_UUID_HEADER             = "X-Client-UUID";

    String X_SESSION_UUID_HEADER            = "X-Session-UUID";

    String X_CLIENT_NAME_HEADER             = "X-Client-Name";

    String X_SCHEMA_VERSION_HEADER          = "X-Schema-Version";

    String X_XSRF_TOKEN_HEADER              = "X-Request-XSRFToken";

    String X_CLIENT_API_TOKEN_HEADER        = "X-Client-API-Token";

    String X_STRICT_JSON_FORMAT_HEADER      = "X-Strict-JSON-Format";

    String X_CONTENT_TYPE_OPTIONS_HEADER    = "X-Content-Type-Options";

    String CACHE_CONTROL_MAX_AGE_PREFIX     = "max-age=";

    String NO_CACHE_PRAGMA_HEADER_VALUE     = "no-cache";

    String NO_CACHE_CONTROL_HEADER_VALUE    = "no-cache, no-store, must-revalidate";

    long   DAY_IN_SECONDS                   = 86400L;

    long   DAY_IN_MILLISECONDS              = 86400000L;

    long   WEEK_IN_SECONDS                  = 604800L;

    long   WEEK_IN_MILLISECONDS             = 604800000L;

    long   YEAR_IN_SECONDS                  = 31536000L;

    long   YEAR_IN_MILLISECONDS             = 31536000000L;
}
