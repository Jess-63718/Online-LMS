
## Course-Registration-System

## Description

The **Course Registration System** is a web application designed to streamline the process of course management and registration. This system enables administrators to create, manage, and delete courses while also allowing users to browse and register for available courses. The application is built using Spring Framework, Java, HTML, CSS, and Thymeleaf as the view engine. Persistent data is managed with JPA and stored in a MySQL database or an in-memory database as needed.

## Functionality

### Admin Functionality
- **Create Courses**: Administrators can create new courses and specify the maximum number of participants allowed per course.
- **Edit Courses**: Existing courses can be edited to update details such as course name, description, or participant limit.
- **Delete Courses**: Courses that are no longer needed can be removed from the system.
- **Browse Courses**: Administrators can view all courses and see the list of participants registered for each course.
- **Remove Participants**: Administrators have the ability to remove specific students from courses.

### User Functionality
- **Browse Available Courses**: Users can view a list of all courses available for registration.
- **Register for Courses**: Users can register for one or more courses, subject to availability and participant limits.

## Technologies Used
- **Backend**: 
  - Spring Framework (Spring Boot)
  - Java
  - JPA (Java Persistence API) for data persistence
  - MySQL or In-Memory Database for storage

- **Frontend**:
  - HTML
  - CSS
  - Thymeleaf (View Engine)

- **Additional Features**:
  - Beans and Dependency Injection for modular and maintainable code.
  - Interceptors and Listeners where relevant for handling cross-cutting concerns.
  - Minimal JavaScript, used only for client-side validation or specific functionality like long polling.

## Getting Started

### Prerequisites
- Java 11 or higher
- Maven or Gradle
- MySQL database (if using MySQL for persistent data)
- IDE such as IntelliJ IDEA or Eclipse

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/Course-Registration-System.git
   ```
2. Navigate to the project directory:
   ```bash
   cd Course-Registration-System
   ```
3. Configure the application properties for database connection if using MySQL:
   - Edit `src/main/resources/application.properties` to update your MySQL credentials.

4. Build the project:
   ```bash
   mvn clean install
   ```
5. Run the application:
   ```bash
   mvn spring-boot:run
   ```
6. Access the application by navigating to `http://localhost:8080` in your web browser.

## Usage

### Admin Access
- Admins can log in through the `/admin` route and access the course management features.
  
### User Access
- Users can browse courses and register for them directly from the homepage.

## Contributing
Feel free to fork the repository and submit pull requests. Contributions are welcome to improve the functionality, design, and performance of the application.


