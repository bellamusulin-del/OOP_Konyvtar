
package konyvtar;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

class KonyvtarTeszt {

    @TempDir
    Path ideiglenesMappa;

    private Konyvtar peldaKonyvtar() {
        Konyvtar k = new Konyvtar();

        k.konyvetHozzaad(new Konyv(
                "ISBN-1", "Titkos kert, uj kiadas", "A. Szerzo",
                2020, 2));

        k.olvasotHozzaad(new Olvaso("OLV-1", "Anna"));

        return k;
    }

    @Test
    void szerializacioMentEsBetolt() throws IOException {
        Konyvtar eredeti = peldaKonyvtar();
        Path fajl = ideiglenesMappa.resolve("teszt.dat");

        KonyvtarTar tar = new SzerializaciosTar();
        tar.ment(eredeti, fajl);

        Konyvtar betoltott = tar.betolt(fajl);

        assertEquals(eredeti.getKonyvek(), betoltott.getKonyvek());
        assertEquals(eredeti.getOlvasok(), betoltott.getOlvasok());
    }

    @Test
    void csvMentEsBetolt() throws IOException {
        Konyvtar eredeti = peldaKonyvtar();
        Path fajl = ideiglenesMappa.resolve("teszt.csv");

        KonyvtarTar tar = new CsvTar();
        tar.ment(eredeti, fajl);

        Konyvtar betoltott = tar.betolt(fajl);

        assertEquals(eredeti.getKonyvek(), betoltott.getKonyvek());
        assertEquals(eredeti.getOlvasok(), betoltott.getOlvasok());
    }

    @Test
    void csvMegorziACimetAmibenVesszoVan() throws IOException {
        Konyvtar eredeti = peldaKonyvtar();
        Path fajl = ideiglenesMappa.resolve("vesszo.csv");

        KonyvtarTar tar = new CsvTar();
        tar.ment(eredeti, fajl);

        Konyvtar betoltott = tar.betolt(fajl);

        assertEquals("Titkos kert, uj kiadas",
                betoltott.keresIsbn("ISBN-1").getCim());
    }

    @Test
    void kesedelmiDijNapontaSzamolodik() {
        Konyv k = new Konyv("ISBN-1", "Konyv", "Szerzo", 2020, 1);
        Olvaso o = new Olvaso("OLV-1", "Anna");

        Kolcsonzes kolcsonzes = new Kolcsonzes(
                k, o,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 8));

        assertEquals(0, kolcsonzes.kesedelmiDij(
                LocalDate.of(2025, 1, 8), 5));

        assertEquals(15, kolcsonzes.kesedelmiDij(
                LocalDate.of(2025, 1, 11), 5));
    }

    @Test
    void keresesCimEsSzerzoReszletre() {
        Konyvtar k = peldaKonyvtar();

        assertEquals(1, k.keresCimVagySzerzo("TITKOS").size());
        assertEquals(1, k.keresCimVagySzerzo("szerzo").size());
        assertTrue(k.keresCimVagySzerzo("nincs").isEmpty());
    }

    @Test
    void nemLehetTobbKonyvetKolcsonozniMintAmennyiVan() {
        Konyvtar k = peldaKonyvtar();

        k.kolcsonoz("ISBN-1", "OLV-1",
                LocalDate.of(2025, 1, 1), 7);

        k.kolcsonoz("ISBN-1", "OLV-1",
                LocalDate.of(2025, 1, 1), 7);

        assertEquals(0, k.szabadPeldanyok("ISBN-1"));

        assertThrows(IllegalStateException.class,
                () -> k.kolcsonoz("ISBN-1", "OLV-1",
                        LocalDate.of(2025, 1, 1), 7));
    }

    @Test
    void szerializacioAkolcsonzestIsMegorzi() throws IOException {
        Konyvtar eredeti = peldaKonyvtar();

        eredeti.kolcsonoz("ISBN-1", "OLV-1",
                LocalDate.of(2025, 1, 1), 7);

        Path fajl = ideiglenesMappa.resolve("kolcsonzes.dat");
        KonyvtarTar tar = new SzerializaciosTar();

        tar.ment(eredeti, fajl);
        Konyvtar betoltott = tar.betolt(fajl);

        assertEquals(1, betoltott.aktivKolcsonzesek().size());
        assertEquals(1, betoltott.getKolcsonzesek().size());
    }

    @Test
    void csvAkolcsonzestIsMegorzi() throws IOException {
        Konyvtar eredeti = peldaKonyvtar();

        eredeti.kolcsonoz("ISBN-1", "OLV-1",
                LocalDate.of(2025, 1, 1), 7);

        Path fajl = ideiglenesMappa.resolve("kolcsonzes.csv");
        KonyvtarTar tar = new CsvTar();

        tar.ment(eredeti, fajl);
        Konyvtar betoltott = tar.betolt(fajl);

        assertEquals(1, betoltott.aktivKolcsonzesek().size());
        assertEquals(1, betoltott.getKolcsonzesek().size());
    }
}

