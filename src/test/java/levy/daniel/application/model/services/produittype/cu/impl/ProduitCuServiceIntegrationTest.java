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

import levy.daniel.application.model.dto.produittype.ProduitDTO;
import levy.daniel.application.model.dto.produittype.ProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.ProduitDTO.OutputDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.services.produittype.cu.ProduitICuService;
import levy.daniel.application.model.services.produittype.cu.SousTypeProduitICuService;
import levy.daniel.application.model.services.produittype.cu.TypeProduitICuService;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionDoublon;
import levy.daniel.application.model.services.produittype.gateway.impl.ProduitGatewayJPAService;
import levy.daniel.application.model.services.produittype.gateway.impl.SousTypeProduitGatewayJPAService;
import levy.daniel.application.model.services.produittype.gateway.impl.TypeProduitGatewayJPAService;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreBlank;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreNull;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;
import levy.daniel.application.persistence.metier.produittype.dao.daosJPA.TypeProduitDaoJPA;
import levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA.TypeProduitJPA;

/**
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE ProduitCuServiceIntegrationTest.java :
 * </p>
 * <p>
 * Tests d'intégration complets (avec tests de stockage) du SERVICE ADAPTER METIER CU
 * {@link ProduitCuService}.
 * </p>
 * <p>
 * Ce test vérifie le SERVICE UC avec un vrai stockage JPA/H2.
 * </p>
 * <ul>
 * <li>Il injecte le PORT UC {@link ProduitICuService}.</li>
 * <li>Il injecte aussi les PORTS UC {@link SousTypeProduitICuService}
 * et {@link TypeProduitICuService} pour créer les parents nécessaires
 * aux scénarios de stockage.</li>
 * <li>Il importe explicitement le SERVICE UC testé
 * {@link ProduitCuService}, le SERVICE UC parent direct
 * {@link SousTypeProduitCuService} et le SERVICE UC grand-parent
 * {@link TypeProduitCuService}.</li>
 * <li>Il importe explicitement les Gateways JPA nécessaires
 * {@link ProduitGatewayJPAService},
 * {@link SousTypeProduitGatewayJPAService}
 * et {@link TypeProduitGatewayJPAService}.</li>
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
		ProduitCuService.class,
		SousTypeProduitCuService.class,
		TypeProduitCuService.class,
		ProduitGatewayJPAService.class,
		SousTypeProduitGatewayJPAService.class,
		TypeProduitGatewayJPAService.class
})
@ContextConfiguration(classes = ProduitCuServiceIntegrationTest.ConfigTest.class)
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
@Tag(ProduitCuServiceIntegrationTest.TAG)
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
	 * "Outil".
	 */
	public static final String OUTIL = "Outil";

	/**
	 * "Loisir".
	 */
	public static final String LOISIR = "Loisir";

	/**
	 * "Outillage".
	 */
	public static final String OUTILLAGE = "Outillage";

	/**
	 * "Atelier".
	 */
	public static final String ATELIER = "Atelier";

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
	 * "Vis".
	 */
	public static final String VIS = "Vis";

	/**
	 * "Ecrou".
	 */
	public static final String ECROU = "Ecrou";

	/**
	 * "Cle plate".
	 */
	public static final String CLE_PLATE = "Cle plate";

	/**
	 * "Boite a outils".
	 */
	public static final String BOITE_A_OUTILS = "Boite a outils";

	/**
	 * "Produit inconnu".
	 */
	public static final String PRODUIT_INCONNU = "Produit inconnu";

	/**
	 * "Produit modification absent".
	 */
	public static final String PRODUIT_MODIFICATION_ABSENT = "Produit modification absent";

	/**
	 * "Produit suppression absent".
	 */
	public static final String PRODUIT_SUPPRESSION_ABSENT = "Produit suppression absent";

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

	// =========================== TAG ==================================//
		
	/**
	 * "cu-it-Creer"
	 */
	public static final String TAG_CREER = "cu-it-Creer";

	// ============================ DN ==================================//
	
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

	// ========================== SELECT ================================//
	
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
	 * SERVICE JDBC direct pour preuve de stockage.
	 */
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * SERVICE CU Produit sous test (PORT).
	 */
	@Autowired
	private ProduitICuService service;

	/**
	 * SERVICE CU SousTypeProduit (pour créer les parents nécessaires aux tests de stockage).
	 */
	@Autowired
	private SousTypeProduitICuService sousTypeProduitService;

	/**
	 * SERVICE CU TypeProduit (pour créer les grands-parents nécessaires aux tests de stockage).
	 */
	@Autowired
	private TypeProduitICuService typeProduitService;

	/**
	 * <div>
	 * <p>EntityManager JPA du contexte de test.</p>
	 * <p>
	 * Dans un test {@link DataJpaTest}, chaque méthode s'exécute dans une
	 * transaction de test. Une écriture ou une suppression JPA peut rester
	 * en attente dans le contexte de persistance tant qu'un {@code flush()}
	 * n'a pas été demandé explicitement.
	 * </p>
	 * <p>
	 * Ce test utilise {@link JdbcTemplate} comme preuve indépendante dans le
	 * stockage. Avant une preuve JDBC portant sur une écriture ou une
	 * suppression, le test force donc la synchronisation JPA afin que la
	 * lecture SQL directe voie l'état réellement demandé au stockage par le
	 * SERVICE UC.
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
	public ProduitCuServiceIntegrationTest() {
		super();
	}

    
    
    // ===================== CONFIGURATION SPRING =======================//

    
    
	/**
	 * <div>
	 * <p style="font-weight:bold;">CONFIGURATION DE TEST AUTONOME.</p>
	 *
	 * <p>
	 * Cette classe interne permet à {@link DataJpaTest} de démarrer depuis
	 * cette classe de test, sans dépendre d'une classe de bootstrap ou d'une
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
	 * {@link ProduitCuService}, le SERVICE UC parent direct
	 * {@link SousTypeProduitCuService}, le SERVICE UC grand-parent
	 * {@link TypeProduitCuService} et leurs Gateways JPA
	 * {@link ProduitGatewayJPAService},
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
	 * {@link ProduitICuService#MESSAGE_CREER_NULL_KO}</li>
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
				SELECT_COUNT_FROM_PRODUITS,
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
				.isEqualTo(ProduitICuService.MESSAGE_CREER_NULL_KO);

		/* ASSERT :
		 * compte ensuite (en SQL)
		 * le nombre d'enregistrements dans le stockage
		 * après service.creer(null)
		 * afin de prouver que l'appel au SERVICE UC
		 * n'a produit aucune écriture dans le stockage.
		 */
		final Long countApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
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
	 * {@link ProduitICuService#MESSAGE_CREER_LIBELLE_BLANK_KO}</li>
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
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(countAvant).isNotNull();

		/* prépare un InputDTO
		 * dont le libellé métier est blank. */
		final InputDTO input = new ProduitDTO.InputDTO(
				OUTIL,
				OUTILLAGE,
				ESPACES);
		
		/* ACT - ASSERT :
		 * Garantit que this.service.creer(libellé blank)
		 * - jette une ExceptionParametreBlank
		 * - avec un message MESSAGE_CREER_LIBELLE_BLANK_KO.
		 */
		assertThatThrownBy(() -> this.service.creer(input))
				.isInstanceOf(ExceptionParametreBlank.class)
				.hasMessage(
						ProduitICuService
								.MESSAGE_CREER_LIBELLE_BLANK_KO);
		
		/* Garantit le message utilisateur MESSAGE_CREER_LIBELLE_BLANK_KO
		 * (message contractuel attendu).
		 */
		assertThat(this.service.getMessage())
				.isEqualTo(
						ProduitICuService
								.MESSAGE_CREER_LIBELLE_BLANK_KO);

		/* ASSERT :
		 * compte ensuite (en SQL)
		 * le nombre d'enregistrements dans le stockage
		 * après l'échec contractuel
		 * afin de prouver que l'appel au SERVICE UC
		 * n'a produit aucune écriture dans le stockage.
		 */
		final Long countApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
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
	 * {@link ProduitICuService#MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO}</li>
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
		 * compte d'abord (en SQL)
		 * le nombre d'enregistrements dans le stockage
		 * avant l'appel au SERVICE UC,
		 * afin de pouvoir prouver ensuite
		 * qu'aucune écriture réelle n'a eu lieu dans le stockage.
		 */
		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(countAvant).isNotNull();

		/* prépare un InputDTO
		 * dont le libellé parent est blank. */
		final InputDTO input = new ProduitDTO.InputDTO(
				OUTIL,
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
						ProduitICuService
								.MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO);
		
		/* Garantit le message utilisateur MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO
		 * (message contractuel attendu).
		 */
		assertThat(this.service.getMessage())
				.isEqualTo(
						ProduitICuService
								.MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO);

		/* ASSERT :
		 * compte ensuite (en SQL)
		 * le nombre d'enregistrements dans le stockage
		 * après l'échec contractuel
		 * afin de prouver que l'appel au SERVICE UC
		 * n'a produit aucune écriture dans le stockage.
		 */
		final Long countApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
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
	 * {@link ProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO}</li>
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
		 * compte d'abord (en SQL)
		 * le nombre d'enregistrements dans le stockage
		 * avant l'appel au SERVICE UC,
		 * afin de pouvoir prouver ensuite
		 * qu'aucune écriture réelle n'a eu lieu dans le stockage.
		 */
		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(countAvant).isNotNull();

		/* prépare un InputDTO valide
		 * dont le parent n'a pas été créé dans le stockage. */
		final InputDTO input = new ProduitDTO.InputDTO(
				OUTIL,
				OUTILLAGE,
				MARTEAU);
		
		/* ACT - ASSERT :
		 * Garantit que this.service.creer(parent absent)
		 * - jette une IllegalStateException
		 * - avec un message MESSAGE_CREER_PARENT_NON_PERSISTANT_KO.
		 */
		assertThatThrownBy(() -> this.service.creer(input))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(
						ProduitICuService
								.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);
		
		/* Garantit le message utilisateur MESSAGE_CREER_PARENT_NON_PERSISTANT_KO
		 * (message contractuel attendu).
		 */
		assertThat(this.service.getMessage())
				.isEqualTo(
						ProduitICuService
								.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		/* ASSERT :
		 * compte ensuite (en SQL)
		 * le nombre d'enregistrements dans le stockage
		 * après l'échec contractuel
		 * afin de prouver que l'appel au SERVICE UC
		 * n'a produit aucune écriture dans le stockage.
		 */
		final Long countApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(countApres).isNotNull();
		assertThat(countApres).isEqualTo(countAvant);
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>garantit que si l'appelant tente creer(...)
	 * avec un objet métier déjà présent dans le stockage sous le même parent :</p>
	 * <ul>
	 * <li>la première création réussit réellement ;</li>
	 * <li>la seconde création lève une {@link ExceptionDoublon} ;</li>
	 * <li>le message utilisateur exact est
	 * {@link ProduitICuService#MESSAGE_CREER_DOUBLON_KO} + libellé ;</li>
	 * <li>aucune nouvelle ligne n'est créée dans le stockage
	 * lors de la tentative de doublon ;</li>
	 * <li>l'unique ligne créée portant déjà cet objet métier
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
		 * crée d'abord le parent persistant requis.
		 */
		this.creerParentsDansStockage(OUTIL, OUTILLAGE);

		/* prépare un DTO valide non seedé.
		 *
		 * Le premier appel à creer(...) créera réellement l'objet métier.
		 * Le second appel avec le même DTO déclenchera ensuite
		 * le cas contractuel de doublon.
		 */
		final InputDTO input = new ProduitDTO.InputDTO(
				OUTIL,
				OUTILLAGE,
				MARTEAU);

		/* Vérifie d'abord que l'objet métier du test
		 * n'est pas déjà présent dans le stockage sous ce parent.
		 */
		assertThat(this.compterProduitParCoupleDansStockage(
				OUTIL,
				OUTILLAGE,
				MARTEAU))
				.isEqualTo(0L);

		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(countAvant).isNotNull();

		/* ACT :
		 * crée une première fois l'objet métier.
		 */
		final OutputDTO cree = this.service.creer(input);

		/*
		 * Synchronise explicitement le contexte de persistance JPA
		 * avant les preuves SQL directes.
		 */
		this.entityManager.flush();

		/* ASSERT :
		 * garantit que la première création réussit
		 * et retourne un DTO persistant.
		 */
		assertThat(cree).isNotNull();
		assertThat(cree.getIdProduit()).isNotNull();
		assertThat(cree.getProduit()).isEqualTo(MARTEAU);
		assertThat(cree.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(cree.getTypeProduit()).isEqualTo(OUTIL);

		/* Garantit que le message de succès de création
		 * est positionné avant tout autre appel au SERVICE UC.
		 */
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_CREER_OK);

		/* Garantit physiquement dans le stockage
		 * qu'une seule ligne porte l'objet métier créé sous ce parent.
		 */
		assertThat(this.compterProduitParCoupleDansStockage(
				OUTIL,
				OUTILLAGE,
				MARTEAU))
				.isEqualTo(1L);

		/* Garantit physiquement dans le stockage
		 * que l'identifiant retourné correspond à une ligne réelle.
		 */
		assertThat(this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS_BY_ID,
				Long.class,
				cree.getIdProduit()))
				.isEqualTo(1L);

		/* Garantit physiquement dans le stockage
		 * que la colonne PRODUIT a bien été écrite
		 * avec le libellé métier attendu.
		 */
		assertThat(this.jdbcTemplate.queryForObject(
				SELECT_PRODUIT_LIBELLE_BY_ID,
				String.class,
				cree.getIdProduit()))
				.isEqualTo(MARTEAU);

		/* Garantit physiquement dans le stockage
		 * que le parent stocké est le parent attendu.
		 */
		assertThat(this.jdbcTemplate.queryForObject(
				SELECT_PARENT_SOUS_TYPE_BY_ID_PRODUIT,
				String.class,
				cree.getIdProduit()))
				.isEqualTo(OUTILLAGE);

		final Long countApresPremiereCreation = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(countApresPremiereCreation).isNotNull();
		assertThat(countApresPremiereCreation).isEqualTo(countAvant + 1L);

		/* ACT - ASSERT :
		 * sollicite une deuxième fois la méthode creer(...)
		 * avec le même objet métier déjà présent sous le même parent.
		 *
		 * Le SERVICE UC doit refuser le doublon avant toute nouvelle
		 * écriture dans le stockage.
		 */
		assertThatThrownBy(() -> this.service.creer(input))
				.isInstanceOf(ExceptionDoublon.class)
				.hasMessage(
						ProduitICuService.MESSAGE_CREER_DOUBLON_KO
								+ MARTEAU);

		/* Garantit le message utilisateur exact. */
		assertThat(this.service.getMessage())
				.isEqualTo(
						ProduitICuService.MESSAGE_CREER_DOUBLON_KO
								+ MARTEAU);

		/* ASSERT :
		 * contrôle ensuite par SQL direct
		 * que le stockage contient toujours une seule ligne
		 * pour cet objet métier sous ce parent.
		 */
		assertThat(this.compterProduitParCoupleDansStockage(
				OUTIL,
				OUTILLAGE,
				MARTEAU))
				.isEqualTo(1L);

		assertThat(this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS_BY_ID,
				Long.class,
				cree.getIdProduit()))
				.isEqualTo(1L);

		/* Garantit enfin que le volume total du stockage
		 * n'a pas augmenté lors de la tentative de doublon.
		 */
		final Long countApresDoublon = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(countApresDoublon).isNotNull();
		assertThat(countApresDoublon).isEqualTo(countApresPremiereCreation);
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>garantit que creer(OK) :</p>
	 * <ul>
	 * <li>crée d'abord le parent persistant requis ;</li>
	 * <li>crée réellement une ligne dans le stockage ;</li>
	 * <li>retourne un {@link OutputDTO} persistant ;</li>
	 * <li>émet un message
	 * {@link ProduitICuService#MESSAGE_CREER_OK}</li>
	 * <li>prouve le rattachement au parent dans le stockage ;</li>
	 * <li>rend la donnée retrouvable via le SERVICE UC par libellé et par ID.</li>
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
		this.creerParentsDansStockage(OUTIL, OUTILLAGE);

		/* prépare un DTO valide à créer
		 * et mémorise le nombre de lignes avant création.
		 */
		final InputDTO input = new ProduitDTO.InputDTO(
				OUTIL,
				OUTILLAGE,
				TOURNEVIS);

		/* Vérifie d'abord que l'objet métier du test
		 * n'est pas déjà présent dans le stockage sous ce parent.
		 */
		assertThat(this.compterProduitParCoupleDansStockage(
				OUTIL,
				OUTILLAGE,
				TOURNEVIS))
				.isEqualTo(0L);

		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(countAvant).isNotNull();

		/* ACT :
		 * sollicite la méthode creer(...)
		 * dans un scénario nominal complet de persistance réelle.
		 */
		final OutputDTO cree = this.service.creer(input);

		/*
		 * Synchronise explicitement le contexte de persistance JPA
		 * avant les preuves SQL directes.
		 */
		this.entityManager.flush();

		/* ASSERT :
		 * garantit d'abord que le DTO retourné
		 * est bien persistant et correctement renseigné.
		 */
		assertThat(cree).isNotNull();
		assertThat(cree.getIdProduit()).isNotNull();
		assertThat(cree.getProduit()).isEqualTo(TOURNEVIS);
		assertThat(cree.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(cree.getTypeProduit()).isEqualTo(OUTIL);

		/* Garantit que le message de succès de création
		 * est positionné avant tout autre appel au SERVICE UC.
		 */
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_CREER_OK);

		/* Garantit que la création augmente bien le nombre total
		 * de lignes dans le stockage réel.
		 */
		final Long countApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(countApres).isNotNull();
		assertThat(countApres).isEqualTo(countAvant + 1L);

		/* Garantit physiquement dans le stockage
		 * qu'une seule ligne porte bien l'identifiant créé.
		 */
		assertThat(this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS_BY_ID,
				Long.class,
				cree.getIdProduit()))
				.isEqualTo(1L);

		/* Garantit physiquement dans le stockage
		 * que la colonne PRODUIT a bien été écrite
		 * avec le libellé métier attendu.
		 */
		assertThat(this.jdbcTemplate.queryForObject(
				SELECT_PRODUIT_LIBELLE_BY_ID,
				String.class,
				cree.getIdProduit()))
				.isEqualTo(TOURNEVIS);

		/* Garantit physiquement dans le stockage
		 * que le parent stocké est le parent attendu.
		 */
		assertThat(this.jdbcTemplate.queryForObject(
				SELECT_PARENT_SOUS_TYPE_BY_ID_PRODUIT,
				String.class,
				cree.getIdProduit()))
				.isEqualTo(OUTILLAGE);

		/* Garantit physiquement dans le stockage
		 * qu'une seule ligne porte l'objet métier créé sous ce parent.
		 */
		assertThat(this.compterProduitParCoupleDansStockage(
				OUTIL,
				OUTILLAGE,
				TOURNEVIS))
				.isEqualTo(1L);

		/* Garantit que l'objet nouvellement créé
		 * est bien retrouvable par libellé via le SERVICE UC.
		 */
		final List<OutputDTO> trouvesParLibelle = this.service.findByLibelle(
				TOURNEVIS);

		assertThat(trouvesParLibelle).isNotNull();
		assertThat(trouvesParLibelle).hasSize(1);
		assertThat(trouvesParLibelle.get(0).getIdProduit())
				.isEqualTo(cree.getIdProduit());
		assertThat(trouvesParLibelle.get(0).getProduit())
				.isEqualTo(TOURNEVIS);
		assertThat(trouvesParLibelle.get(0).getSousTypeProduit())
				.isEqualTo(OUTILLAGE);
		assertThat(trouvesParLibelle.get(0).getTypeProduit())
				.isEqualTo(OUTIL);

		/* Garantit que l'objet nouvellement créé
		 * est bien retrouvable par identifiant via le SERVICE UC.
		 */
		final OutputDTO trouveParId 
			= this.service.findById(cree.getIdProduit());

		assertThat(trouveParId).isNotNull();
		assertThat(trouveParId.getIdProduit())
				.isEqualTo(cree.getIdProduit());
		assertThat(trouveParId.getProduit()).isEqualTo(TOURNEVIS);
		assertThat(trouveParId.getSousTypeProduit()).isEqualTo(OUTILLAGE);
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

		creerParentsDansStockage(OUTIL, OUTILLAGE);

		this.service.creer(new ProduitDTO.InputDTO(OUTIL, OUTILLAGE, PERCEUSE));
		this.service.creer(new ProduitDTO.InputDTO(OUTIL, OUTILLAGE, PINCE));

		final List<OutputDTO> dtos = this.service.rechercherTous();

		assertThat(dtos).isNotNull();
		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.contains(PERCEUSE, PINCE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTous() : scénario nominal avec preuve de stockage et cohérence count.</p>
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
		creerParentsDansStockage(OUTIL, OUTILLAGE);
		creerParentsDansStockage(LOISIR, ATELIER);

		final OutputDTO creeGamma = this.service.creer(
				new ProduitDTO.InputDTO(OUTIL, OUTILLAGE, PERCEUSE));
		final OutputDTO creeDelta = this.service.creer(
				new ProduitDTO.InputDTO(LOISIR, ATELIER, PINCE));

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
				.contains(PERCEUSE, PINCE);

		final OutputDTO dtoGamma = dtos.stream()
				.filter(dto -> PERCEUSE.equals(dto.getProduit()))
				.findFirst()
				.orElse(null);

		final OutputDTO dtoDelta = dtos.stream()
				.filter(dto -> PINCE.equals(dto.getProduit()))
				.findFirst()
				.orElse(null);

		assertThat(dtoGamma).isNotNull();
		assertThat(dtoGamma.getIdProduit())
				.isEqualTo(creeGamma.getIdProduit());
		assertThat(dtoGamma.getTypeProduit())
				.isEqualTo(OUTIL);
		assertThat(dtoGamma.getSousTypeProduit())
				.isEqualTo(OUTILLAGE);

		assertThat(dtoDelta).isNotNull();
		assertThat(dtoDelta.getIdProduit())
				.isEqualTo(creeDelta.getIdProduit());
		assertThat(dtoDelta.getTypeProduit())
				.isEqualTo(LOISIR);
		assertThat(dtoDelta.getSousTypeProduit())
				.isEqualTo(ATELIER);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTous() : stockage vide.</p>
	 * <ul>
	 * <li>retourne une liste vide mais non {@code null}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>reste cohérent avec une stockage physiquement vide</li>
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
	@DisplayName("rechercherTous(vide) : liste vide + message MESSAGE_RECHERCHE_VIDE + stockage vide")
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

	
	
	// ===================== rechercherTousString =========================
	
	
	
	/**
	 * <div>
	 * <p>rechercherTousString() : scénario nominal.</p>
	 * <ul>
	 * <li>crée d'abord la hiérarchie parent persistante requise ;</li>
	 * <li>crée plusieurs Produit dans le stockage ;</li>
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
		this.creerParentsDansStockage(OUTIL, OUTILLAGE);

		this.service.creer(
				new ProduitDTO.InputDTO(
						OUTIL,
						OUTILLAGE,
						PERCEUSE));

		this.service.creer(
				new ProduitDTO.InputDTO(
						OUTIL,
						OUTILLAGE,
						PINCE));

		/* ======================= ACT ======================= */
		final List<String> retour = this.service.rechercherTousString();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).contains(PERCEUSE, PINCE);
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


    
    // ================== rechercherTousParPage ===========================
    
    

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
	 * <p>rechercherTousParPage(ok) : test de stockage sur la cohérence du {@link ResultatPage}.</p>
	 * <ul>
	 * <li>le {@code totalElements} reflète l'état du stockage + créations ;</li>
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
		this.creerParentsDansStockage(OUTIL, OUTILLAGE);

		final long baseline = this.service.count();

		this.service.creer(new ProduitDTO.InputDTO(
				OUTIL, OUTILLAGE, RABOTEUSE));
		this.service.creer(new ProduitDTO.InputDTO(
				OUTIL, OUTILLAGE, COUTEAU));
		this.service.creer(new ProduitDTO.InputDTO(
				OUTIL, OUTILLAGE, CISEAU));
		this.service.creer(new ProduitDTO.InputDTO(
				OUTIL, OUTILLAGE, BURIN));
		this.service.creer(new ProduitDTO.InputDTO(
				OUTIL, OUTILLAGE, MAILLET));

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
	
	
	
	// ========================= findByLibelle ============================
	
	
	
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

		final List<OutputDTO> dtos = this.service.findByLibelle(PRODUIT_INCONNU);

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

		this.creerParentsDansStockage(OUTIL, OUTILLAGE);
		this.creerParentsDansStockage(LOISIR, ATELIER);

		this.service.creer(
				new ProduitDTO.InputDTO(
						OUTIL,
						OUTILLAGE,
						PINCE));

		this.service.creer(
				new ProduitDTO.InputDTO(
						LOISIR,
						ATELIER,
						PINCE));

		final List<OutputDTO> dtos = this.service.findByLibelle(PINCE);

		assertThat(dtos).isNotNull();
		assertThat(dtos).hasSize(2);
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.containsExactlyInAnyOrder(PINCE, PINCE);

		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getSousTypeProduit)
				.containsExactlyInAnyOrder(OUTILLAGE, ATELIER);

		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getTypeProduit)
				.containsExactlyInAnyOrder(OUTIL, LOISIR);

	} // __________________________________________________________________	

	
	
	// ====================== findByLibelleRapide =========================
	
	
	
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

		this.creerParentsDansStockage(OUTIL, OUTILLAGE);

		this.service.creer(new ProduitDTO.InputDTO(
				OUTIL, OUTILLAGE, PERCEUSE));
		this.service.creer(new ProduitDTO.InputDTO(
				OUTIL, OUTILLAGE, PINCE));

		final List<OutputDTO> dtos = this.service.findByLibelleRapide(ESPACES);

		assertThat(dtos).isNotNull();
		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.contains(PERCEUSE, PINCE);

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

		final List<OutputDTO> dtos = this.service.findByLibelleRapide(RECHERCHE_ZZ);

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

		this.creerParentsDansStockage(OUTIL, OUTILLAGE);

		this.service.creer(new ProduitDTO.InputDTO(
				OUTIL, OUTILLAGE, RECHERCHE_ALPHA));
		this.service.creer(new ProduitDTO.InputDTO(
				OUTIL, OUTILLAGE, RECHERCHE_ALPIN));
		this.service.creer(new ProduitDTO.InputDTO(
				OUTIL, OUTILLAGE, LIME));

		final List<OutputDTO> dtos = this.service.findByLibelleRapide(RECHERCHE_AL);

		assertThat(dtos).isNotNull();
		assertThat(dtos).hasSize(2);
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.containsExactlyInAnyOrder(RECHERCHE_ALPHA, RECHERCHE_ALPIN);

	} // __________________________________________________________________	
	
	

	// ======================= findAllByParent(...) =======================

	
	
	/**
	 * <div>
	 * <p>findAllByParent(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève une exception ;</li>
	 * <li>positionne {@link ProduitICuService#RECHERCHE_PARENT_NULL}.</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("findAllByParent(null) : positionne message + lève RuntimeException")
	public void testFindAllByParentNull() {

		assertThatThrownBy(() -> this.service.findAllByParent(null))
				.isInstanceOf(RuntimeException.class);

		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.RECHERCHE_PARENT_NULL);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(parent absent) : violation de contrat.</p>
	 * <ul>
	 * <li>lève une exception ;</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO}.</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("findAllByParent(parent absent) : positionne message + lève exception")
	public void testFindAllByParentPasParent() {

		final SousTypeProduitDTO.InputDTO parentDto
			= new SousTypeProduitDTO.InputDTO(LOISIR, ATELIER);

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

		this.creerParentsDansStockage(LOISIR, ATELIER);

		final List<OutputDTO> dtos = this.service.findAllByParent(
				new SousTypeProduitDTO.InputDTO(LOISIR, ATELIER));

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

		this.creerParentsDansStockage(LOISIR, ATELIER);

		this.service.creer(new ProduitDTO.InputDTO(
				LOISIR, ATELIER, PERCEUSE));
		this.service.creer(new ProduitDTO.InputDTO(
				LOISIR, ATELIER, PINCE));

		final List<OutputDTO> dtos = this.service.findAllByParent(
				new SousTypeProduitDTO.InputDTO(LOISIR, ATELIER));

		assertThat(dtos).isNotNull();
		assertThat(dtos).hasSize(2);
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.containsExactlyInAnyOrder(PERCEUSE, PINCE);

		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getSousTypeProduit)
				.containsExactlyInAnyOrder(ATELIER, ATELIER);

		assertThat(dtos)
				.extracting(ProduitDTO.OutputDTO::getTypeProduit)
				.containsExactlyInAnyOrder(LOISIR, LOISIR);

	} // __________________________________________________________________

	
	
	// ========================== findByDTO ===============================
	
	
	
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
	 * <li>positionne {@link ProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("findByDTO(parent blank) : positionne message + lève IllegalStateException")
	public void testFindByDTOParentBlank() {

		final InputDTO dto = new ProduitDTO.InputDTO(
				OUTIL,
				ESPACES,
				MARTEAU);

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

		this.creerParentsDansStockage(OUTIL, OUTILLAGE);

		this.service.creer(new ProduitDTO.InputDTO(
				OUTIL,
				OUTILLAGE,
				MARTEAU));

		final InputDTO input = new ProduitDTO.InputDTO(
				OUTIL,
				OUTILLAGE,
				TOURNEVIS);

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
	 * <li>positionne {@link TypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findByDTO(ok) : retourne OutputDTO cohérent + message MESSAGE_SUCCES_RECHERCHE")
	public void testFindByDTOOk() throws Exception {

		this.creerParentsDansStockage(OUTIL, OUTILLAGE);

		final OutputDTO cree = this.service.creer(new ProduitDTO.InputDTO(
				OUTIL,
				OUTILLAGE,
				MARTEAU));

		assertThat(cree).isNotNull();

		final InputDTO input = new ProduitDTO.InputDTO(
				OUTIL,
				OUTILLAGE,
				MARTEAU);

		final OutputDTO dto = this.service.findByDTO(input);

		assertThat(dto).isNotNull();
		assertThat(dto.getIdProduit()).isEqualTo(cree.getIdProduit());
		assertThat(dto.getProduit()).isEqualTo(MARTEAU);
		assertThat(dto.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(dto.getTypeProduit()).isEqualTo(OUTIL);
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_FINDBYLIBELLE_SUCCES_RECHERCHE);

	} // __________________________________________________________________	

	
	
	// =========================== findById ===============================
	
	
	
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
	 * <li>positionne exactement {@link TypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("findById(ok) : retourne OutputDTO exact + message MESSAGE_SUCCES_RECHERCHE")
	public void testFindByIdOk() throws Exception {

		this.creerParentsDansStockage(OUTIL, OUTILLAGE);

		final OutputDTO cree = this.service.creer(
				new ProduitDTO.InputDTO(
						OUTIL,
						OUTILLAGE,
						PERCEUSE));

		assertThat(cree).isNotNull();
		assertThat(cree.getIdProduit()).isNotNull();

		final OutputDTO relu = this.service.findById(cree.getIdProduit());

		assertThat(relu).isNotNull();
		assertThat(relu.getIdProduit()).isEqualTo(cree.getIdProduit());
		assertThat(relu.getProduit()).isEqualTo(PERCEUSE);
		assertThat(relu.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(relu.getTypeProduit()).isEqualTo(OUTIL);
		assertThat(this.service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

	} // __________________________________________________________________

	
	
	// ============================ update ================================
	
	
	
	/**
	 * <div>
	 * <p>update(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'écrit rien dans le stockage</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(null) : ExceptionParametreNull + message exact MESSAGE_PARAM_NULL + aucune écriture dans le stockage")
	public void testUpdateNull() throws Exception {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThatThrownBy(() -> this.service.update(null))
				.isInstanceOf(ExceptionParametreNull.class);

		/*
		 * Vérifie immédiatement le message porté par update(...)
		 * avant tout autre appel de service susceptible de l'écraser.
		 */
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_NULL);

		/*
		 * Prouve ensuite physiquement dans le stockage
		 * qu'aucune écriture parasite n'a eu lieu.
		 */
		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________
	


	/**
	 * <div>
	 * <p>update(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>n'écrit rien dans le stockage</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(blank) : ExceptionParametreBlank + message exact MESSAGE_PARAM_BLANK + aucune écriture dans le stockage")
	public void testUpdateBlank() throws Exception {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		final InputDTO input = new ProduitDTO.InputDTO(
				OUTIL,
				OUTILLAGE,
				ESPACES);

		assertThatThrownBy(() -> this.service.update(input))
				.isInstanceOf(ExceptionParametreBlank.class);

		/*
		 * Vérifie immédiatement le message porté par update(...)
		 * avant tout autre appel de service susceptible de l'écraser.
		 */
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);

		/*
		 * Prouve ensuite physiquement dans le stockage
		 * qu'aucune écriture parasite n'a eu lieu.
		 */
		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________
	


	/**
	 * <div>
	 * <p>update(parent blank) : violation de contrat structurel.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO}</li>
	 * <li>n'écrit rien dans le stockage</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(parent blank) : IllegalStateException + message exact MESSAGE_PAS_PARENT + aucune écriture dans le stockage")
	public void testUpdateParentBlank() throws Exception {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		final InputDTO input = new ProduitDTO.InputDTO(
				OUTIL,
				ESPACES,
				CLE_PLATE);

		assertThatThrownBy(() -> this.service.update(input))
				.isInstanceOf(IllegalStateException.class);

		/*
		 * Vérifie immédiatement le message porté par update(...)
		 * avant tout autre appel de service susceptible de l'écraser.
		 */
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

		/*
		 * Prouve ensuite physiquement dans le stockage
		 * qu'aucune écriture parasite n'a eu lieu.
		 */
		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________
	


	/**
	 * <div>
	 * <p>update(parent absent) : le parent requis n'existe pas en stockage.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO}</li>
	 * <li>n'écrit rien dans le stockage</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(parent absent) : IllegalStateException + message exact MESSAGE_PAS_PARENT + aucune écriture dans le stockage")
	public void testUpdateParentAbsent() throws Exception {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		final InputDTO input = new ProduitDTO.InputDTO(
				OUTIL,
				OUTILLAGE,
				CLE_PLATE);

		assertThatThrownBy(() -> this.service.update(input))
				.isInstanceOf(IllegalStateException.class);

		/*
		 * Vérifie immédiatement le message porté par update(...)
		 * avant tout autre appel de service susceptible de l'écraser.
		 */
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

		/*
		 * Prouve ensuite physiquement dans le stockage
		 * qu'aucune écriture parasite n'a eu lieu.
		 */
		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

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
	 * <li>ne crée aucune ligne dans le stockage</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(introuvable) : null + message exact MESSAGE_OBJ_INTROUVABLE + libellé + aucune création dans le stockage")
	public void testUpdateIntrouvable() throws Exception {

		this.creerParentsDansStockage(OUTIL, OUTILLAGE);

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		final InputDTO input = new ProduitDTO.InputDTO(
				OUTIL,
				OUTILLAGE,
				PRODUIT_MODIFICATION_ABSENT);

		final OutputDTO dto = this.service.update(input);

		/*
		 * Vérifie immédiatement le résultat observable de update(...)
		 * avant tout autre appel de service susceptible d'écraser le message.
		 */
		assertThat(dto).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(
						ProduitICuService.MESSAGE_OBJ_INTROUVABLE
								+ PRODUIT_MODIFICATION_ABSENT);

		/*
		 * Prouve ensuite physiquement dans le stockage
		 * qu'aucune création parasite n'a eu lieu.
		 */
		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________
	


	/**
	 * <div>
	 * <p>update(ok) : preuve de stockage directe avec JdbcTemplate.</p>
	 * <ul>
	 * <li>crée d'abord le parent persistant requis ;</li>
	 * <li>crée un Produit réel ;</li>
	 * <li>modifie ce même objet sans créer de doublon ;</li>
	 * <li>conserve exactement le même identifiant persistant ;</li>
	 * <li>prouve physiquement dans le stockage que la ligne existe toujours ;</li>
	 * <li>prouve physiquement que le parent du stockage reste identique ;</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_MODIF_OK} + parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("update(ok) : preuve de stockage directe + ID conservé + message exact")
	public void testUpdateOkAvecPreuveStockageEtJdbcTemplate() throws Exception {

		/* ===================== ARRANGE ===================== */
		this.creerParentsDansStockage(OUTIL, OUTILLAGE);

		final OutputDTO cree = this.service.creer(
				new ProduitDTO.InputDTO(
						OUTIL,
						OUTILLAGE,
						CLE_PLATE));

		assertThat(cree).isNotNull();
		assertThat(cree.getIdProduit()).isNotNull();

		final Long idAvantUpdate = cree.getIdProduit();

		final long totalAvantUpdate = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		/* ======================= ACT ======================= */
		final OutputDTO modifie = this.service.update(
				new ProduitDTO.InputDTO(
						OUTIL,
						OUTILLAGE,
						CLE_PLATE));

		/*
		 * Synchronise explicitement le contexte de persistance JPA
		 * avant les preuves SQL directes.
		 */
		this.entityManager.flush();

		/* ===================== ASSERT ====================== */
		assertThat(modifie).isNotNull();
		assertThat(modifie.getIdProduit()).isEqualTo(idAvantUpdate);
		assertThat(modifie.getProduit()).isEqualTo(CLE_PLATE);
		assertThat(modifie.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(modifie.getTypeProduit()).isEqualTo(OUTIL);

		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_MODIF_OK + CLE_PLATE);

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
				idAvantUpdate)).isEqualTo(CLE_PLATE);

		assertThat(this.jdbcTemplate.queryForObject(
				SELECT_PARENT_SOUS_TYPE_BY_ID_PRODUIT,
				String.class,
				idAvantUpdate)).isEqualTo(OUTILLAGE);

		final OutputDTO relu = this.service.findById(idAvantUpdate);

		assertThat(relu).isNotNull();
		assertThat(relu.getIdProduit()).isEqualTo(idAvantUpdate);
		assertThat(relu.getProduit()).isEqualTo(CLE_PLATE);
		assertThat(relu.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(relu.getTypeProduit()).isEqualTo(OUTIL);

	} // __________________________________________________________________

	
	
	// ============================ delete ================================
	
	
	
	/**
	 * <div>
	 * <p>delete(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'écrit rien dans le stockage</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("delete(null) : ExceptionParametreNull + message exact MESSAGE_PARAM_NULL + aucune écriture dans le stockage")
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
	 * <li>n'écrit rien dans le stockage</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("delete(blank) : ExceptionParametreBlank + message exact MESSAGE_PARAM_BLANK + aucune écriture dans le stockage")
	public void testDeleteBlank() {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		final InputDTO input =
				new ProduitDTO.InputDTO(
						OUTIL,
						OUTILLAGE,
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
	 * {@link ProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO}</li>
	 * <li>n'écrit rien dans le stockage</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("delete(parent blank) : IllegalStateException + message exact MESSAGE_PAS_PARENT + aucune écriture dans le stockage")
	public void testDeleteParentBlank() {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		final InputDTO input =
				new ProduitDTO.InputDTO(
						ESPACES,
						OUTILLAGE,
						BOITE_A_OUTILS);

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
	 * {@link ProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO}</li>
	 * <li>n'écrit rien dans le stockage</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("delete(parent absent) : IllegalStateException + message exact MESSAGE_PAS_PARENT + aucune écriture dans le stockage")
	public void testDeleteParentAbsent() {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThatThrownBy(() -> this.service.delete(
				new ProduitDTO.InputDTO(
						OUTIL,
						OUTILLAGE,
						BOITE_A_OUTILS)))
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
	 * <li>ne supprime rien physiquement dans le stockage</li>
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

		this.creerParentsDansStockage(OUTIL, OUTILLAGE);

		final Long nombreAvantDelete = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		this.service.delete(
				new ProduitDTO.InputDTO(
						OUTIL,
						OUTILLAGE,
						PRODUIT_SUPPRESSION_ABSENT));

		final Long nombreApresDelete = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(
						ProduitICuService.MESSAGE_OBJ_INTROUVABLE
								+ PRODUIT_SUPPRESSION_ABSENT);
		assertThat(nombreApresDelete).isEqualTo(nombreAvantDelete);
		assertThat(this.compterProduitParCoupleDansStockage(
				OUTIL,
				OUTILLAGE,
				PRODUIT_SUPPRESSION_ABSENT))
				.isEqualTo(0L);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(ok) : preuve de stockage de la destruction
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
	@DisplayName("delete(ok) : détruit le bon couple [parent, libellé] + message exact + preuve de stockage")
	public void testDeleteOkAvecPreuveCoupleParentLibelle() throws Exception {

		this.creerParentsDansStockage(OUTIL, OUTILLAGE);
		this.creerParentsDansStockage(LOISIR, ATELIER);

		final OutputDTO creeParentA = this.service.creer(
				new ProduitDTO.InputDTO(
						OUTIL,
						OUTILLAGE,
						BOITE_A_OUTILS));

		final OutputDTO creeParentB = this.service.creer(
				new ProduitDTO.InputDTO(
						LOISIR,
						ATELIER,
						BOITE_A_OUTILS));

		assertThat(creeParentA).isNotNull();
		assertThat(creeParentB).isNotNull();
		assertThat(creeParentA.getIdProduit()).isNotNull();
		assertThat(creeParentB.getIdProduit()).isNotNull();

		final Long idCibleParentB = creeParentB.getIdProduit();

		assertThat(this.compterProduitParCoupleDansStockage(
				OUTIL,
				OUTILLAGE,
				BOITE_A_OUTILS))
				.isEqualTo(1L);
		assertThat(this.compterProduitParCoupleDansStockage(
				LOISIR,
				ATELIER,
				BOITE_A_OUTILS))
				.isEqualTo(1L);

		final Long nombreAvantDelete = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		this.service.delete(
				new ProduitDTO.InputDTO(
						LOISIR,
						ATELIER,
						BOITE_A_OUTILS));

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
		 */
		this.entityManager.flush();

		final Long nombreApresDelete = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(this.service.getMessage())
				.isEqualTo(
						ProduitICuService.MESSAGE_DELETE_OK
								+ BOITE_A_OUTILS);

		assertThat(nombreApresDelete).isEqualTo(nombreAvantDelete - 1L);

		assertThat(this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS_BY_ID,
				Long.class,
				idCibleParentB)).isEqualTo(0L);

		assertThat(this.compterProduitParCoupleDansStockage(
				OUTIL,
				OUTILLAGE,
				BOITE_A_OUTILS))
				.isEqualTo(1L);

		assertThat(this.compterProduitParCoupleDansStockage(
				LOISIR,
				ATELIER,
				BOITE_A_OUTILS))
				.isEqualTo(0L);

	} // __________________________________________________________________	

	
	
	// ============================ count =================================
	
	
	
	/**
	 * <div>
	 * <p>count() : retourne le comptage réel du stockage
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

		this.creerParentsDansStockage(OUTIL, OUTILLAGE);

		this.service.creer(
				new ProduitDTO.InputDTO(
						OUTIL,
						OUTILLAGE,
						VIS));

		this.service.creer(
				new ProduitDTO.InputDTO(
						OUTIL,
						OUTILLAGE,
						ECROU));

		final long apresCreations = this.service.count();

		assertThat(apresCreations).isEqualTo(baseline + 2L);
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

		this.service.delete(
				new ProduitDTO.InputDTO(
						OUTIL,
						OUTILLAGE,
						VIS));

		this.service.delete(
				new ProduitDTO.InputDTO(
						OUTIL,
						OUTILLAGE,
						ECROU));

		/*
		 * Synchronise explicitement le contexte de persistance JPA
		 * après les suppressions demandées par le SERVICE UC.
		 */
		this.entityManager.flush();

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

	
	
	// ========================== getMessage ==============================
	
	
	
	/**
	 * <div>
	 * <p>getMessage() au démarrage :
	 * aucun message n'a encore été produit
	 * par le service d'intégration.</p>
	 * </div>
	 */
	@Test
	@DisplayName("getMessage(initial) : retourne null")
	public void testGetMessageInitialNull() {

		assertThat(this.service.getMessage()).isNull();

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>getMessage() après erreur locale bénigne :
	 * le service doit exposer exactement
	 * le message produit par {@code creer(null)}.</p>
	 * <ul>
	 * <li>{@code creer(null)} retourne {@code null}</li>
	 * <li>vérifie immédiatement
	 * le message observable exact</li>
	 * <li>prouve aussi qu'aucune écriture dans le stockage
	 * parasite n'a eu lieu</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("getMessage(après creer(null)) : retourne MESSAGE_CREER_NULL_KO + aucune écriture dans le stockage")
	public void testGetMessageApresErreurLocale() throws Exception {

		final Long nombreAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		final OutputDTO retour = this.service.creer(null);

		assertThat(retour).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_CREER_NULL_KO);

		final Long nombreApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_PRODUITS,
				Long.class);

		assertThat(nombreApres).isEqualTo(nombreAvant);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>getMessage() après succès observable :
	 * le service doit exposer exactement
	 * le message produit par {@code count()}.</p>
	 * <ul>
	 * <li>compare le retour UC
	 * au {@code COUNT(*)} physique</li>
	 * <li>vérifie le message exact :
	 * vide si zéro,
	 * succès si strictement positif</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("getMessage(après count) : retourne le message observable exact produit par count()")
	public void testGetMessageApresCount() throws Exception {

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
	 * <p>getMessage() : le dernier message gagne.</p>
	 * <ul>
	 * <li>produit d'abord un message observable
	 * via {@code count()}</li>
	 * <li>produit ensuite un message plus récent
	 * via {@code creer(null)}</li>
	 * <li>vérifie que le message final
	 * est bien le plus récent</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("getMessage(dernier message gagne) : MESSAGE_CREER_NULL_KO écrase le message précédent")
	public void testGetMessageDernierMessageGagne() throws Exception {

		final long retourUc = this.service.count();

		if (retourUc == 0L) {
			assertThat(this.service.getMessage())
					.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);
		} else {
			assertThat(this.service.getMessage())
					.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);
		}

		final OutputDTO retourCreer = this.service.creer(null);

		assertThat(retourCreer).isNull();
		assertThat(this.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_CREER_NULL_KO);

	} // __________________________________________________________________
	

	
	// *************************** METHODES UTILITAIRES ********************/

	
	
	/**
	 * <div>
	 * <p>Crée la hiérarchie de parents nécessaire aux tests de stockage Produit :</p>
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
	private void creerParentsDansStockage(final String typeProduit, final String sousTypeProduit) throws Exception {

		/*
		 * Test de stockage : on crée explicitement les parents, comme dans les tests d'intégration validés
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
	private Long compterProduitParCoupleDansStockage(
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
	 * <p>Méthode utilitaire : vérifie la cohérence d'un ResultatPage.</p>
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
