
package konyvtar;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Kolcsonzes implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Konyv konyv;
    private final Olvaso olvaso;
    private final LocalDate datum;
    private final LocalDate hatarido;
    private LocalDate visszahozasDatum;

    public Kolcsonzes(Konyv konyv, Olvaso olvaso,
                      LocalDate datum, LocalDate hatarido) {

        this.konyv = Objects.requireNonNull(konyv);
        this.olvaso = Objects.requireNonNull(olvaso);
        this.datum = Objects.requireNonNull(datum);
        this.hatarido = Objects.requireNonNull(hatarido);

        if (hatarido.isBefore(datum)) {
            throw new IllegalArgumentException(
                    "A hatarido nem lehet a kolcsonzes elott.");
        }
    }

    public Konyv getKonyv() {
        return konyv;
    }

    public Olvaso getOlvaso() {
        return olvaso;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public LocalDate getHatarido() {
        return hatarido;
    }

    public LocalDate getVisszahozasDatum() {
        return visszahozasDatum;
    }

    public boolean visszaVanHozva() {
        return visszahozasDatum != null;
    }

    public void visszahoz(LocalDate datum) {
        Objects.requireNonNull(datum);

        if (visszaVanHozva()) {
            throw new IllegalStateException(
                    "Ezt a konyvet mar visszahoztak.");
        }

        if (datum.isBefore(this.datum)) {
            throw new IllegalArgumentException(
                    "A visszahozas nem lehet a kolcsonzes elott.");
        }

        visszahozasDatum = datum;
    }

    // Restores the return date when loading saved data.
    void betoltottVisszahozas(LocalDate datum) {
        if (datum != null) {
            visszahoz(datum);
        }
    }

    public long kesedelmesNapok(LocalDate vizsgaltDatum) {
        Objects.requireNonNull(vizsgaltDatum);

        LocalDate vege = visszahozasDatum == null
                ? vizsgaltDatum
                : visszahozasDatum;

        if (!vege.isAfter(hatarido)) {
            return 0;
        }

        return ChronoUnit.DAYS.between(hatarido, vege);
    }

    public long kesedelmiDij(LocalDate vizsgaltDatum,
                             long napiDij) {
        if (napiDij < 0) {
            throw new IllegalArgumentException(
                    "A napi dij nem lehet negativ.");
        }

        return kesedelmesNapok(vizsgaltDatum) * napiDij;
    }

    @Override
    public String toString() {
        return konyv.getCim() + " | " + olvaso.getNev()
                + " | kolcsonzes: " + datum
                + " | hatarido: " + hatarido
                + " | visszahozas: "
                + (visszahozasDatum == null
                        ? "meg nincs visszahozva"
                        : visszahozasDatum);
    }
}
