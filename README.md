# Therapy Notes Management System
Desktop application for mental health professionals to manage client records and document therapy sessions.

## Overview
Java Swing-based practice management system designed specifically for mental health professionals. It provides a complete workflow from client intake through session documentation, clinical assessments, and practice administration protected by PIN authentication.

## Features
### Client Management
- Complete CRUD operations for client records
- Unique client identification codes for confidentiality
- Emergency contact management
### Session Documentation
- Structured therapy note templates with standardized sections:
	- Session information and clinical context
	- Clinical symptoms assessment
	- Narrative documentation
	- Mental status examination
	- Post-session administrative notes
- Customizable assessment options with ICD-10 diagnosis code support
### Document Export
- Professional PDF and DOCX generation
- Bulk export capabilities with progress tracking
- Formatted clinical documentation suitable for records retention
- Multi-session export support
### Security & Administration
- PIN-based authentication with configurable lockout mechanisms
- PBKDF2 password hashing (OWASP-compliant)
- Audit logging of operations
- User preferences and customizable application settings
- Secure first-run configuration wizard
### User Experience
- Intuitive multi-step setup wizard for initial configuration
- Clean, professional UI with CardLayout-based navigation
- Responsive search and filtering across client records
- Progress indicators for long-running operations

## Technology Stack
- **Frontend:** Java Swing with custom components
- **Database:** SQLite with relational schema and foreign key constraints
- **Document Generation:** Apache POI (DOCX), Apache PDFBox (PDF)
- **Security:** Java cryptography (PBKDF2 hashing), secure PIN management
- **Testing:** JUnit with in-memory SQLite for reliable unit testing
### Key Dependencies:
- Apache POI 5.x
- Apache PDFBox 3.x
- JDateChooser (date selection)

## Installation & Setup
### Requirements
- Java 17 or higher
- SQLite (included in application bundle)
- Free disk space

## Getting Started
1. **Clone the repository:**
```
git clone https://github.com/yourusername/therapy-notes.git
cd therapy-notes
```
2. **Build the project:**
```
mvn clean package
```
3. **Run the application:**
```
java -jar therapy-notes.jar
```
4. **Initial Setup:**
- The application will launch a configuration wizard on first run
	- Configure practice information
	- Set your application PIN (minimum 4 digits)
	- Choose where to save your data

## Usage

### Managing Clients
- Navigate to the Clients menu to add new or edit existing client records
- Search by name or ID
- Manage emergency contact information

### Documenting Sessions
- Create new session notes from the Notes menu
- Use standardized templates for consistent documentation
- Select appropriate clinical assessments and diagnoses
- Save session documentation with automatic timestamp

### Exporting Records
- Generate individual session PDFs or Word documents
- Export multiple sessions in bulk with progress tracking
- Create formatted clinical records suitable for filing
- Maintain complete practice documentation

### Administration
- Access logs for all system operations
- Configure application preferences and settings
- Manage user PIN

## Security Considerations

This application handles sensitive health information and implements security best practices:

- **Authentication:** PIN-based access with configurable lockout mechanisms
- **Hashing:** PBKDF2 with SHA-256 for secure password storage
- **Data Validation:** Input validation on all clinical and administrative data

**Important:** This application should be installed only on secure, dedicated machines. Follow HIPAA and applicable healthcare privacy regulations for data protection and access control.

## Architecture & Design Patterns

The application follows established design patterns for maintainability and extensibility:

- **Separation of Concerns:** Clear division between UI, Business Logic, and Data Access
- **Abstract Base Classes:** Shared functionality centralized in abstract parent classes
- **Factory Pattern:** Assessment and configuration object creation
- **SwingWorker:** Asynchronous operations for responsive UI
- **DAO Pattern:** Clean database abstraction layer

## Testing
The project includes comprehensive unit tests using JUnit:
```
mvn test
```
Tests use in-memory SQLite databases for isolated, reliable testing without external dependencies. Test classes are organized by functional area for clear separation of concerns.

## Disclaimer
This application is designed to support mental health practitioners in practice management and documentation. Users are responsible for ensuring compliance with all applicable healthcare regulations, including HIPAA and state-specific privacy laws. Always follow your jurisdiction's requirements for clinical record management and patient data protection.


## Acknowledgements
Credit to Dr. Sarah Conklin, Psy.D. for the idea for this application and providing specific requirements to get it started.

**Version:** 1.0.0

**UUID:** 7cae61c8-89b4-4703-9c7c-b1ae04a99143

**Last Updated:** January 2026

**Maintainer:** Alex Pacheco
