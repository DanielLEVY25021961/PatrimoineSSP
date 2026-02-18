package levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA; // NOPMD by danyl on 05/02/2026 22:33

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import levy.daniel.application.model.metier.produittype.Produit;
import levy.daniel.application.model.metier.produittype.ProduitI;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.SousTypeProduitI;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.metier.produittype.TypeProduitI;


/**
 * 
 *
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 30 décembre 2025
 */
public class ConvertisseurJPAToMetierTest {

	// ************************ATTRIBUTS************************************/

	/**
	 * Boolean qui commande l'affichage pour tous les tests.<br/>
	 */
	public static final Boolean AFFICHAGE_GENERAL = true;
	
	/**
	 * "unused"
	 */
	public static final String UNUSED = "unused";
		
	/**
	 * System.getProperty("line.separator")
	 */
	public static final String SAUT_DE_LIGNE 
		= System.getProperty("line.separator");
	
	/**
	 * "null" 
	 */
	public static final String NULL = "null";
	
	/**
	 * "- typeProduit du sousTypeProduit : %-13s "
	 */
	public static final String FORMAT_TYPEPRODUIT_STP 
		= "- typeProduit du sousTypeProduit : %-13s ";

	/**
	 * "%-1s"
	 */
	public static final String FORMAT_IDTP = "%-1s";
	
	/**
	 * "typeProduit"
	 */
	public static final String TYPEPRODUIT = "typeProduit";

	/**
	 * "%-1s"
	 */
	public static final String FORMAT_IDSTP = "%-1s";
	
	/**
	 * "- [idSousTypeProduit : %-1s "
	 */
	public static final String FORMAT_IDSOUSTYPEPRODUIT 
		= "- [idSousTypeProduit : %-1s ";
	
	/**
	 * "%-1s"
	 */
	public static final String FORMAT_IDPRODUIT = "%-1s";

	/**
	 * "sousTypeProduit"
	 */
	public static final String SOUSTYPEPRODUIT = "sousTypeProduit";
	
	/**
	 * "typeProduitStringJPA doit valoir \"vêtement\" : "
	 */
	public static final String TYPEPRODUIT_VETEMENT 
		= "typeProduitStringJPA doit valoir \"vêtement\" : ";

	/**
	 * "vêtement"
	 */
	public static final String VETEMENT = "vêtement";

	/**
	 * "chemise manches longues"
	 */
	public static final String CHEMISES_MANCHES_LONGUES 
		= "chemise manches longues";

	/**
	 * "TypeProduit.sousTypeProduits"
	 */
	public static final String TP_STP = "TypeProduit.sousTypeProduits";

	/**
	 * "vêtement pour homme"
	 */
	public static final String VETEMENT_POUR_HOMME = "vêtement pour homme";

	/**
	 * "SousTypeProduit(produits)"
	 */
	public static final String STP_PRODUIT = "SousTypeProduit(produits)";
	
	/**
	 * "JPAToMetier-Beton"
	 */
	public static final String JPATOMETIER_BETON = "JPAToMetier-Beton";

	
	/* ------------------------------------------------------------------ */
	
	/**
	 * new TypeProduitJPA(1L, "vêtement")
	 */
	public TypeProduitJPA typeProduitVetementJPA;
		
	/**
	 * new TypeProduitJPA(2L, "pêche")
	 */
	public TypeProduitJPA typeProduitPecheJPA;
	
	/**
	 * new SousTypeProduitJPA(1L, "vêtement pour homme"
	 * , this.typeProduitVetementJPA)
	 */
	public SousTypeProduitJPA sousTypeProduitVetementHommeJPA;

	/**
	 * new SousTypeProduitJPA(2L, "vêtement pour femme"
	 * , this.typeProduitVetementJPA)
	 */
	public SousTypeProduitJPA sousTypeProduitVetementFemmeJPA;
	
	/**
	 * new SousTypeProduitJPA(3L, "vêtement pour enfant"
	 * , this.typeProduitVetementJPA)
	 */
	public SousTypeProduitJPA sousTypeProduitVetementEnfantJPA;
	
	/**
	 * new SousTypeProduitJPA(4L, "canne", this.typeProduitPecheJPA)
	 */
	public SousTypeProduitJPA sousTypeProduitPecheCanneJPA;
	
	/**
	 * new SousTypeProduitJPA(5L, "cuiller", this.typeProduitPecheJPA)
	 */
	public SousTypeProduitJPA sousTypeProduitPecheCuillerJPA;
	
	/**
	 * new ProduitJPA(1L, "chemise à manches longues pour homme"
	 * , this.sousTypeProduitVetementHommeJPA)
	 */
	public ProduitJPA produitChemiseManchesLonguesPourHomme;
	
	/**
	 * new ProduitJPA(2L, "chemise à manches courtes pour homme"
	 * , this.sousTypeProduitVetementHommeJPA)
	 */
	public ProduitJPA produitChemiseManchesCourtesPourHomme;
	
	/**
	 * new ProduitJPA(3L, "sweatshirt pour homme"
	 * , this.sousTypeProduitVetementHommeJPA)
	 */
	public ProduitJPA produitSweatshirtPourHomme;
	
	/**
	 *  new ProduitJPA(4L, "teeshirt pour homme"
	 * , this.sousTypeProduitVetementHommeJPA)
	 */
	public ProduitJPA produitTeeshirtPourHomme;
	
	/**
	 * <style>p, ul, li {line-height : 1em;}</style>
	 * <div>
	 * <p>LOG : Logger : </p>
	 * <p>Logger pour Log4j (utilisant org.apache.logging.log4j).</p>
	 * <p>dépendances : </p>
	 * <ul>
	 * <li><code>org.apache.logging.log4j.Logger</code></li>
	 * <li><code>org.apache.logging.log4j.LogManager</code></li>
	 * </ul>
	 * </div>
	 */
	private static final Logger LOG = LogManager
			.getLogger(ConvertisseurJPAToMetierTest.class);

	// *************************METHODES************************************/



	/**
	* <div>
	* <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	* </div>
	*/
	public ConvertisseurJPAToMetierTest() {
		super();
	} // Fin du CONSTRUCTEUR D'ARITE NULLE.________________________________

	
	
	// ==========================================================
	// Tests
	// ==========================================================

	
	
	/**
	 * <div>
	 * <p>s'assure que lors d'une conversion : </p>
	 * <ul>
	 * <li>typeProduitJPAToMetier(null) retourne null.</li>
	 * <li>typeProduitJPAToMetier(constructeur null) retourne null.</li>
	 * <li>typeProduitJPAToMetier(blank) retourne null.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testTypeProduitJPAToMetierNull() : vérifie le comportement de la méthode typeProduitJPAToMetier(null)")
	@Tag("JPAToMetier")
	@Test
	public final void testTypeProduitJPAToMetierNull() {
				
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE ConvertisseurJPAToMetierTest - méthode testTypeProduitJPAToMetierNull() ********** ");
			System.out.println("CE TEST VERIFIE le fonctionnement de la méthode typeProduitJPAToMetier(Null).");
			System.out.println();				
		}
		
		//**** ARRANGE - GIVEN
		final TypeProduitJPA typeProduitJPANull = null;
		final TypeProduitJPA typeProduitJPAConstructeurNull = null;
		final TypeProduitJPA typeProduitJPABlank = new TypeProduitJPA("  ");
		
		// ACT - WHEN
		final TypeProduitI typeProduitNull 
			= ConvertisseurJPAToMetier
				.typeProduitJPAToMetier(typeProduitJPANull);
		final TypeProduitI typeProduitConstructeurNull 
			= ConvertisseurJPAToMetier
				.typeProduitJPAToMetier(typeProduitJPAConstructeurNull);
		final TypeProduitI typeProduitBlank 
			= ConvertisseurJPAToMetier
				.typeProduitJPAToMetier(typeProduitJPABlank);
		
		// ASSERT - THEN
		/* typeProduitJPAToMetier(null) retourne null.  */
		assertNull(typeProduitNull
				, "la conversion de null doit donner null : ");
		/* typeProduitJPAToMetier(constructeur null) retourne null. */
		assertNull(typeProduitConstructeurNull
				, "la conversion de Constructeur null doit donner null : ");
		/* typeProduitJPAToMetier(blank) retourne null. */
		assertNull(typeProduitBlank
				, "la conversion de Blank doit donner null : ");
		
	} //___________________________________________________________________
	
	
	
	/**
	* <div>
	* <p>s'assure que lors d'une conversion : </p>
	* <ul>
	* <p>NIVEAU TypeProduit : </p>
	* <li>idTypeProduit bien converti.</li>
	* <li>idTypeProduit = idTypeProduitJPA.</li>
	* <li>TypeProduit bien converti.</li>
	* <li>typeProduit = typeProduitJPA.</li>
	* <li>sousTypeProduits bien converti.</li>
	* <li>sousTypeProduits a la même taille que sousTypeProduitsJPA.</li>
	* <p>NIVEAU de chaque SousTypeProduit de sousTypeProduits du TypeProduit : </p>
	* <li>l'ID de chaque SousTypeProduit dans sousTypeProduits 
	* n'est pas null.</li>
	* <li>le sousTypeProduitString de chaque SousTypeProduit 
	* dans sousTypeProduits n'est pas null.</li>
	* <li>le TypeProduit de chaque SousTypeProduit 
	* est le bon dans sousTypeProduits.</li>
	* <li>la collection produits de chaque SousTypeProduit 
	* dans sousTypeProduits est correcte.</li>
	* <p>Dans chaque Produit de la collection produits 
	* de chaque SousTypeProduit de sousTypeProduits : </p>
	* <li>l'ID du produit n'est pas null.</li>
	* <li>Le SousTypeProduit de chaque Produit est le bon.</li>
	* <li>Le produit (String) du produit n'est pas null.</li>
	* <li>Le TypeProduit de chaque Produit est le bon.</li>
	* </ul>
	* </div>
	* 
	* 
	* <div>
	* <p style="text-decoration: underline;font-weight:bold;padding : 20px;">
    * Diagramme de Classes du Produit qualifié par un SousTypeProduit qui est lui-même une déclinaison d'un TypeProduit</p>
    * <p>
    * <img src="../../../../../../../../../../../javadoc/images/persistence/metier/produittype/entities/entitiesJPA/diagramme_classes_produittype.jpg" 
    * alt="diagramme de classes d'un Produit Typé" border="1" align="center" height= 200px />
    * </p>
	* </div>
	*
	* <div>
	* <p style="text-decoration: underline;font-weight:bold;padding : 20px;">
    * Diagramme d'activités de la méthode typeProduitJPAToMetier</p>
    * <p>
    * <img src="../../../../../../../../../../../javadoc/images/persistence/metier/produittype/entities/entitiesJPA/diagramme_activités_convertisseur_TypeProduitJPAToMetier.jpg" 
    * alt="diagramme d'activités de la méthode typeProduitJPAToMetier" border="1" align="center" height= 800px />
    * </p>
	* </div>
	*/
	@SuppressWarnings(UNUSED)
	@DisplayName("testTypeProduitJPAToMetier() : vérifie le comportement général de la méthode typeProduitJPAToMetier(TypeProduitJPA pTypeProduitJPA)")
	@Tag("JPAToMetier")
	@Test
	public final void testTypeProduitJPAToMetier() {
				
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE ConvertisseurJPAToMetierTest - méthode testTypeProduitJPAToMetier() ********** ");
			System.out.println("CE TEST VERIFIE le fonctionnement de la méthode typeProduitJPAToMetier(TypeProduitJPA pTypeProduitJPA).");
			System.out.println();				
		}
		
		//**** ARRANGE - GIVEN
		this.creerScenario();
		
		// ACT - WHEN
		final Long idTypeProduitJPA 
			= this.typeProduitVetementJPA.getIdTypeProduit();
		final String typeProduitStringJPA 
			= this.typeProduitVetementJPA.getTypeProduit();
		final List<? extends SousTypeProduitI> sousTypeProduitsJPA 
			= this.typeProduitVetementJPA.getSousTypeProduits();
		
		final SousTypeProduitJPA sousTypeProduitHommeJPA 
			= (SousTypeProduitJPA) sousTypeProduitsJPA.get(0);
		final Long idSoustypeProduitHommeJPA 
			= sousTypeProduitHommeJPA.getIdSousTypeProduit();
		final TypeProduitJPA typeProduitHommeJPA 
			= (TypeProduitJPA) sousTypeProduitHommeJPA.getTypeProduit();
		final String sousTypeProduitHommeStringJPA 
			= sousTypeProduitHommeJPA.getSousTypeProduit();
		final List<? extends ProduitI> listProduitsHommeJPA 
			= sousTypeProduitHommeJPA.getProduits();
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println(this.afficherTypeProduitFormate(this.typeProduitVetementJPA));
			System.out.println();
		}

		// ASSERT - THEN
		assertEquals(1L, idTypeProduitJPA, "idTypeProduitJPA doit valoir 1L : ");
		assertEquals(VETEMENT, typeProduitStringJPA, TYPEPRODUIT_VETEMENT);
		assertTrue(sousTypeProduitsJPA.size() == 3, "La liste sousTypeProduits doit contenir 3 éléments : ");
		assertEquals(1L, idSoustypeProduitHommeJPA, "idSoustypeProduitHommeJPA doit valoir 1L : ");
		assertEquals(typeProduitHommeJPA, sousTypeProduitHommeJPA.getTypeProduit(), "typeProduitHommeJPA doit valoir \"vêtement\" : ");
		assertEquals(VETEMENT_POUR_HOMME, sousTypeProduitHommeStringJPA, "sousTypeProduitHommeStringJPA doit valoir \"vêtement pour homme\" : ");
		assertTrue(listProduitsHommeJPA.size() == 4, "La liste produits (listProduitsHommeJPA) doit contenir 4 éléments : ");
		
		// ACT - WHEN
		/* CONVERSION. */
		final TypeProduitI typeProduitVetement 
			= ConvertisseurJPAToMetier.typeProduitJPAToMetier(
					this.typeProduitVetementJPA);
		
		final Long idTypeProduit 
			= typeProduitVetement.getIdTypeProduit();
		final String typeProduitString 
			= typeProduitVetement.getTypeProduit();
		final List<SousTypeProduit> sousTypeProduits 
			= (List<SousTypeProduit>) typeProduitVetement.getSousTypeProduits();
		
		final SousTypeProduit sousTypeProduitHomme 
			= sousTypeProduits.get(0);
		final Long idSoustypeProduitHomme 
			= sousTypeProduitHomme.getIdSousTypeProduit();
		final TypeProduit typeProduitHomme 
			= (TypeProduit) sousTypeProduitHomme.getTypeProduit();
		final String sousTypeProduitHommeString 
			= sousTypeProduitHomme.getSousTypeProduit();
		final List<Produit> listProduitsHomme 
			= convertirListProduitEnListProduit(
					sousTypeProduitHomme.getProduits());
			
						
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** NIVEAU TYPEPRODUIT **************");
			System.out.println("**** typeProduitVetement après CONVERSION *****");
			System.out.println(this.afficherTypeProduitFormate(typeProduitVetement));
			System.out.println();
		}


		// ASSERT - THEN
		/* idTypeProduit bien converti. */
		assertEquals(1L, idTypeProduit, "idTypeProduit doit valoir 1L : ");
		/* idTypeProduit = idTypeProduitJPA.*/
		assertEquals(this.typeProduitVetementJPA.getIdTypeProduit()
				, typeProduitVetement.getIdTypeProduit()
				, "les ID de l'Entity et de l'Objet métier doivent être les mêmes : ");
		/* TypeProduit bien converti. */
		assertEquals(VETEMENT, typeProduitString
				, "typeProduitString doit valoir \"vêtement\" : ");
		/* typeProduit = typeProduitJPA. */
		assertEquals(this.typeProduitVetementJPA.getTypeProduit()
				, typeProduitVetement.getTypeProduit()
				, "les TypeProduit de l'Entity et de l'Objet métier doivent être les mêmes : ");
		/* sousTypeProduits bien converti. */
		assertTrue(sousTypeProduits.size() == 3, "La liste sousTypeProduits doit contenir 3 éléments : ");
		/* sousTypeProduits a la même taille que sousTypeProduitsJPA. */
		assertEquals(this.typeProduitVetementJPA.getSousTypeProduits().size()
				, typeProduitVetement.getSousTypeProduits().size()
				, "les sousTypeProduits de l'Entity et de l'Objet métier doivent avoir la même taille : ");
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println("****** LISTE sousTypeProduits du TypeProduit \"vêtement\" typeProduitVetement **********");
		}
		
		/* sousTypeProduits */
		for (final SousTypeProduit sousTypeProduit : sousTypeProduits) {
			
			/* AFFICHAGE A LA CONSOLE. */
			if (AFFICHAGE_GENERAL && affichage) {
				System.out.println(sousTypeProduit);
			}
			
			/* l'ID de chaque SousTypeProduit dans 
			 * sousTypeProduits n'est pas null. */
			assertNotNull(sousTypeProduit.getIdSousTypeProduit()
					, "l'ID du SousTypeProduit ne doit pas être null : ");
			
			/* le sousTypeProduitString de chaque SousTypeProduit 
			 * dans sousTypeProduits n'est pas null. */
			assertNotNull(sousTypeProduit.getSousTypeProduit()
					, "le SousTypeProduit ne doit pas être null : ");
			
			/* le TypeProduit de chaque SousTypeProduit est le bon 
			 * dans sousTypeProduits. */
			assertEquals(typeProduitVetement
					, sousTypeProduit.getTypeProduit()
						, "Le TypeProduit de chaque SousTypeProduit "
								+ "doit être typeProduitVetement : ");
			
			final List<Produit> produits 
				= convertirListProduitEnListProduit(
						sousTypeProduit.getProduits());
			
			/* AFFICHAGE A LA CONSOLE. */
			if (AFFICHAGE_GENERAL && affichage) {
				System.out.println("contenu de la liste produits dans le sousTypeProduit : " + sousTypeProduit.getSousTypeProduit());
				System.out.println(this.afficherProduits(produits));
			}

			/* la collection produits de chaque SousTypeProduit dans sousTypeProduits est correcte. */
			if (produits != null) {
								
				for (final Produit produit : produits) {
					
					if (produit != null) {
												
						assertEquals(typeProduitVetement
								, produit.getTypeProduit()
								, "Le TypeProduit de chaque Produit "
										+ "doit être typeProduitVetement : ");
					}					
				}
			}
					
		}
		
		
		assertEquals(1L, idSoustypeProduitHomme, "idSoustypeProduitHomme doit valoir 1L : ");
		assertEquals(typeProduitHomme, sousTypeProduitHomme.getTypeProduit(), "typeProduitHomme doit valoir \"vêtement\" : ");
		assertEquals(VETEMENT_POUR_HOMME, sousTypeProduitHommeString, "sousTypeProduitHommeString doit valoir \"vêtement pour homme\" : ");
		assertTrue(listProduitsHomme.size() == 4, "La liste produits (listProduitsHomme) doit contenir 4 éléments : ");

		/* vérifie que le SousTypeProduit de chaque Produit est bien renseigné. */
		for (final Produit produit : listProduitsHomme) {
			
			if (produit != null) {
				
				/* l'ID du produit n'est pas null. */
				assertNotNull(produit.getIdProduit()
						, "l'ID du Produit ne doit pas être null : ");
				
				/* Le SousTypeProduit de chaque Produit est le bon. */
				assertEquals(sousTypeProduitHomme
						, produit.getSousTypeProduit()
						, "le SousTypeProduit de chaque Produit "
								+ "doit être sousTypeProduitHomme : ");
				
				/* Le produit (String) du produit n'est pas null. */
				assertNotNull(produit.getProduit()
						, "le produitString du Produit ne doit pas être null : ");
				
				/* Le TypeProduit de chaque Produit est le bon.*/
				assertEquals(typeProduitVetement
						, produit.getTypeProduit()
						, "le TypeProduit de chaque Produit "
								+ "doit être typeProduitVetement : ");								
			}
		}
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>s'assure que lors d'une conversion : </p>
	 * <ul>
	 * <p>SousTypeProduit : </p>
	 * <li>idSousTypeProduit bien converti.</li>
	 * <li>typeProduit bien convert</li>
	 * <li>SousTypeProduit bien converti.</li>
	 * <li>produits bien converti.</li>
	 * <p>Dans chaque Produit de produits : </p>
	 * <li>l'ID de chaque Produit dans produits 
	 * n'est pas null.</li>
	 * <li>le produitString de chaque Produit 
	 * dans produits n'est pas null.</li>
	 * <li>le TypeProduit de chaque Produit 
	 * est le bon dans produits.</li>
	 * <p>Dans TypeProduit du SousTypeProduit : </p>
	 * <li>l'ID du TypeProduit n'est pas null.</li>
	 * <li>le TypeProduit est le bon</li>
	 * <li>la liste sousTypeProduit est la bonne.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testSousTypeProduitJPAToMetier() : vérifie le comportement général de la méthode sousTypeProduitJPAToMetier(SousTypeProduitJPA pSousTypeProduitJPA)")
	@Tag("JPAToMetier")
	@Test
	public final void testSousTypeProduitJPAToMetier() {
				
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE ConvertisseurJPAToMetierTest - méthode testSousTypeProduitJPAToMetier() ********** ");
			System.out.println("CE TEST VERIFIE le fonctionnement de la méthode sousTypeProduitJPAToMetier(SousTypeProduitJPA pSousTypeProduitJPA).");
			System.out.println();				
		}
		
		assertNull(
				ConvertisseurJPAToMetier.sousTypeProduitJPAToMetier(null)
				, "ConvertisseurJPAToMetier.sousTypeProduitJPAToMetier(null) "
						+ "doit toujours retourner null : ");
		
		assertNull(
				ConvertisseurJPAToMetier.
				sousTypeProduitJPAToMetier(new SousTypeProduitJPA("   "))
				, "ConvertisseurJPAToMetier.sousTypeProduitJPAToMetier"
						+ "(Blank) doit toujours retourner null : ");
		
		
		//**** ARRANGE - GIVEN
		this.creerScenario();
		
		// ACT - WHEN
		/* CONVERSION */
		final SousTypeProduitI sousTypeProduit 
			= ConvertisseurJPAToMetier
				.sousTypeProduitJPAToMetier(
						this.sousTypeProduitVetementHommeJPA);
		
		final Long idSousTypeProduitJPA 
			= this.sousTypeProduitVetementHommeJPA.getIdSousTypeProduit();
		final String sousTypeProduitJPAString 
			= this.sousTypeProduitVetementHommeJPA.getSousTypeProduit();
		final TypeProduitJPA typeProduitJPA 
			= (TypeProduitJPA) this.sousTypeProduitVetementHommeJPA.getTypeProduit();
		
		/* produits */
		final List<? extends ProduitI> produitsJPA 
			= this.sousTypeProduitVetementHommeJPA.getProduits();
		
		final List<? extends SousTypeProduitI> sousTypeProduitsJPADansTypeProduitDuSousTypeProduit 
			= typeProduitJPA.getSousTypeProduits();

		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			final String presentation = String.format("idSousTypeProduit : %-1s - sousTypeProduit : %-20s - idTypeProduit : %-1s - typeProduit : %-20s", idSousTypeProduitJPA, sousTypeProduitJPAString, typeProduitJPA.getIdTypeProduit(), typeProduitJPA.getTypeProduit());
			System.out.println("**** Affichage du sousTypeProduitJPA sousTypeProduitVetementHommeJPA ******");
			System.out.println(presentation);
			System.out.println("sousTypeProduits du typeProduitJPA : " + typeProduitJPA.getSousTypeProduits());
			System.out.println("**** sousTypeProduits du TypeProduitJPA *****");
			System.out.println(this.afficherSousTypeProduitsJPA(sousTypeProduitsJPADansTypeProduitDuSousTypeProduit));
			System.out.println();			
		}
		
		
		/* SOUSTYPEPRODUIT. */
		/* idSousTypeProduit */
		final Long idSousTypeProduit 
			= sousTypeProduit.getIdSousTypeProduit();
		
		/* typeProduit */
		final TypeProduit typeProduit = (TypeProduit) sousTypeProduit.getTypeProduit();
		
		/* sousTypeProduitString */
		final String sousTypeProduitString 
			= sousTypeProduit.getSousTypeProduit();
		
		/* produits */
		final List<Produit> produits = (List<Produit>) sousTypeProduit.getProduits();
		
		final List<SousTypeProduit> sousTypeProduitsDansTypeProduitDuSousTypeProduit 
			= (List<SousTypeProduit>) typeProduit.getSousTypeProduits();
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			final String presentation = String.format("idSousTypeProduit : %-1s - sousTypeProduit : %-20s - idTypeProduit : %-1s - typeProduit : %-20s", idSousTypeProduit, sousTypeProduitString, typeProduit.getIdTypeProduit(), typeProduit.getTypeProduit());
			System.out.println("**** Affichage du sousTypeProduit converti de sousTypeProduitVetementHommeJPA ******");
			System.out.println(presentation);
			System.out.println("sousTypeProduits du TypeProduit : " + typeProduit.getSousTypeProduits());
			System.out.println("**** sousTypeProduits du TypeProduit *****");
			System.out.println(this.afficherSousTypeProduits(sousTypeProduitsDansTypeProduitDuSousTypeProduit));
			System.out.println();
		}
		
		// ASSERT - THEN
		/* idSousTypeProduit bien converti. */
		assertEquals(1L
				, idSousTypeProduit
				, "idSousTypeProduit doit valoir 1L : ");
		assertEquals(this.sousTypeProduitVetementHommeJPA.getIdSousTypeProduit()
				, idSousTypeProduit
				, "idSousTypeProduit doit valoir l'ID de l'Entity : ");
		
		assertEquals(
				sousTypeProduitsJPADansTypeProduitDuSousTypeProduit.size()
				, sousTypeProduitsDansTypeProduitDuSousTypeProduit.size()
				, "la liste sousTypeProduits doit avoir le même taille dans sousTypeProduitJPA et sousTypeProduit: ");
		
	} //___________________________________________________________________	

	
	
	/**
	 * <div>
	 * <p>s'assure que : </p>
	 * <ul>
	 * <li>null.equalsMetier(null) retourne true.</li>
	 * <li>null.equalsMetier(nonNull) retourne false.</li>
	 * <li>typeProduitJPA.equalsMetier(typeProduit avec id different) 
	 * retourne false.</li>
	 * <li>la comparaison sur la String typeProduit fonctionne 
	 * correctement. </li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testEqualsMetierTypeProduit() : vérifie le comportement général de la méthode equalsMetier(TypeProduitJPA pTypeProduitJPA, TypeProduit pTypeProduit)")
	@Tag("equalsMetier")
	@Test
	public final void testEqualsMetierTypeProduit() {
				
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE ConvertisseurJPAToMetierTest - méthode testEqualsMetierTypeProduit() ********** ");
			System.out.println("CE TEST VERIFIE le fonctionnement de la méthode equalsMetier(TypeProduitJPA pTypeProduitJPA, TypeProduit pTypeProduit).");
			System.out.println();				
		}
		
		//**** ARRANGE - GIVEN
		final String typeProduitString = TYPEPRODUIT;
		final TypeProduitJPA typeProduitJPANull = null;
		final TypeProduitJPA typeProduitJPAIdNull = new TypeProduitJPA(null, "typeProduitIdNull");
		final TypeProduitJPA typeProduitJPAIdNonNull = new TypeProduitJPA(1L, "typeProduitIdNonNull");
		final TypeProduitJPA typeProduitJPAIdDifferent = new TypeProduitJPA(2L, typeProduitString);
		final TypeProduitJPA typeProduitJPA = new TypeProduitJPA(1L, typeProduitString);
		
		final TypeProduit typeProduitNull = null;
		final TypeProduit typeProduitIdNull = new TypeProduit(null, "typeProduitIdNull");
		final TypeProduit typeProduitIdNonNull = new TypeProduit(1L, "typeProduitIdNonNull");
		final TypeProduit typeProduitIdDifferent = new TypeProduit(2L, typeProduitString);
		final TypeProduit typeProduit = new TypeProduit(1L, typeProduitString);
		
		// ACT - WHEN
		final boolean nullequalsMetierNull 
			= ConvertisseurJPAToMetier.equalsMetier(
					typeProduitJPANull, typeProduitNull);
		final boolean unSeulNull1 
			= ConvertisseurJPAToMetier.equalsMetier(
					typeProduitJPANull, typeProduit);
		final boolean unSeulNull2 
			= ConvertisseurJPAToMetier.equalsMetier(
					typeProduitJPA, typeProduitNull);
		final boolean unSeulIdNull1 
			= ConvertisseurJPAToMetier.equalsMetier(
					typeProduitJPAIdNull, typeProduitIdNonNull);
		final boolean unSeulIdNull2 
			= ConvertisseurJPAToMetier.equalsMetier(
					typeProduitJPAIdNonNull, typeProduitIdNull);
		final Boolean idDifferents 
			= ConvertisseurJPAToMetier.equalsMetier(
					typeProduitJPAIdDifferent, typeProduit);
		final Boolean idDifferents2 
			= ConvertisseurJPAToMetier.equalsMetier(
					typeProduitJPA, typeProduitIdDifferent);
		final boolean equalsMetierAvecIdNull 
			= ConvertisseurJPAToMetier.equalsMetier(
					typeProduitJPAIdNull, typeProduitIdNull);
		final Boolean equalsMetierClassique 
			= ConvertisseurJPAToMetier.equalsMetier(
					typeProduitJPA, typeProduit);
		final boolean equalMetierFalse1 
			= ConvertisseurJPAToMetier.equalsMetier(
					typeProduitJPAIdDifferent, typeProduit);
		final boolean equalMetierFalse2 
			= ConvertisseurJPAToMetier.equalsMetier(
					typeProduitJPA, typeProduitIdDifferent);
		
		// ASSERT - THEN
		/* null.equalsMetier(null) retourne true. */
		assertTrue(nullequalsMetierNull
				, "null.equalsMetier(null) doit retourner true : ");
		/* null.equalsMetier(nonNull) retourne false. */
		assertFalse(unSeulNull1
				, "null.equalsMetier(nonNull) doit retourner false : ");
		assertFalse(unSeulNull2
				, "nonNull.equalsMetier(null) doit retourner false : ");
		/* typeProduitJPA.equalsMetier(typeProduit avec id different) retourne false. */
		assertFalse(unSeulIdNull1
				, "typeProduitJPAIdNull.equalsMetier(typeProduitIdNonNull) doit retourner false : ");
		assertFalse(unSeulIdNull2
				, "typeProduitJPAIdNonNull.equalsMetier(typeProduitIdNull) doit retourner false : ");
		assertFalse(idDifferents
				, "typeProduitJPAIdDifferent.equalsMetier(typeProduit) doit retourner false : ");
		assertFalse(idDifferents2
				, "typeProduitJPA.equalsMetier(typeProduitIdDifferent) doit retourner false : ");
		/* la comparaison sur la String typeProduit fonctionne correctement. */
		assertTrue(equalsMetierAvecIdNull
				, "typeProduitJPAIdNull.equalsMetier(typeProduitIdNull) doit retourner true : ");
		assertTrue(equalsMetierClassique
				, "typeProduitJPA.equalsMetier(typeProduit) doit retourner true : ");
		assertFalse(equalMetierFalse1
				, "typeProduitJPAIdDifferent.equalsMetier(typeProduit) doit retourner false : ");
		assertFalse(equalMetierFalse2
				, "typeProduitJPA.equalsMetier(typeProduitIdDifferent) doit retourner false : ");
		
	} //___________________________________________________________________


	
	/**
	 * <div>
	 * <p>s'assure que : </p>
	 * <ul>
	 * <li>null.equalsMetier(null) retourne true.</li>
	 * <li>null.equalsMetier(nonNull) retourne false.</li>
	 * <li>sousTypeProduitJPA.equalsMetier(sousTypeProduit avec id different) 
	 * retourne false.</li>
	 * <li>sousTypeProduitJPA.equalsMetier(sousTypeProduit avec TypeProduit different) retourne false.</li>
	 * <li>la comparaison sur la String SousTypeProduit fonctionne 
	 * correctement. </li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testEqualsMetierSousTypeProduit() : vérifie le comportement général de la méthode equalsMetier(SousTypeProduitJPA pSousTypeProduitJPA, SousTypeProduit pSousTypeProduit)")
	@Tag("equalsMetier")
	@Test
	public final void testEqualsMetierSousTypeProduit() {
				
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE ConvertisseurJPAToMetierTest - méthode testEqualsMetierSousTypeProduit() ********** ");
			System.out.println("CE TEST VERIFIE le fonctionnement de la méthode equalsMetier(SousTypeProduitJPA pSousTypeProduitJPA, SousTypeProduit pSousTypeProduit).");
			System.out.println();				
		}
		
		//**** ARRANGE - GIVEN
		/* String */
		final String sousTypeProduitPasNullString = "sousTypeProduitPasNull";
		final String typeProduitPasNullString = "typeProduitPasNull";
		final String typeProduitString = TYPEPRODUIT;
		final String sousTypeProduitString = SOUSTYPEPRODUIT;
		final String typeProduitDifferentString = "typeProduit différent";
		
		/* TypeProduitJPA */
		final TypeProduitJPA typeProduitJPANonNull 
			= new TypeProduitJPA(null, typeProduitPasNullString);
		final TypeProduitJPA typeProduitJPA 
			= new TypeProduitJPA(1L, typeProduitString);
		final TypeProduitJPA typeProduitJPADifferent 
			= new TypeProduitJPA(1L, typeProduitDifferentString); 
		
		/* SousTypeProduitJPA */
		final SousTypeProduitJPA sousTypeProduitJPANull = null;
		final SousTypeProduitJPA sousTypeProduitJPAIdNull 
			= new SousTypeProduitJPA(null, sousTypeProduitPasNullString, null);
		final SousTypeProduitJPA sousTypeProduitJPA 
			= new SousTypeProduitJPA(1L, sousTypeProduitString, typeProduitJPA, null);
		final SousTypeProduitJPA sousTypeProduitJPAIdDifferent 
			= new SousTypeProduitJPA(2L, sousTypeProduitString, typeProduitJPA, null);
		final SousTypeProduitJPA sousTypeProduitJPASTPDifferent 
			= new SousTypeProduitJPA(1L, sousTypeProduitString, typeProduitJPADifferent, null);
		
		/* TypeProduit */		
		final TypeProduit typeProduitNonNull 
			= new TypeProduit(null, typeProduitPasNullString);
		final TypeProduit typeProduit 
			= new TypeProduit(1L, typeProduitString);
		final TypeProduit typeProduitDifferent 
			= new TypeProduit(1L, typeProduitDifferentString);
		
		/* SousTypeProduit */
		final SousTypeProduit sousTypeProduitNull = null;
		final SousTypeProduit sousTypeProduitIdNull 
			= new SousTypeProduit(null, sousTypeProduitPasNullString, null);
		final SousTypeProduit sousTypeProduit 
			= new SousTypeProduit(1L, sousTypeProduitString, typeProduit, null);
		final SousTypeProduit sousTypeProduitIdDifferent 
			= new SousTypeProduit(2L, sousTypeProduitString, typeProduit, null);
		final SousTypeProduit sousTypeProduitSTPDifferent 
			= new SousTypeProduit(1L, sousTypeProduitString, typeProduitDifferent, null);
		final SousTypeProduit sousTypeProduitDiff 
			= new SousTypeProduit(1L, "toto", typeProduit, null);
		
		// ACT - WHEN
		final boolean nullequalsMetierNull 
			= ConvertisseurJPAToMetier.equalsMetier(
					sousTypeProduitJPANull, sousTypeProduitNull);
		final boolean unSeulNull1 
			= ConvertisseurJPAToMetier.equalsMetier(
					sousTypeProduitJPANull, sousTypeProduit);
		final boolean unSeulNull2 
			= ConvertisseurJPAToMetier.equalsMetier(
					sousTypeProduitJPA, sousTypeProduitNull);
		final boolean unIdNull1 
			= ConvertisseurJPAToMetier.equalsMetier(
					sousTypeProduitJPAIdNull, sousTypeProduit);
		final boolean unIdNull2 
			= ConvertisseurJPAToMetier.equalsMetier(
					sousTypeProduitJPA, sousTypeProduitIdNull);
		final boolean idDifferents1 
			= ConvertisseurJPAToMetier.equalsMetier(
					sousTypeProduitJPA, sousTypeProduitIdDifferent);
		final boolean idDifferents2 
			= ConvertisseurJPAToMetier.equalsMetier(
					sousTypeProduitJPAIdDifferent, sousTypeProduit);
		final boolean sousTypeProduitDifferents1 
			= ConvertisseurJPAToMetier.equalsMetier(
					sousTypeProduitJPA, sousTypeProduitSTPDifferent);
		final boolean sousTypeProduitDifferents2 
			= ConvertisseurJPAToMetier.equalsMetier(
					sousTypeProduitJPASTPDifferent, sousTypeProduit);
		final boolean equalsMetier 
			= ConvertisseurJPAToMetier.equalsMetier(
					sousTypeProduitJPA, sousTypeProduit);
		final boolean pasEqualsMetier 
			= ConvertisseurJPAToMetier.equalsMetier(
					sousTypeProduitJPA, sousTypeProduitDiff);
		
		// ASSERT - THEN
		/* null.equalsMetier(null) retourne true. */
		assertTrue(nullequalsMetierNull
				, "null.equalsMetier(null) doit retourner true : ");
		/* null.equalsMetier(nonNull) retourne false. */
		assertFalse(unSeulNull1
				, "null.equalsMetier(nonNull) doit retourner false : ");
		assertFalse(unSeulNull2
				, "nonNull.equalsMetier(null) doit retourner false : ");
		/* sousTypeProduitJPA.equalsMetier(sousTypeProduit 
		 * avec id different) retourne false. */
		assertFalse(unIdNull1
				, "sousTypeProduitJPAIdNull.equalsMetier(sousTypeProduit) doit retourner false : ");
		assertFalse(unIdNull2
				, "sousTypeProduitJPA.equalsMetier(sousTypeProduitIdNull) doit retourner false : ");
		assertFalse(idDifferents1
				, "sousTypeProduitJPA.equalsMetier(sousTypeProduitIdDifferent) doit retourner false ; ");
		assertFalse(idDifferents2
				, "sousTypeProduitJPAIdDifferent.equalsMetier(sousTypeProduit) doit retourner false ; ");
		/* sousTypeProduitJPA.equalsMetier(sousTypeProduit avec 
		 * TypeProduit different) retourne false. */
		assertFalse(sousTypeProduitDifferents1
				, "sousTypeProduitJPA.equalsMetier(sousTypeProduitSTPDifferent) doit retourner false : ");
		assertFalse(sousTypeProduitDifferents2
				, "sousTypeProduitJPASTPDifferent.equalsMetier(sousTypeProduit) doit retourner false : ");
		/* la comparaison sur la String SousTypeProduit fonctionne correctement. */
		assertTrue(equalsMetier, "sousTypeProduitJPA.equalsMetier(sousTypeProduit) doit retourner true : ");
		assertFalse(pasEqualsMetier, "sousTypeProduitJPA.equalsMetier(sousTypeProduitDiff) doit retourner false : ");
		
	} //___________________________________________________________________
	

	
	/**
	 * <div>
	 * <p>s'assure que : </p>
	 * <ul>
	 * <li>null.equalsMetier(null) retourne true.</li>
	 * <li>null.equalsMetier(nonNull) retourne false.</li>
	 * <li>ProduitJPA.equalsMetier(Produit avec id different) 
	 * retourne false.</li>
	 * <li>produitJPA.equalsMetier(produit avec sousTypeProduit different) retourne false.</li>
	 * <li>la comparaison sur la String produit fonctionne 
	 * correctement. </li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testEqualsMetierProduit() : vérifie le comportement général de la méthode equalsMetier(ProduitJPA pProduitJPA, Produit pProduit)")
	@Tag("equalsMetier")
	@Test
	public final void testEqualsMetierProduit() {
				
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE ConvertisseurJPAToMetierTest - méthode testEqualsMetierProduit() ********** ");
			System.out.println("CE TEST VERIFIE le fonctionnement de la méthode equalsMetier(ProduitJPA pProduitJPA, Produit pProduit).");
			System.out.println();				
		}
		
		//**** ARRANGE - GIVEN
		/* String */
		final String sousTypeProduitPasNullString = "sousTypeProduitPasNull";
		final String typeProduitPasNullString = "typeProduitPasNull";
		final String typeProduitString = TYPEPRODUIT;
		final String sousTypeProduitString = SOUSTYPEPRODUIT;
		final String typeProduitDifferentString = "typeProduit différent";
		final String produitString = "produit";
		
		/* typeProduitJPA. */
		final TypeProduitJPA typeProduitJPA 
			= new TypeProduitJPA(1L, typeProduitString);
		
		/* sousTypeProduitJPA. */
		final SousTypeProduitJPA sousTypeProduitJPA 
			= new SousTypeProduitJPA(1L, sousTypeProduitString, typeProduitJPA);
		final SousTypeProduitJPA sousTypeProduitJPADifferent 
		 = new SousTypeProduitJPA(2L, sousTypeProduitString, typeProduitJPA);
		
		/* produitJPA. */
		final ProduitJPA produitJPA 
			= new ProduitJPA(1L, produitString, sousTypeProduitJPA);
		final ProduitJPA produitJPANull = null;
		final ProduitJPA produitJPAIdDifferent 
			= new ProduitJPA(2L, produitString, sousTypeProduitJPA);
		final ProduitJPA produitJPASTPDifferent 
			= new ProduitJPA(1L, produitString, sousTypeProduitJPADifferent);
		
		/* typeProduit. */
		final TypeProduit typeProduit 
			= new TypeProduit(1L, typeProduitString);
		
		/* sousTypeProduit. */
		final SousTypeProduit sousTypeProduit 
			= new SousTypeProduit(1L, sousTypeProduitString, typeProduit);
		
		/* produit. */
		final Produit produit 
			= new Produit(1L, produitString, sousTypeProduit);
		final Produit produitNull = null;
		final Produit produitIdDifferent 
			= new Produit(2L, produitString, sousTypeProduit);
		final Produit produitDifferent 
			= new Produit(1L, "different", sousTypeProduit);
		
		// ACT - WHEN
		final boolean nullequalsMetierNull 
			= ConvertisseurJPAToMetier.equalsMetier(
					produitJPANull, produitNull);
		final boolean unSeulNull1 
			= ConvertisseurJPAToMetier.equalsMetier(
					produitJPANull, produit);
		final boolean unSeulNull2 
			= ConvertisseurJPAToMetier.equalsMetier(
					produitJPA, produitNull);
		final boolean idDifferents1 
			= ConvertisseurJPAToMetier.equalsMetier(
					produitJPAIdDifferent, produit);
		final boolean idDifferents2 
			= ConvertisseurJPAToMetier.equalsMetier(
					produitJPA, produitIdDifferent);
		final boolean stpDifferents 
			= ConvertisseurJPAToMetier.equalsMetier(
					produitJPASTPDifferent, produit);
		final boolean equalsMetier 
			= ConvertisseurJPAToMetier.equalsMetier(
					produitJPA, produit); 
		final boolean pasEqualsMetier 
			= ConvertisseurJPAToMetier.equalsMetier(
					produitJPA, produitDifferent);
		
		// ASSERT - THEN
		/* null.equalsMetier(null) retourne true. */
		assertTrue(nullequalsMetierNull
				, "null.equalsMetier(null) doit retourner true : ");
		/* null.equalsMetier(nonNull) retourne false. */
		assertFalse(unSeulNull1
				, "null.equalsMetier(nonNull) doit retourner false : ");
		assertFalse(unSeulNull2
				, "nonNull.equalsMetier(null) doit retourner false : ");
		/* ProduitJPA.equalsMetier(Produit avec id different) 
		 * retourne false. */
		assertFalse(idDifferents1
				, "produitJPAIdDifferent.equalsMetier(produit) doit retourner false : ");
		assertFalse(idDifferents2
				, "produitJPA.equalsMetier(produitIdDifferent) doit retourner false : ");
		/* produitJPA.equalsMetier(produit avec sousTypeProduit different) 
		 * retourne false. */
		assertFalse(stpDifferents
				, "produitJPASTPDifferent.equalsMetier(produit) doit retourner false : ");
		/* la comparaison sur la String produit fonctionne correctement. */
		assertTrue(equalsMetier, "produitJPA.equalsMetier(produit) doit retourner true : ");
		assertFalse(pasEqualsMetier, "produitJPA.equalsMetier(produitDifferent) doit retourner false : ");
		
	} //___________________________________________________________________
	

		
	/**
	 * Conversion démarrant par TypeProduitJPA : 
	 * graphe cohérent, instances uniques, pas de doublons
	 */
	@Tag("Conversion-Beton")
	@Test
	@DisplayName("Conversion démarrant par TypeProduitJPA : graphe cohérent, instances uniques, pas de doublons")
	public void testConversionDepuisTypeProduitJPA() {

		final ScenarioJPA scenario = creerScenarioVetement();

		final TypeProduitI tp = ConvertisseurJPAToMetier
				.typeProduitJPAToMetier(scenario.typeProduitVetement);

		assertNotNull(tp);
		assertEquals(1L, tp.getIdTypeProduit());
		assertEquals(VETEMENT, tp.getTypeProduit());

		// ---- STP : taille, unicité, rattachement au parent
		final List<? extends SousTypeProduitI> stps = tp
				.getSousTypeProduits();
		assertNotNull(stps);
		assertEquals(3, stps.size());

		assertIdentityUnique(stps, TP_STP);
		assertNoDuplicateOccurrences(stps, TP_STP);

		for (final SousTypeProduitI stp : stps) {

			assertNotNull(stp);
			assertSame(tp, stp.getTypeProduit(),
					"Chaque STP doit référencer le même TypeProduit (même instance)");

		}

		// ---- STP homme + produits
		final SousTypeProduitI stpHomme = findStpByName(tp,
				VETEMENT_POUR_HOMME);
		assertNotNull(stpHomme);

		final List<? extends ProduitI> produits = stpHomme.getProduits();
		assertNotNull(produits);
		assertEquals(4, produits.size());

		assertIdentityUnique(produits, STP_PRODUIT);
		assertNoDuplicateOccurrences(produits, STP_PRODUIT);

		for (final ProduitI p : produits) {

			assertNotNull(p);
			assertSame(stpHomme, p.getSousTypeProduit(),
					"Chaque Produit doit référencer le même STP (même instance)");
			assertSame(tp, p.getTypeProduit(),
					"Chaque Produit doit remonter au même TypeProduit (même instance)");

		}

		// ---- preuve supplémentaire du cache : le même Produit instance
		// est partagé
		final ProduitI p1 = findProduitByName(stpHomme,
				CHEMISES_MANCHES_LONGUES);
		assertNotNull(p1);

		// on récupère à nouveau via la liste et on vérifie l'identité
		final ProduitI p1bis = findProduitByName(stpHomme,
				CHEMISES_MANCHES_LONGUES);
		assertSame(p1, p1bis,
				"Le même Produit doit être une instance unique dans le graphe (cache/identity).");

	} //___________________________________________________________________



	/**
	 * Conversion démarrant par SousTypeProduitJPA : 
	 * stabilité, parent créé + rattachement fait une seule fois.
	 */
	@Tag(JPATOMETIER_BETON)
	@Test
	@DisplayName("Conversion démarrant par SousTypeProduitJPA : stabilité, parent créé + rattachement fait une seule fois")
	public void testConversionDepuisSousTypeProduitJPA() {

		final ScenarioJPA scenario = creerScenarioVetement();

		// Démarre par l'enfant (STP)
		final SousTypeProduitI stpHomme = ConvertisseurJPAToMetier
				.sousTypeProduitJPAToMetier(scenario.stpHomme);

		assertNotNull(stpHomme);
		assertEquals(1L, stpHomme.getIdSousTypeProduit());
		assertEquals(VETEMENT_POUR_HOMME, stpHomme.getSousTypeProduit());

		// Le parent doit exister et contenir ce STP (rattachement fait côté
		// parent)
		final TypeProduitI tp = stpHomme.getTypeProduit();
		assertNotNull(tp,
				"Le parent TypeProduit doit être stabilisé même si on démarre par STP");
		assertEquals(VETEMENT, tp.getTypeProduit());

		final List<? extends SousTypeProduitI> stps = tp
				.getSousTypeProduits();
		assertNotNull(stps);
		assertEquals(3, stps.size());

		// Le STP converti doit être EXACTEMENT la même instance que celle
		// présente dans la liste du parent
		final SousTypeProduitI stpHommeDansListe = findStpByName(tp,
				VETEMENT_POUR_HOMME);
		assertNotNull(stpHommeDansListe);
		assertSame(stpHomme, stpHommeDansListe,
				"Le STP doit être l'instance unique (cache) dans le graphe.");

		// Absence de double rattachement : pas de doublons
		assertIdentityUnique(stps, TP_STP);
		assertNoDuplicateOccurrences(stps, TP_STP);

		// Produits cohérents
		final List<? extends ProduitI> produits = stpHomme.getProduits();
		assertNotNull(produits);
		assertEquals(4, produits.size());

		assertIdentityUnique(produits, STP_PRODUIT);
		assertNoDuplicateOccurrences(produits, STP_PRODUIT);

		for (final ProduitI p : produits) {

			assertSame(stpHomme, p.getSousTypeProduit());
			assertSame(tp, p.getTypeProduit());

		}

	} //___________________________________________________________________



	/**
	 * Conversion démarrant par ProduitJPA : 
	 * stabilité, STP + parent créés, rattachements cohérents, 
	 * pas de doublons.
	 */
	@Tag(JPATOMETIER_BETON)
	@Test
	@DisplayName("Conversion démarrant par ProduitJPA : stabilité, STP + parent créés, rattachements cohérents, pas de doublons")
	public void testConversionDepuisProduitJPA() {

		final ScenarioJPA scenario = creerScenarioVetement();

		// Démarre par le leaf (Produit)
		final ProduitI p1 = ConvertisseurJPAToMetier
				.produitJPAToMetier(scenario.p1);

		assertNotNull(p1);
		assertEquals(1L, p1.getIdProduit());
		assertEquals(CHEMISES_MANCHES_LONGUES, p1.getProduit());

		final SousTypeProduitI stp = p1.getSousTypeProduit();
		assertNotNull(stp, "Le parent STP doit être créé");
		assertEquals(VETEMENT_POUR_HOMME, stp.getSousTypeProduit());

		final TypeProduitI tp = stp.getTypeProduit();
		assertNotNull(tp,
				"Le parent TypeProduit doit être créé/stabilisé même si on démarre par Produit");
		assertEquals(VETEMENT, tp.getTypeProduit());

		// Le produit doit être présent dans la liste des produits du STP et
		// être la même instance
		final ProduitI p1DansListe = findProduitByName(stp,
				CHEMISES_MANCHES_LONGUES);
		assertNotNull(p1DansListe);
		assertSame(p1, p1DansListe,
				"Le Produit doit être une instance unique dans le graphe (cache/identity).");

		// Le STP doit être présent dans le parent et être la même instance
		final SousTypeProduitI stpDansListeParent = findStpByName(tp,
				VETEMENT_POUR_HOMME);
		assertNotNull(stpDansListeParent);
		assertSame(stp, stpDansListeParent);

		// Pas de double rattachement : pas de doublons
		final List<? extends SousTypeProduitI> stps = tp
				.getSousTypeProduits();
		assertIdentityUnique(stps, TP_STP);
		assertNoDuplicateOccurrences(stps, TP_STP);

		final List<? extends ProduitI> produits = stp.getProduits();
		assertIdentityUnique(produits, STP_PRODUIT);
		assertNoDuplicateOccurrences(produits, STP_PRODUIT);

		// Cohérence bidirectionnelle
		assertSame(stp, p1.getSousTypeProduit());
		assertSame(tp, p1.getTypeProduit());
		assertSame(tp, stp.getTypeProduit());
		
	} //___________________________________________________________________



	/**
	 * Vérifie le cas "Hibernate lazy" : collection parent non chargée
	 * (tpJPA.sousTypeProduits ne contient pas le stpJPA),
	 * tout en démarrant la conversion depuis un ProduitJPA.
	 *
	 * @throws Exception
	 */
	@Tag(JPATOMETIER_BETON)
	@Test
	@DisplayName("Lazy: parent.sousTypeProduits non chargé -> conversion depuis ProduitJPA reste cohérente")
	public void testLazyParentCollectionNonChargeeDepuisProduit() throws Exception {

		final TypeProduitJPA tpJPA = new TypeProduitJPA(VETEMENT);
		tpJPA.setIdTypeProduit(1L);

		final SousTypeProduitJPA stpJPA = new SousTypeProduitJPA(VETEMENT_POUR_HOMME);
		stpJPA.setIdSousTypeProduit(10L);

		final ProduitJPA pJPA = new ProduitJPA(CHEMISES_MANCHES_LONGUES);
		pJPA.setIdProduit(100L);

		/* Simule "LAZY non initialisé" :
		 * on relie les parents/enfants via champs, sans setters canoniques,
		 * et on laisse tpJPA.sousTypeProduits vide.
		 */
		setField(stpJPA, TYPEPRODUIT, tpJPA);
		setField(pJPA, SOUSTYPEPRODUIT, stpJPA);

		final List<SousTypeProduitI> sousTypesVides = new ArrayList<SousTypeProduitI>();
		setField(tpJPA, "sousTypeProduits", sousTypesVides);

		/* On ne met PAS stpJPA dans tpJPA.sousTypeProduits. */

		final ProduitI produitMetier = ConvertisseurJPAToMetier.produitJPAToMetier(pJPA);

		assertNotNull(produitMetier);
		assertEquals(100L, produitMetier.getIdProduit());
		assertEquals(CHEMISES_MANCHES_LONGUES, produitMetier.getProduit());

		final SousTypeProduitI stpMetier = produitMetier.getSousTypeProduit();
		assertNotNull(stpMetier);
		assertEquals(10L, stpMetier.getIdSousTypeProduit());
		assertEquals(VETEMENT_POUR_HOMME, stpMetier.getSousTypeProduit());

		final TypeProduitI tpMetier = stpMetier.getTypeProduit();
		assertNotNull(tpMetier, "Le TypeProduit métier doit être stabilisé même si la collection parent est non chargée.");
		assertEquals(1L, tpMetier.getIdTypeProduit());
		assertEquals(VETEMENT, tpMetier.getTypeProduit());

		/* Cohérence d'identité (même instance) */
		assertSame(tpMetier, produitMetier.getTypeProduit());
		assertSame(tpMetier, stpMetier.getTypeProduit());

		/* Et le STP doit être présent dans le parent (rattachement lazy-safe). */
		assertTrue(tpMetier.getSousTypeProduits().contains(stpMetier),
				"Le parent doit contenir le STP après conversion (rattachement lazy-safe).");
		assertIdentityUnique(tpMetier.getSousTypeProduits(), TP_STP);
		assertNoDuplicateOccurrences(tpMetier.getSousTypeProduits(), TP_STP);
		
	} //___________________________________________________________________

	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la conversion avec des collections vides (ex: sousTypeProduits = []).</p>
	 * <p>Garantit que la conversion ne plante pas et retourne une liste vide.</p>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@Tag(JPATOMETIER_BETON)
	@DisplayName("Conversion avec collections vides : ne doit pas planter")
	@Test
	public final void testConversionAvecCollectionsVides() {
	    // **********************************
	    // AFFICHAGE DANS LE TEST ou NON
	    final boolean affichage = false;
	    // **********************************

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("********** CLASSE ConvertisseurJPAToMetierTest - méthode testConversionAvecCollectionsVides() **********");
	        System.out.println("CE TEST VERIFIE QUE LA CONVERSION NE PLANTE PAS AVEC DES COLLECTIONS VIDES.");
	        System.out.println();
	    }

	    //**** ARRANGE - GIVEN
	    final TypeProduitJPA tpVide = new TypeProduitJPA();
	    tpVide.setTypeProduit("Informatique");
	    tpVide.setSousTypeProduits(new ArrayList<>()); // Collection vide

	    //**** ACT - WHEN
	    final TypeProduit tpMetier = ConvertisseurJPAToMetier.typeProduitJPAToMetier(tpVide);

	    //**** ASSERT - THEN
	    assertThat(tpMetier).isNotNull();
	    assertThat(tpMetier.getSousTypeProduits()).isEmpty();
	    assertThat(tpMetier.getTypeProduit()).isEqualTo("Informatique");
	    
	} //___________________________________________________________________

	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la conversion avec des noms dupliqués.</p>
	 * <p>Garantit que les doublons (basés sur le nom) sont éliminés conformément au contrat d'unicité.</p>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@Tag(JPATOMETIER_BETON)
	@DisplayName("Conversion avec noms dupliqués : doit éliminer les doublons")
	@Test
	public final void testConversionAvecNomsDupliques() {
	    // **********************************
	    // AFFICHAGE DANS LE TEST ou NON
	    final boolean affichage = false;
	    // **********************************

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("********** CLASSE ConvertisseurJPAToMetierTest - méthode testConversionAvecNomsDupliques() **********");
	        System.out.println("CE TEST VERIFIE QUE LES DOUBLONS SONT ELIMINES CONFORMEMENT AU CONTRAT D'UNICITE.");
	        System.out.println();
	    }

	    //**** ARRANGE - GIVEN
	    final TypeProduitJPA tpJPA = new TypeProduitJPA();
	    tpJPA.setTypeProduit("Vêtement");

	    /* Deux SousTypeProduitJPA avec le même nom (doublon). */
	    final SousTypeProduitJPA stpJPA1 = new SousTypeProduitJPA();
	    stpJPA1.setSousTypeProduit(VETEMENT_POUR_HOMME);
	    stpJPA1.setTypeProduit(tpJPA);

	    final SousTypeProduitJPA stpJPA2 = new SousTypeProduitJPA();
	    stpJPA2.setSousTypeProduit(VETEMENT_POUR_HOMME);
	    stpJPA2.setTypeProduit(tpJPA);

	    /* Ajout des deux instances (seule la première sera conservée). */
	    final List<SousTypeProduitJPA> sousTypeProduits = new ArrayList<>();
	    sousTypeProduits.add(stpJPA1);
	    sousTypeProduits.add(stpJPA2);
	    
	    /* Utilisation d'une copie modifiable 
         * pour éviter ConcurrentModificationException. */
	    tpJPA.setSousTypeProduits(Collections.unmodifiableList(
	    		new ArrayList<>(sousTypeProduits)));

	    //**** ACT - WHEN
	    final TypeProduit tpMetier 
	    	= ConvertisseurJPAToMetier.typeProduitJPAToMetier(tpJPA);

	    //**** ASSERT - THEN
	    /* Vérifie que seul un exemplaire est conservé (unicité). */
	    assertThat(tpMetier.getSousTypeProduits()).hasSize(1);

	    /* Vérifie que le nom est correct. */
	    assertThat(tpMetier.getSousTypeProduits().get(0).getSousTypeProduit())
	        .isEqualTo(VETEMENT_POUR_HOMME);
	    
	} //___________________________________________________________________


	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste les performances de conversion pour de grandes collections.</p>
	 * <p>Garantit que la conversion reste performante même 
	 * avec un grand nombre d'éléments.</p>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@Tag("JPAToMetier-Beton")
	@DisplayName("Performance : conversion de 1000 SousTypeProduitJPA")
	@Test
	public final void testPerformanceGrandeCollection() {
		
	    // **********************************
	    // AFFICHAGE DANS LE TEST ou NON
	    final boolean affichage = false;
	    // **********************************

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("********** CLASSE ConvertisseurJPAToMetierTest - méthode testPerformanceGrandeCollection() **********");
	        System.out.println("CE TEST VERIFIE LES PERFORMANCES DE CONVERSION POUR DE GRANDES COLLECTIONS.");
	        System.out.println();
	    }

	    //**** ARRANGE - GIVEN
	    final TypeProduitJPA tpJPA = new TypeProduitJPA();
	    tpJPA.setTypeProduit("Électronique");

	    /* Construction d'une liste de 1000 SousTypeProduitJPA. */
	    final List<SousTypeProduitJPA> sousTypeProduits = new ArrayList<>(1000);
	    for (int i = 0; i < 1000; i++) {
	        final SousTypeProduitJPA stpJPA = new SousTypeProduitJPA();
	        stpJPA.setSousTypeProduit("Produit " + i);
	        stpJPA.setTypeProduit(tpJPA);
	        sousTypeProduits.add(stpJPA);
	    }
	    
	    /* Utilisation d'une copie modifiable pour 
         * éviter ConcurrentModificationException. */
	    tpJPA.setSousTypeProduits(Collections.unmodifiableList(
	    		new ArrayList<>(sousTypeProduits)));

	    //**** ACT - WHEN
	    final long start = System.currentTimeMillis();
	    final TypeProduit tpMetier 
	    	= ConvertisseurJPAToMetier.typeProduitJPAToMetier(tpJPA);
	    final long end = System.currentTimeMillis();

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println("Temps de conversion : " + (end - start) + " ms");
	        System.out.println("Nombre de SousTypeProduit convertis : " 
	        		+ tpMetier.getSousTypeProduits().size());
	    }

	    //**** ASSERT - THEN
	    assertThat(tpMetier).isNotNull();
	    assertThat(tpMetier.getSousTypeProduits()).hasSize(1000);
	    assertThat(end - start).as("Le temps de conversion doit être raisonnable")
	                           .isLessThan(1000L); // Seuil arbitraire (ajustable)
	    
	} //___________________________________________________________________

	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la robustesse du cache en cas d'accès concurrents.</p>
	 * <p>Garantit que le convertisseur gère correctement les conversions
	 * simultanées sans conflits.</p>
	 * <p>Utilise un contexte de conversion partagé pour tous les threads
	 * afin de vérifier l'unicité des instances.</p>
	 * <p>Ce test est <b>autonome</b> : il crée son propre scénario de test
	 * et ne dépend d'aucun autre test ou méthode externe.</p>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@Tag("JPAToMetier-Beton")
	@DisplayName("Concurrences : doit gérer les conversions multi-thread")
	@Test
	public final void testConcurrencesDansLesMaps() {
	    // **********************************
	    // AFFICHAGE DANS LE TEST ou NON
	    final boolean affichage = false;
	    // **********************************

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("********** CLASSE ConvertisseurJPAToMetierTest - méthode testConcurrencesDansLesMaps() **********");
	        System.out.println("CE TEST VERIFIE LA ROBUSTESSE DU CONVERTISSEUR EN CAS D'ACCES CONCURRENTS.");
	        System.out.println();
	    }

	    //**** ARRANGE - GIVEN
	    // Création d'un scénario autonome pour ce test
	    final TypeProduitJPA tpJPA = new TypeProduitJPA();
	    tpJPA.setIdTypeProduit(1L);
	    tpJPA.setTypeProduit("Test Concurrence");

	    //**** ACT - WHEN
	    /* Tableau pour stocker les résultats des conversions concurrentes. */
	    final TypeProduit[] results = new TypeProduit[10];

	    // Lancement de 10 threads pour convertir le même TypeProduitJPA en parallèle.
	    final int nombreThreads = 10;
	    final Thread[] threads = new Thread[nombreThreads];

	    // Conversion séquentielle pour obtenir une instance de référence
	    final TypeProduit reference = ConvertisseurJPAToMetier.typeProduitJPAToMetier(tpJPA);

	    for (int i = 0; i < nombreThreads; i++) {
	        final int index = i;
	        threads[i] = new Thread(() -> {
	            results[index] = ConvertisseurJPAToMetier.typeProduitJPAToMetier(tpJPA);
	        });
	        threads[i].start();
	    }

	    // Attente de la fin de tous les threads.
	    for (final Thread thread : threads) {
	        try {
	            thread.join();
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	            fail("Thread interrupted during test");
	        }
	    }

	    //**** ASSERT - THEN
	    /* Vérifie que tous les résultats sont identiques à la référence. */
	    for (int i = 0; i < results.length; i++) {
	        assertNotNull(results[i], "Aucun résultat ne doit être null");
	        assertEquals(reference.getIdTypeProduit(), results[i].getIdTypeProduit(), "Les ID doivent être identiques");
	        assertEquals(reference.getTypeProduit(), results[i].getTypeProduit(), "Les noms doivent être identiques");
	    }
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>Teste la stabilité d'<b>identity</b> (cache) en concurrence :</p>
	 * <ul>
	 * <li>garantit que plusieurs threads convertissant le <b>même</b> TypeProduitJPA retournent la <b>même instance</b> métier.</li>
	 * <li>utilise un <b>ExecutorService</b> avec timeout (stratégie projet).</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings({ "unused", "resource" })
	@Tag(JPATOMETIER_BETON)
	@Test
	@DisplayName("Concurrence: mêmes conversions -> même instance (identity/cache)")
	public final void testConcurrenceIdentityCacheTypeProduit() throws Exception {
		
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE ConvertisseurJPAToMetierTest - méthode testConcurrenceIdentityCacheTypeProduit() ********** ");
			System.out.println("CE TEST VERIFIE EN CONCURRENCE QUE LE CACHE RETOURNE LA MEME INSTANCE METIER.");
			System.out.println();
		}
		
		// ARRANGE - GIVEN
		final ScenarioJPA scenario = creerScenarioVetement();
		final TypeProduitJPA tpJPA = scenario.typeProduitVetement;
		
		final TypeProduit reference = ConvertisseurJPAToMetier.typeProduitJPAToMetier(tpJPA);
		assertNotNull(reference, "précondition : la référence métier ne doit pas être null : ");
		
		final int nbThreads = 10;
		final ExecutorService executor = Executors.newFixedThreadPool(nbThreads);
		
		final List<Callable<TypeProduit>> tasks = new ArrayList<>();
		
		for (int i = 0; i < nbThreads; i++) {
			
			tasks.add(new Callable<TypeProduit>() {
				@Override
				public TypeProduit call() throws Exception {
					return ConvertisseurJPAToMetier.typeProduitJPAToMetier(tpJPA);
				}
			});
			
		}
		
		// ACT - WHEN
		final List<Future<TypeProduit>> futures = executor.invokeAll(tasks, 5, TimeUnit.SECONDS);
		executor.shutdown();
		
		// ASSERT - THEN
		
		for (final Future<TypeProduit> future : futures) {
			
			assertTrue(future.isDone(), "tâche terminée : ");
			
			final TypeProduit resultat = future.get();
			assertNotNull(resultat, "le résultat ne doit pas être null : ");
			
			/* Identity/cache : même instance attendue. */
			assertSame(reference, resultat, "identity/cache : tous les threads doivent récupérer la même instance métier : ");
			
		}
		
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>Teste le comportement "LAZY" côté JPA lorsque <b>TypeProduitJPA.sousTypeProduits</b> est <b>vide</b>.</p>
	 * <ul>
	 * <li>La conversion depuis un <b>ProduitJPA</b> doit stabiliser le graphe parent ↔ enfant.</li>
	 * <li>Le <b>TypeProduit</b> métier retourné doit contenir le <b>SousTypeProduit</b> métier (pas de wipe).</li>
	 * </ul>
	 * </div>
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	@Tag(JPATOMETIER_BETON)
	@Test
	@DisplayName("Lazy: TypeProduitJPA.sousTypeProduits vide -> conversion stabilise le graphe sans wipe")
	public final void testLazyTypeProduitCollectionVideDepuisProduit() throws Exception {
		
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE ConvertisseurJPAToMetierTest - méthode testLazyTypeProduitCollectionVideDepuisProduit() ********** ");
			System.out.println("CE TEST VERIFIE LE COMPORTEMENT LAZY QUAND TypeProduitJPA.sousTypeProduits EST VIDE.");
			System.out.println();
		}
		
		// ARRANGE - GIVEN
		final TypeProduitJPA tpJPA = new TypeProduitJPA(VETEMENT);
		tpJPA.setIdTypeProduit(1L);
		
		final SousTypeProduitJPA stpJPA = new SousTypeProduitJPA(VETEMENT_POUR_HOMME);
		stpJPA.setIdSousTypeProduit(10L);
		
		final ProduitJPA pJPA = new ProduitJPA("chemise manches longues");
		pJPA.setIdProduit(100L);
		
		/* Simule "lazy": collection vide (le getter ne supporte pas null). */
		setField(tpJPA, "sousTypeProduits", new ArrayList<SousTypeProduitJPA>());
		
		/* Force le graphe minimal sans utiliser les setters canoniques. */
		setField(stpJPA, TYPEPRODUIT, tpJPA);
		setField(pJPA, "sousTypeProduit", stpJPA);
		
		// ACT - WHEN
		final ProduitI produitMetier = ConvertisseurJPAToMetier.produitJPAToMetier(pJPA);
		
		// ASSERT - THEN
		assertNotNull(produitMetier, "Le Produit métier ne doit pas être null : ");
		assertEquals(100L, produitMetier.getIdProduit(), "idProduit converti : ");
		assertEquals("chemise manches longues", produitMetier.getProduit(), "produit (String) converti : ");
		
		final SousTypeProduitI stpMetier = produitMetier.getSousTypeProduit();
		assertNotNull(stpMetier, "Le SousTypeProduit métier ne doit pas être null : ");
		assertEquals(10L, stpMetier.getIdSousTypeProduit(), "idSousTypeProduit converti : ");
		assertEquals(VETEMENT_POUR_HOMME, stpMetier.getSousTypeProduit(), "sousTypeProduit (String) converti : ");
		
		final TypeProduitI tpMetier = stpMetier.getTypeProduit();
		assertNotNull(tpMetier, "Le TypeProduit métier ne doit pas être null : ");
		assertEquals(1L, tpMetier.getIdTypeProduit(), "idTypeProduit converti : ");
		assertEquals(VETEMENT, tpMetier.getTypeProduit(), "typeProduit (String) converti : ");
		
		/* Identity stabilisée. */
		assertSame(tpMetier, produitMetier.getTypeProduit(), "Le Produit doit remonter au même TypeProduit (même instance) : ");
		
		/* Pas de wipe : le parent doit contenir l'enfant. */
		assertNotNull(tpMetier.getSousTypeProduits(), "La liste sousTypeProduits métier ne doit pas être null : ");
		assertTrue(tpMetier.getSousTypeProduits().contains(stpMetier), "Le TypeProduit doit contenir le STP après conversion : ");
		
	} //___________________________________________________________________


	
	/**
	 * <div>
	 * <p>Teste le cache partagé du convertisseur sur <b>deux appels</b> successifs.</p>
	 * <ul>
	 * <li>Deux conversions du <b>même</b> TypeProduitJPA doivent retourner la <b>même instance</b> métier (cache/identity).</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@Tag(JPATOMETIER_BETON)
	@Test
	@DisplayName("Cache partagé: 2 appels typeProduitJPAToMetier(même JPA) -> même instance métier")
	public final void testCachePartageEntreDeuxAppelsTypeProduit() {

		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************

		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE ConvertisseurJPAToMetierTest - méthode testCachePartageEntreDeuxAppelsTypeProduit() ********** ");
			System.out.println("CE TEST VERIFIE LE CACHE PARTAGE (IDENTITY) ENTRE DEUX APPELS SUCCESSIFS.");
			System.out.println();
		}

		// ARRANGE - GIVEN
		final ScenarioJPA scenario = creerScenarioVetement();
		final TypeProduitJPA tpJPA = scenario.typeProduitVetement;

		// ACT - WHEN
		final TypeProduit tpMetier1 = ConvertisseurJPAToMetier.typeProduitJPAToMetier(tpJPA);
		final TypeProduit tpMetier2 = ConvertisseurJPAToMetier.typeProduitJPAToMetier(tpJPA);

		// ASSERT - THEN
		assertNotNull(tpMetier1, "tpMetier1 ne doit pas être null : ");
		assertNotNull(tpMetier2, "tpMetier2 ne doit pas être null : ");
		assertSame(tpMetier1, tpMetier2, "Deux appels sur la même Entity doivent retourner la même instance métier (cache/identity) : ");

	} //___________________________________________________________________


	
	/**
	 * <div>
	 * <p>Teste la méthode privée <b>requireJPA(Object, Class, String)</b> par reflection.</p>
	 * <ul>
	 * <li>Objet null &rarr; IllegalStateException.</li>
	 * <li>Mauvaise implémentation &rarr; IllegalStateException contenant <b>IMPLEMENTATION_NON_JPA</b>.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@Tag(JPATOMETIER_BETON)
	@Test
	@DisplayName("requireJPA(...) : null et mauvaise implémentation -> IllegalStateException")
	public final void testRequireJPA() {

		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************

		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE ConvertisseurJPAToMetierTest - méthode testRequireJPA() ********** ");
			System.out.println("CE TEST VERIFIE requireJPA(...) (NULL + MAUVAISE IMPLEMENTATION).");
			System.out.println();
		}

		// ARRANGE - GIVEN
		final String contexte = "TEST requireJPA";
		final Class<?> clazz = ConvertisseurJPAToMetier.class;

		// ACT - WHEN / ASSERT - THEN
		try {
			final java.lang.reflect.Method m = clazz.getDeclaredMethod(
					"requireJPA",
					Object.class,
					Class.class,
					String.class);
			m.setAccessible(true); // NOPMD by danyl on 18/02/2026 10:43

			/* Cas objet null. */
			try {
				m.invoke(null, null, TypeProduitJPA.class, contexte);
				fail("requireJPA(null, ...) doit lever IllegalStateException : ");
			} catch (final java.lang.reflect.InvocationTargetException e) {
				assertNotNull(e.getCause(), "La cause doit exister : ");
				assertTrue(e.getCause() instanceof IllegalStateException, "La cause doit être IllegalStateException : ");
				assertTrue(e.getCause().getMessage().contains("objet null"), "Le message doit mentionner \"objet null\" : ");
			}

			/* Cas mauvaise implémentation. */
			try {
				m.invoke(null, new Object(), TypeProduitJPA.class, contexte);
				fail("requireJPA(mauvaise implémentation, ...) doit lever IllegalStateException : ");
			} catch (final java.lang.reflect.InvocationTargetException e) {
				assertNotNull(e.getCause(), "La cause doit exister : ");
				assertTrue(e.getCause() instanceof IllegalStateException, "La cause doit être IllegalStateException : ");
				assertTrue(
						e.getCause().getMessage().contains(ConvertisseurJPAToMetier.IMPLEMENTATION_NON_JPA),
						"Le message doit contenir IMPLEMENTATION_NON_JPA : ");
				assertTrue(
						e.getCause().getMessage().contains("contexte=" + contexte),
						"Le message doit contenir le contexte : ");
			}

		} catch (final Exception e) {
			fail("Reflection sur requireJPA(...) a échoué : " + e.getMessage());
		}

	} //___________________________________________________________________
	


	// ==========================================================
	// Helpers - Scénario JPA
	// ==========================================================
	
	
	
	/**
	 */
	private static final class ScenarioJPA {

		/**
		 * 
		 */
		private TypeProduitJPA typeProduitVetement;

		/**
		 * 
		 */
		private SousTypeProduitJPA stpHomme;

		/**
		 * 
		 */
		private SousTypeProduitJPA stpFemme;

		/**
		 * 
		 */
		private SousTypeProduitJPA stpEnfant;

		/**
		 * 
		 */
		private ProduitJPA p1;

		/**
		 * 
		 */
		private ProduitJPA p2;

		/**
		 * 
		 */
		private ProduitJPA p3;

		/**
		 * 
		 */
		private ProduitJPA p4;

	}



	/**
	 * <div>
	 * <p>compare les valeurs equalsMetier d'un TypeProduit Entity JPA 
	 * et d'un TypeProduit objet Métier et ne retourne true que 
	 * si elles sont toutes égales.</p>
	 * <ul>
	 * <li>retourne true si les deux paramètres sont null.</li>
	 * <li>retourne false si un seul des paramètres est null.</li>
	 * <li>compare les idTypeProduit et retourne false 
	 * s'ils ne sont pas égaux.</li>
	 * <li>compare les typeProduit (String) et retourne false 
	 * s'ils ne sont pas égaux.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pTypeProduitJPA
	 * @param pTypeProduit
	 * @return boolean : true si toutes les valeurs sont égales.
	 */
	private boolean memeEqualsTypeProduit(
			final TypeProduitJPA pTypeProduitJPA
				, final TypeProduit pTypeProduit) {
		
		if (pTypeProduitJPA == null) {
			if (pTypeProduit != null) {
				/* retourne false si un seul des paramètres est null. */
				return false;
			}
			
			/* retourne true si les deux paramètres sont null. */
			return true;
		}
		
		/* retourne false si un seul des paramètres est null. */
		if (pTypeProduit == null) {
			return false;
		}
		
		/* idTypeProduit. */
		final Long idTypeProduitJPA = pTypeProduitJPA.getIdTypeProduit();
		final Long idTypeProduit = pTypeProduit.getIdTypeProduit();
		
		/* compare les idTypeProduit et 
		 * retourne false s'ils ne sont pas égaux. */
		if (idTypeProduitJPA != idTypeProduit) {
			return false;
		}
		
		/* typeProduit. */
		final String typeProduitJPA = pTypeProduitJPA.getTypeProduit();
		final String typeProduit = pTypeProduit.getTypeProduit();
		
		/* compare les typeProduit (String) de niveau 1 (TypeProduit) 
		 * et retourne false s'ils ne sont pas égaux. */
		if (!Strings.CS.equals(typeProduitJPA, typeProduit)) {
			return false;
		}
		
		return true;
		
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>compare les valeurs equalsMetier de SousTypeProduit 
	 * d'une Entity JPA de type 
	 * <code><span style="font-weight:bold;">SousTypeProduitJPA</span>
	 *  pSousTypeProduitJPA</code> et d'un objet métier 
	 *  équivalent de type <code><span style="font-weight:bold;">
	 *  SousTypeProduit</span> pSousTypeProduit</code>.</p>
	 *  <p>le paramètre de type <code><span style="font-weight:bold;">
	 *  SousTypeProduit</span> SousTypeProduit</code> est censé être 
	 *  encapsulé dans un <code><span style="font-weight:bold;">
	 *  TypeProduit</span> pTypeProduit</code>.</p>
	 *  <ul>
	 *  <li>retourne false si pTypeProduit == null.</li>
	 *  <li>retourne false si pSousTypeProduitJPA == null.</li>
	 *  <li>retourne false si pSousTypeProduit == null.</li>
	 *  <li>compare les <code><span style="font-weight:bold;">
	 *  idSousTypeProduit</code> de niveau 2 (SousTypeProduit) 
	 *  et retourne false s'ils ne sont pas égaux.</li>
	 *  <li>vérifie que le <code><span style="font-weight:bold;">
	 *  typeProduit</code> du paramètre pSousTypeProduit est l'instance 
	 *  <code>pTypeProduit</code> passée en paramètre. 
	 *  Retourne false si ce n'est pas le cas.</li>
	 *  <li>compare les <code style="font-weight:bold;">
	 *  sousTypeProduit</code> (String) et retourne false 
	 *  s'ils ne sont pas égaux.</li>
	 *  <li>retourne true si toutes les valeurs sont égales.</li>
	 *  </ul>
	 * </div>
	 *
	 * @param pTypeProduit : TypeProduit
	 * @param pSousTypeProduitJPA : SousTypeProduitJPA
	 * @param pSousTypeProduit : SousTypeProduit
	 * 
	 * @return boolean : true si toutes les valeurs sont égales.
	 */
	private boolean memeEqualsSousTypeProduit(
			final TypeProduit pTypeProduit
			, final SousTypeProduitJPA pSousTypeProduitJPA
				, final SousTypeProduit pSousTypeProduit) {
		
		/* retourne false si pTypeProduit == null. */
		if (pTypeProduit == null) {
			return false;
		}
		
		/* retourne false si pSousTypeProduitJPA == null. */
		if (pSousTypeProduitJPA == null) {
			return false;
		}
		
		/* retourne false si pSousTypeProduit == null. */
		if (pSousTypeProduit == null) {
			return false;
		}
		
		/* compare les idSousTypeProduit de niveau 2 (SousTypeProduit) 
		 * et retourne false s'ils ne sont pas égaux. */
		if (pSousTypeProduitJPA.getIdSousTypeProduit() 
				!= pSousTypeProduit.getIdSousTypeProduit()) {
			return false;
		}
		
		/* vérifie que le typeProduit du paramètre pSousTypeProduit 
		 * est l'instance pTypeProduit passée en paramètre. */
		/* retourne false si ce n'est pas le cas. */
		if (pTypeProduit != pSousTypeProduit.getTypeProduit()) {
			return false;
		}
		
		/* compare les sousTypeProduit (String) et retourne 
		 * false s'ils ne sont pas égaux.. */
		if (!Strings.CS.equals(
				pSousTypeProduitJPA.getSousTypeProduit()
				, pSousTypeProduit.getSousTypeProduit())) {
			return false;
		}
		
		/* retourne true si toutes les valeurs sont égales. */ 
		return true;
		
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>retourne une String formatée pour l'affichage 
	 * d'un TypeProduitJPA</p>
	 * <ul>
	 * <li>retourne null si pTypeProduitJPA == null.</li>
	 * <li>affiche le TypeProduitJPA</li>
	 * <li>affiche la liste des SousTypeProduitJPA contenus 
	 * dans le TypeProduitJPA</li>
	 * <li>affiche pour chaque SousTypeProduitJPA la liste des ProduitJPA qu'il contient.</li>
	 * </ul>
	 * </div>
	 * 
	 * <div>
	 * <p style="text-decoration:underline;">Exemple d'affichage : </p>
	 * <pre>******* TypeProduitJPA : vêtement *******
	 * [idTypeProduit : 1 - typeProduit : vêtement  ]
	 * 
	 * ******* sousTypeProduitsJPA du TypeProduitJPA : vêtement
	 * [idSousTypeProduit : 1 - sousTypeProduit : vêtement pour homme  - [idTypeProduit du TypeProduit dans le SousTypeProduit : 1 - typeProduitString du TypeProduit dans le SousTypeProduit : vêtement     ]]
	 * ***** liste des produits dans le sousProduitJPA : vêtement pour homme
	 * [idProduit dans produits du SousTypeProduit : 1 - produit dans produits du SousTypeProduit : chemise à manches longues pour homme     - sousTypeProduit dans le produit : vêtement pour homme ]
	 * [idProduit dans produits du SousTypeProduit : 2 - produit dans produits du SousTypeProduit : chemise à manches courtes pour homme     - sousTypeProduit dans le produit : vêtement pour homme ]
	 * [idProduit dans produits du SousTypeProduit : 3 - produit dans produits du SousTypeProduit : sweatshirt pour homme                    - sousTypeProduit dans le produit : vêtement pour homme ]
	 * [idProduit dans produits du SousTypeProduit : 4 - produit dans produits du SousTypeProduit : teeshirt pour homme                      - sousTypeProduit dans le produit : vêtement pour homme ]
	 * 
	 * [idSousTypeProduit : 2 - sousTypeProduit : vêtement pour femme  - [idTypeProduit du TypeProduit dans le SousTypeProduit : 1 - typeProduitString du TypeProduit dans le SousTypeProduit : vêtement     ]]
	 * ***** liste des produits dans le sousProduitJPA : vêtement pour femme
	 * null
	 * 
	 * [idSousTypeProduit : 3 - sousTypeProduit : vêtement pour enfant - [idTypeProduit du TypeProduit dans le SousTypeProduit : 1 - typeProduitString du TypeProduit dans le SousTypeProduit : vêtement     ]]
	 * ***** liste des produits dans le sousProduitJPA : vêtement pour enfant
	 * null
	 * </pre>
	 * </div>
	 *
	 * @param pTypeProduitJPA : TypeProduitJPA : Entity JPA à afficher
	 * @return String
	 */
	private String afficherTypeProduitFormate(
			final TypeProduitJPA pTypeProduitJPA) {
		
		/* retourne null si pTypeProduitJPA == null. */
		if (pTypeProduitJPA == null) {
			return null;
		}
		
		final Long idTypeProduitJPA = pTypeProduitJPA.getIdTypeProduit();
		final String typeProduitStringJPA 
			= pTypeProduitJPA.getTypeProduit();
		final List<? extends SousTypeProduitI> sousTypeProduitsJPA 
			= pTypeProduitJPA.getSousTypeProduits();
		
		final StringBuilder stb = new StringBuilder();
		
		stb.append("******* TypeProduitJPA : ");
		stb.append(typeProduitStringJPA);
		stb.append(" *******");
		stb.append(SAUT_DE_LIGNE);
				
		final String pres1 
			= String.format("[idTypeProduit : %-1s - typeProduit : %-10s]"
					, idTypeProduitJPA, typeProduitStringJPA);
		stb.append(pres1);
		stb.append(SAUT_DE_LIGNE);
		stb.append(SAUT_DE_LIGNE);
		
		stb.append("******* sousTypeProduitsJPA du TypeProduitJPA : ");
		stb.append(typeProduitStringJPA);
		stb.append(SAUT_DE_LIGNE);
		
		if (sousTypeProduitsJPA == null) {
			stb.append(NULL);
		} else {
			
			for (final SousTypeProduitI 
					sousTypeProduitJPA : sousTypeProduitsJPA) {
				
				final Long idSousTypeProduitJPA 
					= sousTypeProduitJPA.getIdSousTypeProduit();
				final String sousTypeProduitJPAString 
					= sousTypeProduitJPA.getSousTypeProduit();
				final TypeProduitJPA typeProduitJPAduSousTypeProduit 
					= (TypeProduitJPA) sousTypeProduitJPA.getTypeProduit();
				
				final List<? extends ProduitI> produitsDansSousProduit 
					= sousTypeProduitJPA.getProduits();
				
				Long idTypeProduitJPAduSousTypeProduit = null;
				String typeProduitJPAduSousTypeProduitString = null;
				
				if (typeProduitJPAduSousTypeProduit != null) {
					idTypeProduitJPAduSousTypeProduit 
						= typeProduitJPAduSousTypeProduit.getIdTypeProduit();
					typeProduitJPAduSousTypeProduitString 
						= typeProduitJPAduSousTypeProduit.getTypeProduit();
				}
				
				final String pres2 
				= String
				.format("[idSousTypeProduit : %-1s "
						+ "- sousTypeProduit : %-20s "
						+ "- [idTypeProduit du TypeProduit dans le SousTypeProduit : %-1s "
						+ "- typeProduitString du TypeProduit dans le SousTypeProduit : %-13s]]"
						, idSousTypeProduitJPA
						, sousTypeProduitJPAString
						, idTypeProduitJPAduSousTypeProduit
						, typeProduitJPAduSousTypeProduitString);
				
				stb.append(pres2);
				stb.append(SAUT_DE_LIGNE);
				
				stb.append("***** liste des produits dans le sousProduitJPA : ");
				stb.append(sousTypeProduitJPAString);
				stb.append(SAUT_DE_LIGNE);
				if (produitsDansSousProduit == null) {
					stb.append(NULL);
					stb.append(SAUT_DE_LIGNE);
				} else {
					
					for (final ProduitI produitJPA : produitsDansSousProduit) {
						
						final Long idProduitJPA = produitJPA.getIdProduit();
						final String produitJPAString = produitJPA.getProduit();
						final SousTypeProduitJPA sousTypeProduitJPAProduit 
							= (SousTypeProduitJPA) produitJPA.getSousTypeProduit();
						
						String sousTypeProduitJPAProduitString = null;
						
						if (sousTypeProduitJPAProduit != null) {
							sousTypeProduitJPAProduitString 
								= sousTypeProduitJPAProduit.getSousTypeProduit();
						}
						
						stb.append('\t');
						
						final String presProduit 
						= String.format("[idProduit dans produits du SousTypeProduit : %-1s - "
								+ "produit dans produits du SousTypeProduit : %-40s - "
								+ "sousTypeProduit dans le produit : %-20s]"
								, idProduitJPA
								, produitJPAString
								, sousTypeProduitJPAProduitString);
						
						stb.append(presProduit);
						stb.append(SAUT_DE_LIGNE);						
					}
					
				}
				stb.append(SAUT_DE_LIGNE);
			}
			
		}
		
		return stb.toString();
		
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>retourne une String formatée pour l'affichage 
	 * d'un TypeProduit</p>
	 * <ul>
	 * <li>retourne null si pTypeProduit == null.</li>
	 * <li>affiche le TypeProduit</li>
	 * <li>affiche la liste des SousTypeProduit contenus 
	 * dans le TypeProduit</li>
	 * <li>affiche pour chaque SousTypeProduit la liste des Produit qu'il contient.</li>
	 * </ul>
	 * </div>
	 * 
	 * <div>
	 * <p style="text-decoration:underline;">Exemple d'affichage : </p>
	 * <pre>******* TypeProduit : vêtement *******
	 * [idTypeProduit : 1 - typeProduit : vêtement  ]
	 * 
	 * ******* sousTypeProduits du TypeProduit : vêtement
	 * [idSousTypeProduit : 1 - sousTypeProduit : vêtement pour homme  - [idTypeProduit du TypeProduit dans le SousTypeProduit : 1 - typeProduitString du TypeProduit dans le SousTypeProduit : vêtement     ]]
	 * ***** liste des produits dans le sousProduitJPA : vêtement pour homme
	 * [idProduit dans produits du SousTypeProduit : 1 - produit dans produits du SousTypeProduit : chemise à manches longues pour homme     - sousTypeProduit dans le produit : vêtement pour homme ]
	 * [idProduit dans produits du SousTypeProduit : 2 - produit dans produits du SousTypeProduit : chemise à manches courtes pour homme     - sousTypeProduit dans le produit : vêtement pour homme ]
	 * [idProduit dans produits du SousTypeProduit : 3 - produit dans produits du SousTypeProduit : sweatshirt pour homme                    - sousTypeProduit dans le produit : vêtement pour homme ]
	 * [idProduit dans produits du SousTypeProduit : 4 - produit dans produits du SousTypeProduit : teeshirt pour homme                      - sousTypeProduit dans le produit : vêtement pour homme ]
	 * 
	 * [idSousTypeProduit : 2 - sousTypeProduit : vêtement pour femme  - [idTypeProduit du TypeProduit dans le SousTypeProduit : 1 - typeProduitString du TypeProduit dans le SousTypeProduit : vêtement     ]]
	 * ***** liste des produits dans le sousProduitJPA : vêtement pour femme
	 * null
	 * 
	 * [idSousTypeProduit : 3 - sousTypeProduit : vêtement pour enfant - [idTypeProduit du TypeProduit dans le SousTypeProduit : 1 - typeProduitString du TypeProduit dans le SousTypeProduit : vêtement     ]]
	 * ***** liste des produits dans le sousProduitJPA : vêtement pour enfant
	 * null
	 * </pre>
	 * </div>
	 *
	 * @param pTypeProduit : TypeProduit : Objet métier à afficher
	 * @return String
	 */
	private String afficherTypeProduitFormate(
			final TypeProduitI pTypeProduit) {
		
		/* retourne null si pTypeProduit == null. */
		if (pTypeProduit == null) {
			return null;
		}
		
		final Long idTypeProduit = pTypeProduit.getIdTypeProduit();
		final String typeProduitString 
			= pTypeProduit.getTypeProduit();
		final List<SousTypeProduit> sousTypeProduits 
			= (List<SousTypeProduit>) pTypeProduit.getSousTypeProduits();
		
		final StringBuilder stb = new StringBuilder();
		
		stb.append("******* TypeProduit : ");
		stb.append(typeProduitString);
		stb.append(" *******");
		stb.append(SAUT_DE_LIGNE);
				
		final String pres1 
			= String.format("[idTypeProduit : %-1s - typeProduit : %-10s]"
					, idTypeProduit, typeProduitString);
		stb.append(pres1);
		stb.append(SAUT_DE_LIGNE);
		stb.append(SAUT_DE_LIGNE);
		
		stb.append("******* sousTypeProduits du TypeProduit : ");
		stb.append(typeProduitString);
		stb.append(SAUT_DE_LIGNE);
		
		if (sousTypeProduits == null) {
			stb.append(NULL);
		} else {
			
			for (final SousTypeProduit 
					sousTypeProduit : sousTypeProduits) {
				
				final Long idSousTypeProduit 
					= sousTypeProduit.getIdSousTypeProduit();
				final String sousTypeProduitString 
					= sousTypeProduit.getSousTypeProduit();
				final TypeProduit typeProduitduSousTypeProduit 
					= (TypeProduit) sousTypeProduit.getTypeProduit();
				
				final List<ProduitI> produitsI = sousTypeProduit.getProduits();
				
				final List<Produit> produitsDansSousProduit 
				= convertirListProduitEnListProduit(
						sousTypeProduit.getProduits());
				
				Long idTypeProduitduSousTypeProduit = null;
				String typeProduitduSousTypeProduitString = null;
				
				if (typeProduitduSousTypeProduit != null) {
					idTypeProduitduSousTypeProduit 
						= typeProduitduSousTypeProduit.getIdTypeProduit();
					typeProduitduSousTypeProduitString 
						= typeProduitduSousTypeProduit.getTypeProduit();
				}
				
				final String pres2 
				= String.format("[idSousTypeProduit : %-1s "
						+ "- sousTypeProduit : %-20s - "
						+ "[idTypeProduit du TypeProduit dans le SousTypeProduit : %-1s "
						+ "- typeProduitString du TypeProduit dans le SousTypeProduit : %-13s]]"
						, idSousTypeProduit
						, sousTypeProduitString
						, idTypeProduitduSousTypeProduit
						, typeProduitduSousTypeProduitString);
				
				stb.append(pres2);
				stb.append(SAUT_DE_LIGNE);
				
				stb.append("***** liste des produits dans le sousProduit : ");
				stb.append(sousTypeProduitString);
				stb.append(SAUT_DE_LIGNE);
				if (produitsDansSousProduit == null) {
					stb.append(NULL);
					stb.append(SAUT_DE_LIGNE);
				} else {
					
					for (final Produit produit : produitsDansSousProduit) {
						
						final Long idProduit = produit.getIdProduit();
						final String produitString = produit.getProduit();
						final SousTypeProduit sousTypeProduitProduit 
							= (SousTypeProduit) produit.getSousTypeProduit();
						
						String sousTypeProduitProduitString = null;
						
						if (sousTypeProduitProduit != null) {
							sousTypeProduitProduitString 
								= sousTypeProduitProduit.getSousTypeProduit();
						}
						
						stb.append('\t');
						
						final String presProduit 
						= String.format("[idProduit dans produits du SousTypeProduit : %-1s - "
								+ "produit dans produits du SousTypeProduit : %-40s - "
								+ "sousTypeProduit dans le produit : %-20s]"
								, idProduit
								, produitString
								, sousTypeProduitProduitString);
						
						stb.append(presProduit);
						stb.append(SAUT_DE_LIGNE);						
					}
					
				}
				stb.append(SAUT_DE_LIGNE);
			}
			
		}
		
		return stb.toString();
		
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>retourne une String formatée pour l'affichage 
	 * d'un ProduitJPA</p>
	 * <ul>
	 * <li>retourne null si pTypeProduit == null.</li>
	 * <li>affiche en 1ere ligne [idProduit - produit (String)]</li>
	 * <li>affiche en 1ere ligne [idSousTypeProduit - 
	 * typeProduit du SousTypeProduit - SousTypeProduit (String)]</li>
	 * <li>affiche en 1ère ligne [idTypeProduit - typeProduit (String)]</li>
	 * <li>affiche sur des lignes séparées chaque produit de 
	 * la liste produits du sousTypeProduit.</li>
	 * </ul>
	 * </div>
	 * 
	 * <div>
	 * <p style="text-decoration:underline;">Exemple d'affichage : </p>
	 * <pre>
	 * idProduit : 2 - produit : chemise à manches courtes pour homme     - [idSousTypeProduit : 1 - typeProduit du sousTypeProduit : vêtement      - sousTypeProduit : vêtement pour homme ] - [idTypeProduit : 1 - typeProduit : vêtement     ]
	 * **** Liste des produits du sousTypeProduit : vêtement pour homme
	 * 	[idProduit : 1 - produit : chemise à manches longues pour homme     - [idSousTypeProduit : 1 - typeProduit du sousTypeProduit : vêtement      - sousTypeProduit : vêtement pour homme           ] - [idTypeProduit : 1 - typeProduit : vêtement     ]]
	 * 	[idProduit : 2 - produit : chemise à manches courtes pour homme     - [idSousTypeProduit : 1 - typeProduit du sousTypeProduit : vêtement      - sousTypeProduit : vêtement pour homme           ] - [idTypeProduit : 1 - typeProduit : vêtement     ]]
	 * 	[idProduit : 3 - produit : sweatshirt pour homme                    - [idSousTypeProduit : 1 - typeProduit du sousTypeProduit : vêtement      - sousTypeProduit : vêtement pour homme           ] - [idTypeProduit : 1 - typeProduit : vêtement     ]]
	 * 	[idProduit : 4 - produit : teeshirt pour homme                      - [idSousTypeProduit : 1 - typeProduit du sousTypeProduit : vêtement      - sousTypeProduit : vêtement pour homme           ] - [idTypeProduit : 1 - typeProduit : vêtement     ]]
	 * </pre>
	 * </div>
	 *
	 * @param pProduit : ProduitJPA 
	 * @return String
	 */
	private String afficherProduitFormate(final ProduitJPA pProduit) {
	
		/* retourne null si pTypeProduit == null. */
		if (pProduit == null) {
			return null;
		}
		
		/* INSTANCIATION d'un StringBuilder. */
		final StringBuilder stb = new StringBuilder();
		
		/* NIVEAU Produit. */
		final Long idProduitProv = pProduit.getIdProduit();
		final String produitString = pProduit.getProduit();
		
		/* NIVEAU SousTypeProduit. */
		final SousTypeProduitJPA sousTypeProduitProv 
			= (SousTypeProduitJPA) pProduit.getSousTypeProduit();
		
		Long idSousTypeProduitProv = null;
		TypeProduitI typeProduit = null;
		String typeProduitStringSTP = null;
		String sousTypeProduitString = null;
		
		if (sousTypeProduitProv != null) {
			
			idSousTypeProduitProv 
				= sousTypeProduitProv.getIdSousTypeProduit();
			
			typeProduit = sousTypeProduitProv.getTypeProduit();
			
			if (typeProduit != null) {
				typeProduitStringSTP = typeProduit.getTypeProduit();
			}
			
			sousTypeProduitString = sousTypeProduitProv.getSousTypeProduit();
		}
		
		/* NIVEAU TypeProduit. */
		Long idTypeProduit = null;
		String typeProduitString = null;
		
		final TypeProduitI typeProduitProv = pProduit.getTypeProduit();
		
		if (typeProduitProv != null) {
			idTypeProduit = typeProduitProv.getIdTypeProduit();
			typeProduitString = typeProduitProv.getTypeProduit();
		}
				 		
		final String pres 
		= String.format(
				"idProduit : %-1s - produit : %-40s "
				+ FORMAT_IDSOUSTYPEPRODUIT
				+ FORMAT_TYPEPRODUIT_STP
				+ "- sousTypeProduit : %-20s] "
				+ "- [idTypeProduit : %-1s - typeProduit : %-13s]"
				, idProduitProv
				, produitString
				, idSousTypeProduitProv
				, typeProduitStringSTP
				, sousTypeProduitString
				, idTypeProduit
				, typeProduitString);
	
		stb.append(pres);
		stb.append(SAUT_DE_LIGNE);
		
		/* NIVEAU produits du SousTypeProduit. */		
		List<? extends ProduitI> produitsSTP = null;
		
		if (sousTypeProduitProv != null) {
			
			stb.append("**** Liste des produits du sousTypeProduit : ");
			stb.append(sousTypeProduitProv.getSousTypeProduit());
			stb.append(SAUT_DE_LIGNE);
	
			produitsSTP = sousTypeProduitProv.getProduits();
			
			if (produitsSTP == null) {
				stb.append(NULL);
				stb.append(SAUT_DE_LIGNE);
			} else {
				
				for (final ProduitI produitProv : produitsSTP) {
					
					final Long idProduitSTP = produitProv.getIdProduit();
					final String produitStringProv 
						= produitProv.getProduit();
					
					/* NIVEAU SousTypeProduit du produit du sousTypeProduit. */
					final SousTypeProduitJPA sousTypeProduitProvSTP 
						= (SousTypeProduitJPA) produitProv.getSousTypeProduit();
					
					Long idSousTypeProduitProvSTP = null;
					TypeProduitJPA typeProduitProvSTP = null;
					String typeProduitStringSTPProv = null;
					String sousTypeProduitStringSTP = null;
					
					if (sousTypeProduitProvSTP != null) {
						
						idSousTypeProduitProvSTP 
							= sousTypeProduitProvSTP
								.getIdSousTypeProduit();
						
						typeProduitProvSTP 
							= (TypeProduitJPA) sousTypeProduitProvSTP.getTypeProduit();
						
						if (typeProduitProvSTP != null) {
							typeProduitStringSTPProv 
								= typeProduitProvSTP.getTypeProduit();
						}
						
						sousTypeProduitStringSTP 
							= sousTypeProduitProvSTP.getSousTypeProduit();
					}
	
					/* NIVEAU TypeProduit du produit du sousTypeProduit. */
					Long idTypeProduitSTP = null;
					String typeProduitString1 = null;
					
					final TypeProduitI typeProduitProv1 
						= pProduit.getTypeProduit();
					
					if (typeProduitProv1 != null) {
						idTypeProduitSTP 
							= typeProduitProv1.getIdTypeProduit();
						typeProduitString1 
							= typeProduitProv1.getTypeProduit();
					}
	
					
					stb.append('\t');
					
					final String pres2 
						= String.format(
								"[idProduit : %-1s "
								+ "- produit : %-40s "
								+ FORMAT_IDSOUSTYPEPRODUIT
								+ FORMAT_TYPEPRODUIT_STP
								+ "- sousTypeProduit : %-30s] "
								+ "- [idTypeProduit : %-1s "
								+ "- typeProduit : %-13s]]"
								, idProduitSTP
								, produitStringProv
								, idSousTypeProduitProvSTP
								, typeProduitStringSTPProv
								, sousTypeProduitStringSTP
								, idTypeProduitSTP
								, typeProduitString1);
					
					stb.append(pres2);
					stb.append(SAUT_DE_LIGNE);
					
				}
			}
			
		}
				
		return stb.toString();
		
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>retourne une String formatée pour l'affichage 
	 * d'un Produit</p>
	 * <ul>
	 * <li>retourne null si pTypeProduit == null.</li>
	 * <li>affiche en 1ere ligne [idProduit - produit (String)]</li>
	 * <li>affiche en 1ere ligne [idSousTypeProduit - 
	 * typeProduit du SousTypeProduit - SousTypeProduit (String)]</li>
	 * <li>affiche en 1ère ligne [idTypeProduit - typeProduit (String)]</li>
	 * <li>affiche sur des lignes séparées chaque produit de 
	 * la liste produits du sousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pProduit : Produit
	 * @return String
	 */
	private String afficherProduitFormate(final Produit pProduit) {
	
		if (pProduit == null) {
			return null;
		}
		
		/* INSTANCIATION d'un StringBuilder. */
		final StringBuilder stb = new StringBuilder();
		
		/* NIVEAU Produit. */
		final Long idProduitProv = pProduit.getIdProduit();
		final String produitString = pProduit.getProduit();
		
		/* NIVEAU SousTypeProduit. */
		final SousTypeProduit sousTypeProduitProv 
			= (SousTypeProduit) pProduit.getSousTypeProduit();
		
		Long idSousTypeProduitProv = null;
		TypeProduit typeProduit = null;
		String typeProduitStringSTP = null;
		String sousTypeProduitString = null;
		
		if (sousTypeProduitProv != null) {
			
			idSousTypeProduitProv 
				= sousTypeProduitProv.getIdSousTypeProduit();
			
			typeProduit = (TypeProduit) sousTypeProduitProv.getTypeProduit();
			
			if (typeProduit != null) {
				typeProduitStringSTP = typeProduit.getTypeProduit();
			}
			
			sousTypeProduitString = sousTypeProduitProv.getSousTypeProduit();
		}
		
		/* NIVEAU TypeProduit. */
		Long idTypeProduit = null;
		String typeProduitString = null;
		
		final TypeProduit typeProduitProv = (TypeProduit) pProduit.getTypeProduit();
		
		if (typeProduitProv != null) {
			idTypeProduit = typeProduitProv.getIdTypeProduit();
			typeProduitString = typeProduitProv.getTypeProduit();
		}
		
		final String pres 
		= String.format(
				"idProduit : %-1s - produit : %-40s "
				+ FORMAT_IDSOUSTYPEPRODUIT
				+ FORMAT_TYPEPRODUIT_STP
				+ "- sousTypeProduit : %-20s] "
				+ "- [idTypeProduit : %-1s - typeProduit : %-13s]"
				, idProduitProv
				, produitString
				, idSousTypeProduitProv
				, typeProduitStringSTP
				, sousTypeProduitString
				, idTypeProduit
				, typeProduitString);
		
		stb.append(pres);
		stb.append(SAUT_DE_LIGNE);
		
		/* NIVEAU produits du SousTypeProduit. */		
		List<Produit> produitsSTP = null;
		
		if (sousTypeProduitProv != null) {
			
			stb.append("**** Liste des produits du sousTypeProduit : ");
			stb.append(sousTypeProduitProv.getSousTypeProduit());
			stb.append(SAUT_DE_LIGNE);
	
			produitsSTP 
				= convertirListProduitEnListProduit(
						sousTypeProduitProv.getProduits());
			
			if (produitsSTP == null) {
				stb.append(NULL);
				stb.append(SAUT_DE_LIGNE);
			} else {
				
				for (final Produit produitProv : produitsSTP) {
					
					final Long idProduitSTP = produitProv.getIdProduit();
					final String produitStringProv 
						= produitProv.getProduit();
					
					/* NIVEAU SousTypeProduit du produit du sousTypeProduit. */
					final SousTypeProduit sousTypeProduitProvSTP 
						= (SousTypeProduit) produitProv.getSousTypeProduit();
					
					Long idSousTypeProduitProvSTP = null;
					TypeProduit typeProduitProvSTP = null;
					String typeProduitStringSTPProv = null;
					String sousTypeProduitStringSTP = null;
					
					if (sousTypeProduitProvSTP != null) {
						
						idSousTypeProduitProvSTP 
							= sousTypeProduitProvSTP.getIdSousTypeProduit();
						
						typeProduitProvSTP 
							= (TypeProduit) sousTypeProduitProvSTP.getTypeProduit();
						
						if (typeProduitProvSTP != null) {
							typeProduitStringSTPProv 
								= typeProduitProvSTP.getTypeProduit();
						}
						
						sousTypeProduitStringSTP 
							= sousTypeProduitProvSTP.getSousTypeProduit();
					}
	
					/* NIVEAU TypeProduit du produit du sousTypeProduit. */
					Long idTypeProduitSTP = null;
					String typeProduitString1 = null;
					
					final TypeProduit typeProduitProv1 
						= (TypeProduit) pProduit.getTypeProduit();
					
					if (typeProduitProv1 != null) {
						idTypeProduitSTP 
							= typeProduitProv1.getIdTypeProduit();
						typeProduitString1 
							= typeProduitProv1.getTypeProduit();
					}
	
					
					stb.append('\t');
					
					final String pres2 
						= String.format(
								"[idProduit : %-1s "
								+ "- produit : %-40s "
								+ FORMAT_IDSOUSTYPEPRODUIT
								+ FORMAT_TYPEPRODUIT_STP
								+ "- sousTypeProduit : %-30s] "
								+ "- [idTypeProduit : %-1s "
								+ "- typeProduit : %-13s]]"
								, idProduitSTP
								, produitStringProv
								, idSousTypeProduitProvSTP
								, typeProduitStringSTPProv
								, sousTypeProduitStringSTP
								, idTypeProduitSTP
								, typeProduitString1);
					
					stb.append(pres2);
					stb.append(SAUT_DE_LIGNE);
	
				}
			}
			
		}
		
		return stb.toString();
		
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>fournit une String formatée pour l'affichage à la console 
	 * d'une List&lt;SousTypeProduitJPA&gt;</p>
	 * </div>
	 *
	 * @param pList : List&lt;SousTypeProduitJPA&gt;
	 * 
	 * @return String
	 */
	private String afficherSousTypeProduitsJPA(final List<? extends SousTypeProduitI> pList) {
		
		if (pList == null) {
			return null;
		}
		
		final StringBuilder stb = new StringBuilder();
		
		for (final SousTypeProduitI sousTypeProduit : pList) {
						
			final Long idSousTypeProduit 
				= sousTypeProduit.getIdSousTypeProduit();
			final String sousTypeProduitString 
				= sousTypeProduit.getSousTypeProduit();
			final String typeProduit 
				= sousTypeProduit.getTypeProduit().getTypeProduit();
			
			final String presentation 
				= String.format(
						"idSousTypeProduit : %-1s - sousTypeProduit : %-30s - typeProduit : %-20s"
						, idSousTypeProduit, sousTypeProduitString, typeProduit);
			
			stb.append(presentation);
			stb.append(SAUT_DE_LIGNE);
		}
		
		return stb.toString();	
		
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>fournit une String formatée pour l'affichage à la console 
	 * d'une List&lt;SousTypeProduit&gt;</p>
	 * </div>
	 *
	 * @param pList : List&lt;SousTypeProduit&gt;
	 * 
	 * @return String
	 */
	private String afficherSousTypeProduits(final List<SousTypeProduit> pList) {
		
		if (pList == null) {
			return null;
		}
		
		final StringBuilder stb = new StringBuilder();
		
		for (final SousTypeProduit sousTypeProduit : pList) {
						
			final Long idSousTypeProduit 
				= sousTypeProduit.getIdSousTypeProduit();
			final String sousTypeProduitString 
				= sousTypeProduit.getSousTypeProduit();
			final String typeProduit 
				= sousTypeProduit.getTypeProduit().getTypeProduit();
			
			final String presentation 
				= String.format(
						"idSousTypeProduit : %-1s - sousTypeProduit : %-30s - typeProduit : %-20s"
						, idSousTypeProduit, sousTypeProduitString, typeProduit);
			
			stb.append(presentation);
			stb.append(SAUT_DE_LIGNE);
		}
		
		return stb.toString();
		
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>fournit une String pour l'affichage à la console 
	 * d'une List&lt;ProduitJPA&gt;</p>
	 * </div>
	 *
	 * @param pList : List&lt;ProduitJPA&gt;
	 * 
	 * @return String
	 */
	private String afficherProduitsJPA(final List<ProduitJPA> pList) {
		
		if (pList == null) {
			return null;
		}
		
		final StringBuilder stb = new StringBuilder();
		
		for (final ProduitJPA produit : pList) {
			
			if (produit != null) {
				
				/* idProduit. */
				final Long idProduit = produit.getIdProduit();
				
				/* produit. */
				final String produitString = produit.getProduit();
				
				/* SOUSTYPEPRODUIT.*/
				final SousTypeProduitJPA sousTypeProduit 
					= (SousTypeProduitJPA) produit.getSousTypeProduit();
				
				/* sousTypeProduit. */
				Long idSousProduit = null;
				String sousTypeProduitString = null;
				TypeProduitJPA typeProduitduSousTypeProduit = null;
				String typeProduitduSousTypeProduitString = null;
				
				if (sousTypeProduit != null) {
					idSousProduit 
						= sousTypeProduit.getIdSousTypeProduit();
					sousTypeProduitString 
						= sousTypeProduit.getSousTypeProduit();
					typeProduitduSousTypeProduit 
						= (TypeProduitJPA) sousTypeProduit.getTypeProduit();
					if (typeProduitduSousTypeProduit != null) {
						typeProduitduSousTypeProduitString 
							= typeProduitduSousTypeProduit.getTypeProduit();
					}
				}
				
				/* TYPEPRODUIT*/
				final TypeProduitI typeProduit = produit.getTypeProduit();
				
				Long idTypeProduit = null;
				String typeProduitString = null;
				
				if (typeProduit != null) {
					idTypeProduit = typeProduit.getIdTypeProduit();
					typeProduitString = typeProduit.getTypeProduit();
				}
							
				final String presentation 
					= String.format(
							"idProduit : "
							+ FORMAT_IDPRODUIT
							+ " - produit : "
							+ "%-40s"
							+ " - [idSousProduit : "
							+ FORMAT_IDSTP
							+ " - sousTypeProduit : "
							+ "%-20s"
							+ " - typeProduit du sousTypeProduit : "
							+ "%-12s"
							+ "] - [idTypeProduit du Produit : "
							+ FORMAT_IDTP
							+ " - typeProduit du Produit : "
							+ "%-12s]"
							, idProduit
							, produitString
							, idSousProduit
							, sousTypeProduitString
							, typeProduitduSousTypeProduitString
							, idTypeProduit
							, typeProduitString);
				
				stb.append(presentation);
				stb.append(SAUT_DE_LIGNE);
				
			}
			
		}
		
		return stb.toString();
		
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>fournit une String pour l'affichage à la console 
	 * d'une List&lt;Produit&gt;</p>
	 * </div>
	 *
	 * @param pList : List&lt;Produit&gt;
	 * 
	 * @return String
	 */
	private String afficherProduits(final List<Produit> pList) {
		
		if (pList == null) {
			return null;
		}
		
		final StringBuilder stb = new StringBuilder();
		
		for (final Produit produit : pList) {
			
			if (produit != null) {
				
				/* idProduit. */
				final Long idProduit = produit.getIdProduit();
				
				/* produit. */
				final String produitString = produit.getProduit();
				
				/* SOUSTYPEPRODUIT.*/
				final SousTypeProduit sousTypeProduit 
					= (SousTypeProduit) produit.getSousTypeProduit();
				
				/* sousTypeProduit. */
				Long idSousProduit = null;
				String sousTypeProduitString = null;
				TypeProduit typeProduitduSousTypeProduit = null;
				String typeProduitduSousTypeProduitString = null;
				
				if (sousTypeProduit != null) {
					idSousProduit 
						= sousTypeProduit.getIdSousTypeProduit();
					sousTypeProduitString 
						= sousTypeProduit.getSousTypeProduit();
					typeProduitduSousTypeProduit 
						= (TypeProduit) sousTypeProduit.getTypeProduit();
					if (typeProduitduSousTypeProduit != null) {
						typeProduitduSousTypeProduitString 
							= typeProduitduSousTypeProduit.getTypeProduit();
					}
				}
				
				/* TYPEPRODUIT*/
				final TypeProduit typeProduit = (TypeProduit) produit.getTypeProduit();
				
				Long idTypeProduit = null;
				String typeProduitString = null;
				
				if (typeProduit != null) {
					idTypeProduit = typeProduit.getIdTypeProduit();
					typeProduitString = typeProduit.getTypeProduit();
				}
							
				final String presentation 
					= String.format(
							"idProduit : "
							+ FORMAT_IDPRODUIT
							+ " - produit : "
							+ "%-40s"
							+ " - [idSousProduit : "
							+ FORMAT_IDSTP
							+ " - sousTypeProduit : "
							+ "%-20s"
							+ " - typeProduit du sousTypeProduit : "
							+ "%-12s"
							+ "] - [idTypeProduit du Produit : "
							+ FORMAT_IDTP
							+ " - typeProduit du Produit : "
							+ "%-12s]"
							, idProduit
							, produitString
							, idSousProduit
							, sousTypeProduitString
							, typeProduitduSousTypeProduitString
							, idTypeProduit
							, typeProduitString);
				
				stb.append(presentation);
				stb.append(SAUT_DE_LIGNE);
				
			}
			
		}
		
		return stb.toString();
		
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>peuple les Entities</p>
	 * </div>
	 */
	private void creerScenario() {
		
		/* création de TypeProduit. */
		this.typeProduitVetementJPA = new TypeProduitJPA(1L, VETEMENT);
		this.typeProduitPecheJPA = new TypeProduitJPA(2L, "pêche");
		
		/* création de SousTypeProduit. */
		this.sousTypeProduitVetementHommeJPA 
			= new SousTypeProduitJPA(1L
					, VETEMENT_POUR_HOMME
					, this.typeProduitVetementJPA);
		this.sousTypeProduitVetementFemmeJPA 
			= new SousTypeProduitJPA(2L
				, "vêtement pour femme"
				, this.typeProduitVetementJPA);
		this.sousTypeProduitVetementEnfantJPA 
			= new SousTypeProduitJPA(3L
				, "vêtement pour enfant"
				, this.typeProduitVetementJPA);
		
		this.sousTypeProduitPecheCanneJPA 
			= new SousTypeProduitJPA(4L
					, "canne"
					, this.typeProduitPecheJPA);
		this.sousTypeProduitPecheCuillerJPA 
			= new SousTypeProduitJPA(5L
					, "cuiller"
					, this.typeProduitPecheJPA);
		
		/* création de Produit. */
		this.produitChemiseManchesLonguesPourHomme 
			= new ProduitJPA(
					1L
					, "chemise à manches longues pour homme"
					, this.sousTypeProduitVetementHommeJPA);
		this.produitChemiseManchesCourtesPourHomme 
			= new ProduitJPA(
					2L
					, "chemise à manches courtes pour homme"
					, this.sousTypeProduitVetementHommeJPA);
		this.produitSweatshirtPourHomme 
			= new ProduitJPA(
					3L
					, "sweatshirt pour homme"
					, this.sousTypeProduitVetementHommeJPA);
		this.produitTeeshirtPourHomme 
			= new ProduitJPA(
					4L
					, "teeshirt pour homme"
					, this.sousTypeProduitVetementHommeJPA);
		
	} //___________________________________________________________________



	/**
	 * Crée un graphe JPA cohérent via setters canoniques des entities.
	 * Hypothèse : tes entities JPA ont setTypeProduit/setSousTypeProduit
	 * canoniques (ce que tu as décrit).
	 */
	private ScenarioJPA creerScenarioVetement() {

		final ScenarioJPA s = new ScenarioJPA();

		// Parent
		s.typeProduitVetement = new TypeProduitJPA(VETEMENT);
		s.typeProduitVetement.setIdTypeProduit(1L);

		// Enfants STP
		s.stpHomme = new SousTypeProduitJPA(VETEMENT_POUR_HOMME);
		s.stpHomme.setIdSousTypeProduit(1L);
		s.stpHomme.setTypeProduit(s.typeProduitVetement);

		s.stpFemme = new SousTypeProduitJPA("vêtement pour femme");
		s.stpFemme.setIdSousTypeProduit(2L);
		s.stpFemme.setTypeProduit(s.typeProduitVetement);

		s.stpEnfant = new SousTypeProduitJPA("vêtement pour enfant");
		s.stpEnfant.setIdSousTypeProduit(3L);
		s.stpEnfant.setTypeProduit(s.typeProduitVetement);

		// Produits (enfants du STP homme)
		s.p1 = new ProduitJPA(CHEMISES_MANCHES_LONGUES);
		s.p1.setIdProduit(1L);
		s.p1.setSousTypeProduit(s.stpHomme);

		s.p2 = new ProduitJPA("chemise manches courtes");
		s.p2.setIdProduit(2L);
		s.p2.setSousTypeProduit(s.stpHomme);

		s.p3 = new ProduitJPA("sweatshirt");
		s.p3.setIdProduit(3L);
		s.p3.setSousTypeProduit(s.stpHomme);

		s.p4 = new ProduitJPA("tee-shirt");
		s.p4.setIdProduit(4L);
		s.p4.setSousTypeProduit(s.stpHomme);

		return s;

	}

	// ==========================================================
	// Helpers - Recherche dans le graphe métier
	// ==========================================================



	private static SousTypeProduitI findStpByName(
			final TypeProduitI tp,
			final String name) {

		for (final SousTypeProduitI stp : tp.getSousTypeProduits()) {

			if (stp != null && name.equals(stp.getSousTypeProduit())) {

				return stp;

			}

		}

		return null;

	}



	private static ProduitI findProduitByName(
			final SousTypeProduitI stp,
			final String name) {

		for (final ProduitI p : stp.getProduits()) {

			if (p != null && name.equals(p.getProduit())) {

				return p;

			}

		}

		return null;

	}

	// ==========================================================
	// Helpers - Assertions "béton"
	// ==========================================================



	/**
	 * Vérifie qu'une liste ne contient pas 2 fois la même instance (au sens
	 * identité). (preuve utile pour l'absence de double rattachement et la
	 * stabilité du cache)
	 */
	private static void assertIdentityUnique(
			final List<?> list,
			final String label) {

		assertNotNull(list, label + " : liste null");
		final Set<Object> identitySet = java.util.Collections
				.newSetFromMap(new IdentityHashMap<>());

		for (final Object o : list) {

			assertNotNull(o, label + " : élément null");
			final boolean added = identitySet.add(o);
			assertTrue(added, label
					+ " : doublon d'instance (même référence) détecté");

		}

	}



	/**
	 * Vérifie qu'un même élément (même référence) n'apparaît pas plusieurs
	 * fois. (complément, donne un message plus explicite en cas de bug)
	 */
	private static void assertNoDuplicateOccurrences(
			final List<?> list,
			final String label) {

		assertNotNull(list, label + " : liste null");

		for (int i = 0; i < list.size(); i++) {

			final Object a = list.get(i);

			for (int j = i + 1; j < list.size(); j++) {

				final Object b = list.get(j);
				assertNotSame(a, b, label
						+ " : double rattachement détecté aux indices " + i
						+ " et " + j);

			}

		}

	}


	/* ========================================================== */
	/* Helpers Reflection                                           */
	/* ========================================================== */

	/**
	 * <div>
	 * <p>Force l’écriture d’un champ par reflection (tests uniquement).</p>
	 * </div>
	 *
	 * @param target : Object
	 * @param fieldName : String
	 * @param value : Object
	 * @throws Exception
	 */
	private static void setField(
			final Object target,
			final String fieldName,
			final Object value) throws Exception {

		final Field f = findField(target.getClass(), fieldName);
		f.setAccessible(true); // NOPMD by danyl on 14/01/2026 22:06
		f.set(target, value);
	}


	/**
	 * <div>
	 * <p>Recherche un champ dans la hiérarchie de classes.</p>
	 * </div>
	 *
	 * @param type : Class<?>
	 * @param fieldName : String
	 * @return Field
	 * @throws Exception
	 */
	private static Field findField(
			final Class<?> type,
			final String fieldName) throws Exception {

		Class<?> current = type;

		while (current != null) {

			try {

				return current.getDeclaredField(fieldName);

			} catch (final NoSuchFieldException e) {

				current = current.getSuperclass();

			}

		}

		throw new NoSuchFieldException(fieldName);
	}


	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * convertit une List&lt;ProduitI&gt; en List&lt;Produit&gt;.</p>
	 * <ul>
	 * <li>Si pList == null : return null.</li>
	 * <li>Elimine tout null dans la liste réponse.</li>
	 * <li>Cast les ProduitI en Produit.</li>
	 * <li>Elimine les cas de mauvaise instance.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pList : List&lt;ProduitI&gt; 
	 * @return List&lt;Produit&gt;
	 */
	private static List<Produit> convertirListProduitEnListProduit(
			final List<ProduitI> pList) {
		
		/* Si pList == null : return null. */
		if (pList == null) {
			return null;
		}

		final List<Produit> reponse = new ArrayList<Produit>();

		for (final ProduitI produitI : pList) {

			final Produit p;

			/* Elimine tout null dans la liste réponse. */
			if (produitI == null) {
				continue;
			}

			if (produitI instanceof Produit other) {

				/* Cast les ProduitI en Produit. */
				other = (Produit) produitI;
				reponse.add(other);

			} else {

				/* Elimine les cas de mauvaise instance. */
				continue;

			}

		}
		
		return reponse;
	}

}
