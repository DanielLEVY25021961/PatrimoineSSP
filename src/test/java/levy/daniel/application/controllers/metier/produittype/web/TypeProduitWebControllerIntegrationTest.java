/* ********************************************************************* */
/* ****************** TEST INTEGRATION CONTROLLER WEB ****************** */
/* ********************************************************************* */
package levy.daniel.application.controllers.metier.produittype.web;

import static org.assertj.core.api.Assertions.assertThat;

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

import levy.daniel.application.controllers.metier.produittype.TypeProduitIController;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.OutputDTO;
import levy.daniel.application.model.services.produittype.cu.TypeProduitICuService;

/**
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE TypeProduitWebControllerIntegrationTest.java :
 * </p>
 * <p>
 * Tests d'intégration complets (avec tests "béton") du CONTROLLER ADAPTER
 * WEB {@link TypeProduitWebController}.
 * </p>
 * <p>
 * IMPORTANT :
 * <ul>
 * <li>on charge le vrai bean CONTROLLER Web, le vrai SERVICE UC,
 * le vrai Gateway et la vraie persistance TypeProduit ;</li>
 * <li>on n'exécute pas de test HTTP bout-en-bout : on appelle directement
 * le bean Spring du controller ;</li>
 * <li>on active les profils "web" et "dev" (en plus de "test") afin
 * d'instancier le CONTROLLER Web et le SERVICE UC.</li>
 * </ul>
 * </p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 4 avril 2026
 */
@SpringBootTest(
		classes = TypeProduitWebControllerIntegrationTest.ConfigTest.class,
		webEnvironment = SpringBootTest.WebEnvironment.NONE,
		properties = { "spring.main.web-application-type=none" }
)
@ActiveProfiles({ "test", "web", "dev" })
@Tag(TypeProduitWebControllerIntegrationTest.TAG)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(
		scripts = {
				"classpath:/truncate-test.sql",
				"classpath:/data-test.sql"
		},
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class TypeProduitWebControllerIntegrationTest {

	// *************************** CONSTANTES ******************************/

	/** Tag JUnit : "controller-web-it". */
	public static final String TAG = "controller-web-it";

	/** Chaine blank : "   ". */
	public static final String ESPACES = "   ";

	/** TypeProduit IT controller web : "IT-CTRL-WEB-ALPHA". */
	public static final String IT_ALPHA = "IT-CTRL-WEB-ALPHA";
		
	/**
	 * "classpath:/truncate-test.sql"
	 */
	public static final String CLASSPATH_TRUNCATE_TEST 
		= "classpath:/truncate-test.sql";

	// **************************** BEANS *********************************/

	/** Controller Web réel injecté par Spring. */
	@Autowired
	private TypeProduitWebController controller;

	/** JdbcTemplate de test pour preuve BD directe. */
	@Autowired
	private JdbcTemplate jdbcTemplate;

	// ************************ CONFIGURATION DE TEST *********************/

	/**
	 * <div>
	 * <p style="font-weight:bold;">CONFIGURATION DE TEST SPRING.</p>
	 * <ul>
	 * <li>scan applicatif limité (Controllers TypeProduit + Services TypeProduit + Persistance TypeProduit),</li>
	 * <li>scan des entités JPA,</li>
	 * <li>activation des repositories Spring Data JPA une seule fois.</li>
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
					"levy.daniel.application.controllers.metier.produittype",
					"levy.daniel.application.model.services.produittype",
					"levy.daniel.application.persistence.metier.produittype"
			},
			excludeFilters = {
					@Filter(type = FilterType.REGEX, pattern = ".*IntegrationTest.*"),
					@Filter(type = FilterType.REGEX, pattern = ".*MockTest.*")
			}
	)
	public static class ConfigTest { // NOPMD by danyl on 04/04/2026 17:00
		/* configuration de test. */
	}

	
	
	// *************************** CONSTRUCTEURS ***************************/

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public TypeProduitWebControllerIntegrationTest() {
		super();
	}

	// *************************** METHODES *******************************/

	
	
	// ---------------------- creer(...) --------------------------------//

	
	
	/**
	 * <div>
	 * <p>creer(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link TypeProduitIController#MESSAGE_CREER_VUE_NULL}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("creer(null) : retourne null + message MESSAGE_CREER_VUE_NULL + aucune écriture BD")
	public void testCreerNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.creer(null);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitIController.MESSAGE_CREER_VUE_NULL);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>creer(blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link TypeProduitIController#MESSAGE_CREER_VUE_BLANK}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("creer(blank) : retourne null + message MESSAGE_CREER_VUE_BLANK + aucune écriture BD")
	public void testCreerBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final InputDTO input = new TypeProduitDTO.InputDTO(ESPACES);

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.creer(input);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitIController.MESSAGE_CREER_VUE_BLANK);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>creer(ok) : test béton avec preuve BD.</p>
	 * <ul>
	 * <li>retourne un {@link OutputDTO} persistant</li>
	 * <li>positionne exactement {@link TypeProduitICuService#MESSAGE_CREER_OK}</li>
	 * <li>augmente le nombre de lignes de 1</li>
	 * <li>prouve physiquement l'écriture en base via SQL direct</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("creer(ok) : preuve BD + message exact + OutputDTO persistant")
	public void testCreerOkAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final InputDTO input = new TypeProduitDTO.InputDTO(IT_ALPHA);

		/* ======================= ACT ======================= */
		final OutputDTO cree = this.controller.creer(input);

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(cree.getIdTypeProduit()).isNotNull();
		assertThat(cree.getTypeProduit()).isEqualTo(IT_ALPHA);

		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_CREER_OK);

		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterTypeProduitEnBase(cree.getIdTypeProduit())).isEqualTo(1L);
		assertThat(this.compterTypeProduitParLibelleEnBase(IT_ALPHA)).isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(cree.getIdTypeProduit()))
				.isEqualTo(IT_ALPHA);

	} // __________________________________________________________________
	
	
	
	// ------------------- rechercherTous() ------------------------------//

	
				
	/**
	 * <div>
	 * <p>rechercherTous(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste non nulle</li>
	 * <li>positionne exactement {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>contient la création du test</li>
	 * <li>reste cohérent avec le comptage physique en base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("rechercherTous(ok) : message exact + cohérence count + présence de la création + preuve BD")
	public void testRechercherTousOkAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final OutputDTO cree = this.controller.creer(new TypeProduitDTO.InputDTO(IT_ALPHA));
		final long attendu = this.compterTousLesTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> dtos = this.controller.rechercherTous();

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(dtos).isNotNull();
		assertThat(dtos.size()).isEqualTo((int) attendu);
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);
		assertThat(dtos)
				.extracting(TypeProduitDTO.OutputDTO::getTypeProduit)
				.contains(IT_ALPHA);

		final OutputDTO dtoAlpha = dtos.stream()
				.filter(dto -> IT_ALPHA.equals(dto.getTypeProduit()))
				.findFirst()
				.orElse(null);

		assertThat(dtoAlpha).isNotNull();
		assertThat(dtoAlpha.getIdTypeProduit()).isEqualTo(cree.getIdTypeProduit());
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterTypeProduitEnBase(cree.getIdTypeProduit())).isEqualTo(1L);
		assertThat(this.compterTypeProduitParLibelleEnBase(IT_ALPHA)).isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(cree.getIdTypeProduit()))
				.isEqualTo(IT_ALPHA);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>rechercherTous(vide) : scénario nominal vide avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste vide</li>
	 * <li>positionne exactement {@link TypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>prouve physiquement que la table est vide</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Sql(
			scripts = {
					CLASSPATH_TRUNCATE_TEST
			},
			executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
	)
	@DisplayName("rechercherTous(vide) : liste vide + message exact + preuve BD")
	public void testRechercherTousVideAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> dtos = this.controller.rechercherTous();

		/* ===================== ASSERT ====================== */
		assertThat(baseline).isZero();
		assertThat(dtos).isNotNull();
		assertThat(dtos).isEmpty();
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		assertThat(this.compterTousLesTypeProduitEnBase()).isZero();

	} // __________________________________________________________________
	

	
	// ------------------ rechercherTousString() ------------------------//
	
	
	
	/**
	 * <div>
	 * <p>rechercherTousString(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste de String non nulle</li>
	 * <li>positionne exactement {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>contient la création du test</li>
	 * <li>reste cohérent avec la preuve physique en base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("rechercherTousString(ok) : message exact + présence de la création + preuve BD")
	public void testRechercherTousStringOkAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final OutputDTO cree = this.controller.creer(new TypeProduitDTO.InputDTO(IT_ALPHA));

		/* ======================= ACT ======================= */
		final java.util.List<String> libelles = this.controller.rechercherTousString();

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(libelles).isNotNull();
		assertThat(libelles).contains(IT_ALPHA);
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterTypeProduitEnBase(cree.getIdTypeProduit())).isEqualTo(1L);
		assertThat(this.compterTypeProduitParLibelleEnBase(IT_ALPHA)).isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(cree.getIdTypeProduit()))
				.isEqualTo(IT_ALPHA);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>rechercherTousString(vide) : scénario nominal vide avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste vide</li>
	 * <li>positionne exactement {@link TypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>prouve physiquement que la table est vide</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Sql(
			scripts = {
					CLASSPATH_TRUNCATE_TEST
			},
			executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
	)
	@DisplayName("rechercherTousString(vide) : liste vide + message exact + preuve BD")
	public void testRechercherTousStringVideAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final java.util.List<String> libelles = this.controller.rechercherTousString();

		/* ===================== ASSERT ====================== */
		assertThat(baseline).isZero();
		assertThat(libelles).isNotNull();
		assertThat(libelles).isEmpty();
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		assertThat(this.compterTousLesTypeProduitEnBase()).isZero();

	} // __________________________________________________________________
		
	

	// --------------- rechercherTousParPage(...) -----------------------//
	
	
	
	/**
	 * <div>
	 * <p>rechercherTousParPage(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link TypeProduitIController#MESSAGE_RECHERCHE_PAGINEE_REQUETE_NULL}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("rechercherTousParPage(null) : retourne null + message local + aucune écriture BD")
	public void testRechercherTousParPageNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final levy.daniel.application.model.dto.pagination.ResultatPageDTO<OutputDTO> page
			= this.controller.rechercherTousParPage(null);

		/* ===================== ASSERT ====================== */
		assertThat(page).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(
						TypeProduitIController
							.MESSAGE_RECHERCHE_PAGINEE_REQUETE_NULL);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>rechercherTousParPage(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une page DTO non nulle</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_PAGINEE_OK}</li>
	 * <li>retourne une pagination humaine cohérente pour la VUE</li>
	 * <li>reste cohérent avec le comptage physique en base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("rechercherTousParPage(ok) : page DTO cohérente + message exact + présence de la création + preuve BD")
	public void testRechercherTousParPageOkAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final OutputDTO cree = this.controller.creer(new TypeProduitDTO.InputDTO(IT_ALPHA));
		final long attendu = this.compterTousLesTypeProduitEnBase();

		final levy.daniel.application.model.dto.pagination.RequetePageDTO requete
			= new levy.daniel.application.model.dto.pagination.RequetePageDTO(
					1,
					10,
					java.util.List.of(
							new levy.daniel.application.model.dto.pagination.TriSpecDTO(
									"typeProduit",
									levy.daniel.application.model.dto.pagination.DirectionTriDTO.ASC)));

		/* ======================= ACT ======================= */
		final levy.daniel.application.model.dto.pagination.ResultatPageDTO<OutputDTO> page
			= this.controller.rechercherTousParPage(requete);

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(page).isNotNull();
		assertThat(page.getPageNumber()).isEqualTo(1);
		assertThat(page.getPageSize()).isEqualTo(10);
		assertThat(page.getTotalElements()).isEqualTo(attendu);
		assertThat(page.getTotalPages()).isEqualTo(1);
		assertThat(page.getContent()).isNotNull();
		assertThat(page.getContent().size()).isEqualTo((int) attendu);
		assertThat(page.getContent())
				.extracting(TypeProduitDTO.OutputDTO::getTypeProduit)
				.contains(IT_ALPHA);

		final OutputDTO dtoAlpha = page.getContent().stream()
				.filter(dto -> IT_ALPHA.equals(dto.getTypeProduit()))
				.findFirst()
				.orElse(null);

		assertThat(dtoAlpha).isNotNull();
		assertThat(dtoAlpha.getIdTypeProduit()).isEqualTo(cree.getIdTypeProduit());
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterTypeProduitEnBase(cree.getIdTypeProduit())).isEqualTo(1L);
		assertThat(this.compterTypeProduitParLibelleEnBase(IT_ALPHA)).isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(cree.getIdTypeProduit()))
				.isEqualTo(IT_ALPHA);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>rechercherTousParPage(vide) : scénario nominal vide avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une page DTO non nulle et vide</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_PAGINEE_OK}</li>
	 * <li>prouve physiquement que la table est vide</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Sql(
			scripts = {
					CLASSPATH_TRUNCATE_TEST
			},
			executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
	)
	@DisplayName("rechercherTousParPage(vide) : page vide + message exact + preuve BD")
	public void testRechercherTousParPageVideAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final levy.daniel.application.model.dto.pagination.RequetePageDTO requete
			= new levy.daniel.application.model.dto.pagination.RequetePageDTO(1, 10);

		/* ======================= ACT ======================= */
		final levy.daniel.application.model.dto.pagination.ResultatPageDTO<OutputDTO> page
			= this.controller.rechercherTousParPage(requete);

		/* ===================== ASSERT ====================== */
		assertThat(baseline).isZero();
		assertThat(page).isNotNull();
		assertThat(page.getPageNumber()).isEqualTo(1);
		assertThat(page.getPageSize()).isEqualTo(10);
		assertThat(page.getTotalElements()).isEqualTo(0L);
		assertThat(page.getTotalPages()).isZero();
		assertThat(page.getContent()).isEmpty();
		assertThat(page.isHasNext()).isFalse();
		assertThat(page.isHasPrevious()).isFalse();
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);
		assertThat(this.compterTousLesTypeProduitEnBase()).isZero();

	} // __________________________________________________________________	


	
	// --------------------- findByLibelle(...) -------------------------//
	
	
	
	/**
	 * <div>
	 * <p>findByLibelle(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link TypeProduitIController#MESSAGE_FIND_BY_LIBELLE_VUE_NULL}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelle(null) : retourne null + message local + aucune écriture BD")
	public void testFindByLibelleNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.findByLibelle(null);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitIController.MESSAGE_FIND_BY_LIBELLE_VUE_NULL);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findByLibelle(blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link TypeProduitIController#MESSAGE_FIND_BY_LIBELLE_VUE_BLANK}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelle(blank) : retourne null + message local + aucune écriture BD")
	public void testFindByLibelleBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.findByLibelle(ESPACES);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitIController.MESSAGE_FIND_BY_LIBELLE_VUE_BLANK);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findByLibelle(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne un {@link OutputDTO} non nul</li>
	 * <li>positionne exactement {@link TypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}</li>
	 * <li>retourne le DTO correspondant à l'objet créé en base</li>
	 * <li>ne modifie pas physiquement la base lors de la recherche</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelle(ok) : DTO trouvé + message exact + preuve BD")
	public void testFindByLibelleOkAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final InputDTO input = new TypeProduitDTO.InputDTO(IT_ALPHA);
		final OutputDTO cree = this.controller.creer(input);

		/* ======================= ACT ======================= */
		final OutputDTO trouve = this.controller.findByLibelle(IT_ALPHA);

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(trouve).isNotNull();
		assertThat(trouve.getIdTypeProduit()).isEqualTo(cree.getIdTypeProduit());
		assertThat(trouve.getTypeProduit()).isEqualTo(IT_ALPHA);
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterTypeProduitEnBase(cree.getIdTypeProduit())).isEqualTo(1L);
		assertThat(this.compterTypeProduitParLibelleEnBase(IT_ALPHA)).isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(cree.getIdTypeProduit()))
				.isEqualTo(IT_ALPHA);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findByLibelle(absent) : scénario nominal sans résultat avec preuve BD.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + libellé</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelle(absent) : retourne null + message exact + aucune écriture BD")
	public void testFindByLibelleAbsentAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final String libelle = "IT-CTRL-WEB-INEXISTANT";

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.findByLibelle(libelle);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + libelle);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline);
		assertThat(this.compterTypeProduitParLibelleEnBase(libelle)).isZero();

	} // __________________________________________________________________	


	
	// ------------------ findByLibelleRapide(...) ----------------------//
	
	
	
	/**
	 * <div>
	 * <p>findByLibelleRapide(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link TypeProduitIController#MESSAGE_FIND_BY_LIBELLE_RAPIDE_VUE_NULL}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelleRapide(null) : retourne null + message local + aucune écriture BD")
	public void testFindByLibelleRapideNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> dtos = this.controller.findByLibelleRapide(null);

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(
						TypeProduitIController
							.MESSAGE_FIND_BY_LIBELLE_RAPIDE_VUE_NULL);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelleRapide(blank) : le controller délègue au service.</p>
	 * <ul>
	 * <li>retourne tous les enregistrements selon le contrat observable de rechercherTous()</li>
	 * <li>positionne exactement {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>prouve physiquement la cohérence avec la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelleRapide(blank) : liste complète + message exact + preuve BD")
	public void testFindByLibelleRapideBlankAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final OutputDTO cree = this.controller.creer(new TypeProduitDTO.InputDTO(IT_ALPHA));
		final long attendu = this.compterTousLesTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> dtos = this.controller.findByLibelleRapide(ESPACES);

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(dtos).isNotNull();
		assertThat(dtos.size()).isEqualTo((int) attendu);
		assertThat(dtos)
				.extracting(TypeProduitDTO.OutputDTO::getTypeProduit)
				.contains(IT_ALPHA);
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		final OutputDTO dtoAlpha = dtos.stream()
				.filter(dto -> IT_ALPHA.equals(dto.getTypeProduit()))
				.findFirst()
				.orElse(null);

		assertThat(dtoAlpha).isNotNull();
		assertThat(dtoAlpha.getIdTypeProduit()).isEqualTo(cree.getIdTypeProduit());
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterTypeProduitEnBase(cree.getIdTypeProduit())).isEqualTo(1L);
		assertThat(this.compterTypeProduitParLibelleEnBase(IT_ALPHA)).isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(cree.getIdTypeProduit()))
				.isEqualTo(IT_ALPHA);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelleRapide(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste non vide</li>
	 * <li>positionne exactement {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>contient le DTO correspondant à l'objet créé en base</li>
	 * <li>ne modifie pas physiquement la base lors de la recherche</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelleRapide(ok) : liste trouvée + message exact + preuve BD")
	public void testFindByLibelleRapideOkAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final InputDTO input = new TypeProduitDTO.InputDTO(IT_ALPHA);
		final OutputDTO cree = this.controller.creer(input);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> dtos
			= this.controller.findByLibelleRapide(IT_ALPHA);

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(dtos).isNotNull();
		assertThat(dtos).isNotEmpty();
		assertThat(dtos)
				.extracting(TypeProduitDTO.OutputDTO::getTypeProduit)
				.contains(IT_ALPHA);
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		final OutputDTO dtoAlpha = dtos.stream()
				.filter(dto -> IT_ALPHA.equals(dto.getTypeProduit()))
				.findFirst()
				.orElse(null);

		assertThat(dtoAlpha).isNotNull();
		assertThat(dtoAlpha.getIdTypeProduit()).isEqualTo(cree.getIdTypeProduit());
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterTypeProduitEnBase(cree.getIdTypeProduit())).isEqualTo(1L);
		assertThat(this.compterTypeProduitParLibelleEnBase(IT_ALPHA)).isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(cree.getIdTypeProduit()))
				.isEqualTo(IT_ALPHA);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelleRapide(absent) : scénario nominal sans résultat avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste vide</li>
	 * <li>positionne exactement {@link TypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelleRapide(absent) : liste vide + message exact + aucune écriture BD")
	public void testFindByLibelleRapideAbsentAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final String contenu = "IT-CTRL-WEB-RAPIDE-INEXISTANT";

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> dtos
			= this.controller.findByLibelleRapide(contenu);

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNotNull();
		assertThat(dtos).isEmpty();
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline);
		assertThat(this.compterTypeProduitParLibelleEnBase(contenu)).isZero();

	} // __________________________________________________________________	


	
	// ---------------------- findByDTO(...) ----------------------------//
	
	
	
	/**
	 * <div>
	 * <p>findByDTO(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link TypeProduitIController#MESSAGE_FIND_BY_DTO_VUE_NULL}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(null) : retourne null + message local + aucune écriture BD")
	public void testFindByDTONull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.findByDTO(null);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitIController.MESSAGE_FIND_BY_DTO_VUE_NULL);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByDTO(blank) : le controller délègue au service.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement {@link TypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(blank) : retourne null + message exact + aucune écriture BD")
	public void testFindByDTOBlankAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final InputDTO input = new TypeProduitDTO.InputDTO(ESPACES);

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.findByDTO(input);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByDTO(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne un {@link OutputDTO} non nul</li>
	 * <li>positionne exactement {@link TypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}</li>
	 * <li>retourne le DTO correspondant à l'objet créé en base</li>
	 * <li>ne modifie pas physiquement la base lors de la recherche</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(ok) : DTO trouvé + message exact + preuve BD")
	public void testFindByDTOOkAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final InputDTO inputCreation = new TypeProduitDTO.InputDTO(IT_ALPHA);
		final OutputDTO cree = this.controller.creer(inputCreation);
		final InputDTO inputRecherche = new TypeProduitDTO.InputDTO(IT_ALPHA);

		/* ======================= ACT ======================= */
		final OutputDTO trouve = this.controller.findByDTO(inputRecherche);

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(trouve).isNotNull();
		assertThat(trouve.getIdTypeProduit()).isEqualTo(cree.getIdTypeProduit());
		assertThat(trouve.getTypeProduit()).isEqualTo(IT_ALPHA);
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterTypeProduitEnBase(cree.getIdTypeProduit())).isEqualTo(1L);
		assertThat(this.compterTypeProduitParLibelleEnBase(IT_ALPHA)).isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(cree.getIdTypeProduit()))
				.isEqualTo(IT_ALPHA);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByDTO(absent) : scénario nominal sans résultat avec preuve BD.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + libellé</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(absent) : retourne null + message exact + aucune écriture BD")
	public void testFindByDTOAbsentAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final String libelle = "IT-CTRL-WEB-DTO-INEXISTANT";
		final InputDTO input = new TypeProduitDTO.InputDTO(libelle);

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.findByDTO(input);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + libelle);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline);
		assertThat(this.compterTypeProduitParLibelleEnBase(libelle)).isZero();

	} // __________________________________________________________________


	
	// ----------------------- findById(...) ----------------------------//
	
	
	
	/**
	 * <div>
	 * <p>findById(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link TypeProduitIController#MESSAGE_FIND_BY_ID_VUE_NULL}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findById(null) : retourne null + message local + aucune écriture BD")
	public void testFindByIdNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.findById(null);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitIController.MESSAGE_FIND_BY_ID_VUE_NULL);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findById(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne un {@link OutputDTO} non nul</li>
	 * <li>positionne exactement {@link TypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}</li>
	 * <li>retourne le DTO correspondant à l'objet créé en base</li>
	 * <li>ne modifie pas physiquement la base lors de la recherche</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findById(ok) : DTO trouvé + message exact + preuve BD")
	public void testFindByIdOkAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final InputDTO inputCreation = new TypeProduitDTO.InputDTO(IT_ALPHA);
		final OutputDTO cree = this.controller.creer(inputCreation);

		/* ======================= ACT ======================= */
		final OutputDTO trouve = this.controller.findById(cree.getIdTypeProduit());

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(trouve).isNotNull();
		assertThat(trouve.getIdTypeProduit()).isEqualTo(cree.getIdTypeProduit());
		assertThat(trouve.getTypeProduit()).isEqualTo(IT_ALPHA);
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterTypeProduitEnBase(cree.getIdTypeProduit())).isEqualTo(1L);
		assertThat(this.compterTypeProduitParLibelleEnBase(IT_ALPHA)).isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(cree.getIdTypeProduit()))
				.isEqualTo(IT_ALPHA);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findById(absent) : scénario nominal sans résultat avec preuve BD.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + id</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findById(absent) : retourne null + message exact + aucune écriture BD")
	public void testFindByIdAbsentAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final Long id = 999_999_999L;

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.findById(id);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + id);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline);
		assertThat(this.compterTypeProduitEnBase(id)).isZero();

	} // __________________________________________________________________


	
	// ------------------------ update(...) -----------------------------//


	
	/**
	 * <div>
	 * <p>update(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link TypeProduitIController#MESSAGE_UPDATE_VUE_NULL}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(null) : retourne null + message local + aucune écriture BD")
	public void testUpdateNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.update(null);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitIController.MESSAGE_UPDATE_VUE_NULL);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link TypeProduitIController#MESSAGE_UPDATE_VUE_BLANK}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(blank) : retourne null + message local + aucune écriture BD")
	public void testUpdateBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final InputDTO input = new TypeProduitDTO.InputDTO(ESPACES);

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.update(input);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitIController.MESSAGE_UPDATE_VUE_BLANK);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne un {@link OutputDTO} non nul</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_MODIF_OK} + libellé</li>
	 * <li>retourne le DTO correspondant à l'objet déjà persistant</li>
	 * <li>ne modifie pas le nombre d'enregistrements en base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(ok) : DTO modifié + message exact + preuve BD")
	public void testUpdateOkAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final InputDTO inputCreation = new TypeProduitDTO.InputDTO(IT_ALPHA);
		final OutputDTO cree = this.controller.creer(inputCreation);
		final InputDTO inputModification = new TypeProduitDTO.InputDTO(IT_ALPHA);

		/* ======================= ACT ======================= */
		final OutputDTO modifie = this.controller.update(inputModification);

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(modifie).isNotNull();
		assertThat(modifie.getIdTypeProduit()).isEqualTo(cree.getIdTypeProduit());
		assertThat(modifie.getTypeProduit()).isEqualTo(IT_ALPHA);
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_MODIF_OK + IT_ALPHA);

		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterTypeProduitEnBase(cree.getIdTypeProduit())).isEqualTo(1L);
		assertThat(this.compterTypeProduitParLibelleEnBase(IT_ALPHA)).isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(cree.getIdTypeProduit()))
				.isEqualTo(IT_ALPHA);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(absent) : scénario nominal sans résultat avec preuve BD.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + libellé</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(absent) : retourne null + message exact + aucune écriture BD")
	public void testUpdateAbsentAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final String libelle = "IT-CTRL-WEB-UPDATE-INEXISTANT";
		final InputDTO input = new TypeProduitDTO.InputDTO(libelle);

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.update(input);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + libelle);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline);
		assertThat(this.compterTypeProduitParLibelleEnBase(libelle)).isZero();

	} // __________________________________________________________________


	
	// ------------------------ delete(...) -----------------------------//



	/**
	 * <div>
	 * <p>delete(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>positionne {@link TypeProduitIController#MESSAGE_DELETE_VUE_NULL}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("delete(null) : message local + aucune écriture BD")
	public void testDeleteNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();

		/* ======================= ACT ======================= */
		this.controller.delete(null);

		/* ===================== ASSERT ====================== */
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitIController.MESSAGE_DELETE_VUE_NULL);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>positionne {@link TypeProduitIController#MESSAGE_DELETE_VUE_BLANK}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("delete(blank) : message local + aucune écriture BD")
	public void testDeleteBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final InputDTO input = new TypeProduitDTO.InputDTO(ESPACES);

		/* ======================= ACT ======================= */
		this.controller.delete(input);

		/* ===================== ASSERT ====================== */
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitIController.MESSAGE_DELETE_VUE_BLANK);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(absent) : scénario nominal sans suppression avec preuve BD.</p>
	 * <ul>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + libellé</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("delete(absent) : message exact + aucune suppression BD")
	public void testDeleteAbsentAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final String libelle = "IT-CTRL-WEB-DELETE-INEXISTANT";
		final InputDTO input = new TypeProduitDTO.InputDTO(libelle);

		/* ======================= ACT ======================= */
		this.controller.delete(input);

		/* ===================== ASSERT ====================== */
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + libelle);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline);
		assertThat(this.compterTypeProduitParLibelleEnBase(libelle)).isZero();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(ok) : suppression physique avec preuve BD.</p>
	 * <ul>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_DELETE_OK} + libellé</li>
	 * <li>prouve physiquement la disparition en base</li>
	 * <li>prouve le retour au volume initial en base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("delete(ok) : suppression physique + message exact + preuve BD")
	public void testDeleteOkAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final OutputDTO cree = this.controller.creer(new TypeProduitDTO.InputDTO(IT_ALPHA));

		assertThat(cree).isNotNull();
		assertThat(cree.getIdTypeProduit()).isNotNull();
		assertThat(cree.getTypeProduit()).isEqualTo(IT_ALPHA);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline + 1L);

		/* ======================= ACT ======================= */
		this.controller.delete(new TypeProduitDTO.InputDTO(IT_ALPHA));

		/* ===================== ASSERT ====================== */
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_DELETE_OK + IT_ALPHA);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline);
		assertThat(this.compterTypeProduitEnBase(cree.getIdTypeProduit())).isZero();
		assertThat(this.compterTypeProduitParLibelleEnBase(IT_ALPHA)).isZero();

	} // __________________________________________________________________


	
	// -------------------------- count() -------------------------------//
	
	
	
	/**
	 * <div>
	 * <p>count(initial) : comptage exact avec preuve BD.</p>
	 * <ul>
	 * <li>retourne exactement le nombre de lignes présentes en base</li>
	 * <li>positionne le message exact cohérent avec le volume trouvé</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("count(initial) : comptage exact + message exact + aucune écriture BD")
	public void testCountInitialAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final String messageAttendu
			= baseline == 0L
				? TypeProduitICuService.MESSAGE_RECHERCHE_VIDE
				: TypeProduitICuService.MESSAGE_RECHERCHE_OK;

		/* ======================= ACT ======================= */
		final long retour = this.controller.count();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isEqualTo(baseline);
		assertThat(this.controller.getMessage()).isEqualTo(messageAttendu);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>count(après création) : comptage exact après ajout avec preuve BD.</p>
	 * <ul>
	 * <li>prouve d'abord physiquement l'ajout en base</li>
	 * <li>retourne exactement le nouveau volume</li>
	 * <li>positionne exactement {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("count(après création) : volume exact + message exact + preuve BD")
	public void testCountApresCreationAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesTypeProduitEnBase();
		final OutputDTO cree = this.controller.creer(new TypeProduitDTO.InputDTO(IT_ALPHA));

		assertThat(cree).isNotNull();
		assertThat(cree.getIdTypeProduit()).isNotNull();
		assertThat(cree.getTypeProduit()).isEqualTo(IT_ALPHA);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterTypeProduitParLibelleEnBase(IT_ALPHA)).isEqualTo(1L);

		/* ======================= ACT ======================= */
		final long retour = this.controller.count();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isEqualTo(baseline + 1L);
		assertThat(this.controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);
		assertThat(this.compterTousLesTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterTypeProduitParLibelleEnBase(IT_ALPHA)).isEqualTo(1L);
		assertThat(this.compterTypeProduitEnBase(cree.getIdTypeProduit())).isEqualTo(1L);

	} // __________________________________________________________________
	

	
	// ------------------------ getMessage() ----------------------------//
	
	
																				
	// ************************ METHODES PRIVEES **************************/

	
	
	/**
	 * <div>
	 * <p>Compte physiquement en base le nombre total de lignes de TYPES_PRODUIT.</p>
	 * </div>
	 *
	 * @return Long : nombre total de lignes.
	 */
	private Long compterTousLesTypeProduitEnBase() {

		return this.jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM TYPES_PRODUIT",
				Long.class);

	} // __________________________________________________________________

	

	/**
	 * <div>
	 * <p>Lit physiquement en base le libellé TYPE_PRODUIT pour un ID donné.</p>
	 * </div>
	 *
	 * @param pId : Long : ID_TYPE_PRODUIT.
	 * @return String : valeur de TYPE_PRODUIT.
	 */
	private String lireLibelleTypeProduitEnBase(final Long pId) {

		return this.jdbcTemplate.queryForObject(
				"SELECT TYPE_PRODUIT FROM TYPES_PRODUIT WHERE ID_TYPE_PRODUIT = ?",
				String.class,
				pId);

	} // __________________________________________________________________

	

	/**
	 * <div>
	 * <p>Compte physiquement en base le nombre de lignes portant un ID donné.</p>
	 * </div>
	 *
	 * @param pId : Long : ID_TYPE_PRODUIT.
	 * @return Long : nombre de lignes.
	 */
	private Long compterTypeProduitEnBase(final Long pId) {

		return this.jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM TYPES_PRODUIT WHERE ID_TYPE_PRODUIT = ?",
				Long.class,
				pId);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>Compte physiquement en base le nombre de lignes portant un libellé donné.</p>
	 * </div>
	 *
	 * @param pLibelle : String : TYPE_PRODUIT.
	 * @return Long : nombre de lignes.
	 */
	private Long compterTypeProduitParLibelleEnBase(final String pLibelle) {

		return this.jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM TYPES_PRODUIT WHERE TYPE_PRODUIT = ?",
				Long.class,
				pLibelle);

	} // __________________________________________________________________

	
	
}