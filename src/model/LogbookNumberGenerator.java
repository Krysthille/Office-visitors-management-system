package model;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import config.DatabaseConn;

public class LogbookNumberGenerator {
    public LogbookNumberGenerator() {}
    
    /**
     * Generate a unique logbook number for today
     * Format: YYYYMMDD-XXX (e.g., 20240702-001)
     */
    public String generateLogbookNumber() {
        String currentDate = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        int seq = 1;
        String logbookNumber;
        while (true) {
            logbookNumber = String.format("%s-%03d", currentDate, seq);
            if (!isLogbookNumberExists(logbookNumber)) {
                return logbookNumber;
            }
            seq++;
        }
    }
    
    /**
     * Generate logbook number for a specific date
     */
    public String generateLogbookNumberForDate(LocalDateTime date) {
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int seq = 1;
        String logbookNumber;
        while (true) {
            logbookNumber = String.format("%s-%03d", dateStr, seq);
            if (!isLogbookNumberExists(logbookNumber)) {
                return logbookNumber;
            }
            seq++;
        }
    }
    
    /**
     * Get the next sequence number for today
     */
    public int getNextSequenceNumber() {
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sql = "SELECT COUNT(*) FROM visitors_logbook WHERE logbook_number LIKE ?";
        
        try (Connection conn = DatabaseConn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, currentDate + "-%");
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting next sequence number: " + e.getMessage());
        }
        
        return 1;
    }
    
    /**
     * Validate if a logbook number already exists
     */
    public boolean isLogbookNumberExists(String logbookNumber) {
        String sql = "SELECT COUNT(*) FROM visitors_logbook WHERE logbook_number = ?";
        
        try (Connection conn = DatabaseConn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, logbookNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking logbook number existence: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Get the last generated logbook number for today
     */
    public String getLastLogbookNumberForToday() {
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sql = "SELECT logbook_number FROM visitors_logbook WHERE logbook_number LIKE ? ORDER BY logbook_number DESC LIMIT 1";
        
        try (Connection conn = DatabaseConn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, currentDate + "-%");
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("logbook_number");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting last logbook number: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get statistics about logbook numbers
     */
    public String getLogbookNumberStatistics() {
        String sql = "SELECT " +
                    "COUNT(*) as total, " +
                    "COUNT(DISTINCT DATE(timestamp)) as days, " +
                    "MAX(logbook_number) as last_number, " +
                    "MIN(logbook_number) as first_number " +
                    "FROM visitors_logbook";
        
        try (Connection conn = DatabaseConn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                int total = rs.getInt("total");
                int days = rs.getInt("days");
                String lastNumber = rs.getString("last_number");
                String firstNumber = rs.getString("first_number");
                
                return String.format("Total Entries: %d | Days Active: %d | First: %s | Last: %s", 
                                   total, days, firstNumber, lastNumber);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting logbook number statistics: " + e.getMessage());
        }
        
        return "Statistics unavailable";
    }
} 