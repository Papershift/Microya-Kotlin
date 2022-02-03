#!/usr/local/bin/swift-sh
import AnyLint // @Flinesoft ~> 0.8.3

try Lint.logSummaryAndExit(arguments: CommandLine.arguments) {
    // MARK: - Variables
    let kotlinFiles: Regex = #"^app/src/main/kotlin/.*\.kt$"#
    let javaFiles: Regex = #"^app/src/main/java/.*\.java$"#
    let xmlFiles: Regex = #"^app/src/main/res/.*\.xml$"#
    let gradleFiles: Regex = #"^.*\.gradle$"#

    let readmeFile: Regex = #"^README\.md$"#
    let changelogFile: Regex = #"^CHANGELOG\.md$"#
    let stringsFiles: Regex = #"app/src/.*/res/values.*/strings.*\.xml$"#

    // MARK: - Checks
    // MARK: Changelog
    try Lint.checkFilePaths(
        checkInfo: "Changelog: Each project should have a CHANGELOG.md file, tracking the changes within a project over time.",
        regex: changelogFile,
        matchingExamples: ["CHANGELOG.md"],
        nonMatchingExamples: ["CHANGELOG.markdown", "Changelog.md", "ChangeLog.md"],
        violateIfNoMatchesFound: true
    )

    // MARK: ChangelogEntryStructure
    try Lint.checkFileContents(
        checkInfo: "ChangelogEntryStructure: Changelog entries should have exactly the structure (including whitespaces) like documented in top of the file.",
        regex: #"^[-–]\s*([^\s][^\n]+[^\s])\s*\n\s*((?:Task|Issue|PR|Author)\S.*\))[^)\n]*\n"#,
        matchingExamples: [
            "- Summary.  \nTask: [1234](https://app.asana.com/0/1169100614907018/1166652133391234/f) | PR: [#100](https://github.com/Papershift/Station-Android/pull/100) | Author: [Cihat Gündüz](https://github.com/Jeehut)\n",
            "- Summary.  \n    Task: [1234](https://app.asana.com/0/1169100614907018/1166652133394321/f) | PR: [#100](https://github.com/Papershift/Station-Android/pull/100) | Author: [Cihat Gündüz](https://github.com/Jeehut)  ,  \n",
        ],
        nonMatchingExamples: ["- None.\n##"],
        includeFilters: [changelogFile],
        autoCorrectReplacement: "- $1  \n  $2\n",
        autoCorrectExamples: [
            [
                "before": "- Summary. \nTask: [1234](https://app.asana.com/0/1169100614907018/1166652133391234/f) | PR: [#100](https://github.com/Papershift/Station-Android/pull/100) | Author: [Cihat Gündüz](https://github.com/Jeehut).  \n",
                "after": "- Summary.  \n  Task: [1234](https://app.asana.com/0/1169100614907018/1166652133391234/f) | PR: [#100](https://github.com/Papershift/Station-Android/pull/100) | Author: [Cihat Gündüz](https://github.com/Jeehut)\n"
            ],
        ]
    )

    // MARK: CommentTypeNote
    try Lint.checkFileContents(
        checkInfo: "CommentTypeNote: Use a '// NOTE: ' comment instead.",
        regex: #"// *(WORKAROUND|HACK|WARNING):?\s*"#,
        matchingExamples: ["// WORKAROUND: this is ugly", "// HACK: see link"],
        nonMatchingExamples: ["// NOTE: this is an ugly workaround", "// NOTE: see link"],
        includeFilters: [kotlinFiles, javaFiles, gradleFiles],
        autoCorrectReplacement: #"// NOTE: "#,
        autoCorrectExamples: [
            ["before": "//WORKAROUND this is ugly", "after": "// NOTE: this is ugly"],
            ["before": "// HACK: see link", "after": "// NOTE: see link"],
        ]
    )

    // MARK: CommentTypeTodo
    try Lint.checkFileContents(
        checkInfo: "CommentTypeTodo: Use a '// TODO: ' comment instead.",
        regex: #"// *(?:BUG|MOCK|FIXME|RELEASE|TEST):?\s*"#,
        matchingExamples: ["// BUG: fix it", "// FIXME: no time"],
        nonMatchingExamples: ["// TODO: fix it", "// TODO: no time"],
        includeFilters: [kotlinFiles, javaFiles, gradleFiles],
        autoCorrectReplacement: #"// TODO: "#,
        autoCorrectExamples: [
            ["before": "//BUG fix it", "after": "// TODO: fix it"],
            ["before": "// FIXME: no time", "after": "// TODO: no time"],
        ]
    )

    // MARK: ListWhitespace
    try Lint.checkFileContents(
        checkInfo: "ListWhitespace: List entries should be separated by a single whitespaces around commas.",
        regex: #"\[([^\]]+\S)\s*,(?!"\n)(\S[^\]]+)\]"#,
        includeFilters: [kotlinFiles, javaFiles, xmlFiles, gradleFiles],
        autoCorrectReplacement: "[$1, $2]",
        autoCorrectExamples: [
            [
                "before": "val devices: [Device] = [\n    .iPhone5s, .iPhone6,.iPhone6Plus, .iPhone4, .iPhone4s,.iPhone5\n]",
                "after": "val devices: [Device] = [\n    .iPhone5s, .iPhone6,.iPhone6Plus, .iPhone4, .iPhone4s, .iPhone5\n]"
            ],
            [
                "before": "var devices: [Device] = [\n    .iPhone5s, .iPhone6 ,.iPhone6Plus, .iPhone4, .iPhone4s, .iPhone5\n]",
                "after": "var devices: [Device] = [\n    .iPhone5s, .iPhone6, .iPhone6Plus, .iPhone4, .iPhone4s, .iPhone5\n]"
            ],
        ],
        repeatIfAutoCorrected: true
    )

    // MARK: LongNumberSeparators
    try Lint.checkFileContents(
        checkInfo: "LongNumberSeparators: Long number literals like `50000` should use an underscore separator like in `50_000`.",
        regex: #"(?<!\w|/|© |\.)(\d+)(\d{3})([\s,:;)\]}_])"#,
        matchingExamples: ["= 50000\n", "(x: 123456789)"],
        nonMatchingExamples: [
            "= 50_000\n",
            "(x: 123_456_789)",
            #""0000""#,
            #""abc1234def""#,
            "https://github.com/link/123456",
            "// Copyright © 2019 Flinesoft. All rights reserved.",
            "@interface NSDate (ISO8_601)",
        ],
        includeFilters: [kotlinFiles, javaFiles, gradleFiles],
        autoCorrectReplacement: "$1_$2$3",
        autoCorrectExamples: [
            ["before": "= 50000;", "after": "= 50_000;"],
            ["before": "(x: 123456_789)", "after": "(x: 123_456_789)"],
        ],
        repeatIfAutoCorrected: true
    )

    // MARK: MultilineWhitespaces
    try Lint.checkFileContents(
        checkInfo: "MultilineWhitespaces: Restrict whitespace lines to a maximum of one.",
        regex: #"\n( *\n){2,}"#,
        matchingExamples: ["}\n    \n     \n\nclass", "}\n\n\nvoid"],
        nonMatchingExamples: ["}\n    \n    class"],
        includeFilters: [kotlinFiles, javaFiles, xmlFiles, gradleFiles],
        autoCorrectReplacement: "\n\n",
        autoCorrectExamples: [
            ["before": "}\n    \n     \n\n    class", "after": "}\n\n    class"],
            ["before": "}\n\n\nvoid", "after": "}\n\nvoid"],
        ]
    )

    // MARK: Readme
    try Lint.checkFilePaths(
        checkInfo: "Readme: Each project should have a README.md file, explaining how to use or contribute to the project.",
        regex: readmeFile,
        matchingExamples: ["README.md"],
        nonMatchingExamples: ["README.markdown", "Readme.md", "ReadMe.md"],
        violateIfNoMatchesFound: true
    )

    // MARK: StringsLeadingWhitespaces
    try Lint.checkFileContents(
        checkInfo: "StringsLeadingWhitespaces: `strings.xml` file entries should not start with a whitespace.",
        regex: #"(<(?:string|item)[^>]*>)( +)([^ ].*)"#,
        matchingExamples: [#"<string name="welcome"> Welcome ..."#, #"<item> "Vielen Dank!"</item>"#],
        nonMatchingExamples: [#"<string name="email">E-Mail: </string>"#, #"<string name="welcome">Welcome ..."#, #"<item>"Vielen Dank!" </item>"#],
        includeFilters: [stringsFiles],
        autoCorrectReplacement: "$1$3",
        autoCorrectExamples: [
            ["before": #"<string name="welcome"> Welcome ..."#, "after": #"<string name="welcome">Welcome ..."#],
            ["before": #"<string name="welcome">    Welcome ..."#, "after": #"<string name="welcome">Welcome ..."#],
            ["before": #"<item> "Vielen Dank!"</item>"#, "after": #"<item>"Vielen Dank!"</item>"#],
        ]
    )

    // MARK: SingleQuoteStringsGradle
    try Lint.checkFileContents(
        checkInfo: "SingleQuoteStringsGradle: Non-interpolated Strings should use single quotes to improve build performance.",
        regex: #""([^$\s,"]+)""#,
        matchingExamples: [
            #"version_detekt = "1.6.0""#,
            #"id "io.gitlab.arturbosch.detekt" version "$version_detekt""#,
            #"apply from: file("detekt.gradle")"#,
        ],
        nonMatchingExamples: [
            "version_detekt = '1.6.0'",
            #"classpath "com.android.tools.build:gradle:$version_gradle""#,
            #"version "$version_detekt""#,
            #"input = files("$projectDir/app/src/main/kotlin", "$projectDir/app/src/main/java")"#,
        ],
        includeFilters: [gradleFiles],
        autoCorrectReplacement: "'$1'",
        autoCorrectExamples: [
            ["before": #"version_detekt = "1.6.0""#, "after": "version_detekt = '1.6.0'"],
            ["before": #"id "io.gitlab.arturbosch.detekt" version "$version_detekt""#, "after": #"id 'io.gitlab.arturbosch.detekt' version "$version_detekt""#],
            ["before": #"apply from: file("detekt.gradle")"#, "after": "apply from: file('detekt.gradle')"]
        ]
    )

    // MARK: StringsTrailingWhitespaces
    try Lint.checkFileContents(
        checkInfo: "StringsTrailingWhitespaces: `strings.xml` file entries should not end with a whitespace.",
        regex: #"(<(?:string|item)[^>]*>.*[^ ])( +)(</(?:string|item)>)"#,
        matchingExamples: [#"<string name="email">E-Mail: </string>"#, #"<item>"Vielen Dank!" </item>"#],
        nonMatchingExamples: [#"<string name="email">E-Mail:</string>"#, #"<string name="welcome"> Welcome ..."#, #"<item> "Vielen Dank!"</item>"#],
        includeFilters: [stringsFiles],
        autoCorrectReplacement: "$1$3",
        autoCorrectExamples: [
            ["before": #"<string name="email">E-Mail: </string>"#, "after": #"<string name="email">E-Mail:</string>"#],
            ["before": #"<string name="email">E-Mail:    </string>"#, "after": #"<string name="email">E-Mail:</string>"#],
            ["before": #"<item>"Vielen Dank!" </item>"#, "after": #"<item>"Vielen Dank!"</item>"#],
        ]
    )

    // MARK: TodoFormat
    try Lint.checkFileContents(
        checkInfo: "TodoFormat: All TODOs should have a format with creator credentials & date of their creation documented like this: `// TODO: [cg_YYYY-MM-DD] `.",
        regex: #"// TODO: [^\n]{0,14}\n|// TODO: \[\S{1,12}\]|// TODO: [^\[]|// TODO: \[.{13}[^\]]|// TODO: \[[^a-z]{2}|// TODO: \[.{2}[^_]|// TODO: \[.{7}[^-]|// TODO: \[.{10}[^-]"#,
        matchingExamples: ["// TODO: implement", "// TODO: [2020-02-24] implement", "// TODO: [cg_2020-2-24] implement"],
        nonMatchingExamples: ["// TODO: [cg_2020-02-24] implement"],
        includeFilters: [kotlinFiles, javaFiles, gradleFiles]
    )

    // MARK: TodoUppercase
    try Lint.checkFileContents(
        checkInfo: "TodoUppercase: All TODOs should be all-uppercased like this: `// TODO: [cg_YYYY-MM-DD] `.",
        regex: #"// ?(tODO|ToDO|TOdO|TODo|todo|Todo|ToDo|toDo)"#,
        matchingExamples: ["// todo: ", "// toDo: ", "// Todo: ", "// ToDo: ", "//todo: "],
        nonMatchingExamples: ["// TODO: ", "//TODO: "],
        includeFilters: [kotlinFiles, javaFiles, gradleFiles],
        autoCorrectReplacement: #"// TODO"#,
        autoCorrectExamples: [
            ["before": "// todo: ", "after": "// TODO: "],
            ["before": "// toDo: ", "after": "// TODO: "],
            ["before": "// Todo: ", "after": "// TODO: "],
            ["before": "// ToDo: ", "after": "// TODO: "],
            ["before": "//todo: ", "after": "// TODO: "],
        ]
    )

    // MARK: TodoWhitespacing
    try Lint.checkFileContents(
        checkInfo: "TodoWhitespacing: All TODOs should exactly start like this (mind the whitespacing): `// TODO: `.",
        regex: #"//TODO: *|// TODO:(?=[^ ])|// TODO: {2,}|// {2,}TODO: *|// TODO +|// TODO *(?=\n)"#,
        matchingExamples: ["//TODO: foo", "// TODO foo", "// TODO:foo", "// TODO:   foo", "{\n    // TODO\n}   "],
        nonMatchingExamples: ["// TODO: foo", "// TODO: [cg_2020-02-24] foo"],
        includeFilters: [kotlinFiles, javaFiles, gradleFiles],
        autoCorrectReplacement: #"// TODO: "#,
        autoCorrectExamples: [
            ["before": "//TODO: foo", "after": "// TODO: foo"],
            ["before": "// TODO foo", "after": "// TODO: foo"],
            ["before": "// TODO:foo", "after": "// TODO: foo"],
            ["before": "// TODO:   foo", "after": "// TODO: foo"],
            ["before": "{\n    // TODO\n}   ", "after": "{\n    // TODO: \n}   "]
        ]
    )

    // MARK: TrailingWhitespaces
    try Lint.checkFileContents(
        checkInfo: "TrailingWhitespaces: There should be no trailing whitespaces in lines.",
        regex: #"([\S\n]) +\n"#,
        matchingExamples: ["}  \n", "\n    \n"],
        nonMatchingExamples: ["}\n    void"],
        includeFilters: [kotlinFiles, javaFiles, xmlFiles, gradleFiles],
        autoCorrectReplacement: "$1\n",
        autoCorrectExamples: [
            ["before": "}  \n", "after": "}\n"],
            ["before": "\n    \n", "after": "\n\n"],
        ]
    )

    // MARK: WhitespaceAfterCommentStart
    try Lint.checkFileContents(
        checkInfo: "WhitespaceAfterCommentStart: A comment should always start with at least one whitespace.",
        regex: #"([^:/])//([^\s/])"#,
        matchingExamples: [" //foo", "} //foo"],
        nonMatchingExamples: [" //  foo", "]  //    foo", " // foo", "} // foo", "]  // foo", #"URL(string: "https://flinesoft.com")"#, "\n    \n/// Returns true if"],
        includeFilters: [kotlinFiles, javaFiles, gradleFiles],
        autoCorrectReplacement: "$1// $2",
        autoCorrectExamples: [
            ["before": "\n  //foo", "after": "\n  // foo"],
            ["before": "} //foo", "after": "} // foo"],
        ]
    )

    // MARK: WhitespaceBeforeComment
    try Lint.checkFileContents(
        checkInfo: "WhitespaceBeforeComment: A comment should always be preceded by a single whitespace if on a code line.",
        regex: #"([^:\s/])(?: {0}| {2,})//"#,
        matchingExamples: ["val x = 5// foo", "}//foo", "]  //    foo", "]/// foo"],
        nonMatchingExamples: ["val x = 5 // foo", "} //foo", "] //   foo", "\n  /// foo", #"URL(string: "https://flinesoft.com")"#],
        includeFilters: [kotlinFiles, javaFiles, gradleFiles],
        autoCorrectReplacement: "$1 //",
        autoCorrectExamples: [
            ["before": "val x = 5// foo", "after": "val x = 5 // foo"],
            ["before": "}//foo", "after": "} //foo"],
            ["before": "]  //    foo", "after": "] //    foo"],
        ]
    )

    // MARK: WhitespacesOverTabs
    try Lint.checkFileContents(
        checkInfo: "WhitespacesOverTabs: Use four whitespaces instead of tabs.",
        regex: #"\t"#,
        matchingExamples: ["\n\t\t/// comment", "\n\tclass"],
        nonMatchingExamples: ["\n        /// comment", "\n    class"],
        includeFilters: [kotlinFiles, javaFiles, xmlFiles, gradleFiles],
        autoCorrectReplacement: "    ",
        autoCorrectExamples: [
            ["before": "\n\t\t/// comment", "after": "\n        /// comment"],
            ["before": "\n\tclass", "after": "\n    class"],
        ]
    )
}
