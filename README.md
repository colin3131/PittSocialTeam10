# PittSocial - Team10
CS 1555 Fall 2019 Project - Team 10
By Jonathan Zdobinski, Jake Diecidue, and Colin Spratt

## Compiling / Running Instructions
 - Open the Command Line
 - Open the PostgreSQL shell (`psql -h localhost -U postgres -d postgres`)
 - Run `\i schema.sql`
 - Run `\i trigger.sql`
 - Exit the PostgreSQL shell
 - Add postgresql-42.2.8.jar to the Repository
 - Run `javac -cp ".;postgresql-42.2.8.jar" PittSocial.java`
 - Run `java -cp ".;postgresql-42.2.8.jar" PittSocial`
 - Enter your PostgreSQL username
 - Enter your PostgreSQL password
