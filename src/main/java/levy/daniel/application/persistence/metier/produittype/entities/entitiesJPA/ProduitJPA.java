/* ********************************************************************* */
/* ************************** ENTITY JPA ******************************* */
/* ********************************************************************* */
package levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA;

import java.io.Serializable;

import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import levy.daniel.application.model.metier.produittype.CloneContext;
import levy.daniel.application.model.metier.produittype.ProduitI;
import levy.daniel.application.model.metier.produittype.SousTypeProduitI;
import levy.daniel.application.model.metier.produittype.TypeProduitI;


/**
 * <style>p, ul, li {line-height : 1em;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">CLASSE ProduitJPA :</p>
 * <p>modélise une entity <span style="font-weight:bold;">JPA</span> 
 * à stocker dans une base de données relationnelle (type PostgeSQL) 
 * pour l'objet métier <span style="font-weight:bold;">Produit</span> 
 * du Package <code style="font-weight:bold;">
 * levy.daniel.application.model.metier.produittype</code>
 *  comme par exemple : </p>
 * <ul>
 * <li>le produit "chemise à manches longues pour homme" 
 * pour le sous-produit "vêtement pour homme".</li>
 * <li>le produit "chemise à manches courtes pour homme" 
 * pour le sous-produit "vêtement pour homme".</li>
 * <li>le produit "sweat-shirt pour homme" 
 * pour le sous-produit "vêtement pour homme"</li>
 * </ul>
 * </div>
 * 
 * <div>
 * 
 * <div>
 * <p>Dans ce modèle de PRODUIT TYPE, un TypeProduitJPA comme "vêtement" se décline en SousTypeProduitJPA comme :</p>
 * <ul>
 * <li>"vêtement pour homme"</li>
 * <li>"vêtement pour femme"</li>
 * <li>"vêtement pour enfant"</li>
 * </ul>
 * </div>
 * <div>
 * <p>un SousTypeProduitJPA comme "vêtement pour homme" qualifie des ProduitJPA comme : </p>
 * <ul>
 * <li>"chemise à manches longues pour homme"</li>
 * <li>"chemise à manches courtes pour homme"</li>
 * <li>"tee-shirt pour homme"</li>
 * </ul>
 * <p>Il y a donc 3 Classes : 
 * <ol>
 * <li>TypeProduitJPA,</li> 
 * <li>SousTypeProduitJPA,</li> 
 * <li>ProduitJPA</li>
 * </ol>
 * pour définir une PRODUIT TYPE</p>
 * </div>
 * 
 * 
 * <div>
 * <p style="text-decoration: underline;font-weight:bold;padding : 20px;">
 * Diagramme de Classes du ProduitJPA qualifié par un SousTypeProduitJPA qui est lui-même une déclinaison d'un TypeProduitJPA</p>
 * <p>
 * <img src="../../../../../../../../../../../javadoc/images/model/metier/produittype/diagramme_de_classes_produit_typé.jpg" 
 * alt="architecture applicative" border="1" align="center" height= 300px />
 * </p>
 * </div>
 * 
 * <div>
 * <p style="text-decoration: underline;font-weight:bold;">
 * TYPE DE PRODUIT TypeProduit qui qualifie un SOUS-TYPE DE PRODUIT SousTypeProduit</p>
 * <table>
 * <tr>
 * <td>
 * <img src="../../../../../../../../../../../javadoc/images/model/metier/produittype/type_produit.jpg" 
 * alt="architecture applicative" border="1" align="center" height= 300px />
 * </td>
 * <td>
 * <img src="../../../../../../../../../../../javadoc/images/model/metier/produittype/type_produit-sous_type_produit.jpg" 
 * alt="architecture applicative" border="1" align="center" height= 300px />
 * </td>
 * </tr>
 * </table>
 * </div>
 * 
 * 
 * <div>
 * <p style="text-decoration: underline;font-weight:bold;">
 * Notion de PRODUIT qui précise un Sous-Type de Produit</p>
 * <p>
 * <img src="../../../../../../../../../../../javadoc/images/model/metier/produittype/produit.jpg" 
 * alt="architecture applicative" border="1" align="center" height= 300px />
 * </p>
 * </div>
 * 
 * <div>
 * <p style="text-decoration: underline;font-weight:bold;padding : 0px;">Exemple d'utilisation : </p>
 * <p><code>// Instanciation d'un TypeProduitJPA</code></p>
 * <p><code>final TypeProduitJPA typeProduitVetement = new TypeProduitJPA("vêtement");</code></p>
 * <p><code>// Instanciation d'un SousTypeProduitJPA (qui a pour TypeProduitJPA typeProduitVetement)</code></p>
 * <p><code>final SousTypeProduitJPA sousTypeProduitVetementPourHomme = new SousTypeProduitJPA("vêtement pour homme", typeProduitVetement, null);</code></p>
 * <p><code>// Instanciation d'un ProduitJPA (qui a pour SousTypeProduitJPA sousTypeProduitVetementPourHomme)</code></p>
 * <p><code>final ProduitJPA produitChemisePourHomme = new ProduitJPA("chemise manches longues pour homme", sousTypeProduitVetementPourHomme);</code></p>
 * </div>
 * 
 * </div>
 * 
 * 
 * 
 * @author Daniel LEVY
 * @version 1.0
 * @created 06 décembre 2025 20:59:31
 */
@Entity(name = "ProduitJPA")
@Access(AccessType.FIELD)
@Table(name = "PRODUITS", schema = "PUBLIC"
, indexes = {@Index(name = "INDEX_PRODUIT_SOUS_TYPE_PRODUIT"
, columnList = "PRODUIT ASC, SOUS_TYPE_PRODUIT ASC", unique = true)})
public class ProduitJPA implements ProduitI, Cloneable, Serializable {

	// ************************ATTRIBUTS************************************/
	
	/**
	 * 1L.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * "null"
	 */
	public static final String NULL = "null";

	
	/**
	 * ", "
	 */
	public static final String VIRGULE_ESPACE = ", ";

	
	/**
	 * ']'
	 */
	public static final char CROCHET_FERMANT = ']';

	
	/**
	 * ';'
	 */
	public static final char POINT_VIRGULE = ';';
	
	/**
	 * "le SousTypeProduit passé en paramètre 
	 * n'est pas de type Entity JPA : "
	 */
	public static final String MAUVAISE_INSTANCE_ENFANT_JPA 
		= "le SousTypeProduit passé en paramètre "
				+ "n'est pas de type Entity JPA : ";

	
	/* ----------------------------------------------------------------- */

	
	/**
	 * <div>
	 * <p>ID en base du produit.</p>
	 * </div>
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_PRODUIT")
	private Long idProduit;

	
	/**
	 * <div>
	 * <p>Nom du produit comme par exemple :</p>
	 * <ul>
     * <li>le produit "chemise à manches longues pour homme" 
     * pour le sous-produit "vêtement pour homme".</li>
     * <li>le produit "chemise à manches courtes pour homme" 
     * pour le sous-produit "vêtement pour homme".</li>
     * <li>le produit "sweat-shirt pour homme" 
     * pour le sous-produit "vêtement pour homme"</li>
     * </ul>
     * </div>
	 */
	@Column(name = "PRODUIT"
			, unique = false, updatable = true
			, insertable = true, nullable = false)
	private String produit;
	
	
	/**
	 * <div>
	 * <p>sous-type de produit qui caractérise le présent produit.</p>
	 * <p>par exemple : "vêtement pour homme" pour un PRODUIT 
	 * "tee-shirt pour homme".</p>
	 * <p>ATTENTION : visibilité interface.</p>
	 * </div>
	 */
	@ManyToOne(fetch = FetchType.LAZY
	, optional = false
	, targetEntity = SousTypeProduitJPA.class)
	@JoinColumn(name = "SOUS_TYPE_PRODUIT"
	, nullable = false
	, referencedColumnName = "ID_SOUS_TYPE_PRODUIT"
	, foreignKey = @ForeignKey(name="FK_SOUS_TYPE_PRODUIT"))
	private SousTypeProduitI sousTypeProduit;
	
	
	/**
	 * <p>boolean qui indique si le présent Produit 
	 * possède un SousTypeProduit non null.</p>
	 * <ul>
	 * <li>true si le présent Produit possède un SousTypeProduit non null.</li>
	 * </ul>
	 * <p>Doit être calculé et jamais serializé.</p>
	 */
	private transient boolean valide;
	
	/* ----------------------------------------------------------------- */
	
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
	private static final Logger LOG = LogManager.getLogger(ProduitJPA.class);
	
/* ===============================METHODES ============================ */

	
	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public ProduitJPA() {
		this(null, null, null);
	}
	
	
	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 1.</p>
	 * </div>
	 *
	 * @param pProduit : String : Nom du produit.
	 */
	public ProduitJPA(final String pProduit) {
		this(null, pProduit, null);
	}

	
	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 2.</p>
	 * </div>
	 *
	 * @param pProduit : String : Nom du produit.
	 * @param pSousTypeProduit : SousTypeProduitI : 
	 * sous-type de produit qui caractérise le présent produit.
	 */
	public ProduitJPA(final String pProduit
			, final SousTypeProduitI pSousTypeProduit) {
		this(null, pProduit, pSousTypeProduit);
	}
	
	
	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR COMPLET.</p>
	 * <ul>
	 * <li>passe <code>pIdProduit</code> 
	 * à <code style="font-weight:bold;">this.idProduit</code></li>
	 * <li>passe <code>pProduit</code> 
	 * à <code style="font-weight:bold;">this.produit</code></li>
	 * <li>appelle le Setter intelligent <code style="font-weight:bold;">
	 * this.setSousTypeProduit(pSousTypeProduit)</code> qui : </li>
	 * <ul>
	 * <li>détache le présent Produit de l’ancien parent SousTypeProduit 
	 * et le <span style="font-weight:bold;">
	 * retire de sa liste produits</span>.</li>
	 * <li>passe  <code>pSousTypeProduit</code> à 
	 * <code style="font-weight:bold;">this.sousTypeProduit</code> 
	 * (le SousTypeProduit du présent Produit).</li>
	 * <li>rattache le présent produit au nouveau parent 
	 * et <span style="font-weight:bold;">
	 * l'ajoute à sa liste produits</span>.</li>
	 * <li>passe this.valide à true si this.sousTypeProduit 
	 * n'est pas null.</li>
	 * </ul>
	 * </ul>
	 * </div>
	 *
	 * @param pIdProduit : Long : ID en base du produit. 
	 * @param pProduit : String : Nom du produit.
	 * @param pSousTypeProduit : SousTypeProduitI : 
	 * sous-type de produit qui caractérise le présent produit.
	 */
	public ProduitJPA(final Long pIdProduit
			, final String pProduit
			, final SousTypeProduitI pSousTypeProduit) {
				
		super();
		
		this.idProduit = pIdProduit;
        this.produit = normalize(pProduit);
        
        this.setSousTypeProduit(pSousTypeProduit);

	}

	
	
	/**
	* {@inheritDoc}
	* <div>
	* <p style="font-weight:bold;">proxy-safe</p>
	* <p style="font-weight:bold;">hashCode() sur :</p>
	* <ol>
	* <li style="font-weight:bold;">idProduit</li>
	* <li style="font-weight:bold;">idSousTypeProduit</li>
	* <li style="font-weight:bold;">produit (insensible à la casse)</li>
	* </ol>
	* </div>
	*/
	@Override
	public final int hashCode() {

		/* =========================
		 * STRATÉGIE ID-FIRST
		 * =========================
		 */
		final Long thisId = this.getIdProduit();

		if (thisId != null) {
			return thisId.hashCode();
		}

		/* =========================
		 * FALLBACK SUR ID PARENT
		 * =========================
		 */
		final SousTypeProduitI stp = this.getSousTypeProduit();
		final Long stpId = (stp != null)
				? stp.getIdSousTypeProduit()
				: null;

		final String libelle = this.getProduit();
		final String libelleLower = (libelle != null)
				? libelle.toLowerCase(java.util.Locale.ROOT)
				: null;

		if (stpId != null) {
			return java.util.Objects.hash(libelleLower, stpId);
		}

		/* =========================
		 * FALLBACK FINAL MÉTIER
		 * =========================
		 */
		return java.util.Objects.hash(libelleLower, stp);
	}



	/**
	* {@inheritDoc}
	* <div>
	* <p style="font-weight:bold;">proxy-safe</p>
	* <p style="font-weight:bold;">equals() sur :</p>
	* <ol>
	* <li style="font-weight:bold;">idProduit</li>
	* <li style="font-weight:bold;">idSousTypeProduit</li>
	* <li style="font-weight:bold;">produit (insensible à la casse)</li>
	* </ol>
	* </div>
	*/
	@Override
	public final boolean equals(final Object pObject) {

		/* Même instance. */
		if (this == pObject) {
			return true;
		}

		/* Mauvaise instance. */
		if (!(pObject instanceof ProduitJPA other)) {
			return false;
		}

		/* =========================
		 * STRATÉGIE ID-FIRST
		 * =========================
		 */
		final Long thisId = this.getIdProduit();
		final Long otherId = other.getIdProduit();

		if (thisId != null && otherId != null) {
			return thisId.equals(otherId);
		}

		/* =========================
		 * FALLBACK SUR ID PARENT
		 * =========================
		 */
		final SousTypeProduitI thisStp = this.getSousTypeProduit();
		final SousTypeProduitI otherStp = other.getSousTypeProduit();

		final Long thisStpId = (thisStp != null)
				? thisStp.getIdSousTypeProduit()
				: null;

		final Long otherStpId = (otherStp != null)
				? otherStp.getIdSousTypeProduit()
				: null;

		final String thisLib = this.getProduit();
		final String otherLib = other.getProduit();

		if (thisStpId != null && otherStpId != null) {

			return thisStpId.equals(otherStpId)
					&& (thisLib == null
							? otherLib == null
							: thisLib.equalsIgnoreCase(otherLib));
		}

		/* =========================
		 * FALLBACK FINAL MÉTIER
		 * =========================
		 */
		final boolean libEquals =
				(thisLib == null)
						? otherLib == null
						: thisLib.equalsIgnoreCase(otherLib);

		return libEquals
				&& java.util.Objects.equals(thisStp, otherStp);
	}



	/**
	* {@inheritDoc}
	*/
	@Override
	public final String toString() {

		final StringBuilder builder = new StringBuilder();
		
		builder.append("Produit [");

		builder.append("idProduit=");
		
		if (this.getIdProduit() != null) {			
			builder.append(this.getIdProduit());			
		} else {
			builder.append(NULL);
		}
		
		builder.append(VIRGULE_ESPACE);

		builder.append("produit=");
		
		if (this.getProduit() != null) {			
			builder.append(this.getProduit());
		} else {
			builder.append(NULL);
		}
		
		builder.append(VIRGULE_ESPACE);

		builder.append("sousTypeProduit=");
		
		if (this.getSousTypeProduit() != null) {			
			builder.append(this.getSousTypeProduit().toString());
		} else {
			builder.append(NULL);
		}

		builder.append(CROCHET_FERMANT);
		
		return builder.toString();

	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public String afficherProduit() {
		
		if (this.getProduit() == null) {
			return NULL;
		}
		
		return this.getProduit();
	}



	/**
	 * {@inheritDoc}
	 * <div>
	 * <ol>
	 * <p style="font-weight:bold;">
	 * Classe dans l'ordre alphabétique de :</p>
	 * <li style="font-weight:bold;">produit</li>
	 * </ol>
	 * <p style="font-weight:bold;">ATTENTION :
	 * Strings.CI.compare(a, b) place les vides avant tout texte.</p>
	 * </div>
	 */
	@Override
	public final int compareTo(final ProduitI pObject) {

		/* Comparaison de la même instance retourne toujours 0. */
		if (this == pObject) {
			return 0;
		}

		/* Comparaison avec null retourne toujours < 0. */
		if (pObject == null) {
			return -1;
		}

		/*
		 * Entity JPA : stratégie "minimum de verrous".
		 * On compare via une méthode dédiée, 
		 * en privilégiant l'accès direct
		 * aux champs quand l'objet comparé 
		 * est de même type (pattern matching),
		 * sinon via l'interface.
		 */
		return this.compareFields(pObject);
	}


	
	/**
	 * <p style="font-weight:bold;">
	 * Compare les champs en cohérence avec TypeProduitJPA 
	 * et SousTypeProduitJPA
	 * en utilisant le pattern matching Java 21.
	 * </p>
	 *
	 * @param pObject : ProduitI :
	 * L'objet à comparer avec this.
	 * @return Le résultat de la comparaison.
	 */
	private int compareFields(final ProduitI pObject) {

		/*
		 * Accès direct au champ produit du présent objet.
		 * On ne passe pas par le getter pour rester au plus proche
		 * de la donnée portée par l'Entity.
		 */
		final String a = this.produit;

		/*
		 * Récupération de la valeur "produit" de l'objet comparé :
		 * - si ProduitJPA, accès direct au champ (pattern matching)
		 * - sinon, via l'interface ProduitI.
		 */
		final String b;
		if (pObject instanceof ProduitJPA other) {
			b = other.produit;
		} else {
			b = pObject.getProduit();
		}

		/*
		 * Gestion des cas null :
		 * - Si a est null et b est null, 
		 * les objets sont égaux (retourne 0).
		 * - Si a est null et b n'est pas null, 
		 * a est considéré comme "après" b (retourne +1).
		 * - Si a n'est pas null et b est null, 
		 * a est considéré comme "avant" b (retourne -1).
		 */
		if (a == null) {
			return (b == null) ? 0 : +1;
		}

		if (b == null) {
			return -1;
		}

		/*
		 * Comparaison case-insensitive des chaînes de caractères.
		 * Strings.CI.compare() place les chaînes vides avant les autres.
		 */
		return Strings.CI.compare(a, b);
	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public final ProduitJPA clone() throws CloneNotSupportedException {
		return this.cloneDeep();
	}
	
	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Instancie un {@link CloneContext} et appelle 
	 * {@code deepClone(ctxt)} en lui passant le CloneContext.</p>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">INTENTION TECHNIQUE
	 * (scénario nominal) :</p>
	 * <ul>
	 * <li>Appeler {@code deepClone(ctxt)} 
	 * en lui passant un nouveau {@link CloneContext}.</li>
	 * </ul>
	 * </div>
	 * 
	 * <div>
	 * <p style="font-weight:bold;">CONTRAT TECHNIQUE :</p>
	 * <ul>
	 * <li>méthode appelée par {@code clone()}.</li>
	 * <li>méthode private interne invisible.</li>
	 * </ul>
	 * </div>
	 *
	 * @return ProduitJPA : clone profond.
	 */
	private ProduitJPA cloneDeep() {
		return deepClone(new CloneContext());	
	}



	/**
	* {@inheritDoc}
	*/
	@Override
	public final ProduitJPA deepClone(final CloneContext ctx) {

		/* 
		 * vérifie que le clone n'existe pas déjà dans l'IdentityHashMap 
		 * du CloneContext. 
		 * Le cas échéant, retourne le clone déjà existant. 
		 */
		final ProduitJPA existing = ctx.get(this);
		if (existing != null) {
			return existing;
		}

		// CLONAGE DU Produit.
	    /* instancie un clone "nu" cloneP sans parent.*/
		final ProduitJPA cloneP = this.cloneWithoutParent();
		
		/* rajoute le clone "nu" cloneP dans le cache du CloneContext. */
		ctx.put(this, cloneP);

		// CLONAGE DU PARENT (qui clonera aussi TypeProduit si besoin)		
		final SousTypeProduitI stpI = this.getSousTypeProduit();

		if (stpI != null) {

			final SousTypeProduitI cloneSTP = stpI.deepClone(ctx);
			
			cloneP.setSousTypeProduit(cloneSTP);

		}

		return cloneP;
	}



	/**
	* {@inheritDoc}
	*/
	@Override
	public final ProduitJPA cloneWithoutParent() {

		/* clone un ProduitI sans "parent" SousTypeProduitI. */
		final ProduitJPA clone = new ProduitJPA();
		
		clone.idProduit = this.idProduit;
		clone.produit = this.produit;
		clone.sousTypeProduit = null;
		
		clone.recalculerValide();
		
		return clone;
	}
	

	
	/**
	 * <div>
	 * <p>Recalcule le Boolean <b>valide</b> 
	 * en fonction de l'état courant.</p>
	 * <ul>
	 * <li>valide == true si :</li>
	 * <li style="margin-left:20px;">produit != null</li>
	 * <li style="margin-left:20px;">sousTypeProduit != null</li>
	 * <li>sinon valide == false.</li>
	 * </ul>
	 * </div>
	 */
	private void recalculerValide() {

		/* Un ProduitJPA est valide si son libellé est non null
		 * et s'il est rattaché à un SousTypeProduit. */
		this.valide = this.produit != null
				&& this.sousTypeProduit != null;

	}


	
	/**
	 * <div>
	 * <p>Normalise une chaîne :</p>
	 * <ul>
	 * <li>retourne null si pString est null.</li>
	 * <li>trim().</li>
	 * <li>retourne null si la chaîne est vide après trim.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pString : String à normaliser
	 * @return String normalisée
	 */
	private String normalize(final String pString) {

		/* Gestion du null. */
		if (pString == null) {
			return null;
		}

		/* Suppression des espaces en début et fin. */
		final String trimmed = pString.trim();

		/* Chaîne vide -> null. */
		if (trimmed.isEmpty()) {
			return null;
		}

		return trimmed;
	}

	
	
	/**
	 * {@inheritDoc}
	 * <div>
	 * <p style="font-weight:bold;">
	 * retourne une String pour afficher 
	 * l'en-tête d'un Produit en csv.</p>
	 * </div>
	 *
	 * @return String : 
	 * "idproduit;type de produit;sous-type de produit;produit;"
	 */
	@Transient
	@Override
	public final String getEnTeteCsv() {
		return "idproduit;type de produit;sous-type de produit;produit;";
	}


	
	/**
	 * {@inheritDoc}
	 * <div>
	 * <p style="font-weight:bold;">retourne une String pour 
	 * l'affichage d'un PRODUIT 
	 * sous forme de csv avec des séparateurs point-virgule ';'.</p>
	 * <p>Le csv retourné respecte l'ordre indiqué dans l'en-tête : 
	 * "idproduit;type de produit;sous-type de produit;produit;"</p>
	 * <ul>
	 * <p>par exemple : </p>
	 * <li>idproduit;type de produit;sous-type de produit;produit;</li>
	 * <li>null;vêtement;vêtement pour homme;chemise manches longues;</li>
	 * </ul>
	 * </div>
	 *
	 * @return String : "this.idProduit;typeProduit;sous-type de produit;produit;"
	 */
	@Override
	public final String toStringCsv() {
		
		final StringBuilder builder = new StringBuilder();
		
		/* idProduit */
		if (this.getIdProduit() != null) {			
			builder.append(this.getIdProduit());			
		} else {
			builder.append(NULL);
		}
		
		builder.append(POINT_VIRGULE);
		
		/* type de produit */
		if (this.getTypeProduit() == null) {
			builder.append(NULL);
		} else if (this.getTypeProduit().getTypeProduit() == null){
			builder.append(NULL);
		} else {
			builder.append(this.getTypeProduit().getTypeProduit());
		}
		
		builder.append(POINT_VIRGULE);
		
		/* sous-type de produit */
		if (this.getSousTypeProduit() == null) {
			builder.append(NULL);
		} else if (this.getSousTypeProduit().getSousTypeProduit() == null) {
			builder.append(NULL);
		} else {
			builder.append(this.getSousTypeProduit().getSousTypeProduit());
		}
		
		builder.append(POINT_VIRGULE);
		
		/* produit */
		if (this.getProduit() == null) {
			builder.append(NULL);
		} else {
			builder.append(this.getProduit());
		}
		
		builder.append(POINT_VIRGULE);
		
		return builder.toString();
		
	}



	/**
	 * {@inheritDoc}
	 * <div>
	 * <p style="font-weight:bold;">
	 * en-tête Jtable pour un ProduitJPA</b> :</p>
	 * <p>"idproduit;type de produit;sous-type de produit;produit;".</p>
	 * </div>
	 */
	@Transient
	@Override
	public final String getEnTeteColonne(
			final int pI) {

		String entete = null;

		switch (pI) {

		case 0:
			entete = "idproduit";
			break;
			
		case 1:
			entete = "type de produit";
			break;
			
		case 2:
			entete = "sous-type de produit";
			break;
			
		case 3:
			entete = "produit";
			break;
			
		default:
			entete = "invalide";
			break;

		} // Fin du Switch._________________________________

		return entete;

	} // Fin de getEnTeteColonne(...)._____________________________________



	/**
	 * {@inheritDoc}
	 * <div>
	 * <p style="font-weight:bold;">
	 * ligne Jtable pour un ProduitJPA</b> :</p>
	 * <p>"idproduit;type de produit;sous-type de produit;produit;".</p>
	 * </div>
	 */
	@Transient
	@Override
	public final Object getValeurColonne(
			final int pI) {

		Object valeur = null;

		switch (pI) {

		case 0:
			if (this.getIdProduit() != null) {
				valeur = String.valueOf(this.getIdProduit());
			}
			
			break;

		case 1:
			if (this.getTypeProduit() != null) {
				if (this.getTypeProduit().getTypeProduit() != null) {
					valeur = this.getTypeProduit().getTypeProduit();
				}
			}
			
			break;

		case 2:
			if (this.getSousTypeProduit() != null) {
				if (this.getSousTypeProduit().getSousTypeProduit() != null) {
					valeur = this.getSousTypeProduit().getSousTypeProduit();
				}
			}
			
			break;

		case 3:
			if (this.getProduit() != null) {				
				valeur = this.getProduit();				
			}
			
			break;
						
		default:
			valeur = "invalide";
			break;

		} // Fin du Switch._________________________________

		return valeur;
		
	} // Fin de getValeurColonne(...)._____________________________________

		
	
	/**
	* {@inheritDoc}
	*/
	@Transient
	@Override
	public final TypeProduitI getTypeProduit() {	
		return this.sousTypeProduit != null 
				? this.sousTypeProduit.getTypeProduit() : null;	
	}



	/**
	* {@inheritDoc}
	*/
	@Transient
	@Override
	public final boolean isValide() {	
		return this.valide;	
	}



	/**
	* {@inheritDoc}
	*/
	@Override
	public Long getIdProduit() {	
		return this.idProduit;	
	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public void setIdProduit(final Long pIdProduit) {
		this.idProduit = pIdProduit;	
	}



	/**
	* {@inheritDoc}
	*/
	@Override
	public String getProduit() {
		return this.produit;	
	}


		
	/**
	* {@inheritDoc}
	*/
	@Override
	public void setProduit(final String pProduit) {	
		this.produit = normalize(pProduit);	
	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public SousTypeProduitI getSousTypeProduit() {	
		return this.sousTypeProduit;	
	}


		
	/**
	* {@inheritDoc}
	*/
	@Override
	public void setSousTypeProduit(final SousTypeProduitI pSousTypeProduit) {
		
		/* traite le cas d'une mauvaise instance de pSousTypeProduit. */
		this.traiterMauvaiseInstanceSousTypeProduit(pSousTypeProduit);

		/* mémorise l'ancienne valeur de this.sousTypeProduit. */
        final SousTypeProduitI old = this.sousTypeProduit;

        /* ne fait rien et return si la valeur passée 
         * en paramètre vaut l'ancienne valeur. */
        if (old == pSousTypeProduit) {
            return;
        }

        /* SYNTAXE DEPUIS Java 17 : Si old est une instance de 
         * SousTypeProduit, alors cast old en SousTypeProduit 
         * et stocke-le dans la variable oldImpl. */
        if (old instanceof SousTypeProduitJPA oldImplSTP) {
        	
        	/* détache le présent Produit de l’ancien parent 
        	 * SousTypeProduit et le retire de sa liste produits. */
            oldImplSTP.internalRemoveProduit(this);
        }

        /* passe pSousTypeProduit à this.sousTypeProduit. */
        this.sousTypeProduit = pSousTypeProduit;
            
        /* rattache le présent produit au nouveau parent 
         * et l'ajoute à sa liste produits. */
        if (pSousTypeProduit instanceof SousTypeProduitJPA newImplSTP) {
            newImplSTP.internalAddProduit(this);
        }
        
        /* passe this.valide à true si 
         * this.sousTypeProduit n'est pas null. */
        this.recalculerValide();
	}
	


	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * traite le cas où une 
	 * mauvaise instance de SousTypeProduitI non Entity JPA 
	 * est passée en paramètre d'une méthode.</p>
	 * <ul>
	 * <li>return si pSousTypeProduit == null.</li>
	 * <li>LOG.fatal et throw IllegalStateException 
	 * si pSousTypeProduit est une mauvaise instance.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pSousTypeProduit : SousTypeProduitI
	 */
	private void traiterMauvaiseInstanceSousTypeProduit(
			final SousTypeProduitI pSousTypeProduit) {
	
		if (pSousTypeProduit != null) {
	
			/*
			 * LOG.fatal et throw IllegalStateException si un
			 * pSousTypeProduit est une mauvaise instance.
			 */
			if (!(pSousTypeProduit instanceof SousTypeProduitJPA)) {
	
				final String messageKo = MAUVAISE_INSTANCE_ENFANT_JPA
						+ pSousTypeProduit.getClass();
	
				if (LOG.isFatalEnabled()) {
					LOG.fatal(messageKo);
				}
	
				throw new IllegalStateException(messageKo);
	
			}
	
		}
	
		/* return si pTypeProduit == null. */
		return;
	}

	
}