# Kanban board

Ovo je Java Spring Boot aplikacija razvijena za potrebe procesa intervjuiranja za poziciju Java Developera. 
Projekt implementira REST API za CRUD taskove, demonstrirajući razumijevanje Spring Boot frameworka, REST principa i osnovnih principa razvoja softvera.
U okviru zadatka je razvijen REST-API za dohvaćanje, dodavanje, mijenjanje i brisanje taskova.


# Tehnologije

* Java 21
* Spring Boot 3.4 (inicijalno postavljen 3.5, ali zbog problema sa verzijom openapi-ja je smanjena verzija)
* Maven
* MySQL (baza podataka)
* JUnit 5 i Mockito (za testiranje)
* Docker


# Pokretanje aplikacije

Ovaj projekt koristi multi-stage Dockerfile za buildanje Spring Boot aplikacije i Docker compose za zajedničko podizanje aplikacije i MySQL baze.

# Preduvjeti za pokretanje

* Docker
* Docker Compose
* Internet veza (prvi build povlači image sa interneta - maven:3.9.4-eclipse-temurin-21, eclipse-temurin:21-jdk, mysql:8.0.33)

# Pokretanje
Na lokaciji gdje se nalaze Dockerfile i docker-compose u terminalu upisati:
`docker-compose up --build`.  
Nakon što su se aplikacija i baza podigle otići u pregledniku na `http://localhost:8080/swagger-ui/index.html` kako bi pristupili Swagger UI.  
Ukoliko želite ugasiti aplikaciju potrebno je u terminal upisati:
`docker-compose down`

#Demo podaci
Demo korisnik: user  
Demo lozinka: password  
Korisnike je moguće dodati isključio preko baze jer u ovoj fazi razvoja projekta to nije predviđeno nikako drugačije.

Ostali demo podaci su dodani kako bi se moglo testirati preko Swaggera.  
Id-jevi taskova koji se mogu dohvaćati i mijenjati je u rasponu od 1 do 80.


