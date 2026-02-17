package levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

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
 * <p style="font-weight:bold;">CLASSE SousTypeProduitJPATest.java :</p>
 * </div>
 * 
 * <p>
 * Cette classe teste la classe Entity JPA : 
 * <span style="font-weight:bold;">SousTypeProduitJPA</span> 
 * </p>
 * </div>
 * 
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 15 déc. 2025
 */
public class SousTypeProduitJPATest {

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
	 * "Pêche"
	 */
	public static final String PECHE = "Pêche";
	
	/**
	 * "outillage"
	 */
	public static final String OUTILLAGE = "outillage";
	
	/**
	 * "canne à pêche"
	 */
	public static final String CANNE_A_PECHE = "canne à pêche";
	
	/**
	 * "**** sousTypeProduitCanneAPeche ******"
	 */
	public static final String SOUSP_CANNA 
		= "**** sousTypeProduitCanneAPeche ******";
	
	/**
	 * "moulinet"
	 */
	public static final String MOULINET = "moulinet";
	
	/**
	 * "**** sousTypeProduitMoulinet ******"
	 */
	public static final String SOUSP_MOULINET 
		= "**** sousTypeProduitMoulinet ******";
	
	/**
	 * "cuillère"
	 */
	public static final String CUILLERE = "cuillère";
	
	/**
	 * "**** sousTypeProduitCuillere ******"
	 */
	public static final String SOUSP_CUILLERE 
		= "**** sousTypeProduitCuillere ******";
	
	/**
	 * "perceuse"
	 */
	public static final String PERCEUSE = "perceuse";
	
	/**
	 * <div>
	 * <p>"Canne à pêche télescopique".</p>
	 * </div>
	 */
	public static final String CANNE_TELESCOPIQUE 
		= "Canne à pêche télescopique";

	/**
	 * <div>
	 * <p>"Canne à pêche en carbone".</p>
	 * </div>
	 */
	public static final String CANNE_CARBONE 
		= "Canne à pêche en carbone";

	
	/**
	 * "**** sousTypeProduitPerceuse ******"
	 */
	public static final String SOUSP_PERCEUSE 
		= "**** sousTypeProduitPerceuse ******";
	
	/**
	 * "**** sousTypeProduits dans typeProduitPeche ******"
	 */
	public static final String STP_PECHE 
		= "**** sousTypeProduits dans typeProduitPeche ******";
	
	/**
	 * "**** sousTypeProduits dans typeProduitOutillage ******"
	 */
	public static final String STP_OUTILLAGE 
		= "**** sousTypeProduits dans typeProduitOutillage ******";
	
	/**
	 * <div>
	 * <p>"Produit clone modifié".</p>
	 * </div>
	 */
	public static final String PRODUIT_CLONE_MODIFIE 
		= "Produit clone modifié";

	/**
	 * <div>
	 * <p>"Clone modifié".</p>
	 * </div>
	 */
	public static final String CLONE_MODIFIE 
		= "Clone modifié";

	
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
			.getLogger(SousTypeProduitJPATest.class);

	// *************************METHODES************************************/



	/**
	* <div>
	* <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	* </div>
	*/
	public SousTypeProduitJPATest() {
		super();
	} // Fin du CONSTRUCTEUR D'ARITE NULLE.________________________________


	
	/**
	 * <div>
	 * <p>Teste le comportement général des objets 
	 * et des listes qu'ils contiennent.</p>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testGeneral() : vérifie le comportement général de l'objet")
	@Tag("general")
	@Test
	public final void testGeneral() {
				
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE SousTypeProduitJPATest - méthode testGeneral() ********** ");
			System.out.println("CE TEST VERIFIE LE FONCTIONNEMENT GENERAL DE L'OBJET.");
			System.out.println();				
		}
		
		//**** ARRANGE - GIVEN
		/* Scénario CREATION ProduitJPA. */
		final TypeProduitI typeProduitPeche = new TypeProduitJPA(1L, PECHE, null);
		final TypeProduitI typeProduitOutillage = new TypeProduitJPA(2L, OUTILLAGE, null);
		/* Scénario CREATION SousTypeProduitJPA. */
		final SousTypeProduitI sousTypeProduitCanneAPeche 
			= new SousTypeProduitJPA(10L, CANNE_A_PECHE, typeProduitPeche, null);
		final SousTypeProduitI sousTypeProduitMoulinet 
			= new SousTypeProduitJPA(20L, MOULINET, typeProduitPeche, null);
		final SousTypeProduitI sousTypeProduitPerceuse 
			= new SousTypeProduitJPA(30L, PERCEUSE, typeProduitOutillage, null);
						
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("**** APRES Création de sousTypeProduitCanneAPeche, sousTypeProduitMoulinet, sousTypeProduitPerceuse *****");
			System.out.println(SOUSP_CANNA);
			this.afficher(sousTypeProduitCanneAPeche);
			this.afficherSousTypeProduitsDuTypeProduit(sousTypeProduitCanneAPeche);
			System.out.println();
			System.out.println(SOUSP_MOULINET);
			this.afficher(sousTypeProduitMoulinet);
			this.afficherSousTypeProduitsDuTypeProduit(sousTypeProduitMoulinet);
			System.out.println();
			System.out.println(SOUSP_PERCEUSE);
			this.afficher(sousTypeProduitPerceuse);
			this.afficherSousTypeProduitsDuTypeProduit(sousTypeProduitPerceuse);
			System.out.println();
			System.out.println(STP_PECHE);
			this.afficherSousTypeProduitsDansTypeProduit(typeProduitPeche);
			System.out.println();
			System.out.println(STP_OUTILLAGE);
			this.afficherSousTypeProduitsDansTypeProduit(typeProduitOutillage);
			System.out.println();

		}
		
		// ACT - WHEN
		
		// ASSERT - THEN
		assertFalse(typeProduitPeche.getSousTypeProduits().isEmpty(), "la Collection sousTypeProduits de typeProduitPeche ne doit pas être vide : ");

		//**** ARRANGE - GIVEN
		/* Scénario Modification du TypeProduitI d'un SousTypeProduitJPA. */
		sousTypeProduitPerceuse.setTypeProduit(typeProduitPeche);
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
		System.out.println();
		System.out.println("**************************************************************************");
		System.out.println("**** APRES sousTypeProduitPerceuse.setTypeProduit(typeProduitPeche); *****");
		System.out.println(SOUSP_CANNA);
		this.afficher(sousTypeProduitCanneAPeche);
		this.afficherSousTypeProduitsDuTypeProduit(sousTypeProduitCanneAPeche);
		System.out.println();
		System.out.println(SOUSP_MOULINET);
		this.afficher(sousTypeProduitMoulinet);
		this.afficherSousTypeProduitsDuTypeProduit(sousTypeProduitMoulinet);
		System.out.println();
		System.out.println(SOUSP_PERCEUSE);
		this.afficher(sousTypeProduitPerceuse);
		this.afficherSousTypeProduitsDuTypeProduit(sousTypeProduitPerceuse);
		System.out.println();
		System.out.println(STP_PECHE);
		this.afficherSousTypeProduitsDansTypeProduit(typeProduitPeche);
		System.out.println();
		System.out.println(STP_OUTILLAGE);
		this.afficherSousTypeProduitsDansTypeProduit(typeProduitOutillage);
		System.out.println();
		
		}
				
		// ACT - WHEN
		
		// ASSERT - THEN
		assertTrue(typeProduitOutillage.getSousTypeProduits().isEmpty(), "la Collection sousTypeProduits de typeProduitOutillage doit être vide : ");


		//**** ARRANGE - GIVEN
		/* Scénario Modification du TypeProduitI d'un SousTypeProduitJPA. */
		sousTypeProduitPerceuse.setTypeProduit(typeProduitOutillage);
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
		System.out.println();
		System.out.println("**************************************************************************");
		System.out.println("**** APRES sousTypeProduitPerceuse.setTypeProduit(typeProduitOutillage); *****");
		System.out.println(SOUSP_CANNA);
		this.afficher(sousTypeProduitCanneAPeche);
		this.afficherSousTypeProduitsDuTypeProduit(sousTypeProduitCanneAPeche);
		System.out.println();
		System.out.println(SOUSP_MOULINET);
		this.afficher(sousTypeProduitMoulinet);
		this.afficherSousTypeProduitsDuTypeProduit(sousTypeProduitMoulinet);
		System.out.println();
		System.out.println(SOUSP_PERCEUSE);
		this.afficher(sousTypeProduitPerceuse);
		this.afficherSousTypeProduitsDuTypeProduit(sousTypeProduitPerceuse);
		System.out.println();
		System.out.println(STP_PECHE);
		this.afficherSousTypeProduitsDansTypeProduit(typeProduitPeche);
		System.out.println();
		System.out.println(STP_OUTILLAGE);
		this.afficherSousTypeProduitsDansTypeProduit(typeProduitOutillage);
		System.out.println();
		
		}
				
		// ACT - WHEN
		
		// ASSERT - THEN
		assertFalse(typeProduitOutillage.getSousTypeProduits().isEmpty(), "la Collection sousTypeProduits de typeProduitOutillage ne doit pas être vide : ");
		
	} //___________________________________________________________________
	
	

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
	@DisplayName("testSousTypeProduit() : vérifie le comportement général du Constructeur d'arité nulle")
	@Tag("constructeurs")
	@Test
	public final void testSousTypeProduit() {
				
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE SousTypeProduitJPATest - méthode testSousTypeProduit() ********** ");
			System.out.println("CE TEST VERIFIE LE FONCTIONNEMENT DU CONSTRUCTEUR D'ARITE NULLE.");
			System.out.println();				
		}
		
		//**** ARRANGE - GIVEN
		final SousTypeProduitI sousTypeProduitConstructeurNull1 = new SousTypeProduitJPA();
		final SousTypeProduitI sousTypeProduitConstructeurNull2 = new SousTypeProduitJPA();
		
		// ACT - WHEN
		final Long idSousTypeProduitConstructeurNull1 = sousTypeProduitConstructeurNull1.getIdSousTypeProduit();
		final Long idSousTypeProduitConstructeurNull2 = sousTypeProduitConstructeurNull2.getIdSousTypeProduit();
		
		final String sousTypeProduitConstructeurNullString1 = sousTypeProduitConstructeurNull1.getSousTypeProduit();
		final String sousTypeProduitConstructeurNullString2 = sousTypeProduitConstructeurNull2.getSousTypeProduit();
		
		final TypeProduitI typeProduitNull1 = sousTypeProduitConstructeurNull1.getTypeProduit();
		final TypeProduitI typeProduitNull2 = sousTypeProduitConstructeurNull2.getTypeProduit();
		
		final List<? extends ProduitI> produitsNull1 = sousTypeProduitConstructeurNull1.getProduits();
		final List<? extends ProduitI> produitsNull2 = sousTypeProduitConstructeurNull2.getProduits();
		
		final boolean valideSousTypeProduitConstructeurNull1 = sousTypeProduitConstructeurNull1.isValide();
		final boolean valideSousTypeProduitConstructeurNull2 = sousTypeProduitConstructeurNull2.isValide();
		
		final int sousTypeProduitConstructeurNull1HashCode = sousTypeProduitConstructeurNull1.hashCode();
		final int sousTypeProduitConstructeurNull2HashCode = sousTypeProduitConstructeurNull2.hashCode();

		/* Vérifications */
		final boolean memeInstance1 = sousTypeProduitConstructeurNull1 == sousTypeProduitConstructeurNull2;
		final boolean memeInstance2 = sousTypeProduitConstructeurNull2 == sousTypeProduitConstructeurNull1;
		
		final boolean sousTypeProduitConstructeurNull1EqualsSousTypeProduitConstructeurNull2 
			= sousTypeProduitConstructeurNull1.equals(sousTypeProduitConstructeurNull2);
		final boolean sousTypeProduitConstructeurNull2EqualsSousTypeProduitConstructeurNull1 
			= sousTypeProduitConstructeurNull2.equals(sousTypeProduitConstructeurNull1);
		final boolean sousTypeProduitConstructeurNull1EqualsSousTypeProduitConstructeurNull1 
			= sousTypeProduitConstructeurNull1.equals(sousTypeProduitConstructeurNull1);
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("*** SousTypeProduitI sousTypeProduitConstructeurNull1 = new SousTypeProduitJPA(); *** ");
			System.out.println("*** SousTypeProduitI sousTypeProduitConstructeurNull2 = new SousTypeProduitJPA(); *** ");
			System.out.println("idSousTypeProduitConstructeurNull1 : " + idSousTypeProduitConstructeurNull1);
			System.out.println("idSousTypeProduitConstructeurNull2 : " + idSousTypeProduitConstructeurNull2);
			System.out.println();
			System.out.println("sousTypeProduitConstructeurNullString1 : " + sousTypeProduitConstructeurNullString1);
			System.out.println("sousTypeProduitConstructeurNullString2 : " + sousTypeProduitConstructeurNullString2);
			System.out.println();
			System.out.println("typeProduitNull1 : " + typeProduitNull1);
			System.out.println("typeProduitNull2 : " + typeProduitNull2);
			System.out.println();
			System.out.println("produitsNull1 : " + produitsNull1);
			System.out.println("produitsNull2 : " + produitsNull2);
			System.out.println();
			System.out.println("valideSousTypeProduitConstructeurNull1 : " + valideSousTypeProduitConstructeurNull1);
			System.out.println("valideSousTypeProduitConstructeurNull2 : " + valideSousTypeProduitConstructeurNull2);
			System.out.println();
			System.out.println("sousTypeProduitConstructeurNull1 == sousTypeProduitConstructeurNull2 : " + memeInstance1);
			System.out.println("sousTypeProduitConstructeurNull2 == sousTypeProduitConstructeurNull1 : " + memeInstance2);
			System.out.println();
			System.out.println("sousTypeProduitConstructeurNull1EqualsSousTypeProduitConstructeurNull2 : " + sousTypeProduitConstructeurNull1EqualsSousTypeProduitConstructeurNull2);
			System.out.println("sousTypeProduitConstructeurNull2EqualsSousTypeProduitConstructeurNull1 : " + sousTypeProduitConstructeurNull2EqualsSousTypeProduitConstructeurNull1);
			System.out.println("sousTypeProduitConstructeurNull1EqualsSousTypeProduitConstructeurNull1 : " + sousTypeProduitConstructeurNull1EqualsSousTypeProduitConstructeurNull1);
			System.out.println("sousTypeProduitConstructeurNull1HashCode : " + sousTypeProduitConstructeurNull1HashCode);
			System.out.println("sousTypeProduitConstructeurNull2HashCode : " + sousTypeProduitConstructeurNull2HashCode);
			System.out.println();
			
		}
			
		// ASSERT - THEN
		/* vérifie que toutes les propriétés de l'objet sont null. */
		assertNull(idSousTypeProduitConstructeurNull1, "l'ID de sousTypeProduitConstructeurNull1 doit être null : ");
		assertNull(idSousTypeProduitConstructeurNull2, "l'ID de sousTypeProduitConstructeurNull2 doit être null : ");
		
		assertNull(sousTypeProduitConstructeurNullString1, "sousTypeProduitConstructeurNullString1 doit être null : ");
		assertNull(sousTypeProduitConstructeurNullString2, "sousTypeProduitConstructeurNullString2 doit être null : ");
		
		assertNull(typeProduitNull1, "typeProduitNull1 doit être null : ");
		assertNull(typeProduitNull2, "typeProduitNull2 doit être null : ");
		
		assertTrue(produitsNull1.isEmpty(), "produitsNull1 doit être vide : ");
		assertTrue(produitsNull2.isEmpty(), "produitsNull2 doit être vide : ");
		
		/* vérifie que les Booleans valide sont à false. */
		assertFalse(valideSousTypeProduitConstructeurNull1, "valide doit être à false dans sousTypeProduitConstructeurNull1 : ");
		assertFalse(valideSousTypeProduitConstructeurNull2, "valide doit être à false dans sousTypeProduitConstructeurNull2 : ");
		
		/* vérifie que 2 instances créées avec le constructeur null sont différentes. */
		assertFalse(memeInstance1, "sousTypeProduitConstructeurNull1 == sousTypeProduitConstructeurNull2 doit retourner false : ");
		assertFalse(memeInstance2, "sousTypeProduitConstructeurNull2 == sousTypeProduitConstructeurNull1 doit retourner false : ");
		
		/* vérifie que 2 instances créées avec le constructeur null sont equals. */
		assertTrue(sousTypeProduitConstructeurNull1EqualsSousTypeProduitConstructeurNull2, "sousTypeProduitConstructeurNull1 doit être Equals() à sousTypeProduitConstructeurNull2 : ");
		assertTrue(sousTypeProduitConstructeurNull2EqualsSousTypeProduitConstructeurNull1, "sousTypeProduitConstructeurNull2 doit être Equals() à sousTypeProduitConstructeurNull1 : ");
		assertTrue(sousTypeProduitConstructeurNull1EqualsSousTypeProduitConstructeurNull1, "sousTypeProduitConstructeurNull1 doit être Equals() à sousTypeProduitConstructeurNull1 : ");
		assertEquals(sousTypeProduitConstructeurNull1HashCode, sousTypeProduitConstructeurNull2HashCode, "sousTypeProduitConstructeurNull1 doit avoir le même Hashcode que sousTypeProduitConstructeurNull2 : ");

		
		//**** ARRANGE - GIVEN
		// ACT - WHEN
		// INSTANCIATION - Utilisation des Setters
		sousTypeProduitConstructeurNull1.setIdSousTypeProduit(1L);
		sousTypeProduitConstructeurNull1.setSousTypeProduit(CANNE_A_PECHE);
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println("*** APRES sousTypeProduitConstructeurNull1.setIdSousTypeProduit(1L) et sousTypeProduitConstructeurNull1.setSousTypeProduit(\"canne à pêche\"); ***");
			this.afficher(sousTypeProduitConstructeurNull1);
		}
				
		// ASSERT - THEN
		/* vérifie que les setters fonctionnent correctement.*/
		assertEquals(1L, sousTypeProduitConstructeurNull1.getIdSousTypeProduit(), "l'ID de sousTypeProduitConstructeurNull1 doit valoir 1 à ce stade : ");
		assertEquals(CANNE_A_PECHE, sousTypeProduitConstructeurNull1.getSousTypeProduit(), "le sousTypeProduit de sousTypeProduitConstructeurNull1 doit valoir \"canne à pêche\" à ce stade : ");
		assertNull(sousTypeProduitConstructeurNull1.getTypeProduit(), "le type de produit de sousTypeProduitConstructeurNull1 doit être null à ce stade : ");
		assertTrue(sousTypeProduitConstructeurNull1.getProduits().isEmpty(), "la collection de produits de sousTypeProduitConstructeurNull1 doit être vide à ce stade : ");
		assertFalse(sousTypeProduitConstructeurNull1.isValide(), "sousTypeProduitConstructeurNull1 ne doit pas être valide à ce stade : ");
	
		
		//**** ARRANGE - GIVEN
		// ACT - WHEN
		// INSTANCIATION - Utilisation des Setters (suite)
		final TypeProduitI typeProduit1 = new TypeProduitJPA(10L, PECHE, null);
		sousTypeProduitConstructeurNull1.setTypeProduit(typeProduit1);
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("*** APRES TypeProduitI typeProduitPeche = new TypeProduitJPA(10L, \"Pêche\", null); ***");
			System.out.println("*** APRES sousTypeProduitConstructeurNull1.setTypeProduit(typeProduit1); ***");
			this.afficher(sousTypeProduitConstructeurNull1);
			this.afficherSousTypeProduitsDuTypeProduit(sousTypeProduitConstructeurNull1);
		}
		
		// ASSERT - THEN
		/* vérifie que les setters fonctionnent correctement.*/
		assertEquals(1L, sousTypeProduitConstructeurNull1.getIdSousTypeProduit(), "l'ID de sousTypeProduitConstructeurNull1 doit valoir 1 à ce stade : ");
		assertEquals(CANNE_A_PECHE, sousTypeProduitConstructeurNull1.getSousTypeProduit(), "le sousTypeProduit de sousTypeProduitConstructeurNull1 doit valoir \"canne à pêche\" à ce stade : ");
		assertEquals(typeProduit1, sousTypeProduitConstructeurNull1.getTypeProduit(), "Le typeProduit de sousTypeProduitConstructeurNull1 doit valoir typeProduit1 à ce stade : ");
		assertTrue(sousTypeProduitConstructeurNull1.getProduits().isEmpty(), "la collection de produits de sousTypeProduitConstructeurNull1 doit être vide à ce stade : ");
		assertTrue(sousTypeProduitConstructeurNull1.isValide(), "sousTypeProduitConstructeurNull1 doit être valide à ce stade : ");
		assertFalse(typeProduit1.getSousTypeProduits().isEmpty(), "la collection sousTypeProduits de typeProduit1 doit comporter au moins un élément à ce stade : ");
		
	} //___________________________________________________________________



	/**
	 * <div>
	* <p>Teste le constructeur d'arité 1.</p>
	 * <ul>
	 * <li>vérifie que toutes les propriétés de l'objet sont null sauf sousTypeProduit.</li>
	 * <li>vérifie que les Booleans valide sont à false.</li>
	 * <li>vérifie que 2 instances créées avec le constructeur d'arité 1 sont différentes.</li>
	 * <li>vérifie que 2 instances créées avec le constructeur d'arité 1 sont equals.</li>
	 * <li>vérifie que les setters fonctionnent correctement.</li>
	 * </ul>
	 * </div>
	*/
	@SuppressWarnings(UNUSED)
	@DisplayName("testSousTypeProduitString() : vérifie le comportement général du Constructeur d'arité 1")
	@Tag("constructeurs")
	@Test
	public final void testSousTypeProduitString() {
				
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE SousTypeProduitJPATest - méthode testSousTypeProduitString() ********** ");
			System.out.println("CE TEST VERIFIE LE FONCTIONNEMENT DU CONSTRUCTEUR D'ARITE 1.");
			System.out.println();				
		}
		
		//**** ARRANGE - GIVEN
		final TypeProduitI typeProduitCree1 = new TypeProduitJPA(10L, PECHE, null);
		final SousTypeProduitI sousTypeProduit1 = new SousTypeProduitJPA(CANNE_A_PECHE);
		final SousTypeProduitI sousTypeProduit2 = new SousTypeProduitJPA(MOULINET);
		
		// ACT - WHEN
		final Long idSousTypeProduit1 = sousTypeProduit1.getIdSousTypeProduit();
		final Long idSousTypeProduit2 = sousTypeProduit2.getIdSousTypeProduit();
		
		final String sousTypeProduitString1 = sousTypeProduit1.getSousTypeProduit();
		final String sousTypeProduitString2 = sousTypeProduit2.getSousTypeProduit();
		
		final TypeProduitI typeProduit1 = sousTypeProduit1.getTypeProduit();
		final TypeProduitI typeProduit2 = sousTypeProduit2.getTypeProduit();
		
		final List<? extends ProduitI> produits1 = sousTypeProduit1.getProduits();
		final List<? extends ProduitI> produits2 = sousTypeProduit2.getProduits();
		
		final boolean valideSousTypeProduit1 = sousTypeProduit1.isValide();
		final boolean valideSousTypeProduit2 = sousTypeProduit2.isValide();
		
		final int sousTypeProduit1HashCode = sousTypeProduit1.hashCode();
		final int sousTypeProduit2HashCode = sousTypeProduit2.hashCode();

		/* Vérifications */
		final boolean memeInstance1 = sousTypeProduit1 == sousTypeProduit2;
		final boolean memeInstance2 = sousTypeProduit2 == sousTypeProduit1;
		
		final boolean sousTypeProduit1EqualsSousTypeProduit2 
			= sousTypeProduit1.equals(sousTypeProduit2);
		final boolean sousTypeProduit2EqualsSousTypeProduit1 
			= sousTypeProduit2.equals(sousTypeProduit1);
		final boolean sousTypeProduit1EqualsSousTypeProduit1 
			= sousTypeProduit1.equals(sousTypeProduit1);

				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("*** SousTypeProduitI sousTypeProduit1 = new SousTypeProduitJPA(\"canne à pêche\"); *** ");
			System.out.println("*** SousTypeProduitI sousTypeProduit2 = new SousTypeProduitJPA(\"moulinet\"); *** ");
			System.out.println("idSousTypeProduit1 : " + idSousTypeProduit1);
			System.out.println("idSousTypeProduit2 : " + idSousTypeProduit2);
			System.out.println();
			System.out.println("sousTypeProduitString1 : " + sousTypeProduitString1);
			System.out.println("sousTypeProduitString2 : " + sousTypeProduitString2);
			System.out.println();
			System.out.println("typeProduit1 : " + typeProduit1);
			System.out.println("typeProduit2 : " + typeProduit2);
			System.out.println();
			System.out.println("produitsNull1 : " + produits1);
			System.out.println("produitsNull2 : " + produits2);
			System.out.println();
			System.out.println("valideSousTypeProduit1 : " + valideSousTypeProduit1);
			System.out.println("valideSousTypeProduit2 : " + valideSousTypeProduit2);
			System.out.println();
			System.out.println("sousTypeProduit1 == sousTypeProduit2 : " + memeInstance1);
			System.out.println("sousTypeProduit2 == sousTypeProduit1 : " + memeInstance2);
			System.out.println();
			System.out.println("sousTypeProduit1EqualsSousTypeProduit2 : " + sousTypeProduit1EqualsSousTypeProduit2);
			System.out.println("sousTypeProduit2EqualsSousTypeProduit1 : " + sousTypeProduit2EqualsSousTypeProduit1);
			System.out.println("sousTypeProduit1EqualsSousTypeProduit1 : " + sousTypeProduit1EqualsSousTypeProduit1);
			System.out.println("sousTypeProduit1HashCode : " + sousTypeProduit1HashCode);
			System.out.println("sousTypeProduit2HashCode : " + sousTypeProduit2HashCode);
			System.out.println();
			
		}
				
		// ASSERT - THEN
		/* vérifie les propriétés de l'objet. */
		assertNull(idSousTypeProduit1, "l'ID de sousTypeProduit1 doit être null : ");
		assertNull(idSousTypeProduit2, "l'ID de sousTypeProduit2 doit être null : ");
		
		assertEquals(CANNE_A_PECHE, sousTypeProduit1.getSousTypeProduit(), "sousTypeProduit1 doit valoir \"canne à pêche\" : ");
		assertEquals(MOULINET, sousTypeProduit2.getSousTypeProduit(), "sousTypeProduit2 doit valoir \"moulinet\" : ");

		assertNull(typeProduit1, "typeProduit1 doit être null à ce stade : ");
		assertNull(typeProduit2, "typeProduit2 doit être null à ce stade : ");

	} //___________________________________________________________________


	
	/**
	 * <div>
	 * <p>Vérifie la construction générale des objets</p>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testConstructeurs() : vérifie le comportement général des constructeurs et Setters")
	@Tag("constructeurs")
	@Test
	public final void testConstructeurs() {
				
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE SousTypeProduitJPATest - méthode testConstructeurs() ********** ");
			System.out.println("CE TEST VERIFIE LE FONCTIONNEMENT GENERAL DES CONSTRUCTEURS ET SETTERS.");
			System.out.println();				
		}

		//**** ARRANGE - GIVEN
		/* TypeProduitJPA. */
		final TypeProduitI typeProduitPeche = new TypeProduitJPA();
		final TypeProduitI typeProduitOutillage 
			= new TypeProduitJPA(2L, OUTILLAGE, null);
		
		/* SousTypeProduitJPA. */
		final SousTypeProduitI sousTypeProduitCanneAPeche 
			= new SousTypeProduitJPA();
		final SousTypeProduitI sousTypeProduitMoulinet 
			= new SousTypeProduitJPA(20L, MOULINET, typeProduitPeche, null);
		final SousTypeProduitI sousTypeProduitCuillere 
			= new SousTypeProduitJPA(30L, CUILLERE, typeProduitPeche, null);
		final SousTypeProduitI sousTypeProduitPerceuse 
			= new SousTypeProduitJPA(PERCEUSE);
		
		// ACT - WHEN
		/* TypeProduitJPA. */
		typeProduitPeche.setIdTypeProduit(1L);
		typeProduitPeche.setTypeProduit(PECHE);
		
		/* SousTypeProduitJPA. */
		sousTypeProduitCanneAPeche.setIdSousTypeProduit(10L);
		sousTypeProduitCanneAPeche.setSousTypeProduit(CANNE_A_PECHE);
		sousTypeProduitCanneAPeche.setTypeProduit(typeProduitPeche);
		sousTypeProduitPerceuse.setIdSousTypeProduit(40L);
		sousTypeProduitPerceuse.setTypeProduit(typeProduitOutillage);
		
		/* ID */
		final Long idSousTypeProduitCanneAPeche 
			= sousTypeProduitCanneAPeche.getIdSousTypeProduit();
		final Long idSousTypeProduitMoulinet 
			= sousTypeProduitMoulinet.getIdSousTypeProduit();
		final Long idSousTypeProduitCuillere 
			= sousTypeProduitCuillere.getIdSousTypeProduit();
		final Long idSousTypeProduitPerceuse 
			= sousTypeProduitPerceuse.getIdSousTypeProduit();
		
		/* SousTypeProduitI (String). */
		final String sousTypeProduitCanneAPecheString 
			= sousTypeProduitCanneAPeche.getSousTypeProduit();
		final String sousTypeProduitMoulinetString 
			= sousTypeProduitMoulinet.getSousTypeProduit();
		final String sousTypeProduitCuillereString 
			= sousTypeProduitCuillere.getSousTypeProduit();
		final String sousTypeProduitPerceuseString 
			= sousTypeProduitPerceuse.getSousTypeProduit();
		
		/* TypeProduitJPA. */
		final TypeProduitI typeProduitSousTypeProduitCanneAPeche 
			= sousTypeProduitCanneAPeche.getTypeProduit();
		final TypeProduitI typeProduitSousTypeProduitMoulinet 
			= sousTypeProduitMoulinet.getTypeProduit();
		final TypeProduitI typeProduitSousTypeProduitCuillere 
			= sousTypeProduitCuillere.getTypeProduit();
		final TypeProduitI typeProduitSousTypeProduitPerceuse 
			= sousTypeProduitPerceuse.getTypeProduit();

						
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("**** APRES Création de sousTypeProduitCanneAPeche, sousTypeProduitMoulinet, sousTypeProduitCuillere, sousTypeProduitPerceuse *****");
			System.out.println(SOUSP_CANNA);
			this.afficher(sousTypeProduitCanneAPeche);
			this.afficherSousTypeProduitsDuTypeProduit(sousTypeProduitCanneAPeche);
			System.out.println();
			System.out.println(SOUSP_MOULINET);
			this.afficher(sousTypeProduitMoulinet);
			this.afficherSousTypeProduitsDuTypeProduit(sousTypeProduitMoulinet);
			System.out.println();
			System.out.println(SOUSP_CUILLERE);
			this.afficher(sousTypeProduitCuillere);
			this.afficherSousTypeProduitsDuTypeProduit(sousTypeProduitCuillere);
			System.out.println();
			System.out.println(SOUSP_PERCEUSE);
			this.afficher(sousTypeProduitPerceuse);
			this.afficherSousTypeProduitsDuTypeProduit(sousTypeProduitPerceuse);
			System.out.println();
			System.out.println(STP_PECHE);
			this.afficherSousTypeProduitsDansTypeProduit(typeProduitPeche);
			System.out.println();
			System.out.println(STP_OUTILLAGE);
			this.afficherSousTypeProduitsDansTypeProduit(typeProduitOutillage);
			System.out.println();
		
		}
		
		// ASSERT - THEN
		/* vérifie les propriétés des objets. */
		/* ID */
		assertNotNull(idSousTypeProduitCanneAPeche, "l'ID de sousTypeProduitCanneAPeche ne doit pas être null : ");
		assertNotNull(idSousTypeProduitMoulinet, "l'ID de sousTypeProduitMoulinet ne doit pas être null : ");
		assertNotNull(idSousTypeProduitCuillere, "l'ID de sousTypeProduitCuillere ne doit pas être null : ");
		assertNotNull(idSousTypeProduitPerceuse, "l'ID de sousTypeProduitPerceuse ne doit pas être null : ");
		/* SousTypeProduitI (String). */
		assertNotNull(sousTypeProduitCanneAPecheString, "sousTypeProduitCanneAPecheString ne doit pas être null : ");
		assertNotNull(sousTypeProduitMoulinetString, "sousTypeProduitMoulinetString ne doit pas être null : ");
		assertNotNull(sousTypeProduitCuillereString, "sousTypeProduitCuillereString ne doit pas être null : ");
		assertNotNull(sousTypeProduitPerceuseString, "sousTypeProduitPerceuseString ne doit pas être null : ");
		/* TypeProduitJPA. */
		assertSame(typeProduitPeche, typeProduitSousTypeProduitCanneAPeche, "TypeProduitI dans sousTypeProduitCanneAPeche doit être typeProduitPeche : ");
		assertSame(typeProduitPeche, typeProduitSousTypeProduitMoulinet, "TypeProduitI dans sousTypeProduitMoulinet doit être typeProduitPeche : ");
		assertSame(typeProduitPeche, typeProduitSousTypeProduitCuillere, "TypeProduitI dans sousTypeProduitCuillere doit être typeProduitPeche : ");
		assertSame(typeProduitOutillage, typeProduitSousTypeProduitPerceuse, "TypeProduitI dans sousTypeProduitPerceuse doit être typeProduitOutillage : ");
		/* listes sousTypeProduits. */
		assertFalse(typeProduitPeche.getSousTypeProduits().isEmpty(), "la Collection sousTypeProduits de typeProduitPeche ne doit pas être vide : ");
		assertTrue(typeProduitPeche.getSousTypeProduits().size() == 3, "la Collection sousTypeProduits de typeProduitPeche doit contenir 3 éléments : ");
		assertFalse(typeProduitOutillage.getSousTypeProduits().isEmpty(), "la Collection sousTypeProduits de typeProduitOutillage ne doit pas être vide : ");
		assertTrue(typeProduitOutillage.getSousTypeProduits().size() == 1, "la Collection sousTypeProduits de typeProduitOutillage doit contenir 1 élément : ");


		//**** ARRANGE - GIVEN
		/* Scénario Modification du TypeProduitI d'un SousTypeProduitJPA. */
		sousTypeProduitPerceuse.setTypeProduit(typeProduitPeche);

				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("**** APRES sousTypeProduitPerceuse.setTypeProduit(typeProduitPeche); *****");
			System.out.println(SOUSP_CANNA);
			this.afficher(sousTypeProduitCanneAPeche);
			this.afficherSousTypeProduitsDuTypeProduit(sousTypeProduitCanneAPeche);
			System.out.println();
			System.out.println(SOUSP_MOULINET);
			this.afficher(sousTypeProduitMoulinet);
			this.afficherSousTypeProduitsDuTypeProduit(sousTypeProduitMoulinet);
			System.out.println();
			System.out.println(SOUSP_CUILLERE);
			this.afficher(sousTypeProduitCuillere);
			this.afficherSousTypeProduitsDuTypeProduit(sousTypeProduitCuillere);
			System.out.println();
			System.out.println(SOUSP_PERCEUSE);
			this.afficher(sousTypeProduitPerceuse);
			this.afficherSousTypeProduitsDuTypeProduit(sousTypeProduitPerceuse);
			System.out.println();
			System.out.println(STP_PECHE);
			this.afficherSousTypeProduitsDansTypeProduit(typeProduitPeche);
			System.out.println();
			System.out.println(STP_OUTILLAGE);
			this.afficherSousTypeProduitsDansTypeProduit(typeProduitOutillage);
			System.out.println();
		
		}

		
		// ASSERT - THEN
		/* vérifie les propriétés des objets. */
		/* ID */
		assertNotNull(idSousTypeProduitCanneAPeche, "l'ID de sousTypeProduitCanneAPeche ne doit pas être null : ");
		assertNotNull(idSousTypeProduitMoulinet, "l'ID de sousTypeProduitMoulinet ne doit pas être null : ");
		assertNotNull(idSousTypeProduitCuillere, "l'ID de sousTypeProduitCuillere ne doit pas être null : ");
		assertNotNull(idSousTypeProduitPerceuse, "l'ID de sousTypeProduitPerceuse ne doit pas être null : ");
		/* SousTypeProduitI (String). */
		assertNotNull(sousTypeProduitCanneAPecheString, "sousTypeProduitCanneAPecheString ne doit pas être null : ");
		assertNotNull(sousTypeProduitMoulinetString, "sousTypeProduitMoulinetString ne doit pas être null : ");
		assertNotNull(sousTypeProduitCuillereString, "sousTypeProduitCuillereString ne doit pas être null : ");
		assertNotNull(sousTypeProduitPerceuseString, "sousTypeProduitPerceuseString ne doit pas être null : ");
		/* TypeProduitJPA. */
		assertSame(typeProduitPeche, typeProduitSousTypeProduitCanneAPeche, "TypeProduitI dans sousTypeProduitCanneAPeche doit être typeProduitPeche : ");
		assertSame(typeProduitPeche, typeProduitSousTypeProduitMoulinet, "TypeProduitI dans sousTypeProduitMoulinet doit être typeProduitPeche : ");
		assertSame(typeProduitPeche, typeProduitSousTypeProduitCuillere, "TypeProduitI dans sousTypeProduitCuillere doit être typeProduitPeche : ");
		assertSame(typeProduitOutillage, typeProduitSousTypeProduitPerceuse, "TypeProduitI dans sousTypeProduitPerceuse doit être typeProduitOutillage : ");
		/* listes sousTypeProduits. */
		assertFalse(typeProduitPeche.getSousTypeProduits().isEmpty(), "la Collection sousTypeProduits de typeProduitPeche ne doit pas être vide : ");
		assertTrue(typeProduitPeche.getSousTypeProduits().size() == 4, "la Collection sousTypeProduits de typeProduitPeche doit contenir 4 éléments : ");
		assertTrue(typeProduitOutillage.getSousTypeProduits().isEmpty(), "la Collection sousTypeProduits de typeProduitOutillage doit être vide : ");
		
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
			System.out.println("********** CLASSE SousTypeProduitJPATest - méthode testEquals() ********** ");
			System.out.println("CE TEST VERIFIE LE RESPECT DU CONTRAT Java Equals() et Hashcode().");
			System.out.println();				
		}
		
		//*** ARRANGE - GIVEN
		/* TypeProduitI */
		final TypeProduitI typeProduit1 = new TypeProduitJPA(PECHE);
		final TypeProduitI typeProduitEquals1 = new TypeProduitJPA(PECHE);
		final TypeProduitI typeProduitDifferent1 = new TypeProduitJPA(OUTILLAGE);
		/* SousTypeProduitJPA. */
		final SousTypeProduitI objet1 
			= new SousTypeProduitJPA(10L, CANNE_A_PECHE, typeProduit1, null);
		final SousTypeProduitI objetEquals1 
			= new SousTypeProduitJPA(10L, CANNE_A_PECHE, typeProduit1, null);
		final SousTypeProduitI objetDeepEquals1 
			= new SousTypeProduitJPA(10L, CANNE_A_PECHE, typeProduitEquals1, null);
		final SousTypeProduitI objetPasEqualsObjet1 
			= new SousTypeProduitJPA(40L, MOULINET, typeProduit1, null);
		final SousTypeProduitI objetPasEqualsObjetDeep1 
			= new SousTypeProduitJPA(20L, CANNE_A_PECHE, typeProduitDifferent1, null);
		final SousTypeProduitI objetPasEqualsObjetDeep2 
			= new SousTypeProduitJPA(30L, PERCEUSE, typeProduitDifferent1, null);
		
		/* Mauvaise instance. */
		final NullPointerException mauvaiseInstance = new NullPointerException();
		
		// ACT - WHEN
		typeProduit1.setIdTypeProduit(1L);
		typeProduitEquals1.setIdTypeProduit(2L);
		typeProduitDifferent1.setIdTypeProduit(3L);
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("***** objet1 ******");
			this.afficher(objet1);
			this.afficherSousTypeProduitsDuTypeProduit(objet1);
			System.out.println();
			System.out.println("***** objetEquals1 *****");
			this.afficher(objetEquals1);
			this.afficherSousTypeProduitsDuTypeProduit(objetEquals1);
			System.out.println();
			System.out.println("***** objetDeepEquals1 ******");
			this.afficher(objetDeepEquals1);
			this.afficherSousTypeProduitsDuTypeProduit(objetDeepEquals1);
			System.out.println();
			System.out.println("***** objetPasEqualsObjet1 ******");
			this.afficher(objetPasEqualsObjet1);
			this.afficherSousTypeProduitsDuTypeProduit(objetPasEqualsObjet1);
			System.out.println();
			System.out.println("***** objetPasEqualsObjetDeep1 ******");
			this.afficher(objetPasEqualsObjetDeep1);
			this.afficherSousTypeProduitsDuTypeProduit(objetPasEqualsObjetDeep1);
			System.out.println();
			System.out.println("***** objetPasEqualsObjetDeep2 ******");
			this.afficher(objetPasEqualsObjetDeep2);
			this.afficherSousTypeProduitsDuTypeProduit(objetPasEqualsObjetDeep2);
			System.out.println();

		}

		// ASSERT - THEN
		/* garantit que x.equals(mauvaise instance) retourne false. */
		assertFalse(objet1.equals(mauvaiseInstance), "x.equals(mauvaise instance) doit retourner false : ");
		
		/* garantit le contrat Java reflexif x.equals(x). */
		assertEquals(objet1, objet1, "x.equals(x) : ");
		
		/* garantit le contrat Java symétrique 
		 * x.equals(y) ----> y.equals(x). */
		assertNotSame(objet1, objetEquals1, "objet1 et objetEquals1 ne sont pas la même instance : ");
		assertEquals(objet1, objetEquals1, "objet1.equals(objetEquals1) : ");
		assertEquals(objetEquals1, objet1, "objetEquals1.equals(objet1) : ");
		
		/* garantit le contrat Java transitif 
		 * x.equals(y) et y.equals(z) ----> x.equals(z). */
		assertEquals(objet1, objetEquals1, "objet1.equals(objetEquals1) : ");
		assertEquals(objetEquals1, objetDeepEquals1, "objetEquals1.equals(objetDeepEquals1) : ");
		assertEquals(objet1, objetDeepEquals1, "objet1.equals(objetDeepEquals1) : ");
		
		/* garantit le contrat Java sur les hashcode 
		 * x.equals(y) ----> x.hashcode() == y.hashcode(). */
		assertEquals(objet1.hashCode(), objetEquals1.hashCode(), "objet1.hashCode().equals(objetEquals1.hashCode()) : ");

		
		//*** ARRANGE - GIVEN
		final SousTypeProduitI objetConstructeurNull1 = new SousTypeProduitJPA();
		final SousTypeProduitI objetConstructeurNull2 = new SousTypeProduitJPA();
		
		final SousTypeProduitI objetAvecValeursNull1 = new SousTypeProduitJPA(1L, null, typeProduit1, null);
		final SousTypeProduitI objetAvecValeursNull2 = new SousTypeProduitJPA(1L, null, typeProduitEquals1, null);

		final SousTypeProduitI objetAvecValeursVide1 = new SousTypeProduitJPA(1L, "", typeProduit1, null);
		final SousTypeProduitI objetAvecValeursVide2 = new SousTypeProduitJPA(1L, "", typeProduitEquals1, null);
		
		final SousTypeProduitI objetAvecSousValeursNull1 = new SousTypeProduitJPA(1L, CANNE_A_PECHE, null, null);
		final SousTypeProduitI objetAvecSousValeursNull2 = new SousTypeProduitJPA(1L, CANNE_A_PECHE, null, null);

		// ACT - WHEN
		
		// ASSERT - THEN
		/* garantit que les null sont bien gérés dans equals(...). */
		assertEquals(objetConstructeurNull1, objetConstructeurNull2, "objetConstructeurNull1.equals(objetConstructeurNull2) : ");
		assertEquals(objetConstructeurNull1.hashCode(), objetConstructeurNull2.hashCode(), "objetConstructeurNull1.hashcode() == objetConstructeurNull2.hashcode()) : ");
		
		assertEquals(objetAvecValeursNull1, objetAvecValeursNull2, "objetAvecValeursNull1.equals(objetAvecValeursNull2) : ");
		assertEquals(objetAvecValeursVide1, objetAvecValeursVide2, "objetAvecValeursVide1.equals(objetAvecValeursVide2) : ");
		assertEquals(objetAvecSousValeursNull1, objetAvecSousValeursNull2, "objetAvecSousValeursNull1.equals(objetAvecSousValeursNull2) : ");

		//*** ARRANGE - GIVEN
		
		// ASSERT - THEN
		/* garantit que x.equals(null) retourne false (avec x non null). */
		assertNotNull(objet1, "objet1.equals(null) retourne false (avec objet1 non null) : ");
		
		/* garantit le bon fonctionnement de equals() 
		 * en cas d'égalité métier. */
		assertEquals(objet1, objetDeepEquals1, "objet1.equals(objetDeepEquals1) : ");
		
		/* garantit le bon fonctionnement de equals() 
		 * en cas d'inégalité métier. */
		assertNotEquals(objet1, objetPasEqualsObjet1, "objet1 n'est pas equals() avec objetPasEqualsObjet1 : ");
		assertNotEquals(objet1, objetPasEqualsObjetDeep1, "objet1 n'est pas equals() avec objetPasEqualsObjetDeep1 : ");
		assertNotEquals(objet1, objetPasEqualsObjetDeep2, "objet1 n'est pas equals() avec objetPasEqualsObjetDeep2 : ");
		
	} //___________________________________________________________________
	/**
	 * <div>
	 * <ul>
	 * <p>Teste l'égalité métier <b>case-insensitive</b> sur le libellé <b>sousTypeProduit</b> :</p>
	 * <li>force le fallback métier (IDs SousTypeProduit null).</li>
	 * <li>utilise le fallback sur l'ID du parent TypeProduit (IDs parent non null).</li>
	 * <li>vérifie equals() et la cohérence hashCode() en case-insensitive.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testEqualsCaseInsensitiveSurLibelle() : vérifie equals/hashCode case-insensitive sur sousTypeProduit")
	@Tag("equals")
	@Test
	public final void testEqualsCaseInsensitiveSurLibelle() {

		// ARRANGE - GIVEN
		final TypeProduitJPA parent1 = new TypeProduitJPA(PECHE);
		final TypeProduitJPA parent2 = new TypeProduitJPA(PECHE);

		/* Force le fallback sur l'ID du parent (et pas sur l'ID technique du sous-type). */
		parent1.setIdTypeProduit(1L);
		parent2.setIdTypeProduit(1L);

		final String libelleMinuscule = CANNE_A_PECHE;
		final String libelleMajuscule = "CANNE À PÊCHE";

		final SousTypeProduitI stp1
			= new SousTypeProduitJPA(null, libelleMinuscule, parent1, null);
		final SousTypeProduitI stp2
			= new SousTypeProduitJPA(null, libelleMajuscule, parent2, null);

		// ACT - WHEN
		final boolean equals12 = stp1.equals(stp2);
		final boolean equals21 = stp2.equals(stp1);
		final int hash1 = stp1.hashCode();
		final int hash2 = stp2.hashCode();

		// ASSERT - THEN
		assertTrue(equals12, "stp1.equals(stp2) doit retourner true (case-insensitive) : ");
		assertTrue(equals21, "stp2.equals(stp1) doit retourner true (case-insensitive) : ");
		assertEquals(hash1, hash2, "stp1 et stp2 doivent avoir le même hashCode (case-insensitive) : ");

	} //___________________________________________________________________


	/**
	 * <div>
	 * <ul>
	 * <p>Teste le fallback final de equals() (sans IDs) en <b>case-insensitive</b> :</p>
	 * <li>IDs SousTypeProduit null.</li>
	 * <li>IDs TypeProduit parent null.</li>
	 * <li>même instance de parent pour éviter toute ambiguïté sur equals(parent).</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testEqualsFallbackFinalCaseInsensitive() : vérifie le fallback final (sans IDs) en case-insensitive")
	@Tag("equals")
	@Test
	public final void testEqualsFallbackFinalCaseInsensitive() {

		// ARRANGE - GIVEN
		final TypeProduitJPA parent = new TypeProduitJPA(PECHE);

		/* Force l'absence d'IDs parent pour aller sur le fallback final. */
		parent.setIdTypeProduit(null);

		final SousTypeProduitI stp1
			= new SousTypeProduitJPA(null, CANNE_A_PECHE, parent, null);
		final SousTypeProduitI stp2
			= new SousTypeProduitJPA(null, "CANNE À PÊCHE", parent, null);

		// ACT - WHEN
		final boolean equals12 = stp1.equals(stp2);

		// ASSERT - THEN
		assertTrue(equals12, "stp1.equals(stp2) doit retourner true (fallback final case-insensitive) : ");
		assertEquals(stp1.hashCode(), stp2.hashCode(), "hashCode doit rester cohérent avec equals (fallback final case-insensitive) : ");

	} //___________________________________________________________________


	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste <b>compareTo()</b> en cohérence avec equals() en <b>case-insensitive</b> :</p>
	 * <li>stp1.equals(stp2) implique stp1.compareTo(stp2) == 0.</li>
	 * <li>utilise des libellés identiques à la casse près.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testCompareToCaseInsensitive() : vérifie compareTo() cohérent avec equals() en case-insensitive")
	@Tag("compareTo")
	@Test
	public final void testCompareToCaseInsensitive() {

		// ARRANGE - GIVEN
		final TypeProduitJPA parent = new TypeProduitJPA(PECHE);
		parent.setIdTypeProduit(1L);

		final SousTypeProduitJPA stp1
			= new SousTypeProduitJPA(null, CANNE_A_PECHE, parent, null);
		final SousTypeProduitJPA stp2
			= new SousTypeProduitJPA(null, "CANNE À PÊCHE", parent, null);

		// ACT - WHEN
		final boolean equals12 = stp1.equals(stp2);
		final int compare12 = stp1.compareTo(stp2);

		// ASSERT - THEN
		assertTrue(equals12, "Pré-condition : stp1.equals(stp2) doit être true : ");
		assertTrue(compare12 == 0, "Contrat : stp1.equals(stp2) ---> stp1.compareTo(stp2) == 0 : ");

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
			System.out.println("********** CLASSE SousTypeProduitJPATest - méthode testToString() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode toString().");
			System.out.println();				
		}
		
		//**** ARRANGE - GIVEN
		final SousTypeProduitI objetConstructeurNull = new SousTypeProduitJPA();
						
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("***** objetConstructeurNull ******");
			this.afficher(objetConstructeurNull);
			this.afficherSousTypeProduitsDuTypeProduit(objetConstructeurNull);
			System.out.println();
			System.out.println("objetConstructeurNull.toString() : " + objetConstructeurNull.toString());
			System.out.println();
		}
		
		// ACT - WHEN
		
		// ASSERT - THEN
		/* garantit le bon fonctionnement avec les null. */
		assertEquals("SousTypeProduit [idSousTypeProduit=null, sousTypeProduit=null, typeProduit=null]", objetConstructeurNull.toString(), "doit afficher SousTypeProduit [idSousTypeProduit=null, sousTypeProduit=null, typeProduit=null] : ");

		
		//**** ARRANGE - GIVEN
		final TypeProduitI typeProduit1 = new TypeProduitJPA(PECHE);
		final SousTypeProduitI objet1 = new SousTypeProduitJPA(1L, MOULINET, typeProduit1, null);
		
		
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
		final String resultat = "SousTypeProduit [idSousTypeProduit=1"
				+ ", sousTypeProduit=moulinet, "
				+ "typeProduit=TypeProduitJPA "
				+ "[idTypeProduit=null, typeProduit=Pêche]]";
				
		// ASSERT - THEN
		/* garantit le bon fonctionnement de toString() */
		assertEquals(resultat, objet1.toString(), "doit afficher toString() : ");

	} //___________________________________________________________________

	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>compareTo(ProduitJPA pObject)</b> :</p>
	 * <li>garantit que compareTo(memeInstance) retourne 0.</li>
	 * <li>garantit que compareTo(null) retourne un nombre négatif.</li>
	 * <li>garantit le Contrat Java : 
	 * x.equals(y) ---> x.compareTo(y) == 0.</li>
	 * <li>garantit que les null sont bien gérés 
	 * dans compareTo(ProduitJPA pObject).</li>
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
			System.out.println("********** CLASSE SousTypeProduitJPATest - méthode testCompareTo() ********** ");
			System.out.println("CE TEST VERIFIE LE RESPECT des contrats Java de la méthode compareTo().");
			System.out.println();				
		}
		
		// ARRANGE - GIVEN
		/* TypeProduitI */
		final TypeProduitI typeProduit1 = new TypeProduitJPA(PECHE);
		final TypeProduitI typeProduitEquals1 = new TypeProduitJPA(PECHE);
		final TypeProduitI typeProduitAvant1 = new TypeProduitJPA(OUTILLAGE);
		final TypeProduitI typeProduitApres1 = new TypeProduitJPA("Restauration");
		/* SousTypeProduitJPA. */
		final SousTypeProduitI objet1 
			= new SousTypeProduitJPA(1L, CANNE_A_PECHE, typeProduit1, null);
		final SousTypeProduitI objetEqualsObjet1 
			= new SousTypeProduitJPA(1L, CANNE_A_PECHE, typeProduit1, null);
		final SousTypeProduitI objet1MemeInstance = objet1;
		final SousTypeProduitI objet2ApresObjet1 
			= new SousTypeProduitJPA(3L, MOULINET, typeProduitEquals1, null);
		final SousTypeProduitI objet3AvantDeepObjet1 
			= new SousTypeProduitJPA(4L, CANNE_A_PECHE, typeProduitAvant1, null);
			
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("***** objet1 ******");
			this.afficher(objet1);
			this.afficherSousTypeProduitsDuTypeProduit(objet1);
			System.out.println();
			System.out.println("***** objetEqualsObjet1 *****");
			this.afficher(objetEqualsObjet1);
			this.afficherSousTypeProduitsDuTypeProduit(objetEqualsObjet1);
			System.out.println();
			System.out.println("***** objet1MemeInstance ******");
			this.afficher(objet1MemeInstance);
			this.afficherSousTypeProduitsDuTypeProduit(objet1MemeInstance);
			System.out.println();
			System.out.println("***** objet2ApresObjet1 ******");
			this.afficher(objet2ApresObjet1);
			this.afficherSousTypeProduitsDuTypeProduit(objet2ApresObjet1);
			System.out.println();
			System.out.println("***** objet3AvantDeepObjet1 ******");
			this.afficher(objet3AvantDeepObjet1);
			this.afficherSousTypeProduitsDuTypeProduit(objet3AvantDeepObjet1);
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
		final SousTypeProduitI objetConstructeurNull1 = new SousTypeProduitJPA();
		final SousTypeProduitI objetConstructeurNull2 = new SousTypeProduitJPA();
		
		final SousTypeProduitI objetAvecValeursNull1 = new SousTypeProduitJPA(1L, null, typeProduit1, null);
		final SousTypeProduitI objetAvecValeursNullDeepEquals = new SousTypeProduitJPA(2L, null, typeProduitEquals1, null);
		final SousTypeProduitI objetAvecValeursNull2Apres1 = new SousTypeProduitJPA(3L, null, typeProduitApres1, null);

		final SousTypeProduitI objetAvecValeursVide1 = new SousTypeProduitJPA(1L, "", typeProduit1, null);
		final SousTypeProduitI objetAvecValeursVide2 = new SousTypeProduitJPA(2L, "", typeProduitApres1, null);
		
		final SousTypeProduitI objetAvecSousValeursNull1 = new SousTypeProduitJPA(1L, CANNE_A_PECHE, null, null);
		final SousTypeProduitI objetAvecSousValeursNull2 = new SousTypeProduitJPA(2L, CANNE_A_PECHE, null, null);

		// ACT - WHEN
		final int compareToConstructeurNull 
			= objetConstructeurNull1.compareTo(objetConstructeurNull2);
		final int compareToAvecValeursNullDeep 
			= objetAvecValeursNull1.compareTo(objetAvecValeursNullDeepEquals);
		final int compareToAvecValeursNull 
			= objetAvecValeursNull1.compareTo(objetAvecValeursNull2Apres1);		
		final int compareToAvecValeursVides 
			= objetAvecValeursVide1.compareTo(objetAvecValeursVide2);
		final int compareToAvecSousValeursNull 
			= objetAvecSousValeursNull1.compareTo(objetAvecSousValeursNull2);
		
		// ASSERT - THEN
		/* garantit que les null sont bien gérés dans compareTo(). */
		assertTrue(compareToConstructeurNull == 0, "objetConstructeurNull1.compareTo(objetConstructeurNull2) == 0 : ");
		assertTrue(compareToAvecValeursNullDeep == 0, "objetConstructeurNull1.compareTo(objetAvecValeursNullDeepEquals) == 0 : ");
		assertTrue(compareToAvecValeursNull < 0, "objetAvecValeursNull1.compareTo(objetAvecValeursNull2Apres1) < 0 : ");
		assertTrue(compareToAvecValeursVides < 0, "objetAvecValeursVide1.compareTo(objetAvecValeursVide2)  < 0 : ");
		assertTrue(compareToAvecSousValeursNull == 0, "objetAvecSousValeursNull1.compareTo(objetAvecSousValeursNull2) == 0 : ");

		
		//*** ARRANGE - GIVEN
		final SousTypeProduitI objetAvecV1Null = new SousTypeProduitJPA(CANNE_A_PECHE, null);
		final SousTypeProduitI objetAvecV1PasNull = new SousTypeProduitJPA(null, typeProduit1);
		final SousTypeProduitI objetAvecV1PasNullAvantObjet2 = new SousTypeProduitJPA(null, typeProduitAvant1);
		final SousTypeProduitI objetAvecV1PasNullApresObjet1 = new SousTypeProduitJPA(null, typeProduit1);
		final SousTypeProduitI objetAvecV2PasNullAvantObjet2 = new SousTypeProduitJPA("aaa", typeProduit1);
		final SousTypeProduitI objetAvecV2PasNullApresObjet1 = new SousTypeProduitJPA("zzz", typeProduit1);
		final SousTypeProduitI objetAvecV2PasNullIdemObjet2 = new SousTypeProduitJPA("idem", typeProduit1);
		final SousTypeProduitI objetAvecV2PasNullIdemObjet1 = new SousTypeProduitJPA("idem", typeProduit1);
		final SousTypeProduitI objetAvecV2Null1 = new SousTypeProduitJPA(null, typeProduitApres1);
		final SousTypeProduitI objetAvecV2Null2 = new SousTypeProduitJPA(null, typeProduitApres1);
		
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

		assertTrue(objet1.compareTo(objet3AvantDeepObjet1) > 0, "objet1.compareTo(objet3AvantDeepObjet1) doit retourner > 0 : ");
		assertTrue(objet1.compareTo(objet2ApresObjet1) < 0, "objet1.compareTo(objet2ApresObjet1) doit retourner < 0 : ");
		
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
			System.out.println("********** CLASSE SousTypeProduitJPATest - méthode testClone() ********** ");
			System.out.println("CE TEST VERIFIE LE RESPECT des contrats Java de la méthode clone().");
			System.out.println();				
		}

		//***** ARRANGE - GIVEN
		final SousTypeProduitJPA objetConstructeurNull = new SousTypeProduitJPA();
		

		// ACT - WHEN
		final SousTypeProduitJPA objetConstructeurNullClone 
			= objetConstructeurNull.clone();
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("******* Test d'un objet construit avec le constructeur d'arité nulle et de son clone *******");
			System.out.println("*** objetConstructeurNull ****");
			this.afficher(objetConstructeurNull);
			this.afficherSousTypeProduitsDuTypeProduit(objetConstructeurNull);
			System.out.println();
			System.out.println("*** objetConstructeurNullClone ****");
			this.afficher(objetConstructeurNullClone);
			this.afficherSousTypeProduitsDuTypeProduit(objetConstructeurNullClone);
			System.out.println();
		}
				
		// ASSERT - THEN
		/* garantit que les null sont bien gérés dans clone(). */
		assertFalse(objetConstructeurNull == objetConstructeurNullClone, "objetConstructeurNull et objetConstructeurNullClone ne doivent pas être la même instance : ");
		assertEquals(objetConstructeurNull, objetConstructeurNullClone, "objetConstructeurNull doit être equals() à objetConstructeurNullClone : ");
		assertEquals(objetConstructeurNull.getClass(), objetConstructeurNullClone.getClass(), "objetConstructeurNull.getClass() doit être equals() à objetConstructeurNullClone.getClass() : ");

		//***** ARRANGE - GIVEN
		/* TypeProduitI */
		final TypeProduitI typeProduit1 = new TypeProduitJPA(PECHE);
		/* SousTypeProduitJPA. */
		final SousTypeProduitJPA objet1 
			= new SousTypeProduitJPA(1L, CANNE_A_PECHE, typeProduit1, null);
				
		// ACT - WHEN
		final SousTypeProduitJPA objet1Clone = objet1.clone();
						
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("******* Test d'un objet construit avec le constructeur complet et de son clone *******");
			System.out.println("*** objet1 ****");
			this.afficher(objet1);
			this.afficherSousTypeProduitsDuTypeProduit(objet1);
			System.out.println();
			System.out.println("*** objet1Clone ****");
			this.afficher(objet1Clone);
			this.afficherSousTypeProduitsDuTypeProduit(objet1Clone);
			System.out.println();
		}
		
		//***** ARRANGE - GIVEN
		/* Modification du Clone pour vérifier que l'objet 
		 * initial n'est pas modifié.*/
		final TypeProduitI typeProduitClone 
			= new TypeProduitJPA(100L, "Type produit clone modifié", null);
		objet1Clone.setIdSousTypeProduit(2L);		
		objet1Clone.setSousTypeProduit(CLONE_MODIFIE);
		objet1Clone.setTypeProduit(typeProduitClone);
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("******* Test d'un objet après modification du Clone *******");
			System.out.println("*** objet1 ****");
			this.afficher(objet1);
			this.afficherSousTypeProduitsDuTypeProduit(objet1);
			System.out.println();
			System.out.println("*** objet1Clone ****");
			this.afficher(objet1Clone);
			this.afficherSousTypeProduitsDuTypeProduit(objet1Clone);
			System.out.println();
		}
		
		// ACT - WHEN

		/* garantit le clonage profond des propriétés. */
		/* La modification des propriétés du clone ne doit pas modifier les propriétés de l'objet cloné. */
		// ASSERT - THEN
		assertNotSame(objet1, objet1Clone, "objet1 et objet1Clone ne doivent pas être les mêmes instances : ");
		assertFalse(objet1.getIdSousTypeProduit() == objet1Clone.getIdSousTypeProduit(), "la modification de l'ID dans le clone ne doit pas modifier l'ID dans l'objet cloné : ");
		assertFalse(objet1.getTypeProduit().equals(objet1Clone.getTypeProduit()), "la modification du TypeProduitI dans le clone ne doit pas modifier le TypeProduitI dans l'objet cloné : ");
		assertFalse(objet1.getSousTypeProduit().equals(objet1Clone.getSousTypeProduit()), "la modification du sousTypeProduit dans le clone ne doit pas modifier le sousTypeProduit dans l'objet cloné : ");
		
	} //___________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>teste la méthode <span style= "font-weight : bold">getEnTeteCsv()</span></p>
	 * <p>Garantit que enTeteCsv() retourne 
	 * "idSousTypeProduit;type de produit;sous-type de produit;"</p>
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
			System.out.println("********** CLASSE SousTypeProduitJPATest - méthode testGetEnTeteCsv() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode getEnTeteCsv().");
			System.out.println();				
		}
		
		//**** ARRANGE - GIVEN
		final SousTypeProduitI objetConstructeurNull = new SousTypeProduitJPA();
		
		// ACT - WHEN
		final String enTeteCsv = objetConstructeurNull.getEnTeteCsv();
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("enTeteCsv : " + enTeteCsv);
			System.out.println();
		}
		
		final String enTeteCsvPrevue 
			= "idSousTypeProduit;type de produit;sous-type de produit;";
		
		// ASSERT - THEN
		assertEquals(enTeteCsvPrevue, enTeteCsv, "enTeteCsv doit retourner \"idSousTypeProduit;type de produit;sous-type de produit;\" : ");
		
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
			System.out.println("********** CLASSE SousTypeProduitJPATest - méthode testToStringCsv() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode toStringCsv().");
			System.out.println();				
		}
			
		// *** ARRANGE - GIVEN
		final SousTypeProduitI objetConstructeurNull = new SousTypeProduitJPA();
		
		// ACT - WHEN
		final String toStringCsvNull = objetConstructeurNull.toStringCsv();
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("getEnTeteCsv : " + objetConstructeurNull.getEnTeteCsv());
			System.out.println("toStringCsvNull : " + toStringCsvNull);
			System.out.println();
		}

		final String toStringCsvPrevueNull = "null;null;null;";
				
		// ASSERT - THEN
		assertEquals(toStringCsvPrevueNull, toStringCsvNull, "toStringCsv doit retourner \"null;null;null;\" : ");

		
		// *** ARRANGE - GIVEN
		final TypeProduitI typeProduit1 = new TypeProduitJPA(PECHE);
		final SousTypeProduitI objet1 = new SousTypeProduitJPA(1L, MOULINET, typeProduit1, null);
		
		// ACT - WHEN
		final String toStringCsv = objet1.toStringCsv();
						
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("getEnTeteCsv : " + objet1.getEnTeteCsv());
			System.out.println("toStringCsv : " + toStringCsv);
			System.out.println();
		}
		
		final String toStringCsvPrevue = "1;Pêche;moulinet;";

		// ASSERT - THEN
		assertEquals(toStringCsvPrevue, toStringCsv, "toStringCsv doit retourner \"1;Pêche;moulinet;\" : ");

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
			System.out.println("********** CLASSE SousTypeProduitJPATest - méthode testGetEnTeteColonne() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode getEnTeteColonne().");
			System.out.println();				
		}
				
		// *** ARRANGE - GIVEN
		final SousTypeProduitI objetConstructeurNull = new SousTypeProduitJPA();
		
		// ACT - WHEN
		final String enTete0 = objetConstructeurNull.getEnTeteColonne(0);
		final String enTete1 = objetConstructeurNull.getEnTeteColonne(1);
		final String enTete2 = objetConstructeurNull.getEnTeteColonne(2);
		final String enTete7 = objetConstructeurNull.getEnTeteColonne(7);
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("enTete0 : " + enTete0);
			System.out.println("enTete1 : " + enTete1);
			System.out.println("enTete2 : " + enTete2);
			System.out.println("enTete7 : " + enTete7);
			System.out.println();
		}

		// ASSERT - THEN
		assertEquals("idSousTypeProduit", enTete0, "enTete0 doit retourner \"idSousTypeProduit\" :  ");
		assertEquals("type de produit", enTete1, "enTete1 doit retourner \"type de produit\" :  ");
		assertEquals("sous-type de produit", enTete2, "enTete2 doit retourner \"sous-type de produit\" :  ");
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
			System.out.println("********** CLASSE SousTypeProduitJPATest - méthode testGetValeurColonne() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode getValeurColonne().");
			System.out.println();				
		}
		
		// *** ARRANGE - GIVEN
		final SousTypeProduitI objetConstructeurNull = new SousTypeProduitJPA();
		
		// ACT - WHEN
		final String valeur0Null = (String) objetConstructeurNull.getValeurColonne(0);
		final String valeur1Null = (String) objetConstructeurNull.getValeurColonne(1);
		final String valeur2Null = (String) objetConstructeurNull.getValeurColonne(2);
		final String valeur7Null = (String) objetConstructeurNull.getValeurColonne(7);
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("valeur0Null : " + valeur0Null);
			System.out.println("valeur1Null : " + valeur1Null);
			System.out.println("valeur2Null : " + valeur2Null);
			System.out.println("valeur7Null : " + valeur7Null);
			System.out.println();
		}

		// ASSERT - THEN
		assertNull(valeur0Null, "valeur0Null doit retourner \"null\" :  ");
		assertNull(valeur1Null, "valeur1Null doit retourner \"null\" :  ");
		assertNull(valeur2Null, "valeur2Null doit retourner \"null\" :  ");
		assertEquals("invalide", valeur7Null, "valeur7Null doit retourner \"invalide\" :  ");

		
		// *** ARRANGE - GIVEN
		final TypeProduitI typeProduit1 = new TypeProduitJPA(PECHE);
		final SousTypeProduitI objet1 = new SousTypeProduitJPA(1L, MOULINET, typeProduit1, null);
		
		// ACT - WHEN
		final String valeur0 = (String) objet1.getValeurColonne(0);
		final String valeur1 = (String) objet1.getValeurColonne(1);
		final String valeur2 = (String) objet1.getValeurColonne(2);
		final String valeur7 = (String) objet1.getValeurColonne(7);
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("valeur0 : " + valeur0);
			System.out.println("valeur1 : " + valeur1);
			System.out.println("valeur2 : " + valeur2);
			System.out.println("valeur7 : " + valeur7);
			System.out.println();
		}

		// ASSERT - THEN
		assertEquals("1", valeur0, "valeur0 doit retourner \"1\" :  ");
		assertEquals("Pêche", valeur1, "valeur1 doit retourner \"Pêche\" :  ");
		assertEquals("moulinet", valeur2, "valeur2 doit retourner \"moulinet\" :  ");
		assertEquals("invalide", valeur7Null, "valeur7Null doit retourner \"invalide\" :  ");
		
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste les méthodes <b>ajouterSTPauProduit()</b> et <b>retirerSTPauProduit()</b> :</p>
	 * <li>garantit que l'ajout d'un produit fonctionne correctement.</li>
	 * <li>garantit que le retrait d'un produit fonctionne correctement.</li>
	 * <li>garantit que les listes sont mises à jour correctement.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testAjouterRetirerProduit() : vérifie le comportement des méthodes ajouterSTPauProduit() et retirerSTPauProduit()")
	@Tag("produits")
	@Test
	public final void testAjouterRetirerProduit() {
	    // **********************************
	    // AFFICHAGE DANS LE TEST ou NON
	    final boolean affichage = false;
	    // **********************************

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("********** CLASSE SousTypeProduitJPATest - méthode testAjouterRetirerProduit() ********** ");
	        System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT DES METHODES ajouterSTPauProduit() ET retirerSTPauProduit().");
	        System.out.println();
	    }

	    //**** ARRANGE - GIVEN
	    final TypeProduitI typeProduitPeche = new TypeProduitJPA(1L, PECHE, null);
	    final SousTypeProduitI sousTypeProduitCanneAPeche = new SousTypeProduitJPA(10L, CANNE_A_PECHE, typeProduitPeche, null);
	    final ProduitI produit1 = new ProduitJPA(100L, CANNE_TELESCOPIQUE, sousTypeProduitCanneAPeche);
	    final ProduitI produit2 = new ProduitJPA(200L, CANNE_CARBONE, sousTypeProduitCanneAPeche);

	    // ACT - WHEN
	    sousTypeProduitCanneAPeche.ajouterSTPauProduit(produit1);
	    sousTypeProduitCanneAPeche.ajouterSTPauProduit(produit2);

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println("**** APRES AJOUT DE PRODUITS ****");
	        this.afficher(sousTypeProduitCanneAPeche);
	        System.out.println();
	    }

	    // ASSERT - THEN
	    assertEquals(2, sousTypeProduitCanneAPeche.getProduits().size(), "La liste des produits doit contenir 2 éléments après ajout : ");

	    // ACT - WHEN
	    sousTypeProduitCanneAPeche.retirerSTPauProduit(produit1);

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println("**** APRES RETRAIT D'UN PRODUIT ****");
	        this.afficher(sousTypeProduitCanneAPeche);
	        System.out.println();
	    }

	    // ASSERT - THEN
	    assertEquals(1, sousTypeProduitCanneAPeche.getProduits().size(), "La liste des produits doit contenir 1 élément après retrait : ");
	    assertFalse(sousTypeProduitCanneAPeche.getProduits().contains(produit1), "Le produit1 ne doit plus être dans la liste : ");
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste les méthodes <b>getEnTeteCsv()</b> et <b>toStringCsv()</b> avec des produits :</p>
	 * <li>garantit que l'en-tête CSV est correcte.</li>
	 * <li>garantit que la conversion en CSV fonctionne correctement avec des produits.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testCsvAvecProduits() : vérifie le comportement des méthodes getEnTeteCsv() et toStringCsv() avec des produits")
	@Tag("csv")
	@Test
	public final void testCsvAvecProduits() {
	    // **********************************
	    // AFFICHAGE DANS LE TEST ou NON
	    final boolean affichage = false;
	    // **********************************

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("********** CLASSE SousTypeProduitJPATest - méthode testCsvAvecProduits() ********** ");
	        System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT DES METHODES getEnTeteCsv() ET toStringCsv() AVEC DES PRODUITS.");
	        System.out.println();
	    }

	    //**** ARRANGE - GIVEN
	    final TypeProduitI typeProduitPeche = new TypeProduitJPA(1L, PECHE, null);
	    final SousTypeProduitI sousTypeProduitCanneAPeche = new SousTypeProduitJPA(10L, CANNE_A_PECHE, typeProduitPeche, null);
	    final ProduitI produit1 = new ProduitJPA(100L, CANNE_TELESCOPIQUE, sousTypeProduitCanneAPeche);
	    final ProduitI produit2 = new ProduitJPA(200L, CANNE_CARBONE, sousTypeProduitCanneAPeche);

	    // ACT - WHEN
	    sousTypeProduitCanneAPeche.ajouterSTPauProduit(produit1);
	    sousTypeProduitCanneAPeche.ajouterSTPauProduit(produit2);

	    final String enTeteCsv = sousTypeProduitCanneAPeche.getEnTeteCsv();
	    final String toStringCsv = sousTypeProduitCanneAPeche.toStringCsv();

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println("En-tête CSV : " + enTeteCsv);
	        System.out.println("CSV : " + toStringCsv);
	    }

	    // ASSERT - THEN
	    assertEquals("idSousTypeProduit;type de produit;sous-type de produit;", enTeteCsv, "L'en-tête CSV doit être correcte : ");
	    assertTrue(toStringCsv.startsWith("10;Pêche;canne à pêche;"), "Le CSV doit commencer par les informations du sous-type : ");
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>setSousTypeProduit()</b> avec normalisation :</p>
	 * <li>garantit que les chaînes vides sont normalisées en <code>null</code>.</li>
	 * <li>garantit que les chaînes avec espaces sont trimées.</li>
	 * <li>garantit que les <code>null</code> restent <code>null</code>.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testSetSousTypeProduitNormalize() : vérifie la normalisation dans setSousTypeProduit()")
	@Tag("setters")
	@Test
	public final void testSetSousTypeProduitNormalize() {
	    // **********************************
	    // AFFICHAGE DANS LE TEST ou NON
	    final boolean affichage = false;
	    // **********************************

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("********** CLASSE SousTypeProduitJPATest - méthode testSetSousTypeProduitNormalize() **********");
	        System.out.println("CE TEST VERIFIE LA NORMALISATION DANS setSousTypeProduit().");
	        System.out.println();
	    }

	    //**** ARRANGE - GIVEN
	    final SousTypeProduitJPA sousTypeProduit = new SousTypeProduitJPA();

	    // ACT - WHEN
	    sousTypeProduit.setSousTypeProduit(null);
	    final String resultatNull = sousTypeProduit.getSousTypeProduit();

	    sousTypeProduit.setSousTypeProduit("   ");
	    final String resultatVide = sousTypeProduit.getSousTypeProduit();

	    sousTypeProduit.setSousTypeProduit("  canne à pêche  ");
	    final String resultatTrim = sousTypeProduit.getSousTypeProduit();

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println("Résultat avec null : " + resultatNull);
	        System.out.println("Résultat avec chaîne vide : " + resultatVide);
	        System.out.println("Résultat avec chaîne trimée : " + resultatTrim);
	    }

	    // ASSERT - THEN
	    assertNull(resultatNull, "setSousTypeProduit(null) doit retourner null : ");
	    assertNull(resultatVide, "setSousTypeProduit(\"   \") doit retourner null : ");
	    assertEquals("canne à pêche", resultatTrim, "setSousTypeProduit(\"  canne à pêche  \") doit retourner \"canne à pêche\" : ");
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>setTypeProduit()</b> avec rattachement bidirectionnel :</p>
	 * <li>garantit que le sous-type est ajouté à la liste des sous-types du type de produit.</li>
	 * <li>garantit que le sous-type est retiré de l'ancien type de produit.</li>
	 * <li>garantit que <code>valide</code> est mis à jour.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testSetTypeProduitBidirectionnel() : vérifie le rattachement bidirectionnel dans setTypeProduit()")
	@Tag("setters")
	@Test
	public final void testSetTypeProduitBidirectionnel() {
	    // **********************************
	    // AFFICHAGE DANS LE TEST ou NON
	    final boolean affichage = false;
	    // **********************************

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("********** CLASSE SousTypeProduitJPATest - méthode testSetTypeProduitBidirectionnel() **********");
	        System.out.println("CE TEST VERIFIE LE RATTACHEMENT BIDIRECTIONNEL DANS setTypeProduit().");
	        System.out.println();
	    }

	    //**** ARRANGE - GIVEN
	    final TypeProduitJPA typeProduit1 = new TypeProduitJPA(1L, PECHE, null);
	    final TypeProduitJPA typeProduit2 = new TypeProduitJPA(2L, OUTILLAGE, null);
	    final SousTypeProduitJPA sousTypeProduit = new SousTypeProduitJPA(10L, CANNE_A_PECHE, typeProduit1, null);

	    // ACT - WHEN
	    sousTypeProduit.setTypeProduit(typeProduit2);

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println("Sous-type après changement de type : " + sousTypeProduit.getTypeProduit().getTypeProduit());
	        System.out.println("Sous-types de typeProduit1 : " + typeProduit1.getSousTypeProduits().size());
	        System.out.println("Sous-types de typeProduit2 : " + typeProduit2.getSousTypeProduits().size());
	    }

	    // ASSERT - THEN
	    assertEquals(typeProduit2, sousTypeProduit.getTypeProduit(), "Le type de produit doit être mis à jour : ");
	    assertFalse(typeProduit1.getSousTypeProduits().contains(sousTypeProduit), "Le sous-type ne doit plus être dans l'ancien type : ");
	    assertTrue(typeProduit2.getSousTypeProduits().contains(sousTypeProduit), "Le sous-type doit être dans le nouveau type : ");
	    assertTrue(sousTypeProduit.isValide(), "Le sous-type doit être valide : ");
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>clone()</b> avec des produits associés :</p>
	 * <li>garantit que les produits sont clonés profondément.</li>
	 * <li>garantit que les modifications du clone n'affectent pas l'original.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws CloneNotSupportedException
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testCloneAvecProduits() : vérifie le clonage profond avec des produits associés")
	@Tag("clone")
	@Test
	public final void testCloneAvecProduits() throws CloneNotSupportedException {
	    // **********************************
	    // AFFICHAGE DANS LE TEST ou NON
	    final boolean affichage = false;
	    // **********************************

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("********** CLASSE SousTypeProduitJPATest - méthode testCloneAvecProduits() **********");
	        System.out.println("CE TEST VERIFIE LE CLONAGE PROFOND AVEC DES PRODUITS ASSOCIES.");
	        System.out.println();
	    }

	    //**** ARRANGE - GIVEN
	    final TypeProduitJPA typeProduit = new TypeProduitJPA(1L, PECHE, null);
	    final SousTypeProduitJPA sousTypeProduit = new SousTypeProduitJPA(10L, CANNE_A_PECHE, typeProduit, null);
	    final ProduitJPA produit1 = new ProduitJPA(100L, CANNE_TELESCOPIQUE, sousTypeProduit);
	    final ProduitJPA produit2 = new ProduitJPA(200L, CANNE_CARBONE, sousTypeProduit);
	    sousTypeProduit.ajouterSTPauProduit(produit1);
	    sousTypeProduit.ajouterSTPauProduit(produit2);

	    // ACT - WHEN
	    final SousTypeProduitJPA clone = sousTypeProduit.clone();

	    // Modification du clone
	    clone.setSousTypeProduit(CLONE_MODIFIE);
	    final List<? extends ProduitI> produitsClone = clone.getProduits();
	    if (!produitsClone.isEmpty()) {
	        ((ProduitJPA) produitsClone.get(0)).setProduit(PRODUIT_CLONE_MODIFIE);
	    }

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println("Original : " + sousTypeProduit.getSousTypeProduit());
	        System.out.println("Clone : " + clone.getSousTypeProduit());
	        System.out.println("Produit original : " + sousTypeProduit.getProduits().get(0).getProduit());
	        System.out.println("Produit clone : " + clone.getProduits().get(0).getProduit());
	    }

	    // ASSERT - THEN
	    assertNotEquals(sousTypeProduit.getSousTypeProduit(), clone.getSousTypeProduit(), "Le libellé du clone doit être différent : ");
	    assertNotEquals(sousTypeProduit.getProduits().get(0).getProduit(), clone.getProduits().get(0).getProduit(), "Le produit du clone doit être différent : ");
	} //___________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>affiche à la console un SousTypeProduitJPA.</p>
	 * </div>
	 *
	 * @param pSousTypeProduit : SousTypeProduitJPA
	 */
	private void afficher(final SousTypeProduitI pSousTypeProduit) {
		
		if (pSousTypeProduit == null) {
			return;
		}
		
		System.out.println("id du SousTypeProduitI : " + pSousTypeProduit.getIdSousTypeProduit());
		System.out.println("SousTypeProduitI : " + pSousTypeProduit.getSousTypeProduit());
		if (pSousTypeProduit.getTypeProduit() != null) {
			System.out.println("type de produit : " + pSousTypeProduit.getTypeProduit().getTypeProduit());
		} else {
			System.out.println("type de produit : null");
		}
		if (pSousTypeProduit.getProduits() != null) {
			System.out.println("Produits : ");
			for (final ProduitI produit : pSousTypeProduit.getProduits()) {
				System.out.println("produit : " + produit.getProduit());
			}
		} else {
			System.out.println("Produits : null");
		}
		System.out.println("valide : " + pSousTypeProduit.isValide());
		
	}


	
	/**
	 * <div>
	 * <p>affiche le contenu de la collection sousTypeProduits 
	 * du TypeProduitI contenu dans le SousTypeProduitI pSousTypeProduit</p>
	 * </div>
	 *
	 * @param pSousTypeProduit : SousTypeProduitJPA
	 */
	private void afficherSousTypeProduitsDuTypeProduit(
			final SousTypeProduitI pSousTypeProduit) {
		
		if (pSousTypeProduit == null) {
			return;
		}
		
		final TypeProduitI typeProduit = pSousTypeProduit.getTypeProduit();
		
		if (typeProduit == null) {
			return;
		}
		
		final List<? extends SousTypeProduitI> sousTypeProduits 
			= typeProduit.getSousTypeProduits();
				
		if (sousTypeProduits == null) {
			System.out.println("la liste sousTypeProduits du TypeProduitI est null");
			return;
		}
		
		for (final SousTypeProduitI sousTypeProduit : sousTypeProduits) {
			System.out.println("sousTypeProduit dans la collection sousTypeProduits du typeProduit " 
					+ typeProduit.afficherTypeProduit() + " : " 
					+ sousTypeProduit.afficherSousTypeProduit());
		}
		
	}


	
	/**
	 * <div>
	 * <p>affiche le contenu de la collection sousTypeProduits 
	 * de pTypeProduit</p>
	 * </div>
	 *
	 * @param pTypeProduit : TypeProduitJPA
	 */
	private void afficherSousTypeProduitsDansTypeProduit(
			final TypeProduitI pTypeProduit) {
		
		if (pTypeProduit == null) {
			return;
		}
		
		final List<? extends SousTypeProduitI> sousTypeProduits 
			= pTypeProduit.getSousTypeProduits();
				
		if (sousTypeProduits == null) {
			System.out.println("la liste sousTypeProduits du TypeProduitI est null");
			return;
		}
		
		for (final SousTypeProduitI sousTypeProduit : sousTypeProduits) {
			System.out.println("sousTypeProduit dans la collection sousTypeProduits du typeProduit " 
					+ pTypeProduit.afficherTypeProduit() + " : " 
					+ sousTypeProduit.afficherSousTypeProduit());
		}
		
	}
	

}
