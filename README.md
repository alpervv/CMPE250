# CMPE250
## Overview  
CMPE Truck Simulator is a fleet management simulation project for the **CmpE 250: Data Structures and Algorithms** course. The goal is to efficiently manage a fleet of trucks with varying capacities by assigning them to parking lots, processing loads, and handling logistics operations.  

## Input & Output  
- The program processes structured command files and generates outputs accordingly.  
- Sample commands:  
  ```sh
  create_parking_lot 50 15  
  add_truck 100  
  ready 100  
  load 100 50  
  delete_parking_lot 50  
  count 50

## Run the Code  
To compile and run the program, follow these steps:  

1. **Compile the Java files:**  
   ```sh
   javac *.java
   ```
2. **Run the program with an input file:**
   ```sh
   java Main <input_file> <output_file>  
   ```
