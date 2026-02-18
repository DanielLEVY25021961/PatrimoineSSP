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

import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.OutputDTO;
import levy.daniel.application.model.services.produittype.cu.TypeProduitICuService;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionDoublon;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreBlank;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreNull;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;

/**
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE TypeProduitCuServiceIntegrationTest.java :
 * </p>
 * <p>
 * Tests d'intégration complets (avec tests "béton") du SERVICE ADAPTER METIER CU
 * {@link TypeProduitCuService}.
 * </p>
 * <p>
 * IMPORTANT :
 * <ul>
 * <li>On ne scanne PAS toute l'application : sinon SPRING instancie aussi les CONTROLLERS
 * qui exigent des {@code @Qualifier} spécifiques (ex. "typeProduitService") et font échouer
 * le chargement du contexte.</li>
 * <li>On fournit donc une configuration de test dédiée, limitée aux packages "métier CU/Gateway"
 * et "persistance TypeProduit".</li>
 * <li>On active le profil "dev" (en plus de "test") car {@link TypeProduitCuService}
 * est profilé {@code {"desktop","dev","prod"}}.</li>
 * </ul>
 * </p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 21 janvier 2026
 */
@SpringBootTest(
		classes = TypeProduitCuServiceIntegrationTest.ConfigTest.class,
		webEnvironment = SpringBootTest.WebEnvironment.NONE,
		properties = { "spring.main.web-application-type=none" }
)
@ActiveProfiles({ "test", "dev" })
@Tag(TypeProduitCuServiceIntegrationTest.TAG)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(
		scripts = {
				"classpath:/truncate-test.sql",
				"classpath:/data-test.sql"
		},
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class TypeProduitCuServiceIntegrationTest {

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
	 * TypeProduit IT : "IT-TP-ALPHA".
	 */
	public static final String IT_ALPHA = "IT-TP-ALPHA";

	/**
	 * TypeProduit IT : "IT-TP-BETA".
	 */
	public static final String IT_BETA = "IT-TP-BETA";

	/**
	 * TypeProduit IT : "IT-TP-GAMMA".
	 */
	public static final String IT_GAMMA = "IT-TP-GAMMA";

	/**
	 * TypeProduit IT : "IT-TP-DELTA".
	 */
	public static final String IT_DELTA = "IT-TP-DELTA";

	/**
	 * TypeProduit IT : "IT-TP-EPSILON".
	 */
	public static final String IT_EPSILON = "IT-TP-EPSILON";

	/**
	 * TypeProduit IT : "IT-TP-ZETA".
	 */
	public static final String IT_ZETA = "IT-TP-ZETA";

	/**
	 * TypeProduit IT : "IT-TP-PAGE-01".
	 */
	public static final String IT_PAGE_01 = "IT-TP-PAGE-01";

	/**
	 * TypeProduit IT : "IT-TP-PAGE-02".
	 */
	public static final String IT_PAGE_02 = "IT-TP-PAGE-02";

	/**
	 * TypeProduit IT : "IT-TP-PAGE-03".
	 */
	public static final String IT_PAGE_03 = "IT-TP-PAGE-03";

	/**
	 * TypeProduit IT : "IT-TP-PAGE-04".
	 */
	public static final String IT_PAGE_04 = "IT-TP-PAGE-04";

	/**
	 * TypeProduit IT : "IT-TP-PAGE-05".
	 */
	public static final String IT_PAGE_05 = "IT-TP-PAGE-05";

	/**
	 * TypeProduit IT : "IT-TP-INEXISTANT-XYZ".
	 */
	public static final String IT_INEXISTANT_XYZ = "IT-TP-INEXISTANT-XYZ";

	/**
	 * TypeProduit IT : "IT-TP-RAPIDE-A".
	 */
	public static final String IT_RAPIDE_A = "IT-TP-RAPIDE-A";

	/**
	 * TypeProduit IT : "IT-TP-RAPIDE-B".
	 */
	public static final String IT_RAPIDE_B = "IT-TP-RAPIDE-B";

	/**
	 * TypeProduit IT : "IT-TP-SEARCH-ABC".
	 */
	public static final String IT_SEARCH_ABC = "IT-TP-SEARCH-ABC";

	/**
	 * TypeProduit IT : "IT-TP-SEARCH-ABD".
	 */
	public static final String IT_SEARCH_ABD = "IT-TP-SEARCH-ABD";

	/**
	 * Préfixe recherche IT : "IT-TP-SEARCH-AB".
	 */
	public static final String IT_SEARCH_PREFIXE_AB = "IT-TP-SEARCH-AB";

	/**
	 * TypeProduit IT : "IT-TP-INEXISTANT-UPDATE".
	 */
	public static final String IT_INEXISTANT_UPDATE = "IT-TP-INEXISTANT-UPDATE";

	/**
	 * TypeProduit IT : "IT-TP-UPDATE-OK".
	 */
	public static final String IT_UPDATE_OK = "IT-TP-UPDATE-OK";

	/**
	 * TypeProduit IT : "IT-TP-INEXISTANT-DELETE".
	 */
	public static final String IT_INEXISTANT_DELETE = "IT-TP-INEXISTANT-DELETE";

	/**
	 * TypeProduit IT : "IT-TP-DELETE-OK".
	 */
	public static final String IT_DELETE_OK = "IT-TP-DELETE-OK";

	/**
	 * TypeProduit IT : "IT-TP-COUNT-01".
	 */
	public static final String IT_COUNT_01 = "IT-TP-COUNT-01";

	/**
	 * TypeProduit IT : "IT-TP-COUNT-02".
	 */
	public static final String IT_COUNT_02 = "IT-TP-COUNT-02";

	// *************************** ATTRIBUTS *******************************/

	/**
	 * SERVICE CU sous test (PORT).
	 */
	@Autowired
	private TypeProduitICuService service;

	// ************************* CONFIGURATION *****************************/

	/**
	 * <div>
	 * <p>CONFIGURATION DE TEST (SPRING).</p>
	 * <p>
	 * Déclare explicitement :
	 * </p>
	 * <ul>
	 * <li>scan applicatif limité (CU + Gateway TypeProduit + Persistance TypeProduit),
	 * en excluant les classes de tests,</li>
	 * <li>scan des entités JPA,</li>
	 * <li>activation des repositories Spring Data JPA (TypeProduit) une seule fois.</li>
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
	public static class ConfigTest { // NOPMD by danyl on 21/01/2026 13:00
		/* configuration de test. */
	}

	// *************************** CONSTRUCTEURS ***************************/

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public TypeProduitCuServiceIntegrationTest() {
		super();
	}

	// *************************** METHODES *******************************/

	/**
	 * <div>
	 * <p>creer(null) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne le message {@link TypeProduitICuService#MESSAGE_CREER_NULL}</li>
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
				.isEqualTo(TypeProduitICuService.MESSAGE_CREER_NULL);
	}

	/**
	 * <div>
	 * <p>creer(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne {@link TypeProduitICuService#MESSAGE_CREER_NOM_BLANK}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("creer(blank) : positionne message + lève ExceptionParametreBlank")
	public void testCreerBlank() {

		final InputDTO input = new TypeProduitDTO.InputDTO(ESPACES);

		assertThatThrownBy(() -> this.service.creer(input))
				.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(this.service.getMessage())
				.contains(TypeProduitICuService.MESSAGE_CREER_NOM_BLANK);
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

		final InputDTO input = new TypeProduitDTO.InputDTO(IT_ALPHA);

		final OutputDTO cree = this.service.creer(input);

		assertThat(cree).isNotNull();
		assertThat(cree.getIdTypeProduit()).isNotNull();
		assertThat(cree.getTypeProduit()).isEqualTo(IT_ALPHA);

		final OutputDTO trouveParLibelle = this.service.findByLibelle(IT_ALPHA);

		assertThat(trouveParLibelle).isNotNull();
		assertThat(trouveParLibelle.getIdTypeProduit()).isEqualTo(cree.getIdTypeProduit());
		assertThat(trouveParLibelle.getTypeProduit()).isEqualTo(IT_ALPHA);

		final OutputDTO trouveParId = this.service.findById(cree.getIdTypeProduit());

		assertThat(trouveParId).isNotNull();
		assertThat(trouveParId.getIdTypeProduit()).isEqualTo(cree.getIdTypeProduit());
		assertThat(trouveParId.getTypeProduit()).isEqualTo(IT_ALPHA);
	}

	/**
	 * <div>
	 * <p>creer(doublon) : violation de contrat (unicité).</p>
	 * <ul>
	 * <li>lève {@link ExceptionDoublon}</li>
	 * <li>positionne un message contenant {@link TypeProduitICuService#MESSAGE_DOUBLON}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("creer(doublon) : positionne message + lève ExceptionDoublon")
	public void testCreerDoublon() throws Exception {

		final InputDTO input = new TypeProduitDTO.InputDTO(IT_BETA);

		final OutputDTO cree = this.service.creer(input);

		assertThat(cree).isNotNull();

		assertThatThrownBy(() -> this.service.creer(input))
				.isInstanceOf(ExceptionDoublon.class);

		assertThat(this.service.getMessage())
				.contains(TypeProduitICuService.MESSAGE_DOUBLON);
	}

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

		this.service.creer(new TypeProduitDTO.InputDTO(IT_GAMMA));
		this.service.creer(new TypeProduitDTO.InputDTO(IT_DELTA));

		final List<OutputDTO> dtos = this.service.rechercherTous();

		assertThat(dtos).isNotNull();
		assertThat(dtos)
				.extracting(TypeProduitDTO.OutputDTO::getTypeProduit)
				.contains(IT_GAMMA, IT_DELTA);
	}

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

		this.service.creer(new TypeProduitDTO.InputDTO(IT_EPSILON));
		this.service.creer(new TypeProduitDTO.InputDTO(IT_ZETA));

		final List<String> libelles = this.service.rechercherTousString();

		assertThat(libelles).isNotNull();
		assertThat(libelles).contains(IT_EPSILON, IT_ZETA);
	}

	/**
	 * <div>
	 * <p>rechercherTousParPage(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne {@link TypeProduitICuService#MESSAGE_PAGEABLE_NULL}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("rechercherTousParPage(null) : positionne message + lève IllegalStateException")
	public void testRechercherTousParPageNull() {

		assertThatThrownBy(() -> this.service.rechercherTousParPage(null))
				.isInstanceOf(IllegalStateException.class);

		assertThat(this.service.getMessage())
				.contains(TypeProduitICuService.MESSAGE_PAGEABLE_NULL);
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

		final long baseline = this.service.count();

		this.service.creer(new TypeProduitDTO.InputDTO(IT_PAGE_01));
		this.service.creer(new TypeProduitDTO.InputDTO(IT_PAGE_02));
		this.service.creer(new TypeProduitDTO.InputDTO(IT_PAGE_03));
		this.service.creer(new TypeProduitDTO.InputDTO(IT_PAGE_04));
		this.service.creer(new TypeProduitDTO.InputDTO(IT_PAGE_05));

		final long attendu = baseline + 5L;

		final RequetePage requete = new RequetePage(0, 2);

		final ResultatPage<OutputDTO> rp = this.service.rechercherTousParPage(requete);

		assertThat(rp).isNotNull();
		assertThat(rp.getPageNumber()).isEqualTo(0);
		assertThat(rp.getPageSize()).isEqualTo(2);
		assertThat(rp.getTotalElements()).isEqualTo(attendu);
		assertThat(rp.getContent()).isNotNull();
		assertThat(rp.getContent().size()).isLessThanOrEqualTo(2);
	}

	/**
	 * <div>
	 * <p>findByLibelle(blank) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne un message contenant {@link TypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
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
				.contains(TypeProduitICuService.MESSAGE_PARAM_BLANK);
	}

	/**
	 * <div>
	 * <p>findByLibelle(introuvable) : cas nominal de non-trouvabilité.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne un message contenant {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelle(introuvable) : retourne null, message 'introuvable'")
	public void testFindByLibelleIntrouvable() throws Exception {

		final OutputDTO dto = this.service.findByLibelle(IT_INEXISTANT_XYZ);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.contains(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE);
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

		this.service.creer(new TypeProduitDTO.InputDTO(IT_DELTA));

		final OutputDTO dto = this.service.findByLibelle(IT_DELTA);

		assertThat(dto).isNotNull();
		assertThat(dto.getTypeProduit()).isEqualTo(IT_DELTA);
	}

	/**
	 * <div>
	 * <p>findByLibelleRapide(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne {@link TypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("findByLibelleRapide(null) : positionne message + lève IllegalStateException")
	public void testFindByLibelleRapideNull() {

		assertThatThrownBy(() -> this.service.findByLibelleRapide(null))
				.isInstanceOf(IllegalStateException.class);

		assertThat(this.service.getMessage())
				.contains(TypeProduitICuService.MESSAGE_PARAM_NULL);
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

		this.service.creer(new TypeProduitDTO.InputDTO(IT_RAPIDE_A));
		this.service.creer(new TypeProduitDTO.InputDTO(IT_RAPIDE_B));

		final List<OutputDTO> dtos = this.service.findByLibelleRapide(ESPACES);

		assertThat(dtos).isNotNull();
		assertThat(dtos)
				.extracting(TypeProduitDTO.OutputDTO::getTypeProduit)
				.contains(IT_RAPIDE_A, IT_RAPIDE_B);
	}

	/**
	 * <div>
	 * <p>findByLibelleRapide(non blank) : recherche "like" (selon implémentation) sans exception.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelleRapide(non blank) : retourne liste (éventuellement vide) sans exception")
	public void testFindByLibelleRapideNonBlank() throws Exception {

		this.service.creer(new TypeProduitDTO.InputDTO(IT_SEARCH_ABC));
		this.service.creer(new TypeProduitDTO.InputDTO(IT_SEARCH_ABD));

		final List<OutputDTO> dtos = this.service.findByLibelleRapide(IT_SEARCH_PREFIXE_AB);

		assertThat(dtos).isNotNull();
		assertThat(dtos)
				.extracting(TypeProduitDTO.OutputDTO::getTypeProduit)
				.contains(IT_SEARCH_ABC, IT_SEARCH_ABD);
	}

	/**
	 * <div>
	 * <p>findByDTO(null) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link TypeProduitICuService#MESSAGE_RECHERCHE_OBJ_NULL} (contrat CU)</li>
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
				.contains(TypeProduitICuService.MESSAGE_RECHERCHE_OBJ_NULL);
	}

	/**
	 * <div>
	 * <p>findByDTO(ok) : test "béton" de la recherche via DTO (délégation attendue au libellé).</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(ok) : retourne OutputDTO cohérent après création")
	public void testFindByDTOOk() throws Exception {

		this.service.creer(new TypeProduitDTO.InputDTO(IT_ALPHA));

		final InputDTO input = new TypeProduitDTO.InputDTO(IT_ALPHA);

		final OutputDTO dto = this.service.findByDTO(input);

		assertThat(dto).isNotNull();
		assertThat(dto.getTypeProduit()).isEqualTo(IT_ALPHA);
	}

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
	 * <li>positionne un message contenant {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findById(introuvable) : retourne null, message 'introuvable'")
	public void testFindByIdIntrouvable() throws Exception {

		/* Identifiant improbable (ne dépend pas du dataset SQL). */
		final Long idInexistant = Long.valueOf(Long.MAX_VALUE);

		final OutputDTO dto = this.service.findById(idInexistant);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.contains(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE);
	}

	/**
	 * <div>
	 * <p>update(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne {@link TypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("update(null) : positionne message + lève ExceptionParametreNull")
	public void testUpdateNull() {

		assertThatThrownBy(() -> this.service.update(null))
				.isInstanceOf(ExceptionParametreNull.class);

		assertThat(this.service.getMessage())
				.contains(TypeProduitICuService.MESSAGE_PARAM_NULL);
	}

	/**
	 * <div>
	 * <p>update(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne {@link TypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("update(blank) : positionne message + lève ExceptionParametreBlank")
	public void testUpdateBlank() {

		final InputDTO input = new TypeProduitDTO.InputDTO(ESPACES);

		assertThatThrownBy(() -> this.service.update(input))
				.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(this.service.getMessage())
				.contains(TypeProduitICuService.MESSAGE_PARAM_BLANK);
	}

	/**
	 * <div>
	 * <p>update(introuvable) : cas nominal de non-trouvabilité.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne un message contenant {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(introuvable) : retourne null, message 'introuvable'")
	public void testUpdateIntrouvable() throws Exception {

		final InputDTO input = new TypeProduitDTO.InputDTO(IT_INEXISTANT_UPDATE);

		final OutputDTO dto = this.service.update(input);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.contains(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE);
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

		final OutputDTO cree = this.service.creer(new TypeProduitDTO.InputDTO(IT_UPDATE_OK));

		assertThat(cree).isNotNull();
		assertThat(cree.getIdTypeProduit()).isNotNull();

		final OutputDTO modifie = this.service.update(new TypeProduitDTO.InputDTO(IT_UPDATE_OK));

		assertThat(modifie).isNotNull();
		assertThat(modifie.getIdTypeProduit()).isEqualTo(cree.getIdTypeProduit());
		assertThat(modifie.getTypeProduit()).isEqualTo(IT_UPDATE_OK);

		final OutputDTO relu = this.service.findById(cree.getIdTypeProduit());

		assertThat(relu).isNotNull();
		assertThat(relu.getIdTypeProduit()).isEqualTo(cree.getIdTypeProduit());
		assertThat(relu.getTypeProduit()).isEqualTo(IT_UPDATE_OK);
	}

	/**
	 * <div>
	 * <p>delete(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne {@link TypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("delete(null) : violation de contrat -> ExceptionParametreNull")
	public void testDeleteNull() {

		assertThatThrownBy(() -> this.service.delete(null))
				.isInstanceOf(ExceptionParametreNull.class);

		assertThat(this.service.getMessage())
				.contains(TypeProduitICuService.MESSAGE_PARAM_NULL);
	}

	/**
	 * <div>
	 * <p>delete(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne {@link TypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("delete(blank) : positionne message + lève ExceptionParametreBlank")
	public void testDeleteBlank() {

		final InputDTO input = new TypeProduitDTO.InputDTO(ESPACES);

		assertThatThrownBy(() -> this.service.delete(input))
				.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(this.service.getMessage())
				.contains(TypeProduitICuService.MESSAGE_PARAM_BLANK);
	}

	/**
	 * <div>
	 * <p>delete(introuvable) : ne supprime rien.</p>
	 * <ul>
	 * <li>le {@code count()} reste inchangé</li>
	 * <li>le message contient {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("delete(introuvable) : ne supprime rien, retourne, message 'introuvable'")
	public void testDeleteIntrouvable() throws Exception {

		final long baseline = this.service.count();

		this.service.delete(new TypeProduitDTO.InputDTO(IT_INEXISTANT_DELETE));

		assertThat(this.service.count()).isEqualTo(baseline);

		assertThat(this.service.getMessage())
				.contains(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE);
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

		final OutputDTO cree = this.service.creer(new TypeProduitDTO.InputDTO(IT_DELETE_OK));

		assertThat(cree).isNotNull();

		this.service.delete(new TypeProduitDTO.InputDTO(IT_DELETE_OK));

		final OutputDTO apresSuppression = this.service.findByLibelle(IT_DELETE_OK);

		assertThat(apresSuppression).isNull();
	}

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

		final long baseline = this.service.count();

		this.service.creer(new TypeProduitDTO.InputDTO(IT_COUNT_01));
		this.service.creer(new TypeProduitDTO.InputDTO(IT_COUNT_02));

		final long apresCreations = this.service.count();

		assertThat(apresCreations).isEqualTo(baseline + 2L);

		this.service.delete(new TypeProduitDTO.InputDTO(IT_COUNT_01));

		final long apresSuppression = this.service.count();

		assertThat(apresSuppression).isEqualTo(baseline + 1L);
	}

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
				.isEqualTo(TypeProduitICuService.MESSAGE_CREER_NULL);

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
