/* ********************************************************************* */
/* ************************** ENTITY JPA ******************************* */
/* ********************************************************************* */
package levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import levy.daniel.application.model.metier.produittype.CloneContext;
import levy.daniel.application.model.metier.produittype.ProduitI;
import levy.daniel.application.model.metier.produittype.SousTypeProduitI;
import levy.daniel.application.model.metier.produittype.TypeProduitI;

/**
 * <style>p, ul, li {line-height : 1em;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">CLASSE SousTypeProduitJPA :</p>
 * <p>modélise une entity <span style="font-weight:bold;">JPA</span> 
 * à stocker dans une 
 * base de données relationnelle (type PostgeSQL) pour l'objet métier
 *  <span style="font-weight:bold;">SousTypeProduit</span> du Package 
 *  <code style="font-weight:bold;">
 *  levy.daniel.application.model.metier.produittype.SousTypeProduit</code>
 *  comme : </p>
 * <ul>
 * <li>"vêtement pour homme" pour le type de produit "vêtement"</li>
 * <li>"vêtement pour femme" pour le type de produit "vêtement"</li>
 * <li>"vêtement pour enfant" pour le type de produit "vêtement"</li>
 * </ul>
 * <p>ou encore :</p>
 * <ul>
 * <li>"application mobile" pour le type de produit "logiciel"</li>
 * <li>"application web" pour le type de produit "logiciel"</li>
 * </ul>
 * </div>
 * 
 * <div>
 * 
 * <div>
 * <p>Dans ce modèle de PRODUIT TYPE, un TypeProduit comme 
 * "vêtement" se décline en SousTypeProduitJPA comme :</p>
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
 * @author Daniel LEVY
 * @version 1.0
 * @created 06-déc.-2025 20:59:31
 */
@Entity(name = "SousTypeProduitJPA")
@Access(AccessType.FIELD)
@Table(name = "SOUS_TYPES_PRODUIT", schema = "PUBLIC"
, uniqueConstraints = @UniqueConstraint(
		name = "UNICITE_TYPE_PRODUIT-SOUS_TYPE_PRODUIT"
		, columnNames = {"SOUS_TYPE_PRODUIT", "TYPE_PRODUIT"})
, indexes = @Index(name = "INDEX_TYPE_PRODUIT-SOUS_TYPE_PRODUIT"
, columnList = "SOUS_TYPE_PRODUIT ASC, TYPE_PRODUIT ASC", unique = true))
public class SousTypeProduitJPA  implements SousTypeProduitI
										, Cloneable, Serializable {
	
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
	 * "Le pTypeProduit passé en paramètre 
	 * n'est pas de type Entity JPA : "
	 */
	public static final String MAUVAISE_INSTANCE_PARENT_JPA 
		= "Le pTypeProduit passé en paramètre "
				+ "n'est pas de type Entity JPA : ";
	
	/**
	 * "Le pProduit passé en paramètre 
	 * n'est pas de type Entity JPA : "
	 */
	public static final String MAUVAISE_INSTANCE_PETIT_ENFANT_JPA 
		= "Le pProduit passé en paramètre "
				+ "n'est pas de type Entity JPA : ";


	/* ----------------------------------------------------------------- */

	/**
	 * <p>ID en base du sous-type de produit.</p>
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_SOUS_TYPE_PRODUIT")
	private Long idSousTypeProduit;
	
	/**
	 * <p>sous-type de produit comme :</p>
	 * <ul>
	 * <li>"vêtement pour homme"</li>
	 * <li>"vêtement pour femme"</li>
	 * </ul>
	 * <p>pour un type de produit "vêtement"</p>
	 */
	@Column(name = "SOUS_TYPE_PRODUIT"
			, unique = false, updatable = true
			, insertable = true, nullable = false)
	private String sousTypeProduit;
	
	/**
	 * <p>Type de produit auquel est rattaché le présent 
	 * sous-type de produit.</p>
	 * <p>par exemple "vêtement" pour le sous-type de produit 
	 * "vêtement pour homme".</p>
	 * <p>ATTENTION : visibilité interface.</p>
	 */
	@ManyToOne(fetch = FetchType.LAZY
			, optional = false
			, targetEntity = TypeProduitJPA.class)
	@JoinColumn(name = "TYPE_PRODUIT"
	, nullable = false
	, referencedColumnName = "ID_TYPE_PRODUIT"
	, foreignKey = @ForeignKey(name="FK_TYPE_PRODUIT"))
	private TypeProduitI typeProduit;
	
	/**
	 * <div>
	 * <p>Collection des produits qualifiés par le présent 
	 * sous-type de produit.</p>
	 * <p>par exemple : </p>
	 * <ul>
	 * <li>"chemise à manches longues pour homme" pour le sous-produit "vêtement pour homme".</li>
	 * <li>"chemise à manches courtes pour homme" pour le sous-produit "vêtement pour homme".</li>
	 * <li>"sweat-shirt pour homme" pour le sous-produit "vêtement pour homme"</li>
	 * </ul>
	 * <p>ATTENTION : visibilité interface.</p>
	 * </div>
	 */
	@OneToMany(targetEntity = ProduitJPA.class
			, cascade = CascadeType.ALL
			, orphanRemoval = true
			, fetch = FetchType.LAZY
			, mappedBy = "sousTypeProduit")
	private List<ProduitI> produits = new ArrayList<ProduitI>();
	
	
	/**
	 * <div>
	 * <p>boolean qui indique si le présent SousTypeProduit 
	 * possède un TypeProduit non null.</p>
	 * <ul>
	 * <li>true si le présent SousTypeProduit possède un TypeProduit non null.</li>
	 * </ul>
	 * <p>Doit être calculé et jamais serializé.</p>
	 * </div>
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
	private static final Logger LOG 
		= LogManager.getLogger(SousTypeProduitJPA.class);
	
/* ===============================METHODES ============================ */
	
	

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public SousTypeProduitJPA() {
		this(null, null, null, null);
	}
	

	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 1.</p>
	 * </div>
	 *
	 * @param pSousTypeProduit : String : sous-type de produit.
	 */
	public SousTypeProduitJPA(final String pSousTypeProduit) {
		this(null, pSousTypeProduit, null, null);
	}
	

	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 2.</p>
	 * </div>
	 *
	 * @param pSousTypeProduit : String : sous-type de produit.
	 * @param pTypeProduit : TypeProduitI : Type de produit auquel 
	 * est rattaché le présent sous-type de produit.
	 */
	public SousTypeProduitJPA(final String pSousTypeProduit
			, final TypeProduitI pTypeProduit) {
		this(null, pSousTypeProduit, pTypeProduit, null);
	}
	

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 3 utile.</p>
	 * </div>
	 *
	 * @param pIdSousTypeProduit : Long : ID en base du sous-type de produit.
	 * @param pSousTypeProduit : String : sous-type de produit.
	 * @param pTypeProduit : TypeProduitI : Type de produit auquel 
	 * est rattaché le présent sous-type de produit.
	 */
	public SousTypeProduitJPA(final Long pIdSousTypeProduit
			, final String pSousTypeProduit
			, final TypeProduitI pTypeProduit) {
		this(pIdSousTypeProduit, pSousTypeProduit, pTypeProduit, null);
	}
	

	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 3.</p>
	 * </div>
	 *
	 * @param pSousTypeProduit : String : sous-type de produit.
	 * @param pTypeProduit : TypeProduitI : Type de produit auquel 
	 * est rattaché le présent sous-type de produit.
	 * @param pProduits : List&lt;ProduitI&gt; : Collection des produits 
	 * qualifiés par le présent sous-type de produit.
	 */
	public SousTypeProduitJPA(
			final String pSousTypeProduit
			, final TypeProduitI pTypeProduit
				, final List<ProduitI> pProduits) {
		this(null, pSousTypeProduit, pTypeProduit, pProduits);
	}
	

	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR COMPLET.</p>
	 * <ul>
	 * <li>passe les paramètres aux propriétés.</li>
	 * <li>passe pTypeProduit à <code>this.typeProduit</code> en utilisant 
	 * le Setter canonique qui maintient la cohérence des données.</li>
	 * <li>alimente la liste sousTypeProduits 
	 * dans pTypeProduit avec this.</li>
	 * <li>passe <code>this.valide</code> à true 
	 * si pTypeProduit n'est pas null.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pIdSousTypeProduit : Long : ID en base du sous-type de produit.
	 * @param pSousTypeProduit : String : sous-type de produit.
	 * @param pTypeProduit : TypeProduitI : Type de produit auquel 
	 * est rattaché le présent sous-type de produit.
	 * @param pProduits : List&lt;ProduitI&gt; : Collection des produits 
	 * qualifiés par le présent sous-type de produit.
	 */
	public SousTypeProduitJPA(final Long pIdSousTypeProduit
			, final String pSousTypeProduit
			, final TypeProduitI pTypeProduit
				, final List<ProduitI> pProduits) {
		
		super();
		
		this.idSousTypeProduit = pIdSousTypeProduit;
		this.sousTypeProduit = normalize(pSousTypeProduit);
		
		/* Canonique : toujours initialiser les listes */
        this.produits = new ArrayList<>();

        /* passe pTypeProduit à this.typeProduit en utilisant le 
         * Setter canonique qui maintient la cohérence des données. */
        /* Canonique : on passe par le setter d’association 
         * (maintient les 2 côtés). */
        this.setTypeProduit(pTypeProduit);

        /* on remplit la liste this.produits après avoir 
         * passé le TypeProduit via le setter d'association. */
        if (pProduits != null) {
            for (final ProduitI p : pProduits) {
                this.ajouterSTPauProduit(p);
            }
        }

        /* passe this.valide à true si this.typeProduit 
         * n'est pas null. */
        this.recalculerValide();
		
	}

	

	/**
	* {@inheritDoc}
	* <div>
	* <p style="font-weight:bold;">proxy-safe</p>
	* <p style="font-weight:bold;">hashCode() sur :</p>
	* <ol>
	* <li style="font-weight:bold;">idSousTypeProduit</li>
	* <li style="font-weight:bold;">typeProduit</li>
	* <li style="font-weight:bold;">sousTypeProduit</li>
	* </ol>
	* </div>
	*/
	@Override
	public final int hashCode() {

		/* ID. */
		final Long thisId = this.getIdSousTypeProduit();

		if (thisId != null) {
			return thisId.hashCode();
		}

		/* ID du TypeProduit parent. */
		final TypeProduitI tp = this.getTypeProduit();
		final Long tpId = (tp != null) ? tp.getIdTypeProduit() : null;

		if (tpId != null) {
			return Objects.hash(this.getSousTypeProduit(), tpId);
		}

		/* SousTypeProduit. */
		return Objects.hash(this.getSousTypeProduit(), tp);

	}



	/**
	* {@inheritDoc}
	* <div>
	* <p style="font-weight:bold;">proxy-safe</p>
	* <p style="font-weight:bold;">equals() sur :</p>
	* <ol>
	* <li style="font-weight:bold;">idSousTypeProduit</li>
	* <li style="font-weight:bold;">typeProduit</li>
	* <li style="font-weight:bold;">sousTypeProduit</li>
	* </ol>
	* </div>
	*/
	@Override
	public final boolean equals(final Object pObject) {

		/* retourne true si pObject est la présente instance. */
		if (this == pObject) {
			return true;
		}

		/* retourne false si pObject n'est pas une bonne instance. */
		if (!(pObject instanceof SousTypeProduitJPA other)) {
			return false;
		}

		/*
		 * 1) Stratégie ID-first.
		 * Si les deux entités possèdent un ID technique non null,
		 * on considère que l'identité persistence prime.
		 */
		final Long thisId = this.getIdSousTypeProduit();
		final Long otherId = other.getIdSousTypeProduit();

		if (thisId != null && otherId != null) {
			return thisId.equals(otherId);
		}

		/*
		 * 2) Fallback sur l'ID du TypeProduit parent.
		 * Permet de conserver une cohérence métier même
		 * si l'ID technique n'est pas encore attribué.
		 */
		final TypeProduitI thisTp = this.getTypeProduit();
		final TypeProduitI otherTp = other.getTypeProduit();

		final Long thisTpId = (thisTp != null) 
				? thisTp.getIdTypeProduit() : null;
		final Long otherTpId = (otherTp != null) 
				? otherTp.getIdTypeProduit() : null;

		/* Fallback sur ID parent si présent */
		if (thisTpId != null && otherTpId != null) {
			return Objects.equals(
					this.getSousTypeProduit(), other.getSousTypeProduit())
					&& thisTpId.equals(otherTpId);
		}

		/*
		 * 3) Fallback final purement métier.
		 * Comparaison sur :
		 * - le libellé sousTypeProduit
		 * - le parent complet
		 * Garantit la cohérence avec hashCode().
		 */
		return Objects.equals(
				this.getSousTypeProduit(), other.getSousTypeProduit())
				&& Objects.equals(thisTp, otherTp);
	}



	/**
	* {@inheritDoc}
	*/
	@Override
	public final String toString() {

		final StringBuilder finalBuilder = new StringBuilder();
		
		finalBuilder.append("SousTypeProduit [");

		finalBuilder.append("idSousTypeProduit=");
		
		if (this.idSousTypeProduit != null) {			
			finalBuilder.append(this.getIdSousTypeProduit());
		} else {
			finalBuilder.append(NULL);
		}
		
		finalBuilder.append(VIRGULE_ESPACE);

		finalBuilder.append("sousTypeProduit=");
		
		if (this.sousTypeProduit != null) {			
			finalBuilder.append(this.getSousTypeProduit());
		} else {
			finalBuilder.append(NULL);
		}
		
		finalBuilder.append(VIRGULE_ESPACE);

		finalBuilder.append("typeProduit=");
		
		if (this.typeProduit != null) {			
			finalBuilder.append(this.getTypeProduit());
		} else {
			finalBuilder.append(NULL);
		}

		finalBuilder.append(CROCHET_FERMANT);
		
		return finalBuilder.toString();

	}
	

	
	/**
	* {@inheritDoc}
	*/
	@Override
	public final String afficherSousTypeProduit() {
		
		if (this.getSousTypeProduit() == null) {
			return NULL;
		}
		
		return this.getSousTypeProduit();
	}



	/**
	 * {@inheritDoc}
	 * <div>
	 * <ol>
	 * <p style="font-weight:bold;">
	 * Classe dans l'ordre alphabétique de :</p>
	 * <li style="font-weight:bold;">typeProduit</li>
	 * <li style="font-weight:bold;">sousTypeProduit</li>
	 * </ol>
	 * </div>
	 */
	@Override
	public final int compareTo(final SousTypeProduitI pObject) {

		/* Comparaison de la même instance retourne toujours 0. */
		if (this == pObject) {
			return 0;
		}

		/* Comparaison avec null retourne toujours < 0. */
		if (pObject == null) {
			return -1;
		}

		return this.compareFields(pObject);

	} // Fin de compareTo(...)._____________________________________________



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Compare les champs de manière canonique et homogène
	 * (sans verrous, en alignement avec la stratégie "minimum de verrous"
	 * dans les Entities JPA).</p>
	 * <ul>
	 * <li>Compare d'abord les {@code TypeProduit} 
	 * (via {@code compareTo}).</li>
	 * <li>Puis compare {@code sousTypeProduit} 
	 * (case-insensitive) via {@code Strings.CI.compare}.</li>
	 * <li>Les {@code null} sont placés "après" 
	 * (donc considérés plus grands).</li>
	 * </ul>
	 * </div>
	 *
	 * @param pObject : {@code SousTypeProduitI} : l'objet à comparer.
	 * @return int : résultat de la comparaison.
	 */
	private int compareFields(final SousTypeProduitI pObject) {

		/* 1) TypeProduit. */
		final TypeProduitI a = this.getTypeProduit();
		final TypeProduitI b = pObject.getTypeProduit();

		if (a == null) {
			if (b != null) {
				return +1;
			}
		} else {
			if (b == null) {
				return -1;
			}

			final int compareTypeProduit = a.compareTo(b);

			if (compareTypeProduit != 0) {
				return compareTypeProduit;
			}
		}

		/* 2) SousTypeProduit. */
		final String s1 = this.getSousTypeProduit();
		final String s2 = pObject.getSousTypeProduit();

		if (s1 == null) {
			/* null "après". */
			return (s2 == null) ? 0 : +1;
		}

		if (s2 == null) {
			return -1;
		}

		return Strings.CI.compare(s1, s2);

	} // Fin de compareFields(...)._________________________________________
	
	

	/**
	* {@inheritDoc}
	*/
	@Override
	public final SousTypeProduitJPA clone() 
			throws CloneNotSupportedException {		
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
	 * @return SousTypeProduitJPA : clone profond.
	 */
	private SousTypeProduitJPA cloneDeep() {	
		return deepClone(new CloneContext());	
	}
	
	
	
	/**
	* {@inheritDoc}
	*/
	@Override
	public final SousTypeProduitJPA deepClone(final CloneContext ctx) {

		/*
		 * Vérifie si l'objet a déjà été cloné
		 * (cycle-safe via IdentityHashMap).
		 */
		final SousTypeProduitJPA existing = ctx.get(this);

		if (existing != null) {
			return existing;
		}

		/* Clone "nu" sans parent ni enfants. */
		final SousTypeProduitJPA clone 
			= this.cloneWithoutParentAndChildren();

		/* Enregistre dans le contexte AVANT de cloner les relations. */
		ctx.put(this, clone);

		/* ===================== */
		/* Clonage du parent     */
		/* ===================== */

		final TypeProduitI parent = this.getTypeProduit();

		if (parent != null) {

			final TypeProduitI cloneParent = parent.deepClone(ctx);

			/*
			 * Utilise le setter canonique pour
			 * garantir la cohérence bidirectionnelle.
			 */
			clone.setTypeProduit(cloneParent);
		}

		/* ===================== */
		/* Clonage des enfants   */
		/* ===================== */

		final List<? extends ProduitI> produitsProv = this.getProduits();

		if (produitsProv != null) {

			for (final ProduitI produit : produitsProv) {

				if (produit == null) {
					continue;
				}

				final ProduitI cloneProduit = produit.deepClone(ctx);

				/*
				 * Le setter canonique de Produit
				 * rattache automatiquement au clone.
				 */
				cloneProduit.setSousTypeProduit(clone);
			}
		}

		return clone;
	}


	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final SousTypeProduitJPA cloneWithoutParentAndChildren() {
	
		final SousTypeProduitJPA clone = new SousTypeProduitJPA();
		
	    clone.idSousTypeProduit = this.idSousTypeProduit;
	    clone.sousTypeProduit = this.sousTypeProduit;
	    clone.typeProduit = null;
	    clone.produits = new ArrayList<>();
	    
	    clone.recalculerValide();

	    return clone;	
	}



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * passe <code>this.valide</code> à true 
	 * si <code>this.typeProduit</code> n'est pas null.</p>
	 *</div>
	 */
	private void recalculerValide() {
        this.valide = (this.typeProduit != null);
    }	

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * retourne une chaine de caractères "nettoyée" 
	 * sans espaces superflus avant et après (trim()).</p>
	 * <ul>
	 * <li>ne fait rien et retourne null si pString est null.</li>
	 * <li>applique un trim() sur pString.</li>
	 * <li>retourne null si pString est vide.</li>
	 * <li>retourne la String trimée si elle est non vide.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pString : String : String à normaliser
	 * @return String : pString normalisé (trimé).
	 */
	private static String normalize(final String pString) {
	
		/* ne fait rien et retourne null si pString est null. */
		if (pString == null) {
			return null;
		}
		
		/* applique un trim() sur pString. */
		final String trime = pString.trim();
		
		/* retourne null si pString est vide. */
		/* retourne la String trimée si elle est non vide. */
		return trime.isEmpty() ? null : trime;
	
	}
	

	
	/**
	 * {@inheritDoc}
	 * <div>
	 * <p style="font-weight:bold;">retourne une String pour afficher l'en-tête 
	 * d'un SousTypeProduit en csv.</p>
	 * </div>
	 *
	 * @return String : 
	 * "idSousTypeProduit;type de produit;sous-type de produit;"
	 */
	@Transient
	@Override
	public final String getEnTeteCsv() {
		return "idSousTypeProduit;type de produit;sous-type de produit;";
	}


	
	/**
	 * {@inheritDoc}
	 * <div>
	 * <p style="font-weight:bold;">retourne une String pour 
	 * l'affichage d'un SOUS-TYPE-PRODUIT 
	 * sous forme de csv avec des séparateurs point-virgule ';'.</p>
	 * <p>Le csv retourné respecte l'ordre indiqué dans l'en-tête : 
	 * "idSousTypeProduit;type de produit;sous-type de produit;"</p>
	 * <ul>
	 * <p>par exemple : </p>
	 * <li>idSousTypeProduit;type de produit;sous-type de produit;</li>
	 * <li>null;vêtement;vêtement pour homme;</li>
	 * </ul>
	 * </div>  
	 *
	 * @return String : 
	 * "idSousTypeProduit;type de produit;sous-type de produit;"
	 */
	@Override
	public final String toStringCsv() {

		final StringBuilder builder = new StringBuilder();

		/* idSousTypeProduit */
		if (this.getIdSousTypeProduit() != null) {

			builder.append(this.getIdSousTypeProduit());

		} else {

			builder.append(NULL);

		}

		builder.append(POINT_VIRGULE);

		/* type de produit */
		if (this.getTypeProduit() == null) {

			builder.append(NULL);

		} else if (this.getTypeProduit().getTypeProduit() == null) {

			builder.append(NULL);

		} else {

			builder.append(this.getTypeProduit().getTypeProduit());

		}

		builder.append(POINT_VIRGULE);

		/* sous-type de produit */
		if (this.getSousTypeProduit() == null) {

			builder.append(NULL);

		} else {

			builder.append(this.getSousTypeProduit());

		}

		builder.append(POINT_VIRGULE);

		return builder.toString();

	}



	/**
	 * {@inheritDoc}
	 * <div>
	 * <p style="font-weight:bold;">
	 * en-tête Jtable pour un SousTypeProduit</b> :</p>
	 * <p>"idSousTypeProduit;type de produit;sous-type de produit;".</p>
	 * </div>
	 */
	@Transient
	@Override
	public final String getEnTeteColonne(
			final int pI) {
	
		String entete = null;
	
		switch (pI) {
	
		case 0:
			entete = "idSousTypeProduit";
			break;
			
		case 1:
			entete = "type de produit";
			break;
			
		case 2:
			entete = "sous-type de produit";
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
	 * <p style="font-weight:bold;">ligne Jtable pour un SousTypeProduit</b> :</p>
	 * <p>"idSousTypeProduit;type de produit;sous-type de produit;".</p>
	 * </div>
	 */
	@Transient
	@Override
	public final Object getValeurColonne(
			final int pI) {
	
		Object valeur = null;
	
		switch (pI) {
	
		case 0:
			if (this.getIdSousTypeProduit() != null) {
				valeur = String.valueOf(this.getIdSousTypeProduit());
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
				if (this.getSousTypeProduit() != null) {
					valeur = this.getSousTypeProduit();
				}
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
	@Override
	public final void ajouterSTPauProduit(final ProduitI pProduit) {
		
		/* traite le cas d'une mauvaise instance passée en paramètre. */
		this.traiterMauvaiseInstanceProduit(pProduit);

		if (pProduit == null) {
			return;
		}

		/* Passe le présent SousTypeProduitI comme SousProduit parent 
		 * du produit pProduit en utilisant son Setter canonique 
		 * qui maintient la cohérence des données.  */
		pProduit.setSousTypeProduit(this);

	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void retirerSTPauProduit(final ProduitI pProduit) {
		
		/* traite le cas d'une mauvaise instance passée en paramètre. */
		this.traiterMauvaiseInstanceProduit(pProduit);

		if (pProduit == null) {
			return;
		}

		if (!this.produits.contains(pProduit)) {
			return;
		}

		pProduit.setSousTypeProduit(null);

	}



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * ajoute pProduit à la <code>List&lt;ProduitI&gt;</code> 
	 * <code style="font-weight:bold;">this.produits</code> si 
	 * la liste ne le contient pas déjà.</p>
	 * <p>traite le cas d'une mauvaise instance passée en paramètre.</p>
	 * <p>Méthode interne au Package (protected) 
	 * utilisée par l'enfant Produit pour éviter les boucles.</p>
	 * </div>
	 *
	 * @param pProduit : ProduitI
	 */
	protected final void internalAddProduit(final ProduitI pProduit) {
		
		/* traite le cas d'une mauvaise instance passée en paramètre. */
		this.traiterMauvaiseInstanceProduit(pProduit);

		if (pProduit == null) {
			return;
		}

		if (!this.produits.contains(pProduit)) {
			this.produits.add(pProduit);
		}

	}



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * retire pProduit de la <code>List&lt;ProduitI&gt;</code> 
	 * <code style="font-weight:bold;">this.produits</code></p>
	 * <p>traite le cas d'une mauvaise instance passée en paramètre.</p>
	 * <p>Méthode interne au Package (protected) 
	 * utilisée par l'enfant Produit pour éviter les boucles.</p>
	 * </div>
	 *
	 * @param pProduit : ProduitI
	 */
	protected final void internalRemoveProduit(final ProduitI pProduit) {
		
		/* traite le cas d'une mauvaise instance passée en paramètre. */
		this.traiterMauvaiseInstanceProduit(pProduit);

		if (pProduit == null) {
			return;
		}

		this.produits.remove(pProduit);
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
	public Long getIdSousTypeProduit() {	
		return this.idSousTypeProduit;	
	}


		
	/**
	* {@inheritDoc}
	*/
	@Override
	public void setIdSousTypeProduit(
			final Long pIdSousTypeProduit) {
		this.idSousTypeProduit = pIdSousTypeProduit;	
	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public String getSousTypeProduit() {	
		return this.sousTypeProduit;	
	}


	
	/**
	* {@inheritDoc}
	* <div>
	* <p>Dans l'Entity JPA, on normalize.</p>
	* </div>
	*/
	@Override
	public void setSousTypeProduit(final String pSousTypeProduit) {
		this.sousTypeProduit = normalize(pSousTypeProduit);	
	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public TypeProduitI getTypeProduit() {	
		return this.typeProduit;	
	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
    public void setTypeProduit(final TypeProduitI pTypeProduit) {

		/* traite le cas d'une mauvaise instance. */
		this.traiterMauvaiseInstanceTypeProduit(pTypeProduit);
		
        final TypeProduitI old = this.typeProduit;

        /* ne fait rien et return si pTypeProduit == this.typeProduit. */
        if (old == pTypeProduit) {
            return;
        }

        /* détache le présent SousTypeProduit de l’ancien parent. */
        if (old instanceof TypeProduitJPA oldImplTP) {
            oldImplTP.internalRemoveSousTypeProduit(this);
        }

        /* passe la nouvelle valeur pTypeProduit à this.typeProduit. */    	
        this.typeProduit = pTypeProduit;            
       
        /* rattache le présent SousTypeProduit au nouveau parent 
         * et l'ajoute dans la liste sousTypeProduits du parent. */
        if (pTypeProduit instanceof TypeProduitJPA newImplTP) {
            newImplTP.internalAddSousTypeProduit(this);
        }

        /* recalcule this.valide. */
        this.recalculerValide();        
    }


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public List<? extends ProduitI> getProduits() {
        return Collections.unmodifiableList(this.produits);
    }


		
	/**
	* {@inheritDoc}
	*/
	@Override
	public void setProduits(final List<? extends ProduitI> pProduits) {	
		
		/* traite le cas d'une mauvaise instance dans pProduits. */
		this.traiterMauvaiseInstanceDansListeProduits(pProduits);
		
		/* Détache tous ProduitI enfants de la présente 
    	 * liste this.produits en utilisant le Setter canonique 
    	 * de l'enfant ProduitI. */
	    for (final ProduitI p : new ArrayList<>(this.produits)) {
	        if (p != null) {
	            p.setSousTypeProduit(null);
	        }
	    }

	    /* attache les nouveaux ProduitI enfants contenus 
         * dans pProduits en utilisant le Setter canonique 
         * de l'enfant ProduitI. */
	    if (pProduits != null) {
	        for (final ProduitI p : pProduits) {
	            this.ajouterSTPauProduit(p);
	        }
	    } else {
	    	/* vide la liste this.produits avec clear() si pProduits == null. 
	    	 * Ne jamais créer une nouvelle liste avec new ArrayList() 
	    	 * pour être Hibernate-safe. */
	    	this.produits.clear();
	    }
	}



	/**
	 * <div>
	 * <p style="font-weight:bold;">traite le cas où une 
	 * mauvaise instance de TypeProduitI non Entity JPA 
	 * est passée en paramètre d'une méthode.</p>
	 * <ul>
	 * <li>return si pTypeProduit == null.</li>
	 * <li>LOG.fatal et throw IllegalStateException 
	 * si pTypeProduit est une mauvaise instance.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pTypeProduit : TypeProduitI
	 */
	private void traiterMauvaiseInstanceTypeProduit(
			final TypeProduitI pTypeProduit) {
	
		if (pTypeProduit != null) {
	
			/*
			 * LOG.fatal et throw IllegalStateException si un
			 * pTypeProduit est une mauvaise instance.
			 */
			if (!(pTypeProduit instanceof TypeProduitJPA)) {
	
				final String messageKo = MAUVAISE_INSTANCE_PARENT_JPA
						+ pTypeProduit.getClass();
	
				if (LOG.isFatalEnabled()) {
					LOG.fatal(messageKo);
				}
	
				throw new IllegalStateException(messageKo);
	
			}
	
		}
	
		/* return si pTypeProduit == null. */
		return;
	}	



	/**
	 * <div>
	 * <p style="font-weight:bold;">traite le cas où une 
	 * mauvaise instance de ProduitI non Entity JPA 
	 * est passée en paramètre d'une méthode.</p>
	 * <ul>
	 * <li>return si pProduit == null.</li>
	 * <li>LOG.fatal et throw IllegalStateException 
	 * si pProduit est une mauvaise instance.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pProduit : ProduitI
	 */
	private void traiterMauvaiseInstanceProduit(
			final ProduitI pProduit) {
	
		if (pProduit != null) {
	
			/*
			 * LOG.fatal et throw IllegalStateException si un
			 * pProduit est une mauvaise instance.
			 */
			if (!(pProduit instanceof ProduitJPA)) {
	
				final String messageKo 
					= MAUVAISE_INSTANCE_PETIT_ENFANT_JPA
						+ pProduit.getClass();
	
				if (LOG.isFatalEnabled()) {
					LOG.fatal(messageKo);
				}
	
				throw new IllegalStateException(messageKo);
	
			}
	
		}
	
		/* return si pProduit == null. */
		return;
	}	
	

	
	/**
	 * <div>
	 * <p style="font-weight:bold;">traite le cas où une 
	 * mauvaise instance de ProduitI non Entity JPA 
	 * s'est glissée dans la liste pProduits</p>
	 * <ul>
	 * <li>return si pProduits == null.</li>
	 * <li>LOG.fatal et throw IllegalStateException 
	 * si un ProduitI est une mauvaise instance.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pProduits : List&lt;? extends ProduitI&gt;
	 */
	private void traiterMauvaiseInstanceDansListeProduits(
			final List<? extends ProduitI> pProduits) {
		
		if (pProduits != null) {
			
			for (final ProduitI p : pProduits) {
				
				if (p == null) {
					continue;
				}
				
				/* LOG.fatal et throw IllegalStateException 
				 * si un ProduitI est une mauvaise instance. */
				if (!(p instanceof ProduitJPA)) {
					
					final String messageKo 
						= MAUVAISE_INSTANCE_PETIT_ENFANT_JPA 
						+ p.getClass();
					
					if (LOG.isFatalEnabled()) {
						LOG.fatal(messageKo);
					}
					
					throw new IllegalStateException(messageKo);
				}
			}
		}
		
		/* return si pProduits == null. */
		return;		
	}

	
}