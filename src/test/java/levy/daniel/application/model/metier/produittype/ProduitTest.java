package levy.daniel.application.model.metier.produittype; // NOPMD by danyl on 08/02/2026 03:02

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">CLASSE ProduitTest.java :</p>
 * 
 * <p>
 * Cette classe teste la classe metier : 
 * <span style="font-weight:bold;">ProduitI</span> 
 * </p>
 * 
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 11 déc. 2025
 */
public class ProduitTest {

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
	 * "resource"
	 */
	public static final String RESOURCE = "resource";

	/**
	 * "Le clone ne doit pas être la même instance que l'original."
	 */
	public static final String CLONE_PAS_MEME_INSTANCE 
		= "Le clone ne doit pas être la même instance que l'original.";
	
	/**
	 * "thread-safety"
	 */
	public static final String THREAD_SAFETY = "thread-safety";
	
	/**
	 * System.getProperty("line.separator")
	 */
	public static final String SAUT_DE_LIGNE 
		= System.getProperty("line.separator");
	
	/**
	 * "null" 
	 */
	public static final String NULL = "null";
	
	/* ------------------------------------------------------------------ */

	/**
	 * "Anatomie"
	 */
	public static final String ANATOMIE = "Anatomie";
	
	/**
	 * "Anatomie de la main"
	 */
	public static final String ANATOMIE_MAIN = "Anatomie de la main";
	
	/**
	 * "Anatomie arthroscopique de la main"
	 */
	public static final String ANATOMIE_ARTHRO_MAIN 
		= "Anatomie arthroscopique de la main";
	
	/**
	 * "Photographie"
	 */
	public static final String PHOTOGRAPHIE = "Photographie";
	
	/**
	 * "caméras"
	 */
	public static final String CAMERAS = "caméras";
	
	/**
	 * "appareil photo"
	 */
	public static final String APPAREIL_PHOTO = "appareil photo";
	
	/**
	 * "dindon"
	 */
	public static final String DINDON = "dindon";
	
	/**
	 * "vêtement pour homme"
	 */
	public static final String VETEMENT_HOMME = "vêtement pour homme";
	
	/**
	 * "vêtement pour femme"
	 */
	public static final String VETEMENT_FEMME = "vêtement pour femme";
	
	/**
	 * "vêtement pour enfant"
	 */
	public static final String VETEMENT_ENFANT = "vêtement pour enfant";
	
	/**
	 * "chemise manches longues"
	 */
	public static final String CHEMISE_MANCHES_LONGUES = "chemise manches longues";
	
	/**
	 * "chemise manches courtes"
	 */
	public static final String CHEMISES_MANCHES_COURTES = "chemise manches courtes";
	
	/**
	 * "sweat-shirt"
	 */
	public static final String SWEATSHIRT = "sweat-shirt";
	
	/**
	 * "soutien-gorge"
	 */
	public static final String SOUTIEN_GORGE = "soutien-gorge";
	
	/**
	 * "Produit modifié"
	 */
	public static final String PRODUIT_MODIFIE = "Produit modifié";

	/**
	 * "Sous-type modifié"
	 */
	public static final String SOUS_TYPE_MODIFIE = "Sous-type modifié";

	/**
	 * "Nouveau sous-type"
	 */
	public static final String NOUVEAU_SOUS_TYPE = "Nouveau sous-type";
	
	/* ------------------------------------------------------------------ */
	
	/**
	 * TypeProduit "vêtement"
	 */
	public TypeProduitI typeProduitVetement;
	
	/**
	 * SousTypeProduit "vêtement pour homme" avec TypeProduit "vêtement"
	 */
	public SousTypeProduitI soustypeProduitVetementPourHomme;
	
	/**
	 * SousTypeProduit "vêtement pour femme" avec TypeProduit "vêtement"
	 */
	public SousTypeProduitI soustypeProduitVetementPourFemme;
	
	/**
	 * SousTypeProduit "vêtement pour enfant" avec TypeProduit "vêtement"
	 */
	public SousTypeProduitI soustypeProduitVetementPourEnfant;
	
	/**
	 * <p>SousTypeProduit "vêtement pour homme" avec TypeProduit NULL.</p>
	 * <p>new SousTypeProduit(VETEMENT_HOMME);</p>
	 */
	public SousTypeProduitI soustypeProduitVetementPourHommeSansTypeProduit;
	
	/**
	 * ProduitI "chemise manches longues" avec sous-type ProduitI "vêtement pour homme"
	 */
	public ProduitI produitChemiseManchesLonguesHomme;
	
	/**
	 * ProduitI "chemise manches courtes" avec sous-type ProduitI "vêtement pour homme"
	 */
	public ProduitI produitChemiseManchesCourtesHomme;
	
	/**
	 * new ProduitI(SWEATSHIRT, soustypeProduitVetementPourHomme)
	 */
	public ProduitI produitSweatshirtHomme;
	
	/**
	 * new ProduitI("tee-shirt", soustypeProduitVetementPourHommeSansTypeProduit);
	 */
	public ProduitI produitAvecMauvaisSousTypeProduit;
	
	/**
	 * ProduitI "chemise manches longues" avec sous-type ProduitI "vêtement pour femme"
	 */
	public ProduitI produitChemiseManchesLonguesFemme;
	
	/**
	 * ProduitI "chemise manches courtes" avec sous-type ProduitI "vêtement pour femme"
	 */
	public ProduitI produitChemiseManchesCourtesFemme;
	
	/**
	 * ProduitI "sweatshirt" avec sous-type ProduitI "vêtement pour femme"
	 */
	public ProduitI produitSweatshirtFemme;
	
	/**
	 * ProduitI "soutien-gorge" avec sous-type ProduitI "vêtement pour femme"
	 */
	public ProduitI produitSoutienGorgeFemme;
	
	/**
	 * ProduitI "chemise manches longues" avec sous-type ProduitI "vêtement pour enfant"
	 */
	public ProduitI produitChemiseManchesLonguesEnfant;
	
	/**
	 * ProduitI "chemise manches courtes" avec sous-type ProduitI "vêtement pour enfant"
	 */
	public ProduitI produitChemiseManchesCourtesEnfant;
	
	/**
	 * ProduitI "sweatshirt" avec sous-type ProduitI "vêtement pour enfant"
	 */
	public ProduitI produitSweatshirtEnfant;
	
	/**
	 * liste de produits "vêtement"
	 */
	public List<ProduitI> listeProduitsVetement;
	
	/* ------------------------------------------------------------------ */

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
			.getLogger(ProduitTest.class);

	// *************************METHODES************************************/



	/**
	* <div>
	* <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	* </div>
	*/
	public ProduitTest() {
		super();
	} // Fin du CONSTRUCTEUR D'ARITE NULLE.________________________________


	
	/**
	 * <div>
	 * <p>Teste le constructeur d'arité nulle.</p>
	 * <ul>
	 * <li>vérifie que toutes les propriétés de l'objet sont null.</li>
	 * <li>vérifie que les Booleans valide sont à false.</li>
	 * <li>vérifie que 2 instances créées avec le constructeur null sont différentes.</li>
	 * <li>vérifie que 2 instances créées avec le constructeur null sont equals.</li>
	 * <li>vérifie que les setters fonctionnent correctement.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testConstructeurNull() : vérifie le comportement général du Constructeur d'arité nulle")
	@Tag("constructeurs")
	@Test
	public final void testConstructeurNull() {
				
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE ProduitTest - méthode testConstructeurNull() ********** ");
			System.out.println("CE TEST VERIFIE LE FONCTIONNEMENT DU CONSTRUCTEUR D'ARITE NULLE.");
			System.out.println();				
		}
		
		//**** ARRANGE - GIVEN
		final ProduitI objetConstructeurNull1 = new Produit();
		final ProduitI objetConstructeurNull2 = new Produit();
		
		// ACT - WHEN
		final Long idObjetConstructeurNull1 = objetConstructeurNull1.getIdProduit();
		final Long idObjetConstructeurNull2 = objetConstructeurNull2.getIdProduit();
		
		final String produitConstructeurNullString1 = objetConstructeurNull1.getProduit();
		final String produitConstructeurNullString2 = objetConstructeurNull2.getProduit();
		
		final SousTypeProduit sousTypeProduitNull1 = (SousTypeProduit) objetConstructeurNull1.getSousTypeProduit();
		final SousTypeProduit sousTypeProduitNull2 = (SousTypeProduit) objetConstructeurNull2.getSousTypeProduit();
		
		final TypeProduit typeProduitNull1 = (TypeProduit) objetConstructeurNull1.getTypeProduit();
		final TypeProduit typeProduitNull2 = (TypeProduit) objetConstructeurNull2.getTypeProduit();
		
		final Boolean valideProduitConstructeurNull1 = objetConstructeurNull1.isValide();
		final Boolean valideProduitConstructeurNull2 = objetConstructeurNull2.isValide();
		
		final Boolean memeInstance1 = objetConstructeurNull1 == objetConstructeurNull2;
		final Boolean memeInstance2 = objetConstructeurNull2 == objetConstructeurNull1;
		
		final Boolean produitConstructeurNull1EqualsproduitConstructeurNull2 = objetConstructeurNull1.equals(objetConstructeurNull2);
		final Boolean produitConstructeurNull2EqualsproduitConstructeurNull1 = objetConstructeurNull2.equals(objetConstructeurNull1);
		final Boolean produitConstructeurNull1EqualsproduitConstructeurNull1 = objetConstructeurNull1.equals(objetConstructeurNull1);
		
		final int produitConstructeurNull1HashCode = objetConstructeurNull1.hashCode();
		final int produitConstructeurNull2HashCode = objetConstructeurNull2.hashCode();
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("***ProduitI produitConstructeurNull1 = new ProduitI();*** ");
			System.out.println("***ProduitI produitConstructeurNull2 = new ProduitI();*** ");
			System.out.println("idProduitConstructeurNull1 : " + idObjetConstructeurNull1);
			System.out.println("idProduitConstructeurNull2 : " + idObjetConstructeurNull2);
			System.out.println();
			System.out.println("produitConstructeurNullString1 : " + produitConstructeurNullString1);
			System.out.println("produitConstructeurNullString2 : " + produitConstructeurNullString2);
			System.out.println();
			System.out.println("sousTypeProduitNull1 : " + sousTypeProduitNull1);
			System.out.println("sousTypeProduitNull2 : " + sousTypeProduitNull2);
			System.out.println();
			System.out.println("typeProduitNull1 : " + typeProduitNull1);
			System.out.println("typeProduitNull2 : " + typeProduitNull2);
			System.out.println();
			System.out.println("valideProduitConstructeurNull1 : " + valideProduitConstructeurNull1);
			System.out.println("valideProduitConstructeurNull2 : " + valideProduitConstructeurNull2);
			System.out.println();
			System.out.println("produitConstructeurNull1 == produitConstructeurNull2 : " + memeInstance1);
			System.out.println("produitConstructeurNull2 == produitConstructeurNull1 : " + memeInstance2);
			System.out.println();
			System.out.println("produitConstructeurNull1EqualsproduitConstructeurNull2 : " + produitConstructeurNull1EqualsproduitConstructeurNull2);
			System.out.println("produitConstructeurNull2EqualsproduitConstructeurNull1 : " + produitConstructeurNull2EqualsproduitConstructeurNull1);
			System.out.println("produitConstructeurNull1EqualsproduitConstructeurNull1 : " + produitConstructeurNull1EqualsproduitConstructeurNull1);
			System.out.println("produitConstructeurNull1HashCode : " + produitConstructeurNull1HashCode);
			System.out.println("produitConstructeurNull2HashCode : " + produitConstructeurNull2HashCode);
			System.out.println();

		}
		
		
		// ASSERT - THEN
		/* vérifie que toutes les propriétés de l'objet sont null. */
		assertNull(idObjetConstructeurNull1, "l'ID de produitConstructeurNull1 doit être null : ");
		assertNull(idObjetConstructeurNull2, "l'ID de produitConstructeurNull2 doit être null : ");
		
		assertNull(produitConstructeurNullString1, "produitConstructeurNullString1 doit être null : ");
		assertNull(produitConstructeurNullString2, "produitConstructeurNullString2 doit être null : ");
		
		assertNull(sousTypeProduitNull1, "sousTypeProduitNull1 doit être null : ");
		assertNull(sousTypeProduitNull2, "sousTypeProduitNull2 doit être null : ");
		
		assertNull(typeProduitNull1, "typeProduitNull1 doit être null : ");
		assertNull(typeProduitNull2, "typeProduitNull2 doit être null : ");
		
		/* vérifie que les Booleans valide sont à false. */
		assertFalse(valideProduitConstructeurNull1, "valide doit être à false dans produitConstructeurNull1 : ");
		assertFalse(valideProduitConstructeurNull2, "valide doit être à false dans produitConstructeurNull2 : ");
		
		/* vérifie que 2 instances créées avec le constructeur null sont différentes. */
		assertFalse(memeInstance1, "produitConstructeurNull1 == produitConstructeurNull2 doit retourner false : ");
		assertFalse(memeInstance2, "produitConstructeurNull2 == produitConstructeurNull1 doit retourner false : ");
		
		/* vérifie que 2 instances créées avec le constructeur null sont equals. */
		assertTrue(produitConstructeurNull1EqualsproduitConstructeurNull2, "produitConstructeurNull1 doit être Equals() à produitConstructeurNull2 : ");
		assertTrue(produitConstructeurNull2EqualsproduitConstructeurNull1, "produitConstructeurNull2 doit être Equals() à produitConstructeurNull1 : ");
		assertTrue(produitConstructeurNull1EqualsproduitConstructeurNull1, "produitConstructeurNull1 doit être Equals() à produitConstructeurNull1 : ");
		assertEquals(produitConstructeurNull1HashCode, produitConstructeurNull2HashCode, "produitConstructeurNull1 doit avoir le même Hashcode que produitConstructeurNull2 : ");

		
		//**** ARRANGE - GIVEN
		// ACT - WHEN
		// INSTANCIATION - Utilisation des Setters
		objetConstructeurNull1.setIdProduit(1L);
		objetConstructeurNull1.setProduit("canne à pêche à moulinet");
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println("*** APRES produitConstructeurNull1.setIdProduit(1L) et produitConstructeurNull1.setProduit(\"canne à pêche à moulinet\"); ***");
			this.afficher(objetConstructeurNull1);
		}
		
		// ASSERT - THEN
		/* vérifie que les setters fonctionnent correctement.*/
		assertNull(objetConstructeurNull1.getSousTypeProduit(), "le sous-type ProduitI de produitConstructeurNull1 doit être null à ce stade : ");
		assertNull(objetConstructeurNull1.getTypeProduit(), "le type ProduitI de produitConstructeurNull1 doit être null à ce stade : ");
		assertFalse(objetConstructeurNull1.isValide(), "produitConstructeurNull1 ne doit pas être valide à ce stade : ");

		
		//**** ARRANGE - GIVEN
		// ACT - WHEN
		// INSTANCIATION - Utilisation des Setters (suite)
		final TypeProduit typeProduitPeche = new TypeProduit(10L, "Pêche", null);
		final SousTypeProduit sousTypeProduitCanneMoulinet = new SousTypeProduit(100L, "Cannes et Moulinets", typeProduitPeche, null);
		objetConstructeurNull1.setSousTypeProduit(sousTypeProduitCanneMoulinet);
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("*** APRES TypeProduit typeProduitPeche = new TypeProduit(10L, \"Pêche\", null); et SousTypeProduit sousTypeProduitCanneMoulinet = new SousTypeProduit(100L, \"Cannes et Moulinets\", typeProduitPeche, null); ***");
			System.out.println("*** APRES produitConstructeurNull1.setSousTypeProduit(sousTypeProduitCanneMoulinet); ***");
			this.afficher(objetConstructeurNull1);
		}
		
		// ASSERT - THEN
		/* vérifie que les setters fonctionnent correctement.*/
		assertEquals(objetConstructeurNull1.getSousTypeProduit().getSousTypeProduit(), "Cannes et Moulinets", "le sous-type du ProduitI doit être 'Cannes et Moulinets' : ");
		assertEquals(objetConstructeurNull1.getTypeProduit().getTypeProduit(), "Pêche", "le type de ProduitI doit être 'Pêche' à ce stade : ");
		assertTrue(objetConstructeurNull1.isValide(), "produitConstructeurNull1 doit être valide à ce stade : ");
		
	} //___________________________________________________________________
	

	
	/**
	 * <div>
	 * <p>vérifie le comportement général de la classe ProduitI</p>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testGeneral() : vérifie le comportement général de la classe ProduitI")
	@Tag("constructeurs")
	@Test
	public final void testGeneral() {
				
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE TypeProduitTest - méthode testGeneral() ********** ");
			System.out.println("CE TEST VERIFIE LE FONCTIONNEMENT ORDINAIRE de la classe ProduitI.");
			System.out.println();				
		}
		
		// ARRANGE - GIVEN
		this.creerVetement();
		
		if (this.listeProduitsVetement != null) {
			Collections.sort(this.listeProduitsVetement);
		}
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			this.afficherListeProduits(this.listeProduitsVetement);
		}
		
				
		// ACT - WHEN
		final SousTypeProduit sousTypeProduitVetementHomme 
			= (SousTypeProduit) this.produitChemiseManchesCourtesHomme.getSousTypeProduit();
		
		final SousTypeProduit sousTypeProduitVetementHomme2 
			= (SousTypeProduit) this.produitChemiseManchesLonguesHomme.getSousTypeProduit();
		
		final SousTypeProduit sousTypeProduitAvecMauvaisSousTypeProduit 
			= (SousTypeProduit) this.produitAvecMauvaisSousTypeProduit.getSousTypeProduit();
		
		final SousTypeProduit sousTypeProduitVetementFemme 
			= (SousTypeProduit) this.produitChemiseManchesLonguesFemme.getSousTypeProduit();
	
		
		final List<? extends ProduitI> listeProduitsDuSousTypeProduitVetementHomme 
			= new ArrayList<>(sousTypeProduitVetementHomme.getProduits());
		
		final List<? extends ProduitI> listeProduitsDuSousTypeProduitVetementHomme2 
			= new ArrayList<>(sousTypeProduitVetementHomme2.getProduits());
		
		final List<? extends ProduitI> listeProduitsDuSousTypeProduitAvecMauvaisSousTypeProduit 
			=  new ArrayList<>(sousTypeProduitAvecMauvaisSousTypeProduit.getProduits());
		
		final List<? extends ProduitI> listeProduitsDuSousTypeProduitVetementFemme 
			= new ArrayList<>(sousTypeProduitVetementFemme.getProduits());
		
		
	
		Collections.sort(listeProduitsDuSousTypeProduitVetementHomme);
	
		Collections.sort(listeProduitsDuSousTypeProduitVetementHomme2);
		
		Collections.sort(listeProduitsDuSousTypeProduitAvecMauvaisSousTypeProduit);
		
		Collections.sort(listeProduitsDuSousTypeProduitVetementFemme);
	
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println("**** Produits du sous-type ProduitI vêtement homme ******");
			this.afficherListeProduits(listeProduitsDuSousTypeProduitVetementHomme);
			System.out.println();
			System.out.println("**** Produits du sous-type ProduitI vêtement homme 2 ******");
			this.afficherListeProduits(listeProduitsDuSousTypeProduitVetementHomme2);
			System.out.println();
			System.out.println("**** Produits du sous-type ProduitI AvecMauvaisSousTypeProduit ******");
			this.afficherListeProduits(listeProduitsDuSousTypeProduitAvecMauvaisSousTypeProduit);
			System.out.println();
			System.out.println("**** Produits du sous-type ProduitI vêtement femme ******");
			this.afficherListeProduits(listeProduitsDuSousTypeProduitVetementFemme);
			System.out.println();
		}
		
		
		
		// ASSERT - THEN
		assertTrue(listeProduitsDuSousTypeProduitVetementHomme.size() == 3, "Il doit y avoir 3 éléments dans listeProduitsDuSousTypeProduitVetementHomme : ");
		
		
	} //___________________________________________________________________



	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>equals(Object pObj)</b> :</p>
	 * <li>x.equals(mauvaise instance) retourne false.</li>
	 * <li>garantit le contrat Java reflexif x.equals(x).</li>
	 * <li>garantit le contrat Java symétrique 
	 * x.equals(y) ----> y.equals(x).</li>
	 * <li>garantit le contrat Java transitif 
	 * x.equals(y) et y.equals(z) ----> x.equals(z).</li>
	 * <li>garantit le contrat Java sur les hashcode 
	 * x.equals(y) ----> x.hashcode() == y.hashcode().</li>
	 * <li>garantit que les null sont bien gérés 
	 * dans equals(Object pObj).</li>
	 * <li>garantit que x.equals(null) retourne false 
	 * (avec x non null).</li>
	 * <li>garantit le bon fonctionnement de equals() 
	 * en cas d'égalité métier.</li>
	 * <li>garantit le bon fonctionnement de equals() 
	 * en cas d'inégalité métier.</li>
	 * <li>garantit le bon fonctionnement de Collections.sort()</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings({"unlikely-arg-type", UNUSED})
	@DisplayName("testEquals() : vérifie le respect du contrat Java pour equals() et hashCode()")
	@Tag("equals")
	@Test
	public final void testEquals() {
		
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE ProduitTest - méthode testEquals() ********** ");
			System.out.println("CE TEST VERIFIE LE RESPECT DU CONTRAT Java Equals() et Hashcode().");
			System.out.println();				
		}
		
		// ARRANGE - GIVEN
		final TypeProduitI typeProduit1 = new TypeProduit(PHOTOGRAPHIE);
		final TypeProduitI typeProduit2 = new TypeProduit(PHOTOGRAPHIE);
		final SousTypeProduitI sousTypeProduit1 = new SousTypeProduit(CAMERAS, typeProduit1);
		final SousTypeProduitI sousTypeProduit2 = new SousTypeProduit(CAMERAS, typeProduit1);
		final SousTypeProduitI sousTypeProduitDifferent = new SousTypeProduit("camescopes", typeProduit1);
		final ProduitI objet1 = new Produit(APPAREIL_PHOTO, sousTypeProduit1);
		final ProduitI objet2 = new Produit(APPAREIL_PHOTO, sousTypeProduit2);
		final ProduitI objet3 = new Produit(APPAREIL_PHOTO, sousTypeProduit1);
		final ProduitI objet4 = new Produit(APPAREIL_PHOTO, sousTypeProduitDifferent);
		final ProduitI objet5 = new Produit("ZZ Top", sousTypeProduit1);
		final NullPointerException mauvaiseInstance = new NullPointerException();
		
		final List<ProduitI> listeProduits = new ArrayList<ProduitI>();
		listeProduits.add(objet1);
		listeProduits.add(objet2);
		listeProduits.add(objet3);
		listeProduits.add(objet4);
		listeProduits.add(objet5);
		
		/* tri de la liste; */
		Collections.sort(listeProduits);
		
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println("***APRES ProduitI objet1 = new ProduitI(\"appareil photo\", sousTypeProduit1);***");
			this.afficher(objet1);
			System.out.println();
			System.out.println("***APRES ProduitI objet2 = new ProduitI(\"appareil photo\", sousTypeProduit2);***");
			this.afficher(objet2);
			System.out.println();
			System.out.println("***APRES ProduitI objet3 = new ProduitI(\"appareil photo\", sousTypeProduit1);***");
			this.afficher(objet3);
			System.out.println();
			System.out.println("********** AFFICHAGE DE LA LISTE DE PRODUITS ******");
			this.afficherListeProduits(listeProduits);
		}
		
		// ACT - WHEN
		
		// ASSERT - THEN
		/* garantit que x.equals(mauvaise instance) retourne false. */
		assertFalse(objet1.equals(mauvaiseInstance), "x.equals(mauvaise instance) doit retourner false : ");
		
		/* garantit le contrat Java reflexif x.equals(x). */
		assertEquals(objet1, objet1, "x.equals(x) : ");
		
		/* garantit le contrat Java symétrique 
		 * x.equals(y) ----> y.equals(x). */
		assertNotSame(objet1, objet2, "objet1 et objet2 ne sont pas la même instance : ");
		assertEquals(objet1, objet2, "objet1.equals(objet2) : ");
		assertEquals(objet2, objet1, "objet2.equals(objet1) : ");
		
		/* garantit le contrat Java transitif 
		 * x.equals(y) et y.equals(z) ----> x.equals(z). */
		assertEquals(objet1, objet2, "objet1.equals(objet2) : ");
		assertEquals(objet2, objet3, "objet2.equals(objet3) : ");
		assertEquals(objet1, objet3, "objet1.equals(objet3) : ");
		
		/* garantit le contrat Java sur les hashcode 
		 * x.equals(y) ----> x.hashcode() == y.hashcode(). */
		assertEquals(objet1.hashCode(), objet2.hashCode(), "objet1.hashCode().equals(objet2.hashCode()) : ");

		
		//*** ARRANGE - GIVEN
		final ProduitI objetConstructeurNull1 = new Produit();
		final ProduitI objetConstructeurNull2 = new Produit();
		
		final ProduitI objetAvecValeursNull1 = new Produit(1L, null, sousTypeProduit1);
		final ProduitI objetAvecValeursNull2 = new Produit(2L, null, sousTypeProduit2);

		final ProduitI objetAvecValeursVide1 = new Produit(1L, "", sousTypeProduit1);
		final ProduitI objetAvecValeursVide2 = new Produit(2L, "", sousTypeProduit2);
		
		final ProduitI objetAvecSousValeursNull1 = new Produit(1L, DINDON, null);
		final ProduitI objetAvecSousValeursNull2 = new Produit(2L, DINDON, null);

		// ACT - WHEN
		
		// ASSERT - THEN
		/* garantit que les null sont bien gérés dans equals(...). */
		assertEquals(objetConstructeurNull1, objetConstructeurNull2, "objetConstructeurNull1.equals(objetConstructeurNull2) : ");
		assertEquals(objetConstructeurNull1.hashCode(), objetConstructeurNull2.hashCode(), "objetConstructeurNull1.hashcode() == objetConstructeurNull2.hashcode()) : ");
		
		assertEquals(objetAvecValeursNull1, objetAvecValeursNull2, "objetAvecValeursNull1.equals(objetAvecValeursNull2) : ");
		assertEquals(objetAvecValeursVide1, objetAvecValeursVide2, "objetAvecValeursVide1.equals(objetAvecValeursVide2) : ");
		assertEquals(objetAvecSousValeursNull1, objetAvecSousValeursNull2, "objetAvecSousValeursNull1.equals(objetAvecSousValeursNull2) : ");

		
		//*** ARRANGE - GIVEN
		final ProduitI objetPasEqualsObjet1 = new Produit("camescope", sousTypeProduit1);
		final ProduitI objetPasEqualsObjet2 = new Produit(APPAREIL_PHOTO, sousTypeProduitDifferent);
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println("***APRES ProduitI objetPasEqualsObjet1 = new ProduitI(\"camescope\", sousTypeProduit1);***");
			this.afficher(objetPasEqualsObjet1);
			System.out.println();
			System.out.println("***APRES objetPasEqualsObjet2 = new ProduitI(\"appareil photo\", sousTypeProduitDifferent);***");
			this.afficher(objetPasEqualsObjet2);
			System.out.println();
		}
		
		// ACT - WHEN
		
		// ASSERT - THEN
		/* garantit que x.equals(null) retourne false (avec x non null). */
		assertNotNull(objet1, "objet1.equals(null) retourne false (avec objet1 non null) : ");
		
		/* garantit le bon fonctionnement de equals() 
		 * en cas d'égalité métier. */
		assertEquals(objet1, objet2, "objet1.equals(objet2) : ");
		
		/* garantit le bon fonctionnement de equals() 
		 * en cas d'inégalité métier. */
		assertNotEquals(objet1, objetPasEqualsObjet1, "objet1 n'est pas equals() avec objetPasEqualsObjet1 : ");
		assertNotEquals(objet1, objetPasEqualsObjet2, "objet1 n'est pas equals() avec objetPasEqualsObjet2 : ");

	} //___________________________________________________________________

	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>toString()</b> :</p>
	 * <li>garantit le bon fonctionnement avec les null.</li>
	 * <li>garantit le bon fonctionnement de toString()</li>
	 *</ul>
	 *</div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testToString() : vérifie le bon fonctionnement de toString()")
	@Tag("toString")
	@Test
	public final void testToString() {
		
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE ProduitTest - méthode testToString() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode toString().");
			System.out.println();				
		}
		
		//**** ARRANGE - GIVEN
		final ProduitI objetConstructeurNull = new Produit();
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("******* Test d'un objet construit avec le constructeur d'arité nulle *******");
			System.out.println("*** objetConstructeurNull ****");
			this.afficher(objetConstructeurNull);
			System.out.println();
			System.out.println("objetConstructeurNull.toString() : " + objetConstructeurNull.toString());
		}
		
		// ACT - WHEN
		
		// ASSERT - THEN
		/* garantit le bon fonctionnement avec les null. */
		assertEquals("Produit [idProduit=null, produit=null, sousTypeProduit=null]", objetConstructeurNull.toString(), "doit afficher Produit [idProduit=null, produit=null, sousTypeProduit=null] : ");
		
		
		//**** ARRANGE - GIVEN
		final TypeProduitI typeProduit1 = new TypeProduit(ANATOMIE);
		final SousTypeProduitI sousTypeProduit1 = new SousTypeProduit(ANATOMIE_MAIN, typeProduit1);
		final ProduitI objet1 = new Produit(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit1);
		
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("******* Test d'un objet construit avec le constructeur complet *******");
			System.out.println("*** objet1 ****");
			this.afficher(objet1);
			System.out.println();
			System.out.println("objet1.toString() : " + objet1.toString());
		}

		
		// ACT - WHEN
		final String resultat = "Produit [idProduit=1, produit=Anatomie arthroscopique de la main"
				+ ", sousTypeProduit=SousTypeProduit [idSousTypeProduit=null"
				+ ", sousTypeProduit=Anatomie de la main"
				+ ", typeProduit=TypeProduit [idTypeProduit=null, typeProduit=Anatomie]]]";

		
		// ASSERT - THEN
		/* garantit le bon fonctionnement de toString() */
		assertEquals(resultat, objet1.toString(), "doit afficher toString() : ");

	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>Teste la méthode <b>toString()</b> en environnement multi-thread.</p>
	 * <ul>
	 * <li>Vérifie que l'appel concurrent à toString() ne provoque pas d'erreurs.</li>
	 * <li>Utilise un ExecutorService pour simuler des accès concurrents.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings({ RESOURCE, UNUSED })
	@DisplayName("testToStringThreadSafe() : vérifie le thread-safety de toString()")
	@Tag(THREAD_SAFETY)
	@Test
	public final void testToStringThreadSafe() throws InterruptedException, ExecutionException {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;
	
	    /*
	     * ARRANGE - GIVEN : Création d'un Produit.
	     */
	    final TypeProduitI typeProduit = new TypeProduit(ANATOMIE);
	    final SousTypeProduitI sousTypeProduit = new SousTypeProduit(ANATOMIE_MAIN, typeProduit);
	    final ProduitI produit = new Produit(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit);
	
	    /*
	     * ACT - WHEN : Exécution concurrente de toString().
	     */
	    final ExecutorService executor = Executors.newFixedThreadPool(10);
	    final List<Callable<String>> tasks = new ArrayList<>();
	    for (int i = 0; i < 100; i++) {
	        tasks.add(() -> produit.toString());
	    }
	
	    final List<Future<String>> results = executor.invokeAll(tasks);
	    executor.shutdown();
	
	    /*
	     * ASSERT - THEN : Vérification des résultats.
	     */
	    for (final Future<String> result : results) {
	        assertNotNull(result.get(), 
	        		"toString() ne doit jamais retourner null en environnement multi-thread.");
	    }
	
	    /*
	     * AFFICHAGE A LA CONSOLE.
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("***** Test toString() en multi-thread réussi *****");
	        System.out.println("Résultat de toString() : " + produit.toString());
	    }
	} //___________________________________________________________________



	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>compareTo(ProduitI pObject)</b> :</p>
	 * <li>garantit que compareTo(memeInstance) retourne 0.</li>
	 * <li>garantit que compareTo(null) retourne un nombre négatif.</li>
	 * <li>garantit le Contrat Java : 
	 * x.equals(y) ---> x.compareTo(y) == 0.</li>
	 * <li>garantit que les null sont bien gérés 
	 * dans compareTo(ProduitI pObject).</li>
	 * <li>garantit le bon fonctionnement (bon ordre) de compareTo().</li>
	 * <li>garantit le bon fonctionnement de Collections.sort()</li>
	 * </ul>
	 * <div>
	 * <p>
	 * <img src="../../../../../../../../../javadoc/images/model/metier/methodes/activités_methode_compareTo.jpg" 
	 * alt="méthode compareTo()" border="1" align="center" height= 800px />
	 * </p>
	 * <p>
	 * <img src="../../../../../../../../../javadoc/images/model/metier/methodes/activités_methode_compareTo_2valeurs.jpg" 
	 * alt="méthode compareTo()" border="1" align="center" height= 800px />
	 * </p>
	 * </div>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testCompareTo() : vérifie le respect du contrat Java pour compareTo()")
	@Tag("compareTo")
	@Test
	public final void testCompareTo() {
		
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE ProduitTest - méthode testCompareTo() ********** ");
			System.out.println("CE TEST VERIFIE LE RESPECT des contrats Java de la méthode compareTo().");
			System.out.println();				
		}
		
		//*** ARRANGE - GIVEN
		/* TypeProduit */
		final TypeProduitI typeProduit1 = new TypeProduit(ANATOMIE);
		final TypeProduitI typeProduit2 = new TypeProduit(PHOTOGRAPHIE);
		/* SousTypeProduit. */
		final SousTypeProduitI sousTypeProduit1 = new SousTypeProduit(ANATOMIE_MAIN, typeProduit1);
		final SousTypeProduitI sousTypeProduit2 = new SousTypeProduit(CAMERAS, typeProduit2);
		/* ProduitI. */
		final ProduitI objet1 = new Produit(ANATOMIE_ARTHRO_MAIN, sousTypeProduit1);
		final ProduitI objetEqualsObjet1 = new Produit(ANATOMIE_ARTHRO_MAIN, sousTypeProduit1);
		final ProduitI objet1MemeInstance = objet1;
		final ProduitI objet2ApresObjet1 = new Produit(ANATOMIE_ARTHRO_MAIN, sousTypeProduit2);
		final ProduitI objet3ApresObjet1 = new Produit("Zétamine", sousTypeProduit1);
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println("***APRES ProduitI objet1 = new ProduitI(ANATOMIE_ARTHRO_MAIN, sousTypeProduit1);***");
			this.afficher(objet1);
			System.out.println();
			System.out.println("***APRES ProduitI objet2ApresObjet1 = new ProduitI(ANATOMIE_ARTHRO_MAIN, sousTypeProduit2);***");
			this.afficher(objet2ApresObjet1);
			System.out.println();
			System.out.println("***APRES ProduitI objet3ApresObjet1 = new ProduitI(\"Zétamine\", sousTypeProduit1);***");
			this.afficher(objet3ApresObjet1);
			System.out.println();
		}
		
		
		// ACT - WHEN
		final int compareMemeInstance 
			= objet1.compareTo(objet1MemeInstance);
		final int compareToNull = objet1.compareTo(null);
		final int compareToEquals = objet1.compareTo(objetEqualsObjet1);
		
		// ASSERT - THEN
		/* garantit que compareTo(memeInstance) retourne 0. */
		assertTrue(compareMemeInstance == 0, "compareTo(memeInstance) doit retourner 0 : ");
		
		/* garantit que compareTo(null) retourne -1. */
		assertTrue(compareToNull < 0, "compareTo(null) doit retourner négatif : ");
		
		/* garantit le Contrat Java : 
		 * x.equals(y) ---> x.compareTo(y) == 0. */	
		assertNotSame(objet1, objetEqualsObjet1, "objet1 n'est pas la même instance que objetEqualsObjet1 : ");
		assertEquals(objet1, objetEqualsObjet1, "objet1 equals objetEqualsObjet1 : ");
		assertEquals(objet1.hashCode(), objetEqualsObjet1.hashCode(), "objet1.hashCode() == objetEqualsObjet1.hashCode() : ");
		assertTrue(compareToEquals == 0, "objet1 equals objetEqualsObjet1 ----> objet1.compareTo(objetEqualsObjet1) == 0 : ");
		
				
		//*** ARRANGE - GIVEN
		final ProduitI objetConstructeurNull1 = new Produit();
		final ProduitI objetConstructeurNull2 = new Produit();
		
		final ProduitI objetAvecValeursNull1 = new Produit(1L, null, sousTypeProduit1);
		final ProduitI objetAvecValeursNull2Apres1 = new Produit(2L, null, sousTypeProduit2);

		final ProduitI objetAvecValeursVide1 = new Produit(1L, "", sousTypeProduit1);
		final ProduitI objetAvecValeursVide2 = new Produit(2L, "", sousTypeProduit2);
		
		final ProduitI objetAvecSousValeursNull1 = new Produit(1L, DINDON, null);
		final ProduitI objetAvecSousValeursNull2 = new Produit(2L, DINDON, null);
		
		// ACT - WHEN
		final int compareToConstructeurNull 
			= objetConstructeurNull1.compareTo(objetConstructeurNull2);
		final int compareToAvecValeursNull 
			= objetAvecValeursNull1.compareTo(objetAvecValeursNull2Apres1);
		final int compareToAvecValeursVides 
			= objetAvecValeursVide1.compareTo(objetAvecValeursVide2);
		
		
		// ASSERT - THEN
		/* garantit que les null sont bien gérés dans compareTo(). */
		assertTrue(compareToConstructeurNull == 0, "objetConstructeurNull1.compareTo(objetConstructeurNull2) == 0 : ");
		assertTrue(compareToAvecValeursNull < 0, "objetAvecValeursNull1.compareTo(objetAvecValeursNull2Apres1) < 0 : ");
		assertTrue(compareToAvecValeursVides < 0, "objetAvecValeursVide1.compareTo(objetAvecValeursVide2)  < 0 : ");

		
		//*** ARRANGE - GIVEN
		final ProduitI objetAvecV1Null = new Produit(ANATOMIE, null);
		final ProduitI objetAvecV1PasNull = new Produit(ANATOMIE, sousTypeProduit2);
		final ProduitI objetAvecV1PasNullAvantObjet2 = new Produit("toto", sousTypeProduit1);
		final ProduitI objetAvecV1PasNullApresObjet1 = new Produit("toto", sousTypeProduit2);
		final ProduitI objetAvecV2PasNullAvantObjet2 = new Produit("aaa", sousTypeProduit1);
		final ProduitI objetAvecV2PasNullApresObjet1 = new Produit("zzz", sousTypeProduit1);
		final ProduitI objetAvecV2PasNullIdemObjet2 = new Produit("idem", sousTypeProduit1);
		final ProduitI objetAvecV2PasNullIdemObjet1 = new Produit("idem", sousTypeProduit1);
		final ProduitI objetAvecV2Null1 = new Produit(null, sousTypeProduit1);
		final ProduitI objetAvecV2Null2 = new Produit(null, sousTypeProduit1);
		
		// ACT - WHEN
		
		// ASSERT - THEN
		/* garantit que this.v1 == null et other.v1 != null retourne > 0 . */
		assertTrue(objetAvecV1Null.compareTo(objetAvecV1PasNull) > 0, "this.v1 == null et other.v1 != null doit retourner > 0 : ");
		/* garantit que this.v1 != null et other.v1 == null retourne < 0 . */
		assertTrue(objetAvecV1PasNull.compareTo(objetAvecV1Null) < 0, "this.v1 != null et other.v1 == null doit retourner < 0 : ");
		/* garantit que this.v1 != null et other.v1 != null et this.v1.compareTo(other.v1) != 0 retourne this.v1.compareTo(other.v1) . */
		assertTrue(objetAvecV1PasNullAvantObjet2.compareTo(objetAvecV1PasNullApresObjet1) < 0, "this.v1 != null et other.v1 != null et this.v1.compareTo(other.v1) != 0 doit retourner this.v1.compareTo(other.v1) : ");
		/* garantit que this.v2 != null et other.v2 != null et this.v2.compareTo(other.v2) != 0 retourne this.v2.compareTo(other.v2). */
		assertTrue(objetAvecV2PasNullAvantObjet2.compareTo(objetAvecV2PasNullApresObjet1) < 0, "this.v2 != null et other.v2 != null et this.v2.compareTo(other.v2) != 0 doit retourner this.v2.compareTo(other.v2) : ");
		/* garantit que this.v2 != null et other.v2 != null et this.v2.compareTo(other.v2) == 0 retourne this.v2.compareTo(other.v2). */
		assertTrue(objetAvecV2PasNullIdemObjet2.compareTo(objetAvecV2PasNullIdemObjet1) == 0, "this.v2 != null et other.v2 != null et this.v2.compareTo(other.v2) == 0 doit retourner this.v2.compareTo(other.v2) : ");
		/* garantit que this.v2 == null et other.v2 == null et v1 pareils non nuls retourne 0. */
		assertTrue(objetAvecV2Null1.compareTo(objetAvecV2Null2) == 0, "this.v2 == null et other.v2 == null et v1 pareils non nuls doit retourner 0 : ");
		
		
		final List<ProduitI> listeProduits = new ArrayList<ProduitI>();
		listeProduits.add(objet1);
		listeProduits.add(objetEqualsObjet1);
		listeProduits.add(objet1MemeInstance);
		listeProduits.add(objet2ApresObjet1);
		listeProduits.add(objet3ApresObjet1);
		listeProduits.add(objetConstructeurNull1);
		listeProduits.add(objetConstructeurNull2);
		listeProduits.add(objetAvecValeursNull1);
		listeProduits.add(objetAvecValeursNull2Apres1);
		listeProduits.add(objetAvecValeursVide1);
		listeProduits.add(objetAvecValeursVide2);
		listeProduits.add(objetAvecSousValeursNull1);
		listeProduits.add(objetAvecSousValeursNull2);
		listeProduits.add(objetAvecV1PasNull);
		listeProduits.add(objetAvecV1PasNullAvantObjet2);
		listeProduits.add(objetAvecV1PasNullApresObjet1);
		listeProduits.add(objetAvecV2PasNullAvantObjet2);
		listeProduits.add(objetAvecV2PasNullApresObjet1);
		listeProduits.add(objetAvecV2PasNullIdemObjet2);
		listeProduits.add(objetAvecV2PasNullIdemObjet1);
		listeProduits.add(objetAvecV2Null1);
		listeProduits.add(objetAvecV2Null2);
		
		
		/* tri de la liste; */
		Collections.sort(listeProduits);
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println("********** AFFICHAGE DE LA LISTE DE PRODUITS ******");
			this.afficherListeProduits(listeProduits);

		}
		
	} //___________________________________________________________________
	

	
	/**
	 * <div>
	 * <p>Teste la méthode <b>compareTo(ProduitI)</b> en environnement multi-thread.</p>
	 * <ul>
	 * <li>Vérifie que l'appel concurrent à compareTo() retourne toujours un résultat cohérent.</li>
	 * <li>Utilise un ExecutorService pour simuler des comparaisons concurrentes.</li>
	 * <li>Synchronise les accès aux objets pour éviter les incohérences.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings({ RESOURCE, UNUSED })
	@DisplayName("testCompareToThreadSafe() : vérifie le thread-safety de compareTo(ProduitI)")
	@Tag(THREAD_SAFETY)
	@Test
	public final void testCompareToThreadSafe()
	        throws InterruptedException, ExecutionException {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;
	
	    /*
	     * ARRANGE - GIVEN : Création des objets nécessaires.
	     */
	    final TypeProduitI typeProduit = new TypeProduit(ANATOMIE);
	    final SousTypeProduitI sousTypeProduit = new SousTypeProduit(ANATOMIE_MAIN, typeProduit);
	    final ProduitI produit1 = new Produit(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit);
	    final ProduitI produit2 = new Produit(2L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit);
	
	    /*
	     * Résultat attendu (calculé une fois avant le test).
	     */
	    final int expectedResult = produit1.compareTo(produit2);
	
	    /*
	     * ACT - WHEN : Exécution concurrente de compareTo().
	     */
	    final ExecutorService executor = Executors.newFixedThreadPool(10);
	    final List<Callable<Integer>> tasks = new ArrayList<>();
	    for (int i = 0; i < 100; i++) {
	        tasks.add(() -> {
	            synchronized (produit1) {
	                synchronized (produit2) {
	                    return produit1.compareTo(produit2);
	                }
	            }
	        });
	    }
	
	    final List<Future<Integer>> results = executor.invokeAll(tasks);
	    executor.shutdown();
	
	    /*
	     * ASSERT - THEN : Vérification des résultats.
	     */
	    for (final Future<Integer> result : results) {
	        assertEquals(expectedResult, result.get(),
	                "compareTo() doit toujours retourner le même résultat en environnement multi-thread.");
	    }
	
	    /*
	     * AFFICHAGE A LA CONSOLE.
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("***** Test compareTo() en multi-thread réussi *****");
	        System.out.println("Résultat attendu : " + expectedResult);
	        System.out.println("Résultat obtenu : " + results.get(0).get());
	    }
	} //___________________________________________________________________



	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>clone()</b> :</p>
	 * <li>garantit que les null sont bien gérés dans clone().</li>
	 * <li>garantit que clonex.equals(x).</li>
	 * <li>garantit que clonex.getClass() == x.getClass().</li>
	 * <li>garantit que x et son clone ne sont pas la même instance.</li>
	 * <li>garantit le clonage profond des propriétés.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws CloneNotSupportedException
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testClone() : vérifie le respect du contrat Java pour clone()")
	@Tag("clone")
	@Test
	public final void testClone() throws CloneNotSupportedException {
				
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE ProduitTest - méthode testClone() ********** ");
			System.out.println("CE TEST VERIFIE LE RESPECT des contrats Java de la méthode clone().");
			System.out.println();				
		}
		
		//***** ARRANGE - GIVEN
		final Produit objetConstructeurNull = new Produit();

		// ACT - WHEN
		final Produit objetConstructeurNullClone 
			= (Produit) objetConstructeurNull.clone();
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("******* Test d'un objet construit avec le constructeur d'arité nulle et de son clone *******");
			System.out.println("*** objetConstructeurNull ****");
			this.afficher(objetConstructeurNull);
			System.out.println();
			System.out.println("*** objetConstructeurNullClone ****");
			this.afficher(objetConstructeurNullClone);
			System.out.println();
		}
		
		// ASSERT - THEN
		/* garantit que les null sont bien gérés dans clone(). */
		assertFalse(objetConstructeurNull == objetConstructeurNullClone, "objetConstructeurNull et objetConstructeurNullClone ne doivent pas être la même instance : ");
		assertEquals(objetConstructeurNull, objetConstructeurNullClone, "objetConstructeurNull doit être equals() à objetConstructeurNullClone : ");
		assertEquals(objetConstructeurNull.getClass(), objetConstructeurNullClone.getClass(), "objetConstructeurNull.getClass() doit être equals() à objetConstructeurNullClone.getClass() : ");

		
		//***** ARRANGE - GIVEN
		final TypeProduitI typeProduit1 = new TypeProduit(ANATOMIE);
		final SousTypeProduitI sousTypeProduit1 = new SousTypeProduit(ANATOMIE_MAIN, typeProduit1);
		final Produit objet1 = new Produit(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit1);
		
		// ACT - WHEN
		final Produit objet1Clone = objet1.clone();
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("******* Test d'un objet construit avec le constructeur complet et de son clone *******");
			System.out.println("*** objet1 ****");
			this.afficher(objet1);
			System.out.println();
			System.out.println("*** objet1Clone ****");
			this.afficher(objet1Clone);
			System.out.println();
		}
		
		// ASSERT - THEN
		/* garantit le contrat Java de clone() */
		assertFalse(objet1 == objet1Clone, "objet1 et objet1Clone ne doivent pas être la même instance : ");
		assertEquals(objet1, objet1Clone, "objet1 doit être equals() à objet1Clone : ");
		assertEquals(objet1.getClass(), objet1Clone.getClass(), "objet1.getClass() doit être equals() à objet1Clone.getClass() : ");
		
		
		//***** ARRANGE - GIVEN
		final SousTypeProduit sousTypeProduit2 = new SousTypeProduit("Anatomie modifiée", typeProduit1);
		final ProduitI produit2 = new Produit("ProduitI modifié", sousTypeProduit2);
		objet1Clone.setIdProduit(2L);		
		objet1Clone.setSousTypeProduit(sousTypeProduit2);
		objet1Clone.setProduit(produit2.getProduit());
		
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("******* Test d'un objet après modification du Clone *******");
			System.out.println("*** objet1 ****");
			this.afficher(objet1);
			System.out.println();
			System.out.println("*** objet1Clone ****");
			this.afficher(objet1Clone);
			System.out.println();
		}
		
		// ACT - WHEN

		/* garantit le clonage profond des propriétés. */
		/* La modification des propriétés du clone ne doit pas modifier les propriétés de l'objet cloné. */
		// ASSERT - THEN
		assertNotSame(objet1, objet1Clone, "objet1 et objet1Clone ne doivent pas être les mêmes instances : ");
		assertFalse(objet1.getIdProduit() == objet1Clone.getIdProduit(), "la modification de l'ID dans le clone ne doit pas modifier l'ID dans l'objet cloné : ");
		assertFalse(objet1.getProduit().equals(objet1Clone.getProduit()), "la modification du ProduitI dans le clone ne doit pas modifier le ProduitI dans l'objet cloné : ");
		assertFalse(objet1.getSousTypeProduit().equals(objet1Clone.getSousTypeProduit()), "la modification du sousTypeProduit dans le clone ne doit pas modifier le sousTypeProduit dans l'objet cloné : ");
		
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>Teste la méthode <code>deepClone(CloneContext)</code>.</p>
	 * <ul>
	 * <li>Vérifie le clonage profond avec gestion des cycles.</li>
	 * <li>Vérifie que le clone est indépendant de l'original.</li>
	 * <li>Vérifie la cohérence des relations après clonage.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testDeepClone() : vérifie le clonage profond avec CloneContext")
	@Tag("clone")
	@Test
	public final void testDeepClone() {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;

	    /*
	     * ARRANGE - GIVEN : Création des objets nécessaires
	     */
	    final TypeProduitI typeProduit = new TypeProduit(ANATOMIE);
	    final SousTypeProduitI sousTypeProduit = new SousTypeProduit(ANATOMIE_MAIN, typeProduit);
	    final ProduitI original = new Produit(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit);

	    /*
	     * ACT - WHEN : Clonage profond
	     */
	    final CloneContext ctx = new CloneContext();
	    final ProduitI clone = original.deepClone(ctx);

	    /*
	     * ASSERT - THEN : Vérifications
	     */
	    assertNotSame(original, clone, CLONE_PAS_MEME_INSTANCE);
	    assertEquals(original, clone, "Le clone doit être equals() à l'original.");
	    assertEquals(original.getIdProduit(), clone.getIdProduit(), "L'ID doit être identique.");
	    assertEquals(original.getProduit(), clone.getProduit(), "Le produit doit être identique.");
	    assertEquals(original.getSousTypeProduit(), clone.getSousTypeProduit(), "Le sousTypeProduit doit être identique.");
	    assertNotSame(original.getSousTypeProduit(), clone.getSousTypeProduit(), "Le sousTypeProduit du clone doit être un clone profond.");

	    /*
	     * ACT - WHEN : Modification du clone
	     */
	    clone.setProduit("Produit modifié");
	    ((SousTypeProduit) clone.getSousTypeProduit()).setSousTypeProduit("Sous-type modifié");

	    /*
	     * ASSERT - THEN : Vérification de l'indépendance
	     */
	    assertNotEquals(original.getProduit(), clone.getProduit(), "La modification du clone ne doit pas affecter l'original.");
	    assertNotEquals(original.getSousTypeProduit().getSousTypeProduit(), clone.getSousTypeProduit().getSousTypeProduit(), "La modification du sousTypeProduit du clone ne doit pas affecter l'original.");

	    /*
	     * AFFICHAGE A LA CONSOLE
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println("**** TEST DEEP CLONE *****");
	        System.out.println("Original : " + original);
	        System.out.println("Clone : " + clone);
	    }
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>Teste la méthode <b>deepClone(CloneContext)</b> en environnement multi-thread.</p>
	 * <ul>
	 * <li>Vérifie que le clonage profond est thread-safe.</li>
	 * <li>Utilise un ExecutorService pour simuler des clonages concurrents.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings({ "unchecked", UNUSED, RESOURCE })
	@DisplayName("testDeepCloneThreadSafe() : vérifie le thread-safety de deepClone(CloneContext)")
	@Tag(THREAD_SAFETY)
	@Test
	public final void testDeepCloneThreadSafe() throws InterruptedException, ExecutionException {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;
	
	    /*
	     * ARRANGE - GIVEN : Création d'un Produit avec un SousTypeProduit.
	     */
	    final TypeProduitI typeProduit = new TypeProduit(ANATOMIE);
	    final SousTypeProduitI sousTypeProduit = new SousTypeProduit(ANATOMIE_MAIN, typeProduit);
	    final ProduitI produit = new Produit(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit);
	
	    /*
	     * ACT - WHEN : Exécution concurrente de deepClone().
	     */
	    final ExecutorService executor = Executors.newFixedThreadPool(10);
	    final List<Callable<ProduitI>> tasks = new ArrayList<>();
	    for (int i = 0; i < 100; i++) {
	        tasks.add(() -> produit.deepClone(new CloneContext()));
	    }
	
	    final List<Future<ProduitI>> results = executor.invokeAll(tasks);
	    executor.shutdown();
	
	    /*
	     * ASSERT - THEN : Vérification des clones.
	     */
	    for (final Future<ProduitI> result : results) {
	        final ProduitI clone = result.get();
	        assertEquals(produit, clone, "Le clone doit être equals() à l'original.");
	        assertNotSame(produit, clone, CLONE_PAS_MEME_INSTANCE);
	    }
	
	    /*
	     * AFFICHAGE A LA CONSOLE.
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("***** Test deepClone() en multi-thread réussi *****");
	        System.out.println("Nombre de clones créés : " + results.size());
	    }
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>Teste la méthode <code>cloneWithoutParent()</code>.</p>
	 * <ul>
	 * <li>Vérifie que le clone est indépendant de l'original.</li>
	 * <li>Vérifie que le clone n'a pas de parent (sousTypeProduit = null).</li>
	 * <li>Vérifie que les autres propriétés sont copiées correctement.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testCloneWithoutParent() : vérifie le clonage sans parent")
	@Tag("clone")
	@Test
	public final void testCloneWithoutParent() {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;

	    /*
	     * ARRANGE - GIVEN : Création d'un produit avec parent
	     */
	    final TypeProduitI typeProduit = new TypeProduit(ANATOMIE);
	    final SousTypeProduitI sousTypeProduit = new SousTypeProduit(ANATOMIE_MAIN, typeProduit);
	    final ProduitI original = new Produit(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit);

	    /*
	     * ACT - WHEN : Clonage sans parent
	     */
	    final ProduitI clone = original.cloneWithoutParent();

	    /*
	     * ASSERT - THEN : Vérifications
	     */
	    assertNotSame(original, clone, CLONE_PAS_MEME_INSTANCE);
	    assertEquals(original.getIdProduit(), clone.getIdProduit(), "L'ID doit être identique.");
	    assertEquals(original.getProduit(), clone.getProduit(), "Le produit doit être identique.");
	    assertNull(clone.getSousTypeProduit(), "Le sousTypeProduit du clone doit être null.");
	    assertFalse(clone.isValide(), "Le clone ne doit pas être valide (sans parent).");

	    /*
	     * AFFICHAGE A LA CONSOLE
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println("**** TEST CLONE WITHOUT PARENT *****");
	        System.out.println("Original : " + original);
	        System.out.println("Clone : " + clone);
	    }
	} //___________________________________________________________________
	

	
	/**
	 * <div>
	 * <p>Teste la méthode <b>clone()</b> en environnement multi-thread.</p>
	 * <ul>
	 * <li>Vérifie que le clonage est thread-safe.</li>
	 * <li>Utilise un ExecutorService pour simuler des clonages concurrents.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings({ "unchecked", UNUSED, RESOURCE })
	@DisplayName("testCloneThreadSafe() : vérifie le thread-safety de clone()")
	@Tag(THREAD_SAFETY)
	@Test
	public final void testCloneThreadSafe() 
			throws InterruptedException, ExecutionException, CloneNotSupportedException {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;
	
	    /*
	     * ARRANGE - GIVEN : Création d'un Produit.
	     */
	    final TypeProduitI typeProduit = new TypeProduit(ANATOMIE);
	    final SousTypeProduitI sousTypeProduit = new SousTypeProduit(ANATOMIE_MAIN, typeProduit);
	    final Produit produit = new Produit(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit);
	
	    /*
	     * ACT - WHEN : Exécution concurrente de clone().
	     */
	    final ExecutorService executor = Executors.newFixedThreadPool(10);
	    final List<Callable<Produit>> tasks = new ArrayList<>();
	    for (int i = 0; i < 100; i++) {
	        tasks.add(() -> produit.clone());
	    }
	
	    final List<Future<Produit>> results = executor.invokeAll(tasks);
	    executor.shutdown();
	
	    /*
	     * ASSERT - THEN : Vérification des clones.
	     */
	    for (final Future<Produit> result : results) {
	        final ProduitI clone = result.get();
	        assertEquals(produit, clone, "Le clone doit être equals() à l'original.");
	        assertNotSame(produit, clone, CLONE_PAS_MEME_INSTANCE);
	    }
	
	    /*
	     * AFFICHAGE A LA CONSOLE.
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("***** Test clone() en multi-thread réussi *****");
	        System.out.println("Nombre de clones créés : " + results.size());
	    }
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>Teste la méthode <b>cloneWithoutParent()</b> en environnement multi-thread.</p>
	 * <ul>
	 * <li>Vérifie que le clonage sans parent est thread-safe.</li>
	 * <li>Utilise un ExecutorService pour simuler des clonages concurrents.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings({ RESOURCE, UNUSED })
	@DisplayName("testCloneWithoutParentThreadSafe() : vérifie le thread-safety de cloneWithoutParent()")
	@Tag(THREAD_SAFETY)
	@Test
	public final void testCloneWithoutParentThreadSafe() 
			throws InterruptedException, ExecutionException {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;
	
	    /*
	     * ARRANGE - GIVEN : Création d'un Produit avec un SousTypeProduit.
	     */
	    final TypeProduitI typeProduit = new TypeProduit(ANATOMIE);
	    final SousTypeProduitI sousTypeProduit = new SousTypeProduit(ANATOMIE_MAIN, typeProduit);
	    final ProduitI produit = new Produit(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit);
	
	    /*
	     * ACT - WHEN : Exécution concurrente de cloneWithoutParent().
	     */
	    final ExecutorService executor = Executors.newFixedThreadPool(10);
	    final List<Callable<ProduitI>> tasks = new ArrayList<>();
	    for (int i = 0; i < 100; i++) {
	        tasks.add(() -> produit.cloneWithoutParent());
	    }
	
	    final List<Future<ProduitI>> results = executor.invokeAll(tasks);
	    executor.shutdown();
	
	    /*
	     * ASSERT - THEN : Vérification des clones.
	     */
	    for (final Future<ProduitI> result : results) {
	        final ProduitI clone = result.get();
	        assertEquals(produit.getIdProduit(), clone.getIdProduit(), "L'ID doit être identique.");
	        assertEquals(produit.getProduit(), clone.getProduit(), "Le produit doit être identique.");
	        assertNull(clone.getSousTypeProduit(), "Le sousTypeProduit du clone doit être null.");
	        assertFalse(clone.isValide(), "Le clone ne doit pas être valide (sans parent).");
	    }
	
	    /*
	     * AFFICHAGE A LA CONSOLE.
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("***** Test cloneWithoutParent() en multi-thread réussi *****");
	        System.out.println("Nombre de clones créés : " + results.size());
	    }
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>Teste la méthode <b>recalculerValide()</b> en environnement multi-thread.</p>
	 * <ul>
	 * <li>Vérifie que l'appel concurrent à recalculerValide() ne provoque pas d'erreurs.</li>
	 * <li>Utilise un ExecutorService pour simuler des appels concurrents.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings({ RESOURCE, UNUSED })
	@DisplayName("testRecalculerValideThreadSafe() : vérifie le thread-safety de recalculerValide()")
	@Tag(THREAD_SAFETY)
	@Test
	public final void testRecalculerValideThreadSafe() throws InterruptedException, ExecutionException {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;
	
	    /*
	     * ARRANGE - GIVEN : Création d'un Produit.
	     */
	    final TypeProduitI typeProduit = new TypeProduit(ANATOMIE);
	    final SousTypeProduitI sousTypeProduit = new SousTypeProduit(ANATOMIE_MAIN, typeProduit);
	    final ProduitI produit = new Produit(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit);
	
	    /*
	     * ACT - WHEN : Exécution concurrente de recalculerValide().
	     */
	    final ExecutorService executor = Executors.newFixedThreadPool(10);
	    final List<Callable<Void>> tasks = new ArrayList<>();
	    for (int i = 0; i < 100; i++) {
	        tasks.add(() -> {
	            produit.setSousTypeProduit(null);
	            produit.setSousTypeProduit(sousTypeProduit);
	            return null;
	        });
	    }
	
	    executor.invokeAll(tasks);
	    executor.shutdown();
	
	    /*
	     * ASSERT - THEN : Vérification de la cohérence de la validité.
	     */
	    assertTrue(produit.isValide(), 
	    		"recalculerValide() doit toujours mettre à jour la validité de manière cohérente en environnement multi-thread.");
	
	    /*
	     * AFFICHAGE A LA CONSOLE.
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("***** Test recalculerValide() en multi-thread réussi *****");
	        System.out.println("Statut de validité du produit : " + produit.isValide());
	    }
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>Teste la méthode <b>normalize(String)</b> en environnement multi-thread.</p>
	 * <ul>
	 * <li>Vérifie que l'appel concurrent à normalize() ne provoque pas d'erreurs.</li>
	 * <li>Utilise un ExecutorService pour simuler des appels concurrents.</li>
	 * <li>Utilise la réflexion pour accéder à la méthode privée.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings({ RESOURCE, UNUSED })
	@DisplayName("testNormalizeThreadSafe() : vérifie le thread-safety de normalize(String)")
	@Tag(THREAD_SAFETY)
	@Test
	public final void testNormalizeThreadSafe() throws Exception {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;
	
	    /*
	     * ARRANGE - GIVEN : Récupération de la méthode normalize via réflexion
	     */
	    final java.lang.reflect.Method methodNormalize 
	    	= Produit.class.getDeclaredMethod("normalize", String.class);
	    methodNormalize.setAccessible(true); // NOPMD by danyl on 05/02/2026 02:18
	
	    /*
	     * ACT - WHEN : Exécution concurrente de normalize().
	     */
	    final ExecutorService executor = Executors.newFixedThreadPool(10);
	    final List<Callable<String>> tasks = new ArrayList<>();
	    for (int i = 0; i < 100; i++) {
	        tasks.add(() -> (String) methodNormalize.invoke(null, "  test  "));
	    }
	
	    final List<Future<String>> results = executor.invokeAll(tasks);
	    executor.shutdown();
	
	    /*
	     * ASSERT - THEN : Vérification des résultats.
	     */
	    for (final Future<String> result : results) {
	        assertEquals("test", result.get(), 
	        		"normalize() doit toujours retourner 'test' en environnement multi-thread.");
	    }
	
	    /*
	     * AFFICHAGE A LA CONSOLE.
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("***** Test normalize() en multi-thread réussi *****");
	        System.out.println("Résultat de normalize() : " + methodNormalize.invoke(null, "  test  "));
	    }
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>teste la méthode <span style= "font-weight : bold">getEnTeteCsv()</span></p>
	 * <p>Garantit que enTeteCsv() retourne 
	 * "idproduit;type de ProduitI;sous-type de ProduitI;ProduitI;"</p>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testGetEnTeteCsv() : vérifie le comportement de la méthode getEnTeteCsv()")
	@Tag("csv")
	@Test
	public final void testGetEnTeteCsv() {
				
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE ProduitTest - méthode testGetEnTeteCsv() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode getEnTeteCsv().");
			System.out.println();				
		}
		
		//**** ARRANGE - GIVEN
		final Produit objetConstructeurNull = new Produit();
		
		// ACT - WHEN
		final String enTeteCsv = objetConstructeurNull.getEnTeteCsv();
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("enTeteCsv : " + enTeteCsv);
			System.out.println();
		}
		
		final String enTeteCsvPrevue 
			= "idproduit;type de produit;sous-type de produit;produit;";
		
		// ASSERT - THEN
		assertEquals(enTeteCsvPrevue, enTeteCsv, "enTeteCsv doit retourner \"idproduit;type de produit;sous-type de produit;produit;\" : ");
		
	} //___________________________________________________________________
	

	
	/**
	 * <div>
	 * <ul>
	 * <p>teste la méthode <span style= "font-weight : bold">toStringCsv()</span></p>
	 * <li>garantit que les null sont bien gérés.</li>
	 * <li>garantit le bon fonctionnement de la méthode.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testToStringCsv() : vérifie le comportement de la méthode toStringCsv()")
	@Tag("csv")
	@Test
	public final void testToStringCsv() {
				
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE ProduitTest - méthode testToStringCsv() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode toStringCsv().");
			System.out.println();				
		}
				
		// *** ARRANGE - GIVEN
		final ProduitI objetConstructeurNull = new Produit();
		
		// ACT - WHEN
		final String toStringCsvNull = objetConstructeurNull.toStringCsv();
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("getEnTeteCsv : " + objetConstructeurNull.getEnTeteCsv());
			System.out.println("toStringCsvNull : " + toStringCsvNull);
			System.out.println();
		}

		final String toStringCsvPrevueNull = "null;null;null;null;";
				
		// ASSERT - THEN
		assertEquals(toStringCsvPrevueNull, toStringCsvNull, "toStringCsv doit retourner \"null;null;null;null;\" : ");

		
		// *** ARRANGE - GIVEN
		final TypeProduitI typeProduit1 = new TypeProduit(ANATOMIE);
		final SousTypeProduitI sousTypeProduit1 = new SousTypeProduit(ANATOMIE_MAIN, typeProduit1);
		final ProduitI objet1 = new Produit(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit1);
		
		// ACT - WHEN
		final String toStringCsv = objet1.toStringCsv();
						
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("toStringCsv : " + toStringCsv);
			System.out.println();
		}
		
		final String toStringCsvPrevue 
			= "1;Anatomie;Anatomie de la main;Anatomie arthroscopique de la main;";

		// ASSERT - THEN
		assertEquals(toStringCsvPrevue, toStringCsv, "toStringCsv doit retourner \"1;Anatomie;Anatomie de la main;Anatomie arthroscopique de la main;\" : ");

		
		// *** ARRANGE - GIVEN
		final TypeProduitI typeProduitZarbi = new TypeProduit(100L, null, null);
		final SousTypeProduitI sousTypeProduitZarbi = new SousTypeProduit(200L, null, null, null);
		final ProduitI produitZarbi = new Produit(1L, null, sousTypeProduitZarbi);
		final ProduitI produitZarbi2 = new Produit("zarbi2");
		
		// ACT - WHEN
		final String toStringCsvZarbi = produitZarbi.toStringCsv();
		final String toStringCsvZarbi2 = produitZarbi2.toStringCsv();
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
		System.out.println();
		System.out.println("toStringCsvArbi : " + toStringCsvZarbi);
		System.out.println("toStringCsvZarbi2 : " + toStringCsvZarbi2);
		System.out.println();
		}
		
		final String toStringCsvZarbiPrevue = "1;null;null;null;";
		final String toStringCsvZarbi2Prevue = "null;null;null;zarbi2;";

		// ASSERT - THEN
		assertEquals(toStringCsvZarbiPrevue, toStringCsvZarbi, "toStringCsvZarbi doit retourner \"1;null;null;null;\" : ");
		assertEquals(toStringCsvZarbi2Prevue, toStringCsvZarbi2, "toStringCsvZarbi doit retourner \"null;null;null;zarbi2;\" : ");
		
	} //___________________________________________________________________

	
		
	/**
	 * <div>
	 * <p>Teste la méthode <b>toStringCsv()</b> en environnement multi-thread.</p>
	 * <ul>
	 * <li>Vérifie que l'appel concurrent à toStringCsv() ne provoque pas d'erreurs.</li>
	 * <li>Utilise un ExecutorService pour simuler des accès concurrents.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings({ RESOURCE, UNUSED })
	@DisplayName("testToStringCsvThreadSafe() : vérifie le thread-safety de toStringCsv()")
	@Tag(THREAD_SAFETY)
	@Test
	public final void testToStringCsvThreadSafe() throws InterruptedException, ExecutionException {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;
	
	    /*
	     * ARRANGE - GIVEN : Création d'un Produit.
	     */
	    final TypeProduitI typeProduit = new TypeProduit(ANATOMIE);
	    final SousTypeProduitI sousTypeProduit = new SousTypeProduit(ANATOMIE_MAIN, typeProduit);
	    final ProduitI produit = new Produit(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit);
	
	    /*
	     * ACT - WHEN : Exécution concurrente de toStringCsv().
	     */
	    final ExecutorService executor = Executors.newFixedThreadPool(10);
	    final List<Callable<String>> tasks = new ArrayList<>();
	    for (int i = 0; i < 100; i++) {
	        tasks.add(() -> produit.toStringCsv());
	    }
	
	    final List<Future<String>> results = executor.invokeAll(tasks);
	    executor.shutdown();
	
	    /*
	     * ASSERT - THEN : Vérification des résultats.
	     */
	    for (final Future<String> result : results) {
	        assertNotNull(result.get(), "toStringCsv() ne doit jamais retourner null en environnement multi-thread.");
	    }
	
	    /*
	     * AFFICHAGE A LA CONSOLE.
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("***** Test toStringCsv() en multi-thread réussi *****");
	        System.out.println("Résultat de toStringCsv() : " + produit.toStringCsv());
	    }
	} //___________________________________________________________________



	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <span style= "font-weight : bold">getEnTeteColonne(int pI)</span></p>
	 * <li>garantit que les null sont gérés dans 
	 * getEnTeteColonne(int pI).</li> 
	 * <li>garantit que getEnTeteColonne(int pI) retourne 
	 * la bonne en-tête de colonne.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testGetEnTeteColonne() : vérifie le comportement de la méthode getEnTeteColonne()")
	@Tag("JTable")
	@Test
	public final void testGetEnTeteColonne() {

		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE ProduitTest - méthode testGetEnTeteColonne() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode getEnTeteColonne().");
			System.out.println();				
		}
				
		// *** ARRANGE - GIVEN
		final ProduitI objetConstructeurNull = new Produit();
		
		// ACT - WHEN
		final String enTete0 = objetConstructeurNull.getEnTeteColonne(0);
		final String enTete1 = objetConstructeurNull.getEnTeteColonne(1);
		final String enTete2 = objetConstructeurNull.getEnTeteColonne(2);
		final String enTete3 = objetConstructeurNull.getEnTeteColonne(3);
		final String enTete7 = objetConstructeurNull.getEnTeteColonne(7);
						
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
		System.out.println();
		System.out.println("enTete0 : " + enTete0);
		System.out.println("enTete1 : " + enTete1);
		System.out.println("enTete2 : " + enTete2);
		System.out.println("enTete3 : " + enTete3);
		System.out.println("enTete7 : " + enTete7);
		System.out.println();
		}

		// ASSERT - THEN
		assertEquals("idproduit", enTete0, "enTete0 doit retourner \"idproduit\" :  ");
		assertEquals("type de produit", enTete1, "enTete1 doit retourner \"type de produit\" :  ");
		assertEquals("sous-type de produit", enTete2, "enTete2 doit retourner \"sous-type de produit\" :  ");
		assertEquals("produit", enTete3, "enTete3 doit retourner \"produit\" :  ");
		assertEquals("invalide", enTete7, "enTete7 doit retourner \"invalide\" :  ");

	} //___________________________________________________________________
	

	
	/**
	 * <div>
	 * <p>Teste la méthode <b>getEnTeteColonne(int)</b> en environnement multi-thread.</p>
	 * <ul>
	 * <li>Vérifie que l'appel concurrent à getEnTeteColonne(int) ne provoque pas d'erreurs.</li>
	 * <li>Utilise un ExecutorService pour simuler des accès concurrents.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings({ RESOURCE, UNUSED })
	@DisplayName("testGetEnTeteColonneThreadSafe() : vérifie le thread-safety de getEnTeteColonne(int)")
	@Tag(THREAD_SAFETY)
	@Test
	public final void testGetEnTeteColonneThreadSafe() 
			throws InterruptedException, ExecutionException {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;
	
	    /*
	     * ARRANGE - GIVEN : Création d'un Produit.
	     */
	    final ProduitI produit = new Produit(1L, ANATOMIE_ARTHRO_MAIN, null);
	
	    /*
	     * ACT - WHEN : Exécution concurrente de getEnTeteColonne(0).
	     */
		final ExecutorService executor = Executors.newFixedThreadPool(10);
	    final List<Callable<String>> tasks = new ArrayList<>();
	    for (int i = 0; i < 100; i++) {
	        tasks.add(() -> produit.getEnTeteColonne(0));
	    }
	
	    final List<Future<String>> results = executor.invokeAll(tasks);
	    executor.shutdown();
	
	    /*
	     * ASSERT - THEN : Vérification des résultats.
	     */
	    for (final Future<String> result : results) {
	        assertEquals("idproduit", result.get(), 
	        		"getEnTeteColonne(0) doit toujours retourner 'idproduit' en environnement multi-thread.");
	    }
	
	    /*
	     * AFFICHAGE A LA CONSOLE.
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("***** Test getEnTeteColonne() en multi-thread réussi *****");
	        System.out.println("En-tête de colonne 0 : " + produit.getEnTeteColonne(0));
	    }
	} //___________________________________________________________________



	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <span style= "font-weight : bold">getValeurColonne(int pI)</span></p>
	 * <li>garantit que les null sont gérés dans 
	 * getValeurColonne(int pI). Une valeur null doit retourner null.</li> 
	 * <li>garantit que getValeurColonne(int pI) retourne 
	 * la bonne valeur de colonne.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testGetValeurColonne() : vérifie le comportement de la méthode getValeurColonne()")
	@Tag("JTable")
	@Test
	public final void testGetValeurColonne() {

		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE ProduitTest - méthode testGetValeurColonne() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode getValeurColonne().");
			System.out.println();				
		}
				
		// *** ARRANGE - GIVEN
		final ProduitI objetConstructeurNull = new Produit();
		
		// ACT - WHEN
		final String valeur0Null = (String) objetConstructeurNull.getValeurColonne(0);
		final String valeur1Null = (String) objetConstructeurNull.getValeurColonne(1);
		final String valeur2Null = (String) objetConstructeurNull.getValeurColonne(2);
		final String valeur3Null = (String) objetConstructeurNull.getValeurColonne(3);
		final String valeur7Null = (String) objetConstructeurNull.getValeurColonne(7);
						
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
		System.out.println();
		System.out.println("valeur0Null : " + valeur0Null);
		System.out.println("valeur1Null : " + valeur1Null);
		System.out.println("valeur2Null : " + valeur2Null);
		System.out.println("valeur3Null : " + valeur3Null);
		System.out.println("valeur7Null : " + valeur7Null);
		System.out.println();
		}

		// ASSERT - THEN
		assertNull(valeur0Null, "valeur0Null doit retourner \"null\" :  ");
		assertNull(valeur1Null, "valeur1Null doit retourner \"null\" :  ");
		assertNull(valeur2Null, "valeur2Null doit retourner \"null\" :  ");
		assertNull(valeur3Null, "valeur3Null doit retourner \"null\" :  ");
		assertEquals("invalide", valeur7Null, "valeur7Null doit retourner \"invalide\" :  ");

		
		// *** ARRANGE - GIVEN
		final TypeProduitI typeProduit1 = new TypeProduit(ANATOMIE);
		final SousTypeProduitI sousTypeProduit1 = new SousTypeProduit(ANATOMIE_MAIN, typeProduit1);
		final ProduitI objet1 = new Produit(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit1);
		
		// ACT - WHEN
		final String valeur0 = (String) objet1.getValeurColonne(0);
		final String valeur1 = (String) objet1.getValeurColonne(1);
		final String valeur2 = (String) objet1.getValeurColonne(2);
		final String valeur3 = (String) objet1.getValeurColonne(3);
		final String valeur7 = (String) objet1.getValeurColonne(7);
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
		System.out.println();
		System.out.println("valeur0 : " + valeur0);
		System.out.println("valeur1 : " + valeur1);
		System.out.println("valeur2 : " + valeur2);
		System.out.println("valeur3 : " + valeur3);
		System.out.println("valeur7 : " + valeur7);
		System.out.println();
		}

		// ASSERT - THEN
		assertEquals("1", valeur0, "valeur0 doit retourner \"1\" :  ");
		assertEquals("Anatomie", valeur1, "valeur1 doit retourner \"Anatomie\" :  ");
		assertEquals("Anatomie de la main", valeur2, "valeur2 doit retourner \"Anatomie de la main\" :  ");
		assertEquals("Anatomie arthroscopique de la main", valeur3, "valeur3 doit retourner \"Anatomie arthroscopique de la main\" :  ");
		assertEquals("invalide", valeur7Null, "valeur7Null doit retourner \"invalide\" :  ");

	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>Teste la méthode <b>getValeurColonne(int)</b> en environnement multi-thread.</p>
	 * <ul>
	 * <li>Vérifie que l'appel concurrent à getValeurColonne(int) ne provoque pas d'erreurs.</li>
	 * <li>Utilise un ExecutorService pour simuler des accès concurrents.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings({ RESOURCE, UNUSED })
	@DisplayName("testGetValeurColonneThreadSafe() : vérifie le thread-safety de getValeurColonne(int)")
	@Tag(THREAD_SAFETY)
	@Test
	public final void testGetValeurColonneThreadSafe() 
			throws InterruptedException, ExecutionException {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;
	
	    /*
	     * ARRANGE - GIVEN : Création d'un Produit.
	     */
	    final TypeProduitI typeProduit = new TypeProduit(ANATOMIE);
	    final SousTypeProduitI sousTypeProduit = new SousTypeProduit(ANATOMIE_MAIN, typeProduit);
	    final ProduitI produit = new Produit(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit);
	
	    /*
	     * ACT - WHEN : Exécution concurrente de getValeurColonne(0).
	     */
	    final ExecutorService executor = Executors.newFixedThreadPool(10);
	    final List<Callable<Object>> tasks = new ArrayList<>();
	    for (int i = 0; i < 100; i++) {
	        tasks.add(() -> produit.getValeurColonne(0));
	    }
	
	    final List<Future<Object>> results = executor.invokeAll(tasks);
	    executor.shutdown();
	
	    /*
	     * ASSERT - THEN : Vérification des résultats.
	     */
	    for (final Future<Object> result : results) {
	        assertEquals("1", result.get(), 
	        		"getValeurColonne(0) doit toujours retourner '1' en environnement multi-thread.");
	    }
	
	    /*
	     * AFFICHAGE A LA CONSOLE.
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("***** Test getValeurColonne() en multi-thread réussi *****");
	        System.out.println("Valeur de colonne 0 : " + produit.getValeurColonne(0));
	    }
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>Teste la méthode <code>isValide()</code>.</p>
	 * <ul>
	 * <li>Vérifie que isValide() retourne false si sousTypeProduit est null.</li>
	 * <li>Vérifie que isValide() retourne true si sousTypeProduit est non null.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testIsValide() : vérifie le comportement du getter isValide()")
	@Tag("getters")
	@Test
	public final void testIsValide() {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;
	
	    /*
	     * ARRANGE - GIVEN : Création des objets nécessaires
	     */
	    final TypeProduitI typeProduit = new TypeProduit(ANATOMIE);
	    final SousTypeProduitI sousTypeProduit = new SousTypeProduit(ANATOMIE_MAIN, typeProduit);
	    final ProduitI produitAvecParent = new Produit(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit);
	    final ProduitI produitSansParent = new Produit(1L, ANATOMIE_ARTHRO_MAIN, null);
	
	    /*
	     * ACT - WHEN : Test avec sousTypeProduit non null
	     */
	    assertTrue(produitAvecParent.isValide(), "Le produit doit être valide avec un sousTypeProduit non null.");
	
	    /*
	     * ACT - WHEN : Test avec sousTypeProduit null
	     */
	    assertFalse(produitSansParent.isValide(), "Le produit ne doit pas être valide sans sousTypeProduit.");
	
	    /*
	     * AFFICHAGE A LA CONSOLE
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println("**** TEST IS VALIDE *****");
	        System.out.println("Valide avec parent : " + produitAvecParent.isValide());
	        System.out.println("Valide sans parent : " + produitSansParent.isValide());
	    }
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>Teste la méthode <b>isValide()</b> en environnement multi-thread.</p>
	 * <ul>
	 * <li>Vérifie que l'appel concurrent à isValide() ne provoque pas d'erreurs.</li>
	 * <li>Utilise un ExecutorService pour simuler des accès concurrents.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings({ RESOURCE, UNUSED })
	@DisplayName("testIsValideThreadSafe() : vérifie le thread-safety de isValide()")
	@Tag(THREAD_SAFETY)
	@Test
	public final void testIsValideThreadSafe() 
			throws InterruptedException, ExecutionException {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;
	
	    /*
	     * ARRANGE - GIVEN : Création d'un Produit valide.
	     */
	    final TypeProduitI typeProduit = new TypeProduit(ANATOMIE);
	    final SousTypeProduitI sousTypeProduit = new SousTypeProduit(ANATOMIE_MAIN, typeProduit);
	    final ProduitI produit = new Produit(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit);
	
	    /*
	     * ACT - WHEN : Exécution concurrente de isValide().
	     */
	    final ExecutorService executor = Executors.newFixedThreadPool(10);
	    final List<Callable<Boolean>> tasks = new ArrayList<>();
	    for (int i = 0; i < 100; i++) {
	        tasks.add(() -> produit.isValide());
	    }
	
	    final List<Future<Boolean>> results = executor.invokeAll(tasks);
	    executor.shutdown();
	
	    /*
	     * ASSERT - THEN : Vérification des résultats.
	     */
	    for (final Future<Boolean> result : results) {
	        assertTrue(result.get(), 
	        		"isValide() doit toujours retourner true en environnement multi-thread.");
	    }
	
	    /*
	     * AFFICHAGE A LA CONSOLE.
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("***** Test isValide() en multi-thread réussi *****");
	        System.out.println("Statut de validité du produit : " + produit.isValide());
	    }
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>Teste la méthode <b>getIdProduit()</b> en environnement multi-thread.</p>
	 * <ul>
	 * <li>Vérifie que l'appel concurrent à getIdProduit() ne provoque pas d'erreurs.</li>
	 * <li>Utilise un ExecutorService pour simuler des accès concurrents.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings({ RESOURCE, UNUSED })
	@DisplayName("testGetIdProduitThreadSafe() : vérifie le thread-safety de getIdProduit()")
	@Tag(THREAD_SAFETY)
	@Test
	public final void testGetIdProduitThreadSafe() 
			throws InterruptedException, ExecutionException {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;
	
	    /*
	     * ARRANGE - GIVEN : Création d'un Produit avec un ID.
	     */
	    final ProduitI produit = new Produit(1L, ANATOMIE_ARTHRO_MAIN, null);
	
	    /*
	     * ACT - WHEN : Exécution concurrente de getIdProduit().
	     */
	    final ExecutorService executor = Executors.newFixedThreadPool(10);
	    final List<Callable<Long>> tasks = new ArrayList<>();
	    for (int i = 0; i < 100; i++) {
	        tasks.add(() -> produit.getIdProduit());
	    }
	
	    final List<Future<Long>> results = executor.invokeAll(tasks);
	    executor.shutdown();
	
	    /*
	     * ASSERT - THEN : Vérification des résultats.
	     */
	    for (final Future<Long> result : results) {
	        assertEquals(1L, result.get(), 
	        		"getIdProduit() doit toujours retourner 1 en environnement multi-thread.");
	    }
	
	    /*
	     * AFFICHAGE A LA CONSOLE.
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("***** Test getIdProduit() en multi-thread réussi *****");
	        System.out.println("ID du produit : " + produit.getIdProduit());
	    }
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>Teste la méthode <b>setIdProduit(Long)</b> en environnement multi-thread.</p>
	 * <ul>
	 * <li>Vérifie que l'appel concurrent à setIdProduit() ne corrompt pas l'ID.</li>
	 * <li>Utilise un ExecutorService pour simuler des modifications concurrentes.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings({ RESOURCE, UNUSED })
	@DisplayName("testSetIdProduitThreadSafe() : vérifie le thread-safety de setIdProduit(Long)")
	@Tag(THREAD_SAFETY)
	@Test
	public final void testSetIdProduitThreadSafe() throws InterruptedException, ExecutionException {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;
	
	    /*
	     * ARRANGE - GIVEN : Création d'un Produit.
	     */
	    final ProduitI produit = new Produit(1L, ANATOMIE_ARTHRO_MAIN, null);
	
	    /*
	     * ACT - WHEN : Exécution concurrente de setIdProduit().
	     */
	    final ExecutorService executor = Executors.newFixedThreadPool(10);
	    final List<Callable<Void>> tasks = new ArrayList<>();
	    for (int i = 0; i < 100; i++) {
	        tasks.add(() -> {
	            produit.setIdProduit(2L);
	            return null;
	        });
	    }
	
	    executor.invokeAll(tasks);
	    executor.shutdown();
	
	    /*
	     * ASSERT - THEN : Vérification de la cohérence de l'ID.
	     */
	    assertEquals(2L, produit.getIdProduit(), 
	    		"setIdProduit() doit toujours mettre à jour l'ID de manière cohérente en environnement multi-thread.");
	
	    /*
	     * AFFICHAGE A LA CONSOLE.
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("***** Test setIdProduit() en multi-thread réussi *****");
	        System.out.println("ID du produit après modifications concurrentes : " + produit.getIdProduit());
	    }
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>Teste la méthode <b>getProduit()</b> en environnement multi-thread.</p>
	 * <ul>
	 * <li>Vérifie que l'appel concurrent à getProduit() ne provoque pas d'erreurs.</li>
	 * <li>Utilise un ExecutorService pour simuler des accès concurrents.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings({ RESOURCE, UNUSED })
	@DisplayName("testGetProduitThreadSafe() : vérifie le thread-safety de getProduit()")
	@Tag(THREAD_SAFETY)
	@Test
	public final void testGetProduitThreadSafe() 
			throws InterruptedException, ExecutionException {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;
	
	    /*
	     * ARRANGE - GIVEN : Création d'un Produit avec un nom.
	     */
	    final ProduitI produit = new Produit(1L, ANATOMIE_ARTHRO_MAIN, null);
	
	    /*
	     * ACT - WHEN : Exécution concurrente de getProduit().
	     */
	    final ExecutorService executor = Executors.newFixedThreadPool(10);
	    final List<Callable<String>> tasks = new ArrayList<>();
	    for (int i = 0; i < 100; i++) {
	        tasks.add(() -> produit.getProduit());
	    }
	
	    final List<Future<String>> results = executor.invokeAll(tasks);
	    executor.shutdown();
	
	    /*
	     * ASSERT - THEN : Vérification des résultats.
	     */
	    for (final Future<String> result : results) {
	        assertEquals(ANATOMIE_ARTHRO_MAIN, result.get()
	        		, "getProduit() doit toujours retourner le même nom en environnement multi-thread.");
	    }
	
	    /*
	     * AFFICHAGE A LA CONSOLE.
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("***** Test getProduit() en multi-thread réussi *****");
	        System.out.println("Nom du produit : " + produit.getProduit());
	    }
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>Teste la méthode <code>setProduit(String)</code>.</p>
	 * <ul>
	 * <li>Vérifie la gestion des valeurs null.</li>
	 * <li>Vérifie la gestion des chaînes vides.</li>
	 * <li>Vérifie la gestion des chaînes à trimmer.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testSetProduit() : vérifie le comportement du setter setProduit(String)")
	@Tag("setters")
	@Test
	public final void testSetProduit() {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;

	    /*
	     * ARRANGE - GIVEN : Création d'un produit
	     */
	    final ProduitI produit = new Produit();

	    /*
	     * ACT - WHEN : Test avec null
	     */
	    produit.setProduit(null);
	    assertNull(produit.getProduit(), "setProduit(null) doit conserver null.");

	    /*
	     * ACT - WHEN : Test avec chaîne vide
	     */
	    produit.setProduit("   ");
	    assertEquals("   ", produit.getProduit(), "setProduit(\"   \") doit conserver les espaces (normalisation gérée ailleurs).");

	    /*
	     * ACT - WHEN : Test avec chaîne à trimmer
	     */
	    produit.setProduit("  Produit  ");
	    assertEquals("  Produit  ", produit.getProduit(), "setProduit(\"  Produit  \") doit conserver les espaces (normalisation gérée ailleurs).");

	    /*
	     * ACT - WHEN : Test avec chaîne normale
	     */
	    produit.setProduit("Produit normal");
	    assertEquals("Produit normal", produit.getProduit(), "setProduit(\"Produit normal\") doit conserver la chaîne.");

	    /*
	     * AFFICHAGE A LA CONSOLE
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println("**** TEST SET PRODUIT *****");
	        System.out.println("Produit après setProduit(null) : " + produit.getProduit());
	        System.out.println("Produit après setProduit(\"   \") : " + produit.getProduit());
	        System.out.println("Produit après setProduit(\"  Produit  \") : " + produit.getProduit());
	        System.out.println("Produit après setProduit(\"Produit normal\") : " + produit.getProduit());
	    }
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>Teste la méthode <b>setProduit(String)</b> en environnement multi-thread.</p>
	 * <ul>
	 * <li>Vérifie que l'appel concurrent à setProduit() ne corrompt pas le nom du produit.</li>
	 * <li>Utilise un ExecutorService pour simuler des modifications concurrentes.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings({ RESOURCE, UNUSED })
	@DisplayName("testSetProduitThreadSafe() : vérifie le thread-safety de setProduit(String)")
	@Tag(THREAD_SAFETY)
	@Test
	public final void testSetProduitThreadSafe() 
			throws InterruptedException, ExecutionException {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;
	
	    /*
	     * ARRANGE - GIVEN : Création d'un Produit.
	     */
	    final ProduitI produit = new Produit(1L, ANATOMIE_ARTHRO_MAIN, null);
	
	    /*
	     * ACT - WHEN : Exécution concurrente de setProduit().
	     */
	    final ExecutorService executor = Executors.newFixedThreadPool(10);
	    final List<Callable<Void>> tasks = new ArrayList<>();
	    for (int i = 0; i < 100; i++) {
	        tasks.add(() -> {
	            produit.setProduit(PRODUIT_MODIFIE);
	            return null;
	        });
	    }
	
	    executor.invokeAll(tasks);
	    executor.shutdown();
	
	    /*
	     * ASSERT - THEN : Vérification de la cohérence du nom.
	     */
	    assertEquals(PRODUIT_MODIFIE, produit.getProduit(),
	    		"setProduit() doit toujours mettre à jour le nom de manière cohérente en environnement multi-thread.");
	
	    /*
	     * AFFICHAGE A LA CONSOLE.
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("***** Test setProduit() en multi-thread réussi *****");
	        System.out.println("Nom du produit après modifications concurrentes : " + produit.getProduit());
	    }
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>Teste la méthode <code>setSousTypeProduit(SousTypeProduitI)</code>.</p>
	 * <ul>
	 * <li>Vérifie la gestion des mauvaises instances.</li>
	 * <li>Vérifie la gestion des null.</li>
	 * <li>Vérifie la cohérence des relations bidirectionnelles.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testSetSousTypeProduit() : vérifie le comportement du setter setSousTypeProduit(SousTypeProduitI)")
	@Tag("setters")
	@Test
	public final void testSetSousTypeProduit() {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;
	
	    /*
	     * ARRANGE - GIVEN : Création des objets nécessaires
	     */
	    final TypeProduitI typeProduit = new TypeProduit(ANATOMIE);
	    final SousTypeProduitI sousTypeProduit1 = new SousTypeProduit(ANATOMIE_MAIN, typeProduit);
	    final SousTypeProduitI sousTypeProduit2 = new SousTypeProduit("Nouveau sous-type", typeProduit);
	    final ProduitI produit = new Produit(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit1);
	
	    /*
	     * ACT - WHEN : Test avec null
	     */
	    produit.setSousTypeProduit(null);
	    assertNull(produit.getSousTypeProduit(), "setSousTypeProduit(null) doit mettre sousTypeProduit à null.");
	    assertFalse(produit.isValide(), "Le produit ne doit pas être valide sans sousTypeProduit.");
	
	    /*
	     * ACT - WHEN : Test avec un nouveau sousTypeProduit
	     */
	    produit.setSousTypeProduit(sousTypeProduit2);
	    assertEquals(sousTypeProduit2, produit.getSousTypeProduit(), "Le sousTypeProduit doit être mis à jour.");
	    assertTrue(produit.isValide(), "Le produit doit être valide avec un sousTypeProduit non null.");
	    assertTrue(sousTypeProduit2.getProduits().contains(produit), "Le produit doit être dans la liste des produits du nouveau sousTypeProduit.");
	    assertFalse(sousTypeProduit1.getProduits().contains(produit), "Le produit ne doit plus être dans la liste des produits de l'ancien sousTypeProduit.");
	
	    /*
	     * ACT - WHEN : Test avec la même instance
	     */
	    produit.setSousTypeProduit(sousTypeProduit2);
	    assertEquals(sousTypeProduit2, produit.getSousTypeProduit(), "Le sousTypeProduit ne doit pas changer.");
	    assertTrue(produit.isValide(), "Le produit doit rester valide.");
	
	    /*
	     * AFFICHAGE A LA CONSOLE
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println("**** TEST SET SOUS TYPE PRODUIT *****");
	        System.out.println("Produit après setSousTypeProduit(null) : " + produit.getSousTypeProduit());
	        System.out.println("Produit après setSousTypeProduit(sousTypeProduit2) : " + produit.getSousTypeProduit());
	        System.out.println("Produits de sousTypeProduit1 : " + sousTypeProduit1.getProduits());
	        System.out.println("Produits de sousTypeProduit2 : " + sousTypeProduit2.getProduits());
	    }
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>Teste la méthode <b>getSousTypeProduit()</b> en environnement multi-thread.</p>
	 * <ul>
	 * <li>Vérifie que l'appel concurrent à getSousTypeProduit() ne provoque pas d'erreurs.</li>
	 * <li>Utilise un ExecutorService pour simuler des accès concurrents.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings({ RESOURCE, UNUSED })
	@DisplayName("testGetSousTypeProduitThreadSafe() : vérifie le thread-safety de getSousTypeProduit()")
	@Tag(THREAD_SAFETY)
	@Test
	public final void testGetSousTypeProduitThreadSafe() 
			throws InterruptedException, ExecutionException {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;
	
	    /*
	     * ARRANGE - GIVEN : Création d'un Produit avec un SousTypeProduit.
	     */
	    final TypeProduitI typeProduit = new TypeProduit(ANATOMIE);
	    final SousTypeProduitI sousTypeProduit = new SousTypeProduit(ANATOMIE_MAIN, typeProduit);
	    final ProduitI produit = new Produit(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit);
	
	    /*
	     * ACT - WHEN : Exécution concurrente de getSousTypeProduit().
	     */
	    final ExecutorService executor = Executors.newFixedThreadPool(10);
	    final List<Callable<SousTypeProduitI>> tasks = new ArrayList<>();
	    for (int i = 0; i < 100; i++) {
	        tasks.add(() -> produit.getSousTypeProduit());
	    }
	
	    final List<Future<SousTypeProduitI>> results = executor.invokeAll(tasks);
	    executor.shutdown();
	
	    /*
	     * ASSERT - THEN : Vérification des résultats.
	     */
	    for (final Future<SousTypeProduitI> result : results) {
	        assertEquals(sousTypeProduit, result.get(),
	        		"getSousTypeProduit() doit toujours retourner le même SousTypeProduit en environnement multi-thread.");
	    }
	
	    /*
	     * AFFICHAGE A LA CONSOLE.
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("***** Test getSousTypeProduit() en multi-thread réussi *****");
	        System.out.println("SousTypeProduit du produit : " + produit.getSousTypeProduit());
	    }
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>Teste la méthode <b>setSousTypeProduit(SousTypeProduitI)</b> en environnement multi-thread.</p>
	 * <ul>
	 * <li>Vérifie que l'appel concurrent à setSousTypeProduit() ne corrompt pas les relations bidirectionnelles.</li>
	 * <li>Utilise un ExecutorService pour simuler des modifications concurrentes.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings({ RESOURCE, UNUSED })
	@DisplayName("testSetSousTypeProduitThreadSafe() : vérifie le thread-safety de setSousTypeProduit(SousTypeProduitI)")
	@Tag(THREAD_SAFETY)
	@Test
	public final void testSetSousTypeProduitThreadSafe() 
			throws InterruptedException, ExecutionException {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;

	    /*
	     * ARRANGE - GIVEN : Création des objets nécessaires.
	     */
	    final TypeProduitI typeProduit = new TypeProduit(ANATOMIE);
	    final SousTypeProduitI sousTypeProduit1 = new SousTypeProduit(ANATOMIE_MAIN, typeProduit);
	    final SousTypeProduitI sousTypeProduit2 = new SousTypeProduit(SOUS_TYPE_MODIFIE, typeProduit);
	    final ProduitI produit = new Produit(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit1);

	    /*
	     * ACT - WHEN : Exécution concurrente de setSousTypeProduit().
	     */
	    final ExecutorService executor = Executors.newFixedThreadPool(10);
	    final List<Callable<Void>> tasks = new ArrayList<>();
	    for (int i = 0; i < 100; i++) {
	        tasks.add(() -> {
	            produit.setSousTypeProduit(sousTypeProduit2);
	            return null;
	        });
	    }

	    executor.invokeAll(tasks);
	    executor.shutdown();

	    /*
	     * ASSERT - THEN : Vérification de la cohérence des relations.
	     */
	    assertEquals(sousTypeProduit2, produit.getSousTypeProduit(), 
	    		"setSousTypeProduit() doit toujours mettre à jour le SousTypeProduit de manière cohérente en environnement multi-thread.");
	    assertTrue(sousTypeProduit2.getProduits().contains(produit), 
	    		"Le produit doit être dans la liste des produits du nouveau SousTypeProduit.");
	    assertFalse(sousTypeProduit1.getProduits().contains(produit), 
	    		"Le produit ne doit plus être dans la liste des produits de l'ancien SousTypeProduit.");

	    /*
	     * AFFICHAGE A LA CONSOLE.
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("***** Test setSousTypeProduit() en multi-thread réussi *****");
	        System.out.println("SousTypeProduit du produit après modifications concurrentes : " + produit.getSousTypeProduit());
	    }
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>Teste la méthode <code>getTypeProduit()</code>.</p>
	 * <ul>
	 * <li>Vérifie le retour null si sousTypeProduit est null.</li>
	 * <li>Vérifie le retour du typeProduit via sousTypeProduit.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testGetTypeProduit() : vérifie le comportement du getter getTypeProduit()")
	@Tag("getters")
	@Test
	public final void testGetTypeProduit() {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;
	
	    /*
	     * ARRANGE - GIVEN : Création des objets nécessaires
	     */
	    final TypeProduitI typeProduit = new TypeProduit(ANATOMIE);
	    final SousTypeProduitI sousTypeProduit = new SousTypeProduit(ANATOMIE_MAIN, typeProduit);
	    final ProduitI produitAvecParent = new Produit(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit);
	    final ProduitI produitSansParent = new Produit(1L, ANATOMIE_ARTHRO_MAIN, null);
	
	    /*
	     * ACT - WHEN : Test avec sousTypeProduit non null
	     */
	    assertEquals(typeProduit, produitAvecParent.getTypeProduit(), "Le typeProduit doit être retourné via sousTypeProduit.");
	
	    /*
	     * ACT - WHEN : Test avec sousTypeProduit null
	     */
	    assertNull(produitSansParent.getTypeProduit(), "Le typeProduit doit être null si sousTypeProduit est null.");
	
	    /*
	     * AFFICHAGE A LA CONSOLE
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println("**** TEST GET TYPE PRODUIT *****");
	        System.out.println("TypeProduit avec parent : " + produitAvecParent.getTypeProduit());
	        System.out.println("TypeProduit sans parent : " + produitSansParent.getTypeProduit());
	    }
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>Teste la méthode <b>getTypeProduit()</b> en environnement multi-thread.</p>
	 * <ul>
	 * <li>Vérifie que l'appel concurrent à getTypeProduit() ne provoque pas d'erreurs.</li>
	 * <li>Utilise un ExecutorService pour simuler des accès concurrents.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings({ RESOURCE, UNUSED })
	@DisplayName("testGetTypeProduitThreadSafe() : vérifie le thread-safety de getTypeProduit()")
	@Tag(THREAD_SAFETY)
	@Test
	public final void testGetTypeProduitThreadSafe() throws InterruptedException, ExecutionException {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;

	    /*
	     * ARRANGE - GIVEN : Création d'un Produit avec un TypeProduit.
	     */
	    final TypeProduitI typeProduit = new TypeProduit(ANATOMIE);
	    final SousTypeProduitI sousTypeProduit = new SousTypeProduit(ANATOMIE_MAIN, typeProduit);
	    final ProduitI produit = new Produit(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit);

	    /*
	     * ACT - WHEN : Exécution concurrente de getTypeProduit().
	     */
	    final ExecutorService executor = Executors.newFixedThreadPool(10);
	    final List<Callable<TypeProduitI>> tasks = new ArrayList<>();
	    for (int i = 0; i < 100; i++) {
	        tasks.add(() -> produit.getTypeProduit());
	    }

	    final List<Future<TypeProduitI>> results = executor.invokeAll(tasks);
	    executor.shutdown();

	    /*
	     * ASSERT - THEN : Vérification des résultats.
	     */
	    for (final Future<TypeProduitI> result : results) {
	        assertEquals(typeProduit, result.get(), 
	        		"getTypeProduit() doit toujours retourner le même TypeProduit en environnement multi-thread.");
	    }

	    /*
	     * AFFICHAGE A LA CONSOLE.
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("***** Test getTypeProduit() en multi-thread réussi *****");
	        System.out.println("TypeProduit du produit : " + produit.getTypeProduit());
	    }
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>affiche à la console un ProduitI.</p>
	 * </div>
	 *
	 * @param pProduit : ProduitI
	 */
	private void afficher(final ProduitI pProduit) {
		
		if (pProduit == null) {
			return;
		}
		
		System.out.println("id du ProduitI : " + pProduit.getIdProduit());
		System.out.println("ProduitI : " + pProduit.getProduit());
		if (pProduit.getSousTypeProduit() != null) {
			System.out.println("sous-type de ProduitI : " + pProduit.getSousTypeProduit().getSousTypeProduit());
		} else {
			System.out.println("sous-type de ProduitI : null");
		}
		if (pProduit.getTypeProduit() != null) {
			System.out.println("type de ProduitI : " + pProduit.getTypeProduit().getTypeProduit());
		} else {
			System.out.println("type de ProduitI : null");
		}
		System.out.println("valide : " + pProduit.isValide());
		
	} //___________________________________________________________________
	

	
	/**
	 * <div>
	 * <p>Affiche une liste de ProduitI en déclinant 
	 * SousTypeProduit et ProduitI</p>
	 * </div>
	 *
	 * @param pListeProduits
	 */
	private void afficherListeProduits(final List<? extends ProduitI> pListeProduits) {
		
		if (pListeProduits == null) {
			return;
		}
		
		final StringBuilder stb = new StringBuilder();
		
		for (final ProduitI produitInterne : pListeProduits) {
			
			String sousTypeProduitString = null;
			String produitString = null;
			
			if (produitInterne == null) {
				
				stb.append("ProduitI NULL");
				stb.append(SAUT_DE_LIGNE);
				
			} else {
				
				final SousTypeProduit sousTypeProduit 
					= (SousTypeProduit) produitInterne.getSousTypeProduit();
				
				if (sousTypeProduit == null) {
					sousTypeProduitString = NULL;
				} else {
					sousTypeProduitString 
						= sousTypeProduit.getSousTypeProduit();					
				}
				
				produitString = produitInterne.getProduit();
				
				stb.append(String.format("%-30s %-30s", sousTypeProduitString, produitString));
				stb.append(SAUT_DE_LIGNE);
				
			}
						
		}
		
		System.out.println(stb.toString());
		
	}
	
	
	
	/**
	 * <div>
	 * <p>Instancie : </p>
	 * <ul>
	 * <li><code>this.typeProduitVetement</code> avec "vêtement"</li>
	 * <li><code>this.soustypeProduitVetementPourHomme</code> avec "vêtement pour homme"</li>
	 * <li><code>this.soustypeProduitVetementPourFemme</code> avec "vêtement pour femme"</li>
	 * <li><code>this.soustypeProduitVetementPourEnfant</code> avec "vêtement pour enfant"</li>
	 * <li><code>this.soustypeProduitVetementPourHommeSansTypeProduit</code> avec "vêtement pour homme" avec un TypeProduit NULL</li>
	 * <li><code>this.produitChemiseManchesLonguesHomme</code> avec "vêtement pour homme"</li>
	 * <li><code>this.produitChemiseManchesCourtesHomme</code> avec "vêtement pour homme"</li>
	 * <li><code>this.produitSweatshirtHomme</code> avec "vêtement pour homme"</li>
	 * <li><code>this.produitAvecMauvaisSousTypeProduit</code> avec "vêtement pour homme" (soustypeProduitVetementPourHommeSansTypeProduit)</li>
	 * <li><code>this.produitChemiseManchesLonguesFemme</code> avec "vêtement pour femme"</li>
	 * <li><code>this.produitChemiseManchesCourtesFemme</code> avec "vêtement pour femme"</li>
	 * <li><code>this.produitSweatshirtFemme</code> avec "vêtement pour femme"</li>
	 * <li><code>this.produitSoutienGorgeFemme</code> avec "vêtement pour femme"</li>
	 * <li><code>this.produitChemiseManchesLonguesEnfant</code> avec "vêtement pour enfant"</li>
	 * <li><code>this.produitChemiseManchesCourtesEnfant</code> avec "vêtement pour enfant"</li>
	 * <li><code>this.produitSweatshirtEnfant</code> avec "vêtement pour enfant"</li>
	 *</ul>
	 *</div>
	 */
	private void creerVetement() {
		
		this.typeProduitVetement = new TypeProduit("vêtement");
		
		this.soustypeProduitVetementPourHomme = new SousTypeProduit(VETEMENT_HOMME, this.typeProduitVetement);
		this.soustypeProduitVetementPourFemme = new SousTypeProduit(VETEMENT_FEMME, this.typeProduitVetement);
		this.soustypeProduitVetementPourEnfant= new SousTypeProduit(VETEMENT_ENFANT, this.typeProduitVetement);
		this.soustypeProduitVetementPourHommeSansTypeProduit = new SousTypeProduit(VETEMENT_HOMME);
		
		
		/* création de produits de type vêtement. */
		/* Homme */
		this.produitChemiseManchesLonguesHomme 
			= new Produit(CHEMISE_MANCHES_LONGUES, this.soustypeProduitVetementPourHomme);
		this.produitChemiseManchesCourtesHomme
			= new Produit(CHEMISES_MANCHES_COURTES, soustypeProduitVetementPourHomme);
		this.produitSweatshirtHomme
			= new Produit(SWEATSHIRT, soustypeProduitVetementPourHomme);
		this.produitAvecMauvaisSousTypeProduit 
			= new Produit("tee-shirt", soustypeProduitVetementPourHommeSansTypeProduit);

		/* Femme */
		this.produitChemiseManchesLonguesFemme 
			= new Produit(CHEMISE_MANCHES_LONGUES, soustypeProduitVetementPourFemme);
		this.produitChemiseManchesCourtesFemme 
			= new Produit(CHEMISES_MANCHES_COURTES, soustypeProduitVetementPourFemme);
		this.produitSweatshirtFemme 
			= new Produit(SWEATSHIRT, soustypeProduitVetementPourFemme);		
		this.produitSoutienGorgeFemme 
			= new Produit(SOUTIEN_GORGE, soustypeProduitVetementPourFemme);

		/* Enfant */
		this.produitChemiseManchesLonguesEnfant 
			= new Produit(CHEMISE_MANCHES_LONGUES, soustypeProduitVetementPourEnfant);
		this.produitChemiseManchesCourtesEnfant 
			= new Produit(CHEMISES_MANCHES_COURTES, soustypeProduitVetementPourEnfant);
		this.produitSweatshirtEnfant 
			= new Produit(SWEATSHIRT, soustypeProduitVetementPourEnfant);
		
		this.listeProduitsVetement = new ArrayList<ProduitI>();
		listeProduitsVetement.add(this.produitChemiseManchesLonguesHomme);
		listeProduitsVetement.add(this.produitChemiseManchesCourtesHomme);
		listeProduitsVetement.add(this.produitSweatshirtHomme);
		listeProduitsVetement.add(this.produitAvecMauvaisSousTypeProduit);
		listeProduitsVetement.add(this.produitChemiseManchesLonguesFemme);
		listeProduitsVetement.add(this.produitChemiseManchesCourtesFemme);
		listeProduitsVetement.add(this.produitSweatshirtFemme);
		listeProduitsVetement.add(this.produitSoutienGorgeFemme);
		listeProduitsVetement.add(this.produitChemiseManchesLonguesEnfant);
		listeProduitsVetement.add(this.produitChemiseManchesCourtesEnfant);
		listeProduitsVetement.add(this.produitSweatshirtEnfant);
		
	}
	
	

}
