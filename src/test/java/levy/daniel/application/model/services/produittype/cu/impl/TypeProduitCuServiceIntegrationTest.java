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
import org.springframework.context.annotation.Import;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import jakarta.persistence.EntityManager;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.OutputDTO;
import levy.daniel.application.model.services.produittype.cu.TypeProduitICuService;
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
 * CLASSE TypeProduitCuServiceIntegrationTest.java :
 * </p>
 * <p>
 * Tests d'intégration complets (avec tests "béton") du SERVICE ADAPTER METIER UC
 * {@link TypeProduitCuService}.
 * </p>
 *
 * <p>Ce test vérifie le SERVICE UC avec un vrai stockage JPA/H2.</p>
 * <ul>
 * <li>Il injecte le PORT UC {@link TypeProduitICuService}.</li>
 * <li>Il importe explicitement le SERVICE UC testé
 * {@link TypeProduitCuService}.</li>
 * <li>Il importe explicitement le Gateway JPA nécessaire
 * {@link TypeProduitGatewayJPAService}.</li>
 * <li>Il utilise un stockage H2 en mémoire via {@link DataJpaTest}.</li>
 * <li>Il initialise le stockage avec
 * <code>truncate-test.sql</code> puis <code>data-test.sql</code>.</li>
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
 * <li>le SERVICE UC et le Gateway nécessaires au test sont ajoutés
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
 * {@link AutoConfigurationPackage}, le package du DAO
 * {@link TypeProduitDaoJPA} et le package de l'entity
 * {@link TypeProduitJPA} ;</li>
 * <li>elle ne déclare ni {@code @SpringBootTest},
 * ni {@code @ComponentScan}, ni {@code @EnableJpaRepositories},
 * ni {@code @EntityScan} ;</li>
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
 * @since 21 janvier 2026
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
		TypeProduitCuService.class,
		TypeProduitGatewayJPAService.class
})
@ContextConfiguration(classes = TypeProduitCuServiceIntegrationTest.ConfigTest.class)
/*
 * @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
 * Recrée le contexte Spring après chaque méthode de test.
 *
 * @Sql réinitialise le stockage avant chaque test, mais ne réinitialise pas
 * l'état local du bean TypeProduitCuService injecté par Spring. Or ce SERVICE UC
 * mémorise le dernier message utilisateur retourné par getMessage().
 *
 * L'annotation est donc placée au niveau de la classe, après la déclaration
 * du contexte autonome chargé par @ContextConfiguration : elle ne participe pas
 * à la découverte des repositories et ne masque aucun conflit Spring, mais
 * force uniquement un nouveau contexte de test après chaque méthode.
 *
 * Elle garantit ainsi qu'un test comme testGetMessageInitialNull() reçoit
 * toujours un SERVICE UC neuf, avec un message initial null, que le test soit
 * lancé seul, après un autre test, ou dans la suite complète.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Tag(TypeProduitCuServiceIntegrationTest.TAG)
public class TypeProduitCuServiceIntegrationTest {

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
	 * "Outil"
	 */
	public static final String OUTIL = "Outil";

	/**
	 * "Vêtement"
	 */
	public static final String VETEMENT = "Vêtement";
	
	/**
	 * "Eléctronique"
	 */
	public static final String NON_SEEDE = "Eléctronique";

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

	/**
	 * "cu-it-Creer"
	 */
	public static final String TAG_CREER = "cu-it-Creer";
	
	/**
	 * "cu-it-RechercherTous"
	 */
	public static final String TAG_RECHERCHERTOUS ="cu-it-RechercherTous"; 
	
	/**
	 * "creer(null) : retourne null, message utilisateur, aucune exception, stockage inchangé"
	 */
	public static final String DN_CREER_NULL
		= "creer(null) : retourne null, message utilisateur, aucune exception, stockage inchangé";
	
	/**
	 * "creer(blank) : ExceptionParametreBlank + message exact + stockage inchangé"
	 */
	public static final String DN_CREER_BLANK
		= "creer(blank) : ExceptionParametreBlank + message exact + stockage inchangé";
	
	/**
	 * "creer(doublon) : ExceptionDoublon + message exact + preuve stockage d'unicité"
	 */
	public static final String DN_CREER_DOUBLON
		= "creer(doublon) : ExceptionDoublon + message exact + preuve stockage d'unicité";
	
	/**
	 * "creer(ok) : preuve stockage + message exact + round-trip findByLibelle/findById"
	 */
	public static final String DN_CREER_OK
		= "creer(ok) : preuve stockage + message exact + round-trip findByLibelle/findById";
	/**
	 * "SELECT COUNT(*) FROM TYPES_PRODUIT"
	 */
	public static final String SELECT_COUNT_FROM_TYPES_PRODUIT 
		= "SELECT COUNT(*) FROM TYPES_PRODUIT";

	// *************************** ATTRIBUTS *******************************/

	/**
	 * SERVICE CU sous test (PORT).
	 */
	@Autowired
	private TypeProduitICuService service;
	
	/**
	 * JdbcTemplate (Spring) pour lire la base directement
	 * et prouver physiquement les écritures du CU.
	 */
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
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

    
    
    // ************************ CONSTRUCTEUR *****************************/
    
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public TypeProduitCuServiceIntegrationTest() {
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
	 * l'auto-configuration Spring Boot les packages nécessaires au test :
	 * le package du DAO {@link TypeProduitDaoJPA} et le package de l'entity
	 * {@link TypeProduitJPA} ;</li>
	 * <li>{@link Import} ajoute explicitement au contexte le SERVICE UC testé
	 * {@link TypeProduitCuService} et son Gateway JPA
	 * {@link TypeProduitGatewayJPAService} ;</li>
	 * <li>le test ne déclare pas {@code @EnableJpaRepositories} :
	 * les repositories restent pris en charge par le slice
	 * {@link DataJpaTest} ;</li>
	 * <li>le test ne déclare pas {@code @EntityScan} :
	 * le package de l'entity JPA est fourni par
	 * {@link AutoConfigurationPackage} avec {@link TypeProduitJPA}.</li>
	 * </ul>
	 *
	 * <p style="font-weight:bold;">Ce que cette configuration ne fait pas :</p>
	 * <ul>
	 * <li>elle ne déclare pas {@code @SpringBootTest} ;</li>
	 * <li>elle ne déclare pas {@code @ComponentScan} ;</li>
	 * <li>elle ne déclare pas {@code @EnableJpaRepositories} ;</li>
	 * <li>elle ne déclare pas {@code @EntityScan} ;</li>
	 * <li>elle ne force aucun scan manuel des repositories ;</li>
	 * <li>elle ne charge aucun Controller ;</li>
	 * <li>elle n'active pas
	 * {@code spring.main.allow-bean-definition-overriding=true}.</li>
	 * </ul>
	 *
	 * <p>
	 * Le SERVICE UC testé, le Gateway JPA, le repository et l'entity JPA
	 * utiles sont découverts ou importés dans le périmètre explicite déclaré
	 * pour ce test. Le test reste donc un test d'intégration SERVICE UC,
	 * autonome, et limité au stockage JPA nécessaire.
	 * </p>
	 * </div>
	 *
	 * @author Daniel Lévy
	 * @version 1.0
	 * @since 21 janvier 2026
	 */
	@SpringBootConfiguration(proxyBeanMethods = false)
	@AutoConfigurationPackage(basePackageClasses = {
			TypeProduitDaoJPA.class,
			TypeProduitJPA.class
	})
	public static final class ConfigTest { // NOPMD by danyl on 21/01/2026 13:00

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
	 * {@link TypeProduitICuService#MESSAGE_CREER_NULL_KO}</li>
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
		 * compte d'abord (en SQL)
		 * le nombre d'enregistrements dans le stockage
		 * avant l'appel au SERVICE UC,
		 * afin de pouvoir prouver ensuite
		 * qu'aucune écriture réelle n'a eu lieu dans le stockage.
		 */
		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvant).isNotNull();
		
		/* ACT :
		 * appelle service.creer(null) :
		 * - le SERVICE UC retourne null ;
		 * - positionne le message utilisateur MESSAGE_CREER_NULL_KO
		 * (message contractuel) ;
		 * - ne jette aucune exception.
		 */
		final OutputDTO dto = this.service.creer(null);

		/* ASSERT :
		 * garantit que service.creer(null) retourne null.
		 */
		assertThat(dto).isNull();
		
		/* Garantit que service.creer(null) émet un message 
		 * MESSAGE_CREER_NULL_KO. */
		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_CREER_NULL_KO);

		/* ASSERT :
		 * compte ensuite (en SQL)
		 * le nombre d'enregistrements dans le stockage
		 * après service.creer(null)
		 * afin de prouver que l'appel au SERVICE UC
		 * n'a produit aucune écriture dans le stockage.
		 */
		final Long countApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		assertThat(countApres).isNotNull();
		assertThat(countApres).isEqualTo(countAvant);
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>garantit que creer(...) avec un libellé blank :</p>
	 * <ul>
	 * <li>jette une {@link ExceptionParametreBlank}</li>
	 * <li>émet un message
	 * {@link TypeProduitICuService#MESSAGE_CREER_LIBELLE_BLANK_KO}</li>
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
		 * compte d'abord (en SQL)
		 * le nombre d'enregistrements dans le stockage
		 * avant l'appel au SERVICE UC,
		 * afin de pouvoir prouver ensuite
		 * qu'aucune écriture réelle n'a eu lieu dans le stockage.
		 */
		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvant).isNotNull();

		/* prépare un InputDTO
		 * dont le libellé métier est blank. */
		final InputDTO input = new TypeProduitDTO.InputDTO(ESPACES);
		
		/* ACT - ASSERT :
		 * Garantit que this.service.creer(libellé blank)
		 * - jette une ExceptionParametreBlank
		 * - avec un message MESSAGE_CREER_LIBELLE_BLANK_KO.
		 */
		assertThatThrownBy(() -> this.service.creer(input))
				.isInstanceOf(ExceptionParametreBlank.class)
				.hasMessage(TypeProduitICuService.MESSAGE_CREER_LIBELLE_BLANK_KO);
		
		/* Garantit le message utilisateur MESSAGE_CREER_LIBELLE_BLANK_KO
		 * (message contractuel attendu).
		 */
		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_CREER_LIBELLE_BLANK_KO);

		/* ASSERT :
		 * compte ensuite (en SQL)
		 * le nombre d'enregistrements dans le stockage
		 * après l'échec contractuel
		 * afin de prouver que l'appel au SERVICE UC
		 * n'a produit aucune écriture dans le stockage.
		 */
		final Long countApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		assertThat(countApres).isNotNull();
		assertThat(countApres).isEqualTo(countAvant);
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>garantit que si l'appelant tente creer(...)
	 * avec un libellé déjà présent dans le stockage :</p>
	 * <ul>
	 * <li>la première création réussit réellement ;</li>
	 * <li>la seconde création lève une {@link ExceptionDoublon} ;</li>
	 * <li>le message utilisateur exact est
	 * {@link TypeProduitICuService#MESSAGE_CREER_DOUBLON_KO} + libellé ;</li>
	 * <li>aucune nouvelle ligne n'est créée dans le stockage
	 * lors de la tentative de doublon ;</li>
	 * <li>l'unique ligne créée portant déjà ce libellé
	 * reste inchangée.</li>
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
		 * prépare un DTO valide non seedé.
		 *
		 * Le premier appel à creer(...) créera réellement l'objet métier.
		 * Le second appel avec le même DTO déclenchera ensuite
		 * le cas contractuel de doublon.
		 */
		final InputDTO input = new TypeProduitDTO.InputDTO(NON_SEEDE);

		/* Vérifie d'abord que le libellé du test
		 * n'est pas déjà présent dans le stockage.
		 */
		assertThat(this.compterTypeProduitParLibelleEnBase(NON_SEEDE))
				.isEqualTo(0L);

		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
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
		assertThat(cree.getIdTypeProduit()).isNotNull();
		assertThat(cree.getTypeProduit()).isEqualTo(NON_SEEDE);

		/* Garantit physiquement dans le stockage
		 * qu'une seule ligne porte le libellé créé.
		 */
		assertThat(this.compterTypeProduitParLibelleEnBase(NON_SEEDE))
				.isEqualTo(1L);

		/* Garantit physiquement dans le stockage
		 * que l'identifiant retourné correspond à une ligne réelle.
		 */
		assertThat(this.compterTypeProduitEnBase(cree.getIdTypeProduit()))
				.isEqualTo(1L);

		final Long countApresPremiereCreation = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		assertThat(countApresPremiereCreation).isNotNull();
		assertThat(countApresPremiereCreation).isEqualTo(countAvant + 1L);

		/* ACT - ASSERT :
		 * sollicite une deuxième fois la méthode creer(...)
		 * avec le même libellé déjà présent.
		 *
		 * Le SERVICE UC doit refuser le doublon avant toute nouvelle
		 * écriture dans le stockage.
		 */
		assertThatThrownBy(() -> this.service.creer(input))
				.isInstanceOf(ExceptionDoublon.class)
				.hasMessage(TypeProduitICuService.MESSAGE_CREER_DOUBLON_KO + NON_SEEDE);

		/* Garantit le message utilisateur exact. */
		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_CREER_DOUBLON_KO + NON_SEEDE);

		/* ASSERT :
		 * contrôle ensuite par SQL direct
		 * que le stockage contient toujours une seule ligne
		 * pour ce libellé.
		 */
		assertThat(this.compterTypeProduitParLibelleEnBase(NON_SEEDE))
				.isEqualTo(1L);

		assertThat(this.compterTypeProduitEnBase(cree.getIdTypeProduit()))
				.isEqualTo(1L);

		/* Garantit enfin que le volume total du stockage
		 * n'a pas augmenté lors de la tentative de doublon.
		 */
		final Long countApresDoublon = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		assertThat(countApresDoublon).isNotNull();
		assertThat(countApresDoublon).isEqualTo(countApresPremiereCreation);
		
	} // __________________________________________________________________		
	
	

	/**
	 * <div>
	 * <p>garantit que creer(OK) :</p>
	 * <ul>
	 * <li>crée réellement une ligne dans le stockage ;</li>
	 * <li>retourne un {@link OutputDTO} persistant ;</li>
	 * <li>émet un message
	 * {@link TypeProduitICuService#MESSAGE_CREER_OK}</li>
	 * <li>rend la donnée retrouvable dans le stockage via le SERVICE UC ;</li>
	 * <li>ne supprime ni n'altère les données seedées.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DN_CREER_OK)
	@Test
	public void testCreerNominalAvecPreuveStockageEtRoundTrip() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide à créer
		 * et mémorise le nombre de lignes avant création.
		 */
		final InputDTO input = new TypeProduitDTO.InputDTO(OUTIL);

		/* Vérifie d'abord que le libellé du test
		 * n'est pas déjà présent dans le stockage.
		 */
		assertThat(this.compterTypeProduitParLibelleEnBase(OUTIL))
				.isEqualTo(0L);

		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
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
		assertThat(cree.getIdTypeProduit()).isNotNull();
		assertThat(cree.getTypeProduit()).isEqualTo(OUTIL);

		/* Garantit que le message de succès de création
		 * est positionné avant tout autre appel au SERVICE UC.
		 */
		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_CREER_OK);

		/* Garantit que la création augmente bien le nombre total
		 * de lignes dans le stockage réel.
		 */
		final Long countApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		assertThat(countApres).isNotNull();
		assertThat(countApres).isEqualTo(countAvant + 1L);

		/* Garantit physiquement dans le stockage
		 * qu'une seule ligne porte bien l'identifiant créé.
		 */
		assertThat(this.compterTypeProduitEnBase(cree.getIdTypeProduit()))
				.isEqualTo(1L);

		/* Garantit physiquement dans le stockage
		 * que la colonne TYPE_PRODUIT a bien été écrite
		 * avec le libellé métier attendu.
		 */
		assertThat(this.lireLibelleTypeProduitEnBase(cree.getIdTypeProduit()))
				.isEqualTo(OUTIL);

		/* Garantit physiquement dans le stockage
		 * qu'une seule ligne porte le libellé créé.
		 */
		assertThat(this.compterTypeProduitParLibelleEnBase(OUTIL))
				.isEqualTo(1L);

		/* Garantit que l'objet nouvellement créé
		 * est bien retrouvable par libellé via le SERVICE UC.
		 */
		final OutputDTO trouveParLibelle = this.service.findByLibelle(OUTIL);

		assertThat(trouveParLibelle).isNotNull();
		assertThat(trouveParLibelle.getIdTypeProduit())
				.isEqualTo(cree.getIdTypeProduit());
		assertThat(trouveParLibelle.getTypeProduit())
				.isEqualTo(OUTIL);

		/* Garantit que l'objet nouvellement créé
		 * est bien retrouvable par identifiant via le SERVICE UC.
		 */
		final OutputDTO trouveParId 
			= this.service.findById(cree.getIdTypeProduit());

		assertThat(trouveParId).isNotNull();
		assertThat(trouveParId.getIdTypeProduit())
				.isEqualTo(cree.getIdTypeProduit());
		assertThat(trouveParId.getTypeProduit())
				.isEqualTo(OUTIL);
		
	} // __________________________________________________________________
    
    
    
    // ======================== RechercherTous ============================
	
	
	
	/**
	 * <div>
	 * <p>rechercherTous() : doit retourner une liste non nulle contenant les créations du test.</p>
	 * </div>
	 *
	 * @throws Exception
	 */	
	@Tag(TAG_RECHERCHERTOUS)
	@DisplayName("rechercherTous() : retourne une liste non nulle contenant les créations du test")
	@Test
	public void testRechercherTous() throws Exception {

		this.service.creer(new TypeProduitDTO.InputDTO(IT_GAMMA));
		this.service.creer(new TypeProduitDTO.InputDTO(IT_DELTA));

		final List<OutputDTO> dtos = this.service.rechercherTous();

		assertThat(dtos).isNotNull();
		assertThat(dtos)
				.extracting(TypeProduitDTO.OutputDTO::getTypeProduit)
				.contains(IT_GAMMA, IT_DELTA);
		
	}// __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>rechercherTous() : scénario nominal béton avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste non {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>reste cohérent avec {@link TypeProduitICuService#count()}</li>
	 * <li>contient les créations du test</li>
	 * <li>permet de relier les DTO retournés à des lignes réellement présentes
	 * en base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("rechercherTous(ok) : message exact + cohérence count + présence des créations + preuve BD")
	public void testRechercherTousOkAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final OutputDTO creeGamma = this.service.creer(
				new TypeProduitDTO.InputDTO(IT_GAMMA));
		final OutputDTO creeDelta = this.service.creer(
				new TypeProduitDTO.InputDTO(IT_DELTA));

		final long attendu = this.service.count();

		/* ======================= ACT ======================= */
		final List<TypeProduitDTO.OutputDTO> dtos = this.service.rechercherTous();

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNotNull();
		assertThat(dtos.size()).isEqualTo((int) attendu);

		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		assertThat(dtos)
				.extracting(TypeProduitDTO.OutputDTO::getTypeProduit)
				.contains(IT_GAMMA, IT_DELTA);

		final OutputDTO dtoGamma = dtos.stream()
				.filter(dto -> IT_GAMMA.equals(dto.getTypeProduit()))
				.findFirst()
				.orElse(null);

		final OutputDTO dtoDelta = dtos.stream()
				.filter(dto -> IT_DELTA.equals(dto.getTypeProduit()))
				.findFirst()
				.orElse(null);

		assertThat(dtoGamma).isNotNull();
		assertThat(dtoGamma.getIdTypeProduit())
				.isEqualTo(creeGamma.getIdTypeProduit());

		assertThat(dtoDelta).isNotNull();
		assertThat(dtoDelta.getIdTypeProduit())
				.isEqualTo(creeDelta.getIdTypeProduit());

		/* preuve BD : les lignes existent physiquement et portent le bon libellé. */
		assertThat(this.compterTypeProduitEnBase(creeGamma.getIdTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(creeGamma.getIdTypeProduit()))
				.isEqualTo(IT_GAMMA);

		assertThat(this.compterTypeProduitEnBase(creeDelta.getIdTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(creeDelta.getIdTypeProduit()))
				.isEqualTo(IT_DELTA);

		assertThat(this.compterTypeProduitParLibelleEnBase(IT_GAMMA))
				.isEqualTo(1L);
		assertThat(this.compterTypeProduitParLibelleEnBase(IT_DELTA))
				.isEqualTo(1L);
		
	}// __________________________________________________________________

	

	/**
	 * <div>
	 * <p>rechercherTous() : stockage vide.</p>
	 * <ul>
	 * <li>retourne une liste vide mais non {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>reste cohérent avec une base physiquement vide</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Sql(
			scripts = "classpath:/truncate-test.sql",
			executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@DisplayName("rechercherTous(vide) : liste vide + message MESSAGE_RECHERCHE_VIDE + base vide")
	public void testRechercherTousVide() throws Exception {

		/* ===================== ARRANGE ===================== */
		assertThat(this.service.count()).isEqualTo(0L);

		/* ======================= ACT ======================= */
		final List<TypeProduitDTO.OutputDTO> dtos = this.service.rechercherTous();

		/* ===================== ASSERT ====================== */
		assertThat(dtos).isNotNull();
		assertThat(dtos).isEmpty();

		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		
	}// __________________________________________________________________

	
	
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

		this.service.creer(new TypeProduitDTO.InputDTO(IT_EPSILON));
		this.service.creer(new TypeProduitDTO.InputDTO(IT_ZETA));

		final List<String> libelles = this.service.rechercherTousString();

		assertThat(libelles).isNotNull();
		assertThat(libelles).contains(IT_EPSILON, IT_ZETA);
		
	}// __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>rechercherTousString() : scénario nominal béton avec preuve BD.</p>
	 * <ul>
	 * <li>retourne une liste non {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>contient les libellés créés</li>
	 * <li>n'expose aucun doublon</li>
	 * <li>reste cohérent avec la présence physique en base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("rechercherTousString(ok) : message exact + contient les créations + sans doublon + preuve BD")
	public void testRechercherTousStringOkAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final OutputDTO creeEpsilon = this.service.creer(
				new TypeProduitDTO.InputDTO(IT_EPSILON));
		final OutputDTO creeZeta = this.service.creer(
				new TypeProduitDTO.InputDTO(IT_ZETA));

		assertThat(creeEpsilon).isNotNull();
		assertThat(creeZeta).isNotNull();

		/* ======================= ACT ======================= */
		final List<String> libelles = this.service.rechercherTousString();

		/* ===================== ASSERT ====================== */
		assertThat(libelles).isNotNull();
		assertThat(libelles).contains(IT_EPSILON, IT_ZETA);
		assertThat(libelles).doesNotHaveDuplicates();
		assertThat(libelles).allMatch(libelle -> libelle != null && !libelle.isBlank());

		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* preuve BD : les lignes créées existent physiquement. */
		assertThat(this.compterTypeProduitEnBase(creeEpsilon.getIdTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(creeEpsilon.getIdTypeProduit()))
				.isEqualTo(IT_EPSILON);

		assertThat(this.compterTypeProduitEnBase(creeZeta.getIdTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(creeZeta.getIdTypeProduit()))
				.isEqualTo(IT_ZETA);

		assertThat(this.compterTypeProduitParLibelleEnBase(IT_EPSILON))
				.isEqualTo(1L);
		assertThat(this.compterTypeProduitParLibelleEnBase(IT_ZETA))
				.isEqualTo(1L);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousString() : stockage vide.</p>
	 * <ul>
	 * <li>retourne une liste vide mais non {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>reste cohérent avec une base physiquement vide</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Sql(
			scripts = "classpath:/truncate-test.sql",
			executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@DisplayName("rechercherTousString(vide) : liste vide + message MESSAGE_RECHERCHE_VIDE + base vide")
	public void testRechercherTousStringVide() throws Exception {

		/* ===================== ARRANGE ===================== */
		assertThat(this.service.count()).isEqualTo(0L);

		/* ======================= ACT ======================= */
		final List<String> libelles = this.service.rechercherTousString();

		/* ===================== ASSERT ====================== */
		assertThat(libelles).isNotNull();
		assertThat(libelles).isEmpty();

		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________


    
    // ================== rechercherTousParPage ===========================
    
    

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
		
	}// __________________________________________________________________
	
	

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
		
	}// __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousParPage(ok) : test béton avec pagination cohérente
	 * et preuve BD.</p>
	 * <ul>
	 * <li>retourne un {@link ResultatPage} non {@code null}</li>
	 * <li>reprend le numéro de page</li>
	 * <li>reprend la taille de page</li>
	 * <li>reprend le total d'éléments</li>
	 * <li>retourne un contenu DTO cohérent avec les créations du test</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_PAGINEE_OK}</li>
	 * <li>prouve physiquement l'existence en base
	 * des objets créés</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("rechercherTousParPage(ok) : ResultatPage cohérent + message exact + preuve BD")
	public void testRechercherTousParPageOkAvecPreuveBd() throws Exception {

		/* ===================== ARRANGE ===================== */
		final long baseline = this.service.count();

		final OutputDTO cree01 = this.service.creer(
				new TypeProduitDTO.InputDTO(IT_PAGE_01));
		final OutputDTO cree02 = this.service.creer(
				new TypeProduitDTO.InputDTO(IT_PAGE_02));
		final OutputDTO cree03 = this.service.creer(
				new TypeProduitDTO.InputDTO(IT_PAGE_03));
		final OutputDTO cree04 = this.service.creer(
				new TypeProduitDTO.InputDTO(IT_PAGE_04));
		final OutputDTO cree05 = this.service.creer(
				new TypeProduitDTO.InputDTO(IT_PAGE_05));

		final long attendu = baseline + 5L;

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
				.extracting(OutputDTO::getTypeProduit)
				.contains(
						IT_PAGE_01,
						IT_PAGE_02,
						IT_PAGE_03,
						IT_PAGE_04,
						IT_PAGE_05);

		assertThat(rp.getContent())
				.extracting(OutputDTO::getIdTypeProduit)
				.contains(
						cree01.getIdTypeProduit(),
						cree02.getIdTypeProduit(),
						cree03.getIdTypeProduit(),
						cree04.getIdTypeProduit(),
						cree05.getIdTypeProduit());

		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

		/* preuve BD : les 5 créations existent physiquement. */
		assertThat(this.compterTypeProduitEnBase(cree01.getIdTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(cree01.getIdTypeProduit()))
				.isEqualTo(IT_PAGE_01);

		assertThat(this.compterTypeProduitEnBase(cree02.getIdTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(cree02.getIdTypeProduit()))
				.isEqualTo(IT_PAGE_02);

		assertThat(this.compterTypeProduitEnBase(cree03.getIdTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(cree03.getIdTypeProduit()))
				.isEqualTo(IT_PAGE_03);

		assertThat(this.compterTypeProduitEnBase(cree04.getIdTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(cree04.getIdTypeProduit()))
				.isEqualTo(IT_PAGE_04);

		assertThat(this.compterTypeProduitEnBase(cree05.getIdTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(cree05.getIdTypeProduit()))
				.isEqualTo(IT_PAGE_05);

	} // __________________________________________________________________	
	
	
	
	// ========================= findByLibelle ============================
	
	
	
	/**
	 * <div>
	 * <p>findByLibelle(blank) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>ne lève aucune exception</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelle(blank) : retourne null + message exact MESSAGE_PARAM_BLANK")
	public void testFindByLibelleBlank() throws Exception {

		final OutputDTO dto = this.service.findByLibelle(ESPACES);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(introuvable) : cas nominal de non-trouvabilité.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + libellé</li>
	 * <li>ne lève aucune exception</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelle(introuvable) : retourne null + message exact MESSAGE_OBJ_INTROUVABLE + libellé")
	public void testFindByLibelleIntrouvable() throws Exception {

		final String libelleAbsent = "IT_FIND_BY_LIBELLE_ABSENT_BD_01";

		final OutputDTO dto = this.service.findByLibelle(libelleAbsent);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + libelleAbsent);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(ok) : test béton avec preuve BD.</p>
	 * <ul>
	 * <li>crée d'abord un TypeProduit réel</li>
	 * <li>retrouve ensuite exactement ce même objet par son libellé</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}</li>
	 * <li>prouve physiquement la présence en base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelle(ok) : OutputDTO cohérent + message exact + preuve BD")
	public void testFindByLibelleOkAvecPreuveBd() throws Exception {

		final String libelle = "IT_FIND_BY_LIBELLE_OK_BD_01";

		final OutputDTO cree = this.service.creer(new TypeProduitDTO.InputDTO(libelle));

		assertThat(cree).isNotNull();
		assertThat(cree.getIdTypeProduit()).isNotNull();
		assertThat(cree.getTypeProduit()).isEqualTo(libelle);

		final OutputDTO dto = this.service.findByLibelle(libelle);

		assertThat(dto).isNotNull();
		assertThat(dto.getIdTypeProduit()).isEqualTo(cree.getIdTypeProduit());
		assertThat(dto.getTypeProduit()).isEqualTo(libelle);

		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		assertThat(this.compterTypeProduitEnBase(cree.getIdTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(cree.getIdTypeProduit()))
				.isEqualTo(libelle);
		assertThat(this.compterTypeProduitParLibelleEnBase(libelle))
				.isEqualTo(1L);

	} // __________________________________________________________________

	
	
	// ====================== findByLibelleRapide =========================
	
	
	
	/**
	 * <div>
	 * <p>findByLibelleRapide(null) :
	 * émet MESSAGE_PARAM_NULL + IllegalStateException.</p>
	 * </div>
	 */
	@Test
	@DisplayName("findByLibelleRapide(null) : IllegalStateException + message MESSAGE_PARAM_NULL")
	public void testFindByLibelleRapideNull() {

		assertThatThrownBy(() -> this.service.findByLibelleRapide(null))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(TypeProduitICuService.MESSAGE_PARAM_NULL);

		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_NULL);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>Si pContenu est blank :
	 * délègue au scénario complet de rechercherTous().</p>
	 * <ul>
	 * <li>retourne tous les objets présents</li>
	 * <li>positionne le message observable de rechercherTous()</li>
	 * <li>reste cohérent avec la présence physique en base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelleRapide(blank) : délègue à rechercherTous() + message MESSAGE_RECHERCHE_OK")
	public void testFindByLibelleRapideBlank() throws Exception {

		final String libelle1 = "IT_FRAPIDE_BLANK_01";
		final String libelle2 = "IT_FRAPIDE_BLANK_02";

		final OutputDTO cree1 = this.service.creer(new TypeProduitDTO.InputDTO(libelle1));
		final OutputDTO cree2 = this.service.creer(new TypeProduitDTO.InputDTO(libelle2));

		final List<OutputDTO> dtos = this.service.findByLibelleRapide(ESPACES);

		assertThat(dtos).isNotNull();
		assertThat(dtos)
				.extracting(OutputDTO::getTypeProduit)
				.contains(libelle1, libelle2);

		assertThat(dtos)
				.extracting(OutputDTO::getIdTypeProduit)
				.contains(cree1.getIdTypeProduit(), cree2.getIdTypeProduit());

		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		assertThat(this.compterTypeProduitEnBase(cree1.getIdTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.compterTypeProduitEnBase(cree2.getIdTypeProduit()))
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

		final List<OutputDTO> dtos = this.service.findByLibelleRapide("ZZZ_AUCUN_RESULTAT_FRAPIDE_01");

		assertThat(dtos).isNotNull();
		assertThat(dtos).isEmpty();

		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>Si des libellés correspondent :
	 * retourne une liste DTO cohérente, sans doublon,
	 * et émet MESSAGE_RECHERCHE_OK.</p>
	 * <ul>
	 * <li>les objets correspondants existent physiquement en base</li>
	 * <li>les objets hors cible ne doivent pas être attendus dans le résultat</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByLibelleRapide(ok) : liste DTO cohérente + sans doublon + message exact + preuve BD")
	public void testFindByLibelleRapideOkAvecPreuveBd() throws Exception {

		final String fragment = "UC_QA_A1";
		final String libelleMatch1 = "IT_FRAPIDE_" + fragment + "_X";
		final String libelleMatch2 = "IT_FRAPIDE_" + fragment + "_Y";
		final String libelleHorsCible = "IT_FRAPIDE_UC_QA_B2_Z";

		final OutputDTO cree1 = this.service.creer(new TypeProduitDTO.InputDTO(libelleMatch1));
		final OutputDTO cree2 = this.service.creer(new TypeProduitDTO.InputDTO(libelleMatch2));
		final OutputDTO creeHorsCible = this.service.creer(new TypeProduitDTO.InputDTO(libelleHorsCible));

		final List<OutputDTO> dtos = this.service.findByLibelleRapide(fragment);

		assertThat(dtos).isNotNull();
		assertThat(dtos).doesNotHaveDuplicates();

		assertThat(dtos)
				.extracting(OutputDTO::getTypeProduit)
				.contains(libelleMatch1, libelleMatch2)
				.doesNotContain(libelleHorsCible);

		assertThat(dtos)
				.extracting(OutputDTO::getIdTypeProduit)
				.contains(cree1.getIdTypeProduit(), cree2.getIdTypeProduit())
				.doesNotContain(creeHorsCible.getIdTypeProduit());

		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		assertThat(this.compterTypeProduitEnBase(cree1.getIdTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(cree1.getIdTypeProduit()))
				.isEqualTo(libelleMatch1);

		assertThat(this.compterTypeProduitEnBase(cree2.getIdTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(cree2.getIdTypeProduit()))
				.isEqualTo(libelleMatch2);

		assertThat(this.compterTypeProduitEnBase(creeHorsCible.getIdTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(creeHorsCible.getIdTypeProduit()))
				.isEqualTo(libelleHorsCible);

		assertThat(this.compterTypeProduitParLibelleEnBase(libelleMatch1))
				.isEqualTo(1L);
		assertThat(this.compterTypeProduitParLibelleEnBase(libelleMatch2))
				.isEqualTo(1L);
		assertThat(this.compterTypeProduitParLibelleEnBase(libelleHorsCible))
				.isEqualTo(1L);

	} // __________________________________________________________________	

	
	
	// ========================== findByDTO ===============================
	
	
	
	/**
	 * <div>
	 * <p>findByDTO(null) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_OBJ_NULL}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(null) : retourne null + message exact MESSAGE_RECHERCHE_OBJ_NULL")
	public void testFindByDTONull() throws Exception {

		final OutputDTO dto = this.service.findByDTO(null);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OBJ_NULL);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>findByDTO(blank) : délégation exacte au scénario blank
	 * de findByLibelle(...).</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>n'écrit rien en base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(blank) : retourne null + message exact MESSAGE_PARAM_BLANK")
	public void testFindByDTOBlank() throws Exception {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		final OutputDTO dto = this.service.findByDTO(new TypeProduitDTO.InputDTO(ESPACES));

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>findByDTO(introuvable) : délégation exacte au scénario
	 * d'introuvabilité de findByLibelle(...).</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + libellé</li>
	 * <li>prouve physiquement l'absence en base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(introuvable) : retourne null + message exact MESSAGE_OBJ_INTROUVABLE + libellé")
	public void testFindByDTOIntrouvable() throws Exception {

		final String libelleAbsent = "IT_FIND_BY_DTO_ABSENT_BD_01";
		final InputDTO input = new TypeProduitDTO.InputDTO(libelleAbsent);

		final OutputDTO dto = this.service.findByDTO(input);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + libelleAbsent);
		assertThat(this.compterTypeProduitParLibelleEnBase(libelleAbsent))
				.isEqualTo(0L);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>findByDTO(ok) : test béton de la recherche via DTO.</p>
	 * <ul>
	 * <li>crée d'abord un TypeProduit réel</li>
	 * <li>recherche ensuite cet objet via un InputDTO portant le même libellé</li>
	 * <li>retourne un OutputDTO cohérent</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}</li>
	 * <li>prouve physiquement la présence unique en base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(ok) : OutputDTO cohérent + message exact + preuve BD")
	public void testFindByDTOOkAvecPreuveBd() throws Exception {

		final String libelle = "IT_FIND_BY_DTO_OK_BD_01";

		final OutputDTO cree = this.service.creer(new TypeProduitDTO.InputDTO(libelle));

		assertThat(cree).isNotNull();
		assertThat(cree.getIdTypeProduit()).isNotNull();
		assertThat(cree.getTypeProduit()).isEqualTo(libelle);

		final InputDTO input = new TypeProduitDTO.InputDTO(libelle);
		final OutputDTO dto = this.service.findByDTO(input);

		assertThat(dto).isNotNull();
		assertThat(dto.getIdTypeProduit()).isEqualTo(cree.getIdTypeProduit());
		assertThat(dto.getTypeProduit()).isEqualTo(libelle);
		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);
		assertThat(this.compterTypeProduitEnBase(cree.getIdTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(cree.getIdTypeProduit()))
				.isEqualTo(libelle);
		assertThat(this.compterTypeProduitParLibelleEnBase(libelle))
				.isEqualTo(1L);

	} // __________________________________________________________________	

	
	
	// =========================== findById ===============================
	
	
	
	/**
	 * <div>
	 * <p>findById(null) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findById(null) : retourne null + message exact MESSAGE_PARAM_NULL")
	public void testFindByIdNull() throws Exception {

		final OutputDTO dto = this.service.findById(null);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_NULL);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>findById(introuvable) : cas nominal de non-trouvabilité.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + id</li>
	 * <li>prouve physiquement l'absence en base</li>
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
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + idInexistant);
		assertThat(this.compterTypeProduitEnBase(idInexistant))
				.isEqualTo(0L);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>findById(ok) : test béton de la recherche par identifiant.</p>
	 * <ul>
	 * <li>crée d'abord un TypeProduit réel</li>
	 * <li>recherche ensuite cet objet via son identifiant persistant</li>
	 * <li>retourne un OutputDTO cohérent</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}</li>
	 * <li>prouve physiquement la présence unique en base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findById(ok) : OutputDTO cohérent + message exact + preuve BD")
	public void testFindByIdOkAvecPreuveBd() throws Exception {

		final String libelle = "IT_FIND_BY_ID_OK_BD_01";

		final OutputDTO cree = this.service.creer(new TypeProduitDTO.InputDTO(libelle));

		assertThat(cree).isNotNull();
		assertThat(cree.getIdTypeProduit()).isNotNull();
		assertThat(cree.getTypeProduit()).isEqualTo(libelle);

		final Long id = cree.getIdTypeProduit();

		final OutputDTO dto = this.service.findById(id);

		assertThat(dto).isNotNull();
		assertThat(dto.getIdTypeProduit()).isEqualTo(id);
		assertThat(dto.getTypeProduit()).isEqualTo(libelle);
		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);
		assertThat(this.compterTypeProduitEnBase(id))
				.isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(id))
				.isEqualTo(libelle);
		assertThat(this.compterTypeProduitParLibelleEnBase(libelle))
				.isEqualTo(1L);

	} // __________________________________________________________________	

	
	
	// ============================ update ================================
	
	
	
	/**
	 * <div>
	 * <p>update(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'écrit rien en base</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("update(null) : ExceptionParametreNull + message exact MESSAGE_PARAM_NULL + aucune écriture BD")
	public void testUpdateNull() {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		assertThatThrownBy(() -> this.service.update(null))
				.isInstanceOf(ExceptionParametreNull.class);

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_NULL);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>n'écrit rien en base</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("update(blank) : ExceptionParametreBlank + message exact MESSAGE_PARAM_BLANK + aucune écriture BD")
	public void testUpdateBlank() {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		final InputDTO input = new TypeProduitDTO.InputDTO(ESPACES);

		assertThatThrownBy(() -> this.service.update(input))
				.isInstanceOf(ExceptionParametreBlank.class);

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(introuvable) : aucun objet persistant
	 * ne correspond au libellé exact transmis.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + libellé</li>
	 * <li>ne crée aucune ligne en base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(introuvable) : null + message exact MESSAGE_OBJ_INTROUVABLE + libellé + aucune création BD")
	public void testUpdateIntrouvable() throws Exception {

		final String libelleAbsent = "IT_UPDATE_INTR_BD_01";
		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		final InputDTO input = new TypeProduitDTO.InputDTO(libelleAbsent);
		final OutputDTO dto = this.service.update(input);

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + libelleAbsent);
		assertThat(this.compterTypeProduitParLibelleEnBase(libelleAbsent))
				.isEqualTo(0L);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(ok) : test béton du scénario nominal réel.</p>
	 * <ul>
	 * <li>crée d'abord un TypeProduit réel</li>
	 * <li>met ensuite à jour ce même objet
	 * via un {@link InputDTO} portant le même libellé exact</li>
	 * <li>retourne un {@link OutputDTO} cohérent</li>
	 * <li>conserve exactement le même identifiant persistant</li>
	 * <li>ne crée aucun doublon et n'altère pas le volume total</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_MODIF_OK} + libellé</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(ok) : OutputDTO cohérent + ID conservé + message exact + absence de doublon")
	public void testUpdateOkAvecPreuveBdEtIdConserve() throws Exception {

		final String libelle = "IT_UPDATE_OK_BD_01";

		final OutputDTO cree = this.service.creer(new TypeProduitDTO.InputDTO(libelle));

		assertThat(cree).isNotNull();
		assertThat(cree.getIdTypeProduit()).isNotNull();
		assertThat(cree.getTypeProduit()).isEqualTo(libelle);

		final Long idAvantUpdate = cree.getIdTypeProduit();
		final Long nombreAvantUpdate = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		final OutputDTO modifie = this.service.update(new TypeProduitDTO.InputDTO(libelle));
		final String message = this.service.getMessage();

		final Long nombreApresUpdate = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		assertThat(modifie).isNotNull();
		assertThat(modifie.getIdTypeProduit()).isEqualTo(idAvantUpdate);
		assertThat(modifie.getTypeProduit()).isEqualTo(libelle);
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_MODIF_OK + libelle);

		assertThat(nombreApresUpdate).isEqualTo(nombreAvantUpdate);
		assertThat(this.compterTypeProduitEnBase(idAvantUpdate))
				.isEqualTo(1L);
		assertThat(this.lireLibelleTypeProduitEnBase(idAvantUpdate))
				.isEqualTo(libelle);
		assertThat(this.compterTypeProduitParLibelleEnBase(libelle))
				.isEqualTo(1L);

		final OutputDTO relu = this.service.findById(idAvantUpdate);

		assertThat(relu).isNotNull();
		assertThat(relu.getIdTypeProduit()).isEqualTo(idAvantUpdate);
		assertThat(relu.getTypeProduit()).isEqualTo(libelle);

	} // __________________________________________________________________	

	
	
	// ============================ delete ================================
	
	
	
	/**
	 * <div>
	 * <p>delete(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'écrit rien en base</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("delete(null) : ExceptionParametreNull + message exact MESSAGE_PARAM_NULL + aucune écriture BD")
	public void testDeleteNull() {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		assertThatThrownBy(() -> this.service.delete(null))
				.isInstanceOf(ExceptionParametreNull.class);

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_NULL);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>n'écrit rien en base</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("delete(blank) : ExceptionParametreBlank + message exact MESSAGE_PARAM_BLANK + aucune écriture BD")
	public void testDeleteBlank() {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		final InputDTO input = new TypeProduitDTO.InputDTO(ESPACES);

		assertThatThrownBy(() -> this.service.delete(input))
				.isInstanceOf(ExceptionParametreBlank.class);

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(introuvable) : aucun objet persistant
	 * ne correspond au libellé exact transmis.</p>
	 * <ul>
	 * <li>ne supprime rien</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + libellé</li>
	 * <li>la base reste strictement inchangée</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("delete(introuvable) : aucune suppression + message exact MESSAGE_OBJ_INTROUVABLE + libellé")
	public void testDeleteIntrouvable() throws Exception {

		final String libelleAbsent = IT_INEXISTANT_DELETE;
		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		this.service.delete(new TypeProduitDTO.InputDTO(libelleAbsent));

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + libelleAbsent);
		assertThat(this.compterTypeProduitParLibelleEnBase(libelleAbsent))
				.isEqualTo(0L);
		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(ok) : test béton du scénario nominal réel.</p>
	 * <ul>
	 * <li>crée d'abord un TypeProduit réel</li>
	 * <li>supprime ensuite cet objet via un {@link InputDTO}
	 * portant le même libellé exact</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_DELETE_OK} + libellé</li>
	 * <li>prouve physiquement la disparition en base</li>
	 * <li>prouve la baisse du volume total en base</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("delete(ok) : suppression physique + message exact + preuve BD")
	public void testDeleteOkAvecPreuveBd() throws Exception {

		final String libelle = IT_DELETE_OK;

		final OutputDTO cree = this.service.creer(new TypeProduitDTO.InputDTO(libelle));

		assertThat(cree).isNotNull();
		assertThat(cree.getIdTypeProduit()).isNotNull();
		assertThat(cree.getTypeProduit()).isEqualTo(libelle);

		final Long id = cree.getIdTypeProduit();
		final Long nombreAvantDelete = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		this.service.delete(new TypeProduitDTO.InputDTO(libelle));

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
				SELECT_COUNT_FROM_TYPES_PRODUIT,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_DELETE_OK + libelle);
		assertThat(nombreApresDelete).isEqualTo(nombreAvantDelete - 1L);
		assertThat(this.compterTypeProduitEnBase(id)).isEqualTo(0L);
		assertThat(this.compterTypeProduitParLibelleEnBase(libelle)).isEqualTo(0L);

		final OutputDTO apresSuppression = this.service.findByLibelle(libelle);

		assertThat(apresSuppression).isNull();

	} // __________________________________________________________________		

	
	
	// ============================ count =================================
	
	
	
	/**
	 * <div>
	 * <p>count() : retourne le comptage réel de la base
	 * et positionne le message observable correspondant.</p>
	 * <ul>
	 * <li>compare le résultat UC
	 * au {@code SELECT COUNT(*)} physique</li>
	 * <li>vérifie le message exact :
	 * vide si 0, succès si strictement positif</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("count() : résultat identique au COUNT(*) physique + message observable exact")
	public void testCountRetourneLeNombrePhysiqueEtLeMessageObservable() throws Exception {

		final Long nombrePhysique = this.jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM TYPES_PRODUIT",
				Long.class);

		final long retourUc = this.service.count();

		assertThat(retourUc).isEqualTo(nombrePhysique.longValue());

		if (retourUc == 0L) {
			assertThat(this.service.getMessage())
					.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		} else {
			assertThat(this.service.getMessage())
					.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);
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
	public void testCountCoherentAvecMessagesAvantApresCreationsPuisNettoyage() throws Exception {

		final long baseline = this.service.count();

		if (baseline == 0L) {
			assertThat(this.service.getMessage())
					.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		} else {
			assertThat(this.service.getMessage())
					.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);
		}

		this.service.creer(new TypeProduitDTO.InputDTO(IT_COUNT_01));
		this.service.creer(new TypeProduitDTO.InputDTO(IT_COUNT_02));

		final long apresCreations = this.service.count();

		assertThat(apresCreations).isEqualTo(baseline + 2L);
		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		this.service.delete(new TypeProduitDTO.InputDTO(IT_COUNT_01));
		this.service.delete(new TypeProduitDTO.InputDTO(IT_COUNT_02));

		final long apresNettoyage = this.service.count();

		assertThat(apresNettoyage).isEqualTo(baseline);

		if (apresNettoyage == 0L) {
			assertThat(this.service.getMessage())
					.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		} else {
			assertThat(this.service.getMessage())
					.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);
		}

		final Long nombrePhysiqueFinal = this.jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM TYPES_PRODUIT",
				Long.class);

		assertThat(apresNettoyage).isEqualTo(nombrePhysiqueFinal.longValue());

	} // __________________________________________________________________	

	
	
	// ========================== getMessage ==============================
	
	
	
	/**
	 * <div>
	 * <p>getMessage(initial) : état initial du service intégré.</p>
	 * <ul>
	 * <li>retourne {@code null} avant toute opération,
	 * ce qui est acceptable</li>
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
	 * positionné par le comptage réel de la base.</p>
	 * <ul>
	 * <li>si le comptage réel vaut 0,
	 * le message est
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>sinon,
	 * le message est
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
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
					.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		} else {
			assertThat(message)
					.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);
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
	 * {@link TypeProduitICuService#MESSAGE_CREER_NULL_KO}</li>
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
				.isEqualTo(TypeProduitICuService.MESSAGE_CREER_NULL_KO);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>getMessage(dernier message gagne) :
	 * une opération réelle plus récente
	 * écrase bien le message précédent.</p>
	 * <ul>
	 * <li>après une erreur locale,
	 * le message vaut d'abord
	 * {@link TypeProduitICuService#MESSAGE_CREER_NULL_KO}</li>
	 * <li>après un {@code count()} réel,
	 * le message courant devient le message observable
	 * du comptage réel</li>
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
				.isEqualTo(TypeProduitICuService.MESSAGE_CREER_NULL_KO);

		if (retour == 0L) {
			assertThat(messageFinal)
					.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		} else {
			assertThat(messageFinal)
					.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);
		}

	} // __________________________________________________________________
	
	

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
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>Lit physiquement en base la colonne TYPE_PRODUIT
	 * pour un ID donné.</p>
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
	 * <p>Compte physiquement en base le nombre de lignes
	 * portant un ID donné.</p>
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
	 * <p>Compte physiquement en base le nombre de lignes
	 * portant un libellé donné.</p>
	 * </div>
	 *
	 * @param pLibelle : String : TYPE_PRODUIT.
	 * @return Long : nombre de lignes.
	 */
	private Long compterTypeProduitParLibelleEnBase(
			final String pLibelle) {

		return this.jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM TYPES_PRODUIT WHERE TYPE_PRODUIT = ?",
				Long.class,
				pLibelle);
		
	} // __________________________________________________________________
	
	

}
