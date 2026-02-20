package levy.daniel.application.model.metier.produittype;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">
 * Test JUnit de {@link CloneContext}.
 * </p>
 *
 * <p>
 * Vérifie :
 * </p>
 * <ul>
 * <li>get(null) retourne null</li>
 * <li>put puis get : retourne la même instance</li>
 * <li>deux clés différentes peuvent pointer vers deux clones différents</li>
 * <li>isolation : un autre contexte ne "voit" pas le cache</li>
 * <li>contains(key) retourne true après un put(key, clone)</li>
 * <li>size() retourne le nombre d'objets clonés</li>
 * <li>clear() vide le cache</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.1
 * @since 18 janvier 2026
 */
public class CloneContextTest {

    // ************************ATTRIBUTS************************************/

    /** Tag JUnit : "CloneContext". */
    public static final String TAG_CLONE_CONTEXT = "CloneContext";

    /** Tag JUnit : "CloneContext-Beton". */
    public static final String TAG_CLONE_CONTEXT_BETON 
    	= "CloneContext-Beton";

    /** Message AssertJ : "Le contexte ne doit pas être null." */
    public static final String MSG_CONTEXT_NOT_NULL 
    	= "Le contexte ne doit pas être null.";

    /** Message AssertJ : "get(null) doit retourner null." */
    public static final String MSG_GET_NULL 
    	= "get(null) doit retourner null.";

    /** Message AssertJ : "Le clone doit être récupéré via get(key)." */
    public static final String MSG_GET_RETOURNE_CLONE 
    	= "Le clone doit être récupéré via get(key).";

    /** Message AssertJ : 
     * "Deux clés distinctes doivent retrouver leurs clones respectifs." */
    public static final String MSG_DEUX_CLES 
    	= "Deux clés distinctes doivent retrouver leurs clones respectifs.";

    /** Message AssertJ : 
     * "Les contextes doivent être isolés." */
    public static final String MSG_ISOLATION 
    	= "Les contextes doivent être isolés.";

    /** Message AssertJ : 
     * "contains(key) doit retourner true après un put(key, clone)." */
    public static final String MSG_CONTAINS_TRUE 
    	= "contains(key) doit retourner true après un put(key, clone).";

    /** Message AssertJ : 
     * "size() doit retourner le nombre d'objets clonés." */
    public static final String MSG_SIZE_CORRECT 
    	= "size() doit retourner le nombre d'objets clonés.";

    /** Message AssertJ : 
     * "clear() doit vider le cache." */
    public static final String MSG_CLEAR_EFFECTIF 
    	= "clear() doit vider le cache.";

    // *************************METHODES************************************/

    /**
     * <div>
     * <p>
     * CONSTRUCTEUR D'ARITE NULLE.
     * </p>
     * </div>
     */
    public CloneContextTest() {
        super();
    } // Fin du CONSTRUCTEUR D'ARITE NULLE.________________________________

    
    /* ============================== TESTS ============================== */

    /**
     * <div>
     * <p>
     * Vérifie que le constructeur crée un objet non null.
     * </p>
     * </div>
     */
    @Tag(TAG_CLONE_CONTEXT)
    @DisplayName("Création : new CloneContext() != null")
    @Test
    public void testCreationCloneContext() {
        final CloneContext context = new CloneContext();
        assertThat(context).as(MSG_CONTEXT_NOT_NULL).isNotNull();
    }

    
    
    /**
     * <div>
     * <p>
     * Vérifie que get(null) retourne null.
     * </p>
     * </div>
     */
    @Tag(TAG_CLONE_CONTEXT)
    @DisplayName("Tolérance : get(null) -> null")
    @Test
    public void testGetNull() {
        final CloneContext context = new CloneContext();
        final Object retrieved = context.get(null);
        assertThat(retrieved).as(MSG_GET_NULL).isNull();
    }

    
    
    /**
     * <div>
     * <p>
     * Vérifie put puis get : on récupère la même instance de clone.
     * </p>
     * </div>
     */
    @Tag(TAG_CLONE_CONTEXT_BETON)
    @DisplayName("put puis get : retourne la même instance")
    @Test
    public void testPutThenGetReturnsSameInstance() {
        final CloneContext context = new CloneContext();
        final Object key = new Object();
        final Object clone = new Object();

        context.put(key, clone);
        final Object retrieved = context.get(key);

        assertThat(retrieved).as(MSG_GET_RETOURNE_CLONE).isSameAs(clone);
    }

    
    
    /**
     * <div>
     * <p>
     * Vérifie que deux clés distinctes pointent vers leurs clones respectifs.
     * </p>
     * </div>
     */
    @Tag(TAG_CLONE_CONTEXT_BETON)
    @DisplayName("Deux clés distinctes -> deux clones distincts")
    @Test
    public void testTwoKeysTwoClones() {
        final CloneContext context = new CloneContext();
        final Object key1 = new Object();
        final Object key2 = new Object();
        final Object clone1 = new Object();
        final Object clone2 = new Object();

        context.put(key1, clone1);
        context.put(key2, clone2);

        final Object retrieved1 = context.get(key1);
        final Object retrieved2 = context.get(key2);

        assertThat(retrieved1).as(MSG_DEUX_CLES).isSameAs(clone1);
        assertThat(retrieved2).as(MSG_DEUX_CLES).isSameAs(clone2);
    }

    
    
    /**
     * <div>
     * <p>
     * Vérifie l'isolation : deux contextes ne partagent pas le cache.
     * </p>
     * </div>
     */
    @Tag(TAG_CLONE_CONTEXT_BETON)
    @DisplayName("Isolation : deux contextes ne partagent pas le cache")
    @Test
    public void testIsolationBetweenContexts() {
        final CloneContext context1 = new CloneContext();
        final CloneContext context2 = new CloneContext();
        final Object key = new Object();
        final Object clone = new Object();

        context1.put(key, clone);
        final Object retrievedFromContext2 = context2.get(key);

        assertThat(retrievedFromContext2).as(MSG_ISOLATION).isNull();
    }

    
    
    /**
     * <div>
     * <p>
     * Vérifie que contains(key) retourne true après un put(key, clone).
     * </p>
     * </div>
     */
    @Tag(TAG_CLONE_CONTEXT_BETON)
    @DisplayName("contains(key) retourne true après un put(key, clone)")
    @Test
    public void testContainsReturnsTrueAfterPut() {
        final CloneContext context = new CloneContext();
        final Object key = new Object();
        final Object clone = new Object();

        context.put(key, clone);
        final boolean containsKey = context.contains(key);

        assertThat(containsKey).as(MSG_CONTAINS_TRUE).isTrue();
    }

    
    
    /**
     * <div>
     * <p>
     * Vérifie que size() retourne le nombre d'objets clonés.
     * </p>
     * </div>
     */
    @Tag(TAG_CLONE_CONTEXT_BETON)
    @DisplayName("size() retourne le nombre d'objets clonés")
    @Test
    public void testSizeReturnsNumberOfClonedObjects() {
        final CloneContext context = new CloneContext();
        final Object key1 = new Object();
        final Object key2 = new Object();
        final Object clone1 = new Object();
        final Object clone2 = new Object();

        context.put(key1, clone1);
        context.put(key2, clone2);

        final int size = context.size();
        assertThat(size).as(MSG_SIZE_CORRECT).isEqualTo(2);
    }

    
    
    /**
     * <div>
     * <p>
     * Vérifie que clear() vide le cache.
     * </p>
     * </div>
     */
    @Tag(TAG_CLONE_CONTEXT_BETON)
    @DisplayName("clear() vide le cache")
    @Test
    public void testClearEmptiesCache() {
        final CloneContext context = new CloneContext();
        final Object key = new Object();
        final Object clone = new Object();

        context.put(key, clone);
        context.clear();

        final int sizeAfterClear = context.size();
        final boolean containsKeyAfterClear = context.contains(key);

        assertThat(sizeAfterClear).as(MSG_CLEAR_EFFECTIF).isZero();
        assertThat(containsKeyAfterClear).as(MSG_CLEAR_EFFECTIF).isFalse();
    }

    
    
    /**
     * <div>
     * <p>
     * Vérifie que contains(null) retourne false.
     * </p>
     * </div>
     */
    @Tag(TAG_CLONE_CONTEXT)
    @DisplayName("Tolérance : contains(null) -> false")
    @Test
    public void testContainsNullReturnsFalse() {
        final CloneContext context = new CloneContext();
        final boolean containsNull = context.contains(null);
        assertThat(containsNull).as("contains(null) doit retourner false.").isFalse();
    }
    
    
    
    /**
     *  .
     *
     * @throws Exception
     */
    @SuppressWarnings("resource")
	@Tag(TAG_CLONE_CONTEXT_BETON)
    @DisplayName("computeIfAbsent : unicité inter-threads (une seule computation)")
    @Test
    public void testComputeIfAbsentInterThreadsUnicite() throws Exception {

        final CloneContext context = new CloneContext();
        final Object key = new Object();

        final java.util.concurrent.atomic.AtomicInteger counter
                = new java.util.concurrent.atomic.AtomicInteger(0);

        final java.util.concurrent.Callable<Object> task
                = new java.util.concurrent.Callable<Object>() {

                    @Override
                    public Object call() {

                        return context.computeIfAbsent(
                                key,
                                new CloneContext.CloneComputation<Object>() {

                                    @Override
                                    public Object compute() {

                                        counter.incrementAndGet();

                                        final Object clone = new Object();
                                        context.put(key, clone);

                                        return clone;
                                    }
                                });
                    }
                };

        final int nbThreads = 16;

        final java.util.concurrent.ExecutorService executor
                = java.util.concurrent.Executors.newFixedThreadPool(nbThreads);

        try {

            final java.util.List<java.util.concurrent.Callable<Object>> tasks
                    = new java.util.ArrayList<>();

            for (int i = 0; i < nbThreads; i++) {
                tasks.add(task);
            }

            final java.util.List<java.util.concurrent.Future<Object>> futures
                    = executor.invokeAll(tasks);

            Object first = null;

            for (final java.util.concurrent.Future<Object> f : futures) {

                final Object r = f.get();

                if (first == null) {
                    first = r;
                }

                assertThat(r)
                        .as("Tous les threads doivent récupérer la même instance de clone.")
                        .isSameAs(first);
            }

            assertThat(counter.get())
                    .as("La computation doit être exécutée une seule fois.")
                    .isEqualTo(1);

        } finally {
            executor.shutdownNow();
        }
    }

}
