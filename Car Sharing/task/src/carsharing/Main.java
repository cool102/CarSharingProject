package carsharing;

import java.sql.*;
import java.util.*;

import static carsharing.MenuState.CUSTOMER_MENU;


enum MenuState {
    WELCOME,
    LOGIN_AS_MANAGER,
    CREATE_COMPANY,
    CHOOSE_THE_COMPANY,
    COMPANY_MENU,
    BACK,
    CREATE_CAR,
    GET_CAR_LIST,

    SELECTING_COMPANY_FROM_DB,
    LOGIN_AS_CUSTOMER,
    CREATE_A_CUSTOMER,
    CUSTOMER_MENU,
    SELECTING_CUSTOMER_FROM_DB,

    CHOOSE_THE_CUSTOMER,

    MY_RENTED_CAR,
    RETURN_A_RENTED_CAR,
    RENT_A_CAR,
    CHOOSE_A_CAR_FROM_DB_MENU,
    CHOOSING_CAR,
    EXIT
}

public class Main {

    private final String EMPTYLIST_COMPANY = "The company list is empty!";
    private final String EMPTYLIST_CAR = "The car list is empty!";
    private final String EMPTYLIST_CUSTOMER = "The customer list is empty!";

    private final String CREATED_COMPANY = "The company was created!";
    private final String CREATED_CAR = "The car was added!";
    private final String CREATED_CUSTOMER = "The customer was added!";
    MenuState state = MenuState.WELCOME;
    private String company_name = "";
    private String customer_name = "";
    private int company_id;
    private int customer_id;

    public static String getDbName(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-databaseFileName")) {
                if (i + 1 < args.length) {
                    return args[i + 1];
                }
            }
        }
        return "carsharing";
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Main main = new Main();

        Class.forName("org.h2.Driver");
        Connection connection = DriverManager.getConnection("jdbc:h2:file:D:\\devel\\Car Sharing1\\Car Sharing\\task\\src\\carsharing\\db\\" + getDbName(args));
        connection.setAutoCommit(true);
        Statement st = connection.createStatement();
        //company
        st.execute("DROP TABLE COMPANY IF EXISTS CASCADE");
        st.execute("CREATE TABLE IF NOT EXISTS  COMPANY (ID INT auto_increment, NAME VARCHAR(50) UNIQUE NOT NULL, PRIMARY KEY (ID))");
        //car
        st.execute("DROP TABLE CAR IF EXISTS CASCADE");
        st.execute("CREATE TABLE IF NOT EXISTS  CAR (ID INT auto_increment primary key, NAME VARCHAR(50) UNIQUE NOT NULL, COMPANY_ID INT NOT NULL)");
        st.execute("ALTER table CAR ADD FOREIGN KEY(COMPANY_ID) REFERENCES COMPANY(ID)");
        //customer
        st.execute("DROP TABLE CUSTOMER IF EXISTS CASCADE");
        st.execute("CREATE TABLE IF NOT EXISTS  CUSTOMER(ID INT auto_increment primary key, NAME VARCHAR(50) UNIQUE NOT NULL, RENTED_CAR_ID INT DEFAULT NULL)");
        st.execute("ALTER table CUSTOMER ADD FOREIGN KEY(RENTED_CAR_ID) REFERENCES CAR(ID)");


        while (main.state != MenuState.EXIT) {

            main.showControls();

            Scanner sc = new Scanner(System.in);
            String choice = sc.nextLine();

            main.invokeInstruction(choice, st);
        }
    }

    public void invokeInstruction(String input, Statement st) throws ClassNotFoundException, SQLException {
        if (state == MenuState.COMPANY_MENU) {
            switch (input) {
                case "1":
                    state = MenuState.GET_CAR_LIST;
                    List<Car> carList = new ArrayList<>();
                    Connection connection3 = DriverManager.getConnection("jdbc:h2:file:../Car Sharing1/task/src/carsharing/db/carsharing");
                    connection3.setAutoCommit(true);
                    Statement st2 = connection3.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * FROM CAR WHERE company_id =" + company_id);

                    while (rs.next()) {
                        int id = rs.getInt("ID");

                        String name = rs.getString("NAME");
                        carList.add(new Car(id, name));
                    }

                    if (carList.isEmpty()) {
                        System.out.println(EMPTYLIST_CAR);

                        state = MenuState.COMPANY_MENU;
                        return;
                    } else {
                        System.out.println("Car list:");
                        for (int i = 0; i < carList.size(); i++) {
                            System.out.printf("%d. %s\n", i + 1, carList.get(i).getName());
                        }
                        // st.close();
                        //  connection3.close();
                        state = MenuState.COMPANY_MENU;
                        return;
                    }

                case "2":
                    state = MenuState.CREATE_CAR;
                    return;
                case "0":
                    state = MenuState.CHOOSE_THE_COMPANY;
                    return;

            }
        }


        if (state == MenuState.WELCOME) {
            switch (input) {
                case "1":
                    state = MenuState.LOGIN_AS_MANAGER;
                    return;
                case "2":
                    state = MenuState.LOGIN_AS_CUSTOMER;
                    return;
                case "3":
                    state = MenuState.CREATE_A_CUSTOMER;
                    return;
                case "0":
                    state = MenuState.EXIT;
                    return;
            }
        }

        if (state == MenuState.LOGIN_AS_MANAGER) {
            switch (input) {
                case "1":
                    state = MenuState.CHOOSE_THE_COMPANY;
                    return;
                case "2":
                    state = MenuState.CREATE_COMPANY;
                    return;
                case "0":
                    state = MenuState.WELCOME;
                    return;
            }
        }


        //in invokeInstructions()
        if (state == CUSTOMER_MENU) {
            switch (input) {
                case "1":
                    state = MenuState.RENT_A_CAR;
                    break;
                case "2":
                    state = MenuState.RETURN_A_RENTED_CAR; //TODO
                    return;
                case "3":
                    state = MenuState.MY_RENTED_CAR;
                    return;
                case "0":
                    state = MenuState.WELCOME;
                    return;
            }
        }

        if (state == MenuState.RENT_A_CAR) {
            List<Company> companys = new ArrayList<>();
            ResultSet rs = st.executeQuery("SELECT * FROM COMPANY");

            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("NAME");
                companys.add(new Company(id, name));
            }

            if (companys.isEmpty()) {
                System.out.println(EMPTYLIST_COMPANY);
                state = CUSTOMER_MENU;
                return;
            } else {
                System.out.println("Choose a company:");
                // for (int i = 0; i < companys.size(); i++) {
                //     System.out.printf("%d. %s\n", i + 1, companys.get(i).getName());
                // }
                companys.stream().forEach(System.out::println);
                System.out.println("0. Back");
                System.out.print(">");
                state = MenuState.CHOOSE_A_CAR_FROM_DB_MENU;
                return;
            }
        }


        if (state == MenuState.CHOOSE_THE_COMPANY) {

            switch (input) {
                case "0":
                    state = MenuState.LOGIN_AS_MANAGER;
                    return;

                default:
                    state = MenuState.SELECTING_COMPANY_FROM_DB;
                    break;

            }


        }

        if (state == MenuState.CHOOSE_THE_CUSTOMER) {

            switch (input) {
                case "0":
                    state = MenuState.WELCOME;
                    return;

                default:
                    state = MenuState.SELECTING_CUSTOMER_FROM_DB;
                    break;

            }


        }

        if (state == MenuState.SELECTING_COMPANY_FROM_DB) {
            // Connection connection2 = DriverManager.getConnection("jdbc:h2:file:../task/src/carsharing/db/carsharing");
            // connection2.setAutoCommit(true);
            // Statement st2 = connection2.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM COMPANY WHERE id =" + input);
            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("NAME");
                company_name = name;
                company_id = id;
            }
            state = MenuState.COMPANY_MENU;
            // st.close();
            // connection2.close();
            return;
        }

        if (state == MenuState.SELECTING_CUSTOMER_FROM_DB) {
            // Connection connection2 = DriverManager.getConnection("jdbc:h2:file:../task/src/carsharing/db/carsharing");
            // connection2.setAutoCommit(true);
            // Statement st2 = connection2.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM CUSTOMER WHERE id = " + input);
            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("NAME");
                customer_name = name;
                customer_id = id;
            }
            state = CUSTOMER_MENU;
            // st.close();
            // connection2.close();
            return;
        }


        if (state == MenuState.CREATE_A_CUSTOMER) {

            // Connection connection3 = DriverManager.getConnection("jdbc:h2:file:../task/src/carsharing/db/carsharing");
            // connection3.setAutoCommit(true);
            // Statement st3 = connection3.createStatement();
            st.execute("INSERT INTO CUSTOMER (NAME) VALUES ('" + input + "')");
            //  st.close();
            //   connection3.close();
            System.out.println(CREATED_CUSTOMER);
            state = MenuState.WELCOME;
            //   st.close();
            // connection3.close();
        }


        if (state == MenuState.CREATE_CAR) {

            // Connection connection3 = DriverManager.getConnection("jdbc:h2:file:../task/src/carsharing/db/carsharing");
            // connection3.setAutoCommit(true);
            // Statement st3 = connection3.createStatement();
            st.execute("INSERT INTO CAR (NAME, COMPANY_ID) VALUES ('" + input + "','" + company_id + "')");
            //  st.close();
            //   connection3.close();
            System.out.println(CREATED_CAR);
            state = MenuState.COMPANY_MENU;
            //   st.close();
            // connection3.close();
        }

        if (state == MenuState.CREATE_COMPANY) {

            //Connection connection3 = DriverManager.getConnection("jdbc:h2:file:../task/src/carsharing/db/carsharing");
            //connection3.setAutoCommit(true);
            //Statement st3 = connection3.createStatement();
            st.execute("INSERT INTO COMPANY (NAME) VALUES ('" + input + "')");
            //st.close();
            // connection3.close();
            System.out.println(CREATED_COMPANY);
            state = MenuState.LOGIN_AS_MANAGER;
            //  st.close();
            //  connection3.close();

        }


        if (state == MenuState.CHOOSE_A_CAR_FROM_DB_MENU) {
            Set<Car> avialableCarsInCarTableSet = new HashSet<>();
            String nameOfCar = "";
            int idOfCar;
            ResultSet rs = st.executeQuery("SELECT * FROM CAR WHERE COMPANY_ID=" + input);
            while (rs.next()) {
                int avialableID = rs.getInt("ID");
                String name = rs.getString("NAME");
                nameOfCar = name;
                avialableCarsInCarTableSet.add(new Car(avialableID,name));
            }
            ResultSet rs2 = st.executeQuery("SELECT RENTED_CAR_ID FROM CUSTOMER");
            Set<Car> rentedCarsIdInCustomerTable = new HashSet<>();
            while (rs2.next()) {
                int rentedCarId = rs2.getInt("RENTED_CAR_ID");
                rentedCarsIdInCustomerTable.add(new Car(rentedCarId));
            }
            avialableCarsInCarTableSet.removeAll(rentedCarsIdInCustomerTable);
            List<Car> availCars = new ArrayList<>();
            for (Car carId:avialableCarsInCarTableSet
                 ) {
                ResultSet rs3 = st.executeQuery("SELECT * FROM CAR WHERE ID="+carId.getId());
                while (rs3.next()) {

                    String name = rs3.getString("NAME");
                    int id = rs3.getInt("ID");

                    availCars.add(new Car(id,name));
                }

            }

            if (avialableCarsInCarTableSet.isEmpty()) {
                System.out.println("No available cars in the '" + company_id + "' company");
                state = MenuState.CHOOSE_THE_COMPANY;
                return;
            }
            System.out.println("Choose a car:");

            availCars.stream().forEach(System.out::println);
            System.out.println("0. Back");
            System.out.print(">");
            state = MenuState.CHOOSING_CAR;


            return;
        }


        if (state == MenuState.CHOOSING_CAR) {
            // st.execute("INSERT INTO CUSTOMER (NAME, RENTED_CAR_ID) VALUES ('"+customer_name+"',"+input+")");


            ResultSet rs2 = st.executeQuery("SELECT * FROM CUSTOMER WHERE ID=" + customer_id);
            String tempRentedCarId = "";
            while (rs2.next()) {

                String rentedCarID = rs2.getString("RENTED_CAR_ID");
                tempRentedCarId = rentedCarID;

            }
            if (tempRentedCarId == null) {
                st.execute("UPDATE CUSTOMER SET RENTED_CAR_ID=" + input + " WHERE id=" + customer_id);

                ResultSet rs = st.executeQuery("SELECT * FROM CAR WHERE ID=" + input);
                String tempName = "";
                while (rs.next()) {

                    String name = rs.getString("NAME");
                    tempName = name;

                }
                System.out.println("You rented '" + tempName + "'");
                state = MenuState.CUSTOMER_MENU;

            } else {
                System.out.println("You've already rented a car!");
                state = MenuState.CUSTOMER_MENU;

            }

        }
    }


    //МЕНЮ
    // SCANNER
    // ВЫПОЛНИТЬ ИНСТРУКЦИЮ
    public void showControls() throws SQLException {

        if (state == MenuState.CHOOSE_A_CAR_FROM_DB_MENU) {

        }

        if (state == MenuState.MY_RENTED_CAR) {
            printAllRentedCar();
        }


        if (state == MenuState.CHOOSE_THE_COMPANY) {
            printAllCompany();
        }

        if (state == MenuState.LOGIN_AS_MANAGER) {
            showChoiceMenu();
        }

        if (state == MenuState.CREATE_COMPANY) {
            enterCompanyName();
        }

        if (state == MenuState.CREATE_CAR) {
            enterCarName();
        }

        if (state == MenuState.CREATE_A_CUSTOMER) {
            printEnterCustomerName();
        }

        if (state == MenuState.LOGIN_AS_CUSTOMER) {
            printAllCustomers();
        }

        if (state == MenuState.WELCOME) {
            showWelcomeMenu();
        }


        if (state == MenuState.BACK) {
            enterCompanyName();
        }
        if (state == CUSTOMER_MENU) {
            printCustomerMenu();
        }

        if (state == MenuState.COMPANY_MENU) {
            printCompanyMenu();
        }


        if (state == MenuState.SELECTING_COMPANY_FROM_DB) {

        }

        if (state == MenuState.SELECTING_CUSTOMER_FROM_DB) {

        }

    }

    public void printAllCustomers() throws SQLException {
        List<Customer> customerList = new ArrayList<>();
        Connection connection = DriverManager.getConnection("jdbc:h2:file:D:\\devel\\Car Sharing1\\Car Sharing\\task\\src\\carsharing\\db\\carsharing");
        connection.setAutoCommit(true);
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM CUSTOMER");

        while (rs.next()) {
            int id = rs.getInt("ID");

            String name = rs.getString("NAME");
            customerList.add(new Customer(id, name));
        }
        if (customerList.isEmpty()) {
            System.out.println(EMPTYLIST_CUSTOMER);
            state = MenuState.WELCOME;

        } else {
            System.out.println("Customer list:");
            customerList.stream().forEach(System.out::println);
            // for (int i = 0; i < customerList.size(); i++) {
            //     System.out.printf("%d. %s\n", i + 1, customerList.get(i).getName());
            // }
            System.out.println("0. Back");
            System.out.print(">");
            state = MenuState.CHOOSE_THE_CUSTOMER;
            return;
        }
    }

    public void printAllCompany() throws SQLException {
        List<Company> companyList = new ArrayList<>();
        Connection connection = DriverManager.getConnection("jdbc:h2:file:D:\\devel\\Car Sharing1\\Car Sharing\\task\\src\\carsharing\\db\\carsharing");
        connection.setAutoCommit(true);
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM COMPANY");

        while (rs.next()) {
            int id = rs.getInt("ID");

            String name = rs.getString("NAME");
            companyList.add(new Company(id, name));
        }
        if (companyList.isEmpty()) {
            System.out.println(EMPTYLIST_COMPANY);
            state = MenuState.LOGIN_AS_MANAGER;

        } else {
            System.out.println("Choose the company:");
            companyList.stream().forEach(System.out::println);
            System.out.println("0. Back");
            System.out.print(">");
            //st.close();
            //connection.close();

        }
    }

    ;

    public void showWelcomeMenu() {
        System.out.println("1. Log in as a manager");
        System.out.println("2. Log in as a customer");
        System.out.println("3. Create a customer");
        System.out.println("0. Exit");
        System.out.print(">");
    }

    public void printAllRentedCar() throws SQLException {
        String tempCarName = "";
        List<String> rentedCarsList = new ArrayList<>();
        Connection connection = DriverManager.getConnection("jdbc:h2:file:D:\\devel\\Car Sharing1\\Car Sharing\\task\\src\\carsharing\\db\\carsharing");
        connection.setAutoCommit(true);
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery("SELECT CAR.NAME FROM CAR LEFT JOIN CUSTOMER ON CAR.ID=CUSTOMER.RENTED_CAR_ID WHERE CUSTOMER.NAME='" + customer_name + "'");

        while (rs.next()) {

            String name = rs.getString("NAME");
            tempCarName = name;
            rentedCarsList.add(name);
        }
        if (rentedCarsList.isEmpty()) {
            System.out.println("You didn't rent a car!");
            state = CUSTOMER_MENU;

        } else {
            System.out.println("Your rented car:");
            rentedCarsList.stream().forEach(System.out::println);
            //for (int i = 0; i < customerList.size(); i++) {
            //    System.out.printf("%s\n", customerList.get(i).getName());
            //}
            Connection connection2 = DriverManager.getConnection("jdbc:h2:file:D:\\devel\\Car Sharing1\\Car Sharing\\task\\src\\carsharing\\db\\carsharing");
            Statement st2 = connection2.createStatement();
            ResultSet rs2 = st2.executeQuery("SELECT COMPANY.NAME FROM COMPANY LEFT JOIN CAR ON COMPANY.ID=CAR.COMPANY_ID WHERE CAR.NAME='" + tempCarName + "'");
            String tempCompanyName = "";
            while (rs2.next()) {

                String name = rs2.getString("NAME");
                tempCompanyName = name;

            }

            System.out.println("Company:");

            System.out.println(tempCompanyName);
            state = CUSTOMER_MENU;
            return;
        }
    }

    public void showChoiceMenu() {
        System.out.println("1. Company list");
        System.out.println("2. Create a company");
        System.out.println("0. Back");
        System.out.print(">");
    }

    public void enterCompanyName() {
        System.out.println("Enter the company name:");
        System.out.print(">");
    }

    public void enterCarName() {
        System.out.println("Enter the car name:");
        System.out.print(">");
    }

    public void printEnterCustomerName() {
        System.out.println("Enter the customer name:");
        System.out.print(">");
    }


    public void printCompanyMenu() {
        System.out.println(company_name + " company");
        System.out.println("1. Car list");
        System.out.println("2. Create a car");
        System.out.println("0. Back");
        System.out.print(">");
    }

    public void printCustomerMenu() {
        System.out.println(customer_name);
        System.out.println("1. Rent a car");
        System.out.println("2. Return a rented car");
        System.out.println("3. My rented car");
        System.out.println("0. Back");
        System.out.print(">");
    }

}
