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

// The below mentioned functions are referenced from https://stedolan.github.io/jq/manual/.
export const jqBuiltInFunctions = [
  {
    functionName: "length",
    description: "The builtin function length gets the length of various different types of value.",
  },
  {
    functionName: "utf8bytelength",
    description: "The builtin function utf8bytelength outputs the number of bytes used to encode a string in UTF-8.",
  },
  {
    functionName: "keys",
    description: "The builtin function keys, when given an object, returns its keys in an array.",
  },
  {
    functionName: "keys_unsorted",
    description:
      "The keys_unsorted function is just like keys, but if the input is an object then the keys will not be sorted, instead the keys will roughly be in insertion order.",
  },
  {
    functionName: "has(key)",
    description:
      "The builtin function has returns whether the input object has the given key, or the input array has an element at the given index.",
  },
  {
    functionName: "in",
    description:
      "The builtin function in returns whether or not the input key is in the given object, or the input index corresponds to an element in the given array. It is, essentially, an inversed version of has.",
  },
  {
    functionName: "map(x)",
    description:
      "For any filter x, map(x) will run that filter for each element of the input array, and return the outputs in a new array. map(.+1) will increment each element of an array of numbers.",
  },
  {
    functionName: "map_values(x)",
    description:
      "map_values(x) will run that filter for each element, but it will return an object when an object is passed.",
  },
  {
    functionName: "path(path_expression)",
    description:
      "Path expressions are jq expressions like .a, but also .[]. There are two types of path expressions: ones that can match exactly, and ones that cannot. For example, .a.b.c is an exact match path expression, while .a[].b is not.",
  },
  {
    functionName: "del(path_expression)",
    description: "The builtin function del removes a key and its corresponding value from an object.",
  },
  {
    functionName: "getpath(PATHS)",
    description: "The builtin function getpath outputs the values in . found at each path in PATHS.",
  },
  { functionName: "setpath(PATHS; VALUE)", description: "The builtin function setpath sets the PATHS in . to VALUE." },
  {
    functionName: "delpaths(PATHS)",
    description:
      "The builtin function delpaths sets the PATHS in .. PATHS must be an array of paths, where each path is an array of strings and numbers.",
  },
  {
    functionName: "to_entries",
    description:
      'These functions convert between an object and an array of key-value pairs. If to_entries is passed an object, then for each k: v entry in the input, the output array includes {"key": k, "value": v}.',
  },
  {
    functionName: "from_entries",
    description:
      "from_entries does the opposite conversion, and with_entries(foo) is a shorthand for to_entries | map(foo) | from_entries, useful for doing some operation to all keys and values of an object. from_entries accepts key, Key, name, Name, value and Value as keys.",
  },
  {
    functionName: "with_entries",
    description:
      "from_entries does the opposite conversion, and with_entries(foo) is a shorthand for to_entries | map(foo) | from_entries, useful for doing some operation to all keys and values of an object. from_entries accepts key, Key, name, Name, value and Value as keys.",
  },
  { functionName: "select(boolean_expression)", description: "" },
  {
    functionName: "arrays",
    description:
      "The function select(foo) produces its input unchanged if foo returns true for that input, and produces no output otherwise.",
  },
  {
    functionName: "objects",
    description:
      "These built-ins select only inputs that are arrays, objects, iterables (arrays or objects), booleans, numbers, normal numbers, finite numbers, strings, null, non-null values, and non-iterables, respectively.",
  },
  {
    functionName: "iterables",
    description:
      "These built-ins select only inputs that are arrays, objects, iterables (arrays or objects), booleans, numbers, normal numbers, finite numbers, strings, null, non-null values, and non-iterables, respectively.",
  },
  {
    functionName: "booleans",
    description:
      "These built-ins select only inputs that are arrays, objects, iterables (arrays or objects), booleans, numbers, normal numbers, finite numbers, strings, null, non-null values, and non-iterables, respectively.",
  },
  {
    functionName: "numbers",
    description:
      "These built-ins select only inputs that are arrays, objects, iterables (arrays or objects), booleans, numbers, normal numbers, finite numbers, strings, null, non-null values, and non-iterables, respectively.",
  },
  {
    functionName: "normals",
    description:
      "These built-ins select only inputs that are arrays, objects, iterables (arrays or objects), booleans, numbers, normal numbers, finite numbers, strings, null, non-null values, and non-iterables, respectively.",
  },
  {
    functionName: "finites",
    description:
      "These built-ins select only inputs that are arrays, objects, iterables (arrays or objects), booleans, numbers, normal numbers, finite numbers, strings, null, non-null values, and non-iterables, respectively.",
  },
  {
    functionName: "strings",
    description:
      "These built-ins select only inputs that are arrays, objects, iterables (arrays or objects), booleans, numbers, normal numbers, finite numbers, strings, null, non-null values, and non-iterables, respectively.",
  },
  {
    functionName: "nulls",
    description:
      "These built-ins select only inputs that are arrays, objects, iterables (arrays or objects), booleans, numbers, normal numbers, finite numbers, strings, null, non-null values, and non-iterables, respectively.",
  },
  {
    functionName: "values",
    description:
      "These built-ins select only inputs that are arrays, objects, iterables (arrays or objects), booleans, numbers, normal numbers, finite numbers, strings, null, non-null values, and non-iterables, respectively.",
  },
  {
    functionName: "scalars",
    description:
      "These built-ins select only inputs that are arrays, objects, iterables (arrays or objects), booleans, numbers, normal numbers, finite numbers, strings, null, non-null values, and non-iterables, respectively.",
  },
  { functionName: "empty", description: "empty returns no results. None at all. Not even null." },
  {
    functionName: "error(message)",
    description:
      "Produces an error, just like .a applied to values other than null and objects would, but with the given message as the errors value. Errors can be caught with try/catch; see below.",
  },
  {
    functionName: "halt",
    description: "Stops the jq program with no further outputs. jq will exit with exit status 0.",
  },
  {
    functionName: "halt_error",
    description:
      "Stops the jq program with no further outputs. The input will be printed on stderr as raw output (i.e., strings will not have double quotes) with no decoration, not even a newline.",
  },
  {
    functionName: "halt_error(exit_code)",
    description:
      "Stops the jq program with no further outputs. The input will be printed on stderr as raw output (i.e., strings will not have double quotes) with no decoration, not even a newline.",
  },
  {
    functionName: "paths",
    description:
      "paths outputs the paths to all the elements in its input (except it does not output the empty list, representing . itself).",
  },
  {
    functionName: "paths(node_filter)",
    description:
      "paths(f) outputs the paths to any values for which f is true. That is, paths(numbers) outputs the paths to all numeric values.",
  },
  {
    functionName: "leaf_paths",
    description:
      "leaf_paths is an alias of paths(scalars); leaf_paths is deprecated and will be removed in the next major release.",
  },
  {
    functionName: "add",
    description:
      "The filter add takes as input an array, and produces as output the elements of the array added together. This might mean summed, concatenated or merged depending on the types of the elements of the input array - the rules are the same as those for the + operator (described above).",
  },
  {
    functionName: "any",
    description:
      "The filter any takes as input an array of boolean values, and produces true as output if any of the elements of the array are true.",
  },
  {
    functionName: "any(condition)",
    description: "The any(condition) form applies the given condition to the elements of the input array.",
  },
  {
    functionName: "any(generator; condition)",
    description:
      "The any(generator; condition) form applies the given condition to all the outputs of the given generator.",
  },
  {
    functionName: "all",
    description:
      "The filter all takes as input an array of boolean values, and produces true as output if all of the elements of the array are true.",
  },
  {
    functionName: "all(condition)",
    description: "The all(condition) form applies the given condition to the elements of the input array.",
  },
  {
    functionName: "all(generator; condition)",
    description:
      "The all(generator; condition) form applies the given condition to all the outputs of the given generator.",
  },
  {
    functionName: "flatten",
    description:
      "The filter flatten takes as input an array of nested arrays, and produces a flat array in which all arrays inside the original array have been recursively replaced by their values. You can pass an argument to it to specify how many levels of nesting to flatten.",
  },
  {
    functionName: "flatten(depth)",
    description:
      "The filter flatten takes as input an array of nested arrays, and produces a flat array in which all arrays inside the original array have been recursively replaced by their values. You can pass an argument to it to specify how many levels of nesting to flatten.",
  },
  {
    functionName: "range(upto)",
    description:
      "The range function produces a range of numbers. range(4;10) produces 6 numbers, from 4 (inclusive) to 10 (exclusive). The numbers are produced as separate outputs. Use [range(4;10)] to get a range as an array.",
  },
  {
    functionName: "range(from;upto)",
    description: "The two argument form generates numbers from from to upto with an increment of 1.",
  },
  {
    functionName: "range(from;upto;by)",
    description: "The three argument form generates numbers from to upto with an increment of by.",
  },
  { functionName: "floor", description: "The floor function returns the floor of its numeric input." },
  { functionName: "sqrt", description: "The sqrt function returns the square root of its numeric input." },
  {
    functionName: "tonumber",
    description:
      "The tonumber function parses its input as a number. It will convert correctly-formatted strings to their numeric equivalent, leave numbers alone, and give an error on all other input.",
  },
  {
    functionName: "tostring",
    description:
      "The tostring function prints its input as a string. Strings are left unchanged, and all other values are JSON-encoded.",
  },
  {
    functionName: "type",
    description:
      "The type function returns the type of its argument as a string, which is one of null, boolean, number, string, array or object.",
  },
  {
    functionName: "infinite",
    description:
      'Some arithmetic operations can yield infinities and "not a number" (NaN) values. The isinfinite builtin returns true if its input is infinite. The isnan builtin returns true if its input is a NaN. The infinite builtin returns a positive infinite value. The nan builtin returns a NaN. The isnormal builtin returns true if its input is a normal number.',
  },
  {
    functionName: "nan",
    description:
      'Some arithmetic operations can yield infinities and "not a number" (NaN) values. The isinfinite builtin returns true if its input is infinite. The isnan builtin returns true if its input is a NaN. The infinite builtin returns a positive infinite value. The nan builtin returns a NaN. The isnormal builtin returns true if its input is a normal number.',
  },
  {
    functionName: "isinfinite",
    description:
      'Some arithmetic operations can yield infinities and "not a number" (NaN) values. The isinfinite builtin returns true if its input is infinite. The isnan builtin returns true if its input is a NaN. The infinite builtin returns a positive infinite value. The nan builtin returns a NaN. The isnormal builtin returns true if its input is a normal number.',
  },
  {
    functionName: "isnan",
    description:
      'Some arithmetic operations can yield infinities and "not a number" (NaN) values. The isinfinite builtin returns true if its input is infinite. The isnan builtin returns true if its input is a NaN. The infinite builtin returns a positive infinite value. The nan builtin returns a NaN. The isnormal builtin returns true if its input is a normal number.',
  },
  {
    functionName: "isfinite",
    description:
      'Some arithmetic operations can yield infinities and "not a number" (NaN) values. The isinfinite builtin returns true if its input is infinite. The isnan builtin returns true if its input is a NaN. The infinite builtin returns a positive infinite value. The nan builtin returns a NaN. The isnormal builtin returns true if its input is a normal number.',
  },
  {
    functionName: "isnormal",
    description:
      'Some arithmetic operations can yield infinities and "not a number" (NaN) values. The isinfinite builtin returns true if its input is infinite. The isnan builtin returns true if its input is a NaN. The infinite builtin returns a positive infinite value. The nan builtin returns a NaN. The isnormal builtin returns true if its input is a normal number.',
  },
  { functionName: "sort", description: "The sort functions sorts its input, which must be an array." },
  {
    functionName: "sort_by(path_expression)",
    description: "sort_by(foo) compares two elements by comparing the result of foo on each element.",
  },
  {
    functionName: "group_by(path_expression)",
    description:
      "group_by(.foo) takes as input an array, groups the elements having the same .foo field into separate arrays, and produces all of these arrays as elements of a larger array, sorted by the value of the .foo field.",
  },
  { functionName: "min", description: "Find the minimum or maximum element of the input array." },
  { functionName: "max", description: "Find the minimum or maximum element of the input array." },
  {
    functionName: "min_by(path_exp)",
    description:
      "The min_by(path_exp) and max_by(path_exp) functions allow you to specify a particular field or property to examine, e.g. min_by(.foo) finds the object with the smallest foo field.",
  },
  {
    functionName: "max_by(path_exp)",
    description:
      "The min_by(path_exp) and max_by(path_exp) functions allow you to specify a particular field or property to examine, e.g. min_by(.foo) finds the object with the smallest foo field.",
  },
  {
    functionName: "unique",
    description:
      "The unique function takes as input an array and produces an array of the same elements, in sorted order, with duplicates removed.",
  },
  {
    functionName: "unique_by(path_exp)",
    description:
      "The unique_by(path_exp) function will keep only one element for each value obtained by applying the argument. Think of it as making an array by taking one element out of every group produced by group.",
  },
  { functionName: "reverse", description: "This function reverses an array." },
  {
    functionName: "contains(element)",
    description:
      "The filter contains(b) will produce true if b is completely contained within the input. A string B is contained in a string A if B is a substring of A. An array B is contained in an array A if all elements in B are contained in any element in A. An object B is contained in object A if all of the values in B are contained in the value in A with the same key. All other types are assumed to be contained in each other if they are equal.",
  },
  {
    functionName: "indices(s)",
    description:
      "Outputs an array containing the indices in . where s occurs. The input may be an array, in which case if s is an array then the indices output will be those where all elements in . match those of s.",
  },
  {
    functionName: "index(s)",
    description: "Outputs the index of the first (index) or last (rindex) occurrence of s in the input.",
  },
  {
    functionName: "rindex(s)",
    description: "Outputs the index of the first (index) or last (rindex) occurrence of s in the input.",
  },
  {
    functionName: "inside",
    description:
      "The filter inside(b) will produce true if the input is completely contained within b. It is, essentially, an inversed version of contains.",
  },
  { functionName: "startswith(str)", description: "Outputs true if . starts with the given string argument." },
  { functionName: "endswith(str)", description: "Outputs true if . ends with the given string argument." },
  {
    functionName: "combinations",
    description:
      "Outputs all combinations of the elements of the arrays in the input array. If given an argument n, it outputs all combinations of n repetitions of the input array.",
  },
  {
    functionName: "combinations(n)",
    description:
      "Outputs all combinations of the elements of the arrays in the input array. If given an argument n, it outputs all combinations of n repetitions of the input array.",
  },
  {
    functionName: "ltrimstr(str)",
    description: "Outputs its input with the given prefix string removed, if it starts with it.",
  },
  {
    functionName: "rtrimstr(str)",
    description: "Outputs its input with the given suffix string removed, if it ends with it.",
  },
  { functionName: "explode", description: "Converts an input string into an array of the strings codepoint numbers." },
  { functionName: "implode", description: "The inverse of explode." },
  { functionName: "split(str)", description: "Splits an input string on the separator argument." },
  {
    functionName: "join(str)",
    description:
      'Joins the array of elements given as input, using the argument as separator. It is the inverse of split: that is, running split("foo") | join("foo") over any input string returns said input string.',
  },
  {
    functionName: "ascii_downcase",
    description:
      "Emit a copy of the input string with its alphabetic characters (a-z and A-Z) converted to the specified case.",
  },
  {
    functionName: "ascii_upcase",
    description:
      "Emit a copy of the input string with its alphabetic characters (a-z and A-Z) converted to the specified case.",
  },
  {
    functionName: "while(cond; update)",
    description: "The while(cond; update) function allows you to repeatedly apply an update to . until cond is false.",
  },
  {
    functionName: "until(cond; next)",
    description:
      "The until(cond; next) function allows you to repeatedly apply the expression next, initially to . then to its own output, until cond is true. For example, this can be used to implement a factorial function (see below).",
  },
  {
    functionName: "recurse(f)",
    description:
      "The recurse(f) function allows you to search through a recursive structure, and extract interesting data from all levels. Suppose your input represents a filesystem:",
  },
  { functionName: "recurse", description: "When called without an argument, recurse is equivalent to recurse(.[]?)." },
  {
    functionName: "recurse(f; condition)",
    description:
      "recurse(f; condition) is a generator which begins by emitting . and then emits in turn .|f, .|f|f, .|f|f|f, ... so long as the computed value satisfies the condition. For example, to generate all the integers, at least in principle, one could write recurse(.+1; true).",
  },
  {
    functionName: "recurse_down",
    description:
      "For legacy reasons, recurse_down exists as an alias to calling recurse without arguments. This alias is considered deprecated and will be removed in the next major release.",
  },
  {
    functionName: "walk(f)",
    description:
      "The walk(f) function applies f recursively to every component of the input entity. When an array is encountered, f is first applied to its elements and then to the array itself; when an object is encountered, f is first applied to all the values and then to the object. In practice, f will usually test the type of its input, as illustrated in the following examples. The first example highlights the usefulness of processing the elements of an array of arrays before processing the array itself. The second example shows how all the keys of all the objects within the input can be considered for alteration.",
  },
  { functionName: "env", description: "env outputs an object representing jqs current environment." },
  {
    functionName: "transpose",
    description:
      "Transpose a possibly jagged matrix (an array of arrays). Rows are padded with nulls so the result is always rectangular.",
  },
  {
    functionName: "bsearch(x)",
    description:
      "bsearch(x) conducts a binary search for x in the input array. If the input is sorted and contains x, then bsearch(x) will return its index in the array; otherwise, if the array is sorted, it will return (-1 - ix) where ix is an insertion point such that the array would still be sorted after the insertion of x at ix. If the array is not sorted, bsearch(x) will return an integer that is probably of no interest.",
  },
  {
    functionName: "try",
    description:
      "Errors can be caught by using try EXP catch EXP. The first expression is executed, and if it fails then the second is executed with the error message. The output of the handler, if any, is output as if it had been the output of the expression to try.",
  },
  {
    functionName: "catch",
    description:
      "Errors can be caught by using try EXP catch EXP. The first expression is executed, and if it fails then the second is executed with the error message. The output of the handler, if any, is output as if it had been the output of the expression to try.",
  },
  {
    functionName: "if",
    description:
      "if A then B else C end will act the same as B if A produces a value other than false or null, but act the same as C otherwise.",
  },
  {
    functionName: "then",
    description:
      "if A then B else C end will act the same as B if A produces a value other than false or null, but act the same as C otherwise.",
  },
  {
    functionName: "else",
    description:
      "if A then B else C end will act the same as B if A produces a value other than false or null, but act the same as C otherwise.",
  },
  { functionName: "end", description: "end the if-else" },
  {
    functionName: "not",
    description:
      "not is in fact a builtin function rather than an operator, so it is called as a filter to which things can be piped rather than with special syntax, as in .foo and .bar | not.",
  },
  { functionName: "break", description: "break loop" },
  {
    functionName: "test(val)",
    description:
      "Like match, but does not return match objects, only true or false for whether or not the regex matches the input.",
  },
  {
    functionName: "test(regex; flags)",
    description:
      "Like match, but does not return match objects, only true or false for whether or not the regex matches the input.",
  },
  { functionName: "match(val)", description: "match outputs an object for each match it finds. " },
  { functionName: "match(regex; flags)", description: "match outputs an object for each match it finds. " },
  {
    functionName: "capture(val)",
    description:
      "Collects the named captures in a JSON object, with the name of each capture as the key, and the matched string as the corresponding value.",
  },
  {
    functionName: "capture(regex; flags)",
    description:
      "Collects the named captures in a JSON object, with the name of each capture as the key, and the matched string as the corresponding value.",
  },
  {
    functionName: "scan(regex)",
    description:
      "Emit a stream of the non-overlapping substrings of the input that match the regex in accordance with the flags, if any have been specified. If there is no match, the stream is empty. To capture all the matches for each input string, use the idiom [ expr ], e.g. [ scan(regex) ].",
  },
  {
    functionName: "scan(regex; flags)",
    description:
      "Emit a stream of the non-overlapping substrings of the input that match the regex in accordance with the flags, if any have been specified. If there is no match, the stream is empty. To capture all the matches for each input string, use the idiom [ expr ], e.g. [ scan(regex) ].",
  },
  {
    functionName: "split(regex; flags)",
    description: "For backwards compatibility, split splits on a string, not a regex.",
  },
  {
    functionName: "splits(regex)",
    description: "These provide the same results as their split counterparts, but as a stream instead of an array.",
  },
  {
    functionName: "splits(regex; flags)",
    description: "These provide the same results as their split counterparts, but as a stream instead of an array.",
  },
  {
    functionName: "sub(regex; tostring)",
    description:
      'Emit the string obtained by replacing the first match of regex in the input string with tostring, after interpolation. tostring should be a jq string, and may contain references to named captures. The named captures are, in effect, presented as a JSON object (as constructed by capture) to tostring, so a reference to a captured variable named "x" would take the form: "(.x)".',
  },
  {
    functionName: "sub(regex; string; flags)",
    description:
      'Emit the string obtained by replacing the first match of regex in the input string with tostring, after interpolation. tostring should be a jq string, and may contain references to named captures. The named captures are, in effect, presented as a JSON object (as constructed by capture) to tostring, so a reference to a captured variable named "x" would take the form: "(.x)".',
  },
  {
    functionName: "gsub(regex; string)",
    description:
      "gsub is like sub but all the non-overlapping occurrences of the regex are replaced by the string, after interpolation.",
  },
  {
    functionName: "gsub(regex; string; flags)",
    description:
      "gsub is like sub but all the non-overlapping occurrences of the regex are replaced by the string, after interpolation.",
  },
  { functionName: "def", description: "" },
  { functionName: "reduce", description: "" },
  { functionName: "isempty(exp)", description: "Returns true if exp produces no outputs, false otherwise." },
  { functionName: "limit(n; exp)", description: "The limit function extracts up to n outputs from exp." },
  {
    functionName: "first(expr)",
    description: "The first(expr) and last(expr) functions extract the first and last values from expr, respectively.",
  },
  {
    functionName: "last(expr)",
    description: "The first(expr) and last(expr) functions extract the first and last values from expr, respectively.",
  },
  {
    functionName: "nth(n; expr)",
    description:
      "The nth(n; expr) function extracts the nth value output by expr. This can be defined as def nth(n; expr): last(limit(n + 1; expr));. Note that nth(n; expr) doesnt support negative values of n.",
  },
  {
    functionName: "first",
    description: "The first and last functions extract the first and last values from any array at ..",
  },
  {
    functionName: "last",
    description: "The first and last functions extract the first and last values from any array at ..",
  },
  { functionName: "nth(n)", description: "The nth(n) function extracts the nth value of any array at .." },
  {
    functionName: "foreach",
    description:
      "The foreach syntax is similar to reduce, but intended to allow the construction of limit and reducers that produce intermediate results (see example).",
  },
  { functionName: "while", description: "a while loop" },
  {
    functionName: "truncate_stream(stream_expression)",
    description:
      "Consumes a number as input and truncates the corresponding number of path elements from the left of the outputs of the given streaming expression.",
  },
  {
    functionName: "fromstream(stream_expression)",
    description: "Outputs values corresponding to the stream expressions outputs.",
  },
  { functionName: "tostream", description: "The tostream builtin outputs the streamed form of its input." },
  { functionName: "input", description: "Outputs one new input." },
  { functionName: "inputs", description: "Outputs all remaining inputs, one by one." },
  {
    functionName: "debug",
    description:
      'Causes a debug message based on the input value to be produced. The jq executable wraps the input value with ["DEBUG:", <input-value>] and prints that and a newline on stderr, compactly. This may change in the future.',
  },
  {
    functionName: "stderr",
    description:
      "Prints its input in raw and compact mode to stderr with no additional decoration, not even a newline.",
  },
  {
    functionName: "input_filename",
    description:
      "Returns the name of the file whose input is currently being filtered. Note that this will not work well unless jq is running in a UTF-8 locale.",
  },
  { functionName: "input_line_number", description: "Returns the line number of the input currently being filtered." },
];
