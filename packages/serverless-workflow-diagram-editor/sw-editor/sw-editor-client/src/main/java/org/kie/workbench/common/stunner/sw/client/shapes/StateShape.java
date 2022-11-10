/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.client.shapes;

import org.kie.workbench.common.stunner.core.client.shape.HasShapeState;
import org.kie.workbench.common.stunner.core.client.shape.impl.NodeShapeImpl;

public class StateShape extends NodeShapeImpl implements HasShapeState {

    public static final String[] INJECT_STATE_ICON = new String[]{
            "M35.02,28.49s-.03-.03-.05-.04l-10.29-10.29c-1.78-1.78-4.68-1.79-6.46,0-1.79,1.79-1.78,4.68,0,6.46l2.81,2.81H4.57c-2.52,0-4.57,2.04-4.57,4.57s2.05,4.57,4.57,4.57H20.94l-2.75,2.75c-1.78,1.78-1.79,4.68,0,6.46s4.68,1.78,6.46,0l10.09-10.09c.28-.2,.54-.44,.77-.71,.5-.58,.82-1.27,.97-2,.37-1.59-.11-3.33-1.46-4.5Z",
            "M.01,4.23C.19,1.83,2.25,0,4.66,0H59.43c2.53,0,4.57,2.05,4.57,4.57s-2.04,4.57-4.57,4.57H4.57C1.93,9.14-.18,6.91,.01,4.23Z",
            "M46.09,18.29h13.24c2.34,0,4.49,1.91,4.65,4.24,.19,2.68-1.92,4.9-4.56,4.9h-13.43c-2.64,0-4.75-2.22-4.56-4.9,.16-2.34,2.31-4.24,4.65-4.24Z",
            "M59.43,36.57c2.64,0,4.75,2.22,4.56,4.9-.16,2.34-2.31,4.24-4.65,4.24h-13.24c-2.34,0-4.49-1.91-4.65-4.24-.19-2.68,1.92-4.9,4.56-4.9h13.43Z",
            "M0,59.43c0-2.53,2.05-4.57,4.57-4.57H59.34c2.34,0,4.49,1.91,4.65,4.24,.19,2.68-1.92,4.9-4.56,4.9H4.57c-2.52,0-4.57-2.04-4.57-4.57Z"
    };
    public static final String[] SWITCH_STATE_ICON = new String[]{
            "M60.6,46.54h-6.53v-8.71c-.06-1.38-.72-2.69-1.83-3.52l-7.42-5.56h0l-8.41-6.36V4.4C36.4,1.87,34.26-.17,31.69,.01c-2.25,.16-4.09,2.22-4.09,4.48V22.41l-8.41,6.31h0l-7.42,5.55c-1.11,.83-1.76,2.14-1.83,3.52v8.71H3.4c-2.94-.03-4.42,3.53-2.34,5.61l11.01,10.89c1.29,1.29,3.38,1.29,4.68,0l11.01-10.89c2.08-1.96,.61-5.52-2.34-5.47h-6.67v-6.63s8.22-6.08,13.28-9.87c4.96,3.73,13.2,9.92,13.24,9.9v6.63h-6.67c-2.94-.05-4.42,3.51-2.34,5.47l11.01,10.89c1.29,1.29,3.38,1.29,4.68,0l11.01-10.89c2.08-2.08,.6-5.64-2.34-5.61Z"
    };
    public static final String[] OPERATION_STATE_ICON = new String[]{
            "M61.99,20.83c.41,1.07,.06,2.29-.79,3.07l-5.41,4.93c.14,1.04,.21,2.1,.21,3.17s-.08,2.14-.21,3.17l5.41,4.92c.85,.79,1.2,2,.79,3.08-.55,1.49-1.21,2.92-1.96,4.29l-.59,1.01c-.83,1.38-1.75,2.68-2.76,3.91-.75,.89-1.96,1.2-3.06,.85l-6.96-2.22c-1.68,1.29-3.64,2.36-5.5,3.19l-1.56,7.14c-.25,1.12-1.12,1.92-2.28,2.22-1.72,.29-3.5,.44-5.42,.44-1.7,0-3.48-.15-5.2-.44-1.15-.3-2.02-1.1-2.27-2.22l-1.56-7.14c-1.98-.83-3.83-1.9-5.5-3.19l-6.96,2.22c-1.1,.35-2.32,.04-3.06-.85-1.01-1.24-1.94-2.54-2.76-3.91l-.59-1.01c-.76-1.36-1.42-2.8-1.97-4.29-.4-1.08-.06-2.29,.79-3.08l5.41-4.92c-.14-1.04-.21-2.1-.21-3.17s.07-2.14,.21-3.17l-5.41-4.93c-.86-.79-1.2-1.99-.79-3.07,.55-1.49,1.22-2.93,1.97-4.29l.58-1.01c.83-1.38,1.75-2.67,2.77-3.91,.74-.89,1.96-1.2,3.06-.85l6.96,2.22c1.67-1.29,3.52-2.37,5.5-3.18l1.56-7.14c.25-1.13,1.12-2.04,2.27-2.23,1.73-.29,3.5-.44,5.31-.44s3.59,.15,5.31,.44c1.15,.19,2.03,1.09,2.28,2.23l1.56,7.14c1.86,.82,3.82,1.89,5.5,3.18l6.96-2.22c1.1-.35,2.31-.04,3.06,.85,1.01,1.23,1.94,2.53,2.76,3.91l.59,1.01c.75,1.36,1.41,2.8,1.96,4.29h0Zm-29.99,21.17c5.53,0,10-4.47,10-10.11s-4.47-10-10-10-10,4.59-10,10,4.48,10.11,10,10.11Z"
    };
    public static final String[] EVENT_STATE_ICON = new String[]{
            "M24,4v4h16V4c0-2.21,1.79-4,4-4s4,1.79,4,4v4h6c3.31,0,6,2.69,6,6v6H4v-6c0-3.31,2.69-6,6-6h6V4c0-2.21,1.79-4,4-4s4,1.79,4,4ZM4,24H60V58c0,3.31-2.69,6-6,6H10c-3.31,0-6-2.69-6-6V24Zm10,8c-1.1,0-2,.9-2,2v12c0,1.1,.9,2,2,2h12c1.1,0,2-.9,2-2v-12c0-1.1-.9-2-2-2H14Z"
    };
    public static final String[] CALLBACK_STATE_ICON = new String[]{
            "M63.97,36.13c0-2.13-1.64-3.95-3.76-4.07-2.31-.13-4.23,1.7-4.23,3.99,0,6.61-5.37,11.98-11.98,11.98H24.03v-4.99c0-1.18-.7-2.25-1.78-2.74-1.08-.47-2.34-.28-3.23,.52l-9.98,8.98c-.63,.56-.99,1.37-.99,2.22s.36,1.66,.99,2.23l9.98,8.98c.56,.51,1.28,.77,2,.77,.41,0,.83-.09,1.22-.26,1.08-.48,1.78-1.55,1.78-2.74v-4.99h19.97c10.98,0,19.92-8.91,19.97-19.89ZM20,16.08h19.85l.11,4.99c0,1.18,.7,2.25,1.78,2.74,.4,.17,.81,.26,1.11,.26,.73,0,1.44-.27,2.01-.77l9.98-8.98c.74-.57,1.1-1.38,1.1-2.34s-.36-1.66-.99-2.23L44.97,.76c-.88-.79-2.15-.98-3.22-.51-1.08,.6-1.89,1.67-1.89,2.85l.11,4.99H20C8.99,8.09,.03,17.05,.03,28.06c0,2.21,1.79,3.99,3.99,3.99s3.99-1.79,3.99-3.99c0-6.6,5.38-11.98,11.98-11.98Z"
    };
    public static final String[] FOR_EACH_STATE_ICON = new String[]{
            "M64,32.22v27.24c0,2.34-1.91,4.49-4.24,4.65-2.68,.19-4.9-1.92-4.9-4.56v-27.43c0-2.64,2.22-4.75,4.9-4.56,2.34,.16,4.24,2.31,4.24,4.65Z",

            "M45.71,59.56c0,2.64-2.22,4.75-4.9,4.56-2.34-.16-4.24-2.31-4.24-4.65v-27.24c0-2.34,1.91-4.49,4.24-4.65,2.68-.19,4.9,1.92,4.9,4.56v27.43Z",
            "M27.43,4.79v27.24c0,2.34-1.91,4.49-4.24,4.65-2.68,.19-4.9-1.92-4.9-4.56V4.7C18.29,2.07,20.51-.04,23.18,.14c2.34,.16,4.24,2.31,4.24,4.65Z",
            "M9.14,32.13c0,2.64-2.22,4.75-4.9,4.56C1.91,36.53,0,34.38,0,32.04V4.79C0,2.45,1.91,.3,4.24,.14c2.68-.19,4.9,1.92,4.9,4.56v27.43Z"
    };

    public static final String[] PARALLEL_STATE_ICON = new String[]{
            "M50.54,.01c2.34,.16,4.24,2.31,4.24,4.65V59.43c0,2.64-2.23,4.75-4.91,4.56-2.33-.17-4.23-2.31-4.23-4.65V4.57c0-2.63,2.22-4.75,4.9-4.56Z",
            "M13.46,63.99c-2.34-.16-4.24-2.31-4.24-4.65V4.57c0-2.52,2.04-4.57,4.57-4.57s4.57,2.05,4.57,4.57V59.43c0,2.64-2.22,4.75-4.9,4.56Z"
    };
    public static final String[] SLEEP_STATE_ICON = new String[]{
            "M54.24,53.11c3.71-4.77,5.92-10.77,5.92-17.28,0-15.56-12.61-28.17-28.17-28.17S3.83,20.27,3.83,35.83c0,6.49,2.19,12.46,5.87,17.22l-2.53,2.53c-1.63,1.63-1.8,4.46-.28,6.2,1.74,2,4.76,2.07,6.59,.24l2.8-2.8c4.49,3.02,9.9,4.79,15.71,4.79s11.17-1.75,15.65-4.74l2.75,2.75c1.63,1.63,4.46,1.8,6.2,.28,2-1.74,2.07-4.76,.24-6.59l-2.59-2.59Zm-13.09-8.11l-10.59-7.06c-.74-.4-1.18-1.22-1.18-2.11l-.1-15c0-1.47,1.28-2.65,2.65-2.65,1.57,0,2.75,1.18,2.75,2.65v13.58l9.41,6.26c1.21,.82,1.54,2.46,.64,3.67-.72,1.21-2.36,1.54-3.57,.64Z",
            "M23.86,3.69C19.89-.5,13.34-1.27,8.47,2.14c-4.87,3.41-6.39,9.82-3.81,14.99L23.86,3.69Z",
            "M40.14,3.69c3.97-4.19,10.52-4.95,15.39-1.54,4.87,3.41,6.39,9.82,3.81,14.99L40.14,3.69Z"
    };

    private StateShapeView shapeView;

    private StateShape(String name) {
        super(new StateShapeView(name).asAbstractShape());

        shapeView = (StateShapeView) getShape().getShapeView();
    }

    public static StateShape create(String name) {
        return new StateShape(name);
    }

    public StateShape setType(String type) {
        setIcon(type);

        return this;
    }

    public void setIcon(String type) {
        String[] iconElements = new String[0];

        switch (type) {
            case "inject":
                iconElements = INJECT_STATE_ICON;
                shapeView.setIconBackground("#8BC1F7");
                break;
            case "switch":
                iconElements = SWITCH_STATE_ICON;
                shapeView.setIconBackground("#009596");
                break;
            case "operation":
                iconElements = OPERATION_STATE_ICON;
                shapeView.setIconBackground("#0066CC");
                break;
            case "event":
                iconElements = EVENT_STATE_ICON;
                shapeView.setIconBackground("#F4C145");
                break;
            case "callback":
                iconElements = CALLBACK_STATE_ICON;
                shapeView.setIconBackground("#EC7A08");
                break;
            case "foreach":
                iconElements = FOR_EACH_STATE_ICON;
                shapeView.setIconBackground("#8F4700");
                break;
            case "parallel":
                iconElements = PARALLEL_STATE_ICON;
                shapeView.setIconBackground("#4CB140");
                break;
            case "sleep":
                iconElements = SLEEP_STATE_ICON;
                shapeView.setIconBackground("#5752D1");
                break;
        }

        for (String path : iconElements) {
            shapeView.addIconElement(path);
        }
    }
}