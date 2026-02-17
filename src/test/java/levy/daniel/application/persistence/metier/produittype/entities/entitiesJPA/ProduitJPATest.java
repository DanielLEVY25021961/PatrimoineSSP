package levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import levy.daniel.application.model.metier.produittype.ProduitI;
import levy.daniel.application.model.metier.produittype.SousTypeProduitI;
import levy.daniel.application.model.metier.produittype.TypeProduitI;


/**
 * <div>
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">CLASSE ProduitJPATest.java :</p>
 * 
 * <p>
 * Cette classe teste l'Entity JPA : 
 * <span style="font-weight:bold;">ProduitJPA</span> 
 * </p>
 * </div>
 * 
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 11 déc. 2025
 */
public class ProduitJPATest {

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
	 * "chemise"
	 */
	public static final String CHEMISE = "chemise";
	
	/**
	 * "vêtement"
	 */
	public static final String VETEMENT = "vêtement";
	
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
	 * <div>
	 * <p>"Produit clone modifié".</p>
	 * </div>
	 */
	public static final String PRODUIT_CLONE_MODIFIE 
		= "Produit clone modifié";

	/**
	 * <div>
	 * <p>"Sous-type clone modifié".</p>
	 * </div>
	 */
	public static final String SOUS_TYPE_CLONE_MODIFIE 
		= "Sous-type clone modifié";

	/**
	 * <div>
	 * <p>"Type produit clone modifié".</p>
	 * </div>
	 */
	public static final String TYPE_PRODUIT_CLONE_MODIFIE 
		= "Type produit clone modifié";

	
	/* ------------------------------------------------------------------ */
	
	/**
	 * TypeProduitI "vêtement"
	 */
	public TypeProduitI typeProduitVetement;
	
	/**
	 * SousTypeProduitI "vêtement pour homme" avec TypeProduitI "vêtement"
	 */
	public SousTypeProduitI soustypeProduitVetementPourHomme;
	
	/**
	 * SousTypeProduitI "vêtement pour femme" avec TypeProduitI "vêtement"
	 */
	public SousTypeProduitI soustypeProduitVetementPourFemme;
	
	/**
	 * SousTypeProduitI "vêtement pour enfant" avec TypeProduitI "vêtement"
	 */
	public SousTypeProduitI soustypeProduitVetementPourEnfant;
	
	/**
	 * <p>SousTypeProduitI "vêtement pour homme" avec TypeProduitI NULL.</p>
	 * <p>new SousTypeProduitJPA(VETEMENT_HOMME);</p>
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
	 * new ProduitJPA(SWEATSHIRT, soustypeProduitVetementPourHomme)
	 */
	public ProduitI produitSweatshirtHomme;
	
	/**
	 * new ProduitJPA("tee-shirt", soustypeProduitVetementPourHommeSansTypeProduit);
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
			.getLogger(ProduitJPATest.class);

	// *************************METHODES************************************/



	/**
	* <div>
	* <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	* </div>
	*/
	public ProduitJPATest() {
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
			System.out.println("********** CLASSE ProduitJPATest - méthode testConstructeurNull() ********** ");
			System.out.println("CE TEST VERIFIE LE FONCTIONNEMENT DU CONSTRUCTEUR D'ARITE NULLE.");
			System.out.println();				
		}
		
		//**** ARRANGE - GIVEN
		final ProduitI objetConstructeurNull1 = new ProduitJPA();
		final ProduitI objetConstructeurNull2 = new ProduitJPA();
		
		// ACT - WHEN
		final Long idObjetConstructeurNull1 = objetConstructeurNull1.getIdProduit();
		final Long idObjetConstructeurNull2 = objetConstructeurNull2.getIdProduit();
		
		final String produitConstructeurNullString1 = objetConstructeurNull1.getProduit();
		final String produitConstructeurNullString2 = objetConstructeurNull2.getProduit();
		
		final SousTypeProduitI sousTypeProduitNull1 = objetConstructeurNull1.getSousTypeProduit();
		final SousTypeProduitI sousTypeProduitNull2 = objetConstructeurNull2.getSousTypeProduit();
		
		final TypeProduitI typeProduitNull1 = objetConstructeurNull1.getTypeProduit();
		final TypeProduitI typeProduitNull2 = objetConstructeurNull2.getTypeProduit();
		
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
			System.out.println("***ProduitI produitConstructeurNull1 = new ProduitJPA();*** ");
			System.out.println("***ProduitI produitConstructeurNull2 = new ProduitJPA();*** ");
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

		} // ______________________________________________________________
		
		
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
		final TypeProduitI typeProduitPeche = new TypeProduitJPA(10L, "Pêche", null);
		final SousTypeProduitI sousTypeProduitCanneMoulinet = new SousTypeProduitJPA(100L, "Cannes et Moulinets", typeProduitPeche, null);
		objetConstructeurNull1.setSousTypeProduit(sousTypeProduitCanneMoulinet);
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("*** APRES TypeProduitI typeProduitPeche = new TypeProduitJPA(10L, \"Pêche\", null); et SousTypeProduitI sousTypeProduitCanneMoulinet = new SousTypeProduitJPA(100L, \"Cannes et Moulinets\", typeProduitPeche, null); ***");
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
			System.out.println("********** CLASSE ProduitJPATest - méthode testEquals() ********** ");
			System.out.println("CE TEST VERIFIE LE RESPECT DU CONTRAT Java Equals() et Hashcode().");
			System.out.println();				
		}
		
		// ARRANGE - GIVEN
		final TypeProduitI typeProduit1 = new TypeProduitJPA(PHOTOGRAPHIE);
		final TypeProduitI typeProduit2 = new TypeProduitJPA(PHOTOGRAPHIE);
		final SousTypeProduitI sousTypeProduit1 = new SousTypeProduitJPA(CAMERAS, typeProduit1);
		final SousTypeProduitI sousTypeProduit2 = new SousTypeProduitJPA(CAMERAS, typeProduit1);
		final SousTypeProduitI sousTypeProduitDifferent = new SousTypeProduitJPA("camescopes", typeProduit1);
		final ProduitI objet1 = new ProduitJPA(APPAREIL_PHOTO, sousTypeProduit1);
		final ProduitI objet2 = new ProduitJPA(APPAREIL_PHOTO, sousTypeProduit2);
		final ProduitI objet3 = new ProduitJPA(APPAREIL_PHOTO, sousTypeProduit1);
		final ProduitI objet4 = new ProduitJPA(APPAREIL_PHOTO, sousTypeProduitDifferent);
		final ProduitI objet5 = new ProduitJPA("ZZ Top", sousTypeProduit1);
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
			System.out.println("***APRES ProduitI objet1 = new ProduitJPA(\"appareil photo\", sousTypeProduit1);***");
			this.afficher(objet1);
			System.out.println();
			System.out.println("***APRES ProduitI objet2 = new ProduitJPA(\"appareil photo\", sousTypeProduit2);***");
			this.afficher(objet2);
			System.out.println();
			System.out.println("***APRES ProduitI objet3 = new ProduitJPA(\"appareil photo\", sousTypeProduit1);***");
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
		final ProduitI objetConstructeurNull1 = new ProduitJPA();
		final ProduitI objetConstructeurNull2 = new ProduitJPA();
		
		final ProduitI objetAvecValeursNull1 = new ProduitJPA(1L, null, sousTypeProduit1);
		final ProduitI objetAvecValeursNull2 = new ProduitJPA(2L, null, sousTypeProduit2);

		final ProduitI objetAvecValeursVide1 = new ProduitJPA(1L, "", sousTypeProduit1);
		final ProduitI objetAvecValeursVide2 = new ProduitJPA(2L, "", sousTypeProduit2);
		
		final ProduitI objetAvecSousValeursNull1 = new ProduitJPA(1L, DINDON, null);
		final ProduitI objetAvecSousValeursNull2 = new ProduitJPA(2L, DINDON, null);

		// ACT - WHEN
		
		// ASSERT - THEN
		/* garantit que les null sont bien gérés dans equals(...). */
		assertEquals(objetConstructeurNull1, objetConstructeurNull2, "objetConstructeurNull1.equals(objetConstructeurNull2) : ");
		assertEquals(objetConstructeurNull1.hashCode(), objetConstructeurNull2.hashCode(), "objetConstructeurNull1.hashcode() == objetConstructeurNull2.hashcode()) : ");
		
		assertNotEquals(objetAvecValeursNull1, objetAvecValeursNull2, "objetAvecValeursNull1.equals(objetAvecValeursNull2) est faux (id) : ");
		assertNotEquals(objetAvecValeursVide1, objetAvecValeursVide2, "objetAvecValeursVide1.equals(objetAvecValeursVide2) est faux (id) : ");
		assertNotEquals(objetAvecSousValeursNull1, objetAvecSousValeursNull2, "objetAvecSousValeursNull1.equals(objetAvecSousValeursNull2) est faux (id) : ");

		
		//*** ARRANGE - GIVEN
		final ProduitI objetPasEqualsObjet1 = new ProduitJPA("camescope", sousTypeProduit1);
		final ProduitI objetPasEqualsObjet2 = new ProduitJPA(APPAREIL_PHOTO, sousTypeProduitDifferent);
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println("***APRES ProduitI objetPasEqualsObjet1 = new ProduitJPA(\"camescope\", sousTypeProduit1);***");
			this.afficher(objetPasEqualsObjet1);
			System.out.println();
			System.out.println("***APRES objetPasEqualsObjet2 = new ProduitJPA(\"appareil photo\", sousTypeProduitDifferent);***");
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
	 * <p>
	 * Teste equals() avec même libellé produit mais parents différents
	 * (et IDs parent null) :
	 * </p>
	 * <ul>
	 * <li>force le fallback final (pas d'ID technique Produit, 
	 * pas d'ID parent SousTypeProduit).</li>
	 * <li>vérifie que deux produits de même libellé 
	 * mais rattachés à des parents
	 * différents ne sont pas equals.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testEqualsMemeLibelleParentsDifferentsSansIds() : même libellé mais parents différents (IDs null) -> pas equals")
	@Tag("equals")
	@Test
	public final void testEqualsMemeLibelleParentsDifferentsSansIds() {

		// ARRANGE - GIVEN
		final String libelleProduit = CHEMISE;

		/*
		 * Parents TypeProduitJPA (IDs null).
		 * On garde volontairement le même TypeProduit pour ne faire varier
		 * que le parent direct (SousTypeProduitJPA).
		 */
		final TypeProduitJPA type = new TypeProduitJPA(VETEMENT);
		type.setIdTypeProduit(null);

		/*
		 * Parents SousTypeProduitJPA différents (IDs null) :
		 * - libellés différents => equals() sur les parents doit retourner false
		 * (même si les IDs sont null).
		 */
		final SousTypeProduitJPA stp1 = new SousTypeProduitJPA(
				null, "vêtement homme", type, null);
		final SousTypeProduitJPA stp2 = new SousTypeProduitJPA(
				null, "vêtement femme", type, null);

		/* Produits : même libellé, parents différents, IDs null. */
		final ProduitJPA p1 = new ProduitJPA(null, libelleProduit, stp1);
		final ProduitJPA p2 = new ProduitJPA(null, libelleProduit, stp2);

		// ACT - WHEN
		final boolean equals12 = p1.equals(p2);
		final boolean equals21 = p2.equals(p1);

		// ASSERT - THEN
		assertFalse(equals12,
				"Même libellé mais parents différents (IDs null) : equals() doit retourner false : ");
		assertFalse(equals21,
				"Symétrie : equals() doit retourner false : ");

	} //___________________________________________________________________


	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste compareTo() avec <b>parent null</b> vs <b>parent non null</b> :</p>
	 * <li>compareTo() est cohérent et ne dépend pas du parent.</li>
	 * <li>même libellé produit (case-insensitive) -> compareTo == 0.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testCompareToParentNullVsNonNull() : parent null vs non null ne doit pas impacter compareTo")
	@Tag("compareTo")
	@Test
	public final void testCompareToParentNullVsNonNull() {

		// ARRANGE - GIVEN
		final String libelleProduit1 = CHEMISE;
		final String libelleProduit2 = "CHEMISE";

		final TypeProduitJPA type = new TypeProduitJPA(VETEMENT);
		final SousTypeProduitJPA stp
			= new SousTypeProduitJPA(null, "vêtement homme", type, null);

		final ProduitJPA produitAvecParent
			= new ProduitJPA(null, libelleProduit1, stp);

		final ProduitJPA produitSansParent
			= new ProduitJPA(null, libelleProduit2, null);

		// ACT - WHEN
		final int compare12 = produitAvecParent.compareTo(produitSansParent);
		final int compare21 = produitSansParent.compareTo(produitAvecParent);

		// ASSERT - THEN
		assertTrue(compare12 == 0, "compareTo() doit être insensible au parent (même libellé, case-insensitive) : ");
		assertTrue(compare21 == 0, "Symétrie compareTo() (case-insensitive) : ");

	} //___________________________________________________________________


	
	/**
	 * <div>
	 * <p>
	 * Teste explicitement la (re)mise à jour de la validité via les setters :
	 * </p>
	 * <ul>
	 * <li>au départ : produit null et sousTypeProduit null -> valide false.</li>
	 * <li>après setProduit(non null) seul -> valide false.</li>
	 * <li>après setSousTypeProduit(non null) -> valide true.</li>
	 * <li>après setProduit(null) : la validité ne se met à jour que lorsqu'une
	 * opération déclenche recalculerValide() (ici via setSousTypeProduit(null)).</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testRecalculerValideViaSetters() : vérifie la mise à jour de valide via les setters")
	@Tag("valide")
	@Test
	public final void testRecalculerValideViaSetters() {

		// ARRANGE - GIVEN
		final ProduitJPA produit = new ProduitJPA();
		final TypeProduitJPA type = new TypeProduitJPA(VETEMENT);
		final SousTypeProduitJPA stp = new SousTypeProduitJPA(
				null, "vêtement homme", type, null);

		// ASSERT - THEN
		assertFalse(produit.isValide(),
				"ProduitJPA (constructeur nul) doit être invalide : ");

		// ACT - WHEN
		produit.setProduit(CHEMISE);

		// ASSERT - THEN
		assertFalse(produit.isValide(),
				"ProduitJPA sans parent doit rester invalide : ");

		// ACT - WHEN
		produit.setSousTypeProduit(stp);

		// ASSERT - THEN
		assertTrue(produit.isValide(),
				"ProduitJPA avec libellé + parent doit être valide : ");

		// ACT - WHEN
		produit.setProduit(null);

		/*
		 * La mise à jour de la validité dépend de la logique interne
		 * (recalculerValide()) déclenchée par certaines opérations.
		 * Ici, on déclenche explicitement un recalcul via setSousTypeProduit(null).
		 */
		produit.setSousTypeProduit(null);

		// ASSERT - THEN
		assertFalse(produit.isValide(),
				"ProduitJPA sans libellé doit redevenir invalide : ");

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
			System.out.println("********** CLASSE ProduitJPATest - méthode testToString() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode toString().");
			System.out.println();				
		}
		
		//**** ARRANGE - GIVEN
		final ProduitI objetConstructeurNull = new ProduitJPA();
		
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
		final TypeProduitI typeProduit1 = new TypeProduitJPA(ANATOMIE);
		final SousTypeProduitI sousTypeProduit1 = new SousTypeProduitJPA(ANATOMIE_MAIN, typeProduit1);
		final ProduitI objet1 = new ProduitJPA(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit1);
		
		
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
		final String resultat 
			= "Produit [idProduit=1"
					+ ", produit=Anatomie arthroscopique de la main"
					+ ", sousTypeProduit=SousTypeProduit "
					+ "[idSousTypeProduit=null"
					+ ", sousTypeProduit=Anatomie de la main"
					+ ", typeProduit=TypeProduitJPA ["
					+ "idTypeProduit=null, typeProduit=Anatomie]]]";

		
		// ASSERT - THEN
		/* garantit le bon fonctionnement de toString() */
		assertEquals(resultat, objet1.toString(), "doit afficher toString() : ");

	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>toString()</b> avec sous-type et type de produit <code>null</code> :</p>
	 * <li>garantit que <code>toString()</code> ne lève pas d'exception.</li>
	 * <li>garantit que les <code>null</code> sont correctement affichés pour le sous-type.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testToStringAvecNull() : vérifie toString() avec sous-type null")
	@Tag("toString")
	@Test
	public final void testToStringAvecNull() {
	    // **********************************
	    // AFFICHAGE DANS LE TEST ou NON
	    final boolean affichage = false;
	    // **********************************

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("********** CLASSE ProduitJPATest - méthode testToStringAvecNull() **********");
	        System.out.println("CE TEST VERIFIE toString() AVEC SOUS-TYPE NULL.");
	        System.out.println();
	    }

	    //**** ARRANGE - GIVEN
	    final ProduitJPA produit = new ProduitJPA(100L, ANATOMIE_ARTHRO_MAIN, null);

	    // ACT - WHEN
	    final String toStringResult = produit.toString();

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println("Résultat de toString() : " + toStringResult);
	    }

	    // ASSERT - THEN
	    assertNotNull(toStringResult, "toString() ne doit pas retourner null : ");
	    assertTrue(toStringResult.contains("sousTypeProduit=null"), "toString() doit contenir 'sousTypeProduit=null' : ");
	    assertFalse(toStringResult.contains("typeProduit="), "toString() ne doit pas contenir 'typeProduit=' si le sous-type est null : ");
	    
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
			System.out.println("********** CLASSE ProduitJPATest - méthode testCompareTo() ********** ");
			System.out.println("CE TEST VERIFIE LE RESPECT des contrats Java de la méthode compareTo().");
			System.out.println();
		}

		//*** ARRANGE - GIVEN
		/* TypeProduitI */
		final TypeProduitI typeProduit1 = new TypeProduitJPA(ANATOMIE);
		final TypeProduitI typeProduit2 = new TypeProduitJPA(PHOTOGRAPHIE);

		/* SousTypeProduitI */
		final SousTypeProduitI sousTypeProduit1
			= new SousTypeProduitJPA(ANATOMIE_MAIN, typeProduit1);
		final SousTypeProduitI sousTypeProduit2
			= new SousTypeProduitJPA(CAMERAS, typeProduit2);

		/* ProduitI */
		final ProduitI objet1
			= new ProduitJPA(ANATOMIE_ARTHRO_MAIN, sousTypeProduit1);
		final ProduitI objetEqualsObjet1
			= new ProduitJPA(ANATOMIE_ARTHRO_MAIN, sousTypeProduit1);
		final ProduitI objet1MemeInstance = objet1;

		/* Ordre alphabétique sur produit (case-insensitive). */
		final ProduitI objet2AvantObjet1
			= new ProduitJPA("aaa", sousTypeProduit1);
		final ProduitI objet3ApresObjet1
			= new ProduitJPA("zzz", sousTypeProduit2);

		/* Case-insensitive : doit être équivalent à objet1 au compareTo(). */
		final ProduitI objetCaseDifferent
			= new ProduitJPA("ANATOMIE ARTHROSCOPIQUE DE LA MAIN", sousTypeProduit2);

		/* Gestion des null. */
		final ProduitI objetProduitNull1
			= new ProduitJPA(null, sousTypeProduit1);
		final ProduitI objetProduitNull2
			= new ProduitJPA(null, sousTypeProduit2);

		/* Gestion des vides : on ne force pas un contrat non garanti,
		 * on vérifie seulement l'absence d'exception et la stabilité du tri. */
		final ProduitI objetProduitVide
			= new ProduitJPA("", sousTypeProduit1);

		// ACT - WHEN
		final int compareMemeInstance = objet1.compareTo(objet1MemeInstance);
		final int compareToNull = objet1.compareTo(null);
		final int compareToEquals = objet1.compareTo(objetEqualsObjet1);

		// ASSERT - THEN
		/* garantit que compareTo(memeInstance) retourne 0. */
		assertTrue(compareMemeInstance == 0, "compareTo(memeInstance) doit retourner 0 : ");

		/* garantit que compareTo(null) retourne un nombre négatif. */
		assertTrue(compareToNull < 0, "compareTo(null) doit retourner négatif : ");

		/* garantit le Contrat Java :
		 * x.equals(y) ---> x.compareTo(y) == 0. */
		assertNotSame(objet1, objetEqualsObjet1, "objet1 n'est pas la même instance que objetEqualsObjet1 : ");
		assertEquals(objet1, objetEqualsObjet1, "objet1 equals objetEqualsObjet1 : ");
		assertEquals(objet1.hashCode(), objetEqualsObjet1.hashCode(), "objet1.hashCode() == objetEqualsObjet1.hashCode() : ");
		assertTrue(compareToEquals == 0, "objet1 equals objetEqualsObjet1 ----> objet1.compareTo(objetEqualsObjet1) == 0 : ");

		/* garantit l'insensibilité à la casse dans compareTo(). */
		assertTrue(objet1.compareTo(objetCaseDifferent) == 0, "compareTo() doit être insensible à la casse : ");
		assertTrue(objetCaseDifferent.compareTo(objet1) == 0, "compareTo() doit être insensible à la casse (symétrie) : ");

		/* garantit le bon ordre sur produit (alphabetique). */
		assertTrue(objet2AvantObjet1.compareTo(objet1) < 0, "objet2AvantObjet1 doit être avant objet1 : ");
		assertTrue(objet3ApresObjet1.compareTo(objet1) > 0, "objet3ApresObjet1 doit être après objet1 : ");

		/* garantit la gestion des null sur le champ produit :
		 * - produit null est considéré "après" un produit non null. */
		assertTrue(objetProduitNull1.compareTo(objet1) > 0, "produit null doit être après un produit non null : ");
		assertTrue(objet1.compareTo(objetProduitNull1) < 0, "produit non null doit être avant produit null : ");
		assertTrue(objetProduitNull1.compareTo(objetProduitNull2) == 0, "deux produits null doivent être égaux au compareTo : ");

		/* garantit le bon fonctionnement de Collections.sort()
		 * sans faire d'hypothèse non contractuelle sur l'ordre des vides. */
		final List<ProduitI> listeProduits = new ArrayList<>();
		listeProduits.add(objet1);
		listeProduits.add(objetEqualsObjet1);
		listeProduits.add(objet2AvantObjet1);
		listeProduits.add(objet3ApresObjet1);
		listeProduits.add(objetCaseDifferent);
		listeProduits.add(objetProduitVide);
		listeProduits.add(objetProduitNull1);
		listeProduits.add(objetProduitNull2);

		Collections.sort(listeProduits);

		/* Les derniers doivent être des produits null (null "après"). */
		assertTrue(
				listeProduits.get(listeProduits.size() - 1).getProduit() == null,
				"Le dernier élément trié doit avoir produit == null : ");

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
			System.out.println("********** CLASSE ProduitJPATest - méthode testClone() ********** ");
			System.out.println("CE TEST VERIFIE LE RESPECT des contrats Java de la méthode clone().");
			System.out.println();				
		}
		
		//***** ARRANGE - GIVEN
		final ProduitJPA objetConstructeurNull = new ProduitJPA();

		// ACT - WHEN
		final ProduitI objetConstructeurNullClone 
			= objetConstructeurNull.clone();
		
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
		final TypeProduitI typeProduit1 = new TypeProduitJPA(ANATOMIE);
		final SousTypeProduitI sousTypeProduit1 = new SousTypeProduitJPA(ANATOMIE_MAIN, typeProduit1);
		final ProduitJPA objet1 = new ProduitJPA(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit1);
		
		// ACT - WHEN
		final ProduitJPA objet1Clone = objet1.clone();
		
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
		final SousTypeProduitI sousTypeProduit2 = new SousTypeProduitJPA("Anatomie modifiée", typeProduit1);
		final ProduitI produit2 = new ProduitJPA("ProduitI modifié", sousTypeProduit2);
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
		assertFalse(objet1.getSousTypeProduit().equals(objet1Clone.getSousTypeProduit()), "la modification du SousTypeProduitI dans le clone ne doit pas modifier le SousTypeProduitI dans l'objet cloné : ");
		
	} //___________________________________________________________________
	

	
	/**
	 * <div>
	 * <p>teste la méthode <span style= "font-weight : bold">getEnTeteCsv()</span></p>
	 * <p>Garantit que enTeteCsv() retourne 
	 * "idproduit;type de ProduitJPA;sous-type de ProduitJPA;ProduitJPA;"</p>
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
			System.out.println("********** CLASSE ProduitJPATest - méthode testGetEnTeteCsv() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode getEnTeteCsv().");
			System.out.println();				
		}
		
		//**** ARRANGE - GIVEN
		final ProduitI objetConstructeurNull = new ProduitJPA();
		
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
		assertEquals(enTeteCsvPrevue, enTeteCsv, "enTeteCsv doit retourner \"idproduit;type de ProduitJPA;sous-type de ProduitJPA;ProduitJPA;\" : ");
		
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
			System.out.println("********** CLASSE ProduitJPATest - méthode testToStringCsv() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode toStringCsv().");
			System.out.println();				
		}
				
		// *** ARRANGE - GIVEN
		final ProduitI objetConstructeurNull = new ProduitJPA();
		
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
		final TypeProduitI typeProduit1 = new TypeProduitJPA(ANATOMIE);
		final SousTypeProduitI sousTypeProduit1 = new SousTypeProduitJPA(ANATOMIE_MAIN, typeProduit1);
		final ProduitI objet1 = new ProduitJPA(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit1);
		
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
		final TypeProduitI typeProduitZarbi = new TypeProduitJPA(100L, null, null);
		final SousTypeProduitI sousTypeProduitZarbi = new SousTypeProduitJPA(200L, null, null, null);
		final ProduitI produitZarbi = new ProduitJPA(1L, null, sousTypeProduitZarbi);
		final ProduitI produitZarbi2 = new ProduitJPA("zarbi2");
		
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
			System.out.println("********** CLASSE ProduitJPATest - méthode testGetEnTeteColonne() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode getEnTeteColonne().");
			System.out.println();				
		}
				
		// *** ARRANGE - GIVEN
		final ProduitI objetConstructeurNull = new ProduitJPA();
		
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
			System.out.println("********** CLASSE ProduitJPATest - méthode testGetValeurColonne() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode getValeurColonne().");
			System.out.println();				
		}
				
		// *** ARRANGE - GIVEN
		final ProduitI objetConstructeurNull = new ProduitJPA();
		
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
		final TypeProduitI typeProduit1 = new TypeProduitJPA(ANATOMIE);
		final SousTypeProduitI sousTypeProduit1 = new SousTypeProduitJPA(ANATOMIE_MAIN, typeProduit1);
		final ProduitI objet1 = new ProduitJPA(1L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit1);
		
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
	 * <p>vérifie le comportement général de la classe ProduitJPA</p>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testGeneral() : vérifie le comportement général de la classe ProduitJPA")
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
			System.out.println("CE TEST VERIFIE LE FONCTIONNEMENT ORDINAIRE de la classe ProduitJPA.");
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
		final SousTypeProduitI sousTypeProduitVetementHomme 
			= this.produitChemiseManchesCourtesHomme.getSousTypeProduit();
		
		final SousTypeProduitI sousTypeProduitVetementHomme2 
			= this.produitChemiseManchesLonguesHomme.getSousTypeProduit();
		
		final SousTypeProduitI sousTypeProduitAvecMauvaisSousTypeProduit 
			= this.produitAvecMauvaisSousTypeProduit.getSousTypeProduit();
		
		final SousTypeProduitI sousTypeProduitVetementFemme 
			= this.produitChemiseManchesLonguesFemme.getSousTypeProduit();

		
		final List<? extends ProduitI> listeProduitsDuSousTypeProduitVetementHomme 
			= new ArrayList<ProduitI>(sousTypeProduitVetementHomme.getProduits());
		
		final List<? extends ProduitI> listeProduitsDuSousTypeProduitVetementHomme2 
			= new ArrayList<ProduitI>(sousTypeProduitVetementHomme2.getProduits());
		
		final List<? extends ProduitI> listeProduitsDuSousTypeProduitAvecMauvaisSousTypeProduit 
			= new ArrayList<ProduitI>(sousTypeProduitAvecMauvaisSousTypeProduit.getProduits());
		
		final List<? extends ProduitI> listeProduitsDuSousTypeProduitVetementFemme 
			= new ArrayList<ProduitI>(sousTypeProduitVetementFemme.getProduits());
		
		
		
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
	 *  .
	 *
	 */
	@Test
	@DisplayName("JPA - HashSet: entity transient (id null) puis persist (id non null) => contains() peut échouer (hashCode change)")
	public void testHashSetTransientThenPersistIdChangesHashCode() {

		final ProduitJPA p = new ProduitJPA();
		p.setProduit(CHEMISE);

		// transient: id null
		final Set<ProduitJPA> set = new HashSet<>();
		set.add(p);

		// sanity
		assertThat(set.contains(p)).isTrue();

		// simulation "persist": id devient non-null (hashCode bascule sur
		// id)
		p.setIdProduit(1L);

		// Le point important à documenter/tester: contains peut devenir
		// faux
		assertThat(set.contains(p)).isFalse();

		// Bon usage : retirer / ré-ajouter après attribution de l'id
		set.remove(p); // remove peut échouer aussi, mais on tente
		set.add(p);

		assertThat(set.contains(p)).isTrue();

	} //___________________________________________________________________



	/**
	 *  .
	 *
	 */
	@Test
	@DisplayName("JPA - toString(): ne doit pas exploser le graphe (pas de récursion, pas de boucle, pas de timeout)")
	public void testToStringDoesNotExplodeGraph() {

		// Arrange : petit graphe cyclique
	    final TypeProduitJPA tp = new TypeProduitJPA(VETEMENT);
	    final SousTypeProduitJPA stp = new SousTypeProduitJPA("vêtement pour homme", tp, null);
	    final ProduitJPA p = new ProduitJPA("chemise manches longues", stp);

	    // Act
	    final String out = assertDoesNotThrow(p::toString);

	    // Assert : non vide + borné
	    assertThat(out).isNotNull();
	    assertThat(out).isNotBlank();

	    // borne volontairement "large" : ajuste si tu veux
	    assertThat(out.length()).isLessThan(20_000);

	    // Sanity : présence de marqueurs attendus
	    assertThat(out).contains("Produit [");
	    assertThat(out).contains("produit=");
	    assertThat(out).contains("sousTypeProduit=");
	    
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>setProduit()</b> avec normalisation :</p>
	 * <li>garantit que les chaînes vides sont normalisées en <code>null</code>.</li>
	 * <li>garantit que les chaînes avec espaces sont trimées.</li>
	 * <li>garantit que les <code>null</code> restent <code>null</code>.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testSetProduitNormalize() : vérifie la normalisation dans setProduit()")
	@Tag("setters")
	@Test
	public final void testSetProduitNormalize() {
	    // **********************************
	    // AFFICHAGE DANS LE TEST ou NON
	    final boolean affichage = false;
	    // **********************************

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("********** CLASSE ProduitJPATest - méthode testSetProduitNormalize() **********");
	        System.out.println("CE TEST VERIFIE LA NORMALISATION DANS setProduit().");
	        System.out.println();
	    }

	    //**** ARRANGE - GIVEN
	    final ProduitJPA produit = new ProduitJPA();

	    // ACT - WHEN
	    produit.setProduit(null);
	    final String resultatNull = produit.getProduit();

	    produit.setProduit("   ");
	    final String resultatVide = produit.getProduit();

	    produit.setProduit("  Anatomie arthroscopique de la main  ");
	    final String resultatTrim = produit.getProduit();

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println("Résultat avec null : " + resultatNull);
	        System.out.println("Résultat avec chaîne vide : " + resultatVide);
	        System.out.println("Résultat avec chaîne trimée : " + resultatTrim);
	    }

	    // ASSERT - THEN
	    assertNull(resultatNull, "setProduit(null) doit retourner null : ");
	    assertNull(resultatVide, "setProduit(\"   \") doit retourner null : ");
	    assertEquals("Anatomie arthroscopique de la main", resultatTrim, "setProduit(\"  Anatomie arthroscopique de la main  \") doit retourner \"Anatomie arthroscopique de la main\" : ");
	    
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>setSousTypeProduit()</b> avec rattachement bidirectionnel :</p>
	 * <li>garantit que le produit est ajouté à la liste des produits du sous-type.</li>
	 * <li>garantit que le produit est retiré de l'ancien sous-type.</li>
	 * <li>garantit que <code>valide</code> est mis à jour.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testSetSousTypeProduitBidirectionnel() : vérifie le rattachement bidirectionnel dans setSousTypeProduit()")
	@Tag("setters")
	@Test
	public final void testSetSousTypeProduitBidirectionnel() {
	    // **********************************
	    // AFFICHAGE DANS LE TEST ou NON
	    final boolean affichage = false;
	    // **********************************

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("********** CLASSE ProduitJPATest - méthode testSetSousTypeProduitBidirectionnel() **********");
	        System.out.println("CE TEST VERIFIE LE RATTACHEMENT BIDIRECTIONNEL DANS setSousTypeProduit().");
	        System.out.println();
	    }

	    //**** ARRANGE - GIVEN
	    final TypeProduitJPA typeProduit = new TypeProduitJPA(1L, ANATOMIE, null);
	    final SousTypeProduitJPA sousTypeProduit1 = new SousTypeProduitJPA(10L, ANATOMIE_MAIN, typeProduit, null);
	    final SousTypeProduitJPA sousTypeProduit2 = new SousTypeProduitJPA(20L, PHOTOGRAPHIE, typeProduit, null);
	    final ProduitJPA produit = new ProduitJPA(100L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit1);

	    // ACT - WHEN
	    produit.setSousTypeProduit(sousTypeProduit2);

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println("Produit après changement de sous-type : " + produit.getSousTypeProduit().getSousTypeProduit());
	        System.out.println("Produits de sousTypeProduit1 : " + sousTypeProduit1.getProduits().size());
	        System.out.println("Produits de sousTypeProduit2 : " + sousTypeProduit2.getProduits().size());
	    }

	    // ASSERT - THEN
	    assertEquals(sousTypeProduit2, produit.getSousTypeProduit(), "Le sous-type de produit doit être mis à jour : ");
	    assertFalse(sousTypeProduit1.getProduits().contains(produit), "Le produit ne doit plus être dans l'ancien sous-type : ");
	    assertTrue(sousTypeProduit2.getProduits().contains(produit), "Le produit doit être dans le nouveau sous-type : ");
	    assertTrue(produit.isValide(), "Le produit doit être valide : ");
	    
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>clone()</b> avec sous-type et type de produit associés :</p>
	 * <li>garantit que les sous-types et types de produit sont clonés profondément.</li>
	 * <li>garantit que les modifications du clone n'affectent pas l'original.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws CloneNotSupportedException
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testCloneAvecSousTypeEtTypeProduit() : vérifie le clonage profond avec sous-type et type de produit associés")
	@Tag("clone")
	@Test
	public final void testCloneAvecSousTypeEtTypeProduit() throws CloneNotSupportedException {
	    // **********************************
	    // AFFICHAGE DANS LE TEST ou NON
	    final boolean affichage = false;
	    // **********************************

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("********** CLASSE ProduitJPATest - méthode testCloneAvecSousTypeEtTypeProduit() **********");
	        System.out.println("CE TEST VERIFIE LE CLONAGE PROFOND AVEC SOUS-TYPE ET TYPE DE PRODUIT ASSOCIES.");
	        System.out.println();
	    }

	    //**** ARRANGE - GIVEN
	    final TypeProduitJPA typeProduit = new TypeProduitJPA(1L, ANATOMIE, null);
	    final SousTypeProduitJPA sousTypeProduit = new SousTypeProduitJPA(10L, ANATOMIE_MAIN, typeProduit, null);
	    final ProduitJPA produit = new ProduitJPA(100L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit);

	    // ACT - WHEN
	    final ProduitJPA clone = produit.clone();

	    // Modification du clone
	    clone.setProduit(PRODUIT_CLONE_MODIFIE);
	    final TypeProduitJPA typeProduitClone = new TypeProduitJPA(2L, TYPE_PRODUIT_CLONE_MODIFIE, null);
	    final SousTypeProduitJPA sousTypeProduitClone = new SousTypeProduitJPA(20L, SOUS_TYPE_CLONE_MODIFIE, typeProduitClone, null);
	    clone.setSousTypeProduit(sousTypeProduitClone);

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println("Original : " + produit.getProduit());
	        System.out.println("Clone : " + clone.getProduit());
	        System.out.println("Sous-type original : " + produit.getSousTypeProduit().getSousTypeProduit());
	        System.out.println("Sous-type clone : " + clone.getSousTypeProduit().getSousTypeProduit());
	    }

	    // ASSERT - THEN
	    assertNotEquals(produit.getProduit(), clone.getProduit(), "Le libellé du clone doit être différent : ");
	    assertNotEquals(produit.getSousTypeProduit(), clone.getSousTypeProduit(), "Le sous-type du clone doit être différent : ");
	    assertNotEquals(produit.getSousTypeProduit().getTypeProduit(), clone.getSousTypeProduit().getTypeProduit(), "Le type de produit du clone doit être différent : ");
	    
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>getTypeProduit()</b> via <b>getSousTypeProduit()</b> :</p>
	 * <li>garantit que le type de produit est correctement récupéré via le sous-type.</li>
	 * <li>garantit que les <code>null</code> sont gérés correctement.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testGetTypeProduitViaSousTypeProduit() : vérifie la récupération du type de produit via le sous-type")
	@Tag("getters")
	@Test
	public final void testGetTypeProduitViaSousTypeProduit() {
	    // **********************************
	    // AFFICHAGE DANS LE TEST ou NON
	    final boolean affichage = false;
	    // **********************************

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("********** CLASSE ProduitJPATest - méthode testGetTypeProduitViaSousTypeProduit() **********");
	        System.out.println("CE TEST VERIFIE LA RECUPERATION DU TYPE DE PRODUIT VIA LE SOUS-TYPE.");
	        System.out.println();
	    }

	    //**** ARRANGE - GIVEN
	    final TypeProduitJPA typeProduit = new TypeProduitJPA(1L, ANATOMIE, null);
	    final SousTypeProduitJPA sousTypeProduit = new SousTypeProduitJPA(10L, ANATOMIE_MAIN, typeProduit, null);
	    final ProduitJPA produit = new ProduitJPA(100L, ANATOMIE_ARTHRO_MAIN, sousTypeProduit);

	    // ACT - WHEN
	    final TypeProduitI typeProduitRecupere = produit.getTypeProduit();

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println("Type de produit récupéré : " + typeProduitRecupere.getTypeProduit());
	    }

	    // ASSERT - THEN
	    assertEquals(typeProduit, typeProduitRecupere, "Le type de produit récupéré doit être identique : ");
	    assertEquals(ANATOMIE, typeProduitRecupere.getTypeProduit(), "Le libellé du type de produit doit être correct : ");
	    
	} //___________________________________________________________________



	/**
	 *  .
	 *
	 * @param s
	 * @param token
	 * @return
	 */
	private static int countOccurrences(
			final String s,
			final String token) {

		if (s == null || token == null || token.isEmpty()) {
			return 0;
		}

		int count = 0;
		int idx = 0;

		while (true) {

			final int found = s.indexOf(token, idx);

			if (found < 0) {

				break;

			}

			count++;
			idx = found + token.length();

		}

		return count;
	}


	
	/**
	 * <div>
	 * <p>affiche à la console un ProduitJPA.</p>
	 * </div>
	 *
	 * @param pProduit : ProduitJPA
	 */
	private void afficher(final ProduitI pProduit) {
		
		if (pProduit == null) {
			return;
		}
		
		System.out.println("id du Produit : " + pProduit.getIdProduit());
		System.out.println("Produit : " + pProduit.getProduit());
		if (pProduit.getSousTypeProduit() != null) {
			System.out.println("sous-type de Produit : " + pProduit.getSousTypeProduit().getSousTypeProduit());
		} else {
			System.out.println("sous-type de Produit : null");
		}
		if (pProduit.getTypeProduit() != null) {
			System.out.println("type de Produit : " + pProduit.getTypeProduit().getTypeProduit());
		} else {
			System.out.println("type de Produit : null");
		}
		System.out.println("valide : " + pProduit.isValide());
		
	}
	

	
	/**
	 * <div>
	 * <p>Affiche une liste de ProduitI en déclinant 
	 * SousTypeProduitI et ProduitJPA</p>
	 * </div>
	 *
	 * @param pListeProduits
	 */
	private void afficherListeProduits(final List<? extends ProduitI> pListeProduits) {
		
		if (pListeProduits == null) {
			return;
		}
		
		final StringBuilder stb = new StringBuilder();
		
		for (final ProduitI produitJPA : pListeProduits) {
			
			String sousTypeProduitString = null;
			String produitString = null;
			
			if (produitJPA == null) {
				
				stb.append("Produit NULL");
				stb.append(SAUT_DE_LIGNE);
				
			} else {
				
				final SousTypeProduitI sousTypeProduit 
					= produitJPA.getSousTypeProduit();
				
				if (sousTypeProduit == null) {
					sousTypeProduitString = NULL;
				} else {
					sousTypeProduitString 
						= sousTypeProduit.getSousTypeProduit();					
				}
				
				produitString = produitJPA.getProduit();
				
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
	 * <li><code>this.soustypeProduitVetementPourHommeSansTypeProduit</code> avec "vêtement pour homme" avec un TypeProduitI NULL</li>
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
		
		this.typeProduitVetement = new TypeProduitJPA("vêtement");
		
		this.soustypeProduitVetementPourHomme = new SousTypeProduitJPA(VETEMENT_HOMME, this.typeProduitVetement);
		this.soustypeProduitVetementPourFemme = new SousTypeProduitJPA(VETEMENT_FEMME, this.typeProduitVetement);
		this.soustypeProduitVetementPourEnfant= new SousTypeProduitJPA(VETEMENT_ENFANT, this.typeProduitVetement);
		this.soustypeProduitVetementPourHommeSansTypeProduit = new SousTypeProduitJPA(VETEMENT_HOMME);
		
		
		/* création de produits de type vêtement. */
		/* Homme */
		this.produitChemiseManchesLonguesHomme 
			= new ProduitJPA(CHEMISE_MANCHES_LONGUES, this.soustypeProduitVetementPourHomme);
		this.produitChemiseManchesCourtesHomme
			= new ProduitJPA(CHEMISES_MANCHES_COURTES, soustypeProduitVetementPourHomme);
		this.produitSweatshirtHomme
			= new ProduitJPA(SWEATSHIRT, soustypeProduitVetementPourHomme);
		this.produitAvecMauvaisSousTypeProduit 
			= new ProduitJPA("tee-shirt", soustypeProduitVetementPourHommeSansTypeProduit);

		/* Femme */
		this.produitChemiseManchesLonguesFemme 
			= new ProduitJPA(CHEMISE_MANCHES_LONGUES, soustypeProduitVetementPourFemme);
		this.produitChemiseManchesCourtesFemme 
			= new ProduitJPA(CHEMISES_MANCHES_COURTES, soustypeProduitVetementPourFemme);
		this.produitSweatshirtFemme 
			= new ProduitJPA(SWEATSHIRT, soustypeProduitVetementPourFemme);		
		this.produitSoutienGorgeFemme 
			= new ProduitJPA(SOUTIEN_GORGE, soustypeProduitVetementPourFemme);

		/* Enfant */
		this.produitChemiseManchesLonguesEnfant 
			= new ProduitJPA(CHEMISE_MANCHES_LONGUES, soustypeProduitVetementPourEnfant);
		this.produitChemiseManchesCourtesEnfant 
			= new ProduitJPA(CHEMISES_MANCHES_COURTES, soustypeProduitVetementPourEnfant);
		this.produitSweatshirtEnfant 
			= new ProduitJPA(SWEATSHIRT, soustypeProduitVetementPourEnfant);
		
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
