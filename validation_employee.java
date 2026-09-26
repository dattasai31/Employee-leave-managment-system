package com.employee_leave_managment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class validation_employee {
	
	
	// method to check whether the string is empty or not
	static boolean isempty(String s)
	{
	    if(s == null || s.isEmpty())
	    {
	        return true;
	    }
	    else
	    {
	        return false;
	    }
	}
	
	
	// method to validate email
	static boolean validateemail(String s)
	{
		if(s!=null && !(s.isEmpty()))
		{
			if(s.contains("@") && s.contains("."))
			{
				return true;
			}
		}
		return false;
	}
	
	
	// method to check whether the mobile number entered is valid or not
	static boolean validmobilenumber(String s)
	{
		if(s==null || s.isEmpty())
		{
			return false;
		}
		return s.matches("\\d{10}");
	}
	
	
	// method to check whether the mobile number entered is unique or not
	static boolean uniquemobile(Connection con,String s) throws SQLException
	{
		String query="select * from employees where mobile_number=?";
		try(PreparedStatement pst=con.prepareStatement(query))
		{
			pst.setString(1,s);
			ResultSet rst=pst.executeQuery();
			return rst.next();
		}
	}
	
	
	// method to validate date
	static boolean validatedate(LocalDate fromdate,LocalDate todate)
	{
		LocalDate today=LocalDate.now();
		if(fromdate.isBefore(today))
		{
			System.out.println("From date cannot be before today");
			return false;
		}
		if(todate.isBefore(fromdate))
		{
			System.out.println("To date cannot be before from date");
			return false;
		}
		return true;
	}
	
	
	// method to check whether this employee is registered or not
	static boolean checkemployye(Connection conn,int empid) throws SQLException
	{
		String query="select * from employees where emp_id=?";
		try(PreparedStatement pst=conn.prepareStatement(query))
		{
			pst.setInt(1,empid);
			ResultSet rst=pst.executeQuery();
			return rst.next();
		}
	}
	
	
	// method to validate leave
	static boolean validateleave(String s)
	{
		if(s.equalsIgnoreCase("sick leave") || s.equalsIgnoreCase("casual leave"))
		{
			return true;
		}
		return false;
	}
	
	
	// method to check whether the leave id is valid or not
	static boolean checkvalidleaveid(Connection con,int leaveid) throws SQLException
	{
		String query="select  * from leaverequest where request_id=?";
		try(PreparedStatement pst=con.prepareStatement(query))
		{
			pst.setInt(1, leaveid);
			ResultSet rst=pst.executeQuery();
			return rst.next();
		}
	}
	
	

}




