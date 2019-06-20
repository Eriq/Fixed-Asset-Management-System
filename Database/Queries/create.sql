CREATE TABLE assets (
asset_id SERIAL PRIMARY KEY,
name varchar(20) NOT NULL,
serial_number varchar(30) NOT NULL,
barcode varchar(20),
model varchar(20),
brand varchar(30),
cost FLOAT NOT NULL,
purchase_date DATE NOT NULL,
vendor varchar(20),
asset_desc varchar(500),
condition varchar(20),
class varchar(20) NOT NULL,
location varchar(20) NOT NULL,
department varchar(20),
custodian varchar(20),
asset_image varchar(200),
qr_image varchar(200),
asset_status int 
);

CREATE TABLE components(
component_id SERIAL PRIMARY KEY,
asset_id int NOT NULL,
component_name varchar(20) NOT NULL,
component_serial varchar(30),
condition varchar(10),
component_description varchar(200),
	FOREIGN KEY (asset_id) REFERENCES assets(asset_id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE service(
service_id SERIAL PRIMARY KEY,
asset_id int NOT NULL,
service_provider varchar(20) NOT NULL,
service_date DATE NOT NULL,
warranty DATE,
contract varchar(50),
cost FLOAT NOT NULL,
service_description varchar(500),
	 FOREIGN KEY (asset_id) REFERENCES assets(asset_id) ON UPDATE CASCADE ON DELETE NO ACTION
);

CREATE TABLE retirement(
retirement_id SERIAL PRIMARY KEY,
asset_name varchar(30) NOT NULL UNIQUE,
asset_serial varchar(30) NOT NULL,
retirement_mode varchar(10),
retirement_date DATE NOT NULL
);