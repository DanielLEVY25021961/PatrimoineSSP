/* ********************************************************************* */
/* **************** TEST INTEGRATION METIER CU ************************* */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.cu.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import jakarta.persistence.EntityManager;

import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO.OutputDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.services.produittype.cu.SousTypeProduitICuService;
import levy.daniel.application.model.services.produittype.cu.TypeProduitICuService;
import levy.daniel.application.model.services.produittype.gateway.impl.SousTypeProduitGatewayJPAService;
import levy.daniel.application.model.services.produittype.gateway.impl.TypeProduitGatewayJPAService;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionDoublon;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreBlank;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreNull;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;
import levy.daniel.application.persistence.metier.produittype.dao.daosJPA.TypeProduitDaoJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.TypeProduitJPA;

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
 * Ce test vérifie le SERVICE UC avec un vrai stockage JPA/H2.
 * </p>
 * <ul>
 * <li>Il injecte le PORT UC {@link SousTypeProduitICuService}.</li>
 * <li>Il injecte aussi le PORT UC {@link TypeProduitICuService}
 * pour créer les parents nécessaires aux scénarios béton.</li>
 * <li>Il importe explicitement le SERVICE UC testé
 * {@link SousTypeProduitCuService} et le SERVICE UC parent
 * {@link TypeProduitCuService}.</li>
 * <li>Il importe explicitement les Gateways JPA nécessaires
 * {@link SousTypeProduitGatewayJPAService}
 * et {@link TypeProduitGatewayJPAService}.</li>
 * <li>Il utilise un stockage H2 en mémoire via {@link DataJpaTest}.</li>
 * <li>Il initialise le stockage avec
 * `truncate-test.sql` puis `data-test.sql`.</li>
 * <li>Il relit certaines références directement en SQL avec
 * {@link JdbcTemplate}, afin de comparer le résultat UC avec une preuve
 * indépendante du SERVICE UC testé.</li>
 * </ul>
 *
 * <p style="font-weight:bold;">Contexte SERVICE UC slice :</p>
 * <ul>
 * <li>{@link DataJpaTest} démarre un contexte Spring réduit, centré sur
 * JPA, les repositories, les transactions, le stockage de test,
 * {@link JdbcTemplate} et l'infrastructure JPA ;</li>
 * <li>ce contexte réduit évite de démarrer toute l'application ;</li>
 * <li>il ne charge volontairement pas les Controllers, ni un scan applicatif
 * global, ni leurs configurations d'intégration ;</li>
 * <li>les SERVICES UC et les Gateways nécessaires au test sont ajoutés
 * explicitement avec {@link Import} ;</li>
 * <li>le test reste donc autonome dans STS et rejouable seul ou avec
 * l'ensemble de la suite.</li>
 * </ul>
 *
 * <p style="font-weight:bold;">Configuration autonome du test :</p>
 * <ul>
 * <li>ce test déclare une classe interne {@link ConfigTest}
 * explicitement chargée par {@link ContextConfiguration} ;</li>
 * <li>cette configuration locale fournit le point d'entrée
 * {@link SpringBootConfiguration} que Spring Boot ne trouvait pas
 * automatiquement en remontant les packages ;</li>
 * <li>elle indique à l'auto-configuration Spring Boot, via
 * {@link AutoConfigurationPackage}, le package des DAO via {@link TypeProduitDaoJPA}
 * et le package des entities via {@link TypeProduitJPA} ;</li>
 * <li>elle ne déclare aucun bootstrap applicatif large,
 * aucun scan de composants, aucun scan manuel des repositories
 * et aucun scan manuel des entities ;</li>
 * <li>elle ne scanne pas explicitement les repositories et n'autorise pas
 * l'override des beans ;</li>
 * <li>elle permet donc au test de rester autonome tout en évitant les
 * collisions de beans observées avec les configurations repository
 * explicites.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 22 janvier 2026
 */
@SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
@Sql(
		scripts = {
				"classpath:/truncate-test.sql",
				"classpath:/data-test.sql"
		},
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@DataJpaTest
@ActiveProfiles({ "test-jpa" })
@Import({
		SousTypeProduitCuService.class,
		TypeProduitCuService.class,
		SousTypeProduitGatewayJPAService.class,
		TypeProduitGatewayJPAService.class
})
@ContextConfiguration(classes = SousTypeProduitCuServiceIntegrationTest.ConfigTest.class)
/*
 * @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
 * Recrée le contexte Spring après chaque méthode de test.
 *
 * @Sql réinitialise le stockage avant chaque test, mais ne réinitialise pas
 * l'état local des beans SERVICE UC injectés par Spring. Or ces SERVICES UC
 * mémorisent le dernier message utilisateur retourné par getMessage().
 *
 * L'annotation est donc placée au niveau de la classe, après la déclaration
 * du contexte autonome chargé par @ContextConfiguration : elle ne participe pas
 * à la découverte des repositories et ne masque aucun conflit Spring, mais
 * force uniquement un nouveau contexte de test après chaque méthode.
 *
 * Elle garantit ainsi qu'un test comme testGetMessageInitialNull() reçoit
 * toujours des SERVICES UC neufs, avec un message initial null, que le test
 * soit lancé seul, après un autre test, ou dans la suite complète.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Tag(SousTypeProduitCuServiceIntegrationTest.TAG)
public class SousTypeProduitCuServiceIntegrationTest {

	// *************************** CONSTANTES ******************************/

	/**
	 * "cu-it".
	 */
	public static final String TAG = "cu-it";

	/**
	 * "   ".
	 */
	public static final String ESPACES = "   ";

	/**
	 * "Outil".
	 */
	public static final String OUTIL = "Outil";

	/**
	 * "Loisir".
	 */
	public static final String LOISIR = "Loisir";

	/**
	 * "Marteau".
	 */
	public static final String MARTEAU = "Marteau";

	/**
	 * "Tournevis".
	 */
	public static final String TOURNEVIS = "Tournevis";

	/**
	 * "Perceuse".
	 */
	public static final String PERCEUSE = "Perceuse";

	/**
	 * "Pince".
	 */
	public static final String PINCE = "Pince";

	/**
	 * "Scie".
	 */
	public static final String SCIE = "Scie";

	/**
	 * "Lime".
	 */
	public static final String LIME = "Lime";

	/**
	 * "Raboteuse".
	 */
	public static final String RABOTEUSE = "Raboteuse";

	/**
	 * "Couteau".
	 */
	public static final String COUTEAU = "Couteau";

	/**
	 * "Ciseau".
	 */
	public static final String CISEAU = "Ciseau";

	/**
	 * "Burin".
	 */
	public static final String BURIN = "Burin";

	/**
	 * "Maillet".
	 */
	public static final String MAILLET = "Maillet";

	/**
	 * "Tenaille".
	 */
	public static final String TENAILLE = "Tenaille";

	/**
	 * "Libelle inconnu".
	 */
	public static final String LIBELLE_INCONNU = "Libelle inconnu";

	/**
	 * "Libelle modification absent".
	 */
	public static final String LIBELLE_MODIFICATION_ABSENT = "Libelle modification absent";

	/**
	 * "Libelle suppression absent".
	 */
	public static final String LIBELLE_SUPPRESSION_ABSENT = "Libelle suppression absent";

	/**
	 * "Tournevis de precision".
	 */
	public static final String TOURNEVIS_PRECISION = "Tournevis de precision";

	/**
	 * "Cle plate".
	 */
	public static final String CLE_PLATE = "Cle plate";

	/**
	 * "Boite a outils".
	 */
	public static final String BOITE_A_OUTILS = "Boite a outils";

	/**
	 * "Etabli pliant".
	 */
	public static final String ETABLI_PLIANT = "Etabli pliant";

	/**
	 * "Recherche Alpha".
	 */
	public static final String RECHERCHE_ALPHA = "Recherche Alpha";

	/**
	 * "Recherche Alpin".
	 */
	public static final String RECHERCHE_ALPIN = "Recherche Alpin";

	/**
	 * "Recherche Al".
	 */
	public static final String RECHERCHE_AL = "Recherche Al";

	/**
	 * "Recherche Zz".
	 */
	public static final String RECHERCHE_ZZ = "Recherche Zz";
	
	/**
	 * "cu-it-Creer".
	 */
	public static final String TAG_CREER = "cu-it-Creer";
	
	/**
	 * "creer(null) : retourne null, message utilisateur, aucune exception, stockage inchangé".
	 */
	public static final String DN_CREER_NULL
		= "creer(null) : retourne null, message utilisateur, "
				+ "aucune exception, stockage inchangé";

	/**
	 * "creer(blank) : ExceptionParametreBlank + message exact + stockage inchangé".
	 */
	public static final String DN_CREER_BLANK
		= "creer(blank) : ExceptionParametreBlank "
				+ "+ message exact + stockage inchangé";

	/**
	 * "creer(parent blank) : IllegalStateException + message exact + stockage inchangé".
	 */
	public static final String DN_CREER_PARENT_BLANK
		= "creer(parent blank) : IllegalStateException "
				+ "+ message exact + stockage inchangé";

	/**
	 * "creer(parent absent) : IllegalStateException + message exact + stockage inchangé".
	 */
	public static final String DN_CREER_PARENT_ABSENT
		= "creer(parent absent) : IllegalStateException "
				+ "+ message exact + stockage inchangé";

	/**
	 * "creer(doublon) : ExceptionDoublon + message exact + preuve stockage d'unicité".
	 */
	public static final String DN_CREER_DOUBLON
		= "creer(doublon) : ExceptionDoublon "
				+ "+ message exact + preuve stockage d'unicité";

	/**
	 * "creer(ok) : preuve stockage + parent prouvé + message exact + round-trip findByLibelle/findById".
	 */
	public static final String DN_CREER_OK
		= "creer(ok) : preuve stockage + parent prouvé "
				+ "+ message exact + round-trip findByLibelle/findById";
	
	/**
	 * "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT".
	 */
	public static final String SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT
		= "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT";

	// *************************** ATTRIBUTS *******************************/
	
	/**
	 * JdbcTemplate (Spring) pour lire le stockage directement
	 * et prouver physiquement les écritures du CU.
	 */
	@Autowired
	private JdbcTemplate jdbcTemplate;

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

	/**
	 * <div>
	 * <p>EntityManager JPA du contexte de test.</p>
	 * <p>
	 * Dans un test {@link DataJpaTest}, chaque méthode s'exécute dans une
	 * transaction de test. Une suppression JPA peut rester en attente dans
	 * le contexte de persistance tant qu'un {@code flush()} n'a pas été
	 * demandé explicitement.
	 * </p>
	 * <p>
	 * Ce test utilise {@link JdbcTemplate} comme preuve indépendante dans le
	 * stockage. Avant une preuve JDBC portant sur une suppression, le test
	 * force donc la synchronisation JPA afin que la lecture SQL directe voie
	 * l'état réellement demandé au stockage par le SERVICE UC.
	 * </p>
	 * </div>
	 */
	@Autowired
	private EntityManager entityManager;


	
	// ************************* CONSTRUCTEURS *****************************/
	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public SousTypeProduitCuServiceIntegrationTest() {
		super();
	}

    
    
    // ===================== CONFIGURATION SPRING =======================//

    
    
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Classe interne de configuration Spring du test d'intégration SERVICE UC.
	 * </p>
	 *
	 * <p>
	 * Cette classe rend le test autonome : elle fournit au bootstrap Spring
	 * Boot une configuration locale explicite, au lieu de dépendre d'une
	 * configuration applicative située ailleurs dans le projet.
	 * </p>
	 *
	 * <p style="font-weight:bold;">Pourquoi cette configuration est nécessaire :</p>
	 * <ul>
	 * <li>{@link DataJpaTest} conserve un contexte Spring réduit, centré sur
	 * JPA, les repositories, les transactions, le stockage de test,
	 * {@link JdbcTemplate} et l'infrastructure JPA ;</li>
	 * <li>dans ce projet, {@link DataJpaTest} ne trouve pas tout seul une
	 * classe racine {@link SpringBootConfiguration} en remontant les packages
	 * depuis ce test ;</li>
	 * <li>{@link ContextConfiguration} charge donc explicitement cette classe
	 * interne locale ;</li>
	 * <li>{@link SpringBootConfiguration} fournit le point d'entrée attendu
	 * par Spring Boot ;</li>
	 * <li>{@link AutoConfigurationPackage} indique explicitement à
	 * l'auto-configuration Spring Boot le package des DAO via
	 * {@link TypeProduitDaoJPA} et le package des entities via
	 * {@link TypeProduitJPA} ;</li>
	 * <li>{@link Import} ajoute explicitement au contexte le SERVICE UC testé
	 * {@link SousTypeProduitCuService}, le SERVICE UC parent
	 * {@link TypeProduitCuService} et leurs Gateways JPA
	 * {@link SousTypeProduitGatewayJPAService},
	 * {@link TypeProduitGatewayJPAService} ;</li>
	 * <li>le test ne déclare aucun scan manuel des repositories :
	 * ils restent pris en charge par le slice {@link DataJpaTest} ;</li>
	 * <li>le test ne déclare aucun scan manuel des entities :
	 * le package des entities JPA est fourni par
	 * {@link AutoConfigurationPackage} avec {@link TypeProduitJPA}.</li>
	 * </ul>
	 *
	 * <p style="font-weight:bold;">Ce que cette configuration ne fait pas :</p>
	 * <ul>
	 * <li>elle ne déclare aucun bootstrap applicatif large ;</li>
	 * <li>elle ne déclare aucun scan de composants ;</li>
	 * <li>elle ne déclare aucun scan manuel des repositories ;</li>
	 * <li>elle ne déclare aucun scan manuel des entities ;</li>
	 * <li>elle ne force aucun scan manuel des repositories ;</li>
	 * <li>elle ne charge aucun Controller ;</li>
	 * <li>elle ne masque jamais les collisions de beans Spring.</li>
	 * </ul>
	 *
	 * <p>
	 * Les SERVICES UC testés, les Gateways JPA, les repositories et les
	 * entities JPA utiles sont découverts ou importés dans le périmètre
	 * explicite déclaré pour ce test. Le test reste donc un test
	 * d'intégration SERVICE UC, autonome, et limité au stockage JPA
	 * nécessaire.
	 * </p>
	 * </div>
	 *
	 * @author Daniel Lévy
	 * @version 1.0
	 * @since 22 janvier 2026
	 */
	@SpringBootConfiguration(proxyBeanMethods = false)
	@AutoConfigurationPackage(basePackageClasses = {
			TypeProduitDaoJPA.class,
			TypeProduitJPA.class
	})
	public static final class ConfigTest { // NOPMD by danyl on 22/01/2026 10:00

		/**
		 * <div>
		 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
		 * </div>
		 */
		public ConfigTest() {
			super();
		}

	} // FIN DE LA CLASSE INTERNE ConfigTest.------------------------------

    
    
    // =========================== TESTS ==================================
    
    

    // ============================ creer =================================
    
    
	
	/**
	 * <div>
	 * <p>garantit que creer(null) :</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>émet un message
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_NULL_KO}</li>
	 * <li>ne jette aucune exception</li>
	 * <li>n'écrit rien dans le stockage réel</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DN_CREER_NULL)
	@Test
	public void testCreerNull() throws Exception {

		/* ARRANGE :
		 * compte d'abord en SQL
		 * le nombre d'enregistrements dans le stockage
		 * avant l'appel au SERVICE UC.
		 */
		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvant).isNotNull();

		/* ACT :
		 * appelle service.creer(null).
		 */
		final OutputDTO dto = this.service.creer(null);

		/* ASSERT :
		 * garantit que service.creer(null) retourne null.
		 */
		assertThat(dto).isNull();

		/* Garantit que service.creer(null) émet un message
		 * MESSAGE_CREER_NULL_KO.
		 */
		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_NULL_KO);

		/* ASSERT :
		 * compte ensuite en SQL
		 * le nombre d'enregistrements dans le stockage
		 * après service.creer(null),
		 * afin de prouver que l'appel n'a produit aucune écriture.
		 */
		final Long countApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countApres).isNotNull();
		assertThat(countApres).isEqualTo(countAvant);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que creer(...) avec un libellé enfant blank :</p>
	 * <ul>
	 * <li>jette une {@link ExceptionParametreBlank}</li>
	 * <li>émet un message
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_LIBELLE_BLANK_KO}</li>
	 * <li>n'écrit rien dans le stockage réel</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DN_CREER_BLANK)
	@Test
	public void testCreerBlank() throws Exception {

		/* ARRANGE :
		 * compte d'abord en SQL
		 * le nombre d'enregistrements dans le stockage
		 * avant l'appel au SERVICE UC.
		 */
		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvant).isNotNull();

		/* Prépare un InputDTO
		 * dont le libellé métier enfant est blank.
		 */
		final InputDTO input = new SousTypeProduitDTO.InputDTO(
				OUTIL,
				ESPACES);

		/* ACT - ASSERT :
		 * Garantit que this.service.creer(libellé blank)
		 * - jette une ExceptionParametreBlank
		 * - avec un message MESSAGE_CREER_LIBELLE_BLANK_KO.
		 */
		assertThatThrownBy(() -> this.service.creer(input))
				.isInstanceOf(ExceptionParametreBlank.class)
				.hasMessage(
						SousTypeProduitICuService
								.MESSAGE_CREER_LIBELLE_BLANK_KO);

		/* Garantit le message utilisateur
		 * MESSAGE_CREER_LIBELLE_BLANK_KO.
		 */
		assertThat(this.service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService
								.MESSAGE_CREER_LIBELLE_BLANK_KO);

		/* ASSERT :
		 * compte ensuite en SQL
		 * le nombre d'enregistrements dans le stockage
		 * après l'échec contractuel.
		 */
		final Long countApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countApres).isNotNull();
		assertThat(countApres).isEqualTo(countAvant);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que creer(...) avec un libellé parent blank :</p>
	 * <ul>
	 * <li>jette une {@link IllegalStateException}</li>
	 * <li>émet un message
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO}</li>
	 * <li>n'écrit rien dans le stockage réel</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DN_CREER_PARENT_BLANK)
	@Test
	public void testCreerParentBlank() throws Exception {

		/* ARRANGE :
		 * compte d'abord en SQL
		 * le nombre d'enregistrements dans le stockage
		 * avant l'appel au SERVICE UC.
		 */
		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvant).isNotNull();

		/* Prépare un InputDTO
		 * dont le libellé parent est blank.
		 */
		final InputDTO input = new SousTypeProduitDTO.InputDTO(
				ESPACES,
				MARTEAU);

		/* ACT - ASSERT :
		 * Garantit que this.service.creer(parent blank)
		 * - jette une IllegalStateException
		 * - avec un message MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO.
		 */
		assertThatThrownBy(() -> this.service.creer(input))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(
						SousTypeProduitICuService
								.MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO);

		/* Garantit le message utilisateur
		 * MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO.
		 */
		assertThat(this.service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService
								.MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO);

		/* ASSERT :
		 * compte ensuite en SQL
		 * le nombre d'enregistrements dans le stockage
		 * après l'échec contractuel.
		 */
		final Long countApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countApres).isNotNull();
		assertThat(countApres).isEqualTo(countAvant);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que creer(...) avec un parent absent :</p>
	 * <ul>
	 * <li>jette une {@link IllegalStateException}</li>
	 * <li>émet un message
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO}</li>
	 * <li>n'écrit rien dans le stockage réel</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DN_CREER_PARENT_ABSENT)
	@Test
	public void testCreerParentAbsent() throws Exception {

		/* ARRANGE :
		 * compte d'abord en SQL
		 * le nombre d'enregistrements dans le stockage
		 * avant l'appel au SERVICE UC.
		 */
		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvant).isNotNull();

		/* Prépare un InputDTO
		 * dont le parent n'a pas été créé dans le stockage.
		 */
		final InputDTO input = new SousTypeProduitDTO.InputDTO(
				OUTIL,
				MARTEAU);

		/* ACT - ASSERT :
		 * Garantit que this.service.creer(parent absent)
		 * - jette une IllegalStateException
		 * - avec un message MESSAGE_CREER_PARENT_NON_PERSISTANT_KO.
		 */
		assertThatThrownBy(() -> this.service.creer(input))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(
						SousTypeProduitICuService
								.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		/* Garantit le message utilisateur
		 * MESSAGE_CREER_PARENT_NON_PERSISTANT_KO.
		 */
		assertThat(this.service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService
								.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		/* ASSERT :
		 * compte ensuite en SQL
		 * le nombre d'enregistrements dans le stockage
		 * après l'échec contractuel.
		 */
		final Long countApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countApres).isNotNull();
		assertThat(countApres).isEqualTo(countAvant);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que si l'appelant tente creer(...)
	 * avec un couple [parent, libellé] déjà présent dans le stockage :</p>
	 * <ul>
	 * <li>la première création réussit réellement</li>
	 * <li>la seconde création lève une {@link ExceptionDoublon}</li>
	 * <li>le message utilisateur exact est
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_DOUBLON_KO} + libellé</li>
	 * <li>aucune nouvelle ligne n'est créée dans le stockage
	 * lors de la tentative de doublon</li>
	 * <li>l'unique ligne créée portant déjà ce couple reste inchangée</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DN_CREER_DOUBLON)
	@Test
	public void testCreerDoublonAvecPreuveStockage() throws Exception {

		/* ARRANGE :
		 * crée d'abord le parent persistant requis.
		 */
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));

		/* Prépare un DTO valide non seedé.
		 *
		 * Le premier appel à creer(...) créera réellement l'objet métier.
		 * Le second appel avec le même DTO déclenchera ensuite
		 * le cas contractuel de doublon.
		 */
		final InputDTO input = new SousTypeProduitDTO.InputDTO(
				OUTIL,
				TOURNEVIS);

		/* Vérifie d'abord que le couple du test
		 * n'est pas déjà présent dans le stockage.
		 */
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				TOURNEVIS))
				.isEqualTo(0L);

		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvant).isNotNull();

		/* ACT :
		 * crée une première fois l'objet métier.
		 */
		final OutputDTO cree = this.service.creer(input);

		/* ASSERT :
		 * garantit que la première création réussit
		 * et retourne un DTO persistant.
		 */
		assertThat(cree).isNotNull();
		assertThat(cree.getIdSousTypeProduit()).isNotNull();
		assertThat(cree.getSousTypeProduit()).isEqualTo(TOURNEVIS);
		assertThat(cree.getTypeProduit()).isEqualTo(OUTIL);

		/* Garantit physiquement dans le stockage
		 * qu'une seule ligne porte le couple créé.
		 */
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				TOURNEVIS))
				.isEqualTo(1L);

		/* Garantit physiquement dans le stockage
		 * que l'identifiant retourné correspond à une ligne réelle.
		 */
		assertThat(this.compterSousTypeProduitDansStockage(
				cree.getIdSousTypeProduit()))
				.isEqualTo(1L);

		/* Garantit physiquement dans le stockage
		 * que le parent stocké est le parent attendu.
		 */
		assertThat(this.lireParentSousTypeProduitDansStockage(
				cree.getIdSousTypeProduit()))
				.isEqualTo(OUTIL);

		final Long countApresPremiereCreation = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countApresPremiereCreation).isNotNull();
		assertThat(countApresPremiereCreation).isEqualTo(countAvant + 1L);

		/* ACT - ASSERT :
		 * sollicite une deuxième fois la méthode creer(...)
		 * avec le même couple déjà présent.
		 *
		 * Le SERVICE UC doit refuser le doublon avant toute nouvelle
		 * écriture dans le stockage.
		 */
		assertThatThrownBy(() -> this.service.creer(input))
				.isInstanceOf(ExceptionDoublon.class)
				.hasMessage(
						SousTypeProduitICuService.MESSAGE_CREER_DOUBLON_KO
								+ TOURNEVIS);

		/* Garantit le message utilisateur exact.
		 */
		assertThat(this.service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_CREER_DOUBLON_KO
								+ TOURNEVIS);

		/* ASSERT :
		 * contrôle ensuite par SQL direct
		 * que le stockage contient toujours une seule ligne
		 * pour ce couple.
		 */
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				TOURNEVIS))
				.isEqualTo(1L);

		assertThat(this.compterSousTypeProduitDansStockage(
				cree.getIdSousTypeProduit()))
				.isEqualTo(1L);

		/* Garantit enfin que le volume total du stockage
		 * n'a pas augmenté lors de la tentative de doublon.
		 */
		final Long countApresDoublon = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countApresDoublon).isNotNull();
		assertThat(countApresDoublon).isEqualTo(countApresPremiereCreation);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que creer(OK) :</p>
	 * <ul>
	 * <li>crée d'abord le parent persistant requis</li>
	 * <li>crée réellement une ligne dans le stockage</li>
	 * <li>retourne un {@link OutputDTO} persistant</li>
	 * <li>émet un message
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_OK}</li>
	 * <li>prouve le rattachement au parent dans le stockage</li>
	 * <li>rend la donnée retrouvable via le SERVICE UC par libellé et par ID</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DN_CREER_OK)
	@Test
	public void testCreerNominalAvecPreuveStockageEtRoundTrip()
			throws Exception {

		/* ARRANGE :
		 * crée d'abord le parent persistant requis.
		 */
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));

		/* Prépare un DTO valide à créer.
		 */
		final InputDTO input = new SousTypeProduitDTO.InputDTO(
				OUTIL,
				MARTEAU);

		/* Vérifie d'abord que le couple du test
		 * n'est pas déjà présent dans le stockage.
		 */
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				MARTEAU))
				.isEqualTo(0L);

		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvant).isNotNull();

		/* ACT :
		 * sollicite la méthode creer(...)
		 * dans un scénario nominal complet de persistance réelle.
		 */
		final OutputDTO cree = this.service.creer(input);

		/* ASSERT :
		 * garantit d'abord que le DTO retourné
		 * est bien persistant et correctement renseigné.
		 */
		assertThat(cree).isNotNull();
		assertThat(cree.getIdSousTypeProduit()).isNotNull();
		assertThat(cree.getSousTypeProduit()).isEqualTo(MARTEAU);
		assertThat(cree.getTypeProduit()).isEqualTo(OUTIL);

		/* Garantit que le message de succès de création
		 * est positionné avant tout autre appel au SERVICE UC.
		 */
		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_OK);

		/* Garantit que la création augmente bien le nombre total
		 * de lignes dans le stockage réel.
		 */
		final Long countApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countApres).isNotNull();
		assertThat(countApres).isEqualTo(countAvant + 1L);

		/* Garantit physiquement dans le stockage
		 * qu'une seule ligne porte bien l'identifiant créé.
		 */
		assertThat(this.compterSousTypeProduitDansStockage(
				cree.getIdSousTypeProduit()))
				.isEqualTo(1L);

		/* Garantit physiquement dans le stockage
		 * que la colonne SOUS_TYPE_PRODUIT a bien été écrite
		 * avec le libellé métier attendu.
		 */
		assertThat(this.lireLibelleSousTypeProduitDansStockage(
				cree.getIdSousTypeProduit()))
				.isEqualTo(MARTEAU);

		/* Garantit physiquement dans le stockage
		 * que le parent stocké est le parent attendu.
		 */
		assertThat(this.lireParentSousTypeProduitDansStockage(
				cree.getIdSousTypeProduit()))
				.isEqualTo(OUTIL);

		/* Garantit physiquement dans le stockage
		 * qu'une seule ligne porte le couple créé.
		 */
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				MARTEAU))
				.isEqualTo(1L);

		/* Garantit que l'objet nouvellement créé
		 * est bien retrouvable par libellé via le SERVICE UC.
		 */
		final List<OutputDTO> trouvesParLibelle = this.service.findByLibelle(
				MARTEAU);

		assertThat(trouvesParLibelle).isNotNull();
		assertThat(trouvesParLibelle).hasSize(1);
		assertThat(trouvesParLibelle.get(0).getIdSousTypeProduit())
				.isEqualTo(cree.getIdSousTypeProduit());
		assertThat(trouvesParLibelle.get(0).getSousTypeProduit())
				.isEqualTo(MARTEAU);
		assertThat(trouvesParLibelle.get(0).getTypeProduit())
				.isEqualTo(OUTIL);

		/* Garantit que l'objet nouvellement créé
		 * est bien retrouvable par identifiant via le SERVICE UC.
		 */
		final OutputDTO trouveParId = this.service.findById(
				cree.getIdSousTypeProduit());

		assertThat(trouveParId).isNotNull();
		assertThat(trouveParId.getIdSousTypeProduit())
				.isEqualTo(cree.getIdSousTypeProduit());
		assertThat(trouveParId.getSousTypeProduit()).isEqualTo(MARTEAU);
		assertThat(trouveParId.getTypeProduit()).isEqualTo(OUTIL);

	} // __________________________________________________________________
    
    
    
    // ======================== RechercherTous ============================
	
	
	
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

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));

		this.service.creer(new SousTypeProduitDTO.InputDTO(OUTIL, PERCEUSE));
		this.service.creer(new SousTypeProduitDTO.InputDTO(OUTIL, PINCE));

		final List<OutputDTO> dtos = this.service.rechercherTous();

		assertThat(dtos).isNotNull();
		assertThat(dtos)
				.extracting(SousTypeProduitDTO.OutputDTO::getSousTypeProduit)
				.contains(PERCEUSE, PINCE);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>rechercherTous() : scénario nominal béton avec preuve stockage.</p>
	 * <ul>
	 * <li>retourne une liste non {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>reste cohérent avec {@link SousTypeProduitICuService#count()}</li>
	 * <li>contient les créations du test</li>
	 * <li>permet de relier les DTO retournés à des lignes réellement présentes
	 * dans le stockage</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("rechercherTous(ok) : message exact + cohérence count + présence des créations + preuve stockage")
	public void testRechercherTousOkAvecPreuveStockage() throws Exception {

		/* ===================== ARRANGE ===================== */
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(LOISIR));

		final OutputDTO creeGamma = this.service.creer(
				new SousTypeProduitDTO.InputDTO(OUTIL, PERCEUSE));
		final OutputDTO creeDelta = this.service.creer(
				new SousTypeProduitDTO.InputDTO(LOISIR, PINCE));

		final long attendu = this.service.count();

		/* ======================= ACT ======================= */
		final List<SousTypeProduitDTO.OutputDTO> dtos = this.service.rechercherTous();

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNotNull();
		assertThat(dtos.size()).isEqualTo((int) attendu);

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		assertThat(dtos)
				.extracting(SousTypeProduitDTO.OutputDTO::getSousTypeProduit)
				.contains(PERCEUSE, PINCE);

		final OutputDTO dtoGamma = dtos.stream()
				.filter(dto -> PERCEUSE.equals(dto.getSousTypeProduit()))
				.findFirst()
				.orElse(null);

		final OutputDTO dtoDelta = dtos.stream()
				.filter(dto -> PINCE.equals(dto.getSousTypeProduit()))
				.findFirst()
				.orElse(null);

		assertThat(dtoGamma).isNotNull();
		assertThat(dtoGamma.getIdSousTypeProduit())
				.isEqualTo(creeGamma.getIdSousTypeProduit());
		assertThat(dtoGamma.getTypeProduit())
				.isEqualTo(OUTIL);

		assertThat(dtoDelta).isNotNull();
		assertThat(dtoDelta.getIdSousTypeProduit())
				.isEqualTo(creeDelta.getIdSousTypeProduit());
		assertThat(dtoDelta.getTypeProduit())
				.isEqualTo(LOISIR);

		/* preuve stockage : les lignes existent physiquement et portent le bon parent. */
		assertThat(this.compterSousTypeProduitDansStockage(creeGamma.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(creeGamma.getIdSousTypeProduit()))
				.isEqualTo(PERCEUSE);
		assertThat(this.lireParentSousTypeProduitDansStockage(creeGamma.getIdSousTypeProduit()))
				.isEqualTo(OUTIL);

		assertThat(this.compterSousTypeProduitDansStockage(creeDelta.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(creeDelta.getIdSousTypeProduit()))
				.isEqualTo(PINCE);
		assertThat(this.lireParentSousTypeProduitDansStockage(creeDelta.getIdSousTypeProduit()))
				.isEqualTo(LOISIR);

		assertThat(this.compterSousTypeProduitParCoupleDansStockage(OUTIL, PERCEUSE))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(LOISIR, PINCE))
				.isEqualTo(1L);
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>rechercherTous() : stockage vide.</p>
	 * <ul>
	 * <li>retourne une liste vide mais non {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>reste cohérent avec un stockage physiquement vide</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Sql(
			scripts = "classpath:/truncate-test.sql",
			executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@DisplayName("rechercherTous(vide) : liste vide + message MESSAGE_RECHERCHE_VIDE + stockage vide")
	public void testRechercherTousVide() throws Exception {

		/* ===================== ARRANGE ===================== */
		assertThat(this.service.count()).isEqualTo(0L);
		assertThat(this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class)).isEqualTo(0L);

		/* ======================= ACT ======================= */
		final List<SousTypeProduitDTO.OutputDTO> dtos = this.service.rechercherTous();

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNotNull();
		assertThat(dtos).isEmpty();

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		
	} // __________________________________________________________________	

	
	
	// ===================== rechercherTousString =========================
	
	
	
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

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));

		this.service.creer(new SousTypeProduitDTO.InputDTO(OUTIL, SCIE));
		this.service.creer(new SousTypeProduitDTO.InputDTO(OUTIL, LIME));

		final List<String> libelles = this.service.rechercherTousString();

		assertThat(libelles).isNotNull();
		assertThat(libelles).contains(SCIE, LIME);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousString() : scénario nominal béton avec preuve stockage.</p>
	 * <ul>
	 * <li>retourne une liste non {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>contient les libellés créés</li>
	 * <li>n'expose aucun doublon</li>
	 * <li>n'expose aucun libellé blank</li>
	 * <li>reste cohérent avec la présence physique dans le stockage</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("rechercherTousString(ok) : message exact + contient les créations + sans doublon + preuve stockage")
	public void testRechercherTousStringOkAvecPreuveStockage() throws Exception {

		/* ===================== ARRANGE ===================== */
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));

		final OutputDTO creeEpsilon = this.service.creer(
				new SousTypeProduitDTO.InputDTO(OUTIL, SCIE));
		final OutputDTO creeZeta = this.service.creer(
				new SousTypeProduitDTO.InputDTO(OUTIL, LIME));

		assertThat(creeEpsilon).isNotNull();
		assertThat(creeZeta).isNotNull();

		/* ======================= ACT ======================= */
		final List<String> libelles = this.service.rechercherTousString();

		/* ===================== ASSERT ====================== */
		assertThat(libelles).isNotNull();
		assertThat(libelles).contains(SCIE, LIME);
		assertThat(libelles).doesNotHaveDuplicates();
		assertThat(libelles).allMatch(libelle -> libelle != null && !libelle.isBlank());

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* preuve stockage : les lignes créées existent physiquement. */
		assertThat(this.compterSousTypeProduitDansStockage(creeEpsilon.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(creeEpsilon.getIdSousTypeProduit()))
				.isEqualTo(SCIE);
		assertThat(this.lireParentSousTypeProduitDansStockage(creeEpsilon.getIdSousTypeProduit()))
				.isEqualTo(OUTIL);

		assertThat(this.compterSousTypeProduitDansStockage(creeZeta.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(creeZeta.getIdSousTypeProduit()))
				.isEqualTo(LIME);
		assertThat(this.lireParentSousTypeProduitDansStockage(creeZeta.getIdSousTypeProduit()))
				.isEqualTo(OUTIL);

		assertThat(this.compterSousTypeProduitParCoupleDansStockage(OUTIL, SCIE))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(OUTIL, LIME))
				.isEqualTo(1L);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousString() : stockage vide.</p>
	 * <ul>
	 * <li>retourne une liste vide mais non {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>reste cohérent avec un stockage physiquement vide</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Sql(
			scripts = "classpath:/truncate-test.sql",
			executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@DisplayName("rechercherTousString(vide) : liste vide + message MESSAGE_RECHERCHE_VIDE + stockage vide")
	public void testRechercherTousStringVide() throws Exception {

		/* ===================== ARRANGE ===================== */
		assertThat(this.service.count()).isEqualTo(0L);
		assertThat(this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class)).isEqualTo(0L);

		/* ======================= ACT ======================= */
		final List<String> libelles = this.service.rechercherTousString();

		/* ===================== ASSERT ====================== */
		assertThat(libelles).isNotNull();
		assertThat(libelles).isEmpty();

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________


    
    // ================== rechercherTousParPage ===========================
    
    

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

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousParPage(ok) : test "béton" sur la cohérence du {@link ResultatPage}.</p>
	 * <ul>
	 * <li>le {@code totalElements} reflète l'état stockage + créations</li>
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

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));

		final long countAvant = this.service.count();

		this.service.creer(new SousTypeProduitDTO.InputDTO(OUTIL, RABOTEUSE));
		this.service.creer(new SousTypeProduitDTO.InputDTO(OUTIL, COUTEAU));
		this.service.creer(new SousTypeProduitDTO.InputDTO(OUTIL, CISEAU));
		this.service.creer(new SousTypeProduitDTO.InputDTO(OUTIL, BURIN));
		this.service.creer(new SousTypeProduitDTO.InputDTO(OUTIL, MAILLET));

		final long attendu = countAvant + 5L;

		final RequetePage requete = new RequetePage(0, 2);

		final ResultatPage<OutputDTO> rp = this.service.rechercherTousParPage(requete);

		assertThat(rp).isNotNull();
		assertResultatPageCoherent(rp);
		assertThat(rp.getPageNumber()).isEqualTo(0);
		assertThat(rp.getPageSize()).isEqualTo(2);
		assertThat(rp.getTotalElements()).isEqualTo(attendu);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousParPage(ok) : test béton avec pagination cohérente
	 * et preuve stockage.</p>
	 * <ul>
	 * <li>retourne un {@link ResultatPage} non {@code null}</li>
	 * <li>reprend le numéro de page</li>
	 * <li>reprend la taille de page</li>
	 * <li>reprend le total d'éléments</li>
	 * <li>retourne un contenu DTO cohérent avec les créations du test</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_PAGINEE_OK}</li>
	 * <li>prouve physiquement l'existence dans le stockage
	 * des objets créés</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("rechercherTousParPage(ok) : ResultatPage cohérent + message exact + preuve stockage")
	public void testRechercherTousParPageOkAvecPreuveStockage() throws Exception {

		/* ===================== ARRANGE ===================== */
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));

		final long countAvant = this.service.count();

		final OutputDTO cree01 = this.service.creer(
				new SousTypeProduitDTO.InputDTO(OUTIL, RABOTEUSE));
		final OutputDTO cree02 = this.service.creer(
				new SousTypeProduitDTO.InputDTO(OUTIL, COUTEAU));
		final OutputDTO cree03 = this.service.creer(
				new SousTypeProduitDTO.InputDTO(OUTIL, CISEAU));
		final OutputDTO cree04 = this.service.creer(
				new SousTypeProduitDTO.InputDTO(OUTIL, BURIN));
		final OutputDTO cree05 = this.service.creer(
				new SousTypeProduitDTO.InputDTO(OUTIL, MAILLET));

		final long attendu = countAvant + 5L;

		final RequetePage requete = new RequetePage(0, 100);

		/* ======================= ACT ======================= */
		final ResultatPage<OutputDTO> rp = this.service.rechercherTousParPage(requete);

		/* ===================== ASSERT ====================== */
		assertThat(rp).isNotNull();
		assertThat(rp.getPageNumber()).isEqualTo(0);
		assertThat(rp.getPageSize()).isEqualTo(100);
		assertThat(rp.getTotalElements()).isEqualTo(attendu);

		assertThat(rp.getContent()).isNotNull();
		assertThat(rp.getContent().size()).isLessThanOrEqualTo(100);

		assertThat(rp.getContent())
				.extracting(OutputDTO::getSousTypeProduit)
				.contains(
						RABOTEUSE,
						COUTEAU,
						CISEAU,
						BURIN,
						MAILLET);

		assertThat(rp.getContent())
		.extracting(OutputDTO::getIdSousTypeProduit)
		.contains(
				cree01.getIdSousTypeProduit(),
				cree02.getIdSousTypeProduit(),
				cree03.getIdSousTypeProduit(),
				cree04.getIdSousTypeProduit(),
				cree05.getIdSousTypeProduit());

		/*
		 * La page peut contenir aussi des données déjà présentes via data-test.sql.
		 * On contrôle donc le parent uniquement sur les 5 DTO créés par ce test.
		 */
		final List<OutputDTO> dtosCreesDuTest = rp.getContent().stream()
				.filter(dto ->
						cree01.getIdSousTypeProduit().equals(dto.getIdSousTypeProduit())
						|| cree02.getIdSousTypeProduit().equals(dto.getIdSousTypeProduit())
						|| cree03.getIdSousTypeProduit().equals(dto.getIdSousTypeProduit())
						|| cree04.getIdSousTypeProduit().equals(dto.getIdSousTypeProduit())
						|| cree05.getIdSousTypeProduit().equals(dto.getIdSousTypeProduit()))
				.toList();
		
		assertThat(dtosCreesDuTest).hasSize(5);
		
		assertThat(dtosCreesDuTest)
				.extracting(OutputDTO::getTypeProduit)
				.containsOnly(OUTIL);

	} // __________________________________________________________________	
	
	
	
	// ========================= findByLibelle ============================
	
	
	
	/**
	 * <div>
	 * <p>findByLibelle(blank) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne une liste vide mais non {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelle(blank) : liste vide + message exact MESSAGE_PARAM_BLANK")
	public void testFindByLibelleBlank() throws Exception {

		final List<OutputDTO> dtos = this.service.findByLibelle(ESPACES);

		assertThat(dtos).isNotNull().isEmpty();
		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);

	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>findByLibelle(introuvable) : aucun résultat exact en stockage.</p>
	 * <ul>
	 * <li>retourne une liste vide mais non {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + libellé</li>
	 * <li>prouve physiquement l'absence dans le stockage pour ce libellé</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelle(introuvable) : liste vide + message exact MESSAGE_OBJ_INTROUVABLE + libellé")
	public void testFindByLibelleIntrouvable() throws Exception {

		final List<OutputDTO> dtos = this.service.findByLibelle(LIBELLE_INCONNU);

		assertThat(dtos).isNotNull().isEmpty();
		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + LIBELLE_INCONNU);
		assertThat(this.compterSousTypeProduitParLibelleDansStockage(LIBELLE_INCONNU))
				.isEqualTo(0L);

	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>findByLibelle(ok) : le même libellé peut exister sous plusieurs parents.</p>
	 * <ul>
	 * <li>crée deux parents persistants distincts</li>
	 * <li>crée le même libellé de SousTypeProduit sous ces deux parents</li>
	 * <li>retourne une liste DTO de taille 2</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}</li>
	 * <li>prouve physiquement l'existence des deux couples dans le stockage</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelle(ok) : retourne 2 DTO sur 2 parents distincts + message exact + preuve stockage")
	public void testFindByLibelleOk() throws Exception {

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(LOISIR));

		final OutputDTO creeA = this.service.creer(
				new SousTypeProduitDTO.InputDTO(OUTIL, PINCE));
		final OutputDTO creeB = this.service.creer(
				new SousTypeProduitDTO.InputDTO(LOISIR, PINCE));

		final List<OutputDTO> dtos = this.service.findByLibelle(PINCE);

		assertThat(dtos).isNotNull();
		assertThat(dtos).hasSize(2);

		assertThat(dtos)
				.extracting(OutputDTO::getSousTypeProduit)
				.containsExactly(PINCE, PINCE);

		assertThat(dtos)
				.extracting(OutputDTO::getTypeProduit)
				.containsExactly(OUTIL, LOISIR);

		assertThat(dtos)
				.extracting(OutputDTO::getIdSousTypeProduit)
				.containsExactly(
						creeA.getIdSousTypeProduit(),
						creeB.getIdSousTypeProduit());

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_FINDBYLIBELLE_SUCCES_RECHERCHE);

		assertThat(this.compterSousTypeProduitDansStockage(creeA.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(creeA.getIdSousTypeProduit()))
				.isEqualTo(PINCE);
		assertThat(this.lireParentSousTypeProduitDansStockage(creeA.getIdSousTypeProduit()))
				.isEqualTo(OUTIL);

		assertThat(this.compterSousTypeProduitDansStockage(creeB.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(creeB.getIdSousTypeProduit()))
				.isEqualTo(PINCE);
		assertThat(this.lireParentSousTypeProduitDansStockage(creeB.getIdSousTypeProduit()))
				.isEqualTo(LOISIR);

		assertThat(this.compterSousTypeProduitParCoupleDansStockage(OUTIL, PINCE))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(LOISIR, PINCE))
				.isEqualTo(1L);

	} // __________________________________________________________________

	
	
	// ====================== findByLibelleRapide =========================
	
	
	
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
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_PARAM_NULL);

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_NULL);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>Si pContenu est blank :
	 * délègue au scénario complet de rechercherTous().</p>
	 * <ul>
	 * <li>retourne une liste non nulle</li>
	 * <li>contient les créations du test</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>reste cohérent avec la présence physique dans le stockage</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelleRapide(blank) : délègue à rechercherTous() + message MESSAGE_RECHERCHE_OK")
	public void testFindByLibelleRapideBlank() throws Exception {

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));

		final OutputDTO cree1 = this.service.creer(
				new SousTypeProduitDTO.InputDTO(OUTIL, PERCEUSE));
		final OutputDTO cree2 = this.service.creer(
				new SousTypeProduitDTO.InputDTO(OUTIL, PINCE));

		final List<OutputDTO> dtos = this.service.findByLibelleRapide(ESPACES);

		assertThat(dtos).isNotNull();
		assertThat(dtos)
				.extracting(OutputDTO::getSousTypeProduit)
				.contains(PERCEUSE, PINCE);

		assertThat(dtos)
				.extracting(OutputDTO::getIdSousTypeProduit)
				.contains(cree1.getIdSousTypeProduit(), cree2.getIdSousTypeProduit());

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		assertThat(this.compterSousTypeProduitDansStockage(cree1.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitDansStockage(cree2.getIdSousTypeProduit()))
				.isEqualTo(1L);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>Si aucun libellé ne correspond :
	 * retourne une liste vide + MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelleRapide(introuvable) : liste vide + message MESSAGE_RECHERCHE_VIDE")
	public void testFindByLibelleRapideIntrouvable() throws Exception {

		final List<OutputDTO> dtos = this.service.findByLibelleRapide(RECHERCHE_ZZ);

		assertThat(dtos).isNotNull();
		assertThat(dtos).isEmpty();

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>Si des libellés correspondent :
	 * retourne une liste DTO cohérente, sans doublon,
	 * et émet MESSAGE_RECHERCHE_OK.</p>
	 * <ul>
	 * <li>les objets correspondants existent physiquement dans le stockage</li>
	 * <li>les objets hors cible ne doivent pas être attendus dans le résultat</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelleRapide(ok) : liste DTO cohérente + sans doublon + message exact + preuve stockage")
	public void testFindByLibelleRapideOkAvecPreuveStockage() throws Exception {

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(LOISIR));

		final String fragment = RECHERCHE_AL;

		final OutputDTO cree1 = this.service.creer(
				new SousTypeProduitDTO.InputDTO(OUTIL, RECHERCHE_ALPHA));
		final OutputDTO cree2 = this.service.creer(
				new SousTypeProduitDTO.InputDTO(LOISIR, RECHERCHE_ALPIN));
		final OutputDTO creeHorsCible = this.service.creer(
				new SousTypeProduitDTO.InputDTO(OUTIL, MARTEAU));

		final List<OutputDTO> dtos = this.service.findByLibelleRapide(fragment);

		assertThat(dtos).isNotNull();
		assertThat(dtos).doesNotHaveDuplicates();

		assertThat(dtos)
				.extracting(OutputDTO::getSousTypeProduit)
				.contains(RECHERCHE_ALPHA, RECHERCHE_ALPIN)
				.doesNotContain(MARTEAU);

		assertThat(dtos)
				.extracting(OutputDTO::getIdSousTypeProduit)
				.contains(cree1.getIdSousTypeProduit(), cree2.getIdSousTypeProduit())
				.doesNotContain(creeHorsCible.getIdSousTypeProduit());

		assertThat(dtos)
				.extracting(OutputDTO::getTypeProduit)
				.contains(OUTIL, LOISIR);

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		assertThat(this.compterSousTypeProduitDansStockage(cree1.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(cree1.getIdSousTypeProduit()))
				.isEqualTo(RECHERCHE_ALPHA);
		assertThat(this.lireParentSousTypeProduitDansStockage(cree1.getIdSousTypeProduit()))
				.isEqualTo(OUTIL);

		assertThat(this.compterSousTypeProduitDansStockage(cree2.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(cree2.getIdSousTypeProduit()))
				.isEqualTo(RECHERCHE_ALPIN);
		assertThat(this.lireParentSousTypeProduitDansStockage(cree2.getIdSousTypeProduit()))
				.isEqualTo(LOISIR);

		assertThat(this.compterSousTypeProduitDansStockage(creeHorsCible.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(creeHorsCible.getIdSousTypeProduit()))
				.isEqualTo(MARTEAU);
		assertThat(this.lireParentSousTypeProduitDansStockage(creeHorsCible.getIdSousTypeProduit()))
				.isEqualTo(OUTIL);

	} // __________________________________________________________________	
	
	

	// ======================= findAllByParent(...) =======================

	
	
	/**
	 * <div>
	 * <p>findAllByParent(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne {@link SousTypeProduitICuService#RECHERCHE_PARENT_NULL}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("findAllByParent(null) : positionne message + lève IllegalStateException")
	public void testFindAllByParentNull() {

		assertThatThrownBy(() -> this.service.findAllByParent(null))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.RECHERCHE_PARENT_NULL);

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.RECHERCHE_PARENT_NULL);

	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>findAllByParent(parent blank) : parent non exploitable.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne {@link SousTypeProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("findAllByParent(parent blank) : positionne MESSAGE_CREER_PARENT_NON_PERSISTANT_KO + lève IllegalStateException")
	public void testFindAllByParentParentBlank() {

		final TypeProduitDTO.InputDTO parentDto = new TypeProduitDTO.InputDTO(ESPACES);

		assertThatThrownBy(() -> this.service.findAllByParent(parentDto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>findAllByParent(parent absent) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne {@link SousTypeProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("findAllByParent(parent absent) : positionne MESSAGE_CREER_PARENT_NON_PERSISTANT_KO + lève IllegalStateException")
	public void testFindAllByParentPasParent() {

		final TypeProduitDTO.InputDTO parentDto = new TypeProduitDTO.InputDTO(LOISIR);

		/* Parent non créé. */
		assertThatThrownBy(() -> this.service.findAllByParent(parentDto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);
		
	} // __________________________________________________________________
	
	

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

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(LOISIR));

		final List<OutputDTO> dtos = this.service.findAllByParent(new TypeProduitDTO.InputDTO(LOISIR));

		assertThat(dtos).isNotNull().isEmpty();
		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>findAllByParent(ok) : test béton avec preuve stockage
	 * et rattachement exclusif au parent demandé.</p>
	 * <ul>
	 * <li>retourne une liste non {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>ne retourne que les enfants du parent demandé</li>
	 * <li>prouve physiquement dans le stockage les couples parent / sous-type créés</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findAllByParent(ok) : retourne uniquement les enfants du parent demandé + message exact + preuve stockage")
	public void testFindAllByParentOkAvecPreuveStockage() throws Exception {

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(LOISIR));

		final OutputDTO creeA1 = this.service.creer(
				new SousTypeProduitDTO.InputDTO(OUTIL, RABOTEUSE));
		final OutputDTO creeA2 = this.service.creer(
				new SousTypeProduitDTO.InputDTO(OUTIL, COUTEAU));
		final OutputDTO creeB1 = this.service.creer(
				new SousTypeProduitDTO.InputDTO(LOISIR, CISEAU));

		final List<OutputDTO> dtos = this.service.findAllByParent(
				new TypeProduitDTO.InputDTO(OUTIL));

		assertThat(dtos).isNotNull();
		assertThat(dtos).hasSize(2);

		assertThat(dtos)
				.extracting(OutputDTO::getSousTypeProduit)
				.containsExactly(RABOTEUSE, COUTEAU);

		assertThat(dtos)
				.extracting(OutputDTO::getTypeProduit)
				.containsOnly(OUTIL);

		assertThat(dtos)
				.extracting(OutputDTO::getIdSousTypeProduit)
				.containsExactly(
						creeA1.getIdSousTypeProduit(),
						creeA2.getIdSousTypeProduit());

		assertThat(dtos)
				.extracting(OutputDTO::getIdSousTypeProduit)
				.doesNotContain(creeB1.getIdSousTypeProduit());

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		assertThat(this.compterSousTypeProduitDansStockage(creeA1.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(creeA1.getIdSousTypeProduit()))
				.isEqualTo(RABOTEUSE);
		assertThat(this.lireParentSousTypeProduitDansStockage(creeA1.getIdSousTypeProduit()))
				.isEqualTo(OUTIL);

		assertThat(this.compterSousTypeProduitDansStockage(creeA2.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(creeA2.getIdSousTypeProduit()))
				.isEqualTo(COUTEAU);
		assertThat(this.lireParentSousTypeProduitDansStockage(creeA2.getIdSousTypeProduit()))
				.isEqualTo(OUTIL);

		assertThat(this.compterSousTypeProduitDansStockage(creeB1.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(creeB1.getIdSousTypeProduit()))
				.isEqualTo(CISEAU);
		assertThat(this.lireParentSousTypeProduitDansStockage(creeB1.getIdSousTypeProduit()))
				.isEqualTo(LOISIR);

		assertThat(this.compterSousTypeProduitParCoupleDansStockage(OUTIL, RABOTEUSE))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(OUTIL, COUTEAU))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(LOISIR, CISEAU))
				.isEqualTo(1L);
		
	} // __________________________________________________________________	

	
	
	// ========================== findByDTO ===============================
	
	
	
	/**
	 * <div>
	 * <p>findByDTO(null) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>Retourne {@code null}.</li>
	 * <li>Positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OBJ_NULL}.</li>
	 * <li>N'écrit rien dans le stockage.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(null) : retourne null + message exact MESSAGE_RECHERCHE_OBJ_NULL + aucune écriture stockage")
	public void testFindByDTONull() throws Exception {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		final OutputDTO dto = this.service.findByDTO(null);

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OBJ_NULL);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findByDTO(parent blank) : violation de contrat.</p>
	 * <ul>
	 * <li>Lève {@link IllegalStateException}.</li>
	 * <li>Positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO}.</li>
	 * <li>N'écrit rien dans le stockage.</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("findByDTO(parent blank) : IllegalStateException + message exact MESSAGE_CREER_PARENT_NON_PERSISTANT_KO + aucune écriture stockage")
	public void testFindByDTOParentBlank() {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				ESPACES,
				MARTEAU);

		assertThatThrownBy(() -> this.service.findByDTO(dto))
				.isInstanceOf(IllegalStateException.class);

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findByDTO(parent absent) : aucun parent persistant en stockage.</p>
	 * <ul>
	 * <li>Retourne {@code null}.</li>
	 * <li>Positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}.</li>
	 * <li>N'écrit rien dans le stockage.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(parent absent) : retourne null + message exact MESSAGE_RECHERCHE_VIDE + aucune écriture stockage")
	public void testFindByDTOParentAbsent() throws Exception {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				OUTIL,
				MARTEAU);

		final OutputDTO trouve = this.service.findByDTO(dto);

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(trouve).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findByDTO(couple introuvable) : 
	 * parent existant mais aucun enfant correspondant.</p>
	 * <ul>
	 * <li>Retourne {@code null}.</li>
	 * <li>Positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}.</li>
	 * <li>Prouve dans le stockage que le couple demandé est absent.</li>
	 * <li>Prouve dans le stockage que l'objet voisin déjà créé reste présent.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(couple introuvable) : retourne null + message exact MESSAGE_RECHERCHE_VIDE + preuve stockage")
	public void testFindByDTOCoupleIntrouvableAvecPreuveStockage() throws Exception {

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));

		final OutputDTO cree = this.service.creer(
				new SousTypeProduitDTO.InputDTO(
						OUTIL,
						MARTEAU));

		assertThat(cree).isNotNull();
		assertThat(cree.getIdSousTypeProduit()).isNotNull();

		final InputDTO inputRecherche = new SousTypeProduitDTO.InputDTO(
				OUTIL,
				TOURNEVIS);

		final OutputDTO dto = this.service.findByDTO(inputRecherche);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				TOURNEVIS))
				.isEqualTo(0L);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				MARTEAU))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitDansStockage(
				cree.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(
				cree.getIdSousTypeProduit()))
				.isEqualTo(MARTEAU);
		assertThat(this.lireParentSousTypeProduitDansStockage(
				cree.getIdSousTypeProduit()))
				.isEqualTo(OUTIL);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findByDTO(ok) : 
	 * preuve béton de la recherche sur le couple [parent, libellé].</p>
	 * <ul>
	 * <li>Retourne un {@link OutputDTO} non {@code null}.</li>
	 * <li>Positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}.</li>
	 * <li>Retrouve le bon objet quand le même libellé existe sur plusieurs parents.</li>
	 * <li>Prouve physiquement dans le stockage les deux couples distincts.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(ok) : retrouve le bon couple [parent, libellé] + message exact + preuve stockage")
	public void testFindByDTOOkAvecPreuveCoupleParentLibelle() throws Exception {

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(LOISIR));

		final OutputDTO creeParentA = this.service.creer(
				new SousTypeProduitDTO.InputDTO(
						OUTIL,
						MARTEAU));

		final OutputDTO creeParentB = this.service.creer(
				new SousTypeProduitDTO.InputDTO(
						LOISIR,
						MARTEAU));

		assertThat(creeParentA).isNotNull();
		assertThat(creeParentB).isNotNull();
		assertThat(creeParentA.getIdSousTypeProduit()).isNotNull();
		assertThat(creeParentB.getIdSousTypeProduit()).isNotNull();

		final InputDTO inputRecherche = new SousTypeProduitDTO.InputDTO(
				LOISIR,
				MARTEAU);

		final OutputDTO dto = this.service.findByDTO(inputRecherche);

		assertThat(dto).isNotNull();
		assertThat(dto.getSousTypeProduit()).isEqualTo(MARTEAU);
		assertThat(dto.getTypeProduit()).isEqualTo(LOISIR);
		assertThat(dto.getIdSousTypeProduit())
				.isEqualTo(creeParentB.getIdSousTypeProduit());
		assertThat(dto.getIdSousTypeProduit())
				.isNotEqualTo(creeParentA.getIdSousTypeProduit());
		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_FINDBYDTO_OK);

		assertThat(this.compterSousTypeProduitDansStockage(
				creeParentA.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(
				creeParentA.getIdSousTypeProduit()))
				.isEqualTo(MARTEAU);
		assertThat(this.lireParentSousTypeProduitDansStockage(
				creeParentA.getIdSousTypeProduit()))
				.isEqualTo(OUTIL);

		assertThat(this.compterSousTypeProduitDansStockage(
				creeParentB.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(
				creeParentB.getIdSousTypeProduit()))
				.isEqualTo(MARTEAU);
		assertThat(this.lireParentSousTypeProduitDansStockage(
				creeParentB.getIdSousTypeProduit()))
				.isEqualTo(LOISIR);

		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				MARTEAU))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				LOISIR,
				MARTEAU))
				.isEqualTo(1L);

	} // __________________________________________________________________	

	
	
	// =========================== findById ===============================
	
	
	
	/**
	 * <div>
	 * <p>findById(null) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'écrit rien dans le stockage</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findById(null) : retourne null + message exact MESSAGE_PARAM_NULL + aucune écriture stockage")
	public void testFindByIdNull() throws Exception {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		final OutputDTO dto = this.service.findById(null);

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_NULL);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findById(introuvable) : cas nominal de non-trouvabilité.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + id</li>
	 * <li>prouve physiquement l'absence dans le stockage</li>
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
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE
								+ idInexistant);
		assertThat(this.compterSousTypeProduitDansStockage(idInexistant))
				.isEqualTo(0L);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findById(ok) : test béton de la recherche par identifiant.</p>
	 * <ul>
	 * <li>crée d'abord un SousTypeProduit réel</li>
	 * <li>recherche ensuite cet objet
	 * via son identifiant persistant</li>
	 * <li>retourne un OutputDTO cohérent</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}</li>
	 * <li>prouve physiquement la présence unique dans le stockage</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findById(ok) : OutputDTO cohérent + message exact + preuve stockage")
	public void testFindByIdOkAvecPreuveStockage() throws Exception {

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));

		final OutputDTO cree = this.service.creer(
				new SousTypeProduitDTO.InputDTO(
						OUTIL,
						PERCEUSE));

		assertThat(cree).isNotNull();
		assertThat(cree.getIdSousTypeProduit()).isNotNull();
		assertThat(cree.getSousTypeProduit()).isEqualTo(PERCEUSE);
		assertThat(cree.getTypeProduit()).isEqualTo(OUTIL);

		final Long id = cree.getIdSousTypeProduit();

		final OutputDTO dto = this.service.findById(id);

		assertThat(dto).isNotNull();
		assertThat(dto.getIdSousTypeProduit()).isEqualTo(id);
		assertThat(dto.getSousTypeProduit()).isEqualTo(PERCEUSE);
		assertThat(dto.getTypeProduit()).isEqualTo(OUTIL);
		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_FINDBYID_OK);

		assertThat(this.compterSousTypeProduitDansStockage(id)).isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(id))
				.isEqualTo(PERCEUSE);
		assertThat(this.lireParentSousTypeProduitDansStockage(id))
				.isEqualTo(OUTIL);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				PERCEUSE))
				.isEqualTo(1L);

	} // __________________________________________________________________

	
	
	// ============================ update ================================
	
	
	
	/**
	 * <div>
	 * <p>update(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'écrit rien dans le stockage</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("update(null) : ExceptionParametreNull + message exact MESSAGE_PARAM_NULL + aucune écriture stockage")
	public void testUpdateNull() {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThatThrownBy(() -> this.service.update(null))
				.isInstanceOf(ExceptionParametreNull.class);

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_NULL);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>n'écrit rien dans le stockage</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("update(blank) : ExceptionParametreBlank + message exact MESSAGE_PARAM_BLANK + aucune écriture stockage")
	public void testUpdateBlank() {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		final InputDTO input = new SousTypeProduitDTO.InputDTO(
				OUTIL,
				ESPACES);

		assertThatThrownBy(() -> this.service.update(input))
				.isInstanceOf(ExceptionParametreBlank.class);

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(parent blank) : violation de contrat structurel.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO}</li>
	 * <li>n'écrit rien dans le stockage</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("update(parent blank) : IllegalStateException + message exact MESSAGE_CREER_PARENT_NON_PERSISTANT_KO + aucune écriture stockage")
	public void testUpdateParentBlank() {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		final InputDTO input = new SousTypeProduitDTO.InputDTO(
				ESPACES,
				MARTEAU);

		assertThatThrownBy(() -> this.service.update(input))
				.isInstanceOf(IllegalStateException.class);

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(parent absent) : le parent requis n'existe pas en stockage.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO}</li>
	 * <li>n'écrit rien dans le stockage</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(parent absent) : IllegalStateException + message exact MESSAGE_CREER_PARENT_NON_PERSISTANT_KO + aucune écriture stockage")
	public void testUpdateParentAbsent() throws Exception {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		final InputDTO input = new SousTypeProduitDTO.InputDTO(
				OUTIL,
				MARTEAU);

		assertThatThrownBy(() -> this.service.update(input))
				.isInstanceOf(IllegalStateException.class);

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(introuvable) : aucun objet persistant
	 * ne correspond au couple [parent, libellé].</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + libellé</li>
	 * <li>ne crée aucun doublon</li>
	 * <li>conserve l'objet voisin déjà présent dans le stockage</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(introuvable) : null + message exact MESSAGE_OBJ_INTROUVABLE + libellé + aucune création stockage")
	public void testUpdateIntrouvable() throws Exception {

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));

		final OutputDTO cree = this.service.creer(
				new SousTypeProduitDTO.InputDTO(
						OUTIL,
						MARTEAU));

		assertThat(cree).isNotNull();
		assertThat(cree.getIdSousTypeProduit()).isNotNull();

		final Long nombreAvantUpdate = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		final InputDTO input = new SousTypeProduitDTO.InputDTO(
				OUTIL,
				TOURNEVIS);

		final OutputDTO dto = this.service.update(input);

		final Long nombreApresUpdate = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE
								+ TOURNEVIS);
		assertThat(nombreApresUpdate).isEqualTo(nombreAvantUpdate);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				TOURNEVIS))
				.isEqualTo(0L);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				MARTEAU))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitDansStockage(
				cree.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(
				cree.getIdSousTypeProduit()))
				.isEqualTo(MARTEAU);
		assertThat(this.lireParentSousTypeProduitDansStockage(
				cree.getIdSousTypeProduit()))
				.isEqualTo(OUTIL);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(ok) : preuve béton de la ré-identification
	 * par le couple [parent, libellé].</p>
	 * <ul>
	 * <li>crée d'abord deux parents réels</li>
	 * <li>crée ensuite deux sous-types portant le même libellé
	 * sur deux parents différents</li>
	 * <li>met à jour le couple ciblé
	 * sans créer de doublon</li>
	 * <li>retourne un {@link OutputDTO} cohérent</li>
	 * <li>conserve exactement le même identifiant persistant
	 * pour le couple ciblé</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_MODIF_OK} + libellé</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(ok) : OutputDTO cohérent + ID conservé + message exact + preuve du couple [parent, libellé]")
	public void testUpdateOkAvecPreuveCoupleParentLibelleEtIdConserve()
			throws Exception {

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(LOISIR));

		final OutputDTO creeParentA = this.service.creer(
				new SousTypeProduitDTO.InputDTO(
						OUTIL,
						MARTEAU));

		final OutputDTO creeParentB = this.service.creer(
				new SousTypeProduitDTO.InputDTO(
						LOISIR,
						MARTEAU));

		assertThat(creeParentA).isNotNull();
		assertThat(creeParentB).isNotNull();
		assertThat(creeParentA.getIdSousTypeProduit()).isNotNull();
		assertThat(creeParentB.getIdSousTypeProduit()).isNotNull();

		final Long nombreAvantUpdate = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		final OutputDTO modifie = this.service.update(
				new SousTypeProduitDTO.InputDTO(
						LOISIR,
						MARTEAU));

		final String message = this.service.getMessage();

		final Long nombreApresUpdate = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(modifie).isNotNull();
		assertThat(modifie.getIdSousTypeProduit())
				.isEqualTo(creeParentB.getIdSousTypeProduit());
		assertThat(modifie.getIdSousTypeProduit())
				.isNotEqualTo(creeParentA.getIdSousTypeProduit());
		assertThat(modifie.getSousTypeProduit()).isEqualTo(MARTEAU);
		assertThat(modifie.getTypeProduit()).isEqualTo(LOISIR);
		assertThat(message)
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_MODIF_OK
								+ MARTEAU);

		assertThat(nombreApresUpdate).isEqualTo(nombreAvantUpdate);
		assertThat(this.compterSousTypeProduitDansStockage(
				creeParentA.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitDansStockage(
				creeParentB.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(
				creeParentA.getIdSousTypeProduit()))
				.isEqualTo(MARTEAU);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(
				creeParentB.getIdSousTypeProduit()))
				.isEqualTo(MARTEAU);
		assertThat(this.lireParentSousTypeProduitDansStockage(
				creeParentA.getIdSousTypeProduit()))
				.isEqualTo(OUTIL);
		assertThat(this.lireParentSousTypeProduitDansStockage(
				creeParentB.getIdSousTypeProduit()))
				.isEqualTo(LOISIR);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				MARTEAU))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				LOISIR,
				MARTEAU))
				.isEqualTo(1L);

		final OutputDTO reluParentB = this.service.findById(
				creeParentB.getIdSousTypeProduit());

		assertThat(reluParentB).isNotNull();
		assertThat(reluParentB.getIdSousTypeProduit())
				.isEqualTo(creeParentB.getIdSousTypeProduit());
		assertThat(reluParentB.getSousTypeProduit()).isEqualTo(MARTEAU);
		assertThat(reluParentB.getTypeProduit()).isEqualTo(LOISIR);

	} // __________________________________________________________________

	
	
	// ============================ delete ================================
	
	
	
	/**
	 * <div>
	 * <p>delete(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'écrit rien dans le stockage</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("delete(null) : ExceptionParametreNull + message exact MESSAGE_PARAM_NULL + aucune écriture stockage")
	public void testDeleteNull() {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThatThrownBy(() -> this.service.delete(null))
				.isInstanceOf(ExceptionParametreNull.class);

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_NULL);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>n'écrit rien dans le stockage</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("delete(blank) : ExceptionParametreBlank + message exact MESSAGE_PARAM_BLANK + aucune écriture stockage")
	public void testDeleteBlank() {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		final InputDTO input = new SousTypeProduitDTO.InputDTO(
				OUTIL,
				ESPACES);

		assertThatThrownBy(() -> this.service.delete(input))
				.isInstanceOf(ExceptionParametreBlank.class);

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(parent blank) : violation de contrat structurel.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO}</li>
	 * <li>n'écrit rien dans le stockage</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("delete(parent blank) : IllegalStateException + message exact MESSAGE_CREER_PARENT_NON_PERSISTANT_KO + aucune écriture stockage")
	public void testDeleteParentBlank() {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		final InputDTO input = new SousTypeProduitDTO.InputDTO(
				ESPACES,
				MARTEAU);

		assertThatThrownBy(() -> this.service.delete(input))
				.isInstanceOf(IllegalStateException.class);

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(parent absent) : le parent requis n'existe pas en stockage.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO}</li>
	 * <li>n'écrit rien dans le stockage</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("delete(parent absent) : IllegalStateException + message exact MESSAGE_CREER_PARENT_NON_PERSISTANT_KO + aucune écriture stockage")
	public void testDeleteParentAbsent() throws Exception {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		final InputDTO input = new SousTypeProduitDTO.InputDTO(
				OUTIL,
				MARTEAU);

		assertThatThrownBy(() -> this.service.delete(input))
				.isInstanceOf(IllegalStateException.class);

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(introuvable) : aucun objet persistant
	 * ne correspond au couple [parent, libellé].</p>
	 * <ul>
	 * <li>ne supprime rien</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + libellé</li>
	 * <li>le stockage reste strictement inchangée</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("delete(introuvable) : aucune suppression + message exact MESSAGE_OBJ_INTROUVABLE + libellé")
	public void testDeleteIntrouvable() throws Exception {

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));

		final Long nombreAvantDelete = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		this.service.delete(
				new SousTypeProduitDTO.InputDTO(
						OUTIL,
						LIBELLE_SUPPRESSION_ABSENT));

		final Long nombreApresDelete = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE
								+ LIBELLE_SUPPRESSION_ABSENT);
		assertThat(nombreApresDelete).isEqualTo(nombreAvantDelete);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				LIBELLE_SUPPRESSION_ABSENT))
				.isEqualTo(0L);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(ok) : preuve béton de la destruction
	 * sur le couple [parent, libellé].</p>
	 * <ul>
	 * <li>crée d'abord deux parents réels</li>
	 * <li>crée ensuite deux sous-types portant le même libellé
	 * sur deux parents différents</li>
	 * <li>détruit uniquement le couple ciblé</li>
	 * <li>ne détruit jamais le couple homonyme
	 * rattaché à l'autre parent</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_DELETE_OK} + libellé</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("delete(ok) : détruit le bon couple [parent, libellé] + message exact + preuve stockage")
	public void testDeleteOkAvecPreuveCoupleParentLibelle() throws Exception {

		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(LOISIR));

		final OutputDTO creeParentA = this.service.creer(
				new SousTypeProduitDTO.InputDTO(
						OUTIL,
						CLE_PLATE));

		final OutputDTO creeParentB = this.service.creer(
				new SousTypeProduitDTO.InputDTO(
						LOISIR,
						CLE_PLATE));

		assertThat(creeParentA).isNotNull();
		assertThat(creeParentB).isNotNull();
		assertThat(creeParentA.getIdSousTypeProduit()).isNotNull();
		assertThat(creeParentB.getIdSousTypeProduit()).isNotNull();

		final Long nombreAvantDelete = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		this.service.delete(
				new SousTypeProduitDTO.InputDTO(
						LOISIR,
						CLE_PLATE));

		/*
		 * Synchronise explicitement le contexte de persistance JPA
		 * avant les preuves SQL directes.
		 *
		 * Avec @DataJpaTest, le test s'exécute dans une transaction Spring.
		 * La suppression réalisée par le Gateway via le DAO JPA peut rester
		 * en attente dans l'EntityManager jusqu'au flush.
		 *
		 * JdbcTemplate ne lit pas à travers l'EntityManager : il interroge
		 * directement le stockage. Sans flush explicite, la preuve JDBC
		 * peut donc relire l'état antérieur à la suppression et compter
		 * encore la ligne supprimée.
		 *
		 * Le flush ne modifie pas le scénario métier testé : il rend seulement
		 * observable dans le stockage la suppression déjà demandée par
		 * service.delete(...), avant les assertions de preuve physique.
		 */
		this.entityManager.flush();

		final Long nombreApresDelete = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_DELETE_OK
								+ CLE_PLATE);

		assertThat(nombreApresDelete).isEqualTo(nombreAvantDelete - 1L);

		assertThat(this.compterSousTypeProduitDansStockage(
				creeParentA.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitDansStockage(
				creeParentB.getIdSousTypeProduit()))
				.isEqualTo(0L);

		assertThat(this.lireLibelleSousTypeProduitDansStockage(
				creeParentA.getIdSousTypeProduit()))
				.isEqualTo(CLE_PLATE);
		assertThat(this.lireParentSousTypeProduitDansStockage(
				creeParentA.getIdSousTypeProduit()))
				.isEqualTo(OUTIL);

		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				CLE_PLATE))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				LOISIR,
				CLE_PLATE))
				.isEqualTo(0L);

		final List<OutputDTO> reluParentA = this.service.findByLibelle(CLE_PLATE);

		assertThat(reluParentA).isNotNull();
		assertThat(reluParentA).hasSize(1);
		assertThat(reluParentA.get(0).getIdSousTypeProduit())
				.isEqualTo(creeParentA.getIdSousTypeProduit());
		assertThat(reluParentA.get(0).getTypeProduit())
				.isEqualTo(OUTIL);

	} // __________________________________________________________________	

	
	
	// ============================ count =================================
	
	
	
	/**
	 * <div>
	 * <p>count() : retourne le comptage réel de le stockage
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
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		final long retourUc = this.service.count();

		assertThat(retourUc).isEqualTo(nombrePhysique.longValue());

		if (retourUc == 0L) {
			assertThat(this.service.getMessage())
					.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		} else {
			assertThat(this.service.getMessage())
					.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);
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

		final long countAvant = this.service.count();

		if (countAvant == 0L) {
			assertThat(this.service.getMessage())
					.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		} else {
			assertThat(this.service.getMessage())
					.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);
		}

		this.typeProduitService.creer(
				new TypeProduitDTO.InputDTO(OUTIL));

		this.service.creer(
				new SousTypeProduitDTO.InputDTO(
						OUTIL,
						BOITE_A_OUTILS));

		this.service.creer(
				new SousTypeProduitDTO.InputDTO(
						OUTIL,
						ETABLI_PLIANT));

		final long apresCreations = this.service.count();

		assertThat(apresCreations).isEqualTo(countAvant + 2L);
		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		this.service.delete(
				new SousTypeProduitDTO.InputDTO(
						OUTIL,
						BOITE_A_OUTILS));

		this.service.delete(
				new SousTypeProduitDTO.InputDTO(
						OUTIL,
						ETABLI_PLIANT));

		final long apresNettoyage = this.service.count();

		assertThat(apresNettoyage).isEqualTo(countAvant);

		if (apresNettoyage == 0L) {
			assertThat(this.service.getMessage())
					.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		} else {
			assertThat(this.service.getMessage())
					.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);
		}

		final Long nombrePhysiqueFinal = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(apresNettoyage).isEqualTo(nombrePhysiqueFinal.longValue());

	} // __________________________________________________________________

	
	
	// ========================== getMessage ==============================
	
	
	
	/**
	 * <div>
	 * <p>getMessage(initial) : état initial du service intégré.</p>
	 * <ul>
	 * <li>retourne {@code null} avant toute opération</li>
	 * <li>ce comportement est acceptable</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("getMessage(initial) : retourne null avant toute opération")
	public void testGetMessageInitialNull() throws Exception {

		final String message = this.service.getMessage();

		assertThat(message).isNull();

	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>getMessage(après succès réel) :
	 * retourne le message courant
	 * positionné par le comptage réel de le stockage.</p>
	 * <ul>
	 * <li>si le comptage réel vaut 0,
	 * le message est
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>sinon,
	 * le message est
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("getMessage(après succès réel) : retourne le message observable exact du count()")
	public void testGetMessageApresSuccesReel() throws Exception {

		final long retour = this.service.count();
		final String message = this.service.getMessage();

		if (retour == 0L) {
			assertThat(message)
					.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		} else {
			assertThat(message)
					.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);
		}

	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>getMessage(après erreur locale) :
	 * retourne le message courant
	 * positionné par une erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>après {@code creer(null)},
	 * retourne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_NULL_KO}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("getMessage(après erreur locale) : retourne MESSAGE_CREER_NULL_KO")
	public void testGetMessageApresErreurLocale() throws Exception {

		this.service.creer(null);
		final String message = this.service.getMessage();

		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_NULL_KO);

	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>getMessage(dernier message gagne) :
	 * une opération réelle plus récente
	 * écrase bien le message précédent.</p>
	 * <ul>
	 * <li>après une erreur locale,
	 * le message vaut d'abord
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_NULL_KO}</li>
	 * <li>après un {@code count()} réel,
	 * le message courant devient
	 * le message observable du comptage réel</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("getMessage(dernier message gagne) : le message réel le plus récent écrase le précédent")
	public void testGetMessageDernierMessageGagne() throws Exception {

		this.service.creer(null);
		final String messageErreur = this.service.getMessage();

		final long retour = this.service.count();
		final String messageFinal = this.service.getMessage();

		assertThat(messageErreur)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_NULL_KO);

		if (retour == 0L) {
			assertThat(messageFinal)
					.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		} else {
			assertThat(messageFinal)
					.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);
		}

	} // __________________________________________________________________
	
	
	
	// *************************** METHODES UTILITAIRES ********************/

	
	/**
	 * <div>
	 * <p>Compte le nombre de lignes portant l'identifiant transmis
	 * dans SOUS_TYPES_PRODUIT.</p>
	 * </div>
	 *
	 * @param pId : Long : identifiant physique du SousTypeProduit.
	 * @return Long : nombre de lignes trouvées.
	 */
	private Long compterSousTypeProduitDansStockage(final Long pId) {

		return this.jdbcTemplate.queryForObject(
				"SELECT COUNT(*) "
				+ "FROM SOUS_TYPES_PRODUIT "
				+ "WHERE ID_SOUS_TYPE_PRODUIT = ?",
				Long.class,
				pId);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>Lit le libellé stocké dans le stockage pour l'identifiant transmis.</p>
	 * </div>
	 *
	 * @param pId : Long : identifiant physique du SousTypeProduit.
	 * @return String : libellé SOUS_TYPE_PRODUIT lu dans le stockage.
	 */
	private String lireLibelleSousTypeProduitDansStockage(final Long pId) {

		return this.jdbcTemplate.queryForObject(
				"SELECT SOUS_TYPE_PRODUIT "
				+ "FROM SOUS_TYPES_PRODUIT "
				+ "WHERE ID_SOUS_TYPE_PRODUIT = ?",
				String.class,
				pId);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>Lit le libellé du TypeProduit parent stocké dans le stockage
	 * pour l'identifiant transmis.</p>
	 * </div>
	 *
	 * @param pId : Long : identifiant physique du SousTypeProduit.
	 * @return String : libellé du parent lu dans le stockage.
	 */
	private String lireParentSousTypeProduitDansStockage(final Long pId) {

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
	private Long compterSousTypeProduitParCoupleDansStockage(
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
	
	
	
	/**
	 * <div>
	 * <p>Compte le nombre de lignes physiques pour un libellé exact
	 * de SousTypeProduit, tous parents confondus.</p>
	 * </div>
	 *
	 * @param pSousType : String : libellé exact du SousTypeProduit.
	 * @return Long : nombre de lignes trouvées pour ce libellé.
	 */
	private Long compterSousTypeProduitParLibelleDansStockage(
			final String pSousType) {

		return this.jdbcTemplate.queryForObject(
				"SELECT COUNT(*) "
				+ "FROM SOUS_TYPES_PRODUIT "
				+ "WHERE SOUS_TYPE_PRODUIT = ?",
				Long.class,
				pSousType);

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
