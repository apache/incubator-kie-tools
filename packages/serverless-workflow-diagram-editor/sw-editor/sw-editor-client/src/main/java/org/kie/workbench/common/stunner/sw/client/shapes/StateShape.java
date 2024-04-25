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


package org.kie.workbench.common.stunner.sw.client.shapes;

import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.client.core.types.Transform;
import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.appformer.kogito.bridge.client.resource.interop.ResourceContentOptions;
import org.kie.j2cl.tools.di.ui.translation.client.TranslationService;
import org.kie.workbench.common.stunner.core.client.shape.HasShapeState;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.sw.definition.HasMetadata;
import org.kie.workbench.common.stunner.sw.definition.Metadata;
import org.kie.workbench.common.stunner.sw.definition.State;

import static java.lang.Math.floor;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static org.kie.workbench.common.stunner.core.client.shape.ShapeState.NONE;
import static org.kie.workbench.common.stunner.core.client.shape.ShapeState.SELECTED;
import static org.kie.workbench.common.stunner.sw.client.shapes.StateShapeView.STATE_CORNER_RADIUS;
import static org.kie.workbench.common.stunner.sw.client.shapes.StateShapeView.STATE_SHAPE_ICON_RADIUS;

public class StateShape extends ServerlessWorkflowShape<StateShapeView> implements HasShapeState {

    public static final String ANSIBLE_STATE_ICON = "M 31.047 11.68 L 47.108 46.485 L 22.848 29.706 L 31.047 11.68 Z M 59.577 54.501 L 34.874 2.301 C 34.167 0.796 32.757 0 31.047 0 C 29.331 0 27.819 0.796 27.114 2.301 L 0 59.559 L 9.274 59.559 L 20.008 35.95 L 52.04 58.671 C 53.326 59.586 54.256 60 55.466 60 C 57.885 60 60 58.407 60 56.109 C 60 55.734 59.85 55.14 59.577 54.501 Z";
    public static final String KAOTO_STATE_ICON = "M 13.524 8.426 L 4.212 16.823 L 21.506 16.823 C 35.471 16.823 38.797 16.463 38.797 15.142 C 38.797 13.823 24.387 -0.091 23.056 0.03 C 22.833 0.03 18.512 3.868 13.524 8.426M 44.007 0.87 C 43.564 1.23 43.23 5.067 43.23 9.264 C 43.23 15.743 43.564 16.823 45.227 16.823 C 46.336 16.823 47.776 17.421 48.552 18.262 C 49.661 19.461 51.654 17.901 57.751 11.185 C 62.297 6.387 65.401 2.069 64.958 1.349 C 64.071 -0.091 45.227 -0.569 44.007 0.87M 54.096 22.459 C 51.323 25.578 50.99 26.777 50.99 34.814 L 50.99 43.809 L 54.317 40.091 C 57.642 36.613 57.751 36.013 57.42 27.737 L 57.087 19.101 L 54.096 22.459M 0.775 22.459 C 0.332 22.82 0 31.574 0 41.651 C 0 57.843 0.222 60.122 1.774 59.76 C 4.434 59.163 46.557 23.659 45.892 22.579 C 45.227 21.38 1.774 21.261 0.775 22.459M 36.802 38.052 C 31.702 42.371 27.934 46.327 28.488 46.806 C 32.258 50.884 43.785 60.001 45.005 60.001 C 46.225 60.001 46.557 56.884 46.557 45.01 C 46.557 36.733 46.446 30.016 46.336 30.016 C 46.113 30.136 41.901 33.614 36.802 38.052 Z";
    private final StateShapeView shapeView;
    private final ResourceContentService resourceContentService;

    protected StateShape(State state, ResourceContentService resourceContentService, TranslationService translationService) {
        this(state.getName(), resourceContentService, translationService);
    }

    protected StateShape(String name, ResourceContentService resourceContentService, TranslationService translationService) {
        super(new StateShapeView(name).asAbstractShape(), translationService);

        this.resourceContentService = resourceContentService;
        this.shapeView = getShape().getShapeView();
        this.shapeView.setController(this);
    }

    public static StateShape create(State state, ResourceContentService resourceContentService, TranslationService translationService) {
        switch (state.getType()) {
            case "inject":
                return new InjectStateShape(state, resourceContentService, translationService);
            case "switch":
                return new SwitchStateShape(state, resourceContentService, translationService);
            case "operation":
                return new OperationStateShape(state, resourceContentService, translationService);
            case "event":
                return new EventStateShape(state, resourceContentService, translationService);
            case "callback":
                return new CallbackStateShape(state, resourceContentService, translationService);
            case "foreach":
                return new ForEachStateShape(state, resourceContentService, translationService);
            case "parallel":
                return new ParallelStateShape(state, resourceContentService, translationService);
            case "sleep":
                return new SleepStateShape(state, resourceContentService, translationService);
            default:
                return new StateShape(state, resourceContentService, translationService);
        }
    }

    @Override
    public void applyProperties(Node<View<State>, Edge> element, MutationContext mutationContext) {
        super.applyProperties(element, mutationContext);

        State state = element.getContent().getDefinition();
        getView().setTitle(state.getName());

        if (!(state instanceof HasMetadata)) {
            shapeView.setSvgIcon(getIconColor(), getIconSvg());
            return;
        }

        Metadata metadata = ((HasMetadata<?>) state).getMetadata();

        if (metadata == null) {
            shapeView.setSvgIcon(getIconColor(), getIconSvg());
            return;
        }

        if (StringUtils.nonEmpty(metadata.icon)) {
            if (metadata.icon.startsWith("data:")) {
                Picture picture = new Picture(metadata.icon);
                setIconPicture(picture);
            } else {
                loadIconFromFile(metadata, resourceContentService);
            }
        }

        if (!isIconEmpty()) {
            return;
        }

        if (StringUtils.nonEmpty(metadata.type)) {
            setPredefinedIcon(metadata.type);
        }

        if (isIconEmpty()) {
            shapeView.setSvgIcon(getIconColor(), getIconSvg());
        }
    }

    private void loadIconFromFile(Metadata metadata, ResourceContentService resourceContentService) {
        resourceContentService
                .get(metadata.icon, ResourceContentOptions.binary())
                .then(image -> {
                    setIconPicture(image, metadata.icon);
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

    public StateShape setPredefinedIcon(String type) {
        switch (type) {
            case "ansible":
                shapeView.setSvgIcon("#BB271A", ANSIBLE_STATE_ICON);
                break;
            case "kaoto":
                shapeView.setSvgIcon("#332174", KAOTO_STATE_ICON);
                break;
        }

        return this;
    }

    public String getIconColor() {
        return "";
    }

    public String getIconSvg() {
        return "";
    }

    public NodeMouseExitHandler getExitHandler() {
        return event -> {
            if (getShapeView().getShapeState() == SELECTED) {
                return;
            }

            if (isInsideOfTheState(floor(event.getX() - event.getSource().getAbsoluteLocation().getX()),
                                   floor(event.getY() - event.getSource().getAbsoluteLocation().getY()))) {
                return;
            }

            getShapeView().applyState(NONE);
            shapeView.batch();
        };
    }

    protected boolean isInsideOfTheState(double x, double y) {
        double width = getActualWidth();
        double height = getActualHeight();

        if (isOutsideOfTheBox(x, y, width, height)) {
            return false;
        }

        return !isOutsideOfCorners(x, y, width, height);
    }

    public boolean isOutsideOfTheBox(double x, double y, double width, double height) {
        return x <= getX()
                || x >= getX() + width
                || y <= getY()
                || y >= getY() + height;
    }

    public static boolean isOutsideOfCorners(double x, double y, double width, double height) {
        double checkX = width - x < STATE_CORNER_RADIUS ? width - x : x; // Normalize location to the top left corner
        double checkY = height - y < STATE_CORNER_RADIUS ? height - y : y; // Normalize location to the top left corner
        return isOutsideOfCorner(checkX, checkY);
    }

    public static boolean isOutsideOfCorner(double xCursorPosition, double yCursorPosition) {
        // If cursor position is more than corner radius it means position is not at the corner but over the icon
        if (STATE_CORNER_RADIUS < xCursorPosition
                || STATE_CORNER_RADIUS < yCursorPosition) {
            return false;
        }
        return sqrt(pow(STATE_CORNER_RADIUS - xCursorPosition, 2)
                            + pow(STATE_CORNER_RADIUS - yCursorPosition, 2))
                >= STATE_CORNER_RADIUS;
    }

    protected double getActualWidth() {
        return shapeView.getShape().getBoundingBox().getWidth()
                * shapeView.getShape().asNode().getAbsoluteTransform().getScaleX()
                - 2; // to compensate border
    }

    protected double getActualHeight() {
        return shapeView.getShape().getBoundingBox().getHeight()
                * shapeView.getShape().asNode().getAbsoluteTransform().getScaleY()
                - 1; // to compensate border
    }

    protected double getX() {
        return shapeView.getShape().getX();
    }

    protected double getY() {
        return shapeView.getShape().getY();
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
     *
     * @param width  of the source image
     * @param height of the source image
     * @return scale rate to fit the icon in the icon circle
     */
    public static double calculateIconScale(int width, int height) {
        double size = STATE_SHAPE_ICON_RADIUS * 2;
        return size / min(width, height);
    }
}
