package levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA; // NOPMD by danyl on 05/02/2026 22:33

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import levy.daniel.application.model.metier.produittype.Produit;
import levy.daniel.application.model.metier.produittype.ProduitI;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.SousTypeProduitI;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.metier.produittype.TypeProduitI;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">CLASSE ConvertisseurMetierToJPATest.java :</p>
 *
 * <p>
 * Cette classe de test JUnit 5 vérifie le bon fonctionnement
 * du <span style="font-weight:bold;">ConvertisseurMetierToJPA</span>,
 * chargé de convertir des <span style="font-weight:bold;">objets métier</span>
 * en <span style="font-weight:bold;">Entity JPA</span>.
 * </p>
 *
 * <p>
 * Les tests couvrent :
 * <ul>
 * <li>La conversion des objets métier en Entity JPA.</li>
 * <li>La gestion des cas null, des collections vides, et des doublons.</li>
 * <li>La cohérence des graphes d'objets après conversion.</li>
 * <li>Les performances et la robustesse en cas d'accès concurrents.</li>
 * </ul>
 * </p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 30 décembre 2025
 */
public class ConvertisseurMetierToJPATest {

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

    /**
     * "- typeProduit du sousTypeProduit : %-13s "
     */
    public static final String FORMAT_TYPEPRODUIT_STP
        = "- typeProduit du sousTypeProduit : %-13s ";

    /**
     * "%-1s"
     */
    public static final String FORMAT_IDTP = "%-1s";

    /**
     * "typeProduit"
     */
    public static final String TYPEPRODUIT = "typeProduit";

    /**
     * "%-1s"
     */
    public static final String FORMAT_IDSTP = "%-1s";

    /**
     * "- [idSousTypeProduit : %-1s "
     */
    public static final String FORMAT_IDSOUSTYPEPRODUIT
        = "- [idSousTypeProduit : %-1s ";

    /**
     * "%-1s"
     */
    public static final String FORMAT_IDPRODUIT = "%-1s";

    /**
     * "typeProduitStringJPA doit valoir \"vêtement\" : "
     */
    public static final String TYPEPRODUIT_VETEMENT
        = "typeProduitString doit valoir \"vêtement\" : ";

    /**
     * "vêtement"
     */
    public static final String VETEMENT = "vêtement";

    /**
     * "chemise manches longues"
     */
    public static final String CHEMISES_MANCHES_LONGUES
        = "chemise manches longues";

    /**
     * "TypeProduit.sousTypeProduits"
     */
    public static final String TP_STP = "TypeProduit.sousTypeProduits";

    /**
     * "vêtement pour homme"
     */
    public static final String VETEMENT_POUR_HOMME = "vêtement pour homme";

    /**
     * "SousTypeProduit(produits)"
     */
    public static final String STP_PRODUIT = "SousTypeProduit(produits)";

    /**
     * "MetierToJPA-Beton"
     */
    public static final String METIERTOJPA_BETON = "MetierToJPA-Beton";

    /* ------------------------------------------------------------------ */

    /**
     * new TypeProduit(1L, "vêtement")
     */
    public TypeProduit typeProduitVetement;

    /**
     * new TypeProduit(2L, "pêche")
     */
    public TypeProduit typeProduitPeche;

    /**
     * new SousTypeProduit(1L, "vêtement pour homme", this.typeProduitVetement)
     */
    public SousTypeProduit sousTypeProduitVetementHomme;

    /**
     * new SousTypeProduit(2L, "vêtement pour femme", this.typeProduitVetement)
     */
    public SousTypeProduit sousTypeProduitVetementFemme;

    /**
     * new SousTypeProduit(3L, "vêtement pour enfant", this.typeProduitVetement)
     */
    public SousTypeProduit sousTypeProduitVetementEnfant;

    /**
     * new SousTypeProduit(4L, "canne", this.typeProduitPeche)
     */
    public SousTypeProduit sousTypeProduitPecheCanne;

    /**
     * new SousTypeProduit(5L, "cuiller", this.typeProduitPeche)
     */
    public SousTypeProduit sousTypeProduitPecheCuiller;

    /**
     * new Produit(1L, "chemise à manches longues pour homme", this.sousTypeProduitVetementHomme)
     */
    public Produit produitChemiseManchesLonguesPourHomme;

    /**
     * new Produit(2L, "chemise à manches courtes pour homme", this.sousTypeProduitVetementHomme)
     */
    public Produit produitChemiseManchesCourtesPourHomme;

    /**
     * new Produit(3L, "sweatshirt pour homme", this.sousTypeProduitVetementHomme)
     */
    public Produit produitSweatshirtPourHomme;

    /**
     * new Produit(4L, "teeshirt pour homme", this.sousTypeProduitVetementHomme)
     */
    public Produit produitTeeshirtPourHomme;

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
            .getLogger(ConvertisseurMetierToJPATest.class);

    // *************************METHODES************************************/

    /**
    * <div>
    * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
    * </div>
    */
    public ConvertisseurMetierToJPATest() {
        super();
    } // Fin du CONSTRUCTEUR D'ARITE NULLE.________________________________

    /**
     * <div>
     * <p>s'assure que lors d'une conversion : </p>
     * <ul>
     * <li>typeProduitMETIERToJPA(null) retourne null.</li>
     * <li>typeProduitMETIERToJPA(constructeur null) retourne null.</li>
     * <li>typeProduitMETIERToJPA(blank) retourne null.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testTypeProduitMETIERToJPANull() : vérifie le comportement de la méthode typeProduitMETIERToJPA(null)")
    @Tag("MetierToJPA")
    @Test
    public final void testTypeProduitMETIERToJPANull() {
        final boolean affichage = false;
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("********** CLASSE ConvertisseurMetierToJPATest - méthode testTypeProduitMETIERToJPANull() ********** ");
            System.out.println("CE TEST VERIFIE le fonctionnement de la méthode typeProduitMETIERToJPA(null).");
            System.out.println();
        }
        final TypeProduit typeProduitNull = null;
        final TypeProduit typeProduitConstructeurNull = null;
        final TypeProduit typeProduitBlank = new TypeProduit("  ");
        final TypeProduitJPA typeProduitNullJPA = ConvertisseurMetierToJPA.typeProduitMETIERToJPA(typeProduitNull);
        final TypeProduitJPA typeProduitConstructeurNullJPA = ConvertisseurMetierToJPA.typeProduitMETIERToJPA(typeProduitConstructeurNull);
        final TypeProduitJPA typeProduitBlankJPA = ConvertisseurMetierToJPA.typeProduitMETIERToJPA(typeProduitBlank);
        assertNull(typeProduitNullJPA, "la conversion de null doit donner null : ");
        assertNull(typeProduitConstructeurNullJPA, "la conversion de Constructeur null doit donner null : ");
        assertNull(typeProduitBlankJPA, "la conversion de Blank doit donner null : ");
    }

    /**
     * <div>
     * <p>s'assure que lors d'une conversion : </p>
     * <ul>
     * <p>NIVEAU TypeProduit : </p>
     * <li>idTypeProduit bien converti.</li>
     * <li>idTypeProduit = idTypeProduitMETIER.</li>
     * <li>TypeProduit bien converti.</li>
     * <li>typeProduit = typeProduitMETIER.</li>
     * <li>sousTypeProduits bien converti.</li>
     * <li>sousTypeProduits a la même taille que sousTypeProduitsMETIER.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testTypeProduitMETIERToJPA() : vérifie le comportement général de la méthode typeProduitMETIERToJPA(TypeProduit pTypeProduit)")
    @Tag("MetierToJPA")
    @Test
    public final void testTypeProduitMETIERToJPA() {
        final boolean affichage = false;
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("********** CLASSE ConvertisseurMetierToJPATest - méthode testTypeProduitMETIERToJPA() ********** ");
            System.out.println("CE TEST VERIFIE le fonctionnement de la méthode typeProduitMETIERToJPA(TypeProduit pTypeProduit).");
            System.out.println();
        }
        this.creerScenario();
        final Long idTypeProduit = this.typeProduitVetement.getIdTypeProduit();
        final String typeProduitString = this.typeProduitVetement.getTypeProduit();
        final List<? extends SousTypeProduitI> sousTypeProduits = this.typeProduitVetement.getSousTypeProduits();
        final SousTypeProduit sousTypeProduitHomme = (SousTypeProduit) sousTypeProduits.get(0);
        final Long idSoustypeProduitHomme = sousTypeProduitHomme.getIdSousTypeProduit();
        final TypeProduit typeProduitHomme = (TypeProduit) sousTypeProduitHomme.getTypeProduit();
        final String sousTypeProduitHommeString = sousTypeProduitHomme.getSousTypeProduit();
        final List<? extends ProduitI> listProduitsHomme = sousTypeProduitHomme.getProduits();
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println(this.afficherTypeProduitFormate(this.typeProduitVetement));
            System.out.println();
        }
        assertEquals(1L, idTypeProduit, "idTypeProduit doit valoir 1L : ");
        assertEquals(VETEMENT, typeProduitString, TYPEPRODUIT_VETEMENT);
        assertTrue(sousTypeProduits.size() == 3, "La liste sousTypeProduits doit contenir 3 éléments : ");
        assertEquals(1L, idSoustypeProduitHomme, "idSoustypeProduitHomme doit valoir 1L : ");
        assertEquals(typeProduitHomme, sousTypeProduitHomme.getTypeProduit(), "typeProduitHomme doit valoir \"vêtement\" : ");
        assertEquals(VETEMENT_POUR_HOMME, sousTypeProduitHommeString, "sousTypeProduitHommeString doit valoir \"vêtement pour homme\" : ");
        assertTrue(listProduitsHomme.size() == 4, "La liste produits (listProduitsHomme) doit contenir 4 éléments : ");
        final TypeProduitJPA typeProduitVetementJPA = ConvertisseurMetierToJPA.typeProduitMETIERToJPA(this.typeProduitVetement);
        final Long idTypeProduitJPA = typeProduitVetementJPA.getIdTypeProduit();
        final String typeProduitStringJPA = typeProduitVetementJPA.getTypeProduit();
        final List<SousTypeProduitJPA> sousTypeProduitsJPA = (List<SousTypeProduitJPA>) typeProduitVetementJPA.getSousTypeProduits();
        final SousTypeProduitJPA sousTypeProduitHommeJPA = sousTypeProduitsJPA.get(0);
        final Long idSoustypeProduitHommeJPA = sousTypeProduitHommeJPA.getIdSousTypeProduit();
        final TypeProduitJPA typeProduitHommeJPA = (TypeProduitJPA) sousTypeProduitHommeJPA.getTypeProduit();
        final String sousTypeProduitHommeStringJPA = sousTypeProduitHommeJPA.getSousTypeProduit();
        final List<ProduitJPA> listProduitsHommeJPA = (List<ProduitJPA>) sousTypeProduitHommeJPA.getProduits();
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("********** NIVEAU TYPEPRODUITJPA **************");
            System.out.println("**** typeProduitVetementJPA après CONVERSION *****");
            System.out.println(this.afficherTypeProduitFormate(typeProduitVetementJPA));
            System.out.println();
        }
        assertEquals(1L, idTypeProduitJPA, "idTypeProduitJPA doit valoir 1L : ");
        assertEquals(this.typeProduitVetement.getIdTypeProduit(), typeProduitVetementJPA.getIdTypeProduit(), "les ID de l'Objet métier et de l'Entity JPA doivent être les mêmes : ");
        assertEquals(VETEMENT, typeProduitStringJPA, "typeProduitStringJPA doit valoir \"vêtement\" : ");
        assertEquals(this.typeProduitVetement.getTypeProduit(), typeProduitVetementJPA.getTypeProduit(), "les TypeProduit de l'Objet métier et de l'Entity JPA doivent être les mêmes : ");
        assertTrue(sousTypeProduitsJPA.size() == 3, "La liste sousTypeProduitsJPA doit contenir 3 éléments : ");
        assertEquals(this.typeProduitVetement.getSousTypeProduits().size(), typeProduitVetementJPA.getSousTypeProduits().size(), "les sousTypeProduits de l'Objet métier et de l'Entity JPA doivent avoir la même taille : ");
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println("****** LISTE sousTypeProduitsJPA du TypeProduitJPA \"vêtement\" typeProduitVetementJPA **********");
        }
        for (final SousTypeProduitJPA sousTypeProduitJPA : sousTypeProduitsJPA) {
            if (AFFICHAGE_GENERAL && affichage) {
                System.out.println(sousTypeProduitJPA);
            }
            assertNotNull(sousTypeProduitJPA.getIdSousTypeProduit(), "l'ID du SousTypeProduitJPA ne doit pas être null : ");
            assertNotNull(sousTypeProduitJPA.getSousTypeProduit(), "le SousTypeProduitJPA ne doit pas être null : ");
            assertEquals(typeProduitVetementJPA, sousTypeProduitJPA.getTypeProduit(), "Le TypeProduitJPA de chaque SousTypeProduitJPA doit être typeProduitVetementJPA : ");
            final List<ProduitJPA> produitsJPA = (List<ProduitJPA>) sousTypeProduitJPA.getProduits();
            if (AFFICHAGE_GENERAL && affichage) {
                System.out.println("contenu de la liste produitsJPA dans le sousTypeProduitJPA : " + sousTypeProduitJPA.getSousTypeProduit());
                System.out.println(this.afficherProduitsJPA(produitsJPA));
            }
            if (produitsJPA != null) {
                for (final ProduitJPA produitJPA : produitsJPA) {
                    if (produitJPA != null) {
                        assertEquals(typeProduitVetementJPA, produitJPA.getTypeProduit(), "Le TypeProduitJPA de chaque ProduitJPA doit être typeProduitVetementJPA : ");
                    }
                }
            }
        }
        assertEquals(1L, idSoustypeProduitHommeJPA, "idSoustypeProduitHommeJPA doit valoir 1L : ");
        assertEquals(typeProduitHommeJPA, sousTypeProduitHommeJPA.getTypeProduit(), "typeProduitHommeJPA doit valoir \"vêtement\" : ");
        assertEquals(VETEMENT_POUR_HOMME, sousTypeProduitHommeStringJPA, "sousTypeProduitHommeStringJPA doit valoir \"vêtement pour homme\" : ");
        assertTrue(listProduitsHommeJPA.size() == 4, "La liste produitsJPA (listProduitsHommeJPA) doit contenir 4 éléments : ");
        for (final ProduitJPA produitJPA : listProduitsHommeJPA) {
            if (produitJPA != null) {
                assertNotNull(produitJPA.getIdProduit(), "l'ID du ProduitJPA ne doit pas être null : ");
                assertEquals(sousTypeProduitHommeJPA, produitJPA.getSousTypeProduit(), "le SousTypeProduitJPA de chaque ProduitJPA doit être sousTypeProduitHommeJPA : ");
                assertNotNull(produitJPA.getProduit(), "le produitString du ProduitJPA ne doit pas être null : ");
                assertEquals(typeProduitVetementJPA, produitJPA.getTypeProduit(), "le TypeProduitJPA de chaque ProduitJPA doit être typeProduitVetementJPA : ");
            }
        }
    }

    /**
     * <div>
     * <p>s'assure que lors d'une conversion : </p>
     * <ul>
     * <p>SousTypeProduit : </p>
     * <li>idSousTypeProduit bien converti.</li>
     * <li>typeProduit bien converti.</li>
     * <li>SousTypeProduit bien converti.</li>
     * <li>produits bien converti.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testSousTypeProduitMETIERToJPA() : vérifie le comportement général de la méthode sousTypeProduitMETIERToJPA(SousTypeProduit pSousTypeProduit)")
    @Tag("MetierToJPA")
    @Test
    public final void testSousTypeProduitMETIERToJPA() {
        final boolean affichage = false;
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("********** CLASSE ConvertisseurMetierToJPATest - méthode testSousTypeProduitMETIERToJPA() ********** ");
            System.out.println("CE TEST VERIFIE le fonctionnement de la méthode sousTypeProduitMETIERToJPA(SousTypeProduit pSousTypeProduit).");
            System.out.println();
        }
        assertNull(ConvertisseurMetierToJPA.sousTypeProduitMETIERToJPA(null), "ConvertisseurMetierToJPA.sousTypeProduitMETIERToJPA(null) doit toujours retourner null : ");
        assertNull(ConvertisseurMetierToJPA.sousTypeProduitMETIERToJPA(new SousTypeProduit("   ")), "ConvertisseurMetierToJPA.sousTypeProduitMETIERToJPA(Blank) doit toujours retourner null : ");
        this.creerScenario();
        final SousTypeProduitJPA sousTypeProduitJPA = ConvertisseurMetierToJPA.sousTypeProduitMETIERToJPA(this.sousTypeProduitVetementHomme);
        final Long idSousTypeProduit = this.sousTypeProduitVetementHomme.getIdSousTypeProduit();
        final String sousTypeProduitString = this.sousTypeProduitVetementHomme.getSousTypeProduit();
        final TypeProduit typeProduit = (TypeProduit) this.sousTypeProduitVetementHomme.getTypeProduit();
        final List<? extends ProduitI> produits = this.sousTypeProduitVetementHomme.getProduits();
        final List<? extends SousTypeProduitI> sousTypeProduitsDansTypeProduitDuSousTypeProduit = typeProduit.getSousTypeProduits();
        if (AFFICHAGE_GENERAL && affichage) {
            final String presentation = String.format("idSousTypeProduit : %-1s - sousTypeProduit : %-20s - idTypeProduit : %-1s - typeProduit : %-20s", idSousTypeProduit, sousTypeProduitString, typeProduit.getIdTypeProduit(), typeProduit.getTypeProduit());
            System.out.println("**** Affichage du sousTypeProduit sousTypeProduitVetementHomme ******");
            System.out.println(presentation);
            System.out.println("sousTypeProduits du typeProduit : " + typeProduit.getSousTypeProduits());
            System.out.println("**** sousTypeProduits du TypeProduit *****");
            System.out.println(this.afficherSousTypeProduits(sousTypeProduitsDansTypeProduitDuSousTypeProduit));
            System.out.println();
        }
        final Long idSousTypeProduitJPA = sousTypeProduitJPA.getIdSousTypeProduit();
        final TypeProduitJPA typeProduitJPA = (TypeProduitJPA) sousTypeProduitJPA.getTypeProduit();
        final String sousTypeProduitStringJPA = sousTypeProduitJPA.getSousTypeProduit();
        final List<ProduitJPA> produitsJPA = (List<ProduitJPA>) sousTypeProduitJPA.getProduits();
        final List<SousTypeProduitJPA> sousTypeProduitsJPADansTypeProduitDuSousTypeProduit = (List<SousTypeProduitJPA>) typeProduitJPA.getSousTypeProduits();
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            final String presentation = String.format("idSousTypeProduit : %-1s - sousTypeProduit : %-20s - idTypeProduit : %-1s - typeProduit : %-20s", idSousTypeProduitJPA, sousTypeProduitStringJPA, typeProduitJPA.getIdTypeProduit(), typeProduitJPA.getTypeProduit());
            System.out.println("**** Affichage du sousTypeProduitJPA converti de sousTypeProduitVetementHomme ******");
            System.out.println(presentation);
            System.out.println("sousTypeProduitsJPA du TypeProduitJPA : " + typeProduitJPA.getSousTypeProduits());
            System.out.println("**** sousTypeProduitsJPA du TypeProduitJPA *****");
            System.out.println(this.afficherSousTypeProduitsJPA(sousTypeProduitsJPADansTypeProduitDuSousTypeProduit));
            System.out.println();
        }
        assertEquals(1L, idSousTypeProduitJPA, "idSousTypeProduitJPA doit valoir 1L : ");
        assertEquals(this.sousTypeProduitVetementHomme.getIdSousTypeProduit(), idSousTypeProduitJPA, "idSousTypeProduitJPA doit valoir l'ID de l'Objet métier : ");
        assertEquals(sousTypeProduitsDansTypeProduitDuSousTypeProduit.size(), sousTypeProduitsJPADansTypeProduitDuSousTypeProduit.size(), "la liste sousTypeProduits doit avoir le même taille dans sousTypeProduit et sousTypeProduitJPA: ");
    }

    /**
     * <div>
     * <p>s'assure que : </p>
     * <ul>
     * <li>null.equalsMetier(null) retourne true.</li>
     * <li>null.equalsMetier(nonNull) retourne false.</li>
     * <li>typeProduit.equalsMetier(typeProduitJPA avec id different) retourne false.</li>
     * <li>la comparaison sur la String typeProduit fonctionne correctement.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testEqualsMetierTypeProduit() : vérifie le comportement général de la méthode equalsMetier(TypeProduit pTypeProduit, TypeProduitJPA pTypeProduitJPA)")
    @Tag("equalsMetier")
    @Test
    public final void testEqualsMetierTypeProduit() {
        final boolean affichage = false;
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("********** CLASSE ConvertisseurMetierToJPATest - méthode testEqualsMetierTypeProduit() ********** ");
            System.out.println("CE TEST VERIFIE le fonctionnement de la méthode equalsMetier(TypeProduit pTypeProduit, TypeProduitJPA pTypeProduitJPA).");
            System.out.println();
        }
        final String typeProduitString = TYPEPRODUIT;
        final TypeProduit typeProduitNull = null;
        final TypeProduit typeProduitIdNull = new TypeProduit(null, "typeProduitIdNull");
        final TypeProduit typeProduitIdNonNull = new TypeProduit(1L, "typeProduitIdNonNull");
        final TypeProduit typeProduitIdDifferent = new TypeProduit(2L, typeProduitString);
        final TypeProduit typeProduit = new TypeProduit(1L, typeProduitString);
        final TypeProduitJPA typeProduitJPANull = null;
        final TypeProduitJPA typeProduitJPAIdNull = new TypeProduitJPA(null, "typeProduitIdNull");
        final TypeProduitJPA typeProduitJPAIdNonNull = new TypeProduitJPA(1L, "typeProduitIdNonNull");
        final TypeProduitJPA typeProduitJPAIdDifferent = new TypeProduitJPA(2L, typeProduitString);
        final TypeProduitJPA typeProduitJPA = new TypeProduitJPA(1L, typeProduitString);
        final boolean nullequalsMetierNull = ConvertisseurMetierToJPA.equalsMetier(typeProduitNull, typeProduitJPANull);
        final boolean unSeulNull1 = ConvertisseurMetierToJPA.equalsMetier(typeProduitNull, typeProduitJPA);
        final boolean unSeulNull2 = ConvertisseurMetierToJPA.equalsMetier(typeProduit, typeProduitJPANull);
        final boolean unSeulIdNull1 = ConvertisseurMetierToJPA.equalsMetier(typeProduitIdNull, typeProduitJPAIdNonNull);
        final boolean unSeulIdNull2 = ConvertisseurMetierToJPA.equalsMetier(typeProduitIdNonNull, typeProduitJPAIdNull);
        final Boolean idDifferents = ConvertisseurMetierToJPA.equalsMetier(typeProduitIdDifferent, typeProduitJPA);
        final Boolean idDifferents2 = ConvertisseurMetierToJPA.equalsMetier(typeProduit, typeProduitJPAIdDifferent);
        final boolean equalsMetierAvecIdNull = ConvertisseurMetierToJPA.equalsMetier(typeProduitIdNull, typeProduitJPAIdNull);
        final Boolean equalsMetierClassique = ConvertisseurMetierToJPA.equalsMetier(typeProduit, typeProduitJPA);
        final boolean equalMetierFalse1 = ConvertisseurMetierToJPA.equalsMetier(typeProduitIdDifferent, typeProduitJPA);
        final boolean equalMetierFalse2 = ConvertisseurMetierToJPA.equalsMetier(typeProduit, typeProduitJPAIdDifferent);
        assertTrue(nullequalsMetierNull, "null.equalsMetier(null) doit retourner true : ");
        assertFalse(unSeulNull1, "null.equalsMetier(nonNull) doit retourner false : ");
        assertFalse(unSeulNull2, "nonNull.equalsMetier(null) doit retourner false : ");
        assertFalse(unSeulIdNull1, "typeProduitIdNull.equalsMetier(typeProduitJPAIdNonNull) doit retourner false : ");
        assertFalse(unSeulIdNull2, "typeProduitIdNonNull.equalsMetier(typeProduitJPAIdNull) doit retourner false : ");
        assertFalse(idDifferents, "typeProduitIdDifferent.equalsMetier(typeProduitJPA) doit retourner false : ");
        assertFalse(idDifferents2, "typeProduit.equalsMetier(typeProduitJPAIdDifferent) doit retourner false : ");
        assertTrue(equalsMetierAvecIdNull, "typeProduitIdNull.equalsMetier(typeProduitJPAIdNull) doit retourner true : ");
        assertTrue(equalsMetierClassique, "typeProduit.equalsMetier(typeProduitJPA) doit retourner true : ");
        assertFalse(equalMetierFalse1, "typeProduitIdDifferent.equalsMetier(typeProduitJPA) doit retourner false : ");
        assertFalse(equalMetierFalse2, "typeProduit.equalsMetier(typeProduitJPAIdDifferent) doit retourner false : ");
    }

    
    
    /**
     * <div>
     * <p>s'assure que : </p>
     * <ul>
     * <li>null.equalsMetier(null) retourne true.</li>
     * <li>null.equalsMetier(nonNull) retourne false.</li>
     * <li>sousTypeProduit.equalsMetier(sousTypeProduitJPA avec id different)
     * retourne false.</li>
     * <li>sousTypeProduit.equalsMetier(sousTypeProduitJPA avec TypeProduit different)
     * retourne false.</li>
     * <li>la comparaison sur la String SousTypeProduit fonctionne
     * correctement. </li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testEqualsMetierSousTypeProduit() : vérifie le comportement général de la méthode equalsMetier(SousTypeProduit pSousTypeProduit, SousTypeProduitJPA pSousTypeProduitJPA)")
    @Tag("equalsMetier")
    @Test
    public final void testEqualsMetierSousTypeProduit() {

        // **********************************
        // AFFICHAGE DANS LE TEST ou NON
        final boolean affichage = false;
        // **********************************

        /* AFFICHAGE A LA CONSOLE. */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("********** CLASSE ConvertisseurMetierToJPATest - méthode testEqualsMetierSousTypeProduit() ********** ");
            System.out.println("CE TEST VERIFIE le fonctionnement de la méthode equalsMetier(SousTypeProduit pSousTypeProduit, SousTypeProduitJPA pSousTypeProduitJPA).");
            System.out.println();
        }

        //**** ARRANGE - GIVEN
        /* String */
        final String sousTypeProduitPasNullString = "sousTypeProduitPasNull";
        final String typeProduitPasNullString = "typeProduitPasNull";
        final String typeProduitString = TYPEPRODUIT;
        final String sousTypeProduitString = "sousTypeProduit";
        final String typeProduitDifferentString = "typeProduit différent";

        /* TypeProduit */
        final TypeProduit typeProduitNonNull
            = new TypeProduit(null, typeProduitPasNullString);
        final TypeProduit typeProduit
            = new TypeProduit(1L, typeProduitString);
        final TypeProduit typeProduitDifferent
            = new TypeProduit(1L, typeProduitDifferentString);

        /* SousTypeProduit */
        final SousTypeProduit sousTypeProduitNull = null;
        final SousTypeProduit sousTypeProduitIdNull
            = new SousTypeProduit(null, sousTypeProduitPasNullString, null);
        final SousTypeProduit sousTypeProduit
            = new SousTypeProduit(1L, sousTypeProduitString, typeProduit, null);
        final SousTypeProduit sousTypeProduitIdDifferent
            = new SousTypeProduit(2L, sousTypeProduitString, typeProduit, null);
        final SousTypeProduit sousTypeProduitSTPDifferent
            = new SousTypeProduit(1L, sousTypeProduitString, typeProduitDifferent, null);
        final SousTypeProduit sousTypeProduitDiff
            = new SousTypeProduit(1L, "toto", typeProduit, null);

        /* TypeProduitJPA */
        final TypeProduitJPA typeProduitJPANonNull
            = new TypeProduitJPA(null, typeProduitPasNullString);
        final TypeProduitJPA typeProduitJPA
            = new TypeProduitJPA(1L, typeProduitString);
        final TypeProduitJPA typeProduitJPADifferent
            = new TypeProduitJPA(1L, typeProduitDifferentString);

        /* SousTypeProduitJPA */
        final SousTypeProduitJPA sousTypeProduitJPANull = null;
        final SousTypeProduitJPA sousTypeProduitJPAIdNull
            = new SousTypeProduitJPA(null, sousTypeProduitPasNullString, null);
        final SousTypeProduitJPA sousTypeProduitJPA
            = new SousTypeProduitJPA(1L, sousTypeProduitString, typeProduitJPA, null);
        final SousTypeProduitJPA sousTypeProduitJPAIdDifferent
            = new SousTypeProduitJPA(2L, sousTypeProduitString, typeProduitJPA, null);
        final SousTypeProduitJPA sousTypeProduitJPASTPDifferent
            = new SousTypeProduitJPA(1L, sousTypeProduitString, typeProduitJPADifferent, null);

        // ACT - WHEN
        final boolean nullequalsMetierNull
            = ConvertisseurMetierToJPA.equalsMetier(sousTypeProduitNull, sousTypeProduitJPANull);
        final boolean unSeulNull1
            = ConvertisseurMetierToJPA.equalsMetier(sousTypeProduitNull, sousTypeProduitJPA);
        final boolean unSeulNull2
            = ConvertisseurMetierToJPA.equalsMetier(sousTypeProduit, sousTypeProduitJPANull);
        final boolean unIdNull1
            = ConvertisseurMetierToJPA.equalsMetier(sousTypeProduitIdNull, sousTypeProduitJPA);
        final boolean unIdNull2
            = ConvertisseurMetierToJPA.equalsMetier(sousTypeProduit, sousTypeProduitJPAIdNull);
        final boolean idDifferents1
            = ConvertisseurMetierToJPA.equalsMetier(sousTypeProduit, sousTypeProduitJPAIdDifferent);
        final boolean idDifferents2
            = ConvertisseurMetierToJPA.equalsMetier(sousTypeProduitIdDifferent, sousTypeProduitJPA);
        final boolean sousTypeProduitDifferents1
            = ConvertisseurMetierToJPA.equalsMetier(sousTypeProduit, sousTypeProduitJPASTPDifferent);
        final boolean sousTypeProduitDifferents2
            = ConvertisseurMetierToJPA.equalsMetier(sousTypeProduitSTPDifferent, sousTypeProduitJPA);
        final boolean equalsMetier
            = ConvertisseurMetierToJPA.equalsMetier(sousTypeProduit, sousTypeProduitJPA);

        // ASSERT - THEN
        /* null.equalsMetier(null) retourne true. */
        assertTrue(nullequalsMetierNull, "null.equalsMetier(null) doit retourner true : ");
        /* null.equalsMetier(nonNull) retourne false. */
        assertFalse(unSeulNull1, "null.equalsMetier(nonNull) doit retourner false : ");
        assertFalse(unSeulNull2, "nonNull.equalsMetier(null) doit retourner false : ");
        /* sousTypeProduit.equalsMetier(sousTypeProduitJPA avec id different) retourne false. */
        assertFalse(unIdNull1, "sousTypeProduitIdNull.equalsMetier(sousTypeProduitJPA) doit retourner false : ");
        assertFalse(unIdNull2, "sousTypeProduit.equalsMetier(sousTypeProduitJPAIdNull) doit retourner false : ");
        assertFalse(idDifferents1, "sousTypeProduit.equalsMetier(sousTypeProduitJPAIdDifferent) doit retourner false ; ");
        assertFalse(idDifferents2, "sousTypeProduitIdDifferent.equalsMetier(sousTypeProduitJPA) doit retourner false ; ");
        /* sousTypeProduit.equalsMetier(sousTypeProduitJPA avec TypeProduit different) retourne false. */
        assertFalse(sousTypeProduitDifferents1, "sousTypeProduit.equalsMetier(sousTypeProduitJPASTPDifferent) doit retourner false : ");
        assertFalse(sousTypeProduitDifferents2, "sousTypeProduitSTPDifferent.equalsMetier(sousTypeProduitJPA) doit retourner false : ");
        /* la comparaison sur la String SousTypeProduit fonctionne correctement. */
        assertTrue(equalsMetier, "sousTypeProduit.equalsMetier(sousTypeProduitJPA) doit retourner true : ");
    }

    
    
    /**
     * <div>
     * <p>s'assure que : </p>
     * <ul>
     * <li>null.equalsMetier(null) retourne true.</li>
     * <li>null.equalsMetier(nonNull) retourne false.</li>
     * <li>Produit.equalsMetier(ProduitJPA avec id different) retourne false.</li>
     * <li>produit.equalsMetier(produitJPA avec sousTypeProduit different) retourne false.</li>
     * <li>la comparaison sur la String produit fonctionne correctement.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @DisplayName("testEqualsMetierProduit() : vérifie le comportement général de la méthode equalsMetier(Produit pProduit, ProduitJPA pProduitJPA)")
    @Tag("equalsMetier")
    @Test
    public final void testEqualsMetierProduit() {
        final boolean affichage = false;
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("********** CLASSE ConvertisseurMetierToJPATest - méthode testEqualsMetierProduit() ********** ");
            System.out.println("CE TEST VERIFIE le fonctionnement de la méthode equalsMetier(Produit pProduit, ProduitJPA pProduitJPA).");
            System.out.println();
        }
        
        final String sousTypeProduitPasNullString = "sousTypeProduitPasNull";
        final String typeProduitPasNullString = "typeProduitPasNull";
        final String typeProduitString = TYPEPRODUIT;
        final String sousTypeProduitString = "sousTypeProduit";
        final String typeProduitDifferentString = "typeProduit différent";
        final String produitString = "produit";
        final TypeProduit typeProduit = new TypeProduit(1L, typeProduitString);
        final SousTypeProduit sousTypeProduit = new SousTypeProduit(1L, sousTypeProduitString, typeProduit);
        final Produit produit = new Produit(1L, produitString, sousTypeProduit);
        final Produit produitNull = null;
        final Produit produitIdDifferent = new Produit(2L, produitString, sousTypeProduit);
        final Produit produitDifferent = new Produit(1L, "different", sousTypeProduit);
        final TypeProduitJPA typeProduitJPA = new TypeProduitJPA(1L, typeProduitString);
        final SousTypeProduitJPA sousTypeProduitJPA = new SousTypeProduitJPA(1L, sousTypeProduitString, typeProduitJPA);
        final SousTypeProduitJPA sousTypeProduitJPADifferent = new SousTypeProduitJPA(2L, sousTypeProduitString, typeProduitJPA);
        final ProduitJPA produitJPA = new ProduitJPA(1L, produitString, sousTypeProduitJPA);
        final ProduitJPA produitJPANull = null;
        final ProduitJPA produitJPAIdDifferent = new ProduitJPA(2L, produitString, sousTypeProduitJPA);
        final ProduitJPA produitJPASTPDifferent = new ProduitJPA(1L, produitString, sousTypeProduitJPADifferent);
        final boolean nullequalsMetierNull = ConvertisseurMetierToJPA.equalsMetier(produitNull, produitJPANull);
        final boolean unSeulNull1 = ConvertisseurMetierToJPA.equalsMetier(produitNull, produitJPA);
        final boolean unSeulNull2 = ConvertisseurMetierToJPA.equalsMetier(produit, produitJPANull);
        final boolean idDifferents1 = ConvertisseurMetierToJPA.equalsMetier(produitIdDifferent, produitJPA);
        final boolean idDifferents2 = ConvertisseurMetierToJPA.equalsMetier(produit, produitJPAIdDifferent);
        final boolean stpDifferents = ConvertisseurMetierToJPA.equalsMetier(produit, produitJPASTPDifferent);
        final boolean equalsMetier = ConvertisseurMetierToJPA.equalsMetier(produit, produitJPA);
        final boolean pasEqualsMetier = ConvertisseurMetierToJPA.equalsMetier(produit, ConvertisseurMetierToJPA.produitMETIERToJPA(produitDifferent));
        assertTrue(nullequalsMetierNull, "null.equalsMetier(null) doit retourner true : ");
        assertFalse(unSeulNull1, "null.equalsMetier(nonNull) doit retourner false : ");
        assertFalse(unSeulNull2, "nonNull.equalsMetier(null) doit retourner false : ");
        assertFalse(idDifferents1, "produitIdDifferent.equalsMetier(produitJPA) doit retourner false : ");
        assertFalse(idDifferents2, "produit.equalsMetier(produitJPAIdDifferent) doit retourner false : ");
        assertFalse(stpDifferents, "produit.equalsMetier(produitJPASTPDifferent) doit retourner false : ");
        assertTrue(equalsMetier, "produit.equalsMetier(produitJPA) doit retourner true : ");
        assertFalse(pasEqualsMetier, "produit.equalsMetier(produitDifferent) doit retourner false : ");
    }

    /**
     * <div>
     * <p>compare les valeurs equalsMetier d'un TypeProduit
     * et d'un TypeProduitJPA et ne retourne true que
     * si elles sont toutes égales.</p>
     * <ul>
     * <li>retourne true si les deux paramètres sont null.</li>
     * <li>retourne false si un seul des paramètres est null.</li>
     * <li>compare les idTypeProduit et retourne false
     * s'ils ne sont pas égaux.</li>
     * <li>compare les typeProduit (String) et retourne false
     * s'ils ne sont pas égaux.</li>
     * </ul>
     * </div>
     *
     * @param pTypeProduit
     * @param pTypeProduitJPA
     * @return boolean : true si toutes les valeurs sont égales.
     */
    private boolean memeEqualsTypeProduit(
            final TypeProduit pTypeProduit,
            final TypeProduitJPA pTypeProduitJPA) {
        if (pTypeProduit == null) {
            if (pTypeProduitJPA != null) {
                return false;
            }
            return true;
        }
        if (pTypeProduitJPA == null) {
            return false;
        }
        final Long idTypeProduit = pTypeProduit.getIdTypeProduit();
        final Long idTypeProduitJPA = pTypeProduitJPA.getIdTypeProduit();
        if (idTypeProduit != idTypeProduitJPA) {
            return false;
        }
        final String typeProduit = pTypeProduit.getTypeProduit();
        final String typeProduitJPA = pTypeProduitJPA.getTypeProduit();
        if (!Strings.CS.equals(typeProduit, typeProduitJPA)) {
            return false;
        }
        return true;
    }

    /**
     * <div>
     * <p>compare les valeurs equalsMetier de SousTypeProduit
     * d'un objet métier de type
     * <code><span style="font-weight:bold;">SousTypeProduit</span>
     *  pSousTypeProduit</code> et d'une Entity JPA équivalente de type
     *  <code><span style="font-weight:bold;">
     *  SousTypeProduitJPA</span> pSousTypeProduitJPA</code>.</p>
     *  <p>le paramètre de type
     *  <code><span style="font-weight:bold;">
     *  SousTypeProduitJPA</span> SousTypeProduitJPA</code> est censé être
     *  encapsulé dans un <code><span style="font-weight:bold;">
     *  TypeProduitJPA</span> pTypeProduitJPA</code>.</p>
     *  <ul>
     *  <li>retourne false si pTypeProduitJPA == null.</li>
     *  <li>retourne false si pSousTypeProduit == null.</li>
     *  <li>retourne false si pSousTypeProduitJPA == null.</li>
     *  <li>compare les <code><span style="font-weight:bold;">
     *  idSousTypeProduit</code> de niveau 2 (SousTypeProduit)
     *  et retourne false s'ils ne sont pas égaux.</li>
     *  <li>vérifie que le <code><span style="font-weight:bold;">
     *  typeProduit</code> du paramètre pSousTypeProduitJPA est l'instance
     *  <code>pTypeProduitJPA</code> passée en paramètre.
     *  Retourne false si ce n'est pas le cas.</li>
     *  <li>compare les <code style="font-weight:bold;">
     *  sousTypeProduit</code> (String) et retourne false
     *  s'ils ne sont pas égaux.</li>
     *  <li>retourne true si toutes les valeurs sont égales.</li>
     *  </ul>
     * </div>
     *
     * @param pTypeProduitJPA : TypeProduitJPA
     * @param pSousTypeProduit : SousTypeProduit
     * @param pSousTypeProduitJPA : SousTypeProduitJPA
     *
     * @return boolean : true si toutes les valeurs sont égales.
     */
    private boolean memeEqualsSousTypeProduit(
            final TypeProduitJPA pTypeProduitJPA,
            final SousTypeProduit pSousTypeProduit,
            final SousTypeProduitJPA pSousTypeProduitJPA) {
        if (pTypeProduitJPA == null) {
            return false;
        }
        if (pSousTypeProduit == null) {
            return false;
        }
        if (pSousTypeProduitJPA == null) {
            return false;
        }
        if (pSousTypeProduit.getIdSousTypeProduit() != pSousTypeProduitJPA.getIdSousTypeProduit()) {
            return false;
        }
        if (pTypeProduitJPA != pSousTypeProduitJPA.getTypeProduit()) {
            return false;
        }
        if (!Strings.CS.equals(pSousTypeProduit.getSousTypeProduit(), pSousTypeProduitJPA.getSousTypeProduit())) {
            return false;
        }
        return true;
    }

    /**
     * <div>
     * <p>retourne une String formatée pour l'affichage
     * d'un TypeProduit</p>
     * <ul>
     * <li>retourne null si pTypeProduit == null.</li>
     * <li>affiche le TypeProduit</li>
     * <li>affiche la liste des SousTypeProduit contenus
     * dans le TypeProduit</li>
     * <li>affiche pour chaque SousTypeProduit la liste des Produit qu'il contient.</li>
     * </ul>
     * </div>
     *
     * <div>
     * <p style="text-decoration:underline;">Exemple d'affichage : </p>
     * <pre>******* TypeProduit : vêtement *******
     * [idTypeProduit : 1 - typeProduit : vêtement  ]
     *
     * ******* sousTypeProduits du TypeProduit : vêtement
     * [idSousTypeProduit : 1 - sousTypeProduit : vêtement pour homme  - [idTypeProduit du TypeProduit dans le SousTypeProduit : 1 - typeProduitString du TypeProduit dans le SousTypeProduit : vêtement     ]]
     * ***** liste des produits dans le sousProduit : vêtement pour homme
     * [idProduit dans produits du SousTypeProduit : 1 - produit dans produits du SousTypeProduit : chemise à manches longues pour homme     - sousTypeProduit dans le produit : vêtement pour homme ]
     * [idProduit dans produits du SousTypeProduit : 2 - produit dans produits du SousTypeProduit : chemise à manches courtes pour homme     - sousTypeProduit dans le produit : vêtement pour homme ]
     * [idProduit dans produits du SousTypeProduit : 3 - produit dans produits du SousTypeProduit : sweatshirt pour homme                    - sousTypeProduit dans le produit : vêtement pour homme ]
     * [idProduit dans produits du SousTypeProduit : 4 - produit dans produits du SousTypeProduit : teeshirt pour homme                      - sousTypeProduit dans le produit : vêtement pour homme ]
     *
     * [idSousTypeProduit : 2 - sousTypeProduit : vêtement pour femme  - [idTypeProduit du TypeProduit dans le SousTypeProduit : 1 - typeProduitString du TypeProduit dans le SousTypeProduit : vêtement     ]]
     * ***** liste des produits dans le sousProduit : vêtement pour femme
     * null
     *
     * [idSousTypeProduit : 3 - sousTypeProduit : vêtement pour enfant - [idTypeProduit du TypeProduit dans le SousTypeProduit : 1 - typeProduitString du TypeProduit dans le SousTypeProduit : vêtement     ]]
     * ***** liste des produits dans le sousProduit : vêtement pour enfant
     * null
     * </pre>
     * </div>
     *
     * @param pTypeProduit : TypeProduit : Objet métier à afficher
     * @return String
     */
    private String afficherTypeProduitFormate(final TypeProduit pTypeProduit) {
        if (pTypeProduit == null) {
            return null;
        }
        final Long idTypeProduit = pTypeProduit.getIdTypeProduit();
        final String typeProduitString = pTypeProduit.getTypeProduit();
        final List<? extends SousTypeProduitI> sousTypeProduits = pTypeProduit.getSousTypeProduits();
        final StringBuilder stb = new StringBuilder();
        stb.append("******* TypeProduit : ");
        stb.append(typeProduitString);
        stb.append(" *******");
        stb.append(SAUT_DE_LIGNE);
        final String pres1 = String.format("[idTypeProduit : %-1s - typeProduit : %-10s]", idTypeProduit, typeProduitString);
        stb.append(pres1);
        stb.append(SAUT_DE_LIGNE);
        stb.append(SAUT_DE_LIGNE);
        stb.append("******* sousTypeProduits du TypeProduit : ");
        stb.append(typeProduitString);
        stb.append(SAUT_DE_LIGNE);
        if (sousTypeProduits == null) {
            stb.append(NULL);
        } else {
            for (final SousTypeProduitI sousTypeProduit : sousTypeProduits) {
                final Long idSousTypeProduit = sousTypeProduit.getIdSousTypeProduit();
                final String sousTypeProduitString = sousTypeProduit.getSousTypeProduit();
                final TypeProduitI tpSTPi = sousTypeProduit.getTypeProduit();
                final TypeProduit typeProduitduSousTypeProduit = (tpSTPi instanceof TypeProduit other) ? other : null;
                final List<? extends ProduitI> produitsDansSousProduit = sousTypeProduit.getProduits();
                Long idTypeProduitduSousTypeProduit = null;
                String typeProduitduSousTypeProduitString = null;
                if (typeProduitduSousTypeProduit != null) {
                    idTypeProduitduSousTypeProduit = typeProduitduSousTypeProduit.getIdTypeProduit();
                    typeProduitduSousTypeProduitString = typeProduitduSousTypeProduit.getTypeProduit();
                }
                final String pres2 = String.format("[idSousTypeProduit : %-1s - sousTypeProduit : %-20s - [idTypeProduit du TypeProduit dans le SousTypeProduit : %-1s - typeProduitString du TypeProduit dans le SousTypeProduit : %-13s]]", idSousTypeProduit, sousTypeProduitString, idTypeProduitduSousTypeProduit, typeProduitduSousTypeProduitString);
                stb.append(pres2);
                stb.append(SAUT_DE_LIGNE);
                stb.append("***** liste des produits dans le sousProduit : ");
                stb.append(sousTypeProduitString);
                stb.append(SAUT_DE_LIGNE);
                if (produitsDansSousProduit == null) {
                    stb.append(NULL);
                    stb.append(SAUT_DE_LIGNE);
                } else {
                    for (final ProduitI produit : produitsDansSousProduit) {
                        final Long idProduit = produit.getIdProduit();
                        final String produitString = produit.getProduit();
                        final SousTypeProduitI sousTypeProduitProduit = produit.getSousTypeProduit();
                        String sousTypeProduitProduitString = null;
                        if (sousTypeProduitProduit != null) {
                            sousTypeProduitProduitString = sousTypeProduitProduit.getSousTypeProduit();
                        }
                        stb.append('\t');
                        final String presProduit = String.format("[idProduit dans produits du SousTypeProduit : %-1s - produit dans produits du SousTypeProduit : %-40s - sousTypeProduit dans le produit : %-20s]", idProduit, produitString, sousTypeProduitProduitString);
                        stb.append(presProduit);
                        stb.append(SAUT_DE_LIGNE);
                    }
                }
                stb.append(SAUT_DE_LIGNE);
            }
        }
        return stb.toString();
    }

    /**
     * <div>
     * <p>retourne une String formatée pour l'affichage
     * d'un TypeProduitJPA</p>
     * <ul>
     * <li>retourne null si pTypeProduitJPA == null.</li>
     * <li>affiche le TypeProduitJPA</li>
     * <li>affiche la liste des SousTypeProduitJPA contenus
     * dans le TypeProduitJPA</li>
     * <li>affiche pour chaque SousTypeProduitJPA
     * la liste des ProduitJPA qu'il contient.</li>
     * </ul>
     * </div>
     *
     * <div>
     * <p style="text-decoration:underline;">Exemple d'affichage : </p>
     * <pre>******* TypeProduitJPA : vêtement *******
     * [idTypeProduit : 1 - typeProduit : vêtement  ]
     *
     * ******* sousTypeProduitsJPA du TypeProduitJPA : vêtement
     * [idSousTypeProduit : 1 - sousTypeProduit : vêtement pour homme  - [idTypeProduit du TypeProduit dans le SousTypeProduit : 1 - typeProduitString du TypeProduit dans le SousTypeProduit : vêtement     ]]
     * ***** liste des produits dans le sousProduitJPA : vêtement pour homme
     * [idProduit dans produits du SousTypeProduit : 1 - produit dans produits du SousTypeProduit : chemise à manches longues pour homme     - sousTypeProduit dans le produit : vêtement pour homme ]
     * [idProduit dans produits du SousTypeProduit : 2 - produit dans produits du SousTypeProduit : chemise à manches courtes pour homme     - sousTypeProduit dans le produit : vêtement pour homme ]
     * [idProduit dans produits du SousTypeProduit : 3 - produit dans produits du SousTypeProduit : sweatshirt pour homme                    - sousTypeProduit dans le produit : vêtement pour homme ]
     * [idProduit dans produits du SousTypeProduit : 4 - produit dans produits du SousTypeProduit : teeshirt pour homme                      - sousTypeProduit dans le produit : vêtement pour homme ]
     *
     * [idSousTypeProduit : 2 - sousTypeProduit : vêtement pour femme  - [idTypeProduit du TypeProduit dans le SousTypeProduit : 1 - typeProduitString du TypeProduit dans le SousTypeProduit : vêtement     ]]
     * ***** liste des produits dans le sousProduitJPA : vêtement pour femme
     * null
     *
     * [idSousTypeProduit : 3 - sousTypeProduit : vêtement pour enfant - [idTypeProduit du TypeProduit dans le SousTypeProduit : 1 - typeProduitString du TypeProduit dans le SousTypeProduit : vêtement     ]]
     * ***** liste des produits dans le sousProduitJPA : vêtement pour enfant
     * null
     * </pre>
     * </div>
     *
     * @param pTypeProduitJPA : TypeProduitJPA : Entity JPA à afficher
     * @return String
     */
    private String afficherTypeProduitFormate(final TypeProduitJPA pTypeProduitJPA) {
        if (pTypeProduitJPA == null) {
            return null;
        }
        final Long idTypeProduitJPA = pTypeProduitJPA.getIdTypeProduit();
        final String typeProduitStringJPA = pTypeProduitJPA.getTypeProduit();
        final List<? extends SousTypeProduitI> sousTypeProduitsJPA = pTypeProduitJPA.getSousTypeProduits();
        final StringBuilder stb = new StringBuilder();
        stb.append("******* TypeProduitJPA : ");
        stb.append(typeProduitStringJPA);
        stb.append(" *******");
        stb.append(SAUT_DE_LIGNE);
        final String pres1 = String.format("[idTypeProduit : %-1s - typeProduit : %-10s]", idTypeProduitJPA, typeProduitStringJPA);
        stb.append(pres1);
        stb.append(SAUT_DE_LIGNE);
        stb.append(SAUT_DE_LIGNE);
        stb.append("******* sousTypeProduitsJPA du TypeProduitJPA : ");
        stb.append(typeProduitStringJPA);
        stb.append(SAUT_DE_LIGNE);
        if (sousTypeProduitsJPA == null) {
            stb.append(NULL);
        } else {
            for (final SousTypeProduitI sousTypeProduitJPA : sousTypeProduitsJPA) {
                final Long idSousTypeProduitJPA = sousTypeProduitJPA.getIdSousTypeProduit();
                final String sousTypeProduitJPAString = sousTypeProduitJPA.getSousTypeProduit();
                final TypeProduitI tpSTPi = sousTypeProduitJPA.getTypeProduit();
                final TypeProduitJPA typeProduitduSousTypeProduit = (tpSTPi instanceof TypeProduitJPA other) ? other : null;
                final List<? extends ProduitI> produitsDansSousProduit = sousTypeProduitJPA.getProduits();
                Long idTypeProduitduSousTypeProduit = null;
                String typeProduitduSousTypeProduitString = null;
                if (typeProduitduSousTypeProduit != null) {
                    idTypeProduitduSousTypeProduit = typeProduitduSousTypeProduit.getIdTypeProduit();
                    typeProduitduSousTypeProduitString = typeProduitduSousTypeProduit.getTypeProduit();
                }
                final String pres2 = String.format("[idSousTypeProduit : %-1s - sousTypeProduit : %-20s - [idTypeProduit du TypeProduit dans le SousTypeProduit : %-1s - typeProduitString du TypeProduit dans le SousTypeProduit : %-13s]]", idSousTypeProduitJPA, sousTypeProduitJPAString, idTypeProduitduSousTypeProduit, typeProduitduSousTypeProduitString);
                stb.append(pres2);
                stb.append(SAUT_DE_LIGNE);
                stb.append("***** liste des produits dans le sousProduitJPA : ");
                stb.append(sousTypeProduitJPAString);
                stb.append(SAUT_DE_LIGNE);
                if (produitsDansSousProduit == null) {
                    stb.append(NULL);
                    stb.append(SAUT_DE_LIGNE);
                } else {
                    for (final ProduitI produitJPA : produitsDansSousProduit) {
                        final Long idProduitJPA = produitJPA.getIdProduit();
                        final String produitJPAString = produitJPA.getProduit();
                        final SousTypeProduitI sousTypeProduitJPAProduit = produitJPA.getSousTypeProduit();
                        String sousTypeProduitJPAProduitString = null;
                        if (sousTypeProduitJPAProduit != null) {
                            sousTypeProduitJPAProduitString = sousTypeProduitJPAProduit.getSousTypeProduit();
                        }
                        stb.append('\t');
                        final String presProduit = String.format("[idProduit dans produits du SousTypeProduit : %-1s - produit dans produits du SousTypeProduit : %-40s - sousTypeProduit dans le produit : %-20s]", idProduitJPA, produitJPAString, sousTypeProduitJPAProduitString);
                        stb.append(presProduit);
                        stb.append(SAUT_DE_LIGNE);
                    }
                }
                stb.append(SAUT_DE_LIGNE);
            }
        }
        return stb.toString();
    }

    /**
     * <div>
     * <p>retourne une String formatée pour l'affichage
     * d'un Produit</p>
     * <ul>
     * <li>retourne null si pProduit == null.</li>
     * <li>affiche en 1ere ligne [idProduit - produit (String)]</li>
     * <li>affiche en 1ere ligne [idSousTypeProduit -
     * typeProduit du SousTypeProduit - SousTypeProduit (String)]</li>
     * <li>affiche en 1ère ligne [idTypeProduit - typeProduit (String)]</li>
     * <li>affiche sur des lignes séparées chaque produit de
     * la liste produits du sousTypeProduit.</li>
     * </ul>
     * </div>
     *
     * @param pProduit : Produit
     * @return String
     */
    private String afficherProduitFormate(final Produit pProduit) {
        if (pProduit == null) {
            return null;
        }
        final StringBuilder stb = new StringBuilder();
        final Long idProduitProv = pProduit.getIdProduit();
        final String produitString = pProduit.getProduit();
        final SousTypeProduit sousTypeProduitProv = (SousTypeProduit) pProduit.getSousTypeProduit();
        Long idSousTypeProduitProv = null;
        TypeProduit typeProduit = null;
        String typeProduitStringSTP = null;
        String sousTypeProduitString = null;
        if (sousTypeProduitProv != null) {
            idSousTypeProduitProv = sousTypeProduitProv.getIdSousTypeProduit();
            typeProduit = (TypeProduit) sousTypeProduitProv.getTypeProduit();
            if (typeProduit != null) {
                typeProduitStringSTP = typeProduit.getTypeProduit();
            }
            sousTypeProduitString = sousTypeProduitProv.getSousTypeProduit();
        }
        Long idTypeProduit = null;
        String typeProduitString = null;
        final TypeProduit typeProduitProv = (TypeProduit) pProduit.getTypeProduit();
        if (typeProduitProv != null) {
            idTypeProduit = typeProduitProv.getIdTypeProduit();
            typeProduitString = typeProduitProv.getTypeProduit();
        }
        final String pres = String.format("idProduit : %-1s - produit : %-40s - [idSousTypeProduit : %-1s - typeProduit du sousTypeProduit : %-13s - sousTypeProduit : %-20s] - [idTypeProduit : %-1s - typeProduit : %-13s]", idProduitProv, produitString, idSousTypeProduitProv, typeProduitStringSTP, sousTypeProduitString, idTypeProduit, typeProduitString);
        stb.append(pres);
        stb.append(SAUT_DE_LIGNE);
        List<Produit> produitsSTP = null;
        if (sousTypeProduitProv != null) {
            stb.append("**** Liste des produits du sousTypeProduit : ");
            stb.append(sousTypeProduitProv.getSousTypeProduit());
            stb.append(SAUT_DE_LIGNE);
            
            produitsSTP 
            	= convertirListProduitEnListProduit(
            			sousTypeProduitProv.getProduits());
            
            if (produitsSTP == null) {
                stb.append(NULL);
                stb.append(SAUT_DE_LIGNE);
            } else {
                for (final Produit produitProv : produitsSTP) {
                    final Long idProduitSTP = produitProv.getIdProduit();
                    final String produitStringProv = produitProv.getProduit();
                    final SousTypeProduit sousTypeProduitProvSTP = (SousTypeProduit) produitProv.getSousTypeProduit();
                    Long idSousTypeProduitProvSTP = null;
                    TypeProduit typeProduitProvSTP = null;
                    String typeProduitStringSTPProv = null;
                    String sousTypeProduitStringSTP = null;
                    if (sousTypeProduitProvSTP != null) {
                        idSousTypeProduitProvSTP = sousTypeProduitProvSTP.getIdSousTypeProduit();
                        typeProduitProvSTP = (TypeProduit) sousTypeProduitProvSTP.getTypeProduit();
                        if (typeProduitProvSTP != null) {
                            typeProduitStringSTPProv = typeProduitProvSTP.getTypeProduit();
                        }
                        sousTypeProduitStringSTP = sousTypeProduitProvSTP.getSousTypeProduit();
                    }
                    Long idTypeProduitSTP = null;
                    String typeProduitString1 = null;
                    final TypeProduit typeProduitProv1 = (TypeProduit) pProduit.getTypeProduit();
                    if (typeProduitProv1 != null) {
                        idTypeProduitSTP = typeProduitProv1.getIdTypeProduit();
                        typeProduitString1 = typeProduitProv1.getTypeProduit();
                    }
                    stb.append('\t');
                    final String pres2 = String.format("[idProduit : %-1s - produit : %-40s - [idSousTypeProduit : %-1s - typeProduit du sousTypeProduit : %-13s - sousTypeProduit : %-30s] - [idTypeProduit : %-1s - typeProduit : %-13s]]", idProduitSTP, produitStringProv, idSousTypeProduitProvSTP, typeProduitStringSTPProv, sousTypeProduitStringSTP, idTypeProduitSTP, typeProduitString1);
                    stb.append(pres2);
                    stb.append(SAUT_DE_LIGNE);
                }
            }
        }
        return stb.toString();
    }

    /**
     * <div>
     * <p>retourne une String formatée pour l'affichage
     * d'un ProduitJPA</p>
     * <ul>
     * <li>retourne null si pProduitJPA == null.</li>
     * <li>affiche en 1ere ligne [idProduit - produit (String)]</li>
     * <li>affiche en 1ere ligne [idSousTypeProduit -
     * typeProduit du SousTypeProduit - SousTypeProduit (String)]</li>
     * <li>affiche en 1ère ligne [idTypeProduit - typeProduit (String)]</li>
     * <li>affiche sur des lignes séparées chaque produit de
     * la liste produits du sousTypeProduit.</li>
     * </ul>
     * </div>
     *
     * @param pProduitJPA : ProduitJPA
     * @return String
     */
    private String afficherProduitFormate(final ProduitJPA pProduitJPA) {
        if (pProduitJPA == null) {
            return null;
        }
        final StringBuilder stb = new StringBuilder();
        final Long idProduitProv = pProduitJPA.getIdProduit();
        final String produitString = pProduitJPA.getProduit();
        final SousTypeProduitJPA sousTypeProduitProv = (SousTypeProduitJPA) pProduitJPA.getSousTypeProduit();
        Long idSousTypeProduitProv = null;
        TypeProduitJPA typeProduitJPA = null;
        String typeProduitStringSTP = null;
        String sousTypeProduitString = null;
        if (sousTypeProduitProv != null) {
            idSousTypeProduitProv = sousTypeProduitProv.getIdSousTypeProduit();
            typeProduitJPA = (TypeProduitJPA) sousTypeProduitProv.getTypeProduit();
            if (typeProduitJPA != null) {
                typeProduitStringSTP = typeProduitJPA.getTypeProduit();
            }
            sousTypeProduitString = sousTypeProduitProv.getSousTypeProduit();
        }
        Long idTypeProduit = null;
        String typeProduitString = null;
        final TypeProduitI typeProduitProv = pProduitJPA.getTypeProduit();
        if (typeProduitProv != null) {
            idTypeProduit = typeProduitProv.getIdTypeProduit();
            typeProduitString = typeProduitProv.getTypeProduit();
        }
        final String pres = String.format("idProduit : %-1s - produit : %-40s - [idSousTypeProduit : %-1s - typeProduit du sousTypeProduit : %-13s - sousTypeProduit : %-20s] - [idTypeProduit : %-1s - typeProduit : %-13s]", idProduitProv, produitString, idSousTypeProduitProv, typeProduitStringSTP, sousTypeProduitString, idTypeProduit, typeProduitString);
        stb.append(pres);
        stb.append(SAUT_DE_LIGNE);
        List<? extends ProduitI> produitsSTP = null;
        if (sousTypeProduitProv != null) {
            stb.append("**** Liste des produits du sousTypeProduitJPA : ");
            stb.append(sousTypeProduitProv.getSousTypeProduit());
            stb.append(SAUT_DE_LIGNE);
            produitsSTP = sousTypeProduitProv.getProduits();
            if (produitsSTP == null) {
                stb.append(NULL);
                stb.append(SAUT_DE_LIGNE);
            } else {
                for (final ProduitI produitProv : produitsSTP) {
                    final Long idProduitSTP = produitProv.getIdProduit();
                    final String produitStringProv = produitProv.getProduit();
                    final SousTypeProduitJPA sousTypeProduitProvSTP = (SousTypeProduitJPA) produitProv.getSousTypeProduit();
                    Long idSousTypeProduitProvSTP = null;
                    TypeProduitJPA typeProduitProvSTP = null;
                    String typeProduitStringSTPProv = null;
                    String sousTypeProduitStringSTP = null;
                    if (sousTypeProduitProvSTP != null) {
                        idSousTypeProduitProvSTP = sousTypeProduitProvSTP.getIdSousTypeProduit();
                        typeProduitProvSTP = (TypeProduitJPA) sousTypeProduitProvSTP.getTypeProduit();
                        if (typeProduitProvSTP != null) {
                            typeProduitStringSTPProv = typeProduitProvSTP.getTypeProduit();
                        }
                        sousTypeProduitStringSTP = sousTypeProduitProvSTP.getSousTypeProduit();
                    }
                    Long idTypeProduitSTP = null;
                    String typeProduitString1 = null;
                    final TypeProduitI typeProduitProv1 = pProduitJPA.getTypeProduit();
                    if (typeProduitProv1 != null) {
                        idTypeProduitSTP = typeProduitProv1.getIdTypeProduit();
                        typeProduitString1 = typeProduitProv1.getTypeProduit();
                    }
                    stb.append('\t');
                    final String pres2 = String.format("[idProduit : %-1s - produit : %-40s - [idSousTypeProduit : %-1s - typeProduit du sousTypeProduit : %-13s - sousTypeProduit : %-30s] - [idTypeProduit : %-1s - typeProduit : %-13s]]", idProduitSTP, produitStringProv, idSousTypeProduitProvSTP, typeProduitStringSTPProv, sousTypeProduitStringSTP, idTypeProduitSTP, typeProduitString1);
                    stb.append(pres2);
                    stb.append(SAUT_DE_LIGNE);
                }
            }
        }
        return stb.toString();
    }

    /**
     * <div>
     * <p>fournit une String formatée pour l'affichage à la console
     * d'une List&lt;SousTypeProduitJPA&gt;</p>
     * </div>
     *
     * @param pList : List&lt;SousTypeProduitJPA&gt;
     * @return String
     */
    private String afficherSousTypeProduitsJPA(final List<? extends SousTypeProduitI> pList) {
        if (pList == null) {
            return null;
        }
        final StringBuilder stb = new StringBuilder();
        for (final SousTypeProduitI sousTypeProduit : pList) {
            final Long idSousTypeProduit = sousTypeProduit.getIdSousTypeProduit();
            final String sousTypeProduitString = sousTypeProduit.getSousTypeProduit();
            final String typeProduit = sousTypeProduit.getTypeProduit().getTypeProduit();
            final String presentation = String.format("idSousTypeProduit : %-1s - sousTypeProduit : %-30s - typeProduit : %-20s", idSousTypeProduit, sousTypeProduitString, typeProduit);
            stb.append(presentation);
            stb.append(SAUT_DE_LIGNE);
        }
        return stb.toString();
    }

    /**
     * <div>
     * <p>fournit une String formatée pour l'affichage à la console
     * d'une List&lt;SousTypeProduit&gt;</p>
     * </div>
     *
     * @param pSousTypeProduitsDansTypeProduitDuSousTypeProduit : List&lt;SousTypeProduit&gt;
     * @return String
     */
    private String afficherSousTypeProduits(final List<? extends SousTypeProduitI> pSousTypeProduitsDansTypeProduitDuSousTypeProduit) {
        if (pSousTypeProduitsDansTypeProduitDuSousTypeProduit == null) {
            return null;
        }
        final StringBuilder stb = new StringBuilder();
        for (final SousTypeProduitI sousTypeProduit : pSousTypeProduitsDansTypeProduitDuSousTypeProduit) {
            final Long idSousTypeProduit = sousTypeProduit.getIdSousTypeProduit();
            final String sousTypeProduitString = sousTypeProduit.getSousTypeProduit();
            final String typeProduit = sousTypeProduit.getTypeProduit().getTypeProduit();
            final String presentation = String.format("idSousTypeProduit : %-1s - sousTypeProduit : %-30s - typeProduit : %-20s", idSousTypeProduit, sousTypeProduitString, typeProduit);
            stb.append(presentation);
            stb.append(SAUT_DE_LIGNE);
        }
        return stb.toString();
    }

    /**
     * <div>
     * <p>fournit une String pour l'affichage à la console
     * d'une List&lt;ProduitJPA&gt;</p>
     * </div>
     *
     * @param pList : List&lt;ProduitJPA&gt;
     * @return String
     */
    private String afficherProduitsJPA(final List<ProduitJPA> pList) {
        if (pList == null) {
            return null;
        }
        final StringBuilder stb = new StringBuilder();
        for (final ProduitJPA produit : pList) {
            if (produit != null) {
                final Long idProduit = produit.getIdProduit();
                final String produitString = produit.getProduit();
                final SousTypeProduitJPA sousTypeProduit = (SousTypeProduitJPA) produit.getSousTypeProduit();
                Long idSousProduit = null;
                String sousTypeProduitString = null;
                TypeProduitJPA typeProduitduSousTypeProduit = null;
                String typeProduitduSousTypeProduitString = null;
                if (sousTypeProduit != null) {
                    idSousProduit = sousTypeProduit.getIdSousTypeProduit();
                    sousTypeProduitString = sousTypeProduit.getSousTypeProduit();
                    typeProduitduSousTypeProduit = (TypeProduitJPA) sousTypeProduit.getTypeProduit();
                    if (typeProduitduSousTypeProduit != null) {
                        typeProduitduSousTypeProduitString = typeProduitduSousTypeProduit.getTypeProduit();
                    }
                }
                Long idTypeProduit = null;
                String typeProduitString = null;
                final TypeProduitI typeProduit = produit.getTypeProduit();
                if (typeProduit != null) {
                    idTypeProduit = typeProduit.getIdTypeProduit();
                    typeProduitString = typeProduit.getTypeProduit();
                }
                final String presentation = String.format("idProduit : %-1s - produit : %-40s - [idSousProduit : %-1s - sousTypeProduit : %-20s - typeProduit du sousTypeProduit : %-12s] - [idTypeProduit : %-1s - typeProduit : %-12s]", idProduit, produitString, idSousProduit, sousTypeProduitString, typeProduitduSousTypeProduitString, idTypeProduit, typeProduitString);
                stb.append(presentation);
                stb.append(SAUT_DE_LIGNE);
            }
        }
        return stb.toString();
    }

    /**
     * <div>
     * <p>fournit une String pour l'affichage à la console
     * d'une List&lt;Produit&gt;</p>
     * </div>
     *
     * @param pList : List&lt;Produit&gt;
     * @return String
     */
    private String afficherProduits(final List<Produit> pList) {
        if (pList == null) {
            return null;
        }
        final StringBuilder stb = new StringBuilder();
        for (final Produit produit : pList) {
            if (produit != null) {
                final Long idProduit = produit.getIdProduit();
                final String produitString = produit.getProduit();
                final SousTypeProduit sousTypeProduit = (SousTypeProduit) produit.getSousTypeProduit();
                Long idSousProduit = null;
                String sousTypeProduitString = null;
                TypeProduit typeProduitduSousTypeProduit = null;
                String typeProduitduSousTypeProduitString = null;
                if (sousTypeProduit != null) {
                    idSousProduit = sousTypeProduit.getIdSousTypeProduit();
                    sousTypeProduitString = sousTypeProduit.getSousTypeProduit();
                    typeProduitduSousTypeProduit = (TypeProduit) sousTypeProduit.getTypeProduit();
                    if (typeProduitduSousTypeProduit != null) {
                        typeProduitduSousTypeProduitString = typeProduitduSousTypeProduit.getTypeProduit();
                    }
                }
                Long idTypeProduit = null;
                String typeProduitString = null;
                final TypeProduit typeProduit = (TypeProduit) produit.getTypeProduit();
                if (typeProduit != null) {
                    idTypeProduit = typeProduit.getIdTypeProduit();
                    typeProduitString = typeProduit.getTypeProduit();
                }
                final String presentation = String.format("idProduit : %-1s - produit : %-40s - [idSousProduit : %-1s - sousTypeProduit : %-20s - typeProduit du sousTypeProduit : %-12s] - [idTypeProduit : %-1s - typeProduit : %-12s]", idProduit, produitString, idSousProduit, sousTypeProduitString, typeProduitduSousTypeProduitString, idTypeProduit, typeProduitString);
                stb.append(presentation);
                stb.append(SAUT_DE_LIGNE);
            }
        }
        return stb.toString();
    }

    /**
     * <div>
     * <p>peuple les objets métier</p>
     * </div>
     */
    private void creerScenario() {
        this.typeProduitVetement = new TypeProduit(1L, VETEMENT);
        this.typeProduitPeche = new TypeProduit(2L, "pêche");
        this.sousTypeProduitVetementHomme = new SousTypeProduit(1L, VETEMENT_POUR_HOMME, this.typeProduitVetement);
        this.sousTypeProduitVetementFemme = new SousTypeProduit(2L, "vêtement pour femme", this.typeProduitVetement);
        this.sousTypeProduitVetementEnfant = new SousTypeProduit(3L, "vêtement pour enfant", this.typeProduitVetement);
        this.sousTypeProduitPecheCanne = new SousTypeProduit(4L, "canne", this.typeProduitPeche);
        this.sousTypeProduitPecheCuiller = new SousTypeProduit(5L, "cuiller", this.typeProduitPeche);
        this.produitChemiseManchesLonguesPourHomme = new Produit(1L, "chemise à manches longues pour homme", this.sousTypeProduitVetementHomme);
        this.produitChemiseManchesCourtesPourHomme = new Produit(2L, "chemise à manches courtes pour homme", this.sousTypeProduitVetementHomme);
        this.produitSweatshirtPourHomme = new Produit(3L, "sweatshirt pour homme", this.sousTypeProduitVetementHomme);
        this.produitTeeshirtPourHomme = new Produit(4L, "teeshirt pour homme", this.sousTypeProduitVetementHomme);
    }

    // ==========================================================
    // Tests
    // ==========================================================
    /**
     * Conversion démarrant par TypeProduit :
     * graphe cohérent, instances uniques, pas de doublons
     */
    @Tag("Conversion-Beton")
    @Test
    @DisplayName("Conversion démarrant par TypeProduit : graphe cohérent, instances uniques, pas de doublons")
    public void testConversionDepuisTypeProduit() {
        final ScenarioMetier scenario = creerScenarioVetement();
        final TypeProduitJPA tpJPA = ConvertisseurMetierToJPA.typeProduitMETIERToJPA(scenario.typeProduitVetement);
        assertNotNull(tpJPA);
        assertEquals(1L, tpJPA.getIdTypeProduit());
        assertEquals(VETEMENT, tpJPA.getTypeProduit());
        final List<? extends SousTypeProduitI> stps = tpJPA.getSousTypeProduits();
        assertNotNull(stps);
        assertEquals(3, stps.size());
        assertIdentityUnique(stps, TP_STP);
        assertNoDuplicateOccurrences(stps, TP_STP);
        for (final SousTypeProduitI stp : stps) {
            assertNotNull(stp);
            assertSame(tpJPA, stp.getTypeProduit(), "Chaque STP doit référencer le même TypeProduitJPA (même instance)");
        }
        final SousTypeProduitI stpHomme = findStpByName(tpJPA, VETEMENT_POUR_HOMME);
        assertNotNull(stpHomme);
        final List<? extends ProduitI> produits = stpHomme.getProduits();
        assertNotNull(produits);
        assertEquals(4, produits.size());
        assertIdentityUnique(produits, STP_PRODUIT);
        assertNoDuplicateOccurrences(produits, STP_PRODUIT);
        for (final ProduitI p : produits) {
            assertNotNull(p);
            assertSame(stpHomme, p.getSousTypeProduit(), "Chaque Produit doit référencer le même STP (même instance)");
            assertSame(tpJPA, p.getTypeProduit(), "Chaque Produit doit remonter au même TypeProduitJPA (même instance)");
        }
        final ProduitI p1 = findProduitByName(stpHomme, CHEMISES_MANCHES_LONGUES);
        assertNotNull(p1);
        final ProduitI p1bis = findProduitByName(stpHomme, CHEMISES_MANCHES_LONGUES);
        assertSame(p1, p1bis, "Le même Produit doit être une instance unique dans le graphe (cache/identity).");
    }

    /**
     * Conversion démarrant par SousTypeProduit :
     * stabilité, parent créé + rattachement fait une seule fois.
     */
    @Tag(METIERTOJPA_BETON)
    @Test
    @DisplayName("Conversion démarrant par SousTypeProduit : stabilité, parent créé + rattachement fait une seule fois")
    public void testConversionDepuisSousTypeProduit() {
        final ScenarioMetier scenario = creerScenarioVetement();
        final SousTypeProduitJPA stpHommeJPA = ConvertisseurMetierToJPA.sousTypeProduitMETIERToJPA(scenario.stpHomme);
        assertNotNull(stpHommeJPA);
        assertEquals(1L, stpHommeJPA.getIdSousTypeProduit());
        assertEquals(VETEMENT_POUR_HOMME, stpHommeJPA.getSousTypeProduit());
        final TypeProduitJPA tpJPA = (TypeProduitJPA) stpHommeJPA.getTypeProduit();
        assertNotNull(tpJPA, "Le parent TypeProduitJPA doit être stabilisé même si on démarre par STP");
        assertEquals(VETEMENT, tpJPA.getTypeProduit());
        final List<? extends SousTypeProduitI> stps = tpJPA.getSousTypeProduits();
        assertNotNull(stps);
        assertEquals(3, stps.size());
        final SousTypeProduitI stpHommeDansListe = findStpByName(tpJPA, VETEMENT_POUR_HOMME);
        assertNotNull(stpHommeDansListe);
        assertSame(stpHommeJPA, stpHommeDansListe, "Le STP doit être l'instance unique (cache) dans le graphe.");
        assertIdentityUnique(stps, TP_STP);
        assertNoDuplicateOccurrences(stps, TP_STP);
        final List<? extends ProduitI> produits = stpHommeJPA.getProduits();
        assertNotNull(produits);
        assertEquals(4, produits.size());
        assertIdentityUnique(produits, STP_PRODUIT);
        assertNoDuplicateOccurrences(produits, STP_PRODUIT);
        for (final ProduitI p : produits) {
            assertSame(stpHommeJPA, p.getSousTypeProduit());
            assertSame(tpJPA, p.getTypeProduit());
        }
    }

    /**
     * Conversion démarrant par Produit :
     * stabilité, STP + parent créés, rattachements cohérents, pas de doublons.
     */
    @Tag(METIERTOJPA_BETON)
    @Test
    @DisplayName("Conversion démarrant par Produit : stabilité, STP + parent créés, rattachements cohérents, pas de doublons")
    public void testConversionDepuisProduit() {
        final ScenarioMetier scenario = creerScenarioVetement();
        final ProduitJPA p1JPA = ConvertisseurMetierToJPA.produitMETIERToJPA(scenario.p1);
        assertNotNull(p1JPA);
        assertEquals(1L, p1JPA.getIdProduit());
        assertEquals(CHEMISES_MANCHES_LONGUES, p1JPA.getProduit());
        final SousTypeProduitJPA stpJPA = (SousTypeProduitJPA) p1JPA.getSousTypeProduit();
        assertNotNull(stpJPA, "Le parent STP doit être créé");
        assertEquals(VETEMENT_POUR_HOMME, stpJPA.getSousTypeProduit());
        final TypeProduitJPA tpJPA = (TypeProduitJPA) stpJPA.getTypeProduit();
        assertNotNull(tpJPA, "Le parent TypeProduitJPA doit être créé/stabilisé même si on démarre par Produit");
        assertEquals(VETEMENT, tpJPA.getTypeProduit());
        final ProduitI p1DansListe = findProduitByName(stpJPA, CHEMISES_MANCHES_LONGUES);
        assertNotNull(p1DansListe);
        assertSame(p1JPA, p1DansListe, "Le Produit doit être une instance unique dans le graphe (cache/identity).");
        final SousTypeProduitI stpDansListeParent = findStpByName(tpJPA, VETEMENT_POUR_HOMME);
        assertNotNull(stpDansListeParent);
        assertSame(stpJPA, stpDansListeParent);
        final List<? extends SousTypeProduitI> stps = tpJPA.getSousTypeProduits();
        assertIdentityUnique(stps, TP_STP);
        assertNoDuplicateOccurrences(stps, TP_STP);
        final List<? extends ProduitI> produits = stpJPA.getProduits();
        assertIdentityUnique(produits, STP_PRODUIT);
        assertNoDuplicateOccurrences(produits, STP_PRODUIT);
        assertSame(stpJPA, p1JPA.getSousTypeProduit());
        assertSame(tpJPA, p1JPA.getTypeProduit());
        assertSame(tpJPA, stpJPA.getTypeProduit());
    }

    /**
     * <div>
     * <ul>
     * <p>Teste la conversion avec des collections vides (ex: sousTypeProduits = []).</p>
     * <p>Garantit que la conversion ne plante pas et retourne une liste vide.</p>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @Tag(METIERTOJPA_BETON)
    @DisplayName("Conversion avec collections vides : ne doit pas planter")
    @Test
    public final void testConversionAvecCollectionsVides() {
        final boolean affichage = false;
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("********** CLASSE ConvertisseurMetierToJPATest - méthode testConversionAvecCollectionsVides() **********");
            System.out.println("CE TEST VERIFIE QUE LA CONVERSION NE PLANTE PAS AVEC DES COLLECTIONS VIDES.");
            System.out.println();
        }
        final TypeProduit tpVide = new TypeProduit();
        tpVide.setTypeProduit("Informatique");
        tpVide.setSousTypeProduits(new ArrayList<>());
        final TypeProduitJPA tpJPA = ConvertisseurMetierToJPA.typeProduitMETIERToJPA(tpVide);
        assertThat(tpJPA).isNotNull();
        assertThat(tpJPA.getSousTypeProduits()).isEmpty();
        assertThat(tpJPA.getTypeProduit()).isEqualTo("Informatique");
    }

    
    
    /**
     * <div>
     * <ul>
     * <p>Teste la conversion avec des noms dupliqués.</p>
     * <p>Garantit que les doublons (basés sur le nom) sont éliminés conformément
     * au contrat d'unicité.</p>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @Tag(METIERTOJPA_BETON)
    @DisplayName("Conversion avec noms dupliqués : doit éliminer les doublons")
    @Test
    public final void testConversionAvecNomsDupliques() {

        // **********************************
        // AFFICHAGE DANS LE TEST ou NON
        final boolean affichage = false;
        // **********************************

        /* AFFICHAGE A LA CONSOLE. */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("********** CLASSE ConvertisseurMetierToJPATest - méthode testConversionAvecNomsDupliques() **********");
            System.out.println("CE TEST VERIFIE QUE LES DOUBLONS SONT ELIMINES CONFORMEMENT AU CONTRAT D'UNICITE.");
            System.out.println();
        }

        //**** ARRANGE - GIVEN
        final TypeProduit tp = new TypeProduit();
        tp.setTypeProduit("Vêtement");

        /* Deux SousTypeProduit avec le même nom (doublon). */
        final SousTypeProduit stp1 = new SousTypeProduit();
        stp1.setSousTypeProduit(VETEMENT_POUR_HOMME);
        stp1.setTypeProduit(tp);

        final SousTypeProduit stp2 = new SousTypeProduit();
        stp2.setSousTypeProduit(VETEMENT_POUR_HOMME);
        stp2.setTypeProduit(tp);

        /* Ajout des deux instances (seule la première sera conservée). */
        final List<SousTypeProduit> sousTypeProduits = new ArrayList<>();
        sousTypeProduits.add(stp1);
        sousTypeProduits.add(stp2);

        /* Utilisation d'une copie modifiable 
         * pour éviter ConcurrentModificationException. */
        tp.setSousTypeProduits(Collections.unmodifiableList(
        		new ArrayList<>(sousTypeProduits)));

        //**** ACT - WHEN
        final TypeProduitJPA tpJPA 
        	= ConvertisseurMetierToJPA.typeProduitMETIERToJPA(tp);

        //**** ASSERT - THEN
        /* Vérifie que seul un exemplaire est conservé (unicité). */
        assertThat(tpJPA.getSousTypeProduits()).hasSize(1);

        /* Vérifie que le nom est correct. */
        assertThat(tpJPA.getSousTypeProduits().get(0).getSousTypeProduit())
            .isEqualTo(VETEMENT_POUR_HOMME);
    } //___________________________________________________________________

    
    
    /**
     * <div>
     * <ul>
     * <p>Teste les performances de conversion pour de grandes collections.</p>
     * <p>Garantit que la conversion reste performante même 
     * avec un grand nombre d'éléments.</p>
     * </ul>
     * </div>
     */
    @SuppressWarnings(UNUSED)
    @Tag("MetierToJPA-Beton")
    @DisplayName("Performance : conversion de 1000 SousTypeProduit")
    @Test
    public final void testPerformanceGrandeCollection() {

        // **********************************
        // AFFICHAGE DANS LE TEST ou NON
        final boolean affichage = false;
        // **********************************

        /* AFFICHAGE A LA CONSOLE. */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("********** CLASSE ConvertisseurMetierToJPATest - méthode testPerformanceGrandeCollection() **********");
            System.out.println("CE TEST VERIFIE LES PERFORMANCES DE CONVERSION POUR DE GRANDES COLLECTIONS.");
            System.out.println();
        }

        //**** ARRANGE - GIVEN
        final TypeProduit tp = new TypeProduit();
        tp.setTypeProduit("Électronique");

        /* Construction d'une liste de 1000 SousTypeProduit. */
        final List<SousTypeProduit> sousTypeProduits = new ArrayList<>(1000);
        for (int i = 0; i < 1000; i++) {
            final SousTypeProduit stp = new SousTypeProduit();
            stp.setSousTypeProduit("Produit " + i);
            stp.setTypeProduit(tp);
            sousTypeProduits.add(stp);
        }

        /* Utilisation d'une copie modifiable pour 
         * éviter ConcurrentModificationException. */
        tp.setSousTypeProduits(Collections.unmodifiableList(
        		new ArrayList<>(sousTypeProduits)));

        //**** ACT - WHEN
        final long start = System.currentTimeMillis();
        final TypeProduitJPA tpJPA 
        	= ConvertisseurMetierToJPA.typeProduitMETIERToJPA(tp);
        final long end = System.currentTimeMillis();

        /* AFFICHAGE A LA CONSOLE. */
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println("Temps de conversion : " + (end - start) + " ms");
            System.out.println("Nombre de SousTypeProduitJPA convertis : " 
            		+ tpJPA.getSousTypeProduits().size());
        }

        //**** ASSERT - THEN
        assertThat(tpJPA).isNotNull();
        assertThat(tpJPA.getSousTypeProduits()).hasSize(1000);
        assertThat(end - start).as("Le temps de conversion doit être raisonnable")
                               .isLessThan(1000L); // Seuil arbitraire (ajustable)
        
    } //___________________________________________________________________

    
    
    /**
     * <div>
     * <ul>
     * <p>Teste la robustesse du cache en cas d'accès concurrents.</p>
     * <p>Garantit que le convertisseur gère correctement les conversions
     * simultanées sans conflits.</p>
     * </ul>
     * </div>
     */
    @SuppressWarnings("unused")
    @Tag("MetierToJPA-Beton")
    @DisplayName("Concurrences : doit gérer les conversions multi-thread")
    @Test
    public final void testConcurrencesDansLesMaps() {
        final boolean affichage = false;
        if (AFFICHAGE_GENERAL && affichage) {
            System.out.println();
            System.out.println("********** CLASSE ConvertisseurMetierToJPATest - méthode testConcurrencesDansLesMaps() **********");
            System.out.println("CE TEST VERIFIE LA ROBUSTESSE DU CONVERTISSEUR EN CAS D'ACCES CONCURRENTS.");
            System.out.println();
        }
        final TypeProduit tp = new TypeProduit();
        tp.setIdTypeProduit(1L);
        tp.setTypeProduit("Test Concurrence");
        final TypeProduitJPA[] results = new TypeProduitJPA[10];
        final int nombreThreads = 10;
        final Thread[] threads = new Thread[nombreThreads];
        final TypeProduitJPA reference = ConvertisseurMetierToJPA.typeProduitMETIERToJPA(tp);
        for (int i = 0; i < nombreThreads; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                results[index] = ConvertisseurMetierToJPA.typeProduitMETIERToJPA(tp);
            });
            threads[i].start();
        }
        for (final Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                fail("Thread interrupted during test");
            }
        }
        for (int i = 0; i < results.length; i++) {
            assertNotNull(results[i], "Aucun résultat ne doit être null");
            assertEquals(reference.getIdTypeProduit(), results[i].getIdTypeProduit(), "Les ID doivent être identiques");
            assertEquals(reference.getTypeProduit(), results[i].getTypeProduit(), "Les noms doivent être identiques");
        }
    } //___________________________________________________________________

    
    
    // ==========================================================
    // Helpers - Scénario Métier
    // ==========================================================
    /**
     * <style>p, ul, li, h1 {line-height : 1em;}</style>
     * <style>h1 {text-decoration: underline;}</style>
     * 
     * <div>
     * </div>
     *
     *
     * @author Daniel Lévy
     * @version 1.0
     * @since 7 févr. 2026
     */
    private static final class ScenarioMetier {
    	
        /**
         * 
         */
        private TypeProduit typeProduitVetement;
        
        /**
         * 
         */
        private SousTypeProduit stpHomme;
        
        /**
         * 
         */
        private SousTypeProduit stpFemme;
        
        /**
         * 
         */
        private SousTypeProduit stpEnfant;
        
       
        /**
         * 
         */
        private Produit p1;
                
        /**
         * 
         */
        private Produit p2;
             
        /**
         * 
         */
        private Produit p3;
        
        /**
         * 
         */
        private Produit p4;
    }

    private ScenarioMetier creerScenarioVetement() {
        final ScenarioMetier s = new ScenarioMetier();
        s.typeProduitVetement = new TypeProduit(VETEMENT);
        s.typeProduitVetement.setIdTypeProduit(1L);
        s.stpHomme = new SousTypeProduit(VETEMENT_POUR_HOMME);
        s.stpHomme.setIdSousTypeProduit(1L);
        s.stpHomme.setTypeProduit(s.typeProduitVetement);
        s.stpFemme = new SousTypeProduit("vêtement pour femme");
        s.stpFemme.setIdSousTypeProduit(2L);
        s.stpFemme.setTypeProduit(s.typeProduitVetement);
        s.stpEnfant = new SousTypeProduit("vêtement pour enfant");
        s.stpEnfant.setIdSousTypeProduit(3L);
        s.stpEnfant.setTypeProduit(s.typeProduitVetement);
        s.p1 = new Produit(CHEMISES_MANCHES_LONGUES);
        s.p1.setIdProduit(1L);
        s.p1.setSousTypeProduit(s.stpHomme);
        s.p2 = new Produit("chemise manches courtes");
        s.p2.setIdProduit(2L);
        s.p2.setSousTypeProduit(s.stpHomme);
        s.p3 = new Produit("sweatshirt");
        s.p3.setIdProduit(3L);
        s.p3.setSousTypeProduit(s.stpHomme);
        s.p4 = new Produit("tee-shirt");
        s.p4.setIdProduit(4L);
        s.p4.setSousTypeProduit(s.stpHomme);
        return s;
    }

    // ==========================================================
    // Helpers - Recherche dans le graphe JPA
    // ==========================================================
    
    /**
     *  .
     *
     * @param tpJPA
     * @param name
     * @return
     */
    private static SousTypeProduitI findStpByName(final TypeProduitJPA tpJPA, final String name) {
        for (final SousTypeProduitI stp : tpJPA.getSousTypeProduits()) {
            if (stp != null && name.equals(stp.getSousTypeProduit())) {
                return stp;
            }
        }
        return null;
    }

    
    
    /**
     * <div>
     * <p>Recherche un ProduitI par son nom dans une liste de produits
     * d'un SousTypeProduitI (version générique pour objets métier ou JPA).</p>
     * </div>
     *
     * @param stp : SousTypeProduitI (métier ou JPA)
     * @param name : String (nom du produit recherché)
     * @return ProduitI (trouvé ou null)
     */
    private static ProduitI findProduitByName(
            final SousTypeProduitI stp,
            final String name) {
        if (stp == null || stp.getProduits() == null) {
            return null;
        }
        for (final ProduitI p : stp.getProduits()) {
            if (p != null && name.equals(p.getProduit())) {
                return p;
            }
        }
        return null;
    }
    
    
    
    /**
     *  .
     *
     * @param stpJPA
     * @param name
     * @return
     */
    private static ProduitI findProduitByName(final SousTypeProduitJPA stpJPA, final String name) {
        for (final ProduitI p : stpJPA.getProduits()) {
            if (p != null && name.equals(p.getProduit())) {
                return p;
            }
        }
        return null;
    }

    
    
    // ==========================================================
    // Helpers - Assertions "béton"
    // ==========================================================
    
    /**
     *  .
     *
     * @param list
     * @param label
     */
    private static void assertIdentityUnique(final List<?> list, final String label) {
        assertNotNull(list, label + " : liste null");
        final Set<Object> identitySet = Collections.newSetFromMap(new IdentityHashMap<>());
        for (final Object o : list) {
            assertNotNull(o, label + " : élément null");
            final boolean added = identitySet.add(o);
            assertTrue(added, label + " : doublon d'instance (même référence) détecté");
        }
    }

    
    /**
     *  .
     *
     * @param list
     * @param label
     */
    private static void assertNoDuplicateOccurrences(final List<?> list, final String label) {
        assertNotNull(list, label + " : liste null");
        for (int i = 0; i < list.size(); i++) {
            final Object a = list.get(i);
            for (int j = i + 1; j < list.size(); j++) {
                final Object b = list.get(j);
                assertNotSame(a, b, label + " : double rattachement détecté aux indices " + i + " et " + j);
            }
        }
    }

    // ==========================================================
    // Helpers Reflection
    // ==========================================================
    
    
    /**
     *  .
     *
     * @param target
     * @param fieldName
     * @param value
     * @throws Exception
     */
    private static void setField(final Object target, final String fieldName, final Object value) throws Exception {
        final Field f = findField(target.getClass(), fieldName);
        f.setAccessible(true); // NOPMD by danyl on 07/02/2026 18:14
        f.set(target, value);
    }

    
    
    /**
     *  .
     *
     * @param type
     * @param fieldName
     * @return
     * @throws Exception
     */
    private static Field findField(final Class<?> type, final String fieldName) throws Exception {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (final NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }
    

    
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * convertit une List&lt;ProduitI&gt; en List&lt;Produit&gt;.</p>
	 * <ul>
	 * <li>Si pList == null : return null.</li>
	 * <li>Elimine tout null dans la liste réponse.</li>
	 * <li>Cast les ProduitI en Produit.</li>
	 * <li>Elimine les cas de mauvaise instance.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pList : List&lt;ProduitI&gt; 
	 * @return List&lt;Produit&gt;
	 */
	private static List<Produit> convertirListProduitEnListProduit(
			final List<ProduitI> pList) {
		
		/* Si pList == null : return null. */
		if (pList == null) {
			return null;
		}

		final List<Produit> reponse = new ArrayList<Produit>();

		for (final ProduitI produitI : pList) {

			final Produit p;

			/* Elimine tout null dans la liste réponse. */
			if (produitI == null) {
				continue;
			}

			if (produitI instanceof Produit other) {

				/* Cast les ProduitI en Produit. */
				other = (Produit) produitI;
				reponse.add(other);

			} else {

				/* Elimine les cas de mauvaise instance. */
				continue;

			}

		}
		
		return reponse;
	}

}
