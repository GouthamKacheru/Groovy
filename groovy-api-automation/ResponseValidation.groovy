// 
// Response Validation Script
// 

import groovy.json.JsonSlurper

log.info "Starting Response Validation"

try {
    // Retrieve responses
    def postResponse = testRunner.testCase.testSuite.project.testSuite("API Tests").getPropertyValue("postApiResponse")
    def httpStatusCode = testRunner.testCase.testCase.getPropertyValue("httpStatusCode") as Integer
    
    log.info "Retrieved HTTP Status Code: $httpStatusCode"
    log.info "Retrieved Response: $postResponse"
    
    // Parse JSON response
    def jsonSlurper = new JsonSlurper()
    def parsedResponse = jsonSlurper.parseText(postResponse)
    
    log.info "Parsed JSON Response: $parsedResponse"
    
    //  VALIDATION 1: Check HTTP Status Code 
    if (httpStatusCode == 200) {
        log.info "✓ HTTP Status Code Validation PASSED: Status is 200 OK"
    } else {
        log.error "✗ HTTP Status Code Validation FAILED: Expected 200, Got $httpStatusCode"
        testRunner.fail("HTTP Status validation failed")
    }
    
    //  VALIDATION 2: Check Response Status Field 
    if (parsedResponse.containsKey("status")) {
        def status = parsedResponse.status
        log.info "Response Status Field: $status"
        
        if (status.toString().toLowerCase() == "active") {
            log.info "✓ Status Field Validation PASSED: Status is 'active'"
        } else {
            log.error "✗ Status Field Validation FAILED: Expected 'active', Got '$status'"
            testRunner.fail("Status field validation failed")
        }
    } else {
        log.warn "⚠ Status field not found in response. Available fields: ${parsedResponse.keySet()}"
    }
    
    // VALIDATION 3: Check for Required Fields
    def requiredFields = ["id", "summary", "status"]
    def missingFields = []
    
    requiredFields.each { field ->
        if (!parsedResponse.containsKey(field)) {
            missingFields.add(field)
        }
    }
    
    if (missingFields.isEmpty()) {
        log.info "✓ All Required Fields Validation PASSED"
    } else {
        log.warn "⚠ Missing Fields: $missingFields"
    }
    
    //VALIDATION 4: Detailed Field Validation 
    log.info "=== Detailed Response Fields ==="
    parsedResponse.each { key, value ->
        log.info "$key: $value"
    }
    
    log.info "✓ Response Validation Completed Successfully"
    
} catch (Exception e) {
    log.error "Error during response validation: ${e.message}"
    testRunner.fail("Validation failed: ${e.message}")
}