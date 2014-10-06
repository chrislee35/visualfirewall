default:
	javac -d class/ `find src/ -name '*.java'`

run:
	java -cp class/ edu.gatech.csc.visualfirewall.VisualFirewall

