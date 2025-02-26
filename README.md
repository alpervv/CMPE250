# CMPE250
## Overview  
This project is an implementation of a Feed Manager for a primitive version of Instagram, designed as part of the **CmpE 250: Data Structures and Algorithms** course. The program manages users, posts, likes, and dynamically generates personalized feeds based on user interactions.

## Input & Output  
- The program processes structured command files and generates outputs accordingly.  
- Sample commands:  
  ```
  create_user user1
  follow_user user2 user3
  see_post user4 post1
  see_all_posts_from_user user5 user6 1 1 0 0 1
  generate_feed user7 5
  ```
  
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
