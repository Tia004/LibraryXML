import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    private static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) throws IOException, InterruptedException {
        new Clear().clearTerm();
        final String IP = InetAddress.getLocalHost().getHostAddress();
        final int PORT = 12345;
        Socket socket = new Socket(IP, PORT);
        println("Connesso al server aperto sulla porta " + PORT);

        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        try {
            String serverResponse;
            while (!(serverResponse = in.readUTF()).equals("end")) {
                for (int i = 0; i < serverResponse.split(";").length; i++) {
                    println(serverResponse.split(";")[i]);
                }
                println("");
            }
            while (true) {
                print("Menu:" +
                        "\n1. Aggiungi un libro" +
                        "\n2. Rimuovi un libro" +
                        "\n3. Modifica informazioni di un libro" +
                        "\n4. Cerca libro per genere, titolo, autore o anno di pubblicazione" +
                        "\n5. Stampa tutte le iterazioni di un certo elemento" +
                        "\n0. Esci" +
                        "(Immettere il numero)\n > ");
                String userInput = scan.nextLine();
                out.writeUTF(userInput); // scelta
                out.flush();

                if (userInput.equals("1")) {
                    addBook(in, out);
                } else if (userInput.equals("2")) {
                    removeBook(in, out);
                } else if (userInput.equals("3")) {
                    modifyBook(in, out);
                } else if (userInput.equals("4")) {
                    searchBook(in, out);
                } else if (userInput.equals("5")) {
                    printAllElements(in, out);
                } else if (userInput.equals("0")) {
                    println("Mi disconnetto...");
                    break;
                }
            }
        } finally {
            socket.close();
            scan.close();
        }
    }

    public static void print(Object str) {
        System.out.print(str);
    }

    public static void println(Object str) {
        System.out.println(str);
    }

    /* Inizio funzioni */

    public static void addBook(DataInputStream in, DataOutputStream out) throws IOException {
        print(in.readUTF());
        out.writeUTF(new Scanner(System.in).nextLine());
        print(in.readUTF());
        out.writeUTF(new Scanner(System.in).nextLine());
        print(in.readUTF());
        out.writeUTF(new Scanner(System.in).nextLine());
        print(in.readUTF());
        out.writeUTF(new Scanner(System.in).nextLine());
        print(in.readUTF());
        out.writeUTF(new Scanner(System.in).nextLine());
        print(in.readUTF());
        out.writeUTF(new Scanner(System.in).nextLine());

        println(in.readUTF()); // stringa di successo o di fallimento
    }

    public static void removeBook(DataInputStream in, DataOutputStream out) throws IOException {
        String serverResponse;
        println(in.readUTF());
        while (!(serverResponse = in.readUTF()).equals("end")) {
            println(serverResponse);
        }

        print(in.readUTF());
        out.writeUTF(new Scanner(System.in).nextLine());

        println(in.readUTF()); // stringa di successo o di fallimento
    }

    public static void modifyBook(DataInputStream in, DataOutputStream out) throws IOException {
        String serverResponse;
        println(in.readUTF());
        while (!(serverResponse = in.readUTF()).equals("end")) {
            println(serverResponse);
        }

        print(in.readUTF());
        out.writeUTF(new Scanner(System.in).nextLine());

        while (!(serverResponse = in.readUTF()).equals("end")) {
            print(serverResponse);
            out.writeUTF(new Scanner(System.in).nextLine());
        }

        println(in.readUTF()); // stringa di successo o di fallimento
    }

    public static void searchBook(DataInputStream in, DataOutputStream out) throws IOException {
        String serverResponse;
        print(in.readUTF());
        out.writeUTF(new Scanner(System.in).nextLine());

        print(in.readUTF());
        out.writeUTF(new Scanner(System.in).nextLine());

        while (!(serverResponse = in.readUTF()).equals("end")) {
            println(serverResponse);
        }

        println(in.readUTF()); // stringa di successo o di fallimento
    }

    public static void printAllElements(DataInputStream in, DataOutputStream out) throws IOException {
        print(in.readUTF());
        out.writeUTF(new Scanner(System.in).nextLine());

        println(in.readUTF()); // stringa di successo o di fallimento
    }
}
