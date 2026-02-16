package levy.daniel.application.model.metier.produittype;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE MetierGlobalConformiteTest.java :
 * </p>
 *
 * <p>
 * Tests d'invariants globaux de la couche métier (TypeProduit / SousTypeProduit / Produit)
 * + tests de clonage profond + tests anti-deadlock.
 * </p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 16 février 2026
 */
public class MetierGlobalConformiteTest {

    // ************************ATTRIBUTS************************************/

    /**
     * Tag JUnit : "metier-global".
     */
    public static final String TAG_METIER_GLOBAL = "metier-global";

    /**
     * Tag JUnit : "thread-safety".
     */
    public static final String TAG_THREAD_SAFETY = "thread-safety";

    /**
     * "Le clone ne doit pas être la même instance que l'original."
     */
    public static final String CLONE_PAS_MEME_INSTANCE
        = "Le clone ne doit pas être la même instance que l'original.";

    // *************************METHODES************************************/

    /**
     * <div>
     * <p>
     * CONSTRUCTEUR D'ARITE NULLE.
     * </p>
     * </div>
     */
    public MetierGlobalConformiteTest() {
        super();
    } // Fin du CONSTRUCTEUR D'ARITE NULLE.________________________________


    /**
     * <div>
     * <p>
     * Test d'invariant métier global : cohérence des liens bidirectionnels.
     * </p>
     * <ul>
     * <li>TypeProduit contient ses SousTypeProduit après rattachement.</li>
     * <li>SousTypeProduit contient ses Produit après rattachement.</li>
     * <li>Un changement de parent met à jour les deux côtés.</li>
     * <li>Un détachement enlève le lien des deux côtés.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_METIER_GLOBAL)
    @DisplayName("Invariant global : cohérence TypeProduit<->SousTypeProduit<->Produit")
    @Test
    public final void testInvariantMetierGlobalCoherenceLiens() {

        /* ARRANGE - GIVEN */
        final TypeProduit typeProduit = new TypeProduit(1L, "Vêtement", null);

        final SousTypeProduit sousTypeProduitHomme
            = new SousTypeProduit(10L, "Vêtement pour homme", typeProduit, null);

        final SousTypeProduit sousTypeProduitFemme
            = new SousTypeProduit(20L, "Vêtement pour femme", typeProduit, null);

        final Produit produit
            = new Produit(100L, "Chemise", null);

        /* ACT - WHEN : rattachement Produit -> STP homme via API métier (setSousTypeProduit) */
        produit.setSousTypeProduit(sousTypeProduitHomme);

        /* ASSERT - THEN : pas de contains(sousTypeProduitX) sur List<? extends ...> (bypass du problème de capture) */
        final List<? extends SousTypeProduitI> sousTypesDuTypeProduit
            = new ArrayList<>(typeProduit.getSousTypeProduits());

        assertThat(sousTypesDuTypeProduit)
            .as("TypeProduit doit contenir sousTypeProduitHomme après rattachement.")
            .anyMatch(stp -> stp == sousTypeProduitHomme);

        assertThat(sousTypesDuTypeProduit)
            .as("TypeProduit doit contenir sousTypeProduitFemme (créé avec le même TypeProduit).")
            .anyMatch(stp -> stp == sousTypeProduitFemme);

        assertThat(sousTypeProduitHomme.getProduits())
            .as("sousTypeProduitHomme doit contenir le produit après setSousTypeProduit(...).")
            .anyMatch(p -> p == produit);

        assertThat(produit.getSousTypeProduit())
            .as("Le parent du produit doit être sousTypeProduitHomme après rattachement.")
            .isSameAs(sousTypeProduitHomme);

        assertThat(produit.getTypeProduit())
            .as("Le type du produit doit être le TypeProduit via SousTypeProduit.")
            .isSameAs(typeProduit);

        /* ACT - WHEN : bascule du parent vers STP femme */
        produit.setSousTypeProduit(sousTypeProduitFemme);

        /* ASSERT - THEN : cohérence bidirectionnelle après bascule */
        assertThat(sousTypeProduitFemme.getProduits())
            .as("sousTypeProduitFemme doit contenir le produit après bascule.")
            .anyMatch(p -> p == produit);

        assertThat(sousTypeProduitHomme.getProduits())
            .as("sousTypeProduitHomme ne doit plus contenir le produit après bascule.")
            .noneMatch(p -> p == produit);

        assertThat(produit.getSousTypeProduit())
            .as("Le parent du produit doit être sousTypeProduitFemme après bascule.")
            .isSameAs(sousTypeProduitFemme);

        /* ACT - WHEN : détachement (parent null) */
        produit.setSousTypeProduit(null);

        /* ASSERT - THEN : cohérence bidirectionnelle après détachement */
        assertThat(produit.getSousTypeProduit())
            .as("Après détachement, le parent du produit doit être null.")
            .isNull();

        assertThat(sousTypeProduitFemme.getProduits())
            .as("Après détachement, sousTypeProduitFemme ne doit plus contenir le produit.")
            .noneMatch(p -> p == produit);

        assertThat(sousTypeProduitHomme.getProduits())
            .as("Après détachement, sousTypeProduitHomme ne doit pas contenir le produit.")
            .noneMatch(p -> p == produit);

    } //___________________________________________________________________


    /**
     * <div>
     * <p>
     * Test de clone profond complet : Produit.deepClone(CloneContext).
     * </p>
     * <ul>
     * <li>Le clone n'est pas la même instance que l'original.</li>
     * <li>Le clone est égal métier (equals) à l'original.</li>
     * <li>Le sousTypeProduit est cloné profondément (instance différente).</li>
     * <li>La modification du clone n'impacte pas l'original.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_METIER_GLOBAL)
    @DisplayName("Clone profond complet : Produit.deepClone(CloneContext) (indépendance + cohérence)")
    @Test
    public final void testCloneProfondCompletProduit() {

        /* ARRANGE - GIVEN */
        final TypeProduit typeProduit = new TypeProduit(1L, "Anatomie", null);
        final SousTypeProduit sousTypeProduit = new SousTypeProduit(10L, "Anatomie de la main", typeProduit, null);
        final Produit original = new Produit(100L, "Anatomie arthroscopique de la main", sousTypeProduit);

        /* ACT - WHEN */
        final CloneContext ctx = new CloneContext();
        final Produit clone = (Produit) original.deepClone(ctx);

        /* ASSERT - THEN */
        assertThat(clone)
            .as(CLONE_PAS_MEME_INSTANCE)
            .isNotSameAs(original);

        assertThat(clone)
            .as("Le clone doit être equals() à l'original.")
            .isEqualTo(original);

        assertThat(clone.getIdProduit())
            .as("L'ID doit être identique.")
            .isEqualTo(original.getIdProduit());

        assertThat(clone.getProduit())
            .as("Le libellé produit doit être identique.")
            .isEqualTo(original.getProduit());

        assertThat(clone.getSousTypeProduit())
            .as("Le sousTypeProduit du clone ne doit pas être null.")
            .isNotNull();

        assertThat(clone.getSousTypeProduit())
            .as("Le sousTypeProduit du clone doit être égal métier à celui de l'original.")
            .isEqualTo(original.getSousTypeProduit());

        assertThat(clone.getSousTypeProduit())
            .as("Le sousTypeProduit doit être cloné profondément (instance différente).")
            .isNotSameAs(original.getSousTypeProduit());

        /* ACT - WHEN : modifications sur le clone */
        clone.setProduit("Produit modifié");
        ((SousTypeProduit) clone.getSousTypeProduit()).setSousTypeProduit("Sous-type modifié");

        /* ASSERT - THEN : l'original ne doit pas changer */
        assertThat(original.getProduit())
            .as("La modification du produit du clone ne doit pas impacter l'original.")
            .isNotEqualTo(clone.getProduit());

        assertThat(original.getSousTypeProduit().getSousTypeProduit())
            .as("La modification du sousTypeProduit du clone ne doit pas impacter l'original.")
            .isNotEqualTo(clone.getSousTypeProduit().getSousTypeProduit());

    } //___________________________________________________________________


    /**
     * <div>
     * <p>
     * Vérification finale anti-deadlock : opérations concurrentes sur liens
     * SousTypeProduit &lt;-&gt; Produit.
     * </p>
     * <ul>
     * <li>Exécute en concurrence setSousTypeProduit(...), ajouterSTPauProduit(...), retirerSTPauProduit(...).</li>
     * <li>Détecte un blocage par timeout via invokeAll(..., timeout) et Future.isCancelled().</li>
     * <li>Vérifie une cohérence minimale finale.</li>
     * </ul>
     * </div>
     *
     * @throws InterruptedException si le thread courant est interrompu.
     * @throws ExecutionException si une tâche lève une exception.
     */
    @SuppressWarnings("resource")
	@Tag(TAG_THREAD_SAFETY)
    @DisplayName("Anti-deadlock : concurrence STP<->Produit (timeout + cohérence finale)")
    @Test
    public final void testAntiDeadlockSousTypeProduitProduit()
            throws InterruptedException, ExecutionException {

        /* ARRANGE - GIVEN */
        final TypeProduit typeProduit = new TypeProduit(1L, "Type", null);

        final SousTypeProduit stp1 = new SousTypeProduit(1L, "STP1", typeProduit, null);
        final SousTypeProduit stp2 = new SousTypeProduit(2L, "STP2", typeProduit, null);

        final Produit produit = new Produit(1L, "P", null);

        final ExecutorService executor = Executors.newFixedThreadPool(12);
        final List<Callable<Boolean>> tasks = new ArrayList<>();

        for (int i = 0; i < 300; i++) {

            tasks.add(() -> {

                produit.setSousTypeProduit(stp1);

                return Boolean.TRUE;

            });

            tasks.add(() -> {

                produit.setSousTypeProduit(stp2);

                return Boolean.TRUE;

            });

            tasks.add(() -> {

                produit.setSousTypeProduit(null);

                return Boolean.TRUE;

            });

            tasks.add(() -> {

                stp1.ajouterSTPauProduit(produit);

                return Boolean.TRUE;

            });

            tasks.add(() -> {

                stp1.retirerSTPauProduit(produit);

                return Boolean.TRUE;

            });

            tasks.add(() -> {

                stp2.ajouterSTPauProduit(produit);

                return Boolean.TRUE;

            });

            tasks.add(() -> {

                stp2.retirerSTPauProduit(produit);

                return Boolean.TRUE;

            });

        }

        /* ACT - WHEN */
        final List<Future<Boolean>> futures
            = executor.invokeAll(tasks, 5, TimeUnit.SECONDS);

        executor.shutdown();

        /* ASSERT - THEN : aucune tâche annulée (timeout) et aucune exception. */
        for (final Future<Boolean> future : futures) {

            assertThat(future.isCancelled())
                .as("Une tâche STP<->Produit ne doit pas être annulée (timeout).")
                .isFalse();

            assertThat(future.get())
                .as("Une tâche STP<->Produit doit se terminer normalement.")
                .isTrue();

        }

        /* ASSERT - THEN : cohérence finale minimale (selon l'état final). */
        final SousTypeProduitI parentFinal = produit.getSousTypeProduit();

        if (parentFinal == null) {

            assertThat(stp1.getProduits())
                .as("Si le parent final est null, stp1 ne doit pas contenir le produit.")
                .noneMatch(p -> p == produit);

            assertThat(stp2.getProduits())
                .as("Si le parent final est null, stp2 ne doit pas contenir le produit.")
                .noneMatch(p -> p == produit);

        } else if (parentFinal == stp1) {

            assertThat(stp1.getProduits())
                .as("Si le parent final est stp1, il doit contenir le produit.")
                .anyMatch(p -> p == produit);

            assertThat(stp2.getProduits())
                .as("Si le parent final est stp1, stp2 ne doit pas contenir le produit.")
                .noneMatch(p -> p == produit);

        } else if (parentFinal == stp2) {

            assertThat(stp2.getProduits())
                .as("Si le parent final est stp2, il doit contenir le produit.")
                .anyMatch(p -> p == produit);

            assertThat(stp1.getProduits())
                .as("Si le parent final est stp2, stp1 ne doit pas contenir le produit.")
                .noneMatch(p -> p == produit);

        }

    } //___________________________________________________________________

}
