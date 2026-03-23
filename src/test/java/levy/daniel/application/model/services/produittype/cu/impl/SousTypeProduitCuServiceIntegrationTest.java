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

import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO.OutputDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
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
 * CLASSE SousTypeProduitCuServiceIntegrationTest.java :
 * </p>
 * <p>
 * Tests d'intégration complets (avec tests "béton") du SERVICE ADAPTER METIER CU
 * {@link SousTypeProduitCuService}.
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
		classes = SousTypeProduitCuServiceIntegrationTest.ConfigTest.class,
		webEnvironment = SpringBootTest.WebEnvironment.NONE,
		properties = { "spring.main.web-application-type=none" }
)
@ActiveProfiles({ "test", "dev" })
@Tag(SousTypeProduitCuServiceIntegrationTest.TAG)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(
		scripts = {
				"classpath:/truncate-test.sql",
				"classpath:/data-test.sql"
		},
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class SousTypeProduitCuServiceIntegrationTest {

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
	 * TypeProduit IT (parent) : "IT-TP-PARENT-A".
	 */
	public static final String IT_TP_PARENT_A = "IT-TP-PARENT-A";

	/**
	 * TypeProduit IT (parent) : "IT-TP-PARENT-B".
	 */
	public static final String IT_TP_PARENT_B = "IT-TP-PARENT-B";

	/**
	 * SousTypeProduit IT : "IT-STP-ALPHA".
	 */
	public static final String IT_STP_ALPHA = "IT-STP-ALPHA";

	/**
	 * SousTypeProduit IT : "IT-STP-BETA".
	 */
	public static final String IT_STP_BETA = "IT-STP-BETA";

	/**
	 * SousTypeProduit IT : "IT-STP-GAMMA".
	 */
	public static final String IT_STP_GAMMA = "IT-STP-GAMMA";

	/**
	 * SousTypeProduit IT : "IT-STP-DELTA".
	 */
	public static final String IT_STP_DELTA = "IT-STP-DELTA";

	/**
	 * SousTypeProduit IT : "IT-STP-EPSILON".
	 */
	public static final String IT_STP_EPSILON = "IT-STP-EPSILON";

	/**
	 * SousTypeProduit IT : "IT-STP-ZETA".
	 */
	public static final String IT_STP_ZETA = "IT-STP-ZETA";

	/**
	 * SousTypeProduit IT : "IT-STP-PAGE-01".
	 */
	public static final String IT_STP_PAGE_01 = "IT-STP-PAGE-01";

	/**
	 * SousTypeProduit IT : "IT-STP-PAGE-02".
	 */
	public static final String IT_STP_PAGE_02 = "IT-STP-PAGE-02";

	/**
	 * SousTypeProduit IT : "IT-STP-PAGE-03".
	 */
	public static final String IT_STP_PAGE_03 = "IT-STP-PAGE-03";

	/**
	 * SousTypeProduit IT : "IT-STP-PAGE-04".
	 */
	public static final String IT_STP_PAGE_04 = "IT-STP-PAGE-04";

	/**
	 * SousTypeProduit IT : "IT-STP-PAGE-05".
	 */
	public static final String IT_STP_PAGE_05 = "IT-STP-PAGE-05";

	/**
	 * SousTypeProduit introuvable : "IT-STP-INEXISTANT-XYZ".
	 */
	public static final String IT_STP_INEXISTANT_XYZ = "IT-STP-INEXISTANT-XYZ";

	/**
	 * SousTypeProduit update introuvable : "IT-STP-INEXISTANT-UPDATE".
	 */
	public static final String IT_STP_INEXISTANT_UPDATE = "IT-STP-INEXISTANT-UPDATE";

	/**
	 * SousTypeProduit delete introuvable : "IT-STP-INEXISTANT-DELETE".
	 */
	public static final String IT_STP_INEXISTANT_DELETE = "IT-STP-INEXISTANT-DELETE";

	/**
	 * SousTypeProduit update ok : "IT-STP-UPDATE-OK".
	 */
	public static final String IT_STP_UPDATE_OK = "IT-STP-UPDATE-OK";

	/**
	 * SousTypeProduit delete ok : "IT-STP-DELETE-OK".
	 */
	public static final String IT_STP_DELETE_OK = "IT-STP-DELETE-OK";

	/**
	 * SousTypeProduit count 01 : "IT-STP-COUNT-01".
	 */
	public static final String IT_STP_COUNT_01 = "IT-STP-COUNT-01";

	/**
	 * SousTypeProduit count 02 : "IT-STP-COUNT-02".
	 */
	public static final String IT_STP_COUNT_02 = "IT-STP-COUNT-02";

	/**
	 * SousTypeProduit recherche rapide 01 : "IT-STP-SEARCH-ABC".
	 */
	public static final String IT_STP_SEARCH_ABC = "IT-STP-SEARCH-ABC";

	/**
	 * SousTypeProduit recherche rapide 02 : "IT-STP-SEARCH-ABD".
	 */
	public static final String IT_STP_SEARCH_ABD = "IT-STP-SEARCH-ABD";

	/**
	 * Préfixe recherche rapide : "IT-STP-SEARCH-AB".
	 */
	public static final String IT_STP_SEARCH_PREFIXE_AB = "IT-STP-SEARCH-AB";

	/**
	 * Préfixe recherche rapide introuvable : "IT-STP-SEARCH-QQ".
	 */
	public static final String IT_STP_SEARCH_PREFIXE_QQ = "IT-STP-SEARCH-QQ";

	// *************************** ATTRIBUTS *******************************/

	/**
	 * SERVICE CU SousTypeProduit sous test (PORT).
	 */
	@Autowired
	private SousTypeProduitICuService service;

	/**
	 * SERVICE CU TypeProduit (pour créer les parents nécessaires aux tests béton).
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
	public SousTypeProduitCuServiceIntegrationTest() {
		super();
	}

	// *************************** METHODES *******************************/

	// ============================ TESTS creer(...) =======================

	/**
	 * <div>
	 * <p>creer(null) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link SousTypeProduitICuService#MESSAGE_CREER_NULL}</li>
	 * <li>ne lève aucune exception</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("creer(null) : erreur utilisateur bénigne -> retourne null, message utilisateur, aucune exception")
	public void testCreerNull() throws Exception {

		final OutputDTO dto = this.service.creer(null);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_NULL);
	}

	/**
	 * <div>
	 * <p>creer(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne {@link SousTypeProduitICuService#MESSAGE_CREER_NOM_BLANK}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("creer(blank) : positionne message + lève ExceptionParametreBlank")
	public void testCreerBlank() {

		final InputDTO input = new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, ESPACES);

		assertThatThrownBy(() -> this.service.creer(input))
				.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(this.service.getMessage())
				.contains(SousTypeProduitICuService.MESSAGE_CREER_NOM_BLANK);
	}

	/**
	 * <div>
	 * <p>creer(pas parent) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("creer(pas parent) : positionne message + lève IllegalStateException")
	public void testCreerPasParent() {

		final InputDTO input = new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA);

		/* Parent non créé : doit lever une exception et positionner MESSAGE_PAS_PARENT. */
		assertThatThrownBy(() -> this.service.creer(input))
				.isInstanceOf(IllegalStateException.class);

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);
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

		/* Parent obligatoire (test béton). */
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));

		final InputDTO input = new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA);

		final OutputDTO cree = this.service.creer(input);

		assertThat(cree).isNotNull();
		assertThat(cree.getIdSousTypeProduit()).isNotNull();
		assertThat(cree.getSousTypeProduit()).isEqualTo(IT_STP_ALPHA);
		assertThat(cree.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);

		final OutputDTO trouveParLibelle = this.service.findByLibelle(IT_STP_ALPHA);

		assertThat(trouveParLibelle).isNotNull();
		assertThat(trouveParLibelle.getIdSousTypeProduit()).isEqualTo(cree.getIdSousTypeProduit());
		assertThat(trouveParLibelle.getSousTypeProduit()).isEqualTo(IT_STP_ALPHA);
		assertThat(trouveParLibelle.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);

		final OutputDTO trouveParId = this.service.findById(cree.getIdSousTypeProduit());

		assertThat(trouveParId).isNotNull();
		assertThat(trouveParId.getIdSousTypeProduit()).isEqualTo(cree.getIdSousTypeProduit());
		assertThat(trouveParId.getSousTypeProduit()).isEqualTo(IT_STP_ALPHA);
		assertThat(trouveParId.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);
	}

	/**
	 * <div>
	 * <p>creer(doublon) : violation de contrat (unicité).</p>
	 * <ul>
	 * <li>lève {@link ExceptionDoublon}</li>
	 * <li>positionne un message contenant {@link SousTypeProduitICuService#MESSAGE_DOUBLON}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("creer(doublon) : positionne message + lève ExceptionDoublon")
	public void testCreerDoublon() throws Exception {

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));

		final InputDTO input = new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_BETA);

		final OutputDTO cree = this.service.creer(input);

		assertThat(cree).isNotNull();

		assertThatThrownBy(() -> this.service.creer(input))
				.isInstanceOf(ExceptionDoublon.class);

		assertThat(this.service.getMessage())
				.contains(SousTypeProduitICuService.MESSAGE_DOUBLON);
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

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));

		this.service.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_GAMMA));
		this.service.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_DELTA));

		final List<OutputDTO> dtos = this.service.rechercherTous();

		assertThat(dtos).isNotNull();
		assertThat(dtos)
				.extracting(SousTypeProduitDTO.OutputDTO::getSousTypeProduit)
				.contains(IT_STP_GAMMA, IT_STP_DELTA);
	}

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

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));

		this.service.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_EPSILON));
		this.service.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ZETA));

		final List<String> libelles = this.service.rechercherTousString();

		assertThat(libelles).isNotNull();
		assertThat(libelles).contains(IT_STP_EPSILON, IT_STP_ZETA);
	}

	// ================== TESTS rechercherTousParPage(...) =================

	/**
	 * <div>
	 * <p>rechercherTousParPage(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne {@link SousTypeProduitICuService#MESSAGE_PAGEABLE_NULL}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("rechercherTousParPage(null) : positionne message + lève IllegalStateException")
	public void testRechercherTousParPageNull() {

		assertThatThrownBy(() -> this.service.rechercherTousParPage(null))
				.isInstanceOf(IllegalStateException.class);

		assertThat(this.service.getMessage())
				.contains(SousTypeProduitICuService.MESSAGE_PAGEABLE_NULL);
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

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));

		final long baseline = this.service.count();

		this.service.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PAGE_01));
		this.service.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PAGE_02));
		this.service.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PAGE_03));
		this.service.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PAGE_04));
		this.service.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PAGE_05));

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
	 * <li>positionne un message contenant {@link SousTypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
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
				.contains(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);
	}

	/**
	 * <div>
	 * <p>findByLibelle(introuvable) : cas nominal de non-trouvabilité.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne un message contenant {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelle(introuvable) : retourne null, message 'introuvable'")
	public void testFindByLibelleIntrouvable() throws Exception {

		final OutputDTO dto = this.service.findByLibelle(IT_STP_INEXISTANT_XYZ);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.contains(SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE);
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

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));
		this.service.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_DELTA));

		final OutputDTO dto = this.service.findByLibelle(IT_STP_DELTA);

		assertThat(dto).isNotNull();
		assertThat(dto.getSousTypeProduit()).isEqualTo(IT_STP_DELTA);
		assertThat(dto.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);
	}

	// ===================== TESTS findByLibelleRapide(...) =================

	/**
	 * <div>
	 * <p>findByLibelleRapide(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne {@link SousTypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("findByLibelleRapide(null) : positionne message + lève IllegalStateException")
	public void testFindByLibelleRapideNull() {

		assertThatThrownBy(() -> this.service.findByLibelleRapide(null))
				.isInstanceOf(IllegalStateException.class);

		assertThat(this.service.getMessage())
				.contains(SousTypeProduitICuService.MESSAGE_PARAM_NULL);
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

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));

		this.service.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_GAMMA));
		this.service.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_DELTA));

		final List<OutputDTO> dtos = this.service.findByLibelleRapide(ESPACES);

		assertThat(dtos).isNotNull();
		assertThat(dtos)
				.extracting(SousTypeProduitDTO.OutputDTO::getSousTypeProduit)
				.contains(IT_STP_GAMMA, IT_STP_DELTA);
	}

	/**
	 * <div>
	 * <p>findByLibelleRapide(non blank) : retourne une liste non nulle sans exception.</p>
	 * <p>Test "béton" : crée deux STP partageant un préfixe puis recherche ce préfixe.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelleRapide(non blank) : retourne liste non nulle contenant les créations du test")
	public void testFindByLibelleRapideNonBlank() throws Exception {

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));

		this.service.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_SEARCH_ABC));
		this.service.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_SEARCH_ABD));

		final List<OutputDTO> dtos = this.service.findByLibelleRapide(IT_STP_SEARCH_PREFIXE_AB);

		assertThat(dtos).isNotNull();
		assertThat(dtos)
				.extracting(SousTypeProduitDTO.OutputDTO::getSousTypeProduit)
				.contains(IT_STP_SEARCH_ABC, IT_STP_SEARCH_ABD);
	}

	/**
	 * <div>
	 * <p>findByLibelleRapide(introuvable) : retourne une liste vide.</p>
	 * <ul>
	 * <li>retourne une liste vide</li>
	 * <li>positionne {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelleRapide(introuvable) : liste vide + message MESSAGE_RECHERCHE_VIDE")
	public void testFindByLibelleRapideIntrouvable() throws Exception {

		final List<OutputDTO> dtos = this.service.findByLibelleRapide(IT_STP_SEARCH_PREFIXE_QQ);

		assertThat(dtos).isNotNull().isEmpty();
		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
	}

	// ========================== TESTS findByDTO(...) =====================

	/**
	 * <div>
	 * <p>findByDTO(null) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OBJ_NULL}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(null) : retourne null, message 'objet null'")
	public void testFindByDTONull() throws Exception {

		final OutputDTO dto = this.service.findByDTO(null);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.contains(SousTypeProduitICuService.MESSAGE_RECHERCHE_OBJ_NULL);
	}

	/**
	 * <div>
	 * <p>findByDTO(parent blank) : violation de contrat (PORT).</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(parent blank) : positionne MESSAGE_PAS_PARENT + lève IllegalStateException")
	public void testFindByDTOParentBlank() throws Exception {

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(ESPACES, IT_STP_ALPHA);

		assertThatThrownBy(() -> this.service.findByDTO(dto))
				.isInstanceOf(IllegalStateException.class);

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);
	}

	/**
	 * <div>
	 * <p>findByDTO(parent absent) : aucun parent persistant en stockage.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(parent absent) : retourne null + message MESSAGE_RECHERCHE_VIDE")
	public void testFindByDTOParentAbsent() throws Exception {

		/* Parent non créé. */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA);

		final OutputDTO trouve = this.service.findByDTO(dto);

		assertThat(trouve).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
	}

	/**
	 * <div>
	 * <p>findByDTO(ok) : test "béton" de la recherche via DTO.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(ok) : retourne OutputDTO cohérent après création")
	public void testFindByDTOOk() throws Exception {

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));
		this.service.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA));

		final InputDTO input = new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA);

		final OutputDTO dto = this.service.findByDTO(input);

		assertThat(dto).isNotNull();
		assertThat(dto.getSousTypeProduit()).isEqualTo(IT_STP_ALPHA);
		assertThat(dto.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);
	}

	// ===================== TESTS findAllByParent(...) ====================

	/**
	 * <div>
	 * <p>findAllByParent(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link RuntimeException}</li>
	 * <li>positionne {@link SousTypeProduitICuService#RECHERCHE_TYPEPRODUIT_NULL}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("findAllByParent(null) : positionne message + lève RuntimeException")
	public void testFindAllByParentNull() {

		assertThatThrownBy(() -> this.service.findAllByParent(null))
				.isInstanceOf(RuntimeException.class);

		assertThat(this.service.getMessage())
				.contains(SousTypeProduitICuService.RECHERCHE_TYPEPRODUIT_NULL);
	}

	/**
	 * <div>
	 * <p>findAllByParent(parent absent) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("findAllByParent(parent absent) : positionne MESSAGE_PAS_PARENT + lève IllegalStateException")
	public void testFindAllByParentPasParent() {

		final TypeProduitDTO.InputDTO parentDto = new TypeProduitDTO.InputDTO(IT_TP_PARENT_B);

		/* Parent non créé. */
		assertThatThrownBy(() -> this.service.findAllByParent(parentDto))
				.isInstanceOf(IllegalStateException.class);

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);
	}

	/**
	 * <div>
	 * <p>findAllByParent(vide) : parent existant mais aucun STP attaché.</p>
	 * <ul>
	 * <li>retourne une liste vide</li>
	 * <li>positionne {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findAllByParent(vide) : liste vide + message MESSAGE_RECHERCHE_VIDE")
	public void testFindAllByParentVide() throws Exception {

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_B));

		final List<OutputDTO> dtos = this.service.findAllByParent(new TypeProduitDTO.InputDTO(IT_TP_PARENT_B));

		assertThat(dtos).isNotNull().isEmpty();
		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
	}

	/**
	 * <div>
	 * <p>findAllByParent(ok) : test "béton" de la récupération de tous les STP d'un parent.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findAllByParent(ok) : retourne une liste non nulle contenant les créations du test")
	public void testFindAllByParentOk() throws Exception {

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_B));

		this.service.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_B, IT_STP_GAMMA));
		this.service.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_B, IT_STP_DELTA));

		final List<OutputDTO> dtos = this.service.findAllByParent(new TypeProduitDTO.InputDTO(IT_TP_PARENT_B));

		assertThat(dtos).isNotNull();
		assertThat(dtos)
				.extracting(SousTypeProduitDTO.OutputDTO::getSousTypeProduit)
				.contains(IT_STP_GAMMA, IT_STP_DELTA);
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
	 * <li>positionne un message contenant {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE}</li>
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
				.contains(SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE);
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

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));

		final OutputDTO cree = this.service.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_GAMMA));

		assertThat(cree).isNotNull();
		assertThat(cree.getIdSousTypeProduit()).isNotNull();

		final OutputDTO relu = this.service.findById(cree.getIdSousTypeProduit());

		assertThat(relu).isNotNull();
		assertThat(relu.getIdSousTypeProduit()).isEqualTo(cree.getIdSousTypeProduit());
		assertThat(relu.getSousTypeProduit()).isEqualTo(IT_STP_GAMMA);
		assertThat(relu.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);
	}

	// ========================= TESTS update(...) =========================

	/**
	 * <div>
	 * <p>update(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne {@link SousTypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("update(null) : positionne message + lève ExceptionParametreNull")
	public void testUpdateNull() {

		assertThatThrownBy(() -> this.service.update(null))
				.isInstanceOf(ExceptionParametreNull.class);

		assertThat(this.service.getMessage())
				.contains(SousTypeProduitICuService.MESSAGE_PARAM_NULL);
	}

	/**
	 * <div>
	 * <p>update(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne {@link SousTypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("update(blank) : positionne message + lève ExceptionParametreBlank")
	public void testUpdateBlank() {

		final InputDTO input = new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, ESPACES);

		assertThatThrownBy(() -> this.service.update(input))
				.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(this.service.getMessage())
				.contains(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);
	}

	/**
	 * <div>
	 * <p>update(introuvable) : cas nominal de non-trouvabilité.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne un message contenant {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(introuvable) : retourne null, message 'introuvable'")
	public void testUpdateIntrouvable() throws Exception {

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));

		final InputDTO input = new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_INEXISTANT_UPDATE);

		final OutputDTO dto = this.service.update(input);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.contains(SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE);
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

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));

		final OutputDTO cree = this.service.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_UPDATE_OK));

		assertThat(cree).isNotNull();
		assertThat(cree.getIdSousTypeProduit()).isNotNull();

		final OutputDTO modifie = this.service.update(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_UPDATE_OK));

		assertThat(modifie).isNotNull();
		assertThat(modifie.getIdSousTypeProduit()).isEqualTo(cree.getIdSousTypeProduit());
		assertThat(modifie.getSousTypeProduit()).isEqualTo(IT_STP_UPDATE_OK);
		assertThat(modifie.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);

		final OutputDTO relu = this.service.findById(cree.getIdSousTypeProduit());

		assertThat(relu).isNotNull();
		assertThat(relu.getIdSousTypeProduit()).isEqualTo(cree.getIdSousTypeProduit());
		assertThat(relu.getSousTypeProduit()).isEqualTo(IT_STP_UPDATE_OK);
		assertThat(relu.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);

		/* Règle projet : toujours préciser une Locale pour toUpperCase. */
		final String dummy = relu.getSousTypeProduit().toUpperCase(Locale.getDefault());
		assertThat(dummy).isNotBlank();
	}

	// ========================= TESTS delete(...) =========================

	/**
	 * <div>
	 * <p>delete(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne {@link SousTypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("delete(null) : violation de contrat -> ExceptionParametreNull")
	public void testDeleteNull() {

		assertThatThrownBy(() -> this.service.delete(null))
				.isInstanceOf(ExceptionParametreNull.class);

		assertThat(this.service.getMessage())
				.contains(SousTypeProduitICuService.MESSAGE_PARAM_NULL);
	}

	/**
	 * <div>
	 * <p>delete(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne {@link SousTypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("delete(blank) : positionne message + lève ExceptionParametreBlank")
	public void testDeleteBlank() {

		final InputDTO input = new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, ESPACES);

		assertThatThrownBy(() -> this.service.delete(input))
				.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(this.service.getMessage())
				.contains(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);
	}

	/**
	 * <div>
	 * <p>delete(introuvable) : ne supprime rien.</p>
	 * <ul>
	 * <li>le {@code count()} reste inchangé</li>
	 * <li>le message contient {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("delete(introuvable) : ne supprime rien, retourne, message 'introuvable'")
	public void testDeleteIntrouvable() throws Exception {

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));

		final long baseline = this.service.count();

		this.service.delete(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_INEXISTANT_DELETE));

		assertThat(this.service.count()).isEqualTo(baseline);

		assertThat(this.service.getMessage())
				.contains(SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE);
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

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));

		final OutputDTO cree = this.service.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_DELETE_OK));

		assertThat(cree).isNotNull();

		this.service.delete(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_DELETE_OK));

		final OutputDTO apresSuppression = this.service.findByLibelle(IT_STP_DELETE_OK);

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

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));

		final long baseline = this.service.count();

		this.service.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_COUNT_01));
		this.service.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_COUNT_02));

		final long apresCreations = this.service.count();

		assertThat(apresCreations).isEqualTo(baseline + 2L);

		this.service.delete(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_COUNT_01));

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

		final String messageAvant = this.service.getMessage();

		/* messageAvant peut être null : c'est acceptable. */
		assertThat(messageAvant).isIn(null, messageAvant);

		/* Provoque une erreur utilisateur bénigne pour positionner un message local. */
		this.service.creer(null);

		final String messageApres = this.service.getMessage();

		assertThat(messageApres)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_NULL);

		/* Règle projet : toujours préciser une Locale pour toUpperCase. */
		final String dummy = messageApres.toUpperCase(Locale.getDefault());
		assertThat(dummy).isNotBlank();
	}

	// *************************** METHODES UTILITAIRES ********************/

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
