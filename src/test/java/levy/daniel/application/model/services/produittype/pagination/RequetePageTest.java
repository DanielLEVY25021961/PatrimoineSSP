package levy.daniel.application.model.services.produittype.pagination;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * <div>
 * <p style="font-weight:bold;">CLASSE RequetePageTest.java :</p>
 * <p>Classe de test JUnit pour {@link RequetePage}.</p>
 * <p>Vérifie les valeurs par défaut, les normalisations
 * et les copies défensives sur la liste des tris.</p>
 * </div>
 *
 * <div>
 * <p style="text-decoration:underline;">Tests couverts :</p>
 * <ul>
 * <li>Constructeurs d'arité 0, 2 et 3.</li>
 * <li>Normalisation de <code>pageNumber</code> et <code>pageSize</code>.</li>
 * <li>Gestion de <code>null</code> pour la liste des tris.</li>
 * <li>Copies défensives sur <code>tris</code>.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 26 mars 2026
 */
public class RequetePageTest {

    /* ************************ CONSTANTES ********************************/

    /**
     * Tag JUnit : "RequetePage".
     */
    public static final String TAG_REQUETE_PAGE = "RequetePage";

    /**
     * Tag JUnit : "RequetePage-Beton".
     */
    public static final String TAG_REQUETE_PAGE_BETON = "RequetePage-Beton";

    /**
     * Message AssertJ : "La requête ne doit pas être null.".
     */
    public static final String MSG_REQUETE_NOT_NULL =
        "La requête ne doit pas être null.";

    /**
     * Message AssertJ : "Le numéro de page par défaut doit être appliqué.".
     */
    public static final String MSG_PAGE_DEFAUT =
        "Le numéro de page par défaut doit être appliqué.";

    /**
     * Message AssertJ : "La taille de page par défaut doit être appliquée.".
     */
    public static final String MSG_TAILLE_DEFAUT =
        "La taille de page par défaut doit être appliquée.";

    /**
     * Message AssertJ : "Les tris doivent être non null.".
     */
    public static final String MSG_TRIS_NON_NULL =
        "Les tris doivent être non null.";

    /**
     * Message AssertJ : "Les tris doivent être vides.".
     */
    public static final String MSG_TRIS_VIDES =
        "Les tris doivent être vides.";

    /**
     * Message AssertJ : "Les valeurs valides doivent être conservées.".
     */
    public static final String MSG_VALEURS_VALIDES =
        "Les valeurs valides doivent être conservées.";

    /**
     * Message AssertJ :
     * "Le constructeur doit copier défensivement la liste des tris.".
     */
    public static final String MSG_COPIE_CONSTRUCTEUR =
        "Le constructeur doit copier défensivement la liste des tris.";

    /**
     * Message AssertJ :
     * "getTris() doit retourner une copie défensive.".
     */
    public static final String MSG_COPIE_GETTER =
        "getTris() doit retourner une copie défensive.";

    /**
     * Message AssertJ :
     * "setTris() doit copier défensivement la liste fournie.".
     */
    public static final String MSG_COPIE_SETTER =
        "setTris() doit copier défensivement la liste fournie.";

    /**
     * Message AssertJ :
     * "setTris(null) doit réinitialiser à une liste vide.".
     */
    public static final String MSG_SETTER_NULL =
        "setTris(null) doit réinitialiser à une liste vide.";

    /**
     * Message AssertJ :
     * "setPageNumber() doit normaliser les valeurs invalides.".
     */
    public static final String MSG_SET_PAGE_NUMBER =
        "setPageNumber() doit normaliser les valeurs invalides.";

    /**
     * Message AssertJ :
     * "setPageSize() doit normaliser les valeurs invalides.".
     */
    public static final String MSG_SET_PAGE_SIZE =
        "setPageSize() doit normaliser les valeurs invalides.";

    /**
     * "nom"
     */
    public static final String NOM = "nom";

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
    public RequetePageTest() {
        super();
    }


    /* ************************* METHODES *********************************/

    /**
     * <div>
     * <p>Vérifie que le constructeur d'arité nulle applique
     * les valeurs par défaut et une liste de tris vide.</p>
     * </div>
     */
    @Tag(TAG_REQUETE_PAGE)
    @DisplayName("new RequetePage() : valeurs par défaut et tris vides")
    @Test
    public final void testConstructeurParDefaut() {

        final RequetePage requetePage = new RequetePage();

        assertThat(requetePage).as(MSG_REQUETE_NOT_NULL).isNotNull();
        assertThat(requetePage.getPageNumber())
            .as(MSG_PAGE_DEFAUT)
            .isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(requetePage.getPageSize())
            .as(MSG_TAILLE_DEFAUT)
            .isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(requetePage.getTris())
            .as(MSG_TRIS_NON_NULL)
            .isNotNull();
        assertThat(requetePage.getTris())
            .as(MSG_TRIS_VIDES)
            .isEmpty();
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que le constructeur d'arité 2 conserve
     * les valeurs valides fournies.</p>
     * </div>
     */
    @Tag(TAG_REQUETE_PAGE)
    @DisplayName("RequetePage(pageNumber, pageSize) : conserve les valeurs valides")
    @Test
    public final void testConstructeurAriteDeuxConserveValeursValides() {

        final RequetePage requetePage = new RequetePage(3, 50);

        assertThat(requetePage.getPageNumber())
            .as(MSG_VALEURS_VALIDES)
            .isEqualTo(3);
        assertThat(requetePage.getPageSize())
            .as(MSG_VALEURS_VALIDES)
            .isEqualTo(50);
        assertThat(requetePage.getTris())
            .as(MSG_TRIS_VIDES)
            .isEmpty();
        
    } // __________________________________________________________________

    
    
    /**
     * <div>
     * <p>Vérifie que le constructeur d'arité 3 normalise
     * les valeurs invalides et tolère une liste de tris null.</p>
     * </div>
     */
    @Tag(TAG_REQUETE_PAGE_BETON)
    @DisplayName("RequetePage(-1, 0, null) : normalise et crée des tris vides")
    @Test
    public final void testConstructeurAriteTroisNormaliseValeursInvalidesEtNull() {

        final RequetePage requetePage = new RequetePage(-1, 0, null);

        assertThat(requetePage.getPageNumber())
            .as(MSG_PAGE_DEFAUT)
            .isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(requetePage.getPageSize())
            .as(MSG_TAILLE_DEFAUT)
            .isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(requetePage.getTris())
            .as(MSG_TRIS_NON_NULL)
            .isNotNull();
        assertThat(requetePage.getTris())
            .as(MSG_TRIS_VIDES)
            .isEmpty();
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que le constructeur copie défensivement
     * la liste des tris fournie.</p>
     * </div>
     */
    @Tag(TAG_REQUETE_PAGE_BETON)
    @DisplayName("Constructeur arité 3 : copie défensive de la liste des tris")
    @Test
    public final void testConstructeurCopieDefensivementLaListeDesTris() {

        final TriSpec triNom = new TriSpec(NOM, DirectionTri.ASC);
        final List<TriSpec> trisSource = new ArrayList<TriSpec>();
        trisSource.add(triNom);

        final RequetePage requetePage = new RequetePage(1, 10, trisSource);

        trisSource.clear();

        assertThat(requetePage.getTris())
            .as(MSG_COPIE_CONSTRUCTEUR)
            .containsExactly(triNom);
        
    } // __________________________________________________________________

    
    
    /**
     * <div>
     * <p>Vérifie que getTris() retourne toujours une copie défensive
     * et que la modification de la liste retournée
     * n'altère pas l'état interne.</p>
     * </div>
     */
    @Tag(TAG_REQUETE_PAGE_BETON)
    @DisplayName("getTris() : retourne une copie défensive")
    @Test
    public final void testGetTrisRetourneUneCopieDefensive() {

        final TriSpec triNom = new TriSpec(NOM, DirectionTri.ASC);
        final TriSpec triDateCreation =
            new TriSpec(DATE_CREATION, DirectionTri.DESC);

        final List<TriSpec> trisSource = new ArrayList<TriSpec>();
        trisSource.add(triNom);
        trisSource.add(triDateCreation);

        final RequetePage requetePage = new RequetePage(0, 20, trisSource);

        final List<TriSpec> trisRetournes = requetePage.getTris();
        trisRetournes.clear();

        assertThat(requetePage.getTris())
            .as(MSG_COPIE_GETTER)
            .containsExactly(triNom, triDateCreation);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que setPageNumber() et setPageSize()
     * normalisent les valeurs invalides.</p>
     * </div>
     */
    @Tag(TAG_REQUETE_PAGE_BETON)
    @DisplayName("setPageNumber() et setPageSize() : normalisation des valeurs invalides")
    @Test
    public final void testSettersPageNormalisentLesValeursInvalides() {

        final RequetePage requetePage = new RequetePage(5, 100);

        requetePage.setPageNumber(-8);
        requetePage.setPageSize(0);

        assertThat(requetePage.getPageNumber())
            .as(MSG_SET_PAGE_NUMBER)
            .isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(requetePage.getPageSize())
            .as(MSG_SET_PAGE_SIZE)
            .isEqualTo(RequetePage.TAILLE_DEFAUT);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que setTris() copie défensivement la liste fournie
     * et réinitialise à vide lorsque la valeur est null.</p>
     * </div>
     */
    @Tag(TAG_REQUETE_PAGE_BETON)
    @DisplayName("setTris() : copie défensive puis réinitialisation à vide sur null")
    @Test
    public final void testSetTrisCopieDefensivementEtTolereNull() {

        final RequetePage requetePage = new RequetePage();

        final TriSpec triNom = new TriSpec(NOM, DirectionTri.ASC);
        final List<TriSpec> trisSource = new ArrayList<TriSpec>();
        trisSource.add(triNom);

        requetePage.setTris(trisSource);
        trisSource.clear();

        assertThat(requetePage.getTris())
            .as(MSG_COPIE_SETTER)
            .containsExactly(triNom);

        requetePage.setTris(null);

        assertThat(requetePage.getTris())
            .as(MSG_SETTER_NULL)
            .isNotNull()
            .isEmpty();
        
    } // __________________________________________________________________
    
    

}