package levy.daniel.application.model.utilitaires.metier.produittype;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">CLASSE NormalizerUtilsTest :</p>
 * <p>Classe de test JUnit pour {@link NormalizerUtils}.</p>
 * <p>Tests couverts :</p>
 * <ul>
 * <li>Normalisation avec/sans suppression des diacritiques.</li>
 * <li>Conversion en minuscules selon différentes locales.</li>
 * <li>Gestion des chaînes <code>null</code> et vides.</li>
 * </ul>
 * </div>
 *
 * <div>
 * <p style="text-decoration:underline;">Exemples de tests :</p>
 * <ul>
 * <li>Vérification que <code>normalize("Café")</code> retourne "café" (avec conservation des diacritiques).</li>
 * <li>Vérification que <code>normalize("Hôtel", true, Locale.FRENCH)</code> retourne "hotel".</li>
 * <li>Vérification que <code>normalize(null)</code> retourne une chaîne vide.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 3.0
 * @since 10 janvier 2026
 */
public class NormalizerUtilsTest {

    // ************************ CONSTANTES ********************************/

    /**
     * Boolean qui commande l'affichage pour tous les tests.
     */
    public static final boolean AFFICHAGE_GENERAL = true;

    /**
     * "unused"
     */
    public static final String UNUSED = "unused";

    /**
     * "null"
     */
    public static final String NULL = "null";

    /**
     * "Café"
     */
    public static final String CAFE_ACCENT = "Café";

    /**
     * "café"
     */
    public static final String CAFE_SANS_ACCENT_MINUSCULE = "café";

    /**
     * "cafe"
     */
    public static final String CAFE_SANS_ACCENT = "cafe";

    /**
     * "Hôtel Naïve"
     */
    public static final String HOTEL_NAIVE_ACCENT = "Hôtel Naïve";

    /**
     * "hôtel naïve"
     */
    public static final String HOTEL_NAIVE_ACCENT_MINUSCULE = "hôtel naïve";

    /**
     * "hotel naive"
     */
    public static final String HOTEL_NAIVE_SANS_ACCENT = "hotel naive";

    /**
     * "ÉÉÊËÀÂÄÁÎÏÍÌÔÖÒÛÜÙÇÑ"
     */
    public static final String CHAINE_DIACRITIQUES = "ÉÉÊËÀÂÄÁÎÏÍÌÔÖÒÛÜÙÇÑ";

    /**
     * "eeeeaaaaiiiiooouuucn"
     */
    public static final String CHAINE_SANS_DIACRITIQUES = "eeeeaaaaiiiiooouuucn";

    /**
     * "Chaîne vide"
     */
    public static final String CHAINE_VIDE = "";

    // ************************ ATTRIBUTS ********************************/

    /**
     * <style>p, ul, li {line-height : 1em;}</style>
     * <div>
     * <p>LOG : Logger : </p>
     * <p>Logger pour Log4j (utilisant org.apache.logging.log4j).</p>
     * </div>
     */
    private static final Logger LOG =
        LogManager.getLogger(NormalizerUtilsTest.class);

    // ************************* MÉTHODES **********************************/

    /**
     * <div>
     * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
     * </div>
     */
    public NormalizerUtilsTest() {
        super();
    }

    /**
     * <div>
     * <p>Teste la méthode <code>normalize(String)</code>.</p>
     * <ul>
     * <li>Vérifie la normalisation par défaut (conservation des diacritiques).</li>
     * <li>Vérifie la conversion en minuscules.</li>
     * <li>Vérifie la gestion des chaînes <code>null</code> et vides.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testNormalizeDefault() : vérifie la normalisation par défaut (conservation des diacritiques)")
    @Tag("normalize")
    @Test
    public final void testNormalizeDefault() {
        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("********** CLASSE NormalizerUtilsTest - méthode testNormalizeDefault() **********");
            System.out.println("CE TEST VÉRIFIE LA NORMALISATION PAR DÉFAUT (CONSERVATION DES DIACRITIQUES).");
            System.out.println();
        }

        // ARRANGE - GIVEN
        final String inputCafe = CAFE_ACCENT;
        final String inputHotelNaive = HOTEL_NAIVE_ACCENT;
        final String inputNull = null;
        final String inputVide = CHAINE_VIDE;

        // ACT - WHEN
        final String resultCafe = NormalizerUtils.normalize(inputCafe);
        final String resultHotelNaive = NormalizerUtils.normalize(inputHotelNaive);
        final String resultNull = NormalizerUtils.normalize(inputNull);
        final String resultVide = NormalizerUtils.normalize(inputVide);

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println("normalize(\"Café\") : " + resultCafe);
            System.out.println("normalize(\"Hôtel Naïve\") : " + resultHotelNaive);
            System.out.println("normalize(null) : " + resultNull);
            System.out.println("normalize(\"\") : " + resultVide);
        }

        // ASSERT - THEN
        assertEquals(CAFE_SANS_ACCENT_MINUSCULE, resultCafe, "La normalisation de \"Café\" doit retourner \"café\".");
        assertEquals(HOTEL_NAIVE_ACCENT_MINUSCULE, resultHotelNaive, "La normalisation de \"Hôtel Naïve\" doit retourner \"hôtel naïve\".");
        assertEquals(CHAINE_VIDE, resultNull, "La normalisation de null doit retourner une chaîne vide.");
        assertEquals(CHAINE_VIDE, resultVide, "La normalisation d'une chaîne vide doit retourner une chaîne vide.");
    }

    /**
     * <div>
     * <p>Teste la méthode <code>normalize(String, boolean, Locale)</code>.</p>
     * <ul>
     * <li>Vérifie la normalisation avec suppression des diacritiques.</li>
     * <li>Vérifie la normalisation avec conservation des diacritiques.</li>
     * <li>Vérifie la conversion en minuscules selon différentes locales.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testNormalizeWithOptions() : vérifie la normalisation avec options (suppression/conservation des diacritiques)")
    @Tag("normalize")
    @Test
    public final void testNormalizeWithOptions() {
        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = true;

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("********** CLASSE NormalizerUtilsTest - méthode testNormalizeWithOptions() **********");
            System.out.println("CE TEST VÉRIFIE LA NORMALISATION AVEC OPTIONS (SUPPRESSION/CONSERVATION DES DIACRITIQUES).");
            System.out.println();
        }

        // ARRANGE - GIVEN
        final String inputDiacritiques = CHAINE_DIACRITIQUES;
        final String inputCafe = CAFE_ACCENT;
        final String inputHotelNaive = HOTEL_NAIVE_ACCENT;

        // ACT - WHEN : Avec suppression des diacritiques
        final String resultSansDiacritiques = NormalizerUtils.normalize(inputDiacritiques, true, Locale.FRENCH);
        final String resultCafeSansDiacritiques = NormalizerUtils.normalize(inputCafe, true, Locale.FRENCH);
        final String resultHotelNaiveSansDiacritiques = NormalizerUtils.normalize(inputHotelNaive, true, Locale.FRENCH);

        // ACT - WHEN : Avec conservation des diacritiques
        final String resultAvecDiacritiques = NormalizerUtils.normalize(inputDiacritiques, false, Locale.FRENCH);
        final String resultCafeAvecDiacritiques = NormalizerUtils.normalize(inputCafe, false, Locale.FRENCH);
        final String resultHotelNaiveAvecDiacritiques = NormalizerUtils.normalize(inputHotelNaive, false, Locale.FRENCH);

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println("normalize(\"ÉÉÊËÀÂÄÁÎÏÍÌÔÖÒÛÜÙÇÑ\", true, Locale.FRENCH) : " + resultSansDiacritiques);
            System.out.println("normalize(\"Café\", true, Locale.FRENCH) : " + resultCafeSansDiacritiques);
            System.out.println("normalize(\"Hôtel Naïve\", true, Locale.FRENCH) : " + resultHotelNaiveSansDiacritiques);
            System.out.println("normalize(\"ÉÉÊËÀÂÄÁÎÏÍÌÔÖÒÛÜÙÇÑ\", false, Locale.FRENCH) : " + resultAvecDiacritiques);
            System.out.println("normalize(\"Café\", false, Locale.FRENCH) : " + resultCafeAvecDiacritiques);
            System.out.println("normalize(\"Hôtel Naïve\", false, Locale.FRENCH) : " + resultHotelNaiveAvecDiacritiques);
        }

        // ASSERT - THEN : Avec suppression des diacritiques
        assertEquals(CHAINE_SANS_DIACRITIQUES, resultSansDiacritiques, "La normalisation avec suppression des diacritiques doit retourner \"eeeeaaiiiiooouuucn\".");
        assertEquals(CAFE_SANS_ACCENT, resultCafeSansDiacritiques, "La normalisation de \"Café\" avec suppression des diacritiques doit retourner \"cafe\".");
        assertEquals(HOTEL_NAIVE_SANS_ACCENT, resultHotelNaiveSansDiacritiques, "La normalisation de \"Hôtel Naïve\" avec suppression des diacritiques doit retourner \"hotel naive\".");

        // ASSERT - THEN : Avec conservation des diacritiques
        assertEquals(CHAINE_DIACRITIQUES.toLowerCase(Locale.FRENCH), resultAvecDiacritiques, "La normalisation avec conservation des diacritiques doit retourner la chaîne en minuscules.");
        assertEquals(CAFE_SANS_ACCENT_MINUSCULE, resultCafeAvecDiacritiques, "La normalisation de \"Café\" avec conservation des diacritiques doit retourner \"café\".");
        assertEquals(HOTEL_NAIVE_ACCENT_MINUSCULE, resultHotelNaiveAvecDiacritiques, "La normalisation de \"Hôtel Naïve\" avec conservation des diacritiques doit retourner \"hôtel naïve\".");
    }
}
