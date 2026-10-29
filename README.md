# OOP_Konyvtar
# Library Management System

## 1. CSV Format Description

The application supports saving and loading library data using a custom CSV format.

The first line contains the column names:

```csv
TIPUS,MEZO1,MEZO2,MEZO3,MEZO4,MEZO5
```

Each following line represents a book, reader, or loan.

| Record type | Fields |
|---|---|
| `K` | ISBN, title, author, publication year, number of copies |
| `O` | Reader card number, reader name |
| `L` | ISBN, reader card number, loan date, due date, return date |

Fields are separated by commas and enclosed in double quotes. Double quotes inside a field are escaped by doubling them. This allows text fields to contain commas.

Dates use the `YYYY-MM-DD` format. An empty return-date field means that the book has not been returned yet.

## 2. Example Data File

The following is an example of a valid CSV data file:

```csv
TIPUS,MEZO1,MEZO2,MEZO3,MEZO4,MEZO5
"K","ISBN-1","The Secret Garden","A. Author","2020","2"
"O","READER-1","Anna"
"L","ISBN-1","READER-1","2025-01-01","2025-01-08",""
```

Save the file as `konyvtar.csv`.

The example contains one book with two copies, one reader, and one active loan. The loan has an empty return date because the book has not been returned.

## 3. Running the Application

### Requirements

- Java JDK 17
- Apache Maven or NetBeans with Maven support

### Using NetBeans

1. Open the project in NetBeans.
2. Wait for Maven dependencies to load.
3. Build the project.
4. Run `KonyvtarProgram.java`.
5. Select a storage method in the console:
   - `1` — Java serialization (`konyvtar.dat`)
   - `2` — CSV (`konyvtar.csv`)

### Using the command line

Open a terminal in the project root directory.

Compile the project:

```bash
mvn clean compile
```

Run the application:

```bash
mvn exec:java -Dexec.mainClass="konyvtar.KonyvtarProgram"
```

The command-line run requires the Maven Exec Plugin to be configured in `pom.xml`. You can also run the main class directly from NetBeans.

### Running the tests

Run the automated tests with:

```bash
mvn test
```

The tests check saving and loading, CSV handling, book search, borrowing rules, and late-fee calculation.
