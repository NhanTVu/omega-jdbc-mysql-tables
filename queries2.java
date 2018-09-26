
//  Class:          CSE3330
//  Semester:       Spring 2018
//  Student Name:   Vu, Nhan, ntv3930
//  Student ID:     1001193930
//  Assignment:     project #4

import java.util.*; 
import java.io.*;
import java.sql.*;
import java.text.*;

final class ntv3930_P4{
    
    final static String user = /*getInput("Username: ");//*/"ntv3930";
    final static String password = /*getInput("Password: ");//*/"Apple123";
    final static String db = /*getInput("Database name: ");//*/"ntv3930";
    final static String jdbc = "jdbc:mysql://localhost:3306/"+db+"?user="+user+"&password="+password;

    static Connection con;
    static Statement stmt;

    public static void main ( String[] args ) throws Exception {

    	//Loading driver
        try{
        	Class.forName("com.mysql.jdbc.Driver").newInstance();	
        }catch(Exception e){
        	System.out.println("JDBC MySQL driver failed to load");
        }
        
        //Establishing connection
        con = DriverManager.getConnection(jdbc);
        stmt = con.createStatement();

        //make view from v3 project 3
        makeView();

        while(true){
            printMenu();
            String choice = getInput("Input: ");
            System.out.println( "----------------------------------------");

            if(choice.equals("1")){
                option1();
            }else if(choice.equals("2")){
                option2();
            }else if(choice.equals("3")){
                option3();
            }else if(choice.equals("4")){
                break;
            }else{
                System.out.println("Invalid input");
            } 
        }
    
        stmt.close();
        con.close();
    }

    //print a request and then retrieve an answer as a string
	static String getInput(String request){
		Scanner scanner = new Scanner(System.in);
  		System.out.print(request);
  		return scanner.nextLine();
	} 

	//print Menu for user
	static void printMenu(){
        System.out.println( "");
        System.out.println( "----------------------------------------");
		System.out.println(	"1. Check if Pilot is busy on a certain day ");
        System.out.println( "   and show the pilot assignments for this day");
        System.out.println(	"2. Assign a Pilot to a flight leg instance");
		System.out.println(	"3. Add a Pilot");
		System.out.println(	"4. Quit");
        System.out.println( "----------------------------------------");
	}

    static void option1()throws Exception{
        
        String name = getInput("Enter Pilot's name: ");

        //get info
        System.out.println("Enter a date in the form year-month-day");
        System.out.println("Example: 2018-7-23");
        String date = getInput("Date: "); 
        
        //parse 
        if( date.matches("\\d+-\\d+-\\d+")){
            //if in right format separate the days
            String[] numbers = date.split("-");
            int year = Integer.parseInt(numbers[0]);
            int month = Integer.parseInt(numbers[1]);
            int day = Integer.parseInt(numbers[2]);
            //check if month, days, and year are valid 
            if(month > 12 || month < 1){
                System.out.println("Month must be a number 1-12");
            }else if(day > 31 || day < 1){
                System.out.println("Day myst be a number 1-31");
            }else if(year < 0){
                System.out.println("Year must be a positive number");
            }else{
                //if all format is right
                System.out.println("Success");

                String code =   "select * ";
                code +=         "from   PilotFlyAssignments ";
                code +=         "where  FDate = '"+date;
                code +=        "' and Name = '"+name+"';\n";

               ResultSet rs = stmt.executeQuery(code);

                System.out.println("");
                System.out.println("            Pilot "+name+"'s Schedule on "+date);
                System.out.println("-------------------------------------------------------------------");
                System.out.printf("|%-3s|%-10s|%-5s|%-12s|%-15s|%-15s|\n", "ID","Name","FLNO","FDate","From","To");
                System.out.println("-------------------------------------------------------------------");
                while (rs.next()){
                    System.out.printf("|%-3s|%-10s|%-5s|%-12s|%-15s|%-15s|\n",
                                        rs.getString("ID"),
                                        rs.getString("Name"),
                                        rs.getString("FLNO"),
                                        rs.getString("FDate"),
                                        rs.getString("F"),
                                        rs.getString("T"));
                }
                System.out.println("-------------------------------------------------------------------");
                rs.close();

            }
        }else{
            System.out.println("The format you've used was invalid");
        }

         
    }

    static void option2()throws Exception{
        
        String Id = getInput("Enter Pilot ID: ");
        String num = getInput("Enter FLNO of FlightLegInstance: ");
        String Seq = getInput("Enter Seq of FlightLegInstance: ");
        System.out.println("Enter the date in the form year-month-day");
        System.out.println("Example: 2018-7-23");
        String FDate = getInput("Enter FDate of FlightLegInstance: ");

        String code =   "Update FlightLegInstance ";
        code +=         "set    Pilot = "+Id;
        code +=        " where  FLNO = "+num;
        code +=        " and    Seq = "+Seq;
        code +=        " and    FDate = '"+FDate+"';\n";

        stmt.executeUpdate(code);

        code = "select * from FlightLegInstance;";

        ResultSet rs = stmt.executeQuery(code);

        System.out.println("");
        System.out.println("                 Update FlightLegInstance");
        System.out.println("----------------------------------------------------------");
        System.out.printf("|%-6s|%-3s|%-12s|%-12s|%-12s|%-6s|\n", "FLNO","Seq","FDate","ActDept","ActArr","Pilot");
        System.out.println("----------------------------------------------------------");
        while (rs.next()){
            System.out.printf("|%-6s|%-3s|%-12s|%-12s|%-12s|%-6s|\n",
                                        rs.getString("FLNO"),
                                        rs.getString("Seq"),
                                        rs.getString("FDate"),
                                        rs.getString("ActDept"),
                                        rs.getString("ActArr"),
                                        rs.getString("Pilot"));
        }
        System.out.println("----------------------------------------------------------");
        rs.close();

    }

    static void option3()throws Exception{
        boolean halt = false;
        boolean confirmationNeeded = false;

        String Id = getInput("Enter new pilot ID: ");
        String name = getInput("Enter name of new pilot: ");
        System.out.println("Enter the date in the form year-month-day");
        System.out.println("Example: 2018-7-23");
        String date = getInput("Enter Date Hired: ");

        String code = "select * from Pilot";
        ResultSet rs = stmt.executeQuery(code);

        while (rs.next()){
            if (Id.equals(rs.getString("ID"))){
                halt = true;
                System.out.println("Invalid! an existing pilot already has that ID");
            }else if(name.equals(rs.getString("Name"))){
                System.out.println("An existing pilot already has that name!");
                System.out.println("Would you like to add anyways?");
                String choice = getInput("Y/N: ");
                if(choice.equals("Y") || choice.equals("y")){
                    halt = false;
                    System.out.println("Continuing operation.");
                }else if(choice.equals("N") || choice.equals("n")){
                    halt = true;
                    System.out.println("Halting operation.");
                }else{
                    System.out.println("Invalid response. Halting operation.");
                    halt = true;
                }
            }
        }

        if (!halt){
            //actual adding of the new pilot
            code =   "insert into Pilot " +
                     "values ("+ Id +",'"+name+"',"+"'"+date+"')";
            stmt.executeUpdate(code); 

            code = "select * from Pilot;";

            rs = stmt.executeQuery(code);

            System.out.println("");
            System.out.println("            Update Pilot");
            System.out.println("---------------------------------------");
            System.out.printf("|%-5s|%-15s|%-15s|\n", "ID","Name","DateHired");
            System.out.println("---------------------------------------");
            while (rs.next()){
                System.out.printf("|%-5s|%-15s|%-15s|\n",
                                            rs.getString("ID"),
                                            rs.getString("Name"),
                                            rs.getString("DateHired"));
            }
            System.out.println("---------------------------------------");
            
        }

        rs.close();  

    }

    static void makeView()throws Exception{
        //making the view 1
        String code =   "create or replace view CC as ";

        code +=         "select     FL.FLNO, FL.Seq, A1.City as F, A2.City as T ";
        code +=         "from       FlightLeg as FL, Airport as A1, Airport as A2 ";
        code +=         "where      A1.Code = FL.FromA and A2.Code = FL.ToA; ";

        try {
            Statement st = con.createStatement();
            st.executeUpdate(code);          
        } catch (SQLException s) {
            System.out.println("Unexpected error with view 1");
        }      

        //making the view 2
        code =          "create or replace view CCP as ";

        code +=         "select     FLI.Pilot , FLI.FLNO, FLI.FDate, CC.F, CC.T ";
        code +=         "from       FlightLegInstance as FLI , CC ";
        code +=         "where      FLI.FLNO = CC.FLNO AND FLI.Seq = CC.Seq; ";

        try {
            Statement st = con.createStatement();
            st.executeUpdate(code);          
        } catch (SQLException s) {
            System.out.println("Unexpected error with view 2");
        }  

        //making the final view
        code =          "create or replace view PilotFlyAssignments as  ";

        code +=         "select Pilot.ID, Pilot.Name, CCP.FLNO, CCP.FDate, CCP.F, CCP.T  ";
        code +=         "from   Pilot, CCP ";
        code +=         "where  Pilot.ID = CCP.Pilot; ";

        try {
            Statement st = con.createStatement();
            st.executeUpdate(code);          
        } catch (SQLException s) {
            System.out.println("Unexpected error with final view");
        }     
    }



}

