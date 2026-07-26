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
				"classpath:/truncate-test.sql", // NOPMD by danyl on 25/07/2026 09:29
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
	 * Parent IT rechercherTousString A : "IT-STP-STRING-PARENT-A".
	 */
	public static final String IT_RECHERCHER_TOUS_STRING_PARENT_A
		= "IT-STP-STRING-PARENT-A";

	/**
	 * Parent IT rechercherTousString B : "IT-STP-STRING-PARENT-B".
	 */
	public static final String IT_RECHERCHER_TOUS_STRING_PARENT_B
		= "IT-STP-STRING-PARENT-B";

	/**
	 * Libellé commun IT rechercherTousString : "IT-STP-STRING-COMMUN".
	 */
	public static final String IT_RECHERCHER_TOUS_STRING_LIBELLE_COMMUN
		= "IT-STP-STRING-COMMUN";

	/**
	 * Libellé unique IT rechercherTousString : "IT-STP-STRING-UNIQUE".
	 */
	public static final String IT_RECHERCHER_TOUS_STRING_LIBELLE_UNIQUE
		= "IT-STP-STRING-UNIQUE";

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
	 * "+ message exact + stockage inchangé"
	 */
	public static final String MESSAGE_EXACT 
		= "+ message exact + stockage inchangé";

	/**
	 * "TYPE_PRODUIT"
	 */
	public static final String TP = "TYPE_PRODUIT";
	
	/**
	 * "SOUS_TYPE_PRODUIT"
	 */
	public static final String STP = "SOUS_TYPE_PRODUIT";
	
	/**
	 * "FROM SOUS_TYPES_PRODUIT stp "
	 */
	public static final String FROM_SOUS_TYPE_PRODUIT 
		= "FROM SOUS_TYPES_PRODUIT stp ";
	
	/**
	 * "INNER JOIN TYPES_PRODUIT tp "
	 */
	public static final String INNER_JOIN_TP 
		= "INNER JOIN TYPES_PRODUIT tp ";
	
	/**
	 * "ON stp.TYPE_PRODUIT = tp.ID_TYPE_PRODUIT "
	 */
	public static final String ON_STP_TYPE_PRODUIT 
		= "ON stp.TYPE_PRODUIT = tp.ID_TYPE_PRODUIT ";
	
			
	// ************************ TAGS *************************************
	
	/**
	 * "cu-it-Creer".
	 */
	public static final String TAG_CREER = "cu-it-Creer";
	
	/**
	 * "cu-it-RechercherTous".
	 */
	public static final String TAG_RECHERCHER_TOUS
		= "cu-it-RechercherTous";

	/**
	 * "cu-it-RechercherTousString".
	 */
	public static final String TAG_RECHERCHER_TOUS_STRING
		= "cu-it-RechercherTousString";

	/**
	 * "cu-it-RechercherTousParPage".
	 */
	public static final String TAG_RECHERCHER_TOUS_PAR_PAGE
		= "cu-it-RechercherTousParPage";

	/**
	 * "cu-it-FindByLibelleRapide".
	 */
	public static final String TAG_FIND_BY_LIBELLE_RAPIDE
		= "cu-it-FindByLibelleRapide";
	
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
				+ MESSAGE_EXACT;

	/**
	 * "creer(parent blank) : IllegalStateException + message exact + stockage inchangé".
	 */
	public static final String DN_CREER_PARENT_BLANK
		= "creer(parent blank) : IllegalStateException "
				+ MESSAGE_EXACT;

	/**
	 * "creer(parent absent) : IllegalStateException + message exact + stockage inchangé".
	 */
	public static final String DN_CREER_PARENT_ABSENT
		= "creer(parent absent) : IllegalStateException "
				+ MESSAGE_EXACT;

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
	 * "rechercherTous(vide) : liste vide + MESSAGE_RECHERCHER_TOUS_VIDE + stockage vide".
	 */
	public static final String DN_RECHERCHER_TOUS_VIDE
		= "rechercherTous(vide) : liste vide "
				+ "+ MESSAGE_RECHERCHER_TOUS_VIDE + stockage vide";

	/**
	 * "rechercherTous(ok) : MESSAGE_RECHERCHER_TOUS_OK + créations présentes dans le stockage".
	 */
	public static final String DN_RECHERCHER_TOUS_NOMINAL
		= "rechercherTous(ok) : MESSAGE_RECHERCHER_TOUS_OK "
				+ "+ créations présentes dans le stockage";

	/**
	 * "rechercherTousString(vide) : liste vide + MESSAGE_RECHERCHE_VIDE + stockage vide".
	 */
	public static final String DN_RECHERCHER_TOUS_STRING_VIDE
		= "rechercherTousString(vide) : liste vide "
				+ "+ MESSAGE_RECHERCHE_VIDE + stockage vide";

	/**
	 * "rechercherTousString(ok) : MESSAGE_RECHERCHE_OK + libellés exacts du stockage + stockage inchangé".
	 */
	public static final String DN_RECHERCHER_TOUS_STRING_NOMINAL
		= "rechercherTousString(ok) : MESSAGE_RECHERCHE_OK "
				+ "+ libellés exacts du stockage + stockage inchangé";

	/**
	 * "rechercherTousParPage(null) : IllegalStateException
	 * + MESSAGE_PAGEABLE_NULL + stockage inchangé".
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_NULL
		= "rechercherTousParPage(null) : IllegalStateException "
				+ "+ MESSAGE_PAGEABLE_NULL + stockage inchangé";

	/**
	 * "rechercherTousParPage(stockage vide) : page vide
	 * + MESSAGE_RECHERCHE_PAGINEE_OK + stockage inchangé".
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_VIDE
		= "rechercherTousParPage(stockage vide) : page vide "
				+ "+ MESSAGE_RECHERCHE_PAGINEE_OK + stockage inchangé";

	/**
	 * "rechercherTousParPage(ok) : page DTO cohérente
	 * + message exact + stockage inchangé".
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_NOMINAL
		= "rechercherTousParPage(ok) : page DTO cohérente "
				+ MESSAGE_EXACT;
	
	// ---------------------- findByLibelleRapide(...) --------------------

	/**
	 * "findByLibelleRapide(null) : IllegalStateException
	 * + MESSAGE_PARAM_NULL + stockage inchangé".
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_NULL
		= "findByLibelleRapide(null) : IllegalStateException "
				+ "+ MESSAGE_PARAM_NULL + stockage inchangé";

	/**
	 * "findByLibelleRapide(blank) : résultat rechercherTous()
	 * + MESSAGE_RECHERCHER_TOUS_OK + stockage inchangé".
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_BLANK
		= "findByLibelleRapide(blank) : résultat rechercherTous() "
				+ "+ MESSAGE_RECHERCHER_TOUS_OK + stockage inchangé";

	/**
	 * "findByLibelleRapide(introuvable) : liste vide
	 * + MESSAGE_RECHERCHE_VIDE + stockage inchangé".
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_INTROUVABLE
		= "findByLibelleRapide(introuvable) : liste vide "
				+ "+ MESSAGE_RECHERCHE_VIDE + stockage inchangé";

	/**
	 * "findByLibelleRapide(nominal) : DTO exacts triés sans doublon
	 * + MESSAGE_RECHERCHE_OK + preuve stockage".
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_NOMINAL
		= "findByLibelleRapide(nominal) : DTO exacts triés sans doublon "
				+ "+ MESSAGE_RECHERCHE_OK + preuve stockage";

	/**
	 * "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT".
	 */
	public static final String SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT
		= "SELECT COUNT(*) FROM SOUS_TYPES_PRODUIT";

	/**
	 * Sélectionne les libellés des SousTypeProduit dans l'ordre métier
	 * [TypeProduit, SousTypeProduit].
	 */
	public static final String SELECT_LIBELLES_SOUS_TYPES_PRODUIT_ORDONNES
		= "SELECT stp.SOUS_TYPE_PRODUIT "
				+ FROM_SOUS_TYPE_PRODUIT
				+ INNER_JOIN_TP
				+ ON_STP_TYPE_PRODUIT
				+ "ORDER BY LOWER(tp.TYPE_PRODUIT), "
				+ "LOWER(stp.SOUS_TYPE_PRODUIT)";

	/**
	 * Sélectionne les couples [TypeProduit, SousTypeProduit]
	 * dans l'ordre métier.
	 */
	public static final String SELECT_COUPLES_SOUS_TYPES_PRODUIT_ORDONNES
		= "SELECT tp.TYPE_PRODUIT, stp.SOUS_TYPE_PRODUIT "
				+ FROM_SOUS_TYPE_PRODUIT
				+ INNER_JOIN_TP
				+ ON_STP_TYPE_PRODUIT
				+ "ORDER BY LOWER(tp.TYPE_PRODUIT), "
				+ "LOWER(stp.SOUS_TYPE_PRODUIT)";

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
		 * compte d'abord (en SQL)
		 * le nombre d'enregistrements dans le stockage
		 * avant l'appel au SERVICE UC,
		 * afin de pouvoir prouver ensuite
		 * qu'aucune écriture réelle n'a eu lieu dans le stockage.
		 */
		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_NULL_KO);

		/* ASSERT :
		 * compte ensuite (en SQL)
		 * le nombre d'enregistrements dans le stockage
		 * après service.creer(null)
		 * afin de prouver que l'appel au SERVICE UC
		 * n'a produit aucune écriture dans le stockage.
		 */
		final Long countApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
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
		 * compte d'abord (en SQL)
		 * le nombre d'enregistrements dans le stockage
		 * avant l'appel au SERVICE UC,
		 * afin de pouvoir prouver ensuite
		 * qu'aucune écriture réelle n'a eu lieu dans le stockage.
		 */
		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvant).isNotNull();

		/* prépare un InputDTO
		 * dont le libellé métier est blank. */
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
		
		/* Garantit le message utilisateur MESSAGE_CREER_LIBELLE_BLANK_KO
		 * (message contractuel attendu).
		 */
		assertThat(this.service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService
								.MESSAGE_CREER_LIBELLE_BLANK_KO);

		/* ASSERT :
		 * compte ensuite (en SQL)
		 * le nombre d'enregistrements dans le stockage
		 * après l'échec contractuel
		 * afin de prouver que l'appel au SERVICE UC
		 * n'a produit aucune écriture dans le stockage.
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
		 * compte d'abord (en SQL)
		 * le nombre d'enregistrements dans le stockage
		 * avant l'appel au SERVICE UC,
		 * afin de pouvoir prouver ensuite
		 * qu'aucune écriture réelle n'a eu lieu dans le stockage.
		 */
		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvant).isNotNull();

		/* prépare un InputDTO
		 * dont le libellé parent est blank. */
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
		
		/* Garantit le message utilisateur MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO
		 * (message contractuel attendu).
		 */
		assertThat(this.service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService
								.MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO);

		/* ASSERT :
		 * compte ensuite (en SQL)
		 * le nombre d'enregistrements dans le stockage
		 * après l'échec contractuel
		 * afin de prouver que l'appel au SERVICE UC
		 * n'a produit aucune écriture dans le stockage.
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
		 * compte d'abord (en SQL)
		 * le nombre d'enregistrements dans le stockage
		 * avant l'appel au SERVICE UC,
		 * afin de pouvoir prouver ensuite
		 * qu'aucune écriture réelle n'a eu lieu dans le stockage.
		 */
		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvant).isNotNull();

		/* prépare un InputDTO valide
		 * dont le parent n'a pas été créé dans le stockage. */
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
		
		/* Garantit le message utilisateur MESSAGE_CREER_PARENT_NON_PERSISTANT_KO
		 * (message contractuel attendu).
		 */
		assertThat(this.service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService
								.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		/* ASSERT :
		 * compte ensuite (en SQL)
		 * le nombre d'enregistrements dans le stockage
		 * après l'échec contractuel
		 * afin de prouver que l'appel au SERVICE UC
		 * n'a produit aucune écriture dans le stockage.
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
	 * avec un objet métier déjà présent dans le stockage sous le même parent :</p>
	 * <ul>
	 * <li>la première création réussit réellement ;</li>
	 * <li>la seconde création lève une {@link ExceptionDoublon} ;</li>
	 * <li>le message utilisateur exact est
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_DOUBLON_KO} + libellé ;</li>
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
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));

		/* prépare un DTO valide non seedé.
		 *
		 * Le premier appel à creer(...) créera réellement l'objet métier.
		 * Le second appel avec le même DTO déclenchera ensuite
		 * le cas contractuel de doublon.
		 */
		final InputDTO input = new SousTypeProduitDTO.InputDTO(
				OUTIL,
				TOURNEVIS);

		/* Vérifie d'abord que l'objet métier du test
		 * n'est pas déjà présent dans le stockage sous ce parent.
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
		 * qu'une seule ligne porte l'objet métier créé sous ce parent.
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
		 * que la colonne SOUS_TYPE_PRODUIT a bien été écrite
		 * avec le libellé métier attendu.
		 */
		assertThat(this.lireLibelleSousTypeProduitDansStockage(
				cree.getIdSousTypeProduit()))
				.isEqualTo(TOURNEVIS);

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
		 * avec le même objet métier déjà présent sous le même parent.
		 *
		 * Le SERVICE UC doit refuser le doublon avant toute nouvelle
		 * écriture dans le stockage.
		 */
		assertThatThrownBy(() -> this.service.creer(input))
				.isInstanceOf(ExceptionDoublon.class)
				.hasMessage(
						SousTypeProduitICuService.MESSAGE_CREER_DOUBLON_KO
								+ TOURNEVIS);

		/* Garantit le message utilisateur exact. */
		assertThat(this.service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_CREER_DOUBLON_KO
								+ TOURNEVIS);

		/* ASSERT :
		 * contrôle ensuite par SQL direct
		 * que le stockage contient toujours une seule ligne
		 * pour cet objet métier sous ce parent.
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
	 * <li>crée d'abord le parent persistant requis ;</li>
	 * <li>crée réellement une ligne dans le stockage ;</li>
	 * <li>retourne un {@link OutputDTO} persistant ;</li>
	 * <li>émet un message
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_OK}</li>
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
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));

		/* prépare un DTO valide à créer
		 * et mémorise le nombre de lignes avant création.
		 */
		final InputDTO input = new SousTypeProduitDTO.InputDTO(
				OUTIL,
				MARTEAU);

		/* Vérifie d'abord que l'objet métier du test
		 * n'est pas déjà présent dans le stockage sous ce parent.
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
		 * qu'une seule ligne porte l'objet métier créé sous ce parent.
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
		final OutputDTO trouveParId 
			= this.service.findById(cree.getIdSousTypeProduit());

		assertThat(trouveParId).isNotNull();
		assertThat(trouveParId.getIdSousTypeProduit())
				.isEqualTo(cree.getIdSousTypeProduit());
		assertThat(trouveParId.getSousTypeProduit()).isEqualTo(MARTEAU);
		assertThat(trouveParId.getTypeProduit()).isEqualTo(OUTIL);
		
	} // __________________________________________________________________
    
    
    
    // ======================== RechercherTous ============================
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTous() avec un stockage vide :</p>
	 * <ul>
	 * <li>retourne une liste non {@code null} ;</li>
	 * <li>retourne une liste vide ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHER_TOUS_VIDE} ;</li>
	 * <li>ne crée aucune ligne dans le stockage.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS)
	@Sql(
			scripts = "classpath:/truncate-test.sql",
			executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@DisplayName(DN_RECHERCHER_TOUS_VIDE)
	@Test
	public void testRechercherTousVide() throws Exception {

		/* ARRANGE :
		 * contrôle d'abord que le stockage ne contient aucun SousTypeProduit.
		 */
		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvant).isNotNull();
		assertThat(countAvant).isEqualTo(0L);

		/* ACT :
		 * exécute la recherche exhaustive via le SERVICE UC.
		 */
		final List<OutputDTO> dtos = this.service.rechercherTous();
		final String message = this.service.getMessage();

		/* ASSERT :
		 * garantit que rechercherTous() retourne une liste non null et vide.
		 */
		assertThat(dtos).isNotNull();
		assertThat(dtos).isEmpty();

		/* Garantit que le message utilisateur est celui
		 * de la branche rechercherTous() vide.
		 */
		assertThat(message)
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_RECHERCHER_TOUS_VIDE);

		/* Garantit que l'appel n'a rien écrit dans le stockage. */
		final Long countApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countApres).isNotNull();
		assertThat(countApres).isEqualTo(0L);
		
	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTous(OK) :</p>
	 * <ul>
	 * <li>retourne une liste non {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHER_TOUS_OK} ;</li>
	 * <li>contient les créations réalisées par le test ;</li>
	 * <li>retourne pour ces créations les identifiants persistants
	 * effectivement écrits dans le stockage ;</li>
	 * <li>reste aligné avec le nombre de lignes présentes
	 * dans le stockage.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DN_RECHERCHER_TOUS_NOMINAL)
	@Test
	public void testRechercherTousNominalAvecPreuveStockage()
			throws Exception {

		/* ARRANGE :
		 * prépare deux SousTypeProduit non seedés avec deux parents
		 * et mémorise le volume du stockage avant création.
		 */
		final TypeProduitDTO.InputDTO inputParentOutil
			= new TypeProduitDTO.InputDTO(OUTIL);
		final TypeProduitDTO.InputDTO inputParentLoisir
			= new TypeProduitDTO.InputDTO(LOISIR);
		final InputDTO inputPerceuse
			= new SousTypeProduitDTO.InputDTO(OUTIL, PERCEUSE);
		final InputDTO inputPince
			= new SousTypeProduitDTO.InputDTO(LOISIR, PINCE);

		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				PERCEUSE))
				.isEqualTo(0L);

		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				LOISIR,
				PINCE))
				.isEqualTo(0L);

		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvant).isNotNull();

		/* ACT :
		 * crée réellement les deux parents puis les deux SousTypeProduit
		 * dans le stockage.
		 */
		this.typeProduitService.creer(inputParentOutil);
		this.typeProduitService.creer(inputParentLoisir);

		final OutputDTO creePerceuse = this.service.creer(inputPerceuse);
		final OutputDTO creePince = this.service.creer(inputPince);

		/*
		 * Synchronise explicitement le contexte de persistance JPA
		 * avant les preuves SQL directes.
		 */
		this.entityManager.flush();

		/* ASSERT :
		 * garantit que les deux créations retournent des DTO persistants
		 * rattachés aux parents attendus.
		 */
		assertThat(creePerceuse).isNotNull();
		assertThat(creePerceuse.getIdSousTypeProduit()).isNotNull();
		assertThat(creePerceuse.getTypeProduit()).isEqualTo(OUTIL);
		assertThat(creePerceuse.getSousTypeProduit()).isEqualTo(PERCEUSE);

		assertThat(creePince).isNotNull();
		assertThat(creePince.getIdSousTypeProduit()).isNotNull();
		assertThat(creePince.getTypeProduit()).isEqualTo(LOISIR);
		assertThat(creePince.getSousTypeProduit()).isEqualTo(PINCE);

		/* Garantit physiquement dans le stockage
		 * que les deux créations existent avant la recherche exhaustive.
		 */
		assertThat(this.compterSousTypeProduitDansStockage(
				creePerceuse.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(
				creePerceuse.getIdSousTypeProduit()))
				.isEqualTo(PERCEUSE);
		assertThat(this.lireParentSousTypeProduitDansStockage(
				creePerceuse.getIdSousTypeProduit()))
				.isEqualTo(OUTIL);

		assertThat(this.compterSousTypeProduitDansStockage(
				creePince.getIdSousTypeProduit()))
				.isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(
				creePince.getIdSousTypeProduit()))
				.isEqualTo(PINCE);
		assertThat(this.lireParentSousTypeProduitDansStockage(
				creePince.getIdSousTypeProduit()))
				.isEqualTo(LOISIR);

		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				PERCEUSE))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				LOISIR,
				PINCE))
				.isEqualTo(1L);

		final Long countApresCreation = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countApresCreation).isNotNull();
		assertThat(countApresCreation).isEqualTo(countAvant + 2L);

		/* ACT :
		 * exécute la recherche exhaustive via le SERVICE UC.
		 */
		final List<OutputDTO> dtos = this.service.rechercherTous();
		final String message = this.service.getMessage();

		/* ASSERT :
		 * garantit que rechercherTous() retourne une liste non null
		 * et positionne le message dédié à rechercherTous().
		 */
		assertThat(dtos).isNotNull();
		assertThat(message)
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_RECHERCHER_TOUS_OK);

		/* Garantit que la taille de la liste retournée correspond
		 * au nombre de lignes présentes dans le stockage.
		 */
		assertThat(dtos.size()).isEqualTo(countApresCreation.intValue());

		/* Garantit que les deux créations du test sont présentes
		 * dans la réponse de rechercherTous().
		 */
		final OutputDTO dtoPerceuse = dtos.stream()
				.filter(dto -> OUTIL.equals(dto.getTypeProduit()))
				.filter(dto -> PERCEUSE.equals(dto.getSousTypeProduit()))
				.findFirst()
				.orElse(null);

		final OutputDTO dtoPince = dtos.stream()
				.filter(dto -> LOISIR.equals(dto.getTypeProduit()))
				.filter(dto -> PINCE.equals(dto.getSousTypeProduit()))
				.findFirst()
				.orElse(null);

		assertThat(dtoPerceuse).isNotNull();
		assertThat(dtoPerceuse.getIdSousTypeProduit())
				.isEqualTo(creePerceuse.getIdSousTypeProduit());
		assertThat(dtoPerceuse.getTypeProduit()).isEqualTo(OUTIL);
		assertThat(dtoPerceuse.getSousTypeProduit()).isEqualTo(PERCEUSE);

		assertThat(dtoPince).isNotNull();
		assertThat(dtoPince.getIdSousTypeProduit())
				.isEqualTo(creePince.getIdSousTypeProduit());
		assertThat(dtoPince.getTypeProduit()).isEqualTo(LOISIR);
		assertThat(dtoPince.getSousTypeProduit()).isEqualTo(PINCE);

		/* Garantit que rechercherTous() n'a modifié aucune ligne
		 * et que les deux couples restent présents dans le stockage.
		 */
		final Long countApresRecherche = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countApresRecherche).isNotNull();
		assertThat(countApresRecherche).isEqualTo(countApresCreation);

		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				PERCEUSE))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				LOISIR,
				PINCE))
				.isEqualTo(1L);
		
	} // __________________________________________________________________

	

    // ===================== rechercherTousString =========================



	/**
	 * <div>
	 * <p>garantit que rechercherTousString(vide) :</p>
	 * <ul>
	 * <li>retourne une liste vide mais non {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE} ;</li>
	 * <li>ne crée aucune ligne dans le stockage.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@Sql(
			scripts = "classpath:/truncate-test.sql",
			executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@DisplayName(DN_RECHERCHER_TOUS_STRING_VIDE)
	@Test
	public void testRechercherTousStringVide() throws Exception {

		/* ARRANGE :
		 * contrôle d'abord que le stockage ne contient
		 * aucun SousTypeProduit.
		 */
		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvant).isNotNull();
		assertThat(countAvant).isEqualTo(0L);

		/* ACT :
		 * exécute la recherche exhaustive String via le SERVICE UC.
		 */
		final List<String> libelles = this.service.rechercherTousString();
		final String message = this.service.getMessage();

		/* ASSERT :
		 * garantit que rechercherTousString() retourne
		 * une liste non null et vide.
		 */
		assertThat(libelles).isNotNull();
		assertThat(libelles).isEmpty();

		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* Garantit que l'appel n'a rien écrit dans le stockage. */
		final Long countApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countApres).isNotNull();
		assertThat(countApres).isEqualTo(0L);
		assertThat(countApres).isEqualTo(countAvant);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTousString(OK) :</p>
	 * <ul>
	 * <li>retourne une liste non {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK} ;</li>
	 * <li>retourne exactement les libellés présents dans le stockage,
	 * ordonnés selon l'ordre métier [TypeProduit, SousTypeProduit],
	 * puis dédoublonnés côté String ;</li>
	 * <li>conserve dans le stockage deux SousTypeProduit homonymes
	 * rattachés à deux TypeProduit distincts ;</li>
	 * <li>n'expose qu'une fois leur libellé commun dans la réponse String ;</li>
	 * <li>ne modifie aucune ligne dans le stockage.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DN_RECHERCHER_TOUS_STRING_NOMINAL)
	@Test
	public void testRechercherTousStringNominalAvecPreuveStockage()
			throws Exception {

		/* ARRANGE :
		 * crée deux parents propres au scénario afin de prouver
		 * l'identité [TypeProduit, SousTypeProduit].
		 */
		final TypeProduitDTO.OutputDTO parentA = this.typeProduitService.creer(
				new TypeProduitDTO.InputDTO(
						IT_RECHERCHER_TOUS_STRING_PARENT_A));
		final TypeProduitDTO.OutputDTO parentB = this.typeProduitService.creer(
				new TypeProduitDTO.InputDTO(
						IT_RECHERCHER_TOUS_STRING_PARENT_B));

		assertThat(parentA).isNotNull();
		assertThat(parentA.getIdTypeProduit()).isNotNull();
		assertThat(parentA.getTypeProduit())
				.isEqualTo(IT_RECHERCHER_TOUS_STRING_PARENT_A);

		assertThat(parentB).isNotNull();
		assertThat(parentB.getIdTypeProduit()).isNotNull();
		assertThat(parentB.getTypeProduit())
				.isEqualTo(IT_RECHERCHER_TOUS_STRING_PARENT_B);

		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				IT_RECHERCHER_TOUS_STRING_PARENT_A,
				IT_RECHERCHER_TOUS_STRING_LIBELLE_COMMUN))
				.isEqualTo(0L);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				IT_RECHERCHER_TOUS_STRING_PARENT_A,
				IT_RECHERCHER_TOUS_STRING_LIBELLE_UNIQUE))
				.isEqualTo(0L);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				IT_RECHERCHER_TOUS_STRING_PARENT_B,
				IT_RECHERCHER_TOUS_STRING_LIBELLE_COMMUN))
				.isEqualTo(0L);

		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvant).isNotNull();

		/* Crée deux homonymes sous deux parents distincts
		 * et un libellé supplémentaire sous le premier parent.
		 */
		final OutputDTO communParentA = this.service.creer(
				new SousTypeProduitDTO.InputDTO(
						IT_RECHERCHER_TOUS_STRING_PARENT_A,
						IT_RECHERCHER_TOUS_STRING_LIBELLE_COMMUN));
		final OutputDTO uniqueParentA = this.service.creer(
				new SousTypeProduitDTO.InputDTO(
						IT_RECHERCHER_TOUS_STRING_PARENT_A,
						IT_RECHERCHER_TOUS_STRING_LIBELLE_UNIQUE));
		final OutputDTO communParentB = this.service.creer(
				new SousTypeProduitDTO.InputDTO(
						IT_RECHERCHER_TOUS_STRING_PARENT_B,
						IT_RECHERCHER_TOUS_STRING_LIBELLE_COMMUN));

		this.entityManager.flush();

		assertThat(communParentA).isNotNull();
		assertThat(communParentA.getIdSousTypeProduit()).isNotNull();
		assertThat(communParentA.getTypeProduit())
				.isEqualTo(IT_RECHERCHER_TOUS_STRING_PARENT_A);
		assertThat(communParentA.getSousTypeProduit())
				.isEqualTo(IT_RECHERCHER_TOUS_STRING_LIBELLE_COMMUN);

		assertThat(uniqueParentA).isNotNull();
		assertThat(uniqueParentA.getIdSousTypeProduit()).isNotNull();
		assertThat(uniqueParentA.getTypeProduit())
				.isEqualTo(IT_RECHERCHER_TOUS_STRING_PARENT_A);
		assertThat(uniqueParentA.getSousTypeProduit())
				.isEqualTo(IT_RECHERCHER_TOUS_STRING_LIBELLE_UNIQUE);

		assertThat(communParentB).isNotNull();
		assertThat(communParentB.getIdSousTypeProduit()).isNotNull();
		assertThat(communParentB.getTypeProduit())
				.isEqualTo(IT_RECHERCHER_TOUS_STRING_PARENT_B);
		assertThat(communParentB.getSousTypeProduit())
				.isEqualTo(IT_RECHERCHER_TOUS_STRING_LIBELLE_COMMUN);

		/* Prouve directement que les trois identités existent
		 * dans le stockage avant la recherche.
		 */
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				IT_RECHERCHER_TOUS_STRING_PARENT_A,
				IT_RECHERCHER_TOUS_STRING_LIBELLE_COMMUN))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				IT_RECHERCHER_TOUS_STRING_PARENT_A,
				IT_RECHERCHER_TOUS_STRING_LIBELLE_UNIQUE))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				IT_RECHERCHER_TOUS_STRING_PARENT_B,
				IT_RECHERCHER_TOUS_STRING_LIBELLE_COMMUN))
				.isEqualTo(1L);

		final Long countAvantRecherche = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvantRecherche).isNotNull();
		assertThat(countAvantRecherche).isEqualTo(countAvant + 3L);

		/* Construit l'oracle directement depuis le stockage :
		 * ordre [TypeProduit, SousTypeProduit], filtrage blank,
		 * puis dédoublonnage String en conservant l'ordre.
		 */
		final List<String> libellesStockesAvantRecherche
				= this.jdbcTemplate.queryForList(
						SELECT_LIBELLES_SOUS_TYPES_PRODUIT_ORDONNES,
						String.class);

		final List<String> libellesAttendus
				= libellesStockesAvantRecherche.stream()
						.filter(libelle -> libelle != null && !libelle.isBlank())
						.distinct()
						.toList();

		/* ACT : exécute la recherche exhaustive String. */
		final List<String> libelles = this.service.rechercherTousString();
		final String message = this.service.getMessage();

		/* ASSERT : compare exactement la réponse avec l'oracle stockage. */
		assertThat(libelles).isNotNull();
		assertThat(libelles).containsExactlyElementsOf(libellesAttendus);
		assertThat(libelles)
				.contains(
						IT_RECHERCHER_TOUS_STRING_LIBELLE_COMMUN,
						IT_RECHERCHER_TOUS_STRING_LIBELLE_UNIQUE);
		assertThat(libelles).doesNotHaveDuplicates();
		assertThat(libelles.stream()
				.filter(IT_RECHERCHER_TOUS_STRING_LIBELLE_COMMUN::equals)
				.count())
				.isEqualTo(1L);
		assertThat(libelles)
				.allMatch(libelle -> libelle != null && !libelle.isBlank());
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* Prouve que la lecture n'a modifié ni le volume,
		 * ni les valeurs, ni les identités présentes dans le stockage.
		 */
		final Long countApresRecherche = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countApresRecherche).isNotNull();
		assertThat(countApresRecherche).isEqualTo(countAvantRecherche);

		final List<String> libellesStockesApresRecherche
				= this.jdbcTemplate.queryForList(
						SELECT_LIBELLES_SOUS_TYPES_PRODUIT_ORDONNES,
						String.class);

		assertThat(libellesStockesApresRecherche)
				.containsExactlyElementsOf(libellesStockesAvantRecherche);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				IT_RECHERCHER_TOUS_STRING_PARENT_A,
				IT_RECHERCHER_TOUS_STRING_LIBELLE_COMMUN))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				IT_RECHERCHER_TOUS_STRING_PARENT_A,
				IT_RECHERCHER_TOUS_STRING_LIBELLE_UNIQUE))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				IT_RECHERCHER_TOUS_STRING_PARENT_B,
				IT_RECHERCHER_TOUS_STRING_LIBELLE_COMMUN))
				.isEqualTo(1L);

	} // __________________________________________________________________



    // ================== rechercherTousParPage ===========================
    
    

	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(null) :</p>
	 * <ul>
	 * <li>lève une {@link IllegalStateException} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAGEABLE_NULL} ;</li>
	 * <li>ne modifie aucune ligne dans le stockage.</li>
	 * </ul>
	 * </div>
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_NULL)
	@Test
	public void testRechercherTousParPageNull() {

		/* ARRANGE :
		 * mémorise le volume du stockage avant l'appel invalide.
		 */
		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvant).isNotNull();

		/* ACT - ASSERT :
		 * Garantit que rechercherTousParPage(null)
		 * - jette une IllegalStateException
		 * - avec le message MESSAGE_PAGEABLE_NULL.
		 */
		assertThatThrownBy(() -> this.service.rechercherTousParPage(null))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_PAGEABLE_NULL);

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAGEABLE_NULL);

		/* Garantit que l'appel invalide n'a pas modifié le stockage. */
		final Long countApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countApres).isNotNull();
		assertThat(countApres).isEqualTo(countAvant);
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(stockage vide) :</p>
	 * <ul>
	 * <li>retourne un {@link ResultatPage} non {@code null} ;</li>
	 * <li>retourne un contenu DTO vide mais non {@code null} ;</li>
	 * <li>retourne un total d'éléments égal à zéro ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_PAGINEE_OK} ;</li>
	 * <li>ne crée aucune ligne dans le stockage.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@Sql(
			scripts = "classpath:/truncate-test.sql",
			executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_VIDE)
	@Test
	public void testRechercherTousParPageVide() throws Exception {

		/* ARRANGE :
		 * contrôle que le stockage ne contient aucun SousTypeProduit.
		 */
		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvant).isNotNull();
		assertThat(countAvant).isEqualTo(0L);

		final RequetePage requete = new RequetePage(0, 20);

		/* ACT :
		 * exécute la recherche paginée via le SERVICE UC.
		 */
		final ResultatPage<OutputDTO> resultat
				= this.service.rechercherTousParPage(requete);
		final String message = this.service.getMessage();

		/* ASSERT :
		 * garantit que la page DTO vide reprend la pagination demandée.
		 */
		assertThat(resultat).isNotNull();
		assertThat(resultat.getPageNumber()).isEqualTo(0);
		assertThat(resultat.getPageSize()).isEqualTo(20);
		assertThat(resultat.getTotalElements()).isEqualTo(0L);
		assertThat(resultat.getContent()).isNotNull();
		assertThat(resultat.getContent()).isEmpty();

		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

		/* Garantit que la lecture paginée n'a rien écrit dans le stockage. */
		final Long countApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countApres).isNotNull();
		assertThat(countApres).isEqualTo(0L);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(OK) :</p>
	 * <ul>
	 * <li>retourne un {@link ResultatPage} non {@code null} ;</li>
	 * <li>reprend le numéro de page, la taille de page
	 * et le total d'éléments du stockage ;</li>
	 * <li>retourne exactement les couples
	 * [TypeProduit, SousTypeProduit] présents dans le stockage,
	 * triés selon l'ordre métier et sans doublon ;</li>
	 * <li>retourne les identifiants persistants
	 * des cinq créations réalisées par le test ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_PAGINEE_OK} ;</li>
	 * <li>ne modifie aucune ligne dans le stockage.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_NOMINAL)
	@Test
	public void testRechercherTousParPageNominalAvecPreuveStockage()
			throws Exception {

		/* ARRANGE :
		 * crée un parent persistant puis cinq SousTypeProduit non seedés.
		 */
		final TypeProduitDTO.OutputDTO parentCree
				= this.typeProduitService.creer(
						new TypeProduitDTO.InputDTO(OUTIL));

		assertThat(parentCree).isNotNull();
		assertThat(parentCree.getIdTypeProduit()).isNotNull();

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

		/*
		 * Synchronise le contexte de persistance JPA
		 * avant les preuves SQL directes.
		 */
		this.entityManager.flush();

		assertThat(cree01).isNotNull();
		assertThat(cree02).isNotNull();
		assertThat(cree03).isNotNull();
		assertThat(cree04).isNotNull();
		assertThat(cree05).isNotNull();

		assertThat(cree01.getIdSousTypeProduit()).isNotNull();
		assertThat(cree02.getIdSousTypeProduit()).isNotNull();
		assertThat(cree03.getIdSousTypeProduit()).isNotNull();
		assertThat(cree04.getIdSousTypeProduit()).isNotNull();
		assertThat(cree05.getIdSousTypeProduit()).isNotNull();

		final Long countAvantRecherche = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvantRecherche).isNotNull();

		final List<String> couplesStockesAvantRecherche
				= this.jdbcTemplate.query(
						SELECT_COUPLES_SOUS_TYPES_PRODUIT_ORDONNES,
						(resultSet, rowNumber) ->
								resultSet.getString(TP)
								+ "|"
								+ resultSet.getString(STP));

		final RequetePage requete = new RequetePage(0, 100);

		/* ACT :
		 * exécute la recherche paginée via le SERVICE UC.
		 */
		final ResultatPage<OutputDTO> resultat
				= this.service.rechercherTousParPage(requete);
		final String message = this.service.getMessage();

		/* ASSERT :
		 * garantit que la page DTO correspond à l'état du stockage
		 * observé avant la lecture paginée.
		 */
		assertThat(resultat).isNotNull();
		assertThat(resultat.getPageNumber()).isEqualTo(0);
		assertThat(resultat.getPageSize()).isEqualTo(100);
		assertThat(resultat.getTotalElements()).isEqualTo(countAvantRecherche);
		assertThat(resultat.getContent()).isNotNull();

		assertThat(resultat.getContent())
				.extracting(dto ->
						dto.getTypeProduit()
						+ "|"
						+ dto.getSousTypeProduit())
				.containsExactlyElementsOf(couplesStockesAvantRecherche);

		assertThat(resultat.getContent())
				.extracting(OutputDTO::getIdSousTypeProduit)
				.contains(
						cree01.getIdSousTypeProduit(),
						cree02.getIdSousTypeProduit(),
						cree03.getIdSousTypeProduit(),
						cree04.getIdSousTypeProduit(),
						cree05.getIdSousTypeProduit());

		assertThat(resultat.getContent()).doesNotHaveDuplicates();

		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

		/* Garantit directement dans le stockage
		 * que les cinq couples créés existent après la lecture paginée.
		 */
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				RABOTEUSE))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				COUTEAU))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				CISEAU))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				BURIN))
				.isEqualTo(1L);
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				MAILLET))
				.isEqualTo(1L);

		/* Garantit que la lecture paginée n'a pas modifié le stockage. */
		final Long countApresRecherche = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countApresRecherche).isNotNull();
		assertThat(countApresRecherche).isEqualTo(countAvantRecherche);

		final List<String> couplesStockesApresRecherche
				= this.jdbcTemplate.query(
						SELECT_COUPLES_SOUS_TYPES_PRODUIT_ORDONNES,
						(resultSet, rowNumber) ->
								resultSet.getString(TP)
								+ "|"
								+ resultSet.getString(STP));

		assertThat(couplesStockesApresRecherche)
				.containsExactlyElementsOf(couplesStockesAvantRecherche);

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

		/* findByLibelle(ESPACES). */
		final List<OutputDTO> dtos = this.service.findByLibelle(ESPACES);

		/* Garantit que findByLibelle(ESPACES) retourne vide. */
		assertThat(dtos).isNotNull().isEmpty();
		/* Garantit que findByLibelle(ESPACES) positionne 
		 * un message MESSAGE_PARAM_BLANK. */
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

		/* findByLibelle(INTROUVABLE). */
		final List<OutputDTO> dtos = this.service.findByLibelle(LIBELLE_INCONNU);

		/* Garantit que findByLibelle(INTROUVABLE) retourne vide. */
		assertThat(dtos).isNotNull().isEmpty();
		/* Garantit que findByLibelle(INTROUVABLE) positionne un message 
		 * MESSAGE_OBJ_INTROUVABLE + LIBELLE_INCONNU. */
		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + LIBELLE_INCONNU);
		/* Garantit que INTROUVABLE ne figure pas dans le stockage. */
		assertThat(this.compterSousTypeProduitParLibelleDansStockage(LIBELLE_INCONNU))
				.isEqualTo(0L);

	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>
	 * Vérifie que {@code findByLibelle(...)} retourne tous les
	 * {@link SousTypeProduitDTO.OutputDTO} portant le libellé demandé,
	 * même lorsque ces objets sont rattachés à des parents différents.
	 * </p>
	 * <ul>
	 * <li>crée les deux parents {@code Outil} et {@code Loisir} ;</li>
	 * <li>crée un {@code SousTypeProduit} {@code Pince}
	 * sous chacun de ces parents ;</li>
	 * <li>recherche les deux objets par leur libellé commun {@code Pince} ;</li>
	 * <li>vérifie que la réponse contient les deux couples métier
	 * {@code [Loisir, Pince]} et {@code [Outil, Pince]} ;</li>
	 * <li>vérifie l'ordre métier appliqué par le SERVICE UC :
	 * parent puis libellé ;</li>
	 * <li>vérifie le message utilisateur de succès ;</li>
	 * <li>contrôle directement que les deux objets existent toujours
	 * dans le stockage avec leur parent respectif.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 * si la création des données du scénario
	 * ou leur recherche ne peut pas être réalisée.
	 */
	@Test
	@DisplayName("findByLibelle(ok) : retourne 2 DTO sur 2 parents distincts + message exact + preuve stockage")
	public void testFindByLibelleOk() throws Exception {

		/*
		 * ARRANGE :
		 * crée les deux TypeProduit qui serviront de parents.
		 *
		 * Les parents sont volontairement créés dans l'ordre
		 * Outil puis Loisir. Cet ordre de création ne détermine pas
		 * l'ordre de la réponse de findByLibelle(...).
		 */
		this.typeProduitService.creer(
				new TypeProduitDTO.InputDTO(OUTIL));
		this.typeProduitService.creer(
				new TypeProduitDTO.InputDTO(LOISIR));

		/*
		 * Crée le même libellé de SousTypeProduit sous les deux parents.
		 *
		 * Les deux objets sont distincts car l'identité métier
		 * d'un SousTypeProduit repose sur le couple
		 * [TypeProduit, SousTypeProduit].
		 */
		final OutputDTO creeA = this.service.creer(
				new SousTypeProduitDTO.InputDTO(
						OUTIL,
						PINCE));

		final OutputDTO creeB = this.service.creer(
				new SousTypeProduitDTO.InputDTO(
						LOISIR,
						PINCE));

		/*
		 * ACT :
		 * recherche tous les SousTypeProduit dont le libellé
		 * correspond à PINCE.
		 */
		final List<OutputDTO> dtos
			= this.service.findByLibelle(PINCE);

		/*
		 * ASSERT :
		 * la méthode retourne une liste non null contenant
		 * les deux objets créés.
		 */
		assertThat(dtos).isNotNull();
		assertThat(dtos).hasSize(2);

		/*
		 * Les deux DTO portent bien le libellé recherché.
		 */
		assertThat(dtos)
				.extracting(OutputDTO::getSousTypeProduit)
				.containsExactly(
						PINCE,
						PINCE);

		/*
		 * Le SERVICE UC trie les SousTypeProduit selon leur ordre métier :
		 * d'abord le libellé du parent, puis le libellé de l'enfant.
		 *
		 * Comme les deux enfants portent ici le même libellé PINCE,
		 * l'ordre dépend uniquement des parents :
		 * Loisir précède Outil.
		 */
		assertThat(dtos)
				.extracting(OutputDTO::getTypeProduit)
				.containsExactly(
						LOISIR,
						OUTIL);

		/*
		 * Vérifie que les identifiants suivent le même ordre.
		 *
		 * creeB correspond au couple [Loisir, Pince].
		 * creeA correspond au couple [Outil, Pince].
		 */
		assertThat(dtos)
				.extracting(OutputDTO::getIdSousTypeProduit)
				.containsExactly(
						creeB.getIdSousTypeProduit(),
						creeA.getIdSousTypeProduit());

		/*
		 * Vérifie le message positionné après préparation complète
		 * de la liste retournée.
		 */
		assertThat(this.service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService
								.MESSAGE_FINDBYLIBELLE_SUCCES_RECHERCHE);

		/*
		 * Contrôle directement dans le stockage le premier objet créé :
		 * son identifiant existe, son libellé est PINCE
		 * et son parent est Outil.
		 */
		assertThat(this.compterSousTypeProduitDansStockage(
				creeA.getIdSousTypeProduit()))
				.isEqualTo(1L);

		assertThat(this.lireLibelleSousTypeProduitDansStockage(
				creeA.getIdSousTypeProduit()))
				.isEqualTo(PINCE);

		assertThat(this.lireParentSousTypeProduitDansStockage(
				creeA.getIdSousTypeProduit()))
				.isEqualTo(OUTIL);

		/*
		 * Contrôle directement dans le stockage le second objet créé :
		 * son identifiant existe, son libellé est PINCE
		 * et son parent est Loisir.
		 */
		assertThat(this.compterSousTypeProduitDansStockage(
				creeB.getIdSousTypeProduit()))
				.isEqualTo(1L);

		assertThat(this.lireLibelleSousTypeProduitDansStockage(
				creeB.getIdSousTypeProduit()))
				.isEqualTo(PINCE);

		assertThat(this.lireParentSousTypeProduitDansStockage(
				creeB.getIdSousTypeProduit()))
				.isEqualTo(LOISIR);

		/*
		 * Vérifie enfin que chacun des deux couples métier
		 * est présent une seule fois dans le stockage.
		 */
		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				OUTIL,
				PINCE))
				.isEqualTo(1L);

		assertThat(this.compterSousTypeProduitParCoupleDansStockage(
				LOISIR,
				PINCE))
				.isEqualTo(1L);

	} // __________________________________________________________________

	
	
	// ====================== findByLibelleRapide =========================
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(null) :</p>
	 * <ul>
	 * <li>lève une {@link IllegalStateException}
	 * portant exactement {@link SousTypeProduitICuService#MESSAGE_PARAM_NULL} ;</li>
	 * <li>positionne exactement ce même message ;</li>
	 * <li>ne modifie aucune ligne dans le stockage.</li>
	 * </ul>
	 * </div>
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_NULL)
	@Test
	public void testFindByLibelleRapideNull() {

		/* ARRANGE : mémorise le volume du stockage avant l'appel. */
		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvant).isNotNull();

		/* ACT - ASSERT */
		assertThatThrownBy(() -> this.service.findByLibelleRapide(null))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_PARAM_NULL);

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_NULL);

		/* Garantit que l'appel invalide n'a pas modifié le stockage. */
		final Long countApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countApres).isNotNull();
		assertThat(countApres).isEqualTo(countAvant);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(blank) :</p>
	 * <ul>
	 * <li>retourne exactement le résultat du scénario
	 * {@code rechercherTous()} ;</li>
	 * <li>restitue tous les couples [TypeProduit, SousTypeProduit]
	 * réellement présents dans le stockage, y compris les données seedées ;</li>
	 * <li>contient les deux créations du test avec leurs identifiants
	 * et leurs parents ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHER_TOUS_OK} ;</li>
	 * <li>conserve les créations dans le stockage ;</li>
	 * <li>ne modifie ni le nombre de lignes ni les couples stockés
	 * pendant la recherche.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_BLANK)
	@Test
	public void testFindByLibelleRapideBlank() throws Exception {

		/* ARRANGE : crée le parent et deux enfants persistants. */
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));

		final OutputDTO cree1 = this.service.creer(
				new SousTypeProduitDTO.InputDTO(OUTIL, PERCEUSE));
		final OutputDTO cree2 = this.service.creer(
				new SousTypeProduitDTO.InputDTO(OUTIL, PINCE));

		this.entityManager.flush();

		assertThat(cree1).isNotNull();
		assertThat(cree2).isNotNull();
		assertThat(cree1.getIdSousTypeProduit()).isNotNull();
		assertThat(cree2.getIdSousTypeProduit()).isNotNull();

		final Long countAvantRecherche = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvantRecherche).isNotNull();

		/* Lit directement dans le stockage la réponse exhaustive attendue,
		 * dans l'ordre métier [TypeProduit, SousTypeProduit].
		 */
		final List<String> couplesStockesAvantRecherche
				= this.jdbcTemplate.query(
						SELECT_COUPLES_SOUS_TYPES_PRODUIT_ORDONNES,
						(resultSet, rowNumber) ->
								resultSet.getString(TP)
								+ "|"
								+ resultSet.getString(STP));

		/* ACT : un contenu blank délègue à rechercherTous(). */
		final List<OutputDTO> dtos
				= this.service.findByLibelleRapide(ESPACES);
		final String message = this.service.getMessage();

		/* ASSERT : garantit que la réponse DTO est exactement
		 * la réponse exhaustive prouvée dans le stockage avant l'appel.
		 */
		assertThat(dtos).isNotNull();
		assertThat(dtos)
				.extracting(dto ->
						dto.getTypeProduit()
						+ "|"
						+ dto.getSousTypeProduit())
				.containsExactlyElementsOf(couplesStockesAvantRecherche);

		/* Garantit que les deux créations du test figurent
		 * dans cette réponse exhaustive avec leurs identifiants persistants.
		 */
		assertThat(dtos)
				.extracting(OutputDTO::getIdSousTypeProduit)
				.contains(
						cree1.getIdSousTypeProduit(),
						cree2.getIdSousTypeProduit());

		assertThat(dtos).doesNotHaveDuplicates();

		assertThat(message)
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_RECHERCHER_TOUS_OK);

		/* Garantit directement que les créations du test
		 * sont toujours présentes dans le stockage.
		 */
		assertThat(this.compterSousTypeProduitDansStockage(
				cree1.getIdSousTypeProduit())).isEqualTo(1L);
		assertThat(this.compterSousTypeProduitDansStockage(
				cree2.getIdSousTypeProduit())).isEqualTo(1L);

		/* Garantit que la recherche n'a modifié
		 * ni le volume ni les couples présents dans le stockage.
		 */
		final Long countApresRecherche = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countApresRecherche).isNotNull();
		assertThat(countApresRecherche).isEqualTo(countAvantRecherche);

		final List<String> couplesStockesApresRecherche
				= this.jdbcTemplate.query(
						SELECT_COUPLES_SOUS_TYPES_PRODUIT_ORDONNES,
						(resultSet, rowNumber) ->
								resultSet.getString(TP)
								+ "|"
								+ resultSet.getString(STP));

		assertThat(couplesStockesApresRecherche)
				.containsExactlyElementsOf(couplesStockesAvantRecherche);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(introuvable) :</p>
	 * <ul>
	 * <li>retourne une liste non {@code null} et vide ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE} ;</li>
	 * <li>ne modifie aucune ligne dans le stockage.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_INTROUVABLE)
	@Test
	public void testFindByLibelleRapideIntrouvable() throws Exception {

		/* ARRANGE : mémorise le volume du stockage avant la recherche. */
		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvant).isNotNull();

		/* ACT */
		final List<OutputDTO> dtos
				= this.service.findByLibelleRapide(RECHERCHE_ZZ);

		/* ASSERT */
		assertThat(dtos).isNotNull();
		assertThat(dtos).isEmpty();

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		final Long countApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countApres).isNotNull();
		assertThat(countApres).isEqualTo(countAvant);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(nominal) :</p>
	 * <ul>
	 * <li>retourne uniquement les DTO dont le libellé contient le fragment ;</li>
	 * <li>respecte l'ordre métier [TypeProduit, SousTypeProduit] ;</li>
	 * <li>ne retourne aucun doublon ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK} ;</li>
	 * <li>prouve dans le stockage les deux objets ciblés
	 * et l'objet hors cible ;</li>
	 * <li>ne modifie pas le nombre de lignes pendant la recherche.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_NOMINAL)
	@Test
	public void testFindByLibelleRapideNominalAvecPreuveStockage()
			throws Exception {

		/* ARRANGE : crée deux parents et trois enfants persistants. */
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(OUTIL));
		this.typeProduitService.creer(new TypeProduitDTO.InputDTO(LOISIR));

		final OutputDTO creeOutil = this.service.creer(
				new SousTypeProduitDTO.InputDTO(OUTIL, RECHERCHE_ALPHA));
		final OutputDTO creeLoisir = this.service.creer(
				new SousTypeProduitDTO.InputDTO(LOISIR, RECHERCHE_ALPIN));
		final OutputDTO creeHorsCible = this.service.creer(
				new SousTypeProduitDTO.InputDTO(OUTIL, MARTEAU));

		final Long countAvant = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countAvant).isNotNull();

		/* ACT */
		final List<OutputDTO> dtos
				= this.service.findByLibelleRapide(RECHERCHE_AL);

		/* ASSERT : contrôle le contenu, l'ordre et le dédoublonnage. */
		assertThat(dtos).isNotNull();
		assertThat(dtos).hasSize(2);
		assertThat(dtos).doesNotHaveDuplicates();

		assertThat(dtos.get(0).getTypeProduit()).isEqualTo(LOISIR);
		assertThat(dtos.get(0).getSousTypeProduit())
				.isEqualTo(RECHERCHE_ALPIN);
		assertThat(dtos.get(0).getIdSousTypeProduit())
				.isEqualTo(creeLoisir.getIdSousTypeProduit());

		assertThat(dtos.get(1).getTypeProduit()).isEqualTo(OUTIL);
		assertThat(dtos.get(1).getSousTypeProduit())
				.isEqualTo(RECHERCHE_ALPHA);
		assertThat(dtos.get(1).getIdSousTypeProduit())
				.isEqualTo(creeOutil.getIdSousTypeProduit());

		assertThat(dtos)
				.extracting(OutputDTO::getIdSousTypeProduit)
				.doesNotContain(creeHorsCible.getIdSousTypeProduit());

		assertThat(this.service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* Preuve directe des trois objets dans le stockage. */
		assertThat(this.compterSousTypeProduitDansStockage(
				creeOutil.getIdSousTypeProduit())).isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(
				creeOutil.getIdSousTypeProduit())).isEqualTo(RECHERCHE_ALPHA);
		assertThat(this.lireParentSousTypeProduitDansStockage(
				creeOutil.getIdSousTypeProduit())).isEqualTo(OUTIL);

		assertThat(this.compterSousTypeProduitDansStockage(
				creeLoisir.getIdSousTypeProduit())).isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(
				creeLoisir.getIdSousTypeProduit())).isEqualTo(RECHERCHE_ALPIN);
		assertThat(this.lireParentSousTypeProduitDansStockage(
				creeLoisir.getIdSousTypeProduit())).isEqualTo(LOISIR);

		assertThat(this.compterSousTypeProduitDansStockage(
				creeHorsCible.getIdSousTypeProduit())).isEqualTo(1L);
		assertThat(this.lireLibelleSousTypeProduitDansStockage(
				creeHorsCible.getIdSousTypeProduit())).isEqualTo(MARTEAU);
		assertThat(this.lireParentSousTypeProduitDansStockage(
				creeHorsCible.getIdSousTypeProduit())).isEqualTo(OUTIL);

		/* Garantit que la recherche pure n'a pas modifié le stockage. */
		final Long countApres = this.jdbcTemplate.queryForObject(
				SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT,
				Long.class);

		assertThat(countApres).isNotNull();
		assertThat(countApres).isEqualTo(countAvant);

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
				+ FROM_SOUS_TYPE_PRODUIT
				+ INNER_JOIN_TP
				+ ON_STP_TYPE_PRODUIT
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
				+ FROM_SOUS_TYPE_PRODUIT
				+ INNER_JOIN_TP
				+ ON_STP_TYPE_PRODUIT
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
