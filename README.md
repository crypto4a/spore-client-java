###### TODO
- fix b64 encoding (need to use URL safe)
- Why are claims not matching?

# spore-client-java
Spore client in Java (duh...)

### External Libraries
- [JSON in Java](https://mvnrepository.com/artifact/org.json/json/20140107) 

### Spore
Add link to RFC / Proposal

## Usage
The client offers a simple API based on method calls: each possible Spore request is executed through a method call which returns a JSON object containing the content of the response.

-----
##### POST /getInfo
Retrieves the server's information.

###### Reqest parameters
None

###### Response properties
| Property | Type | Description |
|-|-|-|
|name|String|Descriptive name of the server.|
|entropySize|int|Size in bytes of the entropy served by this server|

###### Client Method Call
```JAVA
JSONObject response = sporeClient.doInfoRequest();
```

###### Sample Response
```
{
    "entropySize": 32,
    "name": "C4A EaaSP Servlet"
}
```

-----
##### POST /getCertChain
Retrieves the server's certificate chain.

###### Reqest parameters
None

###### Response properties
| Property | Type | Description |
|-|-|-|
|certificateChain|String|Certificate chain formated as per RFC 7468.|
|JWT|String|JWT containing the above property and signed by the server.|

###### Client Method Call
```JAVA
JSONObject response = sporeClient.doCertificateChainRequest();
```

###### Sample Response
```
{
    "certificateChain": "-----BEGIN CERTIFICATE-----\nMIIEQD [...]",
    "JWT": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJjZXJ0aWZ [...]"
}
```

-----
##### POST /getEntropy
Retrieve entropy from the server.

###### Reqest parameters
| Parameter | Type | Description |
|-|-|-|
|challenge|String|A challenge that will be included in the server's reponse to insure it was not tempered with. (Usually base 64 encoded random bytes)

###### Response properties
| Property | Type | Description |
|-|-|-|
|entropy|String|Base 64 encoded random bytes.|
|challenge|String|The challenge sent by the client.|
|timestamp|long|Server Unix epoch time at which the request was servred.|
|JWT|String|JWT containing the above properties and signed by the server.|

###### Client Method Call
```JAVA
JSONObject response = sporeClient.doCertificateChainRequest();
```

###### Sample Response
```
{
    "certificateChain": "-----BEGIN CERTIFICATE-----\nMIIEQD [...]",
    "JWT": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJjZXJ0aWZ [...]"
}
```

## Example Scenarios
### Creating a SporeClient instance and testing the connection
```JAVA
// Instantiating the Spore Client
String serverAddress = "http://127.0.0.41:8099/eaasp/";
SporeClient sporeClient = new SporeClient(serverAddress);

// Performing a info request is a simple way to validate that the connection is working. An exception is thrown at this point if the server can't be reached or if an error is returned.
JSONObject response = sporeClient.doInfoRequest();

// Printing the received information is not necessary.
System.out.println("Server name: " + response.get("name"));
System.out.println("Entropy size: " + response.get("entropySize"));
```

### Seeding a local RNG without verification of the entropy's source
This is the simplest use case, where a client wants to seed its local RNG without caring to autheticate the server, verify the freshness of the response or even verify the challenge.

This could be the approach used by an extremely limited IoT device. In the best case, the received entropy was not compromise and the client is left better off after the operation. In the worst case, the received entropy was compromised and the clients entropy pool is left unchanged.

```JAVA
String response = sporeClient.doEntropyRequest(null);
String entropy = response.getString("entropy");

```






