package levy.daniel.application.model.services.produittype.pagination;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

/**
 * <div>
 * <p style="font-weight:bold;">CLASSE ResultatPageTest.java :</p>
 * <p>Classe de test JUnit pour {@link ResultatPage}.</p>
 * <p>Vérifie les normalisations, le calcul des métadonnées
 * de pagination, les copies défensives
 * et la conversion depuis Spring Data.</p>
 * </div>
 *
 * <div>
 * <p style="text-decoration:underline;">Tests couverts :</p>
 * <ul>
 * <li>Constructeur d'arité 4.</li>
 * <li>Normalisation des valeurs invalides.</li>
 * <li>Calcul de <code>totalPages</code>.</li>
 * <li>Navigation <code>isHasNext()</code> et <code>isHasPrevious()</code>.</li>
 * <li>Copies défensives sur <code>content</code>.</li>
 * <li>Conversion avec <code>fromSpringPage(...)</code>.</li>
 * <li>Détection de page vide avec <code>isEmpty()</code>.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 26 mars 2026
 */
public class ResultatPageTest {

    /* ************************ CONSTANTES ********************************/

    /**
     * Tag JUnit : "ResultatPage".
     */
    public static final String TAG_RESULTAT_PAGE = "ResultatPage";

    /**
     * Tag JUnit : "ResultatPage-Beton".
     */
    public static final String TAG_RESULTAT_PAGE_BETON =
        "ResultatPage-Beton";

    /**
     * Message AssertJ : "Le résultat paginé ne doit pas être null.".
     */
    public static final String MSG_RESULTAT_PAGE_NOT_NULL =
        "Le résultat paginé ne doit pas être null.";

    /**
     * Message AssertJ : "Le contenu doit être non null.".
     */
    public static final String MSG_CONTENT_NON_NULL =
        "Le contenu doit être non null.";

    /**
     * Message AssertJ : "Le contenu doit être vide.".
     */
    public static final String MSG_CONTENT_VIDE =
        "Le contenu doit être vide.";

    /**
     * Message AssertJ : "Le contenu doit être conservé.".
     */
    public static final String MSG_CONTENT_CONSERVE =
        "Le contenu doit être conservé.";

    /**
     * Message AssertJ :
     * "Le constructeur doit copier défensivement le contenu.".
     */
    public static final String MSG_COPIE_CONSTRUCTEUR =
        "Le constructeur doit copier défensivement le contenu.";

    /**
     * Message AssertJ :
     * "getContent() doit retourner une copie défensive.".
     */
    public static final String MSG_COPIE_GETTER =
        "getContent() doit retourner une copie défensive.";

    /**
     * Message AssertJ :
     * "Le numéro de page invalide doit être normalisé à 0.".
     */
    public static final String MSG_PAGE_NUMBER_NORMALISE =
        "Le numéro de page invalide doit être normalisé à 0.";

    /**
     * Message AssertJ :
     * "La taille de page invalide doit être normalisée à 1.".
     */
    public static final String MSG_PAGE_SIZE_NORMALISE =
        "La taille de page invalide doit être normalisée à 1.";

    /**
     * Message AssertJ :
     * "Le total d'éléments invalide doit être normalisé à 0.".
     */
    public static final String MSG_TOTAL_ELEMENTS_NORMALISE =
        "Le total d'éléments invalide doit être normalisé à 0.";

    /**
     * Message AssertJ :
     * "Le total de pages doit être calculé correctement.".
     */
    public static final String MSG_TOTAL_PAGES =
        "Le total de pages doit être calculé correctement.";

    /**
     * Message AssertJ :
     * "La page suivante doit exister.".
     */
    public static final String MSG_HAS_NEXT_TRUE =
        "La page suivante doit exister.";

    /**
     * Message AssertJ :
     * "La page suivante ne doit pas exister.".
     */
    public static final String MSG_HAS_NEXT_FALSE =
        "La page suivante ne doit pas exister.";

    /**
     * Message AssertJ :
     * "La page précédente doit exister.".
     */
    public static final String MSG_HAS_PREVIOUS_TRUE =
        "La page précédente doit exister.";

    /**
     * Message AssertJ :
     * "La page précédente ne doit pas exister.".
     */
    public static final String MSG_HAS_PREVIOUS_FALSE =
        "La page précédente ne doit pas exister.";

    /**
     * Message AssertJ :
     * "La page doit être vide.".
     */
    public static final String MSG_IS_EMPTY_TRUE =
        "La page doit être vide.";

    /**
     * Message AssertJ :
     * "La page ne doit pas être vide.".
     */
    public static final String MSG_IS_EMPTY_FALSE =
        "La page ne doit pas être vide.";

    /**
     * Message AssertJ :
     * "fromSpringPage() doit conserver le numéro de page.".
     */
    public static final String MSG_FROM_SPRING_PAGE_NUMBER =
        "fromSpringPage() doit conserver le numéro de page.";

    /**
     * Message AssertJ :
     * "fromSpringPage() doit conserver la taille de page.".
     */
    public static final String MSG_FROM_SPRING_PAGE_SIZE =
        "fromSpringPage() doit conserver la taille de page.";

    /**
     * Message AssertJ :
     * "fromSpringPage() doit conserver le total d'éléments.".
     */
    public static final String MSG_FROM_SPRING_TOTAL_ELEMENTS =
        "fromSpringPage() doit conserver le total d'éléments.";

    /**
     * Message AssertJ :
     * "fromSpringPage() doit conserver le contenu.".
     */
    public static final String MSG_FROM_SPRING_CONTENT =
        "fromSpringPage() doit conserver le contenu.";

    /**
     * "alpha"
     */
    public static final String ALPHA = "alpha";

    /**
     * "beta"
     */
    public static final String BETA = "beta";

    /**
     * "gamma"
     */
    public static final String GAMMA = "gamma";


    /* ************************ CONSTRUCTEUR *******************************/

    /**
     * <div>
     * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
     * </div>
     */
    public ResultatPageTest() {
        super();
    }


    /* ************************* METHODES *********************************/

    
    
    /**
     * <div>
     * <p>Vérifie que le constructeur normalise
     * les valeurs invalides et crée un contenu vide non null.</p>
     * </div>
     */
    @Tag(TAG_RESULTAT_PAGE)
    @DisplayName("ResultatPage(null, -1, 0, -1) : normalise les valeurs invalides")
    @Test
    public final void testConstructeurNormaliseValeursInvalidesEtNull() {

        final ResultatPage<String> resultatPage =
            new ResultatPage<String>(null, -1, 0, -1L);

        assertThat(resultatPage).as(MSG_RESULTAT_PAGE_NOT_NULL).isNotNull();
        assertThat(resultatPage.getContent())
            .as(MSG_CONTENT_NON_NULL)
            .isNotNull();
        assertThat(resultatPage.getContent())
            .as(MSG_CONTENT_VIDE)
            .isEmpty();
        assertThat(resultatPage.getPageNumber())
            .as(MSG_PAGE_NUMBER_NORMALISE)
            .isEqualTo(0);
        assertThat(resultatPage.getPageSize())
            .as(MSG_PAGE_SIZE_NORMALISE)
            .isEqualTo(1);
        assertThat(resultatPage.getTotalElements())
            .as(MSG_TOTAL_ELEMENTS_NORMALISE)
            .isEqualTo(0L);
        assertThat(resultatPage.getTotalPages())
            .as(MSG_TOTAL_PAGES)
            .isEqualTo(0);
        assertThat(resultatPage.isHasNext())
            .as(MSG_HAS_NEXT_FALSE)
            .isFalse();
        assertThat(resultatPage.isHasPrevious())
            .as(MSG_HAS_PREVIOUS_FALSE)
            .isFalse();
        assertThat(resultatPage.isEmpty())
            .as(MSG_IS_EMPTY_TRUE)
            .isTrue();

    } // __________________________________________________________________


    
    /**
     * <div>
     * <p>Vérifie que le constructeur conserve
     * les valeurs valides et calcule correctement les métadonnées.</p>
     * </div>
     */
    @Tag(TAG_RESULTAT_PAGE)
    @DisplayName("ResultatPage(content, 1, 2, 5) : conserve les valeurs et calcule totalPages")
    @Test
    public final void testConstructeurConserveValeursValidesEtCalculeMetadonnees() {

        final List<String> contenu = new ArrayList<String>();
        contenu.add(ALPHA);
        contenu.add(BETA);

        final ResultatPage<String> resultatPage =
            new ResultatPage<String>(contenu, 1, 2, 5L);

        assertThat(resultatPage.getContent())
            .as(MSG_CONTENT_CONSERVE)
            .containsExactly(ALPHA, BETA);
        assertThat(resultatPage.getPageNumber())
            .as(MSG_PAGE_NUMBER_NORMALISE)
            .isEqualTo(1);
        assertThat(resultatPage.getPageSize())
            .as(MSG_PAGE_SIZE_NORMALISE)
            .isEqualTo(2);
        assertThat(resultatPage.getTotalElements())
            .as(MSG_TOTAL_ELEMENTS_NORMALISE)
            .isEqualTo(5L);
        assertThat(resultatPage.getTotalPages())
            .as(MSG_TOTAL_PAGES)
            .isEqualTo(3);
        assertThat(resultatPage.isHasNext())
            .as(MSG_HAS_NEXT_TRUE)
            .isTrue();
        assertThat(resultatPage.isHasPrevious())
            .as(MSG_HAS_PREVIOUS_TRUE)
            .isTrue();
        assertThat(resultatPage.isEmpty())
            .as(MSG_IS_EMPTY_FALSE)
            .isFalse();

    } // __________________________________________________________________


    
    /**
     * <div>
     * <p>Vérifie que le constructeur copie défensivement
     * la liste de contenu fournie.</p>
     * </div>
     */
    @Tag(TAG_RESULTAT_PAGE_BETON)
    @DisplayName("Constructeur : copie défensive du contenu")
    @Test
    public final void testConstructeurCopieDefensivementLeContenu() {

        final List<String> contenuSource = new ArrayList<String>();
        contenuSource.add(ALPHA);
        contenuSource.add(BETA);

        final ResultatPage<String> resultatPage =
            new ResultatPage<String>(contenuSource, 0, 10, 2L);

        contenuSource.clear();

        assertThat(resultatPage.getContent())
            .as(MSG_COPIE_CONSTRUCTEUR)
            .containsExactly(ALPHA, BETA);

    } // __________________________________________________________________


    
    /**
     * <div>
     * <p>Vérifie que getContent() retourne une copie défensive
     * et que la modification de la liste retournée
     * n'altère pas l'état interne.</p>
     * </div>
     */
    @Tag(TAG_RESULTAT_PAGE_BETON)
    @DisplayName("getContent() : retourne une copie défensive")
    @Test
    public final void testGetContentRetourneUneCopieDefensive() {

        final List<String> contenuSource = new ArrayList<String>();
        contenuSource.add(ALPHA);
        contenuSource.add(BETA);

        final ResultatPage<String> resultatPage =
            new ResultatPage<String>(contenuSource, 0, 10, 2L);

        final List<String> contenuRetourne = resultatPage.getContent();
        contenuRetourne.clear();
        contenuRetourne.add(GAMMA);

        assertThat(resultatPage.getContent())
            .as(MSG_COPIE_GETTER)
            .containsExactly(ALPHA, BETA);

    } // __________________________________________________________________


    
    /**
     * <div>
     * <p>Vérifie que fromSpringPage() convertit correctement
     * une page Spring Data en ResultatPage.</p>
     * </div>
     */
    @Tag(TAG_RESULTAT_PAGE_BETON)
    @DisplayName("fromSpringPage() : conversion correcte depuis Spring Data")
    @Test
    public final void testFromSpringPageConvertitCorrectement() {

        final List<String> contenuSpring = new ArrayList<String>();
        contenuSpring.add(ALPHA);
        contenuSpring.add(BETA);

        final Page<String> springPage =
            new PageImpl<String>(contenuSpring, PageRequest.of(2, 5), 12L);

        final ResultatPage<String> resultatPage =
            ResultatPage.fromSpringPage(springPage);

        assertThat(resultatPage).as(MSG_RESULTAT_PAGE_NOT_NULL).isNotNull();
        assertThat(resultatPage.getContent())
            .as(MSG_FROM_SPRING_CONTENT)
            .containsExactly(ALPHA, BETA);
        assertThat(resultatPage.getPageNumber())
            .as(MSG_FROM_SPRING_PAGE_NUMBER)
            .isEqualTo(2);
        assertThat(resultatPage.getPageSize())
            .as(MSG_FROM_SPRING_PAGE_SIZE)
            .isEqualTo(5);
        assertThat(resultatPage.getTotalElements())
            .as(MSG_FROM_SPRING_TOTAL_ELEMENTS)
            .isEqualTo(12L);
        assertThat(resultatPage.getTotalPages())
            .as(MSG_TOTAL_PAGES)
            .isEqualTo(3);
        assertThat(resultatPage.isHasNext())
            .as(MSG_HAS_NEXT_FALSE)
            .isFalse();
        assertThat(resultatPage.isHasPrevious())
            .as(MSG_HAS_PREVIOUS_TRUE)
            .isTrue();
        assertThat(resultatPage.isEmpty())
            .as(MSG_IS_EMPTY_FALSE)
            .isFalse();

    } // __________________________________________________________________


    
    /**
     * <div>
     * <p>Vérifie que le total de pages est borné
     * à Integer.MAX_VALUE lorsque le calcul dépasse cette limite.</p>
     * </div>
     */
    @Tag(TAG_RESULTAT_PAGE_BETON)
    @DisplayName("totalPages : bornage à Integer.MAX_VALUE")
    @Test
    public final void testTotalPagesEstBorneAIntegerMaxValue() {

        final ResultatPage<String> resultatPage =
            new ResultatPage<String>(
                new ArrayList<String>(),
                0,
                1,
                (long) Integer.MAX_VALUE + 10L);

        assertThat(resultatPage.getTotalPages())
            .as(MSG_TOTAL_PAGES)
            .isEqualTo(Integer.MAX_VALUE);

    } // __________________________________________________________________


    
} // FIN DE LA CLASSE ResultatPageTest.___________________________________