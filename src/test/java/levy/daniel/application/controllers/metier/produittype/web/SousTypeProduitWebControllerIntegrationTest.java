/* ********************************************************************* */
/* ****************** TEST INTEGRATION CONTROLLER WEB ****************** */
/* ********************************************************************* */
package levy.daniel.application.controllers.metier.produittype.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
 * <ul>
 * <li>Tests d'intégration complets (avec tests "béton") 
 * du CONTROLLER ADAPTER WEB 
 * {@link SousTypeProduitWebController}.</li>
 * <li>Vérifie l'implémentation des contrats du PORT
 * {@link SousTypeProduitIController}, avec preuve BD directe.</li>
 * </ul>
 * </div>
 * 
 * <div>
 * <p style="font-weight:bold;">IMPORTANT :</p>
 * <ul>
 * <li>on charge :
 * <ul>
 * <li>le vrai bean CONTROLLER Web 
 * {@link SousTypeProduitWebController},</li> 
 * <li>Toute la chaîne de beans nécessaire pour le test,</li>
 * <li>et la vraie persistance JPA/H2 de test</li>
 * </ul>
 * </li>
 * <li>on n'exécute pas de test HTTP bout-en-bout : on appelle directement
 * le bean Spring du controller ;</li>
 * <li>on active le groupe de profils SPRING "test-web-jpa"
 * afin d’obtenir la configuration de test attendue avec le
 * controller web et la vraie persistance JPA/H2 de test.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 7 avril 2026
 */
/* 
 * Démarre un vrai contexte Spring Boot de test à partir de ConfigTest.
 * spring.main.web-application-type=none force Spring Boot à considérer
 * ce test comme non web :
 * aucun serveur web embarqué n'est démarré,
 * aucun environnement HTTP n'est créé,
 * et le controller web est testé comme un bean Spring classique.
 */
@SpringBootTest(
		classes = SousTypeProduitWebControllerIntegrationTest.ConfigTest.class,
		webEnvironment = SpringBootTest.WebEnvironment.NONE,
		properties = { "spring.main.web-application-type=none" }
)

/*
 * Active le groupe de profils test-web-jpa
 * afin d'obtenir le CONTROLLER Desktop réel,
 * le SERVICE UC réel requis
 * et la persistance JPA/H2 de test.
 */
@ActiveProfiles({ "test-web-jpa" })

/*
 * Tag JUnit de la classe :
 * permet de repérer, filtrer ou lancer
 * cette famille de tests d'intégration Controller Web.
 */
@Tag(SousTypeProduitWebControllerIntegrationTest.TAG)

/*
 * Demande à Spring de reconstruire un contexte propre après chaque méthode
 * afin d'éviter qu'un test ne pollue le suivant
 * par l'état des beans ou des messages mémorisés.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)

/*
 * Réinitialise la base avant chaque test
 * en exécutant d'abord le script de nettoyage,
 * puis le script de réinjection des données de test.
 */
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

	/**
	 * <div>
	 * <p>JdbcTemplate de test pour preuve BD directe.</p>
	 * </div>
	 */
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * <div> 
	 * <p>SERVICE UC TypeProduit réel injecté par Spring.</p>
	 * <p>Utilisé pour instancier des objets dans le présent test.</p>
	 * </div>
	 */
	@Autowired
	private TypeProduitICuService typeProduitService;

	/**
	 * <div>
	 * <p>Controller Web réel injecté par Spring.</p>
	 * <p>Son injection entraîne aussi la présence du SERVICE UC 
	 * SousTypeProduitCuService injecté par SPRING dans le constructeur 
	 * du CONTROLLER
	 * et requis dans le contexte Spring de test.</p>
	 * </div>
	 */
	@Autowired
	private SousTypeProduitWebController controller;
	
	// ************************ CONFIGURATION DE TEST *********************/

	/**
	 * <div>
	 * <p style="font-weight:bold;">CONFIGURATION DE TEST SPRING.</p>
	 * <ul>
	 * <li>scan applicatif limité
	 * (Controllers ProduitType + Services ProduitType + Persistance ProduitType),</li>
	 * <li>scan des entités JPA,</li>
	 * <li>activation des repositories Spring Data JPA une seule fois.</li>
	 * </ul>
	 * </div>
	 *
	 * @author Daniel Lévy
	 */
	/*
	 * Déclare une classe de configuration Spring dédiée à ce test d'intégration.
	 * Elle sert de point d'entrée au contexte Spring Boot de test.
	 */
	@Configuration
	
	/*
	 * Demande à Spring Boot d'appliquer son auto-configuration standard
	 * compatible avec les dépendances présentes et le profil de test actif.
	 */
	@EnableAutoConfiguration
	
	/*
	 * Demande à Spring de repérer les entités JPA du périmètre ProduitType.
	 * Cela permet à Hibernate de connaître les tables mappées à charger pour le test.
	 */
	@EntityScan(basePackages = {
			"levy.daniel.application.persistence.metier.produittype"
	})
	
	/*
	 * Active les repositories Spring Data JPA du périmètre ProduitType.
	 * Cela permet d'utiliser les DAO JPA réels dans le contexte de test.
	 */
	@EnableJpaRepositories(basePackages = {
			"levy.daniel.application.persistence.metier.produittype.dao.daosJPA"
	})
	
	/*
	 * Limite le scan Spring aux composants réellement utiles à ce test :
	 * controllers, services et persistance du périmètre ProduitType.
	 * Les classes de tests sont exclues pour éviter qu'elles soient chargées
	 * par erreur comme composants Spring.
	 */
	@ComponentScan(
			basePackages = {
					"levy.daniel.application.controllers.metier.produittype",
					"levy.daniel.application.model.services.produittype",
					"levy.daniel.application.persistence.metier.produittype"
			},
			excludeFilters = {
					@Filter(type = FilterType.REGEX, pattern = ".*IntegrationTest.*"),
					@Filter(type = FilterType.REGEX, pattern = ".*MockTest.*"),
					@Filter(type = FilterType.REGEX, pattern = ".*MockMvcTest.*")
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

	
	
	/**
	 * <div>
	 * <p>findByLibelle(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link SousTypeProduitIController#MESSAGE_FIND_BY_LIBELLE_VUE_NULL}</li>
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
		final long baseline = this.compterTousLesSousTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> dtos = this.controller.findByLibelle(null);

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitIController.MESSAGE_FIND_BY_LIBELLE_VUE_NULL);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link SousTypeProduitIController#MESSAGE_FIND_BY_LIBELLE_VUE_BLANK}</li>
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
		final long baseline = this.compterTousLesSousTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> dtos = this.controller.findByLibelle(ESPACES);

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitIController.MESSAGE_FIND_BY_LIBELLE_VUE_BLANK);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste non nulle</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}</li>
	 * <li>retourne les deux DTO correspondant au même libellé exact</li>
	 * <li>prouve la non-unicité du libellé à travers deux parents distincts</li>
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
		final String parentB = "IT-CTRL-WEB-TP-PARENT-B";
		final long baseline = this.compterTousLesSousTypeProduitEnBase();

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(parentB));

		final OutputDTO creeA
			= this.controller.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA));
		final OutputDTO creeB
			= this.controller.creer(new SousTypeProduitDTO.InputDTO(parentB, IT_STP_ALPHA));

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> dtos = this.controller.findByLibelle(IT_STP_ALPHA);

		/* ===================== ASSERT ====================== */
		assertThat(creeA).isNotNull();
		assertThat(creeB).isNotNull();
		assertThat(dtos).isNotNull();
		assertThat(dtos).hasSize(2);
		assertThat(dtos)
				.extracting(SousTypeProduitDTO.OutputDTO::getSousTypeProduit)
				.containsOnly(IT_STP_ALPHA);
		assertThat(dtos)
				.extracting(SousTypeProduitDTO.OutputDTO::getTypeProduit)
				.containsExactlyInAnyOrder(IT_TP_PARENT_A, parentB);
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		final OutputDTO trouveA = dtos.stream()
				.filter(dto -> IT_TP_PARENT_A.equals(dto.getTypeProduit()))
				.findFirst()
				.orElse(null);
		final OutputDTO trouveB = dtos.stream()
				.filter(dto -> parentB.equals(dto.getTypeProduit()))
				.findFirst()
				.orElse(null);

		assertThat(trouveA).isNotNull();
		assertThat(trouveA.getIdSousTypeProduit()).isEqualTo(creeA.getIdSousTypeProduit());

		assertThat(trouveB).isNotNull();
		assertThat(trouveB.getIdSousTypeProduit()).isEqualTo(creeB.getIdSousTypeProduit());

		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline + 2L);
		assertThat(this.compterSousTypeProduitEnBase(creeA.getIdSousTypeProduit())).isEqualTo(1L);
		assertThat(this.compterSousTypeProduitEnBase(creeB.getIdSousTypeProduit())).isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_A, IT_STP_ALPHA))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(parentB, IT_STP_ALPHA))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitEnBase(creeA.getIdSousTypeProduit()))
				.isEqualTo(IT_STP_ALPHA);
		assertThat(this.lireLibelleSousTypeProduitEnBase(creeB.getIdSousTypeProduit()))
				.isEqualTo(IT_STP_ALPHA);
		assertThat(this.lireParentSousTypeProduitEnBase(creeA.getIdSousTypeProduit()))
				.isEqualTo(IT_TP_PARENT_A);
		assertThat(this.lireParentSousTypeProduitEnBase(creeB.getIdSousTypeProduit()))
				.isEqualTo(parentB);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(absent) : scénario nominal sans résultat avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste vide</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + libellé</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelle(absent) : liste vide + message exact + aucune écriture BD")
	public void testFindByLibelleAbsentAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.compterTousLesSousTypeProduitEnBase();
		final String libelle = "IT-CTRL-WEB-STP-INEXISTANT";

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> dtos = this.controller.findByLibelle(libelle);

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNotNull();
		assertThat(dtos).isEmpty();
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + libelle);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_A, libelle)).isZero();

	} // __________________________________________________________________
	
	
	
	// ----------------- findByLibelleRapide(...) -----------------------//

	
	
	/**
	 * <div>
	 * <p>findByLibelleRapide(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link SousTypeProduitIController#MESSAGE_FIND_BY_LIBELLE_RAPIDE_VUE_NULL}</li>
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
		final long baseline = this.compterTousLesSousTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> dtos = this.controller.findByLibelleRapide(null);

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(
						SousTypeProduitIController
							.MESSAGE_FIND_BY_LIBELLE_RAPIDE_VUE_NULL);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelleRapide(blank) : le controller délègue au service.</p>
	 * <ul>
	 * <li>retourne tous les sous-types présents après création</li>
	 * <li>positionne exactement {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
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
		final long baseline = this.compterTousLesSousTypeProduitEnBase();
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));
		final OutputDTO cree
			= this.controller.creer(new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA));
		final long attendu = this.compterTousLesSousTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> dtos = this.controller.findByLibelleRapide(ESPACES);

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(dtos).isNotNull();
		assertThat(dtos.size()).isEqualTo((int) attendu);
		assertThat(dtos)
				.extracting(SousTypeProduitDTO.OutputDTO::getSousTypeProduit)
				.contains(IT_STP_ALPHA);
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		final OutputDTO dtoAlpha = dtos.stream()
				.filter(dto -> IT_STP_ALPHA.equals(dto.getSousTypeProduit()))
				.findFirst()
				.orElse(null);

		assertThat(dtoAlpha).isNotNull();
		assertThat(dtoAlpha.getIdSousTypeProduit()).isEqualTo(cree.getIdSousTypeProduit());
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterSousTypeProduitEnBase(cree.getIdSousTypeProduit())).isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_A, IT_STP_ALPHA))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitEnBase(cree.getIdSousTypeProduit()))
				.isEqualTo(IT_STP_ALPHA);
		assertThat(this.lireParentSousTypeProduitEnBase(cree.getIdSousTypeProduit()))
				.isEqualTo(IT_TP_PARENT_A);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelleRapide(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste non vide</li>
	 * <li>positionne exactement {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
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
		final long baseline = this.compterTousLesSousTypeProduitEnBase();
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));
		final InputDTO input = new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA);
		final OutputDTO cree = this.controller.creer(input);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> dtos
			= this.controller.findByLibelleRapide(IT_STP_ALPHA);

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(dtos).isNotNull();
		assertThat(dtos).isNotEmpty();
		assertThat(dtos)
				.extracting(SousTypeProduitDTO.OutputDTO::getSousTypeProduit)
				.contains(IT_STP_ALPHA);
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		final OutputDTO dtoAlpha = dtos.stream()
				.filter(dto -> IT_STP_ALPHA.equals(dto.getSousTypeProduit()))
				.findFirst()
				.orElse(null);

		assertThat(dtoAlpha).isNotNull();
		assertThat(dtoAlpha.getIdSousTypeProduit()).isEqualTo(cree.getIdSousTypeProduit());
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterSousTypeProduitEnBase(cree.getIdSousTypeProduit())).isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_A, IT_STP_ALPHA))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitEnBase(cree.getIdSousTypeProduit()))
				.isEqualTo(IT_STP_ALPHA);
		assertThat(this.lireParentSousTypeProduitEnBase(cree.getIdSousTypeProduit()))
				.isEqualTo(IT_TP_PARENT_A);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelleRapide(absent) : scénario nominal sans résultat avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste vide</li>
	 * <li>positionne exactement {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
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
		final long baseline = this.compterTousLesSousTypeProduitEnBase();
		final String contenu = "IT-CTRL-WEB-STP-RAPIDE-INEXISTANT";

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> dtos
			= this.controller.findByLibelleRapide(contenu);

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNotNull();
		assertThat(dtos).isEmpty();
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_A, contenu)).isZero();

	} // __________________________________________________________________
	
	
	
	// ------------------- findAllByParent(...) -------------------------//

	
	
	/**
	 * <div>
	 * <p>findAllByParent(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link SousTypeProduitIController#MESSAGE_FIND_ALL_BY_PARENT_VUE_NULL}</li>
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
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> dtos = this.controller.findAllByParent(null);

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitIController.MESSAGE_FIND_ALL_BY_PARENT_VUE_NULL);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(parent blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link SousTypeProduitIController#MESSAGE_FIND_ALL_BY_PARENT_VUE_PARENT_BLANK}</li>
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
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		final TypeProduitDTO.InputDTO parentDto = new TypeProduitDTO.InputDTO(ESPACES);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> dtos = this.controller.findAllByParent(parentDto);

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(
						SousTypeProduitIController.MESSAGE_FIND_ALL_BY_PARENT_VUE_PARENT_BLANK);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(parent absent) : le controller propage l'échec du service.</p>
	 * <ul>
	 * <li>propage {@link IllegalStateException}</li>
	 * <li>positionne exactement {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("findAllByParent(parent absent) : IllegalStateException + message exact + aucune écriture BD")
	public void testFindAllByParentParentAbsentServiceKo() {

		/* ===================== ARRANGE ===================== */
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		final TypeProduitDTO.InputDTO parentDto
			= new TypeProduitDTO.InputDTO(IT_TP_PARENT_ABSENT);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> this.controller.findAllByParent(parentDto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(vide) : parent existant mais aucun enfant rattaché.</p>
	 * <ul>
	 * <li>retourne une liste vide</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>ne modifie pas physiquement la base des sous-types</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findAllByParent(vide) : liste vide + message exact + aucune écriture BD")
	public void testFindAllByParentVideAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));
		final TypeProduitDTO.InputDTO parentDto
			= new TypeProduitDTO.InputDTO(IT_TP_PARENT_A);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> dtos = this.controller.findAllByParent(parentDto);

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNotNull();
		assertThat(dtos).isEmpty();
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(ok) : retourne uniquement les enfants du parent demandé.</p>
	 * <ul>
	 * <li>retourne une liste non {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>ne retourne que les enfants du parent transmis</li>
	 * <li>prouve physiquement en base les couples parent / sous-type créés</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findAllByParent(ok) : retourne uniquement les enfants du parent demandé + message exact + preuve BD")
	public void testFindAllByParentOkAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		final String parentB = "IT-CTRL-WEB-TP-PARENT-B";
		final String sousTypeProduitGamma = "IT-CTRL-WEB-STP-GAMMA";

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(parentB));

		final OutputDTO creeA1 = this.controller.creer(
				new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA));
		final OutputDTO creeA2 = this.controller.creer(
				new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_BETA));
		final OutputDTO creeB1 = this.controller.creer(
				new SousTypeProduitDTO.InputDTO(parentB, sousTypeProduitGamma));

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> dtos = this.controller.findAllByParent(
				new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNotNull();
		assertThat(dtos).hasSize(2);

		assertThat(dtos)
				.extracting(OutputDTO::getSousTypeProduit)
				.containsExactly(IT_STP_ALPHA, IT_STP_BETA);

		assertThat(dtos)
				.extracting(OutputDTO::getTypeProduit)
				.containsOnly(IT_TP_PARENT_A);

		assertThat(dtos)
				.extracting(OutputDTO::getIdSousTypeProduit)
				.containsExactly(
						creeA1.getIdSousTypeProduit(),
						creeA2.getIdSousTypeProduit());

		assertThat(dtos)
				.extracting(OutputDTO::getIdSousTypeProduit)
				.doesNotContain(creeB1.getIdSousTypeProduit());

		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline + 3L);

		assertThat(this.compterSousTypeProduitEnBase(creeA1.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitEnBase(creeA1.getIdSousTypeProduit()))
				.isEqualTo(IT_STP_ALPHA);
		assertThat(this.lireParentSousTypeProduitEnBase(creeA1.getIdSousTypeProduit()))
				.isEqualTo(IT_TP_PARENT_A);

		assertThat(this.compterSousTypeProduitEnBase(creeA2.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitEnBase(creeA2.getIdSousTypeProduit()))
				.isEqualTo(IT_STP_BETA);
		assertThat(this.lireParentSousTypeProduitEnBase(creeA2.getIdSousTypeProduit()))
				.isEqualTo(IT_TP_PARENT_A);

		assertThat(this.compterSousTypeProduitEnBase(creeB1.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitEnBase(creeB1.getIdSousTypeProduit()))
				.isEqualTo(sousTypeProduitGamma);
		assertThat(this.lireParentSousTypeProduitEnBase(creeB1.getIdSousTypeProduit()))
				.isEqualTo(parentB);

		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_A, IT_STP_ALPHA))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_A, IT_STP_BETA))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(parentB, sousTypeProduitGamma))
				.isEqualTo(1L);

	} // __________________________________________________________________
	
	
	
	// ---------------------- findByDTO(...) ----------------------------//

	
	
	/**
	 * <div>
	 * <p>findByDTO(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link SousTypeProduitIController#MESSAGE_FIND_BY_DTO_VUE_NULL}</li>
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
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.findByDTO(null);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitIController.MESSAGE_FIND_BY_DTO_VUE_NULL);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByDTO(parent blank) : le controller délègue au service.</p>
	 * <ul>
	 * <li>propage {@link IllegalStateException}</li>
	 * <li>positionne exactement {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("findByDTO(parent blank) : IllegalStateException + message exact + aucune écriture BD")
	public void testFindByDTOParentBlank() {

		/* ===================== ARRANGE ===================== */
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		final InputDTO input = new SousTypeProduitDTO.InputDTO(ESPACES, IT_STP_ALPHA);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> this.controller.findByDTO(input))
				.isInstanceOf(IllegalStateException.class);

		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByDTO(parent absent) : scénario nominal sans résultat.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(parent absent) : retourne null + message exact + aucune écriture BD")
	public void testFindByDTOParentAbsentAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		final InputDTO input = new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_ABSENT, IT_STP_ALPHA);

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.findByDTO(input);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_ABSENT, IT_STP_ALPHA))
				.isZero();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByDTO(absent) : parent existant mais aucun enfant correspondant.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>ne modifie pas physiquement la base des sous-types</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(absent) : retourne null + message exact + aucune écriture BD")
	public void testFindByDTOAbsentAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));
		final InputDTO input = new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA);

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.findByDTO(input);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_A, IT_STP_ALPHA))
				.isZero();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByDTO(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne un {@link OutputDTO} non nul</li>
	 * <li>positionne exactement {@link SousTypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}</li>
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
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));
		final OutputDTO cree = this.controller.creer(
				new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA));
		final InputDTO inputRecherche
			= new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA);

		/* ======================= ACT ======================= */
		final OutputDTO trouve = this.controller.findByDTO(inputRecherche);

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(trouve).isNotNull();
		assertThat(trouve.getIdSousTypeProduit()).isEqualTo(cree.getIdSousTypeProduit());
		assertThat(trouve.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);
		assertThat(trouve.getSousTypeProduit()).isEqualTo(IT_STP_ALPHA);
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterSousTypeProduitEnBase(cree.getIdSousTypeProduit())).isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_A, IT_STP_ALPHA))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitEnBase(cree.getIdSousTypeProduit()))
				.isEqualTo(IT_STP_ALPHA);
		assertThat(this.lireParentSousTypeProduitEnBase(cree.getIdSousTypeProduit()))
				.isEqualTo(IT_TP_PARENT_A);

	} // __________________________________________________________________
	
	
	
	// ----------------------- findById(...) ----------------------------//

	
	
	/**
	 * <div>
	 * <p>findById(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link SousTypeProduitIController#MESSAGE_FIND_BY_ID_VUE_NULL}</li>
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
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.findById(null);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitIController.MESSAGE_FIND_BY_ID_VUE_NULL);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findById(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne un {@link OutputDTO} non nul</li>
	 * <li>positionne exactement {@link SousTypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}</li>
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
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));
		final OutputDTO cree = this.controller.creer(
				new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA));

		/* ======================= ACT ======================= */
		final OutputDTO trouve = this.controller.findById(cree.getIdSousTypeProduit());

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(trouve).isNotNull();
		assertThat(trouve.getIdSousTypeProduit()).isEqualTo(cree.getIdSousTypeProduit());
		assertThat(trouve.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);
		assertThat(trouve.getSousTypeProduit()).isEqualTo(IT_STP_ALPHA);
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterSousTypeProduitEnBase(cree.getIdSousTypeProduit())).isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_A, IT_STP_ALPHA))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitEnBase(cree.getIdSousTypeProduit()))
				.isEqualTo(IT_STP_ALPHA);
		assertThat(this.lireParentSousTypeProduitEnBase(cree.getIdSousTypeProduit()))
				.isEqualTo(IT_TP_PARENT_A);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findById(absent) : scénario nominal sans résultat avec preuve BD.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + id</li>
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
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		final Long id = 999_999_999L;

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.findById(id);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + id);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);
		assertThat(this.compterSousTypeProduitEnBase(id)).isZero();

	} // __________________________________________________________________
	
	
	
	// ------------------------ update(...) -----------------------------//

	
	
	/**
	 * <div>
	 * <p>update(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link SousTypeProduitIController#MESSAGE_UPDATE_VUE_NULL}</li>
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
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.update(null);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitIController.MESSAGE_UPDATE_VUE_NULL);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link SousTypeProduitIController#MESSAGE_UPDATE_VUE_BLANK}</li>
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
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		final InputDTO input = new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, ESPACES);

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.update(input);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitIController.MESSAGE_UPDATE_VUE_BLANK);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(parent blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link SousTypeProduitIController#MESSAGE_UPDATE_VUE_PARENT_BLANK}</li>
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
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		final InputDTO input = new SousTypeProduitDTO.InputDTO(ESPACES, IT_STP_ALPHA);

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.update(input);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitIController.MESSAGE_UPDATE_VUE_PARENT_BLANK);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(parent absent) : le controller propage l'échec du service.</p>
	 * <ul>
	 * <li>propage {@link IllegalStateException}</li>
	 * <li>positionne exactement {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("update(parent absent) : IllegalStateException + message exact + aucune écriture BD")
	public void testUpdateParentAbsentAvecPreuveBd() {

		/* ===================== ARRANGE ===================== */
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		final InputDTO input = new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_ABSENT, IT_STP_ALPHA);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> this.controller.update(input))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_ABSENT, IT_STP_ALPHA))
				.isZero();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(absent) : scénario nominal sans résultat avec preuve BD.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + libellé</li>
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
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));
		final InputDTO input = new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA);

		/* ======================= ACT ======================= */
		final OutputDTO dto = this.controller.update(input);

		/* ===================== ASSERT ====================== */
		assertThat(dto).isNull();
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + IT_STP_ALPHA);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_A, IT_STP_ALPHA))
				.isZero();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(ok) : cohérence complète avec preuve BD.</p>
	 * <ul>
	 * <li>retourne un {@link OutputDTO} non nul</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_MODIF_OK} + libellé</li>
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
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));
		final OutputDTO cree = this.controller.creer(
				new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA));
		final InputDTO inputModification
			= new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA);

		/* ======================= ACT ======================= */
		final OutputDTO modifie = this.controller.update(inputModification);

		/* ===================== ASSERT ====================== */
		assertThat(cree).isNotNull();
		assertThat(modifie).isNotNull();
		assertThat(modifie.getIdSousTypeProduit()).isEqualTo(cree.getIdSousTypeProduit());
		assertThat(modifie.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);
		assertThat(modifie.getSousTypeProduit()).isEqualTo(IT_STP_ALPHA);
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_MODIF_OK + IT_STP_ALPHA);

		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterSousTypeProduitEnBase(cree.getIdSousTypeProduit())).isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_A, IT_STP_ALPHA))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitEnBase(cree.getIdSousTypeProduit()))
				.isEqualTo(IT_STP_ALPHA);
		assertThat(this.lireParentSousTypeProduitEnBase(cree.getIdSousTypeProduit()))
				.isEqualTo(IT_TP_PARENT_A);

	} // __________________________________________________________________
	
	
	
	// ------------------------ delete(...) -----------------------------//

	
	
	/**
	 * <div>
	 * <p>delete(null) : erreur utilisateur bénigne côté controller.</p>
	 * <ul>
	 * <li>ne lève aucune exception</li>
	 * <li>positionne {@link SousTypeProduitIController#MESSAGE_DELETE_VUE_NULL}</li>
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
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();

		/* ======================= ACT ======================= */
		this.controller.delete(null);

		/* ===================== ASSERT ====================== */
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitIController.MESSAGE_DELETE_VUE_NULL);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>ne lève aucune exception</li>
	 * <li>positionne {@link SousTypeProduitIController#MESSAGE_DELETE_VUE_BLANK}</li>
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
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		final InputDTO input = new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, ESPACES);

		/* ======================= ACT ======================= */
		this.controller.delete(input);

		/* ===================== ASSERT ====================== */
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitIController.MESSAGE_DELETE_VUE_BLANK);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(parent blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>ne lève aucune exception</li>
	 * <li>positionne
	 * {@link SousTypeProduitIController#MESSAGE_DELETE_VUE_PARENT_BLANK}</li>
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
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		final InputDTO input = new SousTypeProduitDTO.InputDTO(ESPACES, IT_STP_ALPHA);

		/* ======================= ACT ======================= */
		this.controller.delete(input);

		/* ===================== ASSERT ====================== */
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitIController.MESSAGE_DELETE_VUE_PARENT_BLANK);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(parent absent) : le controller propage l'échec du service.</p>
	 * <ul>
	 * <li>propage {@link IllegalStateException}</li>
	 * <li>positionne exactement {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>ne modifie pas physiquement la base</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("delete(parent absent) : IllegalStateException + message exact + aucune écriture BD")
	public void testDeleteParentAbsentAvecPreuveBd() {

		/* ===================== ARRANGE ===================== */
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		final InputDTO input = new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_ABSENT, IT_STP_ALPHA);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> this.controller.delete(input))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_ABSENT, IT_STP_ALPHA))
				.isZero();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(absent) : scénario nominal sans suppression effective.</p>
	 * <ul>
	 * <li>ne lève aucune exception</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + libellé</li>
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
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));
		final InputDTO input = new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA);

		/* ======================= ACT ======================= */
		this.controller.delete(input);

		/* ===================== ASSERT ====================== */
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + IT_STP_ALPHA);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_A, IT_STP_ALPHA))
				.isZero();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(ok) : suppression effective avec preuve BD.</p>
	 * <ul>
	 * <li>ne lève aucune exception</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_DELETE_OK} + libellé</li>
	 * <li>supprime physiquement l'objet persistant visé</li>
	 * <li>ramène le nombre d'enregistrements au niveau initial</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("delete(ok) : suppression effective + message exact + preuve BD")
	public void testDeleteOkAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));
		final OutputDTO cree = this.controller.creer(
				new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA));
		final InputDTO inputSuppression
			= new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA);

		/* ======================= ACT ======================= */
		this.controller.delete(inputSuppression);

		/* ===================== ASSERT ====================== */
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_DELETE_OK + IT_STP_ALPHA);

		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);
		assertThat(this.compterSousTypeProduitEnBase(cree.getIdSousTypeProduit())).isZero();
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_A, IT_STP_ALPHA))
				.isZero();

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
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		final String messageAttendu
			= baseline == 0L
				? SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE
				: SousTypeProduitICuService.MESSAGE_RECHERCHE_OK;

		/* ======================= ACT ======================= */
		final long retour = this.controller.count();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isEqualTo(baseline);
		assertThat(this.controller.getMessage()).isEqualTo(messageAttendu);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>count(après création) : comptage exact après ajout avec preuve BD.</p>
	 * <ul>
	 * <li>prouve d'abord physiquement l'ajout en base</li>
	 * <li>retourne exactement le nouveau volume</li>
	 * <li>positionne exactement {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("count(après création) : volume exact + message exact + preuve BD")
	public void testCountApresCreationAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final Long baseline = this.compterTousLesSousTypeProduitEnBase();
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(IT_TP_PARENT_A));
		final OutputDTO cree
			= this.controller.creer(
					new SousTypeProduitDTO.InputDTO(IT_TP_PARENT_A, IT_STP_ALPHA));

		assertThat(cree).isNotNull();
		assertThat(cree.getIdSousTypeProduit()).isNotNull();
		assertThat(cree.getSousTypeProduit()).isEqualTo(IT_STP_ALPHA);
		assertThat(cree.getTypeProduit()).isEqualTo(IT_TP_PARENT_A);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_A, IT_STP_ALPHA))
				.isEqualTo(1L);

		/* ======================= ACT ======================= */
		final long retour = this.controller.count();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isEqualTo(baseline + 1L);
		assertThat(this.controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline + 1L);
		assertThat(this.compterSousTypeProduitParCoupleEnBase(IT_TP_PARENT_A, IT_STP_ALPHA))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitEnBase(cree.getIdSousTypeProduit())).isEqualTo(1L);

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
		final long baseline = this.compterTousLesSousTypeProduitEnBase();

		/* ======================= ACT ======================= */
		final String message = this.controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(message).isNull();
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

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
		final long baseline = this.compterTousLesSousTypeProduitEnBase();
		final String messageAttendu =
				baseline == 0L
						? SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE
						: SousTypeProduitICuService.MESSAGE_RECHERCHE_OK;

		/* ======================= ACT ======================= */
		final long retour = this.controller.count();
		final String message = this.controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isEqualTo(baseline);
		assertThat(message).isEqualTo(messageAttendu);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>getMessage(après erreur locale) : message produit par le controller.</p>
	 * <ul>
	 * <li>après {@code creer(null)}, retourne exactement
	 * {@link SousTypeProduitIController#MESSAGE_CREER_VUE_NULL}</li>
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
		final long baseline = this.compterTousLesSousTypeProduitEnBase();

		/* ======================= ACT ======================= */
		this.controller.creer(null);
		final String message = this.controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(message)
				.isEqualTo(SousTypeProduitIController.MESSAGE_CREER_VUE_NULL);
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>getMessage(dernier message gagne) : le message courant est écrasé.</p>
	 * <ul>
	 * <li>une erreur locale positionne d'abord
	 * {@link SousTypeProduitIController#MESSAGE_CREER_VUE_NULL}</li>
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
		final long baseline = this.compterTousLesSousTypeProduitEnBase();

		/* ======================= ACT ======================= */
		this.controller.creer(null);
		final String messageErreur = this.controller.getMessage();
		final long retour = this.controller.count();
		final String messageFinal = this.controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(messageErreur)
				.isEqualTo(SousTypeProduitIController.MESSAGE_CREER_VUE_NULL);
		assertThat(retour).isEqualTo(baseline);
		if (retour == 0L) {
			assertThat(messageFinal)
					.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		} else {
			assertThat(messageFinal)
					.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);
		}
		assertThat(this.compterTousLesSousTypeProduitEnBase()).isEqualTo(baseline);

	} // __________________________________________________________________
	
	
		
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