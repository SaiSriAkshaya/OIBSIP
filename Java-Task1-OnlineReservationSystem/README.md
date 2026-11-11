# Online Reservation System (Java Swing + JDBC + SQLite)

Oasis Infobyte SIP – Java Development, Task 1

A GUI train reservation system: log in, book tickets (auto-generated PNR) and cancel bookings using the PNR.

## Features
- **Login form** – username + password; access denied for invalid credentials (passwords stored as SHA-256 hashes)
- **Reservation form** – passenger name, train number, train name (auto-filled from train number), class type, date of journey, source, destination
- **Book button** – saves to the database, generates a unique 10-digit PNR, shows a confirmation dialog
- **Cancellation form** – enter PNR, Fetch shows full booking details, "Are you sure?" dialog, then the booking is removed from the database
- **Validation** – no empty fields, numeric train number, valid and non-past date (yyyy-MM-dd), source and destination must differ
- All SQL uses `PreparedStatement` (prevents SQL injection)

## Classes
| Class | Role |
|---|---|
| `Main` | Entry point, creates the database and opens the login |
| `Database` | JDBC code, table creation, demo data, all queries |
| `LoginForm` | Login window |
| `MainFrame` | Main window with Book / Cancel tabs and Logout |
| `ReservationPanel` | Booking form |
| `CancellationPanel` | Cancellation form |
| `Reservation` | Booking data model |

## Setup (Windows PowerShell)
1. Install JDK 11 or newer.
2. Download the SQLite JDBC driver, **sqlite-jdbc-3.36.0.3.jar**, from
   https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.36.0.3/sqlite-jdbc-3.36.0.3.jar
   and put it inside the `lib` folder.
3. Compile (one line):
```
javac -cp "lib\sqlite-jdbc-3.36.0.3.jar" Main.java Database.java LoginForm.java MainFrame.java ReservationPanel.java CancellationPanel.java Reservation.java
```
4. Run:
```
java -cp ".;lib\sqlite-jdbc-3.36.0.3.jar" Main
```
On Mac/Linux use `:` instead of `;` in the classpath and `/` instead of `\`.

The database file `reservation.db` is created automatically on first run.

## Demo data
- Login: `admin` / `admin123`
- Trains: 12723 Telangana Express, 12760 Charminar Express, 12951 Mumbai Rajdhani, 12627 Karnataka Express, 12301 Howrah Rajdhani, 12009 Shatabdi Express
