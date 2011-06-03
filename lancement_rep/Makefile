testNode:	
	rm -rf node.jar
	jar cfm node.jar NODE_MANIFEST.MF -C ../bin/ .
	chmod 777 node.jar

testCnode:
	rm -rf cNode.jar
	jar cfm cNode.jar C_MANIFEST.MF -C ../bin/ .
	chmod 777 cNode.jar
	./cNode.jar 8888 7892 127.0.0.1 1 1515 127.0.0.1

testCnode2:
	rm -rf cNode.jar
	jar cfm cNode.jar C_MANIFEST.MF -C ../bin/ .
	chmod 777 cNode.jar
	./cNode.jar 1500 10000 127.0.0.1 1 1515 127.0.0.1
