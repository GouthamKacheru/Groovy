// POST Request with Forwarded Authentication
// For: SoapUI


import com.eviware.soapui.support.XmlHolder
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

// Retrieve stored authentication from previous request
def authHeader = testRunner.testCase.testSuite.project.testSuite("API Tests").getPropertyValue("authToken")
def postUrl = "https://www.googleapis.com/calendar/v3/calendars"

log.info "Starting POST Request with Forwarded Authentication"

try {
    // Prepare request body
    def requestBody = [
        "summary": "New Calendar",
        "description": "Test Calendar Creation",
        "timeZone": "America/New_York"
    ]
    
    def jsonBuilder = new JsonBuilder(requestBody)
    def bodyStr = jsonBuilder.toString()
    
    log.info "Request Body: $bodyStr"
    
    // Create HTTP connection
    def http = new URL(postUrl).openConnection() as HttpURLConnection
    
    // Set request method and properties
    http.setRequestMethod("POST")
    http.setDoOutput(true)
    http.setRequestProperty("Authorization", authHeader)
    http.setRequestProperty("Content-Type", "application/json")
    http.setRequestProperty("Accept", "application/json")
    
    log.info "POST URL: $postUrl"
    log.info "Authorization header: $authHeader (forwarded)"
    
    // Write request body
    def outputStream = http.getOutputStream()
    outputStream.write(bodyStr.getBytes("UTF-8"))
    outputStream.close()
    
    // Get response
    def responseCode = http.getResponseCode()
    def responseMessage = http.getResponseMessage()
    def response = http.getInputStream().getText()
    
    log.info "Response Code: $responseCode"
    log.info "Response Message: $responseMessage"
    log.info "Response Body: $response"
    
    // Store response for validation
    testRunner.testCase.testSuite.project.testSuite("API Tests").setPropertyValue("postApiResponse", response)
    testRunner.testCase.testCase.setPropertyValue("httpStatusCode", responseCode.toString())
    
    http.disconnect()
    
} catch (Exception e) {
    log.error "Error during POST request: ${e.message}"
    testRunner.fail("POST request failed: ${e.message}")
}