-- Visitors Logbook Table for OVMS Database
-- Database: ovms_db
-- Table: visitors_logbook

USE ovms_db;

-- Drop table if exists (for development)
DROP TABLE IF EXISTS visitors_logbook;

-- Create visitors logbook table
CREATE TABLE visitors_logbook (
    id INT AUTO_INCREMENT PRIMARY KEY,
    logbook_number VARCHAR(20) NOT NULL UNIQUE,
    timestamp DATETIME NOT NULL,
    
    -- Personal Information
    last_name VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    province VARCHAR(100) NOT NULL,
    municipality VARCHAR(100) NOT NULL,
    barangay VARCHAR(100) NOT NULL,
    age INT,
    gender ENUM('Male', 'Female', 'Prefer not to say'),
    phone_number VARCHAR(20),
    email VARCHAR(255),
    sector VARCHAR(100),
    purpose VARCHAR(500),
    
    -- Signature and Photo paths
    signature_path VARCHAR(500),
    photo_path VARCHAR(500),
    
    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes for better performance
    INDEX idx_logbook_number (logbook_number),
    INDEX idx_timestamp (timestamp),
    INDEX idx_last_name (last_name),
    INDEX idx_municipality (municipality),
    INDEX idx_created_at (created_at)
);

-- Create a view for easy reporting
CREATE OR REPLACE VIEW visitors_summary AS
SELECT 
    DATE(timestamp) as visit_date,
    COUNT(*) as total_visitors,
    COUNT(CASE WHEN gender = 'Male' THEN 1 END) as male_visitors,
    COUNT(CASE WHEN gender = 'Female' THEN 1 END) as female_visitors,
    municipality,
    sector
FROM visitors_logbook 
GROUP BY DATE(timestamp), municipality, sector
ORDER BY visit_date DESC, municipality, sector; 

-- Dump Login Credentials Table
DROP TABLE IF EXISTS dump_login_credentials;
CREATE TABLE dump_login_credentials (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type ENUM('login', 'verification') NOT NULL,
    username VARCHAR(100),
    password VARCHAR(100),
    code VARCHAR(100)
);

-- Insert Login Panel Credentials
INSERT INTO dump_login_credentials (type, username, password) VALUES ('login', 'dictofficevisitorMS', 'dict2016');

-- Insert Verification Panel Credential
INSERT INTO dump_login_credentials (type, code) VALUES ('verification', '9876-5432-1098'); 