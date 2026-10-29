
package konyvtar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CsvTar implements KonyvtarTar {

    @Override
    public void ment(Konyvtar konyvtar, Path fajl) throws IOException {
        Path cel = fajl.toAbsolutePath();
        Path mappa = cel.getParent();

        if (mappa != null) {
            Files.createDirectories(mappa);
        }

        Path ideiglenes = Files.createTempFile(
                mappa, "konyvtar-", ".tmp");

        try {
            try (BufferedWriter ki = Files.newBufferedWriter(
                    ideiglenes, StandardCharsets.UTF_8)) {

                ki.write("TIPUS,MEZO1,MEZO2,MEZO3,MEZO4,MEZO5");
                ki.newLine();

                for (Konyv k : konyvtar.getKonyvek()) {
                    sor(ki, "K", k.getIsbn(), k.getCim(),
                            k.getSzerzo(), String.valueOf(k.getKiadasEve()),
                            String.valueOf(k.getPeldanyszam()));
                }

                for (Olvaso o : konyvtar.getOlvasok()) {
                    sor(ki, "O", o.getOlvasojegySzam(), o.getNev());
                }

                for (Kolcsonzes k : konyvtar.getKolcsonzesek()) {
                    sor(ki, "L", k.getKonyv().getIsbn(),
                            k.getOlvaso().getOlvasojegySzam(),
                            k.getDatum().toString(),
                            k.getHatarido().toString(),
                            k.getVisszahozasDatum() == null
                                    ? ""
                                    : k.getVisszahozasDatum().toString());
                }
            }

            SzerializaciosTar.atnevez(ideiglenes, cel);
        } finally {
            Files.deleteIfExists(ideiglenes);
        }
    }

    private void sor(BufferedWriter ki, String... mezok)
            throws IOException {

        for (int i = 0; i < mezok.length; i++) {
            if (i > 0) {
                ki.write(",");
            }
            ki.write('"');
            ki.write(mezok[i].replace("\"", "\"\""));
            ki.write('"');
        }
        ki.newLine();
    }

    @Override
    public Konyvtar betolt(Path fajl) throws IOException {
        if (!Files.exists(fajl)) {
            throw new IOException("A CSV-fajl nem letezik: " + fajl);
        }

        Konyvtar konyvtar = new Konyvtar();
        Map<String, Konyv> konyvek = new HashMap<>();
        Map<String, Olvaso> olvasok = new HashMap<>();

        try (BufferedReader be = Files.newBufferedReader(
                fajl, StandardCharsets.UTF_8)) {

            String sor = be.readLine();

            if (!"TIPUS,MEZO1,MEZO2,MEZO3,MEZO4,MEZO5".equals(sor)) {
                throw new IOException("Hibas CSV fejlec.");
            }

            int sorszam = 1;

            while ((sor = be.readLine()) != null) {
                sorszam++;

                if (sor.isBlank()) {
                    continue;
                }

                try {
                    String[] m = mezokreBont(sor);

                    switch (m[0]) {
                        case "K" -> {
                            if (m.length != 6) {
                                throw new IllegalArgumentException(
                                        "Hibas konyvrekord.");
                            }

                            Konyv k = new Konyv(
                                    m[1], m[2], m[3],
                                    Integer.parseInt(m[4]),
                                    Integer.parseInt(m[5]));

                            konyvtar.betoltottKonyvetHozzaad(k);
                            konyvek.put(k.getIsbn(), k);
                        }

                        case "O" -> {
                            if (m.length != 3) {
                                throw new IllegalArgumentException(
                                        "Hibas olvasorekord.");
                            }

                            Olvaso o = new Olvaso(m[1], m[2]);

                            konyvtar.betoltottOlvasotHozzaad(o);
                            olvasok.put(o.getOlvasojegySzam(), o);
                        }

                        case "L" -> {
                            if (m.length != 6) {
                                throw new IllegalArgumentException(
                                        "Hibas kolcsonzesrekord.");
                            }

                            Konyv k = konyvek.get(m[1]);
                            Olvaso o = olvasok.get(m[2]);

                            if (k == null || o == null) {
                                throw new IllegalArgumentException(
                                        "Ismeretlen konyv vagy olvaso.");
                            }

                            Kolcsonzes kolcsonzes = new Kolcsonzes(
                                    k, o,
                                    java.time.LocalDate.parse(m[3]),
                                    java.time.LocalDate.parse(m[4]));

                            if (!m[5].isEmpty()) {
                                kolcsonzes.betoltottVisszahozas(
                                        java.time.LocalDate.parse(m[5]));
                            }

                            konyvtar.betoltottKolcsonzestHozzaad(
                                    kolcsonzes);
                        }

                        default -> throw new IllegalArgumentException(
                                "Ismeretlen rekordtipus: " + m[0]);
                    }
                } catch (RuntimeException e) {
                    throw new IOException(
                            "Hibas CSV-adat a(z) " + sorszam
                            + ". sorban: " + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            throw e;
        }

        return konyvtar;
    }

    private String[] mezokreBont(String sor) {
        java.util.List<String> mezok = new java.util.ArrayList<>();
        StringBuilder mezo = new StringBuilder();
        boolean idezojelben = false;

        for (int i = 0; i < sor.length(); i++) {
            char c = sor.charAt(i);

            if (c == '"') {
                if (idezojelben && i + 1 < sor.length()
                        && sor.charAt(i + 1) == '"') {
                    mezo.append('"');
                    i++;
                } else {
                    idezojelben = !idezojelben;
                }
            } else if (c == ',' && !idezojelben) {
                mezok.add(mezo.toString());
                mezo.setLength(0);
            } else {
                mezo.append(c);
            }
        }

        if (idezojelben) {
            throw new IllegalArgumentException(
                    "Lezaratlan idezojel a CSV-sorban.");
        }

        mezok.add(mezo.toString());
        return mezok.toArray(new String[0]);
    }
}
