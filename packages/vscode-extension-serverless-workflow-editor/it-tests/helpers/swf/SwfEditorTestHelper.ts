/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { WebElement } from "selenium-webdriver";
import { By, WebView } from "vscode-extension-tester";
import { EditorTestHelper, sleep } from "@kie-tools/vscode-extension-common-test-helpers";

/**
 * Helper class to easen work with SWF editor inside of a webview.
 * Make sure you switch to the webview's frame before creating and instance
 * via constructor.
 */
export default class SwfEditorTestHelper extends EditorTestHelper {
  constructor(webview: WebView) {
    super(webview);
  }

  public async getAllNodeIds(): Promise<string[]> {
    await this.switchToEditorFrame();
    const driver = this.webview.getDriver();
    const nodeIds = (await driver.executeScript("return window.frames.canvas.getNodeIds()")) as string[];
    await this.switchBack();
    return Promise.resolve(nodeIds);
  }

  /**
   * Selects node on canvas.
   *
   * @param nodeId of the node to be selected.
   */
  public async selectNode(nodeId: string) {
    await this.switchToEditorFrame();
    const driver = this.webview.getDriver();

    const canvasWebElement = await this.getCanvasPanelElementInActiveFrame();
    const nodeCoordinates = await this.calculateNodeCenterCoordinates(nodeId);

    const actions = driver.actions({ async: true });
    await actions.move({ x: nodeCoordinates[0], y: nodeCoordinates[1], origin: canvasWebElement }).click().perform();
    await sleep(8000);
    await this.switchBack();
  }

  public async getWorkbenchPanelViewElement(): Promise<WebElement> {
    await this.switchToEditorFrame();
    const result = await this.webview.findWebElement(By.xpath(".//div[@class='qe-static-workbench-panel-view']"));
    await this.switchBack();
    return Promise.resolve(result);
  }

  /**
   * Gets canvas-panel web element.
   *
   * @returns Promise that resolves to a WebElement representing canvas-panel.
   */
  public async getCanvasPanelElement(): Promise<WebElement> {
    await this.switchToEditorFrame();
    const result = await this.getCanvasPanelElementInActiveFrame();
    await this.switchBack();
    return result;
  }

  public async getAllNodesInMermaidDiagram(): Promise<WebElement[]> {
    await this.switchToEditorFrame();
    const result = await this.webview.findWebElements(
      By.xpath(".//*[name()='svg']//*[name()='g' and (@class='node statediagram-state' or @class='node default')]")
    );
    await this.switchBack();
    return Promise.resolve(result);
  }

  public async getSvgElement(): Promise<WebElement> {
    await this.switchToEditorFrame();
    const result = await this.webview.findWebElement(By.xpath(".//*[name()='svg']"));
    await this.switchBack();
    return Promise.resolve(result);
  }

  public async getMermaidDivElement(): Promise<WebElement> {
    await this.switchToEditorFrame();
    const result = await this.webview.findWebElement(By.xpath(".//div[@class='mermaid']"));
    await this.switchBack();
    return Promise.resolve(result);
  }

  /**
   * Gets canvas-panel web element in active frame.
   *
   * Ensure the function is called from the active frame. To switch to the active frame use switchToEditorFrame() function.
   *
   * @returns Promise that resolves to a WebElement representing canvas-panel.
   */
  private async getCanvasPanelElementInActiveFrame(): Promise<WebElement> {
    const result = await this.webview.findWebElement(By.xpath(".//div[@class='canvas-panel']"));
    return Promise.resolve(result);
  }

  /**
   * Calculates node's center coordinates relative to center of canvas.
   *
   * Make sure the function is called from the active frame. To switch to the active frame use switchToEditorFrame() function.
   *
   * @param nodeId id of the node.
   * @returns Promise that resolves to an array with coordinates: [x, y].
   */
  private async calculateNodeCenterCoordinates(nodeId: string): Promise<number[]> {
    // Node coordinates
    const nodeCoordinates = await this.getNodeCoordinates(nodeId);

    // Node dimensions
    const nodeDimensions = await this.getNodeDimensions(nodeId);
    const nodeWidth = nodeDimensions[0];
    const nodeHeight = nodeDimensions[1];
    const nodeRelativeCenterX = nodeWidth % 2 == 0 ? nodeWidth / 2 : (nodeWidth - 1) / 2;
    const nodeRelativeCenterY = nodeHeight % 2 == 0 ? nodeHeight / 2 : (nodeWidth - 1) / 2;

    // Canvas
    const canvasWebElement = await this.getCanvasPanelElementInActiveFrame();
    const canvasRectangle = await canvasWebElement.getRect();
    const canvasX0PointerOffset =
      canvasRectangle.width % 2 == 0 ? canvasRectangle.width / 2 : (canvasRectangle.width - 1) / 2;
    const canvasY0PointerOffset =
      canvasRectangle.height % 2 == 0 ? canvasRectangle.height / 2 : (canvasRectangle.height - 1) / 2;

    // Node center coordinates relative to center of canvas
    const nodeCenterXCoordinate = nodeCoordinates[0] + nodeRelativeCenterX - canvasX0PointerOffset;
    const nodeCenterYCoordinate = nodeCoordinates[1] + nodeRelativeCenterY - canvasY0PointerOffset;

    return Promise.resolve([nodeCenterXCoordinate, nodeCenterYCoordinate]);
  }

  /**
   * Gets node coordinates.
   *
   * Make sure the function is called from the active frame. To switch to the active frame use switchToEditorFrame() function.
   *
   * @param nodeId id of the node.
   * @returns Promise that resolves to to an array with coordinates: [x, y].
   */
  private async getNodeCoordinates(nodeId: string): Promise<number[]> {
    const driver = this.webview.getDriver();
    const scriptGetNodeCoordinates = `return window.frames.canvas.getLocation("${nodeId}")`;
    const nodeCoordinates = (await driver.executeScript(scriptGetNodeCoordinates)) as number[];
    return Promise.resolve(nodeCoordinates);
  }

  /**
   * Gets node dimensions.
   *
   * Make sure the function is called from the active frame. To switch to the active frame use switchToEditorFrame() function.
   *
   * @param nodeId id of the node.
   * @returns Promise that resolves to to an array with dimensions: [width, height].
   */
  private async getNodeDimensions(nodeId: string): Promise<number[]> {
    const driver = this.webview.getDriver();
    const scriptGetNodeDimensions = `return window.frames.canvas.getDimensions("${nodeId}")`;
    const nodeDimensions = (await driver.executeScript(scriptGetNodeDimensions)) as number[];
    return Promise.resolve(nodeDimensions);
  }
}
