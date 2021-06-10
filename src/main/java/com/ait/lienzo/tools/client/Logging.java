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

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Logging implements ILogging
{
    private static Logging INSTANCE;

    private final  Logger  m_logger;

    public static final Logging get()
    {
        if (null == INSTANCE)
        {
            INSTANCE = new Logging();
        }
        return INSTANCE;
    }

    private Logging()
    {
        // @FIXME getModuleName() does not exist in gwt3, yet (mdp);
        m_logger = Logger.getLogger( "lienzo-core_logger");
        //m_logger = Logger.getLogger(GWT.getModuleName() + "_logger");
    }

    @Override
    public final void info(final String message)
    {
        m_logger.log(Level.INFO, message);
    }

    @Override
    public final void severe(final String message)
    {
        m_logger.log(Level.SEVERE, "SEVERE: " + message);
    }

    @Override
    public final void error(final String message)
    {
        m_logger.log(Level.SEVERE, "ERROR: " + message);
    }

    @Override
    public final void error(final String message, final Throwable e)
    {
        m_logger.log(Level.SEVERE, "ERROR: " + message + " " + e.getMessage());
    }

    @Override
    public final void fine(final String message)
    {
        m_logger.log(Level.FINE, message);
    }

    @Override
    public final void warn(final String message)
    {
        m_logger.log(Level.WARNING, message);
    }

    @Override
    public final void severe(final String message, final Throwable e)
    {
        m_logger.log(Level.SEVERE, "SEVERE: " + message + " " + e.getMessage());
    }
}
