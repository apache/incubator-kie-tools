import { test, expect } from "@playwright/test";

test.describe("Create new node from existing one", () => {
  test("InputData -> Decision", async ({ page }) => {
    await page.goto("http://localhost:6006/iframe.html?args=&id=example-dmndevwebapp--empty-model&viewMode=story");

    await page
      .getByTitle("Input Data", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    await page.getByText("New Input Data").hover();
    await page
      .getByTitle("node_decision")
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });
  });

  test("InputData -> Knowledge Source", async ({ page }) => {
    await page.goto("http://localhost:6006/iframe.html?args=&id=example-dmndevwebapp--empty-model&viewMode=story");

    await page
      .getByTitle("Input Data", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    await page.getByText("New Input Data").hover();
    await page
      .getByTitle("node_knowledgeSource")
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });
  });

  test("InputData -> Text Annotation", async ({ page }) => {
    await page.goto("http://localhost:6006/iframe.html?args=&id=example-dmndevwebapp--empty-model&viewMode=story");

    await page
      .getByTitle("Input Data", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    await page.getByText("New Input Data").hover();
    await page
      .getByTitle("node_textAnnotation")
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });
  });

  test("Decision -> Decision", async ({ page }) => {
    await page.goto("http://localhost:6006/iframe.html?args=&id=example-dmndevwebapp--empty-model&viewMode=story");

    await page
      .getByTitle("Decision", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    // await page.getByText("New Decision").hover();
    await page
      .getByTitle("node_decision")
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });
  });

  test("Decision -> Knoledge Source", async ({ page }) => {
    await page.goto("http://localhost:6006/iframe.html?args=&id=example-dmndevwebapp--empty-model&viewMode=story");

    await page
      .getByTitle("Decision", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    // await page.getByText("New Decision").hover();
    await page
      .getByTitle("node_knowledgeSource")
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });
  });

  test("Decision -> Text Annotation", async ({ page }) => {
    await page.goto("http://localhost:6006/iframe.html?args=&id=example-dmndevwebapp--empty-model&viewMode=story");

    await page
      .getByTitle("Decision", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    // await page.getByText("New Decision").hover();
    await page
      .getByTitle("node_textAnnotation")
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });
  });

  test("BKM -> Decision", async ({ page }) => {
    await page.goto("http://localhost:6006/iframe.html?args=&id=example-dmndevwebapp--empty-model&viewMode=story");

    await page
      .getByTitle("Business Knowledge Model", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    // await page.getByText("New BKM").hover();
    await page
      .getByTitle("node_decision")
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });
  });

  test("BKM -> BKM", async ({ page }) => {
    await page.goto("http://localhost:6006/iframe.html?args=&id=example-dmndevwebapp--empty-model&viewMode=story");

    await page
      .getByTitle("Business Knowledge Model", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    // await page.getByText("New BKM").hover();
    await page.getByTitle("node_bkm").dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });
  });

  test("BKM -> TextAnnotation", async ({ page }) => {
    await page.goto("http://localhost:6006/iframe.html?args=&id=example-dmndevwebapp--empty-model&viewMode=story");

    await page
      .getByTitle("Business Knowledge Model", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    // await page.getByText("New BKM").hover();
    await page
      .getByTitle("node_textAnnotation")
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });
  });
});

test.describe("Connect two existing nodes", () => {
  test("InputData -> Decision", async ({ page }) => {
    await page.goto("http://localhost:6006/iframe.html?args=&id=example-dmndevwebapp--empty-model&viewMode=story");

    // Add two nodes
    await page
      .getByTitle("Decision", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });
    await page
      .getByTitle("Input Data", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    // Connect these nodes
    await page.getByText("New Input Data").hover();
    await page.getByTitle("edge_informationRequirement").locator("visible=true").dragTo(page.getByText("New Decision"));

    await page.screenshot({ path: "screenshotA.png" });
  });

  test("InputData -> Knowledge Source", async ({ page }) => {
    await page.goto("http://localhost:6006/iframe.html?args=&id=example-dmndevwebapp--empty-model&viewMode=story");

    // Add two nodes
    await page
      .getByTitle("Knowledge Source", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });
    await page
      .getByTitle("Input Data", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    // Connect these nodes
    await page.getByText("New Input Data").hover();
    await page
      .getByTitle("edge_informationRequirement")
      .locator("visible=true")
      .dragTo(page.getByText("New Knowledge Source"));
    await page.screenshot({ path: "screenshotB.png" });
  });

  test("InputData -> Text Annotation", async ({ page }) => {
    await page.goto("http://localhost:6006/iframe.html?args=&id=example-dmndevwebapp--empty-model&viewMode=story");

    // Add two nodes
    await page
      .getByTitle("Text Annotation", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });
    await page
      .getByTitle("Input Data", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    // Connect these nodes
    await page.getByText("New Input Data").hover();
    await page.getByTitle("edge_association").locator("visible=true").dragTo(page.getByText("New Text Annotation"));
    await page.screenshot({ path: "screenshotC.png" });
  });

  test("Decision -> Decision", async ({ page }) => {
    await page.goto("http://localhost:6006/iframe.html?args=&id=example-dmndevwebapp--empty-model&viewMode=story");

    // Add two nodes
    await page
      .getByTitle("Decision", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });
    await page
      .getByTitle("Decision", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    // Connect these nodes
    // await page.getByText("New Decision").first().hover();
    await page
      .getByTitle("edge_informationRequirement")
      .locator("visible=true")
      .dragTo(page.getByText("New Decision").first());
    await page.screenshot({ path: "screenshotD.png" });
  });

  test("Decision -> Knoledge Source", async ({ page }) => {
    await page.goto("http://localhost:6006/iframe.html?args=&id=example-dmndevwebapp--empty-model&viewMode=story");

    // Add two nodes
    await page
      .getByTitle("Knowledge Source", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });
    await page
      .getByTitle("Decision", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    // Connect these nodes
    // await page.getByText("New Decision").hover();
    await page
      .getByTitle("edge_informationRequirement")
      .locator("visible=true")
      .dragTo(page.getByText("New Knowledge Source"));
    await page.screenshot({ path: "screenshotE.png" });
  });

  test("Decision -> Text Annotation", async ({ page }) => {
    await page.goto("http://localhost:6006/iframe.html?args=&id=example-dmndevwebapp--empty-model&viewMode=story");

    // Add two nodes
    await page
      .getByTitle("Text Annotation", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });
    await page
      .getByTitle("Decision", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    // Connect these nodes
    // await page.getByText("New Decision").hover();
    await page.getByTitle("edge_association").locator("visible=true").dragTo(page.getByText("New Text Annotation"));
    await page.screenshot({ path: "screenshotF.png" });
  });

  test("BKM -> Decision", async ({ page }) => {
    await page.goto("http://localhost:6006/iframe.html?args=&id=example-dmndevwebapp--empty-model&viewMode=story");

    // Add two nodes
    await page
      .getByTitle("Decision", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });
    await page
      .getByTitle("Business Knowledge Model", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    // Connect these nodes
    // await page.getByText("New BKM").hover();
    await page.getByTitle("edge_knowledgeRequirement").locator("visible=true").dragTo(page.getByText("New Decision"));
    await page.screenshot({ path: "screenshotG.png" });
  });

  test("BKM -> BKM", async ({ page }) => {
    await page.goto("http://localhost:6006/iframe.html?args=&id=example-dmndevwebapp--empty-model&viewMode=story");

    // Add two nodes
    await page
      .getByTitle("Business Knowledge Model", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });
    await page
      .getByTitle("Business Knowledge Model", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    // Connect these nodes
    // await page.getByText("New BKM").first().hover();
    await page
      .getByTitle("edge_knowledgeRequirement")
      .locator("visible=true")
      .dragTo(page.getByText("New BKM").first());
    await page.screenshot({ path: "screenshotH.png" });
  });

  test("BKM -> TextAnnotation", async ({ page }) => {
    await page.goto("http://localhost:6006/iframe.html?args=&id=example-dmndevwebapp--empty-model&viewMode=story");

    // Add two nodes
    await page
      .getByTitle("Business Knowledge Model", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });
    await page
      .getByTitle("Text Annotation", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    // Connect these nodes
    // await page.getByText("New BKM").hover();
    await page.getByTitle("edge_association").locator("visible=true").dragTo(page.getByText("New Text Annotation"));
    await page.screenshot({ path: "screenshotI.png" });
  });
});
