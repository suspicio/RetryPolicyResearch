# Welcome to Retry Policy Research repository. 

## Repository divided to the 3 parts. <br>
### [master](https://github.com/suspicio/RetryPolicyResearch): main up to date code <br>
### [requester](https://github.com/suspicio/RetryPolicyResearch/tree/requester): the client for creating the requests and retry requests
### [local](https://github.com/suspicio/RetryPolicyResearch/tree/local): first version of application with self-running producer and consumer <br>
### [master-front](https://github.com/suspicio/RetryPolicyResearch/tree/master-front): client application with front-end part <br>

# Application configuration guideline<br>
## Docker run
To run the application first is required to configure docker. <br>

1. Download official [cassandra image](https://hub.docker.com/_/cassandra)<br>

2. Create docker network with name `cassandra-network`

3. Run following commands<br><br>
3.1. `docker run --name cassandra_node1 --net cassandra-network -d -e CASSANDRA_CLUSTER_NAME="retrycluster" -e CASSANDRA_SEEDS="cassandra_node1, cassandra_node2, cassandra_node3" -p 9042:9042 cassandra:latest`<br><br>
3.2. `docker run --name cassandra_node2 --net cassandra-network -d -e CASSANDRA_CLUSTER_NAME="retrycluster" -e CASSANDRA_SEEDS="cassandra_node1, cassandra_node2, cassandra_node3" -p 7000:7000 cassandra:latest`<br><br>
3.3. `docker run --name cassandra_node3 --net cassandra-network -d -e CASSANDRA_CLUSTER_NAME="retrycluster" -e CASSANDRA_SEEDS="cassandra_node1, cassandra_node2, cassandra_node3" -p 7001:7001 cassandra:latest`<br><br>

4. To check if everything configured correctly<br><br>
4.1. The desktop docker should show following status<br>
![docker status](https://user-images.githubusercontent.com/74540366/214795101-82b26eb0-2334-4499-841e-d4bce40c4dc8.png)<br><br>
4.2 Then check topology to the image with `docker exec -it cassandra_node1 nodetool status`<br>
There should similar to next image status<br>
![nodetool status](https://user-images.githubusercontent.com/74540366/214797160-4e273315-ff03-4849-8b00-00d1bd2443dd.png)
<br><br>
## Backend run<br>
To run backend application you need to download [local](https://github.com/suspicio/RetryPolicyResearch/tree/local) or [master](https://github.com/suspicio/RetryPolicyResearch) with [requester](https://github.com/suspicio/RetryPolicyResearch/tree/requester) repository then download graddle libraries.<br>

To run it [java 17+](https://www.oracle.com/java/technologies/javase/jdk15-archive-downloads.html) version is required.<br>

To run application configure IntelliJ IDEA with following commands:<br>

![Spring Boot Config](https://user-images.githubusercontent.com/74540366/214797018-c6f2ba65-16ec-46d4-afa5-ec26dcd32ebe.png)

## Frontend run<br>
To run frontend application you need to download [master-front](https://github.com/suspicio/RetryPolicyResearch/tree/master-front) repository.<br>

[npm and nodejs(16+)](https://nodejs.org/dist/v16.16.0/) is required.<br>

After cloning repository run `npm i` command to install all libraries and dependencies.<br>

To start the frontend application run `npm run serve` command.<br>

If similar picture appears everything is configured successfully.<br>

![image](https://user-images.githubusercontent.com/74540366/214799322-7526cb48-33dc-4a8e-8a73-0a3b303e7035.png)

