# Lint commits (see https://github.com/jonallured/danger-commit_lint)
commit_lint.check

# Check if Design Review is needed for this PR
if git.modified_files.include? "app/screenshots/**/*.png"
  warn('Design Review is needed for this PR (screenshot references changed)')
end

# Run Detekt (see https://github.com/NFesquet/danger-kotlin_detekt)
kotlin_detekt.gradle_task = 'detekt'
kotlin_detekt.report_file = 'build/reports/detekt/detekt.xml'
kotlin_detekt.detekt

# Run Android Lint (see https://github.com/loadsmart/danger-android_lint)
android_lint.gradle_task = 'lintDebug'
android_lint.report_file = 'microya/build/reports/lint-results-debug.xml'
android_lint.lint
