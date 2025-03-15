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
- JDK 11 or higher
- Docker and Docker Compose
- Maven

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
```bash
# Build the application
mvn clean package

# Run the application
java -jar target/health-track-1.0-SNAPSHOT.jar
```

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

