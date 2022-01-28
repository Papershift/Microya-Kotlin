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
puts "Found total coverage of #{total_coverage}. Updating JSON for updated README badge ..."

color = case total_coverage
when 60...75
  "orange"
when 75...90
  "yellow"
when 90...100
  "green"
else
  "red"
end

uri = URI('https://api.jsonbin.io/b/5f3128666f8e4e3faf2f83ca')
https = Net::HTTP.new(uri.host,uri.port)
https.use_ssl = true

request = Net::HTTP::Put.new(uri, 'Content-Type' => 'application/json', 'secret-key' => ENV['JSONBIN_SECRET_KEY'])
request.body = { schemaVersion: 1, label: 'Coverage', message: "#{total_coverage.round(1)}%", color: color }.to_json

result = https.request(request)
puts "Badge JSON update status: #{result.code}. Body:\n#{result.body}"
