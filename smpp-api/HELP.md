# Read Me First
The following was discovered as part of building this project:

* The original package name 'com.prai.smpp-api' is invalid and this project uses 'com.prai.smpp_api' instead.

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.7/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.5.7/maven-plugin/build-image.html)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.



docker run -d --name activemq -p 61616:61616 -p 8161:8161 -p 61613:61613 -e ARTEMIS_USERNAME=admin -e ARTEMIS_PASSWORD=admin apache/activemq-artemis
docker ps
docker run -d --name activemq \\n  -p 61616:61616 \\n  -p 8161:8161 \\n  -p 61613:61613 \\n  -e AMQ_USER=admin \\n  -e AMQ_PASSWORD=admin \\n  apache/activemq-artemis\n

docker run --name my-db -e POSTGRES_PASSWORD=mysecretpassword -d postgres

docker run -d --name my-postgres -e POSTGRES_USER=myuser -e POSTGRES_PASSWORD=mypassword -e POSTGRES_DB=mydatabase -p 5432:5432 postgres:latest

docker run -d --name activemq -p 61616:61616 -p 8161:8161 -p 61613:61613 apache/activemq-classic

docker run -p 5050:80 \
-e "PGADMIN_DEFAULT_EMAIL=prai@domain.com" \
-e "PGADMIN_DEFAULT_PASSWORD=SuperSecret" \
-d dpage/pgadmin4:9.10


# relationship

Yes — one-to-many relationships among submit_sm, submit_sm_resp, and deliver_sm are not only possible, but common in SMPP-based systems.
Below is the typical way these relationships are modeled.

✅ 1. Relationship: submit_sm → submit_sm_resp (One-to-One)

For every submit_sm PDU sent, the SMSC returns exactly one submit_sm_resp.

submit_sm (client → SMSC)

submit_sm_resp (SMSC → client)

Key: message_id is generated in the submit_sm_resp.
So the relationship is:

submit_sm (1) —— (1) submit_sm_resp


You can make it one-to-many in a database schema, but in SMPP protocol terms it is one-to-one.

✅ 2. Relationship: submit_sm_resp → deliver_sm (One-to-Many)

A single submitted message may result in multiple deliver_sm PDUs:

Delivery receipts (DLRs)

Intermediate delivery events

MO replies (if linked by message IDs or external correlation)

So:

submit_sm_resp (1) —— (*) deliver_sm


Common reasons for multiple deliver_sm responses:

✔ Multiple delivery attempts

E.g. ENROUTE, DELIVERED, EXPIRED, UNDELIVERABLE.

✔ Multiple network hops

Some SMSCs generate receipts per hop or retry.

✔ MO (Mobile Originated) messages linked via session

Not always tied to the same message_id, but some platforms correlate them.

📌 Summary Table
Entity	Relationship	Reason
submit_sm ↔ submit_sm_resp	1-to-1	One request, one response
submit_sm_resp ↔ deliver_sm	1-to-many	Many DLRs or events per message
📘 Example SQL Schema (Simplified)
CREATE TABLE submit_sm (
id BIGINT PRIMARY KEY,
source_addr VARCHAR(20),
dest_addr VARCHAR(20),
text VARCHAR(500),
submit_timestamp TIMESTAMP
);

CREATE TABLE submit_sm_resp (
id BIGINT PRIMARY KEY,
submit_sm_id BIGINT REFERENCES submit_sm(id),
message_id VARCHAR(64),
status INT
);

CREATE TABLE deliver_sm (
id BIGINT PRIMARY KEY,
message_id VARCHAR(64),
deliver_timestamp TIMESTAMP,
stat VARCHAR(20),
err VARCHAR(10),
description TEXT
);

✔ Final Answer

Yes, you can have one-to-many relationships.

Protocol-wise:

submit_sm → submit_sm_resp is one-to-one

submit_sm_resp → deliver_sm is one-to-many

Database schemas often implement this exactly as described.