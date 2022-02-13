# service-registry
Simple service registry component for microservices.

Service Registry is a bridge component between clients and micro services. Both hosts do not need to know the details about each other.

Services can be registered by their name, version, ip address and port number for a certain period.
Clients can query for services by their name and version.

![](https://i.imgur.com/q6VZch7.jpeg)

When a client asks for a service by it's name and version, service registry returns the service that is least used before compared to all candidates.

At the below example, client wants to get the end-point of the Service 1. It's major version should be 1 and it doesn't matter it's minor version. In this case, there are two candidates whose ids are such as: 1 and 2. Because both services satisfy the name and version criteria and they are used number of equal times before.

![](https://i.imgur.com/jmwdpHp.jpeg)

Let's head over to another example. Client asks for the same service. However, in this case even both services satisfy the name and version criterias, because of the service with id of 2 is used less time before, it will be returned to the client.

![](https://i.imgur.com/V4MMsDa.jpeg)
