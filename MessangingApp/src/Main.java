import javax.swing.*;

public class Main{
    public static void main(String[] args){
        SwingUtilities.invokeLater(LoginPage::new); //Starting App on EDT(Event Dispatch Thread)
    }
}

//javac -cp "lib\miglayout-core-5.3.jar;lib\miglayout-swing-5.3.jar" -d out src\*.java
// java -cp "out;lib\miglayout-core-5.3.jar;lib\miglayout-swing-5.3.jar;lib\mysql-connector-j-9.4.0.jar" Main
//Messanging page make new PrintWriter with clients out stream --> send messages to server --> clientHandler reads the message