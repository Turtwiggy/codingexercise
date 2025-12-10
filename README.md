### Setup

- find the application.properties.template (src/main/resources/application.properties.template)
  - copy application.properties.template => application.properties
  - update the values

### Running

- mvn spring-boot:run
- mvn test

<!-- other
mvn install
mvn validate
mvn compile
mvn package
mvn integration-test
mvn verify
mvn deploy
-->

## Querying

Create -> returns a uuid

```
curl -Method POST http://localhost:8080/packages
  -ContentType "application/json"
  -Body '{
    "name": "Package",
    "description": "The Full Package",
    "productIds": ["id0", "id1"]
  }'
```

List

```
curl -Method GET http://localhost:8080/packages
  -ContentType "application/json"
```

Get

```
curl -Method GET http://localhost:8080/packages/{uuid}
  -ContentType "application/json"
```

Update

```
curl -Method PUT http://localhost:8080/packages/{uuid}
  -ContentType "application/json"
  -Body '{
    "name": "New Package Name",
    "description": "The Full Package (updated)",
    "productIds": ["id0", "id1"]
  }'
```

Delete

```
curl -Method DELETE http://localhost:8080/packages/{uuid}
  -ContentType "application/json"
```
