package org.kie.lienzo.client.util;

import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;

import static elemental2.dom.DomGlobal.document;

public class Console
{
    private String              lastLog;
    private int                 lastLogCount;
    private HTMLDivElement      lastElement;
    private elemental2.dom.Text lastText;

    public Console()
    {
    }

    public void log(String log)
    {
        HTMLDivElement e1;
        elemental2.dom.Text e1Text;
        Element links = document.getElementById("console");

        if (lastLog != null && lastLog.equals(log)) {
            e1 = lastElement;
            lastLogCount++;
            lastText.remove();

            e1Text = document.createTextNode(log+ " (" + lastLogCount + ")");
            lastElement.appendChild(e1Text);
        }
        else
        {
            e1 = (HTMLDivElement) document.createElement("div");
            e1Text = document.createTextNode(log);
            e1.appendChild(e1Text);


            links.appendChild(e1);

            lastLogCount = 1;
            lastLog = log;
            lastText = e1Text;
        }

        lastText = e1Text;
        lastElement = e1;

        links.scrollTop = links.scrollHeight;
    }
}
