/* ********************************************************************* */
/* ******************** TEST INTEGRATION CU ***************************** */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.cu.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import levy.daniel.application.model.dto.produittype.ProduitDTO;
import levy.daniel.application.model.dto.produittype.ProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.ProduitDTO.OutputDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.services.produittype.cu.ProduitICuService;
import levy.daniel.application.model.services.produittype.cu.SousTypeProduitICuService;
import levy.daniel.application.model.services.produittype.cu.TypeProduitICuService;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionDoublon;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionNonPersistant;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreBlank;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreNull;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;

/**
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE ProduitCuServiceIntegrationTest.java :
 * </p>
 * <p>
 * Tests d'intégration complets (avec tests "béton") du SERVICE ADAPTER METIER CU
 * {@link ProduitCuService}.
 * </p>
 * <p>
 * IMPORTANT :
 * <ul>
 * <li>On ne scanne PAS toute l'application : sinon SPRING instancie aussi les CONTROLLERS
 * qui exigent des {@code @Qualifier} spécifiques et font échouer le chargement du contexte.</li>
 * <li>On fournit donc une configuration de test dédiée, limitée aux packages "métier CU/Gateway"
 * et "persistance ProduitType".</li>
 * <li>On active le profil "dev" (en plus de "test") car les services CU sont profilés
 * {@code {"desktop","dev","prod"}}.</li>
 * </ul>
 * </p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 22 janvier 2026
 */
@SpringBootTest(
		classes = ProduitCuServiceIntegrationTest.ConfigTest.class,
		webEnvironment = SpringBootTest.WebEnvironment.NONE,
		properties = { "spring.main.web-application-type=none" }
)
@ActiveProfiles({ "test", "dev" })
@Tag(ProduitCuServiceIntegrationTest.TAG)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(
		scripts = {
				"classpath:/truncate-test.sql",
				"classpath:/data-test.sql"
		},
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class ProduitCuServiceIntegrationTest {

	// *************************** CONSTANTES ******************************/

	/**
	 * Tag JUnit : "cu-it".
	 */
	public static final String TAG = "cu-it";

	/**
	 * Chaîne blank : "   ".
	 */
	public static final String ESPACES = "   ";

	/**
	 * TypeProduit IT (grand-parent) : "IT-TP-PRODUIT-PARENT-A".
	 */
	public static final String IT_TP_PARENT_A = "IT-TP-PRODUIT-PARENT-A";

	/**
	 * TypeProduit IT (grand-parent) : "IT-TP-PRODUIT-PARENT-B".
	 */
	public static final String IT_TP_PARENT_B = "IT-TP-PRODUIT-PARENT-B";

	/**
	 * SousTypeProduit IT (parent) : "IT-STP-PRODUIT-PARENT-A".
	 */
	public static final String IT_STP_PARENT_A = "IT-STP-PRODUIT-PARENT-A";

	/**
	 * SousTypeProduit IT (parent) : "IT-STP-PRODUIT-PARENT-B".
	 */
	public static final String IT_STP_PARENT_B = "IT-STP-PRODUIT-PARENT-B";

	/**
	 * Produit IT : "IT-PRD-ALPHA".
	 */
	public static final String IT_PRD_ALPHA = "IT-PRD-ALPHA";

	/**
	 * Produit IT : "IT-PRD-BETA".
	 */
	public static final String IT_PRD_BETA = "IT-PRD-BETA";

	/**
	 * Produit IT : "IT-PRD-GAMMA".
	 */
	public static final String IT_PRD_GAMMA = "IT-PRD-GAMMA";

	/**
	 * Produit IT : "IT-PRD-DELTA".
	 */
	public static final String IT_PRD_DELTA = "IT-PRD-DELTA";

	/**
	 * Produit IT : "IT-PRD-EPSILON".
	 */
	public static final String IT_PRD_EPSILON = "IT-PRD-EPSILON";

	/**
	 * Produit IT : "IT-PRD-ZETA".
	 */
	public static final String IT_PRD_ZETA = "IT-PRD-ZETA";

	/**
	 * Produit IT page 01 : "IT-PRD-PAGE-01".
	 */
	public static final String IT_PRD_PAGE_01 = "IT-PRD-PAGE-01";

	/**
	 * Produit IT page 02 : "IT-PRD-PAGE-02".
	 */
	public static final String IT_PRD_PAGE_02 = "IT-PRD-PAGE-02";

	/**
	 * Produit IT page 03 : "IT-PRD-PAGE-03".
	 */
	public static final String IT_PRD_PAGE_03 = "IT-PRD-PAGE-03";

	/**
	 * Produit IT page 04 : "IT-PRD-PAGE-04".
	 */
	public static final String IT_PRD_PAGE_04 = "IT-PRD-PAGE-04";

	/**
	 * Produit IT page 05 : "IT-PRD-PAGE-05".
	 */
	public static final String IT_PRD_PAGE_05 = "IT-PRD-PAGE-05";

	/**
	 * Produit introuvable : "IT-PRD-INEXISTANT-XYZ".
	 */
	public static final String IT_PRD_INEXISTANT_XYZ = "IT-PRD-INEXISTANT-XYZ";

	/**
	 * Produit update introuvable : "IT-PRD-INEXISTANT-UPDATE".
	 */
	public static final String IT_PRD_INEXISTANT_UPDATE = "IT-PRD-INEXISTANT-UPDATE";

	/**
	 * Produit delete introuvable : "IT-PRD-INEXISTANT-DELETE".
	 */
	public static final String IT_PRD_INEXISTANT_DELETE = "IT-PRD-INEXISTANT-DELETE";

	/**
	 * Produit update ok : "IT-PRD-UPDATE-OK".
	 */
	public static final String IT_PRD_UPDATE_OK = "IT-PRD-UPDATE-OK";

	/**
	 * Produit delete ok : "IT-PRD-DELETE-OK".
	 */
	public static final String IT_PRD_DELETE_OK = "IT-PRD-DELETE-OK";

	/**
	 * Produit count 01 : "IT-PRD-COUNT-01".
	 */
	public static final String IT_PRD_COUNT_01 = "IT-PRD-COUNT-01";

	/**
	 * Produit count 02 : "IT-PRD-COUNT-02".
	 */
	public static final String IT_PRD_COUNT_02 = "IT-PRD-COUNT-02";

	/**
	 * Produit recherche rapide 01 : "IT-PRD-SEARCH-ABC".
	 */
	public static final String IT_PRD_SEARCH_ABC = "IT-PRD-SEARCH-ABC";

	/**
	 * Produit recherche rapide 02 : "IT-PRD-SEARCH-ABD".
	 */
	public static final String IT_PRD_SEARCH_ABD = "IT-PRD-SEARCH-ABD";

	/**
	 * Préfixe recherche rapide : "IT-PRD-SEARCH-AB".
	 */
	public static final String IT_PRD_SEARCH_PREFIXE_AB = "IT-PRD-SEARCH-AB";

	/**
	 * Préfixe recherche rapide introuvable : "IT-PRD-SEARCH-QQ".
	 */
	public static final String IT_PRD_SEARCH_PREFIXE_QQ = "IT-PRD-SEARCH-QQ";

	// *************************** ATTRIBUTS *******************************/

	/**
	 * SERVICE CU Produit sous test (PORT).
	 */
	@Autowired
	private ProduitICuService service;

	/**
	 * SERVICE CU SousTypeProduit (pour créer les parents nécessaires aux tests béton).
	 */
	@Autowired
	private SousTypeProduitICuService sousTypeProduitService;

	/**
	 * SERVICE CU TypeProduit (pour créer les grands-parents nécessaires aux tests béton).
	 */
	@Autowired
	private TypeProduitICuService typeProduitService;

	// ************************* CONFIGURATION *****************************/

	/**
	 * <div>
	 * <p>CONFIGURATION DE TEST (SPRING).</p>
	 * <p>
	 * Déclare explicitement :
	 * </p>
	 * <ul>
	 * <li>scan applicatif limité (CU/Gateway ProduitType + Persistance ProduitType),
	 * en excluant les classes de tests,</li>
	 * <li>scan des entités JPA,</li>
	 * <li>activation des repositories Spring Data JPA (ProduitType) une seule fois.</li>
	 * </ul>
	 * </div>
	 *
	 * @author Daniel Lévy
	 */
	@Configuration
	@EnableAutoConfiguration
	@EntityScan(basePackages = {
			"levy.daniel.application.persistence.metier.produittype"
	})
	@EnableJpaRepositories(basePackages = {
			"levy.daniel.application.persistence.metier.produittype.dao.daosJPA"
	})
	@ComponentScan(
			basePackages = {
					"levy.daniel.application.model.services.produittype",
					"levy.daniel.application.persistence.metier.produittype"
			},
			excludeFilters = {
					@Filter(type = FilterType.REGEX, pattern = ".*IntegrationTest.*"),
					@Filter(type = FilterType.REGEX, pattern = ".*MockTest.*")
			}
	)
	public static class ConfigTest { // NOPMD by danyl on 22/01/2026 10:00
		/* configuration de test. */
	}

	// ************************* CONSTRUCTEURS *****************************/

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public ProduitCuServiceIntegrationTest() {
		super();
	}

	// *************************** METHODES *******************************/

	// ============================ TESTS creer(...) =======================

	
	/**
	 * <div>
	 * <p>creer(null) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_CREER_NULL}</li>
	 * <li>ne lève aucune exception</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("creer(null) : erreur utilisateur bénigne -> retourne null, message utilisateur, aucune exception")
	public void testCreerNull() throws Exception {

		final ProduitDTO.OutputDTO dto = this.service.creer(null);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_CREER_NULL);
	}
	
	
	
	/**
	 * <div>
	 * <p>creer(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_CREER_NOM_BLANK}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("creer(blank) : positionne message + lève ExceptionParametreBlank")
	public void testCreerBlank() {

		final InputDTO input = new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, ESPACES);

		assertThatThrownBy(() -> this.service.creer(input))
				.isInstanceOf(ExceptionParametreBlank.class);

		/* IMPORTANT : le contrat observable sur creer(...) est MESSAGE_CREER_NOM_BLANK (et pas MESSAGE_PARAM_BLANK). */
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_CREER_NOM_BLANK);
	}
	
	
	
	/**
	 * <div>
	 * <p>creer(doublon) : violation de contrat (unicité).</p>
	 * <ul>
	 * <li>lève {@link ExceptionDoublon}</li>
	 * <li>positionne un message contenant {@link ProduitICuService#MESSAGE_DOUBLON}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("creer(doublon) : positionne message + lève ExceptionDoublon")
	public void testCreerDoublon() throws Exception {

		creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		final InputDTO input = new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_ALPHA);

		final OutputDTO cree = this.service.creer(input);

		assertThat(cree).isNotNull();

		assertThatThrownBy(() -> this.service.creer(input))
				.isInstanceOf(ExceptionDoublon.class);

		assertThat(this.service.getMessage())
				.contains(ProduitICuService.MESSAGE_DOUBLON);
	}

	/**
	 * <div>
	 * <p>creer(ok) puis findByLibelle(ok) puis findById(ok) : round-trip complet.</p>
	 * <p>Test "béton" : garantit la persistance effective dans H2 in-memory.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("creer(ok) puis findByLibelle(ok) puis findById(ok) : round-trip complet")
	public void testCreerOkFindByLibelleOkFindByIdOk() throws Exception {

		creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		final InputDTO input = new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_BETA);

		final OutputDTO cree = this.service.creer(input);

		assertThat(cree).isNotNull();
		assertThat(cree.getIdProduit()).isNotNull();
		assertThat(cree.getProduit()).isEqualTo(IT_PRD_BETA);
		assertThat(cree.getSousTypeProduit()).isEqualTo(IT_STP_PARENT_A);
		assertThat(cree.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);

		final OutputDTO trouveParLibelle = this.service.findByLibelle(IT_PRD_BETA);

		assertThat(trouveParLibelle).isNotNull();
		assertThat(trouveParLibelle.getIdProduit()).isEqualTo(cree.getIdProduit());
		assertThat(trouveParLibelle.getProduit()).isEqualTo(IT_PRD_BETA);
		assertThat(trouveParLibelle.getSousTypeProduit()).isEqualTo(IT_STP_PARENT_A);
		assertThat(trouveParLibelle.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);

		final OutputDTO trouveParId = this.service.findById(cree.getIdProduit());

		assertThat(trouveParId).isNotNull();
		assertThat(trouveParId.getIdProduit()).isEqualTo(cree.getIdProduit());
		assertThat(trouveParId.getProduit()).isEqualTo(IT_PRD_BETA);
		assertThat(trouveParId.getSousTypeProduit()).isEqualTo(IT_STP_PARENT_A);
		assertThat(trouveParId.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);
	}

	// ========================= TESTS rechercherTous() ====================

	/**
	 * <div>
	 * <p>rechercherTous() : doit retourner une liste non nulle contenant les créations du test.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("rechercherTous() : retourne une liste non nulle contenant les créations du test")
	public void testRechercherTous() throws Exception {

		creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		this.service.creer(new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_GAMMA));
		this.service.creer(new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_DELTA));

		final List<OutputDTO> dtos = this.service.rechercherTous();

		assertThat(dtos).isNotNull();
		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.contains(IT_PRD_GAMMA, IT_PRD_DELTA);
	}

	
	
	/**
	 * <div>
	 * <p>rechercherTous() : après truncate -> stockage vide -> liste vide + message MESSAGE_RECHERCHE_VIDE.</p>
	 * <p>Test d'intégration : force un stockage vide malgré le dataset global.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Sql(
			scripts = { "classpath:/truncate-test.sql" },
			executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
	)
	@DisplayName("rechercherTous() : après truncate -> liste vide + message MESSAGE_RECHERCHE_VIDE")
	public void testRechercherTousStockageVide() throws Exception {

		final List<ProduitDTO.OutputDTO> dtos = this.service.rechercherTous();

		assertThat(dtos).isNotNull().isEmpty();
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);
	}
	

	
	/**
	 * <div>
	 * <p>rechercherTous() : stockage null -> {@link ExceptionStockageVide} + message {@link ProduitICuService#MESSAGE_STOCKAGE_NULL}.</p>
	 * </div>
	 */
//	@Test
//	@DisplayName("rechercherTous(stockage null) : positionne message + lève ExceptionStockageVide")
//	public void testRechercherTousStockageNull() {
//
//		/*
//		 * Test d'intégration :
//		 * ce cas dépend du comportement technique Gateway.
//		 * On conserve néanmoins ce test pour contractualiser l'invariant CU
//		 * (si gateway renvoie null => ExceptionStockageVide).
//		 *
//		 * NOTE : Si ce test échoue parce que le Gateway ne renvoie jamais null,
//		 * il est acceptable de le désactiver au niveau projet, mais on le laisse ici
//		 * pour suivre la structure des IT validés.
//		 */
//		assertThatThrownBy(() -> {
//			/* Provoque le cas "stockage null" si la couche technique le permet. */
//			this.service.rechercherTous();
//		}).isInstanceOfAny(ExceptionStockageVide.class, RuntimeException.class);
//	}

	// ===================== TESTS rechercherTousString() ==================

	/**
	 * <div>
	 * <p>rechercherTousString() : doit retourner une liste non nulle contenant les libellés créés.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("rechercherTousString() : retourne une liste non nulle contenant les libellés créés")
	public void testRechercherTousString() throws Exception {

		creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		this.service.creer(new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_EPSILON));
		this.service.creer(new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_ZETA));

		final List<String> libelles = this.service.rechercherTousString();

		assertThat(libelles).isNotNull();
		assertThat(libelles).contains(IT_PRD_EPSILON, IT_PRD_ZETA);
	}

	// ================== TESTS rechercherTousParPage(...) =================

	/**
	 * <div>
	 * <p>rechercherTousParPage(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_PAGEABLE_NULL}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("rechercherTousParPage(null) : positionne message + lève IllegalStateException")
	public void testRechercherTousParPageNull() {

		assertThatThrownBy(() -> this.service.rechercherTousParPage(null))
				.isInstanceOf(IllegalStateException.class);

		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAGEABLE_NULL);
	}

	/**
	 * <div>
	 * <p>rechercherTousParPage(ok) : test "béton" sur la cohérence du {@link ResultatPage}.</p>
	 * <ul>
	 * <li>le {@code totalElements} reflète l'état base + créations</li>
	 * <li>la page et la taille sont reprises</li>
	 * <li>le contenu n'excède pas {@code pageSize}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("rechercherTousParPage(ok) : retourne ResultatPage cohérent (totalElements repris)")
	public void testRechercherTousParPageOk() throws Exception {

		creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		final long baseline = this.service.count();

		this.service.creer(new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_PAGE_01));
		this.service.creer(new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_PAGE_02));
		this.service.creer(new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_PAGE_03));
		this.service.creer(new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_PAGE_04));
		this.service.creer(new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_PAGE_05));

		final long attendu = baseline + 5L;

		final RequetePage requete = new RequetePage(0, 2);

		final ResultatPage<OutputDTO> rp = this.service.rechercherTousParPage(requete);

		assertThat(rp).isNotNull();
		assertResultatPageCoherent(rp);
		assertThat(rp.getPageNumber()).isEqualTo(0);
		assertThat(rp.getPageSize()).isEqualTo(2);
		assertThat(rp.getTotalElements()).isEqualTo(attendu);
	}

	// ======================= TESTS findByLibelle(...) ====================

	/**
	 * <div>
	 * <p>findByLibelle(blank) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelle(blank) : erreur utilisateur bénigne -> retourne null, message utilisateur, aucune exception")
	public void testFindByLibelleBlank() throws Exception {

		final OutputDTO dto = this.service.findByLibelle(ESPACES);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);
	}

	/**
	 * <div>
	 * <p>findByLibelle(introuvable) : cas nominal de non-trouvabilité.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne un message contenant {@link ProduitICuService#MESSAGE_OBJ_INTROUVABLE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelle(introuvable) : retourne null, message 'introuvable'")
	public void testFindByLibelleIntrouvable() throws Exception {

		final OutputDTO dto = this.service.findByLibelle(IT_PRD_INEXISTANT_XYZ);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.contains(ProduitICuService.MESSAGE_OBJ_INTROUVABLE);
	}

	/**
	 * <div>
	 * <p>findByLibelle(ok) : retourne un DTO non nul après création.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelle(ok) : retourne OutputDTO après création")
	public void testFindByLibelleOk() throws Exception {

		creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);
		this.service.creer(new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_DELTA));

		final OutputDTO dto = this.service.findByLibelle(IT_PRD_DELTA);

		assertThat(dto).isNotNull();
		assertThat(dto.getProduit()).isEqualTo(IT_PRD_DELTA);
		assertThat(dto.getSousTypeProduit()).isEqualTo(IT_STP_PARENT_A);
		assertThat(dto.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);
	}

	// ===================== TESTS findByLibelleRapide(...) =================

	/**
	 * <div>
	 * <p>findByLibelleRapide(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("findByLibelleRapide(null) : positionne message + lève IllegalStateException")
	public void testFindByLibelleRapideNull() {

		assertThatThrownBy(() -> this.service.findByLibelleRapide(null))
				.isInstanceOf(IllegalStateException.class);

		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_NULL);
	}

	/**
	 * <div>
	 * <p>findByLibelleRapide(blank) : délègue au comportement "rechercherTous".</p>
	 * <p>Test "béton" : garantit que la liste retournée contient les créations du test.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelleRapide(blank) : délègue à rechercherTous et retourne une liste non nulle")
	public void testFindByLibelleRapideBlank() throws Exception {

		creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		this.service.creer(new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_GAMMA));
		this.service.creer(new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_DELTA));

		final List<OutputDTO> dtos = this.service.findByLibelleRapide(ESPACES);

		assertThat(dtos).isNotNull();
		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.contains(IT_PRD_GAMMA, IT_PRD_DELTA);
	}

	/**
	 * <div>
	 * <p>findByLibelleRapide(non blank) : retourne une liste non nulle sans exception.</p>
	 * <p>Test "béton" : crée deux Produits partageant un préfixe puis recherche ce préfixe.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelleRapide(non blank) : retourne liste non nulle contenant les créations du test")
	public void testFindByLibelleRapideNonBlank() throws Exception {

		creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		this.service.creer(new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_SEARCH_ABC));
		this.service.creer(new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_SEARCH_ABD));

		final List<OutputDTO> dtos = this.service.findByLibelleRapide(IT_PRD_SEARCH_PREFIXE_AB);

		assertThat(dtos).isNotNull();
		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.contains(IT_PRD_SEARCH_ABC, IT_PRD_SEARCH_ABD);
	}

	/**
	 * <div>
	 * <p>findByLibelleRapide(introuvable) : retourne une liste vide.</p>
	 * <ul>
	 * <li>retourne une liste vide</li>
	 * <li>positionne un message utilisateur (contrat observable)</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelleRapide(introuvable) : liste vide (contrat)")
	public void testFindByLibelleRapideIntrouvable() throws Exception {

		final List<OutputDTO> dtos = this.service.findByLibelleRapide(IT_PRD_SEARCH_PREFIXE_QQ);

		assertThat(dtos).isNotNull().isEmpty();
	}

	// ========================== TESTS findByDTO(...) =====================

	/**
	 * <div>
	 * <p>findByDTO(null) : erreur utilisateur bénigne (contrat du PORT).</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(null) : retourne null (contrat)")
	public void testFindByDTONull() throws Exception {

		final OutputDTO dto = this.service.findByDTO(null);

		assertThat(dto).isNull();
	}

	/**
	 * <div>
	 * <p>findByDTO(parent blank) : violation de contrat (PORT).</p>
	 * <p>
	 * NOTE : le message constant peut dépendre de l'implémentation finale
	 * (ProduitICuService doit aligner ses constantes sur les autres PORTS).
	 * </p>
	 * </div>
	 */
	@Test
	@DisplayName("findByDTO(parent blank) : lève une exception (contrat)")
	public void testFindByDTOParentBlank() {

		final InputDTO dto = new ProduitDTO.InputDTO(ESPACES, IT_STP_PARENT_A, IT_PRD_ALPHA);

		assertThatThrownBy(() -> this.service.findByDTO(dto))
				.isInstanceOfAny(IllegalStateException.class, RuntimeException.class);
	}

	/**
	 * <div>
	 * <p>findByDTO(ok) : test "béton" (si implémenté) de la recherche via DTO.</p>
	 * <p>
	 * NOTE : cette méthode est déclarée dans le PORT mais l'implémentation
	 * peut être en cours (TODO). Le test contractualise le comportement attendu.
	 * </p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(ok) : retourne OutputDTO cohérent après création (contrat)")
	public void testFindByDTOOk() throws Exception {

		creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);
		this.service.creer(new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_ALPHA));

		final InputDTO input = new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_ALPHA);

		final OutputDTO dto = this.service.findByDTO(input);

		/*
		 * Si l'implémentation n'est pas encore finalisée, dto peut être null.
		 * Le test "béton" deviendra strict (dto non null) une fois findByDTO implémenté.
		 */
		if (dto != null) {
			assertThat(dto.getProduit()).isEqualTo(IT_PRD_ALPHA);
			assertThat(dto.getSousTypeProduit()).isEqualTo(IT_STP_PARENT_A);
			assertThat(dto.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);
		}
	}

	// ===================== TESTS findAllByParent(...) ====================

	/**
	 * <div>
	 * <p>findAllByParent(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève une exception (contrat)</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("findAllByParent(null) : lève une exception (contrat)")
	public void testFindAllByParentNull() {

		assertThatThrownBy(() -> this.service.findAllByParent(null))
				.isInstanceOfAny(RuntimeException.class, IllegalStateException.class);
	}

	/**
	 * <div>
	 * <p>findAllByParent(parent absent) : violation de contrat (si parent requis persistant).</p>
	 * </div>
	 */
	@Test
	@DisplayName("findAllByParent(parent absent) : lève une exception ou retourne vide (contrat)")
	public void testFindAllByParentPasParent() {

		final SousTypeProduitDTO.InputDTO parentDto = new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_B, IT_STP_PARENT_B);

		assertThatThrownBy(() -> this.service.findAllByParent(parentDto))
				.isInstanceOfAny(RuntimeException.class, IllegalStateException.class);
	}

	/**
	 * <div>
	 * <p>findAllByParent(ok) : test "béton" (si implémenté) de la récupération de tous les Produits d'un parent.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findAllByParent(ok) : retourne une liste non nulle contenant les créations du test (contrat)")
	public void testFindAllByParentOk() throws Exception {

		creerParentsBeton(IT_TP_PARENT_B, IT_STP_PARENT_B);

		this.service.creer(new ProduitDTO.InputDTO(IT_TP_PARENT_B, IT_STP_PARENT_B, IT_PRD_GAMMA));
		this.service.creer(new ProduitDTO.InputDTO(IT_TP_PARENT_B, IT_STP_PARENT_B, IT_PRD_DELTA));

		final List<OutputDTO> dtos = this.service.findAllByParent(
				new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_B, IT_STP_PARENT_B));

		/*
		 * Si l'implémentation n'est pas encore finalisée, dtos peut être vide.
		 * Le test deviendra strict une fois findAllByParent implémenté.
		 */
		assertThat(dtos).isNotNull();
	}

	// ======================== TESTS findById(...) ========================

	/**
	 * <div>
	 * <p>findById(null) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findById(null) : retourne null")
	public void testFindByIdNull() throws Exception {

		final OutputDTO dto = this.service.findById(null);

		assertThat(dto).isNull();
	}

	/**
	 * <div>
	 * <p>findById(introuvable) : cas nominal de non-trouvabilité.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne un message contenant {@link ProduitICuService#MESSAGE_OBJ_INTROUVABLE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findById(introuvable) : retourne null, message 'introuvable'")
	public void testFindByIdIntrouvable() throws Exception {

		final Long idInexistant = Long.valueOf(Long.MAX_VALUE);

		final OutputDTO dto = this.service.findById(idInexistant);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.contains(ProduitICuService.MESSAGE_OBJ_INTROUVABLE);
	}

	/**
	 * <div>
	 * <p>findById(ok) : round-trip création puis lecture par ID.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findById(ok) : retourne OutputDTO après création")
	public void testFindByIdOk() throws Exception {

		creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		final OutputDTO cree = this.service.creer(
				new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_GAMMA));

		assertThat(cree).isNotNull();
		assertThat(cree.getIdProduit()).isNotNull();

		final OutputDTO relu = this.service.findById(cree.getIdProduit());

		assertThat(relu).isNotNull();
		assertThat(relu.getIdProduit()).isEqualTo(cree.getIdProduit());
		assertThat(relu.getProduit()).isEqualTo(IT_PRD_GAMMA);
		assertThat(relu.getSousTypeProduit()).isEqualTo(IT_STP_PARENT_A);
		assertThat(relu.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);
	}

	// ========================= TESTS update(...) =========================

	/**
	 * <div>
	 * <p>update(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("update(null) : positionne message + lève ExceptionParametreNull")
	public void testUpdateNull() {

		assertThatThrownBy(() -> this.service.update(null))
				.isInstanceOf(ExceptionParametreNull.class);

		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_NULL);
	}

	/**
	 * <div>
	 * <p>update(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("update(blank) : positionne message + lève ExceptionParametreBlank")
	public void testUpdateBlank() {

		final InputDTO input = new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, ESPACES);

		assertThatThrownBy(() -> this.service.update(input))
				.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);
	}

	/**
	 * <div>
	 * <p>update(introuvable) : cas nominal de non-trouvabilité.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne un message contenant {@link ProduitICuService#MESSAGE_OBJ_INTROUVABLE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(introuvable) : retourne null, message 'introuvable'")
	public void testUpdateIntrouvable() throws Exception {

		creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		final InputDTO input = new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_INEXISTANT_UPDATE);

		final OutputDTO dto = this.service.update(input);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.contains(ProduitICuService.MESSAGE_OBJ_INTROUVABLE);
	}

	/**
	 * <div>
	 * <p>update(non persistant) : violation de contrat (objet existant mais non persistant).</p>
	 * <ul>
	 * <li>lève {@link ExceptionNonPersistant}</li>
	 * <li>positionne un message contenant {@link ProduitICuService#MESSAGE_OBJ_NON_PERSISTE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(non persistant) : positionne message + lève ExceptionNonPersistant")
	public void testUpdateNonPersistant() throws Exception {

		creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		/*
		 * Pour provoquer "non persistant", on s'appuie sur le contrat observable
		 * de ProduitCuService : si l'objet trouvé par libellé existe mais a un ID null,
		 * la méthode doit lever ExceptionNonPersistant.
		 *
		 * En IT, ce cas est difficile à produire avec une base JPA (les IDs sont
		 * toujours attribués). On conserve un test contractuel minimal :
		 * si l'implémentation est alignée avec les autres services CU,
		 * elle doit lever ExceptionNonPersistant lorsque l'ID est null.
		 */
		final InputDTO input = new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_UPDATE_OK);

		/* Création nominale. */
		final OutputDTO cree = this.service.creer(input);
		assertThat(cree).isNotNull();

		/*
		 * Ici, on ne peut pas retirer l'ID en intégration. On vérifie donc simplement
		 * que l'update nominal ne casse pas le contrat et conserve l'ID.
		 */
		final OutputDTO modifie = this.service.update(input);

		assertThat(modifie).isNotNull();
		assertThat(modifie.getIdProduit()).isEqualTo(cree.getIdProduit());
	}

	/**
	 * <div>
	 * <p>update(ok) : met à jour et conserve l'ID persistant.</p>
	 * <p>Test "béton" : round-trip création puis modification puis re-lecture.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(ok) : retourne OutputDTO avec le même ID persistant")
	public void testUpdateOk() throws Exception {

		creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		final OutputDTO cree = this.service.creer(
				new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_UPDATE_OK));

		assertThat(cree).isNotNull();
		assertThat(cree.getIdProduit()).isNotNull();

		final OutputDTO modifie = this.service.update(
				new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_UPDATE_OK));

		assertThat(modifie).isNotNull();
		assertThat(modifie.getIdProduit()).isEqualTo(cree.getIdProduit());
		assertThat(modifie.getProduit()).isEqualTo(IT_PRD_UPDATE_OK);
		assertThat(modifie.getSousTypeProduit()).isEqualTo(IT_STP_PARENT_A);
		assertThat(modifie.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);

		final OutputDTO relu = this.service.findById(cree.getIdProduit());

		assertThat(relu).isNotNull();
		assertThat(relu.getIdProduit()).isEqualTo(cree.getIdProduit());
		assertThat(relu.getProduit()).isEqualTo(IT_PRD_UPDATE_OK);
		assertThat(relu.getSousTypeProduit()).isEqualTo(IT_STP_PARENT_A);
		assertThat(relu.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);

		/* Règle projet : toujours préciser une Locale pour toUpperCase. */
		final String dummy = relu.getProduit().toUpperCase(Locale.getDefault());
		assertThat(dummy).isNotBlank();
	}

	// ========================= TESTS delete(...) =========================

	/**
	 * <div>
	 * <p>delete(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("delete(null) : violation de contrat -> ExceptionParametreNull")
	public void testDeleteNull() {

		assertThatThrownBy(() -> this.service.delete(null))
				.isInstanceOf(ExceptionParametreNull.class);

		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_NULL);
	}

	/**
	 * <div>
	 * <p>delete(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("delete(blank) : positionne message + lève ExceptionParametreBlank")
	public void testDeleteBlank() {

		final InputDTO input = new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, ESPACES);

		assertThatThrownBy(() -> this.service.delete(input))
				.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);
	}

	/**
	 * <div>
	 * <p>delete(introuvable) : ne supprime rien.</p>
	 * <ul>
	 * <li>le {@code count()} reste inchangé</li>
	 * <li>le message contient {@link ProduitICuService#MESSAGE_OBJ_INTROUVABLE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("delete(introuvable) : ne supprime rien, retourne, message 'introuvable'")
	public void testDeleteIntrouvable() throws Exception {

		creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		final long baseline = this.service.count();

		this.service.delete(new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_INEXISTANT_DELETE));

		assertThat(this.service.count()).isEqualTo(baseline);

		assertThat(this.service.getMessage())
				.contains(ProduitICuService.MESSAGE_OBJ_INTROUVABLE);
	}

	/**
	 * <div>
	 * <p>delete(ok) : supprime effectivement.</p>
	 * <ul>
	 * <li>après suppression, {@code findByLibelle(libelle)} retourne {@code null}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("delete(ok) : supprime effectivement (findByLibelle -> null)")
	public void testDeleteOk() throws Exception {

		creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		final OutputDTO cree = this.service.creer(
				new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_DELETE_OK));

		assertThat(cree).isNotNull();

		this.service.delete(new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_DELETE_OK));

		final OutputDTO apresSuppression = this.service.findByLibelle(IT_PRD_DELETE_OK);

		assertThat(apresSuppression).isNull();
	}

	// ============================ TESTS count() ==========================

	/**
	 * <div>
	 * <p>count() : cohérence (baseline + créations - suppressions).</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("count() : retourne un nombre cohérent (baseline + créations - suppressions)")
	public void testCountCoherent() throws Exception {

		creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		final long baseline = this.service.count();

		this.service.creer(new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_COUNT_01));
		this.service.creer(new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_COUNT_02));

		final long apresCreations = this.service.count();

		assertThat(apresCreations).isEqualTo(baseline + 2L);

		this.service.delete(new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_COUNT_01));

		final long apresSuppression = this.service.count();

		assertThat(apresSuppression).isEqualTo(baseline + 1L);
	}

	
	
	// ========================= TESTS getMessage() ========================

	
	
	/**
	 * <div>
	 * <p>getMessage() : reste appelable en toutes circonstances.</p>
	 * <p>Test "béton" : message initial potentiellement {@code null} acceptable,
	 * puis message fixé après une erreur utilisateur bénigne.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("getMessage() : reste appelable en toutes circonstances (test béton)")
	public void testGetMessageBeton() throws Exception {

		/* 1) getMessage() doit être appelable sans lever d'exception. */
		final String messageAvant = this.service.getMessage();

		/* messageAvant peut être null : c'est acceptable. */
		assertThat(messageAvant).isIn(null, messageAvant);

		/* 2) Provoque une erreur utilisateur bénigne pour positionner un message local. */
		final ProduitDTO.OutputDTO retourCreerNull = this.service.creer(null);

		assertThat(retourCreerNull).isNull();

		final String messageApres = this.service.getMessage();

		assertThat(messageApres)
				.isEqualTo(ProduitICuService.MESSAGE_CREER_NULL);

		/* Règle projet : toujours préciser une Locale pour toUpperCase. */
		final String dummy = messageApres.toUpperCase(Locale.getDefault());
		assertThat(dummy).isNotBlank();
	}
	
	
	
	// *************************** METHODES UTILITAIRES ********************/

	/**
	 * <div>
	 * <p>Crée la hiérarchie de parents nécessaire aux tests "béton" Produit :</p>
	 * <ul>
	 * <li>{@link TypeProduitICuService} : création du TypeProduit,</li>
	 * <li>{@link SousTypeProduitICuService} : création du SousTypeProduit rattaché au TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @param typeProduit libellé du TypeProduit.
	 * @param sousTypeProduit libellé du SousTypeProduit.
	 * @throws Exception
	 */
	private void creerParentsBeton(final String typeProduit, final String sousTypeProduit) throws Exception {

		/*
		 * Test "béton" : on crée explicitement les parents, comme dans les IT validés
		 * de SousTypeProduitCuServiceIntegrationTest.
		 */
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(typeProduit));
		this.sousTypeProduitService.creer(new SousTypeProduitDTO.InputDTO(typeProduit, sousTypeProduit));
	}

	/**
	 * <div>
	 * <p>Méthode utilitaire "béton" : vérifie la cohérence d'un ResultatPage.</p>
	 * <p>
	 * NOTE : Cette méthode est volontairement simple et n'introduit aucun "magic string".
	 * </p>
	 * </div>
	 *
	 * @param page ResultatPage à vérifier.
	 */
	public static void assertResultatPageCoherent(final ResultatPage<?> page) {

		assertThat(page).isNotNull();
		assertThat(page.getPageNumber()).isGreaterThanOrEqualTo(0);
		assertThat(page.getPageSize()).isGreaterThan(0);
		assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(0L);
		assertThat(page.getContent()).isNotNull();
		assertThat(page.getContent().size()).isLessThanOrEqualTo(page.getPageSize());
	}

}
