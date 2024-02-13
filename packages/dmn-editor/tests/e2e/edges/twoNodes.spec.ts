import { test, expect } from "@playwright/test";
import { env } from "../../../env";

test.beforeEach(async ({ page }, testInfo) => {
  await page.goto(
    `http://localhost:${env.dmnEditor.storybook.port}/iframe.html?args=&id=example-dmndevwebapp--empty-model&viewMode=story`
  );
});

test.describe("Create new node from existing one", () => {
  test("InputData -> Decision", async ({ page }) => {
    await page
      .getByTitle("Input Data", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    await page.getByText("New Input Data").hover();
    await page
      .getByTitle("node_decision")
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });

    expect(await page.screenshot()).toMatchSnapshot();
  });

  test("InputData -> Knowledge Source", async ({ page }) => {
    await page
      .getByTitle("Input Data", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    await page.getByText("New Input Data").hover();
    await page
      .getByTitle("node_knowledgeSource")
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });

    expect(await page.screenshot()).toMatchSnapshot();
  });

  test("InputData -> Text Annotation", async ({ page }) => {
    await page
      .getByTitle("Input Data", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 400 } });

    await page.getByText("New Input Data").hover();
    await page
      .getByTitle("node_textAnnotation")
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });

    expect(await page.screenshot()).toMatchSnapshot();
  });

  test("Decision -> Decision", async ({ page }) => {
    await page
      .getByTitle("Decision", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    // await page.getByText("New Decision").hover();
    await page
      .getByTitle("node_decision")
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });

    expect(await page.screenshot()).toMatchSnapshot();
  });

  test("Decision -> Knoledge Source", async ({ page }) => {
    await page
      .getByTitle("Decision", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    // await page.getByText("New Decision").hover();
    await page
      .getByTitle("node_knowledgeSource")
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });

    expect(await page.screenshot()).toMatchSnapshot();
  });

  test("Decision -> Text Annotation", async ({ page }) => {
    await page
      .getByTitle("Decision", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 400 } });

    // await page.getByText("New Decision").hover();
    await page
      .getByTitle("node_textAnnotation")
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });

    expect(await page.screenshot()).toMatchSnapshot();
  });

  test("BKM -> Decision", async ({ page }) => {
    await page
      .getByTitle("Business Knowledge Model", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    // await page.getByText("New BKM").hover();
    await page
      .getByTitle("node_decision")
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });

    expect(await page.screenshot()).toMatchSnapshot();
  });

  test("BKM -> BKM", async ({ page }) => {
    await page
      .getByTitle("Business Knowledge Model", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    // await page.getByText("New BKM").hover();
    await page.getByTitle("node_bkm").dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });

    expect(await page.screenshot()).toMatchSnapshot();
  });

  test("BKM -> TextAnnotation", async ({ page }) => {
    await page
      .getByTitle("Business Knowledge Model", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 400 } });

    // await page.getByText("New BKM").hover();
    await page
      .getByTitle("node_textAnnotation")
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });

    expect(await page.screenshot()).toMatchSnapshot();
  });
});

test.describe("Connect two existing nodes", () => {
  test("InputData -> Decision", async ({ page }) => {
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

    expect(await page.screenshot()).toMatchSnapshot();
  });

  test("InputData -> Knowledge Source", async ({ page }) => {
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
      .getByTitle("edge_knowledgeRequirement")
      .locator("visible=true")
      .dragTo(page.getByText("New Knowledge Source"));

    expect(await page.screenshot()).toMatchSnapshot();
  });

  test("InputData -> Text Annotation", async ({ page }) => {
    // Add two nodes
    await page
      .getByTitle("Text Annotation", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });
    await page
      .getByTitle("Input Data", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 400 } });

    // Connect these nodes
    await page.getByText("New Input Data").hover();
    await page
      .getByTitle("edge_association")
      .locator("visible=true")
      .dragTo(page.getByText("New text annotation"), { targetPosition: { x: 100, y: 100 } });

    expect(await page.screenshot()).toMatchSnapshot();
  });

  test("Decision -> Decision", async ({ page }) => {
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

    expect(await page.screenshot()).toMatchSnapshot();
  });

  test("Decision -> Knoledge Source", async ({ page }) => {
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
      .getByTitle("edge_knowledgeRequirement")
      .locator("visible=true")
      .dragTo(page.getByText("New Knowledge Source"));

    expect(await page.screenshot()).toMatchSnapshot();
  });

  test("Decision -> Text Annotation", async ({ page }) => {
    // Add two nodes
    await page
      .getByTitle("Text Annotation", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });
    await page
      .getByTitle("Decision", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 400 } });

    // Connect these nodes
    // await page.getByText("New Decision").hover();
    await page
      .getByTitle("edge_association")
      .locator("visible=true")
      .dragTo(page.getByText("New text annotation"), { targetPosition: { x: 100, y: 100 } });

    expect(await page.screenshot()).toMatchSnapshot();
  });

  test("BKM -> Decision", async ({ page }) => {
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

    expect(await page.screenshot()).toMatchSnapshot();
  });

  test("BKM -> BKM", async ({ page }) => {
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

    expect(await page.screenshot()).toMatchSnapshot();
  });

  test("BKM -> TextAnnotation", async ({ page }) => {
    // Add two nodes
    await page
      .getByTitle("Business Knowledge Model", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 100 } });
    await page
      .getByTitle("Text Annotation", { exact: true })
      .dragTo(page.getByTestId("rf__wrapper"), { targetPosition: { x: 100, y: 300 } });

    // Connect these nodes
    // await page.getByText("New BKM").hover();
    await page.getByTitle("edge_association").locator("visible=true").dragTo(page.getByText("New BKM"));

    expect(await page.screenshot()).toMatchSnapshot();
  });
});
