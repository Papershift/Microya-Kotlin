# Check Code Coverage (see https://github.com/Malinskiy/danger-jacoco)
jacoco.minimum_project_coverage_percentage = 50
jacoco.files_extension = ['.kt']
jacoco.report('build/codecov-report/jacocoTestReport.xml')

message("Download the full coverage report [here](https://github.com/Papershift/Microya-Kotlin/actions/runs/#{ENV['RUN_ID']}) in the 'Artifacts' section.")
