// Check HTTP respons
#include <WiFi.h>
#include <HTTPClient.h>
#include "DHT.h"

// Define DHT sensor settings
#define DHTPIN 5         // DHT11 data pin connected to GPIO 5 (change if needed)
#define DHTTYPE DHT11    // Sensor type is DHT11
DHT dht(DHTPIN, DHTTYPE);

// Replace these with your actual WiFi credentials and ThingSpeak API Key
const char* ssid = "realme 8";       // Replace with your WiFi SSID
const char* password = "12345678"; // Replace with your WiFi password
const char* serverName = "http://api.thingspeak.com/update"; // ThingSpeak server URL
String apiKey = "POS6MUMONJEQ8Q5F";      // Replace with your ThingSpeak Write API Key

void setup() {
  Serial.begin(115200);
  WiFi.begin(ssid, password);
  dht.begin();
  
  Serial.println("Connecting to WiFi");

  // WiFi connection timeout
  unsigned long startAttemptTime = millis();
  while (WiFi.status() != WL_CONNECTED) {
    if (millis() - startAttemptTime >= 15000) { // Timeout after 15 seconds
      Serial.println("Failed to connect to WiFi");
      return; // Exit setup if WiFi connection fails
    }
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.print("Connected to WiFi network with IP Address: ");
  Serial.println(WiFi.localIP());
}

void loop() {
  if (WiFi.status() == WL_CONNECTED) {
    WiFiClient client;
    HTTPClient http;
    
    delay(15000); // Wait for 15 seconds before sending the next data update

    float t = dht.readTemperature(); // Read temperature
    if (isnan(t)) {
      Serial.println(F("Failed to read from DHT sensor!"));
      return;
    }

    // Send data to ThingSpeak (Temperature only, can add humidity if needed)
    http.begin(client, serverName);
    http.addHeader("Content-Type", "application/x-www-form-urlencoded");

    // Update fields (temperature in field1)
    String httpRequestData = "api_key=" + apiKey + "&field1=" + String(t);           
    int httpResponseCode = http.POST(httpRequestData);

    // Print the response code for debugging
    Serial.print("HTTP Response code: ");
    Serial.println(httpResponseCode);

    http.end(); // End the HTTP connection
  } else {
    Serial.println("WiFi Disconnected");
  }
}