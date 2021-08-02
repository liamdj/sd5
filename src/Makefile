# COS 445 SD5, Spring 2019
# Created by Jose Rodriguez Quinones with Andrew Wonnacott

all: Network.class

Network.class: *.java
	javac -Xlint Network.java *.java

results.csv: all miners.txt
	java -ea Network miners.txt > results.csv

clean:
	rm -rf *.class results.csv

sd5.zip: Network.java NetworkConfig.java Makefile Miner.java Miner_longest.java Miner_aggressive.java Miner_bribable.java ../Tournament.java miners.txt BlockChain.java
	zip -j sd5 Network.java NetworkConfig.java Makefile Miner.java Miner_longest.java Miner_aggressive.java Miner_bribable.java ../Tournament.java miners.txt BlockChain.java

#miners.txt: Miner_*.java
#	@ls | grep -e 'Miner_.*\.java' | sed s/.*Miner_// | sed s/\\.java$$// > miners.txt

test: results.csv
	@cat results.csv

upload: sd5.zip
	scp sd5.zip cos445@cycles.cs.princeton.edu:~/../htdocs/cos445/sd5.zip
