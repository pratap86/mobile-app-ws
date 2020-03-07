package com.pratap.app.test;

import java.sql.Connection;
import java.sql.DriverManager;

public class SqlConnectionTest {

	@SuppressWarnings("unused")
	public static void main(String[] args) {

		String JdbcURL = "jdbc:mysql://localhost:3306/photo_app?useSSL=false";
	      String Username = "pratap";
	      String password = "pratap";
	      Connection con = null;
	      try {
	         con = DriverManager.getConnection(JdbcURL, Username, password);
	         System.out.println("Connected to MySQL database");
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	}

}
