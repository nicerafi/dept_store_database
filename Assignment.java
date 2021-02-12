import java.io.*;
import java.sql.*;

import java.util.Properties;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Year;
import java.util.Scanner;

import java.time.LocalDate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.time.format.DateTimeFormatter;
import java.time.Year;
import java.util.ArrayList;
import java.text.DecimalFormat;

import org.newsclub.net.unix.AFUNIXSocketFactory;

class Assignment{

    private static String readEntry(String prompt) {
        try {
            StringBuffer buffer = new StringBuffer();
            System.out.print(prompt);
            System.out.flush();
            int c = System.in.read();
            while (c != '\n' && c != -1) {
                buffer.append((char) c);
                c = System.in.read();
            }
            return buffer.toString().trim();
        } catch (IOException e) {
            return "";
        }
    }

    public static void main(String args[]) throws SQLException, IOException {
        // You should only need to fetch the connection details once
        Connection conn = getConnection();

        // choice variable which will be used in our menu to decide what method should be used
        String choice;
        // Display simple menu
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("(1) In-Store Purchases");
            System.out.println("(2) Collection ");
            System.out.println("(3) Delivery ");
            System.out.println("(4) Biggest Sellers ");
            System.out.println("(5) Reserved Stock ");
            System.out.println("(6) Staff Life-Time Success ");
            System.out.println("(7) Staff Contribution");
            System.out.println("(8) Employees of the Year");
            System.out.println("(0) Quit\n");
            choice = readEntry("Please choose an option: ");

            switch (choice) {
                case "1":
                    System.out.println("\nPlease enter the amount of products in this order.\n");
                    int num_orders = Integer.valueOf(readEntry("Number of products: "));
                    int[] productIDs = new int[num_orders];
                    int[] quantities = new int[num_orders];
                    for (int i = 0; i < num_orders; i++) {
                        while (true) { // Loops until a correct value is inputted
                            System.out.println("\nPlease enter the product ID:");
                            int product_id = Integer.valueOf(readEntry("ProductID: "));
                            if (does_productid_exist(conn, product_id) == -1) {
                                System.out.println("ProductID doesn't exist! Please try another ProductID\n");
                            } else {
                                productIDs[i] = product_id;
                                break;
                            }
                        }
                        while (true) {
                            System.out.println("\nPlease enter the Quantity:");
                            int quantity = Integer.valueOf(readEntry("Quantity: "));
                            if (quantity <= 0) { // Even though our table has a restraint to stop this we might aswell check the user input
                                System.out.println("Please enter a valid quantity! (Greater than 0)\n");
                            } else {
                                quantities[i] = quantity;
                                break;
                            }
                        }
                    }
                    System.out.println("\nPlease enter the order date");
                    String date = readEntry("Order Date: ");
                    if (!isDateValid(date)) {
                        System.out.println("Date inputted is not valid! Must be in form DD-MMM-YY! e.g 01-Dec-20\n");
                        break;
                    } else {
                        while (true) {
                            System.out.println("\nPlease enter the staff id");
                            int staff_id = Integer.valueOf(readEntry("StaffID: "));
                            if (does_staffid_exist(conn, staff_id) == -1) {
                                System.out.println("StaffID entered doesn't exist in database! Please try another StaffID\n");
                            } else {
                                option1(conn, productIDs, quantities, date, staff_id);
                                break;
                            }
                        }
                        break;
                    }

                case "2":
                    System.out.println("\nPlease enter the amount of products in this order.\n");
                    num_orders = Integer.valueOf(readEntry("Number of products: "));
                    productIDs = new int[num_orders];
                    quantities = new int[num_orders];
                    for (int i = 0; i < num_orders; i++) {
                        while (true) {
                            System.out.println("\nPlease enter the product ID:");
                            int product_id = Integer.valueOf(readEntry("ProductID: "));
                            if (does_productid_exist(conn, product_id) == -1) {
                                System.out.println("ProductID doesn't exist! Please try another ProductID\n");
                            } else {
                                productIDs[i] = product_id;
                                break;
                            }
                        }
                        while (true) {
                            System.out.println("\nPlease enter the Quantity:");
                            int quantity = Integer.valueOf(readEntry("Quantity: "));
                            if (quantity <= 0) {
                                System.out.println("Please enter a valid quantity! (Greater than 0)\n");
                            } else {
                                quantities[i] = quantity;
                                break;
                            }
                        }
                    }
                    System.out.println("\nPlease enter the date sold");
                    String sold_date = readEntry("Sold Date: ");
                    System.out.println("\nPlease enter the date of collection");
                    String collect_date = readEntry("Collection Date: ");
                    if (!isDateValid(sold_date) & !isDateValid(collect_date)) { // If either inputted date is invalid
                        System.out.println("Dates inputted is not valid! Must be in form DD-MMM-YY! e.g 01-Dec-20\n");
                        break;
                    } else if (!isDateBefore(sold_date, collect_date)) { 
                        System.out.println("Collection Date is before Sold Date!\n");
                        break;
                    } else {
                        System.out.println("\nPlease enter first name of the collector");
                        String fname = readEntry("First Name: ");
                        /* We check if the value entered can fit in a VARCHAR(30) output 
                        I know it looks disgusting. */
                        if(!isStringValid(fname)){System.out.println("Value entered is too long! Please limit the value to <= 30 characters.\n");break;}
                        System.out.println("\nPlease enter last name of the collector");
                        String lname = readEntry("Last Name: ");
                        if(!isStringValid(lname)){System.out.println("Value entered is too long! Please limit the value to <= 30 characters.\n");break;}
                        while (true) {
                            System.out.println("\nPlease enter the staff id");
                            int staff_id = Integer.valueOf(readEntry("StaffID: "));
                            if (does_staffid_exist(conn, staff_id) == -1) {
                                System.out.println("StaffID entered doesn't exist in database! Please try another StaffID \n");
                            } else {
                                option2(conn, productIDs, quantities, sold_date, collect_date, fname, lname, staff_id);
                                break;
                            }
                        }
                        break;
                    }
                case "3":
                    System.out.println("\nPlease enter the amount of products in this order.\n");
                    num_orders = Integer.valueOf(readEntry("Number of products: "));
                    productIDs = new int[num_orders];
                    quantities = new int[num_orders];
                    for (int i = 0; i < num_orders; i++) {
                        while (true) {
                            System.out.println("\nPlease enter the product ID:");
                            int product_id = Integer.valueOf(readEntry("ProductID: "));
                            if (does_productid_exist(conn, product_id) == -1) {
                                System.out.println("ProductID doesn't exist! Please try another ProductID\n");
                                break;
                            } else {
                                productIDs[i] = product_id;
                                break;
                            }
                        }
                        while (true) {
                            System.out.println("\nPlease enter the Quantity:");
                            int quantity = Integer.valueOf(readEntry("Quantity: "));
                            if (quantity <= 0) {
                                System.out.println("Please enter a valid quantity! (Greater than 0)\n");
                            } else {
                                quantities[i] = quantity;
                                break;
                            }
                        }
                    }
                    System.out.println("\nPlease enter the date sold");
                    sold_date = readEntry("Sold Date: ");
                    System.out.println("\nPlease enter the date of delivery");
                    String delivery_date = readEntry("Delivery Date: ");
                    if (!isDateValid(sold_date) & !isDateValid(delivery_date)) {
                        System.out.println("Date inputted is not valid! Must be in form DD-MMM-YY! e.g 01-Dec-20\n");
                        break;
                    } else if (!isDateBefore(sold_date, delivery_date)) {
                        System.out.println("Delivery Date is before Sold Date! e.g 01-Dec-20\n");
                        break;
                    } else {
                        System.out.println("\nPlease enter the first name of the collector");
                        String fname = readEntry("First Name: ");
                        if(!isStringValid(fname)){System.out.println("Value entered is too long! Please limit the value to <= 30 characters.\n");break;}
                        System.out.println("\nPlease enter the last name of the collector");
                        String lname = readEntry("Last Name: ");
                        if(!isStringValid(lname)){System.out.println("Value entered is too long! Please limit the value to <= 30 characters.\n");break;}
                        System.out.println("\nPlease enter the house name/no");
                        String house = readEntry("House Name/no: ");
                        if(!isStringValid(house)){System.out.println("Value entered is too long! Please limit the value to <= 30 characters.\n");break;}
                        System.out.println("\nPlease enter the street");
                        String street = readEntry("Street Name: ");
                        if(!isStringValid(street)){System.out.println("Value entered is too long! Please limit the value to <= 30 characters.\n");break;}
                        System.out.println("\nPlease enter the City");
                        String city = readEntry("City Name: ");
                        if(!isStringValid(city)){System.out.println("Value entered is too long! Please limit the value to <= 30 characters.\n");break;}
                        while (true) {
                            System.out.println("\nPlease enter the staff id");
                            int staff_id = Integer.valueOf(readEntry("StaffID: "));
                            if (does_staffid_exist(conn, staff_id) == -1) {
                                System.out.println("StaffID entered doesn't exist in database! Please try another StaffID\n");
                            } else {
                                option3(conn, productIDs, quantities, sold_date, delivery_date, fname, lname, house,
                                        street, city, staff_id);
                                break;
                            }
                        }
                        break;
                    }
                case "4":
                    option4(conn);
                    break;
                case "5":
                    while(true){
                    System.out.println("\nPlease enter the date");
                    date = readEntry("Date: ");
                    if (isDateValid(date)) {
                        option5(conn, date);
                        break;
                    } else {
                        System.out.println("Date inputted is not valid! Must be in form DD-MMM-YY! e.g 01-Dec-20 \n");
                    }
                }
                    break;
                case "6":
                    option6(conn);
                    break;
                case "7":
                    option7(conn);
                    break;
                case "8":
                    while(true){
                    System.out.println("\nPlease enter the year");
                    String year = readEntry("Year: ");
                    if(isYearValid(year)){
                        int year_int = Integer.parseInt(year);
                        option8(conn, year_int);
                        break;    
                    }
                    else{
                        System.out.println("Year entered isn't a valid year! Must be in the form YYYY, e.g. 2020");
                    }
                    }
                    break;
                case "0":
                    System.out.println("\nExiting Program...");
                    conn.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("\nThis is not a valid menu option! Please select another");
                    break;

            }
        }
    }

    /**
     * @param conn       An open database connection
     * @param productIDs An array of productIDs associated with an order
     * @param quantities An array of quantities of a product. The index of a
     *                   quantity correspeonds with an index in productIDs
     * @param orderDate  A string in the form of 'DD-Mon-YY' that represents the
     *                   date the order was made
     * @param staffID    The id of the staff member who sold the order
     */
    public static void option1(Connection conn, int[] productIDs, int[] quantities, String orderDate, int staffID) { // Instore purchase
        // First lets check if the staffID exists
        if (does_staffid_exist(conn, staffID) == 1) { // Sanity check whether StaffID does exist
            // Now lets check if the ProductID exists in our database //
            for (int i = 0; i < productIDs.length; i++) {
                if (does_productid_exist(conn, productIDs[i]) == 1) { // Sanity check whether StaffID does exist
                    /*
                     * Final check!, Lets now check if there is enough stock for the item for the
                     * transaction to occur
                     */
                    int stock_after = currentstock(conn, productIDs[i]) - quantities[i];
                    if (stock_after >= 0) { // Enough stock to carry out transaction
                        // Only want to create one entry for the order
                        try {
                            conn.setAutoCommit(false);
                            if (i == 0) {
                                // Insert into ORDERS // 
                                String insertOrderStatement = "INSERT INTO ORDERS"
                                        + "(OrderID, OrderType, OrderCompleted, OrderPlaced)" // orders
                                        + " VALUES" + "(nextval('OrderIDSequence'), ?, ?, ?)";
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy");
                                LocalDate date = LocalDate.parse(orderDate, formatter);
                                PreparedStatement preparedStatement = conn.prepareStatement(insertOrderStatement);
                                preparedStatement.setString(1, "InStore");
                                preparedStatement.setInt(2, 1);
                                preparedStatement.setDate(3, java.sql.Date.valueOf(date));
                                preparedStatement.executeUpdate();
                                preparedStatement.close();
                                // Insert into STAFF_ORDERS // 
                                String insertStaffStatement = "INSERT INTO STAFF_ORDERS" + "(StaffID, OrderID)"
                                        + " VALUES" + "(?, currval('OrderIDSequence'))";
                                preparedStatement = conn.prepareStatement(insertStaffStatement);
                                preparedStatement.setInt(1, staffID);
                                preparedStatement.executeUpdate();
                                preparedStatement.close();
                            }
                            // Insert into PRODUCTS // 
                            String insertProductStatement = "INSERT INTO ORDER_PRODUCTS"
                                    + "(OrderID, ProductID, ProductQuantity)" + " VALUES"
                                    + "(currval('OrderIDSequence'), ?, ?)";
                            PreparedStatement preparedStatement = conn.prepareStatement(insertProductStatement);
                            preparedStatement.setInt(1, productIDs[i]);
                            preparedStatement.setInt(2, quantities[i]);
                            preparedStatement.executeUpdate();
                            preparedStatement.close();
                            // UPDATE INVENTORY // 
                            String updateStockStatement = "UPDATE INVENTORY SET ProductStockAmount = " + stock_after
                                    + " WHERE ProductID = " + productIDs[i];
                            preparedStatement = conn.prepareStatement(updateStockStatement);
                            preparedStatement.executeUpdate();
                            preparedStatement.close();
                            conn.commit();
                            conn.setAutoCommit(true);
                            System.out.println("Product ID " + productIDs[i] + " stock is now at " + stock_after);
                        } catch (SQLException e) {
                            try {
                                conn.rollback();
                            } catch (SQLException except) {
                                System.out.println("Couldn't rollback!");
                            }
                        }
                    } else {
                        System.out.println(
                                "The Product ID: " + productIDs[i] + " doesn't have enough stock to carry out order!");
                        System.out.println("Checking next ProductID...");
                    }
                } else {
                    System.out.println("The Product ID: " + productIDs[i] + " Doesn't exist!");
                    System.out.println("Checking next ProductID...");
                }
            }
        } else {
            System.out.println("The Staff ID: " + staffID + " Doesn't exist!");
        }
    }

    // Incomplete - Code for option 1 goes here

    /**
     * @param conn           An open database connection
     * @param productIDs     An array of productIDs associated with an order
     * @param quantities     An array of quantities of a product. The index of a
     *                       quantity correspeonds with an index in productIDs
     * @param orderDate      A string in the form of 'DD-Mon-YY' that represents the
     *                       date the order was made
     * @param collectionDate A string in the form of 'DD-Mon-YY' that represents the
     *                       date the order will be collected
     * @param fName          The first name of the customer who will collect the
     *                       order
     * @param LName          The last name of the customer who will collect the
     *                       order
     * @param staffID        The id of the staff member who sold the order
     */
    public static void option2(Connection conn, int[] productIDs, int[] quantities, String orderDate,
            String collectionDate, String fName, String LName, int staffID) { // Collection
        // First lets check if the staffID exists
        if (does_staffid_exist(conn, staffID) == 1) { // Sanity check whether StaffID does exist
            // Now lets check if the ProductID exists in our database //
            for (int i = 0; i < productIDs.length; i++) {
                if (does_productid_exist(conn, productIDs[i]) == 1) { // Sanity check whether ProductID does exist
                    // Final check!, Lets now check if there is enough stock for the item for the
                    // transaction to occur//
                    int stock_after = currentstock(conn, productIDs[i]) - quantities[i];
                    if (stock_after >= 0) { // Enough stock to carry out transaction
                        // Only want to create one entry for the order
                        try {
                            conn.setAutoCommit(false);
                            if (i == 0) {
                                // Insert into ORDERS //
                                String insertOrderStatement = "INSERT INTO ORDERS"
                                        + "(OrderID, OrderType, OrderCompleted, OrderPlaced)" // orders
                                        + " VALUES" + "(nextval('OrderIDSequence'), ?, ?, ?)";
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy");
                                LocalDate date = LocalDate.parse(orderDate, formatter);
                                PreparedStatement preparedStatement = conn.prepareStatement(insertOrderStatement);
                                preparedStatement.setString(1, "Collection");
                                preparedStatement.setInt(2, 0);
                                preparedStatement.setDate(3, java.sql.Date.valueOf(date));
                                preparedStatement.executeUpdate();
                                preparedStatement.close();
                                // Insert into STAFF_ORDERS //
                                String insertStaffStatement = "INSERT INTO STAFF_ORDERS" + "(StaffID, OrderID)"
                                        + " VALUES" + "(?, currval('OrderIDSequence'))";
                                preparedStatement = conn.prepareStatement(insertStaffStatement);
                                preparedStatement.setInt(1, staffID);
                                preparedStatement.executeUpdate();
                                preparedStatement.close();
                                // Insert into COLLECTIONS //
                                String insertCollectionsStatement = "INSERT INTO COLLECTIONS"
                                        + "(OrderID, FName, LName, CollectionDate)" + " VALUES"
                                        + "(currval('OrderIDSequence'), ?, ?, ?)";
                                formatter = DateTimeFormatter.ofPattern("dd-MMM-yy");
                                LocalDate lt = LocalDate.parse(collectionDate, formatter);
                                preparedStatement = conn.prepareStatement(insertCollectionsStatement);
                                preparedStatement.setString(1, fName);
                                preparedStatement.setString(2, LName);
                                preparedStatement.setDate(3, java.sql.Date.valueOf(lt));
                                preparedStatement.executeUpdate();
                                preparedStatement.close();
                            }
                            // Insert into ORDER_PRODUCTS //
                            String insertProductStatement = "INSERT INTO ORDER_PRODUCTS"
                                    + "(OrderID, ProductID, ProductQuantity)" + " VALUES"
                                    + "(currval('OrderIDSequence'), ?, ?)";
                            PreparedStatement preparedStatement = conn.prepareStatement(insertProductStatement);
                            preparedStatement.setInt(1, productIDs[i]);
                            preparedStatement.setInt(2, quantities[i]);
                            preparedStatement.executeUpdate();
                            preparedStatement.close();
                            // Update INVENTORY //
                            String updateStockStatement = "UPDATE INVENTORY SET ProductStockAmount = " + stock_after
                                    + " WHERE ProductID = " + productIDs[i];
                            preparedStatement = conn.prepareStatement(updateStockStatement);
                            preparedStatement.executeUpdate();
                            preparedStatement.close();
                            conn.commit();
                            conn.setAutoCommit(true);
                            System.out.println("Product ID " + productIDs[i] + " stock is now at " + stock_after);
                        } catch (SQLException e) {
                            try {
                                conn.rollback();
                            } catch (SQLException except) {
                                System.out.println("Couldn't rollback!");
                            }
                        }
                    } else {
                        System.out.println(
                                "The Product ID: " + productIDs[i] + " doesn't have enough stock to carry out order!");
                        System.out.println("Checking next ProductID...");
                    }
                } else {
                    System.out.println("The Product ID: " + productIDs[i] + " Doesn't exist!");
                    System.out.println("Checking next ProductID...");
                }
            }
        } else {
            System.out.println("The Staff ID: " + staffID + " Doesn't exist!");
        }
    }

    /**
     * @param conn         An open database connection
     * @param productIDs   An array of productIDs associated with an order
     * @param quantities   An array of quantities of a product. The index of a
     *                     quantity correspeonds with an index in productIDs
     * @param orderDate    A string in the form of 'DD-Mon-YY' that represents the
     *                     date the order was made
     * @param deliveryDate A string in the form of 'DD-Mon-YY' that represents the
     *                     date the order will be delivered
     * @param fName        The first name of the customer who will receive the order
     * @param LName        The last name of the customer who will receive the order
     * @param house        The house name or number of the delivery address
     * @param street       The street name of the delivery address
     * @param city         The city name of the delivery address
     * @param staffID      The id of the staff member who sold the order
     */
    public static void option3(Connection conn, int[] productIDs, int[] quantities, String orderDate,
            String deliveryDate, String fName, String LName, String house, String street, String city, int staffID) {

        if (does_staffid_exist(conn, staffID) == 1) { // Sanity check whether StaffID does exist
            // Now lets check if the ProductID exists in our database //
            for (int i = 0; i < productIDs.length; i++) {
                if (does_productid_exist(conn, productIDs[i]) == 1) { // Sanity check whether ProductID does exist
                    // Final check!, Lets now check if there is enough stock for the item for the
                    // transaction to occur//
                    int stock_after = currentstock(conn, productIDs[i]) - quantities[i];
                    if (stock_after >= 0) { // Enough stock to carry out transaction
                        // Only want to create one entry for the order
                        try {
                            conn.setAutoCommit(false);
                            if (i == 0) {
                                // Insert into ORDERS //
                                String insertOrderStatement = "INSERT INTO ORDERS"
                                        + "(OrderID, OrderType, OrderCompleted, OrderPlaced)" // orders
                                        + " VALUES" + "(nextval('OrderIDSequence'), ?, ?, ?)";
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy");
                                LocalDate date = LocalDate.parse(orderDate, formatter);
                                PreparedStatement preparedStatement = conn.prepareStatement(insertOrderStatement);
                                preparedStatement.setString(1, "Delivery");
                                preparedStatement.setInt(2, 0);
                                preparedStatement.setDate(3, java.sql.Date.valueOf(date));
                                preparedStatement.executeUpdate();
                                preparedStatement.close();
                                // Insert into STAFF_ORDERS //
                                String insertStaffStatement = "INSERT INTO STAFF_ORDERS" + "(StaffID, OrderID)"
                                        + " VALUES" + "(?, currval('OrderIDSequence'))";
                                preparedStatement = conn.prepareStatement(insertStaffStatement);
                                preparedStatement.setInt(1, staffID);
                                preparedStatement.executeUpdate();
                                preparedStatement.close();
                                // Insert into DELIVERIES //
                                String insertCollectionsStatement = "INSERT INTO DELIVERIES"
                                        + "(OrderID, FName, LName, House, Street, City, DeliveryDate)" + " VALUES"
                                        + "(currval('OrderIDSequence'), ?, ?, ?, ?, ?, ?)";
                                formatter = DateTimeFormatter.ofPattern("dd-MMM-yy");
                                LocalDate lt = LocalDate.parse(deliveryDate, formatter);
                                preparedStatement = conn.prepareStatement(insertCollectionsStatement);
                                preparedStatement.setString(1, fName);
                                preparedStatement.setString(2, LName);
                                preparedStatement.setString(3, house);
                                preparedStatement.setString(4, street);
                                preparedStatement.setString(5, city);
                                preparedStatement.setDate(6, java.sql.Date.valueOf(lt));
                                preparedStatement.executeUpdate();
                                preparedStatement.close();
                            }
                            // Insert into ORDER_PRODUCTS //
                            String insertProductStatement = "INSERT INTO ORDER_PRODUCTS"
                                    + "(OrderID, ProductID, ProductQuantity)" + " VALUES"
                                    + "(currval('OrderIDSequence'), ?, ?)";
                            PreparedStatement preparedStatement = conn.prepareStatement(insertProductStatement);
                            preparedStatement.setInt(1, productIDs[i]);
                            preparedStatement.setInt(2, quantities[i]);
                            preparedStatement.executeUpdate();
                            preparedStatement.close();
                            // Update INVENTORY //
                            String updateStockStatement = "UPDATE INVENTORY SET ProductStockAmount = " + stock_after
                                    + " WHERE ProductID = " + productIDs[i];
                            preparedStatement = conn.prepareStatement(updateStockStatement);
                            preparedStatement.executeUpdate();
                            preparedStatement.close();
                            conn.commit();
                            conn.setAutoCommit(true);
                            System.out.println("Product ID " + productIDs[i] + " stock is now at " + stock_after);
                        } catch (SQLException e) {
                            try {
                                conn.rollback();
                            } catch (SQLException except) {
                                System.out.println("Couldn't rollback!");
                            }
                        }
                    } else {
                        System.out.println(
                                "The Product ID: " + productIDs[i] + " doesn't have enough stock to carry out order!");
                        System.out.println("Checking next ProductID...");
                    }
                } else {
                    System.out.println("The Product ID: " + productIDs[i] + " Doesn't exist!");
                    System.out.println("Checking next ProductID...");
                }
            }
        } else {
            System.out.println("The Staff ID: " + staffID + " Doesn't exist!");
        }
        // Incomplete - Code for option 3 goes here
    }

    /**
     * @param conn An open database connection
     */
    public static void option4(Connection conn) {
        // We will be getting the results from a view I created for this method //
        String select_top_seller = "select * from view_biggest_sellers";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(select_top_seller);
            ResultSet top_seller = preparedStatement.executeQuery();
            // Format the output println so it looks nice
            System.out.format("%-12s%-30s%-20s\n", "ProductID,", "ProductDesc,", "TotalValueSold");
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            while (top_seller.next()) {
                // String prod_id = top_seller.getInt(1) + ",";
                // String prod_desc = top_seller.getString(2) + ",";
                // String val_sold = "£" + df.format(top_seller.getFloat(3));
                System.out.format("%-12s%-30s%-20s\n", top_seller.getInt(1)+",", top_seller.getString(2) + ",", "£" + df.format(top_seller.getFloat(3)));
            }
            preparedStatement.close();
            top_seller.close();

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param conn An open database connection
     * @param date The target date to test collection deliveries against
     */
    public static void option5(Connection conn, String date) { // delete collections 8 days
        // First lets format our date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy");
        LocalDate date_before = LocalDate.parse(date, formatter);
        // We can use minus_Days() to get the date we want
        LocalDate date_after = date_before.minusDays(8);
        String get_collections = "select * from view_collection_quantities where collectiondate <= ?";
        // Create HashSet so we can store all unique OrderIDs
        LinkedHashSet<Integer> order_ids_unique = new LinkedHashSet<Integer>();
        // Create Arraylists to hold our IDs and quantities
        ArrayList<Integer> quantites = new ArrayList<Integer>();
        ArrayList<Integer> product_ids = new ArrayList<Integer>();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(get_collections);
            preparedStatement.setDate(1, java.sql.Date.valueOf(date_after));
            ResultSet collections = preparedStatement.executeQuery();
            while (collections.next()) {
                // Add the row values to our arrays
                order_ids_unique.add(collections.getInt(1));
                product_ids.add(collections.getInt(2));
                quantites.add(collections.getInt(3));
            }
            preparedStatement.close();
            collections.close();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            System.out.println("Screwed up on finding items!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Didn't find any orders that were before that date //
        if (order_ids_unique.isEmpty() || quantites.isEmpty() || product_ids.isEmpty()) { 
            System.out.println("Couldn't find orders that should have been collected 8 or more days ago!");
        } else {
            try {
                // Uses transactions so we can rollback if there are errors //
                conn.setAutoCommit(false);
                // Delete the orders from ORDERS corresponding to the OrderIDs //
                for (int orderid : order_ids_unique) {
                    String delete_order_statement = "DELETE FROM ORDERS WHERE OrderID = " + orderid;
                    PreparedStatement preparedStatement = conn.prepareStatement(delete_order_statement);
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                    System.out.println("Order " + orderid + " has been cancelled.");
                }
                // Update INVENTORY //
                for (int i = 0; i < product_ids.size(); i++) {
                    int current_stock = currentstock(conn, product_ids.get(i));
                    if (current_stock == -1) { // Sanity check if the current_stock() function fails (which it
                                               // shouldn't..)
                        System.out.println("Item doesn't exist or Stock is negative!");
                        break;
                    } else {
                        int updated_stock = current_stock + quantites.get(i);
                        String update_stock_statement = "UPDATE INVENTORY SET ProductStockAmount = " + updated_stock
                                + " WHERE ProductID = " + product_ids.get(i);
                        PreparedStatement preparedStatement = conn.prepareStatement(update_stock_statement);
                        preparedStatement.executeUpdate();
                        preparedStatement.close();
                    }
                }
                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException except) {
                    System.out.println("Couldn't rollback!");
                }
            }
        }
    }

    /**
     * @param conn An open database connection
     */
    public static void option6(Connection conn) {
        // Just select our view we created for this option //
        String get_staff_sold = "select * from view_staff_sold_name_opt6";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(get_staff_sold);
            ResultSet top_seller = preparedStatement.executeQuery();
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            System.out.format("%-20s%-20s\n", "EmployeeName,", "TotalValueSold");
            int counter = 0;
            while (top_seller.next()) {
                counter++;
                String fullname = top_seller.getString(1);
                String val_sold = "£" + df.format(top_seller.getFloat(3));
                System.out.format("%-20s%-20s\n", fullname, val_sold);
            }
            preparedStatement.close();
            top_seller.close();
            if (counter == 0) { // ResultSet is null
                System.out.println("No Corresponding Staff Members Have Been Found!");
            }

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param conn An open database connection
     */
    public static void option7(Connection conn) {
        // Incomplete - Code for option 7 goes here
        String select_highest_rated = "select * from opt_7";
        // Only want unique ProductIDs and StaffIDs so we use a hashset //
        LinkedHashSet<Integer> productids = new LinkedHashSet<Integer>();
        LinkedHashSet<Integer> staffids = new LinkedHashSet<Integer>();
        // Each StaffID has a corresponding Staff Name so we can use a hashmap //
        HashMap<Integer, String> staff_names = new HashMap<Integer, String>();
        // Each combination of ProductID and StaffID has a corresponding Quantity value
        // so we use a hashmap aswell
        HashMap<String, Integer> quantities = new HashMap<String, Integer>();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(select_highest_rated);
            ResultSet top_seller = preparedStatement.executeQuery();
            while (top_seller.next()) {
                staffids.add(top_seller.getInt(1));
                productids.add(top_seller.getInt(3));
                staff_names.put(top_seller.getInt(1), top_seller.getString(2));
                String quantity_key = top_seller.getInt(1) + "," + top_seller.getInt(3);
                quantities.put(quantity_key, top_seller.getInt(4));
            }

            // Always close statements, result sets and connections after use
            // Otherwise you run out of available open cursors!
            preparedStatement.close();
            top_seller.close();

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (staffids.isEmpty() | productids.isEmpty()) {
            System.out.println("No Corresponding Products Or Staff Members!");
        } else {
            Object[] prod_ids = productids.toArray();
            Object[] staff_ids = staffids.toArray();
            // Create the row headers which say EmployeeName, Product 1, Product 2, Product 3, ...
            System.out.format("%-20s", "EmployeeName,");
            for (int i = 0; i < prod_ids.length; i++) {
                if (i < prod_ids.length - 1) {
                    System.out.format("%-15s", " Product" + prod_ids[i] + ",");
                } else {
                    System.out.format("%-15s", " Product" + prod_ids[i]);
                }
            }
            System.out.print("\n");
            /* Prints out the staff name and there corresponding quantity contribution for
            the product*/
            for (int i = 0; i < staff_ids.length; i++) {
                if (i < staff_ids.length - 1) {
                    System.out.format("%-20s", staff_names.get(staff_ids[i]) + ",");
                } else {
                    System.out.format("%-20s", staff_names.get(staff_ids[i]));
                }
                for (int j = 0; j < prod_ids.length; j++) {
                    String quantity_key = staff_ids[i] + "," + prod_ids[j];
                    if (j < prod_ids.length - 1) {
                        System.out.format("%-15s", " " + quantities.get(quantity_key) + ",");
                    } else {
                        System.out.format("%-15s", " " + quantities.get(quantity_key));
                    }
                }
                System.out.print("\n");
            }
        }

    }

    /**
     * @param conn An open database connection
     * @param year The target year we match employee and product sales against
     */
    public static void option8(Connection conn, int year) {
        // Just select our view we created for this option //
        String select_opt8 = "select * from opt_8 where years = ?";
        int counter = 0;
        try {
            System.out.println("Employees Of The Year: " + year);
            PreparedStatement preparedStatement = conn.prepareStatement(select_opt8);
            /* Year is stored as a varchar(30) in our view so we have to cast our int 
            into a String */
            preparedStatement.setString(1, Integer.toString(year));
            ResultSet result_opt8 = preparedStatement.executeQuery();
            // System.out.println("ProductID, ProductDesc, TotalValueSold");
            while (result_opt8.next()) {
                String fullname = result_opt8.getString(1);
                System.out.println(fullname);
                counter++;
            }
            if (counter == 0) { // Only prints if our resultset is null / empty
                System.out.println("No corresponding staff members...");
            }
            // Always close statements, result sets and connections after use
            // Otherwise you run out of available open cursors!
            preparedStatement.close();
            result_opt8.close();

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Connection getConnection() {
        Properties props = new Properties();
        props.setProperty("socketFactory", "org.newsclub.net.unix.AFUNIXSocketFactory$FactoryArg");

        props.setProperty("socketFactoryArg", System.getenv("PGHOST") + "/.s.PGSQL.5432");
        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost/deptstore", props);
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * public static Connection getConnection() { //This version of getConnection
     * uses ports to connect to the server rather than sockets //If you use this
     * method, you should comment out the above getConnection method, and comment
     * out lines 19 and 21 String user = "me"; String passwrd = "mypassword";
     * Connection conn;
     * 
     * try { Class.forName("org.postgresql.Driver"); } catch (ClassNotFoundException
     * x) { System.out.println("Driver could not be loaded"); }
     * 
     * try { conn = DriverManager.getConnection(
     * "jdbc:postgresql://127.0.0.1:15432/deptstore?user="+ user +"&password=" +
     * passwrd);
     * 
     * return conn; } catch(SQLException e) { e.printStackTrace();
     * System.out.println("Error retrieving connection"); return null; }
     * 
     * }
     */
    /**
     * 
     * /**
     * 
     * @param conn    An open database connection
     * @param staffid StaffID you want to check
     */
    public static int does_staffid_exist(Connection conn, int staffid) { // Uses our psql function does_staffid_exist()
        String checkstaff = "SELECT does_staffid_exist(" + staffid + ")";
        int staff_result = -1;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(checkstaff);
            ResultSet staff_resultset = preparedStatement.executeQuery();
            while (staff_resultset.next()) {
                staff_result = staff_resultset.getInt(1);
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return staff_result;
    }

    /**
     * @param conn      An open database connection
     * @param productid ProductID you want to check
     */
    public static int does_productid_exist(Connection conn, int productid) { // Uses our psql function
                                                                             // does_productid_exist()
        String checkproductid = "SELECT does_productid_exist(" + productid + ")";
        int productid_result = -1;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(checkproductid);
            ResultSet productid_resultset = preparedStatement.executeQuery();
            while (productid_resultset.next()) {
                productid_result = productid_resultset.getInt(1);
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productid_result;
    }

    /**
     * @param conn      An open database connection
     * @param productid ProductID you want to check
     */
    public static int currentstock(Connection conn, int productid) { // Uses our psql function currentstock()
        String checkstock = "SELECT currentstock(" + productid + ")";
        int checkstock_result = -1;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(checkstock);
            ResultSet checkstock_resultset = preparedStatement.executeQuery();
            while (checkstock_resultset.next()) {
                checkstock_result = checkstock_resultset.getInt(1);
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return checkstock_result;
    }

    /**
     * @param conn      An open database connection
     * @param stock     Desired stock of item we want to update
     * @param productid ProductID you want to update
     */
    public static void update_INVENTORY(Connection conn, int stock, int productid) {
        String updateStockStatement = "UPDATE INVENTORY SET ProductStockAmount=? WHERE ProductID=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(updateStockStatement);
            preparedStatement.setInt(1, stock);
            preparedStatement.setInt(2, productid);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            System.out.println("ERROR WITH INVENTORY");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param conn     An open database connection
     * @param order_id OrderID you want to delete
     */
    public static void delete_from_ORDERS(Connection conn, int order_id) {
        String deleteOrderstatement = "DELETE FROM ORDERS WHERE OrderID = " + order_id;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(deleteOrderstatement);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            System.out.println("Screwed up on ORDER Deletion!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param conn     An open database connection
     * @param order_id OrderID you want to delete
     */
    public static void delete_from_ORDER_PRODUCTS(Connection conn, int order_id) {
        String deleteOrder_Productstatement = "DELETE FROM ORDER_PRODUCTS WHERE OrderID = " + order_id;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(deleteOrder_Productstatement);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            System.out.println("Screwed up on ORDER_PRODUCTS Deletion!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param conn     An open database connection
     * @param order_id OrderID you want to delete
     */
    public static void delete_from_COLLECTIONS(Connection conn, int order_id) {
        String deleteOrderProductsStatement = "DELETE FROM COLLECTIONS WHERE OrderID = " + order_id;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(deleteOrderProductsStatement);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            System.out.println("Screwed up on COLLECTIONS Deletion!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param conn     An open database connection
     * @param order_id OrderID you want to delete
     */
    public static void delete_from_STAFF_ORDERS(Connection conn, int order_id) {
        String deleteStaffOrderstatement = "DELETE FROM STAFF_ORDERS WHERE OrderID = " + order_id;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(deleteStaffOrderstatement);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            System.out.println("Screwed up on STAFF_ORDERS Deletion!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param dateStr String that we want to check is valid
     */
    public static boolean isDateValid(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-YY");
        String[] array = dateStr.split("");
        // Check last two digits in array (should be the year values)
        if (array.length != 9) {
            return false;
        }
        format.setLenient(true);

        try {
            format.parse(dateStr);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * @param first_date String form of the first date we want to check
     * @param second_date String form of the second date we want to check
     */
    public static boolean isDateBefore(String first_date, String second_date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy");
        LocalDate date1 = LocalDate.parse(first_date, formatter);
        LocalDate date2 = LocalDate.parse(second_date, formatter);
        if (date1.isBefore(date2)) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * @param string String we want to check if is valid
     */
    public static boolean isStringValid(String string) {
        // Our table only accepts VARCHAR(30) values //
        if (string.length() > 30) { 
            return false;
        }
        return true;
    }
    /**
     * @param year String of the year we want to check if is valid
     */
    public static boolean isYearValid(String year){
        try{
            int year_int = Integer.parseInt(year);
            if(year.length() != 4){
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }  
        return true;
    }

}