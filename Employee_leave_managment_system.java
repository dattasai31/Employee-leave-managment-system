package com.employee_leave_managment;
import java.sql.*;
import java.sql.Connection;
import java.util.Scanner;

public class Employee_leave_managment_system {
	static final String url="jdbc:mysql://localhost:3306/jdbc";
	static final String uname="root";
	static final String pwd="Dattasai@78";
	static Connection con=null;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			con=DriverManager.getConnection(url,uname,pwd);
			con.setAutoCommit(false);
			createtableemployees(con); //creating table for employees
			createtableleaverequests(con);// creating table for leave requests
			createtableleavebalance(con);// creating table for leave balance
			Scanner sc=new Scanner(System.in);
			int choice;
			do
			{
				System.out.println("1.Register new employee");
				System.out.println("2.Apply leave");
				System.out.println("3.Approve/Reject leave");
				System.out.println("4.View leave status");
				System.out.println("5.Check leave balance");
				System.out.println("0.Exit");
				System.out.println("Enter your choice:");
				choice=sc.nextInt();
				sc.nextLine();
				
				switch(choice)
				{
				case 1:
					crud_operations.employeeregistration(con, sc);
					break;
				case 2:
					crud_operations.applyleave(con, sc);
					break;
				case 3:
					crud_operations.leaveapproval(con, sc);
					break;
				case 4:
					crud_operations.viewleaverequest(con, sc);
					break;
				case 5:
					crud_operations.leavebalancetrack(con, sc);
					break;
				case 0:
					System.out.println("Exiting.....");
					break;
				default:
					System.out.println("Enter valid choice");
					break;
				}
			}while(choice!=0);
	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(con!=null)
				{
					con.close();
				}
			}
			catch(SQLException ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	
	
	// method for creating employees table
	static void createtableemployees(Connection con) throws SQLException
	{
		String query="""
				create table if not exists employees(
				emp_id int primary key auto_increment,
				emp_name varchar(100) not null,
				email varchar(50) not null,
				mobile_number varchar(15) unique,
				department varchar(50),
				designation varchar(50))""";
		try(Statement st=con.createStatement())
		{
			st.execute(query);
			System.out.println("Table for employees created successfully");
		}		
	}
	
	
	// method for creating leave requests table
	static void createtableleaverequests(Connection con) throws SQLException
	{
		String query="""
				create table if not exists leaverequest(
				request_id int primary key auto_increment,
				emp_id int not null,
				leave_type varchar(20) not null,
				from_date date not null,
				to_date date not null,
				total_days int not null,
				status varchar(20) default 'Pending',
				foreign key (emp_id) references employees(emp_id))""";
		try(Statement st=con.createStatement())
		{
			st.execute(query);
			System.out.println("Table for leave request created successfully");
		}
			
		
	// method for creating leave balance table
	}
	static void createtableleavebalance(Connection con) throws SQLException
	{
		String query="""
				create table if not exists leavebalance(
				emp_id int primary key,
				sick_leave int default 12,
				casual_leave int default 12,
				foreign key (emp_id) references employees(emp_id))""";
		try(Statement st=con.createStatement())
		{
			st.execute(query);
			System.out.println("Table for leave balance created");
		}
			
	}

}
