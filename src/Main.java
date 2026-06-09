import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        String komunikat =
                "Pociąg do stacji Lublin Główny odjedzie z toru pierwszego przy peronie drugim";

        Syntezator syntezator =
                new Syntezator("synteza_mowy_cwiczenia");

        List<File> pliki =
                syntezator.znajdzFragmenty(komunikat);

        System.out.println("Znalezione pliki:");

        for (File f : pliki) {
            System.out.println(f.getName());
        }

        Syntezator.polaczWav(pliki, "wynik.wav");

        System.out.println("Utworzono plik wynik.wav");
    }
}