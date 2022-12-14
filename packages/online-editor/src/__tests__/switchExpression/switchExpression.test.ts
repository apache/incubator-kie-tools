import { switchExpression } from "../../switchExpression/switchExpression";

type UnionString3Values = "one" | "two" | "three";
type UnionStringSubset = "one" | "three";
enum SomeKind {
  ONE = "one",
  TWO = "two",
  THREE = "three",
}
const someKindSubsetValues = [SomeKind.ONE, SomeKind.THREE] as const;
type SomeKindSubset = (typeof someKindSubsetValues)[number];
type UnionMixed3Values = 1 | "two" | SomeKind.THREE;

describe("switchExpression tests", () => {
  it("test default value when key did not match", () => {
    const value: { type: UnionString3Values } = { type: "two" };
    expect(
      switchExpression(value.type, {
        one: "value1",
        default: "value_default",
      })
    ).toBe("value_default");
  });
  it("test default value when key matched", () => {
    const value: { type: UnionString3Values } = { type: "one" };
    expect(
      switchExpression(value.type, {
        one: "value1",
        default: "value_default",
      })
    ).toBe("value1");
  });
  it("test switchStatement varying types", () => {
    const value: { type: UnionMixed3Values } = { type: SomeKind.THREE };
    expect(
      switchExpression(value.type, {
        1: "value1",
        two: "value2",
        [SomeKind.THREE]: "value3",
      })
    ).toBe("value3");
  });
  it("test restrict case options by explicit cast", () => {
    const value: { type: UnionString3Values } = { type: "three" };
    expect(
      switchExpression(value.type as UnionStringSubset, {
        one: "value1",
        three: "value3",
      })
    ).toBe("value3");
  });
  it("test restrict case options by explicit binding and cast", () => {
    const value: { type: SomeKind } = { type: SomeKind.THREE };
    expect(
      switchExpression<SomeKindSubset, string>(value.type as SomeKindSubset, {
        one: "value1",
        three: "value3",
      })
    ).toBe("value3");
  });
});
