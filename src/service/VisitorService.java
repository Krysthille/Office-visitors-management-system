package service;

import model.Visitor;
import model.LogbookNumberGenerator;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import config.DatabaseConn;

public class VisitorService {
    private LogbookNumberGenerator logbookGenerator;
    
    public VisitorService() {
        this.logbookGenerator = new LogbookNumberGenerator();
    }
    
    /**
     * Save a new visitor to the database
     */
    public boolean saveVisitor(Visitor visitor) {
        String sql = "INSERT INTO visitors_logbook (logbook_number, timestamp, last_name, first_name, middle_name, " +
                    "province, municipality, barangay, age, gender, phone_number, email, sector, purpose, " +
                    "signature_path, photo_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Generate logbook number if not provided
            if (visitor.getLogbookNumber() == null || visitor.getLogbookNumber().trim().isEmpty()) {
                visitor.setLogbookNumber(generateLogbookNumber());
            }
            
            // Set timestamp if not provided
            if (visitor.getTimestamp() == null) {
                visitor.setTimestamp(LocalDateTime.now());
            }
            
            // Debug: Print SQL and parameters
            System.out.println("[DEBUG] SQL: " + sql);
            System.out.println("[DEBUG] logbook_number: " + visitor.getLogbookNumber());
            System.out.println("[DEBUG] timestamp: " + visitor.getTimestamp());
            System.out.println("[DEBUG] last_name: " + visitor.getLastName());
            System.out.println("[DEBUG] first_name: " + visitor.getFirstName());
            System.out.println("[DEBUG] middle_name: " + visitor.getMiddleName());
            System.out.println("[DEBUG] province: " + visitor.getProvince());
            System.out.println("[DEBUG] municipality: " + visitor.getMunicipality());
            System.out.println("[DEBUG] barangay: " + visitor.getBarangay());
            System.out.println("[DEBUG] age: " + visitor.getAge());
            System.out.println("[DEBUG] gender: " + visitor.getGender());
            System.out.println("[DEBUG] phone_number: " + visitor.getPhoneNumber());
            System.out.println("[DEBUG] email: " + visitor.getEmail());
            System.out.println("[DEBUG] sector: " + visitor.getSector());
            System.out.println("[DEBUG] purpose: " + visitor.getPurpose());
            System.out.println("[DEBUG] signature_path: " + visitor.getSignaturePath());
            System.out.println("[DEBUG] photo_path: " + visitor.getPhotoPath());
            
            pstmt.setString(1, visitor.getLogbookNumber());
            pstmt.setTimestamp(2, Timestamp.valueOf(visitor.getTimestamp()));
            pstmt.setString(3, visitor.getLastName());
            pstmt.setString(4, visitor.getFirstName());
            pstmt.setString(5, visitor.getMiddleName());
            pstmt.setString(6, visitor.getProvince());
            pstmt.setString(7, visitor.getMunicipality());
            pstmt.setString(8, visitor.getBarangay());
            pstmt.setObject(9, visitor.getAge());
            pstmt.setString(10, visitor.getGender());
            pstmt.setString(11, visitor.getPhoneNumber());
            pstmt.setString(12, visitor.getEmail());
            pstmt.setString(13, visitor.getSector());
            pstmt.setString(14, visitor.getPurpose());
            pstmt.setString(15, visitor.getSignaturePath());
            pstmt.setString(16, visitor.getPhotoPath());
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("[DEBUG] Rows affected: " + rowsAffected);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("[DEBUG] Error saving visitor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Generate a unique logbook number
     */
    public String generateLogbookNumber() {
        return logbookGenerator.generateLogbookNumber();
    }
    
    /**
     * Get visitor by ID
     */
    public Visitor getVisitorById(int id) {
        String sql = "SELECT * FROM visitors_logbook WHERE id = ?";
        
        try (Connection conn = DatabaseConn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToVisitor(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting visitor by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get visitor by logbook number
     */
    public Visitor getVisitorByLogbookNumber(String logbookNumber) {
        String sql = "SELECT * FROM visitors_logbook WHERE logbook_number = ?";
        
        try (Connection conn = DatabaseConn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, logbookNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToVisitor(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting visitor by logbook number: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get all visitors
     */
    public List<Visitor> getAllVisitors() {
        List<Visitor> visitors = new ArrayList<>();
        String sql = "SELECT * FROM visitors_logbook ORDER BY timestamp DESC";
        
        try (Connection conn = DatabaseConn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                visitors.add(mapResultSetToVisitor(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all visitors: " + e.getMessage());
        }
        
        return visitors;
    }
    
    /**
     * Get visitors by date range
     */
    public List<Visitor> getVisitorsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Visitor> visitors = new ArrayList<>();
        String sql = "SELECT * FROM visitors_logbook WHERE timestamp BETWEEN ? AND ? ORDER BY timestamp DESC";
        
        try (Connection conn = DatabaseConn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                visitors.add(mapResultSetToVisitor(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting visitors by date range: " + e.getMessage());
        }
        
        return visitors;
    }
    
    /**
     * Get visitors by municipality
     */
    public List<Visitor> getVisitorsByMunicipality(String municipality) {
        List<Visitor> visitors = new ArrayList<>();
        String sql = "SELECT * FROM visitors_logbook WHERE municipality = ? ORDER BY timestamp DESC";
        
        try (Connection conn = DatabaseConn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, municipality);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                visitors.add(mapResultSetToVisitor(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting visitors by municipality: " + e.getMessage());
        }
        
        return visitors;
    }
    
    /**
     * Update visitor information
     */
    public boolean updateVisitor(Visitor visitor) {
        String sql = "UPDATE visitors_logbook SET last_name=?, first_name=?, middle_name=?, province=?, " +
                    "municipality=?, barangay=?, age=?, gender=?, phone_number=?, email=?, sector=?, " +
                    "purpose=?, signature_path=?, photo_path=? WHERE id=?";
        
        try (Connection conn = DatabaseConn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, visitor.getLastName());
            pstmt.setString(2, visitor.getFirstName());
            pstmt.setString(3, visitor.getMiddleName());
            pstmt.setString(4, visitor.getProvince());
            pstmt.setString(5, visitor.getMunicipality());
            pstmt.setString(6, visitor.getBarangay());
            pstmt.setObject(7, visitor.getAge());
            pstmt.setString(8, visitor.getGender());
            pstmt.setString(9, visitor.getPhoneNumber());
            pstmt.setString(10, visitor.getEmail());
            pstmt.setString(11, visitor.getSector());
            pstmt.setString(12, visitor.getPurpose());
            pstmt.setString(13, visitor.getSignaturePath());
            pstmt.setString(14, visitor.getPhotoPath());
            pstmt.setInt(15, visitor.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating visitor: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete visitor by ID
     */
    public boolean deleteVisitor(int id) {
        String sql = "DELETE FROM visitors_logbook WHERE id = ?";
        
        try (Connection conn = DatabaseConn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting visitor: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get visitor statistics
     */
    public String getVisitorStatistics() {
        String sql = "SELECT COUNT(*) as total, " +
                    "COUNT(CASE WHEN gender = 'Male' THEN 1 END) as male, " +
                    "COUNT(CASE WHEN gender = 'Female' THEN 1 END) as female, " +
                    "COUNT(DISTINCT municipality) as municipalities " +
                    "FROM visitors_logbook";
        
        try (Connection conn = DatabaseConn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                int total = rs.getInt("total");
                int male = rs.getInt("male");
                int female = rs.getInt("female");
                int municipalities = rs.getInt("municipalities");
                
                return String.format("Total Visitors: %d | Male: %d | Female: %d | Municipalities: %d", 
                                   total, male, female, municipalities);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting visitor statistics: " + e.getMessage());
        }
        
        return "Statistics unavailable";
    }
    
    /**
     * Map ResultSet to Visitor object
     */
    private Visitor mapResultSetToVisitor(ResultSet rs) throws SQLException {
        Visitor visitor = new Visitor();
        visitor.setId(rs.getInt("id"));
        visitor.setLogbookNumber(rs.getString("logbook_number"));
        visitor.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        visitor.setLastName(rs.getString("last_name"));
        visitor.setFirstName(rs.getString("first_name"));
        visitor.setMiddleName(rs.getString("middle_name"));
        visitor.setProvince(rs.getString("province"));
        visitor.setMunicipality(rs.getString("municipality"));
        visitor.setBarangay(rs.getString("barangay"));
        visitor.setAge(rs.getObject("age", Integer.class));
        visitor.setGender(rs.getString("gender"));
        visitor.setPhoneNumber(rs.getString("phone_number"));
        visitor.setEmail(rs.getString("email"));
        visitor.setSector(rs.getString("sector"));
        visitor.setPurpose(rs.getString("purpose"));
        visitor.setSignaturePath(rs.getString("signature_path"));
        visitor.setPhotoPath(rs.getString("photo_path"));
        visitor.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        visitor.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        
        return visitor;
    }
} 