const parseFile = require("../src/parseFile");
const path = require("path");

describe("Parsing", () => {
  test("cypress", () => {
    const failed = [];
    const passed = [];

    parseFile(path.join(__dirname, "./reports/junit-report__from-cypress.xml"), failed, passed);

    expect(failed.length).toStrictEqual(0);
    expect(passed.length).toStrictEqual(6);
  });

  test("jest", () => {
    const failed = [];
    const passed = [];

    parseFile(path.join(__dirname, "./reports/junit-report__from-jest.xml"), failed, passed);

    expect(failed.length).toStrictEqual(0);
    expect(passed.length).toStrictEqual(33);
  });

  test("surefire", () => {
    const failed = [];
    const passed = [];

    parseFile(path.join(__dirname, "./reports/junit-report__from-surefire.xml"), failed, passed);

    expect(failed.length).toStrictEqual(0);
    expect(passed.length).toStrictEqual(1);
  });
});
