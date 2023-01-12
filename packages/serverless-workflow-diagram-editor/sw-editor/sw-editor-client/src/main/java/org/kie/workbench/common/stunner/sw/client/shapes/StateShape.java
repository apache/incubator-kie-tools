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

import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.client.core.types.Transform;
import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.appformer.kogito.bridge.client.resource.interop.ResourceContentOptions;
import org.kie.workbench.common.stunner.core.client.shape.HasShapeState;
import org.kie.workbench.common.stunner.core.client.shape.impl.NodeShapeImpl;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.sw.definition.State;

public class StateShape extends NodeShapeImpl implements HasShapeState {

    public static final String INJECT_STATE_ICON = "M35.02,28.49s-.03-.03-.05-.04l-10.29-10.29c-1.78-1.78-4.68-1.79-6.46,0-1.79,1.79-1.78,4.68,0,6.46l2.81,2.81H4.57c-2.52,0-4.57,2.04-4.57,4.57s2.05,4.57,4.57,4.57H20.94l-2.75,2.75c-1.78,1.78-1.79,4.68,0,6.46s4.68,1.78,6.46,0l10.09-10.09c.28-.2,.54-.44,.77-.71,.5-.58,.82-1.27,.97-2,.37-1.59-.11-3.33-1.46-4.5M.01,4.23C.19,1.83,2.25,0,4.66,0H59.43c2.53,0,4.57,2.05,4.57,4.57s-2.04,4.57-4.57,4.57H4.57C1.93,9.14-.18,6.91,.01,4.23M46.09,18.29h13.24c2.34,0,4.49,1.91,4.65,4.24,.19,2.68-1.92,4.9-4.56,4.9h-13.43c-2.64,0-4.75-2.22-4.56-4.9,.16-2.34,2.31-4.24,4.65-4.24M59.43,36.57c2.64,0,4.75,2.22,4.56,4.9-.16,2.34-2.31,4.24-4.65,4.24h-13.24c-2.34,0-4.49-1.91-4.65-4.24-.19-2.68,1.92-4.9,4.56-4.9h13.43M0,59.43c0-2.53,2.05-4.57,4.57-4.57H59.34c2.34,0,4.49,1.91,4.65,4.24,.19,2.68-1.92,4.9-4.56,4.9H4.57c-2.52,0-4.57-2.04-4.57-4.57Z";
    public static final String SWITCH_STATE_ICON = "M60.6,46.54h-6.53v-8.71c-.06-1.38-.72-2.69-1.83-3.52l-7.42-5.56h0l-8.41-6.36V4.4C36.4,1.87,34.26-.17,31.69,.01c-2.25,.16-4.09,2.22-4.09,4.48V22.41l-8.41,6.31h0l-7.42,5.55c-1.11,.83-1.76,2.14-1.83,3.52v8.71H3.4c-2.94-.03-4.42,3.53-2.34,5.61l11.01,10.89c1.29,1.29,3.38,1.29,4.68,0l11.01-10.89c2.08-1.96,.61-5.52-2.34-5.47h-6.67v-6.63s8.22-6.08,13.28-9.87c4.96,3.73,13.2,9.92,13.24,9.9v6.63h-6.67c-2.94-.05-4.42,3.51-2.34,5.47l11.01,10.89c1.29,1.29,3.38,1.29,4.68,0l11.01-10.89c2.08-2.08,.6-5.64-2.34-5.61Z";
    public static final String OPERATION_STATE_ICON = "M61.99,20.83c.41,1.07,.06,2.29-.79,3.07l-5.41,4.93c.14,1.04,.21,2.1,.21,3.17s-.08,2.14-.21,3.17l5.41,4.92c.85,.79,1.2,2,.79,3.08-.55,1.49-1.21,2.92-1.96,4.29l-.59,1.01c-.83,1.38-1.75,2.68-2.76,3.91-.75,.89-1.96,1.2-3.06,.85l-6.96-2.22c-1.68,1.29-3.64,2.36-5.5,3.19l-1.56,7.14c-.25,1.12-1.12,1.92-2.28,2.22-1.72,.29-3.5,.44-5.42,.44-1.7,0-3.48-.15-5.2-.44-1.15-.3-2.02-1.1-2.27-2.22l-1.56-7.14c-1.98-.83-3.83-1.9-5.5-3.19l-6.96,2.22c-1.1,.35-2.32,.04-3.06-.85-1.01-1.24-1.94-2.54-2.76-3.91l-.59-1.01c-.76-1.36-1.42-2.8-1.97-4.29-.4-1.08-.06-2.29,.79-3.08l5.41-4.92c-.14-1.04-.21-2.1-.21-3.17s.07-2.14,.21-3.17l-5.41-4.93c-.86-.79-1.2-1.99-.79-3.07,.55-1.49,1.22-2.93,1.97-4.29l.58-1.01c.83-1.38,1.75-2.67,2.77-3.91,.74-.89,1.96-1.2,3.06-.85l6.96,2.22c1.67-1.29,3.52-2.37,5.5-3.18l1.56-7.14c.25-1.13,1.12-2.04,2.27-2.23,1.73-.29,3.5-.44,5.31-.44s3.59,.15,5.31,.44c1.15,.19,2.03,1.09,2.28,2.23l1.56,7.14c1.86,.82,3.82,1.89,5.5,3.18l6.96-2.22c1.1-.35,2.31-.04,3.06,.85,1.01,1.23,1.94,2.53,2.76,3.91l.59,1.01c.75,1.36,1.41,2.8,1.96,4.29h0Zm-29.99,21.17c5.53,0,10-4.47,10-10.11s-4.47-10-10-10-10,4.59-10,10,4.48,10.11,10,10.11Z";
    public static final String EVENT_STATE_ICON = "M 45.254 45.197 L 36.216 45.197 L 42.206 31.792 C 42.613 30.98 42.307 29.965 41.597 29.355 C 40.887 28.746 39.768 28.849 39.06 29.457 L 19.561 45.705 C 18.953 46.215 18.646 47.128 18.953 47.939 C 19.258 48.754 19.968 49.262 20.883 49.262 L 29.921 49.262 L 23.928 62.667 C 23.521 63.478 23.825 64.493 24.538 65.103 C 24.943 65.51 25.247 65.609 25.756 65.609 C 26.263 65.609 26.67 65.406 27.076 65.103 L 46.573 48.854 C 47.183 48.347 47.488 47.431 47.183 46.62 C 46.879 45.808 46.066 45.197 45.254 45.197 Z M 53.378 16.865 C 53.275 16.865 53.275 16.865 53.175 16.865 C 53.275 16.153 53.378 15.543 53.378 14.834 C 53.378 9.247 48.808 4.679 43.222 4.679 C 40.073 4.679 37.333 6.101 35.505 8.334 C 33.068 3.765 28.394 0.615 22.911 0.615 C 15.094 0.615 8.694 7.014 8.694 14.834 C 8.694 15.747 8.794 16.661 8.997 17.575 C 4.023 19.2 0.469 23.872 0.571 29.355 C 0.773 36.057 6.258 41.237 12.858 41.237 L 18.646 41.237 L 36.419 26.41 C 37.535 25.496 38.856 24.989 40.276 24.989 C 41.597 24.989 42.919 25.396 43.932 26.207 C 46.169 27.933 46.981 30.98 45.761 33.521 L 42.511 41.237 L 53.275 41.237 C 59.878 41.237 65.36 36.057 65.563 29.457 C 65.766 22.552 60.285 16.865 53.378 16.865 Z";
    public static final String CALLBACK_STATE_ICON = "M63.97,36.13c0-2.13-1.64-3.95-3.76-4.07-2.31-.13-4.23,1.7-4.23,3.99,0,6.61-5.37,11.98-11.98,11.98H24.03v-4.99c0-1.18-.7-2.25-1.78-2.74-1.08-.47-2.34-.28-3.23,.52l-9.98,8.98c-.63,.56-.99,1.37-.99,2.22s.36,1.66,.99,2.23l9.98,8.98c.56,.51,1.28,.77,2,.77,.41,0,.83-.09,1.22-.26,1.08-.48,1.78-1.55,1.78-2.74v-4.99h19.97c10.98,0,19.92-8.91,19.97-19.89ZM20,16.08h19.85l.11,4.99c0,1.18,.7,2.25,1.78,2.74,.4,.17,.81,.26,1.11,.26,.73,0,1.44-.27,2.01-.77l9.98-8.98c.74-.57,1.1-1.38,1.1-2.34s-.36-1.66-.99-2.23L44.97,.76c-.88-.79-2.15-.98-3.22-.51-1.08,.6-1.89,1.67-1.89,2.85l.11,4.99H20C8.99,8.09,.03,17.05,.03,28.06c0,2.21,1.79,3.99,3.99,3.99s3.99-1.79,3.99-3.99c0-6.6,5.38-11.98,11.98-11.98Z";
    public static final String FOR_EACH_STATE_ICON = "M64,32.22v27.24c0,2.34-1.91,4.49-4.24,4.65-2.68,.19-4.9-1.92-4.9-4.56v-27.43c0-2.64,2.22-4.75,4.9-4.56,2.34,.16,4.24,2.31,4.24,4.65M45.71,59.56c0,2.64-2.22,4.75-4.9,4.56-2.34-.16-4.24-2.31-4.24-4.65v-27.24c0-2.34,1.91-4.49,4.24-4.65,2.68-.19,4.9,1.92,4.9,4.56v27.43M27.43,4.79v27.24c0,2.34-1.91,4.49-4.24,4.65-2.68,.19-4.9-1.92-4.9-4.56V4.7C18.29,2.07,20.51-.04,23.18,.14c2.34,.16,4.24,2.31,4.24,4.65M9.14,32.13c0,2.64-2.22,4.75-4.9,4.56C1.91,36.53,0,34.38,0,32.04V4.79C0,2.45,1.91,.3,4.24,.14c2.68-.19,4.9,1.92,4.9,4.56v27.43Z";
    public static final String PARALLEL_STATE_ICON = "M50.54,.01c2.34,.16,4.24,2.31,4.24,4.65V59.43c0,2.64-2.23,4.75-4.91,4.56-2.33-.17-4.23-2.31-4.23-4.65V4.57c0-2.63,2.22-4.75,4.9-4.56M13.46,63.99c-2.34-.16-4.24-2.31-4.24-4.65V4.57c0-2.52,2.04-4.57,4.57-4.57s4.57,2.05,4.57,4.57V59.43c0,2.64-2.22,4.75-4.9,4.56Z";
    public static final String SLEEP_STATE_ICON = "M54.24,53.11c3.71-4.77,5.92-10.77,5.92-17.28,0-15.56-12.61-28.17-28.17-28.17S3.83,20.27,3.83,35.83c0,6.49,2.19,12.46,5.87,17.22l-2.53,2.53c-1.63,1.63-1.8,4.46-.28,6.2,1.74,2,4.76,2.07,6.59,.24l2.8-2.8c4.49,3.02,9.9,4.79,15.71,4.79s11.17-1.75,15.65-4.74l2.75,2.75c1.63,1.63,4.46,1.8,6.2,.28,2-1.74,2.07-4.76,.24-6.59l-2.59-2.59Zm-13.09-8.11l-10.59-7.06c-.74-.4-1.18-1.22-1.18-2.11l-.1-15c0-1.47,1.28-2.65,2.65-2.65,1.57,0,2.75,1.18,2.75,2.65v13.58l9.41,6.26c1.21,.82,1.54,2.46,.64,3.67-.72,1.21-2.36,1.54-3.57,.64M23.86,3.69C19.89-.5,13.34-1.27,8.47,2.14c-4.87,3.41-6.39,9.82-3.81,14.99L23.86,3.69M40.14,3.69c3.97-4.19,10.52-4.95,15.39-1.54,4.87,3.41,6.39,9.82,3.81,14.99L40.14,3.69Z";
    public static final String ANSIBLE_STATE_ICON = "M 31.047 11.68 L 47.108 46.485 L 22.848 29.706 L 31.047 11.68 Z M 59.577 54.501 L 34.874 2.301 C 34.167 0.796 32.757 0 31.047 0 C 29.331 0 27.819 0.796 27.114 2.301 L 0 59.559 L 9.274 59.559 L 20.008 35.95 L 52.04 58.671 C 53.326 59.586 54.256 60 55.466 60 C 57.885 60 60 58.407 60 56.109 C 60 55.734 59.85 55.14 59.577 54.501 Z";
    public static final String KAOTO_STATE_ICON = "M 13.524 8.426 L 4.212 16.823 L 21.506 16.823 C 35.471 16.823 38.797 16.463 38.797 15.142 C 38.797 13.823 24.387 -0.091 23.056 0.03 C 22.833 0.03 18.512 3.868 13.524 8.426M 44.007 0.87 C 43.564 1.23 43.23 5.067 43.23 9.264 C 43.23 15.743 43.564 16.823 45.227 16.823 C 46.336 16.823 47.776 17.421 48.552 18.262 C 49.661 19.461 51.654 17.901 57.751 11.185 C 62.297 6.387 65.401 2.069 64.958 1.349 C 64.071 -0.091 45.227 -0.569 44.007 0.87M 54.096 22.459 C 51.323 25.578 50.99 26.777 50.99 34.814 L 50.99 43.809 L 54.317 40.091 C 57.642 36.613 57.751 36.013 57.42 27.737 L 57.087 19.101 L 54.096 22.459M 0.775 22.459 C 0.332 22.82 0 31.574 0 41.651 C 0 57.843 0.222 60.122 1.774 59.76 C 4.434 59.163 46.557 23.659 45.892 22.579 C 45.227 21.38 1.774 21.261 0.775 22.459M 36.802 38.052 C 31.702 42.371 27.934 46.327 28.488 46.806 C 32.258 50.884 43.785 60.001 45.005 60.001 C 46.225 60.001 46.557 56.884 46.557 45.01 C 46.557 36.733 46.446 30.016 46.336 30.016 C 46.113 30.136 41.901 33.614 36.802 38.052 Z";

    private final StateShapeView shapeView;

    public StateShape(State state, ResourceContentService resourceContentService) {
        this(state.getName());

        if (state.metadata == null) {
            setType(state.getType());
            return;
        }

        if (StringUtils.nonEmpty(state.metadata.icon)) {
            if (state.metadata.icon.startsWith("data:")) {
                Picture picture = new Picture(state.metadata.icon);
                setIconPicture(picture);
            } else {
                loadIconFromFile(state, resourceContentService);
            }
        }

        if (!isIconEmpty()) {
            return;
        }

        if (StringUtils.nonEmpty(state.metadata.type)) {
            setType(state.metadata.type);
        }

        if (isIconEmpty()) {
            setType(state.getType());
        }
    }

    public StateShape(String name) {
        super(new StateShapeView(name).asAbstractShape());

        shapeView = (StateShapeView) getShape().getShapeView();
    }

    private void loadIconFromFile(State state, ResourceContentService resourceContentService) {
        resourceContentService
                .get(state.metadata.icon, ResourceContentOptions.binary())
                .then(image -> {
                    setIconPicture(image, state.metadata.icon);
                    return null;
                });
    }

    protected void setIconPicture(String base64Data, String iconPath) {
        if (StringUtils.isEmpty(base64Data)) {
            return;
        }

        String base64image = iconDataUri(iconPath, base64Data);
        Picture picture = new Picture(base64image);
        setIconPicture(picture);
    }

    protected static String iconDataUri(String iconUri, String iconData) {
        String[] iconUriParts = iconUri.split("\\.");
        if (iconUriParts.length > 1) {
            int fileTypeIndex = iconUriParts.length - 1;
            String fileType = iconUriParts[fileTypeIndex];
            return "data:image/" + fileType + ";base64, " + iconData;
        }
        return iconData;
    }

    public StateShape setType(String type) {
        switch (type) {
            case "inject":
                shapeView.setSvgIcon("#8BC1F7", INJECT_STATE_ICON);
                break;
            case "switch":
                shapeView.setSvgIcon("#009596", SWITCH_STATE_ICON);
                break;
            case "operation":
                shapeView.setSvgIcon("#0066CC", OPERATION_STATE_ICON);
                break;
            case "event":
                shapeView.setSvgIcon("#F4C145", EVENT_STATE_ICON);
                break;
            case "callback":
                shapeView.setSvgIcon("#EC7A08", CALLBACK_STATE_ICON);
                break;
            case "foreach":
                shapeView.setSvgIcon("#8F4700", FOR_EACH_STATE_ICON);
                break;
            case "parallel":
                shapeView.setSvgIcon("#4CB140", PARALLEL_STATE_ICON);
                break;
            case "sleep":
                shapeView.setSvgIcon("#5752D1", SLEEP_STATE_ICON);
                break;
            case "ansible":
                shapeView.setSvgIcon("#BB271A", ANSIBLE_STATE_ICON);
                break;
            case "kaoto":
                shapeView.setSvgIcon("#332174", KAOTO_STATE_ICON);
                break;
        }

        return this;
    }

    public boolean isIconEmpty() {
        return shapeView.isIconEmpty();
    }

    public StateShapeView getView() {
        return shapeView;
    }

    public StateShape setIconPicture(Picture picture) {
        picture.setImageShapeLoadedHandler(p -> {
            double scale = calculateIconScale(p.getImageData().width, p.getImageData().height);
            p.setTransform(new Transform().scale(scale));
            p.getLayer().batch();
        });
        shapeView.setIconPicture(picture);

        return this;
    }

    /**
     * The method calculates the scale for image transformation by using the smaller side
     * of the not-square image as a reference. The longer side of the image is cut to fill
     * the entire icon circle with the source image. If the source image is smaller than
     * the icon circle, it is scaled to full size.
     * @param width of the source image
     * @param height of the source image
     * @return scale rate to fit the icon in the icon circle
     */
    public static double calculateIconScale(int width, int height) {
        double size = StateShapeView.STATE_SHAPE_ICON_RADIUS * 2;
        int min = Math.min(width, height);
        return size / min;
    }
}