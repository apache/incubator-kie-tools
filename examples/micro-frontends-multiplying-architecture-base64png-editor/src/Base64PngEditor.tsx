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

import * as React from "react";
import { useCallback, useEffect, useImperativeHandle, useMemo, useRef, useState } from "react";
import { EditorApi, KogitoEditorEnvelopeApi, KogitoEditorEnvelopeContextType } from "@kie-tools-core/editor/dist/api";
import { EmptyState, EmptyStateIcon, EmptyStateHeader } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Nav, NavItem, NavList } from "@patternfly/react-core/dist/js/components/Nav";
import { Page } from "@patternfly/react-core/dist/js/components/Page";
import { Switch } from "@patternfly/react-core/dist/js/components/Switch";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { Base64PngEdit, Base64PngEditorStateControl } from "./Base64PngEditorStateControl";
import { KogitoEditorChannelApi } from "@kie-tools-core/editor/dist/api";
import "./Base64PngEditor.scss";

const INITIAL_CONTRAST = "100";
const INITIAL_BRIGHTNESS = "100";
const INITIAL_SATURATE = "100";
const INITIAL_SEPIA = "0";
const INITIAL_GRAYSCALE = "0";
const INITIAL_INVERT = "0";

/**
 * envelopeContext All the features and information provided by the Apache KIE Tools Envelope.
 */
interface Props {
  envelopeContext: KogitoEditorEnvelopeContextType<KogitoEditorEnvelopeApi, KogitoEditorChannelApi>;
}

/**
 * This is an Editor component. By exposing its `ref` implementing EditorApi, this component exposes its imperative
 * handles and gives control to its parent. To be able to do that, it's necessary to create a RefForwardingComponent.
 *
 * The EditorApi is a contract created by Apache KIE Tools, which determines the necessary methods for an Editor to
 * implement so that the Channel can manipulate its contents and retrieve valuable information.
 *
 * @param props Any props that are necessary for this Editor to work. In this case..
 * @param props.envelopeContext The object which allows this Editor to communicate with its containing Channel, and access the Envelope services
 */
export const Base64PngEditor = React.forwardRef<EditorApi, Props>((props, forwardedRef) => {
  /**
   * Editor Content - The current Editor value (contains all tweaks).
   * The editorContent has the current value of all tweaks that it has done to the image. This value is the one displayed on the canvas.
   */
  const [editorContent, setEditorContent] = useState("");

  /**
   * Original Content - The original base64 value (can't be changed with tweaks).
   * All new edits are made on top of the original value.
   * This is used because changing the image contrast to 0 would tweak it to a gray image, and turning it back to 100
   * would apply the changes on top of the gray image. This is solved using the originalContent on ever new edit,
   * so it's not possible to lose the image metadata after an edit.
   */
  const [originalContent, setOriginalContent] = useState("");

  /**
   * Initialize the StateControl instance. When the original content is modified it's necessary to get an another instance
   * so the states aren't shared across contents.
   */
  const stateControl = useMemo(() => new Base64PngEditorStateControl(), [originalContent]);

  /**
   * Callback is exposed to the Channel to retrieve the current value of the Editor. It returns the value of
   * the editorContent, which is the state that has the edited image.
   */
  const getContent = useCallback(() => {
    return editorContent;
  }, [editorContent]);

  /**
   * Callback is exposed to the Channel that is called when a new file is opened. It sets the originalContent to the received value.
   */
  const setContent = useCallback(
    (normalizedPosixPathRelativeToTheWorkspaceRoot: string, content: string) => {
      setOriginalContent(content);
      updateEditorToInitialState();
    },
    [stateControl]
  );

  /**
   * Callback is exposed to the Channel to retrieve the SVG content of the Editor. A SVG is a XML file that is
   * wrapped with a <svg> tag. For this Editor, it's necessary to return the edited image (editorContent).
   */
  const getPreview = useCallback(() => {
    const width = imageRef.current!.width;
    const height = imageRef.current!.height;

    return `
<svg version="1.1" width="${width}" height="${height}" viewBox="0 0 ${width} ${height}"
     xmlns="http://www.w3.org/2000/svg"
     xmlns:xlink="http://www.w3.org/1999/xlink">
    <image width="${width}" height="${height}" xlink:href="data:image/png;base64,${editorContent}" />
</svg>`;
  }, [editorContent]);

  /**
   * Do a undo command on the State Control and update the current state of the Editor
   */
  const undo = useCallback(() => {
    stateControl.undo();
    updateEditorStateWithCurrentEdit(stateControl.getCurrentBase64PngEdit());
  }, [stateControl]);

  /**
   * Do a redo command on the State Control and update the current state of the Editor
   */
  const redo = useCallback(() => {
    stateControl.redo();
    updateEditorStateWithCurrentEdit(stateControl.getCurrentBase64PngEdit());
  }, [stateControl]);

  /**
   * Update the current state of the Editor using an edit.
   * If the edit is undefined which indicates that the current state is the initial one, the Editor state goes back to the initial state.
   */
  const updateEditorStateWithCurrentEdit = useCallback((edit?: Base64PngEdit) => {
    if (edit) {
      setContrast(edit.contrast);
      setBrightness(edit.brightness);
      setSaturate(edit.saturate);
      setSepia(edit.sepia);
      setGrayscale(edit.grayscale);
      setInvert(edit.invert);
    } else {
      updateEditorToInitialState();
    }
  }, []);

  const updateEditorToInitialState = useCallback(() => {
    setContrast(INITIAL_CONTRAST);
    setBrightness(INITIAL_BRIGHTNESS);
    setSaturate(INITIAL_SATURATE);
    setSepia(INITIAL_SEPIA);
    setGrayscale(INITIAL_GRAYSCALE);
    setInvert(INITIAL_INVERT);
  }, []);

  /**
   * Notify the channel that the Editor is ready after the first render. That enables it to open files.
   */
  useEffect(() => {
    props.envelopeContext.channelApi.notifications.kogitoEditor_ready.send();
  }, []);

  /**
   * The useImperativeHandler gives the control of the Editor component to who has it's reference, making it possible to communicate with the Editor.
   * It returns all methods that are determined on the EditorApi.
   */
  useImperativeHandle(forwardedRef, () => {
    return {
      getContent: () => Promise.resolve(getContent()),
      setContent: (normalizedPosixPathRelativeToTheWorkspaceRoot: string, content: string) =>
        Promise.resolve(setContent(normalizedPosixPathRelativeToTheWorkspaceRoot, content)),
      getPreview: () => Promise.resolve(getPreview()),
      undo: () => Promise.resolve(undo()),
      redo: () => Promise.resolve(redo()),
      validate: () => Promise.resolve([]),
      setTheme: () => Promise.resolve(),
    };
  });

  /**
   * State that handles if the commands are disabled or not. It's useful in case of a broken image or an empty file is open.
   * It starts disabled by default, and when an image is successfully loaded, it becomes false.
   */
  const [disabled, setDisabled] = useState(true);

  /**
   * The reference of the image. It allows us to access/modify the canvas properties imperatively.
   * The image renders the originalContent.
   */
  const imageRef = useRef<HTMLImageElement>(null);

  /**
   * The reference of the canvas. It allows us to access/modify the canvas properties imperatively.
   * The canvas renders the editorContent.
   */
  const canvasRef = useRef<HTMLCanvasElement>(null);

  /**
   * State that handles the contrast value, 100% is the starting value.
   */
  const [contrast, setContrast] = useState(INITIAL_CONTRAST);

  /**
   * State that handles the brightness value, 100% is the starting value.
   */
  const [brightness, setBrightness] = useState(INITIAL_BRIGHTNESS);

  /**
   * State that handles the saturation value, 0% is the starting value.
   */
  const [saturate, setSaturate] = useState(INITIAL_SATURATE);

  /**
   * State that handles the sepia value, 0% is the starting value.
   */
  const [sepia, setSepia] = useState(INITIAL_SEPIA);

  /**
   * State that handles the grayscale value, 0% is the starting value.
   */
  const [grayscale, setGrayscale] = useState(INITIAL_GRAYSCALE);

  /**
   * The invert value is discrete, and has a value on the interval [0%, 100%].
   * This Editor implements only two possible values: 0% (false) and 100% (true).
   */
  const [invert, setInvert] = useState(INITIAL_INVERT);

  /**
   * This callback tweaks the contrast value. It also create a command object with the updated value to inform
   * the Editor State Control, and then notifies to the Channel that a new edit happened on the Editor. The Channel
   * will handle this notification by updating the Channel State Control with the new edit, so it stays synced with
   * the Editor's State Control.
   */
  const tweakContrast = useCallback(
    (value: string) => {
      setContrast(value);
      const command: Base64PngEdit = {
        id: new Date().getTime().toString(),
        filter: `contrast(${value}%) brightness(${brightness}%) saturate(${saturate}%) sepia(${sepia}%) grayscale(${grayscale}%) invert(${invert}%)`,
        contrast: value,
        brightness,
        saturate,
        sepia,
        grayscale,
        invert,
      };
      stateControl.updateCommandStack({ id: JSON.stringify(command) });
      props.envelopeContext.channelApi.notifications.kogitoWorkspace_newEdit.send(command);
    },
    [contrast, brightness, saturate, sepia, grayscale, invert, stateControl]
  );

  /**
   * This callback tweaks the brightness value. It also create a command object with the updated value to inform
   * the Editor State Control, and then notifies to the Channel that a new edit happened on the Editor. The Channel
   * will handle this notification by updating the Channel State Control with the new edit, so it stays synced with
   * the Editor's State Control.
   */
  const tweakBrightness = useCallback(
    (value: string) => {
      setBrightness(value);
      const command: Base64PngEdit = {
        id: new Date().getTime().toString(),
        filter: `contrast(${contrast}%) brightness(${value}%) saturate(${saturate}%) sepia(${sepia}%) grayscale(${grayscale}%) invert(${invert}%)`,
        contrast,
        brightness: value,
        saturate,
        sepia,
        grayscale,
        invert,
      };
      stateControl.updateCommandStack({ id: JSON.stringify(command) });
      props.envelopeContext.channelApi.notifications.kogitoWorkspace_newEdit.send(command);
    },
    [contrast, brightness, saturate, sepia, grayscale, invert, stateControl]
  );

  /**
   * This callback tweaks the saturation value. It also create a command object with the updated value to inform
   * the Editor State Control, and then notifies to the Channel that a new edit happened on the Editor. The Channel
   * will handle this notification by updating the Channel State Control with the new edit, so it stays synced with
   * the Editor's State Control.
   */
  const tweakSaturate = useCallback(
    (value: string) => {
      setSaturate(value);
      const command: Base64PngEdit = {
        id: new Date().getTime().toString(),
        filter: `contrast(${contrast}%) brightness(${brightness}%) saturate(${value}%) sepia(${sepia}%) grayscale(${grayscale}%) invert(${invert}%)`,
        contrast,
        brightness,
        saturate: value,
        sepia,
        grayscale,
        invert,
      };
      stateControl.updateCommandStack({ id: JSON.stringify(command) });
      props.envelopeContext.channelApi.notifications.kogitoWorkspace_newEdit.send(command);
    },
    [contrast, brightness, saturate, sepia, grayscale, invert, stateControl]
  );

  /**
   * This callback tweaks the sepia value. It also create a command object with the updated value to inform
   * the Editor State Control, and then notifies to the Channel that a new edit happened on the Editor. The Channel
   * will handle this notification by updating the Channel State Control with the new edit, so it stays synced with
   * the Editor's State Control.
   */
  const tweakSepia = useCallback(
    (value: string) => {
      setSepia(value);
      const command: Base64PngEdit = {
        id: new Date().getTime().toString(),
        filter: `contrast(${contrast}%) brightness(${brightness}%) saturate(${saturate}%) sepia(${value}%) grayscale(${grayscale}%) invert(${invert}%)`,
        contrast,
        brightness,
        saturate,
        sepia: value,
        grayscale,
        invert,
      };
      stateControl.updateCommandStack({ id: JSON.stringify(command) });
      props.envelopeContext.channelApi.notifications.kogitoWorkspace_newEdit.send(command);
    },
    [contrast, brightness, saturate, sepia, grayscale, invert, stateControl]
  );

  /**
   * This callback tweaks the grayscale value. It also create a command object with the updated value to inform
   * the Editor State Control, and then notifies to the Channel that a new edit happened on the Editor. The Channel
   * will handle this notification by updating the Channel State Control with the new edit, so it stays synced with
   * the Editor's State Control.
   */
  const tweakGrayscale = useCallback(
    (value: string) => {
      setGrayscale(value);
      const command: Base64PngEdit = {
        id: new Date().getTime().toString(),
        filter: `contrast(${contrast}%) brightness(${brightness}%) saturate(${saturate}%) sepia(${sepia}%) grayscale(${value}%) invert(${invert}%)`,
        contrast,
        brightness,
        saturate,
        sepia,
        grayscale: value,
        invert,
      };
      stateControl.updateCommandStack({ id: JSON.stringify(command) });
      props.envelopeContext.channelApi.notifications.kogitoWorkspace_newEdit.send(command);
    },
    [contrast, brightness, saturate, sepia, grayscale, invert, stateControl]
  );

  /**
   * This callback tweaks the invert value. It also create a command object with the updated value to inform
   * the Editor State Control, and then notifies to the Channel that a new edit happened on the Editor. The Channel
   * will handle this notification by updating the Channel State Control with the new edit, so it stays synced with
   * the Editor's State Control.
   */
  const tweakInvert = useCallback(
    (value: string) => {
      setInvert(value);
      const command: Base64PngEdit = {
        id: new Date().getTime().toString(),
        filter: `contrast(${contrast}%) brightness(${brightness}%) saturate(${saturate}%) sepia(${sepia}%) grayscale(${grayscale}%) invert(${value}%)`,
        contrast,
        brightness,
        saturate,
        sepia,
        grayscale,
        invert: value,
      };
      stateControl.updateCommandStack({ id: JSON.stringify(command) });
      props.envelopeContext.channelApi.notifications.kogitoWorkspace_newEdit.send(command);
    },
    [contrast, brightness, saturate, sepia, grayscale, invert, stateControl]
  );

  /**
   * Register a keyboard shortcut using the keyboardShortcuts service expose on the envelopeContext to tweak the invert value.
   */
  useEffect(() => {
    const invertId = props.envelopeContext.services.keyboardShortcuts.registerKeyPress(
      "i",
      `Edit | Invert Image`,
      async () => {
        if (!disabled && invert === "100") {
          tweakInvert("0");
        } else if (!disabled && invert === "0") {
          tweakInvert("100");
        }
      }
    );
    return () => {
      props.envelopeContext.services.keyboardShortcuts.deregister(invertId);
    };
  }, [disabled, invert]);

  /**
   * After the user makes a new edit, it will change one of the states that handle the tweak
   * values (contrast/brightness/invert/grayscale/sepia/saturate). The content of the canvas needs to be re-printed,
   * applying a filter with the current values. The resultant image is converted to base64 (toDataURL) and
   * then saved in the editorContent after the base64 header is removed (split(",")[1]).
   */
  useEffect(() => {
    const ctx = canvasRef.current!.getContext("2d")!;

    ctx.filter =
      stateControl.getCurrentBase64PngEdit()?.filter ??
      `contrast(${contrast}%) brightness(${brightness}%) saturate(${saturate}%) sepia(${sepia}%) grayscale(${grayscale}%) invert(${invert}%)`;

    ctx.drawImage(imageRef.current!, 0, 0);

    setEditorContent(canvasRef.current!.toDataURL().split(",")[1]);
  }, [contrast, brightness, saturate, sepia, grayscale, invert, stateControl]);

  /**
   * When the Editor starts, it must determine the canvas dimensions, and to do so requires the image dimension.
   * On the first render, the image will not be loaded yet, so it's necessary to add a callback to when the image
   * finishes loading, it'll set the canvas dimensions and show the image. If the image is loaded, the controls are
   * not disabled; otherwise, the controllers will remain disabled.
   */
  useEffect(() => {
    const ctx = canvasRef.current!.getContext("2d")!;
    canvasRef.current!.width = 0;
    canvasRef.current!.height = 0;

    const r = imageRef.current;

    r!.onload = () => {
      canvasRef.current!.width = r!.width;
      canvasRef.current!.height = r!.height;
      ctx.drawImage(r!, 0, 0);
      setEditorContent(canvasRef.current!.toDataURL().split(",")[1]);
      setDisabled(false);
    };

    return () => {
      r!.onload = null;
    };
  }, []);

  return (
    <Page>
      <div className={"base64png-editor--main"}>
        <div className={"base64png-editor--viewport"}>
          <img
            ref={imageRef}
            className={"base64png-editor--image"}
            src={`data:image/png;base64,${originalContent}`}
            alt={"Original"}
          />
          {disabled && (
            <EmptyState>
              <EmptyStateHeader titleText="Empty image" icon={<EmptyStateIcon icon={CubesIcon} />} headingLevel="h5" />
            </EmptyState>
          )}
          <canvas ref={canvasRef} className={"base64png-editor--canvas"} />
        </div>
        <div className={"base64png-editor--tweaks"}>
          <Nav aria-label="Image tweaker">
            <NavList>
              <NavItem className={"base64png-editor--tweaks-nav-item"} itemId={0}>
                <p>Contrast</p>
                <div className={"base64png-editor--tweaks-nav-item-div"}>
                  <input
                    disabled={disabled}
                    className={"base64png-editor--tweaks-nav-item-input"}
                    type="range"
                    min="0"
                    max="200"
                    value={contrast}
                    onChange={(e) => tweakContrast(e.target.value)}
                  />
                  <span style={{ width: "40px", textAlign: "right" }}>{contrast}</span>
                </div>
              </NavItem>
              <NavItem className={"base64png-editor--tweaks-nav-item"} itemId={1}>
                <p>Brightness</p>
                <div className={"base64png-editor--tweaks-nav-item-div"}>
                  <input
                    disabled={disabled}
                    className={"base64png-editor--tweaks-nav-item-input"}
                    type="range"
                    min="0"
                    max="200"
                    value={brightness}
                    onChange={(e) => tweakBrightness(e.target.value)}
                  />
                  <span className={"base64png-editor--tweaks-nav-item-span"}>{brightness}</span>
                </div>
              </NavItem>
              <NavItem className={"base64png-editor--tweaks-nav-item"} itemId={4}>
                <p>Saturate</p>
                <div className={"base64png-editor--tweaks-nav-item-div"}>
                  <input
                    disabled={disabled}
                    className={"base64png-editor--tweaks-nav-item-input"}
                    type="range"
                    min="0"
                    max="200"
                    value={saturate}
                    onChange={(e) => tweakSaturate(e.target.value)}
                  />
                  <span className={"base64png-editor--tweaks-nav-item-span"}>{saturate}</span>
                </div>
              </NavItem>
              <NavItem className={"base64png-editor--tweaks-nav-item"} itemId={2}>
                <p>Sepia</p>
                <div className={"base64png-editor--tweaks-nav-item-div"}>
                  <input
                    disabled={disabled}
                    className={"base64png-editor--tweaks-nav-item-input"}
                    type="range"
                    min="0"
                    max="100"
                    value={sepia}
                    onChange={(e) => tweakSepia(e.target.value)}
                  />
                  <span className={"base64png-editor--tweaks-nav-item-span"}>{sepia}</span>
                </div>
              </NavItem>
              <NavItem className={"base64png-editor--tweaks-nav-item"} itemId={3}>
                <p>Grayscale</p>
                <div className={"base64png-editor--tweaks-nav-item-div"}>
                  <input
                    disabled={disabled}
                    className={"base64png-editor--tweaks-nav-item-input"}
                    type="range"
                    min="0"
                    max="100"
                    value={grayscale}
                    onChange={(e) => tweakGrayscale(e.target.value)}
                  />
                  <span className={"base64png-editor--tweaks-nav-item-span"}>{grayscale}</span>
                </div>
              </NavItem>
              <NavItem itemId={5}>
                <div className={"base64png-editor--tweaks-nav-item"}>
                  <p>Invert</p>
                  <Switch
                    id="invert-switch"
                    isDisabled={disabled}
                    isChecked={invert === "100"}
                    onChange={(_event, value) => tweakInvert(value ? "100" : "0")}
                  />
                </div>
              </NavItem>
              {stateControl.isDirty() && (
                <div style={{ display: "flex", alignItems: "center", padding: "20px" }}>
                  <p style={{ color: "red" }}>Image was edited.</p>
                </div>
              )}
            </NavList>
          </Nav>
        </div>
      </div>
    </Page>
  );
});
