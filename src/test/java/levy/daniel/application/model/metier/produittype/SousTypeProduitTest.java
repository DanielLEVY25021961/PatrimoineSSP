package levy.daniel.application.model.metier.produittype; // NOPMD by danyl on 07/02/2026 23:07

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
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
 * <p style="font-weight:bold;">CLASSE SousTypeProduitTest.java :</p>
 *
 * <p>
 * Cette classe teste la classe métier :
 * <span style="font-weight:bold;">SousTypeProduit</span>
 * </p>
 * <p>
 * Tests couverts :
 * </p>
 * <ul>
 * <li>Constructeurs (arité nulle, 1, 2, 3, complet)</li>
 * <li>equals() et hashCode()</li>
 * <li>toString()</li>
 * <li>compareTo()</li>
 * <li>clone() et deepClone()</li>
 * <li>getEnTeteCsv() et toStringCsv()</li>
 * <li>getEnTeteColonne() et getValeurColonne()</li>
 * <li>ajouterSTPauProduit() et retirerSTPauProduit()</li>
 * <li>isValide()</li>
 * <li>Gestion des relations bidirectionnelles 
 * avec TypeProduit et Produit</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 2.0
 * @since 15 décembre 2025
 */
public class SousTypeProduitTest {

	// ======================= CONSTANTES ================================/
	
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
	 * "thread-safety"
	 */
	public static final String THREAD_SAFETY = "thread-safety";
	
	/**
	 * "invalide"
	 */
	public static final String INVALIDE = "invalide";
		
	/**
	 * System.getProperty("line.separator")
	 */
	public static final String SAUT_DE_LIGNE 
		= System.getProperty("line.separator");
	
	/**
	 * "null" 
	 */
	public static final String NULL = "null";
	
	/* ------------- Constantes pour les Types de Produit ------------- */

    /**
     * "Pêche"
     */
    public static final String PECHE = "Pêche";

    /**
     * "Outillage"
     */
    public static final String OUTILLAGE = "Outillage";

    /**
     * "Restauration"
     */
    public static final String RESTAURATION = "Restauration";
    
    /* ---------- Constantes pour les Sous-Types de Produit ------------ */

    /**
     * "canne à pêche"
     */
    public static final String CANNE_A_PECHE = "canne à pêche";

    /**
     * "moulinet"
     */
    public static final String MOULINET = "moulinet";

    /**
     * "cuillère"
     */
    public static final String CUILLERE = "cuillère";

    /**
     * "perceuse"
     */
    public static final String PERCEUSE = "perceuse";

    /**
     * "fourchette"
     */
    public static final String FOURCHETTE = "fourchette";
	
    /* ------------------- Messages d'affichage ------------------- */

    /**
     * "**** sousTypeProduitCanneAPeche ******"
     */
    public static final String SOUSP_CANNA 
    	= "**** sousTypeProduitCanneAPeche ******";

    /**
     * "**** sousTypeProduitMoulinet ******"
     */
    public static final String SOUSP_MOULINET 
    	= "**** sousTypeProduitMoulinet ******";

    /**
     * "**** sousTypeProduitCuillere ******"
     */
    public static final String SOUSP_CUILLERE 
    	= "**** sousTypeProduitCuillere ******";

    /**
     * "**** sousTypeProduitPerceuse ******"
     */
    public static final String SOUSP_PERCEUSE 
    	= "**** sousTypeProduitPerceuse ******";

    /**
     * "**** sousTypeProduitFourchette ******"
     */
    public static final String SOUSP_FOURCHETTE 
    	= "**** sousTypeProduitFourchette ******";

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
     * "**** sousTypeProduits dans typeProduitRestauration ******"
     */
    public static final String STP_RESTAURATION 
    	= "**** sousTypeProduits dans typeProduitRestauration ******";

    /* ---------------- Constantes pour les Produits ------------------- */

    /**
     * "canne télescopique"
     */
    public static final String CANNE_TELESCOPIQUE 
    	= "canne télescopique";

    /**
     * "moulinet à tambour fixe"
     */
    public static final String MOULINET_TAMBOUR_FIXE 
    	= "moulinet à tambour fixe";

    /**
     * "cuillère tournante"
     */
    public static final String CUILLERE_TOURNANTE 
    	= "cuillère tournante";

    /**
     * "perceuse sans fil"
     */
    public static final String PERCEUSE_SANS_FIL 
    	= "perceuse sans fil";

    /**
     * "fourchette en inox"
     */
    public static final String FOURCHETTE_INOX 
    	= "fourchette en inox";	
    
 // ************************* ATTRIBUTS *********************************/
	
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
			.getLogger(SousTypeProduitTest.class);

	// *************************METHODES**********************************/



	/**
	* <div>
	* <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	* </div>
	*/
	public SousTypeProduitTest() {
		super();
	} // Fin du CONSTRUCTEUR D'ARITE NULLE.________________________________


	 /* ============================= TESTS ============================ */
	
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
        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("********** CLASSE SousTypeProduitTest - méthode testGeneral() ********** ");
            System.out.println("CE TEST VERIFIE LE FONCTIONNEMENT GENERAL DE L'OBJET.");
            System.out.println();
        }

        /*
         * ARRANGE - GIVEN : Création des TypeProduit.
         */
        final TypeProduitI typeProduitPeche = new TypeProduit(1L, PECHE, null);
        final TypeProduitI typeProduitOutillage = new TypeProduit(2L, OUTILLAGE, null);

        /*
         * ARRANGE - GIVEN : Création des SousTypeProduit.
         */
        final SousTypeProduitI sousTypeProduitCanneAPeche 
        	= new SousTypeProduit(
        			10L, CANNE_A_PECHE, typeProduitPeche, null);
        final SousTypeProduitI sousTypeProduitMoulinet 
        	= new SousTypeProduit(
        			20L, MOULINET, typeProduitPeche, null);
        final SousTypeProduitI sousTypeProduitPerceuse 
        	= new SousTypeProduit(
        			30L, PERCEUSE, typeProduitOutillage, null);

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println(SOUSP_CANNA);
            this.afficher(sousTypeProduitCanneAPeche);
            System.out.println();
            System.out.println(SOUSP_MOULINET);
            this.afficher(sousTypeProduitMoulinet);
            System.out.println();
            System.out.println(SOUSP_PERCEUSE);
            this.afficher(sousTypeProduitPerceuse);
            System.out.println();
            System.out.println(STP_PECHE);
            this.afficherSousTypeProduitsDansTypeProduit(typeProduitPeche);
            System.out.println();
            System.out.println(STP_OUTILLAGE);
            this.afficherSousTypeProduitsDansTypeProduit(typeProduitOutillage);
            System.out.println();
        }

        /*
         * ASSERT - THEN : Vérifications initiales.
         */
        assertFalse(
        		typeProduitPeche.getSousTypeProduits().isEmpty()
        			, "La collection sousTypeProduits de typeProduitPeche ne doit pas être vide.");

        /*
         * ARRANGE - GIVEN : Modification du TypeProduit d'un SousTypeProduit.
         */
        sousTypeProduitPerceuse.setTypeProduit(typeProduitPeche);

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("**** APRES sousTypeProduitPerceuse.setTypeProduit(typeProduitPeche); *****");
            this.afficher(sousTypeProduitPerceuse);
            System.out.println();
            System.out.println(STP_PECHE);
            this.afficherSousTypeProduitsDansTypeProduit(typeProduitPeche);
            System.out.println();
            System.out.println(STP_OUTILLAGE);
            this.afficherSousTypeProduitsDansTypeProduit(typeProduitOutillage);
            System.out.println();
        }

        /*
         * ASSERT - THEN : Vérifications après modification.
         */
        assertTrue(
        		typeProduitOutillage.getSousTypeProduits().isEmpty()
        		, "La collection sousTypeProduits de typeProduitOutillage doit être vide.");

        /*
         * ARRANGE - GIVEN : Retour au TypeProduit initial.
         */
        sousTypeProduitPerceuse.setTypeProduit(typeProduitOutillage);

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("**** APRES sousTypeProduitPerceuse.setTypeProduit(typeProduitOutillage); *****");
            this.afficher(sousTypeProduitPerceuse);
            System.out.println();
            System.out.println(STP_PECHE);
            this.afficherSousTypeProduitsDansTypeProduit(typeProduitPeche);
            System.out.println();
            System.out.println(STP_OUTILLAGE);
            this.afficherSousTypeProduitsDansTypeProduit(typeProduitOutillage);
            System.out.println();
        }

        /*
         * ASSERT - THEN : Vérifications finales.
         */
        assertFalse(
        		typeProduitOutillage.getSousTypeProduits().isEmpty()
        		, "La collection sousTypeProduits de typeProduitOutillage ne doit pas être vide.");
    } //___________________________________________________________________

    
	
    /**
     * <div>
     * <p>Teste le constructeur d'arité nulle.</p>
     * <ul>
     * <li>Vérifie que toutes les propriétés de l'objet sont null.</li>
     * <li>Vérifie que les Booleans valide sont à false.</li>
     * <li>Vérifie que 2 instances créées avec le constructeur null sont différentes.</li>
     * <li>Vérifie que 2 instances créées avec le constructeur null sont equals.</li>
     * <li>Vérifie que les setters fonctionnent correctement.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testSousTypeProduit() : vérifie le comportement général du Constructeur d'arité nulle")
    @Tag("constructeurs")
    @Test
    public final void testSousTypeProduit() {
        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * ARRANGE - GIVEN : Création d'instances avec le constructeur d'arité nulle.
         */
        final SousTypeProduitI sousTypeProduitConstructeurNull1 = new SousTypeProduit();
        final SousTypeProduitI sousTypeProduitConstructeurNull2 = new SousTypeProduit();

        /*
         * ACT - WHEN : Récupération des propriétés.
         */
        final Long idSousTypeProduitConstructeurNull1 = sousTypeProduitConstructeurNull1.getIdSousTypeProduit();
        final Long idSousTypeProduitConstructeurNull2 = sousTypeProduitConstructeurNull2.getIdSousTypeProduit();

        final String sousTypeProduitConstructeurNullString1 = sousTypeProduitConstructeurNull1.getSousTypeProduit();
        final String sousTypeProduitConstructeurNullString2 = sousTypeProduitConstructeurNull2.getSousTypeProduit();

        final TypeProduitI typeProduitNull1 = sousTypeProduitConstructeurNull1.getTypeProduit();
        final TypeProduitI typeProduitNull2 = sousTypeProduitConstructeurNull2.getTypeProduit();

        final List<? extends ProduitI> produitsNull1 = sousTypeProduitConstructeurNull1.getProduits();
        final List<? extends ProduitI> produitsNull2 = sousTypeProduitConstructeurNull2.getProduits();

        final Boolean valideSousTypeProduitConstructeurNull1 = sousTypeProduitConstructeurNull1.isValide();
        final Boolean valideSousTypeProduitConstructeurNull2 = sousTypeProduitConstructeurNull2.isValide();

        final int sousTypeProduitConstructeurNull1HashCode = sousTypeProduitConstructeurNull1.hashCode();
        final int sousTypeProduitConstructeurNull2HashCode = sousTypeProduitConstructeurNull2.hashCode();

        /*
         * Vérifications d'égalité et d'identité.
         */
        final Boolean memeInstance1 = sousTypeProduitConstructeurNull1 == sousTypeProduitConstructeurNull2;
        final Boolean memeInstance2 = sousTypeProduitConstructeurNull2 == sousTypeProduitConstructeurNull1;

        final Boolean sousTypeProduitConstructeurNull1EqualsSousTypeProduitConstructeurNull2
            = sousTypeProduitConstructeurNull1.equals(sousTypeProduitConstructeurNull2);
        final Boolean sousTypeProduitConstructeurNull2EqualsSousTypeProduitConstructeurNull1
            = sousTypeProduitConstructeurNull2.equals(sousTypeProduitConstructeurNull1);
        final Boolean sousTypeProduitConstructeurNull1EqualsSousTypeProduitConstructeurNull1
            = sousTypeProduitConstructeurNull1.equals(sousTypeProduitConstructeurNull1);

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("*** SousTypeProduit sousTypeProduitConstructeurNull1 = new SousTypeProduit(); *** ");
            System.out.println("*** SousTypeProduit sousTypeProduitConstructeurNull2 = new SousTypeProduit(); *** ");
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

        /*
         * ASSERT - THEN : Vérifications des propriétés null.
         */
        assertNull(idSousTypeProduitConstructeurNull1, "L'ID de sousTypeProduitConstructeurNull1 doit être null.");
        assertNull(idSousTypeProduitConstructeurNull2, "L'ID de sousTypeProduitConstructeurNull2 doit être null.");

        assertNull(sousTypeProduitConstructeurNullString1, "sousTypeProduitConstructeurNullString1 doit être null.");
        assertNull(sousTypeProduitConstructeurNullString2, "sousTypeProduitConstructeurNullString2 doit être null.");

        assertNull(typeProduitNull1, "typeProduitNull1 doit être null.");
        assertNull(typeProduitNull2, "typeProduitNull2 doit être null.");

        /*
         * ASSERT - THEN : Vérifications des Booleans valide.
         */
        assertFalse(valideSousTypeProduitConstructeurNull1, "valide doit être à false dans sousTypeProduitConstructeurNull1.");
        assertFalse(valideSousTypeProduitConstructeurNull2, "valide doit être à false dans sousTypeProduitConstructeurNull2.");

        /*
         * ASSERT - THEN : Vérifications d'identité.
         */
        assertFalse(memeInstance1, "sousTypeProduitConstructeurNull1 == sousTypeProduitConstructeurNull2 doit retourner false.");
        assertFalse(memeInstance2, "sousTypeProduitConstructeurNull2 == sousTypeProduitConstructeurNull1 doit retourner false.");

        /*
         * ASSERT - THEN : Vérifications d'égalité.
         */
        assertTrue(sousTypeProduitConstructeurNull1EqualsSousTypeProduitConstructeurNull2, "sousTypeProduitConstructeurNull1 doit être Equals() à sousTypeProduitConstructeurNull2.");
        assertTrue(sousTypeProduitConstructeurNull2EqualsSousTypeProduitConstructeurNull1, "sousTypeProduitConstructeurNull2 doit être Equals() à sousTypeProduitConstructeurNull1.");
        assertTrue(sousTypeProduitConstructeurNull1EqualsSousTypeProduitConstructeurNull1, "sousTypeProduitConstructeurNull1 doit être Equals() à sousTypeProduitConstructeurNull1.");
        assertEquals(sousTypeProduitConstructeurNull1HashCode, sousTypeProduitConstructeurNull2HashCode, "sousTypeProduitConstructeurNull1 doit avoir le même Hashcode que sousTypeProduitConstructeurNull2.");

        /*
         * ARRANGE - GIVEN : Utilisation des Setters.
         */
        sousTypeProduitConstructeurNull1.setIdSousTypeProduit(1L);
        sousTypeProduitConstructeurNull1.setSousTypeProduit(CANNE_A_PECHE);

        /*
         * ASSERT - THEN : Vérifications après utilisation des Setters.
         */
        assertEquals(1L, sousTypeProduitConstructeurNull1.getIdSousTypeProduit(), "L'ID de sousTypeProduitConstructeurNull1 doit valoir 1 à ce stade.");
        assertEquals(CANNE_A_PECHE, sousTypeProduitConstructeurNull1.getSousTypeProduit(), "Le sousTypeProduit de sousTypeProduitConstructeurNull1 doit valoir \"canne à pêche\" à ce stade.");
        assertNull(sousTypeProduitConstructeurNull1.getTypeProduit(), "Le type de produit de sousTypeProduitConstructeurNull1 doit être null à ce stade.");
        assertTrue(sousTypeProduitConstructeurNull1.getProduits().isEmpty(), "La collection de produits de sousTypeProduitConstructeurNull1 doit être vide à ce stade.");
        assertFalse(sousTypeProduitConstructeurNull1.isValide(), "sousTypeProduitConstructeurNull1 ne doit pas être valide à ce stade.");

        /*
         * ARRANGE - GIVEN : Ajout d'un TypeProduit.
         */
        final TypeProduitI typeProduit1 = new TypeProduit(10L, PECHE, null);
        sousTypeProduitConstructeurNull1.setTypeProduit(typeProduit1);

        /*
         * ASSERT - THEN : Vérifications après ajout du TypeProduit.
         */
        assertEquals(1L, sousTypeProduitConstructeurNull1.getIdSousTypeProduit(), "L'ID de sousTypeProduitConstructeurNull1 doit valoir 1 à ce stade.");
        assertEquals(CANNE_A_PECHE, sousTypeProduitConstructeurNull1.getSousTypeProduit(), "Le sousTypeProduit de sousTypeProduitConstructeurNull1 doit valoir \"canne à pêche\" à ce stade.");
        assertEquals(typeProduit1, sousTypeProduitConstructeurNull1.getTypeProduit(), "Le typeProduit de sousTypeProduitConstructeurNull1 doit valoir typeProduit1 à ce stade.");
        assertTrue(sousTypeProduitConstructeurNull1.getProduits().isEmpty(), "La collection de produits de sousTypeProduitConstructeurNull1 doit être vide à ce stade.");
        assertTrue(sousTypeProduitConstructeurNull1.isValide(), "sousTypeProduitConstructeurNull1 doit être valide à ce stade.");
        assertFalse(typeProduit1.getSousTypeProduits().isEmpty(), "La collection sousTypeProduits de typeProduit1 doit comporter au moins un élément à ce stade.");
    } //___________________________________________________________________



    /**
     * <div>
     * <p>Teste le constructeur d'arité 1.</p>
     * <ul>
     * <li>Vérifie que toutes les propriétés de l'objet sont null sauf sousTypeProduit.</li>
     * <li>Vérifie que les Booleans valide sont à false.</li>
     * <li>Vérifie que 2 instances créées avec le constructeur d'arité 1 sont différentes.</li>
     * <li>Vérifie que 2 instances créées avec le constructeur d'arité 1 sont equals.</li>
     * <li>Vérifie que les setters fonctionnent correctement.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testSousTypeProduitString() : vérifie le comportement général du Constructeur d'arité 1")
    @Tag("constructeurs")
    @Test
    public final void testSousTypeProduitString() {
        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * ARRANGE - GIVEN : Création d'instances avec le constructeur d'arité 1.
         */
        final TypeProduitI typeProduitCree1 = new TypeProduit(10L, PECHE, null);
        final SousTypeProduitI sousTypeProduit1 = new SousTypeProduit(CANNE_A_PECHE);
        final SousTypeProduitI sousTypeProduit2 = new SousTypeProduit(MOULINET);

        /*
         * ACT - WHEN : Récupération des propriétés.
         */
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

        /*
         * Vérifications d'identité et d'égalité.
         */
        final boolean memeInstance1 = sousTypeProduit1 == sousTypeProduit2;
        final boolean memeInstance2 = sousTypeProduit2 == sousTypeProduit1;

        final boolean sousTypeProduit1EqualsSousTypeProduit2 = sousTypeProduit1.equals(sousTypeProduit2);
        final boolean sousTypeProduit2EqualsSousTypeProduit1 = sousTypeProduit2.equals(sousTypeProduit1);
        final boolean sousTypeProduit1EqualsSousTypeProduit1 = sousTypeProduit1.equals(sousTypeProduit1);

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("*** SousTypeProduit sousTypeProduit1 = new SousTypeProduit(\"canne à pêche\"); *** ");
            System.out.println("*** SousTypeProduit sousTypeProduit2 = new SousTypeProduit(\"moulinet\"); *** ");
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

        /*
         * ASSERT - THEN : Vérifications des propriétés.
         */
        assertNull(idSousTypeProduit1, "L'ID de sousTypeProduit1 doit être null.");
        assertNull(idSousTypeProduit2, "L'ID de sousTypeProduit2 doit être null.");

        assertEquals(CANNE_A_PECHE, sousTypeProduit1.getSousTypeProduit(), "sousTypeProduit1 doit valoir \"canne à pêche\".");
        assertEquals(MOULINET, sousTypeProduit2.getSousTypeProduit(), "sousTypeProduit2 doit valoir \"moulinet\".");

        assertNull(typeProduit1, "typeProduit1 doit être null à ce stade.");
        assertNull(typeProduit2, "typeProduit2 doit être null à ce stade.");
    } //___________________________________________________________________
    

	
    /**
     * <div>
     * <p>Teste les constructeurs d'arité 2, 3 et complet.</p>
     * <ul>
     * <li>Vérifie la construction et l'initialisation des objets.</li>
     * <li>Vérifie les relations bidirectionnelles avec TypeProduit.</li>
     * <li>Vérifie la cohérence des listes de produits.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testConstructeursComplets() : vérifie le comportement des constructeurs d'arité 2, 3 et complet")
    @Tag("constructeurs")
    @Test
    public final void testConstructeursComplets() {
        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * ARRANGE - GIVEN : Création des TypeProduit.
         */
        final TypeProduitI typeProduitPeche = new TypeProduit(1L, PECHE, null);
        final TypeProduitI typeProduitOutillage = new TypeProduit(2L, OUTILLAGE, null);

        /*
         * ARRANGE - GIVEN : Création des SousTypeProduit avec différents constructeurs.
         */
        final SousTypeProduitI sousTypeProduitArite2 = new SousTypeProduit(CANNE_A_PECHE, typeProduitPeche);
        final SousTypeProduitI sousTypeProduitArite3 = new SousTypeProduit(10L, MOULINET, typeProduitPeche);
        final SousTypeProduitI sousTypeProduitComplet 
        	= new SousTypeProduit(20L, CUILLERE, typeProduitOutillage, new ArrayList<ProduitI>());

        /*
         * ACT - WHEN : Vérification des propriétés.
         */
        assertNull(sousTypeProduitArite2.getIdSousTypeProduit(), "L'ID de sousTypeProduitArite2 doit être null.");
        assertEquals(CANNE_A_PECHE, sousTypeProduitArite2.getSousTypeProduit(), "Le sousTypeProduit de sousTypeProduitArite2 doit valoir \"canne à pêche\".");
        assertEquals(typeProduitPeche, sousTypeProduitArite2.getTypeProduit(), "Le typeProduit de sousTypeProduitArite2 doit être typeProduitPeche.");
        assertTrue(sousTypeProduitArite2.getProduits().isEmpty(), "La liste des produits de sousTypeProduitArite2 doit être vide.");
        assertTrue(sousTypeProduitArite2.isValide(), "sousTypeProduitArite2 doit être valide.");

        assertEquals(10L, sousTypeProduitArite3.getIdSousTypeProduit(), "L'ID de sousTypeProduitArite3 doit valoir 10.");
        assertEquals(MOULINET, sousTypeProduitArite3.getSousTypeProduit(), "Le sousTypeProduit de sousTypeProduitArite3 doit valoir \"moulinet\".");
        assertEquals(typeProduitPeche, sousTypeProduitArite3.getTypeProduit(), "Le typeProduit de sousTypeProduitArite3 doit être typeProduitPeche.");
        assertTrue(sousTypeProduitArite3.getProduits().isEmpty(), "La liste des produits de sousTypeProduitArite3 doit être vide.");
        assertTrue(sousTypeProduitArite3.isValide(), "sousTypeProduitArite3 doit être valide.");

        assertEquals(20L, sousTypeProduitComplet.getIdSousTypeProduit(), "L'ID de sousTypeProduitComplet doit valoir 20.");
        assertEquals(CUILLERE, sousTypeProduitComplet.getSousTypeProduit(), "Le sousTypeProduit de sousTypeProduitComplet doit valoir \"cuillère\".");
        assertEquals(typeProduitOutillage, sousTypeProduitComplet.getTypeProduit(), "Le typeProduit de sousTypeProduitComplet doit être typeProduitOutillage.");
        assertTrue(sousTypeProduitComplet.getProduits().isEmpty(), "La liste des produits de sousTypeProduitComplet doit être vide.");
        assertTrue(sousTypeProduitComplet.isValide(), "sousTypeProduitComplet doit être valide.");

        /*
         * ASSERT - THEN : Vérification des relations bidirectionnelles.
         */
        assertTrue(typeProduitPeche.getSousTypeProduits().contains(sousTypeProduitArite2), "La liste des sousTypeProduits de typeProduitPeche doit contenir sousTypeProduitArite2.");
        assertTrue(typeProduitPeche.getSousTypeProduits().contains(sousTypeProduitArite3), "La liste des sousTypeProduits de typeProduitPeche doit contenir sousTypeProduitArite3.");
        assertTrue(typeProduitOutillage.getSousTypeProduits().contains(sousTypeProduitComplet), "La liste des sousTypeProduits de typeProduitOutillage doit contenir sousTypeProduitComplet.");
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
        /*
         * ARRANGE - GIVEN : Création des TypeProduit.
         */
        final TypeProduitI typeProduit1 = new TypeProduit(1L, PECHE, null);
        final TypeProduitI typeProduitEquals1 = new TypeProduit(2L, PECHE, null);
        final TypeProduitI typeProduitDifferent1 = new TypeProduit(3L, OUTILLAGE, null);

        /*
         * ARRANGE - GIVEN : Création des SousTypeProduit.
         */
        final SousTypeProduitI objet1 = new SousTypeProduit(10L, CANNE_A_PECHE, typeProduit1, null);
        final SousTypeProduitI objetEquals1 = new SousTypeProduit(20L, CANNE_A_PECHE, typeProduit1, null);
        final SousTypeProduitI objetDeepEquals1 = new SousTypeProduit(30L, CANNE_A_PECHE, typeProduitEquals1, null);
        final SousTypeProduitI objetPasEqualsObjet1 = new SousTypeProduit(40L, MOULINET, typeProduit1, null);
        final SousTypeProduitI objetPasEqualsObjetDeep1 = new SousTypeProduit(10L, CANNE_A_PECHE, typeProduitDifferent1, null);
        final SousTypeProduitI objetPasEqualsObjetDeep2 = new SousTypeProduit(10L, PERCEUSE, typeProduitDifferent1, null);

        /*
         * ARRANGE - GIVEN : Mauvaise instance.
         */
        final NullPointerException mauvaiseInstance = new NullPointerException();

        /*
         * ACT - WHEN : Vérifications du contrat Java.
         */
        assertFalse(objet1.equals(mauvaiseInstance), "x.equals(mauvaise instance) doit retourner false.");
        assertEquals(objet1, objet1, "Contrat Java reflexif x.equals(x).");
        assertNotSame(objet1, objetEquals1, "objet1 et objetEquals1 ne sont pas la même instance.");
        assertEquals(objet1, objetEquals1, "objet1.equals(objetEquals1).");
        assertEquals(objetEquals1, objet1, "objetEquals1.equals(objet1).");
        assertEquals(objet1, objetDeepEquals1, "Contrat Java transitif : x.equals(y) et y.equals(z) ---> x.equals(z).");
        assertEquals(objet1.hashCode(), objetEquals1.hashCode(), "Contrat Java sur les hashcode : x.equals(y) ---> x.hashcode() == y.hashcode().");

        /*
         * ARRANGE - GIVEN : Instances avec valeurs null.
         */
        final SousTypeProduitI objetConstructeurNull1 = new SousTypeProduit();
        final SousTypeProduitI objetConstructeurNull2 = new SousTypeProduit();
        final SousTypeProduitI objetAvecValeursNull1 = new SousTypeProduit(1L, null, typeProduit1, null);
        final SousTypeProduitI objetAvecValeursNull2 = new SousTypeProduit(2L, null, typeProduitEquals1, null);
        final SousTypeProduitI objetAvecValeursVide1 = new SousTypeProduit(1L, "", typeProduit1, null);
        final SousTypeProduitI objetAvecValeursVide2 = new SousTypeProduit(2L, "", typeProduitEquals1, null);
        final SousTypeProduitI objetAvecSousValeursNull1 = new SousTypeProduit(1L, CANNE_A_PECHE, null, null);
        final SousTypeProduitI objetAvecSousValeursNull2 = new SousTypeProduit(2L, CANNE_A_PECHE, null, null);

        /*
         * ACT - WHEN : Vérifications des null.
         */
        assertEquals(objetConstructeurNull1, objetConstructeurNull2, "objetConstructeurNull1.equals(objetConstructeurNull2).");
        assertEquals(objetConstructeurNull1.hashCode(), objetConstructeurNull2.hashCode(), "objetConstructeurNull1.hashcode() == objetConstructeurNull2.hashcode().");
        assertEquals(objetAvecValeursNull1, objetAvecValeursNull2, "objetAvecValeursNull1.equals(objetAvecValeursNull2).");
        assertEquals(objetAvecValeursVide1, objetAvecValeursVide2, "objetAvecValeursVide1.equals(objetAvecValeursVide2).");
        assertEquals(objetAvecSousValeursNull1, objetAvecSousValeursNull2, "objetAvecSousValeursNull1.equals(objetAvecSousValeursNull2).");

        /*
         * ACT - WHEN : Vérifications d'inégalité.
         */
        assertNotNull(objet1, "objet1.equals(null) retourne false (avec objet1 non null).");
        assertEquals(objet1, objetDeepEquals1, "objet1.equals(objetDeepEquals1).");
        assertNotEquals(objet1, objetPasEqualsObjet1, "objet1 n'est pas equals() avec objetPasEqualsObjet1.");
        assertNotEquals(objet1, objetPasEqualsObjetDeep1, "objet1 n'est pas equals() avec objetPasEqualsObjetDeep1.");
        assertNotEquals(objet1, objetPasEqualsObjetDeep2, "objet1 n'est pas equals() avec objetPasEqualsObjetDeep2.");
    } //___________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>
     * Vérifie que l'égalité métier est insensible à la casse sur getSousTypeProduit().
     * </p>
     * <ul>
     * <li>Vérifie la symétrie.</li>
     * <li>Vérifie la cohérence equals() / hashCode().</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testEqualsIgnoreCase() : vérifie que l'égalité métier est insensible à la casse sur getSousTypeProduit()")
    @Tag("equals")
    @Test
    public final void testEqualsIgnoreCase() {

        /* AFFICHAGE DANS LE TEST ou NON */
        final boolean affichage = false;

        /* AFFICHAGE A LA CONSOLE. */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("********** CLASSE SousTypeProduitTest - méthode testEqualsIgnoreCase() ********** ");
            System.out.println("CE TEST VERIFIE L'EGALITE INSENSIBLE A LA CASSE SUR getSousTypeProduit().");
            System.out.println();
        }

        /* ARRANGE - GIVEN : Création des TypeProduit (égaux, insensibles à la casse). */
        final TypeProduitI typeProduit1 = new TypeProduit(1L, PECHE, null);
        final TypeProduitI typeProduit2 = new TypeProduit(2L, "pêche", null);

        /* ARRANGE - GIVEN : Création des SousTypeProduit (égaux, insensibles à la casse). */
        final SousTypeProduitI objet1 = new SousTypeProduit(10L, CANNE_A_PECHE, typeProduit1, null);
        final SousTypeProduitI objet2 = new SousTypeProduit(20L, "CANNE À PÊCHE", typeProduit2, null);

        /* ACT - WHEN */
        final boolean isEquals = objet1.equals(objet2);
        final boolean isEqualsSym = objet2.equals(objet1);

        /* ASSERT - THEN */
        assertTrue(isEquals,
                "Deux SousTypeProduit doivent être égaux même si la casse diffère sur getSousTypeProduit().");
        assertTrue(isEqualsSym,
                "L'égalité doit être symétrique même si la casse diffère sur getSousTypeProduit().");
        assertEquals(objet1.hashCode(), objet2.hashCode(),
                "Si equals() est true malgré une casse différente, hashCode() doit être identique.");

    } //___________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>
     * Vérifie equals() et hashCode() en environnement multi-thread.
     * </p>
     * <ul>
     * <li>Vérifie la cohérence equals() / hashCode().</li>
     * <li>Vérifie l'absence d'annulation des tâches sous timeout.</li>
     * <li>Vérifie la stabilité des résultats en exécution concurrente.</li>
     * </ul>
     * </div>
     * @throws InterruptedException si le thread courant est interrompu.
     * @throws ExecutionException si une tâche lève une exception.
     */
    @SuppressWarnings({ RESOURCE, UNUSED })
    @DisplayName("testEqualsHashCodeThreadSafe() : vérifie equals() et hashCode() en environnement multi-thread")
    @Tag(THREAD_SAFETY)
    @Test
    public final void testEqualsHashCodeThreadSafe()
            throws InterruptedException, ExecutionException {

        /* AFFICHAGE DANS LE TEST ou NON */
        final boolean affichage = false;

        /* AFFICHAGE A LA CONSOLE. */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("********** CLASSE SousTypeProduitTest - méthode testEqualsHashCodeThreadSafe() ********** ");
            System.out.println("CE TEST VERIFIE equals() ET hashCode() EN MULTI-THREAD.");
            System.out.println();
        }

        /* ARRANGE - GIVEN : Création des objets nécessaires. */
        final TypeProduit typeProduit1 = new TypeProduit(1L, PECHE, null);
        final TypeProduit typeProduit2 = new TypeProduit(2L, PECHE, null);
        final TypeProduit typeProduitDifferent = new TypeProduit(3L, OUTILLAGE, null);

        final SousTypeProduit stp1 = new SousTypeProduit(10L, CANNE_A_PECHE, typeProduit1, null);
        final SousTypeProduit stp2 = new SousTypeProduit(20L, "CANNE À PÊCHE", typeProduit2, null);
        final SousTypeProduit stp3 = new SousTypeProduit(30L, MOULINET, typeProduitDifferent, null);

        /* ACT - WHEN : Vérification des contrats Java (mono-thread). */
        assertTrue(stp1.equals(stp1),
                "x.equals(x) doit retourner true : ");

        assertTrue(stp1.equals(stp2) && stp2.equals(stp1),
                "x.equals(y) doit être symétrique : ");

        assertEquals(stp1.hashCode(), stp2.hashCode(),
                "x.equals(y) doit impliquer x.hashCode() == y.hashCode() : ");

        assertFalse(stp1.equals(null), // NOPMD by danyl on 15/02/2026 11:00
                "x.equals(null) doit retourner false : ");

        assertFalse(stp1.equals(stp3),
                "x.equals(y) doit retourner false si x != y : ");

        /* ACT - WHEN : Test multi-thread pour vérifier la thread-safety. */
        final ExecutorService executor = Executors.newFixedThreadPool(10);

        final List<Callable<Boolean>> tasks = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            tasks.add(() -> stp1.equals(stp2)
                    && stp2.equals(stp1)
                    && stp1.hashCode() == stp2.hashCode()
                    && !stp1.equals(stp3));
        }

        /* IMPORTANT : timeout pour éviter tout blocage infini si une régression introduit un deadlock. */
        final List<Future<Boolean>> results =
                executor.invokeAll(tasks, 5, java.util.concurrent.TimeUnit.SECONDS);

        executor.shutdown();

        /* ASSERT - THEN : Vérification des résultats. */
        for (final Future<Boolean> result : results) {
            assertFalse(result.isCancelled(),
                    "Une tâche equals()/hashCode() ne doit pas être annulée (timeout) : ");
            assertTrue(result.get(),
                    "equals()/hashCode() doivent rester cohérents en environnement multi-thread : ");
        }

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
			System.out.println("********** CLASSE SousTypeProduitTest - méthode testToString() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode toString().");
			System.out.println();				
		}
		
		//**** ARRANGE - GIVEN
		final SousTypeProduitI objetConstructeurNull = new SousTypeProduit();
						
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
		final TypeProduitI typeProduit1 = new TypeProduit(PECHE);
		final SousTypeProduitI objet1 = new SousTypeProduit(1L, MOULINET, typeProduit1, null);
		
		
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
				+ "typeProduit=TypeProduit "
				+ "[idTypeProduit=null, typeProduit=Pêche]]";
				
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
			System.out.println("********** CLASSE SousTypeProduitTest - méthode testCompareTo() ********** ");
			System.out.println("CE TEST VERIFIE LE RESPECT des contrats Java de la méthode compareTo().");
			System.out.println();				
		}
		
		// ARRANGE - GIVEN
		/* TypeProduit */
		final TypeProduitI typeProduit1 = new TypeProduit(PECHE);
		final TypeProduitI typeProduitEquals1 = new TypeProduit(PECHE);
		final TypeProduitI typeProduitAvant1 = new TypeProduit(OUTILLAGE);
		final TypeProduitI typeProduitApres1 = new TypeProduit("Restauration");
		/* SousTypeProduit. */
		final SousTypeProduitI objet1 
			= new SousTypeProduit(1L, CANNE_A_PECHE, typeProduit1, null);
		final SousTypeProduitI objetEqualsObjet1 
			= new SousTypeProduit(2L, CANNE_A_PECHE, typeProduit1, null);
		final SousTypeProduitI objet1MemeInstance = objet1;
		final SousTypeProduitI objet2ApresObjet1 
			= new SousTypeProduit(3L, MOULINET, typeProduitEquals1, null);
		final SousTypeProduitI objet3AvantDeepObjet1 
			= new SousTypeProduit(4L, CANNE_A_PECHE, typeProduitAvant1, null);
			
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
		final SousTypeProduitI objetConstructeurNull1 = new SousTypeProduit();
		final SousTypeProduitI objetConstructeurNull2 = new SousTypeProduit();
		
		final SousTypeProduitI objetAvecValeursNull1 = new SousTypeProduit(1L, null, typeProduit1, null);
		final SousTypeProduitI objetAvecValeursNullDeepEquals = new SousTypeProduit(2L, null, typeProduitEquals1, null);
		final SousTypeProduitI objetAvecValeursNull2Apres1 = new SousTypeProduit(3L, null, typeProduitApres1, null);

		final SousTypeProduitI objetAvecValeursVide1 = new SousTypeProduit(1L, "", typeProduit1, null);
		final SousTypeProduitI objetAvecValeursVide2 = new SousTypeProduit(2L, "", typeProduitApres1, null);
		
		final SousTypeProduitI objetAvecSousValeursNull1 = new SousTypeProduit(1L, CANNE_A_PECHE, null, null);
		final SousTypeProduitI objetAvecSousValeursNull2 = new SousTypeProduit(2L, CANNE_A_PECHE, null, null);

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
		final SousTypeProduitI objetAvecV1Null = new SousTypeProduit(CANNE_A_PECHE, null);
		final SousTypeProduitI objetAvecV1PasNull = new SousTypeProduit(null, typeProduit1);
		final SousTypeProduitI objetAvecV1PasNullAvantObjet2 = new SousTypeProduit(null, typeProduitAvant1);
		final SousTypeProduitI objetAvecV1PasNullApresObjet1 = new SousTypeProduit(null, typeProduit1);
		final SousTypeProduitI objetAvecV2PasNullAvantObjet2 = new SousTypeProduit("aaa", typeProduit1);
		final SousTypeProduitI objetAvecV2PasNullApresObjet1 = new SousTypeProduit("zzz", typeProduit1);
		final SousTypeProduitI objetAvecV2PasNullIdemObjet2 = new SousTypeProduit("idem", typeProduit1);
		final SousTypeProduitI objetAvecV2PasNullIdemObjet1 = new SousTypeProduit("idem", typeProduit1);
		final SousTypeProduitI objetAvecV2Null1 = new SousTypeProduit(null, typeProduitApres1);
		final SousTypeProduitI objetAvecV2Null2 = new SousTypeProduit(null, typeProduitApres1);
		
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
			System.out.println("********** CLASSE SousTypeProduitTest - méthode testClone() ********** ");
			System.out.println("CE TEST VERIFIE LE RESPECT des contrats Java de la méthode clone().");
			System.out.println();				
		}

		//***** ARRANGE - GIVEN
		final SousTypeProduit objetConstructeurNull = new SousTypeProduit();
		

		// ACT - WHEN
		final SousTypeProduit objetConstructeurNullClone 
			= (SousTypeProduit) objetConstructeurNull.clone();
				
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
		/* TypeProduit */
		final TypeProduit typeProduit1 = new TypeProduit(PECHE);
		/* SousTypeProduit. */
		final SousTypeProduit objet1 
			= new SousTypeProduit(1L, CANNE_A_PECHE, typeProduit1, null);
				
		// ACT - WHEN
		final SousTypeProduit objet1Clone = objet1.clone();
						
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
		final TypeProduit typeProduitClone 
			= new TypeProduit(100L, "Type produit clone modifié", null);
		objet1Clone.setIdSousTypeProduit(2L);		
		objet1Clone.setSousTypeProduit("clone modifié");
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
		assertFalse(objet1.getTypeProduit().equals(objet1Clone.getTypeProduit()), "la modification du TypeProduit dans le clone ne doit pas modifier le TypeProduit dans l'objet cloné : ");
		assertFalse(objet1.getSousTypeProduit().equals(objet1Clone.getSousTypeProduit()), "la modification du sousTypeProduit dans le clone ne doit pas modifier le sousTypeProduit dans l'objet cloné : ");
		
	} //___________________________________________________________________
	
	

    /**
     * <div>
     * <p>Teste la méthode deepClone().</p>
     * <ul>
     * <li>Vérifie le clonage profond avec CloneContext.</li>
     * <li>Vérifie la gestion des cycles.</li>
     * <li>Vérifie que le clone est indépendant de l'original.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testDeepClone() : vérifie le clonage profond avec CloneContext")
    @Tag("clone")
    @Test
    public final void testDeepClone() {
        /*
         * ARRANGE - GIVEN : Création des TypeProduit.
         */
        final TypeProduit typeProduitPeche = new TypeProduit(1L, PECHE, null);
        final TypeProduit typeProduitOutillage = new TypeProduit(2L, OUTILLAGE, null);

        /*
         * ARRANGE - GIVEN : Création des SousTypeProduit.
         */
        final SousTypeProduit sousTypeProduitCanneAPeche = new SousTypeProduit(10L, CANNE_A_PECHE, typeProduitPeche, null);
        final SousTypeProduit sousTypeProduitMoulinet = new SousTypeProduit(20L, MOULINET, typeProduitPeche, null);

        /*
         * ARRANGE - GIVEN : Création des Produit.
         */
        final Produit produitCanneTelescopique = new Produit(100L, CANNE_TELESCOPIQUE, sousTypeProduitCanneAPeche);
        final Produit produitMoulinetTambourFixe = new Produit(200L, MOULINET_TAMBOUR_FIXE, sousTypeProduitMoulinet);

        /*
         * ACT - WHEN : Ajout des Produit aux SousTypeProduit.
         */
        final List<ProduitI> produitsCanneAPeche 
        	= new ArrayList<ProduitI>(
        			sousTypeProduitCanneAPeche.getProduits());
        final List<ProduitI> produitsMoulinet 
    	= new ArrayList<ProduitI>(
    			sousTypeProduitMoulinet.getProduits());
        
        produitsCanneAPeche.add(produitCanneTelescopique);
        produitsMoulinet.add(produitMoulinetTambourFixe);

        /*
         * ACT - WHEN : Clonage profond.
         */
        final CloneContext ctx = new CloneContext();
        final SousTypeProduit cloneCanneAPeche = sousTypeProduitCanneAPeche.deepClone(ctx);
        final SousTypeProduit cloneMoulinet = sousTypeProduitMoulinet.deepClone(ctx);

        /*
         * ASSERT - THEN : Vérifications du clonage.
         */
        assertNotSame(sousTypeProduitCanneAPeche, cloneCanneAPeche, "Le clone ne doit pas être la même instance que l'original.");
        assertEquals(sousTypeProduitCanneAPeche, cloneCanneAPeche, "Le clone doit être equals() à l'original.");
        assertEquals(sousTypeProduitCanneAPeche.getIdSousTypeProduit(), cloneCanneAPeche.getIdSousTypeProduit(), "L'ID doit être identique.");
        assertEquals(sousTypeProduitCanneAPeche.getSousTypeProduit(), cloneCanneAPeche.getSousTypeProduit(), "Le sousTypeProduit doit être identique.");
        assertEquals(sousTypeProduitCanneAPeche.getTypeProduit(), cloneCanneAPeche.getTypeProduit(), "Le typeProduit doit être identique.");
        assertFalse(sousTypeProduitCanneAPeche.getProduits() == cloneCanneAPeche.getProduits(), "Les listes de produits ne doivent pas être les mêmes instances.");
        assertEquals(sousTypeProduitCanneAPeche.getProduits().size(), cloneCanneAPeche.getProduits().size(), "Le nombre de produits doit être identique.");

        /*
         * ACT - WHEN : Modification du clone.
         */
        cloneCanneAPeche.setSousTypeProduit("canne modifiée");
        cloneCanneAPeche.getTypeProduit().setTypeProduit("pêche modifiée");

        /*
         * ASSERT - THEN : Vérifications après modification.
         */
        assertNotEquals(sousTypeProduitCanneAPeche.getSousTypeProduit(), cloneCanneAPeche.getSousTypeProduit(), "La modification du clone ne doit pas affecter l'original.");
        assertNotEquals(sousTypeProduitCanneAPeche.getTypeProduit().getTypeProduit(), cloneCanneAPeche.getTypeProduit().getTypeProduit(), "La modification du typeProduit du clone ne doit pas affecter l'original.");
    } //___________________________________________________________________
    

    
    /**
     * <div>
     * <p>Teste les méthodes getEnTeteCsv() et toStringCsv().</p>
     * <ul>
     * <li>Vérifie le format CSV.</li>
     * <li>Vérifie la gestion des null.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testCsv() : vérifie le comportement des méthodes getEnTeteCsv() et toStringCsv()")
    @Tag("csv")
    @Test
    public final void testCsv() {
        /*
         * ARRANGE - GIVEN : Instance avec constructeur d'arité nulle.
         */
        final SousTypeProduitI objetConstructeurNull = new SousTypeProduit();

        /*
         * ACT - WHEN : Vérification de l'en-tête CSV.
         */
        final String enTeteCsv = objetConstructeurNull.getEnTeteCsv();
        final String enTeteCsvPrevue = "idSousTypeProduit;type de produit;sous-type de produit;";
        assertEquals(enTeteCsvPrevue, enTeteCsv, "L'en-tête CSV doit être correct.");

        /*
         * ACT - WHEN : Vérification du toStringCsv() avec null.
         */
        final String toStringCsvNull = objetConstructeurNull.toStringCsv();
        final String toStringCsvPrevueNull = "null;null;null;";
        assertEquals(toStringCsvPrevueNull, toStringCsvNull, "toStringCsv() doit retourner \"null;null;null;\" avec des valeurs null.");

        /*
         * ARRANGE - GIVEN : Instance avec constructeur complet.
         */
        final TypeProduitI typeProduit1 = new TypeProduit(1L, PECHE, null);
        final SousTypeProduitI objet1 = new SousTypeProduit(10L, MOULINET, typeProduit1, null);

        /*
         * ACT - WHEN : Vérification du toStringCsv() avec valeurs.
         */
        final String toStringCsv = objet1.toStringCsv();
        final String toStringCsvPrevue = "10;Pêche;moulinet;";
        assertEquals(toStringCsvPrevue, toStringCsv, "toStringCsv() doit retourner \"10;Pêche;moulinet;\" avec les valeurs attendues.");
    } //___________________________________________________________________
    
    

    /**
     * <div>
     * <p>Teste les méthodes getEnTeteColonne() et getValeurColonne().</p>
     * <ul>
     * <li>Vérifie les en-têtes de colonne.</li>
     * <li>Vérifie les valeurs de colonne.</li>
     * <li>Vérifie la gestion des null et des indices invalides.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testJTable() : vérifie le comportement des méthodes getEnTeteColonne() et getValeurColonne()")
    @Tag("JTable")
    @Test
    public final void testJTable() {
        /*
         * ARRANGE - GIVEN : Instance avec constructeur d'arité nulle.
         */
        final SousTypeProduitI objetConstructeurNull = new SousTypeProduit();

        /*
         * ACT - WHEN : Vérification des en-têtes de colonne.
         */
        final String enTete0 = objetConstructeurNull.getEnTeteColonne(0);
        final String enTete1 = objetConstructeurNull.getEnTeteColonne(1);
        final String enTete2 = objetConstructeurNull.getEnTeteColonne(2);
        final String enTete7 = objetConstructeurNull.getEnTeteColonne(7);

        assertEquals("idSousTypeProduit", enTete0, "enTete0 doit retourner \"idSousTypeProduit\".");
        assertEquals("type de produit", enTete1, "enTete1 doit retourner \"type de produit\".");
        assertEquals("sous-type de produit", enTete2, "enTete2 doit retourner \"sous-type de produit\".");
        assertEquals(INVALIDE, enTete7, "enTete7 doit retourner \"invalide\".");

        /*
         * ACT - WHEN : Vérification des valeurs de colonne avec null.
         */
        final String valeur0Null = (String) objetConstructeurNull.getValeurColonne(0);
        final String valeur1Null = (String) objetConstructeurNull.getValeurColonne(1);
        final String valeur2Null = (String) objetConstructeurNull.getValeurColonne(2);
        final String valeur7Null = (String) objetConstructeurNull.getValeurColonne(7);

        assertNull(valeur0Null, "valeur0Null doit retourner null.");
        assertNull(valeur1Null, "valeur1Null doit retourner null.");
        assertNull(valeur2Null, "valeur2Null doit retourner null.");
        assertEquals(INVALIDE, valeur7Null, "valeur7Null doit retourner \"invalide\".");

        /*
         * ARRANGE - GIVEN : Instance avec constructeur complet.
         */
        final TypeProduitI typeProduit1 = new TypeProduit(1L, PECHE, null);
        final SousTypeProduitI objet1 = new SousTypeProduit(10L, MOULINET, typeProduit1, null);

        /*
         * ACT - WHEN : Vérification des valeurs de colonne avec valeurs.
         */
        final String valeur0 = (String) objet1.getValeurColonne(0);
        final String valeur1 = (String) objet1.getValeurColonne(1);
        final String valeur2 = (String) objet1.getValeurColonne(2);
        final String valeur7 = (String) objet1.getValeurColonne(7);

        assertEquals("10", valeur0, "valeur0 doit retourner \"10\".");
        assertEquals("Pêche", valeur1, "valeur1 doit retourner \"Pêche\".");
        assertEquals("moulinet", valeur2, "valeur2 doit retourner \"moulinet\".");
        assertEquals(INVALIDE, valeur7, "valeur7 doit retourner \"invalide\".");
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
			System.out.println("********** CLASSE SousTypeProduitTest - méthode testGetEnTeteCsv() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode getEnTeteCsv().");
			System.out.println();				
		}
		
		//**** ARRANGE - GIVEN
		final SousTypeProduitI objetConstructeurNull = new SousTypeProduit();
		
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
			System.out.println("********** CLASSE SousTypeProduitTest - méthode testToStringCsv() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode toStringCsv().");
			System.out.println();				
		}
			
		// *** ARRANGE - GIVEN
		final SousTypeProduitI objetConstructeurNull = new SousTypeProduit();
		
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
		final TypeProduitI typeProduit1 = new TypeProduit(PECHE);
		final SousTypeProduitI objet1 = new SousTypeProduit(1L, MOULINET, typeProduit1, null);
		
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
			System.out.println("********** CLASSE SousTypeProduitTest - méthode testGetEnTeteColonne() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode getEnTeteColonne().");
			System.out.println();				
		}
				
		// *** ARRANGE - GIVEN
		final SousTypeProduitI objetConstructeurNull = new SousTypeProduit();
		
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
		assertEquals(INVALIDE, enTete7, "enTete7 doit retourner \"invalide\" :  ");
		
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
			System.out.println("********** CLASSE SousTypeProduitTest - méthode testGetValeurColonne() ********** ");
			System.out.println("CE TEST VERIFIE LE BON FONCTIONNEMENT de la méthode getValeurColonne().");
			System.out.println();				
		}
		
		// *** ARRANGE - GIVEN
		final SousTypeProduitI objetConstructeurNull = new SousTypeProduit();
		
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
		assertEquals(INVALIDE, valeur7Null, "valeur7Null doit retourner \"invalide\" :  ");

		
		// *** ARRANGE - GIVEN
		final TypeProduitI typeProduit1 = new TypeProduit(PECHE);
		final SousTypeProduitI objet1 = new SousTypeProduit(1L, MOULINET, typeProduit1, null);
		
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
		assertEquals(INVALIDE, valeur7Null, "valeur7Null doit retourner \"invalide\" :  ");
		
	} //___________________________________________________________________
	
	
	
	   /**
     * <div>
     * <p>Teste les méthodes ajouterSTPauProduit() et retirerSTPauProduit().</p>
     * <ul>
     * <li>Vérifie l'ajout et le retrait de Produit.</li>
     * <li>Vérifie la cohérence des relations bidirectionnelles.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testGestionProduits() : vérifie l'ajout et le retrait de Produit")
    @Tag("relations")
    @Test
    public final void testGestionProduits() {
        /*
         * ARRANGE - GIVEN : Création des TypeProduit et SousTypeProduit.
         */
        final TypeProduitI typeProduitPeche = new TypeProduit(1L, PECHE, null);
        final SousTypeProduitI sousTypeProduitCanneAPeche = new SousTypeProduit(10L, CANNE_A_PECHE, typeProduitPeche, null);

        /*
         * ARRANGE - GIVEN : Création des Produit.
         */
        final ProduitI produitCanneTelescopique = new Produit(100L, CANNE_TELESCOPIQUE, null);

        /*
         * ACT - WHEN : Ajout d'un Produit.
         */
        sousTypeProduitCanneAPeche.ajouterSTPauProduit(produitCanneTelescopique);

        /*
         * ASSERT - THEN : Vérifications après ajout.
         */
        assertTrue(sousTypeProduitCanneAPeche.getProduits().contains(produitCanneTelescopique), "La liste des produits doit contenir produitCanneTelescopique.");
        assertEquals(sousTypeProduitCanneAPeche, produitCanneTelescopique.getSousTypeProduit(), "Le sousTypeProduit du produit doit être sousTypeProduitCanneAPeche.");

        /*
         * ACT - WHEN : Retrait du Produit.
         */
        sousTypeProduitCanneAPeche.retirerSTPauProduit(produitCanneTelescopique);

        /*
         * ASSERT - THEN : Vérifications après retrait.
         */
        assertFalse(sousTypeProduitCanneAPeche.getProduits().contains(produitCanneTelescopique), "La liste des produits ne doit plus contenir produitCanneTelescopique.");
        assertNull(produitCanneTelescopique.getSousTypeProduit(), "Le sousTypeProduit du produit doit être null.");
        
    } //___________________________________________________________________
    
    

    /**
     * <div>
     * <p>Teste la méthode isValide().</p>
     * <ul>
     * <li>Vérifie que isValide() retourne true si le TypeProduit est non null.</li>
     * <li>Vérifie que isValide() retourne false si le TypeProduit est null.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testIsValide() : vérifie le comportement de la méthode isValide()")
    @Tag("valide")
    @Test
    public final void testIsValide() {
        /*
         * ARRANGE - GIVEN : Instance avec TypeProduit null.
         */
        final SousTypeProduitI sousTypeProduitSansType = new SousTypeProduit(10L, CANNE_A_PECHE, null, null);

        /*
         * ACT - WHEN : Vérification de isValide() avec TypeProduit null.
         */
        assertFalse(sousTypeProduitSansType.isValide(), "isValide() doit retourner false si le TypeProduit est null.");

        /*
         * ARRANGE - GIVEN : Instance avec TypeProduit non null.
         */
        final TypeProduitI typeProduitPeche = new TypeProduit(1L, PECHE, null);
        final SousTypeProduitI sousTypeProduitAvecType = new SousTypeProduit(20L, MOULINET, typeProduitPeche, null);

        /*
         * ACT - WHEN : Vérification de isValide() avec TypeProduit non null.
         */
        assertTrue(sousTypeProduitAvecType.isValide(), "isValide() doit retourner true si le TypeProduit est non null.");
        
    } //___________________________________________________________________
    
    

    /**
     * <div>
     * <p>Teste la méthode cloneWithoutParentAndChildren().</p>
     * <ul>
     * <li>Vérifie que le clone est indépendant de l'original.</li>
     * <li>Vérifie que les propriétés sont copiées correctement.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testCloneWithoutParentAndChildren() : vérifie le clonage sans parent ni enfants")
    @Tag("clone")
    @Test
    public final void testCloneWithoutParentAndChildren() {
        /*
         * ARRANGE - GIVEN : Instance avec constructeur complet.
         */
        final TypeProduitI typeProduit1 = new TypeProduit(1L, PECHE, null);
        final SousTypeProduitI objet1 = new SousTypeProduit(10L, CANNE_A_PECHE, typeProduit1, null);

        /*
         * ACT - WHEN : Clonage sans parent ni enfants.
         */
        final SousTypeProduitI clone = objet1.cloneWithoutParentAndChildren();

        /*
         * ASSERT - THEN : Vérifications du clone.
         */
        assertNotSame(objet1, clone, "Le clone ne doit pas être la même instance que l'original.");
        assertEquals(objet1.getIdSousTypeProduit(), clone.getIdSousTypeProduit(), "L'ID doit être identique.");
        assertEquals(objet1.getSousTypeProduit(), clone.getSousTypeProduit(), "Le sousTypeProduit doit être identique.");
        assertNull(clone.getTypeProduit(), "Le typeProduit du clone doit être null.");
        assertTrue(clone.getProduits().isEmpty(), "La liste des produits du clone doit être vide.");
        assertFalse(clone.isValide(), "Le clone ne doit pas être valide.");
    } //___________________________________________________________________
    
    

    /**
     * <div>
     * <p>Teste la méthode recalculerValide().</p>
     * <ul>
     * <li>Vérifie que la validité est recalculée correctement.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testRecalculerValide() : vérifie le recalcul de la validité")
    @Tag("valide")
    @Test
    public final void testRecalculerValide() {
        /*
         * ARRANGE - GIVEN : Instance avec TypeProduit null.
         */
        final SousTypeProduitI objetSansType = new SousTypeProduit(10L, CANNE_A_PECHE, null, null);

        /*
         * ACT - WHEN : Vérification de la validité.
         */
        assertFalse(objetSansType.isValide(), "L'objet doit être invalide si le TypeProduit est null.");

        /*
         * ARRANGE - GIVEN : Ajout d'un TypeProduit.
         */
        final TypeProduitI typeProduit1 = new TypeProduit(1L, PECHE, null);
        objetSansType.setTypeProduit(typeProduit1);

        /*
         * ACT - WHEN : Vérification de la validité après ajout.
         */
        assertTrue(objetSansType.isValide(), "L'objet doit être valide si le TypeProduit est non null.");
    } //___________________________________________________________________
    
    

    /**
     * <div>
     * <p>Teste la méthode utilitaire <code>normalize(String)</code> via réflexion.</p>
     * <ul>
     * <li>Vérifie la normalisation des chaînes (trim + null si vide).</li>
     * <li>Utilise la réflexion pour accéder à la méthode privée.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testNormalizeViaReflexion() : vérifie normalize(String) via réflexion")
    @Tag("utilitaires")
    @Test
    public final void testNormalizeViaReflexion() throws Exception {
        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * ARRANGE - GIVEN : Récupération de la méthode normalize via réflexion
         */
        final java.lang.reflect.Method methodNormalize = SousTypeProduit.class.getDeclaredMethod("normalize", String.class);
        methodNormalize.setAccessible(true); // NOPMD by danyl on 05/02/2026 02:18

        /*
         * ACT - WHEN : Tests de normalisation
         */
        // Test avec null
        final String resultNull = (String) methodNormalize.invoke(null, (String) null);
        assertNull(resultNull, "normalize(null) doit retourner null.");

        // Test avec chaîne vide
        final String resultEmpty = (String) methodNormalize.invoke(null, "   ");
        assertNull(resultEmpty, "normalize(\"   \") doit retourner null.");

        // Test avec chaîne à trimmer
        final String resultTrim = (String) methodNormalize.invoke(null, " test ");
        assertEquals("test", resultTrim, "normalize(\" test \") doit retourner \"test\".");

        // Test avec chaîne déjà normalisée
        final String resultNormalized = (String) methodNormalize.invoke(null, "test");
        assertEquals("test", resultNormalized, "normalize(\"test\") doit retourner \"test\".");

        /*
         * AFFICHAGE A LA CONSOLE
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("**** TEST NORMALIZE VIA REFLEXION *****");
            System.out.println("normalize(null) -> " + resultNull);
            System.out.println("normalize(\"   \") -> " + resultEmpty);
            System.out.println("normalize(\" test \") -> " + resultTrim);
            System.out.println("normalize(\"test\") -> " + resultNormalized);
        }
    } //___________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>Teste les méthodes internalAddProduit() et internalRemoveProduit().</p>
     * <ul>
     * <li>Vérifie l'ajout et le retrait interne de Produit.</li>
     * <li>Vérifie la cohérence des listes.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testInternalGestionProduits() : vérifie l'ajout et le retrait interne de Produit")
    @Tag("relations")
    @Test
    public final void testInternalGestionProduits() {
        /*
         * ARRANGE - GIVEN : Création des TypeProduit et SousTypeProduit.
         */
        final TypeProduitI typeProduitPeche = new TypeProduit(1L, PECHE, null);
        final SousTypeProduit sousTypeProduitCanneAPeche = new SousTypeProduit(10L, CANNE_A_PECHE, typeProduitPeche, null);

        /*
         * ARRANGE - GIVEN : Création des Produit.
         */
        final Produit produitCanneTelescopique = new Produit(100L, CANNE_TELESCOPIQUE, null);

        /*
         * ACT - WHEN : Ajout interne d'un Produit.
         */
        sousTypeProduitCanneAPeche.internalAddProduit(produitCanneTelescopique);

        /*
         * ASSERT - THEN : Vérifications après ajout.
         */
        assertTrue(
        		sousTypeProduitCanneAPeche.getProduits().contains(produitCanneTelescopique),
        		"La liste des produits doit contenir produitCanneTelescopique.");

        /*
         * ACT - WHEN : Retrait interne du Produit.
         */
        sousTypeProduitCanneAPeche.internalRemoveProduit(produitCanneTelescopique);

        /*
         * ASSERT - THEN : Vérifications après retrait.
         */
        assertFalse(sousTypeProduitCanneAPeche.getProduits().contains(produitCanneTelescopique),
        		"La liste des produits ne doit plus contenir produitCanneTelescopique.");
        
    } //___________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>Teste la méthode <b>toString()</b> 
     * en environnement multi-thread.</p>
     * <ul>
     * <li>Vérifie que l'appel concurrent à toString() 
     * ne provoque pas d'erreurs.</li>
     * <li>Utilise un ExecutorService pour simuler 
     * des accès concurrents.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testToStringThreadSafe() : vérifie le thread-safety de toString()")
    @Tag(THREAD_SAFETY)
    @Test
    public final void testToStringThreadSafe() 
    		throws InterruptedException, ExecutionException {
        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * ARRANGE - GIVEN : Création d'un SousTypeProduit.
         */
        final TypeProduitI typeProduit = new TypeProduit(1L, PECHE, null);
        final SousTypeProduitI sousTypeProduit 
        	= new SousTypeProduit(10L, CANNE_A_PECHE, typeProduit, null);

        /*
         * ACT - WHEN : Exécution concurrente de toString().
         */
        @SuppressWarnings(RESOURCE)
		final ExecutorService executor = Executors.newFixedThreadPool(10);
        final List<Callable<String>> tasks = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            tasks.add(() -> sousTypeProduit.toString());
        }

        final List<Future<String>> results = executor.invokeAll(tasks);
        executor.shutdown();

        /*
         * ASSERT - THEN : Vérification des résultats.
         */
        for (final Future<String> result : results) {
            assertNotNull(result.get()
            		, "toString() ne doit jamais retourner null en environnement multi-thread.");
        }

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("***** Test toString() en multi-thread réussi *****");
            System.out.println("Résultat de toString() : " + sousTypeProduit.toString());
        }
    }
    
    
    
    /**
     * <div>
     * <p>Teste la méthode <b>getProduits()</b> en environnement multi-thread.</p>
     * <ul>
     * <li>Vérifie que l'appel concurrent à getProduits() ne provoque pas de ConcurrentModificationException.</li>
     * <li>Utilise un ExecutorService pour simuler des accès concurrents.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings({ RESOURCE, UNUSED})
    @DisplayName("testGetProduitsThreadSafe() : vérifie le thread-safety de getProduits()")
    @Tag(THREAD_SAFETY)
    @Test
    public final void testGetProduitsThreadSafe() throws InterruptedException, ExecutionException {
        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * ARRANGE - GIVEN : Création d'un SousTypeProduit avec des Produits.
         */
        final TypeProduitI typeProduit = new TypeProduit(1L, PECHE, null);
        final SousTypeProduitI sousTypeProduit = new SousTypeProduit(10L, CANNE_A_PECHE, typeProduit, null);
        final ProduitI produit1 = new Produit(100L, CANNE_TELESCOPIQUE, sousTypeProduit);
        final ProduitI produit2 = new Produit(200L, MOULINET_TAMBOUR_FIXE, sousTypeProduit);
        sousTypeProduit.ajouterSTPauProduit(produit1);
        sousTypeProduit.ajouterSTPauProduit(produit2);

        /*
         * ACT - WHEN : Exécution concurrente de getProduits().
         */
        final ExecutorService executor = Executors.newFixedThreadPool(10);
        final List<Callable<List<? extends ProduitI>>> tasks = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            tasks.add(() -> sousTypeProduit.getProduits());
        }

        final List<Future<List<? extends ProduitI>>> results 
        	= executor.invokeAll(tasks);
        executor.shutdown();

        /*
         * ASSERT - THEN : Vérification des résultats.
         */
        for (final Future<List<? extends ProduitI>> result : results) {
            assertEquals(2, result.get().size()
            		, "getProduits() doit toujours retourner 2 produits en environnement multi-thread.");
        }

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("***** Test getProduits() en multi-thread réussi *****");
            System.out.println("Nombre de produits : " + sousTypeProduit.getProduits().size());
        }
    }
    
    
    
    /**
     * <div>
     * <p>Teste les méthodes <b>ajouterSTPauProduit()</b> et <b>retirerSTPauProduit()</b> en environnement multi-thread.</p>
     * <ul>
     * <li>Vérifie que les ajouts/retraits concurrents ne corrompent pas la liste des produits.</li>
     * <li>Utilise un ExecutorService pour simuler des modifications concurrentes.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings({ RESOURCE, UNUSED})
    @DisplayName("testAjouterRetirerProduitThreadSafe() : vérifie le thread-safety de ajouterSTPauProduit() et retirerSTPauProduit()")
    @Tag(THREAD_SAFETY)
    @Test
    public final void testAjouterRetirerProduitThreadSafe() 
    		throws InterruptedException, ExecutionException {
        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * ARRANGE - GIVEN : Création d'un SousTypeProduit.
         */
        final TypeProduitI typeProduit = new TypeProduit(1L, PECHE, null);
        final SousTypeProduitI sousTypeProduit 
        	= new SousTypeProduit(10L, CANNE_A_PECHE, typeProduit, null);

        /*
         * ACT - WHEN : Exécution concurrente d'ajouts et retraits.
         */
        final ExecutorService executor = Executors.newFixedThreadPool(10);
        final List<Callable<Void>> tasks = new ArrayList<>();
        
        for (int i = 0; i < 50; i++) {
        	
            final ProduitI produit 
            	= new Produit((long) i, "Produit " + i, null);
            
            tasks.add(() -> {
                sousTypeProduit.ajouterSTPauProduit(produit);
                sousTypeProduit.retirerSTPauProduit(produit);
                return null;
            });
        }

        executor.invokeAll(tasks);
        executor.shutdown();

        /*
         * ASSERT - THEN : Vérification de la cohérence de la liste.
         */
        assertTrue(sousTypeProduit.getProduits().isEmpty()
        		, "La liste des produits doit être vide après des ajouts/retraits concurrents.");

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("***** Test ajouterSTPauProduit() et retirerSTPauProduit() en multi-thread réussi *****");
            System.out.println("Nombre de produits : " + sousTypeProduit.getProduits().size());
        }
    }
    
    
    
    /**
     * <div>
     * <p>Teste la méthode <b>deepClone()</b> en environnement multi-thread.</p>
     * <ul>
     * <li>Vérifie que le clonage profond est thread-safe.</li>
     * <li>Utilise un ExecutorService pour simuler des clonages concurrents.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings({ "unchecked", RESOURCE, UNUSED})
    @DisplayName("testDeepCloneThreadSafe() : vérifie le thread-safety de deepClone()")
    @Tag(THREAD_SAFETY)
    @Test
    public final void testDeepCloneThreadSafe() throws InterruptedException, ExecutionException {
        /*
         * AFFICHAGE DANS LE TEST ou NON
         */
        final boolean affichage = false;

        /*
         * ARRANGE - GIVEN : Création d'un SousTypeProduit avec des Produits.
         */
        final TypeProduit typeProduit = new TypeProduit(1L, PECHE, null);
        final SousTypeProduit sousTypeProduit = new SousTypeProduit(10L, CANNE_A_PECHE, typeProduit, null);
        final Produit produit = new Produit(100L, CANNE_TELESCOPIQUE, sousTypeProduit);
        sousTypeProduit.ajouterSTPauProduit(produit);

        /*
         * ACT - WHEN : Exécution concurrente de deepClone().
         */
        final ExecutorService executor = Executors.newFixedThreadPool(10);
        final List<Callable<SousTypeProduit>> tasks = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            tasks.add((Callable<SousTypeProduit>) () -> sousTypeProduit.deepClone(new CloneContext()));
        }

        final List<Future<SousTypeProduit>> results = executor.invokeAll(tasks);
        executor.shutdown();

        /*
         * ASSERT - THEN : Vérification des clones.
         */
        for (final Future<SousTypeProduit> result : results) {
            final SousTypeProduit clone = result.get();
            assertEquals(sousTypeProduit, clone, "Le clone doit être equals() à l'original.");
            assertNotSame(sousTypeProduit, clone, "Le clone ne doit pas être la même instance que l'original.");
        }

        /*
         * AFFICHAGE A LA CONSOLE.
         */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("***** Test deepClone() en multi-thread réussi *****");
            System.out.println("Nombre de clones créés : " + results.size());
        }
    }
    
	
	
	/**
	 * <div>
	 * <p>affiche à la console un SousTypeProduit.</p>
	 * </div>
	 *
	 * @param pSousTypeProduit : SousTypeProduit
	 */
	private void afficher(final SousTypeProduitI pSousTypeProduit) {
		
		if (pSousTypeProduit == null) {
			return;
		}
		
		System.out.println("id du SousTypeProduit : " + pSousTypeProduit.getIdSousTypeProduit());
		System.out.println("SousTypeProduit : " + pSousTypeProduit.getSousTypeProduit());
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
	 * du TypeProduit contenu dans le SousTypeProduit pSousTypeProduit</p>
	 * </div>
	 *
	 * @param pSousTypeProduit : SousTypeProduit
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
			System.out.println("la liste sousTypeProduits du TypeProduit est null");
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
	 * @param pTypeProduit : TypeProduit
	 */
	private void afficherSousTypeProduitsDansTypeProduit(
			final TypeProduitI pTypeProduit) {
		
		if (pTypeProduit == null) {
			return;
		}
		
		final List<? extends SousTypeProduitI> sousTypeProduits 
			= pTypeProduit.getSousTypeProduits();
				
		if (sousTypeProduits == null) {
			System.out.println("la liste sousTypeProduits du TypeProduit est null");
			return;
		}
		
		for (final SousTypeProduitI sousTypeProduit : sousTypeProduits) {
			System.out.println("sousTypeProduit dans la collection sousTypeProduits du typeProduit " 
					+ pTypeProduit.afficherTypeProduit() + " : " 
					+ sousTypeProduit.afficherSousTypeProduit());
		}
		
	}
	

}
