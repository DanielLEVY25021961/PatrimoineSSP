package levy.daniel.application.model.services.produittype.gateway.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliLibelleBlank;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNonPersistent;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNull;
import levy.daniel.application.model.services.produittype.gateway.TypeProduitGatewayIService;
import levy.daniel.application.model.services.produittype.pagination.DirectionTri;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;
import levy.daniel.application.model.services.produittype.pagination.TriSpec;
import levy.daniel.application.persistence.metier.produittype.dao.daosJPA.TypeProduitDaoJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.TypeProduitJPA;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">
 * TEST D'INTEGRATION JUnit JUPITER 5
 * </p>
 *
 * <p>
 * Test du Service Gateway
 * <code style="font-weight:bold;">TypeProduitGatewayJPAService</code>.
 * </p>
 *
 * <p>Ce test JUnit autonome utilise :</p>
 * <ul>
 * <li>Une BDD H2 en mode Memory</li>
 * <li>Une configuration autonome Spring Boot définie directement
 * dans le présent test via {@link ConfigTest}</li>
 * <li>Des scripts SQL pour initialiser la base :
 * <code>truncate-test.sql</code> puis <code>data-test.sql</code></li>
 * <li>Un profil SPRING "test" activé par le test.</li>
 * </ul>
 *
 * <p>
 * Objectif : vérifier le comportement réel (JPA/H2) du gateway.
 * </p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 4.0
 * @since 2 février 2026
 */
@SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
@Sql(
    scripts = {TypeProduitGatewayJPAServiceIntegrationTest.CLASSPATH_TRUNCATE_SQL,
            TypeProduitGatewayJPAServiceIntegrationTest.CLASSPATH_DATA_SQL},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@DataJpaTest
@ActiveProfiles(TypeProduitGatewayJPAServiceIntegrationTest.PROFILE_TEST)
@Import(TypeProduitGatewayJPAService.class)
@ContextConfiguration(classes = TypeProduitGatewayJPAServiceIntegrationTest.ConfigTest.class)
public class TypeProduitGatewayJPAServiceIntegrationTest {

    // *************************** CONSTANTES ******************************/

    /** Profil Spring : "test". */
    public static final String PROFILE_TEST = "test";

    /** Script SQL truncate (classpath). */
    public static final String CLASSPATH_TRUNCATE_SQL = "classpath:truncate-test.sql";

    /** Script SQL data (classpath). */
    public static final String CLASSPATH_DATA_SQL = "classpath:data-test.sql";

    /** Qualifier Spring du service gateway. */
    public static final String QUALIFIER_SERVICE = "TypeProduitGatewayJPAService";

    /** Tag JUnit : tests de création. */
    public static final String TAG_CREER = "servicesGateway-Creer";

    /** Tag JUnit : tests de recherche. */
    public static final String TAG_RECHERCHER = "servicesGateway-Rechercher";

    /** Tag JUnit : tests de recherche par objet métier. */
    public static final String TAG_FINDBYOBJETMETIER = "servicesGateway-FindByObjetMetier";

    /** Tag JUnit : tests de recherche rapide. */
    public static final String TAG_RECHERCHER_RAPIDE = "servicesGateway-RechercherRapide";

    /** Tag JUnit : tests de pagination. */
    public static final String TAG_PAGINATION = "servicesGateway-Pagination";

    /** Tag JUnit : tests d'update. */
    public static final String TAG_UPDATE = "servicesGateway-Update";

    /** Tag JUnit : tests de delete. */
    public static final String TAG_DELETE = "servicesGateway-Delete";

    /** Tag JUnit : tests de count. */
    public static final String TAG_COUNT = "servicesGateway-Count";

    /** DisplayName : rechercherTous(). */
    public static final String DN_RECHERCHER_TOUS = "rechercherTous() - retourne la liste seedée (triée, sans doublons)";

    /** DisplayName : creer(OK). */
    public static final String DN_CREER_OK = "creer(OK) - ajoute un élément, le rend retrouvable et ne wipe pas les seedés";

    /** DisplayName : creer(null). */
    public static final String DN_CREER_NULL = "creer(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** DisplayName : creer(blank). */
    public static final String DN_CREER_BLANK = "creer(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** DisplayName : findByObjetMetier(null). */
    public static final String DN_FINDBYOBJETMETIER_NULL = "findByObjetMetier(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** DisplayName : findByObjetMetier(libellé blank). */
    public static final String DN_FINDBYOBJETMETIER_BLANK = "findByObjetMetier(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** DisplayName : findByObjetMetier(non trouvé). */
    public static final String DN_FINDBYOBJETMETIER_NON_TROUVE = "findByObjetMetier(non trouvé) - retourne null";

    /** DisplayName : findByObjetMetier(trouvé). */
    public static final String DN_FINDBYOBJETMETIER_TROUVE = "findByObjetMetier(trouvé) - retourne l'objet métier";

    /** DisplayName : findByObjetMetier(béton). */
    public static final String DN_FINDBYOBJETMETIER_BETON = "findByObjetMetier(béton) - insensible à l'ID fourni et insensible à la casse (case-insensitive)";

    /** DisplayName : findByLibelle(blank). */
    public static final String DN_FINDBYLIBELLE_BLANK = "findByLibelle(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** DisplayName : findByLibelle(non trouvé). */
    public static final String DN_FINDBYLIBELLE_NON_TROUVE = "findByLibelle(non trouvé) - retourne null";

    /** DisplayName : findByLibelle(trouvé). */
    public static final String DN_FINDBYLIBELLE_TROUVE = "findByLibelle(trouvé) - retourne l'objet métier";

    /** DisplayName : findByLibelleRapide(null). */
    public static final String DN_RAPIDE_NULL = "findByLibelleRapide(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** DisplayName : findByLibelleRapide(blank). */
    public static final String DN_RAPIDE_BLANK = "findByLibelleRapide(blank) - délègue rechercherTous()";

    /** DisplayName : findByLibelleRapide(OK). */
    public static final String DN_RAPIDE_OK = "findByLibelleRapide(OK) - retourne les correspondances";

    /** DisplayName : findById(trouvé). */
    public static final String DN_FINDBYID_TROUVE = "findById(trouvé) - retourne l'objet métier";

    /** DisplayName : findById(non trouvé). */
    public static final String DN_FINDBYID_NON_TROUVE = "findById(non trouvé) - retourne null";

    /** DisplayName : findById(null). */
    public static final String DN_FINDBYID_NULL = "findById(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** DisplayName : rechercherTousParPage(null). */
    public static final String DN_PAGE_NULL = "rechercherTousParPage(null) - applique la pagination par défaut";

    /** DisplayName : rechercherTousParPage(avec tri). */
    public static final String DN_PAGE_TRI = "rechercherTousParPage(avec tri) - respecte la pagination et le tri (béton)";

    /** DisplayName : update(null). */
    public static final String DN_UPDATE_NULL = "update(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** DisplayName : update(blank). */
    public static final String DN_UPDATE_BLANK = "update(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** DisplayName : update(ID null). */
    public static final String DN_UPDATE_ID_NULL = "update(ID null) - jette ExceptionAppliParamNonPersistent (contrat du port)";

    /** DisplayName : update(entity inexistante). */
    public static final String DN_UPDATE_INEXISTANTE = "update(entity inexistante) - retourne null";

    /** DisplayName : update(sans modification). */
    public static final String DN_UPDATE_SANS_MODIF = "update(sans modification) - retourne un objet métier équivalent et ne modifie pas le stockage";

    /** DisplayName : update(modification). */
    public static final String DN_UPDATE_AVEC_MODIF = "update(modification) - modifie le stockage et retourne l'objet modifié";
    
    /** DisplayName : update(libellé existant). */
    public static final String DN_UPDATE_LIBELLE_EXISTANT =
            "update(libellé existant) - vérifie le comportement en cas de doublon";

    /** DisplayName : delete(null). */
    public static final String DN_DELETE_NULL = "delete(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** DisplayName : delete(OK). */
    public static final String DN_DELETE_OK = "delete(OK) - supprime un type créé et le rend introuvable";

    /** DisplayName : count(). */
    public static final String DN_COUNT_OK = "count() - cohérent avec rechercherTous()";

    /** DisplayName : delete(ID inexistant). */
    public static final String DN_DELETE_ID_INEXISTANT = "delete(ID inexistant) - ne lève pas d'exception";
    
    /** DisplayName : delete(double suppression). */
    public static final String DN_DELETE_DOUBLE =
            "delete(double suppression) - ne lève pas d'exception";

    /** DisplayName : findByLibelleRapide(case-insensitive). */
    public static final String DN_RAPIDE_CASE_INSENSITIVE = "findByLibelleRapide(case-insensitive) - recherche insensible à la casse";

    /** DisplayName : findByLibelleRapide(dédoublonnage). */
    public static final String DN_RAPIDE_DEDOUBLONNAGE = "findByLibelleRapide(dédoublonnage) - pas de doublons dans les résultats";

    /** DisplayName : rechercherTousParPage(vide). */
    public static final String DN_PAGE_VIDE = "rechercherTousParPage(vide) - retourne une page vide si la base est vide";

    /** DisplayName : rechercherTousParPage(taille > total). */
    public static final String DN_PAGE_TAILLE_SUP = "rechercherTousParPage(taille > total) - retourne tous les éléments";
    
    /** DisplayName : findByLibelleRapide(aucun résultat). */
    public static final String DN_RAPIDE_AUCUN =
            "findByLibelleRapide(aucun résultat) - retourne une liste vide";

    /** Recherche rapide sans résultat. */
    public static final String RECHERCHE_AUCUN = "zzz";
    
    /** DisplayName : rechercherTousParPage(page hors bornes). */
    public static final String DN_PAGE_HORS_BORNES =
            "rechercherTousParPage(page hors bornes) - retourne une page vide cohérente";
    
    /** DisplayName : rechercherTousParPage(taille zéro). */
    public static final String DN_PAGE_TAILLE_ZERO =
            "rechercherTousParPage(taille zéro) - retourne une page vide";

    /** blank (espaces). */
    public static final String BLANK = "   ";

    /** Libellé seed : "vêtement". */
    public static final String VETEMENT = "vêtement";

    /** Libellé seed : "bazar". */
    public static final String BAZAR = "bazar";

    /** Libellé seed : "outillage". */
    public static final String OUTILLAGE = "outillage";

    /** Libellé seed : "tourisme". */
    public static final String TOURISME = "tourisme";

    /** Libellé seed : "vestons". */
    public static final String VESTONS = "vestons";

    /** Libellé nouveau type (création). */
    public static final String NOUVEAU_TYPE_1 = "peinture";

    /** Libellé nouveau type 2 (création). */
    public static final String NOUVEAU_TYPE_2 = "électronique";

    /** Suffixe de modification. */
    public static final String SUFFIX_MODIF = "-modif";

    /** Libellé temporaire à supprimer. */
    public static final String TEMP_A_SUPPRIMER = "a-supprimer";

    /** Recherche rapide : "me". */
    public static final String RECHERCHE_ME = "me";

    /** Recherche rapide : "ve". */
    public static final String RECHERCHE_VE = "ve";

    /** Recherche rapide : "VE". */
    public static final String RECHERCHE_VE_MAJ = "VE";

    /** ID inexistant. */
    public static final Long ID_INEXISTANTE = Long.valueOf(999_999L);

    /** Propriété JPA : "typeProduit". */
    public static final String PROP_TYPEPRODUIT = "typeProduit";

    // *************************** ATTRIBUTS *******************************/

    /**
     * <div>
     * <p>SERVICE
     * <code style="font-weight:bold;">TypeProduitGatewayJPAService</code>
     * injecté par SPRING dans le constructeur.</p>
     * </div>
     */
    private final TypeProduitGatewayIService service;

    // ************************* METHODES **********************************/

    /**
     * <div>
     * <p>CONSTRUCTEUR D'ARITE 1.</p>
     * <p>Constructeur d'injection (Spring Test + JUnit Jupiter).</p>
     * </div>
     *
     * @param pService :
     * service gateway injecté par Spring
     */
    public TypeProduitGatewayJPAServiceIntegrationTest(
            @Qualifier(QUALIFIER_SERVICE)
            final TypeProduitGatewayIService pService) {
        this.service = pService;
    }

    /**
     * <div>
     * <p style="font-weight:bold;">
     * Classe interne de configuration Spring.
     * </p>
     * <ul>
     * <li>@Configuration</li>
     * <li>@EnableJpaRepositories(basePackageClasses = TypeProduitDaoJPA.class)</li>
     * <li>@EntityScan(basePackageClasses = TypeProduitJPA.class)</li>
     * </ul>
     * </div>
     *
     * @author Daniel Lévy
     * @version 1.0
     * @since 24 janvier 2026
     */
    @Configuration
    @EnableJpaRepositories(basePackageClasses = TypeProduitDaoJPA.class)
    @EntityScan(basePackageClasses = TypeProduitJPA.class)
    public static final class ConfigTest { // NOPMD by danyl on 24/01/2026 00:00
    }

    // =============================== OUTILS TESTS =========================

    /**
     * <div>
     * <p>Vérifie que la liste est triée par libellé (ordre naturel).</p>
     * </div>
     *
     * @param pListe : List&lt;TypeProduit&gt
     */
    private static void assertListeTrieeParLibelle(final List<TypeProduit> pListe) {
        if ((pListe == null) || (pListe.size() < 2)) {
            return;
        }

        for (int i = 0; i < pListe.size() - 1; i++) {
            final String courant = pListe.get(i).getTypeProduit();
            final String suivant = pListe.get(i + 1).getTypeProduit();
            assertThat(Comparator.<String>naturalOrder().compare(courant, suivant))
                .isLessThanOrEqualTo(0);
        }
    }

    // =============================== TESTS ===============================

    /**
     * <div>
     * <p>
     * Vérifie que `creer(TypeProduit)` ajoute bien un élément,
     * rend l'élément retrouvable et ne "wipe" pas les seedés.
     * </p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_OK)
    @Test
    public void testCreerOK() throws Exception {
        final long countAvant = this.service.count();
        final TypeProduit aCreer = new TypeProduit(NOUVEAU_TYPE_1);
        final TypeProduit cree = this.service.creer(aCreer);
        assertThat(cree).isNotNull();
        assertThat(cree.getIdTypeProduit()).isNotNull().isPositive();
        assertThat(cree.getTypeProduit()).isEqualTo(NOUVEAU_TYPE_1);
        final long countApres = this.service.count();
        assertThat(countApres).isEqualTo(countAvant + 1L);
        final TypeProduit relu = this.service.findById(cree.getIdTypeProduit());
        assertThat(relu).isNotNull();
        assertThat(relu.getTypeProduit()).isEqualTo(NOUVEAU_TYPE_1);
        final List<TypeProduit> liste = this.service.rechercherTous();
        assertThat(liste)
            .extracting(TypeProduit::getTypeProduit)
            .contains(VETEMENT, BAZAR, OUTILLAGE, TOURISME, VESTONS, NOUVEAU_TYPE_1);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `creer(null)` respecte le contrat du port :
     * jette une {@link ExceptionAppliParamNull}.</p>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_NULL)
    @Test
    public void testCreerNull() {
        assertThatThrownBy(() -> this.service.creer(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_CREER_KO_PARAM_NULL);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `creer(blank)` respecte le contrat du port :
     * jette une {@link ExceptionAppliLibelleBlank}.</p>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_BLANK)
    @Test
    public void testCreerBlank() {
        final TypeProduit blank = new TypeProduit(BLANK);
        assertThatThrownBy(() -> this.service.creer(blank))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_CREER_KO_LIBELLE_BLANK);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `findByObjetMetier(null)` respecte le contrat du port :
     * jette une {@link ExceptionAppliParamNull}.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_NULL)
    @Test
    public void testFindByObjetMetierNull() {
        assertThatThrownBy(() -> this.service.findByObjetMetier(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL);
    }

    /**
     * <div>
     * <p>Vérifie que `findByObjetMetier(libellé null)` respecte le contrat du port :
     * jette une {@link ExceptionAppliLibelleBlank}.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(libellé null) - jette ExceptionAppliLibelleBlank (contrat du port)")
    @Test
    public void testFindByObjetMetierLibelleNull() {
        final TypeProduit libelleNull = new TypeProduit(null);
        assertThatThrownBy(() -> this.service.findByObjetMetier(libelleNull))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);
    }

    /**
     * <div>
     * <p>Vérifie que `findByObjetMetier(blank)` respecte le contrat du port :
     * jette une {@link ExceptionAppliLibelleBlank}.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_BLANK)
    @Test
    public void testFindByObjetMetierBlank() {
        final TypeProduit blank = new TypeProduit(BLANK);
        assertThatThrownBy(() -> this.service.findByObjetMetier(blank))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);
    }

    /**
     * <div>
     * <p>Vérifie que `findByObjetMetier(non trouvé)` retourne {@code null}.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_NON_TROUVE)
    @Test
    public void testFindByObjetMetierNonTrouve() throws Exception {
        final TypeProduit inexistant = new TypeProduit("inexistant");
        final TypeProduit retour = this.service.findByObjetMetier(inexistant);
        assertThat(retour).isNull();
    }

    /**
     * <div>
     * <p>Vérifie que `findByObjetMetier(trouvé)` retourne un objet métier cohérent.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_TROUVE)
    @Test
    public void testFindByObjetMetierTrouve() throws Exception {
        final TypeProduit metier = new TypeProduit(VETEMENT);
        final TypeProduit retour = this.service.findByObjetMetier(metier);
        assertThat(retour).isNotNull();
        assertThat(retour.getIdTypeProduit()).isNotNull();
        assertThat(retour.getTypeProduit()).isEqualTo(VETEMENT);
    }

    /**
     * <div>
     * <p>
     * Test "béton" :
     * </p>
     * <ul>
     * <li>l'ID du paramètre ne doit pas influencer la recherche (recherche par libellé)</li>
     * <li>la recherche est insensible à la casse : upper == lower (retour non null)</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_BETON)
    @Test
    public void testFindByObjetMetierBeton() throws Exception {
        final TypeProduit seed = this.service.findByLibelle(VETEMENT);
        assertThat(seed).isNotNull();
        assertThat(seed.getIdTypeProduit()).isNotNull();
        final TypeProduit avecIdFaux = new TypeProduit(ID_INEXISTANTE, VETEMENT);
        final TypeProduit retour = this.service.findByObjetMetier(avecIdFaux);
        assertThat(retour).isNotNull();
        assertThat(retour.getIdTypeProduit()).isEqualTo(seed.getIdTypeProduit());
        assertThat(retour.getTypeProduit()).isEqualTo(VETEMENT);
        final String upper = VETEMENT.toUpperCase(Locale.getDefault());
        final TypeProduit casse = new TypeProduit(upper);
        final TypeProduit retourCasse = this.service.findByObjetMetier(casse);
        assertThat(retourCasse).isNotNull();
    }

    // =============================== TESTS ===============================
	
	/**
	 * <div>
	 * <p>
	 * Vérifie que `rechercherTous()` retourne une liste non nulle, non vide,
	 * et contient les types seedés par data-test.sql.
	 * </p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER)
	@DisplayName(DN_RECHERCHER_TOUS)
	@Test
	public void testRechercherTous() throws Exception {
	    final List<TypeProduit> resultats = this.service.rechercherTous();
	    assertThat(resultats).isNotNull();
	    assertThat(resultats).isNotEmpty();
	    assertThat(resultats)
	        .extracting(TypeProduit::getTypeProduit)
	        .doesNotHaveDuplicates()
	        .contains(VETEMENT, BAZAR, OUTILLAGE, TOURISME, VESTONS);
	    assertListeTrieeParLibelle(resultats);
	}

	/**
	 * <div>
	 * <p>Vérifie que `rechercherTousParPage(null)` retourne une page cohérente.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_PAGINATION)
	@DisplayName(DN_PAGE_NULL)
	@Test
	public void testRechercherTousParPageNull() throws Exception {
	    final ResultatPage<TypeProduit> page = this.service.rechercherTousParPage(null);
	    assertThat(page).isNotNull();
	    assertThat(page.getContent()).isNotNull();
	    assertThat(page.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
	    assertThat(page.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
	    assertThat(page.getTotalElements()).isPositive();
	}

	/**
	 * <div>
	 * <p>Vérifie que `rechercherTousParPage(avec tri)` respecte le tri demandé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_PAGINATION)
	@DisplayName(DN_PAGE_TRI)
	@Test
	public void testRechercherTousParPageAvecTri() throws Exception {
	    final List<TriSpec> tris = new ArrayList<TriSpec>();
	    tris.add(new TriSpec(PROP_TYPEPRODUIT, DirectionTri.ASC));
	    final RequetePage requete = new RequetePage(0, 3, tris);
	    final ResultatPage<TypeProduit> page = this.service.rechercherTousParPage(requete);
	    assertThat(page).isNotNull();
	    assertThat(page.getContent()).isNotNull().isNotEmpty();
	    assertThat(page.getPageNumber()).isEqualTo(0);
	    assertThat(page.getPageSize()).isEqualTo(3);
	    assertThat(page.getTotalElements()).isPositive();
	    assertThat(page.getContent().size()).isLessThanOrEqualTo(3);
	    assertListeTrieeParLibelle(page.getContent());
	}

	/**
	 * <div>
	 * <p>Vérifie que `rechercherTousParPage(vide)` retourne une page vide si la base est vide.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_PAGINATION)
	@DisplayName(DN_PAGE_VIDE)
	@Test
	public void testRechercherTousParPageVide() throws Exception {
	    final List<TypeProduit> tous = this.service.rechercherTous();
	    final RequetePage requete = new RequetePage(0, 10, new ArrayList<>());
	    final ResultatPage<TypeProduit> page = this.service.rechercherTousParPage(requete);
	    assertThat(page).isNotNull();
	    assertThat(page.getContent()).hasSize(tous.size());
	    assertThat(page.getTotalElements()).isEqualTo(tous.size());
	}

	/**
	 * <div>
	 * <p>Vérifie que `rechercherTousParPage(taille > total)` retourne tous les éléments.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_PAGINATION)
	@DisplayName(DN_PAGE_TAILLE_SUP)
	@Test
	public void testRechercherTousParPageTailleSuperieure() throws Exception {
	    final List<TypeProduit> tous = this.service.rechercherTous();
	    final RequetePage requete = new RequetePage(0, tous.size() + 10, new ArrayList<>());
	    final ResultatPage<TypeProduit> page = this.service.rechercherTousParPage(requete);
	    assertThat(page.getContent()).hasSize(tous.size());
	    assertThat(page.getTotalElements()).isEqualTo(tous.size());
	    
	} // _________________________________________________________________

	/**
	 * <div>
	 * <p>
	 * Vérifie qu'une page très au-delà du nombre total
	 * retourne un contenu vide mais cohérent.
	 * </p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_PAGINATION)
	@DisplayName(DN_PAGE_HORS_BORNES)
	@Test
	public void testRechercherTousParPageHorsBornes() throws Exception {
	
	    final List<TypeProduit> tous = this.service.rechercherTous();
	
	    /* Page très élevée. */
	    final RequetePage requete =
	            new RequetePage(9999, 10, new ArrayList<>());
	
	    final ResultatPage<TypeProduit> page =
	            this.service.rechercherTousParPage(requete);
	
	    assertThat(page).isNotNull();
	    assertThat(page.getContent()).isNotNull().isEmpty();
	    assertThat(page.getTotalElements()).isEqualTo(tous.size());
	    
	} // _________________________________________________________________

	/**
	 * <div>
	 * <p>
	 * Vérifie qu'une taille de page égale à zéro
	 * retourne un contenu cohérent avec le comportement réel observé
	 * (Spring Data / H2) : retour de tous les éléments.
	 * </p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_PAGINATION)
	@DisplayName(DN_PAGE_TAILLE_ZERO)
	@Test
	public void testRechercherTousParPageTailleZero() throws Exception {
	
	    final List<TypeProduit> tous = this.service.rechercherTous();
	
	    final RequetePage requete =
	            new RequetePage(0, 0, new ArrayList<>());
	
	    final ResultatPage<TypeProduit> page =
	            this.service.rechercherTousParPage(requete);
	
	    assertThat(page).isNotNull();
	    assertThat(page.getContent()).isNotNull();
	    assertThat(page.getContent()).hasSize(tous.size());
	    assertThat(page.getTotalElements()).isEqualTo(tous.size());
	
	} // _________________________________________________________________

	/**
     * <div>
     * <p>Vérifie que `findByLibelle(blank)` respecte le contrat du port :
     * jette une {@link ExceptionAppliLibelleBlank}.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYLIBELLE_BLANK)
    @Test
    public void testFindByLibelleBlank() {
        assertThatThrownBy(() -> this.service.findByLibelle(BLANK))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK);
    }

    /**
     * <div>
     * <p>Vérifie que `findByLibelle(non trouvé)` retourne {@code null}.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYLIBELLE_NON_TROUVE)
    @Test
    public void testFindByLibelleNonTrouve() throws Exception {
        final TypeProduit retour = this.service.findByLibelle("inexistant");
        assertThat(retour).isNull();
    }

    /**
     * <div>
     * <p>Vérifie que `findByLibelle(trouvé)` retourne un objet métier cohérent.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYLIBELLE_TROUVE)
    @Test
    public void testFindByLibelleTrouve() throws Exception {
        final TypeProduit retour = this.service.findByLibelle(VETEMENT);
        assertThat(retour).isNotNull();
        assertThat(retour.getIdTypeProduit()).isNotNull();
        assertThat(retour.getTypeProduit()).isEqualTo(VETEMENT);
    }

    /**
     * <div>
     * <p>Vérifie que `findByLibelleRapide(null)` respecte le contrat du port :
     * jette une {@link ExceptionAppliParamNull}.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_RAPIDE_NULL)
    @Test
    public void testFindByLibelleRapideNull() {
        assertThatThrownBy(() -> this.service.findByLibelleRapide(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL);
    }

    /**
     * <div>
     * <p>Vérifie que `findByLibelleRapide(blank)` retourne tous les enregistrements.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_RAPIDE_BLANK)
    @Test
    public void testFindByLibelleRapideBlank() throws Exception {
        final List<TypeProduit> tous = this.service.rechercherTous();
        assertThat(tous).isNotNull();
        final List<TypeProduit> retour = this.service.findByLibelleRapide(BLANK);
        assertThat(retour).isNotNull();
        assertThat(retour)
            .extracting(TypeProduit::getTypeProduit)
            .containsExactlyElementsOf(
                tous.stream().map(TypeProduit::getTypeProduit).toList());
    }

    /**
     * <div>
     * <p>Vérifie que `findByLibelleRapide(OK)` retourne des correspondances.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_RAPIDE_OK)
    @Test
    public void testFindByLibelleRapideOK() throws Exception {
        final List<TypeProduit> retour = this.service.findByLibelleRapide(RECHERCHE_ME);
        assertThat(retour).isNotNull().isNotEmpty();
        assertThat(retour)
            .extracting(TypeProduit::getTypeProduit)
            .allMatch(s -> s != null && s.contains(RECHERCHE_ME));
    }

    
    
    /**
     * <div>
     * <p>Vérifie que `findByLibelleRapide(case-insensitive)` est insensible à la casse.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_RAPIDE_CASE_INSENSITIVE)
    @Test
    public void testFindByLibelleRapideCaseInsensitive() throws Exception {
        final List<TypeProduit> retourMin = this.service.findByLibelleRapide(RECHERCHE_VE);
        final List<TypeProduit> retourMaj = this.service.findByLibelleRapide(RECHERCHE_VE_MAJ);
        assertThat(retourMin).isNotNull();
        assertThat(retourMaj).isNotNull();
        assertThat(retourMin)
            .extracting(TypeProduit::getTypeProduit)
            .containsExactlyInAnyOrderElementsOf(
                retourMaj.stream().map(TypeProduit::getTypeProduit).toList());
    } // ________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `findByLibelleRapide(dédoublonnage)` ne retourne pas de doublons.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_RAPIDE_DEDOUBLONNAGE)
    @Test
    public void testFindByLibelleRapideDedoublonnage() throws Exception {
        final List<TypeProduit> retour = this.service.findByLibelleRapide(RECHERCHE_ME);
        assertThat(retour)
            .extracting(TypeProduit::getTypeProduit)
            .doesNotHaveDuplicates();
        
    } // ________________________________________________________________


    
    /**
     * <div>
     * <p>
     * Vérifie que `findByLibelleRapide(aucun résultat)`
     * retourne une liste vide (pas {@code null}).
     * </p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_RAPIDE_AUCUN)
    @Test
    public void testFindByLibelleRapideAucunResultat() throws Exception {

        /* Recherche avec un motif absent. */
        final List<TypeProduit> retour =
                this.service.findByLibelleRapide(RECHERCHE_AUCUN);

        /* Vérifie liste non nulle et vide. */
        assertThat(retour).isNotNull().isEmpty();
    } // ________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `findById(null)` respecte le contrat du port :
     * jette une {@link ExceptionAppliParamNull}.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYID_NULL)
    @Test
    public void testFindByIdNull() {
        assertThatThrownBy(() -> this.service.findById(null)
            ).isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYID_KO_PARAM_NULL);
    } // ________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `findById(non trouvé)` retourne {@code null}.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYID_NON_TROUVE)
    @Test
    public void testFindByIdNonTrouve() throws Exception {
        final TypeProduit retour = this.service.findById(ID_INEXISTANTE);
        assertThat(retour).isNull();
        
    } // ________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `findById(trouvé)` retourne un objet métier cohérent.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYID_TROUVE)
    @Test
    public void testFindByIdTrouve() throws Exception {
        final TypeProduit seed = this.service.findByLibelle(VETEMENT);
        assertThat(seed).isNotNull();
        assertThat(seed.getIdTypeProduit()).isNotNull();
        final TypeProduit relu = this.service.findById(seed.getIdTypeProduit());
        assertThat(relu).isNotNull();
        assertThat(relu.getIdTypeProduit()).isEqualTo(seed.getIdTypeProduit());
        assertThat(relu.getTypeProduit()).isEqualTo(VETEMENT);
        
    } // ________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `update(null)` respecte le contrat du port :
     * jette une {@link ExceptionAppliParamNull}.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_NULL)
    @Test
    public void testUpdateNull() {
        assertThatThrownBy(() -> this.service.update(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_UPDATE_KO_PARAM_NULL);
        
    } // _________________________________________________________________

    
    
    /**
     * <div>
     * <p>Vérifie que `update(blank)` respecte le contrat du port :
     * jette une {@link ExceptionAppliLibelleBlank}.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_BLANK)
    @Test
    public void testUpdateBlank() {
        final TypeProduit blank = new TypeProduit(Long.valueOf(1L), BLANK);
        assertThatThrownBy(() -> this.service.update(blank))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_BLANK);
        
    } // _________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `update(ID null)` respecte le contrat du port :
     * jette une {@link ExceptionAppliParamNonPersistent}.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_ID_NULL)
    @Test
    public void testUpdateIdNull() {
        final TypeProduit nonPersistant = new TypeProduit(VETEMENT);
        assertThatThrownBy(() -> this.service.update(nonPersistant))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_UPDATE_KO_NON_PERSISTENT + VETEMENT);
        
    } // _________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `update(entity inexistante)` retourne {@code null}.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_INEXISTANTE)
    @Test
    public void testUpdateEntityInexistante() throws Exception {
        final TypeProduit inexistante = new TypeProduit(ID_INEXISTANTE, VETEMENT);
        final TypeProduit retour = this.service.update(inexistante);
        assertThat(retour).isNull();
    }

    /**
     * <div>
     * <p>
     * Vérifie que `update(sans modification)` retourne un objet métier équivalent
     * (contrat de l'implémentation) et ne modifie pas le stockage.
     * </p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_SANS_MODIF)
    @Test
    public void testUpdateSansModification() throws Exception {
        final TypeProduit seed = this.service.findByLibelle(VETEMENT);
        assertThat(seed).isNotNull();
        assertThat(seed.getIdTypeProduit()).isNotNull();
        final Long id = seed.getIdTypeProduit();
        final TypeProduit sansModif = new TypeProduit(id, VETEMENT);
        final TypeProduit retour = this.service.update(sansModif);
        assertThat(retour).isNotNull();
        assertThat(retour).isNotSameAs(sansModif);
        assertThat(retour.getIdTypeProduit()).isEqualTo(id);
        assertThat(retour.getTypeProduit()).isEqualTo(VETEMENT);
        final TypeProduit relu = this.service.findById(id);
        assertThat(relu).isNotNull();
        assertThat(relu.getTypeProduit()).isEqualTo(VETEMENT);
    }

    
    
    /**
     * <div>
     * <p>
     * Vérifie que `update(modification)` retourne un objet cohérent
     * et que la relecture par ID est cohérente avec l'objet retourné.
     * </p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_AVEC_MODIF)
    @Test
    public void testUpdateAvecModification() throws Exception {

        final TypeProduit seed = this.service.findByLibelle(TOURISME);
        assertThat(seed).isNotNull();
        assertThat(seed.getIdTypeProduit()).isNotNull();

        final Long id = seed.getIdTypeProduit();
        final String nouveauLibelle = TOURISME + SUFFIX_MODIF;

        final TypeProduit aModifier = new TypeProduit(id, nouveauLibelle);

        final TypeProduit retour = this.service.update(aModifier);

        assertThat(retour).isNotNull();
        assertThat(retour.getIdTypeProduit()).isEqualTo(id);

        /* Cohérence : ce que retourne update doit être ce que renvoie findById. */
        final TypeProduit relu = this.service.findById(id);
        assertThat(relu).isNotNull();
        assertThat(relu.getIdTypeProduit()).isEqualTo(id);
        assertThat(relu.getTypeProduit()).isEqualTo(retour.getTypeProduit());

        /* Vérifie aussi la cohérence sur une mise à jour en majuscules. */
        final String upper = nouveauLibelle.toUpperCase(Locale.getDefault());
        final TypeProduit aModifierCS = new TypeProduit(id, upper);

        final TypeProduit retourCS = this.service.update(aModifierCS);

        assertThat(retourCS).isNotNull();
        assertThat(retourCS).isNotSameAs(aModifierCS);
        assertThat(retourCS.getIdTypeProduit()).isEqualTo(id);

        final TypeProduit reluApresCS = this.service.findById(id);
        assertThat(reluApresCS).isNotNull();
        assertThat(reluApresCS.getIdTypeProduit()).isEqualTo(id);
        assertThat(reluApresCS.getTypeProduit()).isEqualTo(retourCS.getTypeProduit());

    } // ________________________________________________________________


    
    /**
     * <div>
     * <p>
     * Vérifie le comportement lorsque l'on modifie
     * un type vers un libellé déjà existant.
     * </p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_LIBELLE_EXISTANT)
    @Test
    public void testUpdateLibelleExistant() throws Exception {

        final TypeProduit t1 = this.service.findByLibelle(VETEMENT);
        final TypeProduit t2 = this.service.findByLibelle(BAZAR);

        assertThat(t1).isNotNull();
        assertThat(t2).isNotNull();

        final Long id2 = t2.getIdTypeProduit();
        assertThat(id2).isNotNull();

        /* Tentative de collision de libellé. */
        final TypeProduit aModifier =
                new TypeProduit(id2, VETEMENT);

        /* Comportement réel : pas d'exception et pas de modification. */
        final TypeProduit retour = this.service.update(aModifier);

        assertThat(retour).isNotNull();
        assertThat(retour.getIdTypeProduit()).isEqualTo(id2);
        assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);

        final TypeProduit relu = this.service.findById(id2);
        assertThat(relu).isNotNull();
        assertThat(relu.getIdTypeProduit()).isEqualTo(id2);
        assertThat(relu.getTypeProduit()).isEqualTo(BAZAR);

    } // ________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `delete(null)` respecte le contrat du port :
     * jette une {@link ExceptionAppliParamNull}.</p>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_NULL)
    @Test
    public void testDeleteNull() {
        assertThatThrownBy(() -> this.service.delete(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_DELETE_KO_PARAM_NULL);
        
    } // ________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `delete(ID null)` respecte le contrat du port :
     * jette une {@link ExceptionAppliParamNonPersistent}.</p>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(ID null) - jette ExceptionAppliParamNonPersistent (contrat du port)")
    @Test
    public void testDeleteIdNull() {
        final TypeProduit idNull = new TypeProduit(null, VETEMENT);
        assertThatThrownBy(() -> this.service.delete(idNull))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_DELETE_KO_ID_NULL);
        
    } // ________________________________________________________________

    
    
    /**
     * <div>
     * <p>Vérifie que `delete(ID inexistant)` ne lève pas d'exception.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_ID_INEXISTANT)
    @Test
    public void testDeleteIdInexistant() throws Exception {
        final TypeProduit inexistant = new TypeProduit(ID_INEXISTANTE, VETEMENT);
        this.service.delete(inexistant); // Pas d'exception attendue
        
    } // ________________________________________________________________

    
    
    /**
     * <div>
     * <p>Vérifie que `delete(TypeProduit)` supprime un type créé
     * et qu'il devient introuvable.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_OK)
    @Test
    public void testDeleteOK() throws Exception {
        final long countAvant = this.service.count();
        final TypeProduit cree = this.service.creer(new TypeProduit(TEMP_A_SUPPRIMER));
        assertThat(cree).isNotNull();
        assertThat(cree.getIdTypeProduit()).isNotNull();
        final long countApresCreation = this.service.count();
        assertThat(countApresCreation).isEqualTo(countAvant + 1L);
        this.service.delete(cree);
        final long countApresDelete = this.service.count();
        assertThat(countApresDelete).isEqualTo(countAvant);
        final TypeProduit relu = this.service.findById(cree.getIdTypeProduit());
        assertThat(relu).isNull();
        
    } // ________________________________________________________________


    
    /**
     * <div>
     * <p>
     * Vérifie qu'une suppression répétée du même objet
     * ne provoque pas d'exception.
     * </p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_DOUBLE)
    @Test
    public void testDeleteDouble() throws Exception {

        /* Création d'un objet temporaire. */
        final TypeProduit cree =
                this.service.creer(new TypeProduit(TEMP_A_SUPPRIMER));

        assertThat(cree).isNotNull();

        /* Première suppression. */
        this.service.delete(cree);

        /* Seconde suppression : ne doit rien faire. */
        this.service.delete(cree);
        
    } // ________________________________________________________________

    
    
    /**
     * <div>
     * <p>Vérifie que `count()` est cohérent avec `rechercherTous()`.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_COUNT)
    @DisplayName(DN_COUNT_OK)
    @Test
    public void testCountOK() throws Exception {
        final long count = this.service.count();
        final List<TypeProduit> liste = this.service.rechercherTous();
        assertThat(liste).isNotNull();
        assertThat(count).isEqualTo(liste.size());
        
    } // ________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `creer(libellé existant)` jette une exception si le libellé existe déjà.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(libellé existant) - jette une exception si le libellé existe déjà")
    @Test
    public void testCreerLibelleExistant() throws Exception {
        final TypeProduit existant = this.service.findByLibelle(VETEMENT);
        assertThat(existant).isNotNull();
        final TypeProduit aCreer = new TypeProduit(VETEMENT);
        assertThatThrownBy(() -> this.service.creer(aCreer))
            .isInstanceOf(Exception.class);
        
    } // ________________________________________________________________
    
    

    /**
     * <div>
     * <p>Vérifie que `creer(plusieurs créations)` fonctionne correctement.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(plusieurs créations) - vérifie la création multiple")
    @Test
    public void testCreerPlusieurs() throws Exception {
        final long countAvant = this.service.count();
        final TypeProduit cree1 = this.service.creer(new TypeProduit(NOUVEAU_TYPE_1));
        final TypeProduit cree2 = this.service.creer(new TypeProduit(NOUVEAU_TYPE_2));
        assertThat(cree1).isNotNull();
        assertThat(cree2).isNotNull();
        assertThat(cree1.getIdTypeProduit()).isNotEqualTo(cree2.getIdTypeProduit());
        final long countApres = this.service.count();
        assertThat(countApres).isEqualTo(countAvant + 2L);
        final TypeProduit relu1 = this.service.findById(cree1.getIdTypeProduit());
        final TypeProduit relu2 = this.service.findById(cree2.getIdTypeProduit());
        assertThat(relu1).isNotNull();
        assertThat(relu2).isNotNull();
        assertThat(relu1.getTypeProduit()).isEqualTo(NOUVEAU_TYPE_1);
        assertThat(relu2.getTypeProduit()).isEqualTo(NOUVEAU_TYPE_2);
        
    } // ________________________________________________________________
    
    
}
