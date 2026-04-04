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

	
	
	// ------------------ rechercherTousString() -------------------------//

		

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