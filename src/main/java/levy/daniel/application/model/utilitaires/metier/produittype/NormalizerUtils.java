package levy.daniel.application.model.utilitaires.metier.produittype;

import java.text.Normalizer;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <style>p, ul, li {line-height : 1em;}</style>
 * <div>
 * <p style="font-weight:bold;">CLASSE NormalizerUtils :</p>
 * <p>Classe utilitaire pour la normalisation des chaînes de caractères.</p>
 * <p>Fonctionnalités principales :</p>
 * <ul>
 * <li>Suppression ou remplacement des diacritiques (accents, cédilles, etc.).</li>
 * <li>Conversion en minuscules selon une locale choisie.</li>
 * <li>Gestion des chaînes <code>null</code> et vides.</li>
 * </ul>
 * </div>
 *
 * <div>
 * <p style="text-decoration:underline;">Exemples d'utilisation :</p>
 * <ul>
 * <li><code>NormalizerUtils.normalize("Café")</code> → "cafe"</li>
 * <li><code>NormalizerUtils.normalize("Hôtel Naïve", true, Locale.FRENCH)</code> → "hotel naive"</li>
 * <li><code>NormalizerUtils.normalize("Hôtel Naïve", false, Locale.ENGLISH)</code> → "hôtel naïve"</li>
 * <li><code>NormalizerUtils.normalize(null)</code> → ""</li>
 * </ul>
 * </div>
 *
 * <div>
 * <p style="font-weight:bold;">Cas particuliers :</p>
 * <ul>
 * <li>Les caractères spéciaux (ex : <code>@#$</code>) ne sont pas modifiés.</li>
 * <li>Les chaînes vides restent vides.</li>
 * <li>Les espaces et ponctuations sont conservés.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 11.0
 * @since 10 janvier 2026
 */
public final class NormalizerUtils {

    // ************************ATTRIBUTS************************************/

    /**
     * <div>
     * <p>Pattern pour supprimer les diacritiques.</p>
     * </div>
     */
    private static final Pattern DIACRITICS_PATTERN =
            Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    /**
     * <div>
     * <p>Map pour remplacer les caractères accentués.</p>
     * </div>
     */
    private static final Map<Character, Character> ACCENTED_CHARACTERS_MAP 
    	= new HashMap<>();

    static {
        ACCENTED_CHARACTERS_MAP.put('À', 'a');
        ACCENTED_CHARACTERS_MAP.put('Â', 'a');
        ACCENTED_CHARACTERS_MAP.put('Ä', 'a');
        ACCENTED_CHARACTERS_MAP.put('Á', 'a');
        ACCENTED_CHARACTERS_MAP.put('È', 'e');
        ACCENTED_CHARACTERS_MAP.put('É', 'e');
        ACCENTED_CHARACTERS_MAP.put('Ê', 'e');
        ACCENTED_CHARACTERS_MAP.put('Ë', 'e');
        ACCENTED_CHARACTERS_MAP.put('Î', 'i');
        ACCENTED_CHARACTERS_MAP.put('Ï', 'i');
        ACCENTED_CHARACTERS_MAP.put('Í', 'i');
        ACCENTED_CHARACTERS_MAP.put('Ì', 'i');
        ACCENTED_CHARACTERS_MAP.put('Ô', 'o');
        ACCENTED_CHARACTERS_MAP.put('Ö', 'o');
        ACCENTED_CHARACTERS_MAP.put('Ò', 'o');
        ACCENTED_CHARACTERS_MAP.put('Û', 'u');
        ACCENTED_CHARACTERS_MAP.put('Ü', 'u');
        ACCENTED_CHARACTERS_MAP.put('Ù', 'u');
        ACCENTED_CHARACTERS_MAP.put('Ç', 'c');
        ACCENTED_CHARACTERS_MAP.put('Ñ', 'n');
    }

    /**
     * <style>p, ul, li {line-height : 1em;}</style>
     * <div>
     * <p>LOG : Logger : </p>
     * <p>Logger pour Log4j (utilisant org.apache.logging.log4j).</p>
     * <p>dépendances : </p>
     * <ul>
     * <li><code>org.apache.logging.log4j.Logger</code></li>
     * <li><code>org.apache.logging.log4j.LogManager</code></li>
     * </ul>
     * </div>
     */
    private static final Logger LOG =
        LogManager.getLogger(NormalizerUtils.class);

    // *************************METHODES************************************/

    /**
     * <div>
     * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
     * <p>Private pour bloquer l'instanciation (classe utilitaire).</p>
     * </div>
     */
    private NormalizerUtils() {
        super();
    }

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * Normalise une chaîne en conservant les diacritiques
     * et en la convertissant en minuscules.</p>
     * <p>Utilise la locale par défaut du système.</p>
     * </div>
     *
     * @param pInput : String : La chaîne à normaliser.
     * @return String :
     * La chaîne normalisée 
     * (avec conservation des diacritiques, en minuscules).
     * @see #normalize(String, boolean, Locale)
     */
    public static String normalize(final String pInput) {
        return normalize(pInput, false, Locale.getDefault());
    }

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">
     * Normalise une chaîne avec des options de configuration.</p>
     * </div>
     *
     * @param pInput : String : La chaîne à normaliser.
     * @param removeDiacritics : boolean :
     *          Si <code>true</code>, supprime les diacritiques.
     *          Si <code>false</code>, les conserve.
     * @param locale : Locale :
     * La locale à utiliser pour la conversion en minuscules.
     * @return String :
     * La chaîne normalisée selon les options choisies.
     */
    public static String normalize(
        final String pInput,
        final boolean removeDiacritics,
        final Locale locale) {

        /*
         * Retourne une chaîne vide si l'entrée est null.
         */
        if (pInput == null) {
            return "";
        }

        /*
         * Retourne la chaîne inchangée si elle est vide.
         */
        if (pInput.isEmpty()) {
            return pInput;
        }

        String result = pInput;

        /*
         * Remplacement des caractères accentués 
         * par leur équivalent non-accentué en minuscule.
         */
        if (removeDiacritics) {
        	
            final StringBuilder stringBuilder = new StringBuilder();
            
            for (final char c : result.toCharArray()) {
            	
                final Character replacement = ACCENTED_CHARACTERS_MAP.get(c);
                stringBuilder.append(replacement != null ? replacement : c);
            }
            result = stringBuilder.toString();
        }

        /*
         * Décomposition des caractères en forme NFD
         * pour séparer les diacritiques.
         */
        result = Normalizer.normalize(result, Normalizer.Form.NFD);

        /*
         * Suppression des diacritiques si demandé.
         */
        if (removeDiacritics) {
            /*
             * Suppression des diacritiques combinés.
             */
            result = DIACRITICS_PATTERN.matcher(result).replaceAll("");
        }

        /*
         * Recomposition en forme NFC pour éviter
         * les caractères décomposés.
         */
        result = Normalizer.normalize(result, Normalizer.Form.NFC);

        /*
         * Conversion en minuscules selon la locale.
         */
        return result.toLowerCase(locale);
    }
    
}
