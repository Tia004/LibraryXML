import java.io.IOException;

public class Clear {
    public Clear() {
    }

    public void clearTerm() throws IOException, InterruptedException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // Comando per pulire il terminale su win
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } else {
            // Comando per pulire il terminale su mac/linux
            new ProcessBuilder("bash", "-c", "clear").inheritIO().start().waitFor();
        }
    }
}
