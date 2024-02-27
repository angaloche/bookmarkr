# Spring Boot Book Store Application

This is a sample Spring Boot application for a book store, implementing CRUD (Create, Read, Update, Delete) operations with a beautiful and responsive user interface using Thymeleaf.

## Table of Contents
1. [Introduction](#introduction)
2. [Features](#features)
3. [Prerequisites](#prerequisites)
4. [Setup](#setup)
5. [Running the Application](#running-the-application)
6. [Usage](#usage)
7. [Project Structure](#project-structure)
8. [Dependencies](#dependencies)
9. [Contributing](#contributing)
10. [License](#license)

## Introduction

This Spring Boot Book Store application provides a simple yet powerful platform for managing books. It is built using the Spring Boot framework, Thymeleaf for server-side templating, and Bootstrap for a beautiful and responsive user interface.

## Features

- Create, Read, Update, and Delete (CRUD) operations for books.
- Responsive and visually appealing user interface.
- Integration with a relational database for persistent storage.
- Easy-to-use web interface for managing books.

## Prerequisites

Make sure you have the following installed before setting up the project:

- Java Development Kit (JDK) 8 or higher
- Maven
- Your favorite IDE (e.g., IntelliJ IDEA, Eclipse)

## Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/hellojeeban/bookstore-boot-app.git
   ```

2. Open the project in your IDE.

3. Configure the database properties in the `application.properties` file.

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/bookstore
   spring.datasource.username=root
   spring.datasource.password=root_password
   ```

4. Build the project using Maven:

   ```bash
   mvn clean install
   ```

## Running the Application

Run the application from your IDE or use the following Maven command:

```bash
mvn spring-boot:run
```

The application will start at `http://localhost:8080`.

## Usage

Visit `http://localhost:8080` in your web browser to access the Book Store application. Use the intuitive web interface to perform CRUD operations on books.

## Project Structure

The project structure follows the standard Spring Boot layout:

- `src/main/java`: Java source files.
- `src/main/resources`: Application properties, templates, and static resources.
- `src/test`: Test source files.
- `pom.xml`: Maven project configuration.

## Dependencies

- Spring Boot
- Thymeleaf
- Bootstrap
- MySQL Connector

Refer to the `pom.xml` file for a complete list of dependencies.

## Contributing

Feel free to contribute by opening issues or creating pull requests. Your feedback and improvements are highly appreciated.

## License

This project is licensed under the [MIT License](LICENSE).

---

Happy coding! 🚀
