/* ********************************************************************* */
/* **************** TEST INTEGRATION TYPE / SOUS-TYPE ****************** */
/* ********************************************************************* */
package levy.daniel.application.model.metier.produittype;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
     * <p style="font-weight:bold;">
     * Teste la robustesse thread-safe de setSousTypeProduits(...)
     * en cas d'appels concurrents.</p>
     * <ul>
     * <li>détecte tout risque de blocage (deadlock).</li>
     * <li>vérifie que toutes les tâches se terminent.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings("resource")
	@DisplayName("testSetSousTypeProduitsThreadSafe() : vérifie l'absence de deadlock en concurrence")
    @Test
    public void testSetSousTypeProduitsThreadSafe() throws Exception {

        // **********************************
        // ARRANGE
        // **********************************

        final TypeProduit parent1 = new TypeProduit("vêtement");
        final TypeProduit parent2 = new TypeProduit("outillage");

        final SousTypeProduit enfant1 
            = new SousTypeProduit("vêtement homme", parent1, null);

        final SousTypeProduit enfant2 
            = new SousTypeProduit("outil électrique", parent2, null);

        final ExecutorService executor = Executors.newFixedThreadPool(2);

        final Callable<Void> task1 = () -> {
            final List<SousTypeProduitI> liste1 
                = Arrays.asList(enfant1, enfant2);
            parent1.setSousTypeProduits(liste1);
            return null;
        };

        final Callable<Void> task2 = () -> {
            final List<SousTypeProduitI> liste2 
                = Arrays.asList(enfant2, enfant1);
            parent2.setSousTypeProduits(liste2);
            return null;
        };

        // **********************************
        // ACT
        // **********************************

        final var futures 
            = executor.invokeAll(Arrays.asList(task1, task2), 
                                  5, TimeUnit.SECONDS);

        executor.shutdown();

        // **********************************
        // ASSERT
        // **********************************

        boolean cancelled = false;

        for (final var future : futures) {
            if (future.isCancelled()) {
                cancelled = true;
                break;
            }
        }

        assertFalse(
                cancelled,
                "Une tâche setSousTypeProduits() a été annulée (timeout) : risque de blocage."
        );

        assertTrue(
                executor.awaitTermination(5, TimeUnit.SECONDS),
                "Le pool ne s'est pas terminé correctement."
        );

    } //___________________________________________________________________

}
