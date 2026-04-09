/* ********************************************************************* */
/* ****************** TEST INTEGRATION CONTROLLER WEB ****************** */
/* ********************************************************************* */
package levy.daniel.application.controllers.metier.produittype.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

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

import levy.daniel.application.controllers.metier.produittype.ProduitIController;
import levy.daniel.application.model.dto.pagination.DirectionTriDTO;
import levy.daniel.application.model.dto.pagination.RequetePageDTO;
import levy.daniel.application.model.dto.pagination.ResultatPageDTO;
import levy.daniel.application.model.dto.pagination.TriSpecDTO;
import levy.daniel.application.model.dto.produittype.ProduitDTO;
import levy.daniel.application.model.dto.produittype.ProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.ProduitDTO.OutputDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.services.produittype.cu.ProduitICuService;
import levy.daniel.application.model.services.produittype.cu.SousTypeProduitICuService;
import levy.daniel.application.model.services.produittype.cu.TypeProduitICuService;

/**
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE ProduitWebControllerIntegrationTest.java :
 * </p>
 * <p>
 * Tests d'intégration complets du CONTROLLER ADAPTER WEB
 * {@link ProduitWebController}.
 * </p>
 * <p>
 * Vérifie l'implémentation des contrats du PORT
 * {@link ProduitIController},
 * avec preuve BD directe.
 * </p>
 * </div>
 *
 * <div>
 * <p style="font-weight:bold;">IMPORTANT :</p>
 * <ul>
 * <li>on charge le vrai SERVICE UC Produit,
 * les vrais SERVICES UC parents requis,
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
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 9 avril 2026
 */
@SpringBootTest(
		classes = ProduitWebControllerIntegrationTest.ConfigTest.class,
		webEnvironment = SpringBootTest.WebEnvironment.NONE,
		properties = { "spring.main.web-application-type=none" }
)
@ActiveProfiles({ "test", "web", "dev" })
@Tag(ProduitWebControllerIntegrationTest.TAG)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(
		scripts = {
				"classpath:/truncate-test.sql",
				"classpath:/data-test.sql"
		},
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class ProduitWebControllerIntegrationTest {

	// *************************** CONSTANTES ******************************/

	/** Tag JUnit : "controller-web-it". */
	public static final String TAG = "controller-web-it";

	/** Chaine blank : "   ". */
	public static final String ESPACES = "   ";

	/** TypeProduit IT controller web A : "IT-CTRL-WEB-TP-PARENT-A". */
	public static final String IT_TP_PARENT_A = "IT-CTRL-WEB-TP-PARENT-A";

	/** TypeProduit IT controller web B : "IT-CTRL-WEB-TP-PARENT-B". */
	public static final String IT_TP_PARENT_B = "IT-CTRL-WEB-TP-PARENT-B";

	/** SousTypeProduit IT controller web A : "IT-CTRL-WEB-STP-PARENT-A". */
	public static final String IT_STP_PARENT_A = "IT-CTRL-WEB-STP-PARENT-A";

	/** SousTypeProduit IT controller web B : "IT-CTRL-WEB-STP-PARENT-B". */
	public static final String IT_STP_PARENT_B = "IT-CTRL-WEB-STP-PARENT-B";

	/** SousTypeProduit parent absent. */
	public static final String IT_STP_PARENT_ABSENT
		= "IT-CTRL-WEB-STP-PARENT-ABSENT";

	/** Produit IT alpha. */
	public static final String IT_PRD_ALPHA = "IT-CTRL-WEB-PRD-ALPHA";

	/** Produit IT beta. */
	public static final String IT_PRD_BETA = "IT-CTRL-WEB-PRD-BETA";

	/** Produit IT gamma. */
	public static final String IT_PRD_GAMMA = "IT-CTRL-WEB-PRD-GAMMA";

	/** Produit IT delta. */
	public static final String IT_PRD_DELTA = "IT-CTRL-WEB-PRD-DELTA";

	/** Produit introuvable DTO/recherche. */
	public static final String IT_PRD_INEXISTANT_XYZ
		= "IT-CTRL-WEB-PRD-INEXISTANT-XYZ";

	/** Produit update introuvable. */
	public static final String IT_PRD_INEXISTANT_UPDATE
		= "IT-CTRL-WEB-PRD-INEXISTANT-UPDATE";

	/** Produit delete introuvable. */
	public static final String IT_PRD_INEXISTANT_DELETE
		= "IT-CTRL-WEB-PRD-INEXISTANT-DELETE";

	/** Produit update ok. */
	public static final String IT_PRD_UPDATE_OK
		= "IT-CTRL-WEB-PRD-UPDATE-OK";

	/** Produit delete ok. */
	public static final String IT_PRD_DELETE_OK
		= "IT-CTRL-WEB-PRD-DELETE-OK";

	/** Produit count 01. */
	public static final String IT_PRD_COUNT_01
		= "IT-CTRL-WEB-PRD-COUNT-01";

	/** Produit count 02. */
	public static final String IT_PRD_COUNT_02
		= "IT-CTRL-WEB-PRD-COUNT-02";

	/** Produit recherche rapide ABC. */
	public static final String IT_PRD_SEARCH_ABC
		= "IT-CTRL-WEB-PRD-SEARCH-ABC";

	/** Produit recherche rapide ABD. */
	public static final String IT_PRD_SEARCH_ABD
		= "IT-CTRL-WEB-PRD-SEARCH-ABD";

	/** Préfixe recherche rapide AB. */
	public static final String IT_PRD_SEARCH_PREFIXE_AB
		= "IT-CTRL-WEB-PRD-SEARCH-AB";

	/** Préfixe recherche rapide QQ. */
	public static final String IT_PRD_SEARCH_PREFIXE_QQ
		= "IT-CTRL-WEB-PRD-SEARCH-QQ";

	/** "classpath:/truncate-test.sql" */
	public static final String CLASSPATH_TRUNCATE_TEST
		= "classpath:/truncate-test.sql";

	/** "SELECT COUNT(*) FROM PRODUITS" */
	public static final String SELECT_COUNT_FROM_PRODUITS
		= "SELECT COUNT(*) FROM PRODUITS";

	/** "SELECT COUNT(*) FROM PRODUITS WHERE ID_PRODUIT = ?" */
	public static final String SELECT_COUNT_FROM_PRODUITS_BY_ID
		= "SELECT COUNT(*) FROM PRODUITS WHERE ID_PRODUIT = ?";

	/** "SELECT PRODUIT FROM PRODUITS WHERE ID_PRODUIT = ?" */
	public static final String SELECT_PRODUIT_LIBELLE_BY_ID
		= "SELECT PRODUIT FROM PRODUITS WHERE ID_PRODUIT = ?";

	/** Parent SousTypeProduit d'un Produit par son ID. */
	public static final String SELECT_PARENT_STP_BY_ID
		= "SELECT stp.SOUS_TYPE_PRODUIT "
				+ "FROM PRODUITS p "
				+ "INNER JOIN SOUS_TYPES_PRODUIT stp "
				+ "ON p.SOUS_TYPE_PRODUIT = stp.ID_SOUS_TYPE_PRODUIT "
				+ "WHERE p.ID_PRODUIT = ?";

	/** Parent TypeProduit d'un Produit par son ID. */
	public static final String SELECT_PARENT_TP_BY_ID
		= "SELECT tp.TYPE_PRODUIT "
				+ "FROM PRODUITS p "
				+ "INNER JOIN SOUS_TYPES_PRODUIT stp "
				+ "ON p.SOUS_TYPE_PRODUIT = stp.ID_SOUS_TYPE_PRODUIT "
				+ "INNER JOIN TYPES_PRODUIT tp "
				+ "ON stp.TYPE_PRODUIT = tp.ID_TYPE_PRODUIT "
				+ "WHERE p.ID_PRODUIT = ?";

	// **************************** BEANS *********************************/

	/** JdbcTemplate de test pour preuve BD directe. */
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/** SERVICE UC Produit réel injecté par Spring. */
	@Autowired
	private ProduitICuService service;

	/** SERVICE UC TypeProduit réel injecté par Spring. */
	@Autowired
	private TypeProduitICuService typeProduitService;

	/** SERVICE UC SousTypeProduit réel injecté par Spring. */
	@Autowired
	private SousTypeProduitICuService sousTypeProduitService;

	/** Controller Web réel injecté par Spring. */
	@Autowired
	private ProduitWebController controller;
	
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
					"levy.daniel.application.controllers.metier.produittype",
					"levy.daniel.application.model.services.produittype",
					"levy.daniel.application.persistence.metier.produittype"
			},
			excludeFilters = {
					@Filter(type = FilterType.REGEX, pattern = ".*IntegrationTest.*"),
					@Filter(type = FilterType.REGEX, pattern = ".*MockTest.*")
			}
	)
	public static class ConfigTest { // NOPMD by danyl on 09/04/2026 22:40
		/* configuration de test. */
	}



	// *************************** CONSTRUCTEURS ***************************/

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public ProduitWebControllerIntegrationTest() {
		super();
	}

	// *************************** INITIALISATION **************************/


	// *************************** METHODES *******************************/



	// ---------------------- creer(...) --------------------------------//



	/**
	 * <div>
	 * <p>creer(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitIController#MESSAGE_CREER_VUE_NULL}</li>
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
		final long baseline = this.compterTousLesProduitEnBase();

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.creer(null);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitIController.MESSAGE_CREER_VUE_NULL);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>creer(blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitIController#MESSAGE_CREER_VUE_BLANK}</li>
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
		final long baseline = this.compterTousLesProduitEnBase();
		final InputDTO input
			= new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, ESPACES);

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.creer(input);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitIController.MESSAGE_CREER_VUE_BLANK);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>creer(parent blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link ProduitIController#MESSAGE_CREER_VUE_PARENT_BLANK}</li>
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
		final long baseline = this.compterTousLesProduitEnBase();
		final InputDTO input
			= new ProduitDTO.InputDTO(IT_TP_PARENT_A, ESPACES, IT_PRD_ALPHA);

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.creer(input);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitIController.MESSAGE_CREER_VUE_PARENT_BLANK);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>creer(ok) : test béton avec preuve BD.</p>
	 * <ul>
	 * <li>retourne un {@link OutputDTO} persistant</li>
	 * <li>positionne exactement {@link ProduitICuService#MESSAGE_CREER_OK}</li>
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
		final long baseline = this.compterTousLesProduitEnBase();
		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);
		final InputDTO input
			= new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_ALPHA);

		/* ======================= ACT ======================= */
		final OutputDTO cree = this.controller.creer(input);

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(cree.getIdProduit()).isNotNull();
		assertThat(cree.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);
		assertThat(cree.getSousTypeProduit()).isEqualTo(IT_STP_PARENT_A);
		assertThat(cree.getProduit()).isEqualTo(IT_PRD_ALPHA);
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_CREER_OK);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterProduitEnBase(cree.getIdProduit())).isEqualTo(1L);
		assertThat(this.compterProduitParCoupleEnBase(
				IT_TP_PARENT_A,
				IT_STP_PARENT_A,
				IT_PRD_ALPHA)).isEqualTo(1L);
		assertThat(this.lireLibelleProduitEnBase(cree.getIdProduit()))
				.isEqualTo(IT_PRD_ALPHA);
		assertThat(this.lireSousTypeProduitParentEnBase(cree.getIdProduit()))
				.isEqualTo(IT_STP_PARENT_A);
		assertThat(this.lireTypeProduitParentEnBase(cree.getIdProduit()))
				.isEqualTo(IT_TP_PARENT_A);

	} // __________________________________________________________________



	// ------------------- rechercherTous() ------------------------------//



	/**
	 * <div>
	 * <p>rechercherTous(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste non nulle</li>
	 * <li>positionne exactement {@link ProduitICuService#MESSAGE_RECHERCHE_OK}</li>
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
		final long baseline = this.compterTousLesProduitEnBase();
		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);
		final OutputDTO cree = this.controller.creer(
				new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_ALPHA));
		final long attendu = this.compterTousLesProduitEnBase();

		/* ======================= ACT ======================= */
		final List<OutputDTO> dtos = this.controller.rechercherTous();

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(dtos).isNotNull();
		assertThat(dtos.size()).isEqualTo((int) attendu);
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);
		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.contains(IT_PRD_ALPHA);

		final OutputDTO dtoAlpha = dtos.stream()
				.filter(dto -> IT_PRD_ALPHA.equals(dto.getProduit()))
				.findFirst()
				.orElse(null);

		assertThat(dtoAlpha).isNotNull();
		assertThat(dtoAlpha.getIdProduit()).isEqualTo(cree.getIdProduit());
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterProduitEnBase(cree.getIdProduit())).isEqualTo(1L);
		assertThat(this.compterProduitParCoupleEnBase(
				IT_TP_PARENT_A,
				IT_STP_PARENT_A,
				IT_PRD_ALPHA)).isEqualTo(1L);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTous(vide) : scénario nominal vide avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste vide</li>
	 * <li>positionne exactement {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
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
		final long baseline = this.compterTousLesProduitEnBase();

		/* ======================= ACT ======================= */
		final List<OutputDTO> dtos = this.controller.rechercherTous();

		/* ===================== ASSERT ====================== */
		assertThat(baseline).isZero();
		assertThat(dtos).isNotNull();
		assertThat(dtos).isEmpty();
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);
		assertThat(this.compterTousLesProduitEnBase()).isZero();

	} // __________________________________________________________________



	// ------------------ rechercherTousString() -------------------------//



	/**
	 * <div>
	 * <p>rechercherTousString(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste de String non nulle</li>
	 * <li>positionne exactement {@link ProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>contient la création du test</li>
	 * <li>reste cohérent avec la preuve physique en base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("rechercherTousString(ok) : liste non nulle + message exact + présence de la création + preuve BD")
	public void testRechercherTousStringOkAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);
		final OutputDTO cree = this.controller.creer(
				new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_ALPHA));

		/* ======================= ACT ======================= */
		final List<String> libelles = this.controller.rechercherTousString();

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(libelles).isNotNull();
		assertThat(libelles).contains(IT_PRD_ALPHA);
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);
		assertThat(this.compterProduitParCoupleEnBase(
				IT_TP_PARENT_A,
				IT_STP_PARENT_A,
				IT_PRD_ALPHA)).isEqualTo(1L);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousString(vide) : scénario nominal vide avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste vide</li>
	 * <li>positionne exactement {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
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
		final long baseline = this.compterTousLesProduitEnBase();

		/* ======================= ACT ======================= */
		final List<String> libelles = this.controller.rechercherTousString();

		/* ===================== ASSERT ====================== */
		assertThat(baseline).isZero();
		assertThat(libelles).isNotNull();
		assertThat(libelles).isEmpty();
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________



	// ---------------- rechercherTousParPage(...) ----------------------//



	/**
	 * <div>
	 * <p>rechercherTousParPage(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link ProduitIController#MESSAGE_RECHERCHE_PAGINEE_REQUETE_NULL}</li>
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
		final long baseline = this.compterTousLesProduitEnBase();

		/* ======================= ACT ======================= */
		final ResultatPageDTO<OutputDTO> retour
			= this.controller.rechercherTousParPage(null);

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(
						ProduitIController.MESSAGE_RECHERCHE_PAGINEE_REQUETE_NULL);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousParPage(ok) : cohérence pagination + preuve BD.</p>
	 * <ul>
	 * <li>retourne un résultat paginé non nul</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_PAGINEE_OK}</li>
	 * <li>retourne la pagination attendue pour la VUE</li>
	 * <li>reste cohérent avec la preuve physique en base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("rechercherTousParPage(ok) : pagination DTO cohérente + message exact + preuve BD")
	public void testRechercherTousParPageOkAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);
		this.controller.creer(new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_GAMMA));
		this.controller.creer(new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_DELTA));

		final RequetePageDTO requete
			= new RequetePageDTO(
					1,
					10,
					List.of(new TriSpecDTO("produit", DirectionTriDTO.ASC)));

		/* ======================= ACT ======================= */
		final ResultatPageDTO<OutputDTO> page
			= this.controller.rechercherTousParPage(requete);

		/* ===================== ASSERT ====================== */
		assertThat(page).isNotNull();
		assertThat(page.getPageNumber()).isEqualTo(1);
		assertThat(page.getPageSize()).isEqualTo(10);
		assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2L);
		assertThat(page.getContent())
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.contains(IT_PRD_GAMMA, IT_PRD_DELTA);
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

	} // __________________________________________________________________



	// -------------------- findByLibelle(...) --------------------------//



	/**
	 * <div>
	 * <p>findByLibelle(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitIController#MESSAGE_FIND_BY_LIBELLE_VUE_NULL}</li>
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
		final long baseline = this.compterTousLesProduitEnBase();

		/* ======================= ACT ======================= */
		final List<OutputDTO> dtos = this.controller.findByLibelle(null);

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitIController.MESSAGE_FIND_BY_LIBELLE_VUE_NULL);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitIController#MESSAGE_FIND_BY_LIBELLE_VUE_BLANK}</li>
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
		final long baseline = this.compterTousLesProduitEnBase();

		/* ======================= ACT ======================= */
		final List<OutputDTO> dtos = this.controller.findByLibelle(ESPACES);

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitIController.MESSAGE_FIND_BY_LIBELLE_VUE_BLANK);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste non nulle</li>
	 * <li>positionne exactement {@link ProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>retourne les DTO correspondant au bon libellé</li>
	 * <li>ne modifie pas physiquement la base lors de la recherche</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelle(ok) : liste trouvée + message exact + preuve BD")
	public void testFindByLibelleOkAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesProduitEnBase();

		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);
		this.creerParentsBeton(IT_TP_PARENT_B, IT_STP_PARENT_B);

		final OutputDTO creeA = this.controller.creer(
				new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_ALPHA));
		final OutputDTO creeB = this.controller.creer(
				new ProduitDTO.InputDTO(IT_TP_PARENT_B, IT_STP_PARENT_B, IT_PRD_ALPHA));

		/* ======================= ACT ======================= */
		final List<OutputDTO> dtos = this.controller.findByLibelle(IT_PRD_ALPHA);

		/* ===================== ASSERT ====================== */
		assertThat(creeA).isNotNull();
		assertThat(creeB).isNotNull();
		assertThat(dtos).isNotNull();
		assertThat(dtos).hasSize(2);
		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.containsExactlyInAnyOrder(IT_PRD_ALPHA, IT_PRD_ALPHA);
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline + 2L);

	} // __________________________________________________________________
	


	/**
	 * <div>
	 * <p>findByLibelle(absent) : scénario nominal sans résultat avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste vide</li>
	 * <li>positionne exactement {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelle(absent) : retourne liste vide + message exact + aucune écriture BD")
	public void testFindByLibelleAbsentAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesProduitEnBase();

		/* ======================= ACT ======================= */
		final List<OutputDTO> dtos = this.controller.findByLibelle(IT_PRD_INEXISTANT_XYZ);

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNotNull();
		assertThat(dtos).isEmpty();
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________
	


	// ------------------ findByLibelleRapide(...) ----------------------//



	/**
	 * <div>
	 * <p>findByLibelleRapide(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link ProduitIController#MESSAGE_FIND_BY_LIBELLE_RAPIDE_VUE_NULL}</li>
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
		final long baseline = this.compterTousLesProduitEnBase();

		/* ======================= ACT ======================= */
		final List<OutputDTO> dtos = this.controller.findByLibelleRapide(null);

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitIController.MESSAGE_FIND_BY_LIBELLE_RAPIDE_VUE_NULL);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelleRapide(blank) : délégation réelle au service.</p>
	 * <ul>
	 * <li>ne bloque pas localement un contenu blank</li>
	 * <li>retourne une liste non nulle</li>
	 * <li>mémorise le message du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelleRapide(blank) : délégation service + liste non nulle + message exact")
	public void testFindByLibelleRapideBlankAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);
		this.controller.creer(
				new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_GAMMA));
		this.controller.creer(
				new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_DELTA));

		/* ======================= ACT ======================= */
		final List<OutputDTO> dtos = this.controller.findByLibelleRapide(ESPACES);

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNotNull();
		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.contains(IT_PRD_GAMMA, IT_PRD_DELTA);
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelleRapide(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste non nulle</li>
	 * <li>positionne exactement {@link ProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>retourne uniquement les produits correspondant au préfixe</li>
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
		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);
		this.controller.creer(
				new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_SEARCH_ABC));
		this.controller.creer(
				new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_SEARCH_ABD));
		this.controller.creer(
				new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_DELTA));

		/* ======================= ACT ======================= */
		final List<OutputDTO> dtos
			= this.controller.findByLibelleRapide(IT_PRD_SEARCH_PREFIXE_AB);

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNotNull();
		assertThat(dtos).hasSize(2);
		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.containsExactlyInAnyOrder(IT_PRD_SEARCH_ABC, IT_PRD_SEARCH_ABD);
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelleRapide(absent) : scénario nominal sans résultat avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste vide</li>
	 * <li>positionne exactement {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelleRapide(absent) : retourne liste vide + message exact + aucune écriture BD")
	public void testFindByLibelleRapideAbsentAvecPreuveBd() throws Exception {

		/* ======================= ACT ======================= */
		final List<OutputDTO> dtos
			= this.controller.findByLibelleRapide(IT_PRD_SEARCH_PREFIXE_QQ);

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNotNull();
		assertThat(dtos).isEmpty();
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________



	// ------------------- findAllByParent(...) -------------------------//



	/**
	 * <div>
	 * <p>findAllByParent(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link ProduitIController#MESSAGE_FIND_ALL_BY_PARENT_VUE_NULL}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findAllByParent(null) : retourne null + message local + aucune écriture BD")
	public void testFindAllByParentNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesProduitEnBase();

		/* ======================= ACT ======================= */
		final List<OutputDTO> dtos = this.controller.findAllByParent(null);

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitIController.MESSAGE_FIND_ALL_BY_PARENT_VUE_NULL);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(parent blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link ProduitIController#MESSAGE_FIND_ALL_BY_PARENT_VUE_PARENT_BLANK}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findAllByParent(parent blank) : retourne null + message local + aucune écriture BD")
	public void testFindAllByParentParentBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesProduitEnBase();
		final SousTypeProduitDTO.InputDTO parentDto
			= new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, ESPACES);

		/* ======================= ACT ======================= */
		final List<OutputDTO> dtos = this.controller.findAllByParent(parentDto);

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitIController.MESSAGE_FIND_ALL_BY_PARENT_VUE_PARENT_BLANK);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(vide) : délégation réelle au service.</p>
	 * <ul>
	 * <li>retourne une liste vide</li>
	 * <li>positionne exactement {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findAllByParent(vide) : liste vide + message exact + aucune écriture BD")
	public void testFindAllByParentVideAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		this.creerParentsBeton(IT_TP_PARENT_B, IT_STP_PARENT_B);

		/* ======================= ACT ======================= */
		final List<OutputDTO> dtos = this.controller.findAllByParent(
				new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_B, IT_STP_PARENT_B));

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNotNull();
		assertThat(dtos).isEmpty();
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste non nulle</li>
	 * <li>positionne exactement {@link ProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>retourne uniquement les produits du bon parent</li>
	 * <li>ne modifie pas physiquement la base lors de la recherche</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findAllByParent(ok) : liste trouvée + message exact + preuve BD")
	public void testFindAllByParentOkAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		this.creerParentsBeton(IT_TP_PARENT_B, IT_STP_PARENT_B);
		this.controller.creer(
				new ProduitDTO.InputDTO(IT_TP_PARENT_B, IT_STP_PARENT_B, IT_PRD_GAMMA));
		this.controller.creer(
				new ProduitDTO.InputDTO(IT_TP_PARENT_B, IT_STP_PARENT_B, IT_PRD_DELTA));

		/* ======================= ACT ======================= */
		final List<OutputDTO> dtos = this.controller.findAllByParent(
				new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_B, IT_STP_PARENT_B));

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNotNull();
		assertThat(dtos).hasSize(2);
		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.containsExactlyInAnyOrder(IT_PRD_GAMMA, IT_PRD_DELTA);
		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getSousTypeProduit)
				.containsExactlyInAnyOrder(IT_STP_PARENT_B, IT_STP_PARENT_B);
		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getTypeProduit)
				.containsExactlyInAnyOrder(IT_TP_PARENT_B, IT_TP_PARENT_B);
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(parent absent) : propagation brute de l'exception du service.</p>
	 * <ul>
	 * <li>propage l'exception du service</li>
	 * <li>positionne exactement {@link ProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("findAllByParent(parent absent) : propage l'exception + message exact")
	public void testFindAllByParentPasParent() {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitDTO.InputDTO parentDto
			= new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_B, IT_STP_PARENT_ABSENT);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> this.controller.findAllByParent(parentDto))
				.isInstanceOfAny(RuntimeException.class, IllegalStateException.class);

		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

	} // __________________________________________________________________



	// ---------------------- findByDTO(...) ----------------------------//



	/**
	 * <div>
	 * <p>findByDTO(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitIController#MESSAGE_FIND_BY_DTO_VUE_NULL}</li>
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
		final long baseline = this.compterTousLesProduitEnBase();

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.findByDTO(null);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitIController.MESSAGE_FIND_BY_DTO_VUE_NULL);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByDTO(parent blank) : le controller délègue au service.</p>
	 * <ul>
	 * <li>ne bloque pas localement un parent blank</li>
	 * <li>laisse le service lever l'exception</li>
	 * <li>positionne exactement {@link ProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("findByDTO(parent blank) : propage l'exception + message exact")
	public void testFindByDTOParentBlank() {

		/* ===================== ARRANGE ===================== */
		final InputDTO dto
			= new ProduitDTO.InputDTO(IT_TP_PARENT_A, ESPACES, IT_PRD_ALPHA);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> this.controller.findByDTO(dto))
				.isInstanceOf(IllegalStateException.class);

		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByDTO(absent) : scénario nominal sans résultat avec preuve BD.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
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
		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);
		this.controller.creer(
				new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_ALPHA));

		final InputDTO input
			= new ProduitDTO.InputDTO(
					IT_TP_PARENT_A,
					IT_STP_PARENT_A,
					IT_PRD_BETA);

		final long baseline = this.compterTousLesProduitEnBase();

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.findByDTO(input);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByDTO(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne un {@link OutputDTO} non nul</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_SUCCES_RECHERCHE}</li>
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
		final long baseline = this.compterTousLesProduitEnBase();
		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);
		final InputDTO inputCreation
			= new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_ALPHA);
		final OutputDTO cree = this.controller.creer(inputCreation);
		final InputDTO inputRecherche
			= new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_ALPHA);

		/* ======================= ACT ======================= */
		final OutputDTO trouve = this.controller.findByDTO(inputRecherche);

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(trouve).isNotNull();
		assertThat(trouve.getIdProduit()).isEqualTo(cree.getIdProduit());
		assertThat(trouve.getProduit()).isEqualTo(IT_PRD_ALPHA);
		assertThat(trouve.getSousTypeProduit()).isEqualTo(IT_STP_PARENT_A);
		assertThat(trouve.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_SUCCES_RECHERCHE);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline + 1L);

	} // __________________________________________________________________



	// ----------------------- findById(...) ----------------------------//



	/**
	 * <div>
	 * <p>findById(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitIController#MESSAGE_FIND_BY_ID_VUE_NULL}</li>
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
		final long baseline = this.compterTousLesProduitEnBase();

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.findById(null);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitIController.MESSAGE_FIND_BY_ID_VUE_NULL);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findById(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne un {@link OutputDTO} non nul</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_SUCCES_RECHERCHE}</li>
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
		final long baseline = this.compterTousLesProduitEnBase();
		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);
		final OutputDTO cree = this.controller.creer(
				new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_ALPHA));

		/* ======================= ACT ======================= */
		final OutputDTO trouve = this.controller.findById(cree.getIdProduit());

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(trouve).isNotNull();
		assertThat(trouve.getIdProduit()).isEqualTo(cree.getIdProduit());
		assertThat(trouve.getProduit()).isEqualTo(IT_PRD_ALPHA);
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_SUCCES_RECHERCHE);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterProduitEnBase(cree.getIdProduit())).isEqualTo(1L);
		assertThat(this.lireLibelleProduitEnBase(cree.getIdProduit()))
				.isEqualTo(IT_PRD_ALPHA);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findById(absent) : scénario nominal sans résultat avec preuve BD.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_OBJ_INTROUVABLE} + id</li>
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
		final long baseline = this.compterTousLesProduitEnBase();
		final Long id = 999_999_999L;

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.findById(id);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_OBJ_INTROUVABLE + id);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);
		assertThat(this.compterProduitEnBase(id)).isZero();

	} // __________________________________________________________________



	// ------------------------ update(...) -----------------------------//



	/**
	 * <div>
	 * <p>update(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitIController#MESSAGE_UPDATE_VUE_NULL}</li>
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
		final long baseline = this.compterTousLesProduitEnBase();

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.update(null);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitIController.MESSAGE_UPDATE_VUE_NULL);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitIController#MESSAGE_UPDATE_VUE_BLANK}</li>
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
		final long baseline = this.compterTousLesProduitEnBase();
		final InputDTO input
			= new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, ESPACES);

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.update(input);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitIController.MESSAGE_UPDATE_VUE_BLANK);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(parent blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link ProduitIController#MESSAGE_UPDATE_VUE_PARENT_BLANK}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(parent blank) : retourne null + message local + aucune écriture BD")
	public void testUpdateParentBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesProduitEnBase();
		final InputDTO input
			= new ProduitDTO.InputDTO(IT_TP_PARENT_A, ESPACES, IT_PRD_ALPHA);

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.update(input);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitIController.MESSAGE_UPDATE_VUE_PARENT_BLANK);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne un {@link OutputDTO} non nul</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_MODIF_OK} + libellé</li>
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
		final long baseline = this.compterTousLesProduitEnBase();
		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);
		final OutputDTO cree = this.controller.creer(
				new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_ALPHA));
		final InputDTO inputModification
			= new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_ALPHA);

		/* ======================= ACT ======================= */
		final OutputDTO modifie = this.controller.update(inputModification);

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(modifie).isNotNull();
		assertThat(modifie.getIdProduit()).isEqualTo(cree.getIdProduit());
		assertThat(modifie.getProduit()).isEqualTo(IT_PRD_ALPHA);
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_MODIF_OK + IT_PRD_ALPHA);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterProduitEnBase(cree.getIdProduit())).isEqualTo(1L);
		assertThat(this.compterProduitParCoupleEnBase(
				IT_TP_PARENT_A,
				IT_STP_PARENT_A,
				IT_PRD_ALPHA)).isEqualTo(1L);

	} // __________________________________________________________________
	


	/**
	 * <div>
	 * <p>update(absent) : scénario nominal sans résultat avec preuve BD.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_OBJ_INTROUVABLE} + libellé</li>
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
		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);
		final long baseline = this.compterTousLesProduitEnBase();
		final InputDTO input
			= new ProduitDTO.InputDTO(
					IT_TP_PARENT_A,
					IT_STP_PARENT_A,
					IT_PRD_INEXISTANT_UPDATE);

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.update(input);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_OBJ_INTROUVABLE + IT_PRD_INEXISTANT_UPDATE);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	// ------------------------ delete(...) -----------------------------//



	/**
	 * <div>
	 * <p>delete(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>ne lève aucune exception</li>
	 * <li>positionne {@link ProduitIController#MESSAGE_DELETE_VUE_NULL}</li>
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
		final long baseline = this.compterTousLesProduitEnBase();

		/* ======================= ACT ======================= */
		this.controller.delete(null);

		/* ===================== ASSERT ====================== */
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitIController.MESSAGE_DELETE_VUE_NULL);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>ne lève aucune exception</li>
	 * <li>positionne {@link ProduitIController#MESSAGE_DELETE_VUE_BLANK}</li>
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
		final long baseline = this.compterTousLesProduitEnBase();
		final InputDTO input
			= new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, ESPACES);

		/* ======================= ACT ======================= */
		this.controller.delete(input);

		/* ===================== ASSERT ====================== */
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitIController.MESSAGE_DELETE_VUE_BLANK);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(parent blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>ne lève aucune exception</li>
	 * <li>positionne
	 * {@link ProduitIController#MESSAGE_DELETE_VUE_PARENT_BLANK}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("delete(parent blank) : message local + aucune écriture BD")
	public void testDeleteParentBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesProduitEnBase();
		final InputDTO input
			= new ProduitDTO.InputDTO(IT_TP_PARENT_A, ESPACES, IT_PRD_ALPHA);

		/* ======================= ACT ======================= */
		this.controller.delete(input);

		/* ===================== ASSERT ====================== */
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitIController.MESSAGE_DELETE_VUE_PARENT_BLANK);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(ok) : détruit le bon couple [parent, libellé] avec preuve BD.</p>
	 * <ul>
	 * <li>délègue la suppression au service</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_DELETE_OK} + libellé</li>
	 * <li>détruit le bon enregistrement physique</li>
	 * <li>laisse intact un homonyme sous un autre parent</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("delete(ok) : détruit le bon couple [parent, libellé] + message exact + preuve BD")
	public void testDeleteOkAvecPreuveCoupleParentLibelle() throws Exception {

		/* ===================== ARRANGE ===================== */
		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);
		this.creerParentsBeton(IT_TP_PARENT_B, IT_STP_PARENT_B);

		final OutputDTO creeParentA = this.controller.creer(
				new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_DELETE_OK));
		final OutputDTO creeParentB = this.controller.creer(
				new ProduitDTO.InputDTO(IT_TP_PARENT_B, IT_STP_PARENT_B, IT_PRD_DELETE_OK));

		assertThat(creeParentA).isNotNull();
		assertThat(creeParentB).isNotNull();
		assertThat(creeParentA.getIdProduit()).isNotNull();
		assertThat(creeParentB.getIdProduit()).isNotNull();

		final Long idCibleParentB = creeParentB.getIdProduit();

		assertThat(this.compterProduitParCoupleEnBase(
				IT_TP_PARENT_A,
				IT_STP_PARENT_A,
				IT_PRD_DELETE_OK)).isEqualTo(1L);
		assertThat(this.compterProduitParCoupleEnBase(
				IT_TP_PARENT_B,
				IT_STP_PARENT_B,
				IT_PRD_DELETE_OK)).isEqualTo(1L);

		final Long nombreAvantDelete
			= this.jdbcTemplate.queryForObject(SELECT_COUNT_FROM_PRODUITS, Long.class);

		/* ======================= ACT ======================= */
		this.controller.delete(
				new ProduitDTO.InputDTO(
						IT_TP_PARENT_B,
						IT_STP_PARENT_B,
						IT_PRD_DELETE_OK));

		final Long nombreApresDelete
			= this.jdbcTemplate.queryForObject(SELECT_COUNT_FROM_PRODUITS, Long.class);

		/* ===================== ASSERT ====================== */
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_DELETE_OK + IT_PRD_DELETE_OK);
		assertThat(nombreApresDelete).isEqualTo(nombreAvantDelete - 1L);
		assertThat(this.compterProduitEnBase(idCibleParentB)).isEqualTo(0L);
		assertThat(this.compterProduitParCoupleEnBase(
				IT_TP_PARENT_A,
				IT_STP_PARENT_A,
				IT_PRD_DELETE_OK)).isEqualTo(1L);
		assertThat(this.compterProduitParCoupleEnBase(
				IT_TP_PARENT_B,
				IT_STP_PARENT_B,
				IT_PRD_DELETE_OK)).isEqualTo(0L);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(absent) : scénario nominal sans suppression effective.</p>
	 * <ul>
	 * <li>ne lève aucune exception</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_OBJ_INTROUVABLE} + libellé</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("delete(absent) : message exact + aucune écriture BD")
	public void testDeleteAbsentAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);
		final long baseline = this.compterTousLesProduitEnBase();

		/* ======================= ACT ======================= */
		this.controller.delete(
				new ProduitDTO.InputDTO(
						IT_TP_PARENT_A,
						IT_STP_PARENT_A,
						IT_PRD_INEXISTANT_DELETE));

		/* ===================== ASSERT ====================== */
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_OBJ_INTROUVABLE + IT_PRD_INEXISTANT_DELETE);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

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
		final long baseline = this.compterTousLesProduitEnBase();
		final String messageAttendu
			= baseline == 0L
				? ProduitICuService.MESSAGE_RECHERCHE_VIDE
				: ProduitICuService.MESSAGE_RECHERCHE_OK;

		/* ======================= ACT ======================= */
		final long retour = this.controller.count();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isEqualTo(baseline);
		assertThat(this.controller.getMessage()).isEqualTo(messageAttendu);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>count(après création) : comptage exact après ajout avec preuve BD.</p>
	 * <ul>
	 * <li>prouve d'abord physiquement l'ajout en base</li>
	 * <li>retourne exactement le nouveau volume</li>
	 * <li>positionne exactement {@link ProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("count(après création) : volume exact + message exact + preuve BD")
	public void testCountApresCreationAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesProduitEnBase();
		this.creerParentsBeton(IT_TP_PARENT_A, IT_STP_PARENT_A);
		final OutputDTO cree = this.controller.creer(
				new ProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_PARENT_A, IT_PRD_COUNT_01));

		assertThat(cree).isNotNull();
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterProduitParCoupleEnBase(
				IT_TP_PARENT_A,
				IT_STP_PARENT_A,
				IT_PRD_COUNT_01)).isEqualTo(1L);

		/* ======================= ACT ======================= */
		final long retour = this.controller.count();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isEqualTo(baseline + 1L);
		assertThat(this.controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________



	// ------------------------ getMessage() ----------------------------//



	/**
	 * <div>
	 * <p>getMessage(initial) : état initial du controller intégré.</p>
	 * <ul>
	 * <li>retourne {@code null} avant toute opération</li>
	 * <li>n'écrit rien en base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("getMessage(initial) : retourne null avant toute opération")
	public void testGetMessageInitialNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesProduitEnBase();

		/* ======================= ACT ======================= */
		final String message = this.controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(message).isNull();
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>getMessage(après succès réel) : message courant issu du comptage réel.</p>
	 * <ul>
	 * <li>après {@code count()}, retourne le message exact cohérent
	 * avec le volume physique observé</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("getMessage(après succès réel) : retourne le message exact du count()")
	public void testGetMessageApresSuccesReel() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesProduitEnBase();
		final String messageAttendu
			= baseline == 0L
				? ProduitICuService.MESSAGE_RECHERCHE_VIDE
				: ProduitICuService.MESSAGE_RECHERCHE_OK;

		/* ======================= ACT ======================= */
		final long retour = this.controller.count();
		final String message = this.controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isEqualTo(baseline);
		assertThat(message).isEqualTo(messageAttendu);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>getMessage(après erreur locale) : message produit par le controller.</p>
	 * <ul>
	 * <li>après {@code creer(null)}, retourne exactement
	 * {@link ProduitIController#MESSAGE_CREER_VUE_NULL}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("getMessage(après erreur locale) : retourne MESSAGE_CREER_VUE_NULL")
	public void testGetMessageApresErreurLocale() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesProduitEnBase();

		/* ======================= ACT ======================= */
		this.controller.creer(null);
		final String message = this.controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(message)
				.isEqualTo(ProduitIController.MESSAGE_CREER_VUE_NULL);
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>getMessage(dernier message gagne) : le message courant est écrasé.</p>
	 * <ul>
	 * <li>une erreur locale positionne d'abord
	 * {@link ProduitIController#MESSAGE_CREER_VUE_NULL}</li>
	 * <li>un {@code count()} réel remplace ensuite ce message
	 * par le message exact du comptage courant</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("getMessage(dernier message gagne) : le message réel le plus récent écrase le précédent")
	public void testGetMessageDernierMessageGagne() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesProduitEnBase();

		/* ======================= ACT ======================= */
		this.controller.creer(null);
		final String messageErreur = this.controller.getMessage();
		final long retour = this.controller.count();
		final String messageFinal = this.controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(messageErreur)
				.isEqualTo(ProduitIController.MESSAGE_CREER_VUE_NULL);
		assertThat(retour).isEqualTo(baseline);
		if (retour == 0L) {
			assertThat(messageFinal)
					.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);
		} else {
			assertThat(messageFinal)
					.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);
		}
		assertThat(this.compterTousLesProduitEnBase()).isEqualTo(baseline);

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
	 * @param typeProduit : String :
	 * le libellé du TypeProduit parent.
	 * @param sousTypeProduit : String :
	 * le libellé du SousTypeProduit parent.
	 * @throws Exception
	 */
	private void creerParentsBeton(
			final String typeProduit,
			final String sousTypeProduit)
					throws Exception {

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(typeProduit));
		this.sousTypeProduitService.creer(
				new SousTypeProduitDTO.InputDTO(typeProduit, sousTypeProduit));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>Compte le nombre total de lignes physiques de la table PRODUITS.</p>
	 * </div>
	 *
	 * @return long :
	 * le nombre total de Produits en base.
	 */
	private long compterTousLesProduitEnBase() {

		return this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>Compte le nombre de lignes physiques pour un identifiant Produit.</p>
	 * </div>
	 *
	 * @param pIdProduit : Long :
	 * l'identifiant du Produit.
	 * @return long :
	 * le nombre de lignes trouvées.
	 */
	private long compterProduitEnBase(final Long pIdProduit) {

		return this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS_BY_ID,
				Long.class,
				pIdProduit);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>Lit le libellé Produit physique d'une ligne PRODUITS par son ID.</p>
	 * </div>
	 *
	 * @param pIdProduit : Long :
	 * l'identifiant du Produit.
	 * @return String :
	 * le libellé Produit trouvé en base.
	 */
	private String lireLibelleProduitEnBase(final Long pIdProduit) {

		return this.jdbcTemplate.queryForObject(
				SELECT_PRODUIT_LIBELLE_BY_ID,
				String.class,
				pIdProduit);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>Lit le libellé du SousTypeProduit parent d'un Produit par son ID.</p>
	 * </div>
	 *
	 * @param pIdProduit : Long :
	 * l'identifiant du Produit.
	 * @return String :
	 * le libellé du parent SousTypeProduit.
	 */
	private String lireSousTypeProduitParentEnBase(final Long pIdProduit) {

		return this.jdbcTemplate.queryForObject(
				SELECT_PARENT_STP_BY_ID,
				String.class,
				pIdProduit);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>Lit le libellé du TypeProduit parent d'un Produit par son ID.</p>
	 * </div>
	 *
	 * @param pIdProduit : Long :
	 * l'identifiant du Produit.
	 * @return String :
	 * le libellé du parent TypeProduit.
	 */
	private String lireTypeProduitParentEnBase(final Long pIdProduit) {

		return this.jdbcTemplate.queryForObject(
				SELECT_PARENT_TP_BY_ID,
				String.class,
				pIdProduit);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>Compte le nombre de lignes physiques pour un triplet
	 * [type parent, sous-type parent, produit].</p>
	 * </div>
	 *
	 * @param pTypeProduit : String :
	 * le libellé du TypeProduit parent.
	 * @param pSousTypeProduit : String :
	 * le libellé du SousTypeProduit parent.
	 * @param pProduit : String :
	 * le libellé exact du Produit.
	 * @return Long :
	 * le nombre de lignes trouvées pour ce triplet.
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



} // FIN DE LA CLASSE ProduitWebControllerIntegrationTest.------------------------