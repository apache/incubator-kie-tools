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

public final class ComboLogging implements ILogging
{
    private static ComboLogging INSTANCE;

    private final  Logging      m_logging;

    private final  Console      m_console;

    public static final ComboLogging get()
    {
        if (null == INSTANCE)
        {
            INSTANCE = new ComboLogging();
        }
        return INSTANCE;
    }

    private ComboLogging()
    {
        m_logging = Logging.get();

        m_console = Console.get();
    }

    @Override
    public final void info(final String message)
    {
        m_logging.info(message);

        m_console.info(message);
    }

    @Override
    public final void severe(final String message)
    {
        m_logging.severe(message);

        m_console.severe(message);
    }

    @Override
    public final void error(final String message)
    {
        m_logging.error(message);

        m_console.error(message);
    }

    @Override
    public final void error(final String message, final Throwable e)
    {
        m_logging.error(message, e);

        m_console.error(message, e);
    }

    @Override
    public final void fine(final String message)
    {
        m_logging.fine(message);

        m_console.fine(message);
    }

    @Override
    public final void warn(final String message)
    {
        m_logging.warn(message);

        m_console.warn(message);
    }

    @Override
    public final void severe(final String message, final Throwable e)
    {
        m_logging.severe(message, e);

        m_console.severe(message, e);
    }
}
