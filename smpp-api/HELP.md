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
