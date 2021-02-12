-- Drop all our views
DROP VIEW opt_8;
DROP VIEW opt_7;
DROP VIEW distinct_staff_who_sold_bestsellers_cross_prod_ids;
DROP VIEW distinct_staff_who_sold_bestsellers;
DROP VIEW staff_who_sold_bestseller_opt7;
DROP VIEW most_sold_prod_ids;
DROP VIEW view_has_staff_sold_bestseller_per_year;
DROP VIEW num_best_items_sold_per_year;
DROP VIEW view_best_items_sold_per_year;
DROP VIEW view_items_sold_per_year;
DROP VIEW view_staff_sold_opt8;
DROP VIEW view_staff_sold_each_year;
DROP VIEW view_collection_quantities;
DROP VIEW view_biggest_sellers;
DROP VIEW view_staff_sold_name_opt6;
DROP VIEW view_staff_sold_name;
DROP VIEW view_staff_sold;
-- Drop our tables
DROP TABLE ORDER_PRODUCTS CASCADE;
DROP TABLE INVENTORY CASCADE;
DROP TABLE DELIVERIES CASCADE;
DROP TABLE COLLECTIONS CASCADE;
DROP TABLE STAFF_ORDERS CASCADE;
DROP TABLE ORDERS CASCADE;
DROP TABLE STAFF CASCADE;
-- Drop our sequences
DROP SEQUENCE ProductIDSequence;
DROP SEQUENCE OrderIDSequence;
DROP SEQUENCE StaffIDSequence;
-- Drop our functions
DROP FUNCTION does_productid_exist(integer);
DROP FUNCTION stock_after(integer, integer);
DROP FUNCTION currentstock(integer);
DROP FUNCTION does_staffid_exist(integer);
-- Creating sequences for IDs so they can auto increment --
CREATE SEQUENCE ProductIDSequence START 1 INCREMENT BY 1;
CREATE SEQUENCE OrderIDSequence START 1 INCREMENT BY 1;
CREATE SEQUENCE StaffIDSequence START 1 INCREMENT BY 1;

CREATE TABLE INVENTORY (
  ProductID INTEGER PRIMARY KEY,
  ProductDesc VARCHAR(30) NOT NULL,
  ProductPrice NUMERIC(8, 2) NOT NULL,
  ProductStockAmount INTEGER NOT NULL,
  CHECK (ProductPrice >= 0),
  CHECK (ProductStockAmount >= 0)
);

CREATE TABLE ORDERS (
  OrderID INTEGER PRIMARY KEY,
  OrderType VARCHAR(30) NOT NULL,
  OrderCompleted INTEGER NOT NULL,
  OrderPlaced DATE NOT NULL,
  CHECK (
    (OrderType = 'InStore')
    OR (OrderType = 'Collection')
    OR (OrderType = 'Delivery')
  ),
  CHECK (
    (OrderCompleted >= 0)
    AND (OrderCompleted <= 1)
  )
);

CREATE TABLE ORDER_PRODUCTS(
  OrderID INTEGER NOT NULL,
  ProductID INTEGER NOT NULL,
  ProductQuantity INTEGER NOT NULL,
  CHECK (ProductQuantity > 0),
  -- PRIMARY KEY(OrderID,ProductID),
  FOREIGN KEY (OrderID) REFERENCES ORDERS(OrderID) ON DELETE CASCADE, 
  FOREIGN KEY (ProductID) REFERENCES INVENTORY(ProductID) ON DELETE CASCADE
);

CREATE TABLE DELIVERIES(
  OrderID INTEGER NOT NULL,
  FName VARCHAR(30) NOT NULL,
  LName VARCHAR(30) NOT NULL,
  House VARCHAR(30) NOT NULL,
  Street VARCHAR(30) NOT NULL,
  City VARCHAR(30) NOT NULL,
  DeliveryDate DATE NOT NULL,
  PRIMARY KEY(OrderID),
  FOREIGN KEY (OrderID) REFERENCES ORDERS(OrderID) ON DELETE CASCADE

);

CREATE TABLE COLLECTIONS(
  OrderID INTEGER NOT NULL,
  FName VARCHAR(30) NOT NULL,
  LName VARCHAR(30) NOT NULL,
  CollectionDate DATE NOT NULL,
  PRIMARY KEY(OrderID),
  FOREIGN KEY (OrderID) REFERENCES ORDERS(OrderID) ON DELETE CASCADE
);

CREATE TABLE STAFF (
  StaffID INTEGER PRIMARY KEY,
  FName VARCHAR(30) NOT NULL,
  LName VARCHAR(30) NOT NULL
);

CREATE TABLE STAFF_ORDERS (
  StaffID INTEGER NOT NULL,
  OrderID INTEGER NOT NULL,
  PRIMARY KEY(OrderID),
  FOREIGN KEY (StaffID) REFERENCES STAFF(StaffID) ON DELETE CASCADE,
  FOREIGN KEY (OrderID) REFERENCES ORDERS(OrderID) ON DELETE CASCADE
);

CREATE OR REPLACE FUNCTION does_productid_exist(id INTEGER) RETURNS INTEGER LANGUAGE plpgsql AS $$
DECLARE prod_id INTEGER;
BEGIN
SELECT ProductID INTO prod_id
FROM INVENTORY
WHERE ProductID = id;
if prod_id is NULL then return -1;
else RETURN 1;
END if;
END;
$$;

CREATE OR REPLACE FUNCTION currentstock(id INTEGER) RETURNS INTEGER LANGUAGE plpgsql AS $$
DECLARE stock_num INTEGER;
BEGIN
SELECT ProductStockAmount INTO stock_num
FROM INVENTORY
WHERE ProductID = id;
if stock_num is NULL then return -1;
else RETURN stock_num;
END if;
END;
$$;


CREATE OR REPLACE FUNCTION stock_after(quantity INTEGER, id INTEGER) RETURNS INTEGER LANGUAGE plpgsql AS $$
DECLARE stock_num INTEGER;
BEGIN
SELECT ProductStockAmount INTO stock_num
FROM INVENTORY
WHERE ProductID = id;
if stock_num is NULL then return -1;
else RETURN stock_num - quantity;
END if;
END;
$$;

CREATE OR REPLACE FUNCTION does_staffid_exist(id INTEGER) RETURNS INTEGER LANGUAGE plpgsql AS $$ BEGIN if (
    SELECT FName
    FROM STAFF
    WHERE StaffID = id
  ) is NULL then RETURN -1;
else RETURN 1;
END if;
END;
$$;


-- Shows all uncompleted collection orders
CREATE OR REPLACE VIEW view_collection_quantities AS
SELECT ORDER_PRODUCTS.OrderID,ORDER_PRODUCTS.ProductID, ORDER_PRODUCTS.ProductQuantity, COLLECTIONS.CollectionDate 
FROM ORDER_PRODUCTS
NATURAL JOIN ORDERS
NATURAL JOIN COLLECTIONS
WHERE ORDERS.OrderCompleted = 0;

-- Shows total value sold for each item in the store
CREATE OR REPLACE VIEW view_biggest_sellers AS
SELECT INVENTORY.ProductID, INVENTORY.ProductDesc, 
COALESCE(INVENTORY.ProductPrice * sum(ORDER_PRODUCTS.ProductQuantity), 0) AS TotalValueSold
FROM INVENTORY
LEFT JOIN ORDER_PRODUCTS ON INVENTORY.ProductID = ORDER_PRODUCTS.ProductID
GROUP BY INVENTORY.ProductID
ORDER BY TotalValueSold DESC, INVENTORY.ProductID DESC;

-- Table of how much each staff member has sold in the lifetime of the store
CREATE OR REPLACE VIEW view_staff_sold AS
SELECT STAFF_ORDERS.StaffID,
SUM(INVENTORY.ProductPrice * ORDER_PRODUCTS.ProductQuantity) AS ValueSold
FROM ORDERS
NATURAL JOIN STAFF_ORDERS
NATURAL JOIN ORDER_PRODUCTS
NATURAL JOIN INVENTORY
GROUP BY STAFF_ORDERS.StaffID;


CREATE OR REPLACE VIEW most_sold_prod_ids AS
SELECT productid FROM view_biggest_sellers WHERE
totalvaluesold > 20000
ORDER BY totalvaluesold DESC;

-- Shows the quantity sold for each staff member with there names instead of staff id
CREATE OR REPLACE VIEW view_staff_sold_name AS
SELECT 
CONCAT(staff.fname, ' ', staff.lname) AS fullname,
view_staff_sold.staffid,
view_staff_sold.valuesold
FROM staff
NATURAL JOIN view_staff_sold;

-- Shows staff members who have sold >= 50000 using view_staff_sold_name
CREATE OR REPLACE VIEW view_staff_sold_name_opt6 AS
SELECT * FROM view_staff_sold_name WHERE
ValueSold >= 50000
ORDER BY ValueSold DESC;

-- Shows staff members which have sold best selling products and the amount they have sold
CREATE OR REPLACE VIEW staff_who_sold_bestseller_opt7 AS
SELECT StaffID, CONCAT(fname, ' ', lname) AS fullName,ProductID , SUM(ProductQuantity) AS Quantity FROM
STAFF NATURAL JOIN STAFF_ORDERS NATURAL JOIN ORDER_PRODUCTS WHERE ProductID IN (SELECT ProductID FROM most_sold_prod_ids) GROUP BY StaffID, ProductID;


-- Distinct staff members who have sold any bestsellers
CREATE OR REPLACE VIEW distinct_staff_who_sold_bestsellers as 
select DISTINCT StaffID, fullname from staff_who_sold_bestseller_opt7;

-- CROSS JOIN between the distinct staff members and the best selling products
CREATE OR REPLACE VIEW distinct_staff_who_sold_bestsellers_cross_prod_ids AS
select * from distinct_staff_who_sold_bestsellers CROSS JOIN most_sold_prod_ids;

-- LEFT JOIN between distinct_staff_who_sold_bestsellers_cross_prod_ids and staff_who_sold_bestseller_opt7
CREATE OR REPLACE VIEW opt_7 AS
SELECT distinct_staff_who_sold_bestsellers_cross_prod_ids.*, COALESCE(staff_who_sold_bestseller_opt7.Quantity,0) AS Quantity
FROM distinct_staff_who_sold_bestsellers_cross_prod_ids
LEFT JOIN staff_who_sold_bestseller_opt7 on staff_who_sold_bestseller_opt7.StaffID = distinct_staff_who_sold_bestsellers_cross_prod_ids.StaffID
AND staff_who_sold_bestseller_opt7.ProductID = distinct_staff_who_sold_bestsellers_cross_prod_ids.ProductID
ORDER BY Quantity DESC;


-- View which shows the total amount a staff member has sold in a particular year
CREATE OR REPLACE VIEW view_staff_sold_each_year AS
SELECT STAFF_ORDERS.StaffID,
  CONCAT(staff.fname, ' ', staff.lname) AS fullname,
  SUM(
    INVENTORY.ProductPrice * ORDER_PRODUCTS.ProductQuantity
  ) AS ValueSold,
  CAST(EXTRACT(YEAR FROM ORDERS.OrderPlaced) AS varchar(30)) AS years
FROM ORDERS
NATURAL JOIN INVENTORY
NATURAL JOIN ORDER_PRODUCTS
NATURAL JOIN STAFF_ORDERS
NATURAL JOIN STAFF
GROUP BY STAFF_ORDERS.StaffID, years, STAFF.fname, STAFF.lname;


CREATE OR REPLACE VIEW view_staff_sold_opt8 AS
SELECT * FROM view_staff_sold_each_year WHERE
valuesold >= 30000;

-- Shows the amount ProductIDs have sold each year
CREATE OR REPLACE VIEW view_items_sold_per_year AS
SELECT INVENTORY.ProductID,
  INVENTORY.ProductPrice * SUM(ORDER_PRODUCTS.ProductQuantity) AS TotalValueSold,
  CAST(EXTRACT(YEAR FROM ORDERS.OrderPlaced) AS varchar(30)) AS years
FROM INVENTORY
NATURAL JOIN ORDER_PRODUCTS
NATURAL JOIN ORDERS
GROUP BY INVENTORY.ProductID,years
ORDER BY TotalValueSold DESC;

-- ProductIDs of items which sold >= 20000 in a certain year
CREATE OR REPLACE VIEW view_best_items_sold_per_year AS
SELECT productid, years FROM view_items_sold_per_year WHERE
TotalValueSold > 20000;


-- Table of the number of best-selling items for each year
CREATE OR REPLACE VIEW num_best_items_sold_per_year AS
SELECT COUNT(productid) AS num_of_prods, view_items_sold_per_year.years  FROM view_items_sold_per_year
group by view_items_sold_per_year.years;

-- Shows the amount of bestselling products staff members have sold each year
CREATE OR REPLACE VIEW view_has_staff_sold_bestseller_per_year AS
SELECT  view_staff_sold_opt8.fullname,view_staff_sold_opt8.staffid, 
count(distinct inventory.productid) AS best_sellers_sold, view_staff_sold_opt8.years
FROM view_staff_sold_opt8
NATURAL JOIN staff_orders
NATURAL JOIN order_products
NATURAL JOIN inventory
NATURAL JOIN view_best_items_sold_per_year
group by view_staff_sold_opt8.staffid, view_staff_sold_opt8.years,view_staff_sold_opt8.fullname;

-- Finds the staff members that have sold all the best selling items for all years
CREATE OR REPLACE VIEW opt_8 AS
SELECT view_has_staff_sold_bestseller_per_year.fullname, view_has_staff_sold_bestseller_per_year.years  
FROM view_has_staff_sold_bestseller_per_year
INNER JOIN num_best_items_sold_per_year ON num_best_items_sold_per_year.num_of_prods = view_has_staff_sold_bestseller_per_year.best_sellers_sold
AND num_best_items_sold_per_year.years = view_has_staff_sold_bestseller_per_year.years;

-- TODO
-- Make looping menu - DONE
-- Do opt 7 - DONE
-- Write report
-- Add savepoints - DONE ISH
-- Comment
-- Get rid of useless views and methods






