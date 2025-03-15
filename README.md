# Health Track System

## Overview
Health Track is a Java application designed for managing healthcare data. It provides a comprehensive system for tracking patients, doctors, visits, prescriptions, drugs, and insurance information through a user-friendly interface. The application supports complete CRUD (Create, Read, Update, Delete) operations for all entities, backed by a relational database.

## Features
- Patient management
- Doctor and specialist tracking
- Prescription and drug information
- Insurance policy management
- Visit records
- Advanced filtering functionality
- Detailed data views and editing capabilities
- Data validation to ensure integrity

## Technologies
- Java
- Swing (GUI Framework)
- MariaDB (Database)
- Docker and Docker Compose (Containerization)
- Maven (Dependency Management)
- SLF4J (Logging)

## Prerequisites
Before you begin, ensure you have the following installed on your system:

- [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or higher
- [Maven](https://maven.apache.org/download.cgi) (3.6.0 or higher recommended)
- [Docker](https://www.docker.com/products/docker-desktop) and [Docker Compose](https://docs.docker.com/compose/install/) (for database containerization)
## Setup and Installation

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/health-track.git
cd health-track
```

### 2. Database Setup
The application uses a MariaDB database running in Docker. To start the database:

```bash
docker-compose up -d
```

This will create and initialize the database using the configuration in `docker-compose.yml` and the schema in `init.sql`.

### 3. Configure Database Connection
Ensure your `src/main/resources/db.properties` file has the correct database connection parameters:

```properties
db.url=jdbc:mysql://localhost:3307/health_track_db
db.user=user
db.password=password
```

### 4. Build and Run

You can run the application using Maven without creating a JAR file (recommended during development):

```bash
# Compile and run directly
mvn clean compile exec:java
```

Alternatively, you can package the application as an executable JAR and run it:
```bash
# Build the application
mvn clean package

# Run the application
java -jar target/health-track-1.0-SNAPSHOT-jar-with-dependencies.jar
```
### 5. Troubleshooting

- **Database Connection Issues**: 
  - Verify Docker is running with `docker ps`
  - Check if the database container is up with `docker-compose ps`
  - Try restarting the container: `docker-compose restart`

- **Java Version Issues**:
  - Verify your Java version with `java -version`
  - Ensure you're using JDK 17 or higher

- **Maven Issues**:
  - Check Maven installation with `mvn -version`
  - Try clearing Maven cache: `mvn dependency:purge-local-repository`

- **Windows-Specific Issues**:
  - If using Windows, ensure Docker Desktop is running and configured to use Linux containers
  - For path issues in Windows, use backslashes or quoted paths when necessary


## Project Structure
- `src/main/java/com/bougastefa/`
  - `models/`: Entity classes (Patient, Doctor, Insurance, etc.)
  - `database/`: DAO classes for database operations
  - `services/`: Business logic layer
  - `gui/`: Swing UI components
    - `components/`: Reusable UI elements
    - `panels/`: Main application panels
  - `utils/`: Utility classes and constants

## Database Schema
The application connects to a MariaDB database with tables for patients, doctors, visits, prescriptions, drugs, and insurance information. The database is initialized with sample data through the `init.sql` script.

## Usage
1. Launch the application
2. Navigate through the tabs for different entities (Patients, Doctors, etc.)
3. Use the provided buttons to add, edit, or delete records
4. Utilize the filter functionality to search through records

## Academic Context
This project was developed as part of a class assessment to demonstrate skills in:
- Database design and implementation
- Object-oriented programming principles
- GUI development
- Software architecture
- Docker containerization

## Notes for Assessment
- The codebase demonstrates MVC pattern separation
- Implements proper error handling and logging
- Features reusable UI components
- Includes containerized database setup for easy deployment

## License
This project is intended for academic purposes only.

