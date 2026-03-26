package levy.daniel.application.model.services.produittype.pagination;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * <div>
 * <p style="font-weight:bold;">CLASSE DirectionTriTest.java :</p>
 * <p>Classe de test JUnit pour {@link DirectionTri}.</p>
 * <p>Vérifie les valeurs déclarées de l'énumération
 * et leur résolution par nom.</p>
 * </div>
 *
 * <div>
 * <p style="text-decoration:underline;">Tests couverts :</p>
 * <ul>
 * <li>Ordre des valeurs de l'énumération.</li>
 * <li>Résolution de ASC par <code>valueOf(...)</code>.</li>
 * <li>Résolution de DESC par <code>valueOf(...)</code>.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 26 mars 2026
 */
public class DirectionTriTest {

    /* ************************ CONSTANTES ********************************/

    /**
     * Tag JUnit : "DirectionTri".
     */
    public static final String TAG_DIRECTION_TRI = "DirectionTri";

    /**
     * Tag JUnit : "DirectionTri-Beton".
     */
    public static final String TAG_DIRECTION_TRI_BETON =
        "DirectionTri-Beton";

    /**
     * Message AssertJ :
     * "L'énumération ne doit pas être null.".
     */
    public static final String MSG_ENUM_NOT_NULL =
        "L'énumération ne doit pas être null.";

    /**
     * Message AssertJ :
     * "L'ordre des constantes doit être ASC puis DESC.".
     */
    public static final String MSG_ORDRE_CONSTANTES =
        "L'ordre des constantes doit être ASC puis DESC.";

    /**
     * Message AssertJ :
     * "valueOf(\"ASC\") doit retourner DirectionTri.ASC.".
     */
    public static final String MSG_VALUE_OF_ASC =
        "valueOf(\"ASC\") doit retourner DirectionTri.ASC.";

    /**
     * Message AssertJ :
     * "valueOf(\"DESC\") doit retourner DirectionTri.DESC.".
     */
    public static final String MSG_VALUE_OF_DESC =
        "valueOf(\"DESC\") doit retourner DirectionTri.DESC.";

    /**
     * "ASC"
     */
    public static final String ASC = "ASC";

    /**
     * "DESC"
     */
    public static final String DESC = "DESC";


    /* ************************ CONSTRUCTEUR *******************************/

    /**
     * <div>
     * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
     * </div>
     */
    public DirectionTriTest() {
        super();
    }


    /* ************************* METHODES *********************************/

    
    
    /**
     * <div>
     * <p>Vérifie que l'énumération expose
     * exactement les constantes ASC puis DESC
     * dans cet ordre.</p>
     * </div>
     */
    @Tag(TAG_DIRECTION_TRI)
    @DisplayName("DirectionTri.values() : expose ASC puis DESC")
    @Test
    public final void testValuesExposeAscPuisDesc() {

        final DirectionTri[] valeurs = DirectionTri.values();

        assertThat(valeurs).as(MSG_ENUM_NOT_NULL).isNotNull();
        assertThat(valeurs)
            .as(MSG_ORDRE_CONSTANTES)
            .containsExactly(DirectionTri.ASC, DirectionTri.DESC);

    } // __________________________________________________________________


    
    /**
     * <div>
     * <p>Vérifie que valueOf(\"ASC\")
     * résout correctement la constante ASC.</p>
     * </div>
     */
    @Tag(TAG_DIRECTION_TRI_BETON)
    @DisplayName("DirectionTri.valueOf(\"ASC\") : retourne ASC")
    @Test
    public final void testValueOfAscRetourneAsc() {

        final DirectionTri directionTri = DirectionTri.valueOf(ASC);

        assertThat(directionTri).as(MSG_ENUM_NOT_NULL).isNotNull();
        assertThat(directionTri)
            .as(MSG_VALUE_OF_ASC)
            .isEqualTo(DirectionTri.ASC);

    } // __________________________________________________________________

    

    /**
     * <div>
     * <p>Vérifie que valueOf(\"DESC\")
     * résout correctement la constante DESC.</p>
     * </div>
     */
    @Tag(TAG_DIRECTION_TRI_BETON)
    @DisplayName("DirectionTri.valueOf(\"DESC\") : retourne DESC")
    @Test
    public final void testValueOfDescRetourneDesc() {

        final DirectionTri directionTri = DirectionTri.valueOf(DESC);

        assertThat(directionTri).as(MSG_ENUM_NOT_NULL).isNotNull();
        assertThat(directionTri)
            .as(MSG_VALUE_OF_DESC)
            .isEqualTo(DirectionTri.DESC);

    } // __________________________________________________________________


    
} // FIN DE LA CLASSE DirectionTriTest.___________________________________