
package konyvtar;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Konyvtar implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<String, Konyv> konyvek = new LinkedHashMap<>();
    private final Map<String, Olvaso> olvasok = new LinkedHashMap<>();
    private final List<Kolcsonzes> kolcsonzesek = new ArrayList<>();

    public void konyvetHozzaad(Konyv konyv) {
        Objects.requireNonNull(konyv);

        if (konyvek.containsKey(konyv.getIsbn())) {
            throw new IllegalArgumentException(
                    "Mar van ilyen ISBN-u konyv.");
        }

        konyvek.put(konyv.getIsbn(), konyv);
    }

    public void olvasotHozzaad(Olvaso olvaso) {
        Objects.requireNonNull(olvaso);

        if (olvasok.containsKey(olvaso.getOlvasojegySzam())) {
            throw new IllegalArgumentException(
                    "Mar letezik ez az olvasojegy.");
        }

        olvasok.put(olvaso.getOlvasojegySzam(), olvaso);
    }

    public List<Konyv> getKonyvek() {
        return new ArrayList<>(konyvek.values());
    }

    public List<Olvaso> getOlvasok() {
        return new ArrayList<>(olvasok.values());
    }

    public List<Kolcsonzes> getKolcsonzesek() {
        return new ArrayList<>(kolcsonzesek);
    }

    public Konyv keresIsbn(String isbn) {
        return konyvek.get(isbn);
    }

    public Olvaso keresOlvaso(String jegyszam) {
        return olvasok.get(jegyszam);
    }

    public List<Konyv> keresCimVagySzerzo(String keresett) {
        if (keresett == null || keresett.isBlank()) {
            return getKonyvek();
        }

        String minta = keresett.toLowerCase();

        return konyvek.values().stream()
                .filter(k -> k.getCim().toLowerCase().contains(minta)
                        || k.getSzerzo().toLowerCase().contains(minta))
                .toList();
    }

    public List<Konyv> rendezCimSzerint() {
        return konyvek.values().stream()
                .sorted(Comparator.comparing(
                        Konyv::getCim,
                        String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public List<Konyv> rendezEvSzerint() {
        return konyvek.values().stream()
                .sorted(Comparator.comparingInt(Konyv::getKiadasEve))
                .toList();
    }

    public int szabadPeldanyok(String isbn) {
        Konyv konyv = konyvek.get(isbn);

        if (konyv == null) {
            throw new IllegalArgumentException(
                    "Nem talalhato ilyen ISBN-u konyv.");
        }

        long kiadott = kolcsonzesek.stream()
                .filter(k -> k.getKonyv().equals(konyv))
                .filter(k -> !k.visszaVanHozva())
                .count();

        return konyv.getPeldanyszam() - (int) kiadott;
    }

    public Kolcsonzes kolcsonoz(String isbn, String jegyszam,
                                LocalDate datum, int napok) {

        Konyv konyv = konyvek.get(isbn);
        Olvaso olvaso = olvasok.get(jegyszam);

        if (konyv == null) {
            throw new IllegalArgumentException("A konyv nem talalhato.");
        }
        if (olvaso == null) {
            throw new IllegalArgumentException("Az olvaso nem talalhato.");
        }
        if (datum == null || napok < 1) {
            throw new IllegalArgumentException(
                    "Ervenyes datum es legalabb egy nap kell.");
        }
        if (szabadPeldanyok(isbn) < 1) {
            throw new IllegalStateException(
                    "Nincs szabad peldany ebbol a konyvbol.");
        }

        Kolcsonzes kolcsonzes = new Kolcsonzes(
                konyv, olvaso, datum, datum.plusDays(napok));

        kolcsonzesek.add(kolcsonzes);
        return kolcsonzes;
    }

    public long visszahoz(Kolcsonzes kolcsonzes,
                          LocalDate datum, long napiDij) {
        if (!kolcsonzesek.contains(kolcsonzes)) {
            throw new IllegalArgumentException(
                    "Ez a kolcsonzes nem szerepel a konyvtarban.");
        }

        kolcsonzes.visszahoz(datum);
        return kolcsonzes.kesedelmiDij(datum, napiDij);
    }

    public List<Kolcsonzes> aktivKolcsonzesek() {
        return kolcsonzesek.stream()
                .filter(k -> !k.visszaVanHozva())
                .toList();
    }

    // Used by the CSV loader to restore saved data.
    void betoltottKonyvetHozzaad(Konyv konyv) {
        konyvek.put(konyv.getIsbn(), konyv);
    }

    void betoltottOlvasotHozzaad(Olvaso olvaso) {
        olvasok.put(olvaso.getOlvasojegySzam(), olvaso);
    }

    void betoltottKolcsonzestHozzaad(Kolcsonzes kolcsonzes) {
        kolcsonzesek.add(kolcsonzes);
    }
}
