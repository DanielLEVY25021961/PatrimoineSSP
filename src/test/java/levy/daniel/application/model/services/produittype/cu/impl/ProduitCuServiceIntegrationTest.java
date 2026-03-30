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
import org.springframework.jdbc.core.JdbcTemplate;
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
	
	/**
	 * "Creer"
	 */
	public static final String TAG_CREER = "Creer";
	
	/**
	 * "SELECT COUNT(*) FROM PRODUITS"
	 */
	public static final String SELECT_COUNT_FROM_PRODUITS
		= "SELECT COUNT(*) FROM PRODUITS";

	/**
	 * "SELECT COUNT(*) FROM PRODUITS WHERE ID_PRODUIT = ?"
	 */
	public static final String SELECT_COUNT_FROM_PRODUITS_BY_ID
		= "SELECT COUNT(*) FROM PRODUITS WHERE ID_PRODUIT = ?";

	/**
	 * "SELECT PRODUIT FROM PRODUITS WHERE ID_PRODUIT = ?"
	 */
	public static final String SELECT_PRODUIT_LIBELLE_BY_ID
		= "SELECT PRODUIT FROM PRODUITS WHERE ID_PRODUIT = ?";

	/**
	 * Parent SousTypeProduit d'un Produit par son ID.
	 */
	public static final String SELECT_PARENT_SOUS_TYPE_BY_ID_PRODUIT
		= "SELECT stp.SOUS_TYPE_PRODUIT "
		+ "FROM SOUS_TYPES_PRODUIT stp "
		+ "INNER JOIN PRODUITS p "
		+ "ON p.SOUS_TYPE_PRODUIT = stp.ID_SOUS_TYPE_PRODUIT "
		+ "WHERE p.ID_PRODUIT = ?";


	// *************************** ATTRIBUTS *******************************/
	
	/**
	 * SERVICE JDBC direct pour preuve BD.
	 */
	@Autowired
	private JdbcTemplate jdbcTemplate;

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
	@Tag(TAG_CREER)
	@Test
	@DisplayName("creer(null) : erreur utilisateur bénigne -> retourne null, message utilisateur, aucune exception")
	public void testCreerNull() throws Exception {

		final OutputDTO dto = this.service.creer(null);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage()).isEqualTo(ProduitICuService.MESSAGE_CREER_NULL);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>creer(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_CREER_NOM_BLANK}</li>
	 * </ul>
	 * </div>
	 */
	@Tag(TAG_CREER)
	@Test
	@DisplayName("creer(blank) : positionne message + lève ExceptionParametreBlank")
	public void testCreerBlank() {

		final InputDTO input = new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, ESPACES);

		assertThatThrownBy(() -> this.service.creer(input))
			.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(this.service.getMessage()).isEqualTo(ProduitICuService.MESSAGE_CREER_NOM_BLANK);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>creer(parent blank) : positionne message 
	 * + lève IllegalStateException.</p>
	 * </div>
	 */
	@Tag(TAG_CREER)
	@Test
	@DisplayName("creer(parent blank) : positionne message + lève IllegalStateException")
	public void testCreerParentBlank() {

		final InputDTO input = new ProduitDTO.InputDTO(IT_TP_PARENT_A, ESPACES, IT_PRD_ALPHA);

		assertThatThrownBy(() -> this.service.creer(input))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(this.service.getMessage()).isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		
	} // __________________________________________________________________
	
	
	
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
	@Tag(TAG_CREER)
	@Test
	@DisplayName("creer(doublon) : positionne message exact + lève ExceptionDoublon")
	public void testCreerDoublon() throws Exception {

		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		final InputDTO input = new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_ALPHA);

		final OutputDTO cree = this.service.creer(input);
		assertThat(cree).isNotNull();

		assertThatThrownBy(() -> this.service.creer(input))
			.isInstanceOf(ExceptionDoublon.class)
			.hasMessage(ProduitICuService.MESSAGE_DOUBLON + IT_PRD_ALPHA);

		assertThat(this.service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_DOUBLON + IT_PRD_ALPHA);
		
	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>creer(ok) : test béton avec comptage et round-trip par ID.</p>
	 * <ul>
	 * <li>crée d'abord la hiérarchie parent persistante requise ;</li>
	 * <li>retourne un {@link OutputDTO} persistant ;</li>
	 * <li>positionne exactement {@link ProduitICuService#MESSAGE_CREER_OK} ;</li>
	 * <li>augmente le comptage de 1 ;</li>
	 * <li>reste retrouvable par {@link ProduitICuService#findById(Long)}
	 * après création.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@Test
	@DisplayName("creer(ok) : message exact + count + round-trip findById")
	public void testCreerOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		final long nombreAvant = this.service.count();

		final InputDTO input
			= new ProduitDTO.InputDTO(
					IT_TP_PARENT_A,
					IT_STP_PARENT_A,
					IT_PRD_BETA);

		/* ======================= ACT ======================= */
		final OutputDTO cree = this.service.creer(input);

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(cree.getIdProduit()).isNotNull();
		assertThat(cree.getProduit()).isEqualTo(IT_PRD_BETA);
		assertThat(cree.getSousTypeProduit()).isEqualTo(IT_STP_PARENT_A);
		assertThat(cree.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);
		assertThat(this.service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_CREER_OK);

		final long nombreApres = this.service.count();

		assertThat(nombreApres).isEqualTo(nombreAvant + 1L);

		final OutputDTO trouveParId = this.service.findById(cree.getIdProduit());

		assertThat(trouveParId).isNotNull();
		assertThat(trouveParId.getIdProduit()).isEqualTo(cree.getIdProduit());
		assertThat(trouveParId.getProduit()).isEqualTo(IT_PRD_BETA);
		assertThat(trouveParId.getSousTypeProduit()).isEqualTo(IT_STP_PARENT_A);
		assertThat(trouveParId.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);

	} // __________________________________________________________________
		
	
	
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

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTous() : scénario nominal béton avec cohérence count.</p>
	 * <ul>
	 * <li>retourne une liste non {@code null}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>reste cohérent avec {@link ProduitICuService#count()}</li>
	 * <li>contient les créations du test</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("rechercherTous(ok) : message exact + cohérence count + présence des créations")
	public void testRechercherTousOkAvecCohherenceCount() throws Exception {

		/* ===================== ARRANGE ===================== */
		creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);
		creerParentsBeton(IT_TP_PARENT_B, IT_STP_PARENT_B);

		final OutputDTO creeGamma = this.service.creer(
				new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_GAMMA));
		final OutputDTO creeDelta = this.service.creer(
				new ProduitDTO.InputDTO(IT_TP_PARENT_B, IT_STP_PARENT_B, IT_PRD_DELTA));

		final long attendu = this.service.count();

		/* ======================= ACT ======================= */
		final List<ProduitDTO.OutputDTO> dtos = this.service.rechercherTous();

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNotNull();
		assertThat(dtos.size()).isEqualTo((int) attendu);

		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.contains(IT_PRD_GAMMA, IT_PRD_DELTA);

		final OutputDTO dtoGamma = dtos.stream()
				.filter(dto -> IT_PRD_GAMMA.equals(dto.getProduit()))
				.findFirst()
				.orElse(null);

		final OutputDTO dtoDelta = dtos.stream()
				.filter(dto -> IT_PRD_DELTA.equals(dto.getProduit()))
				.findFirst()
				.orElse(null);

		assertThat(dtoGamma).isNotNull();
		assertThat(dtoGamma.getIdProduit())
				.isEqualTo(creeGamma.getIdProduit());
		assertThat(dtoGamma.getTypeProduit())
				.isEqualTo(IT_TP_PARENT_A);
		assertThat(dtoGamma.getSousTypeProduit())
				.isEqualTo(IT_STP_PARENT_A);

		assertThat(dtoDelta).isNotNull();
		assertThat(dtoDelta.getIdProduit())
				.isEqualTo(creeDelta.getIdProduit());
		assertThat(dtoDelta.getTypeProduit())
				.isEqualTo(IT_TP_PARENT_B);
		assertThat(dtoDelta.getSousTypeProduit())
				.isEqualTo(IT_STP_PARENT_B);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTous() : stockage vide.</p>
	 * <ul>
	 * <li>retourne une liste vide mais non {@code null}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>reste cohérent avec une base physiquement vide</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Sql(
			scripts = { "classpath:/truncate-test.sql" },
			executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
	)
	@DisplayName("rechercherTous(vide) : liste vide + message MESSAGE_RECHERCHE_VIDE + base vide")
	public void testRechercherTousVide() throws Exception {

		/* ===================== ARRANGE ===================== */
		assertThat(this.service.count()).isEqualTo(0L);

		/* ======================= ACT ======================= */
		final List<ProduitDTO.OutputDTO> dtos = this.service.rechercherTous();

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNotNull();
		assertThat(dtos).isEmpty();

		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________	
	

	
	// ===================== TESTS rechercherTousString() ==================

	
	
	/**
	 * <div>
	 * <p>rechercherTousString() : scénario nominal.</p>
	 * <ul>
	 * <li>crée d'abord la hiérarchie parent persistante requise ;</li>
	 * <li>crée plusieurs Produit en base ;</li>
	 * <li>retourne une liste non {@code null} ;</li>
	 * <li>retourne les libellés créés ;</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_OK}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("rechercherTousString(ok) : retourne les libellés créés + message MESSAGE_RECHERCHE_OK")
	public void testRechercherTousStringOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		this.service.creer(
				new ProduitDTO.InputDTO(
						IT_TP_PARENT_A,
						IT_STP_PARENT_A,
						IT_PRD_GAMMA));

		this.service.creer(
				new ProduitDTO.InputDTO(
						IT_TP_PARENT_A,
						IT_STP_PARENT_A,
						IT_PRD_DELTA));

		/* ======================= ACT ======================= */
		final List<String> retour = this.service.rechercherTousString();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).contains(IT_PRD_GAMMA, IT_PRD_DELTA);
		assertThat(this.service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousString() : stockage vide.</p>
	 * <ul>
	 * <li>retourne une liste vide mais non {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Sql(
			scripts = { "classpath:/truncate-test.sql" },
			executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
	)
	@DisplayName("rechercherTousString(vide) : liste vide + message MESSAGE_RECHERCHE_VIDE")
	public void testRechercherTousStringVide() throws Exception {

		/* ======================= ACT ======================= */
		final List<String> retour = this.service.rechercherTousString();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull().isEmpty();
		assertThat(this.service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________	
	

	
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
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>rechercherTousParPage(ok) : test "béton" sur la cohérence du {@link ResultatPage}.</p>
	 * <ul>
	 * <li>le {@code totalElements} reflète l'état base + créations ;</li>
	 * <li>la page et la taille sont reprises ;</li>
	 * <li>le contenu n'excède pas {@code pageSize} ;</li>
	 * <li>le message observable est exactement
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_PAGINEE_OK}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("rechercherTousParPage(ok) : retourne ResultatPage cohérent (totalElements repris)")
	public void testRechercherTousParPageOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		final long baseline = this.service.count();

		this.service.creer(new ProduitDTO.InputDTO(
				IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_PAGE_01));
		this.service.creer(new ProduitDTO.InputDTO(
				IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_PAGE_02));
		this.service.creer(new ProduitDTO.InputDTO(
				IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_PAGE_03));
		this.service.creer(new ProduitDTO.InputDTO(
				IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_PAGE_04));
		this.service.creer(new ProduitDTO.InputDTO(
				IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_PAGE_05));

		final long attendu = baseline + 5L;
		final RequetePage requete = new RequetePage(0, 2);

		/* ======================= ACT ======================= */
		final ResultatPage<OutputDTO> rp = this.service.rechercherTousParPage(requete);

		/* ===================== ASSERT ====================== */
		assertThat(rp).isNotNull();
		assertResultatPageCoherent(rp);

		assertThat(rp.getPageNumber()).isEqualTo(0);
		assertThat(rp.getPageSize()).isEqualTo(2);
		assertThat(rp.getTotalElements()).isEqualTo(attendu);

		assertThat(rp.getContent()).isNotNull();
		assertThat(rp.getContent().size()).isLessThanOrEqualTo(rp.getPageSize());

		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

	} // __________________________________________________________________
	
	

	// ======================= TESTS findByLibelle(...) ====================

	
	
	/**
	 * <div>
	 * <p>findByLibelle(blank) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>ne lève aucune exception</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelle(blank) : erreur utilisateur bénigne -> retourne null, message utilisateur, aucune exception")
	public void testFindByLibelleBlank() throws Exception {

		final List<OutputDTO> dtos = this.service.findByLibelle(ESPACES);

		assertThat(dtos).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(introuvable) : cas nominal de non-trouvabilité.</p>
	 * <ul>
	 * <li>retourne une liste vide mais non {@code null}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelle(introuvable) : retourne liste vide + message MESSAGE_RECHERCHE_VIDE")
	public void testFindByLibelleIntrouvable() throws Exception {

		final List<OutputDTO> dtos = this.service.findByLibelle(IT_PRD_INEXISTANT_XYZ);

		assertThat(dtos).isNotNull().isEmpty();
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(ok) : retourne tous les DTO correspondant exactement au libellé.</p>
	 * <ul>
	 * <li>crée deux parents persistants distincts ;</li>
	 * <li>crée deux Produits de même libellé sous deux parents différents ;</li>
	 * <li>retourne une liste non nulle de taille 2 ;</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_RECHERCHE_OK}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelle(ok) : retourne la liste des OutputDTO de même libellé exact")
	public void testFindByLibelleOk() throws Exception {

		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);
		this.creerParentsBeton(IT_TP_PARENT_B, IT_STP_PARENT_B);

		this.service.creer(
				new ProduitDTO.InputDTO(
						IT_TP_PARENT_A,
						IT_STP_PARENT_A,
						IT_PRD_DELTA));

		this.service.creer(
				new ProduitDTO.InputDTO(
						IT_TP_PARENT_B,
						IT_STP_PARENT_B,
						IT_PRD_DELTA));

		final List<OutputDTO> dtos = this.service.findByLibelle(IT_PRD_DELTA);

		assertThat(dtos).isNotNull();
		assertThat(dtos).hasSize(2);
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.containsExactlyInAnyOrder(IT_PRD_DELTA, IT_PRD_DELTA);

		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getSousTypeProduit)
				.containsExactlyInAnyOrder(IT_STP_PARENT_A, IT_STP_PARENT_B);

		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getTypeProduit)
				.containsExactlyInAnyOrder(IT_TP_PARENT_A, IT_TP_PARENT_B);

	} // __________________________________________________________________	
	
	

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

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelleRapide(blank) : délègue au comportement rechercherTous().</p>
	 * <ul>
	 * <li>retourne une liste non nulle ;</li>
	 * <li>retourne les créations du test ;</li>
	 * <li>positionne le message observable de rechercherTous().</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelleRapide(blank) : délègue à rechercherTous et retourne une liste non nulle")
	public void testFindByLibelleRapideBlank() throws Exception {

		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		this.service.creer(new ProduitDTO.InputDTO(
				IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_GAMMA));
		this.service.creer(new ProduitDTO.InputDTO(
				IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_DELTA));

		final List<OutputDTO> dtos = this.service.findByLibelleRapide(ESPACES);

		assertThat(dtos).isNotNull();
		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.contains(IT_PRD_GAMMA, IT_PRD_DELTA);

		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelleRapide(introuvable) : cas nominal de non-trouvabilité.</p>
	 * <ul>
	 * <li>retourne une liste vide mais non {@code null} ;</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelleRapide(introuvable) : retourne liste vide + message MESSAGE_RECHERCHE_VIDE")
	public void testFindByLibelleRapideIntrouvable() throws Exception {

		final List<OutputDTO> dtos = this.service.findByLibelleRapide(IT_PRD_SEARCH_PREFIXE_QQ);

		assertThat(dtos).isNotNull().isEmpty();
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelleRapide(ok) : retourne tous les DTO correspondant au préfixe demandé.</p>
	 * <ul>
	 * <li>crée le parent persistant requis ;</li>
	 * <li>crée deux Produits correspondant au préfixe ;</li>
	 * <li>retourne une liste non nulle ;</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_RECHERCHE_OK}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelleRapide(ok) : retourne la liste des OutputDTO correspondant au préfixe")
	public void testFindByLibelleRapideOk() throws Exception {

		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		this.service.creer(new ProduitDTO.InputDTO(
				IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_SEARCH_ABC));
		this.service.creer(new ProduitDTO.InputDTO(
				IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_SEARCH_ABD));
		this.service.creer(new ProduitDTO.InputDTO(
				IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_ZETA));

		final List<OutputDTO> dtos = this.service.findByLibelleRapide(IT_PRD_SEARCH_PREFIXE_AB);

		assertThat(dtos).isNotNull();
		assertThat(dtos).hasSize(2);
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.containsExactlyInAnyOrder(IT_PRD_SEARCH_ABC, IT_PRD_SEARCH_ABD);

	} // __________________________________________________________________	
		
	

	// ===================== TESTS findAllByParent(...) ====================

	
	
	/**
	 * <div>
	 * <p>findAllByParent(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève une exception ;</li>
	 * <li>positionne {@link ProduitICuService#RECHERCHE_SOUSTYPEPRODUIT_NULL}.</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("findAllByParent(null) : positionne message + lève RuntimeException")
	public void testFindAllByParentNull() {

		assertThatThrownBy(() -> this.service.findAllByParent(null))
				.isInstanceOf(RuntimeException.class);

		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.RECHERCHE_SOUSTYPEPRODUIT_NULL);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(parent absent) : violation de contrat.</p>
	 * <ul>
	 * <li>lève une exception ;</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_PAS_PARENT}.</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("findAllByParent(parent absent) : positionne message + lève exception")
	public void testFindAllByParentPasParent() {

		final SousTypeProduitDTO.InputDTO parentDto
			= new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_B, IT_STP_PARENT_B);

		assertThatThrownBy(() -> this.service.findAllByParent(parentDto))
				.isInstanceOfAny(RuntimeException.class, IllegalStateException.class);

		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(introuvable) : aucun Produit rattaché au parent.</p>
	 * <ul>
	 * <li>retourne une liste vide mais non {@code null} ;</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findAllByParent(introuvable) : retourne liste vide + message MESSAGE_RECHERCHE_VIDE")
	public void testFindAllByParentIntrouvable() throws Exception {

		this.creerParentsBeton(IT_TP_PARENT_B, IT_STP_PARENT_B);

		final List<OutputDTO> dtos = this.service.findAllByParent(
				new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_B, IT_STP_PARENT_B));

		assertThat(dtos).isNotNull().isEmpty();
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(ok) : retourne tous les Produits du parent demandé.</p>
	 * <ul>
	 * <li>crée d'abord le parent persistant requis ;</li>
	 * <li>crée plusieurs Produits sous ce parent ;</li>
	 * <li>retourne une liste non nulle ;</li>
	 * <li>retourne uniquement les Produits de ce parent ;</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_RECHERCHE_OK}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findAllByParent(ok) : retourne la liste non nulle des créations du parent")
	public void testFindAllByParentOk() throws Exception {

		this.creerParentsBeton(IT_TP_PARENT_B, IT_STP_PARENT_B);

		this.service.creer(new ProduitDTO.InputDTO(
				IT_TP_PARENT_B, IT_STP_PARENT_B, IT_PRD_GAMMA));
		this.service.creer(new ProduitDTO.InputDTO(
				IT_TP_PARENT_B, IT_STP_PARENT_B, IT_PRD_DELTA));

		final List<OutputDTO> dtos = this.service.findAllByParent(
				new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_B, IT_STP_PARENT_B));

		assertThat(dtos).isNotNull();
		assertThat(dtos).hasSize(2);
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.containsExactlyInAnyOrder(IT_PRD_GAMMA, IT_PRD_DELTA);

		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getSousTypeProduit)
				.containsExactlyInAnyOrder(IT_STP_PARENT_B, IT_STP_PARENT_B);

		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getTypeProduit)
				.containsExactlyInAnyOrder(IT_TP_PARENT_B, IT_TP_PARENT_B);

	} // __________________________________________________________________
	
	
	
	// ========================== TESTS findByDTO(...) =====================

	
	
	/**
	 * <div>
	 * <p>findByDTO(null) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_RECHERCHE_OBJ_NULL}</li>
	 * <li>ne lève aucune exception</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(null) : retourne null + message MESSAGE_RECHERCHE_OBJ_NULL")
	public void testFindByDTONull() throws Exception {

		final OutputDTO dto = this.service.findByDTO(null);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OBJ_NULL);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByDTO(parent blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("findByDTO(parent blank) : positionne message + lève IllegalStateException")
	public void testFindByDTOParentBlank() {

		final InputDTO dto = new ProduitDTO.InputDTO(
				IT_TP_PARENT_A,
				ESPACES,
				IT_PRD_ALPHA);

		assertThatThrownBy(() -> this.service.findByDTO(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByDTO(introuvable) : aucun Produit exact trouvé.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(introuvable) : retourne null + message MESSAGE_RECHERCHE_VIDE")
	public void testFindByDTOIntrouvable() throws Exception {

		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		this.service.creer(new ProduitDTO.InputDTO(
				IT_TP_PARENT_A,
				IT_STP_PARENT_A,
				IT_PRD_ALPHA));

		final InputDTO input = new ProduitDTO.InputDTO(
				IT_TP_PARENT_A,
				IT_STP_PARENT_A,
				IT_PRD_BETA);

		final OutputDTO dto = this.service.findByDTO(input);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByDTO(ok) : retourne l'OutputDTO exact correspondant au DTO de recherche.</p>
	 * <ul>
	 * <li>crée d'abord la hiérarchie parent persistante requise ;</li>
	 * <li>crée un Produit persistant ;</li>
	 * <li>retrouve ce Produit via le DTO de recherche ;</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_SUCCES_RECHERCHE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(ok) : retourne OutputDTO cohérent + message MESSAGE_SUCCES_RECHERCHE")
	public void testFindByDTOOk() throws Exception {

		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		final OutputDTO cree = this.service.creer(new ProduitDTO.InputDTO(
				IT_TP_PARENT_A,
				IT_STP_PARENT_A,
				IT_PRD_ALPHA));

		assertThat(cree).isNotNull();

		final InputDTO input = new ProduitDTO.InputDTO(
				IT_TP_PARENT_A,
				IT_STP_PARENT_A,
				IT_PRD_ALPHA);

		final OutputDTO dto = this.service.findByDTO(input);

		assertThat(dto).isNotNull();
		assertThat(dto.getIdProduit()).isEqualTo(cree.getIdProduit());
		assertThat(dto.getProduit()).isEqualTo(IT_PRD_ALPHA);
		assertThat(dto.getSousTypeProduit()).isEqualTo(IT_STP_PARENT_A);
		assertThat(dto.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_SUCCES_RECHERCHE);

	} // __________________________________________________________________	

	
	
	// ======================== TESTS findById(...) ========================

	
	
	/**
	 * <div>
	 * <p>findById(null) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>ne lève aucune exception</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findById(null) : retourne null + message MESSAGE_PARAM_NULL")
	public void testFindByIdNull() throws Exception {

		final OutputDTO dto = this.service.findById(null);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_NULL);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findById(introuvable) : cas nominal de non-trouvabilité.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_OBJ_INTROUVABLE} + id</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findById(introuvable) : retourne null + message exact MESSAGE_OBJ_INTROUVABLE + id")
	public void testFindByIdIntrouvable() throws Exception {

		final Long idInexistant = Long.valueOf(Long.MAX_VALUE);

		final OutputDTO dto = this.service.findById(idInexistant);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_OBJ_INTROUVABLE + idInexistant);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findById(ok) : round-trip création puis lecture par ID.</p>
	 * <ul>
	 * <li>crée d'abord la hiérarchie parent persistante requise ;</li>
	 * <li>retourne l'OutputDTO correspondant à l'identifiant demandé ;</li>
	 * <li>positionne exactement {@link ProduitICuService#MESSAGE_SUCCES_RECHERCHE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findById(ok) : retourne OutputDTO exact + message MESSAGE_SUCCES_RECHERCHE")
	public void testFindByIdOk() throws Exception {

		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		final OutputDTO cree = this.service.creer(
				new ProduitDTO.InputDTO(
						IT_TP_PARENT_A,
						IT_STP_PARENT_A,
						IT_PRD_GAMMA));

		assertThat(cree).isNotNull();
		assertThat(cree.getIdProduit()).isNotNull();

		final OutputDTO relu = this.service.findById(cree.getIdProduit());

		assertThat(relu).isNotNull();
		assertThat(relu.getIdProduit()).isEqualTo(cree.getIdProduit());
		assertThat(relu.getProduit()).isEqualTo(IT_PRD_GAMMA);
		assertThat(relu.getSousTypeProduit()).isEqualTo(IT_STP_PARENT_A);
		assertThat(relu.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_SUCCES_RECHERCHE);

	} // __________________________________________________________________
	
	

	// ========================= TESTS update(...) =========================

	
	
	/**
	 * <div>
	 * <p>update(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'écrit rien en base</li>
	 * </ul>
	 * </div>
	 * @throws Exception 
	 */
	@Test
	@DisplayName("update(null) : ExceptionParametreNull + message exact MESSAGE_PARAM_NULL + aucune écriture BD")
	public void testUpdateNull() throws Exception {

		final long nombreAvant = this.service.count();

		assertThatThrownBy(() -> this.service.update(null))
			.isInstanceOf(ExceptionParametreNull.class);

		final long nombreApres = this.service.count();

		assertThat(this.service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_PARAM_NULL);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>n'écrit rien en base</li>
	 * </ul>
	 * </div>
	 * @throws Exception 
	 */
	@Test
	@DisplayName("update(blank) : ExceptionParametreBlank + message exact MESSAGE_PARAM_BLANK + aucune écriture BD")
	public void testUpdateBlank() throws Exception {

		final long nombreAvant = this.service.count();

		final InputDTO input = new ProduitDTO.InputDTO(
				IT_TP_PARENT_A,
				IT_STP_PARENT_A,
				ESPACES);

		assertThatThrownBy(() -> this.service.update(input))
			.isInstanceOf(ExceptionParametreBlank.class);

		final long nombreApres = this.service.count();

		assertThat(this.service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(parent blank) : violation de contrat structurel.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'écrit rien en base</li>
	 * </ul>
	 * </div>
	 * @throws Exception 
	 */
	@Test
	@DisplayName("update(parent blank) : IllegalStateException + message exact MESSAGE_PAS_PARENT + aucune écriture BD")
	public void testUpdateParentBlank() throws Exception {

		final long nombreAvant = this.service.count();

		final InputDTO input = new ProduitDTO.InputDTO(
				IT_TP_PARENT_A,
				ESPACES,
				IT_PRD_UPDATE_OK);

		assertThatThrownBy(() -> this.service.update(input))
			.isInstanceOf(IllegalStateException.class);

		final long nombreApres = this.service.count();

		assertThat(this.service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(parent absent) : le parent requis n'existe pas en stockage.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'écrit rien en base</li>
	 * </ul>
	 * </div>
	 * @throws Exception 
	 */
	@Test
	@DisplayName("update(parent absent) : IllegalStateException + message exact MESSAGE_PAS_PARENT + aucune écriture BD")
	public void testUpdateParentAbsent() throws Exception {

		final long nombreAvant = this.service.count();

		final InputDTO input = new ProduitDTO.InputDTO(
				IT_TP_PARENT_A,
				IT_STP_PARENT_A,
				IT_PRD_UPDATE_OK);

		assertThatThrownBy(() -> this.service.update(input))
			.isInstanceOf(IllegalStateException.class);

		final long nombreApres = this.service.count();

		assertThat(this.service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(introuvable) : aucun objet persistant
	 * ne correspond au couple [parent, libellé].</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_OBJ_INTROUVABLE} + libellé</li>
	 * <li>ne crée aucune ligne en base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(introuvable) : null + message exact MESSAGE_OBJ_INTROUVABLE + libellé + aucune création BD")
	public void testUpdateIntrouvable() throws Exception {

		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		final long nombreAvant = this.service.count();

		final InputDTO input = new ProduitDTO.InputDTO(
				IT_TP_PARENT_A,
				IT_STP_PARENT_A,
				IT_PRD_INEXISTANT_UPDATE);

		final OutputDTO dto = this.service.update(input);

		final long nombreApres = this.service.count();

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_OBJ_INTROUVABLE + IT_PRD_INEXISTANT_UPDATE);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(ok) : preuve BD directe avec JdbcTemplate.</p>
	 * <ul>
	 * <li>crée d'abord le parent persistant requis ;</li>
	 * <li>crée un Produit réel ;</li>
	 * <li>modifie ce même objet sans créer de doublon ;</li>
	 * <li>conserve exactement le même identifiant persistant ;</li>
	 * <li>prouve physiquement en base que la ligne existe toujours ;</li>
	 * <li>prouve physiquement que le parent BD reste identique ;</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_MODIF_OK} + parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(ok) : preuve BD directe + ID conservé + message exact")
	public void testUpdateOkAvecPreuveBdEtJdbcTemplate() throws Exception {

		/* ===================== ARRANGE ===================== */
		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		final OutputDTO cree = this.service.creer(
				new ProduitDTO.InputDTO(
						IT_TP_PARENT_A,
						IT_STP_PARENT_A,
						IT_PRD_UPDATE_OK));

		assertThat(cree).isNotNull();
		assertThat(cree.getIdProduit()).isNotNull();

		final Long idAvantUpdate = cree.getIdProduit();

		final long totalAvantUpdate = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		/* ======================= ACT ======================= */
		final OutputDTO modifie = this.service.update(
				new ProduitDTO.InputDTO(
						IT_TP_PARENT_A,
						IT_STP_PARENT_A,
						IT_PRD_UPDATE_OK));

		/* ===================== ASSERT ====================== */
		assertThat(modifie).isNotNull();
		assertThat(modifie.getIdProduit()).isEqualTo(idAvantUpdate);
		assertThat(modifie.getProduit()).isEqualTo(IT_PRD_UPDATE_OK);
		assertThat(modifie.getSousTypeProduit()).isEqualTo(IT_STP_PARENT_A);
		assertThat(modifie.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);

		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_MODIF_OK + IT_PRD_UPDATE_OK);

		final long totalApresUpdate = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(totalApresUpdate).isEqualTo(totalAvantUpdate);

		assertThat(this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS_BY_ID,
				Long.class,
				idAvantUpdate)).isEqualTo(1L);

		assertThat(this.jdbcTemplate.queryForObject(
				SELECT_PRODUIT_LIBELLE_BY_ID,
				String.class,
				idAvantUpdate)).isEqualTo(IT_PRD_UPDATE_OK);

		assertThat(this.jdbcTemplate.queryForObject(
				SELECT_PARENT_SOUS_TYPE_BY_ID_PRODUIT,
				String.class,
				idAvantUpdate)).isEqualTo(IT_STP_PARENT_A);

		final OutputDTO relu = this.service.findById(idAvantUpdate);

		assertThat(relu).isNotNull();
		assertThat(relu.getIdProduit()).isEqualTo(idAvantUpdate);
		assertThat(relu.getProduit()).isEqualTo(IT_PRD_UPDATE_OK);
		assertThat(relu.getSousTypeProduit()).isEqualTo(IT_STP_PARENT_A);
		assertThat(relu.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);

	} // __________________________________________________________________
	
	

	// ========================= TESTS delete(...) =========================

	
	
	/**
	 * <div>
	 * <p>delete(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'écrit rien en base</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("delete(null) : ExceptionParametreNull + message exact MESSAGE_PARAM_NULL + aucune écriture BD")
	public void testDeleteNull() {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThatThrownBy(() -> this.service.delete(null))
				.isInstanceOf(ExceptionParametreNull.class);

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_NULL);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>n'écrit rien en base</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("delete(blank) : ExceptionParametreBlank + message exact MESSAGE_PARAM_BLANK + aucune écriture BD")
	public void testDeleteBlank() {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		final InputDTO input =
				new ProduitDTO.InputDTO(
						IT_TP_PARENT_A,
						IT_STP_PARENT_A,
						ESPACES);

		assertThatThrownBy(() -> this.service.delete(input))
				.isInstanceOf(ExceptionParametreBlank.class);

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(parent blank) : violation de contrat structurel.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'écrit rien en base</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("delete(parent blank) : IllegalStateException + message exact MESSAGE_PAS_PARENT + aucune écriture BD")
	public void testDeleteParentBlank() {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		final InputDTO input =
				new ProduitDTO.InputDTO(
						ESPACES,
						IT_STP_PARENT_A,
						IT_PRD_DELETE_OK);

		assertThatThrownBy(() -> this.service.delete(input))
				.isInstanceOf(IllegalStateException.class);

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(parent absent) : le parent requis
	 * n'existe pas en stockage.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'écrit rien en base</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("delete(parent absent) : IllegalStateException + message exact MESSAGE_PAS_PARENT + aucune écriture BD")
	public void testDeleteParentAbsent() {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThatThrownBy(() -> this.service.delete(
				new ProduitDTO.InputDTO(
						IT_TP_PARENT_A,
						IT_STP_PARENT_A,
						IT_PRD_DELETE_OK)))
				.isInstanceOf(IllegalStateException.class);

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(introuvable) : aucun objet persistant
	 * ne correspond au couple [parent, libellé].</p>
	 * <ul>
	 * <li>ne supprime rien physiquement en base</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_OBJ_INTROUVABLE}
	 * + libellé</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("delete(introuvable) : aucune suppression + message exact MESSAGE_OBJ_INTROUVABLE + libellé")
	public void testDeleteIntrouvable() throws Exception {

		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		final Long nombreAvantDelete = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		this.service.delete(
				new ProduitDTO.InputDTO(
						IT_TP_PARENT_A,
						IT_STP_PARENT_A,
						IT_PRD_INEXISTANT_DELETE));

		final Long nombreApresDelete = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(
						ProduitICuService.MESSAGE_OBJ_INTROUVABLE
								+ IT_PRD_INEXISTANT_DELETE);
		assertThat(nombreApresDelete).isEqualTo(nombreAvantDelete);
		assertThat(this.compterProduitParCoupleEnBase(
				IT_TP_PARENT_A,
				IT_STP_PARENT_A,
				IT_PRD_INEXISTANT_DELETE))
				.isEqualTo(0L);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(ok) : preuve béton de la destruction
	 * sur le couple [parent, libellé].</p>
	 * <ul>
	 * <li>crée d'abord deux hiérarchies de parents réelles</li>
	 * <li>crée ensuite deux Produits portant le même libellé
	 * sur deux couples parents différents</li>
	 * <li>détruit uniquement le couple ciblé</li>
	 * <li>ne détruit jamais le couple homonyme
	 * rattaché à l'autre parent</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_DELETE_OK} + libellé</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("delete(ok) : détruit le bon couple [parent, libellé] + message exact + preuve BD")
	public void testDeleteOkAvecPreuveCoupleParentLibelle() throws Exception {

		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);
		this.creerParentsBeton(IT_TP_PARENT_B, IT_STP_PARENT_B);

		final OutputDTO creeParentA = this.service.creer(
				new ProduitDTO.InputDTO(
						IT_TP_PARENT_A,
						IT_STP_PARENT_A,
						IT_PRD_DELETE_OK));

		final OutputDTO creeParentB = this.service.creer(
				new ProduitDTO.InputDTO(
						IT_TP_PARENT_B,
						IT_STP_PARENT_B,
						IT_PRD_DELETE_OK));

		assertThat(creeParentA).isNotNull();
		assertThat(creeParentB).isNotNull();
		assertThat(creeParentA.getIdProduit()).isNotNull();
		assertThat(creeParentB.getIdProduit()).isNotNull();

		final Long idCibleParentB = creeParentB.getIdProduit();

		assertThat(this.compterProduitParCoupleEnBase(
				IT_TP_PARENT_A,
				IT_STP_PARENT_A,
				IT_PRD_DELETE_OK))
				.isEqualTo(1L);
		assertThat(this.compterProduitParCoupleEnBase(
				IT_TP_PARENT_B,
				IT_STP_PARENT_B,
				IT_PRD_DELETE_OK))
				.isEqualTo(1L);

		final Long nombreAvantDelete = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		this.service.delete(
				new ProduitDTO.InputDTO(
						IT_TP_PARENT_B,
						IT_STP_PARENT_B,
						IT_PRD_DELETE_OK));

		final Long nombreApresDelete = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(
						ProduitICuService.MESSAGE_DELETE_OK
								+ IT_PRD_DELETE_OK);

		assertThat(nombreApresDelete).isEqualTo(nombreAvantDelete - 1L);

		assertThat(this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS_BY_ID,
				Long.class,
				idCibleParentB)).isEqualTo(0L);

		assertThat(this.compterProduitParCoupleEnBase(
				IT_TP_PARENT_A,
				IT_STP_PARENT_A,
				IT_PRD_DELETE_OK))
				.isEqualTo(1L);

		assertThat(this.compterProduitParCoupleEnBase(
				IT_TP_PARENT_B,
				IT_STP_PARENT_B,
				IT_PRD_DELETE_OK))
				.isEqualTo(0L);

	} // __________________________________________________________________	
	

	
	// ============================ TESTS count() ==========================

	
	
	/**
	 * <div>
	 * <p>count() : retourne le comptage réel de la base
	 * et positionne le message observable correspondant.</p>
	 * <ul>
	 * <li>compare le résultat UC
	 * au {@code SELECT COUNT(*)} physique</li>
	 * <li>vérifie le message exact :
	 * vide si {@code 0},
	 * succès si strictement positif</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("count() : résultat identique au COUNT(*) physique + message observable exact")
	public void testCountRetourneLeNombrePhysiqueEtLeMessageObservable()
			throws Exception {

		final Long nombrePhysique = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		final long retourUc = this.service.count();

		assertThat(retourUc).isEqualTo(nombrePhysique.longValue());

		if (retourUc == 0L) {
			assertThat(this.service.getMessage())
					.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);
		} else {
			assertThat(this.service.getMessage())
					.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);
		}

	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>count() : cohérence renforcée
	 * avant créations, après créations,
	 * puis après suppressions de nettoyage.</p>
	 * <ul>
	 * <li>prouve la hausse exacte après deux créations</li>
	 * <li>prouve le retour exact au niveau initial
	 * après suppression des deux objets créés</li>
	 * <li>vérifie à chaque étape
	 * le message observable exact</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("count() : cohérence complète + messages exacts avant/après créations puis nettoyage")
	public void testCountCoherentAvecMessagesAvantApresCreationsPuisNettoyage()
			throws Exception {

		final long baseline = this.service.count();

		if (baseline == 0L) {
			assertThat(this.service.getMessage())
					.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);
		} else {
			assertThat(this.service.getMessage())
					.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);
		}

		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);

		this.service.creer(
				new ProduitDTO.InputDTO(
						IT_TP_PARENT_A,
						IT_STP_PARENT_A,
						IT_PRD_COUNT_01));

		this.service.creer(
				new ProduitDTO.InputDTO(
						IT_TP_PARENT_A,
						IT_STP_PARENT_A,
						IT_PRD_COUNT_02));

		final long apresCreations = this.service.count();

		assertThat(apresCreations).isEqualTo(baseline + 2L);
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

		this.service.delete(
				new ProduitDTO.InputDTO(
						IT_TP_PARENT_A,
						IT_STP_PARENT_A,
						IT_PRD_COUNT_01));

		this.service.delete(
				new ProduitDTO.InputDTO(
						IT_TP_PARENT_A,
						IT_STP_PARENT_A,
						IT_PRD_COUNT_02));

		final long apresNettoyage = this.service.count();

		assertThat(apresNettoyage).isEqualTo(baseline);

		if (apresNettoyage == 0L) {
			assertThat(this.service.getMessage())
					.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);
		} else {
			assertThat(this.service.getMessage())
					.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);
		}

		final Long nombrePhysiqueFinal = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(apresNettoyage).isEqualTo(nombrePhysiqueFinal.longValue());

	} // __________________________________________________________________
	
	
	
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
		
	} // __________________________________________________________________
	
	
	
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
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>Compte le nombre de lignes physiques pour un triplet
	 * [type parent, sous-type parent, produit].</p>
	 * </div>
	 *
	 * @param pTypeProduit : String : libellé du TypeProduit parent.
	 * @param pSousTypeProduit : String : libellé du SousTypeProduit parent.
	 * @param pProduit : String : libellé exact du Produit.
	 * @return Long : nombre de lignes trouvées pour ce triplet.
	 */
	private Long compterProduitParCoupleEnBase(
			final String pTypeProduit,
			final String pSousTypeProduit,
			final String pProduit) {

		return this.jdbcTemplate.queryForObject(
				"SELECT COUNT(*) "
				+ "FROM PRODUITS p "
				+ "INNER JOIN SOUS_TYPES_PRODUIT stp "
				+ "ON p.SOUS_TYPE_PRODUIT = stp.ID_SOUS_TYPE_PRODUIT "
				+ "INNER JOIN TYPES_PRODUIT tp "
				+ "ON stp.TYPE_PRODUIT = tp.ID_TYPE_PRODUIT "
				+ "WHERE tp.TYPE_PRODUIT = ? "
				+ "AND stp.SOUS_TYPE_PRODUIT = ? "
				+ "AND p.PRODUIT = ?",
				Long.class,
				pTypeProduit,
				pSousTypeProduit,
				pProduit);

	} // __________________________________________________________________

	

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
		
	} // __________________________________________________________________
	
	

}
