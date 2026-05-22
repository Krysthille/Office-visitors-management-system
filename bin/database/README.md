# Database Setup for Office Visitor Management System

## Overview
This document describes the database setup for the Office Visitor Management System (OVMS) using MySQL.

## Database Configuration

### Database Details
- **Database Name**: `ovms_db`
- **Host**: `localhost`
- **Port**: `3306`
- **Username**: `root`
- **Password**: `` (empty for XAMPP default)

### Prerequisites
1. XAMPP installed and running
2. MySQL service started
3. Database `ovms_db` created in phpMyAdmin

## Setup Instructions

### 1. Create Database
1. Open phpMyAdmin (http://localhost/phpmyadmin)
2. Click "New" to create a new database
3. Enter database name: `ovms_db`
4. Click "Create"

### 2. Import Database Schema
1. In phpMyAdmin, select the `ovms_db` database
2. Click "Import" tab
3. Choose the file: `src/database/visitors_logbook.sql`
4. Click "Go" to execute the SQL

### 3. Verify Setup
After import, you should see:
- Table: `visitors_logbook`
- View: `visitors_summary`

## Database Schema

### Table: visitors_logbook
| Column | Type | Description |
|--------|------|-------------|
| id | INT | Primary key, auto-increment |
| logbook_number | VARCHAR(20) | Unique logbook number |
| timestamp | DATETIME | Visit timestamp |
| last_name | VARCHAR(100) | Visitor's last name |
| first_name | VARCHAR(100) | Visitor's first name |
| middle_name | VARCHAR(100) | Visitor's middle name |
| province | VARCHAR(100) | Province |
| municipality | VARCHAR(100) | Municipality |
| barangay | VARCHAR(100) | Barangay |
| age | INT | Visitor's age |
| gender | ENUM | Gender (Male/Female/Prefer not to say) |
| phone_number | VARCHAR(20) | Contact number |
| email | VARCHAR(255) | Email address |
| sector | VARCHAR(100) | Sector/Organization |
| purpose | VARCHAR(500) | Visit purpose |
| signature_path | VARCHAR(500) | Path to signature file |
| photo_path | VARCHAR(500) | Path to photo file |
| created_at | TIMESTAMP | Record creation time |
| updated_at | TIMESTAMP | Record update time |

### Indexes
- `idx_logbook_number` - For fast logbook number lookups
- `idx_timestamp` - For date-based queries
- `idx_last_name` - For name-based searches
- `idx_municipality` - For municipality-based filtering
- `idx_created_at` - For creation date queries

## Features

### 1. Automatic Logbook Number Generation
- Handled by `LogbookNumberGenerator` in the model layer
- Format: `YYYYMMDD-XXX` (e.g., 20240115-001)
- Queries database to get next sequence number for the day
- Fallback mechanism if database query fails

### 2. Data Validation
- Required field validation
- Age validation (1-150)
- Email format validation (optional)
- Phone number validation

### 3. Reporting
- `visitors_summary` view for aggregated data
- Date range queries
- Municipality-based filtering
- Gender statistics

## Usage Examples

### Java Code Examples

```java
// Initialize service
VisitorService visitorService = new VisitorService();

// Save new visitor
Visitor visitor = new Visitor();
visitor.setLastName("Doe");
visitor.setFirstName("John");
// ... set other fields
boolean saved = visitorService.saveVisitor(visitor);

// Get visitor by logbook number
Visitor found = visitorService.getVisitorByLogbookNumber("20240115-001");

// Get all visitors
List<Visitor> allVisitors = visitorService.getAllVisitors();

// Get visitors by date range
LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59);
List<Visitor> januaryVisitors = visitorService.getVisitorsByDateRange(start, end);

// Get statistics
String stats = visitorService.getVisitorStatistics();
```

### SQL Queries

```sql
-- Get today's visitors
SELECT * FROM visitors_logbook 
WHERE DATE(timestamp) = CURDATE();

-- Get visitors by municipality
SELECT * FROM visitors_logbook 
WHERE municipality = 'Naval';

-- Get visitor count by gender
SELECT gender, COUNT(*) as count 
FROM visitors_logbook 
GROUP BY gender;

-- Get recent visitors (last 7 days)
SELECT * FROM visitors_logbook 
WHERE timestamp >= DATE_SUB(NOW(), INTERVAL 7 DAY);
```

## Troubleshooting

### Common Issues

1. **Connection Failed**
   - Ensure XAMPP MySQL service is running
   - Check if database `ovms_db` exists
   - Verify username/password in `DatabaseConfig.java`

2. **Table Not Found**
   - Import the SQL file again
   - Check if database name is correct

3. **Permission Denied**
   - Ensure MySQL user has proper permissions
   - For XAMPP, root user should have full access

4. **JDBC Driver Missing**
   - Add MySQL JDBC connector to project classpath
   - Download from: https://dev.mysql.com/downloads/connector/j/

### Testing Connection
```java
DatabaseConfig dbConfig = DatabaseConfig.getInstance();
boolean connected = dbConfig.testConnection();
System.out.println("Database connected: " + connected);
```

## File Structure
```
src/
├── config/
│   └── DatabaseConfig.java          # Database connection configuration
├── model/
│   ├── Visitor.java                 # Visitor data model
│   └── LogbookNumberGenerator.java  # Logbook number generation logic
├── service/
│   └── VisitorService.java          # Database operations service
└── database/
    ├── visitors_logbook.sql         # Database schema
    └── README.md                    # This file
```

## Security Notes
- Change default MySQL password in production
- Use connection pooling for better performance
- Implement proper error handling
- Add input sanitization for user data
- Consider encryption for sensitive data 