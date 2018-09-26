
//  Class:          CSE3330
//  Semester:       Spring 2018
//  Student Name:   Vu, Nhan, ntv3930
//  Student ID:     1001193930
//  Assignment:     project #3

import java.util.*; 
import java.io.*;
import java.sql.*;

final class ntv3930_P3{
    
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
                option4();
            }else if(choice.equals("5")){
                break;
            }else if(choice.equals("egg1")){
                easterEgg1();
            }else if(choice.equals("egg2")){
                easterEgg2();
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
		System.out.println(	"1. View of Unassigned Pilot Flights");
        System.out.println(	"2. View of Due For Maintenance Planes");
		System.out.println(	"3. View of Pilot Fly Assignments");
		System.out.println(	"4. View of Pilot FlightLegs Count");
		System.out.println(	"5. Quit");
        System.out.println( "----------------------------------------");
	}

    static void option1()throws Exception{
        
        //make first view
        String code =   "create or replace view G as ";
        
        code +=         "select FL.FLNO , FL.Seq, FI.FDate ";
        code +=         "from   FlightLeg as FL , FlightInstance as FI ";
        code +=         "where  FL.FLNO = FI.FLNO;\n ";
        
        try {
            Statement st = con.createStatement();
            st.executeUpdate(code);          
        } catch (SQLException s) {
            System.out.println("Unexpected error with view 1");
        }  
       
        //second view based off first
        code =          "create or replace view UnassignedPilotFlights as ";
       
        code +=         "select G.FLNO , G.Seq, G.FDate ";
        code +=         "from   G ";
        code +=         "where  not exists ";
        
        code +=         "       (";
        code +=         "           select  FLI.FLNO, FLI.Seq, FLI.FDate ";
        code +=         "           from    FlightLegInstance as FLI ";
        code +=         "       );\n ";

         try {
            Statement st = con.createStatement();
            st.executeUpdate(code);          
        } catch (SQLException s) {
            System.out.println("Unexpected error with final view");
        }   

        //printing of chart
        System.out.println("");
        System.out.println("        UnassignedPilotFlights");
        System.out.println("----------------------------------------");
        System.out.printf("|%-12s|%-12s|%-12s|\n", "FLNO","Seq","FDate");
        System.out.println("----------------------------------------");
        //printing view to screen
        ResultSet rs = stmt.executeQuery("select * from UnassignedPilotFlights;\n");
        while (rs.next()){
            System.out.printf( "|%-12s|%-12s|%-12s|\n",
                                rs.getString("G.FLNO"),
                                rs.getString("G.Seq"),
                                rs.getString("G.FDate"));
        }
        System.out.println("----------------------------------------");
        rs.close();

        System.out.println("View made successfully");    
    }

    static void option2()throws Exception{
        //making the view
        String code =   "create or replace view DueForMaintenacePlanes as ";

        code +=         "select     ID, Maker, Model, LastMaint ";
        code +=         "from       Plane ";
        code +=         "where      LastMaint not between date_sub(curdate(), interval -60 day) and curdate();";

        try {
            Statement st = con.createStatement();
            st.executeUpdate(code);          
        } catch (SQLException s) {
            System.out.println("Unexpected error with view");
        }          

        //printing view to screen
        ResultSet rs = stmt.executeQuery("select * from DueForMaintenacePlanes;\n");

        System.out.println("");
        System.out.println("              DueForMaintenacePlanes");
        System.out.println("--------------------------------------------------");
        System.out.printf("|%-3s|%-15s|%-15s|%-12s|\n", "ID","Maker","Model","LastMaint");
        System.out.println("--------------------------------------------------");
        while (rs.next()){
            System.out.printf("|%-3s|%-15s|%-15s|%-12s|\n",
                                rs.getString("ID"),
                                rs.getString("Maker"),
                                rs.getString("Model"),
                                rs.getString("LastMaint"));
        }
        System.out.println("--------------------------------------------------");
        rs.close();
    }

    static void option3()throws Exception{
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

        //printing view to screen
        ResultSet rs = stmt.executeQuery("select * from PilotFlyAssignments;\n");

        System.out.println("");
        System.out.println("              PilotFlyAssignments");
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

    static void option4()throws Exception{
        //making the view
        String code =   "create or replace view PilotFlightLegsCount as ";

        code +=         "select     P.ID, P.Name, ";
        code +=         "           date_format(FLI.FDate, \"%Y-%m\") as Month_year,";
        code +=         "           count(*) as Flight_Legs_Count ";
        code +=         "from       FlightLegInstance as FLI, Pilot as P ";
        code +=         "where      P.ID = FLI.Pilot ";
        code +=         "group by   P.ID, year(FDate), month(FDate);";

        try {
            Statement st = con.createStatement();
            st.executeUpdate(code);          
        } catch (SQLException s) {
            System.out.println("Unexpected error with view");
        }      

        //printing view to screen
        ResultSet rs = stmt.executeQuery("select * from PilotFlightLegsCount;\n");

        System.out.println("");
        System.out.println("              PilotFlightLegsCount");
        System.out.println("------------------------------------------------");
        System.out.printf("|%-3s|%-10s|%-12s|%-18s|\n", "ID","Name","Month_year","Flight_Legs_Count");
        System.out.println("------------------------------------------------");
        while (rs.next()){
            System.out.printf("|%-3s|%-10s|%-12s|%-18s|\n",
                                rs.getString("ID"),
                                rs.getString("Name"),
                                rs.getString("Month_year"),
                                rs.getString("Flight_Legs_Count"));
        }
        System.out.println("------------------------------------------------");
        rs.close();

    }

    static void easterEgg1()throws Exception{
        ResultSet rs = stmt.executeQuery("select * from Plane");
        while (rs.next()){
            System.out.println(rs.getString("Maker")+" "+rs.getString("Model"));
        }
        rs.close();
    }

    static void easterEgg2()throws Exception{
        try {
            Statement st = con.createStatement();
            String code = " Create VIEW vp As select * from Plane";
            st.executeUpdate(code);          
            System.out.println(" VIEW successfully created!");
        } catch (SQLException s) {
            System.out.println("VIEW already exists!");
        }   
    }


}

