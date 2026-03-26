package levy.daniel.application.model.services.produittype.pagination;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * <div>
 * <p style="font-weight:bold;">CLASSE TriSpecTest.java :</p>
 * <p>Classe de test JUnit pour {@link TriSpec}.</p>
 * <p>Vérifie la direction par défaut, l'inversion de direction
 * et le comportement des getters/setters.</p>
 * </div>
 *
 * <div>
 * <p style="text-decoration:underline;">Tests couverts :</p>
 * <ul>
 * <li>Constructeur d'arité 2.</li>
 * <li>Application de la direction par défaut.</li>
 * <li>Inversion de direction.</li>
 * <li>Conservation de la propriété.</li>
 * <li>Comportement des setters.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 26 mars 2026
 */
public class TriSpecTest {

    /* ************************ CONSTANTES ********************************/

    /**
     * Tag JUnit : "TriSpec".
     */
    public static final String TAG_TRI_SPEC = "TriSpec";

    /**
     * Tag JUnit : "TriSpec-Beton".
     */
    public static final String TAG_TRI_SPEC_BETON = "TriSpec-Beton";

    /**
     * Message AssertJ : "Le tri ne doit pas être null.".
     */
    public static final String MSG_TRI_SPEC_NOT_NULL =
        "Le tri ne doit pas être null.";

    /**
     * Message AssertJ : "La propriété doit être conservée.".
     */
    public static final String MSG_PROPRIETE_CONSERVEE =
        "La propriété doit être conservée.";

    /**
     * Message AssertJ : "La direction fournie doit être conservée.".
     */
    public static final String MSG_DIRECTION_CONSERVEE =
        "La direction fournie doit être conservée.";

    /**
     * Message AssertJ :
     * "La direction par défaut doit être appliquée.".
     */
    public static final String MSG_DIRECTION_DEFAUT =
        "La direction par défaut doit être appliquée.";

    /**
     * Message AssertJ :
     * "inverserDirection() doit retourner une nouvelle instance.".
     */
    public static final String MSG_NOUVELLE_INSTANCE =
        "inverserDirection() doit retourner une nouvelle instance.";

    /**
     * Message AssertJ :
     * "inverserDirection() doit inverser ASC en DESC.".
     */
    public static final String MSG_INVERSION_ASC_DESC =
        "inverserDirection() doit inverser ASC en DESC.";

    /**
     * Message AssertJ :
     * "inverserDirection() doit inverser DESC en ASC.".
     */
    public static final String MSG_INVERSION_DESC_ASC =
        "inverserDirection() doit inverser DESC en ASC.";

    /**
     * Message AssertJ :
     * "setDirection() doit conserver la direction valide.".
     */
    public static final String MSG_SET_DIRECTION_VALIDE =
        "setDirection() doit conserver la direction valide.";

    /**
     * Message AssertJ :
     * "setDirection(null) doit appliquer la direction par défaut.".
     */
    public static final String MSG_SET_DIRECTION_NULL =
        "setDirection(null) doit appliquer la direction par défaut.";

    /**
     * Message AssertJ :
     * "setPropriete() doit conserver la propriété fournie.".
     */
    public static final String MSG_SET_PROPRIETE =
        "setPropriete() doit conserver la propriété fournie.";

    /**
     * Message AssertJ :
     * "setPropriete(null) doit accepter null tel quel.".
     */
    public static final String MSG_SET_PROPRIETE_NULL =
        "setPropriete(null) doit accepter null tel quel.";

    /**
     * "nom"
     */
    public static final String NOM = "nom";

    /**
     * "libelle"
     */
    public static final String LIBELLE = "libelle";

    /**
     * "dateCreation"
     */
    public static final String DATE_CREATION = "dateCreation";


    /* ************************ CONSTRUCTEUR *******************************/

    /**
     * <div>
     * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
     * </div>
     */
    public TriSpecTest() {
        super();
    }


    
    /* ************************* METHODES *********************************/

    
    
    /**
     * <div>
     * <p>Vérifie que le constructeur conserve
     * la propriété et la direction valides fournies.</p>
     * </div>
     */
    @Tag(TAG_TRI_SPEC)
    @DisplayName("TriSpec(propriete, direction) : conserve les valeurs valides")
    @Test
    public final void testConstructeurConserveValeursValides() {

        final TriSpec triSpec = new TriSpec(NOM, DirectionTri.DESC);

        assertThat(triSpec).as(MSG_TRI_SPEC_NOT_NULL).isNotNull();
        assertThat(triSpec.getPropriete())
            .as(MSG_PROPRIETE_CONSERVEE)
            .isEqualTo(NOM);
        assertThat(triSpec.getDirection())
            .as(MSG_DIRECTION_CONSERVEE)
            .isEqualTo(DirectionTri.DESC);

    } // __________________________________________________________________


    
    /**
     * <div>
     * <p>Vérifie que le constructeur applique
     * la direction par défaut lorsque la direction est null.</p>
     * </div>
     */
    @Tag(TAG_TRI_SPEC)
    @DisplayName("TriSpec(propriete, null) : applique la direction par défaut")
    @Test
    public final void testConstructeurAppliqueDirectionParDefautSiNull() {

        final TriSpec triSpec = new TriSpec(NOM, null);

        assertThat(triSpec).as(MSG_TRI_SPEC_NOT_NULL).isNotNull();
        assertThat(triSpec.getPropriete())
            .as(MSG_PROPRIETE_CONSERVEE)
            .isEqualTo(NOM);
        assertThat(triSpec.getDirection())
            .as(MSG_DIRECTION_DEFAUT)
            .isEqualTo(TriSpec.DIRECTION_DEFAUT);

    } // __________________________________________________________________


    
    /**
     * <div>
     * <p>Vérifie que inverserDirection() retourne
     * une nouvelle instance avec la même propriété
     * et la direction inversée de ASC vers DESC.</p>
     * </div>
     */
    @Tag(TAG_TRI_SPEC_BETON)
    @DisplayName("inverserDirection() : inverse ASC en DESC")
    @Test
    public final void testInverserDirectionInverseAscEnDesc() {

        final TriSpec triSpec = new TriSpec(NOM, DirectionTri.ASC);

        final TriSpec triInverse = triSpec.inverserDirection();

        assertThat(triInverse).as(MSG_TRI_SPEC_NOT_NULL).isNotNull();
        assertThat(triInverse)
            .as(MSG_NOUVELLE_INSTANCE)
            .isNotSameAs(triSpec);
        assertThat(triInverse.getPropriete())
            .as(MSG_PROPRIETE_CONSERVEE)
            .isEqualTo(NOM);
        assertThat(triInverse.getDirection())
            .as(MSG_INVERSION_ASC_DESC)
            .isEqualTo(DirectionTri.DESC);

    } // __________________________________________________________________


    
    /**
     * <div>
     * <p>Vérifie que inverserDirection() conserve
     * la propriété et inverse la direction de DESC vers ASC.</p>
     * </div>
     */
    @Tag(TAG_TRI_SPEC_BETON)
    @DisplayName("inverserDirection() : inverse DESC en ASC")
    @Test
    public final void testInverserDirectionInverseDescEnAsc() {

        final TriSpec triSpec = new TriSpec(DATE_CREATION, DirectionTri.DESC);

        final TriSpec triInverse = triSpec.inverserDirection();

        assertThat(triInverse).as(MSG_TRI_SPEC_NOT_NULL).isNotNull();
        assertThat(triInverse)
            .as(MSG_NOUVELLE_INSTANCE)
            .isNotSameAs(triSpec);
        assertThat(triInverse.getPropriete())
            .as(MSG_PROPRIETE_CONSERVEE)
            .isEqualTo(DATE_CREATION);
        assertThat(triInverse.getDirection())
            .as(MSG_INVERSION_DESC_ASC)
            .isEqualTo(DirectionTri.ASC);

    } // __________________________________________________________________


    
    /**
     * <div>
     * <p>Vérifie que setDirection() conserve
     * une direction valide fournie.</p>
     * </div>
     */
    @Tag(TAG_TRI_SPEC_BETON)
    @DisplayName("setDirection(DESC) : conserve la direction valide")
    @Test
    public final void testSetDirectionConserveValeurValide() {

        final TriSpec triSpec = new TriSpec(NOM, DirectionTri.ASC);

        triSpec.setDirection(DirectionTri.DESC);

        assertThat(triSpec.getDirection())
            .as(MSG_SET_DIRECTION_VALIDE)
            .isEqualTo(DirectionTri.DESC);

    } // __________________________________________________________________


    
    /**
     * <div>
     * <p>Vérifie que setDirection(null) applique
     * la direction par défaut.</p>
     * </div>
     */
    @Tag(TAG_TRI_SPEC_BETON)
    @DisplayName("setDirection(null) : applique la direction par défaut")
    @Test
    public final void testSetDirectionNullAppliqueDirectionParDefaut() {

        final TriSpec triSpec = new TriSpec(NOM, DirectionTri.DESC);

        triSpec.setDirection(null);

        assertThat(triSpec.getDirection())
            .as(MSG_SET_DIRECTION_NULL)
            .isEqualTo(TriSpec.DIRECTION_DEFAUT);

    } // __________________________________________________________________


    
    /**
     * <div>
     * <p>Vérifie que setPropriete() conserve
     * la propriété fournie, y compris null.</p>
     * </div>
     */
    @Tag(TAG_TRI_SPEC_BETON)
    @DisplayName("setPropriete() : conserve la valeur fournie puis accepte null")
    @Test
    public final void testSetProprieteConserveValeurPuisAccepteNull() {

        final TriSpec triSpec = new TriSpec(NOM, DirectionTri.ASC);

        triSpec.setPropriete(LIBELLE);

        assertThat(triSpec.getPropriete())
            .as(MSG_SET_PROPRIETE)
            .isEqualTo(LIBELLE);

        triSpec.setPropriete(null);

        assertThat(triSpec.getPropriete())
            .as(MSG_SET_PROPRIETE_NULL)
            .isNull();

    } // __________________________________________________________________

    

} // FIN DE LA CLASSE TriSpecTest.________________________________________