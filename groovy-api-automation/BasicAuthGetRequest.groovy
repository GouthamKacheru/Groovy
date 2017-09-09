// Basic Authentication GET Request Script


import com.eviware.soapui.support.XmlHolder
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

// Define your API endpoint and credentials
def apiUrl = "https://www.googleapis.com/token"
def username = "your-username"
def password = "your-password"

log.info "Starting Basic Auth GET Request"

try {
    // Create HTTP client
    def http = new URL(apiUrl).openConnection() as HttpURLConnection
    
    // Set request method
    http.setRequestMethod("GET")
    
    // Add Basic Authentication Header
    def basicAuth = "$username:$password".getBytes().encodeBase64().toString()
    http.setRequestProperty("Authorization", "Basic $basicAuth")
    http.setRequestProperty("Content-Type", "application/json")
    http.setRequestProperty("Accept", "application/json")
    
    log.info "Request URL: $apiUrl"
    log.info "Authorization header set with Basic Auth"
    
    // Send request and get response
    def responseCode = http.getResponseCode()
    def response = http.getInputStream().getText()
    
    log.info "Response Code: $responseCode"
    log.info "Response Body: $response"
    
    // Store auth credentials in context for later use
    testRunner.testCase.testSuite.project.testSuite("API Tests").setPropertyValue("authToken", "Basic $basicAuth")
    testRunner.testCase.testSuite.project.testSuite("API Tests").setPropertyValue("authUsername", username)
    testRunner.testCase.testSuite.project.testSuite("API Tests").setPropertyValue("authPassword", password)
    testRunner.testCase.testSuite.project.testSuite("API Tests").setPropertyValue("apiResponse", response)
    
    log.info "Authentication credentials stored for forward chaining"
    
    http.disconnect()
    
} catch (Exception e) {
    log.error "Error during authentication request: ${e.message}"
    testRunner.fail("Authentication request failed: ${e.message}")
}