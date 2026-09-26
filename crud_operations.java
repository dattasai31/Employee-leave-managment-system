package com.employee_leave_managment;
import java.util.*;



import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class crud_operations {
	
	
	// method for registering new employee
	static void employeeregistration(Connection conn,Scanner sc)
	{
		try
		{
			System.out.println("Enter employee name:");
			String ename=sc.nextLine();
			if(validation_employee.isempty(ename))
			{
				System.out.println("Please enter valid employee name");
				System.out.println("---------------------------");
				return;
			}
			System.out.println("Enter email id:");
			String email=sc.nextLine();
			if(!(validation_employee.validateemail(email)))
			{
				System.out.println("Enter valid email id");
				System.out.println("----------------------");
				return;
			}
			System.out.println("Enter mobile number:");
			String mobile=sc.nextLine();

			if(!(validation_employee.validmobilenumber(mobile)))
			{
				System.out.println("Please enter valid 10 digit mobile number");
				System.out.println("------------------------------------------");
				return;
			}

			if(validation_employee.uniquemobile(conn, mobile))
			{
				System.out.println("This number is already registered");
				System.out.println("----------------------------------");
				return;
			}
			System.out.println("Enter department name:");
			String dept=sc.nextLine();
			System.out.println("Enter designation:");
			String desig=sc.nextLine();
			
			
			String sql="insert into employees(emp_name,email,mobile_number,department,designation) values(?,?,?,?,?)";
			int gempid=0;
			try(PreparedStatement p=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS))
			{
				p.setString(1, ename);
				p.setString(2, email);
				p.setString(3, mobile);
				p.setString(4, dept);
				p.setString(5, desig);
				int rows=p.executeUpdate();
				if(rows>0)
				{
					ResultSet rst=p.getGeneratedKeys();
					if(rst.next())
					{
						gempid=rst.getInt(1);
					}
				}
			}
			String leavbal="insert into leavebalance(emp_id) values(?)";
			try(PreparedStatement pst=conn.prepareStatement(leavbal))
			{
				pst.setInt(1, gempid);
				pst.executeUpdate();
				
			}
			conn.commit();
			System.out.println("Employee details added successfully");
			System.out.println("-----------------------------------");
		}
		catch(SQLException e)
		{
			try 
			{
				conn.rollback();
				System.out.println("Transactions are roll back");
			}
			catch(SQLException ex)
			{
				System.out.println(ex.getMessage());
			}
			System.out.println(e.getMessage());
		}
	}
	
	
	// method for applying leave
	static void applyleave(Connection conn,Scanner sc)
	{
		try
		{
			System.out.println("Enter employee id:");
			int empid=sc.nextInt();
			sc.nextLine();
			if(!(validation_employee.checkemployye(conn, empid)))
			{
				System.out.println("Employee not found with this ID");
				System.out.println("-----------------------------------");
				return;
			}
			System.out.println("Enter leave type:");
			String leave_type=sc.nextLine();
			if(!(validation_employee.validateleave(leave_type)))
			{
				System.out.println("Please select valid leave");
				System.out.println("---------------------------");
				return;
			}
			System.out.println("Enter from date (YYYY-MM-DD)");
			String fromdate=sc.nextLine();
			LocalDate fdate=LocalDate.parse(fromdate);
			System.out.println("Enter to date (YYYY-MM-DD)");
			String todate=sc.nextLine();
			LocalDate tdate=LocalDate.parse(todate);
			long days=ChronoUnit.DAYS.between(fdate, tdate)+1;
			
			if(!(validation_employee.validatedate(fdate,tdate)))
			{
				System.out.println("---------------------------------------");
				return;
				
			}
			String query="insert into leaverequest(emp_id,leave_type,from_date,to_date,total_days) values(?,?,?,?,?)";
			try(PreparedStatement pst=conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
			{
				pst.setInt(1, empid);
				pst.setString(2, leave_type);
				pst.setString(3, fromdate);
				pst.setString(4, todate);
				pst.setLong(5, days);
				int rows=pst.executeUpdate();
				if(rows>0)
	            {
	                int requestid=0;
	                try(ResultSet rst=pst.getGeneratedKeys())
	                {
	                    if(rst.next())
	                    {
	                        requestid=rst.getInt(1);
	                    }
	                }
	                conn.commit();
	                System.out.println("Leave applied successfully with request ID: "+requestid+", wait for approval");
	                System.out.println("---------------------------------------------");
	            }
	
			}
				
		}
		catch(SQLException ex)
		{
			try
			{
				conn.rollback();
				System.out.println("Transactions are rollback");
			}
			catch(SQLException e)
			{
				System.out.println(e.getMessage());
			}
			System.out.println(ex.getMessage());
		}
		catch(java.time.format.DateTimeParseException e)
		{
		    System.out.println("Invalid date format. Please use YYYY-MM-DD.");
		    System.out.println("-----------------------------------------------");
		}
		
	}
	
	
	// method for leave approval
	static void leaveapproval(Connection conn,Scanner sc)
	{
		try
		{
			String query="select * from leaverequest";
			try(Statement s=conn.createStatement())
			{
				ResultSet rst=s.executeQuery(query);
				while(rst.next())
				{
					System.out.println("Request ID:"+rst.getInt(1)+" |Employee ID:"+rst.getInt(2)+" |Leave type:"+rst.getString(3)+" |From date:"+rst.getDate(4)+" |To date:"+rst.getDate(5)+" |Total Days:"+rst.getInt(6)+" |Status:"+rst.getString(7));
				}
			}
			System.out.println("Enter the leave request ID: ");
			int id=sc.nextInt();
			if(!(validation_employee.checkvalidleaveid(conn, id)))
			{
				System.out.println("No leave found with this id");
				System.out.println("-------------------------------");
				return;
			}
			
			sc.nextLine();
			
			int empid=0;
	        String leavetype=null;
	        int totaldays=0;
	        String sql="select emp_id, leave_type, total_days from leaverequest where request_id=? and status='Pending'";
			try(PreparedStatement pst=conn.prepareStatement(sql))
			{
				pst.setInt(1, id);
				ResultSet rst=pst.executeQuery();
				if(!(rst.next()))
				{
					System.out.println("This request has already been processed or does not exist as pending");
					System.out.println("-------------------------------------");
					return;
				}
				empid=rst.getInt(1);
				leavetype=rst.getString(2);
				totaldays=rst.getInt(3);
			}
			System.out.println("Approve or reject");
			String choice=sc.nextLine();
			if(choice.equalsIgnoreCase("reject"))
			{
				String sq="update leaverequest set status='Rejected' where request_id=?";
				try(PreparedStatement p=conn.prepareStatement(sq))
				{
					p.setInt(1, id);
					int rows=p.executeUpdate();
					if(rows>0)
					{
						System.out.println("Leave rejected");
						System.out.println("-------------------------------------");
						conn.commit();
					}
				}
				return;
			}
			String balancecol;
			if(leavetype.equalsIgnoreCase("sick leave"))
			{
				balancecol="sick_leave";
			}
			else
			{
				balancecol="casual_leave";
			}
			String leave="select "+balancecol+" from leavebalance where emp_id=?";
			int avaliable;
			try(PreparedStatement p1=conn.prepareStatement(leave))
			{
				p1.setInt(1, empid);
				ResultSet rb=p1.executeQuery();
				if(rb.next())
				{
					avaliable=rb.getInt(1);
				}
				else
				{
					avaliable=0;
				}
			}
			if(totaldays<=0)
			{
				System.out.println("Invalid leave request, cannot approve");
				System.out.println("-----------------------------------------");
				return;
			}
			if(avaliable<totaldays)
			{
				System.out.println("Insufficent leaves");
				return;
			}
			String l1="update leaverequest set status='Approved' where request_id=?";
			try(PreparedStatement ps=conn.prepareStatement(l1))
			{
				ps.setInt(1, id);
				ps.executeUpdate();
			}
			String l2="update leavebalance set "+balancecol+" = "+balancecol+" - ? where emp_id=?";
			try(PreparedStatement pss=conn.prepareStatement(l2))
			{
				pss.setInt(1, totaldays);
				pss.setInt(2, empid);
				pss.executeUpdate();
			}
			conn.commit();
			System.out.println("Leave approved succesfully, balance updated");
			System.out.println("---------------------------------------------------");
		}
		catch(SQLException e)
		{
			try
			{
				conn.rollback();
				System.out.println("Transactions are rollback");
			}
			catch(SQLException ex)
			{
				System.out.println(ex.getMessage());
			}
			System.out.println(e.getMessage());
		}
		
	}
	
	
	// method to view leave request status
	static void viewleaverequest(Connection conn,Scanner sc)
	{
		try 
		{
			
			System.out.println("Enter request ID:");
			int rid=sc.nextInt();
			sc.nextLine();
			String query="select * from leaverequest where request_id=?";
			try(PreparedStatement pst=conn.prepareStatement(query))
			{
				pst.setInt(1, rid);
				ResultSet rst=pst.executeQuery();
				boolean found=false;
				while(rst.next())
				{
					found=true;
					System.out.println("Request ID:"+rst.getInt(1)+"| Employee ID:"+rst.getInt(2)+"| Status:"+rst.getString("status"));
					System.out.println("--------------------------------------------------------");
				}
				if(!found)
				{
					System.out.println("No leave request with this id");
					System.out.println("----------------------------------");
				}
			}
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	
	// method to track leave balance
	static void leavebalancetrack(Connection conn,Scanner sc)
	{
		try
		{
			System.out.println("Enter emplyee id:");
			int empid=sc.nextInt();
			sc.nextLine();
			String query="select * from leavebalance where emp_id=?";
			try(PreparedStatement pst=conn.prepareStatement(query))
			{
				pst.setInt(1, empid);
				ResultSet rst=pst.executeQuery();
				if(rst.next())
				{
					System.out.println("Employee id:"+rst.getInt(1)+"| Sick leave:"+rst.getInt(2)+"| Casual leave:"+rst.getInt(3));
					System.out.println("----------------------------------------------------------");
				}
				else
				{
					System.out.println("No employee found with this id");
					System.out.println("--------------------------------------------");
				}
			}
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}

}
