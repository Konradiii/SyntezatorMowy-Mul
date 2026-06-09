import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.SequenceInputStream;
import java.util.*;

public class Syntezator {

    private final Map<String, File> nagrania = new HashMap<>();

    public Syntezator(String katalogGlowny) {
        wczytajNagrania("C:\\Users\\konra\\Desktop\\SZKOŁA\\Semestr 4\\MUL\\Synteza mowy\\synteza_mowy_cwiczenia");
    }

    private void wczytajNagrania(String katalogGlowny) {

        String[] foldery = {
                "stacje",
                "do_z_stacji",
                "perony_i_tory"
        };

        for (String folder : foldery) {

            File katalog = new File(katalogGlowny + File.separator + folder);

            File[] pliki = katalog.listFiles();

            if (pliki == null)
                continue;

            for (File plik : pliki) {

                String nazwa = plik.getName();

                int idx = nazwa.lastIndexOf('.');

                if (idx > 0)
                    nazwa = nazwa.substring(0, idx);

                nagrania.put(nazwa.toLowerCase(), plik);
            }
        }
    }

    public String usunPolskieZnaki(String txt) {

        StringBuilder wynik = new StringBuilder();

        for (char c : txt.toCharArray()) {

            switch (c) {
                case 'ą': c = 'a'; break;
                case 'ć': c = 'c'; break;
                case 'ę': c = 'e'; break;
                case 'ł': c = 'l'; break;
                case 'ń': c = 'n'; break;
                case 'ó': c = 'o'; break;
                case 'ś': c = 's'; break;
                case 'ź':
                case 'ż': c = 'z'; break;
            }

            wynik.append(c);
        }

        return wynik.toString();
    }

    public List<File> znajdzFragmenty(String tekst) {

        tekst = usunPolskieZnaki(tekst)
                .toLowerCase()
                .replace(",", "")
                .replace(".", "");

        String[] slowa = tekst.split("\\s+");

        List<File> wynik = new ArrayList<>();

        int i = 0;

        while (i < slowa.length) {

            boolean znaleziono = false;

            for (int j = slowa.length; j > i; j--) {

                String fragment =
                        String.join("_",
                                Arrays.copyOfRange(slowa, i, j));

                if (nagrania.containsKey(fragment)) {

                    wynik.add(nagrania.get(fragment));

                    i = j;

                    znaleziono = true;

                    break;
                }
            }

            if (!znaleziono) {

                throw new RuntimeException(
                        "Brak nagrania dla fragmentu: "
                                + slowa[i]);
            }
        }

        return wynik;

    }
    public static void polaczWav(List<File> pliki,
                                 String wynik)
            throws Exception {

        List<AudioInputStream> lista = new ArrayList<>();

        AudioFormat format = null;

        for (File plik : pliki) {

            AudioInputStream ais =
                    AudioSystem.getAudioInputStream(plik);

            if (format == null)
                format = ais.getFormat();

            lista.add(ais);
        }

        SequenceInputStream sequence =
                new SequenceInputStream(
                        Collections.enumeration(lista));

        long frames = 0;

        for (AudioInputStream ais : lista)
            frames += ais.getFrameLength();

        AudioInputStream wynikAudio =
                new AudioInputStream(
                        sequence,
                        format,
                        frames);

        AudioSystem.write(
                wynikAudio,
                AudioFileFormat.Type.WAVE,
                new File(wynik));
    }
}