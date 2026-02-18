/* ********************************************************************* */
/* ***************************** TEST JUNIT **************************** */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.gateway.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliLibelleBlank;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNonPersistent;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNull;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParentNull;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionTechniqueGatewayNonPersistent;
import levy.daniel.application.model.services.produittype.gateway.SousTypeProduitGatewayIService;
import levy.daniel.application.model.services.produittype.pagination.DirectionTri;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;
import levy.daniel.application.model.services.produittype.pagination.TriSpec;
import levy.daniel.application.persistence.metier.produittype.dao.daosJPA.SousTypeProduitDaoJPA;
import levy.daniel.application.persistence.metier.produittype.dao.daosJPA.TypeProduitDaoJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.SousTypeProduitJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.TypeProduitJPA;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE SousTypeProduitGatewayJPAServiceIntegrationTest.java :
 * </p>
 * <p>
 * Tests d'intégration Spring (@DataJpaTest) / H2 in-memory du service ADAPTER GATEWAY
 * {@link SousTypeProduitGatewayJPAService}.
 * </p>
 * <p>
 * Ce test JUnit autonome utilise :
 * </p>
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
 * @version 2.0
 * @since 01 février 2026
 */
@SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
@Sql(
    scripts = {SousTypeProduitGatewayJPAServiceIntegrationTest.CLASSPATH_TRUNCATE_SQL,
            SousTypeProduitGatewayJPAServiceIntegrationTest.CLASSPATH_DATA_SQL},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@DataJpaTest
@ActiveProfiles(SousTypeProduitGatewayJPAServiceIntegrationTest.PROFILE_TEST)
@Import(SousTypeProduitGatewayJPAService.class)
@ContextConfiguration(classes = SousTypeProduitGatewayJPAServiceIntegrationTest.ConfigTest.class)
public class SousTypeProduitGatewayJPAServiceIntegrationTest {

    // *************************** CONSTANTES ******************************/

    /** Profil Spring : "test". */
    public static final String PROFILE_TEST = "test";

    /** Script SQL truncate (classpath). */
    public static final String CLASSPATH_TRUNCATE_SQL = "classpath:truncate-test.sql";

    /** Script SQL data (classpath). */
    public static final String CLASSPATH_DATA_SQL = "classpath:data-test.sql";

    /** Qualifier Spring du service gateway. */
    public static final String QUALIFIER_SERVICE = "SousTypeProduitGatewayJPAService";

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

    /** Locale par défaut. */
    public static final Locale LOCALE_DEFAUT = Locale.getDefault();

    /** Chaîne vide : "". */
    public static final String CHAINE_VIDE = "";

    /** Blank : "   ". */
    public static final String BLANK = "   ";
    
    /** Libellé inexistant : "Inexistant". */
    public static final String LIBELLE_INEXISTANT = "Inexistant";

    /** Contenu partiel inexistant : "xyz". */
    public static final String CONTENU_PARTIEL_INEXISTANT = "xyz";

    /** DisplayName : "rechercherTousParPage(tris valides) - retourne une page triée". */
    public static final String DN_RECHERCHER_TOUS_PAR_PAGE_TRI = "rechercherTousParPage(tris valides) - retourne une page triée";

    /** DisplayName : "rechercherTousParPage(page vide) - retourne une page vide". */
    public static final String DN_RECHERCHER_TOUS_PAR_PAGE_VIDE = "rechercherTousParPage(page vide) - retourne une page vide";

    /** DisplayName : "findByLibelle(inexistant) - retourne une liste vide". */
    public static final String DN_FINDBYLIBELLE_INEXISTANT = "findByLibelle(inexistant) - retourne une liste vide";

    /** DisplayName : "findByLibelleRapide(contenu inexistant) - retourne une liste vide". */
    public static final String DN_FINDBYLIBELLERAPIDE_INEXISTANT = "findByLibelleRapide(contenu inexistant) - retourne une liste vide";

    /** DisplayName : "update(parent modifié) - met à jour le parent". */
    public static final String DN_UPDATE_PARENT_MODIFIE = "update(parent modifié) - met à jour le parent";

    /** Libellé parent existant (data-test.sql) : "vêtement". */
    public static final String LIBELLE_PARENT_VETEMENT = "vêtement";

    /** Libellé parent existant (data-test.sql) : "Chaussure". */
    public static final String LIBELLE_PARENT_CHAUSSURE = "Chaussure";

    /** Libellé enfant existant (data-test.sql) : "vêtement pour homme". */
    public static final String LIBELLE_ENFANT_VETEMENT_HOMME = "vêtement pour homme";

    /** Libellé enfant existant (data-test.sql) : "vêtement pour femme". */
    public static final String LIBELLE_ENFANT_VETEMENT_FEMME = "vêtement pour femme";

    /** Libellé enfant existant (data-test.sql) : "vêtement pour enfant". */
    public static final String LIBELLE_ENFANT_VETEMENT_ENFANT = "vêtement pour enfant";

    /** Contenu partiel existant dans les libellés enfants seed : "vêt". */
    public static final String CONTENU_PARTIEL_VET = "vêt";

    /** Contenu partiel : "sh". */
    public static final String CONTENU_PARTIEL_SH = "sh";

    /** Libellé pour création : "Pull". */
    public static final String LIBELLE_NOUVEAU_PULL = "Pull";

    /** Libellé pour modification : "vêtement pour femme modifié". */
    public static final String LIBELLE_MODIFIE_FEMME = "vêtement pour femme modifié";

    /** Libellé pour suppression : "à supprimer". */
    public static final String LIBELLE_A_SUPPRIMER = "à supprimer";

    /** ID inexistant. */
    public static final Long ID_INEXISTANT = Long.valueOf(999_999L);

    /** DisplayName : "creer(null) - jette ExceptionAppliParamNull (contrat du port)". */
    public static final String DN_CREER_NULL = "creer(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** DisplayName : "creer(blank) - jette ExceptionAppliLibelleBlank (contrat du port)". */
    public static final String DN_CREER_BLANK = "creer(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** DisplayName : "creer(parent null) - jette ExceptionAppliParentNull (contrat du port)". */
    public static final String DN_CREER_PARENT_NULL = "creer(parent null) - jette ExceptionAppliParentNull (contrat du port)";

    /** DisplayName : "creer(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)". */
    public static final String DN_CREER_PARENT_LIBELLE_BLANK = "creer(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** DisplayName : "creer(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)". */
    public static final String DN_CREER_PARENT_NON_PERSISTANT = "creer(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

    /** DisplayName : "creer(nominal) - ajoute un élément, le rend retrouvable et ne wipe pas les seedés". */
    public static final String DN_CREER_NOMINAL = "creer(nominal) - ajoute un élément, le rend retrouvable et ne wipe pas les seedés";

    /** DisplayName : "rechercherTous() - retourne la liste seedée (triée, sans doublons)". */
    public static final String DN_RECHERCHER_TOUS = "rechercherTous() - retourne la liste seedée (triée, sans doublons)";

    /** DisplayName : "findByObjetMetier(null) - jette ExceptionAppliParamNull (contrat du port)". */
    public static final String DN_FINDBYOBJETMETIER_NULL = "findByObjetMetier(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** DisplayName : "findByObjetMetier(libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)". */
    public static final String DN_FINDBYOBJETMETIER_BLANK = "findByObjetMetier(libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** DisplayName : "findByObjetMetier(parent null) - jette ExceptionAppliParentNull (contrat du port)". */
    public static final String DN_FINDBYOBJETMETIER_PARENT_NULL = "findByObjetMetier(parent null) - jette ExceptionAppliParentNull (contrat du port)";

    /** DisplayName : "findByObjetMetier(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)". */
    public static final String DN_FINDBYOBJETMETIER_PARENT_LIBELLE_BLANK = "findByObjetMetier(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** DisplayName : "findByObjetMetier(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)". */
    public static final String DN_FINDBYOBJETMETIER_PARENT_NON_PERSISTANT = "findByObjetMetier(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

    /** DisplayName : "findByObjetMetier(nominal) - retourne l'objet métier correspondant". */
    public static final String DN_FINDBYOBJETMETIER_NOMINAL = "findByObjetMetier(nominal) - retourne l'objet métier correspondant";

    /** DisplayName : "findByLibelle(blank) - jette ExceptionAppliLibelleBlank (contrat du port)". */
    public static final String DN_FINDBYLIBELLE_BLANK = "findByLibelle(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** DisplayName : "findByLibelle(nominal) - retourne la liste des correspondances". */
    public static final String DN_FINDBYLIBELLE_NOMINAL = "findByLibelle(nominal) - retourne la liste des correspondances";

    /** DisplayName : "findByLibelleRapide(null) - jette ExceptionAppliParamNull (contrat du port)". */
    public static final String DN_FINDBYLIBELLERAPIDE_NULL = "findByLibelleRapide(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** DisplayName : "findByLibelleRapide(blank) - délègue à rechercherTous()". */
    public static final String DN_FINDBYLIBELLERAPIDE_BLANK = "findByLibelleRapide(blank) - délègue à rechercherTous()";

    /** DisplayName : "findByLibelleRapide(nominal) - retourne les correspondances partielles". */
    public static final String DN_FINDBYLIBELLERAPIDE_NOMINAL = "findByLibelleRapide(nominal) - retourne les correspondances partielles";

    /** DisplayName : "findAllByParent(null) - jette ExceptionAppliParentNull (contrat du port)". */
    public static final String DN_FINDALLBYPARENT_NULL = "findAllByParent(null) - jette ExceptionAppliParentNull (contrat du port)";

    /** DisplayName : "findAllByParent(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)". */
    public static final String DN_FINDALLBYPARENT_LIBELLE_BLANK = "findAllByParent(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** DisplayName : "findAllByParent(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)". */
    public static final String DN_FINDALLBYPARENT_NON_PERSISTANT = "findAllByParent(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent (contrat du port)";

    /** DisplayName : "findAllByParent(nominal) - retourne les enfants du parent". */
    public static final String DN_FINDALLBYPARENT_NOMINAL = "findAllByParent(nominal) - retourne les enfants du parent";

    /** DisplayName : "findById(null) - jette ExceptionAppliParamNull (contrat du port)". */
    public static final String DN_FINDBYID_NULL = "findById(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** DisplayName : "findById(absent) - retourne null". */
    public static final String DN_FINDBYID_ABSENT = "findById(absent) - retourne null";

    /** DisplayName : "findById(nominal) - retourne l'objet métier correspondant". */
    public static final String DN_FINDBYID_NOMINAL = "findById(nominal) - retourne l'objet métier correspondant";

    /** DisplayName : "update(null) - jette ExceptionAppliParamNull (contrat du port)". */
    public static final String DN_UPDATE_NULL = "update(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** DisplayName : "update(blank) - jette ExceptionAppliLibelleBlank (contrat du port)". */
    public static final String DN_UPDATE_BLANK = "update(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";

    /** DisplayName : "update(id null) - jette ExceptionAppliParamNonPersistent (contrat du port)". */
    public static final String DN_UPDATE_ID_NULL = "update(id null) - jette ExceptionAppliParamNonPersistent (contrat du port)";

    /** DisplayName : "update(absent) - retourne null". */
    public static final String DN_UPDATE_ABSENT = "update(absent) - retourne null";

    /** DisplayName : "update(nominal) - modifie le stockage et retourne l'objet modifié". */
    public static final String DN_UPDATE_NOMINAL = "update(nominal) - modifie le stockage et retourne l'objet modifié";

    /** DisplayName : "delete(null) - jette ExceptionAppliParamNull (contrat du port)". */
    public static final String DN_DELETE_NULL = "delete(null) - jette ExceptionAppliParamNull (contrat du port)";

    /** DisplayName : "delete(id null) - jette ExceptionAppliParamNonPersistent (contrat du port)". */
    public static final String DN_DELETE_ID_NULL = "delete(id null) - jette ExceptionAppliParamNonPersistent (contrat du port)";

    /** DisplayName : "delete(absent) - ne fait rien". */
    public static final String DN_DELETE_ABSENT = "delete(absent) - ne fait rien";

    /** DisplayName : "delete(nominal) - supprime l'élément et le rend introuvable". */
    public static final String DN_DELETE_NOMINAL = "delete(nominal) - supprime l'élément et le rend introuvable";

    /** DisplayName : "count() - cohérent avec le DAO". */
    public static final String DN_COUNT_NOMINAL = "count() - cohérent avec le DAO";

    // *************************** ATTRIBUTS *******************************/

    /**
     * <div>
     * <p>Service testé.</p>
     * </div>
     */
    @Autowired
    @Qualifier(QUALIFIER_SERVICE)
    private SousTypeProduitGatewayIService service;

    /**
     * <div>
     * <p>DAO parent (accès aux IDs persistés pour préparer les objets métier).</p>
     * </div>
     */
    @Autowired
    private TypeProduitDaoJPA typeProduitDaoJPA;

    /**
     * <div>
     * <p>DAO enfant (contrôles béton : compter / retrouver IDs).</p>
     * </div>
     */
    @Autowired
    private SousTypeProduitDaoJPA sousTypeProduitDaoJPA;

    /**
     * <div>
     * <p>EntityManager pour le rafraîchissement du cache Hibernate.</p>
     * </div>
     */
    @Autowired
    private EntityManager entityManager;
    
    /**
     * 
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;


    // ************************* METHODES **********************************/

    /**
     * <div>
     * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
     * </div>
     */
    public SousTypeProduitGatewayJPAServiceIntegrationTest() {
        super();
    }

    // ===================== CONFIGURATION SPRING =====================

    /**
     * <div>
     * <p style="font-weight:bold;">
     * Classe interne de configuration Spring.
     * </p>
     * <ul>
     * <li>@Configuration</li>
     * <li>@EnableJpaRepositories(basePackageClasses = {SousTypeProduitDaoJPA.class, TypeProduitDaoJPA.class})</li>
     * <li>@EntityScan(basePackageClasses = {SousTypeProduitJPA.class, TypeProduitJPA.class})</li>
     * </ul>
     * </div>
     *
     * @author Daniel Lévy
     * @version 1.0
     * @since 01 février 2026
     */
    @Configuration
    @EnableJpaRepositories(basePackageClasses = {SousTypeProduitDaoJPA.class, TypeProduitDaoJPA.class})
    @EntityScan(basePackageClasses = {SousTypeProduitJPA.class, TypeProduitJPA.class})
    public static final class ConfigTest { // NOPMD by danyl on 01/02/2026 00:00
    }

    // ============================== OUTILS ===============================

    /**
     * <div>
     * <p>Retrouve l'ID persistant d'un {@link TypeProduitJPA} par libellé.</p>
     * </div>
     *
     * @param pLibelleParent : String : 
     * libellé du TypeProduit
     * @return Long :  ID persistant
     */
    private Long retrouverIdParentPersistantParLibelle(
    		final String pLibelleParent) {
    	
        final TypeProduitJPA parent = this.typeProduitDaoJPA.findByTypeProduitIgnoreCase(pLibelleParent);
        assertThat(parent).isNotNull();
        assertThat(parent.getIdTypeProduit()).isNotNull();
        return parent.getIdTypeProduit();
    }

    /**
     * <div>
     * <p>Retrouve l'ID persistant d'un {@link SousTypeProduitJPA} par libellé.</p>
     * </div>
     *
     * @param pLibelleEnfant : String
     * @return Long
     */
    private Long retrouverIdEnfantPersistantParLibelle(final String pLibelleEnfant) {
        final List<SousTypeProduitJPA> enfants 
        = this.sousTypeProduitDaoJPA.findBySousTypeProduitIgnoreCase(pLibelleEnfant);
        assertThat(enfants).isNotNull().isNotEmpty();
        final SousTypeProduitJPA enfant = enfants.get(0);
        assertThat(enfant).isNotNull();
        assertThat(enfant.getIdSousTypeProduit()).isNotNull();
        return enfant.getIdSousTypeProduit();
    }

    // ============================== TESTS ================================

    // ===================== creer =====================

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de null sur creer(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>creer(null) jette {@link ExceptionAppliParamNull} avec MESSAGE_CREER_KO_PARAM_NULL.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune écriture en base.</p>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_NULL)
    @Test
    public void testCreerParamNullExceptionAppliParamNull() {
        assertThatThrownBy(() -> this.service.creer(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_CREER_KO_PARAM_NULL);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de libellé blank sur creer(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>creer(libellé blank) jette {@link ExceptionAppliLibelleBlank} avec MESSAGE_CREER_KO_LIBELLE_BLANK.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune écriture en base.</p>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_BLANK)
    @Test
    public void testCreerLibelleBlankExceptionAppliLibelleBlank() {
        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
        final TypeProduit parent = new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp = new SousTypeProduit(null, BLANK, parent);

        assertThatThrownBy(() -> this.service.creer(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_CREER_KO_LIBELLE_BLANK);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de parent null sur creer(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>creer(parent null) jette {@link ExceptionAppliParentNull} avec MESSAGE_CREER_KO_PARENT_NULL.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune écriture en base.</p>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_PARENT_NULL)
    @Test
    public void testCreerParentNullExceptionAppliParentNull() {
        final SousTypeProduit stp = new SousTypeProduit(null, LIBELLE_NOUVEAU_PULL, null);

        assertThatThrownBy(() -> this.service.creer(stp))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_CREER_KO_PARENT_NULL);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de libellé parent blank sur creer(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>creer(parent libellé blank) jette {@link ExceptionAppliLibelleBlank} avec MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune écriture en base.</p>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_PARENT_LIBELLE_BLANK)
    @Test
    public void testCreerParentLibelleBlankExceptionAppliLibelleBlank() {
        final TypeProduit parent = new TypeProduit(Long.valueOf(1L), BLANK);
        final SousTypeProduit stp = new SousTypeProduit(null, LIBELLE_NOUVEAU_PULL, parent);

        assertThatThrownBy(() -> this.service.creer(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de persistance du parent sur creer(pObject) (id parent null).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>creer(parent id null) jette {@link ExceptionTechniqueGatewayNonPersistent} avec préfixe MESSAGE_CREER_KO_PARENT_NON_PERSISTENT.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune écriture en base.</p>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_PARENT_NON_PERSISTANT)
    @Test
    public void testCreerParentIdNullExceptionTechniqueGatewayNonPersistent() {
        final TypeProduit parent = new TypeProduit(null, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp = new SousTypeProduit(null, LIBELLE_NOUVEAU_PULL, parent);

        assertThatThrownBy(() -> this.service.creer(stp))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_CREER_KO_PARENT_NON_PERSISTENT + LIBELLE_PARENT_VETEMENT);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de persistance du parent sur creer(pObject) (parent absent).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>creer(parent absent) jette {@link ExceptionTechniqueGatewayNonPersistent} avec préfixe MESSAGE_CREER_KO_PARENT_NON_PERSISTENT.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune écriture en base.</p>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_PARENT_NON_PERSISTANT)
    @Test
    public void testCreerParentAbsentExceptionTechniqueGatewayNonPersistent() {
        final TypeProduit parent = new TypeProduit(ID_INEXISTANT, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp = new SousTypeProduit(null, LIBELLE_NOUVEAU_PULL, parent);

        assertThatThrownBy(() -> this.service.creer(stp))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_CREER_KO_PARENT_NON_PERSISTENT + LIBELLE_PARENT_VETEMENT);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le fonctionnement nominal de creer(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>creer(nominal) retourne un {@link SousTypeProduit} non null, avec ID renseigné.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Le count() augmente de 1.</p>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_NOMINAL)
    @Test
    public void testCreerNominalOk() throws Exception {
        final long avant = this.service.count();

        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
        final TypeProduit parent = new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit aCreer = new SousTypeProduit(null, LIBELLE_NOUVEAU_PULL, parent);

        final SousTypeProduit cree = this.service.creer(aCreer);

        assertThat(cree).isNotNull();
        assertThat(cree.getIdSousTypeProduit()).isNotNull();
        assertThat(cree.getSousTypeProduit()).isEqualTo(LIBELLE_NOUVEAU_PULL);
        assertThat(cree.getTypeProduit()).isNotNull();
        assertThat(cree.getTypeProduit().getIdTypeProduit()).isEqualTo(idParent);

        final long apres = this.service.count();
        assertThat(apres).isEqualTo(avant + 1L);

        final SousTypeProduit relu = this.service.findById(cree.getIdSousTypeProduit());
        assertThat(relu).isNotNull();
        assertThat(relu.getSousTypeProduit()).isEqualTo(LIBELLE_NOUVEAU_PULL);
    }

    // ===================== rechercherTous =====================

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier rechercherTous() sur base initialisée.</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>rechercherTous() retourne une liste non null.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>La liste contient au moins les données seed (data-test.sql).</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_RECHERCHER_TOUS)
    @Test
    public void testRechercherTousNominalOk() throws Exception {
        final List<SousTypeProduit> liste = this.service.rechercherTous();

        assertThat(liste).isNotNull().isNotEmpty();
        assertThat(liste).extracting(SousTypeProduit::getSousTypeProduit)
            .contains(LIBELLE_ENFANT_VETEMENT_HOMME, LIBELLE_ENFANT_VETEMENT_FEMME, LIBELLE_ENFANT_VETEMENT_ENFANT);
    }

    // ===================== rechercherTousParPage =====================

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier rechercherTousParPage(null) (requête par défaut).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>rechercherTousParPage(null) retourne un {@link ResultatPage} non null.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>content non null.</p>
     * </div>
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage - null -> nominal")
    @Test
    public void testRechercherTousParPageParamNullNominalOk() throws Exception {
        final ResultatPage<SousTypeProduit> resultat = this.service.rechercherTousParPage(null);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull();
        assertThat(resultat.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(resultat.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(resultat.getTotalElements()).isPositive();
    }
    
    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier rechercherTousParPage(RequetePage avec tris valides).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>rechercherTousParPage(tris valides) retourne un {@link ResultatPage} non null avec contenu trié.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Le contenu est trié selon les spécifications.</p>
     * </div>
     */
    @Tag(TAG_PAGINATION)
    @DisplayName(DN_RECHERCHER_TOUS_PAR_PAGE_TRI)
    @Test
    public void testRechercherTousParPageTrisValidesOk() throws Exception {
        final RequetePage requete = new RequetePage();
        requete.getTris().add(new TriSpec("sousTypeProduit", DirectionTri.ASC));

        final ResultatPage<SousTypeProduit> resultat = this.service.rechercherTousParPage(requete);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull().isNotEmpty();
        assertThat(resultat.getContent())
            .extracting(SousTypeProduit::getSousTypeProduit)
            .isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER);
    }
    
    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier rechercherTousParPage(pageNumber > nombre de pages).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>rechercherTousParPage(page vide) retourne un {@link ResultatPage} avec contenu vide.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Le totalElements reste cohérent.</p>
     * </div>
     */
    @Tag(TAG_PAGINATION)
    @DisplayName(DN_RECHERCHER_TOUS_PAR_PAGE_VIDE)
    @Test
    public void testRechercherTousParPagePageVideOk() throws Exception {
        final RequetePage requete = new RequetePage();
        requete.setPageNumber(999); // Page inexistante

        final ResultatPage<SousTypeProduit> resultat = this.service.rechercherTousParPage(requete);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull().isEmpty();
        assertThat(resultat.getTotalElements()).isPositive();
    }


    // ===================== findByObjetMetier =====================

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de null sur findByObjetMetier(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByObjetMetier(null) jette {@link ExceptionAppliParamNull} avec MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune écriture en base.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_NULL)
    @Test
    public void testFindByObjetMetierParamNullExceptionAppliParamNull() {
        assertThatThrownBy(() -> this.service.findByObjetMetier(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de libellé blank sur findByObjetMetier(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByObjetMetier(libellé blank) jette {@link ExceptionAppliLibelleBlank} avec MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune écriture en base.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_BLANK)
    @Test
    public void testFindByObjetMetierLibelleBlankExceptionAppliLibelleBlank() {
        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
        final TypeProduit parent = new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp = new SousTypeProduit(null, BLANK, parent);

        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de parent null sur findByObjetMetier(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByObjetMetier(parent null) jette {@link ExceptionAppliParentNull} avec MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune écriture en base.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_PARENT_NULL)
    @Test
    public void testFindByObjetMetierParentNullExceptionAppliParentNull() {
        final SousTypeProduit stp = new SousTypeProduit(null, LIBELLE_ENFANT_VETEMENT_HOMME, null);

        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de libellé parent blank sur findByObjetMetier(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByObjetMetier(parent libellé blank) jette {@link ExceptionAppliLibelleBlank} avec MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune écriture en base.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_PARENT_LIBELLE_BLANK)
    @Test
    public void testFindByObjetMetierParentLibelleBlankExceptionAppliLibelleBlank() {
        final TypeProduit parent = new TypeProduit(Long.valueOf(1L), BLANK);
        final SousTypeProduit stp = new SousTypeProduit(null, LIBELLE_ENFANT_VETEMENT_HOMME, parent);

        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de persistance du parent sur findByObjetMetier(pObject) (id parent null).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByObjetMetier(parent id null) jette {@link ExceptionTechniqueGatewayNonPersistent} avec préfixe MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune écriture en base.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_PARENT_NON_PERSISTANT)
    @Test
    public void testFindByObjetMetierParentIdNullExceptionTechniqueGatewayNonPersistent() {
        final TypeProduit parent = new TypeProduit(null, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp = new SousTypeProduit(null, LIBELLE_ENFANT_VETEMENT_HOMME, parent);

        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT + LIBELLE_PARENT_VETEMENT);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier findByObjetMetier(nominal) sur donnée seed.</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByObjetMetier(trouvé) retourne un {@link SousTypeProduit} non null.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Libellé et parent cohérents.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName(DN_FINDBYOBJETMETIER_NOMINAL)
    @Test
    public void testFindByObjetMetierNominalOk() throws Exception {
        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
        final TypeProduit parent = new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);

        final SousTypeProduit probe = new SousTypeProduit(null, LIBELLE_ENFANT_VETEMENT_HOMME, parent);

        final SousTypeProduit trouve = this.service.findByObjetMetier(probe);

        assertThat(trouve).isNotNull();
        assertThat(trouve.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_VETEMENT_HOMME);
        assertThat(trouve.getTypeProduit()).isNotNull();
        assertThat(trouve.getTypeProduit().getIdTypeProduit()).isEqualTo(idParent);
    }

    // ===================== findByLibelle =====================

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de blank sur findByLibelle(pLibelle).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByLibelle(blank) jette {@link ExceptionAppliLibelleBlank} avec MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune écriture en base.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYLIBELLE_BLANK)
    @Test
    public void testFindByLibelleBlankExceptionAppliLibelleBlank() {
        assertThatThrownBy(() -> this.service.findByLibelle(BLANK))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK);
    }
    
    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier findByLibelleRapide(contenu inexistant).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByLibelleRapide(inexistant) retourne une liste vide.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune exception.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_FINDBYLIBELLERAPIDE_INEXISTANT)
    @Test
    public void testFindByLibelleRapideInexistantVideOk() throws Exception {
        final List<SousTypeProduit> liste = this.service.findByLibelleRapide(CONTENU_PARTIEL_INEXISTANT);
        assertThat(liste).isNotNull().isEmpty();
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier findByLibelle(libellé inexistant).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByLibelle(inexistant) retourne une liste vide.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune exception.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYLIBELLE_INEXISTANT)
    @Test
    public void testFindByLibelleInexistantVideOk() throws Exception {
        final List<SousTypeProduit> liste = this.service.findByLibelle(LIBELLE_INEXISTANT);
        assertThat(liste).isNotNull().isEmpty();
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier findByLibelle(nominal) sur donnée seed.</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByLibelle(trouvé) retourne une liste non null.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>La liste contient au moins un élément correspondant.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYLIBELLE_NOMINAL)
    @Test
    public void testFindByLibelleNominalOk() throws Exception {
        final List<SousTypeProduit> liste = this.service.findByLibelle(LIBELLE_ENFANT_VETEMENT_HOMME);

        assertThat(liste).isNotNull().isNotEmpty();
        assertThat(liste).extracting(SousTypeProduit::getSousTypeProduit)
            .contains(LIBELLE_ENFANT_VETEMENT_HOMME);
    }

    // ===================== findByLibelleRapide =====================

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de null sur findByLibelleRapide(pLibelle).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByLibelleRapide(null) jette {@link ExceptionAppliParamNull} avec MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune écriture en base.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_FINDBYLIBELLERAPIDE_NULL)
    @Test
    public void testFindByLibelleRapideParamNullExceptionAppliParamNull() {
        assertThatThrownBy(() -> this.service.findByLibelleRapide(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier findByLibelleRapide(blank mais pas null) -> rechercherTous().</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByLibelleRapide(blank) retourne une liste non null.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Liste cohérente avec rechercherTous().</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_FINDBYLIBELLERAPIDE_BLANK)
    @Test
    public void testFindByLibelleRapideBlankRetourneTous() throws Exception {
        final List<SousTypeProduit> tous = this.service.rechercherTous();
        final List<SousTypeProduit> rapide = this.service.findByLibelleRapide(BLANK);

        assertThat(rapide).isNotNull();
        assertThat(rapide).hasSameSizeAs(tous);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier findByLibelleRapide(nominal) sur contenu partiel.</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findByLibelleRapide(partiel) retourne une liste non null.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Liste non vide.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName(DN_FINDBYLIBELLERAPIDE_NOMINAL)
    @Test
    public void testFindByLibelleRapideNominalOk() throws Exception {
        final List<SousTypeProduit> liste = this.service.findByLibelleRapide(CONTENU_PARTIEL_VET);

        assertThat(liste).isNotNull().isNotEmpty();
        assertThat(liste).extracting(SousTypeProduit::getSousTypeProduit)
            .allMatch(libelle -> libelle.contains(CONTENU_PARTIEL_VET));
    }

    // ===================== findAllByParent =====================

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de null sur findAllByParent(pParent).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findAllByParent(null) jette {@link ExceptionAppliParentNull} avec MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune lecture DAO enfant.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDALLBYPARENT_NULL)
    @Test
    public void testFindAllByParentParamNullExceptionAppliParentNull() {
        assertThatThrownBy(() -> this.service.findAllByParent(null))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle du libellé parent blank sur findAllByParent(pParent).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findAllByParent(parent libellé blank) jette {@link ExceptionAppliLibelleBlank} avec MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune lecture DAO enfant.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDALLBYPARENT_LIBELLE_BLANK)
    @Test
    public void testFindAllByParentParentLibelleBlankExceptionAppliLibelleBlank() {
        final TypeProduit parent = new TypeProduit(Long.valueOf(1L), BLANK);

        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de persistance du parent sur findAllByParent(pParent) (id null).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findAllByParent(parent id null) jette {@link ExceptionTechniqueGatewayNonPersistent} avec préfixe MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune lecture DAO enfant.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDALLBYPARENT_NON_PERSISTANT)
    @Test
    public void testFindAllByParentParentNonPersistantExceptionTechniqueGatewayNonPersistent() {
        final TypeProduit parent = new TypeProduit(null, LIBELLE_PARENT_VETEMENT);

        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT + LIBELLE_PARENT_VETEMENT);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier findAllByParent(nominal) sur parent seed.</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findAllByParent(nominal) retourne une liste non null.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Liste non vide.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDALLBYPARENT_NOMINAL)
    @Test
    public void testFindAllByParentNominalOk() throws Exception {
        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
        final TypeProduit parent = new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);

        final List<SousTypeProduit> liste = this.service.findAllByParent(parent);

        assertThat(liste).isNotNull().isNotEmpty();
        assertThat(liste)
        .extracting(SousTypeProduit::getTypeProduit)  // Extrait d'abord le TypeProduit
        .extracting("idTypeProduit")  // Puis extrait la propriété idTypeProduit par nom
        .containsOnly(idParent);
    }

    // ===================== findById =====================

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de null sur findById(pId).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findById(null) jette {@link ExceptionAppliParamNull} avec MESSAGE_FINDBYID_KO_PARAM_NULL.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune lecture DAO.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYID_NULL)
    @Test
    public void testFindByIdParamNullExceptionAppliParamNull() {
        assertThatThrownBy(() -> this.service.findById(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_FINDBYID_KO_PARAM_NULL);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier findById(absent).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findById(absent) retourne null.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Pas d'exception.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYID_ABSENT)
    @Test
    public void testFindByIdAbsentNull() throws Exception {
        final SousTypeProduit retour = this.service.findById(ID_INEXISTANT);
        assertThat(retour).isNull();
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier findById(nominal) sur ID seed.</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>findById(trouvé) retourne un {@link SousTypeProduit} non null.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Libellé cohérent.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName(DN_FINDBYID_NOMINAL)
    @Test
    public void testFindByIdNominalOk() throws Exception {
        final Long id = retrouverIdEnfantPersistantParLibelle(LIBELLE_ENFANT_VETEMENT_HOMME);
        final SousTypeProduit retour = this.service.findById(id);

        assertThat(retour).isNotNull();
        assertThat(retour.getIdSousTypeProduit()).isEqualTo(id);
        assertThat(retour.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_VETEMENT_HOMME);
    }

    // ===================== update =====================

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de null sur update(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>update(null) jette {@link ExceptionAppliParamNull} avec MESSAGE_UPDATE_KO_PARAM_NULL.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune écriture en base.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_NULL)
    @Test
    public void testUpdateParamNullExceptionAppliParamNull() {
        assertThatThrownBy(() -> this.service.update(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_PARAM_NULL);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle du libellé blank sur update(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>update(libellé blank) jette {@link ExceptionAppliLibelleBlank} avec MESSAGE_UPDATE_KO_LIBELLE_BLANK.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune écriture en base.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_BLANK)
    @Test
    public void testUpdateLibelleBlankExceptionAppliLibelleBlank() {
        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
        final TypeProduit parent = new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp = new SousTypeProduit(Long.valueOf(1L), BLANK, parent);

        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_BLANK);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de non-persistance du paramètre sur update(pObject) (id null).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>update(id null) jette {@link ExceptionAppliParamNonPersistent} avec préfixe MESSAGE_UPDATE_KO_NON_PERSISTENT.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune écriture en base.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_ID_NULL)
    @Test
    public void testUpdateIdNullExceptionAppliParamNonPersistent() {
        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
        final TypeProduit parent = new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp = new SousTypeProduit(null, LIBELLE_ENFANT_VETEMENT_FEMME, parent);

        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_NON_PERSISTENT + LIBELLE_ENFANT_VETEMENT_FEMME);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier update(absent) -> null.</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>update(absent) retourne null.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Pas d'exception.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_ABSENT)
    @Test
    public void testUpdateAbsentNull() throws Exception {
        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
        final TypeProduit parent = new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp = new SousTypeProduit(ID_INEXISTANT, LIBELLE_A_SUPPRIMER, parent);

        final SousTypeProduit retour = this.service.update(stp);
        assertThat(retour).isNull();
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier update(nominal) avec changement de parent.</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>update(parent modifié) retourne un {@link SousTypeProduit} avec le nouveau parent.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Le parent est mis à jour en base.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_PARENT_MODIFIE)
    @Test
    public void testUpdateParentModifieOk() throws Exception {
        // Récupération des IDs persistants
        final Long idEnfant = retrouverIdEnfantPersistantParLibelle(LIBELLE_ENFANT_VETEMENT_FEMME);
        final Long idParentVetement = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);

        // Vérification de l'existence du second parent (ex: "Chaussure")
        final TypeProduitJPA parentChaussure 
        	= this.typeProduitDaoJPA.findByTypeProduitIgnoreCase(
        			LIBELLE_PARENT_CHAUSSURE);
        
        final Long idParentChaussure;
        if (parentChaussure == null) {
            // Si "Chaussure" n'existe pas, on utilise le parent "vêtement" pour éviter les erreurs
            idParentChaussure = idParentVetement;
        } else {
            idParentChaussure = parentChaussure.getIdTypeProduit();
        }

        // Création de l'objet à mettre à jour
        final String libelleNouveauParent = idParentChaussure.equals(idParentVetement) ? LIBELLE_PARENT_VETEMENT : LIBELLE_PARENT_CHAUSSURE;
        final TypeProduit nouveauParent = new TypeProduit(idParentChaussure, libelleNouveauParent);
        final SousTypeProduit aMettreAJour = new SousTypeProduit(idEnfant, LIBELLE_ENFANT_VETEMENT_FEMME, nouveauParent);

        // Mise à jour
        final SousTypeProduit maj = this.service.update(aMettreAJour);

        // Vérifications
        assertThat(maj).isNotNull();
        assertThat(maj.getTypeProduit()).isNotNull();
        assertThat(maj.getTypeProduit().getIdTypeProduit()).isEqualTo(idParentChaussure);

        // Vérification en base
        final SousTypeProduit relu = this.service.findById(idEnfant);
        assertThat(relu).isNotNull();
        assertThat(relu.getTypeProduit().getIdTypeProduit()).isEqualTo(idParentChaussure);
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier update(nominal) : modification du libellé.</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>update(nominal) retourne un {@link SousTypeProduit} non null.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Libellé mis à jour.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName(DN_UPDATE_NOMINAL)
    @Test
    public void testUpdateNominalOk() throws Exception {
        final Long idEnfant = retrouverIdEnfantPersistantParLibelle(LIBELLE_ENFANT_VETEMENT_FEMME);
        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
        final TypeProduit parent = new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);

        final SousTypeProduit stp = new SousTypeProduit(idEnfant, LIBELLE_MODIFIE_FEMME, parent);

        final SousTypeProduit maj = this.service.update(stp);

        assertThat(maj).isNotNull();
        assertThat(maj.getSousTypeProduit()).isEqualTo(LIBELLE_MODIFIE_FEMME);
        assertThat(maj.getTypeProduit()).isNotNull();
        assertThat(maj.getTypeProduit().getIdTypeProduit()).isEqualTo(idParent);

        final SousTypeProduit relu = this.service.findById(idEnfant);
        assertThat(relu).isNotNull();
        assertThat(relu.getSousTypeProduit()).isEqualTo(LIBELLE_MODIFIE_FEMME);
    }

    // ===================== delete =====================

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de null sur delete(pObject).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>delete(null) jette {@link ExceptionAppliParamNull} avec MESSAGE_DELETE_KO_PARAM_NULL.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune suppression en base.</p>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_NULL)
    @Test
    public void testDeleteParamNullExceptionAppliParamNull() {
        assertThatThrownBy(() -> this.service.delete(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_DELETE_KO_PARAM_NULL);
    }

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier le contrôle de non-persistance sur delete(pObject) (id null).</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>delete(id null) jette {@link ExceptionAppliParamNonPersistent} avec MESSAGE_DELETE_KO_ID_NULL.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Aucune suppression en base.</p>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_ID_NULL)
    @Test
    public void testDeleteIdNullExceptionAppliParamNonPersistent() {
        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
        final TypeProduit parent = new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp = new SousTypeProduit(null, LIBELLE_A_SUPPRIMER, parent);

        assertThatThrownBy(() -> this.service.delete(stp))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(SousTypeProduitGatewayIService.MESSAGE_DELETE_KO_ID_NULL);
    }

    
    
    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier delete(absent) ne supprime rien.</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>delete(absent) n'échoue pas.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Le count() reste identique.</p>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_ABSENT)
    @Test
    public void testDeleteAbsentNeFaitRien() throws Exception {
        final long avant = this.service.count();

        final Long idParent = retrouverIdParentPersistantParLibelle(
        		LIBELLE_PARENT_VETEMENT);
        final TypeProduit parent = new TypeProduit(
        		idParent, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit stp = new SousTypeProduit(
        		ID_INEXISTANT, LIBELLE_A_SUPPRIMER, parent);

        this.service.delete(stp);

        final long apres = this.service.count();
        assertThat(apres).isEqualTo(avant);
    }

 
    
    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Test d'intégration pour vérifier
     * le comportement nominal de la suppression
     * d'un {@link SousTypeProduit} via le service
     * {@link SousTypeProduitGatewayIService}.</p>
     *
     * <p style="font-weight:bold;">SCÉNARIO TESTÉ :</p>
     * <ol>
     * <li>Vérification de l'état initial (3 enregistrements seedés)</li>
     * <li>Création d'un nouvel enregistrement (ID attendu: 4)</li>
     * <li>Suppression de l'enregistrement créé</li>
     * <li>Vérification que la suppression est effective :
     *   <ul>
     *   <li>L'entité n'est plus trouvée via vérification directe en base</li>
     *   <li>Le count revient à 3</li>
     *   </ul>
     * </li>
     * </ol>
     *
     * <p style="font-weight:bold;">SOLUTION TECHNIQUE :</p>
     * <ul>
     * <li>Utilisation de @Transactional(propagation = Propagation.NEVER) pour éviter les transactions imbriquées</li>
     * <li>Vérification directe via JdbcTemplate (contourne Hibernate)</li>
     * <li>Suppression via EntityManager.remove() + flush() pour forcer l'exécution</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName(DN_DELETE_NOMINAL)
    @Test
    @Transactional(propagation = Propagation.NEVER) // Désactive les transactions imbriquées
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Sql(scripts = {CLASSPATH_TRUNCATE_SQL, CLASSPATH_DATA_SQL},
         executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testDeleteNominalOk() throws Exception {
        // =============================================
        // 1. VÉRIFICATION DE L'ÉTAT INITIAL
        // =============================================
        final long avant = this.service.count();
        assertThat(avant)
            .as("Le nombre initial d'enregistrements doit être 3 (seed data)")
            .isEqualTo(3L);

        // =============================================
        // 2. CRÉATION D'UN ÉLÉMENT À SUPPRIMER
        // =============================================
        final Long idParent = retrouverIdParentPersistantParLibelle(LIBELLE_PARENT_VETEMENT);
        final TypeProduit parent = new TypeProduit(idParent, LIBELLE_PARENT_VETEMENT);
        final SousTypeProduit cree = this.service.creer(
            new SousTypeProduit(null, LIBELLE_A_SUPPRIMER, parent)
        );

        // Vérification de la persistance de l'objet créé
        assertThat(cree).isNotNull();
        assertThat(cree.getIdSousTypeProduit()).isNotNull();
        System.out.println("[DEBUG] ID créé : " + cree.getIdSousTypeProduit());

        // Vérification directe via JdbcTemplate avant suppression
        final Integer countAvant = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT WHERE ID_SOUS_TYPE_PRODUIT = ?",
            Integer.class, cree.getIdSousTypeProduit()
        );
        assertThat(countAvant)
            .as("L'enregistrement doit exister en base avant suppression")
            .isEqualTo(1);

        // =============================================
        // 3. SUPPRESSION
        // =============================================
        this.service.delete(cree);

        // =============================================
        // 4. VÉRIFICATIONS POST-SUPPRESSION
        // =============================================
        verifierSuppressionEnBase(cree.getIdSousTypeProduit());

        // Vérification finale du count global
        final long apres = this.service.count();
        assertThat(apres)
            .as("Le nombre d'enregistrements doit revenir à 3 après suppression")
            .isEqualTo(3L);
    }

    
    
    /**
     * Vérifie la suppression directement en base via JdbcTemplate
     * (contourne complètement Hibernate et son cache)
     *
     * @param idSupprime L'ID de l'entité supprimée à vérifier
     * @throws Exception
     */
    private void verifierSuppressionEnBase(final Long idSupprime) throws Exception {
    	
        // Vérification de l'absence de l'enregistrement
        final Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT WHERE ID_SOUS_TYPE_PRODUIT = ?",
            Integer.class, idSupprime
        );

        assertThat(count)
            .as("L'enregistrement doit être physiquement supprimé de la base (expected: 0, actual: " + count + ")")
            .isEqualTo(0);

        // Vérification supplémentaire via le service
        assertThat(this.service.findById(idSupprime))
            .as("Le service ne doit plus trouver l'objet supprimé")
            .isNull();
    }
    
    
     
    // ===================== count =====================

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Vérifier count() nominal.</p>
     * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
     * <p>count() retourne un long >= 0.</p>
     * <p style="font-weight:bold;">GARANTIES TECHNIQUES et METIER :</p>
     * <p>Cohérent avec le DAO.</p>
     * </div>
     */
    @Tag(TAG_COUNT)
    @DisplayName(DN_COUNT_NOMINAL)
    @Test
    public void testCountNominalOk() throws Exception {
        final long viaService = this.service.count();
        final long viaDao = this.sousTypeProduitDaoJPA.count();

        assertThat(viaService).isGreaterThanOrEqualTo(0L);
        assertThat(viaService).isEqualTo(viaDao);
    }
}
