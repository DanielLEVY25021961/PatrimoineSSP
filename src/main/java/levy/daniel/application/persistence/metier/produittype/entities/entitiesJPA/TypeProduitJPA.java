/* ********************************************************************* */
/* ************************** ENTITY JPA ******************************* */
/* ********************************************************************* */
package levy.daniel.application.persistence.metier.produittype.entities.entitiesJPA;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
 * <p style="font-weight:bold;">CLASSE TypeProduitJPA :</p>
 * <p>modélise une entity <span style="font-weight:bold;">JPA</span> 
 * à stocker dans une base de données relationnelle (type PostgeSQL) 
 * pour l'objet métier
 *  <span style="font-weight:bold;">TypeProduit</span> du Package 
 *  <span style="font-weight:bold;">
 *  levy.daniel.application.model.metier.produittype</span> qui modelise un 
 *  <span style="font-weight:bold;">type de produit</span>
 * comme "vêtement", "outillage", "logiciel"...</p>
 * </div>
 * 
 * <div>
 * 
 * <div>
 * <p>Dans ce modèle de PRODUIT TYPE, un TypeProduitJPA 
 * comme "vêtement" se décline en SousTypeProduitJPA comme :</p>
 * <ul>
 * <li>"vêtement pour homme"</li>
 * <li>"vêtement pour femme"</li>
 * <li>"vêtement pour enfant"</li>
 * </ul>
 * </div>
 * <div>
 * <p>un SousTypeProduitJPA comme "vêtement pour homme" 
 * qualifie des ProduitJPA comme : </p>
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
 * Diagramme de Classes du ProduitJPA qualifié par un SousTypeProduitJPA 
 * qui est lui-même une déclinaison d'un TypeProduitJPA</p>
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
 * @author Daniel LEVY
 * @version 1.0
 * @created 06/12/2025
 */
@Entity(name = "TypeProduitJPA")
@Access(AccessType.FIELD)
@Table(name = "TYPES_PRODUIT", schema = "PUBLIC"
, uniqueConstraints = @UniqueConstraint(
		name = "UNICITE_TYPE_PRODUIT", columnNames = {"TYPE_PRODUIT"})
, indexes = {@Index(name = "INDEX_TYPE_PRODUIT", columnList = "TYPE_PRODUIT")})
public class TypeProduitJPA implements TypeProduitI, Cloneable, Serializable {

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
	 * System.getProperty("line.separator")
	 */
	public static final String SAUT_DE_LIGNE 
		= System.getProperty("line.separator");
	
	/**
	 * "TYPE_PRODUIT"
	 */
	public static final String TYPE_PRODUIT = "TYPE_PRODUIT";
		
	/**
	 * "le SousTypeProduit dans la liste passée en paramètre 
	 * n'est pas de type Entity JPA : "
	 */
	public static final String MAUVAISE_INSTANCE_JPA 
		= "le SousTypeProduit dans la liste passée en paramètre "
				+ "n'est pas de type Entity JPA : ";
	
	/**
	 * "le SousTypeProduit passé en paramètre 
	 * n'est pas de type Entity JPA : "
	 */
	public static final String MAUVAISE_INSTANCE_ENFANT_JPA 
		= "le SousTypeProduit passé en paramètre "
				+ "n'est pas de type Entity JPA : ";


	/* ------------------------------------------------------------------ */
	
	/**
	 * <p>ID en base du type de produit.</p>
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_TYPE_PRODUIT")
	private Long idTypeProduit;
 
	/**
	 * <p>type de produit comme "vêtement", "outillage", ...</p>
	 */
	@Column(name = TYPE_PRODUIT
			, unique = false, updatable = true
			, insertable = true, nullable = false)
	private String typeProduit;
	
	/**
	 * <p>Liste des sous-types de produit du présent type de produit.</p>
	 * <p>par exemple, pour le type de produit "vêtement" :</p>
	 * <ul>
	 * <li>vêtement pour homme</li>
	 * <li>vêtement pour femme</li>
	 * <li>vêtement pour enfant</li>
	 * </ul>
	 */
	@OneToMany(targetEntity = SousTypeProduitJPA.class
			, cascade = CascadeType.ALL
			, orphanRemoval = true
			, fetch=FetchType.LAZY
			, mappedBy="typeProduit")
	private List<SousTypeProduitI> sousTypeProduits 
		= new ArrayList<SousTypeProduitI>();

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
		= LogManager.getLogger(TypeProduitJPA.class);
	
// ====================== METHODES ======================================
	
	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public TypeProduitJPA() {
		this(null, null, null);
	}

	
	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 1.</p>
	 * </div>
	 *
	 * @param pTypeProduit : String : type de produit 
	 * comme "vêtement", "outillage", ...
	 */
	public TypeProduitJPA(final String pTypeProduit) {
		this(null, pTypeProduit, null);
	}
	

	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 2 UTILE avec id et typeProduit.</p>
	 * </div>
	 *
	 * @param pIdTypeProduit : Long : ID en base du type de produit
	 * @param pTypeProduit : String : type de produit 
	 * comme "vêtement", "outillage", ...
	 */
	public TypeProduitJPA(final Long pIdTypeProduit
			, final String pTypeProduit) {
		this(pIdTypeProduit, pTypeProduit, null);
	}
	
	
	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE 2.</p>
	 * </div>
	 *
	 * @param pTypeProduit : String : type de produit 
	 * comme "vêtement", "outillage", ...
	 * @param pSousTypeProduits : List&lt;SousTypeProduitI&gt; : 
	 * Liste des sous-types de produit du présent type de produit.
	 * Liste typée sur l’interface SousTypeProduitI pour rester 
	 * proxy-safe et découplé de l’implémentation.
	 */
	public TypeProduitJPA(final String pTypeProduit,
			final List<SousTypeProduitI> pSousTypeProduits) {
		this(null, pTypeProduit, pSousTypeProduits);
	}
	

	
	/**
	 * <div>
	 * <p>CONSTRUCTEUR COMPLET.</p>
	 * <p>ATTENTION : avec cette implémentation, 
	 * la liste <code>this.sousTypeProduits</code> 
	 * n'est jamais null (mais vide).</p>
	 * </div>
	 *
	 * @param pIdTypeProduit : Long : ID en base du type de produit
	 * @param pTypeProduit : String : type de produit 
	 * comme "vêtement", "outillage", ...
	 * @param pSousTypeProduits : List&lt;SousTypeProduitI&gt; : 
	 * Liste des sous-types de produit du présent type de produit.
	 * Liste typée sur l’interface SousTypeProduitI pour 
	 * rester proxy-safe et découplé de l’implémentation
	 */
	public TypeProduitJPA(final Long pIdTypeProduit
			, final String pTypeProduit
			, final List<SousTypeProduitI> pSousTypeProduits) {

		super();
		this.idTypeProduit = pIdTypeProduit;
		this.typeProduit = normalize(pTypeProduit);

		// Canonique : on ne garde jamais une référence externe
		// directe si on veut maîtriser la cohérence.
		this.sousTypeProduits = new ArrayList<>();

		if (pSousTypeProduits != null) {

			for (final SousTypeProduitI stp : pSousTypeProduits) {
				this.rattacherEnfantSTP(stp);
			}
		}
	}
	
	
	
	/**
	* {@inheritDoc}
	* <div>
	* <p style="font-weight:bold;">proxy-safe</p>
	* <p style="font-weight:bold;">hashCode() sur :</p>
	* <ol>
	* <li style="font-weight:bold;">idTypeProduit</li>
	* <li style="font-weight:bold;">typeProduit</li>
	* </ol>
	* </div>
	*/
	@Override
	public final int hashCode() {
	    
	    final Long thisId = this.getIdTypeProduit();
	    
	    /* Stratégie id-first :
	     * Si l'identifiant technique est non nul,
	     * hashCode() est basé sur l'identité technique (proxy-safe).
	     */
	    if (thisId != null) {
	        return thisId.hashCode();
	    }
	    
	    /* Stratégie JPA : minimum de verrous.
	     * Alignement métier : hashCode insensible à la casse sur typeProduit.
	     */
	    if (this.typeProduit == null) {
	        return 0;
	    }
	    
	    return this.typeProduit.toLowerCase(Locale.ROOT).hashCode();	    
	}
	
	

	/**
	* {@inheritDoc}
	* <div>
	* <p style="font-weight:bold;">proxy-safe</p>
	* <p style="font-weight:bold;">equals() sur :</p>
	* <ol>
	* <li style="font-weight:bold;">idTypeProduit</li>
	* <li style="font-weight:bold;">typeProduit</li>
	* </ol>
	* </div>
	*/
	@Override
	public final boolean equals(final Object obj) {
		
		/*
		 * retourne true si les références sont identiques.
		 */
		if (this == obj) {
			return true;
		}
		
		/*
		 * return false si pObject == null.
		 */
		if (obj == null) {
			return false;
		}
		
		/*
		 * retourne false si pObject
		 * n'est pas une bonne instance.
		 */
		if (!(obj instanceof TypeProduitJPA other)) {
			return false;
		}

		final Long thisId = this.getIdTypeProduit();
		final Long otherId = other.getIdTypeProduit();
		
		/* Stratégie id-first :
		 * Si les deux identifiants sont non nuls,
		 * comparaison stricte sur l'identité technique.
		 */
		if (thisId != null && otherId != null) {
			return thisId.equals(otherId);
		}
		
		/* Fallback métier :
		 * comparaison insensible à la casse sur [TypeProduit].
		 */
		final String thisType = this.typeProduit;
		final String otherType = other.typeProduit;
		
		if (thisType == null && otherType == null) {
			return true;
		}
		
		if (thisType == null || otherType == null) {
			return false;
		}
		
		return thisType.equalsIgnoreCase(otherType);		
	}



	/**
	* {@inheritDoc}
	*/
	@Override
	public final String toString() {

		final StringBuilder finalBuilder = new StringBuilder();
		
		finalBuilder.append("TypeProduitJPA [");

		finalBuilder.append("idTypeProduit=");
			
		if (this.idTypeProduit != null) {
			finalBuilder.append(this.getIdTypeProduit());
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
	public final String afficherTypeProduit() {
		
		if (this.getTypeProduit() == null) {
			return NULL;
		}
		
		return this.getTypeProduit();
	}



	/**
	* {@inheritDoc}
	*/
	@Override
	public final String afficherTypeProduitFormate(
			final TypeProduitI pTypeProduit) {
		
		/* retourne null si pTypeProduit == null. */
		if (pTypeProduit == null) {
			return null;
		}
		
		final Long idTypeProduitProv = pTypeProduit.getIdTypeProduit();
		final String typeProduitString 
			= pTypeProduit.getTypeProduit();
		final List<? extends SousTypeProduitI> sousTypeProduitsProv 
			= pTypeProduit.getSousTypeProduits();
		
		final StringBuilder stb = new StringBuilder();
		
		stb.append("******* TypeProduit : ");
		stb.append(typeProduitString);
		stb.append(" *******");
		stb.append(SAUT_DE_LIGNE);
				
		final String pres1 
			= String.format("[idTypeProduit : %-2s - typeProduit : %-10s]"
					, idTypeProduitProv, typeProduitString);
		stb.append(pres1);
		stb.append(SAUT_DE_LIGNE);
		stb.append(SAUT_DE_LIGNE);
		
		stb.append("******* sousTypeProduits du TypeProduit : ");
		stb.append(typeProduitString);
		stb.append(SAUT_DE_LIGNE);
		
		if (sousTypeProduitsProv == null) {
			stb.append(NULL);
		} else if (sousTypeProduitsProv.isEmpty()) {
			stb.append("vide");
		} else {
			
			for (final SousTypeProduitI 
					sousTypeProduit : sousTypeProduitsProv) {
				
				final Long idSousTypeProduit 
					= sousTypeProduit.getIdSousTypeProduit();
				final String sousTypeProduitString 
					= sousTypeProduit.getSousTypeProduit();
				final TypeProduitI typeProduitduSousTypeProduit 
					= sousTypeProduit.getTypeProduit();
				
				final List<? extends ProduitI> produitsDansSousProduit 
					= sousTypeProduit.getProduits();
				
				Long idTypeProduitduSousTypeProduit = null;
				String typeProduitduSousTypeProduitString = null;
				
				if (typeProduitduSousTypeProduit != null) {
					idTypeProduitduSousTypeProduit 
						= typeProduitduSousTypeProduit.getIdTypeProduit();
					typeProduitduSousTypeProduitString 
						= typeProduitduSousTypeProduit.getTypeProduit();
				}
				
				final String pres2 
				= String.format("[idSousTypeProduit : %-2s "
						+ "- sousTypeProduit : %-20s - "
						+ "[idTypeProduit du TypeProduit dans le SousTypeProduit : %-2s "
						+ "- typeProduitString du TypeProduit dans le SousTypeProduit : %-13s]]"
						, idSousTypeProduit
						, sousTypeProduitString
						, idTypeProduitduSousTypeProduit
						, typeProduitduSousTypeProduitString);
				
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
						final SousTypeProduitI sousTypeProduitProduit 
							= produit.getSousTypeProduit();
						
						String sousTypeProduitProduitString = null;
						
						if (sousTypeProduitProduit != null) {
							sousTypeProduitProduitString 
								= sousTypeProduitProduit.getSousTypeProduit();
						}
						
						stb.append('\t');
						
						final String presProduit 
						= String.format("[idProduit dans produits du SousTypeProduit : %-2s - "
								+ "produit dans produits du SousTypeProduit : %-40s - "
								+ "sousTypeProduit dans le produit : %-20s]"
								, idProduit
								, produitString
								, sousTypeProduitProduitString);
						
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
	* {@inheritDoc}
	*/
	@Override
	public final String afficherSousTypeProduits() {
		
		if (this.sousTypeProduits == null) {
			return NULL;
		}
		
		final StringBuilder builder = new StringBuilder();
		
		builder.append("Sous-Types de Produit=[");
		
		builder.append(
			    this.sousTypeProduits.stream()
			        .map(SousTypeProduitI::afficherSousTypeProduit)
			        .collect(Collectors.joining(VIRGULE_ESPACE))
			);
		
		builder.append(CROCHET_FERMANT);
		
		return builder.toString();
	}


	
	/**
	 * {@inheritDoc}
	 * <div>
	 * <ol>
	 * <p style="font-weight:bold;">Classe dans l'ordre alphabétique de :</p>
	 * <li style="font-weight:bold;">typeProduit</li>
	 * </ol>
	 * <p style="font-weight:bold;">ATTENTION : 
	 * Strings.CI.compare(a, b) place les vides avant tout texte.</p>
	 * </div>
	 */
	@Override
	public final int compareTo(final TypeProduitI pObject) {

	    /* Comparaison de la même instance retourne toujours 0. */
	    if (this == pObject) {
	        return 0;
	    }

	    /* Comparaison avec null retourne toujours < 0. */
	    if (pObject == null) {
	        return -1;
	    }

	    return compareFields(pObject);
	}
	

	
	/**
	 * <p style="font-weight:bold;">
	 * Compare les champs sans synchronisation
	 * conformément à la stratégie JPA
	 * "minimum de verrous".</p>
	 *
	 * @param pObject : TypeProduitI :
	 * L'objet à comparer avec this.
	 * @return Le résultat de la comparaison.
	 */
	private int compareFields(final TypeProduitI pObject) {

	    final String a = this.typeProduit;

	    final String b = (pObject instanceof TypeProduitJPA other)
	            ? other.typeProduit
	            : pObject.getTypeProduit();

	    /*
	     * Gestion des cas null :
	     * - Si a est null et b est null, retourne 0.
	     * - Si a est null et b non null, retourne +1.
	     * - Si a non null et b null, retourne -1.
	     */
	    if (a == null) {
	        return (b == null) ? 0 : +1;
	    }

	    if (b == null) {
	        return -1;
	    }

	    /*
	     * Comparaison case-insensitive.
	     * Strings.CI.compare() place les chaînes vides avant les autres.
	     */
	    return Strings.CI.compare(a, b);
	}



	/**
	* {@inheritDoc}
	*/
	@Override
	public final TypeProduitJPA clone() throws CloneNotSupportedException {
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
	 * @return TypeProduitJPA : clone profond.
	 */
	private TypeProduitJPA cloneDeep() {
		return deepClone(new CloneContext());
	}



	/**
	* {@inheritDoc}
	*/
	@Override
	public final TypeProduitJPA deepClone(final CloneContext ctx) {

		/*
		 * vérifie que le clone n'existe pas déjà dans l'IdentityHashMap du
		 * CloneContext. 
		 * Le cas échéant, retourne le clone déjà existant.
		 */		
		final TypeProduitJPA existing = ctx.get(this);
		if (existing != null) {
		    return existing;
		}
		
		// CLONAGE DU PARENT.
		/* instancie un clone parent "nu" cloneTP sans enfants. */
		final TypeProduitJPA  cloneTP 
				= this.cloneWithoutChildren();

		/* rajoute cloneTP dans le cache du CloneContext. */
		ctx.put(this, cloneTP);

		// CLONAGE DES ENFANTS et PETITS-ENFANTS
		final List<? extends SousTypeProduitI> enfants = this
				.getSousTypeProduits();

		if (enfants != null) {

			for (final SousTypeProduitI stpI : enfants) {

				if (stpI == null) {
					continue;
				}

				/* clone profond l'enfant SousTypeProduit. */
				final SousTypeProduitI cloneSTP = stpI.deepClone(ctx);

				/*
				 * recolle le clone profond du SousTypeProduit au
				 * TypeProduit parent cloneTP via le Setter canonique qui
				 * l'ajoute dans cloneTP.sousTypeProduits.
				 */
				cloneSTP.setTypeProduit(cloneTP);

			}

		}

		return cloneTP;
	}



	/**
	* {@inheritDoc}
	*/
	@Override
	public final TypeProduitJPA cloneWithoutChildren() {

		final TypeProduitJPA clone = new TypeProduitJPA();
		clone.idTypeProduit = this.idTypeProduit;
		clone.typeProduit = this.typeProduit;
		clone.sousTypeProduits = new ArrayList<SousTypeProduitI>();

		return clone;

	}


	
	/**
	* {@inheritDoc}
	* <div>
	* <p style="font-weight:bold">Pas de verrou ici. 
	* Utilisation monothreadée des Entities JPA.</p>
	* </div>
	*/
	@Override
	public final void rattacherEnfantSTP(
	        final SousTypeProduitI pEnfant) {

	    /* Traite le cas d'une mauvaise instance en paramètre. */
	    this.traiterMauvaiseInstanceSousTypeProduit(pEnfant);

	    /* retourne et ne fait rien si pEnfant == null
	     * ou si le libellé de pEnfant est blank (null ou espaces). */
	    if (pEnfant == null 
	            || StringUtils.isBlank(pEnfant.getSousTypeProduit())) {
	        return;
	    }

	    this.rattacherSiNecessaire(pEnfant);
	}



	 /**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Rattache l'enfant {@code SousTypeProduitI} pEnfant
	 * au présent parent {@code TypeProduit} si ce n'est pas déjà fait.
	 * </p>
	 * <ul>
	 * <li>Récupère le parent de pEnfant.</li>
	 * <li>Ne fait rien si le parent de pEnfant est déjà this.</li>
	 * <li>Sinon, rattache pEnfant au présent parent this.</li>
	 * <li>Utilise le SETTER CANONIQUE INTELLIGENT 
	 * {@code setTypeProduit(this)}  de l'enfant 
	 * {@code SousTypeProduitI} pEnfant pour le rattachement.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pEnfant : {@code SousTypeProduitI} :
	 * enfant à rattacher au présent parent this.
	 */
	private void rattacherSiNecessaire(final SousTypeProduitI pEnfant) {
		
		/* Récupère le parent de pEnfant. */
	    final TypeProduitI parentDeEnfant = pEnfant.getTypeProduit();
	    
	    /* Rattache pEnfant au présent parent this. */
	    if (parentDeEnfant != this) {
	        pEnfant.setTypeProduit(this);
	    }
	}



	/**
	* {@inheritDoc}
	* <div>
	* <p style="font-weight:bold">Pas de verrou ici. 
	* Utilisation monothreadée des Entities JPA.</p>
	* </div>
	*/
	@Override
	public final void detacherEnfantSTP(
	        final SousTypeProduitI pEnfant) {

	    /* Traite le cas d'une mauvaise instance en paramètre. */
	    this.traiterMauvaiseInstanceSousTypeProduit(pEnfant);

	    /* retourne et ne fait rien si pEnfant == null
	     * ou si le libellé de pEnfant est blank (null ou espaces). */
	    if (pEnfant == null 
	            || StringUtils.isBlank(pEnfant.getSousTypeProduit())) {
	        return;
	    }

	    this.detacherSiNecessaire(pEnfant);
	}
	 
	
	
	 /**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Détache l'enfant {@code SousTypeProduitI} pEnfant
	 * du présent parent {@code TypeProduit} si ce n'est pas déjà fait.
	 * </p>
	 * <ul>
	 * <li>Récupère le parent de pEnfant.</li>
	 * <li>Ne fait rien si le parent de pEnfant n'est pas this.</li>
	 * <li>Sinon, détache pEnfant du présent parent this.</li>
	 * <li>Utilise le SETTER CANONIQUE INTELLIGENT 
	 * {@code setTypeProduit(null)}  de l'enfant 
	 * {@code SousTypeProduitI} pEnfant pour le détachement.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pEnfant : {@code SousTypeProduitI} :
	 * enfant à détacher du présent parent this.
	 */
	private void detacherSiNecessaire(final SousTypeProduitI pEnfant) {
		
		/* Récupère le parent de pEnfant. */
	    final TypeProduitI parentDeEnfant = pEnfant.getTypeProduit();
	    
	    /* Détache pEnfant du présent parent this. */
	    if (parentDeEnfant == this) {
	        pEnfant.setTypeProduit(null);
	    }
	}
	 
	
	
    /**
     * <div>
     * <p>ajoute pSousTypeProduit directement (add) 
     * <span style="font-weight:bold;">sans synchronisation</span> 
     * dans la liste 
     * <code style="font-weight:bold;">this.sousTypeProduits</code> 
     * du présent parent TypeProduitI</p>
     * <p>Méthode protected interne au Package 
     * (utilisée par SousTypeProduit pour éviter les boucles).</p>
     * <ul>
     * <li>traite le cas d'une mauvaise instance en paramètre.</li>
     * <li>ne fait rien et return si pSousTypeProduit == null.</li>
     * <li>ne fait rien si this.sousTypeProduits.contains(pSousTypeProduit). 
     * Donc jamais de doublons.</li>
     * <li>fait un ajout direct sans synchronisation par add() 
     * de pSousTypeProduit dans <code style="font-weight:bold;">
     * this.sousTypeProduits</code>.</li>
     * </ul>
     * </div>
     *
     * @param pSousTypeProduit : SousTypeProduitI
     */
    protected final void internalAddSousTypeProduit(
    		final SousTypeProduitI pSousTypeProduit) {

    	/* traite le cas d'une mauvaise instance en paramètre. */
    	this.traiterMauvaiseInstanceSousTypeProduit(pSousTypeProduit);

    	/* ne fait rien et return si pSousTypeProduit == null. */
        if (pSousTypeProduit == null) {
            return;
        }

        /* ne fait rien si this.sousTypeProduits.contains(pSousTypeProduit). 
         * Donc jamais de doublons. */
        if (!this.sousTypeProduits.contains(pSousTypeProduit)) {
        	/* fait un ajout direct sans synchronisation par add() 
        	 * de pSousTypeProduit dans this.sousTypeProduits. */
            this.sousTypeProduits.add(pSousTypeProduit);
        }
    } // Fin de internalAddSousTypeProduit(...).____________________________

    
    
    /**
     * <div>
     * <p>retire pSousTypeProduit directement (remove) 
     * <span style="font-weight:bold;">sans synchronisation</span>
     * dans la liste 
     * <code style="font-weight:bold;">this.sousTypeProduits</code> 
     * du présent parent TypeProduitI</p>
     * <p>Méthode protected interne au Package 
     * (utilisée par SousTypeProduit pour éviter les boucles).</p>
     * <ul>
     * <li>traite le cas d'une mauvaise instance en paramètre.</li>
     * <li>ne fait rien et return si pSousTypeProduit == null.</li>
     * <li>ne fait rien si la liste ne contient pas pSousTypeProduit.</li>
     * <li>fait un retrait direct sans synchronisation par remove() 
     * de pSousTypeProduit dans <code style="font-weight:bold;">
     * this.sousTypeProduits</code>.</li>
     * </ul>
     * </div>
     *
     * @param pSousTypeProduit
     */
    protected final void internalRemoveSousTypeProduit(
    		final SousTypeProduitI pSousTypeProduit) {

    	/* traite le cas d'une mauvaise instance en paramètre. */
    	this.traiterMauvaiseInstanceSousTypeProduit(pSousTypeProduit);

    	/* ne fait rien et return si pSousTypeProduit == null. */
        if (pSousTypeProduit == null) {
            return;
        }

        /* ne fait rien si la liste ne contient pas pSousTypeProduit. */
        /* fait un retrait direct sans synchronisation par remove(). */
        this.sousTypeProduits.remove(pSousTypeProduit);
    }


    
	/**
	 * <div>
	 * <p>retourne une chaine de caractères "nettoyée" 
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
	 * <p style="font-weight:bold;">retourne une String pour 
	 * afficher l'en-tête d'un TypeProduitJPA en csv.</p>
	 * </div>
	 * 
	 * @return String : 
	 * "idTypeProduit;type de produit;"
	*/
	@Transient
	@Override
	public final String getEnTeteCsv() {
		return "idTypeProduit;type de produit;";	
	}



	/**
	* {@inheritDoc}
	* <div>
	 * <p style="font-weight:bold;">retourne une String pour 
	 * l'affichage d'un TYPE-PRODUIT 
	 * sous forme de csv avec des séparateurs point-virgule ';'.</p>
	 * <p>Le csv retourné respecte l'ordre indiqué dans l'en-tête : 
	 * "idTypeProduit;type de produit;"</p>
	 * <ul>
	 * <p>par exemple : </p>
	 * <li>idTypeProduit;type de produit;</li>
	 * <li>null;vêtement;</li>
	 * </ul>
	 * </div>  
	 *
	 * @return String : "idTypeProduit;type de produit;"
	*/
	@Override
	public final String toStringCsv() {
		
		final StringBuilder builder = new StringBuilder();
		
		/* idTypeProduit */
		if (this.getIdTypeProduit() != null) {			
			builder.append(this.getIdTypeProduit());			
		} else {
			builder.append(NULL);
		}
		
		builder.append(POINT_VIRGULE);
				
		/* type de produit */
		if (this.getTypeProduit() == null) {
			builder.append(NULL);
		} else {
			builder.append(this.getTypeProduit());
		}
		
		builder.append(POINT_VIRGULE);
		
		return builder.toString();

	}



	/**
	* {@inheritDoc}
	* <div>
	 * <p style="font-weight:bold;">en-tête Jtable pour un SousTypeProduitJPA</b> :</p>
	 * <p>"idTypeProduit;type de produit;".</p>
	 * </div>
	*/
	@Transient
	@Override
	public final String getEnTeteColonne(final int pI) {

		String entete = null;

		switch (pI) {

		case 0:
			entete = "idTypeProduit";
			break;
			
		case 1:
			entete = "type de produit";
			break;
			
		default:
			entete = "invalide";
			break;

		} // Fin du Switch._________________________________

		return entete;

	} // Fin de getEnTeteColonne(...)._____________________________________



	/**
	* {@inheritDoc}
	*  <div>
	 * <p style="font-weight:bold;">ligne Jtable pour un TypeProduitJPA</b> :</p>
	 * <p>"idTypeProduit;type de produit;".</p>
	 * </div>
	*/
	@Transient
	@Override
	public final Object getValeurColonne(final int pI) {

		Object valeur = null;

		switch (pI) {

		case 0:
			if (this.getIdTypeProduit() != null) {
				valeur = String.valueOf(this.getIdTypeProduit());
			}
			
			break;

		case 1:
			if (this.getTypeProduit() != null) {
				valeur = this.getTypeProduit();
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
	public Long getIdTypeProduit() {	
		return this.idTypeProduit;	
	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public void setIdTypeProduit(final Long pIdTypeProduit) {	
		this.idTypeProduit = pIdTypeProduit;	
	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public String getTypeProduit() {	
		return this.typeProduit;	
	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public void setTypeProduit(final String pTypeProduit) {	
		this.typeProduit = normalize(pTypeProduit);	
	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public List<? extends SousTypeProduitI> getSousTypeProduits() {	
		return Collections.unmodifiableList(this.sousTypeProduits);
	}
	
	
	  
    /**
    * {@inheritDoc}
    */
	@Override
    public void setSousTypeProduits(
    		final List<? extends SousTypeProduitI> pSousTypeProduits) {
		
		/* traite pSousTypeProduits mal constituée. */
		this.traiterMauvaiseInstanceDansListe(pSousTypeProduits);

    	/* Détache tous les SousTypeProduitI enfants de la présente 
    	 * liste this.sousTypeProduits en utilisant le Setter canonique 
    	 * de l'enfant SousTypeProduitI. */
        for (final SousTypeProduitI stp 
        		: new ArrayList<>(this.sousTypeProduits)) {
            if (stp != null) {
                stp.setTypeProduit(null);
            }
        }

        /* attache les nouveaux SousTypeProduitI enfants contenus 
         * dans pSousTypeProduits en utilisant le Setter canonique 
         * de l'enfant SousTypeProduitI. */
        if (pSousTypeProduits != null) {
            for (final SousTypeProduitI stp : pSousTypeProduits) {
                this.rattacherEnfantSTP(stp);
            }
        } else {
        	/* vide la liste avec clear() si pSousTypeProduits == null. 
        	 * Ne jamais faire new ArrayList pour être Hibernate-safe.*/
        	this.sousTypeProduits.clear();
        }
    }
	
	

	
	/**
	 * <div>
	 * <p style="font-weight:bold;">traite le cas où une 
	 * mauvaise instance de SousTypeProduitI non Entity JPA 
	 * s'est glissée dans la liste pSousTypeProduits</p>
	 * <ul>
	 * <li>return si pSousTypeProduits == null.</li>
	 * <li>LOG.fatal et throw IllegalStateException 
	 * si un SousTypeProduitI est une mauvaise instance.</li>
	 * </ul>
	 * </div>
	 *
	 * @param pSousTypeProduits : List&lt;? extends SousTypeProduitI&gt;
	 */
	private void traiterMauvaiseInstanceDansListe(
			final List<? extends SousTypeProduitI> pSousTypeProduits) {
		
		if (pSousTypeProduits != null) {
			for (final SousTypeProduitI stp : pSousTypeProduits) {
				
				if (stp == null) {
					continue;
				}
				
				/* LOG.fatal et throw IllegalStateException 
				 * si un SousTypeProduitI est une mauvaise instance. */
				if (!(stp instanceof SousTypeProduitJPA)) {
					
					final String messageKo 
						= MAUVAISE_INSTANCE_JPA + stp.getClass();
					
					if (LOG.isFatalEnabled()) {
						LOG.fatal(messageKo);
					}
					
					throw new IllegalStateException(messageKo);
				}
			}
		}
		
		/* return si pSousTypeProduits == null. */
		return;		
	}



	/**
	 * <div>
	 * <p style="font-weight:bold;">traite le cas où une 
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
	
		/* return si pSousTypeProduit == null. */
		return;
	}


}