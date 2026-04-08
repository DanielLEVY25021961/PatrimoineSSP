/* ********************************************************************* */
/* ****************** TEST INTEGRATION CONTROLLER WEB ****************** */
/* ********************************************************************* */
package levy.daniel.application.controllers.metier.produittype.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
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

import levy.daniel.application.controllers.metier.produittype.SousTypeProduitIController;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO.OutputDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.services.produittype.cu.SousTypeProduitICuService;
import levy.daniel.application.model.services.produittype.cu.TypeProduitICuService;

/**
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE SousTypeProduitWebControllerIntegrationTest.java :
 * </p>
 * <p>
 * Tests d'intégration complets du CONTROLLER ADAPTER WEB
 * {@link SousTypeProduitWebController}.
 * </p>
 * <p>
 * IMPORTANT :
 * <ul>
 * <li>on charge le vrai SERVICE UC SousTypeProduit,
 * le vrai SERVICE UC TypeProduit,
 * les vrais Gateways
 * et la vraie persistance ProduitType ;</li>
 * <li>on n'exécute pas de test HTTP bout-en-bout :
 * on instancie directement le controller
 * avec le vrai SERVICE UC injecté par Spring ;</li>
 * <li>on active les profils "web" et "dev"
 * (en plus de "test")
 * afin d'instancier les SERVICES UC concrets
 * et de rester cohérent avec le chantier WEB.</li>
 * </ul>
 * </p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 7 avril 2026
 */
@SpringBootTest(
		classes = SousTypeProduitWebControllerIntegrationTest.ConfigTest.class,
		webEnvironment = SpringBootTest.WebEnvironment.NONE,
		properties = { "spring.main.web-application-type=none" }
)
@ActiveProfiles({ "test", "web", "dev" })
@Tag(SousTypeProduitWebControllerIntegrationTest.TAG)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(
		scripts = {
				"classpath:/truncate-test.sql",
				"classpath:/data-test.sql"
		},
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class SousTypeProduitWebControllerIntegrationTest {

	// *************************** CONSTANTES ******************************/

	/** Tag JUnit : "controller-web-it". */
	public static final String TAG = "controller-web-it";

	/** Chaine blank : "   ". */
	public static final String ESPACES = "   ";

	/** TypeProduit IT controller web : "IT-CTRL-WEB-TP-PARENT-A". */
	public static final String IT_TP_PARENT_A = "IT-CTRL-WEB-TP-PARENT-A";

	/** TypeProduit parent absent : "IT-CTRL-WEB-TP-PARENT-ABSENT". */
	public static final String IT_TP_PARENT_ABSENT = "IT-CTRL-WEB-TP-PARENT-ABSENT";

	/** SousTypeProduit IT controller web : "IT-CTRL-WEB-STP-ALPHA". */
	public static final String IT_STP_ALPHA = "IT-CTRL-WEB-STP-ALPHA";

	/** SousTypeProduit IT controller web : "IT-CTRL-WEB-STP-BETA". */
	public static final String IT_STP_BETA = "IT-CTRL-WEB-STP-BETA";
	
	/**
	 * "classpath:/truncate-test.sql"
	 */
	public static final String CLASSPATH_TRUNCATE_TEST 
		= "classpath:/truncate-test.sql";

	// **************************** BEANS *********************************/

	/** JdbcTemplate de test pour preuve BD directe. */
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/** SERVICE UC SousTypeProduit réel injecté par Spring. */
	@Autowired
	private SousTypeProduitICuService service;

	/** SERVICE UC TypeProduit réel injecté par Spring. */
	@Autowired
	private TypeProduitICuService typeProduitService;

	/** Controller Web réel instancié sur le vrai SERVICE UC. */
	private SousTypeProduitWebController controller;

	// ************************ CONFIGURATION DE TEST *********************/

	/**
	 * <div>
	 * <p style="font-weight:bold;">CONFIGURATION DE TEST SPRING.</p>
	 * <ul>
	 * <li>scan applicatif limité
	 * (Services ProduitType + Persistance ProduitType),</li>
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
					"levy.daniel.application.model.services.produittype",
					"levy.daniel.application.persistence.metier.produittype"
			},
			excludeFilters = {
					@Filter(type = FilterType.REGEX, pattern = ".*IntegrationTest.*"),
					@Filter(type = FilterType.REGEX, pattern = ".*MockTest.*")
			}
	)
	public static class ConfigTest { // NOPMD by danyl on 07/04/2026 16:00
		/* configuration de test. */
	}

	// *************************** CONSTRUCTEURS ***************************/

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public SousTypeProduitWebControllerIntegrationTest() {
		super();
	}

	// *************************** INITIALISATION **************************/

	/**
	 * <div>
	 * <p>Instancie le controller réel sur le vrai SERVICE UC
	 * avant chaque test.</p>
	 * </div>
	 */
	@BeforeEach
	public void setUp() {
		this.controller = new SousTypeProduitWebController(this.service);
	}

	// *************************** METHODES *******************************/

	
	
	// ---------------------- creer(...) --------------------------------//

	
	
	/**
	 * <div>
	 * <p>creer(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link SousTypeProduitIController#MESSAGE_CREER_VUE_NULL}</li>
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
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.creer(null);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitIController.MESSAGE_CREER_VUE_NULL);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>creer(blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link SousTypeProduitIController#MESSAGE_CREER_VUE_BLANK}</li>
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
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		final InputDTO input = new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, ESPACES);

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.creer(input);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitIController.MESSAGE_CREER_VUE_BLANK);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>creer(parent blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link SousTypeProduitIController#MESSAGE_CREER_VUE_PARENT_BLANK}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("creer(parent blank) : retourne null + message MESSAGE_CREER_VUE_PARENT_BLANK + aucune écriture BD")
	public void testCreerParentBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		final InputDTO input = new SousTypeProduitDTO.InputDTO(ESPACES, IT_STP_ALPHA);

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.creer(input);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitIController.MESSAGE_CREER_VUE_PARENT_BLANK);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>creer(parent absent) : aucun TypeProduit persistant n'est trouvé.</p>
	 * <ul>
	 * <li>propage {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("creer(parent absent) : IllegalStateException + message exact MESSAGE_PAS_PARENT + aucune écriture BD")
	public void testCreerParentAbsent() {

		/* ===================== ARRANGE ===================== */
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		final InputDTO input = new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_ABSENT, IT_STP_BETA);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> this.controller.creer(input))
				.isInstanceOf(IllegalStateException.class);

		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_ABSENT, IT_STP_BETA))
				.isZero();

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>creer(ok) : preuve BD avec parent réel et round-trip complet.</p>
	 * <ul>
	 * <li>crée d'abord le parent persistant requis</li>
	 * <li>retourne un {@link OutputDTO} persistant</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_OK}</li>
	 * <li>augmente le comptage de 1</li>
	 * <li>prouve physiquement l'écriture en base</li>
	 * <li>prouve le rattachement au parent en base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("creer(ok) : preuve BD + parent prouvé + message exact + OutputDTO persistant")
	public void testCreerOkAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));

		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		final InputDTO input = new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA);

		/* ======================= ACT ======================= */
		final OutputDTO cree = this.controller.creer(input);

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(cree.getIdSousTypeProduit()).isNotNull();
		assertThat(cree.getSousTypeProduit()).isEqualTo(IT_STP_ALPHA);
		assertThat(cree.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);

		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_OK);

		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterSousTypeProduitEnBase(cree.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitEnBase(cree.getIdSousTypeProduit()))
				.isEqualTo(IT_STP_ALPHA);
		assertThat(this.lireParentSousTypeProduitEnBase(cree.getIdSousTypeProduit()))
				.isEqualTo(IT_TP_PARENT_A);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_A, IT_STP_ALPHA))
				.isEqualTo(1L);

	} // __________________________________________________________________

	
	
	// -------------------- rechercherTous() ----------------------------//

	
	
	/**
	 * <div>
	 * <p>rechercherTous(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste non nulle</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
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
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));

		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		final OutputDTO cree
			= this.controller.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA));
		final Long attendu = this.compterTousLesSousTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final java.util.List<SousTypeProduitDTO.OutputDTO> dtos
			= this.controller.rechercherTous();

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(dtos).isNotNull();
		assertThat(dtos.size()).isEqualTo(attendu.intValue());
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		assertThat(dtos)
				.extracting(SousTypeProduitDTO.OutputDTO::getSousTypeProduit)
				.contains(IT_STP_ALPHA);

		final OutputDTO dtoAlpha = dtos.stream()
				.filter(dto -> IT_STP_ALPHA.equals(dto.getSousTypeProduit()))
				.findFirst()
				.orElse(null);

		assertThat(dtoAlpha).isNotNull();
		assertThat(dtoAlpha.getIdSousTypeProduit()).isEqualTo(cree.getIdSousTypeProduit());
		assertThat(dtoAlpha.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);

		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterSousTypeProduitEnBase(cree.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_A, IT_STP_ALPHA))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitEnBase(cree.getIdSousTypeProduit()))
				.isEqualTo(IT_STP_ALPHA);
		assertThat(this.lireParentSousTypeProduitEnBase(cree.getIdSousTypeProduit()))
				.isEqualTo(IT_TP_PARENT_A);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTous(vide) : scénario nominal vide avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste vide</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>prouve physiquement que la table est vide</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Sql(
		scripts = { CLASSPATH_TRUNCATE_TEST },
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
	)
	@DisplayName("rechercherTous(vide) : liste vide + message exact + preuve BD")
	public void testRechercherTousVideAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final java.util.List<SousTypeProduitDTO.OutputDTO> dtos
			= this.controller.rechercherTous();

		/* ===================== ASSERT ====================== */
		assertThat(baseline).isZero();
		assertThat(dtos).isNotNull();
		assertThat(dtos).isEmpty();
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isZero();

	} // __________________________________________________________________
	

	
	// ------------------ rechercherTousString() ------------------------//

	
	
	/**
	 * <div>
	 * <p>rechercherTousString(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste non nulle de libellés</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>contient le libellé créé pendant le test</li>
	 * <li>reste cohérent avec le comptage physique en base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("rechercherTousString(ok) : message exact + cohérence count + présence du libellé créé + preuve BD")
	public void testRechercherTousStringOkAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));

		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		final OutputDTO cree
			= this.controller.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA));
		final Long attendu = this.compterTousLesSousTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final java.util.List<String> libelles = this.controller.rechercherTousString();

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(libelles).isNotNull();
		assertThat(libelles.size()).isEqualTo(attendu.intValue());
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		assertThat(libelles).contains(IT_STP_ALPHA);

		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterSousTypeProduitEnBase(cree.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_A, IT_STP_ALPHA))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitEnBase(cree.getIdSousTypeProduit()))
				.isEqualTo(IT_STP_ALPHA);
		assertThat(this.lireParentSousTypeProduitEnBase(cree.getIdSousTypeProduit()))
				.isEqualTo(IT_TP_PARENT_A);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousString(vide) : scénario nominal vide avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste vide</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>prouve physiquement que la table est vide</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Sql(
		scripts = { CLASSPATH_TRUNCATE_TEST },
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
	)
	@DisplayName("rechercherTousString(vide) : liste vide + message exact + preuve BD")
	public void testRechercherTousStringVideAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final java.util.List<String> libelles = this.controller.rechercherTousString();

		/* ===================== ASSERT ====================== */
		assertThat(baseline).isZero();
		assertThat(libelles).isNotNull();
		assertThat(libelles).isEmpty();
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isZero();

	} // __________________________________________________________________
	

	
	// ---------------- rechercherTousParPage(...) ----------------------//

	
	
	/**
	 * <div>
	 * <p>rechercherTousParPage(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link SousTypeProduitIController#MESSAGE_RECHERCHE_PAGINEE_REQUETE_NULL}</li>
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
		final long baseline = this.compterTousLesSousTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final levy.daniel.application.model.dto.pagination.ResultatPageDTO<OutputDTO> page
			= this.controller.rechercherTousParPage(null);

		/* ===================== ASSERT ====================== */
		assertThat(page).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(
						SousTypeProduitIController
							.MESSAGE_RECHERCHE_PAGINEE_REQUETE_NULL);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousParPage(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une page DTO non nulle</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_PAGINEE_OK}</li>
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
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));

		final long baseline = this.compterTousLesSousTypeProduitEnBase();
		final OutputDTO cree
			= this.controller.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA));
		final long attendu = this.compterTousLesSousTypeProduitEnBase();

		final levy.daniel.application.model.dto.pagination.RequetePageDTO requete
			= new levy.daniel.application.model.dto.pagination.RequetePageDTO(
					1,
					10,
					java.util.List.of(
							new levy.daniel.application.model.dto.pagination.TriSpecDTO(
									"sousTypeProduit",
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
				.extracting(SousTypeProduitDTO.OutputDTO::getSousTypeProduit)
				.contains(IT_STP_ALPHA);

		final OutputDTO dtoAlpha = page.getContent().stream()
				.filter(dto -> IT_STP_ALPHA.equals(dto.getSousTypeProduit()))
				.findFirst()
				.orElse(null);

		assertThat(dtoAlpha).isNotNull();
		assertThat(dtoAlpha.getIdSousTypeProduit()).isEqualTo(cree.getIdSousTypeProduit());
		assertThat(dtoAlpha.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterSousTypeProduitEnBase(cree.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_A, IT_STP_ALPHA))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitEnBase(cree.getIdSousTypeProduit()))
				.isEqualTo(IT_STP_ALPHA);
		assertThat(this.lireParentSousTypeProduitEnBase(cree.getIdSousTypeProduit()))
				.isEqualTo(IT_TP_PARENT_A);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousParPage(vide) : scénario nominal vide avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une page DTO non nulle et vide</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_PAGINEE_OK}</li>
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
		final long baseline = this.compterTousLesSousTypeProduitEnBase();
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
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isZero();

	} // __________________________________________________________________

	
	
	// -------------------- findByLibelle(...) --------------------------//

	
	
	// ----------------- findByLibelleRapide(...) -----------------------//

	
	
	// ------------------- findAllByParent(...) -------------------------//

	
	
	// ---------------------- findByDTO(...) ----------------------------//

	
	
	// ----------------------- findById(...) ----------------------------//

	
	
	// ------------------------ update(...) -----------------------------//

	
	
	// ------------------------ delete(...) -----------------------------//

	
	
	// -------------------------- count() -------------------------------//

	
	
	// ------------------------ getMessage() ----------------------------//

	
		
	// ************************ METHODES PRIVEES **************************/

	
	
	/**
	 * <div>
	 * <p>Compte physiquement en base le nombre total de lignes
	 * de SOUS_TYPES_PRODUIT.</p>
	 * </div>
	 *
	 * @return Long : nombre total de lignes.
	 */
	private Long compterTousLesSousTypeProduitEnBase() {

		return this.jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT",
				Long.class);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>Compte le nombre de lignes portant l'identifiant transmis
	 * dans SOUS_TYPES_PRODUIT.</p>
	 * </div>
	 *
	 * @param pId : Long : identifiant physique du SousTypeProduit.
	 * @return Long : nombre de lignes trouvées.
	 */
	private Long compterSousTypeProduitEnBase(final Long pId) {

		return this.jdbcTemplate.queryForObject(
				"SELECT COUNT(*) "
				+ "FROM SOUS_TYPES_PRODUIT "
				+ "WHERE ID_SOUS_TYPE_PRODUIT = ?",
				Long.class,
				pId);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>Lit le libellé stocké en base pour l'identifiant transmis.</p>
	 * </div>
	 *
	 * @param pId : Long : identifiant physique du SousTypeProduit.
	 * @return String : libellé SOUS_TYPE_PRODUIT lu en base.
	 */
	private String lireLibelleSousTypeProduitEnBase(final Long pId) {

		return this.jdbcTemplate.queryForObject(
				"SELECT SOUS_TYPE_PRODUIT "
				+ "FROM SOUS_TYPES_PRODUIT "
				+ "WHERE ID_SOUS_TYPE_PRODUIT = ?",
				String.class,
				pId);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>Lit le libellé du TypeProduit parent stocké en base
	 * pour l'identifiant transmis.</p>
	 * </div>
	 *
	 * @param pId : Long : identifiant physique du SousTypeProduit.
	 * @return String : libellé du parent lu en base.
	 */
	private String lireParentSousTypeProduitEnBase(final Long pId) {

		return this.jdbcTemplate.queryForObject(
				"SELECT tp.TYPE_PRODUIT "
				+ "FROM SOUS_TYPES_PRODUIT stp "
				+ "INNER JOIN TYPES_PRODUIT tp "
				+ "ON stp.TYPE_PRODUIT = tp.ID_TYPE_PRODUIT "
				+ "WHERE stp.ID_SOUS_TYPE_PRODUIT = ?",
				String.class,
				pId);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>Compte le nombre de lignes physiques pour un couple
	 * parent / sous-type donné.</p>
	 * </div>
	 *
	 * @param pParent : String : libellé du TypeProduit parent.
	 * @param pSousType : String : libellé du SousTypeProduit.
	 * @return Long : nombre de lignes trouvées pour ce couple.
	 */
	private Long compterSousTypeProduitParCoupleEnBase(
			final String pParent,
			final String pSousType) {

		return this.jdbcTemplate.queryForObject(
				"SELECT COUNT(*) "
				+ "FROM SOUS_TYPES_PRODUIT stp "
				+ "INNER JOIN TYPES_PRODUIT tp "
				+ "ON stp.TYPE_PRODUIT = tp.ID_TYPE_PRODUIT "
				+ "WHERE tp.TYPE_PRODUIT = ? "
				+ "AND stp.SOUS_TYPE_PRODUIT = ?",
				Long.class,
				pParent,
				pSousType);

	} // __________________________________________________________________

	
	
} // FIN DE LA CLASSE SousTypeProduitWebControllerIntegrationTest.---------