/* ********************************************************************* */
/* **************** TEST INTEGRATION TYPE / SOUS-TYPE ****************** */
/* ********************************************************************* */
package levy.daniel.application.model.metier.produittype;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * <div>
 * <style>p, ul, li {line-height : 1em;}</style>
 * <p style="font-weight:bold;">
 * CLASSE TypeProduitSousTypeProduitIntegrationTest :</p>
 * <p>Tests d'intégration entre 
 * <span style="font-weight:bold;">TypeProduit</span> 
 * et <span style="font-weight:bold;">SousTypeProduit</span>.</p>
 * </div>
 *
 * <div>
 * <p style="font-weight:bold;">OBJECTIF :</p>
 * <ul>
 * <li>valider la cohérence bidirectionnelle parent/enfant.</li>
 * <li>valider l'absence de deadlock en contexte concurrent.</li>
 * <li>valider la robustesse du re-parenting.</li>
 * </ul>
 * </div>
 *
 * @author Daniel LEVY
 */
@Tag("integration")
public class TypeProduitSousTypeProduitIntegrationTest {

	// ====================== CONSTANTES ================================ //
	
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
     * "getEnTeteCsv : "
     */
    public static final String GETENTETECSV = "getEnTeteCsv : ";
    
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
     * "invalide"
     */
    public static final String INVALIDE = "invalide";

    /**
     * "csv"
     */
    public static final String CSV = "csv";

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
    private static final Logger LOG 
    	= LogManager.getLogger(TypeProduitTest.class);


    // *************************METHODES************************************/


    
    /**
     * <div>
     * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
     * </div>
     */
    public TypeProduitSousTypeProduitIntegrationTest() {
        super();
    } //___________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>
     * Teste la cohérence bidirectionnelle nominale TypeProduit &lt;-&gt; SousTypeProduit.
     * </p>
     * <ul>
     * <li>Rattache 2 enfants à un parent via setSousTypeProduits(...).</li>
     * <li>Vérifie la présence des enfants dans la liste du parent par identité (==).</li>
     * <li>Vérifie que chaque enfant pointe vers le bon parent via getTypeProduit().</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings({ "unchecked", UNUSED })
    @DisplayName("testIntegrationBidirectionnelleNominale() : vérifie parent<->enfant en nominal")
    @Tag(RELATIONS)
    @Test
    public final void testIntegrationBidirectionnelleNominale() {

        /* ARRANGE - GIVEN */
        final TypeProduit parent = new TypeProduit("vêtement");

        final SousTypeProduit enfant1 = new SousTypeProduit("vêtement homme", parent, null);
        final SousTypeProduit enfant2 = new SousTypeProduit("vêtement femme", parent, null);

        /* ACT - WHEN */
        final List<SousTypeProduitI> liste = Arrays.asList(enfant1, enfant2);
        parent.setSousTypeProduits(liste);

        /* ASSERT - THEN : liste parent par identité (==). */
        final List<SousTypeProduitI> snapshot = (List<SousTypeProduitI>) parent.getSousTypeProduits();

        boolean present1 = false;
        boolean present2 = false;

        for (final SousTypeProduitI stp : snapshot) {
            if (stp == enfant1) {
                present1 = true;
            }
            if (stp == enfant2) {
                present2 = true;
            }
        }

        assertTrue(present1,
                "enfant1 doit être présent dans parent.getSousTypeProduits() par identité (==) : ");
        assertTrue(present2,
                "enfant2 doit être présent dans parent.getSousTypeProduits() par identité (==) : ");

        /* ASSERT - THEN : chaque enfant pointe vers le parent. */
        assertTrue(enfant1.getTypeProduit() == parent,
                "enfant1.getTypeProduit() doit retourner le parent par identité (==) : ");
        assertTrue(enfant2.getTypeProduit() == parent,
                "enfant2.getTypeProduit() doit retourner le parent par identité (==) : ");

    } //___________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>
     * Teste la robustesse du re-parenting TypeProduit &lt;-&gt; SousTypeProduit en nominal.
     * </p>
     * <ul>
     * <li>Attache un enfant à parent1.</li>
     * <li>Re-attache le même enfant à parent2 via setSousTypeProduits(...).</li>
     * <li>Vérifie que l'enfant n'est plus présent dans parent1 (identité).</li>
     * <li>Vérifie que l'enfant est présent dans parent2 (identité) et pointe vers parent2.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings({ "unchecked", UNUSED })
    @DisplayName("testIntegrationReParentingNominal() : vérifie re-parenting en nominal")
    @Tag(RELATIONS)
    @Test
    public final void testIntegrationReParentingNominal() {

        /* ARRANGE - GIVEN */
        final TypeProduit parent1 = new TypeProduit("vêtement");
        final TypeProduit parent2 = new TypeProduit("outillage");

        final SousTypeProduit enfant = new SousTypeProduit("enfant", parent1, null);

        /* ACT - WHEN : rattachement initial. */
        parent1.setSousTypeProduits(Arrays.asList(enfant));

        /* ACT - WHEN : re-parenting vers parent2. */
        parent2.setSousTypeProduits(Arrays.asList(enfant));

        /* ASSERT - THEN : l'enfant pointe vers parent2. */
        assertTrue(enfant.getTypeProduit() == parent2,
                "Après re-parenting, enfant.getTypeProduit() doit pointer vers parent2 (==) : ");

        /* ASSERT - THEN : absent de parent1 par identité. */
        final List<SousTypeProduitI> snap1 = (List<SousTypeProduitI>) parent1.getSousTypeProduits();

        boolean presentDansParent1 = false;

        for (final SousTypeProduitI stp : snap1) {
            if (stp == enfant) {
                presentDansParent1 = true;
            }
        }

        assertFalse(presentDansParent1,
                "Après re-parenting, l'enfant ne doit plus être présent dans parent1 (==) : ");

        /* ASSERT - THEN : présent dans parent2 par identité. */
        final List<SousTypeProduitI> snap2 = (List<SousTypeProduitI>) parent2.getSousTypeProduits();

        boolean presentDansParent2 = false;

        for (final SousTypeProduitI stp : snap2) {
            if (stp == enfant) {
                presentDansParent2 = true;
            }
        }

        assertTrue(presentDansParent2,
                "Après re-parenting, l'enfant doit être présent dans parent2 (==) : ");

    } //___________________________________________________________________

    

    /**
     * <div>
     * <p>
     * Teste la robustesse thread-safe de setSousTypeProduits(...) en cas d'appels concurrents.
     * </p>
     * <ul>
     * <li>Détecte tout risque de blocage via un timeout.</li>
     * <li>Vérifie qu'aucune tâche n'est annulée et qu'aucune tâche ne lève d'exception.</li>
     * <li>Vérifie les invariants finaux parent/enfant par identité (==).</li>
     * </ul>
     * </div>
     * @throws Exception si une erreur survient pendant le test.
     */
    @SuppressWarnings({ RESOURCE, UNUSED })
    @DisplayName("testSetSousTypeProduitsThreadSafe() : vérifie l'absence de deadlock et les invariants en concurrence")
    @Tag(THREAD_SAFETY)
    @Test
    public final void testSetSousTypeProduitsThreadSafe() throws Exception {

        /* ARRANGE - GIVEN */
        final TypeProduit parent1 = new TypeProduit("vêtement");
        final TypeProduit parent2 = new TypeProduit("outillage");

        final SousTypeProduit enfant1 = new SousTypeProduit("vêtement homme", parent1, null);
        final SousTypeProduit enfant2 = new SousTypeProduit("outil électrique", parent2, null);

        final ExecutorService executor = Executors.newFixedThreadPool(2);

        final Callable<Void> task1 = () -> {
            final List<SousTypeProduitI> liste1 = Arrays.asList(enfant1, enfant2);
            parent1.setSousTypeProduits(liste1);
            return null;
        };

        final Callable<Void> task2 = () -> {
            final List<SousTypeProduitI> liste2 = Arrays.asList(enfant2, enfant1);
            parent2.setSousTypeProduits(liste2);
            return null;
        };

        /* ACT - WHEN */
        final List<Callable<Void>> tasks = Arrays.asList(task1, task2);

        final List<java.util.concurrent.Future<Void>> futures =
                executor.invokeAll(tasks, 5, TimeUnit.SECONDS);

        executor.shutdown();

        /* ASSERT - THEN : aucune tâche annulée et aucune exception. */
        for (final java.util.concurrent.Future<Void> future : futures) {

            assertFalse(future.isCancelled(),
                    "Une tâche setSousTypeProduits() a été annulée (timeout) : risque de blocage : ");

            future.get();

        }

        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS),
                "Le pool ne s'est pas terminé correctement : ");

        /* ASSERT - THEN : invariants finaux.
         * Chaque enfant doit pointer vers le parent qui le contient effectivement.
         * Un enfant ne doit pas appartenir simultanément aux deux parents.
         */
        final TypeProduitI parentEnfant1 = enfant1.getTypeProduit();
        final TypeProduitI parentEnfant2 = enfant2.getTypeProduit();

        assertTrue(parentEnfant1 == parent1 || parentEnfant1 == parent2 || parentEnfant1 == null,
                "enfant1 doit pointer vers parent1, parent2 ou null : ");
        assertTrue(parentEnfant2 == parent1 || parentEnfant2 == parent2 || parentEnfant2 == null,
                "enfant2 doit pointer vers parent1, parent2 ou null : ");

        final List<SousTypeProduitI> snapP1 = new ArrayList<>(parent1.getSousTypeProduits());
        final List<SousTypeProduitI> snapP2 = new ArrayList<>(parent2.getSousTypeProduits());

        boolean enfant1DansP1 = false;
        boolean enfant1DansP2 = false;
        boolean enfant2DansP1 = false;
        boolean enfant2DansP2 = false;

        for (final SousTypeProduitI stp : snapP1) {
            if (stp == enfant1) {
                enfant1DansP1 = true;
            }
            if (stp == enfant2) {
                enfant2DansP1 = true;
            }
        }

        for (final SousTypeProduitI stp : snapP2) {
            if (stp == enfant1) {
                enfant1DansP2 = true;
            }
            if (stp == enfant2) {
                enfant2DansP2 = true;
            }
        }

        assertFalse(enfant1DansP1 && enfant1DansP2,
                "enfant1 ne doit pas être présent simultanément dans les deux parents (==) : ");
        assertFalse(enfant2DansP1 && enfant2DansP2,
                "enfant2 ne doit pas être présent simultanément dans les deux parents (==) : ");

        if (enfant1DansP1) {
            assertTrue(enfant1.getTypeProduit() == parent1,
                    "Si enfant1 est dans parent1, enfant1.getTypeProduit() doit être parent1 (==) : ");
        }
        if (enfant1DansP2) {
            assertTrue(enfant1.getTypeProduit() == parent2,
                    "Si enfant1 est dans parent2, enfant1.getTypeProduit() doit être parent2 (==) : ");
        }
        if (enfant2DansP1) {
            assertTrue(enfant2.getTypeProduit() == parent1,
                    "Si enfant2 est dans parent1, enfant2.getTypeProduit() doit être parent1 (==) : ");
        }
        if (enfant2DansP2) {
            assertTrue(enfant2.getTypeProduit() == parent2,
                    "Si enfant2 est dans parent2, enfant2.getTypeProduit() doit être parent2 (==) : ");
        }

    } //___________________________________________________________________

    
    
}
