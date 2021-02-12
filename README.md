## README

## **Design Choices**

 - Instead of using a sequence for our primary keys for `ProductID`, `OrderID` and `StaffID` we could of used the `SERIAL` datatype to autoincrement the primary keys without the use of a sequence.
 - I believe that we should archive deleted orders as in real life scenarios errors and misjudgements occur when cancelling orders and we could use the past order information for customer support.
 - If possible normalise the database to Boyce-Codd Normal Form (BCNF) to reduce redundant data and any anomalies when performing actions (INSERT,UPDATE,DELETE) on the database.

## **Constraints**
**Value Constraints**
The additional constraints I added to my schema file were that `ProductStockAmount` in `INVENTORY` will be >= 0 as we wouldn't wanting a customer ordering something that isn't in stock. I also made sure that `ProductQuantity` in `ORDER_PRODUCTS` will always be > 0 as a customer shouldn't be ordering nothing or ordering negative stock.
I also made sure that all the columns in my table cannot have `NULL` values as it makes dealing with the database much easier.

**Key Restraints** 

| Table | Primary Key | Foreign Key | Justification |
|-|-|-|-|
| `INVENTORY` | `ProductID` | N/A |Each product needs to be unique.|
| `ORDERS` | `OrderID` | N/A |Each order needs to be unique.|
| `ORDER_PRODUCTS` | N/A | `OrderID`, `ProductID` |An Order can have multiple products and a product can also be in multiple orders so no need for `OrderID` and `ProductID` to be primary keys. We want to reference `ORDERS` and `INVENTORY` for our IDs so we use them as foreign keys|
| DELIVERIES | `OrderID` | `OrderID` |Deliveries should be unique but also reference there corresponding `ORDERID` from `ORDERS` so it is also a foreign key.|
| `COLLECTIONS` | `OrderID` | `OrderID` |Collections should be unique but also reference there corresponding `ORDERID` from `ORDERS` so it is also a foreign key.|
| `STAFF` | `StaffID` | N/A | Each staff member should be unique|
| `STAFF_ORDERS` | `OrderID` | `OrderID`,`StaffID` |A staff member can have many orders but an order can only have 1 corresponding staff member. We want to be able to reference `OrderID` from `ORDERS` and `StaffID` from `STAFF` so we also use them as foreign keys|


## Functions and Procedures - SQL
 - `does_productid_exist(id INTEGER)` - Simple function which returns -1 or 1 depending on if the `ProductID` given exists in the `INVENTORY` table.
 - `currentstock(id INTEGER)` - Simple function which returns the current stock of the item with `ProductID = id` in `INVENTORY`
 - `stock_after(quantity INTEGER, id INTEGER)` - Returns the stock of an item with `ProductID = id` after a transaction of `quantity`.
 - `does_staffid_exist(id INTEGER)`- Similar function to `does_productid_exist(id INTEGER)` but checks the `StaffID` instead
 ## Functions - Java
 
 - `isDateValid(String  dateStr)` - Returns a Boolean value if the given date string is valid. Date has to be in the form DD-MMM-YY e.g 07-May-20. Used in options 1 - 3.
 - `isStringValid(String  string)` - Returns a Boolean value if the given string is valid, as all strings in the database use the data type of `VARCHAR(30)` we check if the given string is <= 30
 - `isDateBefore(String  first_date,  String  second_date)`- Returns a Boolean value if the date `first_date` is before the date `second_date` which is useful for validating inputs for Option 2 and 3 as we don't want to input a `DeliveryDate` / `CollectionDate` which is before an `OrderDate`.
 - `isYearValid(String year)` - Checks whether the year value given can be used as a year when querying in Option 8. Year has to be in the form YYYY e.g 2020.
 - Each SQL function I created also had a java method which called the relevant SQL function as they are all called in Option 1 - 3.
 ## Options
 - **Options 1 - 3** 
	 - Used transactions so database can rollback if there is an error with the inserting data.
	 -  All methods use `isDateValid()` to check whether we can parse the users inputted date into our prepared statement.
	 - Options 2 and 3 use `isDateBefore()` to check whether the `OrderDate` is before the `DeliveryDate` / `CollectionDate`.
	 - Options 2 and 3 also use `isStringValid()` to check if the values for `City`,`Street`,`FirstName`, etc can fit in a `VARCHAR(30)`.
	 -  Uses my SQL functions `does_productid_exist()` `does_staffid_exist()` to check whether we can actually do the order 
 - **Option 4**
	 - Created a view called `view_biggest_sellers` which shows all `ProductIDs` and the total value they have sold in the lifetime of the store.
	 -	Java method simply outputs the results from `SELECT * FROM view_biggest_sellers` which we then format to the desired output.
 - **Option 5**
		 - Uses `LocalDate.minusDays()` to get the date we should start searching from.
		 - Uses a `HashSet` to store the unique `OrderIDs`  and  `ArrayList` to store our `ProductIDs` and `Quantites`.
		 - Tables `ORDER_PRODUCTS`,`STAFF_ORDERS` and `COLLECTIONS` have a `DROP CASCADE` clause on their foreign key references to `ORDERS` so we only need to delete from `ORDERS` to delete from their related tables.
		 - Statements use transactions so we can rollback if there is an error.
- **Option 6**
	-	Created multiple views for this option
		-	`view_staff_sold` View which shows how much each `StaffID` in `STAFF_ORDERS` has sold all together
		-	`view_staff_sold_name` View which shows `view_staff_sold`  output but now including the `StaffIDs` corresponding  full name.
		-	`view_staff_sold_name_opt6` View which shows values from `view_staff_sold_name` where `ValueSold >=  50000` .
	-	Java method simply outputs the results from `SELECT * FROM view_staff_sold_name_opt6` which we then format to the desired output.
- **Option 7**
	-	Created multiple views for this option
		-	`most_sold_prod_ids` - View which shows `ProductIDs` of items which have sold > £20,000 in the Department Stores lifetime.
		-	`staff_who_sold_bestseller_opt7` - View which shows Staff Members which have sold the products with the corresponding `ProductIDs` from `most_sold_prod_ids` and the quantity sold by the Staff Member.
		- `distinct_staff_who_sold_bestsellers` - View which shows distinct staff members who have sold >= 1 of the bestselling products
		- `distinct_staff_who_sold_bestsellers_cross_prod_ids` -View which shows the cartesian product between `distinct_staff_who_sold_bestsellers` and `most_sold_prod_ids` using `CROSS JOIN` 
		-	Our final table `opt_7` shows the `LEFT JOIN` between `staff_who_sold_bestseller_opt7` and   `distinct_staff_who_sold_bestsellers_cross_prod_ids`, we use `COALESCE()` to turn the null values into 0 for our final table
	- In our Java method we first get all the data from the query `SELECT * FROM opt_7`
		- We store the `StaffIDs` and `ProductIDs` in  `HashSets` as we only want unique values
		- We use `HashMaps` to store the staff members names and Quantities using `StaffID` and `StaffID,ProductID` as the keys respectively.
		- As the output had to be in a pivot table-esque form we have to format the output in Java.
- **Option 8**
	-	Created multiple views for this option
		-	`view_staff_sold_each_year` Shows the total value of stock they have sold in a certain year
		-	`view_items_sold_per_year` Shows the amount sold for each item in a certain year
		-	`view_staff_sold_opt8` shows the staff members which have sold >= £30,000
		-	`num_best_items_sold_per_year`- Shows the number of items which have sold > £20,000
		-	`view_has_staff_sold_bestseller_per_year` shows the distinct number of bestselling products that a staff member has sold in a certain year
		-	Our final table `OPT_8` only shows the staff members who have sold all the best selling items for the corresponding
	-	Java method simply outputs the results from `SELECT * FROM OPT_8` which we then format to the desired output.



    
