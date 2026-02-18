package levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA; // NOPMD by danyl on 17/02/2026 12:15

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import levy.daniel.application.model.metier.produittype.SousTypeProduitI;


/**
 *
 *
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 17 février 2026
 */
public class TypeProduitJPARattachementDetachementTest {

	// ************************ATTRIBUTS************************************/

	/**
	 * Boolean qui commande l'affichage pour tous les tests.<br/>
	 */
	public static final Boolean AFFICHAGE_GENERAL = true;

	/**
	 * "vêtement"
	 */
	public static final String VETEMENT = "vêtement";

	/**
	 * "vêtement pour homme"
	 */
	public static final String VETEMENT_POUR_HOMME = "vêtement pour homme";

	/**
	 * "JPATest-Rattachement"
	 */
	public static final String JPA_TEST_RATTACHEMENT = "JPATest-Rattachement";

	/**
	 * "JPATest-Detachement"
	 */
	public static final String JPA_TEST_DETACHEMENT = "JPATest-Detachement";

	/**
	 * "JPATest-BlankNull"
	 */
	public static final String JPA_TEST_BLANK_NULL = "JPATest-BlankNull";

	// *************************METHODES************************************/


	/**
	* <div>
	* <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	* </div>
	*/
	public TypeProduitJPARattachementDetachementTest() {
		super();
	} // Fin du CONSTRUCTEUR D'ARITE NULLE.________________________________



	/**
	 * <div>
	 * <p>s'assure que le rattachement est correct : </p>
	 * <ul>
	 * <li>rattachement via setter canonique (stp.setTypeProduit(tp)).</li>
	 * <li>bidirectionnalité : tp contient stp et stp.typeProduit == tp.</li>
	 * <li>pas de doublon si on rattache plusieurs fois (idempotence).</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings("unused")
	@Tag(JPA_TEST_RATTACHEMENT)
	@DisplayName("Rattachement JPA : setter canonique -> bidirectionnalité, pas de doublon, idempotence")
	@Test
	public final void testRattachementSetterCanoniqueSansDoublon() {

		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************

		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE TypeProduitJPARattachementDetachementTest - méthode testRattachementSetterCanoniqueSansDoublon() ********** ");
			System.out.println("CE TEST VERIFIE LE RATTACHEMENT VIA SETTER CANONIQUE : BIDIRECTIONNALITE, PAS DE DOUBLON, IDEMPOTENCE.");
			System.out.println();
		}

		//**** ARRANGE - GIVEN
		final TypeProduitJPA tp = new TypeProduitJPA(VETEMENT);
		tp.setIdTypeProduit(1L);

		final SousTypeProduitJPA stp = new SousTypeProduitJPA(VETEMENT_POUR_HOMME);
		stp.setIdSousTypeProduit(10L);

		//**** ACT - WHEN
		/* rattachement via setter canonique. */
		stp.setTypeProduit(tp);

		/* rattachement à nouveau : doit être idempotent (pas de doublon). */
		stp.setTypeProduit(tp);

		//**** ASSERT - THEN
		assertSame(tp, stp.getTypeProduit(), "Le STP doit référencer le parent TypeProduit (même instance).");

		final List<? extends SousTypeProduitI> stps = tp.getSousTypeProduits();
		assertTrue(stps != null, "La liste sousTypeProduits ne doit pas être null.");
		assertEquals(1, stps.size(), "La liste sousTypeProduits doit contenir un seul élément (pas de doublon).");

		final SousTypeProduitI stpDansListe = stps.get(0);
		assertSame(stp, stpDansListe, "Le STP présent dans le parent doit être la même instance.");

	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>s'assure que le détachement est correct : </p>
	 * <ul>
	 * <li>idempotence : detacher 2 fois ne change rien et ne plante pas.</li>
	 * <li>suppression côté parent : tp ne contient plus stp.</li>
	 * <li>null côté enfant : stp.typeProduit == null.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings("unused")
	@Tag(JPA_TEST_DETACHEMENT)
	@DisplayName("Détachement JPA : idempotence, suppression côté parent, null côté enfant")
	@Test
	public final void testDetachementIdempotentParentEtEnfant() {

		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************

		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE TypeProduitJPARattachementDetachementTest - méthode testDetachementIdempotentParentEtEnfant() ********** ");
			System.out.println("CE TEST VERIFIE LE DETACHEMENT : IDEMPOTENCE, SUPPRESSION COTE PARENT, NULL COTE ENFANT.");
			System.out.println();
		}

		//**** ARRANGE - GIVEN
		final TypeProduitJPA tp = new TypeProduitJPA(VETEMENT);
		tp.setIdTypeProduit(1L);

		final SousTypeProduitJPA stp = new SousTypeProduitJPA(VETEMENT_POUR_HOMME);
		stp.setIdSousTypeProduit(10L);

		/* rattachement préalable. */
		stp.setTypeProduit(tp);

		assertSame(tp, stp.getTypeProduit(), "Précondition : STP doit être rattaché au parent.");
		assertTrue(tp.getSousTypeProduits().contains(stp), "Précondition : parent doit contenir le STP.");

		//**** ACT - WHEN
		/* détachement via parent : doit utiliser le setter canonique intelligent côté enfant. */
		tp.detacherEnfantSTP(stp);

		/* détachement à nouveau : idempotence. */
		tp.detacherEnfantSTP(stp);

		//**** ASSERT - THEN
		assertNull(stp.getTypeProduit(), "Après détachement, l'enfant doit avoir typeProduit == null.");
		assertFalse(tp.getSousTypeProduits().contains(stp), "Après détachement, le parent ne doit plus contenir l'enfant.");

	} //___________________________________________________________________



	/**
	 * <div>
	 * <p>s'assure que les cas blank/null retournent sans effet : </p>
	 * <ul>
	 * <li>rattacherEnfantSTP(null) : sans effet.</li>
	 * <li>detacherEnfantSTP(null) : sans effet.</li>
	 * <li>rattacherEnfantSTP(blank) : sans effet.</li>
	 * <li>detacherEnfantSTP(blank) : sans effet.</li>
	 * </ul>
	 * </div>
	 */
	@SuppressWarnings("unused")
	@Tag(JPA_TEST_BLANK_NULL)
	@DisplayName("Blank/Null : rattachement/détachement doivent retourner sans effet")
	@Test
	public final void testBlankNullRetourSansEffet() {

		// **********************************
		// AFFICHAGE DANS LE TEST ou NON
		final boolean affichage = false;
		// **********************************

		/* AFFICHAGE A LA CONSOLE. */
		if (AFFICHAGE_GENERAL && affichage) {
			System.out.println();
			System.out.println("********** CLASSE TypeProduitJPARattachementDetachementTest - méthode testBlankNullRetourSansEffet() ********** ");
			System.out.println("CE TEST VERIFIE LES CAS BLANK/NULL : RETOUR SANS EFFET.");
			System.out.println();
		}

		//**** ARRANGE - GIVEN
		final TypeProduitJPA tp = new TypeProduitJPA(VETEMENT);
		tp.setIdTypeProduit(1L);

		final SousTypeProduitJPA stpValide = new SousTypeProduitJPA(VETEMENT_POUR_HOMME);
		stpValide.setIdSousTypeProduit(10L);

		final SousTypeProduitJPA stpBlank = new SousTypeProduitJPA("   ");
		stpBlank.setIdSousTypeProduit(11L);

		/* rattachement préalable d'un enfant valide pour vérifier "sans effet" sur la liste. */
		tp.rattacherEnfantSTP(stpValide);
		assertEquals(1, tp.getSousTypeProduits().size(), "Précondition : la liste doit contenir 1 élément.");

		//**** ACT - WHEN
		tp.rattacherEnfantSTP(null);
		tp.detacherEnfantSTP(null);

		tp.rattacherEnfantSTP(stpBlank);
		tp.detacherEnfantSTP(stpBlank);

		//**** ASSERT - THEN
		assertEquals(1, tp.getSousTypeProduits().size(), "Les opérations blank/null ne doivent pas modifier la liste du parent.");
		assertTrue(tp.getSousTypeProduits().contains(stpValide), "L'enfant valide déjà rattaché doit rester présent.");
		assertSame(tp, stpValide.getTypeProduit(), "L'enfant valide doit rester rattaché au parent.");

	} //___________________________________________________________________

} // Fin de CLASSE TypeProduitJPARattachementDetachementTest.______________
