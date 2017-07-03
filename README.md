# NgayThoBet

## Technology Stack
- [Spring Boot](https://projects.spring.io/spring-boot/)
- [Spring Framework](http://projects.spring.io/spring-framework/)
- [Thymeleaf](http://www.thymeleaf.org/) with [AdminLTE](https://almsaeedstudio.com/themes/AdminLTE/index2.html)
- [Spring Data JPA](http://projects.spring.io/spring-data-jpa/)
- [Spring Security](https://projects.spring.io/spring-security/)
- [Flyway](https://flywaydb.org/)
- [Project Lombok](https://projectlombok.org/) (development purpose, reducing Java boilerplate code)

## Prerequisites
This application has the following software prerequisites:

* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Maven 3](https://maven.apache.org/download.cgi)
* [PostgreSQL](https://www.postgresql.org/)
* [Project Lombok - IDE plugin](https://projectlombok.org/download.html) (select correct Lombok plugin for your IDE)


## Configuration
All configurations are located at `resources/application.yml`

* Customize the database connection
```
url: jdbc:postgresql://localhost:5432/ngaythobet
username: ngaythobet
password: ngaythobet@123
driver-class-name: org.postgresql.Driver
```

* Customize application configuration
```
ngaythobet:
    xxx: yyy
```

## Building
```
mvn clean package
```

## Running
You can run the application in development mode:
```
mvn spring-boot:run
```

Or you can build and run as a packaged application:
```
java -jar target/ngaythobet.jar
```
