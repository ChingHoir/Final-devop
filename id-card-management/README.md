# ID Card Management System

A Spring Boot application for managing and generating ID cards with QR codes and barcodes.

## Features

- Profile management (create, update, delete, search)
- ID card template management
- QR code generation for profiles
- Barcode generation (Code 128, Code 39)
- PDF ID card generation
- Photo upload support

## Tech Stack

- **Backend:** Spring Boot 3.2, Spring Data JPA, Thymeleaf
- **Database:** H2 (in-memory)
- **QR Code:** ZXing
- **PDF:** iTextPDF
- **Build Tool:** Maven
- **Java Version:** 17

## Project Structure

```
id-card-management/
├── src/
│   ├── main/
│   │   ├── java/com/example/idcard/
│   │   │   ├── IdCardApplication.java
│   │   │   ├── config/
│   │   │   │   └── WebConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── ProfileController.java
│   │   │   │   └── TemplateController.java
│   │   │   ├── model/
│   │   │   │   ├── Profile.java
│   │   │   │   ├── ProfileBuilder.java
│   │   │   │   ├── ProfileType.java
│   │   │   │   ├── Template.java
│   │   │   │   └── BarcodeType.java
│   │   │   ├── repository/
│   │   │   │   ├── ProfileRepository.java
│   │   │   │   └── TemplateRepository.java
│   │   │   ├── service/
│   │   │   │   ├── ProfileService.java
│   │   │   │   ├── QRCodeService.java
│   │   │   │   ├── BarcodeService.java
│   │   │   │   └── PDFService.java
│   │   │   └── dto/
│   │   │       └── ProfileDTO.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── templates/
│   │       │   ├── profile-form.html
│   │       │   └── id-card-preview.html
│   │       └── static/
│   │           └── css/
│   │               └── style.css
│   └── test/
│       └── java/com/example/idcard/
│           ├── ProfileServiceTest.java
│           └── ProfileControllerTest.java
├── uploads/ (for photo storage)
├── pom.xml
└── README.md
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8+

### Running the Application

```bash
cd id-card-management
mvn spring-boot:run
```

The application will start at `http://localhost:8080`.

### Accessing H2 Console

Navigate to `http://localhost:8080/h2-console` with:
- JDBC URL: `jdbc:h2:mem:idcarddb`
- Username: `sa`
- Password: (empty)

## API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | /profiles | List all profiles |
| GET | /profiles/new | Show create profile form |
| POST | /profiles | Create a new profile |
| GET | /profiles/{id} | View profile details |
| GET | /profiles/{id}/edit | Edit profile form |
| POST | /profiles/{id} | Update profile |
| POST | /profiles/{id}/delete | Soft delete profile |
| GET | /profiles/api | Get all profiles as JSON |
| GET | /templates | List templates |
| GET | /templates/new | Show create template form |
| POST | /templates | Create new template |