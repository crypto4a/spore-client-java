# spore-client-java
Spore client in Java (duh...)

### External Libraries
- [JSON in Java](https://mvnrepository.com/artifact/org.json/json/20140107) 

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