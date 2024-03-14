import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

public class Server {

    private static final String FILE_PATH = "XML/biblioteca.xml";
    private static List<String> bibliotecaXML = new ArrayList<>();

    private static DocumentBuilderFactory factory;
    private static DocumentBuilder builder;
    private static Document document;

    public static void main(String[] args) throws IOException, InterruptedException {
        new Clear().clearTerm();

        readXML(FILE_PATH);

        ServerSocket serverSocket = new ServerSocket(12345);
        try {
            println("Server avviato sulla porta 12345.");
            while (true) {
                Socket socket = serverSocket.accept();
                println("Client " + socket.getInetAddress().getHostAddress() + " connesso.");

                // Creazione di un nuovo thread per gestire la connessione
                Thread clientThread = new Thread(() -> {
                    try {
                        handleClientRequest(socket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                            println("Connessione con il client chiusa.");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                clientThread.start();
            }
        } finally {
            serverSocket.close();
        }
    }

    public static void handleClientRequest(Socket socket) throws IOException {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF("Catalogo dei libri:");
        for (int i = 0; i < bibliotecaXML.size(); i++) {
            out.writeUTF(bibliotecaXML.get(i));
        }
        out.writeUTF("end");
        while (true) {
            String scelta = in.readUTF();
            println("Client " + socket.getInetAddress().getHostAddress() + " ha scelto: " + scelta);
            switch (scelta) {
                case "1":
                    addBook(in, out);
                    break;
                case "2":
                    removeBook(in, out);
                    break;
                case "3":
                    modifyBook(in, out);
                    break;
                case "4":
                    searchBook(in, out);
                    break;
                case "5":
                    printAllElements(in, out);
                    break;
                case "0":
                    out.writeUTF("Disconessione in corso...");
                    return;
                default:
                    out.writeUTF("Scelta non valida.");
                    break;
            }
            out.flush();
        }
    }

    public static void print(Object str) {
        System.out.print(str);
    }

    public static void println(Object str) {
        System.out.println(str);
    }

    public static void readXML(String filePath) {
        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            document = builder.parse(new File(filePath));

            NodeList scaffali = document.getElementsByTagName("scaffale");
            for (int i = 0; i < scaffali.getLength(); i++) {
                Node scaffaleNode = scaffali.item(i);
                if (scaffaleNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element scaffaleElement = (Element) scaffaleNode;
                    NodeList sezioni = scaffaleElement.getElementsByTagName("sezione");
                    for (int j = 0; j < sezioni.getLength(); j++) {
                        Node sezioneNode = sezioni.item(j);
                        if (sezioneNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element sezioneElement = (Element) sezioneNode;
                            NodeList libri = sezioneElement.getElementsByTagName("libro");
                            for (int k = 0; k < libri.getLength(); k++) {
                                Node libroNode = libri.item(k);
                                if (libroNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element libroElement = (Element) libroNode;
                                    String ISBN = libroElement.getElementsByTagName("ISBN").item(0)
                                            .getTextContent();
                                    String titolo = libroElement.getElementsByTagName("titolo").item(0)
                                            .getTextContent();
                                    String autore = libroElement.getElementsByTagName("autore").item(0)
                                            .getTextContent();
                                    String annoPubblicazione = libroElement.getElementsByTagName("anno_pubblicazione")
                                            .item(0).getTextContent();
                                    String genere = libroElement.getElementsByTagName("genere").item(0)
                                            .getTextContent();
                                    String immagineCopertina = libroElement.getElementsByTagName("immagine_copertina")
                                            .item(0).getTextContent();
                                    String libroInfo = String.format(
                                            "ISBN: %s; Titolo: %s; Autore: %s; Anno di pubblicazione: %s; Genere: %s; Immagine copertina: %s",
                                            ISBN, titolo, autore, annoPubblicazione, genere, immagineCopertina);
                                    bibliotecaXML.add(libroInfo);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /* inizio addBook */

    public static void addBook(DataInputStream in, DataOutputStream out) throws IOException {
        try {
            // Leggi i dettagli del nuovo libro dal client
            out.writeUTF("Inserisci ISBN: ");
            String ISBN = in.readUTF();
            out.writeUTF("Inserisci titolo: ");
            String titolo = in.readUTF();
            out.writeUTF("Inserisci autore: ");
            String autore = in.readUTF();
            out.writeUTF("Inserisci anno di pubblicazione (AAAA): ");
            String annoPubblicazione = in.readUTF();
            out.writeUTF("Inserisci genere: ");
            String genere = in.readUTF();
            out.writeUTF("Inserisci immagine di copertina: ");
            String immagineCopertina = in.readUTF();

            // Genera un nuovo ID per il libro
            int newBookId = generateNewBookId();

            // Aggiungi il nuovo libro al documento XML
            Element newBookElement = document.createElement("libro");
            newBookElement.setAttribute("id", String.valueOf(newBookId));

            Element ISBNElement = document.createElement("ISBN");
            ISBNElement.setTextContent(ISBN);
            newBookElement.appendChild(ISBNElement);

            Element titoloElement = document.createElement("titolo");
            titoloElement.setTextContent(titolo);
            newBookElement.appendChild(titoloElement);

            Element autoreElement = document.createElement("autore");
            autoreElement.setTextContent(autore);
            newBookElement.appendChild(autoreElement);

            Element annoPubblicazioneElement = document.createElement("anno_pubblicazione");
            annoPubblicazioneElement.setTextContent(annoPubblicazione);
            newBookElement.appendChild(annoPubblicazioneElement);

            Element genereElement = document.createElement("genere");
            genereElement.setTextContent(genere);
            newBookElement.appendChild(genereElement);

            Element immagineCopertinaElement = document.createElement("immagine_copertina");
            immagineCopertinaElement.setTextContent(immagineCopertina);
            newBookElement.appendChild(immagineCopertinaElement);

            // Trova il scaffale e la sezione dove aggiungere il nuovo libro (sezione con
            // meno libri)
            NodeList scaffali = document.getElementsByTagName("scaffale");
            Element targetScaffale = null;
            Element targetSezione = null;
            int minLibriInSection = Integer.MAX_VALUE;
            for (int i = 0; i < scaffali.getLength(); i++) {
                Element scaffale = (Element) scaffali.item(i);
                NodeList sezioni = scaffale.getElementsByTagName("sezione");
                for (int j = 0; j < sezioni.getLength(); j++) {
                    Element sezione = (Element) sezioni.item(j);
                    int numLibriInSection = sezione.getElementsByTagName("libro").getLength();
                    if (numLibriInSection < minLibriInSection) {
                        minLibriInSection = numLibriInSection;
                        targetScaffale = scaffale;
                        targetSezione = sezione;
                    }
                }
            }

            // Aggiungi il nuovo libro alla sezione target nel documento XML
            targetSezione.appendChild(newBookElement);

            // Aggiungi il nuovo libro all'elenco dei libri in memoria
            String libroInfo = String.format(
                    "ISBN: %s; Titolo: %s; Autore: %s; Anno di pubblicazione: %s; Genere: %s; Immagine copertina: %s",
                    ISBN, titolo, autore, annoPubblicazione, genere, immagineCopertina);
            bibliotecaXML.add(libroInfo);

            // Salva le modifiche sul file XML
            saveXML(FILE_PATH);

            out.writeUTF("Libro aggiunto con successo.\n\n");
        } catch (Exception e) {
            out.writeUTF("Errore durante l'aggiunta del libro.");
            e.printStackTrace();
        }
    }

    public static int generateNewBookId() {
        // Genera un nuovo ID per il libro incrementando l'ID massimo attuale
        int maxId = 0;
        NodeList libri = document.getElementsByTagName("libro");
        for (int i = 0; i < libri.getLength(); i++) {
            Element libro = (Element) libri.item(i);
            int id = Integer.parseInt(libro.getAttribute("id"));
            if (id > maxId) {
                maxId = id;
            }
        }
        return maxId + 1;
    }

    public static void saveXML(String filePath) throws TransformerException {
        // Salva le modifiche sul file XML
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }

    /* fine addBook */

    /* inizio removeBook */

    public static void removeBook(DataInputStream in, DataOutputStream out) throws IOException {
        try {
            boolean bookRemoved = false;
            NodeList libri = document.getElementsByTagName("libro");
            List<String> titoliList = new ArrayList<>();

            for (int i = 0; i < libri.getLength(); i++) {
                Element libro = (Element) libri.item(i);
                NodeList titoli = libro.getElementsByTagName("titolo");
                Element titoloElement = (Element) titoli.item(0);
                String titolo = titoloElement.getTextContent();
                titoliList.add(titolo);
            }

            out.writeUTF("Libri disponibili:");
            for (String titolo : titoliList) {
                out.writeUTF("\t- " + titolo);
            }
            out.writeUTF("end");

            // Chiedi all'utente il nome del libro da rimuovere
            out.writeUTF("Inserisci il nome del libro da rimuovere: ");
            String bookToRemove = in.readUTF();

            // Trova e rimuove il libro dal documento XML e dall'elenco dei libri in memoria
            libri = document.getElementsByTagName("libro"); // re-inizializzo così funziona
            for (int i = 0; i < libri.getLength(); i++) {
                Element libro = (Element) libri.item(i);
                NodeList titoli = libro.getElementsByTagName("titolo");
                Element titoloElement = (Element) titoli.item(0);
                String titolo = titoloElement.getTextContent();
                if (titolo.equals(bookToRemove)) {
                    libro.getParentNode().removeChild(libro);
                    bibliotecaXML.remove(i);
                    bookRemoved = true;
                    break;
                }
            }

            // Se il libro è stato rimosso, salva le modifiche sul file XML
            if (bookRemoved) {
                saveXML(FILE_PATH);
                out.writeUTF("Libro rimosso con successo.\n\n");
            }
        } catch (Exception e) {
            out.writeUTF("Errore durante la rimozione del libro.");
            e.printStackTrace();
        }
    }

    /* fine removeBook */

    /* inizio modifyBook */

    public static void modifyBook(DataInputStream in, DataOutputStream out) throws IOException {
        try {
            // Trova il libro da modificare nel documento XML e nell'elenco dei libri in
            // memoria
            boolean bookModified = false;
            NodeList libri = document.getElementsByTagName("libro");
            List<String> titoliList = new ArrayList<>();

            for (int i = 0; i < libri.getLength(); i++) {
                Element libro = (Element) libri.item(i);
                NodeList titoli = libro.getElementsByTagName("titolo");
                Element titoloElement = (Element) titoli.item(0);
                String titolo = titoloElement.getTextContent();
                titoliList.add(titolo);
            }

            out.writeUTF("Libri disponibili:");
            for (String titolo : titoliList) {
                out.writeUTF("\t- " + titolo);
            }
            out.writeUTF("end");

            // Chiedi all'utente il nome del libro da rimuovere
            out.writeUTF("Inserisci il nome del libro da modificare: ");
            String bookToModify = in.readUTF();

            libri = document.getElementsByTagName("libro"); // re-inizializzo così funziona
            for (int i = 0; i < libri.getLength(); i++) {
                Element libro = (Element) libri.item(i);
                NodeList titoli = libro.getElementsByTagName("titolo");
                Element titoloElement = (Element) titoli.item(0);
                String titolo = titoloElement.getTextContent();
                if (titolo.equals(bookToModify)) {
                    // Modifica gli attributi del libro
                    out.writeUTF("Inserisci il nuovo ISBN: ");
                    String newISBN = in.readUTF();
                    libro.getElementsByTagName("ISBN").item(0).setTextContent(newISBN);

                    out.writeUTF("Inserisci il nuovo titolo: ");
                    String newTitolo = in.readUTF();
                    libro.getElementsByTagName("titolo").item(0).setTextContent(newTitolo);

                    out.writeUTF("Inserisci il nuovo autore: ");
                    String newAutore = in.readUTF();
                    libro.getElementsByTagName("autore").item(0).setTextContent(newAutore);

                    out.writeUTF("Inserisci il nuovo anno di pubblicazione (AAAA): ");
                    String newAnnoPubblicazione = in.readUTF();
                    libro.getElementsByTagName("anno_pubblicazione").item(0).setTextContent(newAnnoPubblicazione);

                    out.writeUTF("Inserisci il nuovo genere: ");
                    String newGenere = in.readUTF();
                    libro.getElementsByTagName("genere").item(0).setTextContent(newGenere);

                    out.writeUTF("Inserisci la nuova immagine di copertina: ");
                    String newImmagineCopertina = in.readUTF();
                    libro.getElementsByTagName("immagine_copertina").item(0).setTextContent(newImmagineCopertina);

                    // Segna il libro come modificato
                    bookModified = true;
                    break;
                }
            }
            out.writeUTF("end");

            // Se il libro è stato modificato, salva le modifiche sul file XML
            if (bookModified) {
                saveXML(FILE_PATH);
                out.writeUTF("Libro modificato con successo.\n\n");
            }
        } catch (Exception e) {
            out.writeUTF("Errore durante la modifica del libro.");
            e.printStackTrace();
        }
    }

    /* fine modifyBook */

    /* inizio searchBook */

    public static void searchBook(DataInputStream in, DataOutputStream out) throws IOException {
        try {
            // Chiedi all'utente quale elemento cercare (ISBN, titolo, autore o anno di
            // pubblicazione)
            out.writeUTF("Cosa vuoi cercare? (Scegliere tra ISBN o titolo): ");
            String searchElement = in.readUTF();

            // Chiedi all'utente il valore da cercare per l'elemento specificato
            out.writeUTF("Inserisci il valore da cercare: ");
            String searchValue = in.readUTF();

            // Cerca i libri corrispondenti nell'elenco dei libri in memoria
            List<String> matchingBooks = new ArrayList<>();
            for (String libroInfo : bibliotecaXML) {
                if (libroInfo.contains(searchValue)) {
                    matchingBooks.add(libroInfo);
                }
            }

            // Se ci sono libri corrispondenti, stampa la lista
            if (!matchingBooks.isEmpty()) {
                out.writeUTF("Libri trovati:");
                for (String book : matchingBooks) {
                    for (int i = 0; i < book.split(";").length; i++)
                        out.writeUTF(book.split(";")[i]);
                }
                out.writeUTF("end");
            }

            out.writeUTF("Libro trovato con successo.\n\n");
        } catch (Exception e) {
            out.writeUTF("Errore durante la ricerca dei libri.");
            e.printStackTrace();
        }
    }

    /* fine searchBook */

    /* inizio printAllElements */

    public static void printAllElements(DataInputStream in, DataOutputStream out) throws IOException {
        try {
            // Chiedi all'utente il nome dell'elemento da stampare
            out.writeUTF("Inserisci il nome dell'elemento da stampare: ");
            String elementName = in.readUTF();

            // Crea il documento XML di output
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document outputDocument = builder.newDocument();

            // Crea l'elemento radice per il documento XML di output
            Element rootElement = outputDocument.createElement("richiesta");
            outputDocument.appendChild(rootElement);

            // Cerca gli elementi corrispondenti nell'XML originale e aggiungili al
            // documento di output
            NodeList elements = document.getElementsByTagName(elementName);
            for (int i = 0; i < elements.getLength(); i++) {
                Element originalElement = (Element) elements.item(i);
                String elementTextContent = originalElement.getTextContent();

                // Crea un nuovo elemento nel documento XML di output
                Element newElement = outputDocument.createElement(elementName);
                newElement.setTextContent(elementTextContent);
                rootElement.appendChild(newElement);
            }

            // Scrivi il documento XML di output su un file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(outputDocument);
            StreamResult result = new StreamResult(new File("output.xml"));
            transformer.transform(source, result);

            out.writeUTF("File XML di output generato con successo.\n\n");
        } catch (Exception e) {
            out.writeUTF("Errore durante la generazione del file XML di output.");
            e.printStackTrace();
        }
    }

    /* fine printAllElements */
}
