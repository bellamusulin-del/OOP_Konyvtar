
package konyvtar;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class KonyvtarProgram {

    private static final Scanner BE = new Scanner(System.in);
    private static final long NAPI_DIJ = 5;

    public static void main(String[] args) {
        System.out.println("==================================");
        System.out.println("       Konyvtari kisokos");
        System.out.println("==================================");

        KonyvtarTar tar = tarValasztas();
        Path fajl = Path.of(
                tar instanceof CsvTar ? "konyvtar.csv" : "konyvtar.dat");

        Konyvtar konyvtar;

        try {
            if (java.nio.file.Files.exists(fajl)) {
                konyvtar = tar.betolt(fajl);
                System.out.println("A mentett adatok betoltve.");
            } else {
                konyvtar = peldaAdatok();
                System.out.println("Uj konyvtar letrehozva.");
            }
        } catch (IOException e) {
            System.out.println("Nem sikerult betolteni: " + e.getMessage());
            System.out.println("Ures konyvtarral indul a program.");
            konyvtar = new Konyvtar();
        }

        boolean fut = true;

        while (fut) {
            menu();
            String valasz = BE.nextLine().strip();

            try {
                switch (valasz) {
                    case "1" -> konyvHozzaad(konyvtar);
                    case "2" -> olvasoHozzaad(konyvtar);
                    case "3" -> konyvekListaz(konyvtar);
                    case "4" -> keres(konyvtar);
                    case "5" -> kolcsonoz(konyvtar);
                    case "6" -> visszahoz(konyvtar);
                    case "7" -> aktivKolcsonzesek(konyvtar);
                    case "8" -> ment(tar, konyvtar, fajl);
                    case "0" -> {
                        ment(tar, konyvtar, fajl);
                        fut = false;
                        System.out.println("Viszlat!");
                    }
                    default -> System.out.println("Ismeretlen menupont.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Hiba: " + e.getMessage());
            }
        }
    }

    private static KonyvtarTar tarValasztas() {
        System.out.println("Tarolasi mod:");
        System.out.println("1 - Java szerializacio");
        System.out.println("2 - CSV");
        System.out.print("Valasztas: ");

        return BE.nextLine().strip().equals("2")
                ? new CsvTar()
                : new SzerializaciosTar();
    }

    private static void menu() {
        System.out.println();
        System.out.println("1. Konyv hozzaadasa");
        System.out.println("2. Olvaso hozzaadasa");
        System.out.println("3. Konyvek listazasa");
        System.out.println("4. Kereses cim vagy szerzo szerint");
        System.out.println("5. Konyv kolcsonzese");
        System.out.println("6. Konyv visszahozasa");
        System.out.println("7. Aktiv kolcsonzesek");
        System.out.println("8. Mentes");
        System.out.println("0. Mentes es kilepes");
        System.out.print("Valasztas: ");
    }

    private static String kerdez(String szoveg) {
        System.out.print(szoveg);
        return BE.nextLine().strip();
    }

    private static int egesz(String szoveg) {
        return Integer.parseInt(kerdez(szoveg));
    }

    private static void konyvHozzaad(Konyvtar konyvtar) {
        Konyv konyv = new Konyv(
                kerdez("ISBN: "),
                kerdez("Cim: "),
                kerdez("Szerzo: "),
                egesz("Kiadas eve: "),
                egesz("Peldanyszam: "));

        konyvtar.konyvetHozzaad(konyv);
        System.out.println("A konyv hozzaadva.");
    }

    private static void olvasoHozzaad(Konyvtar konyvtar) {
        Olvaso olvaso = new Olvaso(
                kerdez("Olvasojegy szama: "),
                kerdez("Olvaso neve: "));

        konyvtar.olvasotHozzaad(olvaso);
        System.out.println("Az olvaso hozzaadva.");
    }

    private static void konyvekListaz(Konyvtar konyvtar) {
        System.out.println("1 - Cim szerint");
        System.out.println("2 - Kiadas eve szerint");
        String valasz = kerdez("Rendezes: ");

        List<Konyv> konyvek = valasz.equals("2")
                ? konyvtar.rendezEvSzerint()
                : konyvtar.rendezCimSzerint();

        konyvek.forEach(k -> System.out.println(
                k + " | Szabad peldany: "
                + konyvtar.szabadPeldanyok(k.getIsbn())));
    }

    private static void keres(Konyvtar konyvtar) {
        String minta = kerdez("Cim vagy szerzo reszlete: ");
        List<Konyv> talalatok = konyvtar.keresCimVagySzerzo(minta);

        if (talalatok.isEmpty()) {
            System.out.println("Nincs talalat.");
        } else {
            talalatok.forEach(System.out::println);
        }
    }

    private static void kolcsonoz(Konyvtar konyvtar) {
        String isbn = kerdez("A konyv ISBN-je: ");
        String jegy = kerdez("Olvasojegy szama: ");
        int napok = egesz("Kolcsonzesi napok szama: ");

        Kolcsonzes k = konyvtar.kolcsonoz(
                isbn, jegy, LocalDate.now(), napok);

        System.out.println("Sikeres kolcsonzes.");
        System.out.println("Hatarido: " + k.getHatarido());
    }

    private static void visszahoz(Konyvtar konyvtar) {
        List<Kolcsonzes> aktiv = konyvtar.aktivKolcsonzesek();

        if (aktiv.isEmpty()) {
            System.out.println("Nincs aktiv kolcsonzes.");
            return;
        }

        for (int i = 0; i < aktiv.size(); i++) {
            System.out.println((i + 1) + ". " + aktiv.get(i));
        }

        int valasztott = egesz("Melyik kolcsonzes? ") - 1;

        if (valasztott < 0 || valasztott >= aktiv.size()) {
            System.out.println("Hibas sorszam.");
            return;
        }

        long dij = konyvtar.visszahoz(
                aktiv.get(valasztott), LocalDate.now(), NAPI_DIJ);

        System.out.println("A konyv visszahozva.");
        System.out.println("Kesedelmi dij: " + dij);
    }

    private static void aktivKolcsonzesek(Konyvtar konyvtar) {
        List<Kolcsonzes> aktiv = konyvtar.aktivKolcsonzesek();

        if (aktiv.isEmpty()) {
            System.out.println("Nincs aktiv kolcsonzes.");
        } else {
            aktiv.forEach(System.out::println);
        }
    }

    private static void ment(KonyvtarTar tar,
                             Konyvtar konyvtar, Path fajl) {
        try {
            tar.ment(konyvtar, fajl);
            System.out.println("Mentes sikeres: " + fajl);
        } catch (IOException e) {
            System.out.println("A mentes nem sikerult: " + e.getMessage());
        }
    }

    private static Konyvtar peldaAdatok() {
        Konyvtar konyvtar = new Konyvtar();

        konyvtar.konyvetHozzaad(new Konyv(
                "ISBN-001", "A titkos kert", "Frances Hodgson Burnett",
                1911, 2));

        konyvtar.konyvetHozzaad(new Konyv(
                "ISBN-002", "Kisasszonyok", "Louisa May Alcott",
                1868, 1));

        konyvtar.olvasotHozzaad(new Olvaso(
                "OLV-001", "Virag Anna"));

        return konyvtar;
    }
}
