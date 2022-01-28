#!/usr/bin/ruby
require 'uri'
require 'net/http'
require 'net/https'
require 'json'

coverage_report = File.read 'build/codecov-report/jacocoTestReport.xml'

match_data = /\<\/package\>\<counter type="INSTRUCTION" missed="(\d+)" covered="(\d+)"/.match coverage_report

missed_instructions = match_data.captures[0].to_f
covered_instructions = match_data.captures[1].to_f

total_coverage = covered_instructions / (covered_instructions + missed_instructions) * 100
puts total_coverage.round(2)
