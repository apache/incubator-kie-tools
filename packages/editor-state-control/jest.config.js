module.exports = {
    reporters: ["default"],
    moduleDirectories: ["node_modules", "src"],
    moduleFileExtensions: ["js", "jsx", "ts", "tsx"],
    setupFilesAfterEnv: ["./src/__tests__/jest.setup.ts"],
    testRegex: "/__tests__/.*\\.test\\.(jsx?|tsx?)$",
    transform: {
        "^.+\\.jsx?$": "babel-jest",
        "^.+\\.tsx?$": "ts-jest"
    }
};
