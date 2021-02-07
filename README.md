
# You can see the instruction.pdf for better understanding.

## Process to run the file:


# A. Open Example3-query folder in terminal:
  1. Build maven package: mvn package
  2. Then Run: java -jar target/example3-1.2.jar //Available in target folder.

# B. Results will be built in the Cran​ folder with name Query.results. 

# D. Go to the ​Trec_eval-9.0.7​ folder in terminal:
  1. Run the command: make
  2. Then Run: ./trec_eval test/Qrel.test ../cran/Query.results

# You can get this output.

P_5​: 0.4373, ​P_10​: 0.3071, ​iprec_at_recall_0.00​: 0.8251,​ iprec_at_recall_0.10​: 0.7876

<img src="Screenshot 2020-11-15 at 4.33.19 PM.png" width="500">
