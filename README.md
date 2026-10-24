# Number Range Summarizer

Turns a comma-delimited list of integers into a summary where sequential numbers are grouped into ranges.

```
Input:  "1,3,6,7,8,12,13,14,15,21,22,23,24,31"
Output: "1, 3, 6-8, 12-15, 21-24, 31"
```

## Design

| Class | Responsibility |
|---|---|
| `NumberRangeSummarizer` | Provided interface |
| `NumberRangeSummarizerImpl` | Parses input (`collect`) and builds the summary (`summarizeCollection`) |
| `NumberRange` | Immutable value object for an inclusive range and its string form |

## Assumptions
(Each is expressed as a unit test, marked `Assumption:` in its display name.)

- Whitespace around numbers is ignored.
- Null, empty or blank input contains no numbers.
- Non-numeric, empty (e.g. trailing comma) or out-of-`int`-range tokens throw `IllegalArgumentException`.
- Duplicates are collapsed; output is in ascending order.
- Two or more consecutive numbers form a range (`1,2` becomes `1-2`).
- Negative numbers are supported.
- A null collection summarizes to `""`; a collection containing null throws `IllegalArgumentException`.

## Build and test

Requires Java 8+ and Maven.

```
mvn test
```
