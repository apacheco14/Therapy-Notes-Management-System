package main.java.com.alexpacheco.therapynotes.model.dao;

import java.util.List;

import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOptionFactory;
import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOptionType;
import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOption;
import main.java.com.alexpacheco.therapynotes.util.DbUtil;

import java.sql.*;
import java.util.ArrayList;

public class AssessmentOptionsDao
{
	/**
	 * Inserts a new assessment option into the database.
	 */
	public void createOption(AssessmentOption option) throws SQLException
	{
		String sql = "INSERT INTO assessment_options (type, name, description) VALUES (?, ?, ?)";
		
		try (Connection conn = DbUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			pstmt.setString(1, option.getOptionType().getDbTypeKey());
			pstmt.setString(2, option.getName());
			pstmt.setString(3, option.getDescription());
			pstmt.executeUpdate();
		}
	}
	
	/**
	 * Inserts new assessment options into the database in a single database transaction.
	 */
	public void createOptionsBatch(List<AssessmentOption> options) throws SQLException
	{
		String sql = "INSERT INTO assessment_options (type, name, description) VALUES (?, ?, ?)";
		
		Connection conn = null;
		try
		{
			conn = DbUtil.getConnection();
			conn.setAutoCommit(false); // Start Transaction
			
			try (PreparedStatement pstmt = conn.prepareStatement(sql))
			{
				for (AssessmentOption option : options)
				{
					pstmt.setString(1, option.getOptionType().getDbTypeKey());
					pstmt.setString(2, option.getName());
					pstmt.setString(3, option.getDescription());
					pstmt.addBatch(); // Add to the local buffer
				}
				
				pstmt.executeBatch(); // Send all updates to the DB at once
				conn.commit(); // Finalize the changes
			}
		}
		catch (SQLException e)
		{
			if (conn != null)
			{
				conn.rollback(); // Undo everything if one update fails
			}
			throw e;
		}
		finally
		{
			if (conn != null)
			{
				conn.setAutoCommit(true);
				conn.close();
			}
		}
	}
	
	/**
	 * Updates a single existing option.
	 */
	public void updateOption(AssessmentOption option) throws SQLException
	{
		String sql = "UPDATE assessment_options SET name = ?, description = ? WHERE id = ?";
		
		try (Connection conn = DbUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			pstmt.setString(1, option.getName());
			pstmt.setString(2, option.getDescription());
			pstmt.setInt(3, option.getId());
			
			int affectedRows = pstmt.executeUpdate();
			if (affectedRows == 0)
			{
				throw new SQLException("Update failed: Option ID " + option.getId() + " not found.");
			}
		}
	}
	
	/**
	 * Updates multiple options in a single database transaction.
	 */
	public void updateOptionsBatch(List<AssessmentOption> options) throws SQLException
	{
		String sql = "UPDATE assessment_options SET name = ?, description = ?, type = ? WHERE id = ?";
		
		Connection conn = null;
		try
		{
			conn = DbUtil.getConnection();
			conn.setAutoCommit(false); // Start Transaction
			
			try (PreparedStatement pstmt = conn.prepareStatement(sql))
			{
				for (AssessmentOption option : options)
				{
					pstmt.setString(1, option.getName());
					pstmt.setString(2, option.getDescription());
					pstmt.setString(3, option.getOptionType().getDbTypeKey());
					pstmt.setInt(4, option.getId());
					pstmt.addBatch(); // Add to the local buffer
				}
				
				pstmt.executeBatch(); // Send all updates to the DB at once
				conn.commit(); // Finalize the changes
			}
		}
		catch (SQLException e)
		{
			if (conn != null)
			{
				conn.rollback(); // Undo everything if one update fails
			}
			throw e;
		}
		finally
		{
			if (conn != null)
			{
				conn.setAutoCommit(true);
				conn.close();
			}
		}
	}
	
	/**
	 * Retrieves all options from the table.
	 */
	public List<AssessmentOption> getOptions() throws SQLException
	{
		List<AssessmentOption> options = new ArrayList<>();
		String sql = "SELECT * FROM assessment_options WHERE inactive = 0 ORDER BY type, name ASC";
		
		try (Connection conn = DbUtil.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql))
		{
			while (rs.next())
			{
				AssessmentOption option = AssessmentOptionFactory.createAssessmentOption(rs.getInt("id"), rs.getString("name"),
						rs.getString("description"), rs.getString("type"));
				options.add(option);
			}
		}
		return options;
	}
	
	/**
	 * Retrieves options of a certain type.
	 */
	public List<AssessmentOption> getOptions(AssessmentOptionType type) throws SQLException
	{
		List<AssessmentOption> options = new ArrayList<>();
		String sql = "SELECT * FROM assessment_options WHERE inactive = 0 AND type = ? ORDER BY name ASC";
		
		try (Connection conn = DbUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			pstmt.setString(1, type.getDbTypeKey());
			
			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					AssessmentOption option = AssessmentOptionFactory.createAssessmentOption(rs.getInt("id"), rs.getString("name"),
							rs.getString("description"), rs.getString("type"));
					options.add(option);
				}
			}
		}
		
		return options;
	}
	
	public AssessmentOption getOption(Integer assessmentOptionId) throws SQLException
	{
		String sql = "SELECT * FROM assessment_options WHERE id = ?";
		try (Connection conn = DbUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			pstmt.setInt(1, assessmentOptionId);
			
			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					return AssessmentOptionFactory.createAssessmentOption(rs.getInt("id"), rs.getString("name"),
							rs.getString("description"), rs.getString("type"));
				}
			}
		}
		
		return null;
	}
}