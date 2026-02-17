package levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import levy.daniel.application.model.metier.produittype.SousTypeProduitI;


/**
 * <div>
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">CLASSE TypeProduitJPATest.java :</p>
 * 
 * <p>
 * Cette classe teste l'Entity JPA : 
 * <span style="font-weight:bold;">TypeProduitJPA</span> 
 * </p>
 * </div>
 * 
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 15 déc. 2025
 */
public class TypeProduitJPATest {

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
	 * "clone"
	 */
	public static final String CLONE = "clone";
	
	/**
	 * "null" 
	 */
	public static final String NULL = "null";
	
	/**
	 * "setters"
	 */
	public static final String SETTERS = "setters";
	
	/**
	 * "getters"
	 */
	public static final String GETTERS = "getters";
	
	/* ------------------------------------------------------------------ */
	
	/**
	 * "Photographie"
	 */
	public static final String PHOTOGRAPHIE = "Photographie";

	/**
	 * "Anatomie"
	 */
	public static final String ANATOMIE = "Anatomie";
	
	/**
	 * "Pêche"
	 */
	public static final String PECHE = "Pêche";
	
	/**
	 * "outillage"
	 */
	public static final String OUTILLAGE = "outillage";
	
	/**
	 * "Restauration"
	 */
	public static final String RESTAURATION = "Restauration";
	
	/**
	 * "vêtement"
	 */
	public static final String VETEMENT = "vêtement";
	
	/**
	 * "vêtement enfant"
	 */
	public static final String VETEMENT_ENFANT = "vêtement enfant";
	
	/**
	 * "vêtement homme"
	 */
	public static final String VETEMENT_HOMME = "vêtement homme";

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
			.getLogger(TypeProduitJPATest.class);

	// *************************METHODES************************************/



	/**
	* <div>
	* <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	* </div>
	*/
	public TypeProduitJPATest() {
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
			System.out.println("********** CLASSE TypeProduitJPATest - méthode testConstructeurNull() ********** ");
			System.out.println("CE TEST VERIFIE LE FONCTIONNEMENT DU CONSTRUCTEUR D'ARITE NULLE.");
			System.out.println();				
		}
		
		//**** ARRANGE - GIVEN
		final TypeProduitJPA objetConstructeurNull1 = new TypeProduitJPA();
		final TypeProduitJPA objetConstructeurNull2 = new TypeProduitJPA();
		
		// ACT - WHEN
		final Long idObjetConstructeurNull1 = objetConstructeurNull1.getIdTypeProduit();
		final Long idObjetConstructeurNull2 = objetConstructeurNull2.getIdTypeProduit();
		
		final String typeProduitConstructeurNull1 = objetConstructeurNull1.getTypeProduit();
		final String typeProduitConstructeurNull2 = objetConstructeurNull2.getTypeProduit();
		
		final Boolean memeInstance1 = objetConstructeurNull1 == objetConstructeurNull2;
		final Boolean memeInstance2 = objetConstructeurNull2 == objetConstructeurNull1;
		
		final Boolean constructeurNull1EqualsConstructeurNull2 = objetConstructeurNull1.equals(objetConstructeurNull2);
		final Boolean constructeurNull2EqualsConstructeurNull1 = objetConstructeurNull2.equals(objetConstructeurNull1);
		final Boolean constructeurNull1EqualsConstructeurNull1 = objetConstructeurNull1.equals(objetConstructeurNull1);
		
		final int constructeurNull1HashCode = objetConstructeurNull1.hashCode();
		final int constructeurNull2HashCode = objetConstructeurNull2.hashCode();

		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("*** TypeProduitJPA objetConstructeurNull1 = new TypeProduitJPA(); *** ");
			System.out.println("*** TypeProduitJPA objetConstructeurNull2 = new TypeProduitJPA(); *** ");
			System.out.println("idObjetConstructeurNull1 : " + idObjetConstructeurNull1);
			System.out.println("idObjetConstructeurNull2 : " + idObjetConstructeurNull2);
			System.out.println();
			System.out.println("typeProduitConstructeurNull1 : " + typeProduitConstructeurNull1);
			System.out.println("typeProduitConstructeurNull2 : " + typeProduitConstructeurNull2);
			System.out.println();
			System.out.println("produitConstructeurNull1 == produitConstructeurNull2 : " + memeInstance1);
			System.out.println("produitConstructeurNull2 == produitConstructeurNull1 : " + memeInstance2);
			System.out.println();
			System.out.println("constructeurNull1EqualsConstructeurNull2 : " + constructeurNull1EqualsConstructeurNull2);
			System.out.println("constructeurNull2EqualsConstructeurNull1 : " + constructeurNull2EqualsConstructeurNull1);
			System.out.println("constructeurNull1EqualsConstructeurNull1 : " + constructeurNull1EqualsConstructeurNull1);
			System.out.println("constructeurNull1HashCode : " + constructeurNull1HashCode);
			System.out.println("constructeurNull2HashCode : " + constructeurNull2HashCode);
			System.out.println();

		}
		
		
		// ASSERT - THEN
		/* vérifie que toutes les propriétés de l'objet sont null. */
		assertNull(idObjetConstructeurNull1, "l'ID de produitConstructeurNull1 doit être null : ");
		assertNull(idObjetConstructeurNull2, "l'ID de produitConstructeurNull2 doit être null : ");
				
		assertNull(typeProduitConstructeurNull1, "typeProduitConstructeurNull1 doit être null : ");
		assertNull(typeProduitConstructeurNull2, "typeProduitConstructeurNull2 doit être null : ");
				
		/* vérifie que 2 instances créées avec le constructeur null sont différentes. */
		assertFalse(memeInstance1, "objetConstructeurNull1 == objetConstructeurNull2 doit retourner false : ");
		assertFalse(memeInstance2, "objetConstructeurNull2 == objetConstructeurNull1 doit retourner false : ");
		
		/* vérifie que 2 instances créées avec le constructeur null sont equals. */
		assertTrue(constructeurNull1EqualsConstructeurNull2, "objetConstructeurNull1 doit être Equals() à objetConstructeurNull2 : ");
		assertTrue(constructeurNull2EqualsConstructeurNull1, "objetConstructeurNull2 doit être Equals() à objetConstructeurNull1 : ");
		assertTrue(constructeurNull1EqualsConstructeurNull1, "objetConstructeurNull1 doit être Equals() à objetConstructeurNull1 : ");
		assertEquals(constructeurNull1HashCode, constructeurNull2HashCode, "objetConstructeurNull1 doit avoir le même Hashcode que objetConstructeurNull2 : ");

		
		//**** ARRANGE - GIVEN
		// ACT - WHEN
		// INSTANCIATION - Utilisation des Setters
		objetConstructeurNull1.setIdTypeProduit(1L);
		objetConstructeurNull1.setTypeProduit(VETEMENT);
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println("*** APRES objetConstructeurNull1.setIdTypeProduit(1L); et objetConstructeurNull1.setTypeProduit(\"vêtement\"); ***");
			this.afficher(objetConstructeurNull1);
		}

		
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
			System.out.println("********** CLASSE TypeProduitJPATest - méthode testEquals() ********** ");
			System.out.println("CE TEST VERIFIE LE RESPECT DU CONTRAT Java Equals() et Hashcode().");
			System.out.println();				
		}
		
		// ARRANGE - GIVEN
		final TypeProduitJPA objet1 = new TypeProduitJPA(1L, PHOTOGRAPHIE, null);
		final TypeProduitJPA objet2EqualsObjet1 = new TypeProduitJPA(1L, PHOTOGRAPHIE, null);
		final TypeProduitJPA objet3EqualsObjet1 = new TypeProduitJPA(1L, PHOTOGRAPHIE, null);
		final TypeProduitJPA objet4PasEqualsObjet1 = new TypeProduitJPA(4L, ANATOMIE, null);
		final NullPointerException mauvaiseInstance = new NullPointerException();
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println("*** APRES TypeProduitJPA objet1 = new TypeProduitJPA(1L, PHOTOGRAPHIE, null); ***");
			this.afficher(objet1);
			System.out.println();
			System.out.println("*** TypeProduitJPA objet2EqualsObjet1 = new TypeProduitJPA(1L, PHOTOGRAPHIE, null); ***");
			this.afficher(objet2EqualsObjet1);
			System.out.println();
			System.out.println("*** TypeProduitJPA objet3EqualsObjet1 = new TypeProduitJPA(1L, PHOTOGRAPHIE, null); ***");
			this.afficher(objet3EqualsObjet1);
			System.out.println();
			System.out.println("*** TypeProduitJPA objet4PasEqualsObjet1 = new TypeProduitJPA(4L, ANATOMIE, null); ***");
			this.afficher(objet4PasEqualsObjet1);
			System.out.println();

		}
		
		// ACT - WHEN
		
		// ASSERT - THEN
		
		/* garantit que x.equals(mauvaise instance) retourne false 
		 * ("unlikely-arg-type"). */
		assertFalse(objet1.equals(mauvaiseInstance), "x.equals(mauvaise instance) doit retourner false : ");
		
		/* garantit que x.equals(null) retourne false (avec x non null). */
		assertFalse(objet1.equals(null), "x.equals(null) doit retourner false (avec x non null) : "); // NOPMD by danyl on 17/02/2026 10:03
		
		/* garantit le contrat Java reflexif x.equals(x). */
		assertEquals(objet1, objet1, "x.equals(x) : ");
		
		/* garantit le contrat Java symétrique 
		 * x.equals(y) ----> y.equals(x). */
		assertNotSame(objet1, objet2EqualsObjet1, "objet1 et objet2EqualsObjet1 ne sont pas la même instance : ");
		assertEquals(objet1, objet2EqualsObjet1, "objet1.equals(objet2EqualsObjet1) : ");
		assertEquals(objet2EqualsObjet1, objet1, "objet2EqualsObjet1.equals(objet1) : ");
		
		/* garantit le contrat Java transitif 
		 * x.equals(y) et y.equals(z) ----> x.equals(z). */
		assertEquals(objet1, objet2EqualsObjet1, "objet1.equals(objet2EqualsObjet1) : ");
		assertEquals(objet2EqualsObjet1, objet3EqualsObjet1, "objet2EqualsObjet1.equals(objet3EqualsObjet1) : ");
		assertEquals(objet1, objet3EqualsObjet1, "objet1.equals(objet3EqualsObjet1) : ");
		
		/* garantit le contrat Java sur les hashcode 
		 * x.equals(y) ----> x.hashcode() == y.hashcode(). */
		assertEquals(objet1.hashCode(), objet2EqualsObjet1.hashCode(), "objet1.hashCode().equals(objet2EqualsObjet1.hashCode()) : ");
		
		/* garantit le bon fonctionnement de equals() 
		 * en cas d'égalité métier. */
		assertEquals(objet1, objet2EqualsObjet1, "objet1.equals(objet2EqualsObjet1) : ");
		
		/* garantit le bon fonctionnement de equals() 
		 * en cas d'inégalité métier. */
		assertNotEquals(objet1, objet4PasEqualsObjet1, "objet1 n'est pas equals() avec objet4PasEqualsObjet1 : ");
		
		
		/* -------------------------------------------------------------- */
		/* Branche manquante : id non null / id non null (id-first)        */
		/* -------------------------------------------------------------- */
		
		/* Cas id non null / id non null : même typeProduit mais IDs différents => false. */
		final TypeProduitJPA objetIdsDifferentsMemeType1 = new TypeProduitJPA(1L, VETEMENT, null);
		final TypeProduitJPA objetIdsDifferentsMemeType2 = new TypeProduitJPA(2L, "VÊTEMENT", null);
		
		assertNotEquals(
				objetIdsDifferentsMemeType1
				, objetIdsDifferentsMemeType2
				, "id non null/id non null : IDs différents => equals false même si typeProduit identique : ");
		assertNotEquals(
				objetIdsDifferentsMemeType2
				, objetIdsDifferentsMemeType1
				, "symétrie id non null/id non null : IDs différents => equals false : ");
		
		
		/* ----------------------------- */
		/* Branches manquantes : fallback */
		/* ----------------------------- */
		
		/* Cas id null / id null : fallback métier (case-insensitive). */
		final TypeProduitJPA objetIdNull1 = new TypeProduitJPA(null, VETEMENT, null);
		final TypeProduitJPA objetIdNull2 = new TypeProduitJPA(null, "VÊTEMENT", null);
		
		assertEquals(objetIdNull1, objetIdNull2, "id null/id null : fallback métier case-insensitive : ");
		assertEquals(objetIdNull1.hashCode(), objetIdNull2.hashCode(), "id null/id null : hashCode cohérent avec equals : ");
		
		/* Cas id non null / id null : fallback métier (case-insensitive). */
		final TypeProduitJPA objetIdNonNull = new TypeProduitJPA(7L, VETEMENT, null);
		final TypeProduitJPA objetIdNull = new TypeProduitJPA(null, "VÊTEMENT", null);
		
		assertEquals(objetIdNonNull, objetIdNull, "id non null / id null : fallback métier case-insensitive : ");
		assertEquals(objetIdNull, objetIdNonNull, "symétrie id non null / id null : ");
		
		/* Cas typeProduit null / typeProduit non null : false. */
		final TypeProduitJPA objetTypeNull = new TypeProduitJPA(null, null, null);
		final TypeProduitJPA objetTypeNonNull = new TypeProduitJPA(null, VETEMENT, null);
		
		assertNotEquals(objetTypeNull, objetTypeNonNull, "typeProduit null / non null : doit être false : ");
		assertNotEquals(objetTypeNonNull, objetTypeNull, "symétrie typeProduit null / non null : ");
		
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
			System.out.println("********** CLASSE TypeProduitJPATest - méthode testToString() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode toString().");
			System.out.println();				
		}
		
		//**** ARRANGE - GIVEN
		final TypeProduitJPA objetConstructeurNull = new TypeProduitJPA();
						
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("***** objetConstructeurNull ******");
			this.afficher(objetConstructeurNull);
			System.out.println();
			System.out.println("objetConstructeurNull.toString() : " + objetConstructeurNull.toString());
			System.out.println();
		}
		
		// ACT - WHEN
		
		// ASSERT - THEN
		/* garantit le bon fonctionnement avec les null. */
		assertEquals("TypeProduitJPA [idTypeProduit=null, typeProduit=null]", objetConstructeurNull.toString(), "doit afficher TypeProduitJPA [idTypeProduit=null, typeProduit=null] : ");

		
		//**** ARRANGE - GIVEN
		final TypeProduitJPA objet1 = new TypeProduitJPA(PECHE);
				
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
			= "TypeProduitJPA [idTypeProduit=null, typeProduit=Pêche]";
				
		// ASSERT - THEN
		/* garantit le bon fonctionnement de toString() */
		assertEquals(resultat, objet1.toString(), "doit afficher toString() : ");

	} //___________________________________________________________________

	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>compareTo(Produit pObject)</b> :</p>
	 * <li>garantit que compareTo(memeInstance) retourne 0.</li>
	 * <li>garantit que compareTo(null) retourne un nombre négatif.</li>
	 * <li>garantit le Contrat Java : 
	 * x.equals(y) ---> x.compareTo(y) == 0.</li>
	 * <li>garantit que les null sont bien gérés 
	 * dans compareTo(Produit pObject).</li>
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
			System.out.println("********** CLASSE TypeProduitJPATest - méthode testCompareTo() ********** ");
			System.out.println("CE TEST VERIFIE LE RESPECT des contrats Java de la méthode compareTo().");
			System.out.println();				
		}
		
		// ARRANGE - GIVEN
		/* TypeProduitJPA */
		final TypeProduitJPA objet1 = new TypeProduitJPA(PECHE);
		final TypeProduitJPA objetEqualsObjet1 = new TypeProduitJPA(PECHE);
		final TypeProduitJPA objet1MemeInstance = objet1;
		final TypeProduitJPA objet2AvantObjet1 = new TypeProduitJPA(OUTILLAGE);
		final TypeProduitJPA objet3ApresObjet1 = new TypeProduitJPA(RESTAURATION);
			
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("***** objet1 ******");
			this.afficher(objet1);
			System.out.println();
			System.out.println("***** objetEqualsObjet1 *****");
			this.afficher(objetEqualsObjet1);
			System.out.println();
			System.out.println("***** objet1MemeInstance ******");
			this.afficher(objet1MemeInstance);
			System.out.println();
			System.out.println("***** objet2AvantObjet1 ******");
			this.afficher(objet2AvantObjet1);
			System.out.println();
			System.out.println("***** objet3ApresObjet1 ******");
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
		assertTrue(objet1.compareTo(objetEqualsObjet1) == 0, "objet1.compareTo(objetEqualsObjet1) doit retourner 0 : ");
		assertTrue(objet1.compareTo(objet1MemeInstance) == 0, "objet1.compareTo(objet1MemeInstance) doit retourner 0 : ");
		assertTrue(objet1.compareTo(objet2AvantObjet1) > 0, "objet1.compareTo(objet2AvantObjet1) doit retourner > 0 : ");
		assertTrue(objet1.compareTo(objet3ApresObjet1) < 0, "objet1.compareTo(objet3ApresObjet1) doit retourner < 0 : ");

		
		//*** ARRANGE - GIVEN
		final TypeProduitJPA objetConstructeurNull1 = new TypeProduitJPA();
		final TypeProduitJPA objetConstructeurNull2 = new TypeProduitJPA();
		
		final TypeProduitJPA objetAvecTypeNull1 = new TypeProduitJPA(1L, null, null);
		final TypeProduitJPA objetAvecTypeNull2 = new TypeProduitJPA(2L, null, null);
		final TypeProduitJPA objetAvecTypeNonNull3 = new TypeProduitJPA(3L, "toto", null);
		
		// ACT - WHEN
		final int compareToConstructeurNull 
			= objetConstructeurNull1.compareTo(objetConstructeurNull2);
		final int compareToAvecTypesNull 
			= objetAvecTypeNull1.compareTo(objetAvecTypeNull2);
		final int compareToAvecTypeNonNull 
			= objetAvecTypeNonNull3.compareTo(objetAvecTypeNull1);
		
		// ASSERT - THEN
		/* garantit que les null sont bien gérés dans compareTo(). */
		assertTrue(compareToConstructeurNull == 0, "objetConstructeurNull1.compareTo(objetConstructeurNull2) == 0 : ");
		assertTrue(compareToAvecTypesNull == 0, "objetAvecTypeNull1.compareTo(objetAvecTypeNull2)  == 0 : ");
		assertTrue(compareToAvecTypeNonNull < 0, "objetAvecTypeNonNull3.compareTo(objetAvecTypeNull1)  < 0 : ");		
		
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
	@Tag(CLONE)
	@Test
	public final void testClone() throws CloneNotSupportedException {
				
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE TypeProduitJPATest - méthode testClone() ********** ");
			System.out.println("CE TEST VERIFIE LE RESPECT des contrats Java de la méthode clone().");
			System.out.println();				
		}

		//***** ARRANGE - GIVEN
		final TypeProduitJPA objetConstructeurNull = new TypeProduitJPA();
		

		// ACT - WHEN
		final TypeProduitJPA objetConstructeurNullClone 
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
		/* TypeProduitJPA */
		final TypeProduitJPA objet1 = new TypeProduitJPA(PECHE);
				
		// ACT - WHEN
		final TypeProduitJPA objet1Clone = objet1.clone();
						
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
		
		//***** ARRANGE - GIVEN
		/* Modification du Clone pour vérifier que l'objet 
		 * initial n'est pas modifié.*/
		objet1Clone.setIdTypeProduit(2L);		
		objet1Clone.setTypeProduit("clone modifié");
				
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
		assertFalse(objet1.getIdTypeProduit() == objet1Clone.getIdTypeProduit(), "la modification de l'ID dans le clone ne doit pas modifier l'ID dans l'objet cloné : ");
		assertFalse(objet1.getTypeProduit().equals(objet1Clone.getTypeProduit()), "la modification du TypeProduitJPA dans le clone ne doit pas modifier le TypeProduitJPA dans l'objet cloné : ");
		
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>cloneDeep()</b> sur un graphe complet :</p>
	 * <li>garantit que le clone et l'original ne sont pas la même instance.</li>
	 * <li>garantit que les listes ne sont pas partagées.</li>
	 * <li>garantit que les enfants clonés pointent vers le parent cloné.</li>
	 * </ul>
	 * </div>
	 * @throws CloneNotSupportedException 
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testCloneDeepGrapheComplet() : vérifie le clonage profond du graphe TypeProduitJPA→SousTypeProduitJPA→ProduitJPA")
	@Tag(CLONE)
	@Test
	public final void testCloneDeepGrapheComplet() throws CloneNotSupportedException {
		
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE TypeProduitJPATest - méthode testCloneDeepGrapheComplet() ********** ");
			System.out.println("CE TEST VERIFIE LE CLONAGE PROFOND DU GRAPHE COMPLET.");
			System.out.println();
		}
		
		// ARRANGE - GIVEN
		final TypeProduitJPA typeVetement = new TypeProduitJPA(null, VETEMENT, null);
		final SousTypeProduitJPA sousTypeHomme = new SousTypeProduitJPA(null, VETEMENT_HOMME, typeVetement);
		final ProduitJPA produitChemise = new ProduitJPA(null, "chemise homme", sousTypeHomme);
		
		/* Preconditions minimales. */
		assertTrue(typeVetement.getSousTypeProduits().contains(sousTypeHomme), "précondition : typeVetement contient sousTypeHomme : ");
		assertTrue(sousTypeHomme.getProduits().contains(produitChemise), "précondition : sousTypeHomme contient produitChemise : ");
		
		// ACT - WHEN
		final TypeProduitJPA clone = typeVetement.clone();
		
		// ASSERT - THEN
		assertNotSame(typeVetement, clone, "le clone ne doit pas être la même instance : ");
		assertEquals(typeVetement, clone, "clonex.equals(x) doit être vrai : ");
		assertEquals(typeVetement.getClass(), clone.getClass(), "clone.getClass() == x.getClass() : ");
		
		/* Vérifie que les listes ne sont pas partagées. */
		assertNotSame(typeVetement.getSousTypeProduits(), clone.getSousTypeProduits(), "les listes sousTypeProduits ne doivent pas être partagées : ");
		
		/* Vérifie le rattachement enfant->parent sur le clone. */
		assertNotNull(clone.getSousTypeProduits(), "clone.getSousTypeProduits() ne doit pas être null : ");
		assertTrue(clone.getSousTypeProduits().size() == 1, "le clone doit avoir 1 sousTypeProduit : ");
		
		final SousTypeProduitI cloneStpI = clone.getSousTypeProduits().get(0);
		assertNotNull(cloneStpI, "le sousTypeProduit cloné ne doit pas être null : ");
		assertNotSame(sousTypeHomme, cloneStpI, "l'enfant cloné ne doit pas être la même instance que l'original : ");
		assertSame(clone, cloneStpI.getTypeProduit(), "l'enfant cloné doit référencer le parent cloné : ");
		
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>cloneWithoutChildren()</b> :</p>
	 * <li>garantit que le clone n'est pas la même instance que l'original.</li>
	 * <li>garantit que les propriétés simples (id, typeProduit) sont copiées.</li>
	 * <li>garantit que la liste <b>sousTypeProduits</b> du clone est vide et non partagée.</li>
	 * <li>garantit que les enfants de l'original restent rattachés à l'original.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testCloneWithoutChildren() : vérifie cloneWithoutChildren() (clone nu sans enfants)")
	@Tag(CLONE)
	@Test
	public final void testCloneWithoutChildren() {
		
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE TypeProduitJPATest - méthode testCloneWithoutChildren() ********** ");
			System.out.println("CE TEST VERIFIE cloneWithoutChildren() : clone nu sans enfants.");
			System.out.println();
		}
		
		// ARRANGE - GIVEN
		final TypeProduitJPA typeVetement = new TypeProduitJPA(1L, VETEMENT, null);
		final SousTypeProduitJPA sousTypeHomme = new SousTypeProduitJPA(null, VETEMENT_HOMME, typeVetement);
		
		/* Préconditions minimales. */
		assertTrue(
				typeVetement.getSousTypeProduits().contains(sousTypeHomme)
				, "précondition : typeVetement contient sousTypeHomme : ");
		assertSame(
				typeVetement
				, sousTypeHomme.getTypeProduit()
				, "précondition : sousTypeHomme référence typeVetement : ");
		
		// ACT - WHEN
		final TypeProduitJPA cloneNu = typeVetement.cloneWithoutChildren();
		
		// ASSERT - THEN
		
		/* garantit que le clone n'est pas la même instance que l'original. */
		assertNotSame(typeVetement, cloneNu, "cloneNu ne doit pas être la même instance que typeVetement : ");
		
		/* garantit que les propriétés simples (id, typeProduit) sont copiées. */
		assertEquals(typeVetement.getIdTypeProduit(), cloneNu.getIdTypeProduit(), "idTypeProduit doit être copié : ");
		assertEquals(typeVetement.getTypeProduit(), cloneNu.getTypeProduit(), "typeProduit doit être copié : ");
		
		/* garantit que la liste sousTypeProduits du clone est vide et non partagée. */
		assertNotNull(cloneNu.getSousTypeProduits(), "cloneNu.getSousTypeProduits() ne doit pas être null : ");
		assertTrue(cloneNu.getSousTypeProduits().isEmpty(), "cloneNu ne doit avoir aucun SousTypeProduit : ");
		assertNotSame(
				typeVetement.getSousTypeProduits()
				, cloneNu.getSousTypeProduits()
				, "les listes sousTypeProduits ne doivent pas être partagées : ");
		
		/* garantit que les enfants de l'original restent rattachés à l'original. */
		assertSame(
				typeVetement
				, sousTypeHomme.getTypeProduit()
				, "l'enfant original doit rester rattaché au parent original : ");
		
	} //___________________________________________________________________
	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>deepClone(CloneContext)</b> :</p>
	 * <li>garantit que le <b>CloneContext</b> joue son rôle de cache (même instance clone retournée).</li>
	 * <li>garantit la cohérence du rattachement enfant-&gt;parent sur les clones.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testDeepCloneAvecCacheCloneContext() : vérifie deepClone(ctx) et le cache du CloneContext")
	@Tag(CLONE)
	@Test
	public final void testDeepCloneAvecCacheCloneContext() {
		
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE TypeProduitJPATest - méthode testDeepCloneAvecCacheCloneContext() ********** ");
			System.out.println("CE TEST VERIFIE deepClone(ctx) et le cache du CloneContext.");
			System.out.println();
		}
		
		// ARRANGE - GIVEN
		final TypeProduitJPA typeVetement = new TypeProduitJPA(null, VETEMENT, null);
		final SousTypeProduitJPA sousTypeHomme = new SousTypeProduitJPA(null, VETEMENT_HOMME, typeVetement);
		
		/* Préconditions minimales. */
		assertTrue(
				typeVetement.getSousTypeProduits().contains(sousTypeHomme)
				, "précondition : typeVetement contient sousTypeHomme : ");
		
		final levy.daniel.application.model.metier.produittype.CloneContext ctx =
				new levy.daniel.application.model.metier.produittype.CloneContext();
		
		// ACT - WHEN
		final TypeProduitJPA clone1 = typeVetement.deepClone(ctx);
		final TypeProduitJPA clone2 = typeVetement.deepClone(ctx);
		final SousTypeProduitI cloneStpViaCtx = sousTypeHomme.deepClone(ctx);
		
		// ASSERT - THEN
		
		/* garantit que le CloneContext joue son rôle de cache. */
		assertSame(clone1, clone2, "deepClone(ctx) doit retourner la même instance clone (cache) : ");
		
		/* garantit que le clone n'est pas la même instance que l'original. */
		assertNotSame(typeVetement, clone1, "le clone ne doit pas être la même instance que l'original : ");
		
		/* garantit la cohérence des rattachements sur les clones. */
		assertNotNull(clone1.getSousTypeProduits(), "clone1.getSousTypeProduits() ne doit pas être null : ");
		assertTrue(clone1.getSousTypeProduits().size() == 1, "clone1 doit avoir 1 sousTypeProduit : ");
		
		final SousTypeProduitI cloneStpDansCloneParent = clone1.getSousTypeProduits().get(0);
		assertSame(
				cloneStpDansCloneParent
				, cloneStpViaCtx
				, "le clone d'enfant via ctx doit être le même que l'enfant du clone parent : ");
		assertSame(
				clone1
				, cloneStpViaCtx.getTypeProduit()
				, "l'enfant cloné doit référencer le parent cloné : ");
		
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>teste la méthode <span style= "font-weight : bold">getEnTeteCsv()</span></p>
	 * <p>Garantit que enTeteCsv() retourne 
	 * "idTypeProduit;type de produit;"</p>
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
			System.out.println("********** CLASSE TypeProduitJPATest - méthode testGetEnTeteCsv() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode getEnTeteCsv().");
			System.out.println();				
		}
		
		//**** ARRANGE - GIVEN
		final TypeProduitJPA objetConstructeurNull = new TypeProduitJPA();
		
		// ACT - WHEN
		final String enTeteCsv = objetConstructeurNull.getEnTeteCsv();
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("enTeteCsv : " + enTeteCsv);
			System.out.println();
		}
		
		final String enTeteCsvPrevue 
			= "idTypeProduit;type de produit;";
		
		// ASSERT - THEN
		assertEquals(enTeteCsvPrevue, enTeteCsv, "enTeteCsv doit retourner \"idTypeProduit;type de produit;\" : ");
		
	} //___________________________________________________________________


	
	/**
	 * <div>
	 * <ul>
	 * <p>teste la méthode <span style= "font-weight : bold">toStringCsv()</span></p>
	 * <li>garantit que les null sont bien gérés.</li>
	 * <li>garantit le bon fonctionnement de la méthode.</li>
	 * </ul>
	 * <p>"idTypeProduit;type de produit;"</p>
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
			System.out.println("********** CLASSE TypeProduitJPATest - méthode testToStringCsv() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode toStringCsv().");
			System.out.println();				
		}
			
		// *** ARRANGE - GIVEN
		final TypeProduitJPA objetConstructeurNull = new TypeProduitJPA();
		
		// ACT - WHEN
		final String toStringCsvNull = objetConstructeurNull.toStringCsv();
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("*** APRES TypeProduitJPA objetConstructeurNull = new TypeProduitJPA(); ***");
			System.out.println("getEnTeteCsv : " + objetConstructeurNull.getEnTeteCsv());
			System.out.println("toStringCsvNull : " + toStringCsvNull);
			System.out.println();
		}

		final String toStringCsvPrevueNull = "null;null;";
				
		// ASSERT - THEN
		assertEquals(toStringCsvPrevueNull, toStringCsvNull, "toStringCsv doit retourner \"null;null;\" : ");

		
		// *** ARRANGE - GIVEN
		final TypeProduitJPA objet1 = new TypeProduitJPA(1L, PECHE);
		
		// ACT - WHEN
		final String toStringCsv = objet1.toStringCsv();
						
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("*** APRES TypeProduitJPA objet1 = new TypeProduitJPA(1L, PECHE, null); ***");
			System.out.println("getEnTeteCsv : " + objet1.getEnTeteCsv());
			System.out.println("toStringCsv : " + toStringCsv);
			System.out.println();
		}
		
		final String toStringCsvPrevue = "1;Pêche;";

		// ASSERT - THEN
		assertEquals(toStringCsvPrevue, toStringCsv, "toStringCsv doit retourner \"1;Pêche;\" : ");

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
	 * <p>"idTypeProduit;type de produit;"</p>
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
			System.out.println("********** CLASSE TypeProduitJPATest - méthode testGetEnTeteColonne() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode getEnTeteColonne().");
			System.out.println();				
		}
				
		// *** ARRANGE - GIVEN
		final TypeProduitJPA objetConstructeurNull = new TypeProduitJPA();
		
		// ACT - WHEN
		final String enTete0 = objetConstructeurNull.getEnTeteColonne(0);
		final String enTete1 = objetConstructeurNull.getEnTeteColonne(1);
		final String enTete7 = objetConstructeurNull.getEnTeteColonne(7);
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("enTete0 : " + enTete0);
			System.out.println("enTete1 : " + enTete1);
			System.out.println("enTete7 : " + enTete7);
			System.out.println();
		}

		// ASSERT - THEN
		assertEquals("idTypeProduit", enTete0, "enTete0 doit retourner \"idTypeProduit\" :  ");
		assertEquals("type de produit", enTete1, "enTete1 doit retourner \"type de produit\" :  ");
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
	 * <p>"idTypeProduit;type de produit;"</p>
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
			System.out.println("********** CLASSE TypeProduitJPATest - méthode testGetValeurColonne() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode getValeurColonne().");
			System.out.println();				
		}
		
		// *** ARRANGE - GIVEN
		final TypeProduitJPA objetConstructeurNull = new TypeProduitJPA();
		
		// ACT - WHEN
		final String valeur0Null = (String) objetConstructeurNull.getValeurColonne(0);
		final String valeur1Null = (String) objetConstructeurNull.getValeurColonne(1);
		final String valeur7Null = (String) objetConstructeurNull.getValeurColonne(7);
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("*** APRES TypeProduitJPA objetConstructeurNull = new TypeProduitJPA(); ***");
			System.out.println("valeur0Null : " + valeur0Null);
			System.out.println("valeur1Null : " + valeur1Null);
			System.out.println("valeur7Null : " + valeur7Null);
			System.out.println();
		}

		// ASSERT - THEN
		assertNull(valeur0Null, "valeur0Null doit retourner \"null\" :  ");
		assertNull(valeur1Null, "valeur1Null doit retourner \"null\" :  ");
		assertEquals("invalide", valeur7Null, "valeur7Null doit retourner \"invalide\" :  ");

		
		// *** ARRANGE - GIVEN
		final TypeProduitJPA objet1 = new TypeProduitJPA(1L, PECHE);
		
		// ACT - WHEN
		final String valeur0 = (String) objet1.getValeurColonne(0);
		final String valeur1 = (String) objet1.getValeurColonne(1);
		final String valeur7 = (String) objet1.getValeurColonne(7);
				
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("*** APRES TypeProduitJPA objet1 = new TypeProduitJPA(1L, PECHE); ***");
			System.out.println("valeur0 : " + valeur0);
			System.out.println("valeur1 : " + valeur1);
			System.out.println("valeur7 : " + valeur7);
			System.out.println();
		}

		// ASSERT - THEN
		assertEquals("1", valeur0, "valeur0 doit retourner \"1\" :  ");
		assertEquals("Pêche", valeur1, "valeur1 doit retourner \"Pêche\" :  ");
		assertEquals("invalide", valeur7, "valeur7 doit retourner \"invalide\" :  ");
		
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>getTypeProduit()</b> :</p>
	 * <li>vérifie que getTypeProduit() retourne null si le champ typeProduit est null.</li>
	 * <li>vérifie que getTypeProduit() retourne la bonne valeur String si le champ typeProduit est défini.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testGetTypeProduit() : vérifie le comportement de la méthode getTypeProduit()")
	@Tag(GETTERS)
	@Test
	public final void testGetTypeProduit() {

	    // **********************************
	    // AFFICHAGE DANS LE TEST ou NON
	    final boolean affichage = false;
	    // **********************************

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("********** CLASSE TypeProduitJPATest - méthode testGetTypeProduit() ********** ");
	        System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode getTypeProduit().");
	        System.out.println();
	    }

	    //**** ARRANGE - GIVEN
	    final TypeProduitJPA typeProduitJPA = new TypeProduitJPA();

	    // ACT - WHEN
	    /* getTypeProduit() retourne une String. */
	    final String typeProduitString = typeProduitJPA.getTypeProduit();

	    // ASSERT - THEN
	    /* vérifie que getTypeProduit() retourne null si le champ typeProduit est null. */
	    assertNull(typeProduitString, "getTypeProduit() doit retourner null si le champ typeProduit est null : ");

	    //**** ARRANGE - GIVEN
	    typeProduitJPA.setTypeProduit(VETEMENT);

	    // ACT - WHEN
	    final String typeProduitAvecValeur = typeProduitJPA.getTypeProduit();

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println("TypeProduitJPA avec valeur : " + typeProduitAvecValeur);
	    }

	    // ASSERT - THEN
	    /* vérifie que getTypeProduit() retourne la bonne valeur String si le champ typeProduit est défini. */
	    assertNotNull(typeProduitAvecValeur, "getTypeProduit() ne doit pas retourner null si le champ typeProduit est défini : ");
	    assertEquals(VETEMENT, typeProduitAvecValeur, "getTypeProduit() doit retourner la valeur \"vêtement\" : ");
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>setIdTypeProduit(Long)</b> :</p>
	 * <li>vérifie que setIdTypeProduit() modifie correctement l'ID.</li>
	 * <li>vérifie que setIdTypeProduit(null) ne lève pas d'exception.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testSetIdTypeProduit() : vérifie le comportement de la méthode setIdTypeProduit(Long)")
	@Tag(SETTERS)
	@Test
	public final void testSetIdTypeProduit() {

	    // **********************************
	    // AFFICHAGE DANS LE TEST ou NON
	    final boolean affichage = false;
	    // **********************************

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("********** CLASSE TypeProduitJPATest - méthode testSetIdTypeProduit() ********** ");
	        System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode setIdTypeProduit(Long).");
	        System.out.println();
	    }

	    //**** ARRANGE - GIVEN
	    final TypeProduitJPA typeProduitJPA = new TypeProduitJPA();

	    // ACT - WHEN
	    typeProduitJPA.setIdTypeProduit(1L);

	    // ASSERT - THEN
	    /* vérifie que setIdTypeProduit() modifie correctement l'ID. */
	    assertEquals(1L, typeProduitJPA.getIdTypeProduit(), "setIdTypeProduit(1L) doit modifier l'ID à 1L : ");

	    //**** ARRANGE - GIVEN
	    typeProduitJPA.setIdTypeProduit(null);

	    // ACT - WHEN & ASSERT - THEN
	    /* vérifie que setIdTypeProduit(null) ne lève pas d'exception. */
	    assertNull(typeProduitJPA.getIdTypeProduit(), "setIdTypeProduit(null) doit mettre l'ID à null : ");
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Test béton pour vérifier la cohérence du graphe d'objets TypeProduitJPA et SousTypeProduitJPA.</p>
	 * <li>vérifie la création d'un graphe cohérent de TypeProduitJPA et SousTypeProduitJPA.</li>
	 * <li>vérifie les rattachements bidirectionnels.</li>
	 * <li>vérifie l'unicité des instances.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testBetonTypeProduitJPAAvecSousTypeProduitJPA() : vérifie la cohérence du graphe d'objets TypeProduitJPA et SousTypeProduitJPA")
	@Tag("beton")
	@Test
	public final void testBetonTypeProduitJPAAvecSousTypeProduitJPA() {

	    // **********************************
	    // AFFICHAGE DANS LE TEST ou NON
	    final boolean affichage = false;
	    // **********************************

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("********** CLASSE TypeProduitJPATest - méthode testBetonTypeProduitJPAAvecSousTypeProduitJPA() ********** ");
	        System.out.println("CE TEST VERIFIE LA COHERENCE DU GRAPHE D'OBJETS TypeProduitJPA et SousTypeProduitJPA.");
	        System.out.println();
	    }

	    //**** ARRANGE - GIVEN
	    final TypeProduitJPA typeProduitVetement = new TypeProduitJPA(1L, VETEMENT);

	    final SousTypeProduitJPA sousTypeProduitVetementHomme = new SousTypeProduitJPA(1L, "vêtement pour homme", typeProduitVetement);

	    // ACT - WHEN

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println("TypeProduitJPA : " + typeProduitVetement);
	        System.out.println("SousTypeProduitJPA : " + sousTypeProduitVetementHomme);
	    }

	    // ASSERT - THEN
	    /* vérifie la création d'un graphe cohérent de TypeProduitJPA et SousTypeProduitJPA. */
	    assertNotNull(typeProduitVetement.getSousTypeProduits(),
	                 "Le TypeProduitJPA doit avoir une liste de SousTypeProduitJPA non null : ");
	    assertTrue(typeProduitVetement.getSousTypeProduits().contains(sousTypeProduitVetementHomme),
	              "Le TypeProduitJPA doit contenir le SousTypeProduitJPA : ");

	    /* vérifie les rattachements bidirectionnels. */
	    assertEquals(typeProduitVetement, sousTypeProduitVetementHomme.getTypeProduit(),
	                 "Le SousTypeProduitJPA doit référencer le bon TypeProduitJPA : ");

	    /* vérifie l'unicité des instances. */
	    assertSame(typeProduitVetement, sousTypeProduitVetementHomme.getTypeProduit(),
	              "Le TypeProduitJPA doit être la même instance : ");
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Teste la méthode getSousTypeProduits().</p>
	 * <ul>
	 * <li>Vérifie que getSousTypeProduits() retourne une liste non null.</li>
	 * <li>Vérifie que la liste est défensive (modification impossible).</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testGetSousTypeProduits() : vérifie le comportement de la méthode getSousTypeProduits()")
	@Tag(GETTERS)
	@Test
	public final void testGetSousTypeProduits() {

		/* ********************************** */
		/* AFFICHAGE DANS LE TEST ou NON */
		final boolean affichage = false;
		/* ********************************** */

		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println("********** CLASSE TypeProduitJPATest - méthode testGetSousTypeProduits() **********");
		}

		/* ARRANGE - GIVEN */
		final TypeProduitJPA parent = new TypeProduitJPA(1L, VETEMENT);

		/* ACT - WHEN */
		final List<? extends SousTypeProduitI> sousTypes = parent.getSousTypeProduits();

		/* ASSERT - THEN */
		assertNotNull(sousTypes, "getSousTypeProduits() doit retourner une liste non null : ");

		/* Vérification de la liste défensive : clear() doit lever UnsupportedOperationException. */
		assertThrows(
				UnsupportedOperationException.class,
				() -> sousTypes.clear(),
				"La liste doit être défensive (impossible à modifier) : ");

	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>setTypeProduit(String)</b> :</p>
	 * <li>vérifie que setTypeProduit(null) met à jour le champ avec null.</li>
	 * <li>vérifie que setTypeProduit("  valeur  ") normalise la chaîne (trim).</li>
	 * <li>vérifie que setTypeProduit("") retourne null après normalisation.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testSetTypeProduitNormalisation() : vérifie la normalisation dans setTypeProduit(String)")
	@Tag(SETTERS)
	@Test
	public final void testSetTypeProduitNormalisation() {
	    // **********************************
	    // AFFICHAGE DANS LE TEST ou NON
	    final boolean affichage = false;
	    // **********************************

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("********** CLASSE TypeProduitJPATest - méthode testSetTypeProduitNormalisation() **********");
	    }

	    //**** ARRANGE - GIVEN
	    final TypeProduitJPA typeProduit = new TypeProduitJPA();

	    // ACT - WHEN & ASSERT - THEN
	    /* Test avec null. */
	    typeProduit.setTypeProduit(null);
	    assertNull(typeProduit.getTypeProduit(), "setTypeProduit(null) doit mettre null : ");

	    /* Test avec chaîne vide. */
	    typeProduit.setTypeProduit("");
	    assertNull(typeProduit.getTypeProduit(), "setTypeProduit(\"\") doit retourner null après normalisation : ");

	    /* Test avec espaces. */
	    typeProduit.setTypeProduit("  vêtement  ");
	    assertEquals(VETEMENT, typeProduit.getTypeProduit(), "setTypeProduit(\"  vêtement  \") doit normaliser en \"vêtement\" : ");
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Test béton étendu pour vérifier la cohérence du graphe complet :
	 * TypeProduitJPA → SousTypeProduitJPA → ProduitJPA.</p>
	 * <li>vérifie les rattachements bidirectionnels à 3 niveaux.</li>
	 * <li>vérifie la propagation des modifications.</li>
	 * <li>vérifie l'immuabilité des IDs après création.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testBetonGrapheComplet() : vérifie la cohérence du graphe complet TypeProduitJPA→SousTypeProduitJPA→ProduitJPA")
	@Tag("beton")
	@Test
	public final void testBetonGrapheComplet() {
	    // **********************************
	    // AFFICHAGE DANS LE TEST ou NON
	    final boolean affichage = false;
	    // **********************************

	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println("********** CLASSE TypeProduitJPATest - méthode testBetonGrapheComplet() **********");
	    }

	    //**** ARRANGE - GIVEN
	    final TypeProduitJPA typeVetement = new TypeProduitJPA(1L, VETEMENT);
	    final SousTypeProduitJPA sousTypeHomme = new SousTypeProduitJPA(1L, VETEMENT_HOMME, typeVetement);
	    final ProduitJPA produitChemise = new ProduitJPA(1L, "chemise homme", sousTypeHomme);

	    // ACT - WHEN & ASSERT - THEN
	    /* Vérification des rattachements. */
	    assertSame(typeVetement, sousTypeHomme.getTypeProduit(), "SousType doit référencer son TypeProduit : ");
	    assertSame(sousTypeHomme, produitChemise.getSousTypeProduit(), "Produit doit référencer son SousType : ");

	    /* Vérification de la cohérence des listes. */
	    assertTrue(typeVetement.getSousTypeProduits().contains(sousTypeHomme),
	              "TypeProduit doit contenir son SousType : ");
	    assertTrue(sousTypeHomme.getProduits().contains(produitChemise),
	              "SousType doit contenir son Produit : ");

	    /* Test de modification (doit être isolée). */
	    produitChemise.setProduit("chemise modifiée");
	    assertEquals("chemise modifiée", produitChemise.getProduit(),
	                 "Modification du libellé doit être isolée : ");
	    assertEquals(VETEMENT_HOMME, sousTypeHomme.getSousTypeProduit(),
	                 "SousType ne doit pas être affecté : ");
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>setSousTypeProduits(List)</b> :</p>
	 * <li>vérifie que setSousTypeProduits(null) vide la liste et détache les enfants.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testSetSousTypeProduitsNull() : vérifie que setSousTypeProduits(null) vide la liste et détache les enfants")
	@Tag(SETTERS)
	@Test
	public final void testSetSousTypeProduitsNull() {
		
		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************
		
		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE TypeProduitJPATest - méthode testSetSousTypeProduitsNull() ********** ");
			System.out.println("CE TEST VERIFIE setSousTypeProduits(null).");
			System.out.println();
		}
		
		// ARRANGE - GIVEN
		final TypeProduitJPA parent = new TypeProduitJPA(1L, VETEMENT);
		final SousTypeProduitJPA enfant1 = new SousTypeProduitJPA(1L, VETEMENT_ENFANT, parent);
		final SousTypeProduitJPA enfant2 = new SousTypeProduitJPA(2L, "vêtement adulte", parent);
		
		assertTrue(parent.getSousTypeProduits().contains(enfant1), "précondition : parent contient enfant1 : ");
		assertTrue(parent.getSousTypeProduits().contains(enfant2), "précondition : parent contient enfant2 : ");
		assertSame(parent, enfant1.getTypeProduit(), "précondition : enfant1 pointe vers parent : ");
		assertSame(parent, enfant2.getTypeProduit(), "précondition : enfant2 pointe vers parent : ");
		
		// ACT - WHEN
		parent.setSousTypeProduits(null);
		
		// ASSERT - THEN
		assertNotNull(parent.getSousTypeProduits(), "la liste ne doit jamais être null : ");
		assertTrue(parent.getSousTypeProduits().isEmpty(), "setSousTypeProduits(null) doit vider la liste : ");
		assertNull(enfant1.getTypeProduit(), "setSousTypeProduits(null) doit détacher enfant1 : ");
		assertNull(enfant2.getTypeProduit(), "setSousTypeProduits(null) doit détacher enfant2 : ");
		
	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Teste la méthode setSousTypeProduits(List).</p>
	 * <ul>
	 * <li>Vérifie que setSousTypeProduits(liste) rattache correctement les enfants.</li>
	 * <li>Vérifie que l'ancienne liste est détachée.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testSetSousTypeProduitsListe() : vérifie le rattachement et le détachement via setSousTypeProduits(List)")
	@Tag(SETTERS)
	@Test
	public final void testSetSousTypeProduitsListe() {

		/* ********************************** */
		/* AFFICHAGE DANS LE TEST ou NON */
		final boolean affichage = false;
		/* ********************************** */

		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE TypeProduitJPATest - méthode testSetSousTypeProduitsListe() ********** ");
			System.out.println("CE TEST VERIFIE setSousTypeProduits(List).");
			System.out.println();
		}

		/* ARRANGE - GIVEN */
		final TypeProduitJPA parent = new TypeProduitJPA(1L, VETEMENT);

		final SousTypeProduitJPA ancien1 = new SousTypeProduitJPA(1L, "ancien 1", parent);
		final SousTypeProduitJPA ancien2 = new SousTypeProduitJPA(2L, "ancien 2", parent);

		assertTrue(parent.getSousTypeProduits().contains(ancien1), "précondition : parent contient ancien1 : ");
		assertTrue(parent.getSousTypeProduits().contains(ancien2), "précondition : parent contient ancien2 : ");

		final SousTypeProduitJPA nouveau1 = new SousTypeProduitJPA();
		nouveau1.setIdSousTypeProduit(10L);
		nouveau1.setSousTypeProduit("nouveau 1");

		final SousTypeProduitJPA nouveau2 = new SousTypeProduitJPA();
		nouveau2.setIdSousTypeProduit(11L);
		nouveau2.setSousTypeProduit("nouveau 2");

		final List<SousTypeProduitI> listeNouveaux = Arrays.asList(nouveau1, nouveau2);

		/* ACT - WHEN */
		parent.setSousTypeProduits(listeNouveaux);

		/* ASSERT - THEN */
		assertNotNull(parent.getSousTypeProduits(), "parent.getSousTypeProduits() ne doit pas être null : ");
		assertTrue(parent.getSousTypeProduits().size() == 2, "parent doit contenir 2 nouveaux enfants : ");

		assertNull(ancien1.getTypeProduit(), "ancien1 doit être détaché : ");
		assertNull(ancien2.getTypeProduit(), "ancien2 doit être détaché : ");

		assertSame(parent, nouveau1.getTypeProduit(), "nouveau1 doit référencer parent : ");
		assertSame(parent, nouveau2.getTypeProduit(), "nouveau2 doit référencer parent : ");

		assertTrue(parent.getSousTypeProduits().contains(nouveau1), "parent doit contenir nouveau1 : ");
		assertTrue(parent.getSousTypeProduits().contains(nouveau2), "parent doit contenir nouveau2 : ");

	} //___________________________________________________________________

		
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Teste le contrat "vue live" de getSousTypeProduits().</p>
	 * <ul>
	 * <li>La liste retournée est immuable (non modifiable).</li>
	 * <li>La liste retournée est une vue live : si le parent est modifié
	 * par une opération canonique, la vue reflète la modification.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testGetSousTypeProduitsVueLive() : vérifie que getSousTypeProduits() retourne une vue live immuable")
	@Tag(GETTERS)
	@Test
	public final void testGetSousTypeProduitsVueLive() {

		/* ********************************** */
		/* AFFICHAGE DANS LE TEST ou NON */
		final boolean affichage = false;
		/* ********************************** */

		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE TypeProduitJPATest - méthode testGetSousTypeProduitsVueLive() ********** ");
			System.out.println("CE TEST VERIFIE QUE getSousTypeProduits() RETOURNE UNE VUE LIVE IMMUABLE.");
			System.out.println();
		}

		/* ARRANGE - GIVEN */
		final TypeProduitJPA parent = new TypeProduitJPA(1L, VETEMENT, null);

		final List<? extends SousTypeProduitI> vue = parent.getSousTypeProduits();

		assertNotNull(vue, "La vue retournée par getSousTypeProduits() ne doit pas être null : ");
		assertTrue(vue.isEmpty(), "La vue doit être vide au départ : ");

		/* Vérifie immutabilité : clear() doit lever UnsupportedOperationException. */
		assertThrows(
				UnsupportedOperationException.class,
				() -> vue.clear(),
				"La liste retournée doit être immuable : ");

		final SousTypeProduitJPA enfant = new SousTypeProduitJPA();
		enfant.setIdSousTypeProduit(1L);
		enfant.setSousTypeProduit(VETEMENT_ENFANT);

		/* ACT - WHEN */
		parent.rattacherEnfantSTP(enfant);

		/* ASSERT - THEN */
		assertEquals(
				1,
				vue.size(),
				"La vue doit refléter la modification (vue live) : ");

		assertTrue(
				vue.contains(enfant),
				"La vue doit contenir l'enfant ajouté : ");

		assertSame(
				parent,
				enfant.getTypeProduit(),
				"L'enfant doit référencer le parent après rattachement : ");

	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Teste les méthodes de fail-fast pour les mauvaises instances.</p>
	 * <ul>
	 * <li>rattacherEnfantSTP(mauvaise instance) doit lever IllegalStateException.</li>
	 * <li>setSousTypeProduits(liste contenant mauvaise instance) doit lever IllegalStateException.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testFailFastMauvaiseInstance() : vérifie le fail-fast en cas de mauvaise instance SousTypeProduitI non-JPA")
	@Tag("fail-fast")
	@Test
	public final void testFailFastMauvaiseInstance() {

		/* ********************************** */
		/* AFFICHAGE DANS LE TEST ou NON */
		final boolean affichage = false;
		/* ********************************** */

		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE TypeProduitJPATest - méthode testFailFastMauvaiseInstance() ********** ");
			System.out.println("CE TEST VERIFIE LE FAIL-FAST SUR MAUVAISE INSTANCE.");
			System.out.println();
		}

		/* ARRANGE - GIVEN */
		final TypeProduitJPA parent = new TypeProduitJPA(1L, VETEMENT);
		final SousTypeProduitI mauvaiseInstance = creerMauvaiseInstanceSousTypeProduitI();

		/* ACT - WHEN / ASSERT - THEN */
		assertThrows(
				IllegalStateException.class,
				() -> {
					parent.rattacherEnfantSTP(mauvaiseInstance);
				},
				"rattacherEnfantSTP(mauvaise instance) doit lever IllegalStateException : ");

		final List<SousTypeProduitI> liste = new ArrayList<>();
		liste.add(mauvaiseInstance);

		assertThrows(
				IllegalStateException.class,
				() -> {
					parent.setSousTypeProduits(liste);
				},
				"setSousTypeProduits(liste contenant mauvaise instance) doit lever IllegalStateException : ");

	} //___________________________________________________________________



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Teste la méthode getSousTypeProduits().</p>
	 * <ul>
	 * <li>Vérifie que la liste est typée correctement (SousTypeProduitI).</li>
	 * <li>Vérifie que la liste est immuable (UnsupportedOperationException).</li>
	 * <li>Vérifie que les éléments sont bien des SousTypeProduitJPA.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testGetSousTypeProduitsTypage() : vérifie le typage et l'immuabilité de getSousTypeProduits()")
	@Tag("getters")
	@Test
	public final void testGetSousTypeProduitsTypage() {

		/* ********************************** */
		/* AFFICHAGE DANS LE TEST ou NON */
		final boolean affichage = false;
		/* ********************************** */

		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println("********** CLASSE TypeProduitJPATest - méthode testGetSousTypeProduitsTypage() **********");
		}

		/* ARRANGE - GIVEN */
		final TypeProduitJPA parent = new TypeProduitJPA(1L, VETEMENT);
		final SousTypeProduitJPA enfant = new SousTypeProduitJPA(1L, VETEMENT_ENFANT, parent);

		/* ACT - WHEN */
		final List<? extends SousTypeProduitI> sousTypes = parent.getSousTypeProduits();

		/* ASSERT - THEN */
		assertNotNull(sousTypes, "La liste ne doit pas être null : ");
		assertTrue(sousTypes.contains(enfant), "La liste doit contenir l'enfant : ");

		assertThrows(
				UnsupportedOperationException.class,
				() -> sousTypes.clear(),
				"La liste doit être immuable : ");

		for (final SousTypeProduitI st : sousTypes) {
			if (st == null) {
				continue;
			}
			assertTrue(
					st instanceof SousTypeProduitJPA,
					"Tous les éléments doivent être des SousTypeProduitJPA : ");
		}

	} //___________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>rattacherEnfantSTP(SousTypeProduitI)</b> :</p>
	 * <li>rattache un enfant au parent (bidirectionnel).</li>
	 * <li>ajoute l'enfant dans la liste du parent.</li>
	 * <li>ne crée pas de doublon si on rattache deux fois le même enfant.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testRattacherEnfantSTP() : vérifie le rattachement bidirectionnel et l'absence de doublons")
	@Tag("relations")
	@Test
	public final void testRattacherEnfantSTP() {

		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************

		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE TypeProduitJPATest - méthode testRattacherEnfantSTP() ********** ");
			System.out.println("CE TEST VERIFIE rattacherEnfantSTP(SousTypeProduitI).");
			System.out.println();
		}

		// ARRANGE - GIVEN
		final TypeProduitJPA parent = new TypeProduitJPA(1L, VETEMENT);

		final SousTypeProduitJPA enfant = new SousTypeProduitJPA();
		enfant.setIdSousTypeProduit(1L);
		enfant.setSousTypeProduit(VETEMENT_ENFANT);

		/* préconditions. */
		assertNotNull(parent.getSousTypeProduits(), "précondition : parent.getSousTypeProduits() ne doit pas être null : ");
		assertTrue(parent.getSousTypeProduits().isEmpty(), "précondition : parent ne doit contenir aucun enfant : ");
		assertNull(enfant.getTypeProduit(), "précondition : enfant.getTypeProduit() doit être null : ");

		// ACT - WHEN
		parent.rattacherEnfantSTP(enfant);

		// ASSERT - THEN
		assertSame(parent, enfant.getTypeProduit(), "rattacherEnfantSTP() doit rattacher l'enfant au parent : ");
		assertTrue(parent.getSousTypeProduits().contains(enfant), "rattacherEnfantSTP() doit ajouter l'enfant dans la liste du parent : ");
		assertTrue(parent.getSousTypeProduits().size() == 1, "le parent doit contenir exactement 1 enfant : ");

		// ACT - WHEN (2) : rattachement du même enfant une seconde fois.
		parent.rattacherEnfantSTP(enfant);

		// ASSERT - THEN : pas de doublons.
		assertTrue(parent.getSousTypeProduits().size() == 1, "rattacher 2 fois le même enfant ne doit pas créer de doublon : ");

	} //___________________________________________________________________


	
	/**
	 * <div>
	 * <ul>
	 * <p>Teste la méthode <b>detacherEnfantSTP(SousTypeProduitI)</b> :</p>
	 * <li>détache un enfant du parent (bidirectionnel).</li>
	 * <li>retire l'enfant de la liste du parent.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testDetacherEnfantSTP() : vérifie le détachement bidirectionnel et le retrait de la liste du parent")
	@Tag("relations")
	@Test
	public final void testDetacherEnfantSTP() {

		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************

		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE TypeProduitJPATest - méthode testDetacherEnfantSTP() ********** ");
			System.out.println("CE TEST VERIFIE detacherEnfantSTP(SousTypeProduitI).");
			System.out.println();
		}

		// ARRANGE - GIVEN
		final TypeProduitJPA parent = new TypeProduitJPA(1L, VETEMENT);
		final SousTypeProduitJPA enfant = new SousTypeProduitJPA(1L, VETEMENT_ENFANT, parent);

		/* préconditions. */
		assertSame(parent, enfant.getTypeProduit(), "précondition : enfant doit être rattaché au parent : ");
		assertTrue(parent.getSousTypeProduits().contains(enfant), "précondition : parent doit contenir enfant : ");
		assertTrue(parent.getSousTypeProduits().size() == 1, "précondition : parent doit contenir exactement 1 enfant : ");

		// ACT - WHEN
		parent.detacherEnfantSTP(enfant);

		// ASSERT - THEN
		assertNull(enfant.getTypeProduit(), "detacherEnfantSTP() doit détacher l'enfant (parent null) : ");
		assertFalse(parent.getSousTypeProduits().contains(enfant), "detacherEnfantSTP() doit retirer l'enfant de la liste du parent : ");
		assertTrue(parent.getSousTypeProduits().isEmpty(), "après détachement, la liste du parent doit être vide : ");

	} //___________________________________________________________________
	
	

	/**
	 * <p>affiche à la console un TypeProduitJPA.</p>
	 *
	 * @param pTypeProduit : TypeProduitJPA
	 */
	private void afficher(final TypeProduitJPA pTypeProduit) {
		
		if (pTypeProduit == null) {
			return;
		}
		
		System.out.println("id du TypeProduitJPA : " + pTypeProduit.getIdTypeProduit());
		System.out.println("TypeProduitJPA : " + pTypeProduit.getTypeProduit());		
	}
	
	
	
	/**
	 * <div>
	 * <p>Crée une mauvaise instance de {@code SousTypeProduitI}
	 * (non Entity JPA) pour tester le fail-fast.</p>
	 * <ul>
	 * <li>Utilise un Proxy dynamique Java.</li>
	 * <li>Retourne null pour toutes les méthodes.</li>
	 * <li>Instance non {@code SousTypeProduitJPA} par construction.</li>
	 * </ul>
	 * </div>
	 *
	 * @return SousTypeProduitI : une mauvaise instance (non-JPA).
	 */
	private SousTypeProduitI creerMauvaiseInstanceSousTypeProduitI() {
		
		final InvocationHandler handler = (proxy, method, args) -> {
			return null;
		};
		
		return (SousTypeProduitI) Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(),
				new Class<?>[] { SousTypeProduitI.class },
				handler);
		
	}

	

} 
