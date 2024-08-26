import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class CRUD {
    static final String db_name = "jdbc:mysql://localhost:3306/eav";
    static final String user = "root";
    static final String password = "12345";

    static boolean create_table = false;


    // insert data

    static void insertData(LinkedHashMap<String, String> map) {
        try {

            System.out.println("Map Data : " + map);
            String table_name = map.get("table");


            Connection con = DriverManager.getConnection(db_name, user, password);

            DatabaseMetaData metaData = con.getMetaData();

            ResultSet resultSet = metaData.getTables(null, null, table_name, null);
//           System.out.println(resultSet.next());

            if (!resultSet.next() && (create_table == false)) {
                System.out.println("First you have to create the table....");
                createTable(map);

            } else {

                // get all the values from the values key

                String values_data = map.get("values");
//              System.out.println(values_data);


                // remove the curly braces from the string

                values_data = values_data.substring(1, values_data.length() - 1);



                // create a pair split with ,

                String[] pairs = values_data.split(",");


                LinkedHashMap<String, String> valuesMap = new LinkedHashMap<>();

                // Insert the value into the map from the string
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=");
                    valuesMap.put(keyValue[0], keyValue[1]);
                }

                System.out.println(valuesMap);


                // separate the keys and values from the valuesMap and store into two strings

                StringBuilder keys = new StringBuilder();
                StringBuilder values = new StringBuilder();


                for (String key : valuesMap.keySet()) {

                    keys.append(key).append(",");
                    values.append("'").append(valuesMap.get(key)).append("', ");

                }

                String tableColumnString = keys.toString().trim().substring(0, keys.length() - 1);
//                System.out.println("Table Columns : "+ tableColumnString);

                String tableColumnValueString = values.toString().trim().substring(0, values.length() - 2);
//                System.out.println("Table Column Values : "+tableColumnValueString);


                // Insert query

                System.out.println("Inserting records into the table...");

                String sql = "insert into " + table_name + " ( " + tableColumnString + ") values(" + tableColumnValueString + ")";
                System.out.println(sql);


                // execute the query
                Statement stmt = con.createStatement();
                stmt.executeUpdate(sql);


                System.out.println("Inserted records into the table...");

                create_table = false;


            }

            con.close();


        } catch (Exception e) {
            System.out.println(e);
        }
    }



    private static void createTable(LinkedHashMap<String, String> map) {
        try {

            Connection con = DriverManager.getConnection(db_name, user, password);
            String table_name = map.get("table");
            String values_data = map.get("values");

            values_data = values_data.substring(1, values_data.length() - 1);

            String[] pairs = values_data.split(",");

            StringBuilder sb = new StringBuilder();
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                sb.append(keyValue[0]);
                sb.append(",");
            }

            String keys = sb.toString().trim().substring(0, sb.length() - 1);
            System.out.println(keys);


            String[] keysArr = keys.split(",");

//            Arrays.stream(keysArr).forEach((ele)-> System.out.println(ele));


            String sql = "CREATE TABLE " + table_name + " (id INT PRIMARY KEY AUTO_INCREMENT, " +
                    keysArr[0] + " VARCHAR(100) NOT NULL, " +
                    keysArr[1] + " VARCHAR(100) NOT NULL, " +
                    keysArr[2] + " VARCHAR(100) NOT NULL, " +
                    keysArr[3] + " VARCHAR(50) NOT NULL, " +
                    keysArr[4] + " VARCHAR(4) NOT NULL)";

            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);

            System.out.println("Create the table");

            create_table = true;

            if (create_table) {
                insertData(map);
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Get datas from the table
    public static void getTableData(String tableName) {

        try {


            Connection con = DriverManager.getConnection(db_name, user, password);

            DatabaseMetaData metaData = con.getMetaData();

            ResultSet resultSet = metaData.getTables(null, null, tableName, null);

            System.out.println(resultSet.next());

            if (!resultSet.next()) {
                System.out.println("Please create the table and then get the value");
            } else {

                // execute the query

                String sql = "select * from " + tableName;

                Statement stmt = con.createStatement();

                ResultSet rs = stmt.executeQuery(sql);

                System.out.println("ID | First Name | Last Name |  Address  |   Phone No    | Age");
                System.out.println("--------------------------------------------------------------");

                while (rs.next()) {
                    System.out.println(rs.getInt(1) + "  | " + rs.getString(2) + "\t\t|" + rs.getString(3) + "\t|" + rs.getString(4) + "\t\t|" + rs.getString(5) + "\t\t|" + rs.getString(6));
                    System.out.println("--------------------------------------------------------------");
                }


            }

            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }




    public static void deleteDataFrom(LinkedHashMap<String,String> deleteMap)
    {
        try{
            Connection con = DriverManager.getConnection(db_name,user,password);
            String tableName = deleteMap.get("table_name");
            String deleteValues = deleteMap.get("values");

            deleteValues =  deleteValues.substring(1,deleteValues.length()-1).trim();

            String[] deleteValuesArray = deleteValues.split(",");

            LinkedHashMap<String, String> deleteValuesMap = new LinkedHashMap<>();

            // Insert the value into the map from the string
            for (String pair : deleteValuesArray) {
                String[] keyValue = pair.split("=");
                deleteValuesMap.put(keyValue[0], keyValue[1]);
            }




            StringBuilder keysValues = new StringBuilder();
            StringBuilder values = new StringBuilder();


            for(String k : deleteValuesMap.keySet())
            {

                String value = deleteValuesMap.get(k);
                if (value.matches("\\d+")) {
                    // If the value is a number, don't add quotes
                    keysValues.append(k).append(" = ").append(value).append(",");
                } else {
                    // If the value is a string, add quotes
                    keysValues.append(k).append(" = ").append("'").append(value).append("',");
                }
            }

            String keysValueStr = keysValues.toString().trim().substring(0,keysValues.length()-1);

            System.out.println(keysValueStr);

            String[]keyValueArr = keysValueStr.split(",");




            StringBuilder sb  = new StringBuilder();

            for(String pair : keyValueArr)
            {
                sb.append(pair.trim()).append(" AND ");
            }

            String condStr = sb.toString().trim();

            if(condStr.endsWith("AND")){
                condStr = condStr.substring(0,condStr.length()-4);
            }

            System.out.println(condStr);

            System.out.println("DELETE data from "+tableName+" where "+condStr);

            String sql = "DELETE FROM "+tableName+" WHERE "+condStr;

            Statement stmt = con.createStatement();

            stmt.executeUpdate(sql);

            System.out.println("Delete done..........");


            con.close();


        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public static void main(String[] args) {


        while(true) {



            System.out.println("*** CRUD Operation ***");

            System.out.println("1. Insert the data into the table");
            System.out.println("2. Get datas from database");
            System.out.println("3. Delete data from table");
            System.out.println("4. Exit from the application");



            System.out.println("Enter the choice:");
            Scanner sc = new Scanner(System.in);
            int choice =  sc.nextInt();

            sc.nextLine();


            switch (choice){

                case 1:
                    System.out.println("-- Insert the data into database --");

                    System.out.println("Enter the name of the table : ");
                    String table = sc.nextLine();
                    System.out.println("Enter the first_name : ");
                    String first_name = sc.nextLine();
                    System.out.println("Enter the last_name : ");
                    String last_name = sc.nextLine();
                    System.out.println("Enter the address : ");
                    String address = sc.nextLine();
                    System.out.println("Enter the phone_no : ");
                    String phone_no = sc.nextLine();
                    System.out.println("Enter the age : ");
                    String age = sc.nextLine();

                    LinkedHashMap<String, String> table_details = new LinkedHashMap<>();
                    table_details.put("first_name", first_name);
                    table_details.put("last_name", last_name);
                    table_details.put("address", address);
                    table_details.put("phone_no", phone_no);
                    table_details.put("age", age);

                    String table_data = table_details.toString();

                    LinkedHashMap<String, String> map = new LinkedHashMap<>();
                    map.put("table", table);
                    map.put("values", table_data);

                    insertData(map);
                    break;
                case 2:

                    System.out.println("-- Get the data from database -- ");

                    System.out.println("Enter the table name : ");
                    String tableName = sc.nextLine();

                    getTableData(tableName);
                    break;

                case 3:
                    System.out.println("Enter table name : ");
                    String table_name =  sc.nextLine();

                    LinkedHashMap<String,Object> delete_values = new LinkedHashMap<>();

                    delete_values.put("id",3);
                    delete_values.put("address","Kalyani");

                    LinkedHashMap<String,String> deleteMap = new LinkedHashMap<>();

                    deleteMap.put("table_name",table_name);
                    deleteMap.put("values", delete_values.toString());

                    deleteDataFrom(deleteMap);
                    break;



                case 4:

                    System.out.println("Exit the application");
                    System.exit(0);
                default:

                    System.out.println("This is a valid choice . Please enter the correct choice ");

            }



        }

    }
}

//
//
//    public static void updateTableColumn(LinkedHashMap<String, String> update) {
//        try{
//            Connection con = DriverManager.getConnection(db_name,user,password);
//
//            String tableName = update.get("table");
//            String values = update.get("values");
//            String condition = update.get("condition");
//
////            String sql = "UPDATE Customers SET  City= 'Kolkata' WHERE city = 'Sector V' ";
//
//            System.out.println(values);
//            System.out.println(condition);
//
//
//
//
//
//
//            con.close();
//        }
//        catch(Exception e){
//            System.out.println(e);
//        }
//
//    }


//                case 4:
//
//                    System.out.println("Enter the table_name : ");
//                    String update_table = sc.nextLine();
//
//                    LinkedHashMap<String,String> updateValues = new LinkedHashMap<>();
//
//                    updateValues.put("city","Kolkata");
//
//                    LinkedHashMap<String,String> condition = new LinkedHashMap<>();
//                    condition.put("city","Sector V");
//                    condition.put("city","Kalyani");
//
//                    LinkedHashMap<String,String> updateMap = new LinkedHashMap<>();
//
//                    updateMap.put("table",(String)update_table);
//                    updateMap.put("values",updateValues.toString());
//                    updateMap.put("condition",condition.toString());
//
//                    updateTableColumn(updateMap);
