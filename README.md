# sciXiv
Software for working with scientific publications. Similar to arXiv ( https://arxiv.org/ ).

Project for course "XML and WEB Services" at Faculty of Technical Sciences, University of Novi Sad.

Team members:
* Danijel Radulović
* Nikola Zubić - https://github.com/NikolaZubic
* Mihajlo Kušljić - https://github.com/mihajlokusljic


# Technologies used in the project
* [Java Programming language](https://www.java.com/en/download/) : general-purpose programming language that is class-based, object-oriented, and designed to have as few implementation dependencies as possible [BACKEND]
* [Spring Framework](https://spring.io/) : an application framework and inversion of control container for the Java platform [BACKEND]
* [eXist-db](http://exist-db.org/exist/apps/homepage/index.html) : an open source software project for NoSQL databases built on XML technology
* [Apache Jena Fuseki](https://jena.apache.org/documentation/fuseki2/) : SPARQL server used for metadata manipulation & extraction (in our case, it was used for scientific publications)
* [Angular](https://angular.io/) : TypeScript-based open-source web application framework [FRONTEND]

# Project setup
Clone the repository:
```
git clone https://github.com/NikolaZubic/sciXiv.git
```
* [BACKEND]<br>
Requirements include Java IDE and Spring Boot installed on machine. From there we can run the project from sciXiv/src/main/java/xml/web/services/team2/sciXiv/SciXivApplication.java and use it at port:8080.
* [eXist-db]<br>
Runs on port:8081
* [Apache Jena Fuseki]<br>
Download Apache Jena Fuseki and extract it from .zip file. After that, run fuseki-server Batch file ; available on port:3030.
* [FRONTEND]<br>
In sciXiv/Frontend/ run: `npm install` and then `npm run start` ; available on port:4200.

<br>
Copyright (c) 2019 Nikola Zubić, Danijel Radulović, Mihajlo Kušljić
