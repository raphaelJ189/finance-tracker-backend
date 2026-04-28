# Docker Setup - Finance tracker backend

This document explains how to build and run the Finance Tracker backend using Docker.

## Prerequisites

- Docker Desktop installed and running
- PostgreSQL installed and running on your machine
- A PostgreSQL database created for this project

## Environment Variables

The application requires the following environment variables to be passed at runtime. No sensitive values are hardcoded in the image.

| Variable | Description | Example |
|---|---|---|
| DATABASE_URL | Full JDBC URL to your PostgreSQL database | jdbc:postgresql://host.docker.internal:5432/finance_tracker_db |
| DATABASE_USERNAME | Your PostgreSQL username | postgres |
| DATABASE_PASSWORD | Your PostgreSQL password | yourpassword |
| JWT_SECRET | Strong random secret for signing JWT tokens | generate using openssl rand -base64 64 |
| FRONTEND_URL | URL of your frontend application for CORS | http://localhost:3000 |
| SPRING_PROFILES_ACTIVE | Spring profile to activate | prod |

## Generating a Strong JWT Secret

Run this command to generate a secure JWT secret:

    openssl rand -base64 64

Copy the output and use it as your JWT_SECRET value.

## Building the Image

Navigate to the root of the project where the Dockerfile is located and run:

    docker build -t fintrack-backend:1.0 .

## Running the Container

    docker run -d \
      -p YOUR_PORT:8080 \
      --name fintrack-backend \
      -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/your_database_name \
      -e DATABASE_USERNAME=your_username \
      -e DATABASE_PASSWORD=your_password \
      -e JWT_SECRET=your_generated_secret \
      -e FRONTEND_URL=http://localhost:YOUR_FRONTEND_PORT \
      -e SPRING_PROFILES_ACTIVE=prod \
      fintrack-backend:1.0

Replace YOUR_PORT with any available port on your machine. The right side must always stay as 8080 since that is the port Spring Boot listens on inside the container.

## Verifying the Container is Running

    docker ps

## Checking Application Logs

    docker logs fintrack-backend

If the application started successfully you will see this line in the logs:

    Started FinanceTrackerApplication in XX seconds

You will also see confirmation that the database connected successfully:

    HikariPool-1 - Start completed

## Stopping the Container

    docker stop fintrack-backend

## Removing the Container

    docker rm fintrack-backend

## Important Notes

- Make sure PostgreSQL is running on your machine before starting the container.
- On Windows and Mac use host.docker.internal as the database host in your DATABASE_URL. This is a special DNS name Docker provides to reach your local machine from inside a container.
- On Linux replace host.docker.internal with 172.17.0.1 in your DATABASE_URL.
- The image uses a non root user for security. Your application runs as appuser inside the container.
- All sensitive values are passed at runtime and are never stored inside the image.