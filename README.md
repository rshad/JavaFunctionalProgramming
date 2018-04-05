# Learn Functional Programming in Java
This application represents my first functional programming in Java

## The application is designed to work with the following files
  1) data.txt
  2) Sector.txt
  3) Route.txt

## Details
 ### 1) data.txt
 Represents the personal data of employees.
 
 ### 2) Sector.txt
 Each employee must be assigned to a Sector. There are 2 sectors, Sector1 and Sector2.
 
 ### 3) Route.txt
 Represents the routes. Each employee must be assigned to a route. Each sector, contains 3 routes, route1, route2 and route 3.
 
 ## Purpos
 The data is not complete and in some cases has some repeated data or invalid data. So we can distinct the following cases:
  1) Repeated employee's ID.
  2) Repeated employee's Email.
  3) An employee not assigned to a sector.
  4) An employee not assigned to a sector nor route.
  5) An employee assigned to a sector, but not to any route.
  6) An employee assigned to a route, but no to any sector.
  
 So we have to resolve these cases, using Functional Java. To detect the right functionality of the application, we had to design 
 some tests, using Junit.
 
 ## Project Directory's Structure:
 - /src: contains the source files.
 - /test: contains the test files - Usin Junit.
 - /data: contains the data text files.
 
 
  
