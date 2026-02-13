package levy.daniel.application.model.metier.produittype; // NOPMD by danyl on 08/02/2026 13:24

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
 * <p style="font-weight:bold;">CLASSE TypeProduitTest.java :</p>
 *
 * <p>
 * Cette classe teste la classe metier :
 * <span style="font-weight:bold;">TypeProduit</span>
 * </p>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 15 décembre 2025
 */
public class TypeProduitTest {

	 /* ---------------------- CONSTANTES ------------------------------ */
	
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
     * "relations"
     */
    public static final String RELATIONS = "relations";
    
    /**
     * "thread-safety"
     */
    public static final String THREAD_SAFETY = "thread-safety";
    
    /**
     * "Résultat obtenu : "
     */
    public static final String RESULTAT_OBTENU = "Résultat obtenu : ";

    /**
     * "Nombre de sous-types : "
     */
    public static final String NOMBRE_STP = "Nombre de sous-types : ";
    
    /**
     * "SousTypeProduit "
     */
    public static final String SOUSTYPEPRODUIT = "SousTypeProduit ";
    
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
     * "objet1.equals(objet2EqualsObjet1) : "
     */
    public static final String OBJET1_EQUALS_OBJET2EQUALSOBJET1 
    	= "objet1.equals(objet2EqualsObjet1) : ";

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
    public static final String CHEMISE_MANCHES_LONGUES 
    	= "chemise manches longues";

    /**
     * "chemise manches courtes"
     */
    public static final String CHEMISES_MANCHES_COURTES 
    	= "chemise manches courtes";

    /**
     * "sweat-shirt"
     */
    public static final String SWEATSHIRT = "sweat-shirt";

    /**
     * "soutien-gorge"
     */
    public static final String SOUTIEN_GORGE = "soutien-gorge";

    /**
     * "testAjouterEtRetirerSousTypeProduit"
     */
    public static final String TEST_AJOUTER_RETIRER_SOUS_TYPE_PRODUIT 
    	= "testAjouterEtRetirerSousTypeProduit";

    /**
     * "testInternalAddEtRemoveSousTypeProduit"
     */
    public static final String TEST_INTERNAL_ADD_REMOVE_SOUS_TYPE_PRODUIT 
    	= "testInternalAddEtRemoveSousTypeProduit";

    /**
     * "testSetSousTypeProduits"
     */
    public static final String TEST_SET_SOUS_TYPE_PRODUITS 
    	= "testSetSousTypeProduits";

    
    // ************************ATTRIBUTS**********************************/

    /**
     * TypeProduitI "vêtement"
     */
    public TypeProduitI typeProduitVetement;

    /**
     * SousTypeProduitI "vêtement pour homme" 
     * avec TypeProduitI "vêtement"
     */
    public SousTypeProduitI soustypeProduitVetementPourHomme;

    /**
     * SousTypeProduitI "vêtement pour femme" 
     * avec TypeProduitI "vêtement"
     */
    public SousTypeProduitI soustypeProduitVetementPourFemme;

    /**
     * SousTypeProduitI "vêtement pour enfant" 
     * avec TypeProduitI "vêtement"
     */
    public SousTypeProduitI soustypeProduitVetementPourEnfant;

    /**
     * <p>SousTypeProduitI "vêtement pour homme" 
     * avec TypeProduitI NULL.</p>
     * <p>new SousTypeProduitI(VETEMENT_HOMME);</p>
     */
    public SousTypeProduitI soustypeProduitVetementPourHommeSansTypeProduit;

    /**
     * ProduitI "chemise manches longues" 
     * avec sous-type ProduitI "vêtement pour homme"
     */
    public ProduitI produitChemiseManchesLonguesHomme;

    /**
     * ProduitI "chemise manches courtes" 
     * avec sous-type ProduitI "vêtement pour homme"
     */
    public ProduitI produitChemiseManchesCourtesHomme;

    /**
     * new ProduitI(SWEATSHIRT, soustypeProduitVetementPourHomme)
     */
    public ProduitI produitSweatshirtHomme;

    /**
     * new ProduitI(
     * "tee-shirt", soustypeProduitVetementPourHommeSansTypeProduit);
     */
    public ProduitI produitAvecMauvaisSousTypeProduit;

    /**
     * ProduitI 
     * "chemise manches longues" 
     * avec sous-type ProduitI "vêtement pour femme"
     */
    public ProduitI produitChemiseManchesLonguesFemme;

    /**
     * ProduitI 
     * "chemise manches courtes" 
     * avec sous-type ProduitI "vêtement pour femme"
     */
    public ProduitI produitChemiseManchesCourtesFemme;

    /**
     * ProduitI 
     * "sweatshirt" 
     * avec sous-type ProduitI "vêtement pour femme"
     */
    public ProduitI produitSweatshirtFemme;

    /**
     * ProduitI 
     * "soutien-gorge" 
     * avec sous-type ProduitI "vêtement pour femme"
     */
    public ProduitI produitSoutienGorgeFemme;

    /**
     * ProduitI 
     * "chemise manches longues" 
     * avec sous-type ProduitI "vêtement pour enfant"
     */
    public ProduitI produitChemiseManchesLonguesEnfant;

    /**
     * ProduitI 
     * "chemise manches courtes" 
     * avec sous-type ProduitI "vêtement pour enfant"
     */
    public ProduitI produitChemiseManchesCourtesEnfant;

    /**
     * ProduitI 
     * "sweatshirt" 
     * avec sous-type ProduitI "vêtement pour enfant"
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
    private static final Logger LOG = LogManager.getLogger(TypeProduitTest.class);

    // *************************METHODES************************************/

    /**
     * <div>
     * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
     * </div>
     */
    public TypeProduitTest() {
        super();
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
            System.out.println("********** CLASSE TypeProduitTest - méthode testConstructeurNull() ********** ");
            System.out.println("CE TEST VERIFIE LE FONCTIONNEMENT DU CONSTRUCTEUR D'ARITE NULLE.");
            System.out.println();
        }

        //**** ARRANGE - GIVEN
        final TypeProduitI objetConstructeurNull1 = new TypeProduit();
        final TypeProduitI objetConstructeurNull2 = new TypeProduit();

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
            System.out.println("*** TypeProduit objetConstructeurNull1 = new TypeProduit(); *** ");
            System.out.println("*** TypeProduit objetConstructeurNull2 = new TypeProduit(); *** ");
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
        final List<? extends SousTypeProduitI> sousTypeProduits = objetConstructeurNull1.getSousTypeProduits();

        /* AFFICHAGE A LA CONSOLE. */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println("*** APRES objetConstructeurNull1.setIdTypeProduit(1L); et objetConstructeurNull1.setTypeProduit(\"vêtement\"); ***");
            this.afficher(objetConstructeurNull1);
        }

        assertEquals(1L, objetConstructeurNull1.getIdTypeProduit(), "l'ID doit valoir 1 à ce stade : ");
        assertEquals(VETEMENT, objetConstructeurNull1.getTypeProduit(), "le typeProduit doit valoir \"vêtement\" à ce stade : ");
        assertTrue(sousTypeProduits.isEmpty(), "la liste sousTypeProduits doit être vide à ce stade : ");
    } //___________________________________________________________________
    
    

    /**
     * <div>
     * <ul>
     * <p>Teste la méthode <b>equals(Object pObj)</b> :</p>
     * <li>garantit que x.equals(x) retourne true.</li>
     * <li>garantit que x.equals(null) retourne false.</li>
     * <li>garantit que x.equals(mauvaise instance) retourne false.</li>
     * <li>garantit le contrat Java reflexif x.equals(x).</li>
     * <li>garantit le contrat Java symétrique x.equals(y) ----> y.equals(x).</li>
     * <li>garantit le contrat Java transitif x.equals(y) et y.equals(z) ----> x.equals(z).</li>
     * <li>garantit le contrat Java sur les hashcode x.equals(y) ----> x.hashcode() == y.hashcode().</li>
     * <li>garantit que les null sont bien gérés dans equals(Object pObj).</li>
     * <li>garantit que x.equals(null) retourne false (avec x non null).</li>
     * <li>garantit le bon fonctionnement de equals() en cas d'égalité métier.</li>
     * <li>garantit le bon fonctionnement de equals() en cas d'inégalité métier.</li>
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
            System.out.println("********** CLASSE TypeProduitTest - méthode testEquals() ********** ");
            System.out.println("CE TEST VERIFIE LE RESPECT DU CONTRAT Java Equals() et Hashcode().");
            System.out.println();
        }

        // ARRANGE - GIVEN
        final TypeProduitI objet1 = new TypeProduit(1L, PHOTOGRAPHIE, null);
        final TypeProduitI objet2EqualsObjet1 = new TypeProduit(2L, PHOTOGRAPHIE, null);
        final TypeProduitI objet3EqualsObjet1 = new TypeProduit(3L, PHOTOGRAPHIE, null);
        final TypeProduitI objet4PasEqualsObjet1 = new TypeProduit(4L, ANATOMIE, null);
        final NullPointerException mauvaiseInstance = new NullPointerException();

        /* AFFICHAGE A LA CONSOLE. */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println("*** APRES TypeProduit objet1 = new TypeProduit(1L, PHOTOGRAPHIE, null); ***");
            this.afficher(objet1);
            System.out.println();
            System.out.println("*** TypeProduit objet2EqualsObjet1 = new TypeProduit(2L, PHOTOGRAPHIE, null); ***");
            this.afficher(objet2EqualsObjet1);
            System.out.println();
            System.out.println("*** TypeProduit objet3EqualsObjet1 = new TypeProduit(3L, PHOTOGRAPHIE, null); ***");
            this.afficher(objet3EqualsObjet1);
            System.out.println();
            System.out.println("*** TypeProduit objet4PasEqualsObjet1 = new TypeProduit(4L, ANATOMIE, null); ***");
            this.afficher(objet4PasEqualsObjet1);
            System.out.println();
        }

        // ACT - WHEN
        final boolean isEqualsMemeinstance = objet1.equals(objet1);
        final boolean isEqualsNull = objet1.equals(null); // NOPMD by danyl on 12/01/2026 11:36
        final boolean isMauvaiseInstance = objet1.equals(mauvaiseInstance);
        final boolean isMemeInstance = objet1 == objet2EqualsObjet1;
        final boolean isEquals = objet1.equals(objet2EqualsObjet1);
        final boolean isEqualsSym = objet2EqualsObjet1.equals(objet1);
        final boolean isNonEquals = objet1.equals(objet4PasEqualsObjet1);

        /* AFFICHAGE A LA CONSOLE. */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("********************** EQUALS *******************************");
            System.out.println("objet1.equals(objet1) : " + isEqualsMemeinstance);
            System.out.println("objet1.equals(null) : " + isEqualsNull);
            System.out.println("objet1.equals(mauvaiseInstance) : " + isMauvaiseInstance);
            System.out.println("objet1 == objet2EqualsObjet1 : " + isMemeInstance);
            System.out.println(OBJET1_EQUALS_OBJET2EQUALSOBJET1 + isEquals);
            System.out.println("objet2EqualsObjet1.equals(objet1) : " + isEqualsSym);
            System.out.println("objet1.equals(objet4PasEqualsObjet1) : " + isNonEquals);
        }

        // ASSERT - THEN
        /* garantit que x.equals(x) retourne true.  */
        assertTrue(isEqualsMemeinstance, "x.equals(x) doit retourner true : ");
        /* garantit que x.equals(null) retourne false. */
        assertFalse(isEqualsNull, "x.equals(mauvaise null) doit retourner false : ");
        /* garantit que x.equals(mauvaise instance) retourne false ("unlikely-arg-type"). */
        assertFalse(isMauvaiseInstance, "x.equals(mauvaise instance) doit retourner false : ");

        /* garantit le contrat Java reflexif x.equals(x). */
        assertEquals(objet1, objet1, "x.equals(x) : ");

        /* garantit le contrat Java symétrique x.equals(y) ----> y.equals(x). */
        assertNotSame(objet1, objet2EqualsObjet1, "objet1 et objet2EqualsObjet1 ne sont pas la même instance : ");
        assertEquals(objet1, objet2EqualsObjet1, OBJET1_EQUALS_OBJET2EQUALSOBJET1);
        assertEquals(objet2EqualsObjet1, objet1, "objet2EqualsObjet1.equals(objet1) : ");

        /* garantit le contrat Java transitif x.equals(y) et y.equals(z) ----> x.equals(z). */
        assertEquals(objet1, objet2EqualsObjet1, OBJET1_EQUALS_OBJET2EQUALSOBJET1);
        assertEquals(objet2EqualsObjet1, objet3EqualsObjet1, "objet2EqualsObjet1.equals(objet3EqualsObjet1) : ");
        assertEquals(objet1, objet3EqualsObjet1, "objet1.equals(objet3EqualsObjet1) : ");

        /* garantit le contrat Java sur les hashcode x.equals(y) ----> x.hashcode() == y.hashcode(). */
        assertEquals(objet1.hashCode(), objet2EqualsObjet1.hashCode(), "objet1.hashCode().equals(objet2EqualsObjet1.hashCode()) : ");
        /* garantit que x.equals(null) retourne false (avec x non null). */
        assertNotNull(objet1, "objet1.equals(null) retourne false (avec objet1 non null) : ");

        /* garantit le bon fonctionnement de equals() en cas d'égalité métier. */
        assertEquals(objet1, objet2EqualsObjet1, OBJET1_EQUALS_OBJET2EQUALSOBJET1);

        /* garantit le bon fonctionnement de equals() en cas d'inégalité métier. */
        assertNotEquals(objet1, objet4PasEqualsObjet1, "objet1 n'est pas equals() avec objet4PasEqualsObjet1 : ");
        
    } //___________________________________________________________________
    
    
    
    /**
     * <div>
	 * <p>vérifie que l'égalité métier est insensible à la casse.</p>
	 * </div>
     *
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testEqualsIgnoreCase() : vérifie que l'égalité métier est insensible à la casse")
    @Tag("equals")
    @Test
    public final void testEqualsIgnoreCase() {

        /*
         * ARRANGE - GIVEN
         */
        final TypeProduitI objet1 = new TypeProduit(1L, "Vêtement", null);
        final TypeProduitI objet2 = new TypeProduit(2L, "vêtement", null);

        /*
         * ACT - WHEN
         */
        final boolean isEquals = objet1.equals(objet2);
        final boolean isEqualsSym = objet2.equals(objet1);

        /*
         * ASSERT - THEN
         */
        assertTrue(isEquals, "Deux TypeProduit doivent être égaux même si la casse diffère.");
        assertTrue(isEqualsSym, "L'égalité doit être symétrique même si la casse diffère.");
        assertEquals(objet1.hashCode(), objet2.hashCode()
                , "Si equals() est true malgré une casse différente, hashCode() doit être identique.");
    } //___________________________________________________________________
    
    

    /**
     * <div>
     * <p>Teste les méthodes <b>equals()</b> et <b>hashCode()</b> :</p>
     * <ul>
     * <li>garantit le contrat Java reflexif x.equals(x).</li>
     * <li>garantit le contrat Java symétrique x.equals(y) ----> y.equals(x).</li>
     * <li>garantit le contrat Java transitif x.equals(y) et y.equals(z) ----> x.equals(z).</li>
     * <li>garantit le contrat Java sur les hashcode x.equals(y) ----> x.hashcode() == y.hashcode().</li>
     * <li>garantit que les null sont bien gérés dans equals(Object pObj).</li>
     * <li>garantit que x.equals(null) retourne false (avec x non null).</li>
     * <li>garantit le bon fonctionnement de equals() en cas d'égalité métier.</li>
     * <li>garantit le bon fonctionnement de equals() en cas d'inégalité métier.</li>
     * <li>garantit la thread-safety des méthodes equals() et hashCode().</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings({ RESOURCE, UNUSED })
    @DisplayName("testEqualsHashCodeThreadSafe() : vérifie le respect du contrat Java pour equals() et hashCode() en environnement multi-thread")
    @Tag("equals")
    @Test
    public final void testEqualsHashCodeThreadSafe()
            throws InterruptedException, ExecutionException {

        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * ARRANGE - GIVEN : Création des objets nécessaires.
         */
        final TypeProduit type1 = new TypeProduit(VETEMENT);
        final TypeProduit type2 = new TypeProduit(VETEMENT);
        final TypeProduit type3 = new TypeProduit("photographie");

        /*
         * ACT - WHEN : Vérification des contrats Java.
         */

        /* garantit le contrat Java reflexif x.equals(x). */
        assertTrue(type1.equals(type1)
                , "x.equals(x) doit retourner true : ");

        /* garantit le contrat Java symétrique x.equals(y) ----> y.equals(x). */
        assertTrue(type1.equals(type2) && type2.equals(type1)
                , "x.equals(y) doit être symétrique : ");

        /* garantit le contrat Java transitif x.equals(y) et y.equals(z) ----> x.equals(z). */
        assertTrue(type1.equals(type2) && type2.equals(type1) && type1.equals(type2)
                , "x.equals(y) et y.equals(z) doit impliquer x.equals(z) : ");

        /* garantit que les null sont bien gérés dans equals(Object pObj). */
        assertFalse(type1.equals(type3)
                , "x.equals(y) doit retourner false si x != y : ");

        /* garantit que x.equals(null) retourne false (avec x non null). */
        assertFalse(type1.equals(null) // NOPMD by danyl on 13/02/2026 00:22
                , "x.equals(null) doit retourner false : "); // NOPMD by danyl on 08/02/2026 13:24

        /* garantit le contrat Java sur les hashcode x.equals(y) ----> x.hashcode() == y.hashcode(). */
        assertEquals(type1.hashCode(), type2.hashCode()
                , "x.equals(y) doit impliquer x.hashCode() == y.hashCode() : ");

        /*
         * ACT - WHEN : Test multi-thread pour vérifier la thread-safety.
         */
        final ExecutorService executor = Executors.newFixedThreadPool(10);
        final List<Callable<Boolean>> tasks = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            tasks.add(() -> type1.equals(type2));
        }

        /*
         * IMPORTANT : timeout pour éviter tout blocage infini
         * si une régression introduit un deadlock.
         */
        final List<Future<Boolean>> results = executor.invokeAll(tasks, 5, TimeUnit.SECONDS);

        executor.shutdown();

        /*
         * ASSERT - THEN : Vérification des résultats.
         */
        for (final Future<Boolean> result : results) {

            assertFalse(result.isCancelled()
                    , "Une tâche equals() ne doit pas être annulée (timeout) : ");

            assertTrue(result.get()
                    , "equals() doit toujours retourner le même résultat en environnement multi-thread : ");
        }

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {

            System.out.println();
            System.out.println("***** Test equals() et hashCode() en multi-thread réussi *****");
            System.out.println("Résultat de type1.equals(type2) : " + type1.equals(type2));
            System.out.println("Hashcode de type1 : " + type1.hashCode());
            System.out.println("Hashcode de type2 : " + type2.hashCode());
        }

    } //___________________________________________________________________



	/**
     * <div>
     * <ul>
     * <p>Teste la méthode <b>toString()</b> :</p>
     * <li>garantit le bon fonctionnement avec les null.</li>
     * <li>garantit le bon fonctionnement de toString()</li>
     * </ul>
     * </div>
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
            System.out.println("********** CLASSE TypeProduitTest - méthode testToString() ********** ");
            System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode toString().");
            System.out.println();
        }

        //**** ARRANGE - GIVEN
        final TypeProduitI objetConstructeurNull = new TypeProduit();

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
        assertEquals("TypeProduit [idTypeProduit=null, typeProduit=null]", objetConstructeurNull.toString(), "doit afficher TypeProduit [idTypeProduit=null, typeProduit=null] : ");

        //**** ARRANGE - GIVEN
        final TypeProduitI objet1 = new TypeProduit(PECHE);

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
        final String resultat = "TypeProduit [idTypeProduit=null, typeProduit=Pêche]";

        // ASSERT - THEN
        /* garantit le bon fonctionnement de toString() */
        assertEquals(resultat, objet1.toString(), "doit afficher toString() : ");
        
    } //___________________________________________________________________
    
    

    /**
	 * <div>
	 * <p>Teste la méthode <b>toString()</b> :</p>
	 * <ul>
	 * <li>garantit le bon fonctionnement avec les null.</li>
	 * <li>garantit le bon fonctionnement de toString() en environnement mono-thread.</li>
	 * <li>garantit le bon fonctionnement de toString() en environnement multi-thread.</li>
	 * <li>garantit que la représentation textuelle est cohérente avec les valeurs des champs.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings({ RESOURCE, UNUSED })
	@DisplayName("testToStringThreadSafe() : vérifie le thread-safety et le bon fonctionnement de toString()")
	@Tag("toString")
	@Test
	public final void testToStringThreadSafe() 
			throws InterruptedException, ExecutionException {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;
	
	    /*
	     * ARRANGE - GIVEN : Création des objets nécessaires.
	     */
	    final TypeProduit typeProduitAvecValeurs = new TypeProduit(1L, VETEMENT);
	    final TypeProduit typeProduitAvecNulls = new TypeProduit();
	    typeProduitAvecNulls.setIdTypeProduit(null);
	    typeProduitAvecNulls.setTypeProduit(null);
	    final TypeProduit typeProduitConstructeurNull = new TypeProduit();
	
	    /*
	     * Résultat attendu pour typeProduitAvecValeurs.
	     */
	    final String resultatAttenduAvecValeurs = "TypeProduit [idTypeProduit=1, typeProduit=vêtement]";
	
	    /*
	     * Résultat attendu pour typeProduitAvecNulls.
	     */
	    final String resultatAttenduAvecNulls = "TypeProduit [idTypeProduit=null, typeProduit=null]";
	
	    /*
	     * Résultat attendu pour typeProduitConstructeurNull.
	     */
	    final String resultatAttenduConstructeurNull = "TypeProduit [idTypeProduit=null, typeProduit=null]";
	
	    /*
	     * ACT - WHEN : Appels à toString() en mono-thread.
	     */
	    final String toStringAvecValeurs = typeProduitAvecValeurs.toString();
	    final String toStringAvecNulls = typeProduitAvecNulls.toString();
	    final String toStringConstructeurNull = typeProduitConstructeurNull.toString();
	
	    /*
	     * AFFICHAGE A LA CONSOLE.
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("***** Test toString() en mono-thread *****");
	        System.out.println("typeProduitAvecValeurs.toString() : " + toStringAvecValeurs);
	        System.out.println("typeProduitAvecNulls.toString() : " + toStringAvecNulls);
	        System.out.println("typeProduitConstructeurNull.toString() : " + toStringConstructeurNull);
	    }
	
	    /*
	     * ASSERT - THEN : Vérification des résultats en mono-thread.
	     */
	    assertEquals(resultatAttenduAvecValeurs, toStringAvecValeurs,
	            "toString() doit retourner 'TypeProduit [idTypeProduit=1, typeProduit=vêtement]' : ");
	    assertEquals(resultatAttenduAvecNulls, toStringAvecNulls,
	            "toString() doit retourner 'TypeProduit [idTypeProduit=null, typeProduit=null]' : ");
	    assertEquals(resultatAttenduConstructeurNull, toStringConstructeurNull,
	            "toString() doit retourner 'TypeProduit [idTypeProduit=null, typeProduit=null]' : ");
	
	    /*
	     * ACT - WHEN : Appels concurrents à toString() pour vérifier la thread-safety.
	     */
	    final ExecutorService executor = Executors.newFixedThreadPool(10);
	    final List<Callable<String>> tasks = new ArrayList<>();
	    for (int i = 0; i < 100; i++) {
	        tasks.add(() -> typeProduitAvecValeurs.toString());
	    }
	
	    final List<Future<String>> results = executor.invokeAll(tasks);
	    executor.shutdown();
	
	    /*
	     * ASSERT - THEN : Vérification des résultats en multi-thread.
	     */
	    for (final Future<String> result : results) {
	        assertEquals(resultatAttenduAvecValeurs, result.get(),
	                "toString() doit toujours retourner le même résultat en environnement multi-thread : ");
	    }
	
	    /*
	     * AFFICHAGE A LA CONSOLE.
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("***** Test toString() en multi-thread réussi *****");
	        System.out.println("Résultat attendu : " + resultatAttenduAvecValeurs);
	        System.out.println(RESULTAT_OBTENU + results.get(0).get());
	    }
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>vérifie le bon affichage de afficherSousTypeProduits()</p>
	 * </div>
	 * @throws CloneNotSupportedException
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName("testAfficherSousTypeProduits() : vérifie le comportement de la méthode afficherSousTypeProduits()")
	@Tag("affichage")
	@Test
	public final void testAfficherSousTypeProduits() throws CloneNotSupportedException {
	    // **********************************
	    // AFFICHAGE DANS LE TEST ou NON
	    final boolean affichage = false;
	    // **********************************
	
	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("********** CLASSE TypeProduitTest - méthode testAfficherSousTypeProduits() ********** ");
	        System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode afficherSousTypeProduits().");
	        System.out.println();
	    }
	
	    // *** ARRANGE - GIVEN
	    this.creerVetement();
	
	    // ACT - WHEN
	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println(this.typeProduitVetement.afficherSousTypeProduits());
	    }
	
	    // ASSERT - THEN
	    assertEquals("Sous-Types de Produit=[vêtement pour homme, vêtement pour femme, vêtement pour enfant]",
	            this.typeProduitVetement.afficherSousTypeProduits(),
	            "Bon affichage : ");
	
	    final TypeProduit cloneTP = ((TypeProduit) this.typeProduitVetement).clone();
	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("******* afficher le clone du TypeProduit this.typeProduitVetement *******");
	        this.afficher(cloneTP);
	    }
	} //___________________________________________________________________



	/**
     * <div>
     * <ul>
     * <p>Teste la méthode <b>compareTo(Produit pObject)</b> :</p>
     * <li>garantit que compareTo(memeInstance) retourne 0.</li>
     * <li>garantit que compareTo(null) retourne un nombre négatif.</li>
     * <li>garantit le Contrat Java : x.equals(y) ---> x.compareTo(y) == 0.</li>
     * <li>garantit que les null sont bien gérés dans compareTo(Produit pObject).</li>
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
            System.out.println("********** CLASSE TypeProduitTest - méthode testCompareTo() ********** ");
            System.out.println("CE TEST VERIFIE LE RESPECT des contrats Java de la méthode compareTo().");
            System.out.println();
        }

        // ARRANGE - GIVEN
        /* TypeProduit */
        final TypeProduitI objet1 = new TypeProduit(PECHE);
        final TypeProduitI objetEqualsObjet1 = new TypeProduit(PECHE);
        final TypeProduitI objet1MemeInstance = objet1;
        final TypeProduitI objet2AvantObjet1 = new TypeProduit(OUTILLAGE);
        final TypeProduitI objet3ApresObjet1 = new TypeProduit(RESTAURATION);

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
        final int compareMemeInstance = objet1.compareTo(objet1MemeInstance);
        final int compareToNull = objet1.compareTo(null);
        final int compareToEquals = objet1.compareTo(objetEqualsObjet1);

        // ASSERT - THEN
        /* garantit que compareTo(memeInstance) retourne 0. */
        assertTrue(compareMemeInstance == 0, "compareTo(memeInstance) doit retourner 0 : ");

        /* garantit que compareTo(null) retourne -1. */
        assertTrue(compareToNull < 0, "compareTo(null) doit retourner négatif : ");

        /* garantit le Contrat Java : x.equals(y) ---> x.compareTo(y) == 0. */
        assertNotSame(objet1, objetEqualsObjet1, "objet1 n'est pas la même instance que objetEqualsObjet1 : ");
        assertEquals(objet1, objetEqualsObjet1, "objet1 equals objetEqualsObjet1 : ");
        assertEquals(objet1.hashCode(), objetEqualsObjet1.hashCode(), "objet1.hashCode() == objetEqualsObjet1.hashCode() : ");
        assertTrue(compareToEquals == 0, "objet1 equals objetEqualsObjet1 ----> objet1.compareTo(objetEqualsObjet1) == 0 : ");
        assertTrue(objet1.compareTo(objetEqualsObjet1) == 0, "objet1.compareTo(objetEqualsObjet1) doit retourner 0 : ");
        assertTrue(objet1.compareTo(objet1MemeInstance) == 0, "objet1.compareTo(objet1MemeInstance) doit retourner 0 : ");
        assertTrue(objet1.compareTo(objet2AvantObjet1) > 0, "objet1.compareTo(objet2AvantObjet1) doit retourner > 0 : ");
        assertTrue(objet1.compareTo(objet3ApresObjet1) < 0, "objet1.compareTo(objet3ApresObjet1) doit retourner < 0 : ");

        //*** ARRANGE - GIVEN
        final TypeProduitI objetConstructeurNull1 = new TypeProduit();
        final TypeProduitI objetConstructeurNull2 = new TypeProduit();

        final TypeProduitI objetAvecValeurV1Vide1 = new TypeProduit(1L, "", null);
        final TypeProduitI objetAvecValeurV1Vide2 = new TypeProduit(2L, "", null);
        final TypeProduitI objetAvecValeurV1Null2 = new TypeProduit(2L, null, null);
        final TypeProduitI objetAvecValeurV1NonVide3 = new TypeProduit(3L, "toto", null);

        /* AFFICHAGE A LA CONSOLE. */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println(" ********* Avec des valeurs vides ou null **************** ");
            System.out.println("objetConstructeurNull1 : " + objetConstructeurNull1.toString());
            System.out.println("objetConstructeurNull2 : " + objetConstructeurNull2.toString());
            System.out.println("objetAvecValeurV1Vide1 : " + objetAvecValeurV1Vide1.toString());
            System.out.println("objetAvecValeurV1Vide2 : " + objetAvecValeurV1Vide2.toString());
            System.out.println("objetAvecValeurV1Null2 : " + objetAvecValeurV1Null2.toString());
            System.out.println("objetAvecValeurV1NonVide3 : " + objetAvecValeurV1NonVide3.toString());
        }

        final List<TypeProduitI> liste = new ArrayList<TypeProduitI>();
        liste.add(objet1);
        liste.add(objetEqualsObjet1);
        liste.add(objet1MemeInstance);
        liste.add(objet2AvantObjet1);
        liste.add(objet3ApresObjet1);
        liste.add(objetConstructeurNull1);
        liste.add(objetConstructeurNull2);
        liste.add(objetAvecValeurV1Vide1);
        liste.add(objetAvecValeurV1Vide2);
        liste.add(objetAvecValeurV1Null2);
        liste.add(objetAvecValeurV1NonVide3);

        Collections.sort(liste);

        // ACT - WHEN
        final int compareToConstructeurNull = objetConstructeurNull1.compareTo(objetConstructeurNull2);
        final int compareToAvecValeursVides = objetAvecValeurV1Vide1.compareTo(objetAvecValeurV1Vide2);
        final int compareToAvecValeurNonVide = objetAvecValeurV1NonVide3.compareTo(objetAvecValeurV1Vide1);
        final int compareNullToVide = objetAvecValeurV1Null2.compareTo(objetAvecValeurV1Vide1);

        /* AFFICHAGE A LA CONSOLE. */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println(" ********* COMPARAISONS Avec des valeurs vides ou null **************** ");
            System.out.println("objetConstructeurNull1.compareTo(objetConstructeurNull2) : " + compareToConstructeurNull);
            System.out.println("objetAvecValeurV1Vide1.compareTo(objetAvecValeurV1Vide2) : " + compareToAvecValeursVides);
            System.out.println("objetAvecValeurV1NonVide3.compareTo(objetAvecValeurV1Vide1) : " + compareToAvecValeurNonVide);
            System.out.println("objetAvecValeurV1Null2.compareTo(objetAvecValeurV1Vide1) : " + compareNullToVide);
            System.out.println();
            System.out.println(" ****** impression de la liste : ********* ");
            liste.stream().forEach(tp -> System.out.println(tp));
        }

        // ASSERT - THEN
        /* garantit que les null sont bien gérés dans compareTo(). */
        assertTrue(compareToConstructeurNull == 0, "objetConstructeurNull1.compareTo(objetConstructeurNull2) == 0 : ");
        assertTrue(compareToAvecValeursVides == 0, "objetAvecValeurV1Vide1.compareTo(objetAvecValeurV1Vide2)  == 0 : ");
        assertTrue(compareToAvecValeurNonVide > 0, "objetAvecValeurV1NonVide3.compareTo(objetAvecValeurV1Vide1)  > 0 : ");
        assertTrue(objetAvecValeurV1Null2.compareTo(objetAvecValeurV1Vide1) > 0, "objetAvecValeurV1Null2.compareTo(objetAvecValeurV1Vide1) doit retourner > 0 : ");
        
    } //___________________________________________________________________
    
    

    /**
     * <div>
     * <p>Teste la méthode <b>compareTo(...)</b> en environnement multi-thread.</p>
     * <ul>
     * <li>Vérifie que compareTo(...) ne deadlock pas.</li>
     * <li>Utilise un timeout sur invokeAll(...) pour éviter un blocage infini.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings({ "resource", "unused" })
    @DisplayName("testCompareToThreadSafe() : compareTo(...) ne doit pas deadlocker")
    @Tag(THREAD_SAFETY)
    @Test
    public final void testCompareToThreadSafe() throws Exception {

        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * ARRANGE - GIVEN
         */
        final TypeProduitI typeProduit1 = new TypeProduit(1L, VETEMENT, null);
        final TypeProduitI typeProduit2 = new TypeProduit(2L, OUTILLAGE, null);

        /*
         * ACT - WHEN
         */
        final ExecutorService executor = Executors.newFixedThreadPool(2);

        try {

            final List<Callable<Integer>> tasks = new ArrayList<>();

            /*
             * Deux sens en parallèle pour tester l'absence de deadlock :
             * A.compareTo(B) et B.compareTo(A).
             */
            tasks.add(() -> typeProduit1.compareTo(typeProduit2));
            tasks.add(() -> typeProduit2.compareTo(typeProduit1));

            final List<Future<Integer>> futures
                = executor.invokeAll(tasks, 5, TimeUnit.SECONDS);

            /*
             * ASSERT - THEN
             * Si un deadlock survient, les tâches seront annulées (timeout)
             * et au moins un Future sera isCancelled().
             */
            for (final Future<Integer> f : futures) {
                assertFalse(f.isCancelled()
                    , "compareTo(...) ne doit pas deadlocker : aucune tâche ne doit être annulée par timeout.");
            }

        } finally {

            executor.shutdownNow();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("***** Test compareTo(...) thread-safe réussi *****");
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
            System.out.println("********** CLASSE TypeProduitTest - méthode testClone() ********** ");
            System.out.println("CE TEST VERIFIE LE RESPECT des contrats Java de la méthode clone().");
            System.out.println();
        }

        //***** ARRANGE - GIVEN
        final TypeProduit objetConstructeurNull = new TypeProduit();

        // ACT - WHEN
        final TypeProduit objetConstructeurNullClone = objetConstructeurNull.clone();

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
        /* TypeProduit */
        final TypeProduit objet1 = new TypeProduit(PECHE);

        // ACT - WHEN
        final TypeProduit objet1Clone = objet1.clone();

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
        assertFalse(objet1.getTypeProduit().equals(objet1Clone.getTypeProduit()), "la modification du TypeProduit dans le clone ne doit pas modifier le TypeProduit dans l'objet cloné : ");
        
    } //___________________________________________________________________
    
    

    /**
     * <div>
     * <p>Teste la méthode <b>cloneWithoutChildren()</b>.</p>
     * <ul>
     * <li>Vérifie que le clone n'est pas la même instance que l'original.</li>
     * <li>Vérifie que le clone conserve les attributs simples (id, libellé).</li>
     * <li>Vérifie que le clone ne possède aucun enfant SousTypeProduitI.</li>
     * <li>Vérifie que les enfants de l'original restent rattachés à l'original
     * et ne sont pas déplacés vers le clone.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testCloneWithoutChildren() : vérifie le comportement de cloneWithoutChildren()")
    @Tag("clone")
    @Test
    public final void testCloneWithoutChildren() {

        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * ARRANGE - GIVEN : TypeProduit avec 2 SousTypeProduit enfants.
         */
        final TypeProduitI original = new TypeProduit(1L, VETEMENT, null);

        final SousTypeProduitI enfant1 = new SousTypeProduit(1L, VETEMENT_HOMME, null);
        final SousTypeProduitI enfant2 = new SousTypeProduit(2L, VETEMENT_FEMME, null);

        original.rattacherEnfantSTP(enfant1);
        original.rattacherEnfantSTP(enfant2);

        /*
         * ACT - WHEN : clonage sans enfants.
         */
        final TypeProduitI clone = original.cloneWithoutChildren();

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("********** CLASSE TypeProduitTest - méthode testCloneWithoutChildren() ********** ");
            System.out.println("CE TEST VERIFIE LE FONCTIONNEMENT DE cloneWithoutChildren().");
            System.out.println();
            this.afficher(original);
            System.out.println();
            this.afficher(clone);
            System.out.println();
        }

        /*
         * ASSERT - THEN : Vérifications.
         */

        /*
         * Le clone doit exister et ne pas être la même instance.
         */
        assertNotNull(clone, "clone doit être non null : ");
        assertNotSame(original, clone, "clone ne doit pas être la même instance que l'original : ");

        /*
         * Le clone doit conserver les attributs simples.
         */
        assertEquals(original.getIdTypeProduit(), clone.getIdTypeProduit(), "Le clone doit conserver l'idTypeProduit : ");
        assertEquals(original.getTypeProduit(), clone.getTypeProduit(), "Le clone doit conserver le typeProduit : ");

        /*
         * Le clone ne doit contenir aucun enfant SousTypeProduit.
         */
        assertTrue(clone.getSousTypeProduits().isEmpty(), "Le clone ne doit contenir aucun SousTypeProduit : ");

        /*
         * L'original doit conserver ses enfants.
         */
        assertEquals(2, original.getSousTypeProduits().size(), "L'original doit conserver ses 2 SousTypeProduit : ");

        /*
         * Les enfants doivent rester rattachés à l'original (et pas au clone).
         */
        assertSame(original, enfant1.getTypeProduit(), "enfant1 doit rester rattaché à l'original : ");
        assertSame(original, enfant2.getTypeProduit(), "enfant2 doit rester rattaché à l'original : ");
        assertNotSame(clone, enfant1.getTypeProduit(), "enfant1 ne doit pas être rattaché au clone : ");
        assertNotSame(clone, enfant2.getTypeProduit(), "enfant2 ne doit pas être rattaché au clone : ");
    } //___________________________________________________________________

    

    /**
     * <div>
     * <p>Teste le clonage profond thread-safe de TypeProduit :</p>
     * <ul>
     * <li>garantit que clone() ne lève pas d'exception.</li>
     * <li>garantit que cloneDeep() produit un clone profond.</li>
     * <li>garantit que deepClone() gère correctement les cycles.</li>
     * <li>garantit que le clonage est thread-safe en environnement concurrent.</li>
     * <li>garantit que les enfants (SousTypeProduit) sont correctement clonés.</li>
     * <li>garantit que les modifications du clone n'affectent pas l'original.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings({ UNUSED, RESOURCE })
    @DisplayName("testCloneThreadSafe() : vérifie le thread-safety du clonage profond")
    @Tag("clone")
    @Test
    public final void testCloneThreadSafe()
            throws CloneNotSupportedException, InterruptedException, ExecutionException {
        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * ARRANGE - GIVEN : Création des objets nécessaires.
         */
        final TypeProduitI original = new TypeProduit(1L, "vêtement");

        /*
         * Création des sous-types et attachement au parent via ajouterSTPauProduit().
         */
        final SousTypeProduit sousType1 = new SousTypeProduit(10L, "vêtement homme", null);
        final SousTypeProduit sousType2 = new SousTypeProduit(11L, "vêtement femme", null);

        /*
         * Attachement des sous-types au parent via la méthode dédiée.
         */
        original.rattacherEnfantSTP(sousType1);
        original.rattacherEnfantSTP(sousType2);

        /*
         * ACT - WHEN : Clonage en mono-thread.
         */
        final TypeProduit cloneMonoThread = ((TypeProduit) original).clone();

        /*
         * Vérification que le clone est différent de l'original.
         */
        assertNotSame(original, cloneMonoThread
                , "Le clone doit être une nouvelle instance distincte de l'original : ");

        /*
         * Vérification que les champs sont égaux.
         */
        assertEquals(original.getIdTypeProduit(), cloneMonoThread.getIdTypeProduit()
                , "L'ID doit être identique entre l'original et le clone : ");
        assertEquals(original.getTypeProduit(), cloneMonoThread.getTypeProduit()
                , "Le typeProduit doit être identique entre l'original et le clone : ");

        /*
         * Vérification que les enfants sont clonés.
         */
        assertEquals(original.getSousTypeProduits().size(), cloneMonoThread.getSousTypeProduits().size()
                , "Le nombre d'enfants doit être identique entre l'original et le clone : ");

        /*
         * Vérification que les enfants sont des instances distinctes.
         */
        assertNotSame(original.getSousTypeProduits().get(0), cloneMonoThread.getSousTypeProduits().get(0)
                , "Les enfants doivent être des instances distinctes entre l'original et le clone : ");

        /*
         * Vérification que les enfants clonés ont bien le clone comme parent.
         */
        assertSame(cloneMonoThread, cloneMonoThread.getSousTypeProduits().get(0).getTypeProduit()
                , "Le parent des enfants clonés doit être le clone, pas l'original : ");

        /*
         * ACT - WHEN : Test de la thread-safety du clonage.
         * Exécution de 100 clonages concurrents.
         */
        final ExecutorService executor = Executors.newFixedThreadPool(10);
        final List<Callable<TypeProduit>> tasks = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            tasks.add(() -> ((TypeProduit) original).clone());
        }

        final List<Future<TypeProduit>> results = executor.invokeAll(tasks, 5, TimeUnit.SECONDS);
        executor.shutdown();

        /*
         * ASSERT - THEN : Vérification des résultats en multi-thread.
         */
        for (final Future<TypeProduit> result : results) {

            /*
             * Le test doit échouer si une tâche a été annulée (timeout/deadlock).
             */
            assertFalse(result.isCancelled()
                    , "Une tâche de clonage a été annulée (timeout) : risque de blocage/délockage.");

            final TypeProduit clone = result.get();

            /*
             * Vérification que chaque clone est différent de l'original.
             */
            assertNotSame(original, clone
                    , "Chaque clone doit être une nouvelle instance distincte de l'original : ");

            /*
             * Vérification que les enfants sont clonés.
             */
            assertEquals(original.getSousTypeProduits().size(), clone.getSousTypeProduits().size()
                    , "Le nombre d'enfants doit être identique entre l'original et chaque clone : ");

            /*
             * Vérification que les enfants sont des instances distinctes.
             */
            assertNotSame(original.getSousTypeProduits().get(0), clone.getSousTypeProduits().get(0)
                    , "Les enfants doivent être des instances distinctes entre l'original et chaque clone : ");

            /*
             * Vérification que les enfants clonés ont bien le clone comme parent.
             */
            assertSame(clone, clone.getSousTypeProduits().get(0).getTypeProduit()
                    , "Le parent des enfants clonés doit être le clone, pas l'original : ");
        }

        /*
         * ACT - WHEN : Vérification de l'indépendance entre original et clone.
         */
        cloneMonoThread.setTypeProduit("nouveau type");
        cloneMonoThread.getSousTypeProduits().get(0).setSousTypeProduit("nouveau sous-type");

        /*
         * ASSERT - THEN : Vérification que l'original n'est pas affecté.
         */
        assertNotEquals(original.getTypeProduit(), cloneMonoThread.getTypeProduit()
                , "La modification du clone ne doit pas affecter l'original : ");
        assertNotEquals(original.getSousTypeProduits().get(0).getSousTypeProduit()
                , cloneMonoThread.getSousTypeProduits().get(0).getSousTypeProduit()
                , "La modification des enfants du clone ne doit pas affecter l'original : ");

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (affichage) {
            System.out.println();
            System.out.println("***** Test clone() en multi-thread réussi *****");
            System.out.println("Original : " + original);
            System.out.println("Clone mono-thread : " + cloneMonoThread);
            System.out.println("Clone multi-thread (exemple) : " + results.get(0).get());
        }
    } //___________________________________________________________________



	/**
	 * <div>
	 * <p>Teste les méthodes <b>rattacherEnfantSTP</b> et 
	 * <b>detacherEnfantSTP</b>.</p>
	 * <ul>
	 * <li>garantit que l'ajout d'un SousTypeProduit à un TypeProduit fonctionne correctement.</li>
	 * <li>garantit que le retrait d'un SousTypeProduit d'un TypeProduit fonctionne correctement.</li>
	 * <li>vérifie la synchronisation des relations.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName(TEST_AJOUTER_RETIRER_SOUS_TYPE_PRODUIT + "() : vérifie le bon fonctionnement des méthodes rattacherEnfantSTP et detacherEnfantSTP")
	@Tag(RELATIONS)
	@Test
	public final void testAjouterEtRetirerSousTypeProduit() {
	    // **********************************
	    // AFFICHAGE DANS LE TEST ou NON
	    final boolean affichage = false;
	    // **********************************
	
	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("********** CLASSE TypeProduitTest - méthode " + TEST_AJOUTER_RETIRER_SOUS_TYPE_PRODUIT + "() ********** ");
	        System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT DES METHODES rattacherEnfantSTP ET detacherEnfantSTP.");
	        System.out.println();
	    }
	
	    // ARRANGE - GIVEN
	    final TypeProduitI typeProduit = new TypeProduit(VETEMENT);
	    final SousTypeProduitI sousTypeProduit = new SousTypeProduit(VETEMENT_HOMME);
	
	    // ACT - WHEN
	    typeProduit.rattacherEnfantSTP(sousTypeProduit);
	
	    // ASSERT - THEN
	    assertTrue(typeProduit.getSousTypeProduits().contains(sousTypeProduit), "Le SousTypeProduit doit être ajouté à la liste des sous-types du TypeProduit.");
	    assertEquals(typeProduit, sousTypeProduit.getTypeProduit(), "Le TypeProduit doit être correctement synchronisé avec le SousTypeProduit.");
	
	    // ACT - WHEN
	    typeProduit.detacherEnfantSTP(sousTypeProduit);
	
	    // ASSERT - THEN
	    assertFalse(typeProduit.getSousTypeProduits().contains(sousTypeProduit), "Le SousTypeProduit doit être retiré de la liste des sous-types du TypeProduit.");
	    assertNull(sousTypeProduit.getTypeProduit(), "Le TypeProduit du SousTypeProduit doit être null après le retrait.");
	    
	} //___________________________________________________________________
	
	
	
    /**
     * <div>
     * <p>Teste la méthode <b>rattacherEnfantSTP(...)</b> en environnement mono-thread
     * sur des cas limites et des scénarios métier.</p>
     * <ul>
     * <li>pEnfant == null : ne fait rien (pas d'exception, liste inchangée).</li>
     * <li>libellé blank (null/espaces) : ne fait rien.</li>
     * <li>appel répété avec le même enfant : pas de doublon.</li>
     * <li>enfant déjà rattaché à un autre parent : bascule proprement vers le nouveau parent.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testRattacherEnfantSTP() : cas limites (null/blank/doublon/bascule parent)")
    @Tag(RELATIONS)
    @Test
    public final void testRattacherEnfantSTPCasLimitesMonoThread() {

        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * ARRANGE - GIVEN
         */
        final TypeProduitI parent1 = new TypeProduit(VETEMENT);
        final TypeProduitI parent2 = new TypeProduit(OUTILLAGE);

        /*
         * ACT - WHEN : pEnfant == null.
         */
        parent1.rattacherEnfantSTP(null);

        /*
         * ASSERT - THEN : ne fait rien.
         */
        assertTrue(parent1.getSousTypeProduits().isEmpty()
                , "rattacherEnfantSTP(null) ne doit pas modifier la liste.");

        /*
         * ACT - WHEN : libellé blank.
         */
        final SousTypeProduitI enfantBlank = new SousTypeProduit("   ");
        parent1.rattacherEnfantSTP(enfantBlank);

        /*
         * ASSERT - THEN : ne fait rien.
         */
        assertTrue(parent1.getSousTypeProduits().isEmpty()
                , "rattacherEnfantSTP(enfantBlank) ne doit pas modifier la liste.");
        assertNull(enfantBlank.getTypeProduit()
                , "Un enfant blank ne doit pas être rattaché à un parent.");

        /*
         * ACT - WHEN : appel répété avec le même enfant.
         */
        final SousTypeProduitI enfant = new SousTypeProduit(VETEMENT_HOMME);

        parent1.rattacherEnfantSTP(enfant);
        parent1.rattacherEnfantSTP(enfant);

        /*
         * ASSERT - THEN : pas de doublon et cohérence bidirectionnelle.
         */
        assertEquals(1, parent1.getSousTypeProduits().size()
                , "Un même enfant rattaché plusieurs fois ne doit pas créer de doublon.");
        assertTrue(parent1.getSousTypeProduits().contains(enfant)
                , "La liste doit contenir l'enfant rattaché.");
        assertEquals(parent1, enfant.getTypeProduit()
                , "L'enfant doit avoir parent1 comme parent après rattachement.");

        /*
         * ACT - WHEN : bascule de parent (enfant déjà rattaché à parent1).
         */
        parent2.rattacherEnfantSTP(enfant);

        /*
         * ASSERT - THEN : l'enfant est détaché de parent1 et rattaché à parent2.
         */
        assertFalse(parent1.getSousTypeProduits().contains(enfant)
                , "Après bascule, parent1 ne doit plus contenir l'enfant.");
        assertTrue(parent2.getSousTypeProduits().contains(enfant)
                , "Après bascule, parent2 doit contenir l'enfant.");
        assertEquals(parent2, enfant.getTypeProduit()
                , "Après bascule, l'enfant doit avoir parent2 comme parent.");

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("***** Test rattacherEnfantSTP() cas limites mono-thread réussi *****");
            System.out.println(NOMBRE_STP + parent2.getSousTypeProduits().size());
        }

    } //___________________________________________________________________



    /**
     * <div>
     * <p>Teste la méthode rattacherEnfantSTP() en environnement multi-thread.</p>
     * <ul>
     * <li>Vérifie que les ajouts concurrents ne corrompent pas la liste des sous-types.</li>
     * <li>Utilise un ExecutorService pour simuler des modifications concurrentes.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings({ RESOURCE, UNUSED })
    @DisplayName("testRattacherEnfantSTPThreadSafe() : vérifie le thread-safety de rattacherEnfantSTP()")
    @Tag(THREAD_SAFETY)
    @Test
    public final void testRattacherEnfantSTPThreadSafe()
            throws InterruptedException, ExecutionException {

        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * ARRANGE - GIVEN : Création d'un TypeProduit.
         */
        final TypeProduitI typeProduit = new TypeProduit(1L, VETEMENT, null);

        /*
         * ACT - WHEN : Exécution concurrente d'ajouts.
         */
        final ExecutorService executor = Executors.newFixedThreadPool(10);
        final List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            final SousTypeProduitI sousTypeProduit = new SousTypeProduit((long) i, SOUSTYPEPRODUIT + i, null);
            tasks.add(() -> {
                typeProduit.rattacherEnfantSTP(sousTypeProduit);
                return null;
            });
        }

        final List<Future<Void>> futures = executor.invokeAll(tasks, 5, TimeUnit.SECONDS);
        executor.shutdown();

        /*
         * ASSERT - THEN : Vérification qu'aucune tâche n'a été annulée (timeout).
         */
        for (final Future<Void> f : futures) {
            assertFalse(f.isCancelled()
                    , "Une tâche d'ajout a été annulée (timeout) : risque de blocage/délockage.");
            f.get();
        }

        /*
         * ASSERT - THEN : Vérification de la cohérence de la liste.
         */
        assertEquals(50, typeProduit.getSousTypeProduits().size()
                , "La liste des sous-types doit contenir 50 éléments après des ajouts concurrents.");

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("***** Test rattacherEnfantSTP() en multi-thread réussi *****");
            System.out.println(NOMBRE_STP + typeProduit.getSousTypeProduits().size());
        }

    } //___________________________________________________________________



	/**
	 * <div>
	 * <p>Teste la méthode <b>detacherEnfantSTP()</b> en environnement multi-thread.</p>
	 * <ul>
	 * <li>Vérifie que les retraits concurrents ne corrompent pas la liste des sous-types.</li>
	 * <li>Utilise un ExecutorService pour simuler des modifications concurrentes.</li>
	 * </ul>
	 * </div>
	 * @throws TimeoutException 
	 */
	@SuppressWarnings({ RESOURCE, UNUSED })
	@DisplayName("testDetacherEnfantSTPThreadSafe() : vérifie le thread-safety de detacherEnfantSTP()")
	@Tag(THREAD_SAFETY)
	@Test
	public final void testDetacherEnfantSTPThreadSafe() 
			throws InterruptedException, ExecutionException, TimeoutException {
	    /*
	     * AFFICHAGE DANS LE TEST ou NON
	     */
	    final boolean affichage = false;
	
	    /*
	     * ARRANGE - GIVEN : Création d'un TypeProduit avec des SousTypeProduit.
	     */
	    final TypeProduitI typeProduit = new TypeProduit(1L, VETEMENT, null);
	    final List<SousTypeProduitI> sousTypeProduits = new ArrayList<>();
	    for (int i = 0; i < 50; i++) {
	        final SousTypeProduitI sousTypeProduit = new SousTypeProduit((long) i, SOUSTYPEPRODUIT + i, null);
	        sousTypeProduits.add(sousTypeProduit);
	        typeProduit.rattacherEnfantSTP(sousTypeProduit);
	    }
	
	    /*
	     * ACT - WHEN : Exécution concurrente de retraits.
	     */
	    final ExecutorService executor = Executors.newFixedThreadPool(10);
	    final List<Callable<Void>> tasks = new ArrayList<>();
	    for (final SousTypeProduitI sousTypeProduit : sousTypeProduits) {
	        tasks.add(() -> {
	            typeProduit.detacherEnfantSTP(sousTypeProduit);
	            return null;
	        });
	    }
	
	    executor.invokeAll(tasks, 5, TimeUnit.SECONDS);
	    executor.shutdown();
	
	    /*
	     * ASSERT - THEN : Vérification de la cohérence de la liste.
	     */
	    final ExecutorService verifier = Executors.newSingleThreadExecutor();
	    try {
	        final Future<Boolean> f = verifier.submit(() -> typeProduit.getSousTypeProduits().isEmpty());
	        assertTrue(f.get(2, TimeUnit.SECONDS),
	            "La liste des sous-types doit être vide après des retraits concurrents.");
	    } finally {
	        verifier.shutdownNow();
	    }
	
	    /*
	     * AFFICHAGE A LA CONSOLE.
	     */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("***** Test retirerTypeProduitIauSousTypeProduit() en multi-thread réussi *****");
	        System.out.println(NOMBRE_STP + typeProduit.getSousTypeProduits().size());
	    }
	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>Teste les méthodes <b>internalAddSousTypeProduit</b> et <b>internalRemoveSousTypeProduit</b>.</p>
	 * <ul>
	 * <li>garantit que l'ajout interne d'un SousTypeProduit fonctionne correctement.</li>
	 * <li>garantit que le retrait interne d'un SousTypeProduit fonctionne correctement.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings(UNUSED)
	@DisplayName(TEST_INTERNAL_ADD_REMOVE_SOUS_TYPE_PRODUIT + "() : vérifie le bon fonctionnement des méthodes internalAddSousTypeProduit et internalRemoveSousTypeProduit")
	@Tag(RELATIONS)
	@Test
	public final void testInternalAddEtRemoveSousTypeProduit() {
	    // **********************************
	    // AFFICHAGE DANS LE TEST ou NON
	    final boolean affichage = false;
	    // **********************************
	
	    /* AFFICHAGE A LA CONSOLE. */
	    if (AFFICHAGE_GENERAL && affichage) {
	        System.out.println();
	        System.out.println("********** CLASSE TypeProduitTest - méthode " + TEST_INTERNAL_ADD_REMOVE_SOUS_TYPE_PRODUIT + "() ********** ");
	        System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT DES METHODES internalAddSousTypeProduit ET internalRemoveSousTypeProduit.");
	        System.out.println();
	    }
	
	    // ARRANGE - GIVEN
	    final TypeProduit typeProduit = new TypeProduit(VETEMENT);
	    final SousTypeProduit sousTypeProduit = new SousTypeProduit(VETEMENT_HOMME);
	
	    // ACT - WHEN
	    typeProduit.internalAddSousTypeProduit(sousTypeProduit);
	
	    // ASSERT - THEN
	    assertTrue(typeProduit.getSousTypeProduits().contains(sousTypeProduit), "Le SousTypeProduit doit être ajouté à la liste des sous-types du TypeProduit.");
	
	    // ACT - WHEN
	    typeProduit.internalRemoveSousTypeProduit(sousTypeProduit);
	
	    // ASSERT - THEN
	    assertFalse(typeProduit.getSousTypeProduits().contains(sousTypeProduit), "Le SousTypeProduit doit être retiré de la liste des sous-types du TypeProduit.");
	    
	} //___________________________________________________________________



    /**
     * Teste la méthode internalAddSousTypeProduit() en environnement multi-thread.
     *
     *
     * <ul>
     * <li>Vérifie que les ajouts internes concurrents ne corrompent pas la liste des sous-types.</li>
     * <li>Utilise un ExecutorService pour simuler des modifications concurrentes.</li>
     * </ul>
     *
     */
    @SuppressWarnings({ RESOURCE, UNUSED })
    @DisplayName("testInternalAddSousTypeProduitThreadSafe() : vérifie le thread-safety de internalAddSousTypeProduit()")
    @Tag(THREAD_SAFETY)
    @Test
    public final void testInternalAddSousTypeProduitThreadSafe()
            throws InterruptedException, ExecutionException {

        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * ARRANGE - GIVEN : Création d'un TypeProduit.
         */
        final TypeProduit typeProduit = new TypeProduit(1L, VETEMENT, null);

        /*
         * ACT - WHEN : Exécution concurrente d'ajouts internes.
         */
        final ExecutorService executor = Executors.newFixedThreadPool(10);
        final List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < 50; i++) {

            final SousTypeProduit sousTypeProduit
                = new SousTypeProduit((long) i, SOUSTYPEPRODUIT + i, null);

            tasks.add(() -> {

                typeProduit.internalAddSousTypeProduit(sousTypeProduit);

                return null;
            });
        }

        final List<Future<Void>> futures = executor.invokeAll(tasks, 5, TimeUnit.SECONDS);

        executor.shutdown();

        /*
         * ASSERT - THEN : Vérification qu'aucune tâche n'a été annulée (timeout).
         */
        for (final Future<Void> f : futures) {

            assertFalse(f.isCancelled()
                    , "Une tâche d'ajout interne a été annulée (timeout) : risque de blocage/délockage.");

            f.get();
        }

        /*
         * ASSERT - THEN : Vérification de la cohérence de la liste.
         */
        assertEquals(50, typeProduit.getSousTypeProduits().size()
                , "La liste des sous-types doit contenir 50 éléments après des ajouts internes concurrents.");

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {

            System.out.println();
            System.out.println("***** Test internalAddSousTypeProduit() en multi-thread réussi *****");
            System.out.println(NOMBRE_STP + typeProduit.getSousTypeProduits().size());
        }

    } //___________________________________________________________________



    /**
     * Teste la méthode internalRemoveSousTypeProduit() en environnement multi-thread.
     *
     *
     * <ul>
     * <li>Vérifie que les retraits internes concurrents ne corrompent pas la liste des sous-types.</li>
     * <li>Utilise un ExecutorService pour simuler des modifications concurrentes.</li>
     * </ul>
     *
     */
    @SuppressWarnings({ RESOURCE, UNUSED })
    @DisplayName("testInternalRemoveSousTypeProduitThreadSafe() : vérifie le thread-safety de internalRemoveSousTypeProduit()")
    @Tag(THREAD_SAFETY)
    @Test
    public final void testInternalRemoveSousTypeProduitThreadSafe()
            throws InterruptedException, ExecutionException {

        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * ARRANGE - GIVEN : Création d'un TypeProduit avec des SousTypeProduit.
         */
        final TypeProduit typeProduit = new TypeProduit(1L, VETEMENT, null);

        final List<SousTypeProduit> sousTypeProduits = new ArrayList<>();

        for (int i = 0; i < 50; i++) {

            final SousTypeProduit sousTypeProduit
                = new SousTypeProduit((long) i, SOUSTYPEPRODUIT + i, null);

            sousTypeProduits.add(sousTypeProduit);

            typeProduit.internalAddSousTypeProduit(sousTypeProduit);
        }

        /*
         * ACT - WHEN : Exécution concurrente de retraits internes.
         */
        final ExecutorService executor = Executors.newFixedThreadPool(10);

        final List<Callable<Void>> tasks = new ArrayList<>();

        for (final SousTypeProduit sousTypeProduit : sousTypeProduits) {

            tasks.add(() -> {

                typeProduit.internalRemoveSousTypeProduit(sousTypeProduit);

                return null;
            });
        }

        final List<Future<Void>> futures = executor.invokeAll(tasks, 5, TimeUnit.SECONDS);

        executor.shutdown();

        /*
         * ASSERT - THEN : Vérification qu'aucune tâche n'a été annulée (timeout).
         */
        for (final Future<Void> f : futures) {

            assertFalse(f.isCancelled()
                    , "Une tâche de retrait interne a été annulée (timeout) : risque de blocage/délockage.");

            f.get();
        }

        /*
         * ASSERT - THEN : Vérification de la cohérence de la liste.
         */
        assertTrue(typeProduit.getSousTypeProduits().isEmpty()
                , "La liste des sous-types doit être vide après des retraits internes concurrents.");

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {

            System.out.println();
            System.out.println("***** Test internalRemoveSousTypeProduit() en multi-thread réussi *****");
            System.out.println(NOMBRE_STP + typeProduit.getSousTypeProduits().size());
        }

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
            System.out.println("********** CLASSE TypeProduitTest - méthode testGetEnTeteCsv() ********** ");
            System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode getEnTeteCsv().");
            System.out.println();
        }

        //**** ARRANGE - GIVEN
        final TypeProduit objetConstructeurNull = new TypeProduit();

        // ACT - WHEN
        final String enTeteCsv = objetConstructeurNull.getEnTeteCsv();

        /* AFFICHAGE A LA CONSOLE. */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("enTeteCsv : " + enTeteCsv);
            System.out.println();
        }

        final String enTeteCsvPrevue = "idTypeProduit;type de produit;";

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
            System.out.println("********** CLASSE TypeProduitTest - méthode testToStringCsv() ********** ");
            System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode toStringCsv().");
            System.out.println();
        }

        // *** ARRANGE - GIVEN
        final TypeProduit objetConstructeurNull = new TypeProduit();

        // ACT - WHEN
        final String toStringCsvNull = objetConstructeurNull.toStringCsv();

        /* AFFICHAGE A LA CONSOLE. */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("*** APRES TypeProduit objetConstructeurNull = new TypeProduit(); ***");
            System.out.println("getEnTeteCsv : " + objetConstructeurNull.getEnTeteCsv());
            System.out.println("toStringCsvNull : " + toStringCsvNull);
            System.out.println();
        }

        final String toStringCsvPrevueNull = "null;null;";

        // ASSERT - THEN
        assertEquals(toStringCsvPrevueNull, toStringCsvNull, "toStringCsv doit retourner \"null;null;\" : ");

        // *** ARRANGE - GIVEN
        final TypeProduit objet1 = new TypeProduit(1L, PECHE);

        // ACT - WHEN
        final String toStringCsv = objet1.toStringCsv();

        /* AFFICHAGE A LA CONSOLE. */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("*** APRES TypeProduit objet1 = new TypeProduit(1L, PECHE, null); ***");
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
     * <p>Teste la méthode <b>toStringCsv()</b> en environnement multi-thread.</p>
     * <ul>
     * <li>Vérifie que les appels concurrents retournent toujours la même valeur.</li>
     * <li>Utilise un ExecutorService avec timeout pour éviter un blocage infini.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings({ RESOURCE, UNUSED })
    @DisplayName("testToStringCsvThreadSafe() : vérifie le thread-safety et le bon fonctionnement de toStringCsv()")
    @Tag(THREAD_SAFETY)
    @Test
    public final void testToStringCsvThreadSafe()
            throws InterruptedException, ExecutionException {

        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * ARRANGE - GIVEN : Création des objets nécessaires.
         */
        final TypeProduit typeProduitAvecValeurs = new TypeProduit(1L, PECHE);
        final TypeProduit typeProduitAvecNulls = new TypeProduit();

        /*
         * Résultats attendus.
         */
        final String resultatAttenduAvecValeurs = "1;Pêche;";
        final String resultatAttenduAvecNulls = "null;null;";

        /*
         * ACT - WHEN : Appels à toStringCsv() en mono-thread.
         */
        final String toStringCsvAvecValeurs = typeProduitAvecValeurs.toStringCsv();
        final String toStringCsvAvecNulls = typeProduitAvecNulls.toStringCsv();

        /*
         * ASSERT - THEN : Vérification des résultats en mono-thread.
         */
        assertEquals(resultatAttenduAvecValeurs, toStringCsvAvecValeurs
                , "toStringCsv() doit retourner '1;Pêche;' : ");
        assertEquals(resultatAttenduAvecNulls, toStringCsvAvecNulls
                , "toStringCsv() doit retourner 'null;null;' : ");

        /*
         * ACT - WHEN : Appels concurrents à toStringCsv() pour vérifier la thread-safety.
         */
        final ExecutorService executor = Executors.newFixedThreadPool(10);
        final List<Callable<String>> tasks = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            tasks.add(() -> typeProduitAvecValeurs.toStringCsv());
        }

        final List<Future<String>> results = executor.invokeAll(tasks, 5, TimeUnit.SECONDS);
        executor.shutdown();

        /*
         * ASSERT - THEN : Vérification qu'aucune tâche n'a été annulée (timeout).
         */
        for (final Future<String> result : results) {
            assertFalse(result.isCancelled()
                    , "Une tâche toStringCsv() a été annulée (timeout) : risque de blocage.");
            assertEquals(resultatAttenduAvecValeurs, result.get()
                    , "toStringCsv() doit toujours retourner le même résultat en multi-thread : ");
        }

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("***** Test toStringCsv() en multi-thread réussi *****");
            System.out.println("Résultat attendu : " + resultatAttenduAvecValeurs);
            System.out.println(RESULTAT_OBTENU + results.get(0).get());
        }
    }
    
    

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
            System.out.println("********** CLASSE TypeProduitTest - méthode testGetEnTeteColonne() ********** ");
            System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode getEnTeteColonne().");
            System.out.println();
        }

        // *** ARRANGE - GIVEN
        final TypeProduit objetConstructeurNull = new TypeProduit();

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
            System.out.println("********** CLASSE TypeProduitTest - méthode testGetValeurColonne() ********** ");
            System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode getValeurColonne().");
            System.out.println();
        }

        // *** ARRANGE - GIVEN
        final TypeProduit objetConstructeurNull = new TypeProduit();

        // ACT - WHEN
        final String valeur0Null = (String) objetConstructeurNull.getValeurColonne(0);
        final String valeur1Null = (String) objetConstructeurNull.getValeurColonne(1);
        final String valeur7Null = (String) objetConstructeurNull.getValeurColonne(7);

        /* AFFICHAGE A LA CONSOLE. */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("*** APRES TypeProduit objetConstructeurNull = new TypeProduit(); ***");
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
        final TypeProduit objet1 = new TypeProduit(1L, PECHE);

        // ACT - WHEN
        final String valeur0 = (String) objet1.getValeurColonne(0);
        final String valeur1 = (String) objet1.getValeurColonne(1);
        final String valeur7 = (String) objet1.getValeurColonne(7);

        /* AFFICHAGE A LA CONSOLE. */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("*** APRES TypeProduit objet1 = new TypeProduit(1L, PECHE); ***");
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
     * <p>Teste la méthode <b>getValeurColonne(int pI)</b> en environnement multi-thread.</p>
     * <ul>
     * <li>Vérifie que les appels concurrents retournent toujours la même valeur
     * quand l'objet n'est pas modifié.</li>
     * <li>Utilise un ExecutorService avec timeout pour éviter un blocage infini.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings({ RESOURCE, UNUSED })
    @DisplayName("testGetValeurColonneThreadSafe() : vérifie le thread-safety de getValeurColonne()")
    @Tag(THREAD_SAFETY)
    @Test
    public final void testGetValeurColonneThreadSafe()
            throws InterruptedException, ExecutionException {

        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * ARRANGE - GIVEN
         */
        final TypeProduit objet1 = new TypeProduit(1L, PECHE);

        final String attendu0 = "1";
        final String attendu1 = "Pêche";

        /*
         * ACT - WHEN : appels concurrents.
         */
        final ExecutorService executor = Executors.newFixedThreadPool(10);

        final List<Callable<String>> tasks = new ArrayList<>();

        for (int i = 0; i < 200; i++) {

            tasks.add(() -> {

                final String v0 = (String) objet1.getValeurColonne(0);
                final String v1 = (String) objet1.getValeurColonne(1);

                return v0 + "|" + v1;
            });
        }

        final List<Future<String>> results = executor.invokeAll(tasks, 5, TimeUnit.SECONDS);

        executor.shutdown();

        /*
         * ASSERT - THEN : aucune annulation (timeout) et résultats stables.
         */
        for (final Future<String> result : results) {

            assertFalse(result.isCancelled()
                    , "Une tâche getValeurColonne() a été annulée (timeout) : risque de blocage.");

            assertEquals(attendu0 + "|" + attendu1, result.get()
                    , "getValeurColonne() doit retourner des valeurs stables en multi-thread : ");
        }

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {

            System.out.println();
            System.out.println("***** Test getValeurColonne() en multi-thread réussi *****");
            System.out.println("Résultat attendu : " + attendu0 + "|" + attendu1);
            System.out.println(RESULTAT_OBTENU + results.get(0).get());
        }

    } //___________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>Teste la méthode <b>getSousTypeProduits()</b> en environnement mono-thread.</p>
     * <ul>
     * <li>Vérifie que la liste retournée est immuable (UnsupportedOperationException).</li>
     * <li>Vérifie que la liste retournée est un snapshot (les modifications ultérieures
     * du parent n'altèrent pas la liste déjà obtenue).</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testGetSousTypeProduits() : vérifie le snapshot immuable retourné par getSousTypeProduits()")
    @Tag(RELATIONS)
    @Test
    public final void testGetSousTypeProduits() {

        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * ARRANGE - GIVEN
         */
        final TypeProduitI typeProduit = new TypeProduit(1L, VETEMENT, null);

        final SousTypeProduitI sousTypeProduit1 = new SousTypeProduit(10L, VETEMENT_HOMME, null);
        final SousTypeProduitI sousTypeProduit2 = new SousTypeProduit(20L, VETEMENT_FEMME, null);

        typeProduit.rattacherEnfantSTP(sousTypeProduit1);
        typeProduit.rattacherEnfantSTP(sousTypeProduit2);

        /*
         * ACT - WHEN : récupération d'un snapshot.
         */
        final List<? extends SousTypeProduitI> snapshot 
        	= typeProduit.getSousTypeProduits();

        /*
         * ASSERT - THEN : snapshot contient 2 éléments.
         */
        assertEquals(2, snapshot.size()
                , "getSousTypeProduits() doit retourner 2 sous-types.");

        /*
         * ASSERT - THEN : la liste retournée est immuable.
         * snapshot est List<? extends SousTypeProduitI>, 
         * donc Java interdit add(...) (il ne peut pas garantir le type).
         * En castant snapshot en List brut puis List<Object>,
         *  on peut appeler add(...) et 
         *  vérifier que la liste est bien non modifiable à l’exécution.
         */
        @SuppressWarnings({ "unchecked", "rawtypes" })
        final List<Object> snapshotAsObjectList = (List) snapshot;

        assertThrows(UnsupportedOperationException.class
                , () -> snapshotAsObjectList.add(new SousTypeProduit(30L, SOUSTYPEPRODUIT + "X", null, null))
                , "La liste retournée par getSousTypeProduits() doit être immuable.");

        /*
         * ASSERT - THEN : snapshot reste un snapshot si le parent est modifié ensuite.
         */
        final SousTypeProduitI sousTypeProduit3 = new SousTypeProduit(30L, SOUSTYPEPRODUIT + "3", null);
        typeProduit.rattacherEnfantSTP(sousTypeProduit3);

        assertEquals(2, snapshot.size()
                , "Le snapshot déjà obtenu ne doit pas être modifié par une modification ultérieure du parent.");

        assertEquals(3, typeProduit.getSousTypeProduits().size()
                , "Le parent doit contenir 3 sous-types après ajout.");

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("***** Test getSousTypeProduits() snapshot immuable réussi *****");
            System.out.println(NOMBRE_STP + typeProduit.getSousTypeProduits().size());
        }

    } //___________________________________________________________________
    
    

    /**
     * <div>
     * <p>Teste la méthode <b>getSousTypeProduits()</b> en environnement multi-thread.</p>
     * <ul>
     * <li>Vérifie que l'appel concurrent à getSousTypeProduits() ne provoque pas
     * de ConcurrentModificationException.</li>
     * <li>Utilise un ExecutorService avec timeout pour éviter un blocage infini.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings({ RESOURCE, UNUSED })
    @DisplayName("testGetSousTypeProduitsThreadSafe() : vérifie le thread-safety de getSousTypeProduits()")
    @Tag(THREAD_SAFETY)
    @Test
    public final void testGetSousTypeProduitsThreadSafe()
            throws InterruptedException, ExecutionException {

        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * ARRANGE - GIVEN : Création d'un TypeProduit avec des SousTypeProduit.
         */
        final TypeProduitI typeProduit = new TypeProduit(1L, VETEMENT, null);
        final SousTypeProduitI sousTypeProduit1 = new SousTypeProduit(10L, VETEMENT_HOMME, typeProduit, null);
        final SousTypeProduitI sousTypeProduit2 = new SousTypeProduit(20L, VETEMENT_FEMME, typeProduit, null);

        typeProduit.rattacherEnfantSTP(sousTypeProduit1);
        typeProduit.rattacherEnfantSTP(sousTypeProduit2);

        /*
         * ACT - WHEN : Exécution concurrente de getSousTypeProduits().
         */
        final ExecutorService executor = Executors.newFixedThreadPool(10);
        final List<Callable<List<? extends SousTypeProduitI>>> tasks = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            tasks.add(() -> typeProduit.getSousTypeProduits());
        }

        final List<Future<List<? extends SousTypeProduitI>>> results
            = executor.invokeAll(tasks, 5, TimeUnit.SECONDS);

        executor.shutdown();

        /*
         * ASSERT - THEN : Vérification des résultats.
         */
        for (final Future<List<? extends SousTypeProduitI>> result : results) {

            assertFalse(result.isCancelled()
                    , "Une tâche getSousTypeProduits() a été annulée (timeout) : risque de blocage.");

            assertEquals(2, result.get().size()
                    , "getSousTypeProduits() doit toujours retourner 2 sous-types en environnement multi-thread.");
        }

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("***** Test getSousTypeProduits() en multi-thread réussi *****");
            System.out.println(NOMBRE_STP + typeProduit.getSousTypeProduits().size());
        }

    } //___________________________________________________________________



    /**
     * <div>
     * <p>Teste la méthode <b>setSousTypeProduits()</b>.</p>
     * <ul>
     * <li>Vérifie que la liste des SousTypeProduit est correctement mise à jour.</li>
     * <li>Vérifie la synchronisation des relations (bidirectionnalité).</li>
     * <li>Vérifie que les anciens enfants sont détachés et que les nouveaux sont rattachés.</li>
     * <li>Vérifie que setSousTypeProduits(null) vide la liste et détache les enfants.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName(TEST_SET_SOUS_TYPE_PRODUITS + "() : vérifie le bon fonctionnement de la méthode setSousTypeProduits")
    @Tag(RELATIONS)
    @Test
    public final void testSetSousTypeProduits() {

        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("********** CLASSE TypeProduitTest - méthode " + TEST_SET_SOUS_TYPE_PRODUITS + "() ********** ");
            System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT DE LA METHODE setSousTypeProduits.");
            System.out.println();
        }

        /*
         * ARRANGE - GIVEN : parent + anciens enfants rattachés.
         */
        final TypeProduit typeProduit = new TypeProduit(VETEMENT);

        final SousTypeProduitI ancien1 = new SousTypeProduit(VETEMENT_HOMME);
        final SousTypeProduitI ancien2 = new SousTypeProduit(VETEMENT_FEMME);

        typeProduit.rattacherEnfantSTP(ancien1);
        typeProduit.rattacherEnfantSTP(ancien2);

        assertEquals(2, typeProduit.getSousTypeProduits().size()
                , "Le parent doit contenir 2 sous-types avant mise à jour.");
        assertSame(typeProduit, ancien1.getTypeProduit()
                , "ancien1 doit être rattaché au parent avant mise à jour.");
        assertSame(typeProduit, ancien2.getTypeProduit()
                , "ancien2 doit être rattaché au parent avant mise à jour.");

        /*
         * ACT - WHEN : set de nouveaux enfants (non rattachés au départ).
         */
        final List<SousTypeProduitI> nouveaux = new ArrayList<>();
        final SousTypeProduitI nouveau1 = new SousTypeProduit(VETEMENT_ENFANT);
        final SousTypeProduitI nouveau2 = new SousTypeProduit(SOUSTYPEPRODUIT + "X");
        nouveaux.add(nouveau1);
        nouveaux.add(nouveau2);

        typeProduit.setSousTypeProduits(nouveaux);

        /*
         * ASSERT - THEN : parent mis à jour + anciens détachés + nouveaux rattachés.
         */
        assertEquals(2, typeProduit.getSousTypeProduits().size()
                , "La liste des SousTypeProduit doit contenir 2 éléments après set.");
        assertTrue(typeProduit.getSousTypeProduits().containsAll(nouveaux)
                , "La liste des SousTypeProduit doit contenir les éléments définis par setSousTypeProduits().");

        assertNull(ancien1.getTypeProduit()
                , "ancien1 doit être détaché (TypeProduit == null) après setSousTypeProduits().");
        assertNull(ancien2.getTypeProduit()
                , "ancien2 doit être détaché (TypeProduit == null) après setSousTypeProduits().");

        assertSame(typeProduit, nouveau1.getTypeProduit()
                , "nouveau1 doit être rattaché au parent après setSousTypeProduits().");
        assertSame(typeProduit, nouveau2.getTypeProduit()
                , "nouveau2 doit être rattaché au parent après setSousTypeProduits().");

        /*
         * ACT - WHEN : set à null.
         */
        typeProduit.setSousTypeProduits(null);

        /*
         * ASSERT - THEN : parent vide + enfants détachés.
         */
        assertTrue(typeProduit.getSousTypeProduits().isEmpty()
                , "Le parent doit être vide après setSousTypeProduits(null).");
        assertNull(nouveau1.getTypeProduit()
                , "nouveau1 doit être détaché (TypeProduit == null) après setSousTypeProduits(null).");
        assertNull(nouveau2.getTypeProduit()
                , "nouveau2 doit être détaché (TypeProduit == null) après setSousTypeProduits(null).");

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("***** Test setSousTypeProduits() réussi *****");
            System.out.println(NOMBRE_STP + typeProduit.getSousTypeProduits().size());
        }

    } //___________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>Teste la méthode <b>setSousTypeProduits()</b> en environnement multi-thread.</p>
     * <ul>
     * <li>Vérifie que les mises à jour concurrentes de la liste des sous-types ne corrompent pas l'état.</li>
     * <li>Utilise un ExecutorService avec timeout pour éviter un blocage infini.</li>
     * <li>Force un état final déterministe et vérifie la cohérence bidirectionnelle parent/enfants.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings({ RESOURCE, UNUSED })
    @DisplayName("testSetSousTypeProduitsThreadSafe() : vérifie le thread-safety de setSousTypeProduits()")
    @Tag(THREAD_SAFETY)
    @Test
    public final void testSetSousTypeProduitsThreadSafe()
            throws InterruptedException, ExecutionException {

        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * ARRANGE - GIVEN : Création d'un TypeProduit.
         */
        final TypeProduit typeProduit = new TypeProduit(1L, VETEMENT, null);

        /*
         * ACT - WHEN : Exécution concurrente de mises à jour.
         */
        final ExecutorService executor = Executors.newFixedThreadPool(10);
        final List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < 50; i++) {

            final List<SousTypeProduitI> sousTypeProduits = new ArrayList<>();

            for (int j = 0; j < 5; j++) {
                sousTypeProduits.add(new SousTypeProduit((long) j, SOUSTYPEPRODUIT + j + " - " + i, null));
            }

            tasks.add(() -> {
                typeProduit.setSousTypeProduits(sousTypeProduits);
                return null;
            });
        }

        final List<Future<Void>> results = executor.invokeAll(tasks, 10, TimeUnit.SECONDS);
        executor.shutdown();

        /*
         * ASSERT - THEN : aucune tâche annulée (timeout) et aucune exception.
         */
        for (final Future<Void> result : results) {
            assertFalse(result.isCancelled()
                    , "Une tâche setSousTypeProduits() a été annulée (timeout) : risque de blocage.");
            assertNull(result.get()
                    , "Toutes les tâches doivent se terminer sans erreur.");
        }

        /*
         * ACT - WHEN : impose un état final déterministe.
         */
        final List<SousTypeProduitI> listeFinale = new ArrayList<>();
        for (int j = 0; j < 5; j++) {
            listeFinale.add(new SousTypeProduit((long) j, SOUSTYPEPRODUIT + j + " - FINAL", null));
        }
        typeProduit.setSousTypeProduits(listeFinale);

        /*
         * ASSERT - THEN : cohérence du parent + cohérence bidirectionnelle.
         */
        assertEquals(5, typeProduit.getSousTypeProduits().size()
                , "La liste des sous-types doit contenir 5 éléments après l'état final imposé.");
        assertTrue(typeProduit.getSousTypeProduits().containsAll(listeFinale)
                , "Le parent doit contenir exactement les éléments de la liste finale.");

        for (final SousTypeProduitI stp : listeFinale) {
            assertSame(typeProduit, stp.getTypeProduit()
                    , "Chaque enfant de la liste finale doit avoir son parent correctement positionné.");
        }

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("***** Test setSousTypeProduits() en multi-thread réussi *****");
            System.out.println(NOMBRE_STP + typeProduit.getSousTypeProduits().size());
        }

    } //___________________________________________________________________
    
    
    
    /**
     * <p>affiche à la console un TypeProduit.</p>
     *
     * @param pTypeProduit : TypeProduit
     */
    private void afficher(final TypeProduitI pTypeProduit) {
        if (pTypeProduit == null) {
            return;
        }

        System.out.println("id du TypeProduit : " + pTypeProduit.getIdTypeProduit());
        System.out.println("TypeProduit : " + pTypeProduit.getTypeProduit());
    } //___________________________________________________________________
    
    

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
        this.typeProduitVetement = new TypeProduit(VETEMENT);

        this.soustypeProduitVetementPourHomme = new SousTypeProduit(VETEMENT_HOMME, this.typeProduitVetement);
        this.soustypeProduitVetementPourFemme = new SousTypeProduit(VETEMENT_FEMME, this.typeProduitVetement);
        this.soustypeProduitVetementPourEnfant = new SousTypeProduit(VETEMENT_ENFANT, this.typeProduitVetement);
        this.soustypeProduitVetementPourHommeSansTypeProduit = new SousTypeProduit(VETEMENT_HOMME);

        /* création de produits de type vêtement. */
        /* Homme */
        this.produitChemiseManchesLonguesHomme = new Produit(CHEMISE_MANCHES_LONGUES, this.soustypeProduitVetementPourHomme);
        this.produitChemiseManchesCourtesHomme = new Produit(CHEMISES_MANCHES_COURTES, this.soustypeProduitVetementPourHomme);
        this.produitSweatshirtHomme = new Produit(SWEATSHIRT, this.soustypeProduitVetementPourHomme);
        this.produitAvecMauvaisSousTypeProduit = new Produit("tee-shirt", this.soustypeProduitVetementPourHommeSansTypeProduit);

        /* Femme */
        this.produitChemiseManchesLonguesFemme = new Produit(CHEMISE_MANCHES_LONGUES, this.soustypeProduitVetementPourFemme);
        this.produitChemiseManchesCourtesFemme = new Produit(CHEMISES_MANCHES_COURTES, this.soustypeProduitVetementPourFemme);
        this.produitSweatshirtFemme = new Produit(SWEATSHIRT, this.soustypeProduitVetementPourFemme);
        this.produitSoutienGorgeFemme = new Produit(SOUTIEN_GORGE, this.soustypeProduitVetementPourFemme);

        /* Enfant */
        this.produitChemiseManchesLonguesEnfant = new Produit(CHEMISE_MANCHES_LONGUES, this.soustypeProduitVetementPourEnfant);
        this.produitChemiseManchesCourtesEnfant = new Produit(CHEMISES_MANCHES_COURTES, this.soustypeProduitVetementPourEnfant);
        this.produitSweatshirtEnfant = new Produit(SWEATSHIRT, this.soustypeProduitVetementPourEnfant);

        this.listeProduitsVetement = new ArrayList<ProduitI>();
        this.listeProduitsVetement.add(this.produitChemiseManchesLonguesHomme);
        this.listeProduitsVetement.add(this.produitChemiseManchesCourtesHomme);
        this.listeProduitsVetement.add(this.produitSweatshirtHomme);
        this.listeProduitsVetement.add(this.produitAvecMauvaisSousTypeProduit);
        this.listeProduitsVetement.add(this.produitChemiseManchesLonguesFemme);
        this.listeProduitsVetement.add(this.produitChemiseManchesCourtesFemme);
        this.listeProduitsVetement.add(this.produitSweatshirtFemme);
        this.listeProduitsVetement.add(this.produitSoutienGorgeFemme);
        this.listeProduitsVetement.add(this.produitChemiseManchesLonguesEnfant);
        this.listeProduitsVetement.add(this.produitChemiseManchesCourtesEnfant);
        this.listeProduitsVetement.add(this.produitSweatshirtEnfant);
        
    } //___________________________________________________________________
    
}
