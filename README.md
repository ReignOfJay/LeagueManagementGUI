# 🏀 League Manager GUI
A Desktop Application for Basketball League Management
<p align="center"> <strong>Manage leagues. Organize teams. Track players. Schedule games.</strong> </p>
<p align="center"> <img src="https://img.shields.io/badge/Language-Java-orange?style=for-the-badge&logo=openjdk" alt="Java" /> <img src="https://img.shields.io/badge/GUI-JavaFX-2D6A4F?style=for-the-badge" alt="JavaFX" /> <img src="https://img.shields.io/badge/Testing-JUnit-25A162?style=for-the-badge" alt="JUnit" /> <img src="https://img.shields.io/badge/Version_Control-Git%20%26%20GitHub-181717?style=for-the-badge&logo=github" alt="Git and GitHub" /> </p>

## 📌 Project Overview
League Manager GUI is a Java desktop application developed collaboratively by a team of students as part of a Software Engineering course at Valdosta State University.
Based on a project narrative, our team designed and developed a basketball league management system that brings league administration, team organization, player management, and game scheduling into one graphical interface.
The application provides role-based access for different types of users, allowing administrators and officials to interact with league information according to their responsibilities.
Throughout development, our team followed an iterative, sprint-based workflow, maintained detailed user stories, coordinated team meetings, and used Git and GitHub to support version control and collaborative development.
## 🎯 Project Goals
Build a functional graphical user interface for basketball league management.
Provide role-specific access to administrative and league-management features.
Organize teams, players, leagues, and scheduled games in one application.
Apply software engineering practices through sprint planning, testing, collaboration, and version control.
Adapt to evolving technical needs throughout the development process.

## ✨ Application Features
### 🔐 1. Login and Role-Based Access
The application includes a login interface for users to access the system with their credentials.
The system distinguishes between three user roles:
League Administrator (LeagueA) — Administrative access to user management and league operations.
League Official (LeagueO) — Access to designated league and team management features.
Team Official (TeamO) — Access to relevant team, player, league, and schedule information.
Each role has different permissions, helping separate administrative responsibilities from general viewing and management tasks.
### 👤 2. User Management — League Administrator
League Administrators have access to user-management functionality, including:
Creating and managing user accounts.
Viewing the registered user list.
Managing usernames or user IDs and passwords.
Supporting access to the application according to assigned roles.
Viewing users in an organized, sorted list.
These features support centralized account administration for the league management system.
### 🏆 3. League Management
The application supports league administration through a graphical dashboard.
Features include:
Creating and viewing leagues.
Organizing teams within a league.
Accessing league-related information.
Viewing league-level statistics and standings where supported by the application.
### 🐯 4. Team Management
Team-related functionality helps organize participating teams within a league.
Features include:
Adding and viewing teams.
Organizing teams within the league.
Accessing team rosters.
Viewing team-related information and statistics.
Displaying teams in an organized interface.
### 🏃 5. Player Management
The system supports player-related operations for team and league officials, subject to their assigned permissions.
Features include:
Adding and viewing player profiles.
Associating players with teams.
Viewing team rosters and league player information.
Organizing player records for easier navigation.
Accessing player-related information and statistics where implemented.
### 📅 6. Game Scheduling and Results
The application includes functionality for managing basketball games and their schedules.
Features include:
Scheduling games between teams.
Viewing the game schedule.
Accessing game-related information.
Supporting game-result and score management through the application's game-management functionality.
Viewing relevant game information separately from team and league information.
### 📊 7. Statistics and Leaderboards
The system is designed to make league information easier to navigate by separating different views of the data.
Depending on the selected view, users can access information related to:
Leagues
Teams
Games
Players
Standings and leaderboard information
Access to features depends on the user's role and the functionality available in the application.
### 🖥️ 8. Graphical User Interface
The application uses JavaFX to provide a desktop GUI rather than relying exclusively on console-based interaction.
The interface brings key management functions into a central application experience, with dedicated areas for login, dashboard navigation, and league-management tasks.

## 🛠️ Technology Stack
Technology
Purpose
Java
Core programming language and application logic
JavaFX
Desktop graphical user interface
JUnit
Unit testing and verification of application behavior
Git
Source control and change tracking
GitHub
Repository hosting and collaborative version control
Microsoft Excel
Sprint planning, user-story organization, and tracking
CSS
GUI styling and visual customization

## 🔄 Development Process
### Agile-Inspired, Sprint-Based Development
Our team's work was organized development into three sprints, using the project narrative to guide implementation and task planning.
Our workflow included:
User-story planning: Organized and maintained a Microsoft Excel workbook containing detailed user stories for each sprint.
Sprint organization: Used the user stories to help structure development priorities and track planned functionality.
Team coordination: Organized team meetings and communicated directives to improve coordination, clarify responsibilities, and keep work focused.
Iterative implementation: Developed and refined application features throughout the project.
Testing: Used JUnit tests to support verification of application components.
Version control: Used Git and GitHub to track changes and maintain the codebase during development.
Technical adaptation: Pivoted from Swing toward JavaFX as the GUI implementation evolved.
This process strengthened our understanding of how planning, communication, testing, and source control contribute to collaborative software development.

## 🧪 Testing
JUnit was used to support testing of application components.
The repository includes test classes for several areas of the system, including:
Game management and game behavior
League management
Team management and team behavior
Player behavior
User behavior
GUI-related components
The tests are intended to help verify expected behavior and identify issues as the application evolves.

## 🧠 Key Takeaways
This project provided practical experience with both software development and the coordination required to deliver a team-built application.
Key areas of growth included:
Developing a Java desktop application with JavaFX.
Implementing and working with role-based permissions.
Organizing application functionality across users, leagues, teams, players, and games.
Writing and maintaining JUnit tests.
Using Git and GitHub for source control and change tracking.
Planning development around user stories and three sprints.
Coordinating team meetings and communicating development priorities.
Adapting to changing technical requirements by transitioning from Swing to JavaFX.
The project also strengthened my appreciation for the relationship between technical implementation, clear requirements, team communication, and disciplined version control.

## 🎬 GUI Showcase
<img width="959" height="599" alt="image" src="https://github.com/user-attachments/assets/c1fc8111-e192-4fff-9ca5-420184430cba" />


<img width="959" height="595" alt="image" src="https://github.com/user-attachments/assets/a137bf50-0e01-4b4f-b299-23f89971a1cb" />

<img width="959" height="599" alt="image" src="https://github.com/user-attachments/assets/db99b568-9f71-43c2-afb5-4c3dc094992c" />


## 🎮 Getting Started 

### Prerequisites

- Java Development Kit (JDK): 25
- Maven
- JavaFX 

### Build and Test

From the project root, run the build and test commands
supported by the repository's Maven configuration.

### Run the Application

Follow the project-specific instructions to launch the JavaFX application.

> Note: Add the exact Java version, commands, and launch
> configuration after verifying them against the project.


Video demonstration coming soon!
I plan to add a walkthrough demonstrating the application's interface and major workflows, including login, role-based access, user management, league and team administration, player management, and game scheduling.
📹 Showcase video will be added here.

## 👥 Team Project
Developed collaboratively as part of a Software Engineering course at Valdosta State University.
The project was based on a defined project narrative and developed through a three-sprint, team-oriented workflow.

<p align="center"> <strong>Built with Java, teamwork, iterative development, and a commitment to learning.</strong> </p>
