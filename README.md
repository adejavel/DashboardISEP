# Dashboard'ISEP

## Intro

This project is a web platform which main purpose is to facilitate student and teachers interaction enabling students working on different projects to easily exchange with their tutor. The platform aim to facilitate students organization as well, providing a phase and task management.

This project is a school project to learn web technologies.

## Technologies
This project is divided into 3 parts : 

1. Database: MySQL database;
2. Frontend: Javascript (using ReactJS) application;
3. Backend: Java (using Spring) REST API;

To facilitate deployment and requirements installation, we use Docker to build and run our applications.

## Installation

Working on Mac and Linux:
To install the project and run it, you just have to clone this repo.
Then you can `cd` into the project folder and run `docker-compose up`.
It will fail because there is a problem that we didn't solve at the moment concerning the database root user, allowing only `localhost` connection (physically, backend is localhost for database but the problem comes from that Docker creates a network and so when the Backend application tries to communicate with the database, both application don't use the `localhost` host but the IP address given by Docker and so mysql block the connection).
To solve the problem, we just have to enter mysql docker application using CLI and make some users modifications as following:
* run `docker-compose up -d` to launch all the applications in background (Backend will fail but we don't care)
* run `docker exec -it mysqldb bash` to connect to mysql container through CLI
* run `mysql -u root -p` to connect to mysql server. Password will be asked, just enter `root`
* run `show databases;` to show all the databases present in the mysql container, `dashboardisep` should be present in the list
* run `select Host,User from mysql.user;` to show all the users and corresponding allowed host. You should see a `root` user with host `localhost` 
* run `update mysql.user set Host='%' where User='root';` to change the allowed host of `root` user from `localhost` to `%`, that corresponds to all hosts.
* run again `select Host,User from mysql.user;` to check that modification has effectively been done.
* run `exit;` to exit from mysql server
* run `exit` to exit from the mysql container
* run `docker-compose stop` to stop the different applications
* run `docker-compose up` to restart all the applications